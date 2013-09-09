package app.trade;

import java.util.Map;

/**
 *
 * @author omar
 */
public class Extern {
    private Map settings;
    
    public Extern(Map<String, Object> settings){
        this.settings = settings;
    }
    
    public Integer getInteger(String ex){
        return Integer.parseInt((String)this.settings.get(ex));
    }
    
    public Double getDouble(String ex){
        return Double.parseDouble((String)this.settings.get(ex));
    }
}
