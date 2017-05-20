package pl.xcrafters.xcrbungeetools.commands;

import java.util.ArrayList;
import java.util.List;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import pl.xcrafters.xcrbungeetools.data.DataUser;
import pl.xcrafters.xcrbungeetools.ToolsPlugin;

public class UnmuteCommand extends Command implements TabExecutor{

    ToolsPlugin plugin;
    
    public UnmuteCommand(ToolsPlugin plugin){
        super("unmute", "tools.unmute");
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
        if(args.length == 0){
            sender.sendMessage(plugin.color("&9Poprawne uzycie: &b/unmute <nick>"));
            return;
        }
        String nick = args[0];
        DataUser user = plugin.dataManager.getUserByNick(nick);
        if(user == null){
            sender.sendMessage(plugin.color("&cNie znaleziono gracza o nicku &6" + nick + " &cw bazie danych!"));
            return;
        }
        user.setMuteReason(null);
        user.setMuteTime(0L);
        user.update();
        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(nick);
        if(player != null){
            player.sendMessage(plugin.color("&cZostales odciszony przez " + (sender instanceof ProxiedPlayer ? sender.getName() : "konsole") + "."));
        }
        for(ProxiedPlayer online : ProxyServer.getInstance().getPlayers()){
            if(online.hasPermission("tools.unmute.notify")){
                online.sendMessage(plugin.color("&c[UNMUTE] " + user.getNick() + " zostal odciszony przez " + (sender instanceof ProxiedPlayer ? sender.getName() : "konsole") + "."));
            }
        }
        ProxyServer.getInstance().getConsole().sendMessage(plugin.color("&c[UNMUTE] " + user.getNick() + " zostal odciszony przez " + (sender instanceof ProxiedPlayer ? sender.getName() : "konsole") + "."));
    }
    
}
