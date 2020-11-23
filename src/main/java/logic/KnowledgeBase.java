package logic;

import java.util.ArrayList;
import java.util.TreeMap;

public class KnowledgeBase {
    TreeMap<Point, PerceptionNode[]> breeze_nodes = new TreeMap<>();
    TreeMap<Point, ResultNode[]> pit_nodes = new TreeMap<>();
    TreeMap<Point, PerceptionNode[]> stench_nodes = new TreeMap<>();
    TreeMap<Point, ResultNode[]> wumpus_nodes = new TreeMap<>();

    public KnowledgeBase(int w, int h){
        for(int i=0; i<w; ++i){
            for(int j=0; j<h; ++j){
                Point point = new Point(i, j);
                PerceptionNode[] point_breeze_nodes = new PerceptionNode[2];
                ResultNode[] point_pit_nodes = new ResultNode[2];
                PerceptionNode[] point_stench_nodes = new PerceptionNode[2];
                ResultNode[] point_wumpus_nodes = new ResultNode[2];
                for(int k=0; k<2; ++k){
                    point_breeze_nodes[k] = new PerceptionNode(point, k==0);
                    point_stench_nodes[k] = new PerceptionNode(point, k==0);
                    point_pit_nodes[k] = new ResultNode(point, k==0);
                    point_wumpus_nodes[k] = new ResultNode(point, k==0);
                }
                breeze_nodes.put(point, point_breeze_nodes);
                pit_nodes.put(point, point_pit_nodes);
                stench_nodes.put(point, point_stench_nodes);
                wumpus_nodes.put(point, point_wumpus_nodes);
            }
        }
        for(int i=0; i<w; ++i){
            for(int j=0; j<h; ++j){
                Point point = new Point(i, j);
                ArrayList<Point> neighbours = getNeighbours(point,w,h);
                PerceptionNode[] p_and_list = new PerceptionNode[neighbours.size()];
                for(int k=0; k<neighbours.size(); ++k){
                    p_and_list[k]=(breeze_nodes.get(neighbours.get(k))[0]);
                    breeze_nodes.get(neighbours.get(k))[0].targets.add(pit_nodes.get(point)[0]);
                    pit_nodes.get(point)[1].clauses.add(new PerceptionNode[]{breeze_nodes.get(neighbours.get(k))[1]});
                    breeze_nodes.get(neighbours.get(k))[1].targets.add(pit_nodes.get(point)[1]);

                    wumpus_nodes.get(point)[1].clauses.add(new PerceptionNode[]{stench_nodes.get(neighbours.get(k))[1]});
                    stench_nodes.get(neighbours.get(k))[1].targets.add(wumpus_nodes.get(point)[1]);

                    ArrayList<Point> neighbours_of_neighbour = getNeighbours(neighbours.get(k),w,h);
                    neighbours_of_neighbour.remove(point);
                    Node[] clauses = new Node[neighbours_of_neighbour.size()+1];
                    clauses[0] = stench_nodes.get(neighbours.get(k))[0];
                    stench_nodes.get(neighbours.get(k))[0].targets.add(wumpus_nodes.get(point)[0]);
                    for(int n_o_n_i = 0; n_o_n_i<neighbours_of_neighbour.size(); ++n_o_n_i){
                        clauses[n_o_n_i+1] = wumpus_nodes.get(neighbours_of_neighbour.get(n_o_n_i))[1];
                    }
                    wumpus_nodes.get(point)[0].clauses.add(clauses);
                }
                pit_nodes.get(point)[0].clauses.add(p_and_list);

                for(int i_=0; i_<w; ++i_) {
                    for (int j_ = 0; j_ < h; ++j_) {
                        Point p_ = new Point(i_, j_);
                        if(!point.equals(p_) && !neighbours.contains(p_)){
                            stench_nodes.get(point)[0].targets.add(wumpus_nodes.get(p_)[1]);
                            wumpus_nodes.get(p_)[1].clauses.add(new PerceptionNode[]{stench_nodes.get(point)[0]});
                        }
                    }
                }
//                wumpus_nodes.get(point)[0].clauses.add(w_and_list);
            }
        }
        pit_nodes.get(new Point(0,3))[0].setValue(false);
        pit_nodes.get(new Point(0,3))[1].setValue(true);
        wumpus_nodes.get(new Point(0,3))[0].setValue(false);
        wumpus_nodes.get(new Point(0,3))[1].setValue(true);
    }

    private ArrayList<Point> getNeighbours(Point point, int w, int h) {
        ArrayList<Point> res = new ArrayList<>();
        if(point.x>0) res.add(new Point(point.x-1, point.y));
        if(point.x<w-1) res.add(new Point(point.x+1, point.y));
        if(point.y>0) res.add(new Point(point.x, point.y-1));
        if(point.y<h-1) res.add(new Point(point.x, point.y+1));
        return res;
    }

    public ArrayList<ResultNode>[] getCurrentState(Point pos, boolean feelsStench, boolean feelsBreeze, boolean feelsScream){
        return new ArrayList[]{
                updatePerception(pos, feelsBreeze, breeze_nodes, pit_nodes),
                updatePerception(pos, feelsStench, stench_nodes, wumpus_nodes)
        };
    }

    private ArrayList<ResultNode> updatePerception(
            Point pos, boolean feelsBreeze, TreeMap<Point, PerceptionNode[]> perception_nodes, TreeMap<Point, ResultNode[]> result_nodes) {
        ArrayList<ResultNode> updated = new ArrayList<>();
        int node_i = (feelsBreeze) ? 0 : 1;
    	PerceptionNode node = perception_nodes.get(pos)[node_i];
		if(!node.isEvaluated){
			node.setValue(true);
            perception_nodes.get(pos)[(node_i+1)%2].setValue(false);
			for(int target_id = 0; target_id < node.targets.size(); ++target_id){
                ResultNode target = node.targets.get(target_id);
			    if(!target.isEvaluated){
                    Boolean value = target.evaluate();
                    if(value!=null){
                        target.setValue(value);
                        result_nodes.get(target.getCoords())[(node_i+1)%2].setValue(!value);
                        updated.add((feelsBreeze) ? target : result_nodes.get(target.getCoords())[(node_i+1)%2]);
                    }
                }

			}
		}
		return updated;
    }
}
