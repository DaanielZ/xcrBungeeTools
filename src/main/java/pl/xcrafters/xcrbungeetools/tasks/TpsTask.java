package pl.xcrafters.xcrbungeetools.tasks;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.config.ServerInfo;
import pl.xcrafters.xcrbungeetools.ToolsPlugin;

public class TpsTask implements Runnable{

    ToolsPlugin plugin;

    public TpsTask(ToolsPlugin plugin){
        this.plugin = plugin;
    }

    Gson gson = new Gson();

    @Override
    public void run(){
        for(final ServerInfo info : ProxyServer.getInstance().getServers().values()){
            Callback<ServerPing> pingBack = new Callback<ServerPing>() {
                @Override
                public void done(ServerPing result, Throwable error) {
                    if(info != null) {
                        plugin.lastTps.remove(info.getName());
                        if (error == null) {
                            double tps = 0.0;
                            try {
                                tps = gson.fromJson(ChatColor.stripColor(result.getDescription()), JsonObject.class).get("tps").getAsDouble();
                            } catch (NumberFormatException ex) {
                                ex.printStackTrace();
                            }
                            plugin.lastTps.put(info.getName(), tps);
                        } else {
                        }
                    }
                }
            };
            info.ping(pingBack);
        }
    }

}
