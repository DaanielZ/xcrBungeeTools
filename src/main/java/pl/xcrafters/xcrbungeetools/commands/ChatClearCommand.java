package pl.xcrafters.xcrbungeetools.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Command;
import pl.xcrafters.xcrbungeetools.ToolsPlugin;

public class ChatClearCommand extends Command{

    ToolsPlugin plugin;
    
    public ChatClearCommand(ToolsPlugin plugin){
        super("chatclear", "tools.chat-clear", "cc");
        this.plugin = plugin;
        ProxyServer.getInstance().getPluginManager().registerCommand(plugin, this);
    }
    
    @Override
    public void execute(CommandSender sender, String[] args){
        plugin.cleanChat();
    }
    
}
