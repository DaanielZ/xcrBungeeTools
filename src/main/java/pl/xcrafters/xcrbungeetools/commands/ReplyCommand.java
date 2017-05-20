package pl.xcrafters.xcrbungeetools.commands;

import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import pl.xcrafters.xcrbungeeconnect.ConnectAPI;
import pl.xcrafters.xcrbungeetools.data.DataUser;
import pl.xcrafters.xcrbungeetools.ToolsPlugin;

public class ReplyCommand extends Command {

    ToolsPlugin plugin;
    
    public ReplyCommand(ToolsPlugin plugin){
        super("reply", "tools.reply", "r");
        this.plugin = plugin;
        ProxyServer.getInstance().getPluginManager().registerCommand(plugin, this);
    }
    
    Gson gson = new Gson();
    
    @Override
    public void execute(CommandSender sender, String[] args){
        if(args.length == 0){
            sender.sendMessage(plugin.color("&9Poprawne uzycie: &b/reply <wiadomosc>"));
            return;
        }
        ProxiedPlayer player = (ProxiedPlayer) sender;
        UUID uuid = plugin.lastMessage.get(player.getUniqueId());
        if(uuid == null || !ConnectAPI.isOnline(uuid)){
            sender.sendMessages(plugin.color("&cGracz ktoremu chcesz odpowiedziec nie jest online!"));
            return;
        }
        DataUser user = plugin.dataManager.getUserByNick(sender.getName());
        if(user != null){
            if(user.getMessageTime() != null && System.currentTimeMillis() - user.getMessageTime() <= 1000 && !player.hasPermission("tools.tell.bypass")){
                sender.sendMessage(plugin.color("&cWykryto spam!"));
                return;
            }
            user.setMessageTime(System.currentTimeMillis());
        }
        String msg = args[0];
        for(int i=1; i<args.length; i++){
            msg += " " + args[i];
        }
        DataUser other = plugin.dataManager.getUserByUUID(uuid);
        sender.sendMessage(plugin.color("&6[ja -> " + other.getNick() + "] &7") + msg);
        if(!other.getIgnored().contains(sender.getName().toLowerCase())) {
            ConnectAPI.sendMessage(uuid, plugin.color("&6[" + sender.getName() + " -> ja] &7") + msg);
        }
        for(String spy : plugin.socialSpy){
            ProxiedPlayer admin = ProxyServer.getInstance().getPlayer(spy);
            if(admin != null){
                admin.sendMessage(plugin.color("&7&oSOCIAL SPY: &6[" + sender.getName() + " -> " + other.getNick() + "] &7") + msg);
            }
        }
        plugin.lastMessage.put(uuid, player.getUniqueId());
        plugin.lastMessage.put(player.getUniqueId(), uuid);

        JsonObject object = new JsonObject();
        object.addProperty("uuid", uuid.toString());
        object.addProperty("senderUUID", player.getUniqueId().toString());
        object.addProperty("message", msg);
        object.addProperty("instance", plugin.redisManager.getInstance());
        plugin.redisManager.sendMessage("SendMessage", gson.toJson(object));
    }
    
}
