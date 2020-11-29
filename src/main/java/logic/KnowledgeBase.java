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
                // PerceptionNode[], ResultNode[] has size 2:
                // [0] - positive node
                // [1] - negative
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
                // Set conditions foe result nodes at (i,j)
                Point point = new Point(i, j);
                // Get neighbour points of (i,j) = [(i+1,j), (i-1,j), (i, j+1), (i, j-1)] within restrictions of map size
                ArrayList<Point> neighbours = getNeighbours(point,w,h);
                // Create array of conditions to fulfil "There's pit on (i,j)" for AND operator
                PerceptionNode[] p_and_list = new PerceptionNode[neighbours.size()];
                for(int k=0; k<neighbours.size(); ++k){
                    p_and_list[k]=(breeze_nodes.get(neighbours.get(k))[0]);
                    breeze_nodes.get(neighbours.get(k))[0].targets.add(pit_nodes.get(point)[0]);
                    // Linked "There's NO breeze on neighbour to (i,j) tile" and "There's NO pit on (i,j)"
                    pit_nodes.get(point)[1].clauses.add(new PerceptionNode[]{breeze_nodes.get(neighbours.get(k))[1]});
                    breeze_nodes.get(neighbours.get(k))[1].targets.add(pit_nodes.get(point)[1]);

                    //Wumpus part
                    // Linked "There's NO stench on neighbour to (i,j) tile" and "There's NO wumpus on (i,j)"
                    wumpus_nodes.get(point)[1].clauses.add(new PerceptionNode[]{stench_nodes.get(neighbours.get(k))[1]});
                    stench_nodes.get(neighbours.get(k))[1].targets.add(wumpus_nodes.get(point)[1]);

                    // Create list of neighbour tiles to neighbour tile
                    ArrayList<Point> neighbours_of_neighbour = getNeighbours(neighbours.get(k),w,h);
                    // Exclude point (i, j) from it
                    neighbours_of_neighbour.remove(point);
                    // Create array of conditions to fulfil "There's wumpus on (i,j)" for AND operator
                    Node[] clauses = new Node[neighbours_of_neighbour.size()+1];
                    // First condition - "There's stench on THIS neighbour tile"
                    clauses[0] = stench_nodes.get(neighbours.get(k))[0];
                    stench_nodes.get(neighbours.get(k))[0].targets.add(wumpus_nodes.get(point)[0]);
                    // All other conditions mean: "There's NO wumpus on neighbour of THIS neighbour"
                    for(int n_o_n_i = 0; n_o_n_i<neighbours_of_neighbour.size(); ++n_o_n_i){
                        clauses[n_o_n_i+1] = wumpus_nodes.get(neighbours_of_neighbour.get(n_o_n_i))[1];
                    }
                    wumpus_nodes.get(point)[0].clauses.add(clauses);

                    Node[] clauses_ = new Node[neighbours_of_neighbour.size()+1];
                    clauses_[0] = breeze_nodes.get(neighbours.get(k))[0];
//                    breeze_nodes.get(neighbours.get(k))[0].targets.add(pit_nodes.get(point)[0]);
                    for(int n_o_n_i = 0; n_o_n_i<neighbours_of_neighbour.size(); ++n_o_n_i){
                        clauses_[n_o_n_i+1] = pit_nodes.get(neighbours_of_neighbour.get(n_o_n_i))[1];
                    }
                    pit_nodes.get(point)[0].clauses.add(clauses_);
                }
                // Add array of conditions to fulfil "There's pit on (i,j)" for AND operator, as one of possible ways to prove "There's pit on (i,j)"
                pit_nodes.get(point)[0].clauses.add(p_and_list);

                ArrayList<ResultNode> all_other_points = new ArrayList<>(15);
                for(int i_=0; i_<w; ++i_) {
                    for (int j_ = 0; j_ < h; ++j_) {
                        Point p_ = new Point(i_, j_);
                        if(!point.equals(p_)) {
                            if (!neighbours.contains(p_)){
                                // If at any tile which is not (i, j) and not neighbour of (i,j) stench, "There's NO wumpus at (i,j)"
                                stench_nodes.get(point)[0].targets.add(wumpus_nodes.get(p_)[1]);
                                wumpus_nodes.get(p_)[1].clauses.add(new PerceptionNode[]{stench_nodes.get(point)[0]});
                            } else {
                                // Add link which means "If somewhere not on (i,j) proved that there's NO wumpus, check is there enough proofs that there's wumpus on (i,j)"
                                wumpus_nodes.get(point)[1].targets.add(wumpus_nodes.get(p_)[0]);
                            }
                            all_other_points.add(wumpus_nodes.get(p_)[1]);
                        }
                    }
                }
                Node[] all_other_points_array = new ResultNode[15];
                for(int l=0; l<all_other_points.size() && l<15; ++l){
                    all_other_points_array[l] = all_other_points.get(l);
                }
                wumpus_nodes.get(point)[0].clauses.add(all_other_points_array);
//                wumpus_nodes.get(point)[0].clauses.add(w_and_list);
            }
        }
        // Start point is at (0,3)
        // Statement: "There's no pit or wumpus on start point"
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
            Point pos, boolean feelsPerception, TreeMap<Point, PerceptionNode[]> perception_nodes, TreeMap<Point, ResultNode[]> result_nodes) {
        // Create list for nodes whose values will be proven now
        ArrayList<ResultNode> updated = new ArrayList<>();
        int node_i = (feelsPerception) ? 0 : 1;
        // Get positive or negative node
    	PerceptionNode node = perception_nodes.get(pos)[node_i];
		if(!node.isEvaluated){
			node.setValue(true);
			// Set opposite perception node to false
            perception_nodes.get(pos)[(node_i+1)%2].setValue(false);
            // Create list of all nodes affected by changes
            ArrayList<ResultNode> all_targets = (ArrayList<ResultNode>) node.targets.clone();
            all_targets.addAll(perception_nodes.get(pos)[(node_i+1)%2].targets);
            ArrayList<ResultNode> new_generation = new ArrayList<>();
            do {
                new_generation.clear();
                for(int target_id = 0; target_id < all_targets.size(); ++target_id){
                    ResultNode target = all_targets.get(target_id);
                    if(!target.isEvaluated){
                        Boolean value = target.evaluate();
                        if(value!=null && value.booleanValue()){
                            target.setValue(value);
                            result_nodes.get(target.getCoords())[target.positive ? 1 : 0].setValue(!value);
                            updated.add(result_nodes.get(target.getCoords())[0]);

                            for(ResultNode resultNode : target.targets){
                                if(!new_generation.contains(resultNode)){
                                    new_generation.add(resultNode);
                                }
                            }
                        }
                    }
                }
            }
            while(!new_generation.isEmpty());
		}
		return updated;
    }
}
