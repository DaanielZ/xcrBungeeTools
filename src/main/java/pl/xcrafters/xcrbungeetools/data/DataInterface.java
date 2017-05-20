package pl.xcrafters.xcrbungeetools.data;

import pl.xcrafters.xcrbungeetools.mysql.MySQLManager;

public interface DataInterface {

    public void setPrimary(int id);
    public int getPrimary();
    
    public void insert();
    public void update();
    public void delete();
    
    public String prepareQuery(MySQLManager.QueryType type);

    public String getUpdateChannel(MySQLManager.QueryType type);

    public void synchronize();
    
}
