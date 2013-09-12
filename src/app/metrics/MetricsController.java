package app.metrics;

import app.App;
import app.metrics.base.Monthly;
import app.metrics.base.Metric;
import app.metrics.base.Pain;
import java.util.ArrayList;

/**
 *
 * @author omar
 */
public class MetricsController {
    private static ArrayList<Metric> metricsPool = new ArrayList();
    private static Double lastValue;
    public static Pain newPain(Integer initialAccount, String from, String to) {
        Pain p = new Pain(initialAccount, from, to);
        metricsPool.add(p);
        lastValue = new Double(initialAccount);
        return p;
    }
    
    public static Monthly newIR(String from, String to) {
        Monthly ir = new Monthly(from, to);
        metricsPool.add(ir);
        return ir;
    }
    
    public static void refresh(String date, Double val) {
        
        for (int i = 0; i < metricsPool.size(); i++) {
            Metric m = metricsPool.get(i);
            if(m.isActive(date)){
                if(m.isNew()){
                    m.setLastValue(lastValue);
                }
                m.feed(val);
                
            }
        }
        lastValue = val;
    } 
    
    public static void flushMetrics(Double val) {
        for (int i = 0; i < metricsPool.size(); i++) {
            Metric m = metricsPool.get(i);
            if(m.canFlush()){
                m.feed(val);
            }
        }
    }
}
