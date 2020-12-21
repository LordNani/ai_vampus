package logic.pathfinder;

public class AStarAlgorithm extends AwareAlgorithm {

    public AStarAlgorithm(EuclidVertex startPosition, EuclidVertex targetPosition) {
        super(startPosition, targetPosition);
    }

    @Override
    public void updateVertex(Vertex v) {
        int known_index = used.indexOf((EuclidVertex) v);
        if (known_index != -1) {
            int old_distance = targetPosition.squaredDistance(used.get(known_index));
            int new_distance = targetPosition.squaredDistance((EuclidVertex) v);
            if (old_distance > new_distance) {
                used.remove(known_index);
                used.add((EuclidVertex) v);
            }
        } else {
            known_index = euclidVertices.indexOf((EuclidVertex) v);
            if (known_index != -1) {
                int old_distance = targetPosition.squaredDistance(euclidVertices.get(known_index));
                int new_distance = targetPosition.squaredDistance((EuclidVertex) v);
                if (old_distance > new_distance) {
                    euclidVertices.remove(known_index);
                    euclidVertices.add((EuclidVertex) v);
                }
            } else {
                euclidVertices.add((EuclidVertex) v);
//				used.add((EuclidVertex)v);
            }
        }
    }
}
