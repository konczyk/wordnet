import com.beust.jcommander.IParameterValidator;
import com.beust.jcommander.IParameterValidator2;
import com.beust.jcommander.IValueValidator;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.beust.jcommander.Parameters;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Client {

    @Parameter(
        names = {"--nouns", "-n"},
        required = true,
        variableArity = true,
        description = "List of nouns separated by a space"
    )
    private List<String> nouns = new ArrayList<>();

    @Parameter(
        names = {"--action", "-a"},
        required = true,
        description = "Action to perform on nouns (ancestor|outcast)",
        validateWith = ActionValidator.class
    )
    private String action;

    @Parameter(
        names = {"--help", "-h"},
        description = "Usage help",
        help = true)
    private boolean help = false;

    public static class ActionValidator implements IParameterValidator {
        @Override
        public void validate(String param, String value) throws ParameterException {
            if (!value.equals("ancestor") && !value.equals("outcast")) {
                throw new ParameterException("Invalid action: " + value);
            }
        }
    }

    public static void main(String[] args) {
        Client client = new Client();
        JCommander jc = new JCommander(client);
        jc.setProgramName("Client");
        try {
            jc.parse(args);
            client.validate();
            if (client.help || args.length == 0) {
                jc.usage();
                return;
            }
            client.run();
        } catch (ParameterException e) {
            System.out.println(e.getMessage());
        }
    }

    private void validate() throws ParameterException {
        if (action.equals("ancestor") && nouns.size() != 2) {
            throw new ParameterException(
                "ancestor action requires exactly two nouns");
        }

        if (action.equals("outcast") && nouns.size() < 2) {
            throw new ParameterException(
                "outcast action requires at least two nouns");
        }
    }

    private void run() {
        WordNet wn = new WordNet("data/synsets.txt", "data/hypernyms.txt");
        // validate nouns
        List<String> notFound = new ArrayList<>();
        for (String noun: nouns) {
            if (!wn.isNoun(noun)) {
                notFound.add(noun);
            }
        }
        if (!notFound.isEmpty()) {
            throw new ParameterException("not in the synonyms list: " + notFound);
        }
        if (action.equals("outcast")) {
            String outcast = new Outcast(wn).outcast(nouns.toArray(new String[nouns.size()]));
            System.out.println(outcast);
        }
        if (action.equals("ancestor")) {
            String ancestor = wn.sap(nouns.get(0), nouns.get(1));
            System.out.println(ancestor);
        }
    }

}
