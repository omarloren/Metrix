
package util;

import io.Exceptions.SettingNotFound;
import io.Inputs;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Clase que escribe un archivo .csv para ser usado por excel, con los resultados
 * de las pruebas.
 * @author omar
 */
public class Excel {
    
    private String file;
    private ArrayList<String> values = new ArrayList();
    private String header = "";
    private Boolean canWrite;
    public Excel(String file){
        this.file = file + ".csv";
        try {
            this.canWrite = Boolean.parseBoolean(Inputs.getInstance().getInput("logger"));
        } catch (SettingNotFound ex) {
            Logger.getLogger(Excel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void addData(String str) {
       // System.out.println(str);
        this.values.add(str);
    }
    public void setHeader(String header){
        this.header = header;
    }
    public void writeItOut(){
        if(this.canWrite) {
            try (FileWriter wr = new FileWriter(this.file)) {
                wr.append(this.header);
                for (int i = 0; i < this.values.size(); i++) {
                    wr.append(this.values.get(i) + "\n");
                }
                wr.flush();
            }
            catch (IOException ex) {
                Logger.getLogger(Excel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
