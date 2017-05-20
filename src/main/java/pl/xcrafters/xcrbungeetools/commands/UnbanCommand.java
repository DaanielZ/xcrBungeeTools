package pl.xcrafters.xcrbungeetools.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import pl.xcrafters.xcrbungeetools.data.DataUser;
import pl.xcrafters.xcrbungeetools.ToolsPlugin;
import pl.xcrafters.xcrbungeetools.data.DataBan;

public class UnbanCommand extends Command {

    ToolsPlugin plugin;
    
    public UnbanCommand(ToolsPlugin plugin){
        super("unban", "tools.unban");
        this.plugin = plugin;
        ProxyServer.getInstance().getPluginManager().registerCommand(plugin, this);
    }
    
    @Override
    public void execute(CommandSender sender, String[] args){
        if(args.length == 0){
            sender.sendMessage(plugin.color("&9Poprawne uzycie: &b/unban <nick>"));
            return;
        }
        DataBan ban = plugin.dataManager.getActiveBanByNick(args[0]);
        if(ban == null){
            sender.sendMessage(plugin.color("&cGracz o nicku &6" + args[0] + " &cnie jest zbanowany!"));
            return;
        }
        if(!sender.hasPermission("tools.unban.all") && ban.getAdmin() != null && !ban.getAdmin().getNick().equalsIgnoreCase(sender.getName()) && sender instanceof ProxiedPlayer){
            sender.sendMessage(plugin.color("&cNie mozesz odbanowac graczy, ktorych sam nie zbanowales!"));
            return;
        }
        for(ProxiedPlayer online : ProxyServer.getInstance().getPlayers()){
            if(online.hasPermission("tools.unban.notify")){
                online.sendMessage(plugin.color("&c[UNBAN] " + ban.getNick() + " zostal odbanowany przez " + (sender instanceof ProxiedPlayer ? sender.getName() : "konsole") + "."));
            }
        }
        ProxyServer.getInstance().getConsole().sendMessage(plugin.color("&c[UNBAN] " + ban.getNick() + " zostal odbanowany przez " + (sender instanceof ProxiedPlayer ? sender.getName() : "konsole") + "."));
        if(sender instanceof ProxiedPlayer) {
            ProxiedPlayer player = (ProxiedPlayer) sender;
            DataUser user = plugin.dataManager.getUserByPlayer(player);
            ban.setUnbanAdmin(user);
        }
        ban.setActive(false);
        ban.update();
    }
    
}
