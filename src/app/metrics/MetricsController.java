package app.metrics;

import app.metrics.base.IR;
import app.metrics.base.Metric;
import app.metrics.base.Pain;
import java.util.ArrayList;

/**
 *
 * @author omar
 */
public class MetricsController {
    private static ArrayList<Metric> metricsPool = new ArrayList();
    
    public static Pain newPain(Integer initialAccount, Integer from, Integer to) {
        Pain p = new Pain(initialAccount, from, to);
        metricsPool.add(p);
        return p;
    }
    
    public static IR newIR(Integer from, Integer to){
        IR ir = new IR(from, to);
        metricsPool.add(ir);
        return ir;
    }
    
    public static void refresh(Double val) {
        for (int i = 0; i < metricsPool.size(); i++) {
            metricsPool.get(i).feed(val);
            metricsPool.get(i);
        }
    }
}
