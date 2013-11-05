package app.trade;

import help.Date;
import trade.Arithmetic;
import trade.Ordener;


/**
 *
 * @author omar
 */

public class Orden extends Ordener {
    private Broker broker;
    private double profitLoss = 0.0; //Ganancia o Perdida.
    private Integer tickVal = 100000;
    private String date;
    public Integer weekDay;
    private Integer hora;
    private String openTime = "null";
    private String closeTime = "null";
    
    public Orden(String symbol,Integer magic, Double lotes, Character side, Double price){
        super(symbol,lotes,side,price);
        this.setMagic(magic);
        this.date = Date.getDate();
        this.weekDay = Date.dayOfWeek();
        this.openTime = Date.dateToString();
    }
    
    public Orden setBroker(Broker broker) {
        this.broker = broker;
        return this; 
    }
    
    public String getOpenTime(){
        return this.openTime;
    }
    
    /**
     * Marcamos posicion como cerrada.
     * @param time
     * @param close 
     */
    public void close(Double close) {
        this.setClosePrice(close);
        this.setActive(false);
        this.broker.closeOrder(this);
    }
    /**
     * Marcamos posicion como cerrada.
     * @param time
     * @param close 
     */
    public void close(Double close, String reason){
        this.setClosePrice(close);
        this.setActive(false);
        this.setReason(reason);
        this.closeTime = Date.horaToString();
        this.broker.closeOrder((Ordener)this);
    }
    /**
     * Calculamos el 
     * @return 
     */
    public Double getSwap() {
        double swap = 0.0;
        if(!this.date.equals(Date.getDate()) && Date.dayOfWeek() != 1){
            if(this.getSide() == '1') {
                swap = 3.20;
            } else {
                swap = 7.40;
            }
            if(Date.dayOfWeek() == 5){
                swap *= 3;
            }
        } 
        return Arithmetic.redondear(swap, 2);   
    }
    
    public double getLossProfit(){
        double temp = Arithmetic.restar(this.getClosePrice() , this.getOpenPrice());
        double res;
        if (this.getSide() == '2') {
            if (temp < 0) {
                temp = Math.abs(temp);
            } else {
                temp *= -1;
            }
        }
        res= (temp * this.tickVal) - this.getSwap();
        return Arithmetic.redondear(res, 2);
    }
    
    @Override
    public String toString() {
        return Date.dateToString()+" #"+this.getID() + " " +this.getSideStr() 
                +" "+ this.getSymbol() +" a:" + this.getOpenPrice() + " SL:" +this.getSl() + " TP:" + 
                this.getTp() ;
    }
}
