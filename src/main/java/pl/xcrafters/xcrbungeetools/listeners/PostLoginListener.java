package pl.xcrafters.xcrbungeetools.listeners;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import pl.xcrafters.xcrbungeeauth.AuthAPI;
import pl.xcrafters.xcrbungeetools.data.DataUser;
import pl.xcrafters.xcrbungeetools.ToolsPlugin;

import java.util.UUID;

public class PostLoginListener implements Listener {

    ToolsPlugin plugin;

    public PostLoginListener(ToolsPlugin plugin) {
        this.plugin = plugin;
        ProxyServer.getInstance().getPluginManager().registerListener(plugin, this);
    }

    @EventHandler
    public void onPostLogin(PostLoginEvent event) {
        ProxiedPlayer player = event.getPlayer();
        String nick = event.getPlayer().getName();
        UUID uuid = player.getUniqueId();
        DataUser user = plugin.dataManager.getUserByNick(nick);;
        if (user == null) {
            user = plugin.dataManager.getUserByUUID(uuid);
            if(user == null) {
                user = plugin.dataManager.createUser();
                user.setNick(nick);
                user.setUUID(uuid);
                user.setFly(false);
                user.setGamemode(0);
                user.setGod(false);
                user.setOp(false);
                user.setTimePlayed(0L);
                user.insert();
            } else if(!user.getNick().equals(nick)) {
                user.setNick(player.getName());
                user.update();
            }
        }
        user.setLastJoin(System.currentTimeMillis());
        for (String message : plugin.configManager.welcomeMessage) {
            player.sendMessage(plugin.color(message.replaceAll("\\{playername\\}", player.getName()).replaceAll("\\{odwiedzilo\\}", String.valueOf(AuthAPI.getTotalPlayers()))));
        }
    }

}
