import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import junitparams.converters.Nullable;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.rules.TemporaryFolder;

@RunWith(JUnitParamsRunner.class)
public class WordNetTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Rule
	public TemporaryFolder tmp = new TemporaryFolder();

    @Test
    public void constructorWithNullSynsetsThrowsException() {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("synsets file is null");

        new WordNet(null, null);
    }

    @Test
    public void constructorWithInvalidSynsetsThrowsException() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("synsets file does not exist");

        new WordNet(createFile().getAbsolutePath() + "x", null);
    }

    @Test
    public void constructorWithUnreadableSynsetsThrowsException() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("synsets file is not readable");
        File f = createFile();
        f.setReadable(false);

        new WordNet(f.getAbsolutePath(), "");
    }

    @Test
    public void constructorWithNullHypernymsThrowsException() {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("hypernyms file is null");

        new WordNet(createSynsetsFile(), null);
    }

    @Test
    public void constructorWithInvalidHypernymsThrowsException() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("hypernyms file does not exist");

        new WordNet(createSynsetsFile(), createFile().getAbsolutePath() + "x");
    }

    @Test
    public void constructorWithUnreadableHypernymsThrowsException() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("hypernyms file is not readable");
        File f = createFile();
        f.setReadable(false);

        new WordNet(createSynsetsFile(), f.getAbsolutePath());
    }

    @Test
    public void constructorWithCyclicHypernymsThrowsException() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("hypernyms graph contains a cycle");
        String cyclicHypernyms = writeToFile(Arrays.asList("0","1,0,3","2,1,0","3,2"));

        new WordNet(createSynsetsFile(), cyclicHypernyms);
    }

    @Test
    public void constructorWithMultiRootHypernymsThrowsException() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("hypernyms graph contains multiple roots");
        String multiRootHypernyms = writeToFile(Arrays.asList("0","1,0","2,0,3"));

        new WordNet(createSynsetsFile(), multiRootHypernyms);
    }

    @Test
    public void isNounNullThrowsException() {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("word is null");

        WordNet wordnet = new WordNet(createSynsetsFile(), createHypernymsFile());
        wordnet.isNoun(null);
    }

    @Test
    public void nouns() {
        WordNet wordnet = new WordNet(createSynsetsFile(), createHypernymsFile());

        assertThat(wordnet.nouns(),
                   containsInAnyOrder(
                        "root", "Aberdeen", "Depardieu", "Gerard_Depardieu",
                        "actor", "histrion", "player", "thespian", "town",
                        "port_of_entry", "point_of_entry"));
    }

    @Test
    public void isNoun() {
        WordNet wordnet = new WordNet(createSynsetsFile(), createHypernymsFile());

        assertThat(wordnet.isNoun("Aberdeen"), is(true));
    }

    @Test
    @Parameters({"null, root",
                 "root, null"})
    public void distanceWithNullThrowsException(
            @Nullable String nounA, @Nullable String nounB) {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("word is null");
        WordNet wordnet = new WordNet(createSynsetsFile(), createHypernymsFile());

        wordnet.distance(nounA, nounB);
    }

    @Test
    @Parameters({"null, root",
                 "root, null"})
    public void sapWithNullThrowsException(
            @Nullable String nounA, @Nullable String nounB) {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("word is null");
        WordNet wordnet = new WordNet(createSynsetsFile(), createHypernymsFile());

        wordnet.sap(nounA, nounB);
    }

    @Test
    @Parameters({"a, root",
                 "root, a"})
    public void distanceWithInvalidNounThrowsException(String nounA, String nounB) {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("not a WordNet noun: a");
        WordNet wordnet = new WordNet(createSynsetsFile(), createHypernymsFile());

        wordnet.distance(nounA, nounB);
    }

    @Test
    @Parameters({"a, root",
                 "root, a"})
    public void sapWithInvalidNounThrowsException(String nounA, String nounB) {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("not a WordNet noun: a");
        WordNet wordnet = new WordNet(createSynsetsFile(), createHypernymsFile());

        wordnet.sap(nounA, nounB);
    }

    @Test
    public void distance() {
        WordNet wordnet = new WordNet(createSynsetsFile(), createHypernymsFile());

        assertThat(wordnet.distance("Aberdeen", "town"), is(1));
    }

    @Test
    public void sap() {
        WordNet wordnet = new WordNet(createSynsetsFile(), createHypernymsFile());

        assertThat(wordnet.sap("Aberdeen", "Depardieu"), is("root"));
    }

    private String createSynsetsFile() {
        return writeToFile(
            Arrays.asList(
                "0,root,root",
                "1,Aberdeen,a town in western Washington",
                "2,Depardieu Gerard_Depardieu,French film actor",
                "3,actor histrion player thespian,a theatrical performer",
                "4,town,an urban area smaller than a city",
                "5,port_of_entry point_of_entry,a port in the United States"));
	}

    private String createHypernymsFile() {
        return writeToFile(Arrays.asList("0","1,4,5", "2,3", "3,0","4,0","5,0"));
	}

    private File createFile() {
        try {
            return tmp.newFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String writeToFile(List<String> data) {
        try {
            File file = createFile();
            Files.write(file.toPath(), data, StandardCharsets.US_ASCII);

            return file.getAbsolutePath();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
