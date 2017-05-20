package pl.xcrafters.xcrbungeetools.commands;

import java.util.ArrayList;
import java.util.List;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import pl.xcrafters.xcrbungeeconnect.ConnectAPI;
import pl.xcrafters.xcrbungeeperms.PermissionAPI;
import pl.xcrafters.xcrbungeetools.ToolsPlugin;
import pl.xcrafters.xcrbungeetools.data.DataBan;
import pl.xcrafters.xcrbungeetools.data.DataUser;

public class BanCommand extends Command implements TabExecutor{

    ToolsPlugin plugin;
    
    public BanCommand(ToolsPlugin plugin){
        super("ban", "tools.ban");
        this.plugin = plugin;
        ProxyServer.getInstance().getPluginManager().registerCommand(plugin, this);
    }
    
    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args){
        List<String> players = new ArrayList();
        for(ProxiedPlayer player : ProxyServer.getInstance().getPlayers()){
            if(args.length == 0 || (args.length > 0 && player.getName().toLowerCase().startsWith(args[(args.length - 1 >= 0 ? args.length - 1 : 0)].toLowerCase()))){
                players.add(player.getName());
            }
        }
        return players;
    }
    
    @Override
    public void execute(CommandSender sender, String[] args){
        if(args.length < 2){
            sender.sendMessage(plugin.color("&9Poprawne uzycie: &b/ban <nick> <powod>"));
            return;
        }
        String nick = args[0];
        DataUser user = plugin.dataManager.getUserByNick(nick);
        if(user != null && PermissionAPI.hasPermission(user.getUUID(), "tools.ban.bypass")) {
            sender.sendMessage(plugin.color("&cTen gracz nie moze byc zbanowany!"));
            return;
        }
        String reason = args[1];
        for(int i=2; i<args.length; i++){
            reason += " " + args[i];
        }
        DataBan ban = plugin.dataManager.getActiveBanByNick(nick);
        if(ban != null){
            sender.sendMessage(plugin.color("&cGracz o nicku &6" + nick + " &cjest juz zbanowany!"));
            return;
        }
        ban = plugin.dataManager.createBan();
        ban.setNick(nick);
        if(user != null) {
            ban.setUUID(user.getUUID());
        } else {
            ban.setUUID(null);
        }
        ban.setReason(reason);
        ban.setActive(true);
        ban.setTime(0L);
        ban.setCreated(System.currentTimeMillis());
        ban.setAdmin((sender instanceof ProxiedPlayer) ? plugin.dataManager.getUserByNick(sender.getName()) : null);
        ban.insert();
        ConnectAPI.kickPlayer(user == null ? nick : user.getNick(), plugin.color("&cZostales permamentnie zbanowany przez " + (ban.getAdmin() != null ? ban.getAdmin().getNick() : "konsole") + ".\n Powod: " + reason));
        ConnectAPI.broadcastMessage(plugin.color("&c[BAN] " + ban.getNick() + " zostal zbanowany przez " + (ban.getAdmin() != null ? ban.getAdmin().getNick() : "konsole") + ". Powod: " + reason), "tools.ban.notify");
    }
    
}
