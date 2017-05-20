package pl.xcrafters.xcrbungeetools.commands;

import java.util.ArrayList;
import java.util.List;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import pl.xcrafters.xcrbungeeconnect.ConnectAPI;
import pl.xcrafters.xcrbungeetools.ToolsPlugin;

public class SummonCommand extends Command implements TabExecutor {

    ToolsPlugin plugin;
    
    public SummonCommand(ToolsPlugin plugin){
        super("summon", "tools.summon", "tphere", "s");
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
        if(sender.equals(ProxyServer.getInstance().getConsole())){
            sender.sendMessage(plugin.color("&cTa komenda nie moze byc wywolana z konsoli!"));
            return;
        }
        if(args.length == 0){
            sender.sendMessage(plugin.color("&9Poprawne uzycie: &b/summon <gracz>"));
            return;
        }
        ProxiedPlayer player = (ProxiedPlayer) sender;
        String nick = ConnectAPI.getExactNick(args[0]);
        if(nick == null){
            sender.sendMessage(plugin.color("&cGracz o nicku &6" + args[0] + " &cnie jest online!"));
            return;
        }
        ServerInfo info = ConnectAPI.getServer(nick);
        plugin.handleTeleportation(nick, info, player.getName(), player.getServer().getInfo(), 0, null);
        player.sendMessage(plugin.color("&9Przywolales gracza &b" + nick + " &9do siebie!"));
        ConnectAPI.sendMessage(nick, plugin.color("&9Zostales przywolany do gracza &b" + player.getName() + "&9!"));
    }
    
}
