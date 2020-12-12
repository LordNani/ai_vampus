package agents;


import logic.KnowledgeBase;
import logic.Point;
import logic.ResultNode;
import logic.pathfinder.MapGraph;
import wumpus.Agent;
import wumpus.Environment;
import wumpus.Player;

import java.util.*;

public class BCAgent implements Agent {
    private int w, h;
    private boolean debug = true;

    LinkedList<Point> unvisitedSafeTiles = new LinkedList<>();
	LinkedList<Point> visitedTiles = new LinkedList<>();
	LinkedList<Point> dangerousTiles = new LinkedList<>();
	Point current_loc;
	Player.Direction current_direction;
    final Point start_point = new Point(0,3);

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
	private boolean wumpus_killed = false;

	LinkedList<Point> current_path = new LinkedList<>();

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

		if(player.hasScream()) wumpus_killed=true;
        // Grab the gold if senses glitter
        if (player.hasGlitter()) return Environment.Action.GRAB;
        if (wumpus_found && !wumpus_killed && wumpus_location.isConnected(current_loc)){
			nextActions = turnAndKillWumpus(current_direction, current_loc, wumpus_location);
			return nextActions.pollFirst();
		}

        boolean stenching = player.hasStench();
        boolean breezing = player.hasBreeze();
        boolean screaming = player.hasScream();

        // Apply actions pools
        if (nextActions.size() > 0) {
//			if((current_loc.equals(new Point(1,3)) && current_direction.equals(Player.Direction.W)
//				||
//				current_loc.equals(new Point(0,2)) && current_direction.equals(Player.Direction.S))
//				&& nextActions.getFirst().equals(Environment.Action.GO_FORWARD)){
//				System.out.println("dd");
//			}
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
				System.out.println("Wumpus found on "+wumpus_location);
			}
		}
		for(int i=0; i<2; ++i){
			for(ResultNode tile : info[i]){
				if(!unvisitedSafeTiles.contains(tile.getCoords()) && !dangerousTiles.contains(tile.getCoords()) && !visitedTiles.contains(tile.getCoords())){
					if(hasPit[tile.getCoords().x][tile.getCoords().y]==1 && hasWumpus[tile.getCoords().x][tile.getCoords().y]==1){
						unvisitedSafeTiles.add(tile.getCoords());
						LinkedList<Point> path_copy = (LinkedList<Point>) current_path.clone();
						path_copy.addLast(tile.getCoords());
					}
					else if(hasPit[tile.getCoords().x][tile.getCoords().y]==2 || hasWumpus[tile.getCoords().x][tile.getCoords().y]==2){
						dangerousTiles.add(tile.getCoords());
					}
				}
			}
		}
        printUnvisitedSafeTiles();
		try {
			if(wumpus_found && !wumpus_killed){
				nextActions = goToWumpus();
				if(nextActions!=null) {
					if(nextActions.isEmpty()) nextActions = turnAndKillWumpus(current_direction, current_loc, wumpus_location);
					return nextActions.pollFirst();
				}
			}
			if(unvisitedSafeTiles.isEmpty()) {
				nextActions = createActionPathTo(new Point(0, 3));
			} else {
				Point nextVertex = unvisitedSafeTiles.pollLast();
				visitedTiles.add(nextVertex);
				nextActions = createActionPathTo(nextVertex);
			}
		}
		catch (NoSuchElementException e){
			nextActions = createActionPathTo(new Point(0, 3));
		}
//		if(nextActions.getFirst().equals(new Point(0,3))){
//			System.out.println("dd");
//		}
		return nextActions.pollFirst();
    }

	private LinkedList<Environment.Action> turnAndKillWumpus(Player.Direction current_direction, Point current_loc, Point wumpus_location) {
		LinkedList<Environment.Action> result = new LinkedList<>();
		int needed_dir = current_loc.directionTo(wumpus_location);
		int current_dir = current_direction.ordinal();
		while (needed_dir != current_dir){
			switch ((4+needed_dir-current_dir)%4){
				case 0: current_dir=needed_dir;
						break;
				case 1: current_dir++;
				        result.addLast(Environment.Action.TURN_RIGHT);
				        break;
				case 2: result.addLast(Environment.Action.TURN_RIGHT);
						result.addLast(Environment.Action.TURN_RIGHT);
						break;
				case 3: current_dir--;
						result.addLast(Environment.Action.TURN_LEFT);

			}
		}
		result.addLast(Environment.Action.SHOOT_ARROW);
		return result;
	}

	private Player.Direction translateToDirection(int directionTo) {
		switch (directionTo){
			case 0: return Player.Direction.N;
			case 1: return Player.Direction.E;
			case 2: return Player.Direction.S;
			default: return Player.Direction.W;
		}
	}

	private LinkedList<Environment.Action> goToWumpus() {
		ArrayList<Point> wumpus_neighbours = new ArrayList<>();
		for(Point point : visitedTiles){
			if(point.isConnected(wumpus_location)) wumpus_neighbours.add(point);
		}
		for(Point point : unvisitedSafeTiles){
			if(point.isConnected(wumpus_location)) wumpus_neighbours.add(point);
		}
		if(wumpus_neighbours.isEmpty()) return null;
		LinkedList<Environment.Action> best = createActionPathTo(wumpus_neighbours.get(0));
		int min_size = best.size();
		for(int i=1; i<wumpus_neighbours.size(); ++i){
			LinkedList<Environment.Action> candidate = createActionPathTo(wumpus_neighbours.get(i));
			if(candidate.size() < min_size){
				min_size = candidate.size();
				best = candidate;
			}
		}

		return best;
	}

	private void printUnvisitedSafeTiles() {
		String str = "";
		for(Point tile : unvisitedSafeTiles){
			str+=tile.toString()+" ";
		}
		System.out.println(str);
	}

	public LinkedList<Environment.Action> createActionPathTo(Point nextVertex) throws NoSuchElementException {
		LinkedList<Point> planned_path = createPathTo(nextVertex);
		LinkedList<Environment.Action> result = new LinkedList<>();

		int curr_dir = current_direction.ordinal();
		Point temp_point = current_loc;
		for(Point p : planned_path){
			if(!p.equals(temp_point)){
				int next_direction = temp_point.directionTo(p);
				if(next_direction == (curr_dir+1)%4){
					curr_dir = (curr_dir+1)%4;
					result.add(Environment.Action.TURN_RIGHT);
				}
				else if(next_direction == (curr_dir+3)%4){
					curr_dir = (curr_dir+3)%4;
					result.add(Environment.Action.TURN_LEFT);
				}
				else if((next_direction-curr_dir+4)%4==2){
					result.add(Environment.Action.TURN_LEFT);
					result.add(Environment.Action.TURN_LEFT);
					curr_dir = (curr_dir+2)%4;
					System.out.println("Double turn");
				}
				result.add(Environment.Action.GO_FORWARD);
				temp_point=p;
			}
		}
		return result;
	}

	private LinkedList<Point> createPathTo(Point nextVertex) throws NoSuchElementException {
		MapGraph graph = new MapGraph(
				(nextVertex.equals(start_point) || current_loc.equals(start_point)) ? getFullBooleanMap() : getBooleanMap());
		return graph.shortestWay(graph.getTile(current_loc), graph.getTile(nextVertex));
	}

	private boolean[][] getBooleanMap() {
		boolean[][] result = new boolean[w][w];
		for(Point point : visitedTiles){
			if(!point.equals(start_point)) result[point.x][point.y]=true;
		}
		for(Point point : unvisitedSafeTiles){
			result[point.x][point.y]=true;
		}
		return result;
	}

	private boolean[][] getFullBooleanMap() {
		boolean[][] result = getBooleanMap();
		result[0][3]=true;
		return result;
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
//            System.out.println(player.render());
//            System.out.println(player.debug());
        }
    }
}
