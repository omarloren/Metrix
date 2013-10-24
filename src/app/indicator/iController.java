package app.indicator;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author omar
 */
public class iController {
    public static ArrayList indPool = new ArrayList();
    
    
    public static void refresh(String id, HashMap<String, Object> r){
        for (int i = 0; i < indPool.size(); i++) {
            iBand b = (iBand)indPool.get(i);
            if(b.getId().equals(id)){
                b.setValue(r);
            }
        }
    }
    
    public static iBand newBand(String s, int p, int n){
        String id = s + "_" + p + "_" + n;
        for (int i = 0; i < indPool.size(); i++) {
            iBand b = (iBand)indPool.get(i);
             if(b.getId().equals(id)){
                return b;
            }
        }
        
        iBand i = new iBand(s, p, n);
        indPool.add(i);
        return i;
    }    
}
