import java.util.Scanner;
import static java.lang.System.*;

public class Main
{
    private static Scanner scan;
    public static Game game;
    public static void main(String[] args)
    {
        scan = new Scanner(System.in);
        Game.clearScreen();
        out.println("Welcome to your DOOM DUNGEON!!\n");
        out.println("Do you want to play?(Y/n)");
        String user = scan.nextLine();
        if(user.length() > 0)
        {
            switch(user.toLowerCase())
            {
                case "y": break;
                case "n": Game.printLoss(); scan.close(); System.exit(0); break;
            }
        }
        else main(args);
        rules();
        play();
    }

    public static void rules()
    {
        Game.clearScreen();
        out.println("Welcome to your DOOM DUNGEON!!\n\nSome quick notes before you begin \n\nEvery turn you can choose to Attack, Block or use a potion\nBlocking will block up to 100% damage\nPotions will last until you beat the current challenger \nYou lose if your health reaches 0\nYou start with 500 hp and your HP carries on to the next levels\nYou have 3 potions to start and you gain 2 for each opponent that you beat");
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