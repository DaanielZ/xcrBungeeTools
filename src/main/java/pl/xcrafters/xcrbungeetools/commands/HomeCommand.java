package pl.xcrafters.xcrbungeetools.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import pl.xcrafters.xcrbungeetools.data.DataUser;
import pl.xcrafters.xcrbungeetools.ToolsPlugin;

public class HomeCommand extends Command {

    ToolsPlugin plugin;

    public HomeCommand(ToolsPlugin plugin) {
        super("home", "tools.home");
        this.plugin = plugin;
        ProxyServer.getInstance().getPluginManager().registerCommand(plugin, this);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender.equals(ProxyServer.getInstance().getConsole())) {
            sender.sendMessage(plugin.color("&cTa komenda nie moze zostac wywolana z konsoli!"));
            return;
        }
        ProxiedPlayer player = (ProxiedPlayer) sender;
        DataUser user = plugin.dataManager.getUserByPlayer(player);
        if (user == null) {

            return;
        }
        if (args.length == 1 && player.hasPermission("tools.home.others")) {
            user = plugin.dataManager.getUserByNick(args[0]);
            if (!player.hasPermission("tools.home.no-delay")) {
                if (user.getHomeWorld() != null) {
                    player.sendMessage(plugin.color("&9Teleportowanie do domu..."));
                    plugin.handleTeleportation(player, user.getHomeWorld(), user.getHomeX(), user.getHomeY(), user.getHomeZ(), plugin.configManager.teleportDelay, plugin.color("&9Przeteleportowano do domu!"));
                } else {
                    player.sendMessage(plugin.color("&cGracz &6" + user.getNick() + " &cnie ustawil domu!"));
                }
                return;
            }
            plugin.handleTeleportation(player, user.getHomeWorld(), user.getHomeX(), user.getHomeY(), user.getHomeZ(), 0, plugin.color("&9Przeteleportowano do domu!"));
            return;
        }
        if (user.getHomeWorld() == null) {
            player.sendMessage(plugin.color("&cNie ustawiles domu!"));
            return;
        }
        if (!player.hasPermission("tools.home.teleport")) {
            player.sendMessage(plugin.color("&9Koordynaty twojego domu to: x:&7" + user.getHomeX() + " y:" + user.getHomeY() + " z:" + user.getHomeZ()));
            return;
        }
        if (!player.hasPermission("tools.home.no-delay")) {
            player.sendMessage(plugin.color("&9Teleportowanie do domu..."));
            plugin.handleTeleportation(player, user.getHomeWorld(), user.getHomeX(), user.getHomeY(), user.getHomeZ(), plugin.configManager.teleportDelay, plugin.color("&9Przeteleportowano do domu!"));
            return;
        }
        plugin.handleTeleportation(player, user.getHomeWorld(), user.getHomeX(), user.getHomeY(), user.getHomeZ(), 0, plugin.color("&9Przeteleportowano do domu!"));
    }

}
