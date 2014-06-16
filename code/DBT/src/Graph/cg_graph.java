package Graph;

import fileOpe.writefile;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;

import org.jgrapht.DirectedGraph;
import org.jgrapht.Graph;
import org.jgrapht.alg.StrongConnectivityInspector;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.traverse.BreadthFirstIterator;
import org.jgrapht.traverse.TopologicalOrderIterator;


public class cg_graph extends DefaultDirectedWeightedGraph<String, edge> implements Serializable{

	public String gfilename = "";
	public StrongConnectivityInspector<String, edge> sccIns;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public cg_graph() {
		super(edge.class);
	}
	
	/**
	 * find the parent nodes of the specified node
	 * @param n
	 * @return
	 */
	public Vector<String> getParents(String n){
		Vector<String> ps = new Vector<String>();
		for(edge e:this.incomingEdgesOf(n)){
			ps.add((String) e.getSource());
		}
		return ps;
	}
	
	/**
	 * find the children nodes of the specified node
	 * @param n
	 * @return
	 */
	public Vector<String> getChildren(String n){
		Vector<String> cs = new Vector<String>();
		for(edge e: this.outgoingEdgesOf(n)){
			cs.add((String) e.getTarget());
		}
		return cs;
	}
	

	
	/**
	 * breadth first search
	 * @param snode
	 */
	public void bfs(String snode)
	{
		HashSet<String> visited = new HashSet<String>();
		Queue<String> q = new LinkedList<String>();
		q.add(snode);
		visited.add(snode);
		while(!q.isEmpty()){
			String n = q.poll();
			for(edge e:this.outgoingEdgesOf(n)){
				String current = this.getEdgeTarget(e);
				if(!visited.contains(current)){
					q.add(current);
					visited.add(current);	// label the node which has been visited.
				}
			}
		}
	}
	
	
		
	/**
	 *  sort the node in descending order according to degree
	 * @param g
	 * @return
	 */
	public Vector<String> sortwithdegree(){
		Vector<String> nodeSet = new Vector<String>();
		nodeSet.addAll(this.vertexSet());
		Comparator<String> comparator = new nodeComparator(this);
		Collections.sort(nodeSet, comparator);
		return nodeSet;		
	}
	
	/**
	 *  sort the node in descending order according to degree
	 * @param hs : hashset as data structure
	 * @return
	 */
	public Vector<String> sortwithdegree(HashSet<String> hs){
		Vector<String> nodeSet = new Vector<String>();
		nodeSet.addAll(hs);
		Comparator<String> comparator = new nodeComparator(this);
		Collections.sort(nodeSet, comparator);
		return nodeSet;
	}
	
	/**
	 * randomly pick a node in the graph
	 * @return
	 */
	public String getAnode(){
		Vector<String> vlist = new Vector<String>();
		vlist.addAll(this.vertexSet());
		return vlist.elementAt((int)(Math.random()*vlist.size()));
	}
	
	/**
	 *  randomly pick an edge in the graph
	 * @return
	 */
	public edge getAedge(){
		Vector<edge> elist = new Vector<edge>();
		elist.addAll(this.edgeSet());
		return elist.elementAt((int)(Math.random()*elist.size()));
	}	
	
	/**
	 *  condense strongly connected component to be a node
	*/
	public cg_graph sccDAG(){
		StrongConnectivityInspector<String, edge> sccIns = new StrongConnectivityInspector<String, edge>(this);
		List<Set<String>> sccsets = sccIns.stronglyConnectedSets();
		cg_graph sccDAG = new cg_graph();

		HashMap<String, String> clusterMap = new HashMap<String, String>();
		//		int i = 0;
		for(Set<String> s:sccsets){
			String newnode = new String();
			String newtag = "";
			for(String n:s){
				newtag = newtag+n+"_";
			}
			newtag = newtag.substring(0,newtag.lastIndexOf("_"));
			/**
			 * where there is a very large scc, use below two lines to label the node, to avoid the long node id.
			 */
			//			newtag = String.valueOf(i);
			//			i++;
			for(String n:s){
				clusterMap.put(n, newtag);
			}
			newnode = newtag;
			sccDAG.addVertex(newnode);
		}
		for(Set<String> scc:sccsets){
			for(String n:scc){
				String newfrom = clusterMap.get(n);
				if(this.outDegreeOf(n)>0){
					for(edge e:this.outgoingEdgesOf(n)){
						String end = this.getEdgeTarget(e);
						if(!scc.contains(end)){	// get the node outside the current cluster
							String newend = clusterMap.get(end);							
							if(!sccDAG.containsEdge(newfrom, newend)){
								edge newedge = new edge(newfrom,newend);
								sccDAG.addEdge(newfrom, newend, newedge);
							}
						}
					}
				}
			}
		}
		return sccDAG;
	}

	/**
	 * Generate an induced subgraph
	 * @param graph
	 * @param nSet
	 * @return
	 */
	public cg_graph insubGraph(Set<String> nSet){
		cg_graph insubG = new cg_graph();
		for(String n:nSet){
			insubG.addVertex(n);
		}
		for(String n1:nSet){
			for(String n2:nSet){
				if(this.getEdge(n1, n2)!=null){
					edge e = new edge(n1,n2);
					insubG.addEdge(n1, n2, e);
				}
			}
		}
		return insubG;
	}
	
	
	
	/**
	 * @return a set of strongly connected components
	 */
	public List<Set<String>> sccSet(){
		StrongConnectivityInspector<String, edge> sccIns = new StrongConnectivityInspector<String, edge>(this);
		List<Set<String>> sccsets = sccIns.stronglyConnectedSets();
		return sccsets;
	}
	
	
	
	/**
	 * this procedure computes topological rank of the graph
	 */
	public HashMap<String, Integer> topRank(){
		cg_graph DAGP = this.sccDAG();		//	shrink the graph as a DAG first
		Stack<String> s = new Stack<String>();		//	stack not queue
		HashMap<String, Integer> visited = new HashMap<String, Integer>();

		/**
		 * add one virtual vertex 
		 */
		String virtualvertex = "v";
		DAGP.addVertex(virtualvertex);
		for(String v:DAGP.vertexSet()){
			if(!v.equals(virtualvertex)){
				if(DAGP.inDegreeOf(v)==0){
					edge ne = new edge(virtualvertex, v);
					DAGP.addEdge(virtualvertex, v, ne);
				}
			}
		}

		int max = 0;
		boolean flag = true;
		s.add(virtualvertex);

		while(!s.isEmpty()){
			String current = s.peek();	//	remains the node in the stack until its rank is evaluated.
			flag = true;
			max = 0;
			for(edge e:DAGP.outgoingEdgesOf(current)){
				String n = (String) e.getTarget();
				if(!visited.keySet().contains(n)){
					s.add(n);
					flag = false;
				}
				else if(visited.keySet().contains(n) && flag){
					if(max<visited.get(n)){
						max = visited.get(n);
					}
				}
			}
			if(flag == true){
				visited.put(current, max+1);
				s.remove(current);
			}
		}

		// for each node in scc, assign its rank
		visited.remove(virtualvertex);
		HashMap<String, Integer> toprank = new HashMap<String, Integer>();
		for(String hv:visited.keySet()){
			String[] scc = hv.split("_");
			int rank = visited.get(hv);
			if(scc.length>1){
				for(String v:scc){
					toprank.put(v, rank);
				}
			}
			else
				toprank.put(hv, rank);
		}
		return toprank;
	}
	

	
}
