package pl.xcrafters.xcrbungeetools.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import pl.xcrafters.xcrbungeeconnect.ConnectAPI;
import pl.xcrafters.xcrbungeeperms.PermissionAPI;
import pl.xcrafters.xcrbungeetools.ToolsPlugin;

public class HelpOpCommand extends Command{

    ToolsPlugin plugin;
    
    public HelpOpCommand(ToolsPlugin plugin){
        super("helpop", "tools.helpop");
        this.plugin = plugin;
        ProxyServer.getInstance().getPluginManager().registerCommand(plugin, this);
    }
    
    @Override
    public void execute(CommandSender sender, String[] args){
        if(args.length == 0){
            sender.sendMessage(plugin.color("&9Poprawne uzycie: &b/helpop <wiadomosc>"));
            return;
        }
        if(plugin.lastHelpop.get(sender.getName()) != null && (System.currentTimeMillis() - plugin.lastHelpop.get(sender.getName())) <= plugin.configManager.helpOpDelay * 1000 && !sender.hasPermission("tools.helpop.bypass")){
            sender.sendMessage(plugin.color("&cNie spamuj na HelpOp"));
            return;
        }
        plugin.lastHelpop.put(sender.getName(), System.currentTimeMillis());
        String msg = args[0];
        for(int i=1; i<args.length; i++){
            msg += " " + args[i];
        }
        msg = msg.replaceAll("§", "&");
        String server = null;
        if(sender instanceof ProxiedPlayer){
            ProxiedPlayer player = (ProxiedPlayer) sender;
            server = "[" + player.getServer().getInfo().getName().toUpperCase() + "]";
        }
        String message = plugin.color("&4[HelpOp]" + (server != null ? " &7" + server + " " : " ") + "&f" + (sender instanceof ProxiedPlayer ? getGroupColor((ProxiedPlayer)sender) + sender.getName() : "KONSOLA") + "&8: &f") + msg;
        if(!sender.hasPermission("tools.helpop.receive")){
            sender.sendMessage(message);
        }
        ConnectAPI.broadcastMessage(message, "tools.helpop.receive");
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
    
}
