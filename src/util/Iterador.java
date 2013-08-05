
package util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Clase que genera las iteraciones que se correrán para una prueba.
 * posible de variables.
 * @author omar
 */
public class Iterador {
    private Map<String, ArrayList> values;
    private ArrayList<int[]> _collection = new ArrayList();
    private ArrayList<Map<String, Integer>> pool = new ArrayList();
    private Integer contIteraciones = 0;
    private Boolean hasNext = true;
    
    
    public Iterador(Map<String, ArrayList> values) {
        this.values = values;
        Set<String> set = values.keySet();
        for (String key : set) {
            int[] temp = new int[values.get(key).size()];
            for (int i = 0; i < values.get(key).size(); i++) {
                temp[i] = ((Long)values.get(key).get(i)).intValue();
            }
            this._collection.add(temp);
        }
        this.generate(0, this._collection, "");
    }
    
    public Integer getSize(){
        return this.pool.size();
    }
    /**
     * @return Siguente elemento en Pool de datos.
     */
    public Map<String, Integer> next() {
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
    /**
     * MÉTODO RECURSIVO: Que genera todas las posibles combinaciones entre un rango
     * de variables, en este caso esas variables estan en el ArrayList _collection
     * @param pos
     * @param v
     * @param cumulo 
     */
    private void generate(int pos, ArrayList<int[]> v, String cumulo){
        //Cuando llegamos al final quiere decir que obtuvimos una nueva combinación
        if (pos == v.size()) {
            this.rollOn(cumulo);
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
        Map<String, Integer> temp = new HashMap<>();
        int i = 0;
        Set<String> set = values.keySet();
        for (String key : set) {
            temp.put(key, new Integer(base[i]));
            i++;
        }
        this.pool.add(temp);
    }
}
