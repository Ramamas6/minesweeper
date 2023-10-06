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
    private Matrix matrix; // Matrix
    private boolean[][] cases; // Matrix's cases
    private Main main; // Main object
    private Case[][] grille; // Array of Case

    // Dynamic things
    private JButton buttonQuit; // Quit game button
    private JLabel minesLabel; // Label for number of mines left
    private JLabel timerLabel; // Timer label

    // Menu variables
    private JMenuBar menuBar; // Main menu
    private JMenu difficultyMenu;
    private List<JMenuItem> difficultiesMenuItem;
    private Level lvls[] = { Level.EASY, Level.MEDIUM, Level.HARD, Level.DIABOLICAL }; // Possible levels
    private JButton newMenu;

    // Others variables
    private int minesLeft; // Number of mines left
    private int casesLeft; // Number of non-mines left
    private int seconds; // Timer
    private boolean authorizedClick = true; // Wether click on cases is othorized
    private boolean gameStarted = false; // Wether the game is started
    private Level currentLevel = Level.MEDIUM;
    private static int DIMX = 1;
    private static int DIMY = 1;

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
        this.matrix = new Matrix();
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
        timer();
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
        c.gridx = 0;
        // Pseudo
        c.gridy = 0;
        JLabel pseudoLabel = new JLabel("Ramamas6");
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
        // Stats
        c.gridy = 0;
        this.seconds = 0;
        timerLabel = new JLabel("Time : " + String.valueOf(this.seconds));
        panel.add(timerLabel, c);
        c.gridy = 1;
        minesLeft = matrix.getMines();
        minesLabel = new JLabel("Mines left: " + String.valueOf(minesLeft));
        panel.add(minesLabel, c);
        // END
        panel.setBackground(background);
        return panel;
    }
    JPanel createBottomPanel () {
        JPanel panel = new JPanel();
        buttonQuit = new JButton("QUIT");
        buttonQuit.addActionListener(this);
        panel.add(buttonQuit);
        panel.setSize(WIDTH, 200);
        panel.setBackground(background);
        return panel;
    }
    void createMenu() {
        menuBar = new JMenuBar();
        // On/Off line
        JMenu onOffLine = new JMenu("Online : off");
        menuBar.add(onOffLine);
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

    /**
     * Start timer
     */
    void timer() {
        ActionListener taskPerformer = new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                if (gameStarted) seconds ++;
                timerLabel.setText("Time : " + String.valueOf(seconds));
            }
        };
        new Timer(1000, taskPerformer).start();
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
        this.seconds = 0;
        this.currentLevel = level; // Change the level
        this.gameStarted = false; // Wait for the first click
        this.authorizedClick = true; // Authorize to click
        this.matrix.newMatrix(level); // Create a new array in Matrix
        this.minesLeft = this.matrix.getMines(); // Get number of mines
        this.minesLabel.setText("Mines left: " + String.valueOf(minesLeft)); // Change panel with number of mines left
        this.matrixPanel = createMatrixPanel(); // Recreate matrix panel
        add(matrixPanel, BorderLayout.CENTER); // Add matrix panel in the screen
        this.main.setParametersAgain(); // Pack and set screen size
    }

    JPanel createMatrixPanel() {
        // Variables creations
        int dimX = matrix.getDimX();
        int dimY = matrix.getDimY();
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
    public int[] screenSize() {
        // Resize all the Case with the maximum size
        Rectangle maxDim = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        int x = ((int) maxDim.getWidth() - leftPanel.getWidth() - rightPanel.getWidth()) / currentLevel.getDimX();
        int y = ((int) maxDim.getHeight() - bottomPanel.getHeight() - titlePanel.getHeight() - menuBar.getHeight()) / currentLevel.getDimY();
        DIMX = Math.min(x,y);
        DIMY = Math.min(x,y);
        Case.RESIZE(DIMX, DIMY);
        // Return the screen size
        int[] ret = new int[2];
        ret[0] = DIMX * grille.length + leftPanel.getWidth() + rightPanel.getWidth(); // Max DIMX
        ret[1] = DIMY * grille[1].length + bottomPanel.getHeight() + titlePanel.getHeight() + menuBar.getHeight(); // Max DIMY
        ret[0] = Math.min(ret[0], ret[1]);
        ret[1] = Math.min(ret[0], ret[1]);
        return ret;
    }

    /**
     * Start game with a 0 in the first clicked case
     * @param x coordinate x of the first case
     * @param y coordinate y of the first case
     */
    void startGame(int x, int y) {
        this.gameStarted = true;
        // Place mines in Matrix and then in each Case
        this.matrix.fillRandomly(x, y);
        this.cases = this.matrix.getCases();
        for (int i = 0; i < this.matrix.getDimX(); i ++)
            for (int j = 0; j < this.matrix.getDimY(); j ++)
                if (cases[i][j]) grille[i][j].setMine();
        // Compute cases left
        this.casesLeft = this.matrix.getDimX() * this.matrix.getDimY() - this.matrix.getMines() - 1;
        // Propagate the first case
        for (int i = Math.max(0,x-1); i < Math.min(cases.length, x+2);i ++)
            for (int j = Math.max(0,y-1); j < Math.min(cases[0].length, y+2);j ++)
                if (this.grille[i][j].getClicked() != 3) this.grille[i][j].leftClick();
    }

    /**
     * Called when the game end (to reveal all mines)
     */
    void endGame(){
        for(int i = 0; i < cases.length; i ++)
            for(int j = 0; j < cases.length; j ++)
                if(grille[i][j].getClicked() != 3) grille[i][j].reveal(casesLeft == 0);
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
        if(e.getSource()==buttonQuit) main.quit();
        // New game
        if(e.getSource()==newMenu) this.newGame(this.currentLevel);
        // New game with different level
        if(difficultiesMenuItem.contains(e.getSource())) {
            Level level = lvls[difficultiesMenuItem.indexOf(e.getSource())];
            difficultyMenu.setText("Difficulty " + level.getLevel());
            this.newGame(level);
        }
    }

    public int computeMinesNumber(int x, int y) {return matrix.computeMinesNumber(x, y);}

    public boolean getAuthorizedClick() {return this.authorizedClick;}

    /**
     * Increase or decrease the number of mines left and change the label
     * @param i value to add to the variable minesLeft
     */
    int changeBombs(int i) {
        if (minesLeft + i >= 0) {
            minesLeft += i;
            minesLabel.setText("Mines left: " + String.valueOf(minesLeft));
            return minesLeft;
        }
        else return -1;
    }

    /**
     * Called when the Case x,y is left clicked
     * @param x coordinate x of the case
     * @param y coordinate y of the case
     */
    public void isClicked(int x, int y) {
        // If game not started yet (first click) -> start the game
        if (!gameStarted) startGame(x, y);
        // If the game is started
        else {
            // If it is not a mine : decrease casesLeft
            if (!cases[x][y]) casesLeft --;
            // If the game end now (mine found or all cases discovered) -> final message
            if (cases[x][y] || casesLeft == 0) {
                gameStarted = false;
                String titleMessage = "Game Over";
                String message = "What do you want to do ?";
                if (casesLeft == 0) titleMessage = "You win !!!"; // Change the message if it if a victory
                Object[] options = {"Try again !","Quit...", "Observation"};
                int res = JOptionPane.showOptionDialog(this.main,
                    message,titleMessage,
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,     //do not use a custom Icon
                    options, options[0]);
                if(res == -1 || res == 2) { authorizedClick = false;endGame();} // Cross or observation -> observation (no click possible)
                if(res == 0) {this.newGame(this.currentLevel);} // New game
                if(res == 1) {this.main.quit();} // Quit game
            }
            // If the game isn't finished yet and a case 0 is discovered -> propagate
            else if (this.matrix.computeMinesNumber(x, y) == 0) {
                for (int i = Math.max(0,x-1); i < Math.min(cases.length, x+2);i ++)
                    for (int j = Math.max(0,y-1); j < Math.min(cases[0].length, y+2);j ++)
                        if (this.grille[i][j].getClicked() != 3) this.grille[i][j].leftClick();
            }
        }
    }

    /**
     * Resize all the Case to adapt to the screen
     */
    public void redimension(Dimension size) {
        int x = ((int) size.getWidth() - leftPanel.getWidth() - rightPanel.getWidth()) / currentLevel.getDimX();
        int y = ((int) size.getHeight() - bottomPanel.getHeight() - titlePanel.getHeight() - menuBar.getHeight()) / currentLevel.getDimY();
        Case.RESIZE(x, y);
    }
}
