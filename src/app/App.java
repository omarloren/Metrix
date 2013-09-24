
package app;

import app.metrics.MetricsController;
import app.metrics.base.Monthly;
import app.metrics.base.Pain;
import app.trade.Broker;
import app.trade.Gear;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import dao.Mongo;
import io.Exceptions.SettingNotFound;
import io.Inputs;
import java.util.ArrayList;
import trade.Arithmetic;
import util.Date;
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
   
    private ArrayList<String> result = new ArrayList();
    
    public static String _break; //Malditas palabras reservadas
    
    public App() {
        this.inputs = Inputs.getInstance();
        try {
            this.settings = new Settings(this.inputs.getInput("extern_file"));
            _break = ((Long)this.settings.getMetrics().get("break")).toString();
            this.iterador = new Iterador(this.settings.getExterns());
            this.mongo = new Mongo().setDB("data").setCollection(this.settings.getSymbol());         
            MetricsController.newPain("PAIN",this.settings.getInitialWon(), this.settings.getFrom(), this.settings.getTo());
            MetricsController.newIR("LONG", this.settings.getFrom(), _break);
            MetricsController.newIR( "SHORT", _break, this.settings.getTo());
        } catch (SettingNotFound ex) {
            System.out.println(ex);
        }
    }
    
    public void run(){
//        System.err.println("Iniciando prueba, desde "+this.settings.getFrom()+" - hasta "+this.settings.getTo()+" con: " + this.iterador.getSize() + " iteraciones y "+ this.testData.count() + " precios a procesar");
        String str;
        int cont = 0;
        
        while (this.iterador.hasNext()) {
            this.gear = new Gear(this.settings, this.iterador.next());
            System.out.println("-----------------------------------------------------------------------------------------------------");
            this.testData = this.mongo.getRange(Integer.parseInt(this.settings.getFrom()), Integer.parseInt(this.settings.getTo()));
           
            while (this.testData.hasNext()) {
                DBObject o = this.testData.next();
                this.gear.tick(o);
            }
            cont++;
            Broker broker = this.gear.getBroker();
            MetricsController.refresh(Date.getDate(), broker.getBalance());
            Double ir = MetricsController.getIR();
            Pain pain = MetricsController.getPain();
            str = "#" + cont + " " +broker + " \n IR:" + ir + " "+ pain; 
            System.err.println(str);
            this.result.add(str);
            this.gear.flush();
            MetricsController.rebuildMetrics();
        }
    }
    
    public static void main(String[] args) {
        App app = new App();
        app.run();
        System.out.println("EL FIN :)");
    }
}
