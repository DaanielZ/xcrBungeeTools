package pl.xcrafters.xcrbungeetools.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.md_5.bungee.api.ProxyServer;
import pl.xcrafters.xcrbungeetools.data.DataBan;
import pl.xcrafters.xcrbungeetools.data.DataUser;
import pl.xcrafters.xcrbungeetools.ToolsPlugin;
import pl.xcrafters.xcrbungeetools.data.DataIPBan;

public class MySQLManager {

    ToolsPlugin plugin;
    
    public enum QueryType{INSERT, UPDATE, DELETE};
    
    public MySQLManager(ToolsPlugin plugin) throws SQLException{
        this.plugin = plugin;
        conn = prepareConnection();
        loadAll();
        ProxyServer.getInstance().getScheduler().schedule(plugin, new Runnable(){
            public void run(){
                try {
                    saveAll();
                } catch (SQLException ex) {
                    Logger.getLogger(MySQLManager.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }, 1, 1, TimeUnit.SECONDS);
    }
    
    private Connection conn;
    
    public List<DataQuery> queries = new ArrayList();
    private Object queueLock = new Object();

    Gson gson = new Gson();
    
    public Connection prepareConnection() {
        for (int i = 0; i < 5; i++) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                String url = "jdbc:mysql://" + plugin.configManager.mysqlHost + "/" + plugin.configManager.mysqlBase + "?allowReconnect=true";
                return DriverManager.getConnection(url, plugin.configManager.mysqlUser, plugin.configManager.mysqlPass);
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, "CANNOT CONNECT TO DATABASE!", ex);
            } catch (ClassNotFoundException ex) {
                plugin.getLogger().log(Level.SEVERE, "JDBC IS NOT FOUND - CANNOT CONNECT TO DATABASE!", ex);
            }
        }
        return null;
    }

    public Connection getConnection() throws SQLException {
        if(conn.isClosed()){
            conn = prepareConnection();
        }
        return conn;
    }
    
    public void loadAll() throws SQLException{
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM Users");
        ResultSet rs = ps.executeQuery();
        while(rs.next()){
            DataUser user = new DataUser(plugin.dataManager, rs);
            plugin.dataManager.usersByNick.put(user.getNick().toLowerCase(), user);
            plugin.dataManager.usersByUUID.put(user.getUUID(), user);
        }
        ps = conn.prepareStatement("SELECT * FROM Bans");
        rs = ps.executeQuery();
        while(rs.next()){
            DataBan ban = new DataBan(plugin.dataManager, rs);
            plugin.dataManager.bans.add(ban);
        }
        ps = conn.prepareStatement("SELECT * FROM IPBans");
        rs = ps.executeQuery();
        while(rs.next()){
            DataIPBan ipban = new DataIPBan(plugin.dataManager, rs);
            plugin.dataManager.ipBansByIp.put(ipban.getIP().toLowerCase(), ipban);
        }
    }
    
    public void saveAll() throws SQLException {
        Connection conn = getConnection();
        DataQuery[] toSend;
        synchronized(queueLock){
            toSend = new DataQuery[queries.size()];
            toSend = queries.toArray(toSend);
            queries.clear();
        }
        if(toSend.length > 0){
            for(DataQuery dataQuery : toSend){
                String query = dataQuery.data.prepareQuery(dataQuery.type);
                if(query != null){
                    if(conn != null){
                        QueryType type = dataQuery.type;
                         try {
                            if (type == QueryType.INSERT && dataQuery.data != null) {
                                Statement statement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                                statement.execute(query, Statement.RETURN_GENERATED_KEYS);
                                ResultSet rs = statement.getGeneratedKeys();
                                while (rs.next()) {
                                    dataQuery.data.setPrimary(rs.getInt(1));
                                }
                                rs.close();
                                statement.close();
                            } else {
                                PreparedStatement statement = conn.prepareStatement(query);
                                statement.executeUpdate();
                                statement.close();
                            }

                             JsonObject object = new JsonObject();
                             object.addProperty("id", dataQuery.data.getPrimary());
                             object.addProperty("instance", plugin.redisManager.getInstance());
                             plugin.redisManager.sendMessage(dataQuery.data.getUpdateChannel(type), gson.toJson(object));
                        } catch (SQLException ex) {
                            plugin.getLogger().log(Level.WARNING, "Wystapil blad podczas zapisu query: " + query);
                            plugin.getLogger().log(Level.WARNING, ex.getMessage());
                        }
                    }
                    else
                    {
                        queries.add(dataQuery);
                    }
                }
            }
        }
    }

    public DataBan loadBan(int primary) {
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM Bans WHERE banID = ?");
            ps.setInt(1, primary);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new DataBan(plugin.dataManager, rs);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public DataIPBan loadIPBan(int primary) {
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM IPBans WHERE banID = ?");
            ps.setInt(1, primary);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new DataIPBan(plugin.dataManager, rs);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public DataUser loadUser(int primary) {
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM Users WHERE userID = ?");
            ps.setInt(1, primary);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new DataUser(plugin.dataManager, rs);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return null;
    }
    
}
