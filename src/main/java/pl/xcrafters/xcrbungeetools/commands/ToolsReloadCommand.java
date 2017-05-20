package pl.xcrafters.xcrbungeetools.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Command;
import pl.xcrafters.xcrbungeetools.ToolsPlugin;

public class ToolsReloadCommand extends Command{

    ToolsPlugin plugin;
    
    public ToolsReloadCommand(ToolsPlugin plugin){
        super("toolsreload");
        this.plugin = plugin;
        ProxyServer.getInstance().getPluginManager().registerCommand(plugin, this);
    }
    
    @Override
    public void execute(CommandSender sender, String[] args) {
        plugin.configManager.load();
        sender.sendMessage(ChatColor.GREEN + "Poprawnie przeladowano plik konfiguracji.");

        plugin.redisManager.sendMessage("ToolsReloadConfiguration", "");
    }
    
}
