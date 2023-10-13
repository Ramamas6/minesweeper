package src.client;

import java.util.Comparator;

public class SortPlayer implements Comparator<Player> {
    // Used for sorting in descending order the players
    public int compare(Player a, Player b) {
        if(a.alive == b.alive) {
            if(a.score1 == b.score1) {
                return b.score2 - a.score2;
            } else return b.score1 - a.score1;
        }
        else if(a.alive && ! b.alive) return -1;
        else return 1;
    }
}
