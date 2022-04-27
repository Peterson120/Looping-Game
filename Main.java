import static java.lang.System.*;
import java.util.*;

/*
Combat game
Hints for Secret Word
    1. Understand what the functions do
    2. Look at the addValues() function and secretWord() function
    3. The letters are stored as ASCII values
    4. Try reverse engineering the function addValues() use a bit of code to help you
    5. The answer is at the top of the Game class
*/
public class Main
{
    static Scanner scan;
    static Game game;
    private static List<Integer> word;
    public static void main(String[] args)
    {
        scan = new Scanner(System.in); // initialize Scanner
        minigame(); // Create a little minigame
        out.println("\nCongratulations, You have advanced onto the dungeon");
        Game.buffer();
        setup(); // Setup function
    }

    private static void setup() // Print intro to main game
    {
        Game.clearScreen();
        out.println("Welcome to your DOOM DUNGEON!!\n");

        text(); // Go to Rules screen
        play(); // Main game loop
    }

    private static void minigame() // Minigame
    {
        String user;
        word = new ArrayList<Integer>(11); // Initialize list
        addValues(); // Add Values to array list
        do{
            Game.clearScreen();
            out.println("MINI CHALLENGE\n");
            out.println("Uh Oh. It looks like you need a code to enter");
            out.println("\nPassword:");
            user = scan.nextLine(); // Get user input
            if(secretWord(user)) // Check that there are more than one tries and that user did not get the word
            {
                out.println("\nLooks like the code was incorrect");
                out.println("HINT: Look through the source code");
                Game.buffer();
            }
        }while(secretWord(user)); // Check that input does not match secret word
        word.clear();
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
        out.println("Welcome to your DOOM DUNGEON!!\n\nSome quick notes before you begin\n\nThis is a typical rpg\nYou fight against villains and you lose if your health reaches 0\nYou health carries on to the next level and you gain 150% of your current health after beating an opponent");
        Game.buffer();
    }

    static void play() // Main Game
    {
        Player player = new Player();
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
}