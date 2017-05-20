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
import pl.xcrafters.xcrbungeetools.data.DataUser;

public class MuteCommand extends Command implements TabExecutor{

    ToolsPlugin plugin;
    
    public MuteCommand(ToolsPlugin plugin){
        super("mute", "tools.mute");
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
            sender.sendMessage(plugin.color("&9Poprawne uzycie: &b/mute <nick> <powod>"));
            return;
        }
        String nick = args[0];
        UUID uuid = ConnectAPI.getUUID(nick);
        if(PermissionAPI.hasPermission(uuid, "tools.mute.bypass")) {
            sender.sendMessage(plugin.color("&cTen gracz nie moze byc wyciszony!"));
            return;
        }
        DataUser user = plugin.dataManager.getUserByNick(nick);
        if(user == null){
            sender.sendMessage(plugin.color("&cNie znaleziono gracza o nicku &6" + nick + " &cw bazie danych!"));
            return;
        }
        String reason = args[1];
        for(int i=2; i<args.length; i++){
            reason += " " + args[i];
        }
        user.setMuteTime(0L);
        user.setMuteReason(reason);
        user.update();

        ConnectAPI.sendMessage(uuid, plugin.color("&cZostales wyciszony przez " + ((sender instanceof ProxiedPlayer) ? sender.getName() : "konsole") + ". Powod: " + reason));
        ConnectAPI.broadcastMessage(plugin.color("&c[MUTE] " + user.getNick() + " zostal wyciszony przez " + ((sender instanceof ProxiedPlayer) ? sender.getName() : "konsole") + ". Powod: " + reason), "tools.mute.notify");
    }
    
}
