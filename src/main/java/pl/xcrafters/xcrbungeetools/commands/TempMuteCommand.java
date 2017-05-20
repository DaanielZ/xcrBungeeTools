package pl.xcrafters.xcrbungeetools.commands;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import pl.xcrafters.xcrbungeeconnect.ConnectAPI;
import pl.xcrafters.xcrbungeetools.data.DataUser;
import pl.xcrafters.xcrbungeetools.ToolsPlugin;

public class TempMuteCommand extends Command implements TabExecutor{

    ToolsPlugin plugin;
    
    public TempMuteCommand(ToolsPlugin plugin){
        super("tempmute", "tools.tempmute");
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
        if(args.length < 3){
            sender.sendMessage(plugin.color("&9Poprawne uzycie: &b/tempmute <nick> <czas> <powod>"));
            return;
        }
        String nick = args[0];
        String timeFormat = args[1];
        String reason = args[2];
        Long unmuteTime = System.currentTimeMillis() + plugin.parseTimeDiffInMillis(timeFormat);
        for(int i=3; i<args.length; i++){
            reason += " " + args[i];
        }
        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(nick);
        if(player != null && player.hasPermission("tools.tempmute.bypass")){
            sender.sendMessage(plugin.color("&cTen gracz nie zostac byc wyciszony!"));
            return;
        }
        DataUser user = plugin.dataManager.getUserByNick(nick);
        if(user == null){
            sender.sendMessage(plugin.color("&cNie znaleziono gracza o nicku " + nick + "!"));
            return;
        }
        user.setMuteReason(reason);
        user.setMuteTime(unmuteTime);
        user.update();
        Date date = new Date(user.getMuteTime());
        SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timeShow = dt.format(date);
        if(player != null){
            player.sendMessage(plugin.color("&cZostales wyciszony do " + timeShow + " przez " + (sender instanceof ProxiedPlayer ? sender.getName() : "konsole") + ". Powod: " + reason));
        }
        for(ProxiedPlayer online : ProxyServer.getInstance().getPlayers()){
            if(online.hasPermission("tools.tempmute.notify")){
                online.sendMessage(plugin.color("&c[MUTE] " + user.getNick() + " zostal wyciszony do " + timeShow + " przez " + (sender instanceof ProxiedPlayer ? sender.getName() : "konsole") + ". Powod: " + reason));
            }
        }
        if(!sender.equals(ProxyServer.getInstance().getConsole())){
            ProxyServer.getInstance().getConsole().sendMessage(plugin.color("&c[MUTE] " + user.getNick() + " zostal wyciszony do " + timeShow + " przez " + (sender instanceof ProxiedPlayer ? sender.getName() : "konsole") + ". Powod: " + reason));
        }
    }
    
}
