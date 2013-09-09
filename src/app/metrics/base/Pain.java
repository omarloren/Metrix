package app.metrics.base;

import java.util.ArrayList;
import trade.Arithmetic;

/**
 *
 * @author omar
 */
public class Pain extends Metric{
        
    public Pain(Integer initialAccount, Integer from, Integer to) {
        super(from, to);
        this.values.add(initialAccount.doubleValue());
    }
    
    @Override
    public void feed(Double value) {
        this.values.add(value);
    }
    
    public Double getIndex() {
        return Arithmetic.redondear(this.getPeakDrowDrawn() / this.length());
    }
    
    private Double getPeakDrowDrawn() {
        Double sum = 0.0;
        for (int i = 0; i < this.values.size(); i++) {
            Double max = this.getMaximal(i);
            if(this.values.get(i) / max - 1 < 0){
                sum += Math.abs(this.values.get(i) / max - 1);
            }
        }
        return Arithmetic.redondear(sum * 100);
    }
    
    private Double getPercent(Double p) {
        return (p / 10000);
    }
    
    public Double getMean() {
        Double sum = 0.0; 
        for (int i = 0; i < this.values.size(); i++) {
            sum += (this.getPercent(this.values.get(i)));
        }
        return sum / (this.values.size()-1);
    }
    
    public Double getAnunualisedReturn() {
        Double last = this.values.get(this.length()) / 1000;
        double temp = ((last - 100) / 100) / this.length();
        return  Arithmetic.redondear((temp * 12) * 100);
    }
    
    public Double getRatio() {
        return (this.getAnunualisedReturn() - 0) / this.getIndex();
    }
    
    public Double getMaximal(int j) {
        Double max = this.values.get(0);
        for (int i = 0; i < j; i++) {
            if(this.values.get(i) > max){
                max = this.values.get(i);
            }
        }
        return max;
    }
    
    //INCOMPLETO!
    public Double getLargest() {
        Double large = 0.0;
        Double temp  = 0.0;
        for (int i = 0; i < this.values.size(); i++) {
            double actual = this.values.get(i);
            if(actual > temp) {
                large = Arithmetic.sumar(large, actual);
            }
        }
        return large;
    }
    
    public ArrayList<Double> getValues() {
         return this.values;
    }
    
    public ArrayList<Double> getMonthlyValues() {
        return this.values;
    }
    private int length(){
        return this.values.size() - 1;
    }
    @Override
    public String toString(){
         return "Annualised Return: " + this.getAnunualisedReturn() + " Index: "+ this.getIndex() + " Ratio:"+this.getRatio();
    }  
}
