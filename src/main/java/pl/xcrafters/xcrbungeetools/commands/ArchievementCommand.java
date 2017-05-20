package pl.xcrafters.xcrbungeetools.commands;

import java.util.ArrayList;
import java.util.List;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import pl.xcrafters.xcrbungeeconnect.ConnectAPI;
import pl.xcrafters.xcrbungeetools.ToolsPlugin;

public class ArchievementCommand extends Command implements TabExecutor{

    ToolsPlugin plugin;
    
    public ArchievementCommand(ToolsPlugin plugin){
        super("archievement", "tools.archievement", "av");
        this.plugin = plugin;
        ProxyServer.getInstance().getPluginManager().registerCommand(plugin, this);
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args){
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
            sender.sendMessage(plugin.color("&9Poprawne uzycie: &b/archievement <nick> <nazwa>"));
            return;
        }
        String nick = args[0];
        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(nick);
        if(player != null){
            nick = player.getName();
        }
        String name = args[1];
        for(int i=2; i<args.length; i++){
            name += " " + args[i];
        }
        ConnectAPI.broadcastMessage(plugin.color("&a" + nick + " &fhas just earned the archievement &a[" + name + "]"));
    }
    
}
