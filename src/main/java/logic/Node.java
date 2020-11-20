package logic;

public class Node {
    boolean isEvaluated = false;
    boolean value = false;
    Point coords;

    public Node(Point p){
        coords = p;
    }

    public void evaluate() {
        isEvaluated = true;
    }

    public void deEvaluate() {
        isEvaluated = false;
    }

    public void setValue(Boolean val){
        value = val;
    }
}
