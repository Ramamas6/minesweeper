package src.common;

/**
 * Levels possible
 */
public enum Level {

    /**
     * Easiest level
     */
    EASY("easy", 10, 10, 10),
    /**
     * Default level
     */
    MEDIUM("medium", 16, 16, 40),
    /**
     * Hard level
     */
    HARD("hard", 20, 20, 80),
    /**
     * Hardest level
     */
    DIABOLICAL("diabolical", 25, 25, 99);

    private final String LEVEL;
    private final int DIMX;
    private final int DIMY;
    private final int NBMINES;

    /**
     * Constructor
     * @param str name of this level
     * @param dimx dimension x (number of cases in width)
     * @param dimy dimension y (number of cases in height)
     * @param nbMines number of mines for this level
     */
    private Level(String str, int dimx, int dimy, int nbMines) {
        this.LEVEL = str;
        this.DIMX = dimx;
        this.DIMY = dimy;
        this.NBMINES = nbMines;
    }
    /**
     * Second constructor (with same dimension x and y)
     * @param str name of this level
     * @param dim dimension (number of cases in width and height)
     * @param nbMines number of mines for this level
     */
    private Level(String str, int dim, int nbMines) {this(str, dim, dim, nbMines);}
    /**
     * Get the name of the level
     * @return name of the level
     */
    public String getLevel() {return LEVEL;}
    /**
     * Get the dimension x of the level
     * @return number of cases in width
     */
    public int getDimX() {return DIMX;}
    /**
     * Get the dimension y of the level
     * @return number of cases in height
     */
    public int getDimY() {return DIMY;}
    /**
     * Get the number of mines for the level
     * @return number of mines
     */
    public int getMines() {return NBMINES;}
}
