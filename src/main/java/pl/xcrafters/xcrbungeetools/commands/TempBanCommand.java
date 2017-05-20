package pl.xcrafters.xcrbungeetools.commands;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import pl.xcrafters.xcrbungeeconnect.ConnectAPI;
import pl.xcrafters.xcrbungeetools.data.DataBan;
import pl.xcrafters.xcrbungeetools.data.DataUser;
import pl.xcrafters.xcrbungeetools.ToolsPlugin;

public class TempBanCommand extends Command implements TabExecutor{

    ToolsPlugin plugin;

    public TempBanCommand(ToolsPlugin plugin) {
        super("tempban", "tools.tempban");
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
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(plugin.color("&9Poprawne uzycie: &b/tempban <nick> <czas> <powod>"));
            return;
        }
        String nick = args[0];
        DataBan ban = plugin.dataManager.getActiveBanByNick(nick);
        if (ban != null) {
            sender.sendMessage(plugin.color("&cGracz o nicku &6" + ban.getNick() + " &cjest juz zbanowany!"));
            return;
        }
        String timeFormat = args[1];
        String reason = args[2];
        for (int i = 3; i < args.length; i++) {
            reason += " " + args[i];
        }
        Long unbanTime = System.currentTimeMillis() + plugin.parseTimeDiffInMillis(timeFormat);
        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(nick);
        if (player != null && player.hasPermission("tools.tempban.bypass")) {
            sender.sendMessage(plugin.color("&cPodany gracz nie moze byc zbanowany!"));
            return;
        }
        ban = plugin.dataManager.createBan();
        ban.setNick(nick);
        DataUser user = plugin.dataManager.getUserByNick(nick);
        if(user != null) {
            ban.setUUID(user.getUUID());
        } else {
            ban.setUUID(null);
        }
        ban.setTime(unbanTime);
        ban.setCreated(System.currentTimeMillis());
        ban.setReason(reason);
        ban.setActive(true);
        ban.setAdmin((sender instanceof ProxiedPlayer) ? plugin.dataManager.getUserByNick(sender.getName()) : null);
        ban.insert();
        Date date = new Date(ban.getTime());
        SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timeShow = dt.format(date);
//        if(player != null){
//            player.disconnect(plugin.color("&cZostales zbanowany do " + timeShow + " przez " + (ban.getAdmin() != null ? ban.getAdmin().getNick() : "konsole") + ".\n Powod: " + reason));
//        }
        ConnectAPI.kickPlayer(user == null ? nick : user.getNick(), plugin.color("&cZostales zbanowany do " + timeShow + " przez " + (ban.getAdmin() != null ? ban.getAdmin().getNick() : "konsole") + ".\n Powod: " + reason));
//        for(ProxiedPlayer online : ProxyServer.getInstance().getPlayers()){
//            if(online.hasPermission("tools.ban.notify")){
//                online.sendMessage(plugin.color("&c[BAN] " + ban.getNick() + " zostal zbanowany do " + timeShow + " przez " + (ban.getAdmin() != null ? ban.getAdmin().getNick() : "konsole") + ChatColor.GRAY + ".\n Powod: " + reason));
//            }
//        }
//        ProxyServer.getInstance().getConsole().sendMessage(plugin.color("&c[BAN] " + ban.getNick() + " zostal zbanowany do " + timeShow + " przez " + (ban.getAdmin() != null ? ban.getAdmin().getNick() : "konsole") + ChatColor.GRAY + ".\n Powod: " + reason));
        ConnectAPI.broadcastMessage(plugin.color("&c[BAN] " + ban.getNick() + " zostal zbanowany do " + timeShow + " przez " + (ban.getAdmin() != null ? ban.getAdmin().getNick() : "konsole") + ChatColor.GRAY + ".\n Powod: " + reason), "tools.ban.notify");
    }

}
