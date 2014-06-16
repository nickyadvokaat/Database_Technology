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
     * @param Qs A pattern query
     * @param V A set of view definitions
     * @return A subset Va of V that minimally contains Qs
     */
    public static Set<ViewDefinition> run(ViewDefinition Qs, Set<ViewDefinition> V) {
        Set<ViewDefinition> Va = new HashSet<>();
        Set<ViewMatch> S = new HashSet<>();
        Set<Edge> E = new HashSet<>();
        HashMap<Edge, Set<ViewDefinition>> M = new HashMap<>();

        Set<Edge> Ep = new HashSet<>();
        for (ViewDefinition Vi : V) {
            Ep.addAll(Qs.getViewMatch(Vi));
        }

        for (ViewDefinition Vi : V) {
            ViewMatch MQsVi = new ViewMatch(Qs, Vi, Qs.getViewMatch(Vi));
            ViewMatch MQsViCopy = MQsVi.getCopy();
            MQsViCopy.edges.removeAll(E);
            if (!MQsViCopy.edges.isEmpty()) {
                Va.add(Vi);
                S.add(MQsVi);
                E.addAll(MQsVi.edges);
                for (Edge e : MQsVi.edges) {
                    Set<ViewDefinition> Me = M.get(e);
                    if (Me == null) {
                        Me = new HashSet<>();
                    }
                    Me.add(Vi);
                    M.put(e, Me);
                }
            }
        }
        if (!E.equals(Ep)) {
            return null;
        }
        for (ViewMatch MQsVj : S) {
            boolean b = false;
            for (Edge e : MQsVj.edges) {
                Set<ViewDefinition> Me = M.get(e);
                Me.remove(MQsVj.Vi);
                if (Me.isEmpty()) {
                    b = true;
                    break;
                }
            }
            if (!b) {
                Va.remove(MQsVj.Vi);
                // update M ??what is meant by this
            }
        }
        return Va;
    }

    private static void addViewDefinition(Set<ViewDefinition> viewDifinitions, String[] edges) {
        ViewDefinition v = new ViewDefinition();
        for (int i = 0; i < edges.length; i += 2) {
            String A = edges[i];
            String B = edges[i + 1];
            Edge e = new Edge(A, B);
            v.addEdge(e);
        }
        viewDifinitions.add(v);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        Set<ViewDefinition> viewDefinitions = new HashSet<ViewDefinition>();

        //1
        addViewDefinition(viewDefinitions, new String[]{
            "V>=10K", "C=Music"});

        //2
        addViewDefinition(viewDefinitions, new String[]{
            "R>=5", "C=Sports"});

        //3
        addViewDefinition(viewDefinitions, new String[]{
            "C=Music", "V>=10k",
            "C=Music", "R>=4"});

        //4
        addViewDefinition(viewDefinitions, new String[]{
            "A<=100", "C=Sports",
            "C=Sports", "R>=5"});

        //5
        addViewDefinition(viewDefinitions, new String[]{
            "R>=4", "C=Sports",
            "L<=200", "C=Sports"});

        //6
        addViewDefinition(viewDefinitions, new String[]{
            "R>=5", "V>=10K",
            "V>=10K", "C=Ent.",
            "C=Ent.", "R>=5"});

        ViewDefinition patternQuery = new ViewDefinition();
        Edge e0 = new Edge("C=Music", "R>=4");
        Edge e1 = new Edge("R>=4", "C=Sports");
        Edge e2 = new Edge("R>=5", "C=Sports");
        patternQuery.addEdge(e0);
        patternQuery.addEdge(e1);
        patternQuery.addEdge(e2);

        Set<ViewDefinition> result = run(patternQuery, viewDefinitions);

        if (result == null) {
            System.out.println("Result was null");
        } else {
            int i = 0;
            for (ViewDefinition V : result) {
                System.out.println("View " + i++);
                for (Edge e : V.edges) {
                    System.out.println("( " + e.from + " , " + e.to + " )");
                }
            }
        }
    }
}
