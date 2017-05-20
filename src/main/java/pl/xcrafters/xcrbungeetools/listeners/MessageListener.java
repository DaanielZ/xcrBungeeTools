package pl.xcrafters.xcrbungeetools.listeners;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import pl.xcrafters.xcrbungeeconnect.ConnectAPI;
import pl.xcrafters.xcrbungeetools.ToolsPlugin;
import pl.xcrafters.xcrbungeetools.data.DataUser;

import java.io.*;

public class MessageListener implements Listener {

    ToolsPlugin plugin;

    public MessageListener(ToolsPlugin plugin){
        this.plugin = plugin;
        ProxyServer.getInstance().getPluginManager().registerListener(plugin, this);
    }

    @EventHandler
    public void onPluginMessage(PluginMessageEvent event){
        if(!event.getTag().equals("BungeeCord")){
            return;
        }
        try {
            DataInputStream dis = new DataInputStream(new ByteArrayInputStream(event.getData()));
            String channel = dis.readUTF();
            if(channel.equals("SetPlayerHome")) {
                String nick = dis.readUTF();
                String location = dis.readUTF();
                DataUser user = plugin.dataManager.getUserByNick(nick);
                String[] pos = location.split(" ");
                String world = pos[0];
                int x = Integer.parseInt(pos[1]);
                int y = Integer.parseInt(pos[2]);
                int z = Integer.parseInt(pos[3]);
                user.setHome(world, x, y, z);
                user.update();
            } else if(channel.equals("RemovePlayerHome")) {
                String nick = dis.readUTF();
                DataUser user = plugin.dataManager.getUserByNick(nick);
                user.setHome(null, 0, 0, 0);
                user.update();
            } else if(channel.equals("SetServerSpawn")) {
                String location = dis.readUTF();
                String[] pos = location.split(" ");
                String world = pos[0];
                int x = Integer.parseInt(pos[1]);
                int y = Integer.parseInt(pos[2]);
                int z = Integer.parseInt(pos[3]);
                plugin.configManager.spawnWorld = world;
                plugin.configManager.spawnX = x;
                plugin.configManager.spawnY = y;
                plugin.configManager.spawnZ = z;
                plugin.configManager.save();
                plugin.setRespawn(world, x, y, z);
            } else if(channel.equals("TeleportToPosition")){
                String nick = dis.readUTF();
                String pos = dis.readUTF();
                int delay = dis.readInt();
                String afterTp = dis.readUTF();
                ServerInfo info = ConnectAPI.getServer(nick);
                if(info != null) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    DataOutputStream dos = new DataOutputStream(baos);
                    dos.writeUTF("TeleportToPosition");
                    dos.writeUTF(nick);
                    dos.writeUTF(pos);
                    dos.writeInt(delay);
                    dos.writeUTF(afterTp);
                    dos.flush();
                    info.sendData("BungeeCord", baos.toByteArray());
                    dos.close();
                }
            } else if(channel.equals("TeleportToPlayer")){
                String nick = dis.readUTF();
                String nickTo = dis.readUTF();
                String pos = dis.readUTF();
                int delay = dis.readInt();
                String afterTp = dis.readUTF();
                ProxyServer.getInstance().broadcast(nick + ": " + pos);
                ServerInfo infoFrom = ConnectAPI.getServer(nick);
                ServerInfo infoTo = ConnectAPI.getServer(nickTo);
                if(infoFrom != null && infoTo != null){
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    DataOutputStream dos = new DataOutputStream(baos);
                    dos.writeUTF("TeleportToServer");
                    dos.writeUTF(nick);
                    dos.writeUTF(infoTo.getName());
                    dos.writeUTF(pos);
                    dos.writeInt(delay);
                    dos.writeUTF(afterTp);
                    dos.flush();
                    infoFrom.sendData("BungeeCord", baos.toByteArray());
                    dos.close();
                }
            } else if(channel.equals("ExecuteCommand")) {
                String cmd = dis.readUTF();
                ProxyServer.getInstance().getPluginManager().dispatchCommand(ProxyServer.getInstance().getConsole(), cmd);
            }
            dis.close();
        } catch (IOException ex){
            ex.printStackTrace();
        }
    }

}
