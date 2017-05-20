package pl.xcrafters.xcrbungeetools.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import pl.xcrafters.xcrbungeetools.ToolsPlugin;

public class DataManager {

    ToolsPlugin plugin;
    
    public DataManager(ToolsPlugin plugin){
        this.plugin = plugin;
    }

    public enum InventoryType {INVENTORY, ENDERCHEST};
    
    public HashMap<String, DataUser> usersByNick = new HashMap();
    public HashMap<UUID, DataUser> usersByUUID = new HashMap();
    public List<DataBan> bans = new ArrayList();
    public HashMap<String, DataIPBan> ipBansByIp = new HashMap();
    public HashMap<String, List<UUID>> onlinePlayers = new HashMap();
    
    public DataUser getUserByPlayer(ProxiedPlayer player){
        return getUserByNick(player.getName());
    }
    
    public DataUser getUserByNick(String nick){
        return usersByNick.get(nick.toLowerCase());
    }
    
    public DataUser getUserByUUID(UUID uuid){
        return usersByUUID.get(uuid);
    }
    
    public DataBan getActiveBanByPlayer(ProxiedPlayer player){
        return getActiveBanByNick(player.getName());
    }
    
    public DataBan getActiveBanByNick(String nick){
        for(DataBan ban : bans){
            if(ban.getNick().equalsIgnoreCase(nick) && ban.getActive()){
                return ban;
            }
        }
        return null;
    }

    public List<DataBan> getBansByNick(String nick){
        List<DataBan> bans = new ArrayList();
        for(DataBan ban : this.bans){
            if(ban.getNick().equalsIgnoreCase(nick)){
                bans.add(ban);
            }
        }
        return bans;
    }

    public DataBan getActiveBanByUUID(UUID uuid){
        for(DataBan ban : bans){
            if(ban.getUUID() != null && ban.getUUID().equals(uuid) && ban.getActive()){
                return ban;
            }
        }
        return null;
    }

    public List<DataBan> getBansByUUID(UUID uuid){
        List<DataBan> bans = new ArrayList();
        for(DataBan ban : this.bans){
            if(ban.getUUID() != null && ban.getUUID().equals(uuid)){
                bans.add(ban);
            }
        }
        return bans;
    }
    
    public DataIPBan getIPBanByIp(String ip){
        return ipBansByIp.get(ip);
    }

    public DataBan getBanByID(int id) {
        for(DataBan ban : bans) {
            if(ban.getPrimary() == id) {
                return ban;
            }
        }

        return null;
    }

    public DataIPBan getIPBanByID(int id) {
        for(DataIPBan ban : ipBansByIp.values()) {
            if(ban.getPrimary() == id) {
                return ban;
            }
        }

        return null;
    }

    public DataUser getUserByID(int id) {
        for(DataUser user : usersByNick.values()) {
            if(user.getPrimary() == id) {
                return user;
            }
        }

        return null;
    }
    
    public DataUser createUser(){
        return new DataUser(this);
    }
    
    public DataBan createBan(){
        return new DataBan(this);
    }
    
    public DataIPBan createIPBan(){
        return new DataIPBan(this);
    }
    
}
