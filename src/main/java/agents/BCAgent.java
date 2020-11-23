package agents;


import logic.KnowledgeBase;
import logic.Point;
import logic.ResultNode;
import wumpus.Agent;
import wumpus.Environment;
import wumpus.Player;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.TreeSet;

public class BCAgent implements Agent {
    private int w, h;
    private boolean debug = true;

    private boolean isWumpusAlive = false;
    private boolean hasGold = false;
    LinkedList<Point> unvisitedSafeTiles = new LinkedList<>();
	LinkedList<Point> visitedTiles = new LinkedList<>();
	LinkedList<Point> dangerousTiles = new LinkedList<>();
	Point current_loc;

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
					}
					else{
						dangerousTiles.add(tile.getCoords());
					}
				}
			}
		}

		if(unvisitedSafeTiles.isEmpty()){
			nextActions = createPathTo(new Point(0,3));
		}
		else{
			Point nextVertex = unvisitedSafeTiles.pollFirst();
			visitedTiles.add(nextVertex);
			nextActions = createPathTo(nextVertex);
		}
		return nextActions.pollFirst();
    }

	private LinkedList<Environment.Action> createPathTo(Point nextVertex) {
		return null;
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
