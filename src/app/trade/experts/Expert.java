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
    private Date date;
    private Candle candle;
    private Double horaIni;
    private Double horaFin;
    
    public Expert build(Integer periodo) { 
        this.setPeriodo(periodo);
        this.candle = new Candle(periodo);
        return this;
    }
    
    public void orderSend(Double lotes, Double sl, Double tp, Character side,Double price) {
        Orden orden = new Orden(this.date,this.getSymbol(), this.getMagic(),lotes,side,price);
        orden.setStopAndTake(sl, tp);
        orden.setBroker((Broker)this.getBrokeable());
        this.getBrokeable().sendOrder(orden);
    }
    
    public Candle getCandle(){
        return this.candle;
    }
    
    public Expert setExtern(Extern extern){
        this.extern = extern;
        return this;
    }
    
    public Expert setDate(Date date) {
        this.date = date;
        return this;
    }
    
    /**
     * Define si es tiempo de operar.
     * @return 
     */
    public Boolean isTradeTime(){
        double c = (this.getHora() + (this.getMinutes()*0.01)) + (this.getMinutes() /100);
        return (c < this.horaFin) && (c >= this.horaIni) && this.isReady();
    }
    
    public void setHoraIni(Double horaIni){
        this.horaIni = horaIni;
    }
    
    public void setHoraFin(Double horaFin){
        this.horaFin = horaFin;
    }
    
    public Double getHoraIni(){
        return this.horaIni;
    }
    
    public Double getHoraFin(){
        return this.horaFin;
    }
    
    @Override
    public int getSeconds() {
        throw new UnsupportedOperationException("Es un tester de minutos, no sé lo que es un segundo :(");
    }

    @Override
    public int getMinutes() {
        return this.date.getMinutes();
    }

    @Override
    public int getHora() {
        return this.date.getHora();
    }

    @Override
    public int getDay() {
        return this.date.getDay();
    }

    @Override
    public int getMonth() {
        return this.date.getMonth();
    }

    @Override
    public int getYear() {
        return this.date.getYear();
    }
    
    public String getDate() {
        return this.date.getDate();
    }
    
    @Override
    public Boolean isNewCandle() {
        return this.candle.isNew(this.getMinutes());
    }

    @Override
    public void Init(){
        //Vacío a propocito.
    }
    
    @Override
    public void onTick(){
        //Vacío a propocito.
    }
    
    @Override
    public void onDone(){
       //Vacío a propocito.
    }
}
