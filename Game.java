import static java.lang.System.*;
import static java.util.concurrent.TimeUnit.*;
import java.util.*;
import java.security.*;

/*
Ideas
Counter Attack 50% chance + crit if opponent attacks on next turn
block dmg
user name
*/

public class Game 
{
    private int numOppsLeft = 5, turn = 0, hp = 500,atk = 40,def = 5,playerLevel = 1, critDmg = atk/2, numPotions = 1; // Important Variables for Player
    private boolean potionUsed = false; // Check if potion was used this turn
    final public static String[] moves =  {"Attack","Block","Potions","Give Up"}, attackMenu = {"Basic","Counter Attack","Slap","Exit"}, potionMenu = {"ATK DMG","Defense","Heal","Exit"}; // Types of moves
    private static Scanner scan; // Scanner
    public static SecureRandom srand; // Secure RNG
    private Villain challenger; // Current Challenger
    private String lastMove = "none"; //Last move to check for blocks and counters

    public Game() // Game Constructor to set scanner, RNG, and a new challenger
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

    public void newChallenger() // Create a challenger from the villain class
    {
        numOppsLeft--; // Decrease Opponents Left
        numPotions+=2; // Increase Number of Potions
        if(numOppsLeft == 1) 
        {
            out.println("BOSS LEVEL");
            challenger = new Boss(); // Create Boss if Only One Challenger Remains
        }
        else if(numOppsLeft == 2) 
        {
            out.println("The BOSS is Approaching!"); 
            challenger = new SupportCharacter(2000*playerLevel/2,atk*playerLevel/2,def*2);
        }
        else if(numOppsLeft > 1) challenger = new SupportCharacter(2000*playerLevel/2,atk*playerLevel/2,def*2); // Create a regular Character
    }
    
    public void printChallengerHP(int health) // Print Challengers Hp using "|"
    {
        clearScreen();
        out.print(challenger.getName() + "'s Hp:");
        for(int i = 0; i < 26-challenger.getName().length()-6; i++) out.print(" ");
        for(int i = 0; i < health/40; i++) out.print("|");
        out.println("");
    }

    public void printPlayerHP(int health) // Print Player's Hp using "|"
    {
        out.print("Player Hp:                ");
        for(int i = 0; i < health/40; i++) out.print("|");
        out.println("");
    }
    
    public void printHP(int challenger,int player) // Print bars using custom health
    { 
        printChallengerHP(challenger);
        printPlayerHP(player);
    }

    public void printHP() // Print bars using current health
    { 
        printChallengerHP(challenger.getHp());
        printPlayerHP(hp);
    }

    public void input() // Get user input for type of Attack
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

    public void chooseAttack() // User chooses attack and appropriate function is called
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

    public void choosePotion() // User chooses potion and appropriate function is called
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
        else if(numPotions <= 0) 
        {
            out.println("You have no more potions left");
            sDelay(2);
            input();
            return;
        }
        else if(choice.contains("atk") || choice.charAt(0) == 'a') 
        {
            atkPotion();
            return;
        }
        else if(choice.contains("defense") || choice.charAt(0) == 'd')
        {
            dPotion();
            return;
        }
        else if(choice.contains("heal") || choice.charAt(0) == 'h')
        {
            hpPotion();
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

    public void basic() // Basic Attack
    {
        miss();
        int critChance = srand.nextInt(10); // Determine Random Crit Chance Number
        int dmg = critChance == 1 ? atk+critDmg:atk; // Determine amount of Damage
        int cOldHp = challenger.getHp(); // Store computer hp before damage
        int pOldHp = hp; // Store player hp before damage
        if(turn % 2 == 0)
        {
            dmg -= challenger.getDef();
            if(challenger.getLastMove().equals("block"))
            {
                double blockAmount = block();
                dmg *= blockAmount;
                out.println("\n" + challenger.getName() + " blocked " + blockAmount*100 + "% of your attack");
                sDelay(2);
            }
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
        else
        {
            dmg -= def;
            if(lastMove.equals("block"))
            {
                double blockAmount = block();
                dmg *= blockAmount;
                out.println("\nYou blocked " + blockAmount*100 + "% of" + challenger.getName() + "'s attack");
                sDelay(2);
            }
            hp-=dmg;
            challenger.setLastMove("basic");
            out.println("\n" + challenger.getName() + " used a basic attack a did " + dmg + " damage!");
            turn++;
            for(int i = 0; i < 3; i++) 
            {
                printHP(cOldHp,pOldHp);
                out.println("\n" + challenger.getName() + " used a basic attack a did " + dmg + " damage!");
                mDelay(200);
                printHP();
                out.println("\n" + challenger.getName() + " used a basic attack a did " + dmg + " damage!");
                mDelay(200);
            }
        }
        sDelay(1);
    }

    public void miss() // Create a chance at missing attack
    {
        int missChance = srand.nextInt(100); // Determine Random Miss Chance Number
        if(missChance == 1) // 1% chance of missing attack
        {
            clearScreen();
            printHP();
            switch(turn%2)
            {
                case 0:
                    out.println("\nYou Missed");
                    turn++;
                    sDelay(2);
                    challenger.turn();
                case 1:
                    out.println("\n" + challenger.getName() + " Missed");
                    turn++;
                    sDelay(2);
                    input();
            }
        }
    }

    public void slap() // Slap Attack Logic
    {
        if(turn % 2 == 0)
        {
            potionUsed = false;
            out.println("\nYou used slap! It caused emotional damage to " + challenger.getName() +"!");
            lastMove = "slap";
            challenger.setHp(challenger.getHp()-1);
            challenger.setAtk(challenger.getAtk()-5);
            turn++;
        }
        else
        {
            out.println("\nYou got slapped by " + challenger.getName() + "!");
            hp--;
            atk-=5;
            turn++;
        }
        sDelay(2);
    }

    private double block() // Return a random block percentage
    {
        return (srand.nextInt(75) + 25)/100;
    }

    public void dPotion() // Defense Potion Logic
    {
        int nextRand = 1-srand.nextInt(20)/100;
        potionAnimation("DEF");
        if(turn%2==0)
        {
            potionUsed = true;
            numPotions--;
            def += challenger.getAtk()*nextRand;
            printHP();
            out.println("\nYou used a defense potion. Your defense is now " + def);
        }
        else
        {
            challenger.setDef(challenger.getDef()+nextRand);
            printHP();
            out.println("\n" + challenger.getName() + " used a defense potion. Their defense is now " + challenger.getDef());
        }
        sDelay(2);
    }

    public void atkPotion() // Attack Potion Logic
    {
        int nextRand = srand.nextInt(95)+5,originalAtk = turn%2 == 0 ? atk:challenger.getAtk();
        potionAnimation("ATK");
        if(turn % 2 == 0)
        {
            potionUsed = true;
            numPotions--;
            atk += nextRand;
            printHP();
            out.println("\nYou used an Attack Potion and your attack increased by " + (atk-originalAtk) + " damage");
        }
        else
        {
            challenger.setAtk(challenger.getAtk()+nextRand);
            printHP();
            out.println("\n" + challenger.getName() + " used an Attack Potion. Their Attack increased by " + (challenger.getAtk()-originalAtk) + " damage");
        }
        sDelay(2);
    }

    public void hpPotion() // Health Potion
    {
        int nextRand = srand.nextInt(10)/5+1, originalHP = turn % 2 == 0 ? hp : challenger.getHp();
        potionAnimation("HP");
        if(turn%2==0)
        {
            potionUsed = true;
            numPotions--;
            hp *= nextRand;
            out.println("\nYou used a healing potion and gained " + (hp-originalHP) + " health");
        }
        else
        {
            challenger.setHp(challenger.getHp()*nextRand);
            out.println("\n" + challenger.getName() + " used a healing potion and gained " + (challenger.getHp()-originalHP) + " health");
        }
        sDelay(2);
    }

    public static void sDelay(int seconds) // Create a delay in Seconds
    {
        try{SECONDS.sleep(seconds);}
        catch(Exception InterruptedException){out.println("Delay cancelled");}
    }

    private void mDelay(int milliseconds) // Create a delay in MS
    {
        try{MILLISECONDS.sleep(milliseconds);}
        catch(Exception InterruptedException){out.println("Cancelled");}
    }

    public void printWin() // Print Player WIN
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

    public static void printLoss() // Print Player Loss
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

    public String winner() // Return winner
    {
        if(challenger.getHp() <= 0) 
        {
            if(numOppsLeft <= 0)return "player"; // Return Player Wins
            else 
            {
                hp *= 1.5; // Give player 150% of their current HP
                playerLevel++; // Increase Player Level
                turn = 0; // Set turn to 0
                numPotions+=2; // Increase Number of Potions
                newChallenger(); // Create another Challenger
                return "n"; // Return No winner
            }
        }
        else if(hp <= 0) return "boss"; // Return Boss Wins
        return "n"; // Return No one won
    }

    public void playAgain() // Play Again Function
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

    public void potionAnimation(String type) // Animates Potion Effects
    {
        String body = turn % 2 == 0 ? "!T!  <-- YOU" : "!T!  <-- OPP";
        printHP();
        out.println("   ");
        out.println("   ");
        out.println("   ");
        out.println(" O ");
        out.println(body);
        out.println(" |");
        out.println("/ \\ ");
        mDelay(400);
        printHP();
        out.println("   ");
        out.println("   ");
        out.println(type + "++");
        out.println(" O ");
        out.println(body);
        out.println(" | ");
        out.println("/ \\ ");
        mDelay(400);
        printHP();
        out.println("   ");
        out.println(type + "++");
        out.println("   ");
        out.println(" O ");
        out.println(body);
        out.println(" | ");
        out.println("/ \\ ");
        mDelay(400);
        printHP();
        out.println(type + "++");
        out.println("   ");
        out.println("   ");
        out.println(" O ");
        out.println(body);
        out.println(" | ");
        out.println("/ \\ ");
        mDelay(700);
    }

    private void printArray(String[] arr) {out.println(Arrays.toString(arr));} // Print given Array to String

    public static void clearScreen() // Clear Screen
    {
        out.print("\033[H\033[2J"); // Clear Screen
        out.flush(); // Flush Screen / Memory
        out.print("\u001b[H"); // Set cursor to top
    }
}
