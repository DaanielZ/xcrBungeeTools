package pl.xcrafters.xcrbungeetools.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import pl.xcrafters.xcrbungeetools.mysql.DataQuery;
import pl.xcrafters.xcrbungeetools.mysql.MySQLManager;

public class DataUser implements DataInterface {

    DataManager dataManager;
    
    private int id;
    
    @Override
    public void setPrimary(int id){
        this.id = id;
    }
    
    @Override
    public int getPrimary(){
        return id;
    }
    
    public DataUser(DataManager dataManager){
        this.dataManager = dataManager;
    }

    public DataUser(DataManager dataManager, ResultSet rs) throws SQLException{
        this.dataManager = dataManager;
        id = rs.getInt("userID");
        nick = rs.getString("nick");
        if(rs.getString("uuid") != null && !rs.getString("uuid").equals("")){
            uuid = UUID.fromString(rs.getString("uuid"));
        }
        muteReason = rs.getString("mute");
        muteTime = rs.getLong("muteTime");
        homeWorld = rs.getString("homeWorld");
        homeX = rs.getInt("homeX");
        homeY = rs.getInt("homeY");
        homeZ = rs.getInt("homeZ");
        gamemode = rs.getInt("gamemode");
        fly = rs.getInt("fly") == 1;
        god = rs.getInt("god") == 1;
        op = rs.getInt("op") == 1;
        timePlayed = rs.getLong("timePlayed");
    }
    
    private boolean cNick, cUUID, cMuteReason, cMuteTime, cHome, cGamemode, cFly, cGod, cOp, cTimePlayed;
    
    private String nick, muteReason, homeWorld;
    private UUID uuid;
    private Long muteTime, messageTime, timePlayed, lastJoin;
    private int homeX, homeY, homeZ;
    private int gamemode;
    private boolean fly, god, op;
    List<UUID> ignored = new ArrayList();
    
    public boolean online = false;
    public boolean vanished = false;
    
    public void setNick(String nick){ this.nick = nick; cNick = true; }
    public void setUUID(UUID uuid){ this.uuid = uuid; cUUID = true; }
    public void setMuteReason(String muteReason){ this.muteReason = muteReason; cMuteReason = true; }
    public void setMuteTime(Long muteTime){ this.muteTime = muteTime; cMuteTime = true; }
    public void setHome(String world, int x, int y, int z){ this.homeWorld = world; this.homeX = x; this.homeY = y; this.homeZ = z; cHome = true; }
    public void setGamemode(int gamemode){ this.gamemode = gamemode; cGamemode = true; }
    public void setFly(boolean fly){ this.fly = fly; cFly = true; }
    public void setGod(boolean god){ this.god = god; cGod = true; }
    public void setOp(boolean op){ this.op = op; cOp = true; }
    public void setMessageTime(Long messageTime){ this.messageTime = messageTime; }
    public void setTimePlayed(Long timePlayed){ this.timePlayed = timePlayed; cTimePlayed = true; }
    public void setLastJoin(Long lastJoin){ this.lastJoin = lastJoin; }
    public void loadIgnoredPlayers() { this.ignored = dataManager.plugin.redisManager.loadIgnored(this); }
    public void unloadIgnoredPlayers() { this.ignored = new ArrayList(); }
    public void addIgnored(UUID uuid){ this.ignored.add(uuid); dataManager.plugin.redisManager.saveIgnored(this); }
    public void removeIgnored(UUID uuid){ this.ignored.remove(uuid); dataManager.plugin.redisManager.saveIgnored(this); }
    
    public String getNick(){ return nick; }
    public UUID getUUID(){ return uuid; }
    public String getMuteReason(){ return muteReason; }
    public Long getMuteTime(){ return muteTime; }
    public String getHomeWorld(){ return homeWorld; }
    public int getHomeX(){ return homeX; }
    public int getHomeY(){ return homeY; }
    public int getHomeZ(){ return homeZ; }
    public int getGamemode(){ return gamemode; }
    public boolean getFly(){ return fly; }
    public boolean getGod(){ return god; }
    public boolean getOp(){ return op; }
    public Long getMessageTime(){ return messageTime; }
    public Long getTimePlayed(){ return timePlayed; }
    public Long getLastJoin(){ return lastJoin; }
    public List<UUID> getIgnored(){ return ignored; }
    
    @Override
    public void insert(){
        dataManager.usersByNick.put(nick.toLowerCase(), this);
        dataManager.usersByUUID.put(uuid, this);
        dataManager.plugin.mySqlManager.queries.add(new DataQuery(this, MySQLManager.QueryType.INSERT));
    }
    
    @Override
    public void update(){
        if(cNick) {
            for(Map.Entry<String, DataUser> entry : dataManager.usersByNick.entrySet()) {
                if(entry.getValue().equals(this)) {
                    dataManager.usersByNick.remove(entry.getKey());
                    break;
                }
            }
            dataManager.usersByNick.put(nick.toLowerCase(), this);
        }
        dataManager.plugin.mySqlManager.queries.add(new DataQuery(this, MySQLManager.QueryType.UPDATE));
    }
    
    @Override
    public void delete(){
        dataManager.usersByNick.remove(nick.toLowerCase());
        dataManager.usersByUUID.remove(uuid, this);
        dataManager.plugin.mySqlManager.queries.add(new DataQuery(this, MySQLManager.QueryType.DELETE));
    }
    
    @Override
    public String prepareQuery(MySQLManager.QueryType type){
        String query = null;
        List<String> columns = new ArrayList();
        List<String> values = new ArrayList();
        if(cNick){
            cNick = false;
            columns.add("nick");
            values.add(nick);
        }
        if(cUUID){
            cUUID = false;
            columns.add("uuid");
            values.add(uuid.toString());
        }
        if(cMuteReason){
            cMuteReason = false;
            columns.add("mute");
            values.add(muteReason);
        }
        if(cMuteTime){
            cMuteTime = false;
            columns.add("muteTime");
            values.add(String.valueOf(muteTime));
        }
        if(cHome){
            cHome = false;
            columns.add("homeWorld");
            columns.add("homeX");
            columns.add("homeY");
            columns.add("homeZ");
            values.add(homeWorld);
            values.add(String.valueOf(homeX));
            values.add(String.valueOf(homeY));
            values.add(String.valueOf(homeZ));
        }
        if(cGamemode){
            cGamemode = false;
            columns.add("gamemode");
            values.add(String.valueOf(gamemode));
        }
        if(cFly){
            cFly = false;
            columns.add("fly");
            values.add(fly ? "1" : "0");
        }
        if(cGod){
            cGod = false;
            columns.add("god");
            values.add(god ? "1" : "0");
        }
        if(cOp){
            cOp = false;
            columns.add("op");
            values.add(op ? "1" : "0");
        }
        if(cTimePlayed){
            cTimePlayed = false;
            columns.add("timePlayed");
            values.add(String.valueOf(timePlayed));
        }
        if(type == MySQLManager.QueryType.DELETE){
            query = "DELETE FROM Users ";
            query += "WHERE userID=" + getPrimary();
        }
        else if(!values.isEmpty() && !columns.isEmpty()){
            if(type == MySQLManager.QueryType.UPDATE){
                query = "UPDATE Users SET ";
                for(String column : columns){
                    int index = columns.indexOf(column);
                    String value = values.get(index);
                    String comma = "";
                    if(index > 0){
                        comma = ",";
                    }
                    if(value != null){
                        query += comma + column + "='" + value + "'";
                    } else {
                        query += comma + column + "=NULL";
                    }
                }
                query += " WHERE userID=" + getPrimary();
            }
            else if(type == MySQLManager.QueryType.INSERT){
                query = "INSERT INTO Users (";
                for(String column : columns){
                    int index = columns.indexOf(column);
                    String comma = "";
                    if(index > 0){
                        comma = ",";
                    }
                    query += comma + column;
                }
                query += ") VALUES (";
                for(String value : values){
                    int index = values.indexOf(value);
                    String comma = "";
                    if(index > 0){
                        comma = ",";
                    }
                    if (value != null){
                        query += comma + "'" + value + "'";
                    } else {
                        query += comma + "NULL";
                    }
                }
                query += ")";
            }
        }
        return query;
    }

    public String getUpdateChannel(MySQLManager.QueryType type) {
        switch (type) {
            case INSERT:
                return "ToolsInsertUser";
            case UPDATE:
                return "ToolsUpdateUser";
            case DELETE:
                return "ToolsDeleteUser";
            default:
                return null;
        }
    }

    public void synchronize() {
        try {
            Connection conn = dataManager.plugin.mySqlManager.getConnection();

            PreparedStatement ps = conn.prepareStatement("SELECT * FROM Users WHERE userID = ?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                id = rs.getInt("userID");
                nick = rs.getString("nick");
                if(rs.getString("uuid") != null && !rs.getString("uuid").equals("")){
                    uuid = UUID.fromString(rs.getString("uuid"));
                }
                muteReason = rs.getString("mute");
                muteTime = rs.getLong("muteTime");
                homeWorld = rs.getString("homeWorld");
                homeX = rs.getInt("homeX");
                homeY = rs.getInt("homeY");
                homeZ = rs.getInt("homeZ");
                gamemode = rs.getInt("gamemode");
                fly = rs.getInt("fly") == 1;
                god = rs.getInt("god") == 1;
                op = rs.getInt("op") == 1;
                timePlayed = rs.getLong("timePlayed");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
}
