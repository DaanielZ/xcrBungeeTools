package pl.xcrafters.xcrbungeetools.commands;

import java.util.Arrays;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import pl.xcrafters.xcrbungeetools.ToolsPlugin;

public class SetMotdCommand extends Command implements TabExecutor{

    ToolsPlugin plugin;
    
    public SetMotdCommand(ToolsPlugin plugin){
        super("setmotd", "tools.setmotd");
        this.plugin = plugin;
        ProxyServer.getInstance().getPluginManager().registerCommand(plugin, this);
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args){
        return Arrays.asList(plugin.configManager.motd);
    }

    Gson gson = new Gson();
    
    @Override
    public void execute(CommandSender sender, String[] args){
        if(args.length == 0){
            sender.sendMessage(plugin.color("&9Poprawne uzycie: &b/setmotd <motd>"));
            return;
        }
        String motd = args[0];
        for(int i=1; i<args.length; i++){
            motd += " " + args[i];
        }
        plugin.configManager.motd = motd;
        plugin.configManager.save();
        sender.sendMessage(plugin.color("&9MOTD zostalo ustawione na: " + motd));

        JsonObject object = new JsonObject();
        object.addProperty("motd", motd);
        object.addProperty("instance", plugin.redisManager.getInstance());
        plugin.redisManager.sendMessage("SetMotd", gson.toJson(object));
    }
    
}
