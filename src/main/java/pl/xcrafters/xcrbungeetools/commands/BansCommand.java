package pl.xcrafters.xcrbungeetools.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import pl.xcrafters.xcrbungeetools.data.DataBan;
import pl.xcrafters.xcrbungeetools.ToolsPlugin;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class BansCommand extends Command implements TabExecutor {

    ToolsPlugin plugin;

    public BansCommand(ToolsPlugin plugin){
        super("bans", "tools.bans");
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
            sender.sendMessage(plugin.color("&9Poprawne uzycie: &b/bans <nick>"));
            return;
        }
        String nick = args[0];
        List<DataBan> bans = plugin.dataManager.getBansByNick(nick);
        if(bans.size() == 0){
            sender.sendMessage(plugin.color("&9Nie znaleziono banow dla gracza &b" + nick + "&9."));
            return;
        }
        sender.sendMessage(plugin.color("&9Bany dla gracza &b" + nick + "&9:"));
        sender.sendMessage(plugin.color("&7(powod - administrator - aktywny - utworzono - odbanowujacy - [czas]"));
        SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for(int i=0; i<bans.size(); i++){
            DataBan ban = bans.get(i);
            String utworzono = dt.format(ban.getCreated());
            String time = dt.format(ban.getTime());
            sender.sendMessage(plugin.color("&8" + (i + 1) + ". &9" + ban.getReason() + " &7 - &9" + (ban.getAdmin() == null ? "konsola" : ban.getAdmin().getNick()) + " &7 - &9" + (ban.getActive() ? "&aaktywny" : "&cnieaktywny") + " &7 - &9" + utworzono + " &7 - &9" + (!ban.getActive() ? "-" : (ban.getUnbanAdmin() == null ? "konsola" : ban.getUnbanAdmin().getNick())) + (ban.getTime() > 0L ? "&7 - &9" + time : "")));
        }
    }

}
