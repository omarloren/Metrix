
package util;

import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import dao.Mongo;
import io.JSON;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import trade.Arithmetic;

/**
 *
 * @author omar
 */
public class Data {
    private String symbol;
    private String file;
    private String[] fileHeader;
    private Mongo mongo ;
    public Data(String symbol){
        this.symbol = symbol;
        this.file = "data/"+this.symbol + ".txt";
        this.mongo = new Mongo().setDB("data").setCollection(this.symbol);
    }
    public ArrayList<HashMap<String, Object>> getData(Integer from, Integer to) {
        ArrayList<HashMap<String, Object>> r = new ArrayList();
        DBCursor c = this.mongo.getRange(from, to);
        while(c.hasNext()){
            HashMap<String, Object> temp = new HashMap();
            DBObject o = c.next();
            temp.put("DTYYYYMMDD", o.get("DTYYYYMMDD"));
            temp.put("TIME", o.get("TIME"));
            temp.put("OPEN", o.get("OPEN"));
            temp.put("HIGH", o.get("HIGH"));
            temp.put("LOW", o.get("LOW"));
            temp.put("CLOSE", o.get("CLOSE"));
            r.add(temp);
        }
        return r;
    }
    /**
     * Construye el archivo de historicos, en resumen convierte un .txt a JSON
     * para ser salvado en mongo.
     */
    public Data buildFile(){
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(this.file));
            //No sé expresiones regulares :(
            this.fileHeader = br.readLine().replaceAll("\\<","").replaceAll("\\>", "").split("\\,");
            String line = br.readLine();
            while (line != null) {
                line = br.readLine();
                this.saveLine(line);
            }           
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Data.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Data.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                br.close();
            } catch (IOException ex) {
                Logger.getLogger(Data.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return this;
    }
    /**
     * Guarda un String en Mongo
     * @param line 
     */
    private void saveLine(String line) {
        //System.out.println(line);
        String[] values = line.split("\\,");
        HashMap<String, Object> hs = new HashMap();
        for (int i = 0; i < this.fileHeader.length; i++) {
            hs.put(this.fileHeader[i], this.stringToObj(values[i]));
        }
        String json = JSON.HashToJson(evaluate(hs));
        
        //System.out.println(json);
        this.mongo.insert(this.symbol, json);
    }
    /**
     * Evalua la vela de minuto y filtra los precios en el orden que deberán ser
     * procesados.
     * @param e
     * @return Precios filtrados.
     */
    private HashMap<String, Object> evaluate (HashMap<String, Object> e) {
        HashMap<String, Object> r = new HashMap();
        r.put("DTYYYYMMDD", e.get("DTYYYYMMDD"));
        r.put("TIME", e.get("TIME"));
        r.put("TICKER", e.get("TICKER"));
        r.put("VOL", e.get("VOL"));
        
        Double o = (Double)e.get("OPEN");
        Double h = (Double)e.get("HIGH");
        Double l = (Double)e.get("LOW");
        Double c = (Double)e.get("CLOSE");
        Double abs = Arithmetic.redondear(Math.abs(o-c)) * 10000;
        
        /**
         * Por alguna razón MT da como segundo tick el LOW si es que el HIGH y el
         * CLOSE son iguales, sino el HIGH es primero. Además Genera ticks falsos 
         * si es que una vela tiene o == h && l == c.
         */
        if (Arithmetic.equals(h, c) && !Arithmetic.equals(h, l) && !Arithmetic.equals(o, l) &&
                !Arithmetic.equals(o, h)) {
            r.put("OPEN",o);
            r.put("LOW",l);
            r.put("HIGH", h);
            
        } else if(Arithmetic.equals(o, h) && Arithmetic.equals(l, c) && (abs > 0)) {
            double d = Arithmetic.redondearUp(((o+l)/2), 4);
            r.put("OPEN",o);
            r.put("LOW",d);
            r.put("HIGH" ,Arithmetic.redondearUp(((d+l)/2), 4));
        } else if(Arithmetic.equals(o, l) && Arithmetic.equals(h, c) && (abs > 0)) {
            if(!(abs >= 2)){
                r.put("OPEN",o);
                r.put("HIGH",Arithmetic.redondear(o+ 0.0001)); 
                r.put("LOW",c);
            } else if(abs == 3){
               r.put("OPEN",o);
               r.put("HIGH", Arithmetic.redondear(o+ 0.0001)); 
               r.put("LOW",c); 
            } else {
                Double rel = abs / 3;
                r.put("OPEN",o);
                r.put("HIGH", Arithmetic.redondear(o+(rel*0.0001)));
                r.put("LOW",c);                
            }
        } else {
            r.put("OPEN",o);
            r.put("HIGH",h);
            r.put("LOW",l);
            
        }
        r.put("CLOSE",c);
        
        return e;
    }
    /**
     * Convierte un string a su objecto correspondiente (Soporta Integers, 
     * Doubles y Strings).
     * @param str
     * @return 
     */
    private Object stringToObj(String str) {
        Object r = null;
        //Intenta convetir cadena a Integer
        try {
            r = Integer.parseInt(str);
        } catch (NumberFormatException e) {
            //Si no pudo intenta convertirlo a Double
            try{
                r = Double.parseDouble(str);
            } catch(NumberFormatException e1) {
               //Sino pues ya solo retorna el string tal cual
               r = str;
            }
        }
        return r;
    }
    
    /*public static void main(String[] args) {
        Data data = new Data("EURUSD");
        data.buildFile();
    }*/
}
