package src.client;

/**
 * Class representing a player
 */
public class Player {
    private String pseudo;
    private boolean alive = true;
    private int score = 0;
    private int tieBreakScore = 0;

    /**
     * Main constructor
     * @param pseudo pseudo of the player
     * @param alive whether the player is currently alive or not (he already loses the game)
     * @param score main score of the player
     * @param tieBreakScore tie-breaking score of the player
     */
    public Player(String pseudo, boolean alive, int score, int tieBreakScore)
    {
        this.pseudo = pseudo;
        this.alive = alive;
        this.score = score;
        this.tieBreakScore = tieBreakScore;
    }

    /**
     * Small constructor
     * Creates only the object (with the pseudo), the other parameters are automaticaly initialized for a start of a game
     * @param pseudo pseudo of the player
     */
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
            if(this.score == p.score) {
                return this.tieBreakScore - p.tieBreakScore;
            } else return this.score - p.score;
        }
        else if(this.alive && ! p.alive) return 1;
        else return -1;
    }

    /**
     * Reset the stats of the player (for a new game)
     */
    public void reset() {
        this.alive = true;
        this.score = 0;
        this.tieBreakScore = 0;
    }

    /**
     * Get the text to print in the multiplayers endgame box
     * @return text to print in the endgame box with more than 2 players
     */
    public String display() {
        return this.pseudo + " (" + (this.alive ? "alive":"dead") + ") with "
            + this.score + " points (" + this.tieBreakScore + " sub-points)";
    }

    /**
     * Get the text to print in the 2 players endgame box
     * @param tieBreakScore wether to print the tie-breaking score or not
     * @return a text to print in the display box
     */
    public String getString(boolean tieBreakScore){
        if(tieBreakScore) return this.pseudo + " (" + (this.alive ? "alive":"dead") + ") " + this.score + " (" + this.tieBreakScore + ")";
        return this.pseudo + " (" + (this.alive ? "alive":"dead") + ") " + this.score;
    }

    /**
     * Default function for getString
     * @return the text to print in the 2 players endgame box without tie-breaking score
     */
    public String getString(){return this.getString(false);}

    /**
     * Create the entire text to print in the display box for a 2 players game
     * @param p the other player to compare
     * @return an array of texts {title, message} to print in the display box
     */
    public String[] compareAndReturnString(Player p) {
        System.out.println(this.score);
        System.out.println(p.score);
        String[] ret = new String[2];
        int n = this.compare(p);
        // Case you win
        if(n > 0) {
            ret[0] = "WIN";
            if(this.score > p.score || (this.alive && !p.alive)) ret[1] = "You won !\n" + this.getString() + " - " + p.getString();
            else ret[1] = "You won !\n" + this.getString(true) + " - " + p.getString(true);
        } // Case you loose
        else if (n < 0) {
            ret[0] = "LOOSE";
            if(this.score < p.score || (p.alive && !this.alive)) ret[1] = "You lost...\n" + this.getString() + " - " + p.getString();
            else ret[1] = "You lost...\n" + this.getString(true) + " - " +p.getString(true);
        }
        // Case draw
        else {
            ret[0] = "DRAW";
            ret[1] = "Draw\n" + this.getString(true) + " - " + p.getString(true);
        }
        return ret;
    }

    /**
     * GETTERS / SETTERS
     */
    /**
     * Get the pseudo of the player
     * @return pseudo of the player
     */
    public String getPseudo(){return this.pseudo;}

    /**
     * Get if the player is still alive
     * @return true if the player is still alive, false if he has already lost
     */
    public boolean getAlive(){return this.alive;}

    /**
     * Get the main score the the player
     * @return main score
     */
    public int getScore(){return this.score;}

    /**
     * Get the tie-breaking score of the player
     * @return tie-breaking score
     */
    public int getTieBreakScore(){return this.tieBreakScore;}

    /**
     * Set if the player is still alive or not
     * @param alive new value to set
     */
    public void setAlive(boolean alive){this.alive = alive;}

    /**
     * Set the score of the player
     * @param score new score of the player
     */
    public void setScore(int score){this.score = score;}

    /**
     * Set the tie-breaking score of the player
     * @param score new tie-breaking score of the player
     */
    public void setTieBreakScore(int score){this.tieBreakScore = score;}

    /**
     * Adds a value to the score of the player
     * @param score value to add
     */
    public void addScore(int score){this.score += score;}

    /**
     * Adds a value to the tie-breaking score of the player
     * @param score value to add
     */
    public void addTieBreakScore(int score){this.tieBreakScore += score;}
}
