package pl.xcrafters.xcrbungeetools.listeners;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import pl.xcrafters.xcrbungeeconnect.events.ConnectPlayerDisconnectServerEvent;
import pl.xcrafters.xcrbungeetools.ToolsPlugin;

import java.util.UUID;

public class ConnectPlayerDisconnectServerListener implements Listener {

    ToolsPlugin plugin;

    public ConnectPlayerDisconnectServerListener(ToolsPlugin plugin) {
        this.plugin = plugin;
        ProxyServer.getInstance().getPluginManager().registerListener(plugin, this);
    }

    @EventHandler
    public void onConnectPlayerDisconnectServer(ConnectPlayerDisconnectServerEvent event) {
        UUID uuid = event.getUUID();
        ServerInfo info = event.getServerInfo();

        if(plugin.dataManager.onlinePlayers.get(info.getName()) == null) {
            return;
        }

        plugin.dataManager.onlinePlayers.get(info.getName()).remove(uuid);
    }

}
