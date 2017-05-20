package pl.xcrafters.xcrbungeetools.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Command;
import pl.xcrafters.xcrbungeetools.ToolsPlugin;

public class ChatLockCommand extends Command{

    ToolsPlugin plugin;
    
    public ChatLockCommand(ToolsPlugin plugin){
        super("chatlock", "tools.chat-lock", "cl");
        this.plugin = plugin;
        ProxyServer.getInstance().getPluginManager().registerCommand(plugin, this);
    }
    
    @Override
    public void execute(CommandSender sender, String[] args){
        plugin.toggleLockChat();
    }
    
}
