package src.client;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class LeftPanel extends JPanel {

    GridBagConstraints cp = new GridBagConstraints(); // Grid constraint for left panel

    // Game
    private JLabel minesLabel; // Label for number of mines left
    private JLabel timerLabel; // Timer label
    private JLabel playerPanel; // Label "Players" (for online mode)

    // Players (case online)
    private Map<String, JLabel> playerLabels = new HashMap<String, JLabel>(); // Names of players (left panel)
    private Map<String, JLabel> scoreLabels = new HashMap<String, JLabel>(); // Scores of players (left panel)
    private Map<String, JLabel> aliveLabels = new HashMap<String, JLabel>(); // Icon of players (left panel)

    public LeftPanel(Color background) {
        this.setLayout(new GridBagLayout());
        // Game
        cp.gridx = 0;
        cp.gridy = 0;
        JLabel temp1 = new JLabel("Game:");
        temp1.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
        this.add(temp1, cp);
            // Time
            cp.gridy ++;
            cp.gridwidth = 2;
            this.add(new JLabel("Time: "), cp);
            cp.gridwidth = 1;
            cp.gridx = 2;
            timerLabel = new JLabel("0");
            this.add(timerLabel, cp);
            // Mines
            cp.gridy ++;
            cp.gridwidth = 2;
            cp.gridx = 0;
            this.add(new JLabel("Mines left: "), cp);
            minesLabel = new JLabel("0");
            cp.gridwidth = 1;
            cp.gridx = 2;
            this.add(minesLabel, cp);
        // End (jump line)
        cp.gridy ++;
        cp.gridx = 0;
        cp.gridwidth = 3;
        this.add(new JLabel("   "), cp);
        cp.gridwidth = 1;
        cp.gridy ++;
        this.playerPanel = new JLabel("Players:");
        this.playerPanel.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
        this.setBackground(background);
    }

    public void changeTimer(int seconds) {this.timerLabel.setText(String.valueOf(seconds));}
    public void changeMinesLabel(int minesLeft) {this.minesLabel.setText(String.valueOf(minesLeft));}

    public void switchOnline(boolean isOnline) {
        // Case offline
        if(!isOnline) {
            cp.gridy --;
            this.remove(this.playerPanel);
            for (Map.Entry<String, JLabel> entry : this.playerLabels.entrySet()) {
                String player = entry.getKey();
                cp.gridy --;
                this.remove(this.playerLabels.get(player));
                this.remove(this.scoreLabels.get(player));
                this.remove(this.aliveLabels.get(player));
            }
        }
        // Case online
        else {
            cp.gridwidth = 2;
            this.add(this.playerPanel, cp);
            cp.gridy ++;
            cp.gridwidth = 1;
        }
    }

    /**
     * ONLINE GAMES
     */

    /**
     * Reset the players variables for an online game
     */
    public void newGame() {
        for (Map.Entry<String,JLabel> entry : scoreLabels.entrySet()) entry.getValue().setText("0");
        for (Map.Entry<String,JLabel> entry : aliveLabels.entrySet()) entry.getValue().setIcon(new ImageIcon("./assets/void.png"));
    }

    public void changeScore(String player, int value) {
        this.scoreLabels.get(player).setText(String.valueOf(value));
        // TODO : change the order
    }

    public void loses(String player) {this.aliveLabels.get(player).setIcon(new ImageIcon("./assets/skull.png"));}

    public void changePlayer(String newPseudo, String oldPseudo) {
        this.playerLabels.put(newPseudo, this.playerLabels.remove(oldPseudo)); // Change the key for player
        this.scoreLabels.put(newPseudo, this.scoreLabels.remove(oldPseudo)); // Change the key for score
        this.aliveLabels.put(newPseudo, this.aliveLabels.remove(oldPseudo)); // Change the key for life indicator
        this.playerLabels.get(newPseudo).setText(newPseudo); // Change the text
    }

    public void addPlayer(String player) {
        cp.gridy ++;
        // Player label
        cp.gridx = 1;
        JLabel pLabel = new JLabel(player + " ");
        this.playerLabels.put(player, pLabel);
        this.add(pLabel, cp);
        // Score label
        cp.gridx = 2;
        JLabel sLabel = new JLabel("0");
        this.scoreLabels.put(player, sLabel);
        this.add(sLabel, cp);
        // Life label
        cp.gridx = 0;
        JLabel lLabel = new JLabel(new ImageIcon("./assets/void.png"));
        this.aliveLabels.put(player, lLabel);
        this.add(lLabel, cp);

        this.revalidate();
    }

    public void removePlayer(String player) {
        this.remove(this.playerLabels.get(player));
        this.remove(this.scoreLabels.get(player));
        cp.gridy --;
        this.playerLabels.remove(player);
        this.scoreLabels.remove(player);
        this.revalidate();
    }
    
}
