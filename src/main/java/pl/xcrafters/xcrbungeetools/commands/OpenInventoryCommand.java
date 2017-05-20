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
import pl.xcrafters.xcrbungeetools.ToolsPlugin;
import pl.xcrafters.xcrbungeetools.data.DataManager;
import pl.xcrafters.xcrbungeetools.data.DataUser;

public class OpenInventoryCommand extends Command implements TabExecutor {

    ToolsPlugin plugin;

    public OpenInventoryCommand(ToolsPlugin plugin) {
        super("openinventory", "tools.openinventory", "oi", "openinv", "inv");
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

    private Gson gson = new Gson();

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender.equals(ProxyServer.getInstance().getConsole())) {
            sender.sendMessage(plugin.color("&cTa komenda nie moze byc wywolana z konsoli!"));
            return;
        }
        ProxiedPlayer player = (ProxiedPlayer) sender;
        if (args.length == 0) {
            player.sendMessage(plugin.color("&9Poprawne uzycie: &b/openinventory <nick>"));
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
            object.addProperty("type", DataManager.InventoryType.INVENTORY.name());
            plugin.redisManager.sendMessage("UploadInventory", gson.toJson(object));
        } else {
            byte[] inventory = plugin.redisManager.getOpenedInventory(user.getUUID(), DataManager.InventoryType.INVENTORY);

            if(inventory == null) {
//                inventory = SektoryAPI.getInventory(user.getUUID()); //TODO
//                plugin.redisManager.setOpenedInventory(user.getUUID(), DataManager.InventoryType.INVENTORY, inventory);
                return;
            }

            plugin.redisManager.addOpenedInventory(player.getUniqueId(), user.getUUID(), DataManager.InventoryType.INVENTORY);

            JsonObject object = new JsonObject();
            object.addProperty("uuid", player.getUniqueId().toString());
            object.addProperty("inventory_owner_uuid", user.getUUID().toString());
            object.addProperty("inventory_owner_nick", user.getNick());
            object.addProperty("type", DataManager.InventoryType.INVENTORY.name());
            plugin.redisManager.sendMessage("OpenInventory", gson.toJson(object));
        }

    }

}
