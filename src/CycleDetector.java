import java.util.HashSet;
import java.util.Set;

/**
 * Determines whether a digraph contains a directed cycle
 */
public class CycleDetector {

    private final Set<Integer> visited = new HashSet<>();
    private final Set<Integer> stacked = new HashSet<>();
    private final Digraph graph;
    private Boolean cycle;

    public CycleDetector(Digraph graph) {
        if (graph == null) {
            throw new NullPointerException("graph is null");
        }
        this.graph = graph;
    }

    public boolean hasCycle() {
        if (cycle != null) {
            return cycle;
        }

        cycle = false;
        for (int i = 0; i < graph.V() && !cycle; i++) {
            if (!visited.contains(i)) {
                cycle = detectCycle(i);
            }
        }

        return cycle;
    }

    private boolean detectCycle(int v) {
        visited.add(v);
        stacked.add(v);
        for (int w: graph.neighbors(v)) {
            if (!visited.contains(w) && detectCycle(w)) {
                return true;
            } else if (stacked.contains(w)) {
                return true;
            }
        }
        stacked.remove(v);

        return false;
    }

}
