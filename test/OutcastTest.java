import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.IllegalFormatCodePointException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class OutcastTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private WordNet wordnet;

    /**
     *          scala
     *        /       \
     *      java     python
     *                /   \
     *               c   lisp
     */
    @Before
    public void setUp() {
        wordnet = mock(WordNet.class);
        when(wordnet.distance("java", "scala")).thenReturn(1);
        when(wordnet.distance("java", "python")).thenReturn(2);
        when(wordnet.distance("java", "c")).thenReturn(3);
        when(wordnet.distance("java", "lisp")).thenReturn(3);

        when(wordnet.distance("scala", "java")).thenReturn(1);
        when(wordnet.distance("scala", "python")).thenReturn(1);
        when(wordnet.distance("scala", "c")).thenReturn(2);
        when(wordnet.distance("scala", "lisp")).thenReturn(2);

        when(wordnet.distance("python", "java")).thenReturn(2);
        when(wordnet.distance("python", "scala")).thenReturn(1);
        when(wordnet.distance("python", "c")).thenReturn(1);
        when(wordnet.distance("python", "lisp")).thenReturn(1);

        when(wordnet.distance("c", "java")).thenReturn(3);
        when(wordnet.distance("c", "scala")).thenReturn(2);
        when(wordnet.distance("c", "python")).thenReturn(1);
        when(wordnet.distance("c", "lisp")).thenReturn(2);

        when(wordnet.distance("lisp", "java")).thenReturn(3);
        when(wordnet.distance("lisp", "scala")).thenReturn(2);
        when(wordnet.distance("lisp", "python")).thenReturn(1);
        when(wordnet.distance("lisp", "c")).thenReturn(2);

        when(wordnet.distance(eq("php"), anyString())).thenThrow(
            new IllegalArgumentException("not a WordNet noun: php"));
        when(wordnet.distance(anyString(), eq("php"))).thenThrow(
            new IllegalArgumentException("not a WordNet noun: php"));
    }

    @Test
    public void constructorWithNullThrowsException() {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("wordnet is null");

        new Outcast(null);
    }

    @Test
    public void outcastWithInvalidNounsThrowsException() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("not a WordNet noun: php");

        new Outcast(wordnet).outcast(new String[]{"lisp", "java", "php"});
    }

    @Test
    public void outcastWithNullThrowsException() {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("nouns are null");

        new Outcast(wordnet).outcast(null);
    }

    @Test
    public void outcast() {
        Outcast outcast = new Outcast(wordnet);

        String[] nouns = new String[]{"java", "scala", "python", "c", "lisp"};
        assertThat(outcast.outcast(nouns), is("java"));
    }

}
