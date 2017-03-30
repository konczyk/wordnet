import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class CycleDetectorTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void constructorWithNullThrowsException() {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("graph is null");

        new CycleDetector(null);
    }

    /**
     *     ----- 4
     *     |     +
     *     +     |
     *     0 --+ 1 --+ 3
     *     |
     *     +
     *     2
     */
    @Test
    public void graphHasCycle() {
        Digraph graph = new Digraph(5);
        graph.addEdge(0, 1);
        graph.addEdge(0, 2);
        graph.addEdge(1, 3);
        graph.addEdge(1, 4);
        graph.addEdge(4, 0);

        assertThat(new CycleDetector(graph).hasCycle(), is(true));
    }

    /**
     *     ----+ 4
     *     |     +
     *     |     |
     *     0 --+ 1 --+ 3
     *     |
     *     +
     *     2
     */
    @Test
    public void graphHasNoCycle() {
        Digraph graph = new Digraph(5);
        graph.addEdge(0, 1);
        graph.addEdge(0, 2);
        graph.addEdge(0, 4);
        graph.addEdge(1, 3);
        graph.addEdge(1, 4);

        assertThat(new CycleDetector(graph).hasCycle(), is(false));
    }

}
