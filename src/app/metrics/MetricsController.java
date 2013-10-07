package app.metrics;

import app.metrics.base.Monthly;
import app.metrics.base.Metric;
import app.metrics.base.Pain;
import java.util.ArrayList;
import trade.Arithmetic;

/**
 *  TODO: HACER TODO ESTO NO-ESTATICO, PARA QUE PODAMOS CONTRUIR METRICS A PARTIR
 *        DEL ARCHIVO DE CONFIGURACION Y ASI BIEN PADRE :)
 * @author omar
 */
public class MetricsController {
    private static ArrayList<Metric> metricsPool = new ArrayList();
    private static Double lastValue;
    public static Pain newPain(String id,Integer initialAccount, String from, String to) {
        Pain p = new Pain(id, initialAccount, from, to);
        metricsPool.add(p);
        lastValue = new Double(initialAccount);
        return p;
    }
    
    public static Monthly newIR(String id, String from, String to) {
        Monthly ir = new Monthly( id, from, to);
        metricsPool.add(ir);
        return ir;
    }
    
    public static void refresh(String date, Double val) {
        
        for (int i = 0; i < metricsPool.size(); i++) {
            Metric m = metricsPool.get(i);
            if(m.isActive(date) && m.canRefresh()) {
                if(m.isNew()) {
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
    
    public static void rebuildMetrics(){
        ArrayList<Metric> temp = new ArrayList();
        for (int i = 0; i < metricsPool.size(); i++) {
            temp.add(metricsPool.get(i));
        }
        metricsPool = new ArrayList();
        for (int i = 0; i < temp.size(); i++) {
            Metric m = temp.get(i);
            
            if(m.getClass() == Pain.class) {
                Pain p = new Pain(m.getId(),((Pain)m).getInitialAccount(), m.getFrom(), m.getTo());
                metricsPool.add(p);
            } else if(m.getClass() == Monthly.class) {
                Monthly n = new Monthly(m.getId(), m.getFrom(), m.getTo());
                metricsPool.add(n);
            }
        }
    }
    
    public static ArrayList<Metric> getMetrics() {
        return metricsPool;
    }
    
    public static Double getIR() {
        Double _long = 0.0 ;
        Double _short = 0.0;
        for (int i = 0; i < metricsPool.size(); i++) {
             Metric m = metricsPool.get(i);
            if(m.getClass() == Monthly.class) {
                Monthly month = ((Monthly)m);
                 switch (month.getId()) {
                     case "LONG":
                         _long = month.getMonthlyAvg();
                         break;
                     case "SHORT":
                         _short = month.getMonthlyAvg();
                         break;
                 }
            } 
        }
        return Arithmetic.redondear( (_short /_long), 4);
    }
    
    public static Pain getPain(String id){
        Pain p = null;
        for (int i = 0; i < metricsPool.size(); i++) {
            if(metricsPool.get(i).getClass() == Pain.class) {
                
                if(metricsPool.get(i).getId().equals(id)) {
                    p = (Pain)metricsPool.get(i);
                }
            }
        }
        return p;
    }
}
