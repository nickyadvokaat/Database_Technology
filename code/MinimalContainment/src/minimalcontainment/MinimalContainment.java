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
        Set<Set<Edge>> S = new HashSet<>();
        Set<Edge> E = new HashSet<>();
        HashMap<Edge,Set<ViewDefinition>> M = new HashMap<>();
        for(ViewDefinition Vi : Va){
            Set<Edge> MQsVi = Qs.getViewMatch(Vi);
            Set<Edge> MQsViCopy = new HashSet<>(MQsVi);
            MQsViCopy.removeAll(E);
            if(!MQsViCopy.isEmpty()){
                Va.add(Vi);
                S.add(MQsVi);
                E.addAll(MQsVi);
                for(Edge e : MQsVi){
                    Set<ViewDefinition> Me = M.get(e);
                    Me.add(Vi);
                    M.put(e, Me);
                }
            }
        }
        if(){
            return null;
        }
        for(Set<Edge> MQsVj : S){
            for(Edge e : MQsVj){
                Set<ViewDefinition> Me = M.get(e);
                
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
