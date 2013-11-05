package app.trade;

import app.metrics.MetricsController;
import app.trade.experts.BSFF1_8_SV;
import com.mongodb.DBObject;
import help.Candle;
import help.Date;
import io.Exceptions.SettingNotFound;
import io.Inputs;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import trade.Arithmetic;
import util.Settings;

/**
 *
 * @author omar
 */
public class Gear extends Thread{
    
    private Broker broker;
    public int id;
    private BSFF1_8_SV expert;
    //private Prueba expert;
    private Settings settings;    
    private String symbol;
    private Integer periodo;
    private Candle candle;
    private int lastDay = 0;
    private int lastMonth = 1;
    private Integer from;
    private Integer _break;
    private Integer to;
    private Inputs inputs;
    private Integer cont;
    private Boolean metrics;
    private HashMap<String, List> bollsMap = new HashMap();
            
    public Gear(Settings settings, Map<String, Object> it, Integer from, Integer _break, Integer to) {
        this.inputs = Inputs.getInstance();
        this.settings = settings;
        this.from = from;
        this._break = _break;
        this.to = to;
        this.expert = new BSFF1_8_SV();
        //this.expert = new Prueba();
        this.symbol = this.settings.getSymbol();
        this.periodo = this.settings.getPeriod();
        this.candle = new Candle(this.periodo);
        this.candle.setStrict(false);
        this.broker = new Broker(this.settings.getInitialWon());        
        this.broker.setSpread(this.settings.getSpread());
        this.expert.build(this.periodo).__construct(this.broker, this.from, this.symbol,this.settings.getPoint(), this.settings.getMagic());   
        Extern extern = new Extern(it);
        this.expert.setExtern(extern);
        this.expert.Init();
        //this.loadData();
        this.cont = 0;
        try {
            this.metrics = Boolean.getBoolean(this.inputs.getInput("metrics"));
        } catch (SettingNotFound ex) {
            Logger.getLogger(Gear.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void setId(int id){
        this.id = id;
    }
    
    
    public void tick(DBObject t) {
        try {
            Date.setTime(String.valueOf(t.get("DTYYYYMMDD")), String.valueOf(t.get("TIME")));
            ArrayList<Double> arr = this.evaluate(t);
            double open = arr.get(0);
            
            if(Date.getMonth() != this.lastMonth && this.metrics) {
                this.lastMonth = Date.getMonth();
                MetricsController.refresh(Date.getDate(),this.broker.getBalance());
                Integer d = Integer.parseInt(Date.getDate());
                try{
                    if(d >= this._break && d < this.to) {
                        this.broker.reset();
                    }
                } catch(Exception e){
                    System.out.println(d + " " +this._break +" "+this.to );
                }                
            }
            this.expert.setOpenMin(open);            
            
            if (this.candle.isNew(Date.getMinutes())) {   
                //this.refreshData();
                this.broker.setOpenMin(open);
            }
            for (int i = 0; i < arr.size(); i++) {
                double bid =arr.get(i);
                double ask = Arithmetic.sumar(bid, this.settings.getSpread());
                this.broker.ticker(bid);
                this.expert.setBid(bid);  
                this.expert.setAsk(ask);
                //if(this.expert.isTradeTime() || !this.broker.getOrders().isEmpty()){
                    this.expert.onTick();
                //}
            }
        } catch (Exception ex) {
            Logger.getLogger(Gear.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Evalua la vela de minuto y filtra los precios en el orden que deberán ser
     * procesados.
     * @param e
     * @return Precios filtrados.
     */
    private ArrayList<Double> evaluate(DBObject e) {
        ArrayList<Double> r = new ArrayList();
        r.add((Double)e.get("OPEN"));
        r.add((Double)e.get("HIGH"));
        r.add((Double)e.get("LOW"));
        r.add((Double)e.get("CLOSE"));
       
        Double base = r.get(0);
        for (int i = 1; i < r.size(); i++) {   
            if(Double.compare(base , r.get(i)) == 0){
                r.remove(i);
                i--;
            }else {
                base = r.get(i);
            }
        }
        return r;
    }
    
    public Broker getBroker() {
        return this.broker;
    }
    
    public void flush() {
         MetricsController.flushMetrics(this.broker.getBalance());
         this.broker = new Broker(this.settings.getInitialWon()); 
    }
    
    /**
     * Devuelve si un día es hermoso, o nuevo, o algo. 
     * @param day
     * @return 
     */
    private Boolean isABeatifulDay(int day){
        if(this.lastDay != day) {
            this.lastDay = day;
            //Candler vuelve a s estado inicial.
           // this.expert.getCandle().reset();
            return true;
        } else {
            return false;
        }
    }
}