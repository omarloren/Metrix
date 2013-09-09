package app.metrics.base;

import java.util.ArrayList;

/**
 *
 * @author omar
 */
public abstract class Metric { 
    
    ArrayList<Double> values = new ArrayList();
    private Integer from;
    private Integer to;
    
    public Metric(Integer from, Integer to){
        this.from = from;
        this.to = to;
    }
    
    public void setFrom(Integer from){
        this.from = from;
    }
    
    public void setTo(Integer to){
        this.to = to;
    }
    
    public Integer getTo() {
        return this.to;
    }
    
    public Integer getFrom() {
        return this.from;
    }
    
    public abstract void feed(Double val);
}
