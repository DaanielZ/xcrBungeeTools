package pl.xcrafters.xcrbungeetools.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Command;
import pl.xcrafters.xcrbungeetools.ToolsPlugin;

public class GlobalCmdCommand extends Command {

    ToolsPlugin plugin;

    public GlobalCmdCommand(ToolsPlugin plugin) {
        super("globalcmd", "tools.globalcmd", "gcmd");
        this.plugin = plugin;
        ProxyServer.getInstance().getPluginManager().registerCommand(plugin, this);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(plugin.color("&9Poprawne uzycie: &b/globalcmd <komenda (bez /)>"));
            return;
        }
        String cmd = args[0];
        for (int i = 1; i < args.length; i++) {
            cmd += " " + args[i];
        }
        plugin.redisManager.sendMessage("ExecuteCommand", cmd);
        sender.sendMessage(plugin.color("&9Wykonano komende &b/" + cmd + " &9na wszystkich sektorach!"));
    }

}
