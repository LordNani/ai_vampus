package logic.pathfinder;

public interface Vertex {
    boolean equals(Object player_pos);

    boolean isConnected(Vertex v);
}
