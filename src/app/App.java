
package app;

import app.metrics.MetricsController;
import app.metrics.base.Pain;
import app.trade.Broker;
import app.trade.Gear;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import dao.Mongo;
import io.Exceptions.SettingNotFound;
import io.Inputs;
import java.util.HashMap;
import java.util.Map;
import util.Date;
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
    
    public App() {
        this.inputs = Inputs.getInstance();
        try {
            this.settings = new Settings(this.inputs.getInput("extern_file"));
            _break = ((Long)this.settings.getMetrics().get("break")).toString();
            this.iterador = new Iterador(this.settings.getExterns());
            this.mongo = new Mongo().setDB("data").setCollection(this.settings.getSymbol());         
            MetricsController.newPain("LONG",this.settings.getInitialWon(), this.settings.getFrom(), _break);
            MetricsController.newPain("SHORT",this.settings.getInitialWon(), _break, this.settings.getTo());
            MetricsController.newIR("LONG", this.settings.getFrom(), _break);
            MetricsController.newIR( "SHORT", _break, this.settings.getTo());
            this.file = new Excel(this.settings.getSymbol());
            this.from = Integer.parseInt(this.settings.getFrom());
            this.to = Integer.parseInt(this.settings.getTo());
        } catch (SettingNotFound ex) {
            System.out.println(ex);
        }
    }
    
    public void run(){
        String str;
        int cont = 1;
        Map<String, Object> iteracion = new HashMap();
        System.out.println("Iniciando prueba " + this.iterador.getSize());
        while (this.iterador.hasNext()) {
            iteracion = this.iterador.next();
            this.gear = new Gear(this.settings, iteracion, this.from, Integer.parseInt(_break), this.to);
            this.testData = this.mongo.getRange(Integer.parseInt(this.settings.getFrom()), Integer.parseInt(this.settings.getTo()));
            
            while (this.testData.hasNext()) {
                DBObject o = this.testData.next();
                this.gear.tick(o);
            }
            
            Broker broker = this.gear.getBroker();
            MetricsController.refresh(Date.getDate(), broker.getBalance());
            broker.reset();
            Double ir = MetricsController.getIR();
            Pain painS = MetricsController.getPain("SHORT");
            Pain painL = MetricsController.getPain("LONG");
            str = cont + ", " +ir + ", , "+ broker.getLongProfit() + ", "+broker.getLongTrades() + ", "+ broker.getLongRelative() + ", " + painS + " , ,";
            str +=  broker.getProfit() + ", "+broker.getTotalTrades() + ", "+ broker.getDrowDown() + ", "+ painL + ","+ this.iterador.toString(iteracion);
            System.err.println("#"+cont+" "+str );
            this.file.addData(str);
            this.gear.flush();
            MetricsController.rebuildMetrics();
            cont++;
        }
        String headers = "";
        for(Map.Entry<String, Object> head : iteracion.entrySet()){
            headers += head.getKey() + ",";
        }
        this.file.setHeader("Pass, IR, LONG ->, Profit, Trades, Relative, Pain Index, Pain Ratio, Short ->, Profit, Trades, Relative, Pain Index, Pain Ratio, "+headers + "\n");
    }
    
    /**
     * 
     */
    public void theEnd() {
        this.file.writeItOut();
    }
    
    public static void main(String[] args) {
        App app = new App();
        app.run();
        app.theEnd();
    }
}
