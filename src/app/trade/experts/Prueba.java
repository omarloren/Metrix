package app.trade.experts;

import help.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import trade.IExpert;
import trade.indicator.base.BollingerBands;

/**
 *
 * @author omar
 */
public class Prueba extends Expert implements IExpert{
    private BollingerBands b1;
    @Override
    public void Init() {
       b1 = this.iBand(5);
    }

    @Override
    public void onTick() {
        
       if(this.isTradeTime() && this.isNewCandle()) {
            System.out.println(this.getDate() + "-"+Date.horaToString() + " > " + " " +this.b1.values);
            try {
                Thread.sleep(0);
            } catch (InterruptedException ex) {
                Logger.getLogger(BSFF1_8_SV.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public Boolean isTradeTime(){
        int c = this.getHora() + (this.getMinutes() / 100);
        return (c < 1) && (c >= 0);
    }
    
    @Override
    public void onDone() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
     @Override
    public String toString(){
        return this.getOpenMin() +" ==> Up:"+this.b1.getUpperBand() +" Mid: "+this.b1.getMiddleBand()+ " Dn:"+this.b1.getLowerBand();
    }
}
