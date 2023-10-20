package src.client;

import src.common.Level;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Client GUI (graphic interface)
 */
public class GUI extends JPanel implements ActionListener {

    // Main panels
    private JPanel titlePanel;
    private JPanel matrixPanel;
    private LeftPanel leftPanel;
    private JPanel rightPanel;
    private JPanel bottomPanel;

    // Objects
    private Main main; // Main object
    private Case[][] grille; // Array of Case
    private Theme theme = Theme.DEFAULT; // General graphic theme

    // Dynamic things
    private JButton buttonQuit; // Quit game button
    private int minesLeft; // Number of mines left
    private JLabel titleLabel;

    // Others variables
    private int DIMX = 1;
    private int DIMY = 1;

    /*
     **************************************************************************
     * 
     ***************************** FIRST CREATION *****************************
     * 
     **************************************************************************
    */

    /**
     * Constructor
     * @param main GUI-related Main object
     * @param minesNumber number of mines of the default level
     */
    GUI(Main main, int minesNumber) {
        // Get remote objects
        this.main = main;
        this.minesLeft = minesNumber;
        // Create the main panels
        titlePanel = createTitlePanel();
        leftPanel = new LeftPanel(this.theme.getBackground());
        leftPanel.changeMinesLabel(this.minesLeft);
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

    /**
     * Creates the title panel
     * @return title panel
     */
    private JPanel createTitlePanel() {
        JPanel panel = new JPanel();
        titleLabel = new JLabel("Minesweeper");
        panel.add(titleLabel);
        panel.setSize(WIDTH, 200);
        panel.setBackground(this.theme.getBackground());
        return panel;
    }

    /**
     * NOT USED
     * Creates the right panel
     * @return right panel
     */
    private JPanel createRightPanel () {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        // END
        panel.setBackground(this.theme.getBackground());
        return panel;
    }

    /**
     * Creates the bottom panel
     * @return bottom panel
     */
    private JPanel createBottomPanel () {
        JPanel panel = new JPanel();
        buttonQuit = new JButton("QUIT");
        buttonQuit.addActionListener(this);
        panel.add(buttonQuit);
        panel.setBackground(this.theme.getBackground());
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
     * @param minesNumber new number of mines
     */
    void newGame(int minesNumber) {
        // MatrixPanel
        remove(matrixPanel); // Remove the main panel from the screen
        this.matrixPanel = createMatrixPanel(); // Recreate matrix panel
        add(matrixPanel, BorderLayout.CENTER); // Add matrix panel in the screen
        // LeftPanel
        leftPanel.changeMinesLabel(minesNumber);
        this.minesLeft = minesNumber;
        // Case online
        if(this.main.getOnline()) {
            this.leftPanel.newGame();
        }

    }

    /**
     * Show the first case of a new game
     * @param x coordinate x of the case
     * @param y coordinate y of the case
     */
    void newGame(int x, int y) {
        this.grille[x][y].showCase(0);
    }

    /**
     * Creates the main (central) panel
     * @return the central panel
     */
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
        panel.setBackground(this.theme.getBackground());
        return panel;
    }

    /**
     * Calculates the optimum screenSize for a given difficulty
     * @param level current level of the game (for width and height of the matrix)
     * @return optimum screenSize, or [0,0] for full size
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

    /**
     * Display the ranking at the end of an online game
     */
    public void displayClassement() {
        if(this.main.getOnline() && main.getPlayerNumber() > 1) {
            // Case 2 players
            if(main.getPlayerNumber() == 2) {
                // Get 2nd player
                ArrayList<Player> players = main.getAllPlayers();
                String player = players.get(0).getPseudo();
                if(player.equals(this.main.getPseudo())) player = players.get(1).getPseudo();
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

    @Override
    public void actionPerformed(ActionEvent e) {
        // Quit game
        if(e.getSource() == buttonQuit) this.main.quit();
    }

    /**
     * Change the graphics when switching online
     * @param isOnline true if the game switches online, false if the game switches offline
     */
    public void switchOnline(boolean isOnline) {
        // TO DO : Change menu
        // Change left panel
        remove(leftPanel);
        leftPanel.switchOnline(isOnline);
        add(leftPanel, BorderLayout.WEST);
        // TO DO : Change right panel
    }

    /**
     * Change the score of one player
     * @param player pseudo of the player
     * @param value new score of the player
     */
    void changeScore(String player, int value) {this.leftPanel.changeScore(player,value);}

    /**
     * Called when a player loses (change his alive icon in left panel)
     * @param player pseudo of the player
     */
    void loses(String player) {this.leftPanel.loses(player);}

    /**
     * FOR CHANGE DISPLAY
     */

    /**
     * Change the theme of the GUI
     * @param theme new theme
     */
    public void changeTheme(Theme theme) {
        this.theme = theme;
        // Change backgrounds
        titlePanel.setBackground(theme.getBackground());
        titleLabel.setForeground(theme.textColor());
        matrixPanel.setBackground(theme.getBackground());
        leftPanel.changeTheme(theme);
        rightPanel.setBackground(theme.getBackground());
        bottomPanel.setBackground(theme.getBackground());
        // Change cases
        Case.CHANGETHEME(theme);
    }

    /**
     * Get the current theme
     * @return current theme
     */
    public Theme getTheme(){return this.theme;}

    /**
     * Change timer label (in left panel)
     * @param seconds new time to display
     */
    public void changeTimer(int seconds) {leftPanel.changeTimer(seconds);}

    /**
     * Change the pseudo of a player (in left panel)
     * @param newPseudo new pseudo to display
     * @param oldPseudo former pseudo to display
     */
    public void changePlayer(String newPseudo, String oldPseudo) {leftPanel.changePlayer(newPseudo, oldPseudo);}

    /**
     * Adds a player in the panel (in left panel)
     * @param player pseudo of the player
     */
    public void addPlayer(String player) {leftPanel.addPlayer(player);}

    /**
     * Removes a player from the panel (in left panel)
     * @param player pseudo of the player
     */
    public void removePlayer(String player) {leftPanel.removePlayer(player);}
    
    /**
     * Show a case (online mode)
     * @param x coordinate x of the case
     * @param y coordinate y of the case
     * @param n value of the case
     * @param i number of the player who discovered the case
     */
    public void showCase(int x, int y, int n, int i) {this.grille[x][y].showCase(n,i);}

    /**
     * Get the value of a case
     * @param x coordinate x of the case
     * @param y coordinate y of the case
     * @return the value of a case
     */
    public int computeMinesNumber(int x, int y) {return main.computeMinesNumber(x, y);}

    /**
     * Get if the client is currently online
     * @return true if this client is currently online, false otherwise
     */
    public boolean getOnline() {return this.main.getOnline();}

    /**
     * Get whether the client is authorized to click or not
     * @return true if the client is authorized to click, false otherwise
     */
    public boolean getAuthorizedClick() {return main.getAuthorizedClick();}

    /**
     * Adds a value to the number of mines left if it is possible
     * @param i value to add the the number of mines left
     * @return the number of mines left the addition was possible, -1 otherwise
     */
    public int changeBombs(int i) {
        if (this.minesLeft + i >= 0) {
            this.minesLeft += i;
            this.leftPanel.changeMinesLabel(this.minesLeft);
            return this.minesLeft;
        }
        else return -1;
    }

    /**
     * Called when the Case x,y is left clicked by the player
     * @param x coordinate x of the case
     * @param y coordinate y of the case
     */
    public void isClicked(int x, int y) {this.main.isClicked(x, y);}

    /**
     * Simulates a left click in a case (if it was not already discovered)
     * @param x coordinate x of the case
     * @param y coordinate y of the case
     */
    public void leftClick(int x, int y) {if (this.grille[x][y].getClicked() != 3) this.grille[x][y].leftClick();}

    /**
     * Reveal a case at the end of the game if it was not already discovered
     * @param x coordinate x of the case
     * @param y coordinate y of the case
     * @param win true if the game is won, false otherwise
     */
    public void reveal(int x, int y, boolean win) {if(grille[x][y].getClicked() != 3) grille[x][y].reveal(win);}

    /**
     * Set a case as a mine
     * @param x coordinate x of the case
     * @param y coordinate y of the case
     */
    public void setMine(int x, int y) {grille[x][y].setMine();}

    /**
     * Resize all the Case to adapt to the screen
     * @param size size of the window
     * @param level current level of the game
     */
    public void redimension(Dimension size, Level level) {
        int x = ((int) size.getWidth() - leftPanel.getWidth() - rightPanel.getWidth()) / level.getDimX();
        int y = ((int) size.getHeight() - bottomPanel.getHeight() - titlePanel.getHeight() - main.getMenuHeight()) / level.getDimY();
        Case.RESIZE(x, y);
    }
}
