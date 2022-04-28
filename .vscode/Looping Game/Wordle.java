import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

/*Word game*/
class Wordle
{
    // Instance variables
    private String guess = "";
    private Scanner scan;
    private File file;
    int length; // Num of lines in file
    private ArrayList<String> word; // List to store word to make it harder for user to see what the word is
    private String[] bank;
    boolean error = false;

    // All 5 letter English words

    /*
     * Constructor for objects of class CombinationLock
     * Create a WordBank instance and get a word
     */
    Wordle()
    {
        length = Main.fileLength("WordBank.txt");
        file = new File("WordBank.txt"); // Declare WordBank.txt as a file
        scan = new Scanner(System.in); // Initialize Scanner
        word = new ArrayList<String>(1); // Initialize word
        bank = new String[length]; // Set Word Bank length
        addToList(); // Add word to word bank
        word.add(Main.readFile("WordBank.txt", Game.srand.nextInt(length))); // Add random word to list
    }
    
    // Getters
    String getWord() {return word.get(0);}
    String getGuess() {return guess;}

    // Returns string of what characters guesses were right
    public String toString()
    {
        String result = ""; // Initialize result
        for(int i = 0; i < guess.length(); i++)
        {
            char character = guess.charAt(i); // Get character at index i
            if(word.get(0).charAt(i) == character) result += '✓'; // Check if characters of guess and random word at index i equal
            else if(word.get(0).contains(String.valueOf(character))) result += "+"; // Check if character is in the word
            else result += "*"; // Case where character is not in the word
        }
        return result;
    }

    void getInput() // Get user guess
    {
        guess = ""; // Set guess to an invalid guess
        do{
            
            System.out.print("\nGuess a 5 letter word: \n");
            guess = scan.nextLine();
            if(guess.equals("quit") || guess.charAt(0) == 'q') 
            {
                System.out.println("\nAre you sure you want to quit?(Y/n)");
                String user = scan.nextLine();
                if(user.charAt(0) == 'Y')
                {
                    guess = "quit";
                    return;
                }
                error = true;
            }
        }while(error||!isValidGuess()); // Loop while guess is invalid
        guess = guess.toLowerCase(); // Set guess to lowercase
    }
    
    private boolean isValidGuess() // Check that the guess matches the guessing criteria
    {
        if(guess.equals("bypass")) return false;
        else if(guess.length() != 5) // Check that word is at least 5 digits
        {
            System.out.println("\nGuess must be exactly 5 characters long");
            return false;
        }
        if(!isLetters(guess))  // If guess contains symbols or digits
        {
            System.out.println("\nPlease enter only characters");
            return false;
        }
        else if(binarySearch(0,bank.length-1) == -1) // Check if guess is a word
        {
            System.out.println("\n" + guess + " is not a word");
            return false;
        }
        return true;
    }

    private boolean isLetters(String guess) // Check that guess contains only letters(for runtime complexity)
    {
        for(int i = 0; i < guess.length(); i++)
        {
            if(!Character.isLetter(guess.charAt(i))) return false;
        }
        return true;
    }

    private void addToList() // Adds all words from WordBank.txt to an array
    {
        try {
            Scanner fileRead = new Scanner(file); // Get file with Scanner
        
            int i = 0; // Index
            while (fileRead.hasNextLine()) // Read until last line
            {
                bank[i] = fileRead.nextLine();
                i++;
            }
            fileRead.close();
        } 
        catch (Exception e) {e.printStackTrace();} // File not found
    }
    /*
    Existing algorithm
    Modified to use strings instead of numbers
    */
    private int binarySearch(int lower, int higher) // Binary Search algorithm to improve runtime Complexity
    {
        int compare;
        int mid = lower + (higher-lower)/2; // Find middle number of higher and lower

        if(lower > higher) return -1; // Base Case if word is not in list
        else if(bank[mid].equals(guess.toLowerCase())) return mid; // If guess is in list return index
        else compare = guess.toLowerCase().compareToIgnoreCase(bank[mid]); // Compare the two words using ASCII values

        if(compare > 0) return binarySearch(mid+1,higher); // If compare is positive take right side
        else return binarySearch(lower,mid-1); // If compare is negative take left side
    }
    /* 
    If result is positive, guess is higher than current indexed word
    If result is negative, guess is lower than current indexed word
    if result is 0 guess is equal to current indexed word
    */
}