package app;

import app.metrics.MetricsController;
import app.metrics.base.Pain;
import app.metrics.base.StdDev;
import app.trade.Broker;
import app.trade.Gear;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import help.Crono;
import java.util.Map;
import trade.indicator.base.util.StandardDeviation;
import util.Iterador;
import util.Settings;

/**
 *
 * @author omar
 */
public class Thready implements Runnable{
    
    private Gear gear;
    private DBCursor data;
    private static int cont = 1;  
    private MetricsController metricsController;      
    Map<String, Object> iteracion;
    
    public Thready(Settings settings, Map<String, Object> it, Integer from, Integer _break, Integer to){ 
        this.metricsController = new MetricsController();
        this.gear = new Gear(settings, it, from, _break, to);
        this.gear.setId(cont).setMetrics(this.metricsController);            
        this.iteracion = it;
        cont++;
    }
    
    public void setData(DBCursor data){
        this.data = data;
    }
    
    @Override
    public void run(){
        System.err.println("#"+this.gear.id + " iniciando :)");
        Crono c = new Crono();
        c.init();
        while (this.data.hasNext()) {
            DBObject o = this.data.next();
            this.gear.Tick(o);
       }
       Broker broker = this.gear.getBroker();
       double ir = this.metricsController.getIR();
       double shortStd = this.getStdDev("SHORT");
       double longStd = this.getStdDev("LONG");
       double shortMean = this.getMean("SHORT");
       double longMean = this.getMean("LONG");
       Pain painS = this.metricsController.getPain("SHORT");
       Pain painL = this.metricsController.getPain("LONG");
       String str = cont + ", " +ir + ", , "+ broker.getLongProfit() + ", "+broker.getLongTrades() + ", "+ broker.getLongRelative() + ", " + painS +", "+shortMean+", "+ shortStd + " , ,";
       str +=  broker.getProfit() + ", "+broker.getTotalTrades() + ", "+ broker.getDrowDown() + ", "+ painL+", " + longMean+", "+longStd + ","+ Iterador.toString(this.iteracion);
       System.err.println("#"+this.gear.id + " has finished => "+ c.end());
    }
    
    private double getStdDev(String id){
        StdDev stdDev = (StdDev)this.metricsController.getStd(id);
        StandardDeviation res = new StandardDeviation();

        for (int i = 0; i < stdDev.getValues().size(); i++) {
            if (stdDev.getValues().get(i) < 0) {
                res.addValue(stdDev.getValues().get(i));
            }
        }
        return res.calculateStdDev();
    }
    
    private double getMean(String id){
        StdDev stdDev = (StdDev)this.metricsController.getStd(id);
        double sum = 0;
        
        int c = 0;
        for (int i = 0; i < stdDev.getValues().size(); i++) {
            if (stdDev.getValues().get(i) < 0) {
                sum += stdDev.getValues().get(i);
                c++;
            }
        }
        return sum / c;
    }
}
