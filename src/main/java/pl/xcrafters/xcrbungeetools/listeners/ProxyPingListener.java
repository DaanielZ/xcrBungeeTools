package pl.xcrafters.xcrbungeetools.listeners;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.ServerPing.Players;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import pl.xcrafters.xcrbungeetools.ToolsAPI;
import pl.xcrafters.xcrbungeetools.ToolsPlugin;

public class ProxyPingListener implements Listener{

    ToolsPlugin plugin;
    
    public ProxyPingListener(ToolsPlugin plugin){
        this.plugin = plugin;
        ProxyServer.getInstance().getPluginManager().registerListener(plugin, this);
    }
    
    @EventHandler
    public void onProxyPing(ProxyPingEvent event){
        ServerPing ping = event.getResponse();
        if(plugin.configManager.motd != null){
            ping.setDescription(plugin.color(plugin.configManager.motd));
        }
        Players players = ping.getPlayers();
        if(plugin.configManager.maxSlots > 0){
            players.setMax(plugin.configManager.maxSlots);
            players.setOnline(ToolsAPI.getPlayers());
        }
        if(plugin.configManager.whitelistEnabled){
            players.setMax(0);
        }

        event.setResponse(ping);
    }
    
}
