package logic.pathfinder;


import java.util.LinkedList;
import java.util.NoSuchElementException;

public class MapGraph {
    public MapTile[][] tiles;

    public MapGraph(boolean[][] state) {
        tiles = new MapTile[state.length][state[0].length];
        for (int i = 0; i < state.length; ++i) {
            for (int j = 0; j < state[0].length; ++j) {
                if (state[i][j]) {
                    Point p = new Point(i, j);
                    MapTile tile = new MapTile(p);
                    if (j != 0 && state[i][j - 1]) {
                        MapTile upper_tile = tiles[i][j - 1];
                        tile.neighbours[0] = upper_tile;
                        upper_tile.neighbours[2] = tile;
                    }
                    if (i != 0 && state[i - 1][j]) {
                        MapTile left_tile = tiles[i - 1][j];
                        tile.neighbours[3] = left_tile;
                        left_tile.neighbours[1] = tile;
                    }
                    tiles[i][j] = tile;
                }
            }
        }
    }

    public LinkedList<logic.Point> shortestWay(MapTile start, MapTile end) throws NoSuchElementException {
        LinkedList<logic.Point> way = new LinkedList<>();
        LinkedList<Integer> directions =  shortestWay(start.point, end.point);
        logic.Point s = new logic.Point(start.point);
        way.add(s);
        for(Integer direction : directions){
            Point next = new Point(s);
            next.moveInDirection(direction,1);
            s = new logic.Point(next);
            way.add(s);
        }
        return way;
    }

    public LinkedList<Integer> shortestWay(Point start, Point end) throws NoSuchElementException {
        Point current = start.clone();
//		ArrayList<Integer> path = new ArrayList<>();
        Logic logic = new Logic(tiles.length, start, new AStarAlgorithm(new VertexPoint(start), new VertexPoint(end)));
        while (!current.equals(end)) {
            boolean[] surrounding_area = getSurroundingArea(current);
            boolean or=false;
            for(boolean b : surrounding_area) or |= b;
            if(!or) throw new NoSuchElementException();
            int next_step = logic.makeMove(surrounding_area);
            current.moveInDirection(next_step, 1);
//			path.add(next_step);
        }
        return logic.getPosition().getPath();
    }

    private boolean[] getSurroundingArea(Point current) {
        MapTile tile = tiles[current.x][current.y];
        boolean[] res = new boolean[4];
        for (int i = 0; i < 4; ++i) res[i] = tile.neighbours[i] != null;
        boolean used = false;
        for (int i = 0; i < 4; ++i) {
            if (res[i]) used = true;
        }
        if (!used) {
            System.out.println("No way!");
        }
        return res;
    }

    public MapTile getTile(logic.Point current_loc) {
        return tiles[current_loc.x][current_loc.y];
    }
}

class MapTile {
    Point point;
    MapTile[] neighbours;

    public MapTile(Point p) {
        this.point = p;
        neighbours = new MapTile[4];
    }
}