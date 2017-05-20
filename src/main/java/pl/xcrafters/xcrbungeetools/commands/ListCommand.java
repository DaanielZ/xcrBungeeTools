package pl.xcrafters.xcrbungeetools.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.plugin.Command;
import pl.xcrafters.xcrbungeetools.ToolsPlugin;

public class ListCommand extends Command{

    ToolsPlugin plugin;
    
    public ListCommand(ToolsPlugin plugin){
        super("list", "tools.list", "glist");
        this.plugin = plugin;
        ProxyServer.getInstance().getPluginManager().registerCommand(plugin, this);
    }
    
    @Override
    public void execute(CommandSender sender, String[] args){
        if(sender.hasPermission("tools.list.admin")) {
            String message = "";
            int total = 0;
            for (ServerInfo srv : ProxyServer.getInstance().getServers().values()) {
                int players = plugin.dataManager.onlinePlayers.get(srv.getName()) == null ? 0 : plugin.dataManager.onlinePlayers.get(srv.getName()).size();

                total += players;

                message += "&8" + srv.getName().toUpperCase() + ": " + (players == 0 ? "&7" : "&6") + players + " ";
            }
            sender.sendMessage(plugin.color("&8Lacznie graczy: &6" + total + "/" + plugin.configManager.maxSlots));
            sender.sendMessage(plugin.color(message));
        } else {
            int total = 0;
            for (ServerInfo srv : ProxyServer.getInstance().getServers().values()) {
                int players = plugin.dataManager.onlinePlayers.get(srv.getName()) == null ? 0 : plugin.dataManager.onlinePlayers.get(srv.getName()).size();
                total += players;
            }
            sender.sendMessage(plugin.color("&9Na serwerze jest obecnie &b" + total + " &9na &b" + plugin.configManager.maxSlots + " &9mozliwych."));
        }
    }
    
}
