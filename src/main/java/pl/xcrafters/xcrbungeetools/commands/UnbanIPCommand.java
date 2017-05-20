package pl.xcrafters.xcrbungeetools.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import pl.xcrafters.xcrbungeetools.ToolsPlugin;
import pl.xcrafters.xcrbungeetools.data.DataIPBan;

public class UnbanIPCommand extends Command {

    ToolsPlugin plugin;
    
    public UnbanIPCommand(ToolsPlugin plugin){
        super("unbanip", "tools.unbanip");
        this.plugin = plugin;
        ProxyServer.getInstance().getPluginManager().registerCommand(plugin, this);
    }
    
    @Override
    public void execute(CommandSender sender, String[] args){
        if(args.length == 0){
            sender.sendMessage(plugin.color("&9Poprawne uzycie: &b/unbanip <ip>"));
            return;
        }
        String ip = args[0];
        if(!ip.matches("^(?:[0-9]{1,3}\\.){3}[0-9]{1,3}$")){
            sender.sendMessage(plugin.color("&cPodany adres IP &6" + ip + " &cnie jest poprawnym adresem IP!"));
            return;
        }
        DataIPBan ipban = plugin.dataManager.getIPBanByIp(args[0]);
        if(ipban == null){
            sender.sendMessage(plugin.color("&cAdres IP &6" + args[0] + " &cnie jest zbanowany!"));
            return;
        }
        if(!sender.hasPermission("tools.unbanip.all") && ipban.getAdmin() != null && !ipban.getAdmin().getNick().equalsIgnoreCase(sender.getName()) && sender instanceof ProxiedPlayer){
            sender.sendMessage(plugin.color("&cNie mozesz odbanowac adresow IP, ktorych sam nie zbanowales!"));
            return;
        }
        for(ProxiedPlayer online : ProxyServer.getInstance().getPlayers()){
            if(online.hasPermission("tools.unbanip.notify")){
                online.sendMessage(plugin.color("&c[UNBAN] Adres IP " + ipban.getIP()+ " zostal odbanowany przez &6" + (sender instanceof ProxiedPlayer ? sender.getName() : "konsole") + "."));
            }
        }
        ProxyServer.getInstance().getConsole().sendMessage(plugin.color("&c[UNBAN] Adres IP " + ipban.getIP()+ " zostal odbanowany przez &6" + (sender instanceof ProxiedPlayer ? sender.getName() : "konsole") + "."));
        ipban.delete();
    }
    
}
