
package app;

import app.metrics.MetricsController;
import app.metrics.base.IR;
import app.metrics.base.Pain;
import app.trade.Broker;
import app.trade.Gear;
import com.mongodb.DBCursor;
import dao.Mongo;
import io.Exceptions.SettingNotFound;
import io.Inputs;
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
    private Pain pain;
    private IR irShort;
    private IR irLong;
    private Integer _break; //Malditas palabras reservadas
    
    public App() {
        this.inputs = Inputs.getInstance();
        try {
            this.settings = new Settings(this.inputs.getInput("extern_file"));
            this._break = (Integer)this.settings.getMetrics().get("break");
            this.iterador = new Iterador(this.settings.getExterns());
            this.mongo = new Mongo().setDB("data").setCollection(this.settings.getSymbol());
            this.testData = this.mongo.getRange(this.settings.getFrom(), this.settings.getTo());
            this.gear = new Gear(this.settings);
            this.pain = MetricsController.newPain(this.settings.getInitialWon(), this.settings.getFrom(), this.settings.getTo());
            this.irLong = MetricsController.newIR(this.settings.getFrom(), this._break);
            this.irShort = MetricsController.newIR(this._break, this.settings.getTo());
        } catch (SettingNotFound ex) {
            System.out.println(ex);
        }
    }
    
    public void run(){
        System.err.println("Iniciando prueba, desde "+this.settings.getFrom()+" - hasta "+this.settings.getTo()+" con: " + this.iterador.getSize() + " iteraciones y "+ this.testData.count() + " precios a procesar");
        while (this.iterador.hasNext()) {
           this.gear.rollOn(this.iterador.next());
           while (this.testData.hasNext()) {
               this.gear.tick(this.testData.next());
           }
           this.gear.flush();
        }
        Broker broker = this.gear.getBroker();
        System.out.println(broker);
        System.err.println(pain);
        System.out.println("EL FIN :)");
    }
    
    public static void main(String[] args) {
        App app = new App();
        app.run();
    }
}
