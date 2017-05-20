package pl.xcrafters.xcrbungeetools.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import pl.xcrafters.xcrbungeeauth.AuthAPI;
import pl.xcrafters.xcrbungeeconnect.ConnectAPI;
import pl.xcrafters.xcrbungeeperms.PermissionAPI;
import pl.xcrafters.xcrbungeetools.ToolsPlugin;
import pl.xcrafters.xcrbungeetools.data.DataBan;
import pl.xcrafters.xcrbungeetools.data.DataUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class InfoCommand extends Command implements TabExecutor{

    ToolsPlugin plugin;

    public InfoCommand(ToolsPlugin plugin) {
        super("info", "tools.info");
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
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(plugin.color("&9Poprawne uzycie: &b/info <nick>"));
            return;
        }
        DataUser user = plugin.dataManager.getUserByNick(args[0]);
        if (user == null) {
            sender.sendMessage(plugin.color("&cNie znaleziono gracza &6" + args[0] + " &cw bazie danych!"));
            return;
        }
        ServerInfo info = ConnectAPI.getServer(user.getUUID());
        String online = info != null ? ChatColor.GREEN + "tak" : ChatColor.RED + "nie";
        String premium = AuthAPI.isPremium(user.getNick()) ? ChatColor.GREEN + "tak" : ChatColor.RED + "nie";
        String ip = AuthAPI.getIP(user.getNick());
        String group = PermissionAPI.getGroup(user.getNick());
        if(!sender.hasPermission("tools.info.ip") || ((group.equals("helper") || group.equals("mod") || group.equals("admin")) && !group.equals("admin") && sender instanceof ProxiedPlayer)){
            ip = "ukryte";
        }
        String god = user.getGod() ? ChatColor.GREEN + "wlaczony" : ChatColor.RED + "wylaczony";
        String gamemode = ChatColor.RED + plugin.modes.get(user.getGamemode());
        if (user.getGamemode() == 1) {
            gamemode = ChatColor.GREEN + plugin.modes.get(user.getGamemode());
        }
        String fly = user.getFly() ? ChatColor.GREEN + "wlaczony" : ChatColor.RED + "wylaczony";
        String isBanned = ChatColor.RED + "nie";
        DataBan ban = plugin.dataManager.getActiveBanByNick(user.getNick());
        if (ban != null) {
            isBanned = ChatColor.GREEN + ban.getReason();
            if (ban.getTime() != 0L) {
                Date date = new Date(ban.getTime());
                SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String timeShow = dt.format(date);
                isBanned += "(do " + timeShow + ") ";
            }
            String admin = ban.getAdmin() != null ? ban.getAdmin().getNick() : "konsole";
            isBanned += ChatColor.GOLD + " ~" + admin;
        }
        String isMuted = ChatColor.RED + "nie";
        if (user.getMuteReason()!= null) {
            isMuted = ChatColor.GREEN + user.getMuteReason();
            if (user.getMuteTime() != 0L) {
                Date date = new Date(user.getMuteTime());
                SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String timeShow = dt.format(date);
                isMuted += "(do " + timeShow + ")";
            }
        }
        sender.sendMessage(ChatColor.DARK_GRAY + "### " + ChatColor.GOLD + user.getNick() + ChatColor.DARK_GRAY + " ###");
        sender.sendMessage(ChatColor.GOLD + "Online: " + online);
        sender.sendMessage(ChatColor.GOLD + "IP: " + ChatColor.RED + ip);
        sender.sendMessage(ChatColor.GOLD + "Premium: " + premium);
        sender.sendMessage(ChatColor.GOLD + "Gamemode: " + gamemode);
        sender.sendMessage(ChatColor.GOLD + "Tryb latania: " + fly);
        sender.sendMessage(ChatColor.GOLD + "Tryb god'a: " + god);
        sender.sendMessage(ChatColor.GOLD + "Zbanowany: " + isBanned);
        sender.sendMessage(ChatColor.GOLD + "Wyciszony: " + isMuted);
        if(info != null){
            sender.sendMessage(ChatColor.GOLD + "Serwer: " + ChatColor.GRAY + info.getName());
//            sender.sendMessage(ChatColor.GOLD + "Ping: " + ChatColor.GRAY + player.getPing() + "ms");
        }
    }

}
