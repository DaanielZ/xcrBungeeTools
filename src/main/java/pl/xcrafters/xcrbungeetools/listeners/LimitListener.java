package pl.xcrafters.xcrbungeetools.listeners;

import java.util.concurrent.TimeUnit;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import pl.xcrafters.xcrbungeeperms.PermissionAPI;
import pl.xcrafters.xcrbungeetools.ToolsPlugin;

public class LimitListener implements Listener{

    ToolsPlugin plugin;
    
    public LimitListener(ToolsPlugin plugin){
        this.plugin = plugin;
        ProxyServer.getInstance().getPluginManager().registerListener(plugin, this);
        ProxyServer.getInstance().getScheduler().schedule(plugin, new Runnable(){
            public void run(){
                joins = 0;
            }
        }, plugin.configManager.limitSeconds, plugin.configManager.limitSeconds, TimeUnit.SECONDS);
    }
    
    int joins = 0;
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPreLogin(PreLoginEvent event){
        if(event.isCancelled()){
            return;
        }
        String nick = event.getConnection().getName();
        joins++;
        if(joins > plugin.configManager.limitPlayers && !isAllowed(nick)){
            event.setCancelled(true);
            event.setCancelReason(plugin.color("&cZbyt duzo osob loguje sie w tej chwili. Sprobuj pozniej!"));
        }
    }

    boolean isAllowed(String nick) {
        String group = PermissionAPI.getGroup(nick);

        return group.equals("slot") || group.equals("helper") || group.equals("mod") || group.equals("admin") || group.equals("headadmin");
    }
    
}
