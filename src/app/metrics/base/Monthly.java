package app.metrics.base;

/**
 *
 * @author omar
 */
public class Monthly extends Metric{
        
    
    public Monthly(String id,String from, String to){
        super(id, from, to);
    }
    
    @Override
    public void feed(Double val) {
        Double rest = val;
        this.getValues().add(rest);
        this.lastValue = val;
    }
    
    public Double getMonthlyAvg() {
        Double avg = (this.lastValue - 100000) / this.getValues().size();
        return avg / 1000;
    }
    
    @Override
    public Boolean isNew() {
        Boolean b = false;
        if(this.lastValue == -1.0){
            b = true;
        }
        return b;
    }
    
    @Override
    public String toString(){
        return " Months => #"+this.getValues().size()+" From: "+this.getFrom() + " To:"+this.getTo() + " Monthly:"+this.getMonthlyAvg();
    }
}
