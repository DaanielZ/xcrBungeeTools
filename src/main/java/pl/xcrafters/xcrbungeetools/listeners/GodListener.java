package pl.xcrafters.xcrbungeetools.listeners;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import pl.xcrafters.xcrbungeetools.data.DataUser;
import pl.xcrafters.xcrbungeetools.ToolsPlugin;

public class GodListener implements Listener{
    
    ToolsPlugin plugin;
    
    public GodListener(ToolsPlugin plugin){
        this.plugin = plugin;
        ProxyServer.getInstance().getPluginManager().registerListener(plugin, this);
    }
    
    @EventHandler
    public void onServerConnected(ServerConnectedEvent event){
        ProxiedPlayer player = event.getPlayer();
        DataUser user = plugin.dataManager.getUserByPlayer(player);

        if(user == null) {
            return;
        }

        plugin.setGod(player.getName(), event.getServer().getInfo(), user.getGod());
    }
    
}
