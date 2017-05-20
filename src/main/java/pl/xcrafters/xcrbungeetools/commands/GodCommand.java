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
import pl.xcrafters.xcrbungeetools.data.DataUser;
import pl.xcrafters.xcrbungeetools.ToolsPlugin;

public class GodCommand extends Command implements TabExecutor{

    ToolsPlugin plugin;
    
    public GodCommand(ToolsPlugin plugin){
        super("god", "tools.god");
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
        if(args.length == 0 && sender.equals(ProxyServer.getInstance().getConsole())){
            sender.sendMessage(plugin.color("&9Poprawne uzycie: &b/god <nick>"));
            return;
        }
        DataUser user = null;
        if(args.length == 0){
            user  = plugin.dataManager.getUserByPlayer(((ProxiedPlayer) sender));
        } else if(args.length == 1){
            if(!sender.hasPermission("tools.god.others")){
                sender.sendMessage(plugin.color("&cNie masz uprawnien do zmiany trybu godmode dla innych graczy!"));
                return;
            }
            user = plugin.dataManager.getUserByNick(args[0]);
        }
        if (user == null) {
            sender.sendMessage(plugin.color("&cNie znaleziono gracza o nicku &6" + args[0] + " &cw bazie danych!"));
            return;
        }
        ServerInfo info = ConnectAPI.getServer(user.getUUID());
        boolean god = !user.getGod();
        if(info != null){
            plugin.setGod(user.getNick(), info, god);
            ConnectAPI.sendMessage(user.getUUID(), plugin.color("&9Tryb godmode zostal &b" + (god ? "wlaczony" : "wylaczony") + "&9."));
        }
        user.setGod(god);
        user.update();
        if(!sender.getName().equals(user.getNick())){
            sender.sendMessage(plugin.color("&9Tryb godmode zostal &b" + (god ? "wlaczony" : "wylaczony") + " &9dla gracza &b" + user.getNick() + "&9."));
        }
    }
    
}
