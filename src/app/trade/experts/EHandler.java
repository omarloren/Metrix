package app.trade.experts;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author omar
 */
public class EHandler {
    private String expertName;
    private Inception inception = new Inception();
    private BSFF1_8_SV bbsff1_8_sv  = new BSFF1_8_SV();
    
    
    public EHandler(String expertName) {
        this.expertName = expertName;
    }
    
    public Expert expert(){
        switch (this.expertName) {
            case "inception":
                return this.inception;
                
            case "bbsff1_8_sv":
                return this.bbsff1_8_sv;
                
            default:
                return null;
        }
    }
}
