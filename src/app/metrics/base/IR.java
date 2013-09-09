package app.metrics.base;

/**
 *
 * @author omar
 */
public class IR extends Metric{
    
    public IR(Integer from, Integer to){
        super(from, to);
    }
    
    @Override
    public void feed(Double val) {
        this.values.add(val);
    }
}
