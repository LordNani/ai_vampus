package logic;

import java.util.ArrayList;

public class ResultNode extends Node{

    ArrayList<PerceptionNode[]> clauses;

    public ResultNode(Point point) {
        super(point);
        clauses = new ArrayList();
    }

    public void evaluate() {
        isEvaluated = true;
    }
}
