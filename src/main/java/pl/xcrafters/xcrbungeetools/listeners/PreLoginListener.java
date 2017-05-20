package pl.xcrafters.xcrbungeetools.listeners;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import pl.xcrafters.xcrbungeeperms.PermissionAPI;
import pl.xcrafters.xcrbungeetools.ToolsPlugin;

public class PreLoginListener implements Listener {

    ToolsPlugin plugin;

    public PreLoginListener(ToolsPlugin plugin) {
        this.plugin = plugin;
        ProxyServer.getInstance().getPluginManager().registerListener(plugin, this);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPreLogin(PreLoginEvent event) {
        String nick = event.getConnection().getName();
        if (ProxyServer.getInstance().getPlayers().size() > plugin.configManager.maxSlots && !isAllowed(nick)) {
            event.setCancelled(true);
            event.setCancelReason(plugin.color("&cOsiagnieto maksymalna ilosc ucieszonych graczy."));
        }
    }

    boolean isAllowed(String nick) {
        String group = PermissionAPI.getGroup(nick);

        return group.equals("slot") || group.equals("helper") || group.equals("mod") || group.equals("admin") || group.equals("headadmin");
    }

}
