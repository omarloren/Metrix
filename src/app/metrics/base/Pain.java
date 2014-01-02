package app.metrics.base;

import java.util.ArrayList;
import trade.Arithmetic;

/**
 * M
 * @author omar
 */
public class Pain extends Metric{
    
    private Integer initialAccount;
        
    public Pain(String id, Integer initialAccount, String from, String to) {
        super(id, from, to);
        this.initialAccount = initialAccount;
        this.getValues().add(initialAccount.doubleValue());
        this.setFlush(true);
    }
    
    @Override
    public void feed(Double value) {
        this.getValues().add(value);
    }
    
    public Double getIndex() {
        if(this.length() > 1) {
            return Arithmetic.redondear(this.getPeakDrowDrawn() / this.getValues().size());
        } else {
            return 0.0;
        }
    }
    
    public Double getPeakDrowDrawn() {
        Double sum = 0.0;
        for (int i = 0; i < this.getValues().size(); i++) {
            Double max = this.getMaximal(i);
            if(this.getValues().get(i) / max - 1 < 0) {
                sum += Math.abs(this.getValues().get(i) / max - 1);
            }
        }
        return Arithmetic.redondear(sum * 100);
    }
    
    private Double getPercent(Double p) {
        return (p / 10000);
    }
    
    public Double getMean() {
        Double sum = 0.0; 
        for (int i = 0; i < this.getValues().size(); i++) {
            sum += (this.getPercent(this.getValues().get(i)));
        }
        return sum / this.length();
    }
    
    public Double getAnunualisedReturn() {
        if(this.getValues().size()> 1) {
            Double last = (this.getValues().get(this.length()) - 100000);
            return  Arithmetic.redondear(last / this.length() * 12 / 1000, 2);
        } else {
            return 0.0;
        }
    }
    
    public Double getMonthlyAvg() {
        Double last = (this.getValues().get(this.length()) - 100000);
        return Arithmetic.redondear(last / this.length());
    }
    
    
    public Double getRatio() {
        Double temp = Arithmetic.redondear(this.getAnunualisedReturn() / this.getIndex(), 2);
        return temp;
    }
    
    public Double getMaximal(int j) {
        Double max = this.getValues().get(0);
        for (int i = 0; i < j; i++) {
            if(this.getValues().get(i) > max) {
                max = this.getValues().get(i);
            }
        }
        return max;
    }
    
    //INCOMPLETO!
    public Double getLargest() {
        Double large = 0.0;
        Double temp  = 0.0;
        for (int i = 0; i < this.getValues().size(); i++) {
            double actual = this.getValues().get(i);
            if(actual > temp) {
                large = Arithmetic.sumar(large, actual);
            }
        }
        return large;
    }
    
    public ArrayList<Double> getMonthlyValues() {
        return this.getValues();
    }
    
    public Integer getInitialAccount() {
        return this.initialAccount;
    }
    
    private Integer length(){
        return this.getValues().size() - 1;
    }
    
    @Override
    public String toString(){
        return this.getIndex() + ", " + this.getRatio();
    }

    @Override
    public Boolean isNew() {
        return false;
    }
}
