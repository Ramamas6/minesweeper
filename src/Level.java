package src;


public enum Level {

    EASY("easy", 10, 10, 10), MEDIUM("medium", 16, 16, 40),
    HARD("hard", 20, 20, 80), DIABOLICAL("diabolical", 25, 25, 99);

    private final String LEVEL;
    private final int DIMX;
    private final int DIMY;
    private final int NBMINES;

    Level(String str, int dimx, int dimy, int nbMines) {
        this.LEVEL = str;
        this.DIMX = dimx;
        this.DIMY = dimy;
        this.NBMINES = nbMines;
    }
    Level(String str, int dim, int nbMines) {this(str, dim, dim, nbMines);}
    String getLevel() {return LEVEL;}
    int getDimX() {return DIMX;}
    int getDimY() {return DIMY;}
    int getMines() {return NBMINES;}
}
