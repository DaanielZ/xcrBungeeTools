package pl.xcrafters.xcrbungeetools.commands;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import pl.xcrafters.xcrbungeeconnect.ConnectAPI;
import pl.xcrafters.xcrbungeetools.data.DataUser;
import pl.xcrafters.xcrbungeetools.ToolsPlugin;
import pl.xcrafters.xcrbungeetools.data.DataManager.InventoryType;

public class OpenEnderCommand extends Command implements TabExecutor {

    ToolsPlugin plugin;

    public OpenEnderCommand(ToolsPlugin plugin) {
        super("openender", "tools.openender", "oe", "ender");
        this.plugin = plugin;
        ProxyServer.getInstance().getPluginManager().registerCommand(plugin, this);
    }

    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        List<String> players = new ArrayList();
        for(String nick : ConnectAPI.getNicks()){
            if(args.length == 0 || nick.toLowerCase().startsWith(args[(args.length - 1 >= 0 ? args.length - 1 : 0)].toLowerCase())){
                players.add(nick);
            }
        }
        return players;
    }

    Gson gson = new Gson();

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender.equals(ProxyServer.getInstance().getConsole())) {
            sender.sendMessage(plugin.color("&cTa komenda nie moze byc wywolana z konsoli!"));
            return;
        }
        ProxiedPlayer player = (ProxiedPlayer) sender;
        if (args.length == 0) {
            player.sendMessage(plugin.color("&9Poprawne uzycie: &b/openender <nick>"));
            return;
        }
        String nick = args[0];
        DataUser user = plugin.dataManager.getUserByNick(nick);
        if (user == null) {
            player.sendMessage(plugin.color("&cNie znaleziono gracza &6" + nick + " &cw bazie danych!"));
            return;
        }

        if(ConnectAPI.isOnline(user.getUUID())) {
            JsonObject object = new JsonObject();
            object.addProperty("uuid", player.getUniqueId().toString());
            object.addProperty("inventory_owner_uuid", user.getUUID().toString());
            object.addProperty("inventory_owner_nick", user.getNick());
            object.addProperty("type", InventoryType.ENDERCHEST.name());
            plugin.redisManager.sendMessage("UploadInventory", gson.toJson(object));
        } else {
            byte[] inventory = plugin.redisManager.getOpenedInventory(user.getUUID(), InventoryType.ENDERCHEST);

            if(inventory == null) {
//                inventory = SektoryAPI.getEnderchest(user.getUUID()); //TODO
//                plugin.redisManager.setOpenedInventory(user.getUUID(), InventoryType.ENDERCHEST, inventory);
                return;
            }

            plugin.redisManager.addOpenedInventory(player.getUniqueId(), user.getUUID(), InventoryType.ENDERCHEST);

            JsonObject object = new JsonObject();
            object.addProperty("uuid", player.getUniqueId().toString());
            object.addProperty("inventory_owner_uuid", user.getUUID().toString());
            object.addProperty("inventory_owner_nick", user.getNick());
            object.addProperty("type", InventoryType.ENDERCHEST.name());
            plugin.redisManager.sendMessage("OpenInventory", gson.toJson(object));
        }

    }

}
