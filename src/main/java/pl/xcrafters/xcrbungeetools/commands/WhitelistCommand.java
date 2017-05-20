package pl.xcrafters.xcrbungeetools.commands;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Command;
import pl.xcrafters.xcrbungeetools.ToolsPlugin;

import java.util.List;

public class WhitelistCommand extends Command{

    ToolsPlugin plugin;
    
    public WhitelistCommand(ToolsPlugin plugin){
        super("whitelist", "tools.whitelist", "wl");
        this.plugin = plugin;
        ProxyServer.getInstance().getPluginManager().registerCommand(plugin, this);
    }

    Gson gson = new Gson();
    
    @Override
    public void execute(CommandSender sender, String[] args){
        if(args.length == 0){
            sender.sendMessage(plugin.color("&9Poprawne uzycie: &b/whitelist <add/remove/on/off/message/list>"));
            return;
        }
        if(args[0].equalsIgnoreCase("on")){
            plugin.configManager.whitelistEnabled = true;
            plugin.configManager.save();
            sender.sendMessage(plugin.color("&9Whitelist'a zostala wlaczona!"));
        } else if(args[0].equalsIgnoreCase("off")){
            plugin.configManager.whitelistEnabled = false;
            plugin.configManager.save();
            sender.sendMessage(plugin.color("&9Whitelist'a zostala wylaczona!"));
        } else if(args[0].equalsIgnoreCase("add")){
            if(args.length < 2){
                sender.sendMessage(plugin.color("&9Poprawne uzycie: &b/whitelist add <nick>"));
                return;
            }
            if(args.length == 2) {
                plugin.configManager.whitelistedPlayers.add(args[1].toLowerCase());
                plugin.configManager.save();
                sender.sendMessage(plugin.color("&9Dodano gracza &b" + args[1] + " &9do whitelist'y!"));
            } else {
                for(int i=1; i<args.length; i++) {
                    plugin.configManager.whitelistedPlayers.add(args[i].toLowerCase());
                }
                plugin.configManager.save();
                sender.sendMessage(plugin.color("&9Dodano podanych graczy do whitelist'y!"));
            }
        } else if(args[0].equalsIgnoreCase("remove")){
            if(args.length < 2){
                sender.sendMessage(plugin.color("&9Poprawne uzycie: &b/whitelist remove <nick>"));
                return;
            }
            plugin.configManager.whitelistedPlayers.remove(args[1].toLowerCase());
            plugin.configManager.save();
            sender.sendMessage(plugin.color("&9Usunieto gracza &b" + args[1] + " &9z whitelist'y!"));
        } else if(args[0].equalsIgnoreCase("message")){
            if(args.length < 2){
                sender.sendMessage(plugin.color("&9Poprawne uzycie: &b/whitelist message <wiadomosc>"));
                return;
            }
            String msg = args[1];
            for(int i=2; i<args.length; i++){
                msg += " " + args[i];
            }
            plugin.configManager.whitelistMessage = msg;
            plugin.configManager.save();
            sender.sendMessage(plugin.color("&9Ustawiono wiadomosc whitelist'y na:&f " + plugin.color(msg)));
        } else if(args[0].equalsIgnoreCase("list")){
            String players = "";
            for(String player : plugin.configManager.whitelistedPlayers){
                players += player + ", ";
            }
            sender.sendMessage(plugin.color("&9Gracze na whitelist: &b" + players));
        }

        if(!args[0].equalsIgnoreCase("list")) {
            JsonObject object = new JsonObject();
            object.addProperty("enabled", plugin.configManager.whitelistEnabled);
            object.addProperty("message", plugin.configManager.whitelistMessage);

            JsonArray players = gson.toJsonTree(plugin.configManager.whitelistedPlayers, new TypeToken<List<String>>() {}.getType()).getAsJsonArray();

            object.add("players", players);
            object.addProperty("instance", plugin.redisManager.getInstance());

            plugin.redisManager.sendMessage("ToolsWhitelistUpdate", gson.toJson(object));
        }
    }
    
}
