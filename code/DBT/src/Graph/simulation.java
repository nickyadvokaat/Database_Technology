package Graph;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import fileOpe.readfile;
import Graph.edge;
import Graph.cg_graph;


/**
 * this class computes simulation query between a pattern and a data graph
 * @author jiangxianlin
 *
 */
public class simulation {

	public HashMap<String, HashSet<String>> sim;			//	node -> match nodes
	public HashMap<String, HashSet<String>> premv;		//	node -> can not match parents
	private HashMap<String, HashSet<String>> pre;			//	node -> parents
	private HashMap<String, HashSet<String>> suc;		//	node -> children
	private HashSet<String> PSET;									//	set of all parent nodes

	
	/**
	 * Constructor
	 */
	public simulation(){
		this.sim = new HashMap<String, HashSet<String>>();		//	node -> match nodes
		this.premv = new HashMap<String, HashSet<String>>();	//	node -> can not match parents
		this.pre = new HashMap<String, HashSet<String>>();			//	node -> parents
		this.suc = new HashMap<String, HashSet<String>>();			//	node -> children
		this.PSET = new HashSet<String>();									//	set of all parent nodes
	}
	
	
	/**
	 * This procedure initialises the data structures which are used by EfficientSimilarity
	 * @param G
	 */
	public void initidx(cg_graph G){
		for(String v:G.vertexSet()){
			HashSet<String> pset = new HashSet<String>();	//	initialise pset
			for(edge e: G.incomingEdgesOf(v)){
				pset.add(G.getEdgeSource(e));
			}
			this.pre.put(v, pset);
			
			if(pset.size()>0){
				PSET.addAll(pset);
			}
			
			HashSet<String> cset = new HashSet<String>();	//	initialise cset
			for(edge e: G.outgoingEdgesOf(v)){
				cset.add(G.getEdgeTarget(e));
			}
			this.suc.put(v, cset);
		}
	}	

	/**
	 * @param P : pattern 
	 * @param G : graph
	 */
	public void EfficientSimilarity(cg_graph P, HashMap<String, String> attrP, cg_graph G, HashMap<String, String> attrG){
		// initialise sim set.
		for(String u:P.vertexSet()){

			HashSet<String> posmat = new HashSet<String>();			//	a node set which contains nodes that possibly match parents of v 
			HashSet<String> remove = new HashSet<String>();		//	a node set which contains nodes that can not match any parent node of v
			remove.addAll(this.PSET);

			HashSet<String> simset = this.sim.get(u);			//	sim(u)
			if(simset==null){
				simset = new HashSet<String>();					//	initialise simset
			}
			
			String lu = attrP.get(u);		//	label of u
			if(P.outDegreeOf(u)==0){
				for(String v:G.vertexSet()){
					String lv = attrG.get(v);
					if(lu.equals(lv)){
						simset.add(v);
						if(!this.pre.get(v).isEmpty()){
							posmat.addAll(this.pre.get(v));							
						}
					}
				}
			}
			else{
				for(String v:G.vertexSet()){
					String lv = attrG.get(v);
					if(lu.equals(lv) && G.outDegreeOf(v)!=0){
						simset.add(v);
						if(!this.pre.get(v).isEmpty()){
							posmat.addAll(this.pre.get(v));							
						}
					}
				}
			}
			this.sim.put(u, simset);
			remove.removeAll(posmat);
			this.premv.put(u, remove);
		}
		
		Queue<String> q = new LinkedList<String>();
		for(String n:this.premv.keySet()){
			HashSet<String> hs = this.premv.get(n);
			if(!hs.isEmpty()){
				q.add(n);
			}
		}
		
		while(!q.isEmpty()){
			String n = q.poll();
			for(edge e:P.incomingEdgesOf(n)){
				String u = P.getEdgeSource(e);
				HashSet<String> sim = this.sim.get(u);
				for(String w:this.premv.get(n)){
					if(sim.contains(w)){
						sim.remove(w);		// w in G can not match u in P
						for(edge ee:G.incomingEdgesOf(w)){
							String ww = G.getEdgeSource(ee);
							HashSet<String> cset = new HashSet<String>(); 
							cset.addAll(this.suc.get(ww));
							cset.retainAll(sim);
							if(cset.isEmpty()){
								this.premv.get(u).add(ww);
								if(!q.contains(u)){
									q.add(u);
								}
							}
						}
					}
				}
			}
			this.premv.get(n).clear();
		}
	}
}
