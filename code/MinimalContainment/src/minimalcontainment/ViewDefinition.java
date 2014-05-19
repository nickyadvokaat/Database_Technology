/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package minimalcontainment;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author nickyadvokaat
 */
public class ViewDefinition {
    public Set<Vertex> vertices;
    public Set<Edge> edges;
    
    public ViewDefinition(Set<Vertex> vertices, Set<Edge> edges){
        this.vertices = vertices;
        this.edges = edges;
    }
    
    public Set<Edge> getViewMatch(ViewDefinition Vi){
        Set<Edge> result = new HashSet<>();
        for(Edge e1 : this.edges){
            for(Edge e2 : Vi.edges){
                if(edgeEqual(e1, e2)){
                    result.add(e1);
                }
            }
        }
        return result;
    }
    
    private static boolean edgeEqual(Edge e1, Edge e2){
        return e1.from.name.equals(e2.from.name) &&
                e1.to.name.equals(e2.to.name);
    }
}
