package app.metrics.base;

import java.util.ArrayList;

/**
 * Clase base para los métricos.
 * @author omar
 */
public abstract class Metric {

    //Historial de valores con el que se haran los calcúlos.
    private ArrayList<Double> values;
    //Apartir de donde trabajara.
    private String from;
    //Hasta donde trabajara.
    private String to;
    //Si este metrico soporta flushing.
    private Boolean flushing = false;
    private Boolean refresh = true;
    //Identificador de el metrico
    private String id = "";
    //Ultimo valor.
    Double lastValue = -1.0;
    
    
    public Metric(String id, String from, String to){
        this.id = id;
        this.from = from;
        this.to = to;
        this.values = new ArrayList();
    }
    
    public void setFrom(String from){
        this.from = from;
    }
    
    public void setTo(String to){
        this.to = to;
    }
    
    public String getTo() {
        return this.to;
    }
    
    public String getFrom() {
        return this.from;
    }
    
    public Boolean canFlush(){
        return this.flushing;
    }
    
    public Boolean canRefresh(){
        return this.refresh;
    }
    
    public Metric setFlush(Boolean b){
        this.flushing = b;
        return this;
    }
    
    public Metric setRefresh(Boolean b){
        this.refresh = b;
        return this;
    }
    public ArrayList<Double> getValues() {
        return this.values;
    }
    
    public String getId() {
        return this.id;
    }        
    
    public void setLastValue(Double lastValue) {
        this.lastValue = lastValue;
    }
    
   public Boolean isActive(String date) {
        Integer f = Integer.parseInt(this.getFrom());
        Integer t = Integer.parseInt(this.getTo());
        Integer d = Integer.parseInt(date);
        Boolean b = false;
        if(d >= f && d < t) {
            b = true;
        }
        return b;
    }
   
    public abstract void feed(Double val);
    
    public abstract Boolean isNew();
}
