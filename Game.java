import static java.lang.System.*;
import static java.util.concurrent.TimeUnit.*;
import java.util.*;
import java.security.*;

/*
Password for Dungeon is "Secret Word"

Ideas
animation
type i for information of current Screen
s for stats
commenting
line spacing
*/

class Game 
{
    private int numOppsLeft, turn = 0; // Important Variables for game
    final static String[] moves = {"Attack","Block","Potions","Give Up"}, attackMenu = {"Basic","Counter Attack","Slap","Exit"}, potionMenu = {"ATK DMG","Defense","Heal","Exit"}; // Types of moves
    private static Scanner scan;    // Scanner
    static SecureRandom srand = new SecureRandom();  // Secure RNG
    private Player challenger; // Current Challenger
    private Player player; // Player

    Game(Player player) // Game Constructor
    {
        scan = new Scanner(System.in); // Initialize scanner
        this.player = player; // Initialize player
        
        String user;
        while(true)
        {
            out.print("\nChoose the number of Opponents(1-5): "); // Allow Player to choose length of game
            user = scan.nextLine();
            if(user.length() > 0) break; // Check that user had input
        }
        int length = Character.isDigit(user.charAt(0)) ? Integer.parseInt(String.valueOf(user.charAt(0))):3; // If user inputted an invalid character set rounds to 3
        if(length > 5) length = 5; // Set rounds to 5 is rounds is > 5
        else if(length < 1) length = 1; // Set rounds to 1 is rounds is < 1
        numOppsLeft = length; // Set num of opponents to number of rounds

        newChallenger();
    }  

    // Getters and Setters
    int getTurn() {return turn;}
    void setTurn(int turn) {this.turn=turn;}
    Player getVillain() {return challenger;}

    private void newChallenger() // Create a challenger from the villain class
    {
        clearScreen();
        challenger = new Player(2000*player.getLevel()/2,player.getAtk()*player.getLevel()/2,player.getDef()*2); // Create a support character
        if(numOppsLeft == 1) 
        {
            out.println("BOSS LEVEL\n");
            challenger.setBoss(2*player.getLevel()*(player.getAtk()+player.getDef()+player.getHp())); // Set boss parameters override current parameters
        }
        else if(numOppsLeft == 2) 
        {
            out.println("The BOSS is Approaching!\n");
        }
        out.println("A new challenger approaches you!");
        mDelay(3000); // Buffer
    }
    
    private void blink(int cOriginal, int pOriginal, String message) // Creates a blink effect on health bars
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

    private void printChallengerHP(int health) // Print Challengers Hp using "|"
    {
        clearScreen();
        out.println(challenger.getName() + "'s HP:");
        for(int i = 0; i < health/40; i++) out.print("|");
        out.println("\n");
    }

    private void printPlayerHP(int health) // Print Player's Hp using "|"
    {
        out.println(player.getName() + "'s HP:");
        for(int i = 0; i < health/40; i++) out.print("|");
        out.println("");
    }
    
    private void printHP(int challenger,int player) // Print bars using custom health
    { 
        printChallengerHP(challenger);
        printPlayerHP(player);
    }

    void printHP() // Print bars using current health
    { 
        printChallengerHP(challenger.getHp());
        printPlayerHP(player.getHp());
    }

    void input() // Get user input for type of Attack
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
            player.setLastMove("block"); // Set move to block
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

    void chooseAttack() // User chooses attack and appropriate function is called
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
            player.setLastMove("counter"); // Set last Move to counter attack
            out.println("You used counter!");
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

    void choosePotion() // User chooses potion and appropriate function is called
    {
        printHP(challenger.getHp(),player.getHp());
        out.println("\nNumber of Remaining Potions: " + player.getPotions());
        out.println("\nTypes of Potions: ");
        printArray(potionMenu); // List potions
        String choice = scan.nextLine().toLowerCase(); // Get user Input
        if(player.getLastMove().equals("potion")) // Check that potion was not already played this turn
        {
            out.println("You already used a potion this turn!\n");
            buffer();
            input(); // Return to user input function
            return;
        }
        else if(player.getPotions() <= 0) // Check that there are enough potions left
        {
            out.println("You have no more potions left\n");
            buffer();
            input(); // Return to user input function
            return;
        }
        else if(choice.contains("atk") || choice.charAt(0) == 'a') // ATK potion
        {
            player.setLastMove("potion");
            atkPotion(); // Go to atk potion function
            return;
        }
        else if(choice.contains("defense") || choice.charAt(0) == 'd') // Defense Potion
        {
            player.setLastMove("potion");
            dPotion(); // Go to def potion function
            return;
        }
        else if(choice.contains("heal") || choice.charAt(0) == 'h') // Healing
        {
            player.setLastMove("potion");
            hpPotion();
            return;
        }
        else if(choice.contains("exit") || choice.charAt(0) == 'e') // Exit
        {
            input(); // Return to user input function
            return;
        }
        out.println("Please enter a valid input");
        choosePotion(); // If input does not match criteria rerun function
    }

    void basic() // Basic Attack need to add counter attack
    {
        if(!miss()) // Check if attack missed
        {
            int critChance = srand.nextInt(10); // Determine Random Crit Chance Number
            int cOriginal = challenger.getHp(); // Store computer hp before damage
            int pOriginal = player.getHp(); // Store player hp before damage
            if(turn % 2 == 0) // Player Turn
            {
                int dmg = critChance == 1 ? player.getAtk()+player.getAtk()/3-challenger.getDef():player.getAtk()-challenger.getDef(); // Determine amount of Damage
                dmg = dmg < 0 ? 0:dmg; // Check if damage is negative and set dmg to 0 if it is
                dmg = checkSpecial(turn,dmg);
                if(!challenger.getLastMove().equals("counter"))
                {
                    challenger.setHp(challenger.getHp()-dmg); // Set hp
                    out.println("You did " + dmg + " damage!");
                    blink(cOriginal,pOriginal,"\nYou did " + dmg + " damage!"); // Blink Health Bar Animation
                    buffer();
                }
                player.setLastMove("basic");; // Set Last move
            }
            else // Challenger Turn
            {
                int dmg = critChance == 1 ? challenger.getAtk()+challenger.getAtk()/3:challenger.getAtk(); // Determine amount of Damage
                dmg -= player.getDef();
                dmg = dmg < 0 ? 0:dmg;
                dmg = checkSpecial(turn,dmg);
                if(!player.getLastMove().equals("counter"))
                {
                    player.setHp(player.getHp()-dmg);
                    out.println("\n" + challenger.getName() + " used a basic attack a did " + dmg + " damage!");
                    blink(cOriginal,pOriginal,"\n" + challenger.getName() + " used a basic attack and did " + dmg + " damage!");
                    buffer();
                }
                challenger.setLastMove("basic");
            }
        }
        turn++;
    }

    private int checkSpecial(int turn, int dmg)
    {
        int cOriginal = challenger.getHp(); // Store computer hp before damage
        int pOriginal = player.getHp(); // Store player hp before damage
        int counterChance = srand.nextInt(2);

        if(turn % 2 == 0)
        {
            if(challenger.getLastMove().equals("block")) // Check if challenger's move was block
            {
                int blockAmount = block(); // Determine block percentage
                dmg *= blockAmount; // Multiply attack dmg by block percentage and divide by 100
                dmg/=100;
                out.println("\n" + challenger.getName() + " blocked " + (100-blockAmount) + "% of your attack");
                buffer();
            }
            else if(challenger.getLastMove().equals("counter") && counterChance == 1) // If opponent's last move was counter attack
            {
                double counterAmount = challenger.getAtk()*3/2;
                player.setHp((int)(player.getHp()-counterAmount));
                out.println("\n" + challenger.getName() + " used counter attack! You took " + (pOriginal-player.getHp()) + " damage!");
                buffer();
            }
        }
        else
        {
            if(player.getLastMove().equals("block"))
            {
                int blockAmount = block();
                dmg *= blockAmount;
                dmg /= 100;
                out.println("\nYou blocked " + (100 - blockAmount) + "% of " + challenger.getName() + "'s attack");
                buffer();
            }
            else if(player.getLastMove().equals("counter") && counterChance == 1) // If your last move was counter attack
            {
                double counterAmount = player.getAtk()*3/2;
                challenger.setHp((int)(challenger.getHp()-counterAmount));
                out.println("\nYou used counter attack!" + challenger.getName() + " took " + (cOriginal-challenger.getHp()) + " damage!");
                buffer();
            }
        }
        return dmg;
    }

    boolean miss() // Create a chance at missing attack
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
                    buffer();
                    break;
                case 1: // Challenger Turn
                    out.println("\n" + challenger.getName() + " Missed");
                    buffer();
                    break;
            }
            return true;
        }
        return false;
    }

    void slap() // Slap Attack Logic
    {
        int cOriginal = challenger.getHp(),pOriginal=player.getHp(); // Store old Health
        if(turn % 2 == 0) // Player turn
        {
            out.println("\nYou used slap! It caused emotional damage to " + challenger.getName() +"!");
            player.setLastMove("slap");; // Set last move
            int hpAmount = challenger.getHp()/10-challenger.getDef(); // Helper variable
            int atkAmount = challenger.getAtk()/20+challenger.getDef()/20; // Helper Variable
            hpAmount = hpAmount < 0 ? 0 : hpAmount; // Determine if variable is negative
            atkAmount = atkAmount < 0 ? 0 : atkAmount; // Determine if variable is negative
            challenger.setHp(challenger.getHp()-hpAmount); // Set Health
            challenger.setAtk(challenger.getAtk()-atkAmount); // Set Attack
            challenger.setDef(challenger.getDef()/2); // Set defense
            blink(cOriginal,pOriginal,"\nYou used slap! It caused emotional damage to " + challenger.getName() +"!"); // Create blink
        }
        else // Challenger Slap same as Player slap but applied on player
        {
            out.println("\nYou got slapped by " + challenger.getName() + "!");
            int hpAmount = player.getHp()/10-player.getDef();
            int atkAmount = player.getAtk()/20-player.getDef()/20;
            hpAmount = hpAmount < 0 ? 0 : hpAmount;
            atkAmount = atkAmount < 0 ? 0 : atkAmount;
            player.setHp(player.getHp()-hpAmount);
            player.setAtk(player.getAtk()-atkAmount);
            player.setDef(player.getDef()/2);
            blink(cOriginal,pOriginal,"\nYou got slapped by " + challenger.getName() + "!");
        }
        turn++;
        buffer();
    }

    private int block() // Return a random block percentage up to 80% with min of 25%
    {
        return 100 - (srand.nextInt(55) + 25);
    }

    void dPotion() // Defense Potion Logic
    {
        double nextRand = 1.1+srand.nextInt(20)/100; // Get random defense percentage starting at 10% to a max of 30%
        potionAnimation(challenger.getHp(),player.getHp(),"DEF"); // Animate def increase
        if(turn%2==0) // User turn
        {
            player.setPotions(player.getPotions()-1); // Decrease amount of potions
            player.setDef((int)(player.getDef()*nextRand)); // Apply Defense increase
            printHP();
            out.println("\nYou used a defense potion. Your defense is now " + player.getDef());
        }
        else // Challenger turn
        {
            challenger.setPotions(challenger.getPotions()-1); // Decrease computer potions left
            challenger.setDef((int)(challenger.getDef()*nextRand)); // Set Computer Defense
            printHP();
            out.println("\n" + challenger.getName() + " used a defense potion. Their defense is now " + challenger.getDef());
        }
        buffer();
    }

    void atkPotion() // Attack Potion Logic
    {
        double nextRand = srand.nextInt(25)/100+1.05; // Get random percentage starting at 5% up to 30%
        int originalAtk = turn%2 == 0 ? player.getAtk():challenger.getAtk(); //Determine original Attack Amount
        potionAnimation(challenger.getHp(),player.getHp(),"ATK");
        if(turn % 2 == 0)
        {
            player.setPotions(player.getPotions()-1); // Decrement Num of Potions
            player.setAtk((int)(player.getAtk()*nextRand)); // Apply Attack
            printHP();
            out.println("\nYou used an Attack Potion and your attack increased by " + (player.getAtk()-originalAtk) + " damage");
        }
        else
        {
            challenger.setPotions(challenger.getPotions()-1); // Decrease computer potions
            challenger.setAtk((int)(challenger.getAtk()*nextRand)); // Set computer attack
            printHP();
            out.println("\n" + challenger.getName() + " used an Attack Potion. Their Attack increased by " + (challenger.getAtk()-originalAtk) + " damage");
        }
        buffer();
    }

    void hpPotion() // Health Potion
    {
        double increase = srand.nextInt(60)/100+1.15; // Random percentage of health to gain back starting at 15% up to 75%
        int cOriginal=challenger.getHp(),pOriginal=player.getHp();
        if(turn%2==0)
        {
            player.setPotions(player.getPotions()-1); // Decrement potions
            player.setHp((int)(player.getHp()*increase)); // Apply percentage to hp
            printHP();
            potionAnimation(cOriginal,pOriginal,"HP"); // Animate
            out.println("\nYou used a healing potion and gained " + (player.getHp()-pOriginal) + " health");
        }
        else
        {
            challenger.setPotions(challenger.getPotions()-1); // Decrease Computer Potions
            challenger.setHp((int)(challenger.getHp()*increase)); // Set computer hp
            printHP();
            potionAnimation(cOriginal,pOriginal,"HP"); // Animate Health bar
            out.println("\n" + challenger.getName() + " used a healing potion and gained " + (challenger.getHp()-cOriginal) + " health");
        }
        buffer();
    }

    private void mDelay(int milliseconds) // Create a delay in MS
    {
        try{MILLISECONDS.sleep(milliseconds);}
        catch(Exception InterruptedException){out.println("Cancelled");}
    }

    void printWin() // Print Player WIN
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
        buffer();
    }

    static void printLoss() // Print Player Loss
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
        buffer();
    }

    String winner() // Return winner
    {
        if(challenger.getHp() <= 0) 
        {
            numOppsLeft--;
            if(numOppsLeft <= 0)return "player"; // Return Player Wins
            else 
            {
                player.setHp((int)(player.getHp()*1.5)); // Give player 150% of their current HP
                player.setLevel(player.getLevel()+1); // Increase Player Level
                turn = 0; // Set turn back to 0 in the case that it ends on an even turn
                player.setPotions(player.getPotions()+3); // Increase Number of Potions
                newChallenger(); // Create another Challenger
                return "n"; // Return No winner
            }
        }
        else if(player.getHp() <= 0) return "boss"; // Return Boss Wins
        return "n"; // Return No one won
    }

    void playAgain() // Play Again Function
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

    void potionAnimation(int cOriginal,int pOriginal,String type) // Animates Potion Effects
    {
        String body = turn % 2 == 0 ? "!T!  <-- YOU" : "!T!  <-- " + challenger.getName();
        printHP();
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
        printHP();
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
        mDelay(400);
        blink(cOriginal,pOriginal,"");
    }

    private void printArray(String[] arr) {out.println(Arrays.toString(arr));} // Print given Array to String

    static void buffer() // Add a user input buffer
    {
        out.println("\n\nPress Enter to continue");
        Main.scan.nextLine();
    }

    static void clearScreen() // Clear Screen
    {
        out.print("\033[H\033[2J");  // Clear Screen
        out.flush();                 // Flush Screen / Memory
        out.print("\u001b[H");       // Set cursor to top
    }
}
