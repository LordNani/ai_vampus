package logic;

import java.util.LinkedList;

public class VertexPoint extends Point {
    LinkedList<Integer> path;

    public VertexPoint(int x, int y, LinkedList<Integer> path) {
        super(x, y);
        this.path = path;
    }

    public VertexPoint(VertexPoint vp) {
        super(vp.x, vp.y);
        this.path = ((LinkedList<Integer>) vp.path.clone());
    }

    public VertexPoint(Point p) {
        super(p.x, p.y);
        this.path = new LinkedList<>();
    }

    @Override
    public String toString() {
        return super.toString() + " path: " + path.toString();
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    public LinkedList<Integer> getPath() {
        return path;
    }
}
