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
import trade.indicator.base.util.StandardDeviation;
import util.Excel;

/**
 *
 * @author omar
 */
public class Broker extends Brokeable{
    
    IndicatorController indicatorController;
    private Integer totalTrades = 0;
    private double shortsPercent;
    private double longsPercent;
    private Excel file;
    private Boolean tradeLog;
    private double longDrawDown= -1.0;
    private Integer longTrades = -1;
    //variables para acumular datos del periodo largo.
    private double longProfit;
    private Double longSharp;
    private Double longOpIR;
    //Para metricos de operaciones (IR y sharp).
    private ArrayList<Double> overProfitOps = new ArrayList();
    private ArrayList<Double> underProfitOps = new ArrayList();
    private ArrayList<Double> opsProfit = new ArrayList();
    private Date date;
    
    public Broker(Integer initialDeposit){
        super(initialDeposit);
        this.indicatorController = this.getIndicatorController();
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
    
    /**
     * Reinicia variables.
     */
    @Override
    public void reset(){
        this.longDrawDown = this.getDrawDown();
        this.longTrades = this.totalTrades;
        this.longProfit = this.getProfit();
        this.hasBeingReseted = true;
        this.longSharp = this.getSharp();
        this.longOpIR = this.getOpIR();
        this.overProfitOps = new ArrayList();
        this.underProfitOps = new ArrayList();
        this.opsProfit = new ArrayList();
        this.totalTrades = 0;
        super.reset();        
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
        if (this.tradeLog) {
            this.file.addData(orden.getID() + ", "+ orden.getOpenTime() + ", "+ orden.getSideStr() +", "+ 1 + ", " + orden.getOpenPrice() +", "+orden.getSymbol()+", " + orden.
                getSl() + ", "+ orden.getTp() + ", " + this.date.dateToString() + ", "+orden.getClosePrice() + ", " + orden.getSwap() + ", " + orden.getLossProfit());
        }
        this.setBalance(this.getBalance() + orden.getLossProfit());
        /**
         * Al cierre de una operación guardamos su profit
         */
        this.opsProfit.add(orden.getLossProfit());
        /**
         * De acuerdo al promedio de operaciones, guardamos el profit de esta 
         * orden en operaciones que superan el promedio o no.
         */
        if (orden.getLossProfit() >= this.getOpsAvg()) {
            this.overProfitOps.add(orden.getLossProfit());
        } else {
           this.underProfitOps.add(orden.getLossProfit());
        }
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
     * @return  devuelve el promedio de ganancia de las operaciones.
     */
    public Double getOpsAvg() {
        Double sum = 0.0;
        for (int i = 0; i < this.opsProfit.size(); i++) {
            sum += this.opsProfit.get(i);
        }
        return sum / this.opsProfit.size();
    }
    
    /**
     * Calcula el Sharp ratio, que es la división de el promedio de las ganancias
     * de las operaciones entre  la desviación estandar de las operaciones que no 
     * llegaron a ese promedio.
     * @return sharp ratio.
     */
    public Double getSharp() {
        StandardDeviation sd = new StandardDeviation(this.underProfitOps.size(), this.underProfitOps);
        return Arithmetic.redondear(this.getOpsAvg() / sd.calculateStdDev());
    }
    
    /**
     * Calcula el porcentaje de las operaciones que llegaron al promedio entre 
     * la raíz cuadrada de el número de operaciones.
     * @return 
     */
    public Double getOpIR(){
        Double p = this.overProfitOps.size() * 100.0 / this.totalTrades;
        return Arithmetic.redondear(p * Math.sqrt(this.totalTrades));
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
    
    public double getLongSharp(){
        return this.longSharp;
    }
    
    public double getLongOpIR(){
        return this.longOpIR;
    }
    @Override
    public String toString(){
        //return this.date.dateToString()+" Profit: " + this.getProfit() + " DD: "+this.getDrowDown();
        return "";
    }
}
