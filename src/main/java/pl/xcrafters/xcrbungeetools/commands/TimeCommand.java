package pl.xcrafters.xcrbungeetools.commands;

import java.util.HashMap;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Command;
import pl.xcrafters.xcrbungeetools.ToolsPlugin;

public class TimeCommand extends Command{

    ToolsPlugin plugin;
    
    HashMap<String, Long> ticksAliases = new HashMap();
    
    public TimeCommand(ToolsPlugin plugin){
        super("time", "tools.time");
        this.plugin = plugin;
        ProxyServer.getInstance().getPluginManager().registerCommand(plugin, this);
        
        ticksAliases.put("dawn", 0L);
        ticksAliases.put("day", 6000L);
        ticksAliases.put("dusk", 12000L);
        ticksAliases.put("night", 18000L);
    }
    
    @Override
    public void execute(CommandSender sender, String[] args){
        if(args.length == 0){
            sender.sendMessage(plugin.color("&9Poprawne uzycie: &b/time <dawn/day/dusk/night>"));
            return;
        }
        String time = args[0];
        if(ticksAliases.get(time) == null){
            sender.sendMessage(plugin.color("&cNiepoprawna nazwa czasu!"));
            return;
        }
        Long ticks = ticksAliases.get(time);
        plugin.setTime(ticks);
        sender.sendMessage(plugin.color("&9Poprawnie ustawiono czas na serwerze!"));
    }
    
}
