package app.metrics.base;

import trade.Arithmetic;

/**
 *
 * @author omar
 */
public class Monthly extends Metric{
        
    public Monthly(String from, String to){
        super(from, to);        
    }
    
    @Override
    public void feed(Double val) {
        Double rest = val - this.lastValue;
        this.lastValue = val;
        this.values.add(rest);
    }
    
    public Double getMonthlyAvg() {
        Double sum = 0.0;
        for (int i = 0; i < this.values.size(); i++) {
            sum += this.values.get(i);
        }
        
        return Arithmetic.redondear(sum / this.values.size(), 2) / 1000;
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
        return " Months => #"+this.values.size()+" From: "+this.getFrom() + " To:"+this.getTo() + " Monthly:"+this.getMonthlyAvg();
    }

    @Override
    public Boolean isActive(String date) {
        Integer from = Integer.parseInt(this.getFrom());
        Integer to = Integer.parseInt(this.getTo());
        Integer d = Integer.parseInt(date);
        Boolean b = false;
        if(d >= from && d < to) {
            b = true;
        }
        return b;
    }
}
