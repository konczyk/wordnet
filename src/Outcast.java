import java.util.stream.IntStream;

public class Outcast {

    private final WordNet wordnet;

    public Outcast(WordNet wordnet) {
        if (wordnet == null) {
            throw new NullPointerException("wordnet is null");
        }
        this.wordnet = wordnet;
    }

    public String outcast(String[] nouns) {
        if (nouns == null) {
            throw new NullPointerException("nouns are null");
        }

        int maxDistance = 0;
        String outcast = null;

        for (String from: nouns) {
            int nounDistance = IntStream
                .range(0, nouns.length)
                .map(j -> wordnet.distance(from, nouns[j]))
                .sum();

            if (nounDistance > maxDistance) {
                maxDistance = nounDistance;
                outcast = from;
            }
        }

        return outcast;
    }

}
