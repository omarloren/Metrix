/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package app.indicator;

import static app.indicator.iController.indPool;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import dao.Mongo;
import help.Candle;
import help.Date;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import trade.indicator.base.BollingerBands;
import trade.indicator.base.Indicator;

/**
 * Fabrica de bollinger bands, aunque puede ser de cualquier indicador.
 * @author omarlopezorendain
 */
public class Factory {
   
    private Integer from;
    private HashMap<String, List> bollsMap = new HashMap<>();
    
    public Factory(Integer from){
        this.from = from;
        Indicator.setFrom(this.from);
    } 
    
    public List newBollinger(String s, int p, int n) {
        List r = null;
        
        String c = "batch/" + s + "_" + p + "_" + n +".dat";
        for (Map.Entry<String, List> entry : this.bollsMap.entrySet()) {
            if(entry.getKey().equals(c)) {
                System.out.println("Cargando bollinger desde la memoria " + c);
                return entry.getValue();
            }
        }
        if (fileExists(c)) {
            System.out.println("Cargando bolliger desde archivo " + c);
            return this.unSerialize(c);
        } else {
            System.out.println("Contruyendo historico de bollinger "+ c);
            return this.build(s, p, n);
        }
    }
      
    private boolean fileExists(String c) {
        File f = new File(c);
        if(f.exists()) {
            return true;
        }
        return false;
    }
    
    private List build(String s, int p, int n){
        Mongo data = new Mongo().setDB("data");
        String name = s + "_" + p + "_" + n;
        data.setCollection(s);
        DBCursor cursor = data.getRange(this.from, 20131001);
        BollingerBands b = new BollingerBands(s, p, n);
        Candle candle =  new Candle(p);   
        candle.setStrict(false);
        List<HashMap> arr = new ArrayList();
        while(cursor.hasNext()) {
            DBObject o = cursor.next();
            Date.setTime(String.valueOf(o.get("DTYYYYMMDD")), String.valueOf(o.get("TIME")));
            
            if(candle.isNew(Date.getMinutes())) {
                Double open = (Double)o.get("OPEN");
                b.refreshValues(open);
                HashMap<String, Object> r = new HashMap();
                r.put("date", Integer.parseInt(Date.getDate()));
                r.put("time", Integer.parseInt(Date.time()));
                r.put("down", b.getLowerBand());
                r.put("middle", b.getMiddleBand());
                r.put("up", b.getUpperBand());
                arr.add(r);
            }
        }
        serialize("batch/" + s + "_" + p + "_" + n +".dat", arr);
        this.bollsMap.put(name, arr);
        return arr;
    }
    
    private void serialize(String name, List arr){
        
        FileOutputStream fos = null;
        ObjectOutputStream out = null;
        try {
            fos = new FileOutputStream(name);
            out = new ObjectOutputStream(fos);
            out.writeObject(arr);
            out.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Factory.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Factory.class.getName()).log(Level.SEVERE, null, ex);
        }
       
    }
    
    public List unSerialize(String c){
       
        List data = null;
        FileInputStream fis = null;
        ObjectInputStream in = null;
        try {
            fis = new FileInputStream(c);
            in = new ObjectInputStream(fis);
            data = (ArrayList) in.readObject();
        } catch (IOException ex) {
            Logger.getLogger(Factory.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Factory.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.bollsMap.put(c, data);
        return data;
    }
}

