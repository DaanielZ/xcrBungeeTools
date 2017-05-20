package pl.xcrafters.xcrbungeetools.listeners;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import pl.xcrafters.xcrbungeetools.data.DataUser;
import pl.xcrafters.xcrbungeetools.ToolsPlugin;

public class PlayerDisconnectListener implements Listener{

    ToolsPlugin plugin;
    
    public PlayerDisconnectListener(ToolsPlugin plugin){
        this.plugin = plugin;
        ProxyServer.getInstance().getPluginManager().registerListener(plugin, this);
    }
    
    @EventHandler
    public void onPlayerDisconnect(PlayerDisconnectEvent event){
        DataUser user = plugin.dataManager.getUserByPlayer(event.getPlayer());

        if(user == null) {
            return;
        }

        user.setTimePlayed(user.getTimePlayed() + (System.currentTimeMillis() - user.getLastJoin()));
        user.update();
    }
    
}
