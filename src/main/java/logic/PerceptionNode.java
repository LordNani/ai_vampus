package logic;

import java.util.ArrayList;

public class PerceptionNode extends Node{

    ArrayList<ResultNode> targets;

    public PerceptionNode(Point point, boolean positive) {
        super(point, positive);
        targets = new ArrayList<ResultNode>();
    }


}
