package app;

import app.metrics.MetricsController;
import app.metrics.base.Pain;
import app.metrics.base.StdDev;
import app.trade.Broker;
import app.trade.Gear;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import help.Crono;
import java.util.ArrayList;
import java.util.Map;
import trade.Arithmetic;
import trade.indicator.base.util.StandardDeviation;
import util.Excel;
import util.Iterador;
import util.Settings;

/**
 *
 * @author omar
 */
public class Thready implements Runnable{
    
    private Gear gear;
    private DBCursor data;
    private MetricsController metricsController;      
    Map<String, Object> iteracion;
    private Excel file;
    private Integer initialDeposit;

    public Thready(Settings settings, Map<String, Object> it, Integer from, Integer _break, Integer to){ 
        this.initialDeposit = settings.getInitialWon();
        this.metricsController = new MetricsController();
        this.gear = new Gear(settings, it, from, _break, to);
        this.gear.setMetrics(this.metricsController);            
        this.iteracion = it;
    }
    
    public Thready setData(DBCursor data){
        this.data = data;
        return this;
    }
    
    public Thready setFile(Excel file){
        this.file = file;
        return this;
    }
    
    @Override
    public void run() {
        Crono c = new Crono();
        c.init();
        /**
        * Resulta que pinchi windows reinicia el cursor, no he encontrado por qué
        * asi que tengo que hacer que el gear se me mate .
        */
        while (this.data.hasNext() && !this.gear.killMe) {
            DBObject o = this.data.next();
            this.gear.Tick(o);
        }
        Broker broker = this.gear.getBroker();
        double ir = this.metricsController.getIR();
        double shortStd = Arithmetic.redondear(this.getStdDev("SHORT"), 2);
        double longStd = Arithmetic.redondear(this.getStdDev("LONG"), 2);
        double shortMean = Arithmetic.redondear(this.getMean("SHORT"), 2);
        double longMean = Arithmetic.redondear(this.getMean("LONG"), 2);
        Pain painS = this.metricsController.getPain("SHORT");
        Pain painL = this.metricsController.getPain("LONG");
        
        String str = ir + ", , " + broker.getProfit() + ", " + broker.getTotalTrades() + ", " + painS.getIndex() + ", " + painS.getRatio() + ", " + shortMean + ", " + shortStd + ", " + broker.getDrawDown()+ ", ,";
        str += broker.getLongProfit() + ", " + broker.getLongTrades() + ", " + painL.getIndex() + ", " + painL.getRatio() + ", "+ longMean + ", " + longStd + "," + broker.getLongDrawDown() + ", " + Iterador.toString(this.iteracion);
        this.file.addData(str);
        State.addTime(c.end());
        State.step();
    }
    
    private double getStdDev(String id) {
        StdDev stdDev = (StdDev)this.metricsController.getStd(id);
        ArrayList<Double> values = new ArrayList();
        double last = this.initialDeposit;
        //quick-fix
        if (id.equals("SHORT")) {
            stdDev.getValues().remove(0);
        }
        
        for (int i = 0; i < stdDev.getValues().size(); i++) {
            if (stdDev.getValues().get(i) < last) {
                double val = Arithmetic.redondear(stdDev.getValues().get(i),1);
                values.add(last - val);
            }
            last = stdDev.getValues().get(i);
        }
        
        return new StandardDeviation(values.size(), values).calculateStdDev();
    }
    
    private double getMean(String id){
        StdDev stdDev = (StdDev)this.metricsController.getStd(id);
        double sum = 0;
        int c = 0;
        double last = this.initialDeposit;
        //quick-fix
        if (id.equals("SHORT")) {
            stdDev.getValues().remove(0);
        }
        for (int i = 0; i < stdDev.getValues().size(); i++) {
            if (stdDev.getValues().get(i) < last) {
                double val = Arithmetic.redondear(stdDev.getValues().get(i),1);
                sum += (last - val);
                c++;
            }
            last = stdDev.getValues().get(i);
        }
        return Arithmetic.redondear(sum / c);
    }
}
