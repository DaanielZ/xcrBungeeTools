package pl.xcrafters.xcrbungeetools;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import pl.xcrafters.xcrbungeeconnect.ConnectAPI;
import pl.xcrafters.xcrbungeetools.mysql.MySQLManager;
import pl.xcrafters.xcrbungeetools.tasks.AutoMessageTask;
import pl.xcrafters.xcrbungeetools.tasks.TpsTask;
import pl.xcrafters.xcrbungeetools.data.DataManager;
import pl.xcrafters.xcrbungeetools.redis.RedisManager;
import pl.xcrafters.xcrbungeetools.commands.*;
import pl.xcrafters.xcrbungeetools.listeners.*;

public class ToolsPlugin extends Plugin {

    public ConfigManager configManager;
    public DataManager dataManager;
    public MySQLManager mySqlManager;
    public RedisManager redisManager;

    TpCommand tpCommand;
    KickCommand kickCommand;
    ListCommand listCommand;
    SetSlotsCommand setSlotsCommand;
    SetMotdCommand setMotdCommand;
    GtpsCommand gtpsCommand;
    SummonCommand summonCommand;
    TellCommand tellCommand;
    ReplyCommand replyCommand;
    BanCommand banCommand;
    BanIPCommand banIPCommand;
    TempBanCommand tempBanCommand;
    MuteCommand muteCommand;
    TempMuteCommand tempMuteCommand;
    UnbanCommand unbanCommand;
    UnbanIPCommand unbanIPCommand;
    HelpOpCommand helpOpCommand;
    UnmuteCommand unmuteCommand;
    HomeCommand homeCommand;
    SetHomeCommand setHomeCommand;
    SpawnCommand spawnCommand;
    SetSpawnCommand setSpawnCommand;
    GamemodeCommand gamemodeCommand;
    FlyCommand flyCommand;
    InfoCommand infoCommand;
    GodCommand godCommand;
    TimeCommand timeCommand;
    VanishCommand vanishCommand;
    ChatLockCommand chatLockCommand;
    ChatClearCommand chatClearCommand;
    OpenEnderCommand openEnderCommand;
    OpenInventoryCommand openInventoryCommand;
    PomocCommand pomocCommand;
    GlobalCmdCommand globalCmdCommand;
    GlobalOpCommand globalOpCommand;
    BroadcastCommand broadcastCommand;
    CmdCommand cmdCommand;
    WhitelistCommand whitelistCommand;
    ArchievementCommand archievementCommand;
    SocialSpyCommand socialSpyCommand;
    ToolsReloadCommand toolsReloadCommand;
    WeatherCommand weatherCommand;
    BansCommand bansCommand;
    IgnoreCommand ignoreCommand;

    ChatListener chatListener;
    public PlayerChatListener playerChatListener;
    ProxyPingListener proxyPingListener;
    BanListener banListener;
    MuteListener muteListener;
    PostLoginListener postLoginListener;
    VanishListener vanishListener;
    PreLoginListener preLoginListener;
    GodListener godListener;
    WhitelistListener whitelistListener;
    PlayerDisconnectListener playerDisconnectListener;
    LimitListener limitListener;
    MessageListener messageListener;
    RconListener rconListener;
    PreLoginCancelListener preLoginCancelListener;
    TabCompleteListener tabCompleteListener;
    RedisListener redisListener;
    ConnectPlayerLoginListener connectPlayerLoginListener;
    ConnectPlayerDisconnectListener connectPlayerDisconnectListener;
    ConnectPlayerConnectServerListener connectPlayerConnectServerListener;
    ConnectPlayerDisconnectServerListener connectPlayerDisconnectServerListener;

    ScheduledTask autoMessageTask;
    ScheduledTask tpsTask;

    static ToolsPlugin instance;

    public Map<String, Double> lastTps = new HashMap();
    public HashMap<String, Long> lastHelpop = new HashMap();
    public HashMap<ProxiedPlayer, Long> lastMessageTime = new HashMap();
    public List<String> socialSpy = new ArrayList();

    public boolean chatLocked;

    public void onEnable() {
        this.configManager = new ConfigManager(this);
        this.dataManager = new DataManager(this);
        try {
            this.mySqlManager = new MySQLManager(this);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        this.redisManager = new RedisManager(this);

        this.tpCommand = new TpCommand(this);
        this.kickCommand = new KickCommand(this);
        this.listCommand = new ListCommand(this);
        this.setSlotsCommand = new SetSlotsCommand(this);
        this.setMotdCommand = new SetMotdCommand(this);
        this.gtpsCommand = new GtpsCommand(this);
        this.summonCommand = new SummonCommand(this);
        this.tellCommand = new TellCommand(this);
        this.replyCommand = new ReplyCommand(this);
        this.banCommand = new BanCommand(this);
        this.banIPCommand = new BanIPCommand(this);
        this.tempBanCommand = new TempBanCommand(this);
        this.muteCommand = new MuteCommand(this);
        this.tempMuteCommand = new TempMuteCommand(this);
        this.unbanCommand = new UnbanCommand(this);
        this.unbanIPCommand = new UnbanIPCommand(this);
        this.helpOpCommand = new HelpOpCommand(this);
        this.unmuteCommand = new UnmuteCommand(this);
        this.homeCommand = new HomeCommand(this);
        this.setHomeCommand = new SetHomeCommand(this);
        this.spawnCommand = new SpawnCommand(this);
        this.setSpawnCommand = new SetSpawnCommand(this);
        this.gamemodeCommand = new GamemodeCommand(this);
        this.flyCommand = new FlyCommand(this);
        this.infoCommand = new InfoCommand(this);
        this.godCommand = new GodCommand(this);
        this.timeCommand = new TimeCommand(this);
        this.vanishCommand = new VanishCommand(this);
        this.chatLockCommand = new ChatLockCommand(this);
        this.chatClearCommand = new ChatClearCommand(this);
        this.openEnderCommand = new OpenEnderCommand(this);
        this.openInventoryCommand = new OpenInventoryCommand(this);
        this.pomocCommand = new PomocCommand(this);
        this.globalCmdCommand = new GlobalCmdCommand(this);
        this.globalOpCommand = new GlobalOpCommand(this);
        this.broadcastCommand = new BroadcastCommand(this);
        this.cmdCommand = new CmdCommand(this);
        this.whitelistCommand = new WhitelistCommand(this);
        this.archievementCommand = new ArchievementCommand(this);
        this.socialSpyCommand = new SocialSpyCommand(this);
        this.toolsReloadCommand = new ToolsReloadCommand(this);
        this.weatherCommand = new WeatherCommand(this);
        this.bansCommand = new BansCommand(this);
        this.ignoreCommand = new IgnoreCommand(this);

        this.chatListener = new ChatListener(this);
        this.playerChatListener = new PlayerChatListener(this);
        this.proxyPingListener = new ProxyPingListener(this);
        this.banListener = new BanListener(this);
        this.muteListener = new MuteListener(this);
        this.postLoginListener = new PostLoginListener(this);
        this.vanishListener = new VanishListener(this);
        this.preLoginListener = new PreLoginListener(this);
        this.godListener = new GodListener(this);
        this.whitelistListener = new WhitelistListener(this);
        this.playerDisconnectListener = new PlayerDisconnectListener(this);
        this.limitListener = new LimitListener(this);
        this.messageListener = new MessageListener(this);
        this.rconListener = new RconListener(this);
        rconListener.start();
        this.preLoginCancelListener = new PreLoginCancelListener(this);
        this.tabCompleteListener = new TabCompleteListener(this);
        this.redisListener = new RedisListener(this);
        this.connectPlayerLoginListener = new ConnectPlayerLoginListener(this);
        this.connectPlayerDisconnectListener = new ConnectPlayerDisconnectListener(this);
        this.connectPlayerConnectServerListener = new ConnectPlayerConnectServerListener(this);
        this.connectPlayerDisconnectServerListener = new ConnectPlayerDisconnectServerListener(this);

        this.autoMessageTask = ProxyServer.getInstance().getScheduler().schedule(this, new AutoMessageTask(this), configManager.autoMessageDelay, configManager.autoMessageDelay, TimeUnit.SECONDS);
        this.tpsTask = ProxyServer.getInstance().getScheduler().schedule(this, new TpsTask(this), 3, 3, TimeUnit.SECONDS);

        modes.put(0, "survival");
        modes.put(1, "kreatywny");
        modes.put(2, "przygodowy");

        instance = this;

        for(String nick : ConnectAPI.getNicks()) {
            UUID uuid = ConnectAPI.getUUID(nick);
            ServerInfo info = ConnectAPI.getServer(nick);

            if(info != null) {
                if (dataManager.onlinePlayers.get(info.getName()) == null) {
                    dataManager.onlinePlayers.put(info.getName(), new ArrayList());
                }

                dataManager.onlinePlayers.get(info.getName()).add(uuid);
            }
        }
    }

    @Override
    public void onDisable() {
        tpsTask.cancel();
        autoMessageTask.cancel();

        try {
            mySqlManager.saveAll();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static ToolsPlugin getInstance() {
        return instance;
    }

    public HashMap<Integer, String> modes = new HashMap();

    public HashMap<UUID, UUID> lastMessage = new HashMap();

    public enum WeatherType {
        SKY, RAIN, THUNDER
    };

    Gson gson = new Gson();

    public String color(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    public void handleTeleportation(String nickFrom, ServerInfo infoFrom, String nickTo, ServerInfo infoTo, int delay, String afterTeleport) {
        JsonObject object = new JsonObject();

        JsonObject from = new JsonObject();
        from.addProperty("nick", nickFrom);
        from.addProperty("server", infoFrom.getName());
        object.add("from", from);

        JsonObject to = new JsonObject();
        to.addProperty("nick", nickTo);
        to.addProperty("server", infoTo.getName());
        object.add("to", to);

        object.addProperty("delay", delay);
        object.addProperty("afterTeleport", afterTeleport);

        redisManager.sendMessage("TeleportToPlayer", gson.toJson(object));
    }

    public void handleTeleportation(ProxiedPlayer player, String nickTo, ServerInfo infoTo, int delay, String afterTeleport) {
        handleTeleportation(player.getName(), player.getServer().getInfo(), nickTo, infoTo, delay, afterTeleport);
    }

    public void handleTeleportation(String nick, String world, int x, int y, int z, int delay, String afterTeleport) {
        JsonObject object = new JsonObject();

        JsonObject from = new JsonObject();
        from.addProperty("nick", nick);
        object.add("from", from);

        JsonObject to = new JsonObject();
        to.addProperty("world", world);
        to.addProperty("x", x + 0.5D);
        to.addProperty("y", y);
        to.addProperty("z", z + 0.5D);
        object.add("to", to);

        object.addProperty("delay", delay);
        object.addProperty("afterTeleport", afterTeleport);

        redisManager.sendMessage("TeleportToPosition", gson.toJson(object));
    }

    public void handleTeleportation(ProxiedPlayer player, String world, int x, int y, int z, int delay, String afterTeleport) {
        handleTeleportation(player.getName(), player.getServer().getInfo(), world, x, y, z, delay, afterTeleport);
    }

    public void handleTeleportation(String nick, ServerInfo info, String world, int x, int y, int z, int delay, String afterTeleport) {
        JsonObject object = new JsonObject();

        JsonObject from = new JsonObject();
        from.addProperty("nick", nick);
        object.add("from", from);

        JsonObject to = new JsonObject();
        to.addProperty("world", world);
        to.addProperty("x", x);
        to.addProperty("y", y);
        to.addProperty("z", z);
        to.addProperty("server", info.getName());
        object.add("to", to);

        object.addProperty("delay", delay);
        object.addProperty("afterTeleport", afterTeleport);

        redisManager.sendMessage("TeleportToPosition", gson.toJson(object));
    }

    public void handleTeleportation(ProxiedPlayer player, ServerInfo info, String world, int x, int y, int z, int delay, String afterTeleport) {
        handleTeleportation(player.getName(), info, world, x, y, z, delay, afterTeleport);
    }

    public void setPlayerHome(ProxiedPlayer player) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(baos);
            dos.writeUTF("SetPlayerHome");
            dos.writeUTF(player.getName());
            dos.flush();
            player.getServer().sendData("BungeeCord", baos.toByteArray());
            dos.close();
        } catch (IOException ex) {
        }
    }

    public void setServerSpawn(ProxiedPlayer player) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(baos);
            dos.writeUTF("SetServerSpawn");
            dos.writeUTF(player.getName());
            dos.flush();
            player.getServer().sendData("BungeeCord", baos.toByteArray());
            dos.close();
        } catch (IOException ex) {
        }
    }

    public void setGamemode(String nick, ServerInfo info, int gamemode) {
        JsonObject object = new JsonObject();
        object.addProperty("nick", nick);
        object.addProperty("gamemode", gamemode);
        redisManager.sendMessage("SetGamemode", gson.toJson(object));
    }

    public void setFly(String nick, ServerInfo info, boolean fly) {
        JsonObject object = new JsonObject();
        object.addProperty("nick", nick);
        object.addProperty("fly", fly);
        redisManager.sendMessage("SetFly", gson.toJson(object));
    }

    public void setGod(String nick, ServerInfo info, boolean god) {
        JsonObject object = new JsonObject();
        object.addProperty("nick", nick);
        object.addProperty("god", god);
        redisManager.sendMessage("SetGod", gson.toJson(object));
    }

    public void setTime(long time) {
        redisManager.sendMessage("SetTime", String.valueOf(time));
    }

    public void setWeather(WeatherType type) {
        redisManager.sendMessage("SetWeather", type.name());
    }

    public void setRespawn(String world, int x, int y, int z) {
        JsonObject object = new JsonObject();
        object.addProperty("world", world);
        object.addProperty("x", x);
        object.addProperty("y", y);
        object.addProperty("z", z);
        redisManager.sendMessage("SetRespawn", gson.toJson(object));
    }

    public void vanishPlayer(String nick, boolean vanish, boolean notify) {
        JsonObject object = new JsonObject();

        object.addProperty("nick", nick);
        object.addProperty("vanish", vanish);
        object.addProperty("notify", notify);
        object.addProperty("instance", redisManager.getInstance());

        redisManager.sendMessage("VanishPlayer", gson.toJson(object));
    }

    public void setOp(final String nick, final boolean op) {
        redisManager.sendMessage(op ? "OpPlayer" : "DeopPlayer", nick);
    }

    public long parseTimeDiffInMillis(String time) {
        Pattern timePattern = Pattern.compile(
                "(?:([0-9]+)\\s*y[a-z]*[,\\s]*)?"
                + "(?:([0-9]+)\\s*mo[a-z]*[,\\s]*)?"
                + "(?:([0-9]+)\\s*w[a-z]*[,\\s]*)?"
                + "(?:([0-9]+)\\s*d[a-z]*[,\\s]*)?"
                + "(?:([0-9]+)\\s*h[a-z]*[,\\s]*)?"
                + "(?:([0-9]+)\\s*m[a-z]*[,\\s]*)?"
                + "(?:([0-9]+)\\s*(?:s[a-z]*)?)?", Pattern.CASE_INSENSITIVE);
        Matcher m = timePattern.matcher(time);
        long seconds = 0;
        boolean found = false;
        while (m.find()) {
            if (m.group() == null || m.group().isEmpty()) {
                continue;
            }
            for (int i = 0; i < m.groupCount(); i++) {
                if (m.group(i) != null && !m.group(i).isEmpty()) {
                    found = true;
                    break;
                }
            }
            if (found) {
                if (m.group(1) != null && !m.group(1).isEmpty()) {
                    seconds += 31556926 * Integer.parseInt(m.group(1));
                }
                if (m.group(2) != null && !m.group(2).isEmpty()) {
                    seconds += 2629743 * Integer.parseInt(m.group(2));
                }
                if (m.group(3) != null && !m.group(3).isEmpty()) {
                    seconds += 604800 * Integer.parseInt(m.group(3));
                }
                if (m.group(4) != null && !m.group(4).isEmpty()) {
                    seconds += 86400 * Integer.parseInt(m.group(4));
                }
                if (m.group(5) != null && !m.group(5).isEmpty()) {
                    seconds += 3600 * Integer.parseInt(m.group(5));
                }
                if (m.group(6) != null && !m.group(6).isEmpty()) {
                    seconds += 60 * Integer.parseInt(m.group(6));
                }
                if (m.group(7) != null && !m.group(7).isEmpty()) {
                    seconds += Integer.parseInt(m.group(7));
                }
                break;
            }
        }
        if (!found) {
            return -1;
        }
        return seconds * 1000;
    }

    public static double countCapitals(String message) {
        double capitals = 0.0;
        for (int i = 0; i < message.length(); i++) {
            char character = message.charAt(i);
            if (Character.isAlphabetic(character)) {
                if (Character.isUpperCase(character)) {
                    capitals += 1;
                }
            }
        }
        return capitals;
    }

    public static double countSpecials(String message) {
        int specials = message.replaceAll("[(\\w\\s\\_)]", "").length();
        return specials;
    }

    public boolean toLowerCase(String message) {
        if (ToolsPlugin.countCapitals(message) / message.length() >= 0.1) {
            return true;
        }
        return false;
    }

    public boolean removeSpecials(String message) {
        if (ToolsPlugin.countSpecials(message) / message.length() >= 0.2) {
            return true;
        }
        return false;
    }

    public boolean canSendMessage(ProxiedPlayer player) {
        if (this.lastMessageTime.get(player) == null) {
            return true;
        }
        Long time = lastMessageTime.get(player);
        return (System.currentTimeMillis() - time) / 1000 > (ProxyServer.getInstance().getPlayers().size() > 50 ? ProxyServer.getInstance().getPlayers().size() / 10 : 5) || player.hasPermission("tools.chat.bypass");
    }

    public void cleanChat() {
        for (int i = 0; i < 100; i++) {
            for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
                if (!player.hasPermission("tools.chat-clear")) {
                    player.sendMessage(" ");
                }
            }
        }
        for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
            if (player.hasPermission("tools.chat-clear")) {
                player.sendMessage(color("&9Chat zostal wyczyszczony."));
            }
        }
        ProxyServer.getInstance().getConsole().sendMessage(color("&9Chat zostal wyczyszczony."));
    }

    public void toggleLockChat() {
        chatLocked = !chatLocked;
        for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
            if (player.hasPermission("tools.chat-lock")) {
                player.sendMessage(color("&9Chat zostal " + (chatLocked ? "&4zablokowany" : "&2odblokowany") + "&9!"));
            }
        }
        ProxyServer.getInstance().getConsole().sendMessage(color("&9Chat zostal " + (chatLocked ? "&4zablokowany" : "&2odblokowany") + "&9!"));
    }

    private static final String CHAR_LIST = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";

    public String generateRandomString(){
        StringBuffer randStr = new StringBuffer();
        for(int i=0; i<8; i++){
            int number = getRandomNumber();
            char ch = CHAR_LIST.charAt(number);
            randStr.append(ch);
        }
        return randStr.toString();
    }

    private int getRandomNumber() {
        int randomInt = 0;
        Random randomGenerator = new Random();
        randomInt = randomGenerator.nextInt(CHAR_LIST.length());
        if (randomInt - 1 == -1) {
            return randomInt;
        } else {
            return randomInt - 1;
        }
    }

}
