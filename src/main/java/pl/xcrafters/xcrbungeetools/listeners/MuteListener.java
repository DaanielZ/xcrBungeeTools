package pl.xcrafters.xcrbungeetools.listeners;

import java.text.SimpleDateFormat;
import java.util.Date;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import pl.xcrafters.xcrbungeetools.data.DataUser;
import pl.xcrafters.xcrbungeetools.ToolsPlugin;
import pl.xcrafters.xcrbungeetools.events.PlayerChatEvent;

public class MuteListener implements Listener{

    ToolsPlugin plugin;
    
    public MuteListener(ToolsPlugin plugin){
        this.plugin = plugin;
        ProxyServer.getInstance().getPluginManager().registerListener(plugin, this);
    }
    
    @EventHandler
    public void onPlayerChat(PlayerChatEvent event){
        ProxiedPlayer player = event.getSender();
        DataUser user = this.plugin.dataManager.getUserByPlayer(player);
        if(user != null && user.getMuteReason() != null){
            if(user.getMuteTime() != 0L){
                if(user.getMuteTime() <= System.currentTimeMillis()){
                    player.sendMessage(ChatColor.RED + "Od tej pory mozesz juz pisac. Uwazaj, aby znow nie zostac wyciszonym!");
                    user.setMuteReason(null);
                    user.setMuteTime(0L);
                }
                else
                {
                    event.setCancelled(true);
                    Date date = new Date(user.getMuteTime());
                    SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
                    String timeShow = dt.format(date);
                    player.sendMessage(ChatColor.RED + "Zostales wyciszony do " + ChatColor.GOLD + timeShow + ChatColor.RED + ". Powod: " + user.getMuteReason());    
                }
            }
            else
            {
                player.sendMessage(ChatColor.RED + "Zostales wyciszony. Powod: " + user.getMuteReason());
                event.setCancelled(true);
            }
        }
    }
    
}
