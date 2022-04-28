import java.util.Scanner;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/*
Combat game
*/

class Main
{
    static Scanner scan;
    static Game game;
    private static int tokens;
    private static List<Integer> word;
    public static void main(String[] args)
    {
        scan = new Scanner(System.in); // initialize Scanner
        minigame(); // Create a little minigame
        System.out.println("\nCongratulations, You have advanced onto the dungeon");
        Game.buffer();
        setup(); // Setup function
    }

    private static void setup() // Print intro to main game
    {
        Game.clearScreen();
        System.out.println("Welcome to your DOOM DUNGEON!!\n");

        text(); // Go to Rules screen
        play(); // Main game loop
    }

    private static void minigame() // Minigame
    {
        String user;
        String[] hints = {"Look Through the source code","Understand what the functions do","Look at the addValues() function and secretWord() function","The letters are stored as ASCII values","Try reverse engineering the function addValues() use a bit of code to help you","The answer is <Secret Word>"};
        word = new ArrayList<Integer>(11); // Initialize list
        addValues(); // Add Values to array list
        int tries = 0;
        do{
            Game.clearScreen();
            System.out.println("MINI CHALLENGE\n");
            System.out.println("Uh Oh. It looks like you need a code to enter");
            System.out.println("\nPassword:");
            user = scan.nextLine(); // Get user input
            if(secretWord(user)) // Check that there are more than one tries and that user did not get the word
            {
                System.out.println("\nLooks like the code was incorrect");
                System.out.println("HINT: " + hints[tries]);
                Game.buffer();
            }
            tries++;
        }while(secretWord(user)); // Check that input does not match secret word
        word.clear();
        tokens = 7 - tries;
        System.out.println("\nYou solved the puzzle and you earned " + tokens + " tokens for your Dungeon Character");

        System.out.println("\nPlay another minigame?(Y/n)");
        String again = scan.nextLine().toLowerCase();
        if(again.length() <= 0) ; // Override if user has no input
        else if(again.charAt(0) == 'y') minigame2();
    }

    private static void minigame2() // Wordle minigame
    {
        Game.clearScreen();
        System.out.println("Welcome to Walter's Word Guessing game!\n\nYou will get extra tokens depending on how many turns you take\nYour goal is to guess the secret word\nIf the letter is in the correct spot it is represented by ✓.\nIf the letter is in the word, it is represented by +\nIf the letter is not in the word it is represented by a *\n\nType 'quit' to exit");
        //Object with specified word
        Wordle wordGame = new Wordle();
        
        int numGuesses = 0;
        // Determines if the user input matches the word
        while(!(wordGame.getGuess().toLowerCase()).equals(wordGame.getWord()))
        {
            wordGame.getInput(); // Get user guess
            if(wordGame.getGuess().equals("quit"))
            {
                System.out.println("\nYou quit the game and earned no tokens");
                return;
            }
            System.out.println(wordGame); // Print hints
            numGuesses ++; // Increment number of guesses
        }

        int prize = 7-numGuesses; // Calculate num of tokens that player gets
        prize = prize < 0 ? 0 : prize; // Check that num of tokens is not less than 0 or set to 0
        tokens += prize; // Add prize to tokens
        System.out.println("\nYou earned " + prize + " more tokens for your Dungeon character");
    }

    private static void addValues() // Add values to word list
    {
        for(int i = 0; i < 110000; i += 10000) word.add(i);
        word.set(9,5924124);
        word.set(5,6028056);
        word.set(6,1662912);
        word.set(0,4313178);
        word.set(3,5924124);
        word.set(2,5144634);
        word.set(1,5248566);
        word.set(4,5248566);
        word.set(7,4521042);
        word.set(8,5768226);
        word.set(10,5196600);
    }

    private static boolean secretWord(String user) // Check if user input matches word (not commented because it is a challenge for the user to solve)
    {
        List<Integer> list = new ArrayList<Integer>(user.length()); // Create list with length of user string
        for(int i = 0; i < user.length(); i++) list.add((int)user.charAt(i)); // Add characters to list
        list.replaceAll(n->n*0xCafe);
        if(word.size() != list.size()) // Check that user input and list size are equal
        {
            list.clear();
            return true;
        }
        for(int i = 0; i < word.size(); i++) // Check that there is no difference between word and user input
        {
            if(list.get(i) - word.get(i) != 0) 
            {
                list.clear();
                return true;
            }
        }
        list.clear();
        return false;
    }

    private static void text() // Print rules/tips
    {
        Game.clearScreen();
        System.out.println("Welcome to your DOOM DUNGEON!!\n\nSome quick notes before you begin\n\nThis is an RPG\nYou fight against villains and you lose if your health reaches 0\nYour health carries on to the next level and you gain 150% of your current health after beating an opponent");
        Game.buffer();
    }

    static void play() // Main Game
    {
        Player player = new Player(tokens);
        game = new Game(player); // Create game instance
        while(game.winner().equals("n")) // Main game loop
        {
            if(game.getTurn()%2 == 0) game.input(); // If user's turn get user input
            else game.getVillain().turn(); // Otherwise get challenger's move
        }
        if(game.winner().equals("player")) game.printWin(); // Get winner and print win if player
        else game.printLoss(); // Else print lose
        game.playAgain(); // Ask if user wants to play again
    }

    static String readFile(String file, int line) // Choose a name from the list
    {
        int length = fileLength(file);
        if(line > length) // Check if line is outside file
        {
            System.out.println("Index Out of Bounds");
            return "";
        }
        try {return Files.readAllLines(Paths.get(file)).get(line);} // Try getting a line in the list
        catch (IOException e) {System.out.println(file + " not found");} // File is not found Exception
        return "";
    }

    static int fileLength(String file)
    {
        int length = 0;
        try
        {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            while (reader.readLine() != null) length++;
            reader.close();
        }
        catch(Exception e) {System.out.println("File not found");}
        return length;
    }
}