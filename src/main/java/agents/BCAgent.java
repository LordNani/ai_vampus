package agents;


import logic.KnowledgeBase;
import logic.Point;
import logic.ResultNode;
import wumpus.Agent;
import wumpus.Environment;
import wumpus.Player;

import java.util.*;

public class BCAgent implements Agent {
    private int w, h;
    private boolean debug = true;

    private boolean isWumpusAlive = false;
    private boolean hasGold = false;
    LinkedList<Point> unvisitedSafeTiles = new LinkedList<>();
	LinkedList<Point> visitedTiles = new LinkedList<>();
	LinkedList<Point> dangerousTiles = new LinkedList<>();
	Point current_loc;
	Player.Direction current_direction;

//    private boolean[][] visited;
    private int[][] hasWumpus;
    private int[][] hasPit;
    /*
    0 - unknown
    1 - false
    2 - true
     */
    private LinkedList<Environment.Action> nextActions = new LinkedList<Environment.Action>();
    private KnowledgeBase kb;
	private boolean wumpus_found = false;
	private Point wumpus_location;

	LinkedList<Point> current_path = new LinkedList<>();
	TreeMap<Point, LinkedList<Point>> shortest_ways = new TreeMap<>();

	public BCAgent(int w, int h) {
        this.w = w;
        this.h = h;
        hasPit = new int[w][h];
        hasWumpus = new int[w][h];
        kb = new KnowledgeBase(w,h);
        visitedTiles.add(new Point(0,3));
    }

    @Override
    public Environment.Action getAction(Player player) {
        int x = player.getX();
        int y = player.getY();
        current_loc = new Point(x,y);
        current_direction = player.getDirection();
		current_path.add(current_loc.clone());
		updateShortestWays(current_loc, current_path);

        // Grab the gold if senses glitter
        if (player.hasGlitter()) return Environment.Action.GRAB;

        boolean stenching = player.hasStench();
        boolean breezing = player.hasBreeze();
        boolean screaming = player.hasScream();

        // Apply actions pools
        if (nextActions.size() > 0) {
            return nextActions.poll();
        }

        ArrayList<ResultNode>[] info =
                kb.getCurrentState(current_loc, stenching, breezing, screaming );
        for(ResultNode pit_tile : info[0]){
        	hasPit[pit_tile.getCoords().x][pit_tile.getCoords().y]=(pit_tile.getValue()) ? 2 : 1;
		}
		for(ResultNode wumpus_tile : info[1]){
			hasWumpus[wumpus_tile.getCoords().x][wumpus_tile.getCoords().y]=(wumpus_tile.getValue()) ? 2 : 1;
			if(wumpus_tile.getValue()){
				wumpus_found=true;
				wumpus_location = wumpus_tile.getCoords();
			}
		}
		for(int i=0; i<2; ++i){
			for(ResultNode tile : info[i]){
				if(!unvisitedSafeTiles.contains(tile.getCoords()) && !dangerousTiles.contains(tile.getCoords()) && !visitedTiles.contains(tile.getCoords())){
					if(hasPit[tile.getCoords().x][tile.getCoords().y]==1 && hasWumpus[tile.getCoords().x][tile.getCoords().y]==1){
						unvisitedSafeTiles.add(tile.getCoords());
						LinkedList<Point> path_copy = (LinkedList<Point>) current_path.clone();
						path_copy.addLast(tile.getCoords());
						updateShortestWays(tile.getCoords(), path_copy);
					}
					else{
						dangerousTiles.add(tile.getCoords());
					}
				}
			}
		}

		if(unvisitedSafeTiles.isEmpty()){
			nextActions = createActionPathTo(new Point(0,3));
		}
		else{
			Point nextVertex = unvisitedSafeTiles.pollFirst();
			visitedTiles.add(nextVertex);
			nextActions = createActionPathTo(nextVertex);
		}
		return nextActions.pollFirst();
    }

	private void updateShortestWays(Point point, LinkedList<Point> path) {
		if(!shortest_ways.containsKey(point) || shortest_ways.get(point).size() > path.size()){
			shortest_ways.put(point, path);
		}
	}

	private LinkedList<Environment.Action> createActionPathTo(Point nextVertex) {
		LinkedList<Point> planned_path = createPathTo(nextVertex);
		LinkedList<Environment.Action> result = new LinkedList<>();

		int curr_dir = current_direction.ordinal();
		for(Point p : planned_path){
			if(!p.equals(current_loc)){
				int next_direction = current_loc.directionTo(p);
				if(next_direction == curr_dir+1){
					curr_dir = (curr_dir+1)%4;
					result.add(Environment.Action.TURN_RIGHT);
				}
				else if(next_direction+1 == curr_dir){
					curr_dir = (curr_dir+3)%4;
					result.add(Environment.Action.TURN_LEFT);
				}
				else{
					result.add(Environment.Action.TURN_LEFT);
					result.add(Environment.Action.TURN_LEFT);
					curr_dir = (curr_dir+2)%4;
					System.out.println("Double turn");
				}
				result.add(Environment.Action.GO_FORWARD);
			}
		}
		return result;
	}

	private LinkedList<Point> createPathTo(Point nextVertex) {
		LinkedList<Point> res_path = new LinkedList<>();
		// Need to optimize later
		int first_different_index = Math.min(shortest_ways.get(current_loc).size(), shortest_ways.get(nextVertex).size());
		for (int i = 0; i < first_different_index; ++i) {
			if (shortest_ways.get(current_loc).get(i) != shortest_ways.get(nextVertex).get(i)) {
				first_different_index = i;
				break;
			}
		}
		for (int j = first_different_index; j < shortest_ways.get(current_loc).size(); ++j){
			res_path.addFirst(shortest_ways.get(current_loc).get(j));
		}
		if(first_different_index != 0) res_path.addLast(shortest_ways.get(current_loc).get(first_different_index-1));
		for (int j = first_different_index; j < shortest_ways.get(nextVertex).size(); ++j) {
			res_path.addLast(shortest_ways.get(nextVertex).get(j));
		}
		return res_path;
	}

	@Override
    public void beforeAction(Player player) {
        if (debug) {
            System.out.println(player.render());
            System.out.println(player.debug());
        }
    }

    @Override
    public void afterAction(Player player) {
        if (debug) {
            System.out.println(player.render());
            System.out.println(player.debug());
        }
    }
}
