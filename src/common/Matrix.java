package src.common;
import java.util.Random;

/**
 * Matrix of the cases
 */
public class Matrix {
    private boolean [][] cases; // Main cases table
    private boolean [][] casesDiscovered; // Cases table for the server (precising if they are already discovered)
    private int dimX = 10;
    private int dimY = 10;
    private int nbMines = 0;

    /**
     * Constructor
     * @param level level of the game (define dimension and number of mines)
     */
    public Matrix(Level level) {
        // Set parameters
        dimX = level.getDimX();
        dimY = level.getDimY();
        nbMines = level.getMines();
        // Creates matrix and place mines
        cases = new boolean[dimX][dimY]; // Initialisé a false par défaut
    }
    /**
     * Constructor with default level MEDIUM
     */
    public Matrix() {this(Level.MEDIUM);}

    /**
     * Place random mines in the matrix, with none around the specified coordinate
     * @param x coordinate x
     * @param y coordinate y
     */
    public void fillRandomly(int x, int y) {
        Random generator = new Random();
        int mineX, mineY;
        int nb = 0;
        // Place mines
        while (nb < nbMines) {
            mineX = generator.nextInt(cases.length);
            mineY = generator.nextInt(cases[0].length);
            if (!cases[mineX][mineY]) // If not mine
                if (Math.abs(mineX - x) > 1 || Math.abs(mineY - y) > 1) { // If not start case
                cases[mineX][mineY] = true;
                nb ++;
            }
        }
        //this.display();
    }

    /**
     * Display the Matrix
     */
    public void display() {
        System.out.print("\n");
        for (int i = 0; i < cases.length; i ++) {
            for (int j = 0; j < cases[0].length; j ++) {
                if (cases[i][j]) System.out.print(" x ");
                else System.out.print(" " + computeMinesNumber(i, j) + " ");
            }
            System.out.print("\n");
        }
        System.out.print("\n");
    }

    /**
     * Compute number of mines around a case
     * @param x coordinate x of the case
     * @param y coordinate y of the case
     * @return number of mines around the case
     */
    public int computeMinesNumber(int x, int y) {
        int total = 0;
        for (int i = Math.max(0,x-1); i < Math.min(cases.length, x+2);i ++) {
            for (int j = Math.max(0,y-1); j < Math.min(cases[0].length, y+2);j ++) {
                if (cases[i][j]) total ++;
            }
        }
        return total;
    }

    /**
     * Restart the matrix for a new offline game
     * @param level : level of the game
     */
    public void newMatrix(Level level) {
        this.dimX = level.getDimX();
        this.dimY = level.getDimY();
        this.nbMines = level.getMines();
        this.cases = new boolean[dimX][dimY];
    }
    /**
     * Restart the matrix for a new online game
     * @param level level of the game
     * @param dimx dimension x (width) of the grid
     * @param dimy dimension y (height) of the grid
     * @param minesNumber number of mines in the game
     */
    public void newMatrix(Level level, int dimx, int dimy, int minesNumber) {
        this.dimX = dimx;
        this.dimY = dimy;
        this.nbMines = minesNumber;
        this.cases = new boolean[dimX][dimY];
    }
    /**
     * Restart the matrix for the server
     * @param level : level of the game
     * @param x position x of the first case
     * @param y position y of the first case
     */
    public void newMatrix(Level level, int x, int y) {
        this.newMatrix(level);
        this.casesDiscovered = new boolean[this.dimX][this.dimY];
        this.fillRandomly(x, y);
    }

    /**
     * Get the x dimension (width) of the matrix
     * @return width
     */
    public int getDimX() {return this.dimX;}
    /**
     * Get the y dimension (height) of the matrix
     * @return height
     */
    public int getDimY() {return this.dimY;}
    /**
     * Get number of mines of the matrix
     * @return number of mines
     */
    public int getMines() {return this.nbMines;}
    /**
     * Get the cases table of the matrix
     * @return the cases table
     */
    public boolean[][] getCases() {return this.cases;}
    /**
     * Get if a case is a mine or not
     * @param x coordinate x of the case
     * @param y coordinate y of the case
     * @return true if the case is a mine, false otherwise
     */
    public boolean isMine(int x, int y) {return this.cases[x][y];}
    /**
     * Get if the case is already discovered (for the server only)
     * @param x coordinate x of the case
     * @param y coordinate y of the case
     * @return true if the case is already discovered, false otherwise
     */
    public boolean isDiscovered(int x, int y) {return this.casesDiscovered[x][y];}
    /**
     * Set a case as discovered
     * @param x coordinate x of the case
     * @param y coordinate y of the case
     */
    public void setDiscovered(int x, int y) {this.casesDiscovered[x][y] = true;}

}
