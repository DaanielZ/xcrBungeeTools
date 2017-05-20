package pl.xcrafters.xcrbungeetools.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import pl.xcrafters.xcrbungeetools.data.DataUser;
import pl.xcrafters.xcrbungeetools.ToolsPlugin;

public class IgnoreCommand extends Command {

    ToolsPlugin plugin;

    public IgnoreCommand(ToolsPlugin plugin) {
        super("ignore", null);
        this.plugin = plugin;
        ProxyServer.getInstance().getPluginManager().registerCommand(plugin, this);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(plugin.color("&cTa komenda nie moze zostac wywolana z konsoli."));
            return;
        }
        ProxiedPlayer player = (ProxiedPlayer) sender;
        DataUser user = plugin.dataManager.getUserByPlayer(player);
        if(args.length == 0) {
            player.sendMessage(plugin.color("&9Poprawne uzycie: &b/ignore <nick>"));
            return;
        }
        String nick = args[0];
        DataUser other = plugin.dataManager.getUserByNick(nick);

        if(other == null) {
            player.sendMessage(plugin.color("&cNie znaleziono gracza o nicku " + nick + "."));
            return;
        }

        if(!user.getIgnored().contains(other.getUUID())) {
            player.sendMessage(plugin.color("&9Dodano gracza o nicku &b" + nick + " &9do ignorowanych."));
            user.addIgnored(other.getUUID());
        } else {
            player.sendMessage(plugin.color("&9Usunieto gracza o nicku &b" + nick + " &9z ignorowanych."));
            user.removeIgnored(other.getUUID());
        }
    }

}
