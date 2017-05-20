package pl.xcrafters.xcrbungeetools.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import pl.xcrafters.xcrbungeeconnect.ConnectAPI;
import pl.xcrafters.xcrbungeeperms.PermissionAPI;
import pl.xcrafters.xcrbungeetools.ToolsPlugin;

public class KickCommand extends Command implements TabExecutor{

    ToolsPlugin plugin;
    
    public KickCommand(ToolsPlugin plugin){
        super("kick", "tools.kick");
        this.plugin = plugin;
        ProxyServer.getInstance().getPluginManager().registerCommand(plugin, this);
    }
    
    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args){
        List<String> players = new ArrayList();
        for(String nick : ConnectAPI.getNicks()){
            if(args.length == 0 || nick.toLowerCase().startsWith(args[(args.length - 1 >= 0 ? args.length - 1 : 0)].toLowerCase())){
                players.add(nick);
            }
        }
        return players;
    }
    
    @Override
    public void execute(CommandSender sender, String[] args){
        if(args.length < 2){
            sender.sendMessage(plugin.color("&9Poprawne uzycie: &b/kick <gracz> <powod>"));
            return;
        }
        UUID uuid = ConnectAPI.getUUID(args[0]);
        if(uuid == null){
            sender.sendMessage(plugin.color("&cGracz o nicku &6" + args[0] + " &cnie jest online!"));
            return;
        }
        if(PermissionAPI.hasPermission(uuid, "tools.kick.bypass")) {
            sender.sendMessage(plugin.color("&cTen gracz nie moze byc wyrzucony!"));
            return;
        }
        String nick = ConnectAPI.getExactNick(args[0]);
        String reason = args[1];
        for(int i=2; i<args.length; i++){
            reason += " " + args[i];
        }
        ConnectAPI.kickPlayer(uuid, plugin.color("&cZostales wyrzucony przez " + (sender instanceof ProxiedPlayer ? sender.getName() : "konsole") + ".\n Powod: " + reason));
        ConnectAPI.broadcastMessage(plugin.color("&c[KICK] " + nick + " zostal wyrzucony przez " + (sender instanceof ProxiedPlayer ? sender.getName() : "konsole") + ". Powod: " + reason), "tools.kick.notify");
    }
    
}
