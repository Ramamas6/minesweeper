package src.client;

import java.util.Comparator;

/**
 * Class used to sort automaticaly the players
 */
public class SortPlayer implements Comparator<Player> {
    /**
     * Used for sorting in descending order the players
     */
    public int compare(Player a, Player b) {
        if(a.getAlive() == b.getAlive()) {
            if(a.getScore() == b.getScore()) {
                return b.getTieBreakScore() - a.getTieBreakScore();
            } else return b.getScore() - a.getScore();
        }
        else if(a.getAlive() && ! b.getAlive()) return -1;
        else return 1;
    }
}
