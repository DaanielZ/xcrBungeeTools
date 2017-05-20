package pl.xcrafters.xcrbungeetools.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Command;
import pl.xcrafters.xcrbungeeconnect.ConnectAPI;
import pl.xcrafters.xcrbungeetools.ToolsPlugin;

public class BroadcastCommand extends Command{

    ToolsPlugin plugin;
    
    public BroadcastCommand(ToolsPlugin plugin){
        super("broadcast", "tools.broadcast", "bc", "say");
        this.plugin = plugin;
        ProxyServer.getInstance().getPluginManager().registerCommand(plugin, this);
    }
    
    @Override
    public void execute(CommandSender sender, String[] args){
        if(args.length == 0){
            sender.sendMessage(plugin.color("&9Poprawne uzycie: &b/broadcast <wiadomosc>"));
            return;
        }
        String msg = args[0];
        for(int i=1; i<args.length; i++){
            msg += " " + args[i];
        }
        ConnectAPI.broadcastMessage(plugin.color("&4[Ogloszenie&4] &6" + msg));
    }
    
}
