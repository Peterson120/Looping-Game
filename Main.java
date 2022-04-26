import static java.lang.System.*;
import java.util.*;

/*
Combat game
Hint for Secret Word
    Understand what the functions do
*/
public class Main
{
    private static Scanner scan;
    public static Game game;
    private static List<Integer> word;
    public static void main(String[] args)
    {
        scan = new Scanner(System.in); // initialize Scanner
        minigame(); // Create a little minigame
        out.println("\nCongratulations, You have advanced onto the dungeon");
        Game.sDelay(2);
        setup(); // Setup functoin
    }

    private static void setup() // Print intro to main game
    {
        Game.clearScreen();
        out.println("Welcome to your DOOM DUNGEON!!\n");
        out.println("Enter a name to begin: ");
        String user = scan.nextLine(); // Get name
        if(user.length() <= 0 || user.length() > 10) setup();
        String letter = (String.valueOf(user.charAt(0))).toUpperCase(); // Get first letter of name and make it uppercase
        user = letter + user.substring(1); // Set name to upppercase letter and the rest of the name
        rules(); // Go to Rules screen
        play(user); // Main game loop
    }

    private static void minigame() // Minigame
    {
        String user;
        word = new ArrayList<Integer>(11); // Initialize list
        addValues(); // Add Values to array list
        Game.clearScreen();
        out.println("MINI CHALLENGE\n");
        int tries = 0;
        do{
            Game.clearScreen();
            out.println("MINI CHALLENGE\n");
            if(tries > 0) out.println("Looks like the code was incorrect.");
            else out.println("Uh Oh. It looks like you need a code to enter");
            out.println("\nPassword:");
            user = scan.nextLine(); // Get user input
            if(tries > 0 && secretWord(user)) out.println("HINT: Look through the source code"); // Check that there are more than one tries and that user did not get the word
            Game.sDelay(1);
            tries++; // Increment tries
        }while(secretWord(user)); // Check that input does not match secret word
    }

    private static void addValues() // Add values to word list // Go to game block for answer
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

    private static boolean secretWord(String user) // Check if user input matches word
    {
        List<Integer> list = new ArrayList<Integer>(user.length());
        for(int i = 0; i < user.length(); i++) list.add((int)user.charAt(i)); // Add characters to list
        list.replaceAll(n->n*0xCafe);
        if(word.size() != list.size()) return true;
        for(int i = 0; i < word.size(); i++)
        {
            if(list.get(i) - word.get(i) != 0) return true; // Check that there is no difference between word and user input
        }
        return false;
    }

    private static void rules() // Print rules/tips
    {
        Game.clearScreen();
        out.println("Welcome to your DOOM DUNGEON!!\n\nSome quick notes before you begin \n\nEvery turn you can choose to Attack, Block or use a potion\nBlocking will block up to 100% damage\nPotions will last until you beat the current challenger \nYou lose if your health reaches 0\nYou start with 500 hp and your HP carries on to the next levels\nYou have 5 potions to start and you gain 3 for each opponent that you beat\nBlocks and Counter Attacks only work on Basic Attacks");
        Game.sDelay(5); // Wait 5 Seconds before allowing user to input
        out.println("\nType OK to continue");
        String user = scan.nextLine().toLowerCase(); // Get user input
        if(user.contains("ok")) return; // Check is input contains ok
        rules(); // Otherwise call on itself
    }

    public static void play(String name)
    {
        game = new Game(name);
        while(game.winner().equals("n"))
        {
            if(game.getTurn()%2==0) game.input();
            else game.getVillain().turn();
        }
        String win = game.winner();
        if(win.equals("player")) game.printWin();
        else Game.printLoss();
        game.playAgain();
    }
}