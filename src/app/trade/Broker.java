package app.trade;

import java.util.ArrayList;
import trade.Arithmetic;
import trade.Brokeable;
import trade.Ordener;
import trade.indicator.IndicatorController;
import util.Date;

/**
 *
 * @author omar
 */
public class Broker extends Brokeable{
    private ArrayList<Orden> ordersClosed = new ArrayList();
    IndicatorController indicatorController;
    private Integer consecWins = 0;
    private Integer consecLoss = 0;
    private Integer racha = 0;
    private Integer prevOrder = -1;
    private Integer shorts = 0;
    private Integer longs = 0;
    private Integer lossTrades = 0;
    private Integer winTrades = 0;
    private Integer totalTrades = 0;
    private Integer initialDeposit;
    private Double balance ;
    private Double maxFloatProf;
    private Double minFloatProf;
    private Double grossLoss = 0.0;
    private Double grossProfit = 0.0;
    private Double drowDown = 0.0;
    private Double drowDownVal = 0.0;
    private Double largestProfit = 0.0;
    private Double largestLoss = 0.0;
    private Double shortsPercent;
    private Double longsPercent;
    private Double winsPercent;
    private Double lossPercent;
    private Double Bid;
    private Double Ask;
    
    public Broker(Integer initialDeposit){
        super();
        this.indicatorController = this.getIndicatorController();
        this.initialDeposit = initialDeposit;
        this.balance = this.initialDeposit.doubleValue();
        this.maxFloatProf = this.initialDeposit.doubleValue();
        this.minFloatProf = this.initialDeposit.doubleValue();
    }
    
    @Override
    public void setOpenMin(Double d) {
        this.indicatorController.setOpenMinute(d);
    }
    
    /**
     * Saldamos variables.
     */
    public void flushBroker(){
        //Porcentajes:
        //Nota: Si divides dos daran como resultado un entero asi , casteamos 
        //Uno de los dos a float y este cast se hace antes que la division ;).
        this.shortsPercent = new Double ((float)this.shorts / this.totalTrades);
        this.longsPercent = new Double ((float)this.longs / this.totalTrades);
        this.winsPercent = new Double ((float)this.winTrades / this.totalTrades);
        this.lossPercent = new Double ((float)this.lossTrades / this.totalTrades);
        this.drowDownVal = (this.drowDown * this.initialDeposit)/100;
        //La racha:
        if (this.prevOrder > 0) {
            if(this.racha > this.consecWins){
                this.consecWins = this.racha;
            }
        }else if (this.prevOrder < 0) {
            if (this.racha > this.consecLoss) {
                this.consecLoss = this.racha;
            }
        }
    }
    
    /**
     * Refrescamos las perdidas o ganacias flotantes asi como el drow - down.
     * @param orden 
     */
    @Override
    public void refreshDrowDown(Ordener orden){
        Orden o  = (Orden)orden;
        Double floatProfit = this.initialDeposit + o.getLossProfit();
        Double tempDropDn = 0.0;
        if (floatProfit > this.maxFloatProf) {
            this.maxFloatProf = floatProfit;
            tempDropDn = ((this.maxFloatProf - this.minFloatProf)/this.maxFloatProf) * 100;
            
        }else if (floatProfit <this.minFloatProf){
            this.minFloatProf = floatProfit;
            tempDropDn = ((this.maxFloatProf - this.minFloatProf)/this.maxFloatProf) * 100;
        }
        if (this.drowDown < tempDropDn) {
            this.drowDown = tempDropDn;
        }
    }
    
    /**
     * Cerramos todas las ordenes activas.
     */
    public void closeActiveOrders(){
        for (int i = 0; i < this.getOrders().size(); i++) {
            Orden temp = (Orden)this.getOrders().get(i);
            if (temp.isActive()) {
                if(temp.getSide() == '1'){
                    temp.close(this.Ask);
                }else if (temp.getSide() == '2'){
                    temp.close(this.Bid,"Closed by the broker");
                }
            }
        }
    }
    
    
    @Override
    public void ordenOpenCallback(Ordener o) {
        //System.out.println(" + Open  - " +(Orden)o);
    }

    @Override
    public void orderCloseCallback(Ordener o) {
        Orden orden = (Orden) o;
        
        //System.err.println(" - Close - " + Date.dateToString()+ " #"+o.getID() + " " + o.getClosePrice() + " " +  o.getReason() + " Profit:"+ orden.getLossProfit());
        this.ordersClosed.add(orden);
        this.balance += orden.getLossProfit();
        this.totalTrades++;
        if (orden.getSide() == '1') { 
            this.longs++;   //Contamos las compras.
        }else if (orden.getSide() == '2') {
            this.shorts++; //Contamos las ventas.
        }
        //Contamos ganancias/Perdidas, y racha +/-.
        if (orden.getLossProfit()>0) { 
            this.winTrades++;
            
            this.grossProfit += orden.getLossProfit();
            if(orden.getLossProfit() > this.largestProfit) {
                this.largestProfit = orden.getLossProfit();
            }
            if (this.prevOrder != null) { //Si no es la primer orden en cerrar.
                if (this.prevOrder<0) {
                    if (this.consecLoss <this.racha){
                        this.consecLoss = this.racha;
                    }
                    this.racha=0;
                    this.prevOrder = 1; //Marcamos racha como positiva.
                }                       
            } else {
                this.prevOrder = 1;
            }
        }else if (orden.getLossProfit() < 0) { //Si la orden perdió.
            this.lossTrades++;   
            this.grossLoss += orden.getLossProfit();
            if (Math.abs(orden.getLossProfit()) > this.largestLoss) {
                this.largestLoss = orden.getLossProfit();
            }
            if (this.prevOrder != null) {
                if (this.prevOrder>0) {
                    if (this.consecWins<this.racha) {
                        this.consecWins = this.racha;
                    }
                    this.racha=0;
                    this.prevOrder = -1;
                }
            }else{
                this.prevOrder = -1;
            }
        }
        this.racha++;
        //System.out.println(this);
    }
    
    /**
     * Obtenemos el total de ordenes de la prueba.
     * @return 
     */
    @Override
    public ArrayList<Ordener> getOrders(){
        return this.getOrdersActives();
    }
    /**
     * @return balance actual.
     */
    public Double getBalance(){   
        return this.balance;
    }
    public Double getProfit() {
        return (this.getBalance() - this.initialDeposit);
    }
    public Double getDrowDown(){
        return this.drowDown;
    }
    
    public Double getDrowDownValue(){
        return this.drowDownVal;
    }
    public Double getShortPositionsPercent(){
          return this.shortsPercent;
    }
    public Double getLongPositionsPerscent(){
        return this.longsPercent;
    }
    
    public Integer getShortPositions(){
        return this.shorts;
    }
    public Integer getLongPositions(){
        return this.longs;
    }
    
    public Integer getWinTrades(){
        return this.winTrades;
    }
    public Integer getLossTrades(){
        return this.lossTrades;
    }
    
    public Integer getTotalTrades(){
        return this.totalTrades;
    }
    @Override
    public String toString(){
         return " | Trades:"+ this.totalTrades+" | Balance: "+ this.getBalance() + " | Relative DrowDown:"+Arithmetic.redondear(this.drowDown,3) + " Profit:"+this.getProfit();
    }    
}
