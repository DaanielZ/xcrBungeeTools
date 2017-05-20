package pl.xcrafters.xcrbungeetools.listeners;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import pl.xcrafters.xcrbungeeconnect.ConnectAPI;
import pl.xcrafters.xcrbungeetools.data.DataUser;
import pl.xcrafters.xcrbungeetools.ToolsPlugin;

public class VanishListener implements Listener {

    ToolsPlugin plugin;

    public VanishListener(ToolsPlugin plugin) {
        this.plugin = plugin;
        ProxyServer.getInstance().getPluginManager().registerListener(plugin, this);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPostLogin(PostLoginEvent event) {
        ProxiedPlayer player = event.getPlayer();
        if (player.hasPermission("tools.vanish.on-join")) {
            DataUser user = plugin.dataManager.getUserByPlayer(player);
            if(user == null){
                return;
            }
            user.vanished = true;
            player.sendMessage(plugin.color("&8\u00BB &3Dolaczyles/as jako niewidoczny/a."));
            for (ProxiedPlayer online : ProxyServer.getInstance().getPlayers()) {
                if (online.hasPermission("tools.vanish.see") && !online.equals(player)) {
                    online.sendMessage(plugin.color("&3" + player.getName() + " dolaczyl/a jako niewidoczny/a."));
                }
            }

            ConnectAPI.broadcastMessage(plugin.color("&3" + player.getName() + " dolaczyl/a jako niewidoczny/a."), "tools.vanish.see", false);
        }
    }

    @EventHandler
    public void onServerConnected(ServerConnectedEvent event) {
        ProxiedPlayer player = event.getPlayer();
        DataUser user = plugin.dataManager.getUserByPlayer(player);
        if (user != null && !user.vanished) {
            plugin.vanishPlayer(player.getName(), user.vanished, false);
        }
    }

    @EventHandler
    public void onPlayerDisconnect(PlayerDisconnectEvent event) {
        ProxiedPlayer player = event.getPlayer();
        DataUser user = plugin.dataManager.getUserByPlayer(player);
        if (user != null && user.vanished) {
            user.vanished = false;
            plugin.vanishPlayer(user.getNick(), false, false);
        }
    }

}
