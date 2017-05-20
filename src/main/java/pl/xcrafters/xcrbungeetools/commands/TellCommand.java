package pl.xcrafters.xcrbungeetools.commands;

import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import pl.xcrafters.xcrbungeeconnect.ConnectAPI;
import pl.xcrafters.xcrbungeeperms.PermissionAPI;
import pl.xcrafters.xcrbungeetools.ToolsPlugin;
import pl.xcrafters.xcrbungeetools.data.DataUser;

public class TellCommand extends Command {

    ToolsPlugin plugin;
    
    public TellCommand(ToolsPlugin plugin){
        super("tell", "tools.tell", "t", "msg", "m");
        this.plugin = plugin;
        ProxyServer.getInstance().getPluginManager().registerCommand(plugin, this);
    }
    
    Gson gson = new Gson();
    
    @Override
    public void execute(CommandSender sender, String[] args){
        if(!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage("§cTa komenda nie moze byc wywolana z konsoli!");
            return;
        }

        if(args.length < 2){
            sender.sendMessage(plugin.color("&9Poprawne uzycie: &b/tell <nick> <wiadomosc>"));
            return;
        }
        String nick = args[0];
        UUID uuid = ConnectAPI.getUUID(nick);
        if(uuid == null || (PermissionAPI.hasPermission(uuid, "tools.tell.no-message") && !sender.hasPermission("tools.tell.no-message")) || plugin.dataManager.getUserByUUID(uuid) != null && plugin.dataManager.getUserByUUID(uuid).vanished && !sender.hasPermission("tools.vanish.see")){
            sender.sendMessage(plugin.color("&cGracz o nicku &6" + nick + " &cnie jest online!"));
            return;
        }
        DataUser user = plugin.dataManager.getUserByNick(sender.getName());
        if(user != null){
            if(user.getMessageTime() != null && System.currentTimeMillis() - user.getMessageTime() <= 1000 && !(PermissionAPI.hasPermission(uuid, "tools.tell.bypass"))) {
                sender.sendMessage(plugin.color("&cWykryto spam!"));
                return;
            }
            user.setMessageTime(System.currentTimeMillis());
        }
        String msg = args[1];
        for(int i=2; i<args.length; i++){
            msg += " " + args[i];
        }
        DataUser other = plugin.dataManager.getUserByUUID(uuid);
        String exactName = other.getNick();
        sender.sendMessage(plugin.color("&6[ja -> " + exactName + "] &7") + msg);
        if(!other.getIgnored().contains(sender.getName().toLowerCase())) {
            ConnectAPI.sendMessage(uuid, plugin.color("&6[" + sender.getName() + " -> ja] &7") + msg);
        }
        for(String spy : plugin.socialSpy){
            ProxiedPlayer admin = ProxyServer.getInstance().getPlayer(spy);
            if(admin != null){
                admin.sendMessage(plugin.color("&7&oSOCIAL SPY: &6[" + sender.getName() + " -> " + exactName + "] &7") + msg);
            }
        }

        UUID senderUUID = ((ProxiedPlayer)sender).getUniqueId();

        plugin.lastMessage.put(uuid, senderUUID);
        plugin.lastMessage.put(senderUUID, uuid);

        JsonObject object = new JsonObject();
        object.addProperty("uuid", uuid.toString());
        object.addProperty("senderUUID", senderUUID.toString());
        object.addProperty("message", msg);
        object.addProperty("instance", plugin.redisManager.getInstance());
        plugin.redisManager.sendMessage("SendMessage", gson.toJson(object));
    }
    
}
