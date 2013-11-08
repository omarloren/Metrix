package app;

import com.mongodb.DBCursor;
import dao.Mongo;
import io.Exceptions.SettingNotFound;
import io.Inputs;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import util.Iterador;
import util.Settings;

/**
 *
 * @author omar
 */
public class Prueba {
    private Inputs inputs;
    private Mongo mongo;
    private Settings settings = null;
    private DBCursor testData;
    private Iterador iterador;
    private Integer from;
    private Integer to;
    private Integer threads;
    private String _break;
    
    public Prueba(){
        this.inputs = Inputs.getInstance();
        try {
            this.settings = new Settings(this.inputs.getInput("extern_file"));
            this.mongo = new Mongo().setDB("data").setCollection(this.settings.getSymbol());
            this.testData = this.mongo.getRange(Integer.parseInt(this.settings.getFrom()), Integer.parseInt(this.settings.getTo()));
            this.iterador = new Iterador(this.settings.getExterns());
            this.from = Integer.parseInt(this.settings.getFrom());            
            this.to = Integer.parseInt(this.settings.getTo());
            _break = ((Long)this.settings.getMetrics().get("break")).toString();
            this.threads = Integer.parseInt(this.inputs.getInput("threads"));
        } catch (SettingNotFound ex) {
            Logger.getLogger(Prueba.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void run() {
        Map<String, Object> iteracion;
        System.out.println(this.threads + " Threads " + this.iterador.getSize() + " iteraciones");
        ExecutorService executor = Executors.newFixedThreadPool(this.threads);
        
        while (this.iterador.hasNext()) {
            iteracion = this.iterador.next();
            Thready thready = new Thready(this.settings, iteracion, this.from, Integer.parseInt(_break), this.to);
            thready.setData(testData);
            executor.execute(thready);
            this.testData = this.mongo.getRange(Integer.parseInt(this.settings.getFrom()), Integer.parseInt(this.settings.getTo()));
        }
        executor.shutdown();
        System.out.println("Fin :)");
    }
    
    public static void main(String[] args) {
        Prueba p = new Prueba();
        p.run();
    }
}
