package pl.xcrafters.xcrbungeetools.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import pl.xcrafters.xcrbungeetools.ToolsPlugin;

public class SocialSpyCommand extends Command{

    ToolsPlugin plugin;
    
    public SocialSpyCommand(ToolsPlugin plugin){
        super("socialspy", "tools.socialspy", "spy");
        this.plugin = plugin;
        ProxyServer.getInstance().getPluginManager().registerCommand(plugin, this);
    }
    
    @Override
    public void execute(CommandSender sender, String[] args){
        if(sender instanceof ProxiedPlayer){
            ProxiedPlayer player = (ProxiedPlayer) sender;
            if(plugin.socialSpy.contains(player.getName())){
                plugin.socialSpy.remove(player.getName());
                player.sendMessage(plugin.color("&9SocialSpy zostal &bwylaczony&9."));
            } else {
                plugin.socialSpy.add(player.getName());
                player.sendMessage(plugin.color("&9SocialSpy zostal &bwlaczony&9."));
            }
        } else {
            sender.sendMessage(plugin.color("&cTa komenda nie moze byc wywolana z konsoli!"));
        }
    }
    
}
