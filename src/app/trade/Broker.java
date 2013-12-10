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
    private Integer prevOrder = -1;
    private Integer totalTrades = 0;
    private double balance ;
    private double shortsPercent;
    private double longsPercent;
    private Excel file;
    private Boolean tradeLog;
    private double longDrawDown= -1.0;
    private Integer longTrades = -1;
    private double longProfit;
    private Date date;
        
    public Broker(Integer initialDeposit){
        super(initialDeposit);
        this.indicatorController = this.getIndicatorController();
        this.balance = this.getInitialDeposit();
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
    
    @Override
    public void reset(){
        this.longDrawDown = this.getDrawDown();
        super.reset();
        this.longTrades = this.totalTrades;
        this.longProfit = this.getProfit();
        this.balance = this.getInitialDeposit();
        this.totalTrades = 0;
        this.hasBeingReseted = true;
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
    public void ordenOpenCallback(Ordener o) {}

    @Override
    public void orderCloseCallback(Ordener o) {
        Orden orden = (Orden) o;
        if(this.tradeLog) {
            this.file.addData(orden.getID() + ", "+ orden.getOpenTime() + ", "+ orden.getSideStr() +", "+ 1 + ", " + orden.getOpenPrice() +", "+orden.getSymbol()+", " + orden.
                getSl() + ", "+ orden.getTp() + ", " + this.date.dateToString() + ", "+orden.getClosePrice() + ", " + orden.getSwap() + ", " + orden.getLossProfit());
        }
        this.balance += orden.getLossProfit();
        this.totalTrades++;
    }
    
    public Broker setDate(Date date){
        this.date = date;
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
        return Arithmetic.redondear(this.getBalance() - this.getInitialDeposit());
    }
    
    public Double getShortPositionsPercent(){
          return this.shortsPercent;
    }
    
    public Double getLongPositionsPerscent(){
        return this.longsPercent;
    }
    
    public Integer getTotalTrades(){
        return this.totalTrades;
    }
    public Integer getLongTrades()  {
        return this.longTrades;
    }
    
    public double getLongProfit() {
        return Arithmetic.redondear(this.longProfit);
    }
    
    public double getLongDrawDown() {
        return Arithmetic.redondear(this.longDrawDown);
    }
    
    @Override
    public String toString(){
        //return this.date.dateToString()+" Profit: " + this.getProfit() + " DD: "+this.getDrowDown();
        return "";
    }
}
