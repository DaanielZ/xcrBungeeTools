package pl.xcrafters.xcrbungeetools.listeners;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import pl.xcrafters.xcrbungeeconnect.ConnectAPI;
import pl.xcrafters.xcrbungeetools.ToolsPlugin;
import pl.xcrafters.xcrbungeetools.data.DataBan;
import pl.xcrafters.xcrbungeetools.data.DataIPBan;
import pl.xcrafters.xcrbungeetools.data.DataManager;
import pl.xcrafters.xcrbungeetools.data.DataUser;
import redis.clients.jedis.JedisPubSub;

import java.util.List;
import java.util.UUID;

public class RedisListener extends JedisPubSub {

    ToolsPlugin plugin;

    public RedisListener(ToolsPlugin plugin) {
        this.plugin = plugin;
        plugin.redisManager.subscribe(this, "SendMessage", "SetMotd", "SetSlots", "SetRespawn", "UpdateInventory", "VanishPlayer", "SetFakePlayers", "ToolsWhitelistUpdate", "ToolsUpdateIgnored", "ToolsInsertBan", "ToolsUpdateBan", "ToolsDeleteBan", "ToolsInsertIPBan", "ToolsUpdateIPBan", "ToolsDeleteIPBan", "ToolsInsertUser", "ToolsUpdateUser", "ToolsDeleteUser", "ToolsReloadConfiguration");
    }

    Gson gson = new Gson();

    @Override
    public void onMessage(String channel, String json) {
        try {
            JsonObject object = gson.fromJson(json, JsonObject.class);

            if (object.get("instance") != null && object.get("instance").getAsString().equals(plugin.redisManager.getInstance())) {
                return;
            }

            if (channel.equals("SendMessage")) {
                UUID uuid = UUID.fromString(object.get("uuid").getAsString());
                UUID senderUUID = object.get("senderUUID") != null ? UUID.fromString(object.get("senderUUID").getAsString()) : null;
                String message = object.get("message").getAsString();

                plugin.lastMessage.put(uuid, senderUUID);
                plugin.lastMessage.put(senderUUID, uuid);

                for (String spy : plugin.socialSpy) {
                    ProxiedPlayer admin = ProxyServer.getInstance().getPlayer(spy);
                    if (admin != null) {
                        admin.sendMessage(plugin.color("&7&oSOCIAL SPY: &6[" + (senderUUID != null ? plugin.dataManager.getUserByUUID(senderUUID).getNick() : "konsola") + " -> " + plugin.dataManager.getUserByUUID(uuid).getNick() + "] &7") + message);
                    }
                }
            } else if (channel.equals("SetMotd")) {
                plugin.configManager.motd = object.get("motd").getAsString();
                plugin.configManager.save();
            } else if (channel.equals("SetSlots")) {
                plugin.configManager.maxSlots = object.get("slots").getAsInt();
                plugin.configManager.save();
            } else if (channel.equals("SetRespawn")) {
                String world = object.get("world").getAsString();
                int x = object.get("x").getAsInt();
                int y = object.get("y").getAsInt();
                int z = object.get("z").getAsInt();

                plugin.configManager.spawnWorld = world;
                plugin.configManager.spawnX = x;
                plugin.configManager.spawnY = y;
                plugin.configManager.spawnZ = z;
                plugin.configManager.save();
            } else if (channel.equals("UpdateInventory")) {
                UUID uuid = UUID.fromString(object.get("inventory_owner_uuid").getAsString());
                DataManager.InventoryType type = DataManager.InventoryType.valueOf(object.get("type").getAsString());

                if (ConnectAPI.isOnline(uuid)) {
                    return;
                }

                byte[] inventory = plugin.redisManager.getOpenedInventory(uuid, type);

                if (type == DataManager.InventoryType.INVENTORY) {
//                    SektoryAPI.setInventory(uuid, inventory); //TODO
                } else {
//                    SektoryAPI.setEnderchest(uuid, inventory); //TODO
                }
            } else if (channel.equals("VanishPlayer")) {
                String nick = object.get("nick").getAsString();
                boolean vanish = object.get("vanish").getAsBoolean();
                boolean notify = object.get("notify").getAsBoolean();

                DataUser user = plugin.dataManager.getUserByNick(nick);

                if (user == null) {
                    return;
                }

                user.vanished = vanish;

                if (!notify) {
                    return;
                }

                for (ProxiedPlayer online : ProxyServer.getInstance().getPlayers()) {
                    if (online.hasPermission("tools.vanish.see") && !online.getUniqueId().equals(user.getUUID())) {
                        online.sendMessage(plugin.color("&3" + user.getNick() + " jest teraz " + (user.vanished ? "niewidoczny/a" : "widoczny/a") + "."));
                    }
                }
            } else if (channel.equals("ToolsWhitelistUpdate")) {
                boolean enabled = object.get("enabled").getAsBoolean();
                String message = object.get("message").getAsString();
                List<String> players = gson.fromJson(object.get("players").getAsJsonArray().toString(), new TypeToken<List<String>>() {
                }.getType());

                plugin.configManager.whitelistEnabled = enabled;
                plugin.configManager.whitelistMessage = message;
                plugin.configManager.whitelistedPlayers = players;
                plugin.configManager.save();
            } else if (channel.equals("ToolsUpdateIgnored")) {
                UUID uuid = UUID.fromString(object.get("uuid").getAsString());
                DataUser user = plugin.dataManager.getUserByUUID(uuid);
                user.loadIgnoredPlayers();
            } else if (channel.equals("ToolsInsertBan")) {
                int id = object.get("id").getAsInt();

                DataBan ban = plugin.mySqlManager.loadBan(id);
                plugin.dataManager.bans.add(ban);
            } else if (channel.equals("ToolsUpdateBan")) {
                int id = object.get("id").getAsInt();

                DataBan ban = plugin.dataManager.getBanByID(id);
                ban.synchronize();
            } else if (channel.equals("ToolsDeleteBan")) {
                int id = object.get("id").getAsInt();

                DataBan ban = plugin.dataManager.getBanByID(id);
                plugin.dataManager.bans.remove(ban);
            } else if (channel.equals("ToolsInsertIPBan")) {
                int id = object.get("id").getAsInt();

                DataIPBan ipban = plugin.mySqlManager.loadIPBan(id);
                plugin.dataManager.ipBansByIp.put(ipban.getIP().toLowerCase(), ipban);
            } else if (channel.equals("ToolsUpdateIPBan")) {
                int id = object.get("id").getAsInt();

                DataIPBan ipban = plugin.dataManager.getIPBanByID(id);
                ipban.synchronize();
            } else if (channel.equals("ToolsDeleteIPBan")) {
                int id = object.get("id").getAsInt();

                DataIPBan ipban = plugin.dataManager.getIPBanByID(id);
                plugin.dataManager.ipBansByIp.remove(ipban.getIP().toLowerCase());
            } else if (channel.equals("ToolsInsertUser")) {
                int id = object.get("id").getAsInt();

                DataUser user = plugin.mySqlManager.loadUser(id);
                plugin.dataManager.usersByNick.put(user.getNick().toLowerCase(), user);
                plugin.dataManager.usersByUUID.put(user.getUUID(), user);
            } else if (channel.equals("ToolsUpdateUser")) {
                int id = object.get("id").getAsInt();

                DataUser user = plugin.dataManager.getUserByID(id);

                String oldNick = user.getNick();

                user.synchronize();

                String nick = user.getNick();

                if (!oldNick.equals(nick)) {
                    plugin.dataManager.usersByNick.remove(oldNick.toLowerCase());
                    plugin.dataManager.usersByNick.put(nick.toLowerCase(), user);
                }
            } else if (channel.equals("ToolsDeleteUser")) {
                int id = object.get("id").getAsInt();

                DataUser user = plugin.dataManager.getUserByID(id);
                plugin.dataManager.usersByNick.remove(user.getNick().toLowerCase());
                plugin.dataManager.usersByUUID.remove(user.getUUID());
            } else if (channel.equals("ToolsReloadConfiguration")) {
                plugin.configManager.load();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onSubscribe(String channel, int subscribedChannels) { }

    @Override
    public void onUnsubscribe(String channel, int subscribedChannels) { }

    @Override
    public void onPSubscribe(String pattern, int subscribedChannels) { }

    @Override
    public void onPUnsubscribe(String pattern, int subscribedChannels) { }

    @Override
    public void onPMessage(String pattern, String channel, String message) { }

}
