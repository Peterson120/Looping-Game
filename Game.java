import static java.lang.System.*;
import static java.util.concurrent.TimeUnit.*;
import java.util.*;
import java.security.*;

/*
Password for Dungeon is "Secret Word"
Ideas
Counter Attack 50% chance + double damage only if opponent attacks on next turn
animation
*/

public class Game 
{
    private int numOppsLeft = 5, turn = 0, hp = 500,atk = 40,def = 5,playerLevel = 1, critDmg = atk/2, numPotions = 5; // Important Variables for Player
    final public static String[] moves =  {"Attack","Block","Potions","Give Up"}, attackMenu = {"Basic","Counter Attack","Slap","Exit"}, potionMenu = {"ATK DMG","Defense","Heal","Exit"}; // Types of moves
    private static Scanner scan; // Scanner
    public static SecureRandom srand; // Secure RNG
    private Villain challenger; // Current Challenger
    private String lastMove = "none",name; //Last move to check for blocks and counters

    public Game(String name) // Game Constructor to set scanner, RNG, and a new challenger
    {
        scan = new Scanner(System.in);
        srand = new SecureRandom();
        this.name = name;
        out.print("\nChoose a length(1-5): ");
        int length = Integer.valueOf(scan.nextLine());
        if(length > 5) length = 5;
        else if(length < 1) length = 1;
        numOppsLeft = length;
        newChallenger();
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

    private void newChallenger() // Create a challenger from the villain class
    {
        clearScreen();
        numOppsLeft--; // Decrease Opponents Left
        if(numOppsLeft == 0) 
        {
            out.println("BOSS LEVEL");
            challenger = new Boss(); // Create Boss if Only One Challenger Remains
            challenger.setBoss(2*playerLevel*(atk+def+hp)); // Set boss parameters
            sDelay(2);
        }
        else if(numOppsLeft == 1) 
        {
            out.println("The BOSS is Approaching!"); 
            challenger = new SupportCharacter(2000*playerLevel/2,atk*playerLevel/2,def*2); // Create a support character
            sDelay(2);
        }
        else if(numOppsLeft > 1) challenger = new SupportCharacter(2000*playerLevel/2,atk*playerLevel/2,def*2); // Create a regular Character
    }
    
    private void printChallengerHP(int health) // Print Challengers Hp using "|"
    {
        clearScreen();
        out.print(challenger.getName() + "'s HP:");
        for(int i = 0; i < 10-challenger.getName().length(); i++) out.print(" ");
        for(int i = 0; i < health/40; i++) out.print("|");
        out.println("");
    }

    private void printPlayerHP(int health) // Print Player's Hp using "|"
    {
        out.print(name + "'s HP:");
        for(int i = 0; i < 10-name.length(); i++) out.print(" ");
        for(int i = 0; i < health/40; i++) out.print("|");
        out.println("");
    }
    
    private void printHP(int challenger,int player) // Print bars using custom health
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
        printArray(moves); // List possible moves
        String input = scan.nextLine().toLowerCase(); // Get input and make into lowercase
        if(input.length() <= 0) input(); // Check that input is greater than length 0
        else if(input.charAt(0) == 'a' || input.contains("attack")) // Attack
        {
            chooseAttack(); // Go to Attack Menu
            return;
        }
        else if(input.charAt(0) == 'p' || input.contains("potion")) // Potion
        {
            choosePotion(); // Go to Potion Menu
            return;
        }
        else if(input.charAt(0) == 'b' || input.contains("block")) // Block
        {
            lastMove = "block"; // Set move to block
            turn++; // Increase turn
            return;
        }
        else if(input.charAt(0) == 'g' || input.contains("give")) // Give Up
        {
            clearScreen();
            out.println("Are you sure you want to give up?(y/N)");
            String user = scan.nextLine(); // Confirmation
            if(user.charAt(0) == 'y')
            {
                printLoss();
                playAgain();
            }
        }
        input(); // If nothing happened, rerun method
    }

    public void chooseAttack() // User chooses attack and appropriate function is called
    {
        printHP();
        out.println("\nTypes of Attacks:");
        printArray(attackMenu); // List Attacks
        String choice = scan.nextLine().toLowerCase(); // Get User input
        if(choice.length() <= 0) chooseAttack(); // Check that input has length greater than 0
        else if(choice.contains("basic") || choice.charAt(0) == 'b') // Basic Attack
        {
            basic(); // Go to basic attack method
            return;
        }
        else if(choice.contains("counter")) // Counter Attack
        {
            lastMove = "counter"; // Set last Move to counter attack
            turn++; // Increment turn
            return;
        }
        else if(choice.contains("slap") || choice.charAt(0) == 's') // Slap Attack
        {
            slap(); // Go to slap function
            return;
        }
        else if(choice.contains("exit") || choice.charAt(0) == 'e') // Exit Menu
        {
            input(); // Return to input()
            return;
        }
    }

    public void choosePotion() // User chooses potion and appropriate function is called
    {
        printHP(challenger.getHp(),hp);
        out.println("\nTypes of Potions: ");
        printArray(potionMenu); // List potions
        String choice = scan.nextLine().toLowerCase(); // Get user Input
        if(lastMove.equals("potion")) // Check that potion was not already played this turn
        {
            out.println("You already used a potion this turn!");
            sDelay(2);
            input(); // Return to user input function
            return;
        }
        else if(numPotions <= 0) //Check that there are enough potions left
        {
            out.println("You have no more potions left");
            sDelay(2);
            input(); // Return to user input function
            return;
        }
        else if(choice.contains("atk") || choice.charAt(0) == 'a') // ATK potion
        {
            lastMove = "potion";
            atkPotion(); // Go to atk potion function
            return;
        }
        else if(choice.contains("defense") || choice.charAt(0) == 'd') // Defense Potion
        {
            lastMove = "potion";
            dPotion(); // Go to def potion function
            return;
        }
        else if(choice.contains("heal") || choice.charAt(0) == 'h') // Healing
        {
            lastMove = "potion";
            hpPotion();
            return;
        }
        else if(choice.contains("exit") || choice.charAt(0) == 'e') // Exit
        {
            input(); // Return to user input function
            return;
        }
        out.println("Please enter a valid input");
        choosePotion();
    }

    public void basic() // Basic Attack
    {
        miss(); // Check if attack missed
        int critChance = srand.nextInt(10); // Determine Random Crit Chance Number
        int cOriginal = challenger.getHp(); // Store computer hp before damage
        int pOriginal = hp; // Store player hp before damage
        if(turn % 2 == 0) // Player Turn
        {
            int dmg = critChance == 1 ? atk+critDmg-challenger.getDef():atk-challenger.getDef(); // Determine amount of Damage
            dmg = dmg < 0 ? 0:dmg; // Check if damage is negative and set dmg to 0 if it is
            if(challenger.getLastMove().equals("block")) // Check if challenger's move was block
            {
                int blockAmount = block(); // Determine block percentage
                dmg *= blockAmount; // Multiply attack dmg by block percentage and divide by 100
                dmg/=100;
                out.println("\n" + challenger.getName() + " blocked " + (100-blockAmount) + "% of your attack");
                sDelay(2);
            }
            challenger.setHp(challenger.getHp()-dmg); // Set hp
            out.println("You did " + dmg + " damage!");
            lastMove = "basic"; // Set Last move
            turn++; // Increment turn
            blink(cOriginal,pOriginal,"\nYou did " + dmg + " damage!"); // Blink Health Bar Animation
        }
        else // Challenger Turn
        {
            int dmg = critChance == 1 ? challenger.getAtk()+critDmg:challenger.getAtk(); // Determine amount of Damage
            dmg -= def;
            dmg = dmg < 0 ? 0:dmg;
            if(lastMove.equals("block"))
            {
                int blockAmount = block();
                dmg *= blockAmount;
                dmg /= 100;
                out.println("\nYou blocked " + (100 - blockAmount) + "% of " + challenger.getName() + "'s attack");
                sDelay(2);
            }
            hp-=dmg;
            challenger.setLastMove("basic");
            out.println("\n" + challenger.getName() + " used a basic attack a did " + dmg + " damage!");
            turn++;
            blink(cOriginal,pOriginal,"\n" + challenger.getName() + " used a basic attack and did " + dmg + " damage!");
        }
        sDelay(1);
    }

    private void blink(int cOriginal, int pOriginal, String message) // Creates a blink effect
    {
        for(int i = 0; i < 3; i++) // Loop x amount of times
        {
            printHP(cOriginal,pOriginal);
            out.println(message);
            mDelay(200);
            printHP();
            out.println(message);
            mDelay(200);
        }
    }

    public void miss() // Create a chance at missing attack
    {
        int missChance = srand.nextInt(100); // Determine Random Miss Chance Number
        if(missChance == 1) // 1% chance of missing attack
        {
            clearScreen();
            printHP();
            switch(turn%2) // Determine player turn
            {
                case 0:
                    out.println("\nYou Missed");
                    turn++;
                    sDelay(2);
                    challenger.turn();
                case 1: // Challenger Turn
                    out.println("\n" + challenger.getName() + " Missed");
                    turn++;
                    sDelay(2);
                    input();
            }
        }
    }

    public void slap() // Slap Attack Logic
    {
        int cOriginal = challenger.getHp(),pOriginal=hp; // Store old Health
        if(turn % 2 == 0) // Player turn
        {
            out.println("\nYou used slap! It caused emotional damage to " + challenger.getName() +"!");
            lastMove = "slap"; // Set last move
            int hpAmount = challenger.getHp()/10-challenger.getDef(); // Helper variable
            hpAmount = hpAmount < 0 ? 0 : hpAmount; // Determine if variable is negative
            int atkAmount = challenger.getAtk()/20+challenger.getDef()/20; // Helper Variable
            atkAmount = atkAmount < 0 ? 0 : atkAmount; // Determine if variable is negative
            challenger.setHp(challenger.getHp()-hpAmount); // Set Health
            challenger.setAtk(challenger.getAtk()-atkAmount); // Set Attack
            turn++; // Increment turn
            blink(cOriginal,pOriginal,"\nYou used slap! It caused emotional damage to " + challenger.getName() +"!"); // Create blink
        }
        else // Challenger Slap
        {
            out.println("\nYou got slapped by " + challenger.getName() + "!");
            int hpAmount = hp/10-def;
            hpAmount = hpAmount < 0 ? 0 : hpAmount;
            int atkAmount = atk/20-def/20;
            atkAmount = atkAmount < 0 ? 0 : atkAmount;
            hp-=hpAmount;
            atk-=atkAmount;
            turn++;
            blink(cOriginal,pOriginal,"\nYou got slapped by " + challenger.getName() + "!");
        }
        sDelay(2);
    }

    private int block() // Return a random block percentage
    {
        return 100 - (srand.nextInt(75) + 25);
    }

    public void dPotion() // Defense Potion Logic
    {
        int nextRand = 1-srand.nextInt(20)/100;
        potionAnimation(challenger.getHp(),hp,"DEF");
        if(turn%2==0)
        {
            numPotions--;
            def += challenger.getAtk()*nextRand;
            printHP();
            out.println("\nYou used a defense potion. Your defense is now " + def);
        }
        else
        {
            challenger.setPotions(challenger.getPotions()-1);
            challenger.setDef(challenger.getDef()+nextRand);
            printHP();
            out.println("\n" + challenger.getName() + " used a defense potion. Their defense is now " + challenger.getDef());
        }
        sDelay(2);
    }

    public void atkPotion() // Attack Potion Logic
    {
        int nextRand = srand.nextInt(95)+5,originalAtk = turn%2 == 0 ? atk:challenger.getAtk();
        potionAnimation(challenger.getHp(),hp,"ATK");
        if(turn % 2 == 0)
        {
            numPotions--;
            atk += nextRand;
            printHP();
            out.println("\nYou used an Attack Potion and your attack increased by " + (atk-originalAtk) + " damage");
        }
        else
        {
            challenger.setPotions(challenger.getPotions()-1);
            challenger.setAtk(challenger.getAtk()+nextRand);
            printHP();
            out.println("\n" + challenger.getName() + " used an Attack Potion. Their Attack increased by " + (challenger.getAtk()-originalAtk) + " damage");
        }
        sDelay(2);
    }

    public void hpPotion() // Health Potion
    {
        double increase = srand.nextInt(100)/100+0.25;
        int cOriginal=challenger.getHp(),pOriginal=hp;
        if(turn%2==0)
        {
            numPotions--;
            hp += pOriginal*increase;
            printHP();
            potionAnimation(cOriginal,pOriginal,"HP");
            out.println("\nYou used a healing potion and gained " + (hp-pOriginal) + " health");
        }
        else
        {
            challenger.setPotions(challenger.getPotions()-1);
            challenger.setHp((int)(cOriginal+challenger.getHp()*increase));
            printHP();
            potionAnimation(cOriginal,pOriginal,"HP");
            out.println("\n" + challenger.getName() + " used a healing potion and gained " + (challenger.getHp()-cOriginal) + " health");
        }
        sDelay(2);
    }

    public static void sDelay(int seconds) // Create a delay in Seconds
    {
        try{SECONDS.sleep(seconds);}
        catch(Exception InterruptedException){out.println("Cancelled");}
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
        sDelay(4);
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
        sDelay(4);
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
                numPotions+=3; // Increase Number of Potions
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
            if(user.charAt(0) == 'y') Main.play(name); //if user wants to play again run main function
            else if(user.charAt(0) == 'n') { //otherwise close scanner and exit system
                scan.close();
                exit(0);
            }
        }
        out.println("Please enter a valid input"); //if input is invalid tell user to enter a valid input and rerun function
        playAgain();
    }

    public void potionAnimation(int cOriginal,int pOriginal,String type) // Animates Potion Effects
    {
        String body = turn % 2 == 0 ? "!T!  <-- YOU" : "!T!  <-- " + challenger.getName();
        printHP(cOriginal,pOriginal);
        out.println("\n\n\n\n O ");
        out.println(body);
        out.println(" |");
        out.println("/ \\ ");
        mDelay(400);
        printHP();
        out.println("\n\n\n" + type + "++");
        out.println(" O ");
        out.println(body);
        out.println(" | ");
        out.println("/ \\ ");
        mDelay(400);
        printHP(cOriginal,pOriginal);
        out.println("\n\n" + type + "++");
        out.println("\n O ");
        out.println(body);
        out.println(" | ");
        out.println("/ \\ ");
        mDelay(400);
        printHP();
        out.println("\n"+type + "++");
        out.println("\n\n O ");
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
