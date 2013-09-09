package app.trade;

import app.metrics.MetricsController;
import app.metrics.base.Pain;
import app.trade.experts.BSFF1_8_SV;
import com.mongodb.DBObject;
import java.util.ArrayList;
import java.util.Map;
import trade.Arithmetic;
import util.Settings;
import util.Date;
import util.Candle;

/**
 *
 * @author omar
 */
public class Gear {
    
    private Broker broker;
    private BSFF1_8_SV expert;
    //private Prueba expert;
    private Settings settings;    
    private String symbol;
    private Integer periodo;
    private Candle candle;
    private int lastMonth = 1;
    
    public Gear(Settings settings) {
        this.settings = settings;
        this.expert = new BSFF1_8_SV();
        //this.expert = new Prueba();
        this.symbol = this.settings.getSymbol();
        this.periodo = this.settings.getPeriod();
        this.candle = new Candle(this.periodo);
        this.broker = new Broker(this.settings.getInitialWon());        
        this.expert.build(this.periodo).__construct(this.broker,(Integer)this.settings.getFrom(), this.symbol,this.settings.getPoint(), this.settings.getMagic());   
    }
    
    public void rollOn(Map<String, Object> it) {
        Extern extern = new Extern(it);
        this.expert.setExtern(extern);
        this.expert.Init();
        System.out.println("Iniciando interaccion: " + it);
    }
    
    public void tick(DBObject t) {
        
        ArrayList<Double> arr = this.evaluate(t);
        Date.setTime(String.valueOf(t.get("DTYYYYMMDD")), String.valueOf(t.get("TIME")));
        Double open = arr.get(0);
        if(Date.getMonth() != this.lastMonth) {
            this.lastMonth = Date.getMonth(); 
            MetricsController.refresh(this.broker.getBalance());
        }
        this.expert.setOpenMin(open);
        if (this.candle.isNew(Date.getMinutes())){
            this.broker.setOpenMin(open);
        }
        for (int i = 0; i < arr.size(); i++) {
            Double bid =arr.get(i);
            Double ask = Arithmetic.sumar(bid, this.settings.getSpread());
            this.broker.bider(bid);
            this.broker.asker(ask);
            this.expert.setBid(bid);
            this.expert.setAsk(ask);
            this.expert.onTick();
        }
    }
    
    /**
     * Evalua la vela de minuto y filtra los precios si es que son repetidos.
     * @param e
     * @return Precios filtrados.
     */
    private ArrayList<Double> evaluate(DBObject e) {
        ArrayList<Double> r = new ArrayList();
        r.add((Double) e.get("OPEN"));
        r.add((Double) e.get("LOW"));
        r.add((Double) e.get("HIGH"));
        r.add((Double) e.get("CLOSE"));
        Double base = r.get(0);
        for (int i = 1; i < r.size(); i++) {   
            if(Double.compare(base ,r.get(i)) == 0){
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
         MetricsController.refresh(this.broker.getBalance());
    }
}
