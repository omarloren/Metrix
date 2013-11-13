package app.trade.experts;

import app.trade.Broker;
import app.trade.Extern;
import app.trade.Orden;
import help.Candle;
import help.Date;
import trade.AbstractExpert;
;
/**
 *
 * @author omar
 */
public class Expert extends AbstractExpert{
    public Extern extern; 
   
    private Candle candle;
    public Expert build(Integer periodo) { 
        this.setPeriodo(periodo);
        this.candle = new Candle(periodo);
        return this;
    }
    
    public void orderSend(Double lotes, Double sl, Double tp, Character side,Double price) {
        Orden orden = new Orden(this.getSymbol(), this.getMagic(),lotes,side,price);
        orden.setStopAndTake(sl, tp);
        orden.setBroker((Broker)this.getBrokeable());
        this.getBrokeable().sendOrder(orden);
    }
    
    public Candle getCandle(){
        return this.candle;
    }
    
    public void setExtern(Extern extern){
        this.extern = extern;
    }
    
    @Override
    public int getSeconds() {
        throw new UnsupportedOperationException("Es un tester de minutos, no sé lo que es un segundo :(");
    }

    @Override
    public int getMinutes() {
        return Date.getMinutes();
    }

    @Override
    public int getHora() {
        return Date.getHora();
    }

    @Override
    public int getDay() {
        return Date.getDay();
    }

    @Override
    public int getMonth() {
        return Date.getMonth();
    }

    @Override
    public int getYear() {
        return Date.getYear();
    }
    
    public String getDate() {
        return Date.getDate();
    }
    
    @Override
    public Boolean isNewCandle() {
        return this.candle.isNew(this.getMinutes());
    }

}
