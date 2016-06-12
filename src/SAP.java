import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

/**
 * An immutable Shortest Ancestral Path data type
 */
public class SAP {

    private final Digraph graph;
    private Path vPath;
    private Path wPath;

    public SAP(Digraph graph) {
        if (graph == null) {
            throw new NullPointerException("graph is null");
        }
        this.graph = new Digraph(graph);
    }

    private class Path {
        private final Map<Integer, Integer> distance = new HashMap<>();
        private final Queue<Integer> toVisit = new LinkedList<>();

        private int maxDistance = 0;

        public Path(int vertex) {
            toVisit.add(vertex);
            distance.put(vertex, 0);
        }

        public Path(Iterable<Integer> vertices) {
            for (int vertex: vertices) {
                toVisit.add(vertex);
                distance.put(vertex, 0);
            }
        }

        public int distanceTo(int vertex) {
            return distance.get(vertex);
        }

        public int maxDistance() {
            return maxDistance;
        }

        public boolean hasNext() {
            return !toVisit.isEmpty();
        }

        public Iterable<Integer> next() {
            List<Integer> next = new ArrayList<>();
            if (hasNext()) {
                int from = toVisit.poll();
                for (int neighbor: graph.neighbors(from)) {
                    if (!distance.containsKey(neighbor)) {
                        int dist = distance.get(from) + 1;
                        distance.put(neighbor, dist);
                        if (dist > maxDistance) {
                            maxDistance = dist;
                        }
                        next.add(neighbor);
                        toVisit.add(neighbor);
                    }
                }
            }

            return next;
        }
    }

    public int length(int v, int w) {
        return length(ancestor(v, w));
    }

    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        return length(ancestor(v, w));
    }

    private int length(int ancestor) {
        if (ancestor == -1) {
            return -1;
        } else {
            return vPath.distanceTo(ancestor) + wPath.distanceTo(ancestor);
        }
    }

    public int ancestor(int v, int w) {
        validateVertex(v);
        validateVertex(w);

        vPath = new Path(v);
        wPath = new Path(w);

        if (v == w) {
            return v;
        } else {
            return ancestor(new ArrayList<>(Arrays.asList(v, w)));
        }
    }

    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        validateVertices(v);
        validateVertices(w);

        vPath = new Path(v);
        wPath = new Path(w);

        List<Integer> visited = new ArrayList<>();
        for (int vertex: v) {
            visited.add(vertex);
        }
        for (int vertex: w) {
            if (visited.contains(vertex)) {
                return vertex;
            } else {
                visited.add(vertex);
            }
        }

        return ancestor(visited);
    }

    private void validateVertices(Iterable<Integer> vertices) {
        if (vertices == null) {
            throw new NullPointerException("vertices are null");
        }

        for (int vertex: vertices) {
            validateVertex(vertex);
        }
    }

    private void validateVertex(int vertex) {
        if (vertex < 0 || vertex > graph.V()) {
            throw new IndexOutOfBoundsException("invalid vertex: " + vertex);
        }
    }

    private int ancestor(List<Integer> visited) {
        int ancestor = -1;

        Queue<Path> paths = new LinkedList<>();
        paths.add(vPath);
        paths.add(wPath);
        while (!paths.isEmpty()) {
            Path path = paths.poll();
            if (path.hasNext()) {
                for (int vertex: path.next()) {
                    if (visited.contains(vertex)) {
                        if (ancestor == -1 || length(vertex) < length(ancestor)) {
                            ancestor = vertex;
                        }
                    } else {
                        visited.add(vertex);
                    }
                }
                if (ancestor == -1 || path.maxDistance() <= length(ancestor)) {
                    paths.add(path);
                }
            }
        }

        return ancestor;
    }

}
