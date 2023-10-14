package src.client;

import java.util.Comparator;

public class SortPlayer implements Comparator<Player> {
    // Used for sorting in descending order the players
    public int compare(Player a, Player b) {
        if(a.getAlive() == b.getAlive()) {
            if(a.getScore1() == b.getScore1()) {
                return b.getScore2() - a.getScore2();
            } else return b.getScore1() - a.getScore1();
        }
        else if(a.getAlive() && ! b.getAlive()) return -1;
        else return 1;
    }
}
