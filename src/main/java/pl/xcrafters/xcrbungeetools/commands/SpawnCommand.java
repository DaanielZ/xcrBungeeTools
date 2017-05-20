package pl.xcrafters.xcrbungeetools.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import pl.xcrafters.xcrbungeeconnect.ConnectAPI;
import pl.xcrafters.xcrbungeetools.ToolsPlugin;

import java.util.ArrayList;
import java.util.List;

public class
SpawnCommand extends Command implements TabExecutor {

    ToolsPlugin plugin;
    
    public SpawnCommand(ToolsPlugin plugin){
        super("spawn", "tools.spawn");
        this.plugin = plugin;
        ProxyServer.getInstance().getPluginManager().registerCommand(plugin, this);
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        List<String> players = new ArrayList();

        if(sender.hasPermission("tools.spawn.others")) {
            for(String nick : ConnectAPI.getNicks()) {
                if (args.length == 0 || nick.toLowerCase().startsWith(args[(args.length - 1 >= 0 ? args.length - 1 : 0)].toLowerCase())) {
                    players.add(nick);
                }
            }
        }

        return players;
    }
    
    @Override
    public void execute(CommandSender sender, String[] args){
        if(sender.equals(ProxyServer.getInstance().getConsole()) && args.length == 0){
            sender.sendMessage(plugin.color("&cTa komenda nie moze zostac wywolana z konsoli!"));
            return;
        }
        String nick;
        if(args.length == 0){
            nick = ((ProxiedPlayer) sender).getName();
        } else {
            if(!sender.hasPermission("tools.spawn.others")){
                sender.sendMessage(plugin.color("&cNie masz uprawnien do teleportacji innych graczy na spawn!"));
                return;
            }
            nick = ConnectAPI.getExactNick(args[0]);
            if(nick == null){
                sender.sendMessage(plugin.color("&cGracz o podanym nicku nie jest online!"));
                return;
            }
        }
        if(args.length == 0 && !sender.hasPermission("tools.spawn.teleport")){
            sender.sendMessage(plugin.color("&9Koordynaty spawnu: &bx:" + plugin.configManager.spawnX + " y:" + plugin.configManager.spawnY + " z:" + plugin.configManager.spawnZ));
            return;
        }
        String message = plugin.color("&9Przeteleportowano na spawn!");
        if(args.length > 0){
            message = plugin.color("&9Zostales przeteleportowany na spawn przez ");
            if(sender instanceof ProxiedPlayer){
                message += "gracza &b" + sender.getName() + "&9.";
            } else {
                message += "&bkonsole&9.";
            }
            sender.sendMessage(plugin.color("&9Przeteleportowano na spawn gracza &b" + nick + "&9."));
            plugin.handleTeleportation(nick, plugin.configManager.spawnWorld, plugin.configManager.spawnX, plugin.configManager.spawnY, plugin.configManager.spawnZ, 0, message);
            return;
        }
        if(!sender.hasPermission("tools.spawn.no-delay")){
            sender.sendMessage(plugin.color("&9Teleport rozgrzewa sie..."));
            plugin.handleTeleportation(nick, plugin.configManager.spawnWorld, plugin.configManager.spawnX, plugin.configManager.spawnY, plugin.configManager.spawnZ, plugin.configManager.teleportDelay, message);
            return;
        }
        plugin.handleTeleportation(nick, plugin.configManager.spawnWorld, plugin.configManager.spawnX, plugin.configManager.spawnY, plugin.configManager.spawnZ, 0, message);
    }
    
}
