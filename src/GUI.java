package src;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.ArrayList;

public class GUI extends JPanel implements ActionListener {

    
    // Main panels
    private JPanel titlePanel;
    private JPanel matrixPanel;
    private JPanel leftPanel;
    private JPanel rightPanel;
    private JPanel bottomPanel;

    // Objects
    //private boolean[][] cases; // Matrix's cases
    private Main main; // Main object
    private Case[][] grille; // Array of Case

    // Dynamic things
    private JButton buttonQuit; // Quit game button
    private JLabel minesLabel; // Label for number of mines left
    private JLabel timerLabel; // Timer label

    // Menu variables
    private JMenuBar menuBar; // Main menu
    private JButton onlineMenu;
    private JButton onLine;
    private JButton offLine;
    private JMenu difficultyMenu;
    private List<JMenuItem> difficultiesMenuItem;
    private Level lvls[] = { Level.EASY, Level.MEDIUM, Level.HARD, Level.DIABOLICAL }; // Possible levels
    private JButton newMenu;

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
        createMenu();
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
        GridBagConstraints c = new GridBagConstraints();
        // Game
        c.gridx = 0;
        c.gridy = 0;
        timerLabel = new JLabel("Time : 0");
        panel.add(timerLabel, c);
        c.gridy = 1;
        minesLabel = new JLabel("Mines left: 0");
        panel.add(minesLabel, c);
        // Pseudo
        c.gridy ++;
        JLabel pseudoLabel = new JLabel("Guest");
        panel.add(pseudoLabel, c);
        // End
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
    void createMenu() {
        menuBar = new JMenuBar();
        // Online menu
        Color color = new Color(80,80,80);
        onlineMenu = new JButton("  ONLINE");
            onlineMenu.setBackground(color);
            onlineMenu.setBorderPainted(false);
            onlineMenu.setFocusable(false);
            onlineMenu.addActionListener(this);
            onlineMenu.setBorder(BorderFactory.createEmptyBorder());
            onlineMenu.setMaximumSize(new Dimension(75, 26));
        menuBar.add(onlineMenu);
        onLine = new JButton("   ");
            onLine.setBackground(color);
            onLine.setBorderPainted(false);
            onLine.setFocusable(false);
            onLine.addActionListener(this);
            onLine.setMaximumSize(new Dimension(58, 26));
        menuBar.add(onLine);
        offLine = new JButton("OFF");
            offLine.setBackground(color);
            offLine.setBorderPainted(false);
            offLine.setFocusable(false);
            offLine.addActionListener(this);
            offLine.setMaximumSize(new Dimension(58, 26));
        menuBar.add(offLine);
        menuBar.add(new JLabel(" | "));
        // Difficulty
        difficultyMenu = new JMenu("Difficulty Medium");
        difficultiesMenuItem = new ArrayList<JMenuItem>();
        for (int i = 0; i < lvls.length; i ++) {
            difficultiesMenuItem.add(new JMenuItem(lvls[i].getLevel()));
            difficultyMenu.add(difficultiesMenuItem.get(i));
            difficultiesMenuItem.get(i).addActionListener(this);
        }
        menuBar.add(difficultyMenu);
        menuBar.add(new JLabel(" | "));
        // Settings
        JMenu settingsMenu = new JMenu("Settings");
            // Colors
            JMenu colorMenu = new JMenu("Color normal");
            colorMenu.add(new JMenuItem("Color1"));
            colorMenu.add(new JMenuItem("Color2"));
        settingsMenu.add(colorMenu);
        menuBar.add(settingsMenu);
        menuBar.add(new JLabel("   | "));
        // New game
        newMenu = new JButton("New game");
        newMenu.setContentAreaFilled(false);
        newMenu.setBorderPainted(false);
        newMenu.setFocusable(false);
        newMenu.addActionListener(this);
        menuBar.add(newMenu);
        menuBar.add(new JLabel("|"));
        // End
        this.main.setJMenuBar(menuBar);
    }

    /*
     ******************************************************************************
     * 
     ***************************** BEGINNING OF GAMES *****************************
     * 
     ******************************************************************************
    */

    /**
     * Reset the variables for a new game
     * @param level level of the new game
     */
    void newGame(Level level) {
        remove(matrixPanel); // Remove the main panel from the screen
        this.matrixPanel = createMatrixPanel(); // Recreate matrix panel
        add(matrixPanel, BorderLayout.CENTER); // Add matrix panel in the screen
    }

    JPanel createMatrixPanel() {
        // Variables creations
        int dimX = this.main.getDimX();
        int dimY = this.main.getDimY();
        grille = new Case[dimX][dimY];
        // Panel creation and configurations
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
        int y = ((int) maxDim.getHeight() - bottomPanel.getHeight() - titlePanel.getHeight() - menuBar.getHeight()) / level.getDimY();
        DIMX = Math.min(x,y);
        DIMY = Math.min(x,y);
        Case.RESIZE(DIMX, DIMY);
        // Return the screen size for these case size
        int[] ret = new int[2];
        ret[0] = DIMX * grille.length + leftPanel.getWidth() + rightPanel.getWidth(); // Max DIMX
        ret[1] = DIMY * grille[1].length + bottomPanel.getHeight() + titlePanel.getHeight() + menuBar.getHeight(); // Max DIMY
        ret[0] = Math.min(ret[0], ret[1]);
        ret[1] = Math.min(ret[0], ret[1]);
        return ret;
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
        if(e.getSource() == buttonQuit) main.quit();
        // New game
        else if(e.getSource() == newMenu) main.newGame();
        // Online mode activated/desactivated
        else if(e.getSource() == onLine || e.getSource() == offLine || e.getSource() == onlineMenu) {
            Color color = new Color(80,80,80);
            if(onLine.getText().equals("ON")) {
                onLine.setText("   ");
                offLine.setText("OFF");
                main.setOffLine();
            } else {
                color = new Color(0,220,50);
                onLine.setText("ON");
                offLine.setText("   ");
                main.setOnLine();
            }
            onlineMenu.setBackground(color);
            offLine.setBackground(color);
            onLine.setBackground(color);
        }
        // New game with different level
        else if(difficultiesMenuItem.contains(e.getSource())) {
            Level level = lvls[difficultiesMenuItem.indexOf(e.getSource())];
            difficultyMenu.setText("Difficulty " + level.getLevel());
            main.newGame(level);
        }
    }

    public int computeMinesNumber(int x, int y) {return main.computeMinesNumber(x, y);}

    void changeTimer(int seconds) {timerLabel.setText("Time : " + String.valueOf(seconds));}

    /*
     ***************************************************************************
     * 
     ***************************** GETERS / SETERS *****************************
     * 
     ***************************************************************************
    */

    public boolean getAuthorizedClick() {return main.getAuthorizedClick();}

    /**
     * Increase or decrease the number of mines left and change the label
     * @param i value to add to the variable minesLeft
     */
    int changeBombs(int i) {return this.main.changeBombs(i);}
    void changeMinesLabel(int minesLeft) {if(minesLeft >= 0) minesLabel.setText("Mines left: " + String.valueOf(minesLeft));}


    public void isClicked(int x, int y) {this.main.isClicked(x, y);}

    public void leftClick(int i, int j) {if (this.grille[i][j].getClicked() != 3) this.grille[i][j].leftClick();}
    public void reveal(int i, int j, boolean win) {if(grille[i][j].getClicked() != 3) grille[i][j].reveal(win);}
    public void setMine(int i, int j) {grille[i][j].setMine();}

    /**
     * Resize all the Case to adapt to the screen
     */
    public void redimension(Dimension size, Level level) {
        int x = ((int) size.getWidth() - leftPanel.getWidth() - rightPanel.getWidth()) / level.getDimX();
        int y = ((int) size.getHeight() - bottomPanel.getHeight() - titlePanel.getHeight() - menuBar.getHeight()) / level.getDimY();
        Case.RESIZE(x, y);
    }
}
