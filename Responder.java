import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * The responder class represents a response generator object.
 * It is used to generate an automatic response, based on specified input.
 * Input is presented to the responder as a set of words, and based on those
 * words the responder will generate a String that represents the response.
 * <p>
 * Internally, the reponder uses a HashMap to associate words with response
 * strings and a list of default responses. If any of the input words is found
 * in the HashMap, the corresponding response is returned. If none of the input
 * words is recognized, one of the default responses is randomly chosen.
 *
 * @author David J. Barnes and Michael Kölling.
 * @version 2016.02.29
 */
public class Responder {
    // The name of the file containing the default responses.
    private static final String FILE_OF_DEFAULT_RESPONSES = "default.txt";
    private static final String FILE_OF_MAP_RESPONSES = "responses.txt";
    // Used to map key words to responses.
    private HashMap<String, String> responseMap;
    // Default responses to use if we don't recognise a word.
    private ArrayList<String> defaultResponses;
    private Random randomGenerator;

    /**
     * Construct a Responder
     */
    public Responder() {
        responseMap = new HashMap<>();
        defaultResponses = new ArrayList<>();
        fillResponseMap();
        fillDefaultResponses();
        randomGenerator = new Random();
    }

    /**
     * Generate a response from a given set of input words.
     *
     * @param words A set of words entered by the user
     * @return A string that should be displayed as the response
     */
    public String generateResponse(HashSet<String> words) {
        Iterator<String> it = words.iterator();
        while (it.hasNext()) {
            String word = it.next();
            String response = responseMap.get(word);
            if (response != null) {
                return response;
            }
        }
        // If we get here, none of the words from the input line was recognized.
        // In this case we pick one of our default responses (what we say when
        // we cannot think of anything else to say...)
        return pickDefaultResponse();
    }

    /**
     * Enter all the known keywords and their associated responses
     * into our response map.
     */
    private void fillResponseMap() {
        // This is super locked-in for the specific file.
        // Any edge case would immediately break it.
        // TODO: fix this.
        try(BufferedReader reader = Files.newBufferedReader(Paths.get(FILE_OF_MAP_RESPONSES), Charset.forName("US-ASCII"))) {
            String line;
            while((line = reader.readLine()) != null) {
                String[] keys;
                String temp = line.trim();
                if(temp.isEmpty()) {
                    continue;
                }
                if(temp.contains(",") || temp.split(" ").length == 1) {
                    keys = temp.split(",");
                    temp = reader.readLine();
                    for(String key : keys) {
                        key = key.trim();
                        responseMap.put(key, temp);
                    }
                }
            }
            System.out.println("SIZE: " + responseMap.size());
            for(String key : responseMap.keySet()) {
                System.out.println(key + ": " + responseMap.get(key));
            }
        } catch(IOException e) {
            System.err.println(this.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }

    /**
     * Build up a list of default responses from which we can pick
     * if we don't know what else to say.
     */
    private void fillDefaultResponses() {
        Charset charset = Charset.forName("US-ASCII");
        Path path = Paths.get(FILE_OF_DEFAULT_RESPONSES);
        String line;
        try (BufferedReader reader = Files.newBufferedReader(path, charset)) {
            while ((line = reader.readLine()) != null) {
                String temp = line.trim();
                if (temp.isEmpty()) {
                    continue;
                }
                List<String> tokens = new ArrayList<>();
                while (temp != null && !temp.isEmpty()) {
                    tokens.add(temp);
                    temp = reader.readLine();
                }
                String str = "";
                for (String token : tokens) {
                    str += token + "\n";
                }
                defaultResponses.add(str);
            }
            //sanity checks
            System.out.println("SIZE: " + defaultResponses.size());
            for (int i = 0; i < defaultResponses.size(); i++) {
                System.out.println(i + ": " + defaultResponses.get(i));
            }
        } catch (FileNotFoundException e) {
            System.err.println("Unable to open " + FILE_OF_DEFAULT_RESPONSES);
        } catch (IOException e) {
            System.err.println("A problem was encountered reading " +
                    FILE_OF_DEFAULT_RESPONSES);
        }
        // Make sure we have at least one response.
        if (defaultResponses.size() == 0) {
            defaultResponses.add("Could you elaborate on that?");
        }
    }

    /**
     * Randomly select and return one of the default responses.
     *
     * @return A random default response
     */
    private String pickDefaultResponse() {
        // Pick a random number for the index in the default response list.
        // The number will be between 0 (inclusive) and the size of the list (exclusive).
        int index = randomGenerator.nextInt(defaultResponses.size());
        return defaultResponses.get(index);
    }
}
