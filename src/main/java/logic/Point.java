package logic;

public class Point implements Cloneable {
    public int x;
    public int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Point(Point position) {
        this.x = position.x;
        this.y = position.y;
    }

    public Point() {

    }

    @Override
    public boolean equals(Object player_pos) {
        return x == ((Point) player_pos).x && y == ((Point) player_pos).y;
    }

    public int squaredDistance(Point target) {
        return (((Point) target).x - x) * (((Point) target).x - x) + (((Point) target).y - y) * (((Point) target).y - y);
    }

    @Override
    public String toString() {
        return "{x: " + x + ", y: " + y + "}";
    }

    @Override
    public Point clone() {
        return new Point(x, y);
    }

}
