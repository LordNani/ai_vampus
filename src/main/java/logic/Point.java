package logic;

public class Point implements Cloneable, Comparable {
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

    public Point(logic.pathfinder.Point position) {
        this.x = position.x;
        this.y = position.y;
    }

    @Override
    public boolean equals(Object player_pos) {
        return x == ((Point) player_pos).x && y == ((Point) player_pos).y;
    }

//
//    @Override
//    public int hashCode(){
//        return x*0xFFF+y;
//    }

    public int squaredDistance(Point target) {
        return (((Point) target).x - x) * (((Point) target).x - x) + (((Point) target).y - y) * (((Point) target).y - y);
    }

    public boolean isConnected(Point v) {
        return (squaredDistance(v)==1) && (((Point) v).x == x || ((Point) v).y == y);
    }

    public int directionTo(Point point) {
        if (!isConnected(point)) {
            System.out.println("Points are not connected!");
        }
        if (x == point.x) {
            if (y + 1 == point.y) return 2;
            return 0;
        } else {
            if (x + 1 == point.x) return 1;
            return 3;
        }
    }

    @Override
    public String toString() {
        return "{x: " + x + ", y: " + y + "}";
    }

    @Override
    public Point clone() {
        return new Point(x, y);
    }

    @Override
    public int compareTo(Object o) {
        if(x==((Point)o).x){
            return Integer.compare(y, ((Point)o).y);
        }
        return Integer.compare(x, ((Point)o).x);
    }
}
