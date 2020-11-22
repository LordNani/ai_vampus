package logic;

public class Node {
    boolean isEvaluated = false;
    boolean value = false;
    Point coords;

    public Node(Point p){
        coords = p;
    }

    public void setValue(boolean val){
        isEvaluated=true;
        value = val;
    }
}
