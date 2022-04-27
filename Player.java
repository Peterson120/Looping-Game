import java.util.*;
import static java.lang.System.*;
class Player 
{
    private String name,lastMove;
    int hp; // Removed hp from map to increase blink speed
    private Map<String, Integer> values; // Hashmap for all Player related Values excluding Health

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

    Player() // Constructor for Player
    {
        values = new HashMap<String,Integer>();
        chooseName(); // Choose a player name
        chooseValues(); // Choose tokens
        setLastMove(""); // Initialize last move
        setLevel(1); // Set player level
        setPotions(5); // Set number of player potions
    }

    private void setMap(int atk, int def) // Set map Values
    {
        values.put("atk",atk);
        values.put("def",def);
    }
    
    // Getters and Setters
    String getName() {return name;}

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

    void setBoss(int max) // Set boss parameters // max is all the player stats added up and multiplied by level
    {
        hp = Game.srand.nextInt(max/2) + 100; // Get random hp up to 100 + half of maximum
        values.put("atk",Game.srand.nextInt(max/3) + 50); // Get random Atk DMG up to 50 + one third of max
        values.put("def",Game.srand.nextInt(max/3) + 20); // Min of 20 def and up to 20 + one third of max
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
        }
    }

    private void chooseName() // Get user's name
    {
        Game.clearScreen();
        out.println("Welcome to your DOOM DUNGEON!!");
        out.println("\nEnter a name to begin: ");
        String name = Main.scan.nextLine(); // Get name
        if(name.length() <= 0 || name.length() > 10) chooseName();
        String letter = (String.valueOf(name.charAt(0))).toUpperCase(); // Get first letter of name and make it uppercase
        this.name = letter + name.substring(1); // Set name to upppercase letter and the rest of the name
    }
    
    private void chooseValues() // Choose player values function
    {
        values.put("hpTok",3); // Put token values into hashmap
        values.put("atkTok",3);
        values.put("defTok",3);
        String selection = ""; // User input
        Game.clearScreen();
        out.println("TOKEN CHOOSING!!\n\nYou have 12 tokens in total. Each token represents an increase by 500 Health, 50 Attack, or 30 Defense\nThese will be your starting amounts so choose wisely!\n\nEnter (+/-)+(Integer) to change the number of tokens in the category\nPress 'E' to exit\n\nPress Enter");
        Main.scan.nextLine(); // Acts as a delay
        while(true)  // Infinite loop with break statement
        {
            Game.clearScreen();
            out.println("Health Tokens: " + values.get("hpTok")); // Print token amounts
            out.println("Attack Tokens: " + values.get("atkTok"));
            out.println("Defense Tokens: " + values.get("defTok") + "\n");
            out.println("Choose a category: [Health, Attack, Defense, Exit]");
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
                out.println("Please enter a valid input\n\nPress Enter to continue");
                Main.scan.nextLine(); // Acts as timer
            }
        }
    }

    private void menu(char selection) // Token Selection menu
    {
        while(true) // Infinite Loop with break statement
        {
            boolean error = false; // Boolean to check if an error was already thrown
            Game.clearScreen();
            int numTokensLeft = 12-values.get("hpTok")-values.get("atkTok")-values.get("defTok"); // Tokens remaining
            out.println("You have " + numTokensLeft + " tokens remaining"); // Print amount of token left
            out.println("Health Tokens: " + values.get("hpTok")); // Print current token values
            out.println("Attack Tokens: " + values.get("atkTok"));
            out.println("Defense Tokens: " + values.get("defTok") + "\n");

            switch(selection)
            {
                case 'h':
                    out.println("Health tokens:");
                    break;
                case 'a':
                    out.println("Attack tokens:");
                    break;
                case 'd':
                    out.println("Defense tokens:");
                    break;
            }
            String token = Main.scan.nextLine().toLowerCase();
            
            for(int i = 1; i < token.length(); i++) 
            {
                if(!Character.isDigit(token.charAt(i)))
                {
                    out.println("Please enter a number\n\nPress Enter to Continue");
                    Main.scan.nextLine();
                    error = true;
                    break;
                }
            }
            
            if(token.length() <= 0) ; // First case if no input to ensure no exceptions occur
            else if(token.charAt(0) == '+' || (Character.isDigit(token.charAt(0)) && !error)) // If character at first position was positive or is a number
            {
                int userTok = Character.isDigit(token.charAt(0)) ? Integer.valueOf(token):Integer.valueOf(token.substring(1));
                if(userTok > numTokensLeft)
                {
                    out.println("\nNumber of Tokens selected exceeds currently available tokens\n\nPress Enter to Continue");
                    Main.scan.nextLine();
                }
                else
                {
                    switch(selection)
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
                }
            }
            else if(token.charAt(0) == '-' && token.length() > 1) // If character at first position was negative
            {
                int userTok = Integer.valueOf(token.substring(1));
                switch(selection)
                {
                    case 'h':
                        if(userTok > values.get("hpTok"))
                        {
                            out.println("\nNumber of Tokens Selected Exceeds Number of Available Health Tokens\n\nPress Enter to Continue");
                            Main.scan.nextLine();
                        }
                        else values.put("hpTok",values.get("hpTok")-userTok);
                        break;
                    case 'a':
                        if(userTok > values.get("atkTok"))
                        {
                            out.println("\nNumber of Tokens Selected Exceeds Number of Available Attack Tokens\n\nPress Enter to Continue");
                            Main.scan.nextLine();
                        }
                        else values.put("atkTok",values.get("atkTok")-userTok);
                        break;
                    case 'd':
                        if(userTok > values.get("defTok"))
                        {
                            out.println("\nNumber of Tokens Selected Exceeds Number of Available Defense Tokens\n\nPress Enter to Continue");
                            Main.scan.nextLine();
                        }
                        else values.put("defTok",values.get("defTok")-userTok);
                        break;
                }
            }
            else if(!error) // If error was not yet thrown
            {
                switch(token.charAt(0))
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
                    default:
                        out.println("Invalid beginning character\n\nPress Enter to Continue");
                        Main.scan.nextLine();
                }
            }
        }
    }
}
