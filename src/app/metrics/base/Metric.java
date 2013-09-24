package app.metrics.base;

import java.util.ArrayList;

/**
 *
 * @author omar
 */
public abstract class Metric { 
    
    private ArrayList<Double> values;
    private String from;
    private String to;
    private Boolean flushing = false;
    private String id = "";
    Double lastValue = -1.0;
    
    public Metric(String id, String from, String to){
        this.id = id;
        this.from = from;
        this.to = to;
        this.values = new ArrayList();
    }
    
    public void setFrom(String from){
        this.from = from;
    }
    
    public void setTo(String to){
        this.to = to;
    }
    
    public String getTo() {
        return this.to;
    }
    
    public String getFrom() {
        return this.from;
    }
    
    public Boolean canFlush(){
        return this.flushing;
    }
    
    public Metric setFlush(Boolean b){
        this.flushing = b;
        return this;
    }
    
    public ArrayList<Double> getValues() {
        return this.values;
    }
    
    public String getId() {
        return this.id;
    }        
    
    public void setLastValue(Double lastValue) {
        this.lastValue = lastValue;
    }
    public abstract void feed(Double val);
    
    public abstract Boolean isActive(String date);
    public abstract Boolean isNew();
}
