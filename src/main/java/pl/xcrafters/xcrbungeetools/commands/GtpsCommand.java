package pl.xcrafters.xcrbungeetools.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.plugin.Command;
import pl.xcrafters.xcrbungeetools.ToolsPlugin;

public class GtpsCommand extends Command{

    ToolsPlugin plugin;
    
    public GtpsCommand(ToolsPlugin plugin){
        super("gtps", "tools.gtps");
        this.plugin = plugin;
        ProxyServer.getInstance().getPluginManager().registerCommand(plugin, this);
    }
    
    @Override
    public void execute(CommandSender sender, String[] args){
        String message = "";
        for(ServerInfo server : ProxyServer.getInstance().getServers().values()){
            double tps = plugin.lastTps.get(server.getName()) != null ? plugin.lastTps.get(server.getName()) : 0.0;
            message += ChatColor.GOLD + server.getName().toUpperCase() + ": " + format(tps) + " ";
        }
        sender.sendMessage(message);
    }
    
    private String format(double org) {
        double tps = Math.min(20,  Math.round(org * 10) / 10.0);
        ChatColor color;
        if (tps > 19.2D) {
            color = ChatColor.GREEN;
        } else if (tps > 17.4D) {
            color = ChatColor.YELLOW;
        } else {
            color = ChatColor.RED;
        }
        String format = color + (org >= 20 ? "*" : "") + String.valueOf(tps);
        return format;
    }

}
