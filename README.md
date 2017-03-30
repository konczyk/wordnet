# WordNet 

A semantic lexicon for the English language.

## Goal

Group words into sets of synonyms (synsets) and define semantic relationship
between them through hypernyms, allowing shortest ancestor path lookups.

## Sample client

Build project:

    $ ./gradlew assemble

Client options:

    $ java -cp build/libs/wordnet.jar Client -h

Find a common ancestor of two wordnet nouns, in a shortest ancestral path:

    $ java -cp build/libs/wordnet.jar Client -a ancestor -n apple beef

    food solid_food

Find an outcast (noun least related to the others) in the list of wordnet nouns:

    $ java -cp build/libs/wordnet.jar Client -a outcast -n worm bird bottle water

    bottle
