package pl.xcrafters.xcrbungeetools.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import pl.xcrafters.xcrbungeeconnect.ConnectAPI;
import pl.xcrafters.xcrbungeetools.ToolsPlugin;

public class CmdCommand extends Command {

    ToolsPlugin plugin;

    public CmdCommand(ToolsPlugin plugin) {
        super("cmd", "tools.cmd");
        this.plugin = plugin;
        ProxyServer.getInstance().getPluginManager().registerCommand(plugin, this);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer) {
            ProxiedPlayer player = (ProxiedPlayer) sender;
            if (args.length == 0) {
                sender.sendMessage(plugin.color("&9Poprawne uzycie: &b/cmd <komenda (bez /)>"));
                return;
            }
            String cmd = args[0];
            for (int i = 1; i < args.length; i++) {
                cmd += " " + args[i];
            }
            plugin.redisManager.sendMessage("ExecuteCommandAsPlayer", player.getName() + " " + cmd);
        } else if(args.length < 2) {
            sender.sendMessage(plugin.color("&9Poprawne uzycie: &b/cmd <nick> <komenda (bez /)>"));
        } else {
            String nick = ConnectAPI.getExactNick(args[0]);
            if(nick == null) {
                sender.sendMessage(plugin.color("&cGracz o podanym nicku nie jest online!"));
                return;
            }
            String cmd = args[1];
            for (int i = 2; i < args.length; i++) {
                cmd += " " + args[i];
            }
            plugin.redisManager.sendMessage("ExecuteCommandAsPlayer", nick + " " + cmd);
            sender.sendMessage(plugin.color("&9Poprawnie wykonano komende &b/" + cmd + " &9dla gracza &b" + nick + "&9!"));
        }
    }

}
