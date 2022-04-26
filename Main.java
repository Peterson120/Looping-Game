import static java.lang.System.*;
import java.util.*;

public class Main
{
    private static Scanner scan;
    public static Game game;
    private static List<Integer> word;
    public static void main(String[] args)
    {
        scan = new Scanner(System.in);
        minigame();
        out.println("\nCongratulations, You have advanced onto the dungeon");
        Game.sDelay(3);
        Game.clearScreen();
        out.println("Welcome to your DOOM DUNGEON!!\n");
        out.println("Do you want to begin?(Y/n)");
        String user = scan.nextLine();
        if(user.length() > 0)
        {
            switch(user.toLowerCase())
            {
                case "y": break;
                case "n": 
                    Game.sDelay(5);
                    main(args);
            }
        }
        else main(args);
        rules();
        play();
    }

    private static void minigame()
    {
        String user;
        word = new ArrayList<Integer>(11);
        addValues();
        Game.clearScreen();
        out.println("MINI CHALLENGE\n");
        Game.sDelay(1);
        int tries = 0;
        do{
            Game.clearScreen();
            out.println("MINI CHALLENGE\n");
            if(tries > 0) out.println("Looks like the code was incorrect.");
            else out.println("Uh Oh. It looks like you need a code to enter");
            out.println("\nPassword:");
            user = scan.nextLine();
            if(tries > 0 && secretWord(user)) out.println("HINT: Look through the source code");
            Game.sDelay(1);
            tries++;
        }while(secretWord(user));
    }

    private static void addValues()
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

    private static boolean secretWord(String user)
    {
        List<Integer> list = new ArrayList<Integer>(user.length());
        for(int i = 0; i < user.length(); i++) list.add((int)user.charAt(i));
        list.replaceAll(n->n*0xCafe);
        if(word.size() != list.size()) return true;
        for(int i = 0; i < word.size(); i++)
        {
            if(list.get(i) - word.get(i) != 0) return true;
        }
        return false;
    }

    public static void rules()
    {
        Game.clearScreen();
        out.println("Welcome to your DOOM DUNGEON!!\n\nSome quick notes before you begin \n\nEvery turn you can choose to Attack, Block or use a potion\nBlocking will block up to 100% damage\nPotions will last until you beat the current challenger \nYou lose if your health reaches 0\nYou start with 500 hp and your HP carries on to the next levels\nYou have 5 potions to start and you gain 3 for each opponent that you beat\nBlocks and Counter Attacks only work on Basic Attacks");
        Game.sDelay(5);
        out.println("\nType OK to continue");
        String user = scan.nextLine().toLowerCase();
        if(user.contains("ok")) return;
        rules();
    }

    public static void play()
    {
        game = new Game();
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