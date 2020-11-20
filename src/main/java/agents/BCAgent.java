package agents;

import logic.KnowledgeBase;
import logic.Point;
import logic.ResultNode;
import wumpus.Agent;
import wumpus.Environment;
import wumpus.Player;

import java.util.ArrayList;
import java.util.LinkedList;

public class BCAgent implements Agent {
    private int w, h;
    private boolean debug = true;

    private boolean isWumpusAlive = false;
    private boolean hasGold = false;

    private boolean[][] visited;
    private LinkedList<Environment.Action> nextActions = new LinkedList<Environment.Action>();
    private KnowledgeBase kb;

    public BCAgent(int w, int h) {
        this.w = w;
        this.h = h;
        kb = new KnowledgeBase(w,h);
        System.out.println("Hi");
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

        ArrayList<ResultNode> info = kb.getCurrentState(new Point(x,y),stenching,breezing,screaming );

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
