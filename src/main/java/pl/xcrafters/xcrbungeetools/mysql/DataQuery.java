package pl.xcrafters.xcrbungeetools.mysql;

import pl.xcrafters.xcrbungeetools.data.DataInterface;

public class DataQuery {

    public DataQuery(DataInterface data, MySQLManager.QueryType type){
        this.data = data;
        this.type = type;
    }
    
    public DataInterface data;
    public MySQLManager.QueryType type;
    
}
