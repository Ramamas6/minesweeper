package src;
import java.util.Random;

public class Matrix {
    private boolean [][] cases;
    private int dimX = 10;
    private int dimY = 10;
    private int nbMines = 0;

    /**
     * Constructor
     * @param level : level of the game (define dimension and number of mines)
     *                default: MEDIUM
     */
    Matrix(Level level) {
        // Set parameters
        dimX = level.getDimX();
        dimY = level.getDimY();
        nbMines = level.getMines();
        // Creates matrix and place mines
        cases = new boolean[dimX][dimY]; // Initialisé a false par défaut
    }
    Matrix() {this(Level.MEDIUM);}

    /**
     * Place random mines in the matrix
     * @param x
     * @param y
     */
    void fillRandomly(int x, int y) {
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
    void display() {
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
    int computeMinesNumber(int x, int y) {
        int total = 0;
        for (int i = Math.max(0,x-1); i < Math.min(cases.length, x+2);i ++) {
            for (int j = Math.max(0,y-1); j < Math.min(cases[0].length, y+2);j ++) {
                if (cases[i][j]) total ++;
            }
        }
        return total;
    }

    /**
     * Restart the matrix for a new game
     * @param level : level of the game
     */
    void newMatrix(Level level) {
        this.dimX = level.getDimX();
        this.dimY = level.getDimY();
        this.nbMines = level.getMines();
        this.cases = new boolean[dimX][dimY];
    }

    int getDimX() {return dimX;} // Return x dimension (for non-squared matrix)
    int getDimY() {return dimY;} // Return y dimension (for non-squared matrix)
    int getMines() {return nbMines;} // Return number of mines
    boolean[][] getCases() {return cases;} // Return the cases
    boolean isMine(int i, int j) {return cases[i][j];}

}
