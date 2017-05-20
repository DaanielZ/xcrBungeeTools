package pl.xcrafters.xcrbungeetools.listeners;

import java.util.logging.Level;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import pl.xcrafters.xcrbungeeconnect.ConnectAPI;
import pl.xcrafters.xcrbungeetools.ToolsPlugin;
import pl.xcrafters.xcrbungeetools.events.PlayerChatEvent;

public class ChatListener implements Listener{

    ToolsPlugin plugin;
    
    public ChatListener(ToolsPlugin plugin){
        this.plugin = plugin;
        ProxyServer.getInstance().getPluginManager().registerListener(plugin, this);
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(ChatEvent event){
        if(event.getMessage().startsWith("/;")) {
            event.setCancelled(true);
            return;
        }

        if(event.isCommand() && event.getSender() instanceof ProxiedPlayer){
            ProxiedPlayer player = (ProxiedPlayer) event.getSender();
            plugin.getLogger().log(Level.INFO, "[" + player.getServer().getInfo().getName().toUpperCase() + "] [CMD] " + player.getName() + ": " + event.getMessage());
        }
        if(!event.isCommand() && !event.isCancelled()){
            String message = event.getMessage();
            if(event.getSender() instanceof ProxiedPlayer){
                ProxiedPlayer player = (ProxiedPlayer) event.getSender();
                event.setCancelled(true);
                ProxyServer.getInstance().getConsole().sendMessage(player.getName() + ": " + message);
                PlayerChatEvent chatEvent = new PlayerChatEvent(player, message);
                ProxyServer.getInstance().getPluginManager().callEvent(chatEvent);
                if(!chatEvent.isCancelled()){
                    message = ChatColor.DARK_GRAY + ": " + ChatColor.GRAY + chatEvent.getSuffix() + chatEvent.getMessage();
                    TextComponent playerName = new TextComponent(chatEvent.getDisplayName());
                    playerName.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Kliknij, aby wyslac wiadomosc...").color(ChatColor.YELLOW).italic(true).create()));
                    playerName.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/msg " + chatEvent.getSender().getName() + " "));
                    TextComponent msg = new TextComponent(chatEvent.getPrefix());
                    msg.addExtra(playerName);
                    msg.addExtra(new TextComponent(TextComponent.fromLegacyText(message)));
                    ConnectAPI.broadcastMessage(msg);
                }
            }
        }
    }
    
}
