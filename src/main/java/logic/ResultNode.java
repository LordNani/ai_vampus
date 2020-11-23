package logic;

import java.util.ArrayList;

public class ResultNode extends Node{

    ArrayList<Node[]> clauses;

    public ResultNode(Point point, boolean positive) {
        super(point, positive);
        clauses = new ArrayList();
    }

    public boolean canBeEvaluated() {
        for(Node[] and_group : clauses){
            if(isFullyDefined(and_group)) return true;
        }
        return false;
    }

    protected boolean isFullyDefined(Node[] and_group) {
        boolean all_defined = true;
        for(Node clause : and_group){
            all_defined &= clause.isEvaluated;
        }
        return all_defined;
    }

    public Boolean evaluate() {
        if(!canBeEvaluated()) return null;
        boolean or_result = false;
        for(Node[] and_group : clauses){
            if(isFullyDefined(and_group)){
                boolean and_result = true;
                for(Node clause : and_group){
                    and_result &= clause.value;
                }
                or_result |= and_result;
            }
        }
        return or_result;
    }
}
