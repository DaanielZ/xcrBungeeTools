package pl.xcrafters.xcrbungeetools.listeners;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import pl.xcrafters.xcrbungeeconnect.events.ConnectPlayerDisconnectEvent;
import pl.xcrafters.xcrbungeetools.ToolsPlugin;
import pl.xcrafters.xcrbungeetools.data.DataUser;

import java.util.List;
import java.util.UUID;

public class ConnectPlayerDisconnectListener implements Listener {

    ToolsPlugin plugin;

    public ConnectPlayerDisconnectListener(ToolsPlugin plugin) {
        this.plugin = plugin;
        ProxyServer.getInstance().getPluginManager().registerListener(plugin, this);
    }

    @EventHandler
    public void onConnectPlayerDisconnect(ConnectPlayerDisconnectEvent event) {
        UUID uuid = event.getUUID();

        DataUser user = plugin.dataManager.getUserByUUID(uuid);

        for(List<UUID> players : plugin.dataManager.onlinePlayers.values()) {
            players.remove(uuid);
        }

        if(user == null) {
            return;
        }

        user.unloadIgnoredPlayers();
    }

}
