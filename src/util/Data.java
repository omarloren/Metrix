
package util;

import dao.Mongo;
import io.JSON;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

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
        String[] values = line.split("\\,");
        HashMap<String, Object> hs = new HashMap();
        for (int i = 0; i < this.fileHeader.length; i++) {
            hs.put(this.fileHeader[i], this.stringToObj(values[i]));
        }
        String json = JSON.HashToJson(hs);
        this.mongo.insert(this.symbol, json);
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
}
