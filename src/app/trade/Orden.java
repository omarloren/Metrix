package app.trade;

import trade.Arithmetic;
import trade.Ordener;
import util.Date;

/**
 *
 * @author omar
 */

public class Orden extends Ordener {
    private Broker broker;
    private Double profitLoss = 0.0; //Ganancia o Perdida.
    private Integer tickVal = 100000;
    private Integer date;
    private Integer hora;
    private String openTime = "null";
    private String closeTime = "null";
    
    public Orden(String symbol,Integer magic, Double lotes, Character side, Double price){
        super(symbol,lotes,side,price);
        this.setMagic(magic);
    }
    
    public Orden setBroker(Broker broker) {
        this.broker = broker;
        return this;
    }
    /**
     * Marcamos posicion como cerrada.
     * @param time
     * @param close 
     */
    public void close(Double close){
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
   
    
    public Double getLossProfit(){
        Double temp = Arithmetic.restar(this.getClosePrice() , this.getOpenPrice());
        if (this.getSide() == '2') {
            if (temp < 0) {
                temp = Math.abs(temp);
            } else {
                temp *= -1;
            }
        }
        return Arithmetic.redondear(temp * this.tickVal, 1);
    }
    
    @Override
    public String toString() {
        return Date.dateToString()+" #"+this.getID() + " " +this.getSideStr() 
                +" "+ this.getSymbol() +" a:" + this.getOpenPrice() + " SL:" +this.getSl() + " TP:" + 
                this.getTp() ;
    }
}
