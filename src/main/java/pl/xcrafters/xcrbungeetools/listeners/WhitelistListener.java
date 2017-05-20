package pl.xcrafters.xcrbungeetools.listeners;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import pl.xcrafters.xcrbungeetools.ToolsPlugin;

public class WhitelistListener implements Listener{

    ToolsPlugin plugin;
    
    public WhitelistListener(ToolsPlugin plugin){
        this.plugin = plugin;
        ProxyServer.getInstance().getPluginManager().registerListener(plugin, this);
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPreLogin(PreLoginEvent event){
        if(plugin.configManager.whitelistEnabled){
            String nick = event.getConnection().getName().toLowerCase();
            if(!plugin.configManager.whitelistedPlayers.contains(nick)){
                event.setCancelled(true);
                event.setCancelReason(plugin.color(plugin.configManager.whitelistMessage));
            }
        }
    }
    
}
