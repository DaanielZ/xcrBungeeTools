package pl.xcrafters.xcrbungeetools.events;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Cancellable;
import net.md_5.bungee.api.plugin.Event;

public class PlayerChatEvent extends Event implements Cancellable{

    private boolean cancelled = false;
    
    private ProxiedPlayer sender;
    
    private String message;
    private String prefix = "";
    private String suffix = "";
    private ChatColor writeColor;
    private String displayName;
    
    public PlayerChatEvent(ProxiedPlayer sender, String message){
        this.sender = sender;
        this.message = message;
        this.displayName = sender.getName();
    }
    
    public ProxiedPlayer getSender(){ return sender; }
    public String getMessage(){ return message; }
    public String getPrefix(){ return prefix; }
    public String getSuffix(){ return suffix; }
    public ChatColor getWriteColor(){ return writeColor; }
    public String getDisplayName(){ return displayName; }
    
    public void setMessage(String message){ this.message = message; }
    public void setPrefix(String prefix){ this.prefix = prefix; }
    public void setSuffix(String suffix){ this.suffix = suffix; }
    public void setWriteColor(ChatColor writeColor){ this.writeColor = writeColor; }
    public void setDisplayName(String displayName){ this.displayName = displayName; }
    
    @Override
    public void setCancelled(boolean cancelled){
        this.cancelled = cancelled;
    }
    
    @Override
    public boolean isCancelled(){
        return cancelled;
    }
    
}
