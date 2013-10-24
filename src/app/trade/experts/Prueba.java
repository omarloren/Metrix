package app.trade.experts;

import app.indicator.iBand;
import java.util.logging.Level;
import java.util.logging.Logger;
import trade.IExpert;

/**
 *
 * @author omar
 */
public class Prueba extends Expert implements IExpert{
    iBand b1;
    @Override
    public void Init() {
       this.b1 = this.newBand(60);
    }

    @Override
    public void onTick() {
        
       if(this.isTradeTime() && this.isNewCandle()) {
            
            
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
}
