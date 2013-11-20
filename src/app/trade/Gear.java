package app.trade;

import app.metrics.MetricsController;
import app.trade.experts.BSFF1_8_SV;
import com.mongodb.DBObject;
import help.Candle;
import help.Date;
import io.Exceptions.SettingNotFound;
import io.Inputs;
import java.util.ArrayList;
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
    private BSFF1_8_SV expert = new BSFF1_8_SV();
    private Settings settings;    
    private String symbol;
    private Integer periodo;
    private Candle candle;
    private int lastMonth = 1;
    private int lastDay = 0;
    private Integer from;
    private Integer _break;
    private Integer to;
    private MetricsController metricsController;
    private Boolean canSDT = false;
    private Boolean lock = false;
    private int sundayCont = 0;
    public Double longDrowdown;
    public Gear(Settings settings, Map<String, Object> it, Integer from, Integer _break, Integer to) {
        Inputs input = Inputs.getInstance();
        try {
            this.canSDT = Boolean.parseBoolean(input.getInput("SDT"));
        } catch (SettingNotFound ex) {
            Logger.getLogger(Gear.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.settings = settings;
        this.symbol = this.settings.getSymbol();
        this.periodo = this.settings.getPeriod();
        this.candle = new Candle(this.periodo).setStrict(false);
        this.from = from;
        this._break = _break;
        this.to = to;
        this.broker = new Broker(this.settings.getInitialWon());        
        this.broker.setSpread(this.settings.getSpread());
        this.expert.build(this.periodo).__construct(this.broker, this.from, this.symbol,this.settings.getPoint(), this.settings.getMagic());
        this.expert.setExtern(new Extern(it));
        this.expert.Init();
        if(this.canSDT && Date.getMonth() >= 11 || Date.getMonth() < 3){
             this.expert.horaIni = this.sumHour(this.expert.horaIni);
             this.expert.horaFin = this.sumHour(this.expert.horaFin);
        }
    }
    
    public Gear setMetrics(MetricsController m){
        this.metricsController = m;
        this.metricsController.newPain("LONG",this.settings.getInitialWon(), this.settings.getFrom(), String.valueOf(this._break));
        this.metricsController.newPain("SHORT",this.settings.getInitialWon(), String.valueOf(this._break), this.settings.getTo());
        this.metricsController.newIR("LONG", this.settings.getFrom(), String.valueOf(this._break));
        this.metricsController.newIR( "SHORT", String.valueOf(this._break), this.settings.getTo());
        this.metricsController.newStdDev("LONG", this.settings.getFrom(), String.valueOf(this._break));
        this.metricsController.newStdDev("SHORT",  String.valueOf(this._break), this.settings.getTo());
        return this;
    }
    
    public Gear setId(int id) {
        this.id = id;
        return this;
    }
    
    public void Tick(DBObject t) {
        try {
            Date.setTime(String.valueOf(t.get("DTYYYYMMDD")), String.valueOf(t.get("TIME")));
            ArrayList<Double> arr = this.evaluate(t);
            double open = arr.get(0);
            //Manejor de horarios de verano
            if(this.canSDT && Date.dayOfWeek() != this.lastDay){
                if(Date.dayOfWeek() == 1){
                    this.sundayCont++;
                }
                this.lastDay = Date.dayOfWeek();
            }
            if(this.canSDT){
                /**
                 * si es el primer domingo de noviembre sumamos una hora a los 
                 * horarios del expert.
                 */
                if(Date.getMonth() == 11 && this.sundayCont == 1){
                    this.expert.horaIni = this.sumHour(this.expert.horaIni);
                    this.expert.horaFin = this.sumHour(this.expert.horaFin);
                    this.sundayCont = 2;          
                 //Si es el segunto domingo de marzo lo regresamos a como estaba.
                }else if(Date.getMonth() == 3 && this.sundayCont == 2){
                    
                    this.expert.horaFin = this.expert.extern.getDouble("horafinal");
                    this.expert.horaIni = this.expert.extern.getDouble("horainicial");
                    this.sundayCont = 3;
                }
            }
            //Cada mes.
            if(Date.getMonth() != this.lastMonth) {
                this.lastMonth = Date.getMonth();
                this.metricsController.refresh(Date.getDate(),this.broker.getBalance());
                Integer d = Integer.parseInt(Date.getDate());
                this.sundayCont = 0;
                
                if(!lock && d >= this._break && d < this.to) {
                    this.longDrowdown = this.broker.getDrowDown();
                    this.broker.reset();
                    this.lock = true;
                }            
            }
            this.expert.setOpenMin(open);
            if (this.candle.isNew(Date.getMinutes())) {
                this.broker.setOpenMin(open);
            }
            for (int i = 0; i < arr.size(); i++) {
                double bid =arr.get(i);
                double ask = Arithmetic.sumar(bid, this.settings.getSpread());
                this.broker.ticker(bid);
                this.expert.setBid(bid);  
                this.expert.setAsk(ask);
                if(this.expert.isTradeTime() || !this.broker.getOrders().isEmpty()){
                    this.expert.onTick();
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(Gear.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public Double getlongDrowdown(){
        return this.longDrowdown;
    }
    
    /**
     * Suma una hora a la hora actual reseteando a cero si pasa de 24.
     * @param hour
     * @return 
     */
    private Double sumHour(Double hour){
        if(hour >= 24) {
            hour = hour - 24;
        }
        if(hour + 1 >= 24) {
            return (24-(hour+1));
        } else {
            return (hour+1);
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
    
    public MetricsController getController(){
        return this.metricsController;
    }
}