package logic;

import java.util.ArrayList;

public class PerceptionNode extends Node{

    ArrayList<ResultNode> targets;

    public PerceptionNode(Point point) {
        super(point);
        targets = new ArrayList<ResultNode>();
    }


}
