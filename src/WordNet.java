import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WordNet {

    private final Map<String, List<Integer>> synsetsByNoun = new HashMap<>();
    private final Map<Integer, String> synsetsById = new HashMap<>();
    private SAP sap;

    public WordNet(String synsets, String hypernyms) {
        Path synsetsPath = getPath(synsets, "synsets");
        Path hypernymsPath = getPath(hypernyms, "hypernyms");

        readSynsets(synsetsPath);
        readHypernyms(hypernymsPath, synsetsById.size());
    }

    private Path getPath(String filename, String filetype) {
        if (filename == null) {
            throw new NullPointerException(filetype + " file is null");
        }
        Path path = Paths.get(filename);
        if (!Files.isRegularFile(path)) {
            throw new IllegalArgumentException(filetype + " file does not exist");
        }
        if (!Files.isReadable(path)) {
            throw new IllegalArgumentException(filetype + " file is not readable");
        }

        return path;
    }

    private void readSynsets(Path path) {
        Charset charset = StandardCharsets.US_ASCII;
        try (BufferedReader reader = Files.newBufferedReader(path, charset)) {
            String line;
            while ((line = reader.readLine()) != null) {
                addSynset(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addSynset(String line) {
        String[] chunks = line.split(",");
        if (chunks.length < 3 || !chunks[0].matches("^\\d+$")) {
            throw new IllegalArgumentException(
                "synset file contains invalid line: " + line);
        }

        int id = Integer.parseInt(chunks[0]);
        List<String> words = Arrays.asList(chunks[1].split(" "));

        synsetsById.put(id, chunks[1]);

        for (String noun: words) {
            List<Integer> ids = synsetsByNoun.get(noun);
            if (ids == null) {
                ids = new ArrayList<>();
            }
            ids.add(id);
            synsetsByNoun.put(noun, ids);
        }
    }

    private void readHypernyms(Path path, int size) {
        Charset charset = StandardCharsets.US_ASCII;
        Digraph graph = new Digraph(size);
        try (BufferedReader reader = Files.newBufferedReader(path, charset)) {
            String line;
            while ((line = reader.readLine()) != null) {
                addHypernym(graph, line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        sap = new SAP(graph);
    }

    private void addHypernym(Digraph graph, String line) {
        String[] chunks = line.split(",");
        if (chunks.length < 1 || !line.matches("^\\d+(,\\d+)*$")) {
            throw new IllegalArgumentException(
                "hypernym file contains invalid line: " + line);
        }

        if (chunks.length > 1) {
            int synsetId = Integer.parseInt(chunks[0]);
            for (int i = 1; i < chunks.length; i++) {
                graph.addEdge(synsetId, Integer.parseInt(chunks[i]));
            }
        }
    }

    public Iterable<String> nouns() {
        return synsetsByNoun.keySet();
    }

    public boolean isNoun(String word) {
        if (word == null) {
            throw new NullPointerException("word is null");
        }

        return synsetsByNoun.containsKey(word);
    }

    public int distance(String nounA, String nounB) {
        return sap.length(getSynsetIds(nounA), getSynsetIds(nounB));
    }

    public String sap(String nounA, String nounB) {
        int ancestor = sap.ancestor(getSynsetIds(nounA), getSynsetIds(nounB));

        return synsetsById.get(ancestor);
    }

    private Iterable<Integer> getSynsetIds(String word) {
        if (!isNoun(word)) {
            throw new IllegalArgumentException(
                "not a WordNet noun: " + word);
        }

        return synsetsByNoun.get(word);
    }

}
