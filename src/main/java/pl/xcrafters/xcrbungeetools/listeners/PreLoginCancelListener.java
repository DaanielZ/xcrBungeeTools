package pl.xcrafters.xcrbungeetools.listeners;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import pl.xcrafters.xcrbungeetools.ToolsPlugin;

public class PreLoginCancelListener implements Listener {

    ToolsPlugin plugin;

    public PreLoginCancelListener(ToolsPlugin plugin){
        this.plugin = plugin;
        ProxyServer.getInstance().getPluginManager().registerListener(plugin, this);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPreLogin(PreLoginEvent event){
        if(event.isCancelled()){
            event.getConnection().disconnect(event.getCancelReason());
        }
    }

}
