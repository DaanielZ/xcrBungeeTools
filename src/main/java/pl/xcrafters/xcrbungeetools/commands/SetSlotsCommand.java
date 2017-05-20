package pl.xcrafters.xcrbungeetools.commands;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Command;
import pl.xcrafters.xcrbungeetools.ToolsPlugin;

public class SetSlotsCommand extends Command{

    ToolsPlugin plugin;
    
    public SetSlotsCommand(ToolsPlugin plugin){
        super("setslots", "tools.setslots");
        this.plugin = plugin;
        ProxyServer.getInstance().getPluginManager().registerCommand(plugin, this);
    }

    Gson gson = new Gson();
    
    @Override
    public void execute(CommandSender sender, String[] args){
        if(args.length == 0){
            sender.sendMessage(plugin.color("&9Poprawne uzycie: &b/setslots <ilosc>"));
            return;
        }
        int slots = Integer.parseInt(args[0]);
        plugin.configManager.maxSlots = slots;
        plugin.configManager.save();
        sender.sendMessage(plugin.color("&9Ustawiono liczbe slotow na &b" + slots + "&9!"));

        JsonObject object = new JsonObject();
        object.addProperty("slots", slots);
        object.addProperty("instance", plugin.redisManager.getInstance());
        plugin.redisManager.sendMessage("SetSlots", gson.toJson(object));
    }
    
}
