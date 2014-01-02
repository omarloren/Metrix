package app;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import trade.Arithmetic;

/**
 *
 * @author omar
 */
public class State implements Runnable{
    private Integer total;
    private static Integer cont = 0;
    private Integer ref = -1;
    private StringBuilder lastStr = new StringBuilder("");
    private String str;
    private Double percent = 0.0;
    private Double step;
    private Double next = 0.0;
    private static ArrayList<Double> values = new ArrayList();
    /**
     * Imprime FOREX TESTER con letra chola.
     */
    public void hello() {
        cls();
        System.out.println(".__   __.     ___     .______    ______   __      _______  ______  .__   __.");
        System.out.println("|  \\ |  |    /   \\    |   _  \\  /  __  \\ |  |    |   ____|/  __  \\ |  \\ |  |");
        System.out.println("|   \\|  |   /  ^  \\   |  |_)  ||  |  |  ||  |    |  |__  |  |  |  ||   \\|  |");
        System.out.println("|  . `  |  /  /_\\  \\  |   ___/ |  |  |  ||  |    |   __| |  |  |  ||  . `  |");
        System.out.println("|  |\\   | /  _____  \\ |  |     |  `--'  ||  `----|  |____|  `--'  ||  |\\   |");
        System.out.println("|__| \\__|/__/     \\__\\| _|      \\______/ |_______|_______|\\______/ |__| \\__|");
        System.out.println("=======================================================================|0.2.4|");
        System.out.println("");
        this.ref = cont;
        this.lastStr.append(">"); 
        
    }   
    
    public void setTotal(Integer total){
        
        System.out.println("Iniciando prueba " + total + " iteraciones ...");
        this.total = total;
        this.step = 1 / this.total.doubleValue() * 100;
        this.next = (total.doubleValue()*0.05);
    }
    
    public synchronized static void step(){
        cont++;    
    }
    
    public synchronized static void addTime(Double time){
        values.add(time);
    }
    
    public void cls(){
        try{
            String os = System.getProperty("os.name");
            
            if (os.contains("Windows")) {
                Runtime.getRuntime().exec("cls");
            }else{
                Runtime.getRuntime().exec("clear");
            }
        } catch (Exception ex){
            System.out.println(ex);
        }
    }
    
    public void print(String str){
        
    }

    @Override
    public void run() {
        char[] chars = new char[]{'|', '/', '-', '\\'};
        System.out.println("Procesando: ");
        while(cont < this.total){
            try {
                for (int i = 0; i < 4; i++) {
                    String rewind = "\b";
                    if(this.ref != cont){
                        
                        if (cont >= this.next) {
                            this.lastStr.append("=");
                            this.next += (total*0.05);
                        }
                        this.ref = cont;
                        this.percent = Arithmetic.redondear(cont * this.step,2);
                        
                    }
                    this.str = this.getTestAvg()+" sgs-("+cont+"/"+this.total+")"+this.percent+"%"+this.lastStr + ""+ chars[i];
                    for (int j = 0; j <= this.str.length(); j++) {
                        rewind += "\b";
                    }
                    System.out.print(this.str);
                    
                    Thread.sleep(1000);
                    System.out.print(rewind);
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(State.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        System.out.println("");
    }    
    
    private Double getTestAvg(){
        Double sum = 0.0;
        for (int i = 0; i < values.size(); i++) {
            sum += values.get(i);
        }
        if(sum==0.0){
            return 0.0;
        }else{
            return Arithmetic.redondear(sum / values.size(),1);
        }
    }
}
