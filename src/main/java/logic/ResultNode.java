package logic;

import java.util.ArrayList;

public class ResultNode extends Node{

    ArrayList<Node[]> clauses;

    public ResultNode(Point point) {
        super(point);
        clauses = new ArrayList();
    }

    public boolean canBeEvaluated() {
        for(Node[] and_group : clauses){
            if(isFullyDefined(and_group)) return true;
        }
        return false;
    }

    protected boolean isFullyDefined(Node[] and_group) {
        boolean exist_undefined = false;
        for(Node clause : and_group){
            exist_undefined |= !clause.isEvaluated;
        }
        return !exist_undefined;
    }

    public Boolean evaluate() {
        for(Node[] and_group : clauses){
            if(isFullyDefined(and_group)){
                boolean and_result = true;
                for(Node clause : and_group){
                    and_result &= clause.value;
                }
                return and_result;
            }
        }
        return null;
    }
}
