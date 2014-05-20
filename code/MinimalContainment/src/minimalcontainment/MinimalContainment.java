/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package minimalcontainment;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author nickyadvokaat
 */
public class MinimalContainment {

    public static Set<ViewDefinition> run(ViewDefinition Qs, Set<ViewDefinition> V) {
        Set<ViewDefinition> Va = new HashSet<>();
        Set<ViewMatch> S = new HashSet<>();
        Set<Edge> E = new HashSet<>();
        HashMap<Edge,Set<ViewDefinition>> M = new HashMap<>();
        
        Set<Edge> Ep = new HashSet<>();
        for(ViewDefinition Vi : Va){
            Ep.addAll(Qs.getViewMatch(Vi));
        }
        
        for(ViewDefinition Vi : Va){
            ViewMatch MQsVi = new ViewMatch(Qs, Vi, Qs.getViewMatch(Vi));
            ViewMatch MQsViCopy = MQsVi.getCopy();
            MQsViCopy.edges.removeAll(E);
            if(!MQsViCopy.edges.isEmpty()){
                Va.add(Vi);
                S.add(MQsVi);
                E.addAll(MQsVi.edges);
                for(Edge e : MQsVi.edges){
                    Set<ViewDefinition> Me = M.get(e);
                    Me.add(Vi);
                    M.put(e, Me);
                }
            }
        }
        if(!E.equals(Ep)){
            return null;
        }
        for(ViewMatch MQsVj : S){
            boolean b = false;
            for(Edge e : MQsVj.edges){
                Set<ViewDefinition> Me = M.get(e);
                
                if(){
                    b = true;
                    break;
                }
            }
            if(!b){
                Va.remove(MQsVj.Vi);
                // update M
            }
        }
        return Va;
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        Set<ViewDefinition> viewDefinitions = new HashSet<ViewDefinition>();
        viewDefinitions.add(null);
        ViewDefinition patternQuery = new ViewDefinition(null,null);
        run(patternQuery,viewDefinitions);
    }
    
}
