package pl.xcrafters.xcrbungeetools.listeners;

import java.text.SimpleDateFormat;
import java.util.Date;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import pl.xcrafters.xcrbungeetools.data.DataBan;
import pl.xcrafters.xcrbungeetools.ToolsPlugin;
import pl.xcrafters.xcrbungeetools.data.DataIPBan;

public class BanListener implements Listener {
    
    ToolsPlugin plugin;
    
    public BanListener(ToolsPlugin plugin) {
        this.plugin = plugin;
        ProxyServer.getInstance().getPluginManager().registerListener(plugin, this);
    }
    
    @EventHandler(priority = EventPriority.LOW)
    public void onPreLogin(LoginEvent event) {
        String nick = event.getConnection().getName();
        DataBan ban = plugin.dataManager.getActiveBanByNick(nick);
        if(ban == null) {
            ban = plugin.dataManager.getActiveBanByUUID(event.getConnection().getUniqueId());
        }
        if (ban != null) {
            if (ban.getTime() > 0L) {
                if (ban.getTime() <= System.currentTimeMillis()) {
                    ban.setActive(false);
                    ban.update();
                } else {
                    Date date = new Date(ban.getTime());
                    SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");                    
                    String timeShow = dt.format(date);
                    event.setCancelled(true);
                    event.setCancelReason(plugin.color("&cZostales zbanowany do &6" + timeShow + " &cprzez &6" + (ban.getAdmin() != null ? ban.getAdmin().getNick() : "konsole") + "&c.\n Powod: &6" + ban.getReason()));
                }
            } else {
                event.setCancelled(true);
                event.setCancelReason(plugin.color("&cZostales permamentnie zbanowany przez &6" + (ban.getAdmin() != null ? ban.getAdmin().getNick() : "konsole") + "&c.\n Powod: &6" + ban.getReason()));
            }
        }
        DataIPBan ipban = plugin.dataManager.getIPBanByIp(event.getConnection().getAddress().getAddress().getHostAddress());
        if (ipban != null) {
            event.setCancelled(true);
            event.setCancelReason(plugin.color("&cTwoj adres IP zostal zbanowany przez &6" + (ipban.getAdmin() != null ? ipban.getAdmin().getNick() : "konsole") + "&c.\n Powod: &6" + ipban.getReason()));
        }
    }
    
}
