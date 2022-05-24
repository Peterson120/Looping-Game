import java.util.Arrays;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;
import java.security.SecureRandom;
import java.io.IOException;

/*
Password for Dungeon is "Secret Word"

Game class to retain all functions used in the game

Ideas
animation
Split into more functions
*/

class Game 
{
    int tokens;
    private int numOppsLeft, turn = 0; // Important Variables for game
    final static String[] moves = {"Attack","Block","Potions","Give Up"}, attackMenu = {"Basic","Counter Attack","Slap","Exit"}, potionMenu = {"ATK DMG","Defense","Heal","Exit"}; // Types of moves
    private static BufferedReader br;    // Scanner
    static SecureRandom srand = new SecureRandom();  // Secure RNG
    private Player challenger; // Current Challenger
    private Player player; // Player

    // Game Constructor
    Game(Player player)	throws IOException
    {
		br = new BufferedReader(new InputStreamReader(System.in)); // Initialize scanner
		this.player = player; // Initialize player
        
        String user = "";
        while(user.length() <= 0)
        {
            System.out.print("\nChoose the number of Opponents(1-5): "); // Allow Player to choose length of game
            user = br.readLine();
        }
        int gameLength = Character.isDigit(user.charAt(0)) ? Integer.parseInt(String.valueOf(user.charAt(0))):3; // If user inputted an invalid character set rounds to 3
        if(gameLength > 5) gameLength = 5; // Set rounds to 5 is rounds is > 5
        else if(gameLength < 1) gameLength = 1; // Set rounds to 1 is rounds is < 1
        numOppsLeft = gameLength; // Set num of opponents to number of rounds

        newChallenger();
    }  

    // Constructor Helper Function
    private void newChallenger() // Create a challenger from the villain class
    {
        clearScreen();
        challenger = new Player(2000*player.getLevel()/2,player.getAtk()*player.getLevel()/2,player.getDef()*2); // Create a support character
        if(numOppsLeft == 1) 
        {
            System.out.println("BOSS LEVEL\n");
            challenger.setBoss(2*player.getLevel()*(player.getAtk()+player.getDef()+player.getHp())); // Set boss parameters override current parameters
        }
        else if(numOppsLeft == 2) 
        {
            System.out.println("The BOSS is Approaching!\n");
        }
        System.out.println("A new challenger approaches you!");
        mDelay(3000); // Buffer
    }
    
    // Getters and Setters
    int getTurn() {return turn;}
    void setTurn(int turn) {this.turn=turn;}
    Player getVillain() {return challenger;}

    // Main Function
    void input() throws IOException // Get user input for type of Attack
    {
        printHP();
        System.out.println("\nPossible Moves:");
        printArray(moves); // List possible moves
        String input = br.readLine().toLowerCase(); // Get input and make into lowercase
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
            System.out.println("Are you sure you want to give up?(y/N)");
            String user = br.readLine(); // Confirmation
            if(user.charAt(0) == 'y')
            {
                printLoss();
                playAgain();
            }
        }
        else if(input.charAt(0) == 'i' || input.contains("info")) // Main info menu
        {
            mainInfo();
        }
        System.out.println("Please enter a valid input");
        input(); // If input is invalid rerun method
    }

    private void mainInfo() throws IOException // Print information about Main menu
    {
        while(true)
        {
            clearScreen();
            System.out.println("Information Menu: ");
            printArray(moves);
            String user = br.readLine().toLowerCase();
            if(user.length() <= 0) System.out.println("\nNo input was detected");
            else if(user.charAt(0) == 'a' || user.contains("attack"))
            {
                System.out.println("\nGo to an attack menu where you can choose moves that will deal damage to your opponent");
                buffer();
                input();
                return;
            }
            else if(user.charAt(0) == 'b' || user.contains("block"))
            {
                System.out.println("\nBlock your opponent's basic attack\nBlock from 25% to 80% of the opponent's attack\n\nOPPONENT MUST PLAY A BASIC ATTACK");
                buffer();
                input();
                return;
            }
            else if(user.charAt(0) == 'p' || user.contains("potion"))
            {
                System.out.println("\nGo to a potion menu where you can choose potions that increase your stats");
                buffer();
                input();
                return;
            }
            else if(user.charAt(0) == 'e' || user.contains("exit"))
            {
                input();
                return;
            }
            buffer();
        }
    }

    private void potionMenu() throws IOException // Print information about potion menu
    {
        clearScreen();
        System.out.println("You have a very limited supply of potions at your disposal\nYou get 8 potions to start with and you gain 3 for every opponent you beat\nUse them Wisely");
        buffer();
        while(true)
        {
            clearScreen();
            System.out.println("Information Menu: ");
            printArray(potionMenu);
            String user = br.readLine().toLowerCase();
            if(user.length() <= 0) System.out.println("\nNo input was detected");
            else if(user.charAt(0) == 'a' || user.contains("attack"))
            {
                System.out.println("\nUse an attack potion that increases your attack damage by anywhere from 5% to 30% of your current attack damage");
                buffer();
                input();
                return;
            }
            else if(user.charAt(0) == 'd' || user.contains("def"))
            {
                System.out.println("\nUse a defense potion that increases your defense amount by anywhere from 10% to 30% of your current defense");
                buffer();
                input();
                return;
            }
            else if(user.charAt(0) == 'h' || user.contains("health"))
            {
                System.out.println("\nUse a healing potion that increases your health by anywhere from 15% to 50% of your current health");
                buffer();
                input();
                return;
            }
            else if(user.charAt(0) == 'e' || user.contains("exit"))
            {
                input();
                return;
            }
            buffer();
        }
    }

    private void attackMenu() throws IOException // Print information about attack menu
    {
        clearScreen();
        System.out.println("Welcome to the Attack Menu!");
        while(true)
        {
            clearScreen();
            System.out.println("Information Menu: ");
            printArray(attackMenu);
            String user = br.readLine().toLowerCase();
            if(user.length() <= 0) System.out.println("\nNo input was detected");
            else if(user.charAt(0) == 's' || user.contains("slap"))
            {
                System.out.println("\nUse a slap attack that deals emotional damage to your opponent\nThey will lose some health, defense, and atk value\nTHIS IS THE ONLY ATTACK THAT LOWERS ATK OR DEFENSE OF YOUR OPPONENT");
                buffer();
                input();
                return;
            }
            else if(user.charAt(0) == 'b' || user.contains("basic"))
            {
                System.out.println("\nAll basic attacks have a 1% chance of missing\nBasic attacks deal the most damage but can be blocked or countered by your opponent");
                buffer();
                input();
                return;
            }
            else if(user.charAt(0) == 'c' || user.contains("counter"))
            {
                System.out.println("\nIf your opponent uses a basic attack after you use a counter attack you have a 50% chance of using a counter attack\nA counter attack will deal damage to your opponent and you will not take any damage");
                buffer();
                input();
                return;
            }
            else if(user.charAt(0) == 'e' || user.contains("exit"))
            {
                input();
                return;
            }
            buffer();
        }
    }

    // Attack Functions
    void chooseAttack() throws IOException // User chooses attack and appropriate function is called
    {
        printHP();
        System.out.println("\nTypes of Attacks:");
        printArray(attackMenu); // List Attacks
        String choice = br.readLine().toLowerCase(); // Get User input
        if(choice.length() <= 0) chooseAttack(); // Check that input has length greater than 0
        else if(choice.contains("basic") || choice.charAt(0) == 'b') // Basic Attack
        {
            basic(); // Go to basic attack method
            return;
        }
        else if(choice.contains("counter")) // Counter Attack
        {
            player.setLastMove("counter"); // Set last Move to counter attack
            System.out.println("You used counter!");
            turn++; // Increment turn
            return;
        }
        else if(choice.contains("slap") || choice.charAt(0) == 's') // Slap Attack
        {
            slap(); // Go to slap function
            return;
        }
        else if(choice.contains("info") || choice.charAt(0) == 'i') // Attack information menu
        {
            attackMenu();
        }
        else if(choice.contains("exit") || choice.charAt(0) == 'e') // Exit Menu
        {
            input(); // Return to input()
            return;
        }
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
                    System.out.println("You did " + dmg + " damage!");
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
                    System.out.println("\n" + challenger.getName() + " used a basic attack a did " + dmg + " damage!");
                    blink(cOriginal,pOriginal,"\n" + challenger.getName() + " used a basic attack and did " + dmg + " damage!");
                    buffer();
                }
                challenger.setLastMove("basic");
            }
        }
        turn++;
    }

    void slap() // Slap Attack Logic
    {
        int cOriginal = challenger.getHp(),pOriginal=player.getHp(); // Store old Health
        if(turn % 2 == 0) // Player turn
        {
            System.out.println("\nYou used slap! It caused emotional damage to " + challenger.getName() +"!");
            player.setLastMove("slap");; // Set last move
            int hpAmount = challenger.getHp()/15-challenger.getDef(); // Helper variable
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
            System.out.println("\nYou got slapped by " + challenger.getName() + "!");
            int hpAmount = player.getHp()/15-player.getDef();
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

    // Attack Function Modifiers
    boolean miss() // Modifies basic() // Create chance of missing attack
    {
        int missChance = srand.nextInt(100); // Determine Random Miss Chance Number
        if(missChance == 1) // 1% chance of missing attack
        {
            clearScreen();
            printHP();
            switch(turn%2) // Determine player turn
            {
                case 0:
                    System.out.println("\nYou Missed");
                    buffer();
                    break;
                case 1: // Challenger Turn
                    System.out.println("\n" + challenger.getName() + " Missed");
                    buffer();
                    break;
            }
            return true;
        }
        return false;
    }

    private int checkSpecial(int turn, int dmg) // Modifies basic()
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
                System.out.println("\n" + challenger.getName() + " blocked " + (100-blockAmount) + "% of your attack");
                buffer();
            }
            else if(challenger.getLastMove().equals("counter") && counterChance == 1) // If opponent's last move was counter attack
            {
                double counterAmount = challenger.getAtk()*3/2;
                player.setHp((int)(player.getHp()-counterAmount));
                System.out.println("\n" + challenger.getName() + " used counter attack! You took " + (pOriginal-player.getHp()) + " damage!");
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
                System.out.println("\nYou blocked " + (100 - blockAmount) + "% of " + challenger.getName() + "'s attack");
                buffer();
            }
            else if(player.getLastMove().equals("counter") && counterChance == 1) // If your last move was counter attack
            {
                double counterAmount = player.getAtk()*3/2;
                challenger.setHp((int)(challenger.getHp()-counterAmount));
                System.out.println("\nYou used counter attack!" + challenger.getName() + " took " + (cOriginal-challenger.getHp()) + " damage!");
                buffer();
            }
        }
        return dmg;
    }

    // Block
    private int block() // Return a random block percentage up to 80% with min of 25%
    {
        return 100 - (srand.nextInt(55) + 25);
    }

    // Potion Functions
    void choosePotion() throws IOException // User chooses potion and appropriate function is called
    {
        printHP(challenger.getHp(),player.getHp());
        System.out.println("\nNumber of Remaining Potions: " + player.getPotions());
        System.out.println("\nTypes of Potions: ");
        printArray(potionMenu); // List potions
        String choice = br.readLine().toLowerCase(); // Get user Input
        if(player.getLastMove().equals("potion")) // Check that potion was not already played this turn
        {
            System.out.println("You already used a potion this turn!");
            buffer();
            input(); // Return to user input function
            return;
        }
        else if(player.getPotions() <= 0) // Check that there are enough potions left
        {
            System.out.println("You have no more potions left");
            buffer();
            input(); // Return to user input function
            return;
        }
        else if(choice.contains("exit") || choice.charAt(0) == 'e') // Exit
        {
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
        else if(choice.contains("info") || choice.charAt(0) == 'i')
        {
            potionMenu();
        }
        System.out.println("Please enter a valid input");
        choosePotion(); // If input does not match criteria rerun function
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
            System.out.println("\nYou used a defense potion. Your defense is now " + player.getDef());
        }
        else // Challenger turn
        {
            challenger.setPotions(challenger.getPotions()-1); // Decrease computer potions left
            challenger.setDef((int)(challenger.getDef()*nextRand)); // Set Computer Defense
            printHP();
            System.out.println("\n" + challenger.getName() + " used a defense potion. Their defense is now " + challenger.getDef());
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
            System.out.println("\nYou used an Attack Potion and your attack increased by " + (player.getAtk()-originalAtk) + " damage");
        }
        else
        {
            challenger.setPotions(challenger.getPotions()-1); // Decrease computer potions
            challenger.setAtk((int)(challenger.getAtk()*nextRand)); // Set computer attack
            printHP();
            System.out.println("\n" + challenger.getName() + " used an Attack Potion. Their Attack increased by " + (challenger.getAtk()-originalAtk) + " damage");
        }
        buffer();
    }

    void hpPotion() // Health Potion
    {
        double increase = srand.nextInt(35)/100+1.15; // Random percentage of health to gain back starting at 15% up to 50%
        int cOriginal=challenger.getHp(),pOriginal=player.getHp();
        if(turn%2==0)
        {
            player.setPotions(player.getPotions()-1); // Decrement potions
            player.setHp((int)(player.getHp()*increase)); // Apply percentage to hp
            printHP();
            potionAnimation(cOriginal,pOriginal,"HP"); // Animate
            System.out.println("\nYou used a healing potion and gained " + (player.getHp()-pOriginal) + " health");
        }
        else
        {
            challenger.setPotions(challenger.getPotions()-1); // Decrease Computer Potions
            challenger.setHp((int)(challenger.getHp()*increase)); // Set computer hp
            printHP();
            potionAnimation(cOriginal,pOriginal,"HP"); // Animate Health bar
            System.out.println("\n" + challenger.getName() + " used a healing potion and gained " + (challenger.getHp()-cOriginal) + " health");
        }
        buffer();
    }

    void potionAnimation(int cOriginal,int pOriginal,String type) // Animates Potion Effects
    {
        String body = turn % 2 == 0 ? "!T!  <-- YOU" : "!T!  <-- " + challenger.getName();
        printHP();
        System.out.println("\n\n\n\n O ");
        System.out.println(body);
        System.out.println(" |");
        System.out.println("/ \\ ");
        mDelay(400);
        printHP();
        System.out.println("\n\n\n" + type + "++");
        System.out.println(" O ");
        System.out.println(body);
        System.out.println(" | ");
        System.out.println("/ \\ ");
        mDelay(400);
        printHP();
        System.out.println("\n\n" + type + "++");
        System.out.println("\n O ");
        System.out.println(body);
        System.out.println(" | ");
        System.out.println("/ \\ ");
        mDelay(400);
        printHP();
        System.out.println("\n"+type + "++");
        System.out.println("\n\n O ");
        System.out.println(body);
        System.out.println(" | ");
        System.out.println("/ \\ ");
        mDelay(400);
        blink(cOriginal,pOriginal,"");
    }

    // Helper Functions
    static void buffer() // Add a user input buffer
    {
        System.out.println("\n\nPress Enter to continue");
        Main.scan.nextLine();
    }

    static void clearScreen() // Clear Screen
    {
        System.out.print("\033[H\033[2J");  // Clear Screen
        System.out.flush();                 // Flush Screen / Memory
        System.out.print("\u001b[H");       // Set cursor to top
    }

    private void mDelay(int milliseconds) // Create a delay in MS
    {
        try{TimeUnit.MILLISECONDS.sleep(milliseconds);}
        catch(Exception InterruptedException){System.out.println("Cancelled");}
    }
    
    private void blink(int cOriginal, int pOriginal, String message) // Creates a blink effect on health bars
    {
        for(int i = 0; i < 3; i++) // Loop x amount of times
        {
            printHP(cOriginal,pOriginal);
            System.out.println(message);
            mDelay(200);
            printHP();
            System.out.println(message);
            mDelay(200);
        }
    }

    private void printChallengerHP(int health) // Print Challengers Hp using "|"
    {
        clearScreen();
        System.out.println(challenger.getName() + "'s HP:");
        for(int i = 0; i < health/40; i++) System.out.print("|");
        System.out.println("\n");
    }

    private void printPlayerHP(int health) // Print Player's Hp using "|"
    {
        System.out.println(player.getName() + "'s HP:");
        for(int i = 0; i < health/40; i++) System.out.print("|");
        System.out.println("");
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

    private void printArray(String[] arr) {System.out.println(Arrays.toString(arr));} // Print given Array to String

    // End Game Functions
    void printWin() // Print Player WIN
    {
        clearScreen();
        System.out.println("*       *   *****       *       *      *             *   ***********   *        *");
        System.out.println(" *     *  *       *     *       *                             *        * *      *");
        System.out.println("  *   *  *         *    *       *       *           *         *        *  *     *");
        System.out.println("   * *   *         *    *       *                             *        *   *    *");
        System.out.println("    *    *         *    *       *        *    *    *          *        *    *   *");
        System.out.println("    *    *         *    *       *            * *              *        *     *  *");
        System.out.println("    *     *       *      *     *          * *   * *           *        *      * *");
        System.out.println("    *       *****         *****            *     *       ***********   *        *");
        buffer();
    }

    void printLoss() // Print Player Loss
    {
        clearScreen();
        System.out.println("*       *   *****       *       *      *             *****      *******   ******");
        System.out.println(" *     *  *       *     *       *      *           *      *   *           *     ");
        System.out.println("  *   *  *         *    *       *      *          *        *  *           *     ");
        System.out.println("   * *   *         *    *       *      *          *        *     *        ******");
        System.out.println("    *    *         *    *       *      *          *        *        *     *     ");
        System.out.println("    *    *         *    *       *      *          *        *           *  *     ");
        System.out.println("    *     *       *      *     *       *           *      *            *  *     ");
        System.out.println("    *       *****         *****        *********     *****     *******    ******");
        buffer();
    }
    
    void playAgain() throws IOException // Play Again Function
    {
        clearScreen();
        System.out.println("Do you want to play again?(Y/n)");
        String user = br.readLine().toLowerCase(); // Get user input
        if(user.length() > 0) // Check that input is longer than 0 characters
        {
            if(user.charAt(0) == 'y') Main.play(); // If user wants to play again run main game function
            else if(user.charAt(0) == 'n') { // Otherwise close scanner and exit system
                br.close();
                System.exit(0);
            }
        }
        System.out.println("Please enter a valid input"); // If input is invalid tell user to enter a valid input and rerun function
        playAgain();
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
}
