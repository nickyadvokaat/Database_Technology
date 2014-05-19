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

    public static void run(PatternQuery Qs, Set<ViewDefinition> V) {
        Set<ViewDefinition> Va = new HashSet<ViewDefinition>();
        Set<Edge> E;
        HashMap M;
        for(ViewDefinition Vi : Va){
            Set<Edge> MsQVi = Qs.getViewMatch(Vi);
        }
        if(){
            
        }
        for(){
            
        }
        
        // print resultViewDefinitions;
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        Set<ViewDefinition> viewDefinitions = new HashSet<ViewDefinition>();
        viewDefinitions.add(null);
        PatternQuery patternQuery = new PatternQuery();
        run(patternQuery,viewDefinitions);
    }
    
}
