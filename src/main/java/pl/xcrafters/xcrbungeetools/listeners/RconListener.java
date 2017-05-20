package pl.xcrafters.xcrbungeetools.listeners;

import net.md_5.bungee.api.ProxyServer;
import pl.xcrafters.xcrbungeetools.ToolsPlugin;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;

public class RconListener extends Thread {

    ToolsPlugin plugin;

    public RconListener(ToolsPlugin plugin){
        this.plugin = plugin;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        while(!isInterrupted()){
            String cmd = null;

            try {
                Jedis jedis = null;

                try {
                    jedis = plugin.redisManager.getPool().getResource();

                    cmd = jedis.spop("scheduled_commands");

                    if (cmd != null) {
                        ProxyServer.getInstance().getPluginManager().dispatchCommand(ProxyServer.getInstance().getConsole(), cmd);
                    }
                } catch (JedisConnectionException ex) {
                    if (jedis != null) {
                        plugin.redisManager.getPool().returnBrokenResource(jedis);
                    }
                } finally {
                    if (jedis != null) {
                        plugin.redisManager.getPool().returnResource(jedis);
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            if(cmd == null) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public synchronized void start(){
        super.start();
    }

}
