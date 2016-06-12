import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A directed graph of vertices named 0 to V-1
 */
public class Digraph {

    private final int V;
    private final Map<Integer, List<Integer>> neighborhood = new HashMap<>();

    public Digraph(int V) {
        if (V < 0) {
            throw new IllegalArgumentException(
                "Number of vertices must be nonnegative");
        }
        this.V = V;
        while (V >= 0) {
            neighborhood.put(V--, new ArrayList<>());
        }
    }

    public Digraph(Digraph graph) {
        this(graph.V);
        for (Map.Entry<Integer, List<Integer>> entry: graph.neighborhood.entrySet()) {
            neighborhood.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }
    }

    public void addEdge(int from, int to) {
        validateVertex(from);
        validateVertex(to);
        List<Integer> neighbors = neighborhood.get(from);
        if (!neighbors.contains(to)) {
            neighbors.add(to);
        }
    }

    public Iterable<Integer> neighbors(int vertex) {
        validateVertex(vertex);

        return neighborhood.get(vertex);
    }

    private void validateVertex(int vertex) {
        if (vertex < 0 || vertex >= V) {
            throw new IndexOutOfBoundsException(
                "vertex " + vertex + " is not between 0 and " + (V-1)
            );
        }
    }

}
