package src.client;

import src.common.Level;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;

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

    private JPanel createTitlePanel() {
        JPanel panel = new JPanel();
        titleLabel = new JLabel("Minesweeper");
        panel.add(titleLabel);
        panel.setSize(WIDTH, 200);
        panel.setBackground(this.theme.getBackground());
        return panel;
    }

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
        panel.setBackground(this.theme.getBackground());
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
        remove(leftPanel);
        leftPanel.switchOnline(isOnline);
        add(leftPanel, BorderLayout.WEST);
        // TO DO : Change right panel
    }

    void changeScore(String player, int value) {this.leftPanel.changeScore(player,value);}

    void loses(String player) {this.leftPanel.loses(player);}

    /**
     * FOR CHANGE DISPLAY
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
    public Theme getTheme(){return this.theme;}

    public void changeTimer(int seconds) {leftPanel.changeTimer(seconds);}
    public void changePlayer(String newPseudo, String oldPseudo) {leftPanel.changePlayer(newPseudo, oldPseudo);}

    public void addPlayer(String player) {leftPanel.addPlayer(player);}
    public void removePlayer(String player) {leftPanel.removePlayer(player);}

    /**
     * 
     */

    public void showCase(int x, int y, int n) {this.grille[x][y].showCase(n);}
    /**
     * Show case in online mode
     * @param x
     * @param y
     * @param n
     * @param i number of the player who discovered the case
     */
    public void showCase(int x, int y, int n, int i) {this.grille[x][y].showCase(n,i);}

    public int computeMinesNumber(int x, int y) {return main.computeMinesNumber(x, y);}

    public boolean getOnline() {return this.main.getOnline();}

    public boolean getAuthorizedClick() {return main.getAuthorizedClick();}

    public int changeBombs(int i) {
        if (this.minesLeft + i >= 0) {
            this.minesLeft += i;
            this.leftPanel.changeMinesLabel(this.minesLeft);
            return this.minesLeft;
        }
        else return -1;
    }

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
