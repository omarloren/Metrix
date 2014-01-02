package app.trade;

import app.metrics.MetricsController;
import app.trade.experts.EHandler;
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
    private Date date;
    public int id;
    EHandler eHandler;
    private Settings settings;    
    private String symbol;
    private Integer periodo;
    private Candle candle;
    private int lastMonth = -1;
    private int lastDay = 0;
    private Integer from;
    private Integer _break;
    private Integer to;
    private MetricsController metricsController;
    private Boolean canSDT = false;
    private Boolean lock = false;
    private int sundayCont = 0;
    public Double longDrowdown;
    public Boolean killMe = false;
    
    public Gear(Settings settings, Map<String, Object> it, Integer from, Integer _break, Integer to) {
        Inputs input = Inputs.getInstance();
        this.date = new Date();
        
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
        this.broker.setDate(this.date);
        this.broker.setSpread(this.settings.getSpread());
        
        this.eHandler = new EHandler(this.settings.getExpert());
        this.eHandler.expert().build(this.periodo).__construct(this.broker, this.from, this.symbol, this.settings.getPoint(), this.settings.getMagic());
        this.eHandler.expert().setExtern(new Extern(it)).setDate(this.date);
        this.eHandler.expert().Init();
        
        if(this.canSDT && this.date.getMonth() >= 11 || this.date.getMonth() < 3) {
             this.eHandler.expert().setHoraIni(this.sumHour(this.eHandler.expert().getHoraIni()));
             this.eHandler.expert().setHoraFin(this.sumHour(this.eHandler.expert().getHoraFin()));
        }
    }
    
    public Gear setMetrics(MetricsController m) {
        this.metricsController = m;
        this.metricsController.newPain("LONG",this.settings.getInitialWon(), this.settings.getFrom(), String.valueOf(this._break));
        this.metricsController.newPain("SHORT",this.settings.getInitialWon(), String.valueOf(this._break), this.settings.getTo());
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
            this.date.setTime(String.valueOf(t.get("DTYYYYMMDD")), String.valueOf(t.get("TIME")));
            if(this.lastMonth == -1){
                this.lastMonth = this.date.getMonth();
            }
            ArrayList<Double> arr = this.evaluate(t);
            //Primer precio en el array es la apertura de minuto.
            double open = arr.get(0);
            //Si esta activa la opcion para ajustes en horarios de verano.
            if (this.canSDT && this.date.dayOfWeek() != this.lastDay) {
                if (this.date.dayOfWeek() == 1) {
                    this.sundayCont++;
                }
                this.lastDay = this.date.dayOfWeek();
            }
            if (this.canSDT) {
                //Cambiamos el horario de la gráfica el primer domingo de noviembre
                if (this.date.getMonth() == 11 && this.sundayCont == 1) {
                    this.eHandler.expert().setHoraIni(this.sumHour(this.eHandler.expert().getHoraIni()));
                    this.eHandler.expert().setHoraFin(this.sumHour(this.eHandler.expert().getHoraFin()));
                    this.sundayCont = 2;        
                //lo regresamos el segundo domingo de marzo.
                } else if(this.date.getMonth() == 3 && this.sundayCont == 2) {
                    this.eHandler.expert().setHoraFin(this.eHandler.expert().getHoraFinOrigen());
                    this.eHandler.expert().setHoraIni(this.eHandler.expert().getHoraIniOrigen());
                    this.sundayCont = 3;
                }
            }
            
            if(this.date.getMonth() != this.lastMonth) {
                Integer d = Integer.parseInt(this.date.getDate());
                /**
                 * Maldito windows me orillo a que si eto llega a su último mes
                 * se tiene que inmolar.
                 */
                if(this.lock && d <= this._break) {
                    this.killMe = true;
                    return;
                }
                
                //Si llegamos al break;
                if(!this.lock && d >= this._break && d < this.to) {
                    this.metricsController.refresh(this.date.getDate(),this.broker.getBalance());
                    this.longDrowdown = this.broker.getDrawDown();
                    this.broker.reset();
                    this.lock = true;
                    this.lastMonth = -1;
                } else {
                    this.metricsController.refresh(this.date.getDate(),this.broker.getBalance());
                }
                this.lastMonth = this.date.getMonth();
                this.sundayCont = 0;
            }
            this.eHandler.expert().setOpenMin(open);
            //si es una nueva vela.
            if (this.candle.isNew(this.date.getMinutes())) {
                this.broker.setOpenMin(open);
            }
            
            /**
             * Usamos los precios faltante para revisar que que las operaciones
             * lleguen a sus limites etc.
             */
            for (int i = 0; i < arr.size(); i++) {
                double bid =arr.get(i);
                double ask = Arithmetic.sumar(bid, this.settings.getSpread());
                this.broker.ticker(bid);
                this.eHandler.expert().setBid(bid);  
                this.eHandler.expert().setAsk(ask);
                this.eHandler.expert().onTick();
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
    
    public Date getDate(){
        return this.date;
    }
}