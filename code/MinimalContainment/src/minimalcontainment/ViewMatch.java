/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package minimalcontainment;

import java.util.Set;

/**
 *
 * @author nickyadvokaat
 */
public class ViewMatch {
    public ViewDefinition Qs;
    public ViewDefinition Vi;
    public Set<Edge> edges;

    public ViewMatch(ViewDefinition Qs, ViewDefinition Vi, Set<Edge> edges) {
        this.Qs = Qs;
        this.Vi = Vi;
        this.edges = edges;
    }
    
    public ViewMatch getCopy(){
        return new ViewMatch(this.Qs,this.Vi,this.edges);
    }
}
