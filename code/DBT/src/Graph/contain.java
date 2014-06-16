package Graph;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Vector;
import fileOpe.readfile;
import fileOpe.writefile;
import Graph.simulation;
import Graph.cg_graph;
import Graph.edge;
import Graph.ranGraph;
import Graph.ranGraph.grpattr;

/**
 * this class is used by the procedure simTran, by returning two results
 * @author s0944873
 *
 */
class partial{
	HashMap<edge, HashSet<edge>> ans;
	HashMap<edge, edge> reverseMap;
}

public class contain {

	public cg_graph P;										//	pattern P
	public HashMap<String, String> attrP;		//	attribute of P
	public Vector<cg_graph> Qset; 							//	pattern set
	public Vector<HashMap<String, String>> attrQSet;		//	attribute set of the corresponding patterns
	public Vector<String> sortedSet;									//	sorted node set with topological rank in ascending order
	public Vector<HashSet<edge>> MGSet;						//	a set of match graphs
	public Vector<HashMap<edge, HashSet<edge>>> RSimSet;	//	a set of simulation result (edge in P maps to edge set in G )
	public Vector<HashMap<edge, edge>> reverseMap;				//	edge in G maps to edge in P , reversal mapping of RSimSet
	public Vector<HashMap<edge, HashSet<edge>>> View;		//	a set of views of the typical patterns
	public HashMap<String, String> attrG;					//	node label of the data graph
	public Vector<Integer> contain;							//	subset of Q which contains P
	public double containtime;	 								//	time for containment checking



	/**
	 * Constructor
	 */
	public contain(){
		this.Qset = new Vector<cg_graph>();
		this.attrQSet = new Vector<HashMap<String, String>>();
		this.sortedSet = new Vector<String>();
		this.MGSet = new Vector<HashSet<edge>>();
		this.RSimSet = new Vector<HashMap<edge, HashSet<edge>>>();
		this.reverseMap = new Vector<HashMap<edge, edge>>();
		this.View = new Vector<HashMap<edge, HashSet<edge>>>();
		this.contain = new Vector<Integer>();
		this.containtime = 0;
	}


	/** 
	 * This procedure outputs match graph of P in G.
	 * @param P : pattern graph
	 * @param G : data graph
	 * @param sim : match results of P in G
	 * @return eset : a set of edges
	 */
	public HashSet<edge> findMatG(cg_graph P, cg_graph G, HashMap<String, HashSet<String>> sim){
		HashSet<edge> eSet = new HashSet<edge>();

		for(edge e:P.edgeSet()){
			String fu = (String) e.getSource();
			String tu = (String) e.getTarget();

			HashSet<String> simfu = sim.get(fu);
			HashSet<String> simtu = sim.get(tu);

			if(simfu==null || simtu==null){
				eSet.clear();
				break;
			}

			for(String fv:simfu){
				for(String tv:simtu){
					if(G.containsEdge(fv, tv)){
						eSet.add(e);
					}
				}
			}
		}
		return eSet;
	}

	/** 
	 * This procedure transforms the simulation results from node mapping to edge mapping
	 * @param P : pattern graph
	 * @param G : data graph
	 * @param sim : match results of P in G
	 * @return eset : a set of edges
	 */
	public partial simTran(cg_graph P, cg_graph G, HashMap<String, HashSet<String>> sim){
		partial part = new partial();
		HashMap<edge, HashSet<edge>> ans = new HashMap<edge, HashSet<edge>>();
		HashMap<edge, edge> reverseMap = new HashMap<edge, edge>();
		for(edge e:P.edgeSet()){
			HashSet<edge> eSet = new HashSet<edge>();
			String fu = (String) e.getSource();
			String tu = (String) e.getTarget();

			HashSet<String> simfu = sim.get(fu);
			HashSet<String> simtu = sim.get(tu);

			if(simfu.isEmpty() || simtu.isEmpty()){
				eSet.clear();
				ans.clear();
				break;
			}

			for(String fv:simfu){
				for(String tv:simtu){
					edge eg = G.getEdge(fv, tv);
					if(eg!=null){
						eSet.add(eg);
						reverseMap.put(eg, e);
					}
				}
			}
			ans.put(e, eSet);
		}
		part.ans = ans;
		part.reverseMap = reverseMap;
		return part;
	}


	/**
	 * this procedure computes containment between pattern P and a set of patterns Q
	 * @return : true if P is contained by Q, otherwise false
	 */
	public boolean containCheck(){
		double start = System.currentTimeMillis();
		boolean ans = false;
		HashSet<edge> eSet = new HashSet<edge>();
		int counter = this.Qset.size();
		simulation sim = new simulation();
		sim.initidx(this.P);
		for(int i=0; i<counter; i++){
			sim.EfficientSimilarity(this.Qset.get(i), this.attrQSet.get(i), this.P, this.attrP);
			partial part = this.simTran(this.Qset.get(i), this.P, sim.sim);
			HashMap<edge, HashSet<edge>> MG = part.ans;
			HashSet<edge> MGEdge = new HashSet<edge>();
			if(!MG.isEmpty()){
				for(edge e:MG.keySet()){
					HashSet<edge> eset = MG.get(e);
					if(!eset.isEmpty()){
						MGEdge.addAll(eset);
					}
				}
				this.contain.add(i);
			}
			sim.sim.clear();
			sim.premv.clear();
			if(!MGEdge.isEmpty()){
				eSet.addAll(MGEdge);
			}
			this.MGSet.add(MGEdge);			//	initialise match graph set
			this.RSimSet.add(MG);			//	initialise simulation result set
			this.reverseMap.add(part.reverseMap);
		}

		if(eSet.containsAll(this.P.edgeSet())){
			ans = true;
		}
		double end = System.currentTimeMillis();
		System.out.println(end-start);
		this.containtime = (end-start);
		return ans;
	}


	/**
	 * this procedure finds a minimum set of patterns which can contain P
	 * @param sim
	 * @param P
	 * @param attrP
	 * @param Q
	 * @param attrGSet
	 * @return : ids of the typical patterns in minimum cover
	 */
	public Vector<Integer> minContain(){
		HashSet<edge> tmp = new HashSet<edge>();		// current match graph
		HashSet<edge> rem = new HashSet<edge>();		// uncovered edge set		
		Vector<Integer> ans = new Vector<Integer>();		// ids of the patterns in minimum cover 

		rem.addAll(this.P.edgeSet());
		int max = 0;
		while(!rem.isEmpty()){
			max = 0;
			HashSet<edge> pos = null;				//	represent the best choice
			for(HashSet<edge> eset:this.MGSet){
				if(!eset.isEmpty()){
					tmp.addAll(eset);
					tmp.retainAll(rem);				//	intersection between current match graph and uncovered edge set

					if(max<tmp.size()){
						max = tmp.size();
						pos =  eset;						
					}
					tmp.clear();
				}
			}
			if(pos!=null){
				rem.removeAll(pos);
				ans.add(this.MGSet.indexOf(pos));
			}
			tmp.clear();
		}
		return ans;
	}


	/**
	 * this procedure sorts the node in ascending order(bottom-up) of topological rank
	 * @param toprank
	 */
	public Vector<String> ranksort(HashMap<String, Integer> toprank){
		Vector<String> sortedSet = new Vector<String>();
		for(String s1:toprank.keySet()){
			int v1 = toprank.get(s1);
			int pos = 0;
			for(String s2:sortedSet){
				int v2 = toprank.get(s2);
				if(v2>v1){
					break;
				}
				pos++;
			}
			sortedSet.add(pos, s1);
		}
		return sortedSet;
	}



	/**
	 * this procedure iteratively propagates changes until reaches fix point 
	 * @param ESET : removed edge in G maps to edge in P
	 * @param ans : edge in P maps to a set of edges in G
	 * @param union : edge set of match graph of P1 union match graph of P2
	 */
	public HashMap<edge, HashSet<edge>> propogate(HashMap<edge, edge> ESET, HashMap<edge, HashSet<edge>> ans, HashSet<edge> union){
		Queue<edge> eset = new LinkedList<edge>();
		eset.addAll(ESET.keySet());

		while(!eset.isEmpty()){
			edge eg = eset.poll();
			edge ep = ESET.get(eg);
			HashSet<edge> match = ans.get(ep);
			String fv = (String) eg.getSource(); 
			boolean flag = false;			//	represent whether fv is still a valid match
			for(edge e:match){
				String fvv = (String) e.getSource();
				if(fv.equals(fvv)){
					flag = true;
					break;
				}
			}
			if(!flag){
				String fu = (String) ep.getSource();
				for(edge eup:this.P.incomingEdgesOf(fu)){
					if(union.contains(eup)){
						HashSet<edge> mset = ans.get(eup);
						for(edge emat:mset){
							String fv1 = (String) emat.getTarget(); 
							if(fv1.equals(fv)){
								mset.remove(emat);
								eset.add(emat);
							}
						}
					}
				}
			}
		}
		return ans;
	}


	/**
	 * this procedure computes joins of two views 
	 * @param current: current match result
	 * @param p2: index of pattern P2
	 * @param toprank: topological rank of Pattern P
	 * @return : the join of two views
	 */
	public HashMap<edge, HashSet<edge>> rmvRdt(HashMap<edge, HashSet<edge>> current, int p2, HashMap<String, Integer> toprank){
		HashMap<edge, edge> ESET = new HashMap<edge, edge>();			//	maintain redundant edges : edge in G maps to edge in P
		HashSet<edge> union = new HashSet<edge>();						//	maintain the edge set of current Union P2
		union.addAll(current.keySet());
		union.addAll(this.MGSet.get(p2));

		// sort the edges in ascending order with topological rank of target node
		Vector<edge> sorted = new Vector<edge>();								//	ordered edge set of current U P2
		for(edge e:union){
			String tv = (String) e.getTarget();
			int order = toprank.get(tv);
			int pos = 0;
			for(edge e1:sorted){
				String tv1 = (String) e1.getTarget();
				int order1 = toprank.get(tv1);
				if(order1>order){
					break;
				}
				pos++;
			}
			sorted.add(pos,e);
		}


		//	1. initialise match results of P1 X P2
		HashMap<edge, HashSet<edge>> ans = new HashMap<edge, HashSet<edge>>();		//	variable as return result
		for(edge e:sorted){
			edge egp2 = this.reverseMap.get(p2).get(e);

			HashSet<edge> match = new HashSet<edge>();
			if(!current.keySet().contains(e)){
				match.addAll(this.View.get(p2).get(egp2));				
			}
			else if(egp2==null){
				match.addAll(current.get(e));
			}
			else{
				HashSet<edge> tmp = new HashSet<edge>();
				match.addAll(current.get(e));
				match.retainAll(this.View.get(p2).get(egp2));
				tmp.addAll(current.get(e));
				tmp.addAll(this.View.get(p2).get(egp2));
				tmp.removeAll(match);

				for(edge ee:tmp){
					ESET.put(ee, e);
				}
			}
			ans.put(e, match);
		}

		//	2. remove redundant edges 
		HashSet<edge> tmp = new HashSet<edge>();
		for(edge e:sorted){
			HashSet<edge> match = ans.get(e);
			String u1 = (String) e.getSource();
			String lu1 = this.attrP.get(u1);
			for(edge matchedge:match){
				for(edge ee:this.P.outgoingEdgesOf(u1)){
					if(union.contains(ee)){
						HashSet<edge> match1 = ans.get(ee);
						boolean flag = false;
						for(edge e1:match1){
							String v1 = (String) e1.getSource();
							String lv1 = this.attrG.get(v1);
							if(lu1.equals(lv1)){
								flag = true;
								break;
							}
						}
						if(!flag){
							tmp.add(matchedge);
						}
					}
				}
			}
			match.removeAll(tmp);
			for(edge ee:tmp){
				ESET.put(ee, e);
			}
			tmp.clear();
		}
		return this.propogate(ESET, ans, union);
	}


	/**
	 * this procedure sorts relative topological rank for a set of patterns
	 * @param mincontain : indexes of the patterns minimum
	 * @param toprank : topological rank of Pattern P
	 */
	public HashMap<Integer, Integer> relTopRank(Vector<Integer> mincontain, HashMap<String, Integer> toprank){
		HashMap<Integer, Integer> relativetoprank = new HashMap<Integer, Integer>();
		for(Integer num:mincontain){
			int min = Integer.MAX_VALUE;
			for(edge e:this.MGSet.get(num)){
				String tv = (String) e.getTarget(); 
				int rank = toprank.get(tv);
				if(rank<min){
					min = rank;
				}
			}
			relativetoprank.put(num, min);
		}
		return relativetoprank;
	}


	/**
	 * this procedure computes query by using views
	 * @param relTopRank : index of pattern maps to its relative topological rank
	 * @param toprank : topological rank of pattern P
	 */
	public HashMap<edge, HashSet<edge>> queryView(HashMap<Integer, Integer> relTopRank, HashMap<String, Integer> toprank){
		HashMap<edge, HashSet<edge>> ans = new HashMap<edge, HashSet<edge>>();
		// sort the patterns with topological rank in ascending order
		Vector<Integer> rank = new Vector<Integer>();		// pattern index in ascending order
		for(int p:relTopRank.keySet()){
			int rk = relTopRank.get(p);
			int pos = 0;
			for(int p1:rank){
				int rk1 = relTopRank.get(p1);
				if(rk>rk1){
					break;
				}
				pos++;
			}
			rank.add(pos, p);
		}

		for(int pIndex:rank){
			ans = this.rmvRdt(ans, pIndex, toprank);
		}
		return ans;
	}

	/**
	 * this procedure is used to generate sample patterns
	 */	
	public void initlise(){
		cg_graph p = new cg_graph();
		HashMap<String, String> attr = new HashMap<String, String>();
		p.addVertex("0");
		attr.put("0", "A");
		p.addVertex("1");
		attr.put("1", "E");
		p.addVertex("2");
		attr.put("2", "D");
		p.addVertex("3");
		attr.put("3", "C");
		edge e1 = new edge("2","0");
		p.addEdge("2", "0", e1);
		edge e2 = new edge("1","3");
		p.addEdge("1", "3", e2);
		edge e3 = new edge("2","1");
		p.addEdge("2", "1", e3);
		edge e4 = new edge("1","2");
		p.addEdge("1", "2", e4);
		edge e5 = new edge("3","2");
		p.addEdge("3", "2", e5);
		writefile wf = new writefile();
		wf.writeobject("/disk/scratch/dataset/samplePattern/P4-5-1.grp", p);
		wf.writeobject("/disk/scratch/dataset/samplePattern/P4-5-1.atr", attr);
	}
