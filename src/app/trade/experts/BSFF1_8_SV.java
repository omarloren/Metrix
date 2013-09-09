package app.trade.experts;

import app.trade.Orden;
import java.util.logging.Level;
import java.util.logging.Logger;
import trade.Arithmetic;
import trade.IExpert;
import trade.indicator.base.BollingerBands;
import util.Date;

/**
 * BSFF1_8_SV -> Bollinger Sin FIFO 1.8 Salida de Ventas.
 * @author omar
 */
public class BSFF1_8_SV extends Expert implements IExpert{
    //Bandas de bollinger utilizadas.
    
    private BollingerBands b1;
    private BollingerBands b2;
    private BollingerBands b3;
    private BollingerBands bs1;
    private BollingerBands bs2;
    private BollingerBands bs3;
    private BollingerBands bx1;
    private BollingerBands bx2;
    private BollingerBands bx3;
    private Double sl;
    private Double tp; 
    private Double bollSpecial;
    private Double bollXDn;
    private Double bollXUp;
    private Double bollUp;
    private Double bollDn;
    private Double bollUpS;
    private Double bollDnS;
    private Double bollDif;
    private Integer velasSalida;
    private Integer limiteCruce;
    private Integer horaIni;
    private Integer horaFin;
    private Integer contVelas = 0;
    
    /**
     * Inicia las variables.
     */
    @Override
    public void Init() {
        
        b1 = this.iBand(this.extern.getInteger("periodoBoll"));
        b2 = this.iBand(this.extern.getInteger("periodoBoll2"));
        b3 = this.iBand(this.extern.getInteger("periodoBoll3"));
        bs1 = this.iBand(this.extern.getInteger("periodoBollSalida"));
        bs2 = this.iBand(this.extern.getInteger("periodoBollSalida2"));
        bs3 = this.iBand(this.extern.getInteger("periodoBollSalida3"));
        bx1 = this.iBand(this.extern.getInteger("XBoll"));
        bx2 = this.iBand(this.extern.getInteger("XBoll2"));
        bx3 = this.iBand(this.extern.getInteger("XBoll3"));
        bollXDn = this.extern.getDouble("bollXDn") ;
        bollXUp = this.extern.getDouble("bollXUp") ;
        sl = Arithmetic.multiplicar(this.extern.getInteger("sl").doubleValue() , this.getPoint());
        tp = Arithmetic.multiplicar(this.extern.getInteger("tp").doubleValue() , this.getPoint());
        bollSpecial = this.extern.getDouble("bollspecial") ;
        velasSalida = this.extern.getInteger("num_velas_salida");
        limiteCruce = this.extern.getInteger("limiteCruce");
        horaIni = this.extern.getInteger("horainicial");
        horaFin = this.extern.getInteger("horafinal");
        bollUp = this.bollUp();
        bollDn = this.bollDn();
        bollUpS = this.bollUpS();
        bollDnS = this.bollDnS();
        bollDif = this.bollDif();
    }

    @Override
    public void onTick() {
        
        if(this.isNewCandle()) {
            bollUp = this.bollUp();
            bollDn = this.bollDn();
            bollUpS = this.bollUpS();
            bollDnS = this.bollDnS();
            bollDif = this.bollDif();
            this.contVelas++;
        }
        
        if(this.isTradeTime() && this.bollDif < this.bollXUp && this.bollDif > this.bollXDn &&
                this.ordersBySymbol() < this.limiteCruce) {
            
            if(this.priceCut(this.getOpenMin() + this.bollSpecial) <= bollDn) {
                double sl = Arithmetic.restar(this.getBid(), this.sl);
                double tp = Arithmetic.sumar(this.getBid(), this.tp);
                this.orderSend(1.0, sl, tp, '1', this.getAsk());
                contVelas = 0;
                
            } else if(this.priceCut(this.getOpenMin() + this.bollSpecial) >= bollUp) {
                double sl = Arithmetic.sumar(this.getAsk(), this.sl);
                double tp = Arithmetic.restar(this.getBid(), this.tp);
                this.orderSend(1.0, sl, tp, '2', this.getBid());
                contVelas = 0;
            }
            
        } else if(this.ordersBySymbol() > 0) {
            
            for (int i = 0; i < this.ordersBySymbol(); i++) {
                Orden o = (Orden)this.ordersTotal(this.getMagic()).get(i);
                
                if(o.getSide() == '2') {
                    if(this.getOpenMin() <= this.bollDnS) {
                        o.close(this.getAsk(), "Cierre por bollinger");
                       
                    } else if(this.contVelas >= this.velasSalida) {
                        o.close(this.getAsk(), "Cierre por velas");
                        
                    }
                }else if(o.getSide() == '1') {
                    if(this.getOpenMin() >= this.bollUpS) {                        
                        o.close(this.getBid(), "Cierre por bollinger");
                        
                    } else if(this.contVelas >= this.velasSalida) {
                        o.close(this.getBid(), "Cierre por velas");
                                              
                    }
                }
            }
        }
    }

    @Override
    public void onDone() {
        
    }
    /**
     * Define si es tiempo de operar.
     * @return 
     */
    public Boolean isTradeTime(){
        int c = this.getHora() + (this.getMinutes() /100);
        return (c < this.horaFin) && (c >= this.horaIni) && this.isReady();
    }
    
    /**
     * Promedio de entrada de ventas.
     * @return 
     */
    public Double bollUp() {
        
        return (b1.getUpperBand() + b2.getUpperBand() + 
                            b3.getUpperBand())/3;
    }
    /**
     * Promedio Entrada de compras.
     * @return 
     */
    private Double bollDn() {
        return (b1.getLowerBand() + b1.getLowerBand() +
                            b1.getLowerBand())/3;
    }
    
     /**
     * Promedio salida de compras.
     * @return 
     */
    private Double bollUpS() {
        return (bs1.getUpperBand() + bs2.getUpperBand() +
                            bs3.getUpperBand())/3;
    }
    /**
     * Promedio salida de ventas.
     * @return 
     */
    private Double bollDnS() {
        return (bs1.getLowerBand() + bs2.getLowerBand() +
                            bs3.getLowerBand())/3;
    }
    
    private Double bollDif() {
        Double tempUp = Arithmetic.dividir(3, bx1.getUpperBand(), bx2.getUpperBand(), bx3.getUpperBand());
        Double tempDn = Arithmetic.dividir(3, bx1.getLowerBand(), bx2.getLowerBand(), bx3.getLowerBand());
        Double temp = Arithmetic.restar(tempUp, tempDn);
        return temp;
    }
    
    @Override
    public String toString(){
        return Date.horaToString()+" "+this.getOpenMin() +" ==> BollUp:"+bollUp + " BollDn:"+bollDn;
    }
}
