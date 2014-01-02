
package app;

import com.mongodb.DBCursor;
import dao.Mongo;
import io.Exceptions.SettingNotFound;
import io.Inputs;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    public static String _break; //Malditas palabras reservadas
    public Excel file;
    private Integer from;
    private Integer to;
    private Integer threads;
    private String headers = "";
    private State state = new State();
    
    public App() {
        this.inputs = Inputs.getInstance();
        try {
            this.settings = new Settings(this.inputs.getInput("extern_file"));
            _break = ((Long)this.settings.getMetrics().get("break")).toString();
            this.iterador = new Iterador(this.settings.getExterns());
            this.mongo = new Mongo().setDB("data").setCollection(this.settings.getSymbol());
            this.from = Integer.parseInt(this.settings.getFrom());
            this.to = Integer.parseInt(this.settings.getTo());
            this.threads = Integer.parseInt(this.inputs.getInput("threads"));            
        } catch (SettingNotFound ex) {
            System.out.println(ex);
        }
    }
    public App setFile(String name){
        this.file = new Excel("results/"+name);
        return this;
    }
    public void run(){
        ExecutorService executor = Executors.newFixedThreadPool(this.threads) ;
        Map<String, Object> iteracion = new HashMap();
        System.out.println(this.threads + " Pruebas simultaneas...");
        try {
            if(Boolean.parseBoolean(this.inputs.getInput("production"))){
                this.state.setTotal(this.iterador.getSize());
                this.state.hello();
                executor = Executors.newFixedThreadPool(this.threads+1);
                executor.execute(this.state);
            } 
        } catch (SettingNotFound ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        this.file.setLimit(this.iterador.getSize());
        while (this.iterador.hasNext()) {
            Integer _from = Integer.parseInt(this.settings.getFrom());
            Integer _to = Integer.parseInt(this.settings.getTo());
            this.testData = this.mongo.getRange(_from, _to);
            iteracion = this.iterador.next();
            Thready thready = new Thready(this.settings, iteracion, this.from, Integer.parseInt(_break), this.to);
            thready.setData(testData).setFile(this.file);
            executor.execute(thready);
        }
        
        for(Map.Entry<String, Object> head: iteracion.entrySet()){
            this.headers += head.getKey() + ",";
        }
        
        this.file.setHeader("Pass, IR, "
                + "Short ->, Profit, Trades, IR-O, Sharp, Pain Index, Pain Ratio, Loss Avg, Loss stdDev, Drawdown %, "
                + "LONG  ->, Profit, Trades, IR-O, Sharp, Pain Index, Pain Ratio, Loss Avg, Loss stdDev, Drawdown %, "+this.headers + "\n");
        
        /*THE*/executor.shutdown();      
    }
        
    /**
     * 
     */
    public void theEnd() {
        //this.file.writeItOut();
    }
}
