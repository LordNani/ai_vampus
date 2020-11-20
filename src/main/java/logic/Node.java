package logic;

public class Node {
    boolean isEvaluated = false;
    Boolean value = false;
    Point coords = new Point();

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
