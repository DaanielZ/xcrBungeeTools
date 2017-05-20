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

public class FlyCommand extends Command implements TabExecutor {

    ToolsPlugin plugin;
    
    public FlyCommand(ToolsPlugin plugin){
        super("fly", "tools.fly");
        this.plugin = plugin;
        ProxyServer.getInstance().getPluginManager().registerCommand(plugin, this);
    }

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
            sender.sendMessage(plugin.color("&9Poprawne uzycie: &b/fly <nick>"));
            return;
        }
        DataUser user = null;
        if(args.length == 0){
            user = plugin.dataManager.getUserByPlayer(((ProxiedPlayer) sender));
        } else if(args.length > 0){
            if(!sender.hasPermission("tools.fly.others")){
                sender.sendMessage(plugin.color("&cNie masz uprawnien do zmiany trybu latania dla innych graczy!"));
                return;
            }
            user = plugin.dataManager.getUserByNick(args[0]);
        }
        if (user == null) {
            sender.sendMessage(plugin.color("&cNie znaleziono gracza o nicku &6" + args[0] + " &cw bazie danych!"));
            return;
        }
        boolean fly = !user.getFly();
        ServerInfo info = ConnectAPI.getServer(user.getUUID());
        if(info != null) {
            plugin.setFly(user.getNick(), info, fly);
            ConnectAPI.sendMessage(user.getUUID(), plugin.color("&9Twoj tryb latania zostal &b" + (fly ? "wlaczony" : "wylaczony") + "&9."));
        }
        user.setFly(fly);
        user.update();
//        SektoryAPI.setFly(user.getUUID(), fly); //TODO
        if(!sender.getName().equals(user.getNick())){
            sender.sendMessage(plugin.color("&9Tryb latania zostal &b" + (fly ? "wlaczony" : "wylaczony") + " &9dla gracza &b" + user.getNick() + "&9."));
        }
    }
    
}
