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
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.ContainerFactory;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

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
    private Map<String, ArrayList> externs = new HashMap<>();
    
    public Settings(String file){
        try {         
            JSONParser parser = new JSONParser();
            Iterator i = this.getJsonValues(parser.parse(this.fileToText(file)));
            
            while (i.hasNext()) {
                Map.Entry entry = (Map.Entry) i.next();
                String key = (String) entry.getKey();
                
                switch (key) {
                    case "symbol":
                        this.symbol = (String) entry.getValue();
                        break;
                    case "period":
                        this.period = ((Long) entry.getValue()).intValue();
                        break;
                    case "date":
                        LinkedHashMap<String,String> date =(LinkedHashMap)entry.getValue();
                        this.to = (String)date.get("to");
                        this.from = (String)date.get("from");
                        break;
                    case "externs":
                        System.out.println(entry.getValue());
                        LinkedHashMap<String, LinkedHashMap<String,Long>> h = (LinkedHashMap)entry.getValue();
                        for(String k : h.keySet()) {
                            this.externs.put(k, this.getVariable(h.get(k)));
                        }
                        break;
                    case "MAGICMA":
                        this.MAGICMA = ((Long) entry.getValue()).intValue();
                        break;
                    case "initialWon":
                        this.initialWon = ((Long) entry.getValue()).intValue();
                        break;
                    default:
                        System.err.println("COLAPSO TOTAL: Settings");
                        break;
                }
            }
        } catch (ParseException ex) {
            Logger.getLogger(Settings.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
     /**
     * Extraemos el start, step, stop.
     * @param l
     * @return 
     */
    private ArrayList getVariable(LinkedHashMap<String,Long> l){
        ArrayList temp = new ArrayList();
        Long start = l.get("start");
        Long step = l.get("step");
        Long stop = l.get("stop");
        for (long i = start; i <= stop; i+=step) {
            temp.add(i);
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
    private Iterator getJsonValues(Object o){
        Iterator i = null;
        try {
            JSONParser parser = new JSONParser();
            JSONObject str = (JSONObject) o;
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

            Map json = (Map)parser.parse(str.toString(), containerFactory);
            i = json.entrySet().iterator();
            
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
    
    public Integer getTo(){
        Integer i = (new Integer(this.to.replaceAll("-", "")));
        return i;
    }
    
    public Integer getFrom(){
        Integer i = (new Integer(this.from.replaceAll("-", "")));
        return i;
    }
        
    public Map<String, ArrayList> getExterns(){
        return this.externs;
    }
    
    public Integer getMagic(){
        return this.MAGICMA;
    }
    
    public Integer getInitialWon(){
        return this.initialWon;
    }
}
