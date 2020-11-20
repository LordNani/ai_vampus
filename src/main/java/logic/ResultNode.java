package logic;

import java.util.ArrayList;

public class ResultNode extends Node{

    ArrayList<ArrayList<Boolean>> clauses;

    public ResultNode() {
        clauses = new ArrayList<ArrayList<Boolean>>();
    }

    public void evaluate() {
        isEvaluated = true;
    }
}
