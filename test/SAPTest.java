import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Collections;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

@RunWith(JUnitParamsRunner.class)
public class SAPTest {

    @Rule
    public ExpectedException thrown= ExpectedException.none();

    private Digraph acyclicGraph;
    private Digraph cycleGraph;

    @Before
    public void setUp() {
        /**
         *          0
         *         / \
         *        1   2-6
         *       /  \  \
         *      3    4  7
         *     /
         *    5         8
         */
        acyclicGraph = new Digraph(9);
        acyclicGraph.addEdge(1, 0);
        acyclicGraph.addEdge(2, 0);
        acyclicGraph.addEdge(3, 1);
        acyclicGraph.addEdge(4, 1);
        acyclicGraph.addEdge(5, 3);
        acyclicGraph.addEdge(6, 2);
        acyclicGraph.addEdge(7, 2);

        /**
         *         0
         *        / \
         *       /   5
         *      /    |
         *     1-2-3-4
         */
        cycleGraph = new Digraph(6);
        cycleGraph.addEdge(1, 0);
        cycleGraph.addEdge(1, 2);
        cycleGraph.addEdge(2, 3);
        cycleGraph.addEdge(3, 4);
        cycleGraph.addEdge(4, 5);
        cycleGraph.addEdge(5, 0);

    }

    @Test
    public void constructorWithNullThrowsException() {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("graph is null");

        new SAP(null);
    }

    private Object[] iterableNulls() {
        return new Object[]{
            new Iterable[]{Collections.emptyList(), null},
            new Iterable[]{null, Collections.emptyList()},
            new Iterable[]{null, null}
        };
    }

    @Test
    @Parameters(method = "iterableNulls")
    public void lengthWithNullThrowsException(
            Iterable<Integer> v, Iterable<Integer> w) {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("vertices are null");

        new SAP(acyclicGraph).length(v, w);
    }

    @Test
    @Parameters(method = "iterableNulls")
    public void ancestorWithNullThrowsException(
        Iterable<Integer> v, Iterable<Integer> w) {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("vertices are null");

        new SAP(acyclicGraph).ancestor(v, w);
    }

    private Object[] invalidVertices() {
        return new Object[]{
            new Integer[]{-1, 1, -1},
            new Integer[]{10, 1, 10},
            new Integer[]{1, -1, -1},
            new Integer[]{1, 10, 10},
            new Integer[]{-1, 10, -1},
        };
    }

    @Test
    @Parameters(method ="invalidVertices")
    public void lengthWithInvalidVertexThrowsException(int v, int w, int err) {
        thrown.expect(IndexOutOfBoundsException.class);
        thrown.expectMessage("invalid vertex: " + err);

        new SAP(acyclicGraph).length(v, w);
    }

    @Test
    @Parameters(method ="invalidVertices")
    public void ancestorWithInvalidVertexThrowsException(int v, int w, int err) {
        thrown.expect(IndexOutOfBoundsException.class);
        thrown.expectMessage("invalid vertex: " + err);

        new SAP(acyclicGraph).ancestor(v, w);
    }

    private Object[] invalidIterableVertices() {
        return new Object[]{
            new Object[]{Arrays.asList(-1, 3), Arrays.asList(1, 2), -1},
            new Object[]{Arrays.asList(1, 10), Arrays.asList(1, 2), 10},
            new Object[]{Arrays.asList(1, 3), Arrays.asList(-1, 2), -1},
            new Object[]{Arrays.asList(1, 3), Arrays.asList(1, 10), 10},
            new Object[]{Arrays.asList(-1, 3), Arrays.asList(1, -10), -1},
        };
    }

    @Test
    @Parameters(method ="invalidIterableVertices")
    public void lengthWithInvalidIterableVertexThrowsException(
            Iterable<Integer> v, Iterable<Integer> w, int err) {
        thrown.expect(IndexOutOfBoundsException.class);
        thrown.expectMessage("invalid vertex: " + err);

        new SAP(acyclicGraph).length(v, w);
    }

    @Test
    @Parameters(method ="invalidIterableVertices")
    public void ancestorWithInvalidIterableVertexThrowsException(
        Iterable<Integer> v, Iterable<Integer> w, int err) {
        thrown.expect(IndexOutOfBoundsException.class);
        thrown.expectMessage("invalid vertex: " + err);

        new SAP(acyclicGraph).ancestor(v, w);
    }

    @Test
    public void lengthInAcyclicGraphWithAncestor() {
        SAP sap = new SAP(acyclicGraph);

        assertThat(sap.length(5, 2), is(4));
    }

    @Test
    public void lengthBetweenSameVerticesInAcyclicGraph() {
        SAP sap = new SAP(acyclicGraph);

        assertThat(sap.length(5, 5), is(0));
    }

    @Test
    public void lengthForIterableInAcyclicGraphWithAncestor() {
        SAP sap = new SAP(acyclicGraph);

        assertThat(sap.length(Arrays.asList(5, 4), Arrays.asList(6,7,8)),
                   is(4));
    }

    @Test
    public void lengthBetweenSameVerticesForIterableInAcyclicGraph() {
        SAP sap = new SAP(acyclicGraph);

        assertThat(sap.length(Arrays.asList(5, 4), Arrays.asList(6,5,8)),
                   is(0));
    }

    @Test
    public void lengthInAcyclicGraphWithoutAncestor() {
        SAP sap = new SAP(acyclicGraph);

        assertThat(sap.length(5, 8), is(-1));
    }

    @Test
    public void lengthForIterableInAcyclicGraphWithoutAncestor() {
        SAP sap = new SAP(acyclicGraph);

        assertThat(sap.length(Arrays.asList(5, 4), Collections.singletonList(8)),
                   is(-1));
    }

    @Test
    public void lengthInCycleGraph() {
        SAP sap = new SAP(cycleGraph);

        assertThat(sap.length(1, 5), is(2));
    }

    @Test
    public void lengthForIterableInCycleGraph() {
        SAP sap = new SAP(cycleGraph);

        assertThat(sap.length(Arrays.asList(1, 2), Arrays.asList(4, 5)),
                   is(2));
    }

    @Test
    public void ancestorInAcyclicGraph() {
        SAP sap = new SAP(acyclicGraph);

        assertThat(sap.ancestor(5, 2), is(0));
    }

    @Test
    public void ancestorOfSameVerticesInAcyclicGraph() {
        SAP sap = new SAP(acyclicGraph);

        assertThat(sap.ancestor(5, 5), is(5));
    }

    @Test
    public void ancestorForIterableInAcyclicGraph() {
        SAP sap = new SAP(acyclicGraph);

        assertThat(sap.ancestor(Arrays.asList(5, 4), Arrays.asList(6,7,8)),
                   is(0));
    }

    @Test
    public void ancestorOfSameVerticesForIterableInAcyclicGraph() {
        SAP sap = new SAP(acyclicGraph);

        assertThat(sap.ancestor(Arrays.asList(5, 4), Arrays.asList(6,5,8)),
                   is(5));
    }

    @Test
    public void noAncestorInAcyclicGraph() {
        SAP sap = new SAP(acyclicGraph);

        assertThat(sap.ancestor(5, 8), is(-1));
    }

}
