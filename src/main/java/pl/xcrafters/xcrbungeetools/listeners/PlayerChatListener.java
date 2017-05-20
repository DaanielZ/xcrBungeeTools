package pl.xcrafters.xcrbungeetools.listeners;

import java.util.concurrent.TimeUnit;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import pl.xcrafters.xcrbungeeperms.PermissionAPI;
import pl.xcrafters.xcrbungeetools.ToolsPlugin;
import pl.xcrafters.xcrbungeetools.events.PlayerChatEvent;

public class PlayerChatListener implements Listener {

    ToolsPlugin plugin;
    
    int messages = 0;
    
    public String bannedPattern;
    String dotted = "(.*[\\.,']{2,})|([\\.,']{2,}.*)";
    String url = ".*(http://)*(www)*(\\.|,)*[\\w]+(\\.|,)+[a-z-0-9]+.*";

    public PlayerChatListener(ToolsPlugin plugin) {
        this.plugin = plugin;
        ProxyServer.getInstance().getPluginManager().registerListener(plugin, this);

        if(plugin.configManager.disallowedWords.size() > 0) {
            bannedPattern = ".*(";
            for (String word : plugin.configManager.disallowedWords) {
                String comma = "|";
                if (plugin.configManager.disallowedWords.get(0).equals(word)) {
                    comma = "";
                }
                bannedPattern = bannedPattern + comma + word;
            }
            bannedPattern = bannedPattern + "+).*";
        }

        ProxyServer.getInstance().getScheduler().schedule(plugin, new Runnable() {
            public void run() {
                messages = 0;
            }
        }, 1, 1, TimeUnit.SECONDS);
    }

    @EventHandler
    public void onPlayerChat(PlayerChatEvent event) {
        ProxiedPlayer player = event.getSender();
        if (event.getMessage().startsWith("@") && player.hasPermission("tools.helpop")) {
            event.setCancelled(true);
            if (plugin.lastHelpop.get(player.getName()) != null && (System.currentTimeMillis() - plugin.lastHelpop.get(player.getName())) <= plugin.configManager.helpOpDelay * 1000 && !player.hasPermission("tools.helpop.bypass")) {
                player.sendMessage(plugin.color("&cNie spamuj na HelpOp"));
                return;
            }
            plugin.lastHelpop.put(player.getName(), System.currentTimeMillis());
            String msg = event.getMessage().substring(1).replaceAll("§", "&");
            String server = "[" + player.getServer().getInfo().getName().toUpperCase() + "]";
            player.sendMessage(plugin.color("&4[HelpOp] &7" + server + " &f" + getGroupColor(player) + player.getName() + "&8: &f") + msg);
            for (ProxiedPlayer online : ProxyServer.getInstance().getPlayers()) {
                if (online.hasPermission("tools.helpop.receive") && !online.getName().equals(player.getName())) {
                    online.sendMessage(plugin.color("&4[HelpOp] &7" + server + " &f" + getGroupColor(player) + player.getName() + "&8: &f") + msg);
                }
            }
            ProxyServer.getInstance().getConsole().sendMessage(plugin.color("&4[HelpOp] &7" + server + " &f" + getGroupColor(player) + player.getName() + "&8: &f") + msg);
            event.setCancelled(true);
            return;
        }
        String message = event.getMessage();
        if(plugin.removeSpecials(message) && !player.hasPermission("tools.chat.bypass")){
            message = message.replaceAll("[^(\\w\\s\\_)]", "");
            if (message.replaceAll(" ", "").length() == 0) {
                event.setCancelled(true);
                return;
            }
        }
        if (!player.hasPermission("tools.chat.bypass")) {
            String tempMessage = message.toLowerCase();
            if (bannedPattern != null && tempMessage.replaceAll("(\\s|_|\\W)+", "").matches(bannedPattern)) {
                message = "***";
            }
            if (!tempMessage.replaceAll(",", "").matches(dotted) && tempMessage.matches(url)) {
                message = "***";
            }
        }
        if (this.plugin.toLowerCase(event.getMessage()) && !player.hasPermission("tools.chat.bypass")) {
            message = message.toLowerCase();
        }
        if (player.hasPermission("tools.chat.color")) {
            message = plugin.color(message);
        } else {
            message = message.replaceAll("§", "&");
        }
        if (!plugin.chatLocked || player.hasPermission("tools.chat.bypass")) {
            messages += 1;
            if(messages > 5 && !player.hasPermission("tools.chat.bypass")){
                player.sendMessage(plugin.color("&cWykryto spam na chacie! Odczekaj chwile i sprobuj ponownie."));
                event.setCancelled(true);
                return;
            }
            if (!plugin.canSendMessage(player)) {
                player.sendMessage(plugin.color("&cWiadomosc na chacie mozesz wysylac za &6" + ((int)(plugin.lastMessageTime.get(player) - System.currentTimeMillis()) / 1000 + (ProxyServer.getInstance().getPlayers().size() > 50 ? ProxyServer.getInstance().getPlayers().size() / 10 : 5)) + " &csekund/y!"));
                event.setCancelled(true);
                return;
            }
            plugin.lastMessageTime.put(player, System.currentTimeMillis());
            event.setMessage(message);
            event.setDisplayName(getGroupColor(player) + player.getName());
            event.setPrefix(event.getPrefix() + getGroupPrefix(player));
            event.setSuffix(getWriteColor(player) + "");
            return;
        }
        player.sendMessage(plugin.color("&cChat jest obecnie zablokowany!"));
        event.setCancelled(true);
    }

    public String getGroupColor(ProxiedPlayer player) {
        String group = PermissionAPI.getGroup(player.getUniqueId());
        if(group == null) {
            return ChatColor.GRAY + "";
        }
        String color = plugin.configManager.groupColors.get(group.toLowerCase());
        if (color == null) {
            return ChatColor.GRAY + "";
        }
        return ChatColor.translateAlternateColorCodes('&', color);
    }
    
    public ChatColor getWriteColor(ProxiedPlayer player){
        if (player.hasPermission("tools.chat.color.admin") || player.hasPermission("tools.chat.color.mod") || player.hasPermission("tools.chat.color.helper")) {
            return ChatColor.GOLD;
        }
        return ChatColor.WHITE;
    }

    public String getGroupPrefix(ProxiedPlayer player) {
        String group = PermissionAPI.getGroup(player.getUniqueId());
        if(group != null && plugin.configManager.groupPrefixes.get(group.toLowerCase()) != null){
            return plugin.color(plugin.configManager.groupPrefixes.get(group.toLowerCase())) + " ";
        }
        return "";
    }

}
