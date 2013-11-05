package app.trade;

import help.Date;
import io.Exceptions.SettingNotFound;
import io.Inputs;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import trade.Arithmetic;
import trade.Brokeable;
import trade.Ordener;
import trade.indicator.IndicatorController;
import util.Excel;

/**
 *
 * @author omar
 */
public class Broker extends Brokeable{
    
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
    private double balance ;
    private double maxFloatProf;
    private double minFloatProf;
    private double drowDown = 0.0;
    private double drowDownVal = 0.0;
    private double largestProfit = 0.0;
    private double largestLoss = 0.0;
    private double shortsPercent;
    private double longsPercent;
    private double Bid;
    private double Ask;
    private Excel file;
    private Boolean tradeLog;
    private double longRelative = -1.0;
    private Integer longTrades = -1;
    private double longProfit;
    
    public Broker(Integer initialDeposit){
        super();
        this.indicatorController = this.getIndicatorController();
        this.initialDeposit = initialDeposit;
        this.balance = this.initialDeposit.doubleValue();
        this.maxFloatProf = this.initialDeposit.doubleValue();
        this.minFloatProf = this.initialDeposit.doubleValue();
        try {
            this.tradeLog = Boolean.parseBoolean(Inputs.getInstance().getInput("trade_log"));
        } catch (SettingNotFound ex) {
            Logger.getLogger(Broker.class.getName()).log(Level.SEVERE, null, ex);
        }
        if(this.tradeLog){
            this.file = new Excel("result");
            this.file.setHeader("Ticket, Open Time, Type, Size, Item, Price, S/L, T/P, Close Time, Price, Swap, Profit \n");
        }
    }
    
    @Override
    public void setOpenMin(double d) {
        this.indicatorController.setOpenMinute(d);
    }
    
    public void reset(){
        if(this.longTrades == -1 && this.longRelative == -1){
            this.longRelative = this.drowDown;
            this.longTrades = this.totalTrades;
            this.longProfit = this.getProfit();
            this.drowDown = 0.0;
            this.balance = this.initialDeposit.doubleValue();
            this.totalTrades = 0;
            this.maxFloatProf = this.initialDeposit.doubleValue();
            this.minFloatProf = this.initialDeposit.doubleValue();
        }
        this.file.writeItOut();
    }
    
    /**
     * Saldamos variables.
     */
    public void flushBroker(){
        //Porcentajes:
        //Nota: Si divides dos daran como resultado un entero asi , casteamos 
        //Uno de los dos a float y este cast se hace antes que la division ;).
        this.shortsPercent = (float)this.shorts / this.totalTrades;
        this.longsPercent = (float)this.longs / this.totalTrades;
        this.drowDownVal = (this.drowDown * this.initialDeposit)/100;
        //La racha:
        if (this.prevOrder > 0) {
            if(this.racha > this.consecWins) {
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
    public void refreshDrowDown(Ordener orden) {
        Orden o  = (Orden)orden;
        double floatProfit = this.initialDeposit + o.getLossProfit();
        double tempDropDn = 0.0;
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
       // System.out.println(" + Open  - " +(Orden)o);
    }

    @Override
    public void orderCloseCallback(Ordener o) {
        Orden orden = (Orden) o;
        if(this.tradeLog) {
            this.file.addData(orden.getID() + ", "+ orden.getOpenTime() + ", "+ orden.getSideStr() +", "+ 1 + ", " + orden.getOpenPrice() +", "+orden.getSymbol()+", " + orden.
                getSl() + ", "+ orden.getTp() + ", " + Date.dateToString() + ", "+orden.getClosePrice() + ", " + orden.getSwap() + ", " + orden.getLossProfit());
        }
        
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
        } else if (orden.getLossProfit() < 0) { //Si la orden perdió.
            this.lossTrades++;
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
            } else {
                this.prevOrder = -1;
            }
        }
        this.racha++;
        
    }
    
    
    public Broker setLongRelative(Double rel) {
        this.longRelative = rel;
        return this;
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
    public double getBalance(){   
        return this.balance;
    }
    public double getProfit() {
        return (this.getBalance() - this.initialDeposit);
    }
    public double getDrowDown(){
        return Arithmetic.redondear(this.drowDown);
    }
    
    public double getDrowDownValue(){
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
    public Integer getLongTrades()  {
        return this.longTrades;
    }
    
    public double getLongProfit() {
        return this.longProfit;
    }
    
    public double getLongRelative() {
        return this.longRelative;
    }
    
    @Override
    public String toString(){
        return Date.dateToString()+" Profit: " + this.getProfit() + " DD: "+this.getDrowDown();
    }
}
