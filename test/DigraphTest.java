import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.emptyIterable;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class DigraphTest {

    @Rule
    public ExpectedException thrown= ExpectedException.none();

    @Test
    public void constructorWithNegativeVerticesThrowsException() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("vertices must be nonnegative");

        new Digraph(-1);
    }

    @Test
    public void constructor() {
        Digraph digraph = new Digraph(2);

        assertThat(digraph.V(), is(2));
        assertThat(digraph.neighbors(0), is(emptyIterable()));
        assertThat(digraph.neighbors(1), is(emptyIterable()));
    }

    @Test
    public void addEdgeWithInvalidFromVertexThrowsException() {
        thrown.expect(IndexOutOfBoundsException.class);
        thrown.expectMessage("vertex 5 is not between 0 and 4");

        new Digraph(5).addEdge(5, 1);
    }

    @Test
    public void addEdgeWithInvalidToVertexThrowsException() {
        thrown.expect(IndexOutOfBoundsException.class);
        thrown.expectMessage("vertex 5 is not between 0 and 4");

        new Digraph(5).addEdge(1, 5);
    }

    @Test
    public void addEdge() {
        Digraph digraph = new Digraph(5);
        digraph.addEdge(0, 1);

        assertThat(digraph.neighbors(0), contains(1));
    }

    @Test
    public void addEdgeIgnoresDuplicates() {
        Digraph digraph = new Digraph(5);
        digraph.addEdge(0, 1);
        digraph.addEdge(0, 1);

        assertThat(digraph.neighbors(0), contains(1));
    }

    @Test
    public void neighborsWithInvalidVertexThrowsException() {
        thrown.expect(IndexOutOfBoundsException.class);
        thrown.expectMessage("vertex 5 is not between 0 and 4");

        new Digraph(5).neighbors(5);
    }

    @Test
    public void neighbors() {
        Digraph digraph = new Digraph(5);
        digraph.addEdge(0, 1);
        digraph.addEdge(0, 2);

        assertThat(digraph.neighbors(0), contains(1, 2));
    }

    @Test
    public void copyConstructor() {
        Digraph origin = new Digraph(3);
        origin.addEdge(0, 1);
        Digraph copy = new Digraph(origin);
        origin.addEdge(0, 2);

        assertThat(origin.neighbors(0), contains(1, 2));
        assertThat(copy.neighbors(0), contains(1));
    }

}
