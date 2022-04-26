public class Villain 
{
    private int hp, atk, def,numPotions = 8; // Challenger's hp, atk, def, and num of potions
    private String lastMove = "none", name; // Last Move and name

    public Villain(int hp,int atk,int def) // Constructor sets value for hp, atk dmg, name, and def
    {
        this.hp = hp; // Set Hp
        this.atk = atk; // Set Atk
        this.def = def; // Set defense
        Names randName = new Names(); // New name instance
        name = randName.getName(); // Set name
    }

    // Getters and Setters
    public void setBoss(int max) // Set boss parameters
    {
        hp = Game.srand.nextInt(max/2) + 100;
        atk = Game.srand.nextInt(max/3) + 50;
        def = max-hp-atk;
    }
    public void setAtk(int atk)
    {
        this.atk = atk;
        if(atk < 0) atk = 0;
    }
    public void setPotions(int numPotions) {this.numPotions = numPotions;}
    public int getPotions() {return numPotions;}
    public String getName(){return name;}
    public int getAtk(){return atk;}
    public void setHp(int hp){this.hp = hp;}
    public int getHp(){return hp;}
    public int getDef(){return def;}
    public void setDef(int def){this.def = def;}
    public void setLastMove(String move){lastMove = move;}
    public String getLastMove(){return lastMove;}

    public void turn() // Challenger Take Turn Method
    {
        Game.clearScreen();
        Main.game.printHP();
        int type = Game.srand.nextInt(10); // Get a random number
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
}
