package pl.xcrafters.xcrbungeetools.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Command;
import pl.xcrafters.xcrbungeetools.ToolsPlugin;

public class WeatherCommand extends Command{

    ToolsPlugin plugin;
    
    public WeatherCommand(ToolsPlugin plugin){
        super("weather", "tools.weather");
        this.plugin = plugin;
        ProxyServer.getInstance().getPluginManager().registerCommand(plugin, this);
    }
    
    @Override
    public void execute(CommandSender sender, String[] args){
        if(args.length == 0){
            sender.sendMessage(plugin.color("&9Poprawne uzycie: &b/weather <sky/rain/thunder>"));
            return;
        }
        if(!args[0].equalsIgnoreCase("sky") && !args[0].equalsIgnoreCase("rain") && !args[0].equalsIgnoreCase("thunder")){
            sender.sendMessage(plugin.color("&9Poprawne uzycie: &b/weather <sky/rain/thunder>"));
            return; 
        }
        ToolsPlugin.WeatherType type = ToolsPlugin.WeatherType.valueOf(args[0].toUpperCase());
        plugin.setWeather(type);
        sender.sendMessage(plugin.color("&9Ustawiono pogode &b" + type.name() + " &9na serwerze."));
    }
    
}
