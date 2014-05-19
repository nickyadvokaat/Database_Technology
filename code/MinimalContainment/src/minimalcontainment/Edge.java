/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package minimalcontainment;

/**
 *
 * @author nickyadvokaat
 */
public class Edge {
    
    public Vertex from;
    public Vertex to;
    
    public Edge(Vertex from, Vertex to){
        this.from = from;
        this.to = to;
    }
}
