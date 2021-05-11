import java.util.*;

/**
 * Matthew Niculae
 * Professor Knisley
 * EGR227 Data Structures - Evil Hangman
 * 3/5/21
 *
 * Description of class:
 * This class initializes the constructor with the corresponding
 * beginning values, and returns any errors if initialized with
 * the wrong values. The primary purpose it to record each
 * guess that the player makes and then use that guess to create
 * patterns for every word in the dictionary. Once it has created
 * those patterns, the program will determine a new index of strings
 * from which it applies to the next input that a player provides.
 */
public class HangmanManager {

    private int wordSize;
    private int guessLeft;
    private String ogPattern = "";
    private String rightPattern = "";

    private Collection<String> dictionary;
    private TreeSet<String> setWords;
    private SortedSet<Character> letters = new TreeSet<>();

    //HangmanManager constructor
    //This accepts a dictionary of terms, a target word length,
    //and the max possible number of wrong guesses that someone can make.
    //This set of words use all the words from the dictionary file and
    //makes sure any duplicates are removed.
    //These values will always be used to start the state of the game.
    public HangmanManager(Collection<String> dictionary, int length, int max) {
        //protects the game from being initialized with improper values
        if (length < 1 || max < 0) throw new IllegalArgumentException("Game condition is invalid!");

        for (int i = 0; i < length; i++) {
            if (i == length - 1) {
                rightPattern += "-";
                ogPattern += "-";
            } else {
                rightPattern += "- ";
                ogPattern += "- ";
            }

        }

        this.dictionary = new TreeSet<>();
        this.setWords = new TreeSet<>();
        this.wordSize = length;
        this.guessLeft = max;

        for (String word : dictionary) {
            if (word.length() == length) {
                this.dictionary.add(word);
                setWords.add(word);
            }
        }
    }

    //This method is called to give access to the user the current
    // set of words that are being quarried for hangman manager
    public Set<String> words() {
        return setWords;
    }

    //This method is called when the user tries to see how many guesses
    //they have left
    public int guessesLeft() {
        return this.guessLeft;
    }


    //This method is called when the user wants to find out
    //the current set f letters that have been guessed.
    //The method returns all the correct guesses.
    public SortedSet<Character> guesses() {
        return letters;
    }

    //This method returns the correctly guessed letters in their
    //appropriate positions in the word. Correctly guessed letters
    //will show up while the spaces where the correct letters have not been
    //guessed will return "-"
    public String pattern() {
        return rightPattern;
    }

    //This method uses the guess as a parameter to decide
    //what set of words should be used next. It should also
    //update the number of guesses left for the player
    public int record(char guess) {

        //throws IllegalStateException if amount of guesses left is not
        //at least 1 or if the set is empty.
        if (guessLeft < 1 || setWords.isEmpty()) throw new IllegalStateException("Sorry, there was an error");

        //Throws IllegalArgumentException if the set of words is
        //both nonempty and the guessed character has already been guessed.
        if (!setWords.isEmpty() && letters.contains(guess))
            throw new IllegalArgumentException("That letter was already guessed");

        //Adds a letter to the set of guesses
        //since the set doesn't contain it already
        letters.add(guess);

        //Uses the guess to decide the next
        //set of words, and also uses the guess
        //to decide the best pattern
        String pattern = patternHelper(guess);


        //Update the guessed letter set
        //with the letter printed out to the
        //correct corresponding index location
        int foundChar = 0;
        for (int i = 0; i < pattern.length(); i++) {
            if (pattern.charAt(i) == guess) {
                foundChar++;
            }
        }

        //return the number of guesses they have left
        if (foundChar == 0) {
            guessLeft--;
        }
        return foundChar;
    }

    private String patternHelper(char guess) {
        Map<String, TreeSet<String>> map = new TreeMap<>();
        StringBuilder pattern = new StringBuilder();

        //sorts through all the letters in guesses
        //and replaces the hyphens in the pattern
        for (String word : setWords) {
            pattern = new StringBuilder();
            if (!rightPattern.equals(ogPattern)) {
                pattern.append(rightPattern);
            } else {
                for (int i = 0; i < wordSize; i++) {
                    if (i == wordSize - 1) {
                        pattern.append("-");
                    } else {
                        pattern.append("- ");
                    }

                }
            }
            for (int i = 0; i < word.length(); i++) {
                if (word.charAt(i) == guess) {
                    pattern.replace(i * 2, i * 2 + 1, "" + guess);
                }
            }
            if (!map.containsKey(pattern.toString())) {
                TreeSet<String> set = new TreeSet<>();
                set.add(word);
                map.put(pattern.toString(), set);
            } else {
                map.get(pattern.toString()).add(word);
            }
        }

        TreeSet<String> largestSet = new TreeSet<>();
        //finds the largest set of words which can be used to update the word set
        rightPattern = pattern.toString();
        for (String key : map.keySet()) {
            if (map.get(key).size() > largestSet.size()) {
                largestSet = map.get(key);
                rightPattern = key;
            }
        }
        setWords = largestSet;
        return rightPattern;
    }
}
