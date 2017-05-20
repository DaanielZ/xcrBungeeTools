package pl.xcrafters.xcrbungeetools.redis;

import com.google.gson.*;
import net.md_5.bungee.api.ProxyServer;
import pl.xcrafters.xcrbungeetools.ToolsPlugin;
import pl.xcrafters.xcrbungeetools.data.DataManager;
import pl.xcrafters.xcrbungeetools.data.DataUser;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RedisManager {

    ToolsPlugin plugin;

    public RedisManager(ToolsPlugin plugin) {
        this.plugin = plugin;
        this.pool = new JedisPool(new JedisPoolConfig(), plugin.configManager.redisHost);

        this.subscriber = this.pool.getResource();

        this.instance = plugin.generateRandomString();
    }

    JedisPool pool;
    Jedis subscriber;

    String instance;

    Gson gson = new Gson();

    public JedisPool getPool() {
        return pool;
    }

    public void subscribe(final JedisPubSub pubSub, final String... channels) {
        ProxyServer.getInstance().getScheduler().runAsync(plugin, new Runnable() {
            public void run() {
                subscriber.subscribe(pubSub, channels);
            }
        });
    }

    public String getInstance() {
        return this.instance;
    }

    public void sendMessage(String channel, String message) {
        Jedis jedis = pool.getResource();
        try {
            jedis.publish(channel, message);
        } catch (JedisConnectionException ex) {
            pool.returnBrokenResource(jedis);
        } finally {
            pool.returnResource(jedis);
        }
    }

    public byte[] getOpenedInventory(UUID uuid, DataManager.InventoryType type) {
        byte[] inventory = null;

        Jedis jedis = pool.getResource();
        try {
            if(jedis.exists(("tools:" + type.name().toLowerCase() + ":" + uuid.toString()).getBytes("UTF-8"))) {
                inventory = jedis.get(("tools:" + type.name().toLowerCase() + ":" + uuid.toString()).getBytes("UTF-8"));
            }
        } catch (JedisConnectionException ex) {
            pool.returnBrokenResource(jedis);
        } catch (UnsupportedEncodingException e) {
            pool.returnResource(jedis);
        } finally {
            pool.returnResource(jedis);
        }

        return inventory;
    }

    public void setOpenedInventory(UUID uuid, DataManager.InventoryType type, byte[] inventory) {
        Jedis jedis = pool.getResource();
        try {
            jedis.set(("tools:" + type.name().toLowerCase() + ":" + uuid.toString()).getBytes("UTF-8"), inventory);
        } catch (JedisConnectionException ex) {
            pool.returnBrokenResource(jedis);
        } catch (UnsupportedEncodingException e) {
            pool.returnResource(jedis);
        } finally {
            pool.returnResource(jedis);
        }
    }

    public void addOpenedInventory(UUID uuid, UUID ownerUUID, DataManager.InventoryType type) {
        Jedis jedis = pool.getResource();
        try {
            jedis.sadd("tools:" + type.name().toLowerCase() + ":" + ownerUUID.toString() + ":opened", uuid.toString());
        } catch (JedisConnectionException ex) {
            pool.returnBrokenResource(jedis);
        } finally {
            pool.returnResource(jedis);
        }
    }

    public List<UUID> loadIgnored(DataUser user) {
        List<UUID> ignored = new ArrayList();

        Jedis jedis = null;

        try {
            jedis = pool.getResource();

            String json = jedis.hget("tools:ignored", user.getUUID().toString());

            if(json != null) {
                JsonArray array = gson.fromJson(json, JsonArray.class);

                for (JsonElement element : array) {
                    ignored.add(UUID.fromString(element.getAsString()));
                }
            }
        } catch (JedisConnectionException ex) {
            if(jedis != null) {
                pool.returnBrokenResource(jedis);
            }
        } finally {
            if(jedis != null) {
                pool.returnResource(jedis);
            }
        }

        return ignored;
    }

    public void saveIgnored(DataUser user) {
        JsonArray array = new JsonArray();

        for(UUID uuid : user.getIgnored()) {
            array.add(new JsonPrimitive(uuid.toString()));
        }

        JsonObject object = new JsonObject();
        object.addProperty("uuid", user.getUUID().toString());
        object.addProperty("instance", instance);

        Jedis jedis = null;

        try {
            jedis = pool.getResource();

            jedis.hset("tools:ignored", user.getUUID().toString(), gson.toJson(array));

            jedis.publish("ToolsUpdateIgnored", gson.toJson(object));
        } catch (JedisConnectionException ex) {
            if(jedis != null) {
                pool.returnBrokenResource(jedis);
            }
        } finally {
            if(jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

}
