
package util;

import io.Exceptions.SettingNotFound;
import io.Inputs;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Clase que genera las iteraciones que se correrán para una prueba.
 * @author omar
 */
public final class Iterador {
    private Map<String, ArrayList> values;
    private ArrayList<Object[]> _collection = new ArrayList();
    private ArrayList<Map<String, Object>> pool = new ArrayList();
    private Integer contIteraciones = 0;
    private Boolean hasNext = true;
    private Integer cont = 0;
    private Integer sample;
    
    public Iterador(Map<String, ArrayList> values) {
        Integer total = 1;
        this.values = values;
        Set<String> set = values.keySet();
        for (String key : set) {
            Object[] temp = new Object[values.get(key).size()];
            for (int i = 0; i < values.get(key).size(); i++) {
                temp[i] = ((Object)values.get(key).get(i));
            }
            total *= temp.length;
            this._collection.add(temp);
        }
        
        try {
            Integer i = Integer.parseInt(Inputs.getInstance().getInput("muestra"));
            this.sample = total / i < 1 ? 1 : total / i;
           
        } catch (SettingNotFound ex) {
            Logger.getLogger(Iterador.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        this.generate(0, this._collection, "");
    }
        
    public Integer getSize(){
        return this.pool.size();
    }
    
    public ArrayList<Map<String, Object>> getData() {
        return this.pool;
    }
    
    public ArrayList<Object[]>  getCollection() {
        return this._collection;
    }
    
    public Integer getTotal(ArrayList<Object[]> t){
        Integer c = 0;
        for (int i = 0; i < t.size(); i++) {
            c *= t.get(i).length;
        }
        return c;
    }
    /**
     * @return Siguente elemento en Pool de datos.
     */
    public Map<String, Object> next() {
       //si hemos alcanzado el final del pool marcamos hasNext como falsa;
       if (this.contIteraciones + 1 == this.pool.size()) {
           this.hasNext = false;
       }
       return this.pool.get(contIteraciones++);
    }
    
    /**
     * @return Si el pool sigue teniendo valores;
     */
    public Boolean hasNext() {
        return this.hasNext;
    }
    
    public static String toString(Map<String, Object> i){
        Set<String> set = i.keySet();
        String s = " ";
        for (String key : set) {
            s += i.get(key) +", ";
        }
        return s.substring(0, s.length() - 2) + "\n";
    }
    
    /**
     * MÉTODO RECURSIVO: Genera todas las posibles combinaciones entre un rango
     * de variables, en este caso esas variables estan en el ArrayList _collection
     * @param pos
     * @param v
     * @param cumulo 
     */
    private void generate(int pos, ArrayList<Object[]> v, String cumulo) {
        
        //Cuando llegamos al final quiere decir que obtuvimos una nueva combinación
        if (pos == v.size()) {
            this.cont++;
            if(this.cont % this.sample == 0){
                this.rollOn(cumulo);
            }
            return;            
        }
        for (int i = 0; i != v.get(pos).length; i++) {
           generate(pos+1, v,cumulo +""+ v.get(pos)[i]+"*");
        }
    }
    /**
     * Comvertimos cada combinación generada en un elemento de el Pool de datos.
     * @param cumulo 
     */
    private void rollOn(String cumulo){
        String[] base = cumulo.split("\\*");
        Map<String, Object> temp = new HashMap<>();
        int i = 0;
        Set<String> set = values.keySet();
        for (String key : set) {
            temp.put(key, base[i]);
            i++;
        }
        this.pool.add(temp);
    }
}
