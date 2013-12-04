package util;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.json.simple.parser.ContainerFactory;
import org.json.simple.parser.JSONParser;

/**
 *
 * @author omar
 */
public class Settings {
    
    private String symbol;
    private String from;
    private String to;
    private Integer period;
    private Integer MAGICMA;
    private Integer initialWon;
    private Integer spread;
    private Double point;
    private Map<String, ArrayList> externs = new LinkedHashMap<>();
    private Map<String, Object> metrics = new HashMap<>();
    
    public Settings(String file){
        Iterator i = this.getJsonValues(this.fileToText(file));
        
        while (i.hasNext()) {
            Map.Entry entry = (Map.Entry) i.next();
            String key = (String) entry.getKey();
            
            switch (key) {
                case "symbol":
                    this.symbol = (String) entry.getValue();
                    if(this.symbol.equals("USDJPY")){
                        this.point = 0.001;
                    } else {
                        this.point = 0.0001;
                    }
                    break;
                case "period":
                    this.period = ((Long) entry.getValue()).intValue();
                    break;
                case "spread":
                    this.spread = ((Long) entry.getValue()).intValue();
                    break;
                case "date":
                    LinkedHashMap<String,Long> date = (LinkedHashMap) entry.getValue();
                    this.to = ((Long)date.get("to")).toString();
                    this.from = ((Long)date.get("from")).toString();
                    break;
                case "externs":
                    Map<String, LinkedHashMap<String,Object>> h = ((LinkedHashMap)entry.getValue());
                                       
                    for(String k : h.keySet()) {
                        this.externs.put(k, this.getVariable(h.get(k)));
                    }
                    break;
                case "metrics":
                    
                    LinkedHashMap<String, LinkedHashMap<String,Object>> l = (LinkedHashMap)entry.getValue();
                    for(String k : l.keySet()) {
                        this.metrics.put(k, l.get(k));
                    }
                    break;
                case "MAGICMA":
                    this.MAGICMA = ((Long) entry.getValue()).intValue();
                    break;
                case "initialWon":
                    this.initialWon = ((Long) entry.getValue()).intValue();
                    break;
                default:
                    System.err.println("COLAPSO TOTAL: Settings => "+key);
                    break;
            }
        }
    }
        
    /**
    * Extraemos el start, step, stop.
    * @param l
    * @return 
    */
    private ArrayList getVariable(LinkedHashMap l){
        ArrayList temp = new ArrayList();
        Object start;
        Object step;
        Object stop;
        
        if(l.get("start").getClass().getName().equals("java.lang.Double")) {
            start = (Double)l.get("start");
            step = (Double)l.get("step");
            stop = (Double)l.get("stop");
            if((Double)step > 0.0) {
                for (Double i = (Double)start; i <= (Double)stop; i += (Double)step) {
                    temp.add(i);
                }    
            } else {
                temp.add(start);
            }
        } else {
            start = (Long)l.get("start");
            step = (Long)l.get("step");
            stop = (Long)l.get("stop");
            if((Long)step > 0.0) {
                for (Long i = (Long)start; i <= (Long)stop; i += (Long)step) {
                    temp.add(i);
                }
            }else{
               temp.add(start);
            }
        }
        
        return temp;
    }
    /**
     * Convierte un archivo a texto plano.
     * @param file
     * @return 
     */
    private String fileToText(String file){
        String s = "";
        String temp;
        try {
            FileInputStream stream = new FileInputStream(file);
            DataInputStream in = new DataInputStream(stream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            while((temp = br.readLine()) != null){
                //Borramos tabulador, espacio y nueva linea.
                s += temp.replaceAll("\t", "").replaceAll("\n", "").replaceAll(" ", "");
            }
            in.close();
        } catch (Exception ex) {
            System.err.println(ex);
        } 
        return s;
    }
    
    /**
     * Iteramos sobre un sub-elemento JSON: {ejemplo: vars:{1:'hola'}} => para
     * obtener el valor de 1 = 'hola'.
     *
     * @param o Cualquier objeto JSONable(Un archivo o una String).
     * @return
     */
    private Iterator getJsonValues(String str){
        Iterator i = null;
        try {
            JSONParser parser = new JSONParser();
            ContainerFactory containerFactory = new ContainerFactory(){

                @Override
                public Map createObjectContainer() {
                    return new LinkedHashMap();
                }
                
                @Override
                public List creatArrayContainer() {
                    return new LinkedList();
                }
            };
            Object json = parser.parse(str, containerFactory);
            LinkedHashMap map = (LinkedHashMap)json;
            i = map.entrySet().iterator();
            
        } catch (Exception ex) {
            System.err.println(ex);
        }
        return i;
    }
    
    /*
     * GETTERS PAIQUES*
     */
    public String getSymbol(){
        return this.symbol;
    }
    
    public Integer getPeriod(){
        return this.period;
    }
    
    public String getTo(){
        return this.to;
    }
    
    public String getFrom(){
        return this.from;
    }
        
    public Map<String, ArrayList> getExterns(){
        return this.externs;
    }
    
    public Map<String, Object> getMetrics(){
        return this.metrics;
    }
    
    public Integer getMagic(){
        return this.MAGICMA;
    }
    
    public Integer getInitialWon(){
        return this.initialWon;
    }
    
    public Double getSpread() {
        return this.spread *this.point ;
    }
    
    public Double getPoint(){
        return this.point;
    }
}
