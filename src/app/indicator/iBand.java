package app.indicator;

import help.Date;
import java.util.HashMap;

/**
 *
 * @author omar
 */
public class iBand {
    private String id;
    private HashMap<String, Object> r = new HashMap();
    private String symbol;
    private int period;
    private int n;
    public iBand(String s, int p, int n){
        this.id = s + "_" + p + "_" + n;
        this.symbol = s;
        this.period = p;
        this.n = n;
    }
    
    public void setValue(HashMap r){
        this.r = r;
    }
    
    public Double getUp(){
        return (Double) this.r.get("up");
    }
    
    public Double getMiddle(){
        return (Double) this.r.get("middle");
    }
    
    public Double getDown(){
        return (Double) this.r.get("down");
    }
    
    public Integer getDate(){
        return (Integer) this.r.get("date");
    }
    
    public Integer getTime() {
        return (Integer) this.r.get("time");
    }
    
    public String getId() {
        return this.id;
    }
    
    public String getSymbol() {
        return this.symbol;
    }
    
    public int getPeriod(){
        return this.period;
    }
    
    public int getN() {
        return this.n;
    }
}
