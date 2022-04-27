import java.util.Map;
import java.util.HashMap;

/*
Class with helper functions to store values
Used like an ArrayList but has extra functions that help the game run smoothly
Stores the Player and Challenger's Health, Attack, Defense, Potions, Name, Last Move and Level
*/
class Player 
{
    private String name,lastMove; // Name and last move
    int hp; // Removed hp from map to increase hp blink speed (was lagging)
    private Map<String, Integer> values; // Hashmap for all Player related Values excluding Health

    // Constructors
    Player(int hp,int atk,int def) // Constructor sets value for hp, atk dmg, name, and def for challengers
    {
        Names randName = new Names(); // New name instance
        name = randName.getName(); // Set name
        values = new HashMap<String,Integer>(); // Initialize HashMap
        setPotions(8); // Set number of potions
        setLastMove(""); // Initialize LastMove in Hash
        this.hp = hp; // Set hp
        setMap(atk, def); // Set atk and def
    }

    Player(int tokens) // Constructor for Player
    {
        values = new HashMap<String,Integer>();
        setName(); // Choose a player name
        chooseValues(tokens); // Choose tokens
        setLastMove(""); // Initialize last move
        setLevel(1); // Set player level
        setPotions(5); // Set number of player potions
    }

    // Getters and Setters
    int getPotions() {return values.get("potions");}
    void setPotions(int numPotions) {values.put("potions",numPotions);}

    int getAtk() {return values.get("atk");}
    void setAtk(int atk) {values.put("atk",atk);}

    int getHp() {return hp;}
    void setHp(int hp) {this.hp=hp;}

    int getDef() {return values.get("def");}
    void setDef(int def) {values.put("def",def);}

    String getLastMove() {return lastMove;}
    void setLastMove(String move) {lastMove = move;}

    int getLevel() {return values.get("level");}
    void setLevel(int level) {values.put("level",level);}

    String getName() {return name;}
    private void setName() // Get user's name
    {
        Game.clearScreen();
        System.out.println("Welcome to your DOOM DUNGEON!!");
        System.out.println("\nEnter a name to begin: ");
        String choice = Main.scan.nextLine(); // Get name
        if(choice.length() <= 0) setName(); // If length of name is less than 1 call function again
        else
        {
            String letter = (String.valueOf(choice.charAt(0))).toUpperCase(); // Get first letter of name and make it uppercase
            this.name = letter + choice.substring(1); // Set name to uppercase letter and the rest of the name
        }
    }

    void turn() // Challenger Take Turn Method
    {
        Game.clearScreen();
        Main.game.printHP();
        int type = getPotions() > 0 ? Game.srand.nextInt(10) : Game.srand.nextInt(7)+3; // Get a random number
        if(type > 4 || lastMove.equals("potion")) // Attack if number is higher than 4
        {
            int typeAtk = Game.srand.nextInt(4); // Get another number
            if(typeAtk<=1) // Basic Attack
            {
                lastMove = "basic"; // Set Last Move
                Main.game.basic(); // Basic attack function
            }
            else if(typeAtk == 2) // Counter Attack
            {
                lastMove = "counter"; // Set last move
                System.out.println("\n" + name + " played a hidden move");
                Game.buffer();
                Main.game.setTurn(Main.game.getTurn()+1); // Set turn
            }
            else // Slap Attack
            {
                lastMove = "slap"; // Set last Move
                Main.game.slap(); // Game.slap() function
            }
        }
        else if(type < 3) // potion
        {
            int potionType = Game.srand.nextInt(3); // Random number
            lastMove = "potion"; // Set move to potion
            if(potionType == 0) Main.game.atkPotion(); // ATK potion
            else if(potionType == 1) Main.game.dPotion(); // Defense Potion
            else Main.game.hpPotion(); // Health Potion
        }
        else // Block
        {
            Main.game.setTurn(Main.game.getTurn()+1); // Set Turn
            lastMove = "block"; // Set move to block
            System.out.println("\n" + name + " played a hidden move");
            Game.buffer();
        }
    }

    // Helper Functions
    private void chooseValues(int tokens) // Choose player values function
    {
        values.put("hpTok",2); // Put token values into hashmap
        values.put("atkTok",2);
        values.put("defTok",2);
        values.put("earnedTok",tokens);
        String selection = ""; // User input
        Game.clearScreen();
        System.out.println("TOKEN CHOOSING!!\n\nYou have " + (values.get("hpTok")+values.get("atkTok")+values.get("defTok")+values.get("earnedTok")) + " tokens in total. You have one category in each category by default\nEach token represents an increase by 500 Health, 50 Attack, or 30 Defense\nThese will be your starting amounts so choose wisely!\n\nEnter (+/-)+(Integer) to change the number of tokens in the category\nPress 'E' to exit");
        Game.buffer();

        while(true)  // Loop and a half statement
        {
            Game.clearScreen();
            System.out.println("Health Tokens: " + values.get("hpTok")); // Print token amounts
            System.out.println("Attack Tokens: " + values.get("atkTok"));
            System.out.println("Defense Tokens: " + values.get("defTok") + "\n");
            System.out.println("Choose a category: [Health, Attack, Defense, Exit]");
            selection = Main.scan.nextLine().toLowerCase();
            if(selection.length()<=0) ; // Ensure that user inputted something
            else if(selection.charAt(0) == 'e') // If user types e as the beginning character exit
            {
                setMap((values.get("atkTok")+1)*50,(values.get("defTok")+1)*30); // Set permanent player values
                hp = (values.get("hpTok")+1)*500; 
                values.remove("hpTok"); // Remove keys from HashMap
                values.remove("atkTok");
                values.remove("defTok");
                return; // Exit Function
            }
            else if(selection.charAt(0) == 'h' || selection.charAt(0) == 'a' || selection.charAt(0) == 'd') menu(selection.charAt(0)); // Go to selection menu
            else // If input was invalid
            {
                System.out.println("Please enter a valid input");
                Game.buffer();
            }
        }
    }

    private void menu(char selection) // Token Selection menu
    {
        while(true) // Loop and a half statement
        {
            boolean error = false; // Boolean to check if an error was already thrown
            Game.clearScreen();
            int numTokensLeft = (values.get("hpTok")+values.get("atkTok")+values.get("defTok")+values.get("earnedTok"))-values.get("hpTok")-values.get("atkTok")-values.get("defTok"); // Tokens remaining
            System.out.println("You have " + numTokensLeft + " tokens remaining"); // Print amount of token left
            System.out.println("Health Tokens: " + values.get("hpTok")); // Print current token values
            System.out.println("Attack Tokens: " + values.get("atkTok"));
            System.out.println("Defense Tokens: " + values.get("defTok") + "\n");

            switch(selection) // Print menu based on selection
            {
                case 'h':
                    System.out.println("Health tokens:");
                    break;
                case 'a':
                    System.out.println("Attack tokens:");
                    break;
                case 'd':
                    System.out.println("Defense tokens:");
                    break;
            }
            String token = Main.scan.nextLine().toLowerCase(); // Get user input
            
            for(int i = 1; i < token.length(); i++) 
            {
                if(!Character.isDigit(token.charAt(i))&&token.charAt(0)!='a'&&token.charAt(0)!='d'&&token.charAt(0)!='h'&&token.charAt(0)!='e') // Check that the input is a number after the first characters
                {
                    System.out.println("Please enter a number");
                    Game.buffer();
                    error = true; // Error occurred
                    i = token.length();
                }
            }
            
            if(token.length() <= 0) ; // First case if no input to ensure no exceptions occur
            else if(token.charAt(0) == '+' || (Character.isDigit(token.charAt(0)) && !error)) // If character at first position was positive or is a number
            {
                int userTok = Character.isDigit(token.charAt(0)) ? Integer.valueOf(token):Integer.valueOf(token.substring(1)); // Determine the number of token selected
                if(userTok > numTokensLeft) // If more tokens selected than tokens remaining
                {
                    System.out.println("\nNumber of Tokens selected exceeds currently available tokens");
                    Game.buffer();
                }
                else
                {
                    switch(selection) // Switch case depending on selection's character
                    {
                        case 'h':
                            values.put("hpTok",values.get("hpTok")+userTok);
                            break;
                        case 'a':
                            values.put("atkTok",values.get("atkTok")+userTok);
                            break;
                        case 'd':
                            values.put("defTok",values.get("defTok")+userTok);
                            break;
                    }
                    return;
                }
            }
            else if(token.charAt(0) == '-' && token.length() > 1) // If character at first position was negative
            {
                int userTok = Integer.valueOf(token.substring(1)); // Determine number of tokens
                switch(selection) // Switch case depending on current menu
                {
                    case 'h':
                        if(userTok > values.get("hpTok")) exceeds(selection); // If more tokens selected than hp tokens remaining
                        else values.put("hpTok",values.get("hpTok")-userTok);
                        break;
                    case 'a':
                        if(userTok > values.get("atkTok")) exceeds(selection); // If more tokens selected than atk tokens remaining
                        else values.put("atkTok",values.get("atkTok")-userTok);
                        break;
                    case 'd':
                        if(userTok > values.get("defTok")) exceeds(selection); // If more tokens selected than def tokens remaining
                        else values.put("defTok",values.get("defTok")-userTok);
                        break;
                }
                return;
            }
            else if(!error) // If error was not yet thrown
            {
                switch(token.charAt(0)) // Switch case depending on token's first character // Switches selection value
                {
                    case 'a':
                        selection = 'a';
                        break;
                    case 'd':
                        selection = 'd';
                        break;
                    case 'h':
                        selection = 'h';
                        break;
                    case 'e':
                        return;
                    default: // If not valid return invalid
                        System.out.println("Invalid beginning character");
                        Game.buffer();
                }
            }
        }
    }

    private void exceeds(char selection)
    {
        String print = "";
        switch(selection)
        {
            case 'h':
                print = "Health";
            case 'd':
                print = "Defense";
            case 'a':
                print = "Attack";
        }
        System.out.println("\nNumber of Tokens Selected Exceeds Number of Available " + print + " Tokens");
        Game.buffer();
    }

    void setBoss(int max) // Set boss parameters // max is all the player stats added up and multiplied by level
    {
        hp = Game.srand.nextInt(max/2) + 100; // Get random hp up to 100 + half of maximum
        values.put("atk",Game.srand.nextInt(max/3) + 50); // Get random Atk DMG up to 50 + one third of max
        values.put("def",Game.srand.nextInt(max/3) + 20); // Min of 20 def and up to 20 + one third of max
    }

    private void setMap(int atk, int def) // Set map Values
    {
        values.put("atk",atk);
        values.put("def",def);
    }
}