import java.util.*;
import static java.lang.System.*;
public class Player 
{
    private String name,lastMove;
    int hp; // Removed hp from map to increase blink speed
    private Map<String, Integer> values;

    public Player(int hp,int atk,int def) // Constructor sets value for hp, atk dmg, name, and def if it is a villain
    {
        Names randName = new Names(); // New name instance
        name = randName.getName(); // Set name
        values = new HashMap<String,Integer>(); // Initialize HashMap
        setPotions(8); // Set number of potions
        setLastMove(""); // Initialize LastMove in Hash
        this.hp = hp;
        setMap(atk, def);
    }

    public Player() // Constructor for Player
    {
        values = new HashMap<String,Integer>();
        chooseName();
        chooseValues();
        setLastMove("");
        setLevel(1);
        setPotions(5);
    }

    //Set map Values
    private void setMap(int atk, int def) {
        values.put("atk",atk);
        values.put("def",def);
    }
    
    // Getters and Setters
    public String getName() {return name;}

    public int getPotions() {return values.get("potions");}
    public void setPotions(int numPotions) {values.put("potions",numPotions);}

    public int getAtk() {return values.get("atk");}
    public void setAtk(int atk) {values.put("atk",atk);}

    public int getHp() {return hp;}
    public void setHp(int hp) {this.hp=hp;}

    public int getDef() {return values.get("def");}
    public void setDef(int def) {values.put("def",def);}

    public String getLastMove() {return lastMove;}
    public void setLastMove(String move) {lastMove = move;}

    public int getLevel() {return values.get("level");}
    public void setLevel(int level) {values.put("level",level);}

    public void setBoss(int max) // Set boss parameters
    {
        hp = Game.srand.nextInt(max/2) + 100;
        values.put("atk",Game.srand.nextInt(max/3) + 50);
        values.put("def",max-hp-values.get("atk"));
    }

    public void turn() // Challenger Take Turn Method
    {
        Game.clearScreen();
        Main.game.printHP();
        int type = getPotions() > 0 ? Game.srand.nextInt(10) : Game.srand.nextInt(7)+3; // Get a random number
        if(type > 4 || lastMove.equals("potion")) // Attack if number is higher than 4
        {
            int typeAtk = Game.srand.nextInt(4); // Get another number
            if(typeAtk<=1) // Basic Attack
            {
                lastMove = "basic";
                Main.game.basic(); // Basic attack function
            }
            else if(typeAtk == 2) // Counter Attack
            {
                lastMove = "counter";
                Main.game.setTurn(Main.game.getTurn()+1); // Set turn
            }
            else // Slap Attack
            {
                lastMove = "slap";
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

    private void chooseName()
    {
        Game.clearScreen();
        out.println("Welcome to your DOOM DUNGEON!!");
        out.println("\nEnter a name to begin: ");
        String name = Game.scan.nextLine(); // Get name
        if(name.length() <= 0 || name.length() > 10) chooseName();
        String letter = (String.valueOf(name.charAt(0))).toUpperCase(); // Get first letter of name and make it uppercase
        this.name = letter + name.substring(1); // Set name to upppercase letter and the rest of the name
    }
    
    private void chooseValues()
    {
        values.put("hpTok",3);
        values.put("atkTok",3);
        values.put("defTok",3);
        String selection = ""; // User input
        Game.clearScreen();
        out.println("You have 12 tokens in total. Each token represents an increase by 500 Health, 50 Attack, or 30 Defense\n\nEnter (+/-)+(Integer) to change the number of tokens in the category\nPress 'E' to exit\n\nPress Enter");
        Game.scan.nextLine();
        do
        {
            Game.clearScreen();
            out.println("Health Tokens: " + values.get("hpTok"));
            out.println("Attack Tokens: " + values.get("atkTok"));
            out.println("Defense Tokens: " + values.get("defTok") + "\n");
            out.println("Choose a category: [Health, Attack, Defense, Exit]");
            selection = Game.scan.nextLine().toLowerCase();
            if(selection.length()<=0) 
            {
                out.println("Please enter an input");
            }
            else if(selection.charAt(0) == 'e')
            {
                setMap((values.get("atkTok")+1)*50,(values.get("defTok")+1)*30);
                hp = (values.get("hpTok")+1)*500;
                values.remove("hpTok");
                values.remove("atkTok");
                values.remove("defTok");
                return;
            }
            else if(selection.charAt(0) == 'h' || selection.charAt(0) == 'a' || selection.charAt(0) == 'd') menu(selection.charAt(0));
            selection = "";
        }while(selection.equals(""));
    }

    private void menu(char selection)
    {
        String token = "";
        int numTokensLeft = 12-values.get("hpTok")-values.get("atkTok")-values.get("defTok"); // Tokens remaining
        do
        {
            boolean error = false;
            Game.clearScreen();
            numTokensLeft = 12-values.get("hpTok")-values.get("atkTok")-values.get("defTok");
            out.println("You have " + numTokensLeft + " tokens remaining");
            out.println("Health Tokens: " + values.get("hpTok"));
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
            token = Game.scan.nextLine().toLowerCase();
            
            if(token.contains("e")) return;
            for(int i = 1; i < token.length(); i++) 
            {
                if(!Character.isDigit(token.charAt(i)))
                {
                    out.println("Please enter a number\n\nPress Enter to Continue");
                    Game.scan.nextLine();
                    error = true;
                }
            }
            
            if(token.length() <= 1) ; // First case if no input to ensure no exceptions occur
            else if(token.charAt(0) == '+') 
            {
                int userTok = Integer.valueOf(token.substring(1));
                if(userTok > numTokensLeft)
                {
                    out.println("\nNumber of Tokens selected exceeds currently available tokens\n\nPress Enter to Continue");
                    Game.scan.nextLine();
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
            else if(token.charAt(0) == '-') 
            {
                int userTok = Integer.valueOf(token.substring(1));
                switch(selection)
                {
                    case 'h':
                        if(userTok > values.get("hpTok"))
                        {
                            out.println("\nNumber of Tokens Selected Exceeds Number of Available Health Tokens\n\nPress Enter to Continue");
                            token = "";
                            Game.scan.nextLine();
                        }
                        else values.put("hpTok",values.get("hpTok")-userTok);
                        break;
                    case 'a':
                        if(userTok > values.get("atkTok"))
                        {
                            out.println("\nNumber of Tokens Selected Exceeds Number of Available Attack Tokens\n\nPress Enter to Continue");
                            token = "";
                            Game.scan.nextLine();
                        }
                        else values.put("atkTok",values.get("atkTok")-userTok);
                        break;
                    case 'd':
                        if(userTok > values.get("defTok"))
                        {
                            out.println("\nNumber of Tokens Selected Exceeds Number of Available Defense Tokens\n\nPress Enter to Continue");
                            token = "";
                            Game.scan.nextLine();
                        }
                        else values.put("defTok",values.get("defTok")-userTok);
                        break;
                }
            }
            else if(!error)
            {
                out.println("Invalid beginning character\n\nPress Enter to Continue");
                Game.scan.nextLine();
            }
            token = "";
        }while(token.equals(""));
    }
}
