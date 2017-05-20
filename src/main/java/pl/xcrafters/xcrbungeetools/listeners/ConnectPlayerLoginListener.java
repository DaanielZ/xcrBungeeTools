package pl.xcrafters.xcrbungeetools.listeners;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import pl.xcrafters.xcrbungeeconnect.events.ConnectPlayerLoginEvent;
import pl.xcrafters.xcrbungeetools.ToolsPlugin;
import pl.xcrafters.xcrbungeetools.data.DataUser;

import java.util.UUID;

public class ConnectPlayerLoginListener implements Listener {

    ToolsPlugin plugin;

    public ConnectPlayerLoginListener(ToolsPlugin plugin) {
        this.plugin = plugin;
        ProxyServer.getInstance().getPluginManager().registerListener(plugin, this);
    }

    @EventHandler
    public void onConnectPlayerLogin(ConnectPlayerLoginEvent event) {
        UUID uuid = event.getUUID();

        DataUser user = plugin.dataManager.getUserByUUID(uuid);

        if(user == null) {
            return;
        }

        user.loadIgnoredPlayers();
    }

}
