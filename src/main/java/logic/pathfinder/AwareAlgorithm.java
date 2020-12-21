package logic.pathfinder;

import java.util.ArrayList;

public abstract class AwareAlgorithm implements Algorithm {
    ArrayList<EuclidVertex> euclidVertices = new ArrayList<>();
    ArrayList<EuclidVertex> used = new ArrayList<>();
    EuclidVertex targetPosition;
    EuclidVertex position;

    public AwareAlgorithm(EuclidVertex startPosition, EuclidVertex targetPosition) {
        this.targetPosition = targetPosition;
        this.position = startPosition;
        used.add(startPosition);
    }

    @Override
    public boolean isFinished() {
        return euclidVertices.isEmpty();
    }

    @Override
    public Vertex getNextVertex() {
        int closest_index = 0;
        int min_distance = targetPosition.squaredDistance(euclidVertices.get(closest_index));
        int current_distance;
        for (int i = 1; i < euclidVertices.size(); ++i) {
            current_distance = targetPosition.squaredDistance(euclidVertices.get(i));
            if (current_distance < min_distance) {
                min_distance = current_distance;
                closest_index = i;
            }
        }
        EuclidVertex removed = euclidVertices.remove(closest_index);
        used.add(removed);
        return removed;
    }

    public void updatePosition(EuclidVertex ev) {
        position = ev;
    }
}
