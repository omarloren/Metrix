
package app;

import app.trade.Gear;
import com.mongodb.DBCursor;
import dao.Mongo;
import io.Exceptions.SettingNotFound;
import io.Inputs;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import util.Excel;
import util.Iterador;
import util.Settings;

/**
 *
 * @author omar
 */
public class App {
    private Inputs inputs;
    private Mongo mongo;
    private Settings settings = null;
    private Iterador iterador;
    private DBCursor testData;
    private Gear gear;
    public static String _break; //Malditas palabras reservadas
    public Excel file;
    private Integer from;
    private Integer to;
    private Integer threads;
    private String headers;
    
    public App() {
        this.inputs = Inputs.getInstance();
        try {
            this.settings = new Settings(this.inputs.getInput("extern_file"));
            _break = ((Long)this.settings.getMetrics().get("break")).toString();
            this.iterador = new Iterador(this.settings.getExterns());
            this.mongo = new Mongo().setDB("data").setCollection(this.settings.getSymbol());
            this.file = new Excel(this.settings.getSymbol());
            this.from = Integer.parseInt(this.settings.getFrom());
            this.to = Integer.parseInt(this.settings.getTo());
            this.testData = this.mongo.getRange(Integer.parseInt(this.settings.getFrom()), Integer.parseInt(this.settings.getTo()));
            this.threads = Integer.parseInt(this.inputs.getInput("threads"));            
        } catch (SettingNotFound ex) {
            System.out.println(ex);
        }
    }
    
    public void run(){
        Map<String, Object> iteracion = new HashMap();
        System.out.println(this.threads + " Threads " + this.iterador.getSize() + " iteraciones");
        ExecutorService executor = Executors.newFixedThreadPool(this.threads);
        this.file.setLimit(this.iterador.getSize());
        while (this.iterador.hasNext()) {
            iteracion = this.iterador.next();
            Thready thready = new Thready(this.settings, iteracion, this.from, Integer.parseInt(_break), this.to);
            thready.setData(testData).setFile(this.file);
            executor.execute(thready);
            this.testData = this.mongo.getRange(Integer.parseInt(this.settings.getFrom()), Integer.parseInt(this.settings.getTo()));
        }
        
        for(Map.Entry<String, Object> head: iteracion.entrySet()){
            this.headers += head.getKey() + ",";
        }
        
        this.file.setHeader("Pass, IR, Short ->, Profit, Trades, Relative, Pain "
                + "Index, Pain Ratio,Loss Avg, Loss stdDev, LONG ->, Profit, Trades, "
                + "Relative, Pain Index, Pain Ratio, Loss Avg, Loss stdDev, "+headers + "\n");
        
        /*THE*/executor.shutdown();
        System.out.println("Fin :)");
    }
        
    /**
     * 
     */
    public void theEnd() {
        //this.file.writeItOut();
    }
}
