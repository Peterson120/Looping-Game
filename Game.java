import static java.lang.System.*;
import static java.util.concurrent.TimeUnit.*;
import java.util.*;
import java.security.*;

/*
Ideas
Counter Attack 50% chance + crit if opponent attacks on next turn
Regular attack 100% chance + 10% crit
Block up to 100%
Limit num of potions and one potion per turn
*/

public class Game 
{
    private int numOppsLeft = 5, turn = 0, hp = 500,atk = 40,def = 5,playerLevel = 1, critDmg = atk/2, numPotions = 3;
    private boolean potionUsed = false;
    final private String[] moves =  {"Attack","Block","Potions","Give Up"}, attackMenu = {"Basic","Counter Attack","Slap","Exit"}, potionMenu = {"ATK DMG","Defense","Heal","Exit"};
    private static Scanner scan;
    public static SecureRandom srand;
    private Villain challenger;
    private String lastMove;

    public Game()
    {
        newChallenger();
        scan = new Scanner(System.in);
        srand = new SecureRandom();
    }  

    // Getters and Setters
    public String getMove(){return lastMove;}
    public int getTurn(){return turn;}
    public void setTurn(int turn){this.turn=turn;}
    public Villain getVillain() {return challenger;}
    public int getAtk() {return atk;}
    public void setAtk(int atk) {this.atk = atk;}
    public int getDef() {return def;}
    public void setDef(int def) {this.def = def;}

    // Create a challenger from the villain class
    public void newChallenger()
    {
        numOppsLeft--;
        if(numOppsLeft == 1) challenger = new Boss();
        else if(numOppsLeft > 1) challenger = new SupportCharacter(2000*playerLevel/2,atk*playerLevel/2,def*2);
    }
    
    // Print Challengers Hp using "|"
    public void printChallengerHP(int health)
    {
        clearScreen();
        out.print(challenger.getName() + "'s Hp:");
        for(int i = 0; i < 26-challenger.getName().length()-6; i++) out.print(" ");
        for(int i = 0; i < health/40; i++) out.print("|");
        out.println("");
    }

    // Print Player's Hp using "|"
    public void printPlayerHP(int health)
    {
        out.print("Player Hp:                ");
        for(int i = 0; i < health/40; i++) out.print("|");
        out.println("");
    }
    
    public void printHP(int challenger,int player) // print bars using custom health
    { 
        printChallengerHP(challenger);
        printPlayerHP(player);
    }

    public void printHP() // print bars using current health
    { 
        printChallengerHP(challenger.getHp());
        printPlayerHP(hp);
    }

    // Get user input for type of Attack
    public void input()
    {
        printHP();
        out.println("\nPossible Moves:");
        printArray(moves);
        String input = scan.nextLine().toLowerCase();
        if(input.length() <= 0) input();
        else if(input.charAt(0) == 'a' || input.contains("attack"))
        {
            chooseAttack();
            return;
        }
        else if(input.charAt(0) == 'p' || input.contains("potion"))
        {
            choosePotion();
            return;
        }
        else if(input.charAt(0) == 'b' || input.contains("block")) 
        {
            lastMove = "block";
            turn++;
            return;
        }
        else if(input.charAt(0) == 'g' || input.contains("give")) playAgain();
        input();
    }

    public void chooseAttack()
    {
        printHP();
        out.println("\nTypes of Attacks:");
        printArray(attackMenu);
        String choice = scan.nextLine().toLowerCase();
        if(choice.length() <= 0) chooseAttack();
        else if(choice.contains("basic") || choice.charAt(0) == 'b')
        {
            basic();
            return;
        }
        else if(choice.contains("counter"))
        {
            potionUsed = false;
            lastMove = "counter";
            turn++;
            return;
        }
        else if(choice.contains("slap") || choice.charAt(0) == 's')
        {
            slap();
            return;
        }
        else if(choice.contains("exit") || choice.charAt(0) == 'e') 
        {
            input();
            return;
        }
    }

    public void basic()
    {
        int rand = srand.nextInt(10);
        int dmg = rand == 1 ? atk+critDmg:atk;
        int cOldHp = challenger.getHp();
        int pOldHp = hp;
        if(turn % 2 == 0)
        {
            potionUsed = false;
            challenger.setHp(challenger.getHp()-dmg);
            out.println("You did " + dmg + " damage!");
            lastMove = "basic";
            turn++;
            for(int i = 0; i < 3; i++) 
            {
                printHP(cOldHp,pOldHp);
                out.println("\nYou did " + dmg + " damage!");
                mDelay(200);
                printHP();
                out.println("\nYou did " + dmg + " damage!");
                mDelay(200);
            }
        }
        else{
            hp-=dmg;
            challenger.setLastMove("basic");
            out.println(challenger.getName() + " used a basic attack a did " + dmg + " damage!");
            turn++;
            for(int i = 0; i < 3; i++) 
            {
                printHP(cOldHp,pOldHp);
                out.println(challenger.getName() + " used a basic attack a did " + dmg + " damage!");
                mDelay(200);
                printHP();
                out.println(challenger.getName() + " used a basic attack a did " + dmg + " damage!");
                mDelay(200);
            }
        }
        sDelay(1);
    }

    public void slap()
    {
        if(turn % 2 == 0)
        {
            potionUsed = false;
            out.println("You used slap! It caused emotional damage to " + challenger.getName() +"!");
            lastMove = "slap";
            challenger.setHp(challenger.getHp()-1);
            challenger.setAtk(challenger.getAtk()-5);
            turn++;
            sDelay(2);
        }
        else
        {
            out.println("You got slapped by " + challenger.getName() + "!");
            hp--;
            atk-=5;
            turn++;
        }
    }

    public static void sDelay(int seconds)
    {
        try{SECONDS.sleep(seconds);}
        catch(Exception InterruptedException){out.println("Delay cancelled");}
    }

    private void mDelay(int milliseconds)
    {
        try{MILLISECONDS.sleep(milliseconds);}
        catch(Exception InterruptedException){out.println("Cancelled");}
    }

    public void choosePotion()
    {
        printHP(challenger.getHp(),hp);
        out.println("\nTypes of Potions: ");
        printArray(potionMenu);
        String choice = scan.nextLine().toLowerCase();
        lastMove = "potion";
        if(potionUsed)
        {
            out.println("You already used a potion this turn!");
            sDelay(2);
            input();
            return;
        }
        else if(choice.contains("atk") || choice.charAt(0) == 'a') 
        {
            int nextRand = srand.nextInt(95)+5;
            potionUsed = true;
            atk += nextRand;
            turn++;
            atkDmgAnimation();
            printHP();
            out.println("\nYou used an attack Potion. Your attack is now " + atk + " damage");
            sDelay(2);
            return;
        }
        else if(choice.contains("defense") || choice.charAt(0) == 'd')
        {
            int nextRand = 1-srand.nextInt(20)/100;
            potionUsed = true;
            def += challenger.getAtk()*nextRand;
            turn++;
            out.println("\nYou used a defense potion. Your defense is now " + def);
            sDelay(2);
            return;
        }
        else if(choice.contains("heal") || choice.charAt(0) == 'h')
        {
            int nextRand = srand.nextInt(10);
            potionUsed = true;
            hp *= nextRand;
            turn++;
            out.println("\nYou used a healing potion. Your health is now " + hp);
            sDelay(2);
            return;
        }
        else if(choice.contains("exit") || choice.charAt(0) == 'e') 
        {
            input(); 
            return;
        }
        out.println("Please enter a valid input");
        choosePotion();
    }

    public void printWin()
    {
        clearScreen();
        out.println("*       *   *****       *       *      *             *   ***********   *        *");
        out.println(" *     *  *       *     *       *                             *        * *      *");
        out.println("  *   *  *         *    *       *       *           *         *        *  *     *");
        out.println("   * *   *         *    *       *                             *        *   *    *");
        out.println("    *    *         *    *       *        *    *    *          *        *    *   *");
        out.println("    *    *         *    *       *            * *              *        *     *  *");
        out.println("    *     *       *      *     *          * *   * *           *        *      * *");
        out.println("    *       *****         *****            *     *       ***********   *        *");
    }

    public static void printLoss()
    {
        clearScreen();
        out.println("*       *   *****       *       *      *             *****      *******   ******");
        out.println(" *     *  *       *     *       *      *           *      *   *           *     ");
        out.println("  *   *  *         *    *       *      *          *        *  *           *     ");
        out.println("   * *   *         *    *       *      *          *        *     *        ******");
        out.println("    *    *         *    *       *      *          *        *        *     *     ");
        out.println("    *    *         *    *       *      *          *        *           *  *     ");
        out.println("    *     *       *      *     *       *           *      *            *  *     ");
        out.println("    *       *****         *****        *********     *****     *******    ******");
    }

    public String winner()
    {
        if(challenger.getHp() <= 0) 
        {
            if(numOppsLeft <= 0)return "player";
            else 
            {
                hp *= 1.5;
                playerLevel++;
                turn = 0;
                newChallenger(); 
                return "n";
            }
        }
        else if(hp <= 0) return "boss";
        else return "n";
    }

    public void playAgain()
    {
        clearScreen();
        out.println("Do you want to play again?(Y/n)");
        String user = scan.nextLine(); //Get user input
        if(user.length() > 0) // check that input is longer than 0 characters
        {
            user.toLowerCase(); //set input to lowercase
            if(user.charAt(0) == 'y') Main.play(); //if user wants to play again run main function
            else if(user.charAt(0) == 'n') { //otherwise close scanner and exit system
                scan.close();
                exit(0);
            }
        }
        out.println("Please enter a valid input"); //if input is invalid tell user to enter a valid input and rerun function
        playAgain();
    }

    public void atkDmgAnimation()
    {
        printHP();
        out.println("   ");
        out.println("   ");
        out.println("   ");
        out.println(" O ");
        out.println("!T!");
        out.println(" |");
        out.println("/ \\ ");
        mDelay(400);
        printHP();
        out.println("   ");
        out.println("   ");
        out.println("ATK++");
        out.println(" O ");
        out.println("!T!");
        out.println(" | ");
        out.println("/ \\ ");
        mDelay(400);
        printHP();
        out.println("   ");
        out.println("ATK++");
        out.println("   ");
        out.println(" O ");
        out.println("!T!");
        out.println(" | ");
        out.println("/ \\ ");
        mDelay(400);
        printHP();
        out.println("ATK++");
        out.println("   ");
        out.println("   ");
        out.println(" O ");
        out.println("!T!");
        out.println(" | ");
        out.println("/ \\ ");
    }

    private void printArray(String[] arr) {out.println(Arrays.toString(arr));}

    public static void clearScreen()
    {
        out.print("\033[H\033[2J");
        out.flush();
        out.print("\u001b[H");
    }
}
