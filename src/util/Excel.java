
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
    private Integer limit;
    public Excel(String file) {
        this.file = file + ".csv";
        try {
            this.canWrite = Boolean.parseBoolean(Inputs.getInstance().getInput("logger"));
        } catch (SettingNotFound ex) {
            Logger.getLogger(Excel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public synchronized void addData(String str) {
        this.values.add(str);
        if(this.limit == this.values.size()) {
            this.writeItOut();
        }
    }
    
    public Excel setHeader(String header) {
        this.header = header;
        return this;
    }
    public Excel setLimit(Integer i) {
        this.limit = i;
        return this;
    }
    private void writeItOut() {
        if(this.canWrite) {
            
            try (FileWriter wr = new FileWriter(this.file)) {
                wr.append(this.header);
                for (int i = 0; i < this.values.size(); i++) {
                    wr.append((i+1)+", "+this.values.get(i));
                }
                wr.flush();
            }
            catch (IOException ex) {
                Logger.getLogger(Excel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
