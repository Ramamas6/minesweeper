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

/**
 * Left Panel object
 */
public class LeftPanel extends JPanel {

    // Layouts
    private GridBagLayout gridBagLayout;
    private GridBagConstraints cp = new GridBagConstraints(); // Grid constraint for left panel
    private int cFlag; // gridy after the game part

    // Game
    private JLabel gameText;
    private JLabel timerText;
    private JLabel timerLabel; // Timer label
    private JLabel minesText;
    private JLabel minesLabel; // Label for number of mines left
    private JLabel playerPanel; // Label "Players" (for online mode)

    // Players (case online)
    private Map<String, JLabel> playerLabels = new HashMap<String, JLabel>(); // Names of players (left panel)
    private Map<String, JLabel> scoreLabels = new HashMap<String, JLabel>(); // Scores of players (left panel)
    private Map<String, JLabel> aliveLabels = new HashMap<String, JLabel>(); // Icon of players (left panel)

    /**
     * Constructor (creates the panel)
     * @param background background color
     */
    public LeftPanel(Color background) {
        this.gridBagLayout = new GridBagLayout();
        this.setLayout(this.gridBagLayout);
        // Game
        cp.gridx = 0;
        cp.gridy = 0;
        cp.gridwidth = 1;
        gameText = new JLabel(" Game:");
        gameText.setFont(new Font("Comic Sans MS", Font.BOLD, 18));
        this.add(gameText, cp);
            // Time
            cp.gridy ++;
            cp.gridwidth = 2;
            timerText = new JLabel(new ImageIcon("./assets/chrono.png"));
            //timerText = new JLabel("Time: ");
            this.add(timerText, cp);
            cp.gridwidth = 1;
            cp.gridx = 2;
            timerLabel = new JLabel("0");
            timerLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 16));
            this.add(timerLabel, cp);
            // Mines
            cp.gridy ++;
            cp.gridwidth = 2;
            cp.gridx = 0;
            minesText = new JLabel(new ImageIcon("./assets/mines.png"));
            //minesText = new JLabel("Mines left: ");
            this.add(minesText, cp);
            minesLabel = new JLabel("0");
            minesLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 16));
            cp.gridwidth = 1;
            cp.gridx = 2;
            this.add(minesLabel, cp);
        // End (jump line)
        cp.gridy ++;
        cp.gridx = 0;
        cp.gridwidth = 3;
        this.add(new JLabel("   "), cp);
        cp.gridwidth = 3;
        cp.gridy ++;
        this.cFlag = cp.gridy;
        this.playerPanel = new JLabel("Players:");
        this.playerPanel.setFont(new Font("Comic Sans MS", Font.BOLD, 18));
        this.setBackground(background);
    }

    /**
     * Change timer label
     * @param seconds new time to display
     */
    public void changeTimer(int seconds) {this.timerLabel.setText(String.valueOf(seconds));}

    /**
     * Change mines number label
     * @param minesLeft new mines number to display
     */
    public void changeMinesLabel(int minesLeft) {this.minesLabel.setText(String.valueOf(minesLeft));}

    /**
     * Change the graphics when switching online
     * @param isOnline true the game switch online, false if the game switch offline
     */
    public void switchOnline(boolean isOnline) {
        // Case offline
        if(!isOnline) {
            this.remove(this.playerPanel);
            for (Map.Entry<String, JLabel> entry : this.playerLabels.entrySet()) {
                String player = entry.getKey();
                this.remove(this.playerLabels.get(player));
                this.remove(this.scoreLabels.get(player));
                this.remove(this.aliveLabels.get(player));
            }
            this.playerLabels.clear();
            this.scoreLabels.clear();
            this.aliveLabels.clear();
            System.out.println(this.playerLabels.size());
        }
        // Case online
        else {
            cp.gridwidth = 1;
            cp.gridy = this.cFlag;
            cp.gridx = 0;
            this.add(this.playerPanel, cp);
            cp.gridwidth = 1;
        }
    }

    /**
     * Change the theme of the panel
     * @param theme new theme to display
     */
    public void changeTheme(Theme theme) {
        this.gameText.setForeground(theme.textColor());
        this.timerText.setForeground(theme.textColor());
        this.timerLabel.setForeground(theme.textColor());
        this.minesText.setForeground(theme.textColor());
        this.minesLabel.setForeground(theme.textColor());
        this.playerPanel.setForeground(theme.textColor());
        this.setBackground(theme.getBackground());
    }

    /**
     * ONLINE GAMES
     */

    /**
     * Reset the players variables for a new online game
     */
    public void newGame() {
        for (Map.Entry<String,JLabel> entry : scoreLabels.entrySet()) entry.getValue().setText("0");
        for (Map.Entry<String,JLabel> entry : aliveLabels.entrySet()) entry.getValue().setIcon(new ImageIcon("./assets/void.png"));
    }

    /**
     * Change the score of one player
     * @param player pseudo of the aimed player
     * @param value new score of the player
     */
    public void changeScore(String player, int value) {
        this.scoreLabels.get(player).setText(String.valueOf(value));
        // TODO : change the order
    }

    /**
     * Called when a player loses the game (changes his alive icon)
     * @param player pseudo of the player
     */
    public void loses(String player) {this.aliveLabels.get(player).setIcon(new ImageIcon("./assets/skull.png"));}

    /**
     * Change the pseudo of a player
     * @param newPseudo new pseudo to display
     * @param oldPseudo former pseudo to display
     */
    public void changePlayer(String newPseudo, String oldPseudo) {
        this.playerLabels.put(newPseudo, this.playerLabels.remove(oldPseudo)); // Change the key for player
        this.scoreLabels.put(newPseudo, this.scoreLabels.remove(oldPseudo)); // Change the key for score
        this.aliveLabels.put(newPseudo, this.aliveLabels.remove(oldPseudo)); // Change the key for life indicator
        this.playerLabels.get(newPseudo).setText(newPseudo); // Change the text
    }

    /**
     * Adds a player in the panel
     * @param player pseudo of the player
     */
    public void addPlayer(String player) {
        cp.gridy = this.cFlag + this.playerLabels.size() + 1;
        // Life label
        cp.gridx = 0;
        JLabel lLabel = new JLabel(new ImageIcon("./assets/void.png"));
        this.aliveLabels.put(player, lLabel);
        this.add(this.aliveLabels.get(player), cp);
        // Player label
        cp.gridx = 1;
        JLabel pLabel = new JLabel(player + " ");
        pLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 16));
        this.playerLabels.put(player, pLabel);
        this.add(this.playerLabels.get(player), cp);
        // Score label
        cp.gridx = 2;
        JLabel sLabel = new JLabel("0");
        sLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 16));
        this.scoreLabels.put(player, sLabel);
        this.add(this.scoreLabels.get(player), cp);
        // End: display
        this.revalidate();
        this.repaint();
    }

    /**
     * Removes a player from the panel
     * @param player pseudo of the player
     */
    public void removePlayer(String player) {
        // Remove the player
        this.remove(this.playerLabels.get(player));
        this.remove(this.scoreLabels.get(player));
        this.remove(this.aliveLabels.get(player));
        this.playerLabels.remove(player);
        this.scoreLabels.remove(player);
        this.aliveLabels.remove(player);
        // Change the constraints of all the other players
        cp.gridwidth = 1;
        cp.gridy = this.cFlag;
        for (Map.Entry<String, JLabel> entry : this.playerLabels.entrySet()) {
            String txt = entry.getKey();
            cp.gridy ++;
            cp.gridx = 0;
            this.gridBagLayout.setConstraints(this.aliveLabels.get(txt), cp);
            cp.gridx = 1;
            this.gridBagLayout.setConstraints(this.playerLabels.get(txt), cp);
            cp.gridx = 2;
            this.gridBagLayout.setConstraints(this.scoreLabels.get(txt), cp);
        }
        // Refresh display
        this.revalidate();
        this.repaint();
    }
    
}
