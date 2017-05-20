package pl.xcrafters.xcrbungeetools.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Command;
import pl.xcrafters.xcrbungeetools.ToolsPlugin;
import pl.xcrafters.xcrbungeetools.data.DataUser;

public class GlobalOpCommand extends Command {

    ToolsPlugin plugin;
    
    public GlobalOpCommand(ToolsPlugin plugin){
        super("globalop", "tools.globalop", "gop");
        this.plugin = plugin;
        ProxyServer.getInstance().getPluginManager().registerCommand(plugin, this);
    }
    
    @Override
    public void execute(CommandSender sender, String[] args){
        if(args.length == 0){
            sender.sendMessage(plugin.color("&9Poprawne uzycie: &b/globalop <nick>"));
            return;
        }
        String nick = args[0];
        DataUser user = plugin.dataManager.getUserByNick(nick);
        if(user == null){
            sender.sendMessage(plugin.color("&cNie znaleziono gracza &6" + nick + " &cw bazie danych!"));
            return;
        }
        boolean op = !user.getOp();
        user.setOp(op);
        user.update();
        plugin.setOp(nick, op);
        sender.sendMessage(plugin.color("&9" + (op ? "Nadano" : "Odebrano") + " status operatora graczowi &b" + user.getNick() + "&9!"));
    }
    
}
