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
import pl.xcrafters.xcrbungeetools.data.DataIPBan;

public class BanIPCommand extends Command implements TabExecutor{

    ToolsPlugin plugin;
    
    public BanIPCommand(ToolsPlugin plugin){
        super("banip", "tools.banip");
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
            sender.sendMessage(plugin.color("&9Poprawne uzycie: &b/banip <ip/nick> <powod>"));
            return;
        }
        String ip = ConnectAPI.getIP(args[0]);
        String reason = args[1];
        for(int i=2; i<args.length; i++){
            reason += " " + args[i];
        }
        if(ip == null && !args[0].matches("^(?:[0-9]{1,3}\\.){3}[0-9]{1,3}$")){
            sender.sendMessage(plugin.color("&cGracz o nicku &6" + args[0] + " &cnie jest online!"));
            return;
        } else {
            ip = args[0];
        }
        DataIPBan ipban = plugin.dataManager.getIPBanByIp(ip);
        if(ipban != null){
            sender.sendMessage(plugin.color("&cAdres IP &6" + ip + " &cjest juz zbanowany!"));
            return;
        }
        ipban = plugin.dataManager.createIPBan();
        ipban.setIP(ip);
        ipban.setReason(reason);
        ipban.setAdmin((sender.equals(ProxyServer.getInstance().getConsole())) ? null : plugin.dataManager.getUserByNick(sender.getName()));
        ipban.insert();

        ConnectAPI.kickIP(ipban.getIP(), plugin.color("&cTwoj adres IP zostal zbanowany przez " + (ipban.getAdmin() != null ? ipban.getAdmin().getNick() : "konsole") + ".\n Powod:" + reason));
        ConnectAPI.broadcastMessage(plugin.color("&c[BAN] Adres IP " + ipban.getIP() + " zostal zbanowany przez " + (ipban.getAdmin() != null ? ipban.getAdmin().getNick() : "konsole") + ". Powod: " + reason), "tools.banip.notify");

//        for(ProxiedPlayer online : ProxyServer.getInstance().getPlayers()){
//            if(online.getAddress().getAddress().getHostAddress().equals((player != null ? player.getAddress().getAddress().getHostAddress() : ip))){
//                online.disconnect(plugin.color("&cTwoj adres IP zostal zbanowany przez " + (ipban.getAdmin() != null ? ipban.getAdmin().getNick() : "konsole") + ".\n Powod:" + reason));
//            } else if(online.hasPermission("tools.banip.notify")){
//                online.sendMessage(plugin.color("&c[BAN] Adres IP " + ipban.getIP() + " zostal zbanowany przez " + (ipban.getAdmin() != null ? ipban.getAdmin().getNick() : "konsole") + ". Powod: " + reason));
//            }
//        }
//        ProxyServer.getInstance().getConsole().sendMessage(plugin.color("&c[BAN] Adres IP " + ipban.getIP() + " zostal zbanowany przez " + (ipban.getAdmin() != null ? ipban.getAdmin().getNick() : "konsole") + ". Powod: " + reason));
    }
    
}
