import static java.lang.System.*;
import static java.util.concurrent.TimeUnit.*;
import java.util.*;

import java.security.*;

/*
Password for Dungeon is "Secret Word"
Ideas
Player hp bars
animation
*/

public class Game 
{
    private static Map<String, Integer> player;
    private int numOppsLeft = 5, turn = 0,playerLevel = 1, numPotions = 5; // Important Variables for Player
    final public static String[] moves =  {"Attack","Block","Potions","Give Up"}, attackMenu = {"Basic","Counter Attack","Slap","Exit"}, potionMenu = {"ATK DMG","Defense","Heal","Exit"}; // Types of moves
    private static Scanner scan; // Scanner
    public static SecureRandom srand; // Secure RNG
    private Villain challenger; // Current Challenger
    private String lastMove = "none",name; // Last move to check for blocks and counters

    public Game(String name) // Game Constructor to set scanner, RNG, and a new challenger
    {
        player = new HashMap<String,Integer>();
        scan = new Scanner(System.in);
        srand = new SecureRandom();
        player.put("dHp",500);
        player.put("dAtk",40);
        player.put("dDef",5);
        player.put("dCrit",player.get("dAtk")/3);
        setMap(500,40,5);
        this.name = name;
        out.print("\nChoose a length(1-5): ");
        char user = scan.nextLine().charAt(0);
        int length = 3;
        length = Character.isDigit(user) ? Integer.valueOf(user):3;
        if(length > 5) length = 5;
        else if(length < 1) length = 1;
        numOppsLeft = length;
        chooseValues();
        newChallenger();
    }  

    // Getters and Setters
    private void setMap(int hp, int atk, int def) {
        player.put("hp",500);
        player.put("atk",40);
        player.put("def",5);
        player.put("crit",player.get("atk")/3);
    }
    public String getMove(){return lastMove;}
    public int getTurn(){return turn;}
    public void setTurn(int turn){this.turn=turn;}
    public Villain getVillain() {return challenger;}
    public int getAtk() {return player.get("atk");}
    public void setAtk(int atk) {player.put("atk",atk);}
    public int getDef() {return player.get("def");}
    public void setDef(int def) {player.put("def",def);}

    private void chooseValues()
    {
        String user = ""; // User input
        int hpTok=3,atkTok=3,defTok=3, numTokensLeft = 12-hpTok-atkTok-defTok; // Tokens
        clearScreen();
        out.println("You have " + numTokensLeft + " tokens to spend. Each token represent 500 Health, 50 Attack, or 30 Defense. Enter (+/-)\"num\" to increase num tokens in the category\n\nChoose the tokens to change by typing the corresponding name \nWhen you are done press Enter");
        scan.nextLine();
        for(int i = 0; i < 3; i++)
        {
            numTokensLeft = 12-hpTok-atkTok-defTok;
            do
            {
                clearScreen();
                out.println("You have " + (numTokensLeft) + " tokens remaining.");
                out.println("Health Tokens: " + hpTok);
                out.println("Attack Tokens: " + atkTok);
                out.println("Defense Tokens: " + defTok + "\n");
                if(i == 0)
                {
                    out.println("Health tokens:");
                    user = scan.nextLine().toLowerCase();
                }
                else if(i == 1)
                {
                    out.println("Attack tokens:");
                    user = scan.nextLine().toLowerCase();
                }
                else
                {
                    out.println("Defense tokens:");
                    user = scan.nextLine().toLowerCase();
                }
                if(user.length() <= 0) ; // First case if no input
                else if(user.contains("health") || user.charAt(0) == 'h')
                {
                    i = -1;
                    continue;
                }
                else if(user.contains("attack") || user.charAt(0) == 'a')
                {
                    i = 0;
                    continue;
                }
                else if(user.contains("defense") || user.charAt(0) == 'd')
                {
                    i = 1;
                    continue;
                }
                
                for(int j = 1; j < user.length(); j++) 
                {
                    if(!Character.isDigit(user.charAt(j)))
                    {
                        out.println("Enter a valid literal");
                        sDelay(2);
                        continue;
                    }
                }
                
                int userTok = Integer.valueOf(user.substring(1));
                if(user.length() <= 1) ; // First case if no input
                else if(user.charAt(0) == '+') 
                {
                    if(userTok > numTokensLeft)
                    {
                        out.println("\nInvalid Number of Tokens");
                        sDelay(2);
                    }
                    else
                    {
                        switch(i)
                        {
                            case 0:
                                hpTok += userTok;
                                break;
                            case 1:
                                atkTok += userTok;
                                break;
                            case 2:
                                defTok += userTok;
                                break;
                        }
                    }
                }
                else if(user.charAt(0) == '-') 
                {
                    switch(i)
                    {
                        case 0:
                            if(userTok > hpTok)
                            {
                                out.println("\nInvalid Number of Tokens");
                                user = "";
                                sDelay(2);
                            }
                            else hpTok -= userTok;
                            break;
                        case 1:
                            if(userTok > atkTok)
                            {
                                out.println("\nInvalid Number of Tokens");
                                user = "";
                                sDelay(2);
                            }
                            else atkTok -= userTok;
                            break;
                        case 2:
                            if(userTok > defTok)
                            {
                                out.println("\nInvalid Number of Tokens");
                                user = "";
                                sDelay(2);
                            }
                            else defTok -= userTok;
                            break;
                    }
                }
                else 
                {
                    out.println("Invalid beginning literal");
                    user = "";
                    sDelay(2);
                }
            }while(user == "");
        }
        setMap(hpTok*500,atkTok*50,defTok*30);
    }

    private void newChallenger() // Create a challenger from the villain class
    {
        clearScreen();
        numOppsLeft--; // Decrease Opponents Left
        if(numOppsLeft == 0) 
        {
            out.println("BOSS LEVEL");
            challenger = new Boss(); // Create Boss if Only One Challenger Remains
            challenger.setBoss(2*playerLevel*(player.get("atk")+player.get("def")+player.get("hp"))); // Set boss parameters
            sDelay(2);
        }
        else if(numOppsLeft == 1) 
        {
            out.println("The BOSS is Approaching!"); 
            challenger = new SupportCharacter(2000*playerLevel/2,player.get("atk")*playerLevel/2,player.get("def")*2); // Create a support character
            sDelay(2);
        }
        else if(numOppsLeft > 1) challenger = new SupportCharacter(2000*playerLevel/2,player.get("atk")*playerLevel/2,player.get("def")*2); // Create a regular Character
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
        printPlayerHP(player.get("hp"));
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
        printHP(challenger.getHp(),player.get("atk"));
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
        choosePotion(); // If input does not match criteria rerun function
    }

    public void basic() // Basic Attack need to add counter attack
    {
        miss(); // Check if attack missed
        int critChance = srand.nextInt(10); // Determine Random Crit Chance Number
        int cOriginal = challenger.getHp(); // Store computer hp before damage
        int pOriginal = player.get("hp"); // Store player hp before damage
        if(turn % 2 == 0) // Player Turn
        {
            int dmg = critChance == 1 ? player.get("atk")+player.get("crit")-challenger.getDef():player.get("atk")-challenger.getDef(); // Determine amount of Damage
            dmg = dmg < 0 ? 0:dmg; // Check if damage is negative and set dmg to 0 if it is
            dmg = checkSpecial(turn,dmg);
            challenger.setHp(challenger.getHp()-dmg); // Set hp
            out.println("You did " + dmg + " damage!");
            lastMove = "basic"; // Set Last move
            turn++; // Increment turn
            blink(cOriginal,pOriginal,"\nYou did " + dmg + " damage!"); // Blink Health Bar Animation
        }
        else // Challenger Turn
        {
            int dmg = critChance == 1 ? challenger.getAtk()+challenger.getAtk()/3:challenger.getAtk(); // Determine amount of Damage
            dmg -= player.get("def");
            dmg = dmg < 0 ? 0:dmg;
            dmg = checkSpecial(turn,dmg);
            player.put("hp",player.get("hp")-dmg);
            challenger.setLastMove("basic");
            out.println("\n" + challenger.getName() + " used a basic attack a did " + dmg + " damage!");
            turn++;
            blink(cOriginal,pOriginal,"\n" + challenger.getName() + " used a basic attack and did " + dmg + " damage!");
        }
        sDelay(1);
    }

    private int checkSpecial(int turn, int dmg)
    {
        int cOriginal = challenger.getHp(); // Store computer hp before damage
        int pOriginal = player.get("hp"); // Store player hp before damage
        int counterChance = srand.nextInt(2);

        if(turn % 2 == 0)
        {
            if(challenger.getLastMove().equals("block")) // Check if challenger's move was block
            {
                int blockAmount = block(); // Determine block percentage
                dmg *= blockAmount; // Multiply attack dmg by block percentage and divide by 100
                dmg/=100;
                out.println("\n" + challenger.getName() + " blocked " + (100-blockAmount) + "% of your attack");
            }
            else if(challenger.getLastMove().equals("counter") && counterChance == 1) // If opponent's last move was counter attack
            {
                double counterAmount = challenger.getAtk()*3/2;
                player.put("hp",(int)(player.get("hp")-counterAmount));
                out.println("\n" + challenger.getName() + " used counter attack! You took " + (pOriginal-player.get("hp")) + " damage!");
            }
        }
        else
        {
            if(lastMove.equals("block"))
            {
                int blockAmount = block();
                dmg *= blockAmount;
                dmg /= 100;
                out.println("\nYou blocked " + (100 - blockAmount) + "% of " + challenger.getName() + "'s attack");
            }
            else if(lastMove.equals("counter") && counterChance == 1) // If your last move was counter attack
            {
                double counterAmount = player.get("atk")*3/2;
                challenger.setHp((int)(challenger.getHp()-counterAmount));
                out.println("\nYou used counter attack!" + challenger.getName() + " took " + (cOriginal-challenger.getHp()) + " damage!");
            }
        }
        sDelay(2);
        return dmg;
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
        int cOriginal = challenger.getHp(),pOriginal=player.get("hp"); // Store old Health
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
            int hpAmount = player.get("hp")/10-player.get("def");
            hpAmount = hpAmount < 0 ? 0 : hpAmount;
            int atkAmount = player.get("atk")/20-player.get("def")/20;
            atkAmount = atkAmount < 0 ? 0 : atkAmount;
            player.put("hp",player.get("hp")-hpAmount);
            player.put("atk",player.get("atk")-atkAmount);
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
        double nextRand = 1.1+srand.nextInt(20)/100; // Get random defense percentage starting at 10% to a max of 30%
        potionAnimation(challenger.getHp(),player.get("hp"),"DEF"); // Animate def increase
        if(turn%2==0) // User turn
        {
            numPotions--; // Decrease amount of potions
            player.put("def",(int)(player.get("def")*nextRand)); // Apply Defense increase
            printHP();
            out.println("\nYou used a defense potion. Your defense is now " + player.get("def"));
        }
        else // Challenger turn
        {
            challenger.setPotions(challenger.getPotions()-1); // Decrease computer potions left
            challenger.setDef((int)(challenger.getDef()*nextRand)); // Set Computer Defense
            printHP();
            out.println("\n" + challenger.getName() + " used a defense potion. Their defense is now " + challenger.getDef());
        }
        sDelay(2);
    }

    public void atkPotion() // Attack Potion Logic
    {
        double nextRand = srand.nextInt(25)/100+1.05; // Get random percentage starting at 5% up to 30%
        int originalAtk = turn%2 == 0 ? player.get("atk"):challenger.getAtk(); //Determine original Attack Amount
        potionAnimation(challenger.getHp(),player.get("hp"),"ATK");
        if(turn % 2 == 0)
        {
            numPotions--; // Decrement Num of Potions
            player.put("atk",(int)(player.get("atk")*nextRand)); // Apply Attack
            printHP();
            out.println("\nYou used an Attack Potion and your attack increased by " + (player.get("atk")-originalAtk) + " damage");
        }
        else
        {
            challenger.setPotions(challenger.getPotions()-1); // Decrease computer potions
            challenger.setAtk((int)(challenger.getAtk()*nextRand)); // Set computer attack
            printHP();
            out.println("\n" + challenger.getName() + " used an Attack Potion. Their Attack increased by " + (challenger.getAtk()-originalAtk) + " damage");
        }
        sDelay(2);
    }

    public void hpPotion() // Health Potion
    {
        double increase = srand.nextInt(60)/100+1.15; // Random percentage of health to gain back starting at 15% up to 75%
        int cOriginal=challenger.getHp(),pOriginal=player.get("hp");
        if(turn%2==0)
        {
            numPotions--; // Decrement potions
            player.put("hp",(int)(player.get("hp")*increase)); // Apply percentage to hp
            printHP();
            potionAnimation(cOriginal,pOriginal,"HP"); // Animate
            out.println("\nYou used a healing potion and gained " + (player.get("hp")-pOriginal) + " health");
        }
        else
        {
            challenger.setPotions(challenger.getPotions()-1); // Decrease Computer Potions
            challenger.setHp((int)(challenger.getHp()*increase)); // Set computer hp
            printHP();
            potionAnimation(cOriginal,pOriginal,"HP"); // Animate Health bar
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
                player.put("hp",(int)(player.get("hp")*1.5)); // Give player 150% of their current HP
                playerLevel++; // Increase Player Level
                turn = 0; // Set turn to 0
                numPotions+=3; // Increase Number of Potions
                newChallenger(); // Create another Challenger
                return "n"; // Return No winner
            }
        }
        else if(player.get("hp") <= 0) return "boss"; // Return Boss Wins
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
