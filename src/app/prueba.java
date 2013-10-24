package app;

import static app.App._break;
import app.trade.Gear;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import dao.Mongo;
import help.Crono;
import io.Exceptions.SettingNotFound;
import io.Inputs;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import util.Iterador;
import util.Settings;

/**
 *
 * @author omar
 */
public class prueba {
    private Inputs inputs;
    private Mongo mongo;
    private Settings settings = null;
    private DBCursor testData;
    private Iterador iterador;
    private Integer from;
    private Integer to;
    private String _break;
    
    public prueba(){
        this.inputs = Inputs.getInstance();
        try {
            this.settings = new Settings(this.inputs.getInput("extern_file"));
            this.mongo = new Mongo().setDB("data").setCollection(this.settings.getSymbol());
            this.testData = this.mongo.getRange(Integer.parseInt(this.settings.getFrom()), Integer.parseInt(this.settings.getTo()));
            this.iterador = new Iterador(this.settings.getExterns());
            this.from = Integer.parseInt(this.settings.getFrom());            
             this.to = Integer.parseInt(this.settings.getTo());
             _break = ((Long)this.settings.getMetrics().get("break")).toString();
        } catch (SettingNotFound ex) {
            Logger.getLogger(prueba.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void run() {
        Map<String, Object> iteracion;
        
        System.out.println("Threads prueba " + this.iterador.getSize());
        while (this.iterador.hasNext()  ) {
            iteracion = this.iterador.next();
            Thready thready = new Thready(this.settings, iteracion, this.from, Integer.parseInt(_break), this.to);
            thready.setData(testData);
            thready.start();
            Crono.init();
            System.out.println(Crono.end());
            this.testData = this.mongo.getRange(Integer.parseInt(this.settings.getFrom()), Integer.parseInt(this.settings.getTo()));
        }
 
    }
    
    public static void main(String[] args) {
        prueba p = new prueba();
        p.run();
    }
}
