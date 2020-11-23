package logic;

public class Node {
    boolean isEvaluated = false;
    boolean value = false;
    boolean positive;
    private Point coords;

    public Node(Point p, boolean positive){

        coords = p;
        this.positive = positive;
    }

    public void setValue(boolean val){
        isEvaluated=true;
        value = val;
    }

    public Point getCoords() {
        return coords;
    }

    public void setCoords(Point coords) {
        this.coords = coords;
    }

    public boolean getValue() {
        return value;
    }
}
