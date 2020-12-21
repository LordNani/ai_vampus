package logic.pathfinder;

public interface Algorithm {
    boolean isFinished();

    Vertex getNextVertex();

    void updateVertex(Vertex v);
}
