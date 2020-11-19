package agents;

import wumpus.Agent;
import wumpus.Environment;
import wumpus.Player;

import java.util.ArrayList;
import java.util.LinkedList;

public class bcAgent implements Agent {
    private int w, h;
    private LinkedList<Environment.Action> nextActions = new LinkedList<Environment.Action>();
    private boolean debug = true;

    public bcAgent(int w, int h) {
        this.w = w;
        this.h = h;
    }

    @Override
    public Environment.Action getAction(Player player) {
        int x = player.getX();
        int y = player.getY();

        // Apply actions pools
        if (nextActions.size() > 0) {
            return nextActions.poll();
        }

        // Grab the gold if senses glitter
        if (player.hasGlitter()) return Environment.Action.GRAB;

        // Shoot an arrow to every non visited tiles if senses a stench
        if (player.hasStench()) {
            // Apply killer instinct
        }

        // Mark non visited neighbors has dangerous
        if (player.hasBreeze()) {

        }

        // Print the chosen tile
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
