package pl.xcrafters.xcrbungeetools.data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import pl.xcrafters.xcrbungeetools.data.DataManager.InventoryType;

public class DataInventory {

    public InventoryType type;
    public List<ProxiedPlayer> players = new ArrayList();
    public String opened;
    public UUID uuid;
    
}
