package pl.xcrafters.xcrbungeetools;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;

public class ToolsAPI {

    public static int getMaxPlayers() {
        return ToolsPlugin.getInstance().configManager.maxSlots;
    }

    public static int getPlayers(){
        int players = 0;

        for (ServerInfo srv : ProxyServer.getInstance().getServers().values()) {
            players += ToolsPlugin.getInstance().dataManager.onlinePlayers.get(srv.getName()) == null ? 0 : ToolsPlugin.getInstance().dataManager.onlinePlayers.get(srv.getName()).size();
        }

        return players;
    }

}
