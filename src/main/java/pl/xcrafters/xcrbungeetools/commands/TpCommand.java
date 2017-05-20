package pl.xcrafters.xcrbungeetools.commands;

import java.util.ArrayList;
import java.util.List;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import pl.xcrafters.xcrbungeeconnect.ConnectAPI;
import pl.xcrafters.xcrbungeetools.ToolsPlugin;

public class TpCommand extends Command implements TabExecutor {

    ToolsPlugin plugin;
    
    public TpCommand(ToolsPlugin plugin){
        super("tp", "tools.tp");
        this.plugin = plugin;
        ProxyServer.getInstance().getPluginManager().registerCommand(plugin, this);
    }
    
    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args){
        List<String> players = new ArrayList();
        for(String nick : ConnectAPI.getNicks()){
            if(args.length == 0 || nick.toLowerCase().startsWith(args[(args.length - 1 >= 0 ? args.length - 1 : 0)].toLowerCase())){
                players.add(nick);
            }
        }
        return players;
    }
    
    @Override
    public void execute(CommandSender sender, String[] args){
        if(args.length == 0){
            if(sender.equals(ProxyServer.getInstance().getConsole())){
                sender.sendMessage(plugin.color("&9Poprawne uzycie: &b/tp <gracz> <gracz>"));
                return;
            }
            sender.sendMessage(plugin.color("&9Poprawne uzycie: &b/tp [gracz] <gracz>"));
        } else if(args.length == 1){
            if(sender.equals(ProxyServer.getInstance().getConsole())){
                sender.sendMessage(plugin.color("&9Poprawne uzycie: &b/tp <gracz> <gracz>"));
                return;
            }
            ProxiedPlayer player = (ProxiedPlayer) sender;
            String nick = ConnectAPI.getExactNick(args[0]);
            ServerInfo info = ConnectAPI.getServer(nick);
            if (info == null) {
                sender.sendMessage(plugin.color("&cGracz o nicku &6" + args[0] + " &cnie jest online!"));
                return;
            }
            plugin.handleTeleportation(player.getName(), player.getServer().getInfo(), nick, info, 0, null);
            sender.sendMessage(plugin.color("&9Przeteleportowano do gracza &b" + nick + "&9!"));
        } else if(args.length == 2){
            if(sender.hasPermission("tools.tp.others")){
                String nickFrom = ConnectAPI.getExactNick(args[0]);
                if(nickFrom == null){
                    sender.sendMessage(plugin.color("&cGracz o nicku &6" + args[0] + " &cnie jest online!"));
                    return;
                }
                String nickTo = ConnectAPI.getExactNick(args[1]);
                if(nickTo == null){
                    sender.sendMessage(plugin.color("&cGracz o nicku &6" + args[1] + " &cnie jest online!"));
                    return;
                }
                ServerInfo infoFrom = ConnectAPI.getServer(nickFrom);
                ServerInfo infoTo = ConnectAPI.getServer(nickTo);
                plugin.handleTeleportation(nickFrom, infoFrom, nickTo, infoTo, 0, null);
                sender.sendMessage(plugin.color("&9Przeteleportowano gracza &b" + nickFrom + " &9do gracza &b" + nickTo + "&9!"));
                ConnectAPI.sendMessage(nickFrom, plugin.color("&9Zostales przeteleportowany do gracza &b" + nickTo + " &9przez &b" + (sender instanceof ProxiedPlayer ? sender.getName() : "konsole") + "&9!"));
            } else {
                sender.sendMessage(plugin.color("&cNie masz uprawnien do teleportowania innych graczy!"));
            }
        }
    }
    
}
