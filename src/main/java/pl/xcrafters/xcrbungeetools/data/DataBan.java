package pl.xcrafters.xcrbungeetools.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import pl.xcrafters.xcrbungeetools.mysql.DataQuery;
import pl.xcrafters.xcrbungeetools.mysql.MySQLManager;

public class DataBan implements DataInterface{

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
    
    public DataBan(DataManager dataManager){
        this.dataManager = dataManager;
    }
    
    public DataBan(DataManager dataManager, ResultSet rs) throws SQLException{
        this.dataManager = dataManager;
        id = rs.getInt("banID");
        nick = rs.getString("nick");
        uuid = rs.getString("uuid") != null ? UUID.fromString(rs.getString("uuid")) : null;
        reason = rs.getString("powod");
        time = rs.getLong("time");
        created = rs.getLong("created");
        admin = rs.getString("admin") != null ? dataManager.getUserByNick(rs.getString("admin")) : null;
        active = rs.getInt("active") == 1;
        unbanAdmin = rs.getString("unban") != null ? dataManager.getUserByNick(rs.getString("unban")) : null;
    }
    
    private boolean cNick, cUUID, cReason, cTime, cCreated, cAdmin, cActive, cUnbanAdmin;
    
    private String nick, reason;
    private UUID uuid;
    private Long time, created;
    private DataUser admin, unbanAdmin;
    private boolean active;

    public void setNick(String nick){ this.nick = nick; cNick = true; }
    public void setUUID(UUID uuid){ this.uuid = uuid; cUUID = true; }
    public void setReason(String reason){ this.reason = reason; cReason = true; }
    public void setTime(Long time){ this.time = time; cTime = true; }
    public void setCreated(Long created){ this.created = created; cCreated = true; }
    public void setAdmin(DataUser admin){ this.admin = admin; cAdmin = true; }
    public void setActive(boolean active){ this.active = active; cActive = true; }
    public void setUnbanAdmin(DataUser unbanAdmin){ this.unbanAdmin = unbanAdmin; cUnbanAdmin = true; }
    
    public String getNick(){ return nick; }
    public UUID getUUID(){ return uuid; }
    public String getReason(){ return reason; }
    public Long getTime(){ return time; }
    public Long getCreated(){ return created; }
    public DataUser getAdmin(){ return admin; }
    public boolean getActive(){ return active; }
    public DataUser getUnbanAdmin(){ return unbanAdmin; }
    
    @Override
    public void insert(){
        dataManager.bans.add(this);
        dataManager.plugin.mySqlManager.queries.add(new DataQuery(this, MySQLManager.QueryType.INSERT));
    }
    
    @Override
    public void update(){
        dataManager.plugin.mySqlManager.queries.add(new DataQuery(this, MySQLManager.QueryType.UPDATE));
    }
    
    @Override
    public void delete(){
        dataManager.bans.remove(this);
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
            values.add(uuid != null ? uuid.toString() : null);
        }
        if(cReason){
            cReason = false;
            columns.add("powod");
            values.add(reason);
        }
        if(cTime){
            cTime = false;
            columns.add("time");
            values.add(String.valueOf(time));
        }
        if(cCreated){
            cCreated = false;
            columns.add("created");
            values.add(String.valueOf(created));
        }
        if(cAdmin){
            cAdmin = false;
            columns.add("admin");
            values.add(admin != null ? admin.getNick() : null);
        }
        if(cActive){
            cActive = false;
            columns.add("active");
            values.add(String.valueOf(active ? 1 : 0));
        }
        if(cUnbanAdmin){
            cUnbanAdmin = false;
            columns.add("unban");
            values.add(unbanAdmin != null ? unbanAdmin.getNick() : null);
        }
        if(type == MySQLManager.QueryType.DELETE){
            query = "DELETE FROM Bans ";
            query += "WHERE banID=" + getPrimary();
        }
        else if(!values.isEmpty() && !columns.isEmpty()){
            if(type == MySQLManager.QueryType.UPDATE){
                query = "UPDATE Bans SET ";
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
                query = "INSERT INTO Bans (";
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
                return "ToolsInsertBan";
            case UPDATE:
                return "ToolsUpdateBan";
            case DELETE:
                return "ToolsDeleteBan";
            default:
                return null;
        }
    }

    public void synchronize() {
        try {
            Connection conn = dataManager.plugin.mySqlManager.getConnection();

            PreparedStatement ps = conn.prepareStatement("SELECT * FROM Bans WHERE banID = ?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                id = rs.getInt("banID");
                nick = rs.getString("nick");
                uuid = rs.getString("uuid") != null ? UUID.fromString(rs.getString("uuid")) : null;
                reason = rs.getString("powod");
                time = rs.getLong("time");
                created = rs.getLong("created");
                admin = rs.getString("admin") != null ? dataManager.getUserByNick(rs.getString("admin")) : null;
                active = rs.getInt("active") == 1;
                unbanAdmin = rs.getString("unban") != null ? dataManager.getUserByNick(rs.getString("unban")) : null;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
}
