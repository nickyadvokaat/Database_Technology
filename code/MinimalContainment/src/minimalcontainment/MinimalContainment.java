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

    /**
     * Minimal Containment Algorithm
     * 
     * @param Qs    A pattern query
     * @param V     A set of view definitions
     * @return      A subset Va of V that minimally contains Qs
     */
    public static Set<ViewDefinition> run(ViewDefinition Qs, Set<ViewDefinition> V) {
        Set<ViewDefinition> Va = new HashSet<>();
        Set<ViewMatch> S = new HashSet<>();
        Set<Edge> E = new HashSet<>();
        HashMap<Edge,Set<ViewDefinition>> M = new HashMap<>();
        
        Set<Edge> Ep = new HashSet<>();
        for(ViewDefinition Vi : V){
            Ep.addAll(Qs.getViewMatch(Vi));
        }
        
        for(ViewDefinition Vi : V){
            ViewMatch MQsVi = new ViewMatch(Qs, Vi, Qs.getViewMatch(Vi));
            ViewMatch MQsViCopy = MQsVi.getCopy();
            MQsViCopy.edges.removeAll(E);
            if(!MQsViCopy.edges.isEmpty()){
                Va.add(Vi);
                S.add(MQsVi);
                E.addAll(MQsVi.edges);
                for(Edge e : MQsVi.edges){
                    Set<ViewDefinition> Me = M.get(e);
                    if(Me == null){
                        Me = new HashSet<>();
                    }
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
                Me.remove(MQsVj.Vi);
                if(Me.isEmpty()){
                    b = true;
                    break;
                }
            }
            if(!b){
                Va.remove(MQsVj.Vi);
                // update M ??what is meant by this
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
        
        Edge e1 = new Edge("V>=10K","C=Music");
        ViewDefinition v1 = new ViewDefinition();
        v1.addEdge(e1);
        
        Edge e2 = new Edge("C=Music","V>=10k");
        Edge e3 = new Edge("C=Music","R>=4");
        ViewDefinition v2 = new ViewDefinition();
        v2.addEdge(e2);
        v2.addEdge(e3);
        
        viewDefinitions.add(v1);
        viewDefinitions.add(v2);
        
        ViewDefinition patternQuery = new ViewDefinition();
        Edge e0 = new Edge("V>=10K","C=Music");
        patternQuery.addEdge(e0);
        
        Set<ViewDefinition> result = run(patternQuery,viewDefinitions);
        
<<<<<<< HEAD
        if(result == null){
            System.out.println("Result was null");
        }else{
            int i = 0;
            for(ViewDefinition V : result){
               System.out.println("View " + i++);
               for(Edge e : V.edges){
                   System.out.println("( " + e.from + " , " + e.to + " )");
               }
            }
        }
=======
        run(patternQuery,viewDefinitions);
	/*test*/
>>>>>>> FETCH_HEAD
    }
    
}
