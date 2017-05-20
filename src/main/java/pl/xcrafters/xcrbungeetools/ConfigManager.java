package pl.xcrafters.xcrbungeetools;

import java.io.*;
import java.util.HashMap;
import java.util.List;

import com.google.common.io.ByteStreams;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public class ConfigManager {

    ToolsPlugin plugin;
    Configuration config;
    
    public ConfigManager(ToolsPlugin plugin){
        this.plugin = plugin;
        load();
    }
    
    public String mysqlHost;
    public String mysqlBase;
    public String mysqlUser;
    public String mysqlPass;

    public String redisHost;
    
    public int maxSlots;
    public String motd;

    public List<String> welcomeMessage;
    public List<String> pluginsMessage;
    
    public int teleportDelay;
    public int helpOpDelay;
    public int messageDelay;
    
    public String spawnWorld;
    public int spawnX;
    public int spawnY;
    public int spawnZ;
    
    public List<String> disallowedWords;
    public List<String> helpMessage;
    
    public List<String> autoMessages;
    public int autoMessageDelay;
    public String autoMessagePrefix;
    
    public HashMap<String, String> groupPrefixes = new HashMap();
    public HashMap<String, String> groupColors = new HashMap();
    
    public boolean whitelistEnabled;
    public List<String> whitelistedPlayers;
    public String whitelistMessage;
    
    public int limitSeconds;
    public int limitPlayers;
    
    public void load(){
        saveDefaultConfig();
        try {
            config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(this.plugin.getDataFolder(), "config.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        mysqlHost = config.getString("config.mysql.host");
        mysqlBase = config.getString("config.mysql.base");
        mysqlUser = config.getString("config.mysql.user");
        mysqlPass = config.getString("config.mysql.pass");

        redisHost = config.getString("config.redis.host");
        
        maxSlots = config.getInt("config.max-slots");
        motd = config.getString("config.motd");

        welcomeMessage = config.getStringList("config.welcome-message");
        pluginsMessage = config.getStringList("config.plugins-message");
        
        teleportDelay = config.getInt("config.teleport-delay");
        helpOpDelay = config.getInt("config.helpop-delay");
        messageDelay = config.getInt("config.message-delay");
        
        spawnWorld = config.getString("config.spawn-location.world");
        spawnX = config.getInt("config.spawn-location.x");
        spawnY = config.getInt("config.spawn-location.y");
        spawnZ = config.getInt("config.spawn-location.z");
        
        disallowedWords = config.getStringList("config.disallowed-words");
        helpMessage = config.getStringList("config.help-message");
        
        autoMessages = config.getStringList("config.auto-message.messages");
        autoMessageDelay = config.getInt("config.auto-message.delay");
        autoMessagePrefix = config.getString("config.auto-message.prefix");

        if(config.getSection("config.group-prefixes") != null) {
            for (String group : config.getSection("config.group-prefixes").getKeys()) {
                groupPrefixes.put(group.toLowerCase(), config.getString("config.group-prefixes." + group));
            }
        }

        if(config.getSection("config.group-colors") != null) {
            for (String group : config.getSection("config.group-colors").getKeys()) {
                groupColors.put(group.toLowerCase(), config.getString("config.group-colors." + group));
            }
        }
        
        whitelistEnabled = config.getBoolean("config.whitelist.enabled");
        whitelistedPlayers = config.getStringList("config.whitelist.players");
        whitelistMessage = config.getString("config.whitelist.message");
        
        limitSeconds = config.getInt("config.limit.seconds");
        limitPlayers = config.getInt("config.limit.players");
    }
    
    public void save(){
        config.set("config.max-slots", maxSlots);
        config.set("config.motd", motd);

        config.set("config.spawn-location.world", spawnWorld);
        config.set("config.spawn-location.x", spawnX);
        config.set("config.spawn-location.y", spawnY);
        config.set("config.spawn-location.z", spawnZ);
        
        config.set("config.whitelist.enabled", whitelistEnabled);
        config.set("config.whitelist.players", whitelistedPlayers);
        config.set("config.whitelist.message", whitelistMessage);

        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(config, new File(plugin.getDataFolder(), "config.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveDefaultConfig() {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }
        File configFile = new File(plugin.getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            try {
                configFile.createNewFile();
                InputStream is = plugin.getResourceAsStream("config.yml");
                OutputStream os = new FileOutputStream(configFile);
                ByteStreams.copy(is, os);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    
}
