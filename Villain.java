public class Villain 
{
    private int hp, atk, def; // Challenger's hp, atk, and def
    private String lastMove = "none", name; // Last Move and name

    // Constructor sets value for hp, atk dmg, name, and def
    public Villain(int hp,int atk,int def)
    {
        this.hp = hp;
        this.atk = atk;
        this.def = def;
        Names randName = new Names();
        name = randName.getName();
    }

    // Getters and Setters
    public void setAtk(int atk)
    {
        this.atk = atk;
        if(atk < 0) atk = 0;
    }
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
        int rand = Game.srand.nextInt(10);
        if(rand > 5) //attack
        {
            Main.game.setTurn(Main.game.getTurn()+1);
            
        }
        else if(rand < 3) // potion
        {

        }
        else //block
        {
            Main.game.setTurn(Main.game.getTurn()+1);
        }
    }
}
