package src.client;

import src.common.Level;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class GUI extends JPanel implements ActionListener {

    
    // Main panels
    private JPanel titlePanel;
    private JPanel matrixPanel;
    private JPanel leftPanel;
    private JPanel rightPanel;
    private JPanel bottomPanel;
    private Map<String, JLabel> playersPanel = new HashMap<String, JLabel>(); // Names of players (left panel)
    private Map<String, JLabel> scoresPanel = new HashMap<String, JLabel>(); // Scores of players (left panel)
    private Map<String, JLabel> lifePanel = new HashMap<String, JLabel>(); // Icon of players (left panel)
    GridBagConstraints cp = new GridBagConstraints(); // Grid constraint for left panel

    // Objects
    //private boolean[][] cases; // Matrix's cases
    private Main main; // Main object
    private Case[][] grille; // Array of Case
    private Theme theme = Theme.DEFAULT; // General graphic theme

    // Dynamic things
    private JButton buttonQuit; // Quit game button
    private JLabel minesLabel; // Label for number of mines left
    private JLabel timerLabel; // Timer label

    // Others variables
    private int DIMX = 1;
    private int DIMY = 1;


    // Color
    private Color background = new Color(74, 117, 44);

    /*
     **************************************************************************
     * 
     ***************************** FIRST CREATION *****************************
     * 
     **************************************************************************
    */

    GUI(Main main) {
        // Get remote objects
        this.main = main;
        // Create the main panels
        titlePanel = createTitlePanel();
        leftPanel = createLeftPanel();
        rightPanel = createRightPanel();
        bottomPanel = createBottomPanel();
        // Place the main panels
        setLayout(new BorderLayout());
        add(titlePanel, BorderLayout.NORTH);
        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);
        // Create and place matrix panel
        matrixPanel = createMatrixPanel();
        add(matrixPanel, BorderLayout.CENTER);
        // Start timer
    }

    JPanel createTitlePanel() {
        JPanel panel = new JPanel();
        panel.add(new JLabel("Minesweeper"));
        panel.setSize(WIDTH, 200);
        panel.setBackground(background);
        return panel;
    }
    JPanel createLeftPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        // Game
        cp.gridx = 0;
        cp.gridy = 0;
        JLabel temp1 = new JLabel("Game:");
        temp1.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
        panel.add(temp1, cp);
            // Time
            cp.gridy ++;
            cp.gridwidth = 2;
            panel.add(new JLabel("Time: "), cp);
            cp.gridwidth = 1;
            cp.gridx = 2;
            timerLabel = new JLabel("0");
            panel.add(timerLabel, cp);
            // Mines
            cp.gridy ++;
            cp.gridwidth = 2;
            cp.gridx = 0;
            panel.add(new JLabel("Mines left: "), cp);
            minesLabel = new JLabel("0");
            cp.gridwidth = 1;
            cp.gridx = 2;
            panel.add(minesLabel, cp);
        // Players
        cp.gridy ++;
        cp.gridx = 0;
        cp.gridwidth = 3;
        panel.add(new JLabel("   "), cp);
            // Case offline
        if(!this.main.getOnline()) {}
            // Case online
        else {
            cp.gridy ++;
            cp.gridwidth = 2;
            JLabel temp2 = new JLabel("Players:");
            temp2.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
            panel.add(temp2, cp);
        }
        // End
        cp.gridwidth = 1;
        cp.gridy ++;
        panel.setBackground(background);
        return panel;
    }
    JPanel createRightPanel () {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        // END
        panel.setBackground(background);
        return panel;
    }
    JPanel createBottomPanel () {
        JPanel panel = new JPanel();
        buttonQuit = new JButton("QUIT");
        buttonQuit.addActionListener(this);
        panel.add(buttonQuit);
        panel.setBackground(background);
        return panel;
    }

    /*
     ******************************************************************************
     * 
     ***************************** BEGINNING / END GAMES *****************************
     * 
     ******************************************************************************
    */

    /**
     * Reset the variables for a new game
     */
    void newGame() {
        remove(matrixPanel); // Remove the main panel from the screen
        this.matrixPanel = createMatrixPanel(); // Recreate matrix panel
        add(matrixPanel, BorderLayout.CENTER); // Add matrix panel in the screen
        // Case online
        if(this.main.getOnline()) {
            for (Map.Entry<String,JLabel> entry : scoresPanel.entrySet()) entry.getValue().setText("0");
            for (Map.Entry<String,JLabel> entry : lifePanel.entrySet()) entry.getValue().setIcon(new ImageIcon("./assets/void.png"));
        }
    }
    void newGame(int x, int y) {
        this.grille[x][y].showCase(0);
    }

    JPanel createMatrixPanel() {
        // Variables creations
        int dimX = this.main.getDimX();
        int dimY = this.main.getDimY();
        grille = new Case[dimX][dimY];
        // Panel creation and configurationleftClicks
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(dimX, dimY));
        // Add all the cases
        for (int i = 0; i < dimX; i ++) {
            for (int j = 0; j < dimY; j ++) {
                Case plot = new Case(this, i,j);
                grille[i][j] = plot;
                panel.add(grille[i][j]);
            }
        }
        Case.RESIZE(DIMX, DIMY);
        panel.setBackground(background);
        return panel;
    }

    /**
     * @return optimum screenSize for a difficulty or [0,0] for full size
     */
    public int[] screenSize(Level level) {
        // Resize all the Case with the optimal size for square
        Rectangle maxDim = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        int x = ((int) maxDim.getWidth() - leftPanel.getWidth() - rightPanel.getWidth()) / level.getDimX();
        int y = ((int) maxDim.getHeight() - bottomPanel.getHeight() - titlePanel.getHeight() - main.getMenuHeight()) / level.getDimY();
        DIMX = Math.min(x,y);
        DIMY = Math.min(x,y);
        Case.RESIZE(DIMX, DIMY);
        // Return the screen size for these case size
        int[] ret = new int[2];
        ret[0] = DIMX * grille.length + leftPanel.getWidth() + rightPanel.getWidth(); // Max DIMX
        ret[1] = DIMY * grille[1].length + bottomPanel.getHeight() + titlePanel.getHeight() + main.getMenuHeight(); // Max DIMY
        ret[0] = Math.min(ret[0], ret[1]);
        ret[1] = Math.min(ret[0], ret[1]);
        return ret;
    }

    public void displayClassement() {
        if(this.main.getOnline() && playersPanel.size() > 1) {
            // Case 2 players
            if(playersPanel.size() == 2) {
                // Get 2nd player
                String[] temp = scoresPanel.keySet().toArray(new String[2]);
                String player = temp[1];
                if(player.equals(this.main.getPseudo())) player = temp[0];
                // Display the final box
                String[] texts = this.main.getPlayer(this.main.getPseudo()).compareAndReturnString(this.main.getPlayer(player));
                JOptionPane.showMessageDialog(this,texts[1],texts[0],JOptionPane.INFORMATION_MESSAGE);
            }
            // Case multi-players
            else {
                ArrayList<Player> playersList = this.main.getAllPlayers(); // Get the list of players
                Collections.sort(playersList, new SortPlayer()); // Sort the list
                int position = playersList.indexOf(this.main.getPlayer(this.main.getPseudo())) + 1; // Get the position
                // Title
                String title = "You are number " + position + "...";
                if(position < 4) title = "You are number " + position + "!";
                // Message
                String message = "First: " + playersList.get(0).display()
                                + "\nSecond: " + playersList.get(1).display()
                                + "\nThird: " + playersList.get(2).display();
                JOptionPane.showMessageDialog(this,message,title,JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    /*
     ******************************************************************************
     * 
     ***************************** RECURENT FUNCTIONS *****************************
     * 
     ******************************************************************************
    */

    /**
     * Performed actions
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        // Quit game
        if(e.getSource() == buttonQuit) this.main.quit();
    }

    /**
     * Change the graphics when switching online
     * @param isOnline specify wether the game is online or not
     */
    public void switchOnline(boolean isOnline) {
        // TO DO : Change menu
        // Change left panel
        remove(leftPanel); // Remove the main panel from the screen
        this.leftPanel = createLeftPanel(); // Recreate matrix panel
        add(leftPanel, BorderLayout.WEST); // Add matrix panel in the screen
        // TO DO : Change right panel
    }

    void changeScore(String player, int value) {
        int n = Integer.parseInt(this.scoresPanel.get(player).getText());
        this.scoresPanel.get(player).setText(String.valueOf(n+value));
    }

    void loses(String player) {
        System.out.println(player + " loses");
        this.lifePanel.get(player).setIcon(new ImageIcon("./assets/skull.png"));
    }

    /**
     * FOR CHANGE DISPLAY
     */

    public void changeTheme(Theme theme) {
        this.theme = theme;
        // Change backgrounds
        titlePanel.setBackground(theme.getBackground());
        matrixPanel.setBackground(theme.getBackground());
        leftPanel.setBackground(theme.getBackground());
        rightPanel.setBackground(theme.getBackground());
        bottomPanel.setBackground(theme.getBackground());
        // Change cases
        Case.CHANGETHEME(theme);
        // // TODO: Change cases
        // for(int i = 0; i < this.dimX; i ++)
        //     for(int j = 0; j < this.dimY; j ++)
        //         this.grille[i][j].changeTheme
    }
    public Theme getTheme(){return this.theme;}

    public void changeTimer(int seconds) {timerLabel.setText(String.valueOf(seconds));}
    
    public void changeMinesLabel(int minesLeft) {if(minesLeft >= 0) minesLabel.setText(String.valueOf(minesLeft));}
    
    public void changePlayer(String newPseudo, String oldPseudo) {
        this.playersPanel.put(newPseudo, this.playersPanel.remove(oldPseudo)); // Change the key for player
        this.scoresPanel.put(newPseudo, this.scoresPanel.remove(oldPseudo)); // Change the key for score
        this.lifePanel.put(newPseudo, this.lifePanel.remove(oldPseudo)); // Change the key for life indicator
        this.playersPanel.get(newPseudo).setText(newPseudo); // Change the text
    }
    public void addPlayer(String player) {
        cp.gridy ++;
        // Player label
        cp.gridx = 1;
        JLabel pLabel = new JLabel(player + " ");
        this.playersPanel.put(player, pLabel);
        leftPanel.add(pLabel, cp);
        // Score label
        cp.gridx = 2;
        JLabel sLabel = new JLabel("0");
        this.scoresPanel.put(player, sLabel);
        leftPanel.add(sLabel, cp);
        // Life label
        cp.gridx = 0;
        JLabel lLabel = new JLabel(new ImageIcon("./assets/void.png"));
        this.lifePanel.put(player, lLabel);
        leftPanel.add(lLabel, cp);

        leftPanel.revalidate();
    }
    public void removePlayer(String player) {
        leftPanel.remove(this.playersPanel.get(player));
        leftPanel.remove(this.scoresPanel.get(player));
        cp.gridy --;
        this.playersPanel.remove(player);
        this.scoresPanel.remove(player);
        leftPanel.revalidate();
    }

    /**
     * 
     */

    public void showCase(int x, int y, int n) {this.grille[x][y].showCase(n);}

    public int computeMinesNumber(int x, int y) {return main.computeMinesNumber(x, y);}

    public boolean getOnline() {return this.main.getOnline();}

    public boolean getAuthorizedClick() {return main.getAuthorizedClick();}

    public int changeBombs(int i) {return this.main.changeBombs(i);}

    public void isClicked(int x, int y) {this.main.isClicked(x, y);}

    public void leftClick(int i, int j) {if (this.grille[i][j].getClicked() != 3) this.grille[i][j].leftClick();}
    public void reveal(int i, int j, boolean win) {if(grille[i][j].getClicked() != 3) grille[i][j].reveal(win);}
    public void setMine(int i, int j) {grille[i][j].setMine();}

    /**
     * Resize all the Case to adapt to the screen
     */
    public void redimension(Dimension size, Level level) {
        int x = ((int) size.getWidth() - leftPanel.getWidth() - rightPanel.getWidth()) / level.getDimX();
        int y = ((int) size.getHeight() - bottomPanel.getHeight() - titlePanel.getHeight() - main.getMenuHeight()) / level.getDimY();
        Case.RESIZE(x, y);
    }
}
