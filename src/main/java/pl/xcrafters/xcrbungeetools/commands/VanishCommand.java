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
import pl.xcrafters.xcrbungeetools.data.DataUser;
import pl.xcrafters.xcrbungeetools.ToolsPlugin;

public class VanishCommand extends Command implements TabExecutor {

    ToolsPlugin plugin;

    public VanishCommand(ToolsPlugin plugin) {
        super("vanish", "tools.vanish", "v");
        this.plugin = plugin;
        ProxyServer.getInstance().getPluginManager().registerCommand(plugin, this);
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        List<String> players = new ArrayList();
        if (sender.hasPermission("tools.vanish.others")) {
            for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
                if (args.length == 0 || (args.length > 0 && player.getName().toLowerCase().startsWith(args[(args.length - 1 >= 0 ? args.length - 1 : 0)].toLowerCase()))) {
                    players.add(player.getName());
                }
            }
        }
        return players;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        UUID uuid;
        if (sender.equals(ProxyServer.getInstance().getConsole())) {
            if (args.length == 0) {
                sender.sendMessage(plugin.color("&9Poprawne uzycie: &b/vanish <nick>"));
                return;
            }
            uuid = ConnectAPI.getUUID(args[0]);
        } else {
            if (args.length == 1) {
                if (!sender.hasPermission("tools.vanish.others")) {
                    sender.sendMessage(plugin.color("&cNie masz uprawnien do ukrywania innych graczy!"));
                    return;
                }
                uuid = ConnectAPI.getUUID(args[0]);
            } else {
                uuid = ((ProxiedPlayer) sender).getUniqueId();
            }
        }
        if (uuid == null) {
            sender.sendMessage(plugin.color("&cGracz o nicku &6" + args[0] + " &cnie jest online!"));
            return;
        }
        DataUser user = plugin.dataManager.getUserByUUID(uuid);
        user.vanished = !user.vanished;
        plugin.vanishPlayer(user.getNick(), user.vanished, true);
        ConnectAPI.sendMessage(user.getUUID(), plugin.color("&3Od teraz jestes " + (user.vanished ? "niewidoczny/a" : "widoczny/a") + "."));
        for (ProxiedPlayer online : ProxyServer.getInstance().getPlayers()) {
            if (online.hasPermission("tools.vanish.see") && !online.getUniqueId().equals(uuid)) {
                online.sendMessage(plugin.color("&3" + user.getNick() + " jest teraz " + (user.vanished ? "niewidoczny/a" : "widoczny/a") + "."));
            }
        }
    }

}
