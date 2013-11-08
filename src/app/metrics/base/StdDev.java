package app.metrics.base;

/**
 *
 * @author omar
 */
public class StdDev extends Metric{

    public StdDev(String id, String from, String to){
        super(id, from, to);
    }
    @Override
    public void feed(Double val) {
        this.getValues().add(val);
    }

    @Override
    public Boolean isNew() {
       return false;
    }

}
