package pl.xcrafters.xcrbungeetools.listeners;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import pl.xcrafters.xcrbungeeconnect.events.ConnectPlayerConnectServerEvent;
import pl.xcrafters.xcrbungeetools.ToolsPlugin;

import java.util.ArrayList;
import java.util.UUID;

public class ConnectPlayerConnectServerListener implements Listener {

    ToolsPlugin plugin;

    public ConnectPlayerConnectServerListener(ToolsPlugin plugin) {
        this.plugin = plugin;
        ProxyServer.getInstance().getPluginManager().registerListener(plugin, this);
    }

    @EventHandler
    public void onConnectPlayerSwitch(ConnectPlayerConnectServerEvent event) {
        UUID uuid = event.getUUID();
        ServerInfo info = event.getServerInfo();

        if(plugin.dataManager.onlinePlayers.get(info.getName()) == null) {
            plugin.dataManager.onlinePlayers.put(info.getName(), new ArrayList());
        }

        plugin.dataManager.onlinePlayers.get(info.getName()).add(uuid);
    }

}
