package src;


public enum Level {

    EASY("Easy", 10, 10, 10), MEDIUM("Medium", 16, 16, 40),
    HARD("Hard", 20, 20, 80), DIABOLICAL("Diabolical", 25, 25, 99);

    private final String level;
    private final int DIMX;
    private final int DIMY;
    private final int NBMINES;

    Level(String str, int dimx, int dimy, int nbMines) {
        this.level = str;
        this.DIMX = dimx;
        this.DIMY = dimy;
        this.NBMINES = nbMines;
    }
    Level(String str, int dim, int nbMines) {this(str, dim, dim, nbMines);}
    String getLevel() {return level;}
    int getDimX() {return DIMX;}
    int getDimY() {return DIMY;}
    int getMines() {return NBMINES;}
}
