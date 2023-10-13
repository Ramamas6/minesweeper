package src.client;

public class Player {
    String pseudo;
    boolean alive = true;
    int score1 = 0;
    int score2 = 0;

    public Player(String pseudo, boolean alive, int score1, int score2)
    {
        this.pseudo = pseudo;
        this.alive = alive;
        this.score1 = score1;
        this.score2 = score2;
    }
    public Player(String pseudo) {this.pseudo = pseudo;}

    /**
     * Compare the player with another
     * @param p the other player to compare
     * @return  1 or more if this player is better than the player p
     *          0 if they are equality
     *          -1 or less if the player p is better than this player
     */
    public int compare(Player p) {
        if(this.alive == p.alive) {
            if(this.score1 == p.score1) {
                return this.score2 - p.score2;
            } else return this.score1 - p.score1;
        }
        else if(this.alive && ! p.alive) return 1;
        else return -1;
    }

    /**
     * Reset the stats of the player
     */
    public void reset() {
        this.alive = true;
        this.score1 = 0;
        this.score2 = 0;
    }

    /**
     * @return text to print in the endgame box with more than 2 players
     */
    public String display() {return this.pseudo + " (" + (this.alive ? "alive":"dead") + ") with " + this.score1 + " points (" + this.score2 + "sub-points)";}

    /**
     * Used to print at end of game
     * @param score2 wether to print the 2nd score or not
     * @return a text to print in the display box
     */
    public String getString(boolean score2){
        if(score2) return this.pseudo + " (" + (this.alive ? "alive":"dead") + ") " + this.score1 + " (" + this.score2 + ")";
        return this.pseudo + " (" + (this.alive ? "alive":"dead") + ") " + this.score1;
    }
    public String getString(){return this.getString(false);}

    /**
     * Create the entire text to print in the display box
     * @param p the other player to compare
     * @return an array of texts {title, message} to print in the display box
     */
    public String[] compareAndReturnString(Player p) {
        System.out.println(this.score1);
        System.out.println(p.score1);
        String[] ret = new String[2];
        int n = this.compare(p);
        // Case you win
        if(n > 0) {
            ret[0] = "WIN";
            if(this.score1 > p.score1 || (this.alive && !p.alive)) ret[1] = "You won !\n" + this.getString() + " - " + p.getString();
            else ret[1] = "You won !\n" + this.getString(true) + " - " + p.getString(true);
        } // Case you loose
        else if (n < 0) {
            ret[0] = "LOOSE";
            if(this.score1 < p.score1 || (p.alive && !this.alive)) ret[1] = "You lost...\n" + this.getString() + " - " + p.getString();
            else ret[1] = "You lost...\n" + this.getString(true) + " - " +p.getString(true);
        }
        // Case draw
        else {
            ret[0] = "DRAW";
            ret[1] = "Draw\n" + this.getString(true) + " - " + p.getString(true);
        }
        return ret;
    }

}
