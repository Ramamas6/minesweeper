package src.common;
import java.util.Random;

public class Matrix {
    private boolean [][] cases;
    private boolean [][] casesDiscovered;
    private int dimX = 10;
    private int dimY = 10;
    private int nbMines = 0;

    /**
     * Constructor
     * @param level : level of the game (define dimension and number of mines)
     *                default: MEDIUM
     */
    public Matrix(Level level) {
        // Set parameters
        dimX = level.getDimX();
        dimY = level.getDimY();
        nbMines = level.getMines();
        // Creates matrix and place mines
        cases = new boolean[dimX][dimY]; // Initialisé a false par défaut
    }
    public Matrix() {this(Level.MEDIUM);}

    /**
     * Place random mines in the matrix
     * @param x
     * @param y
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
     * Display Matrix
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
     * @param x : x coordinate of the case
     * @param y : y coordinate of the case
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
     * @param level
     * @param x
     * @param y
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

    public int getDimX() {return this.dimX;} // Return x dimension (for non-squared matrix)
    public int getDimY() {return this.dimY;} // Return y dimension (for non-squared matrix)
    public int getMines() {return this.nbMines;} // Return number of mines
    public boolean[][] getCases() {return this.cases;} // Return the cases
    public boolean isMine(int i, int j) {return this.cases[i][j];}
    public boolean isDiscovered(int i, int j) {return this.casesDiscovered[i][j];}
    public void setDiscovered(int i, int j) {this.casesDiscovered[i][j] = true;}

}
