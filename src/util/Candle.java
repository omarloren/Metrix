package util;

/**
 *
 * @author omar
 */
public class Candle {
    private Integer lastMin = 0;
    private Integer p;
    private Boolean strictMode = true;
    public Candle(Integer p){
        this.p = p;
    }
     /**
     * Es una nueva vela, si la hora es mod 0 del periodo.
     * @param hora
     * @return 
     */
    public Boolean isNew(Integer min){
        if(min == 0 && !this.strictMode) {
            min = 60;
        }
        //Cambios de hora malditos.
        if (min == 0 && lastMin == (60 - this.p)) {
            min = 60;
        }
       // System.out.println(Date.dateToString() + " " + lastMin);
        /**
         * Compara la resta de la ultima apertura de minuto con el minuto actual
         * y despues con el, una operacion para saber si el mod corresponde a 0.
         **/
        if ((min - lastMin) >= this.p && (min - (this.p * (min/this.p))) == 0) {
            lastMin = min;
            //Malditos cambios de hora.
            if(lastMin == 60){
                lastMin = 0;
            }
            
            return true;
        } else {
            return false;
        }
    }
    
    public void reset(){
        //this.lastMin = 0;
    }
    
    public void setStrict(boolean s){
        this.strictMode = s;
    }
}
