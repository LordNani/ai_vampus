package agents;

import logic.Point;
import logic.ResultNode;
import wumpus.Agent;
import wumpus.Environment;
import wumpus.Player;

import java.util.ArrayList;
import java.util.LinkedList;

import static logic.AgentLogic.getCurrentState;

public class bcAgent implements Agent {
    private int w, h;
    private boolean debug = true;

    private boolean isWumpusAlive = false;
    private boolean hasGold = false;

    private boolean[][] visited;
    private LinkedList<Environment.Action> nextActions = new LinkedList<Environment.Action>();

    public bcAgent(int w, int h) {
        this.w = w;
        this.h = h;
    }

    @Override
    public Environment.Action getAction(Player player) {
        int x = player.getX();
        int y = player.getY();

        // Grab the gold if senses glitter
        if (player.hasGlitter()) return Environment.Action.GRAB;

        boolean stenching = player.hasStench();
        boolean breezing = player.hasBreeze();
        boolean screaming = player.hasScream();

        // Apply actions pools
        if (nextActions.size() > 0) {
            return nextActions.poll();
        }

        ArrayList<ResultNode> info = getCurrentState(new Point(x,y),stenching,breezing,screaming );

        if (debug) {
//            System.out.format("Go to (%d,%d)%n", next[0], next[1]);
        }

        // Auto execute the first action
        return nextActions.poll();
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
