package pl.xcrafters.xcrbungeetools.listeners;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.TabCompleteEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import pl.xcrafters.xcrbungeeconnect.ConnectAPI;
import pl.xcrafters.xcrbungeetools.ToolsPlugin;
import pl.xcrafters.xcrbungeetools.data.DataUser;

public class TabCompleteListener implements Listener {

    ToolsPlugin plugin;

    public TabCompleteListener(ToolsPlugin plugin){
        this.plugin = plugin;
        ProxyServer.getInstance().getPluginManager().registerListener(plugin, this);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onTabComplete(TabCompleteEvent event){
        if (!event.getSuggestions().isEmpty()) {
            return;
        }
        String[] args = event.getCursor().split(" ");

        ProxiedPlayer player = (ProxiedPlayer)event.getSender();

        final String checked = (args.length > 0 ? args[args.length - 1] : event.getCursor()).toLowerCase();
        for (String nick : ConnectAPI.getNicks()) {
            DataUser user = plugin.dataManager.getUserByNick(nick);

            if (user != null && (!user.vanished || player.hasPermission("tools.vanish.see")) && user.getNick().toLowerCase().startsWith(checked)) {
                event.getSuggestions().add(user.getNick());
            }
        }
    }

}
