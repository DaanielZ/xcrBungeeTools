package pl.xcrafters.xcrbungeetools.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Command;
import pl.xcrafters.xcrbungeetools.ToolsPlugin;

public class PomocCommand extends Command{

    ToolsPlugin plugin;
    
    public PomocCommand(ToolsPlugin plugin){
        super("pomoc", null, "help", "?");
        this.plugin = plugin;
        ProxyServer.getInstance().getPluginManager().registerCommand(plugin, this);
    }
    
    @Override
    public void execute(CommandSender sender, String[] args){
        for(String message : plugin.configManager.helpMessage){
            sender.sendMessage(plugin.color(message));
        }
    }
    
}
