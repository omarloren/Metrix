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
        Double rest = val - this.lastValue;
        this.lastValue = val;
        this.getValues().add(rest);
    }
    
    public Double getMonthlyAvg() {
        Double sum = 0.0;
        for (int i = 0; i < this.getValues().size(); i++) {
            sum += this.getValues().get(i);
        }
        Double prm = (sum / this.getValues().size()) / 1000;
        return prm;
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
