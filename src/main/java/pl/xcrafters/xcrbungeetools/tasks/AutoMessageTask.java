package pl.xcrafters.xcrbungeetools.tasks;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import pl.xcrafters.xcrbungeetools.ToolsPlugin;

public class AutoMessageTask implements Runnable{

    ToolsPlugin plugin;
    
    public AutoMessageTask(ToolsPlugin plugin){
        this.plugin = plugin;
    }
    
    int lastMessageId = 0;
    
    @Override
    public void run(){
        ProxyServer.getInstance().broadcast(ChatColor.translateAlternateColorCodes('&', plugin.configManager.autoMessagePrefix + " " + plugin.configManager.autoMessages.get(lastMessageId)));
        lastMessageId++;
        if(lastMessageId >= plugin.configManager.autoMessages.size()){
            lastMessageId = 0;
        }
    }
    
}
