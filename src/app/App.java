
package app;

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
    private Settings settings = null;
    private Iterador iterador;
    public App(){
        this.inputs = Inputs.getInstance();
        try {
            this.settings = new Settings(this.inputs.getInput("extern_file"));
            this.iterador = new Iterador(this.settings.getExterns());
            
        } catch (SettingNotFound ex) {
            System.out.println(ex);
        }
    }
    public static void main(String[] args) {
        App app = new App();
    }
}
