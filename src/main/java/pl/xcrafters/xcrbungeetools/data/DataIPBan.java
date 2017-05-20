package pl.xcrafters.xcrbungeetools.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import pl.xcrafters.xcrbungeetools.mysql.DataQuery;
import pl.xcrafters.xcrbungeetools.mysql.MySQLManager;

public class DataIPBan implements DataInterface{

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
    
    public DataIPBan(DataManager dataManager){
        this.dataManager = dataManager;
    }
    
    public DataIPBan(DataManager dataManager, ResultSet rs) throws SQLException{
        this.dataManager = dataManager;
        id = rs.getInt("banID");
        ip = rs.getString("ip");
        reason = rs.getString("powod");
        admin = rs.getString("admin") != null ? dataManager.getUserByNick(rs.getString("admin")) : null;
    }
    
    private boolean cIP, cReason, cAdmin;
    
    private String ip, reason;
    private DataUser admin;
    
    public void setIP(String ip){ this.ip = ip; cIP = true; }
    public void setReason(String reason){ this.reason = reason; cReason = true; }
    public void setAdmin(DataUser admin){ this.admin = admin; cAdmin = true; }
    
    public String getIP(){ return ip; }
    public String getReason(){ return reason; }
    public DataUser getAdmin(){ return admin; }
    
    @Override
    public void insert(){
        dataManager.ipBansByIp.put(ip.toLowerCase(), this);
        dataManager.plugin.mySqlManager.queries.add(new DataQuery(this, MySQLManager.QueryType.INSERT));
    }
    
    @Override
    public void update(){
        dataManager.plugin.mySqlManager.queries.add(new DataQuery(this, MySQLManager.QueryType.UPDATE));
    }
    
    @Override
    public void delete(){
        dataManager.ipBansByIp.remove(ip.toLowerCase());
        dataManager.plugin.mySqlManager.queries.add(new DataQuery(this, MySQLManager.QueryType.DELETE));
    }
    
    @Override
    public String prepareQuery(MySQLManager.QueryType type){
        String query = null;
        List<String> columns = new ArrayList();
        List<String> values = new ArrayList();
        if(cIP){
            cIP = false;
            columns.add("ip");
            values.add(ip);
        }
        if(cReason){
            cReason = false;
            columns.add("powod");
            values.add(reason);
        }
        if(cAdmin){
            cAdmin = false;
            columns.add("admin");
            values.add(admin != null ? admin.getNick() : null);
        }
        if(type == MySQLManager.QueryType.DELETE){
            query = "DELETE FROM IPBans ";
            query += "WHERE banID=" + getPrimary();
        }
        else if(!values.isEmpty() && !columns.isEmpty()){
            if(type == MySQLManager.QueryType.UPDATE){
                query = "UPDATE IPBans SET ";
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
                query += " WHERE banID=" + getPrimary();
            }
            else if(type == MySQLManager.QueryType.INSERT){
                query = "INSERT INTO IPBans (";
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
                return "ToolsInsertIPBan";
            case UPDATE:
                return "ToolsUpdateIPBan";
            case DELETE:
                return "ToolsDeleteIPBan";
            default:
                return null;
        }
    }

    public void synchronize() {
        try {
            Connection conn = dataManager.plugin.mySqlManager.getConnection();

            PreparedStatement ps = conn.prepareStatement("SELECT * FROM IPBans WHERE banID = ?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                id = rs.getInt("banID");
                ip = rs.getString("ip");
                reason = rs.getString("powod");
                admin = rs.getString("admin") != null ? dataManager.getUserByNick(rs.getString("admin")) : null;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
}
