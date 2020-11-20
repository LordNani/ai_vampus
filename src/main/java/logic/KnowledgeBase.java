package logic;

import java.util.ArrayList;
import java.util.HashMap;

public class KnowledgeBase {
    HashMap<Point, PerceptionNode[]> breeze_nodes = new HashMap<>();
    HashMap<Point, ResultNode[]> pit_nodes = new HashMap<>();
    HashMap<Point, PerceptionNode[]> stench_nodes = new HashMap<>();
    HashMap<Point, ResultNode[]> wumpus_nodes = new HashMap<>();

    public KnowledgeBase(int w, int h){
        for(int i=0; i<w; ++i){
            for(int j=0; j<h; ++j){
                Point point = new Point(i, j);
                PerceptionNode[] point_breeze_nodes = new PerceptionNode[2];
                ResultNode[] point_pit_nodes = new ResultNode[2];
                PerceptionNode[] point_stench_nodes = new PerceptionNode[2];
                ResultNode[] point_wumpus_nodes = new ResultNode[2];
                for(int k=0; k<2; ++k){
                    point_breeze_nodes[k] = new PerceptionNode(point);
                    point_stench_nodes[k] = new PerceptionNode(point);
                    point_pit_nodes[k] = new ResultNode(point);
                    point_wumpus_nodes[k] = new ResultNode(point);
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
                PerceptionNode[] w_and_list = new PerceptionNode[neighbours.size()];
                for(int k=0; k<neighbours.size(); ++k){
                    p_and_list[k]=(breeze_nodes.get(neighbours.get(k))[0]);
                    breeze_nodes.get(neighbours.get(k))[0].targets.add(pit_nodes.get(point)[0]);
                    pit_nodes.get(point)[1].clauses.add(new PerceptionNode[]{breeze_nodes.get(neighbours.get(k))[1]});
                    breeze_nodes.get(neighbours.get(k))[1].targets.add(pit_nodes.get(point)[1]);

                    w_and_list[k]=(stench_nodes.get(neighbours.get(k))[0]);
                    stench_nodes.get(neighbours.get(k))[0].targets.add(wumpus_nodes.get(point)[0]);
                    wumpus_nodes.get(point)[1].clauses.add(new PerceptionNode[]{stench_nodes.get(neighbours.get(k))[1]});
                    stench_nodes.get(neighbours.get(k))[1].targets.add(wumpus_nodes.get(point)[1]);
                }
                pit_nodes.get(point)[0].clauses.add(p_and_list);
                wumpus_nodes.get(point)[0].clauses.add(w_and_list);
            }
        }
    }

    private ArrayList<Point> getNeighbours(Point point, int w, int h) {
        ArrayList<Point> res = new ArrayList<>();
        if(point.x>0) res.add(new Point(point.x-1, point.y));
        if(point.x<w-1) res.add(new Point(point.x+1, point.y));
        if(point.y>0) res.add(new Point(point.x, point.y-1));
        if(point.y<h-1) res.add(new Point(point.x, point.y+1));
        return res;
    }

    public ArrayList<ResultNode> getCurrentState(Point pos, boolean feelsStench, boolean feelsBreeze, boolean feelsScream){
        return null;
    }
}
