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

public class GamemodeCommand extends Command implements TabExecutor{

    ToolsPlugin plugin;

    public GamemodeCommand(ToolsPlugin plugin) {
        super("gamemode", "tools.gamemode", "gmode", "gm");
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
        DataUser user = null;
        ServerInfo info = null;
        if (args.length == 0 || (args.length == 1 && !(sender instanceof ProxiedPlayer))) {
            sender.sendMessage(plugin.color("&9Poprawne uzycie: &b/gamemode <gamemode> <nick>"));
            return;
        }
        if (args.length == 1) {
            if (sender.equals(ProxyServer.getInstance().getConsole())) {
                sender.sendMessage(plugin.color("&9Poprawne uzycie: &b/gamemode <gamemode> <nick>"));
                return;
            }
            user = plugin.dataManager.getUserByPlayer(((ProxiedPlayer) sender));
            sender.sendMessage(plugin.color("&9Twoj tryb gamemode zostal zmieniony na tryb &b" + plugin.modes.get(Integer.parseInt(args[0])) + "&9."));
        } else if (args.length >= 2) {
            if (!sender.hasPermission("tools.gamemode.others")) {
                sender.sendMessage(plugin.color("&cNie masz uprawnien do zmiany gamemode dla innych graczy!"));
                return;
            }
            user = plugin.dataManager.getUserByNick(args[1]);
            if (user == null) {
                sender.sendMessage(plugin.color("&cNie znaleziono gracza o nicku &6" + args[1] + " &cw bazie danych!"));
                return;
            }
            ConnectAPI.sendMessage(user.getUUID(), plugin.color("&9Twoj tryb gamemode zostal zmieniony na tryb &b" + plugin.modes.get(Integer.parseInt(args[0])) + "&9."));
            sender.sendMessage(plugin.color("&9Tryb gamemode gracza &b" + user.getNick() + " &9zostal zmieniony na tryb &b" + plugin.modes.get(Integer.parseInt(args[0])) + "&9."));
        }
        info = ConnectAPI.getServer(user.getUUID());
        if (info != null) {
            plugin.setGamemode(user.getNick(), info, Integer.parseInt(args[0]));
        }
//        SektoryAPI.setGamemode(user.getUUID(), Integer.parseInt(args[0])); //TODO
        user.setGamemode(Integer.parseInt(args[0]));
        user.update();
    }

}
