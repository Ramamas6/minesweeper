package src;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.Timer;


public class Main extends JFrame{

    // General
    private Socket socket;
    private DataOutputStream sortie;
    private GUI gui;
    private Matrix matrix;
    private boolean[][] cases; // Matrix's cases

    // Game
    private int gameStarted = 0; // Wether the game is started (0 = no, 1 = yes offline, 2 = yes online)
    private boolean authorizedClick = true; // Wether click on cases is othorized
    private Level currentLevel = Level.MEDIUM;
    private int casesLeft; // Number of non-mines left
    private int minesLeft; // Number of mines left
    private int seconds = 0; // Timer

    // Online mode
    private String pseudo = "";
    List<String> players = new ArrayList<String>();

    public static void main(String[] args) {if (args.length > 0) new Main(args[0]);else new Main("");}
    Main(String s) {
        this.timer(); // Start timer
        this.matrix = new Matrix();
        this.pseudo = s;
        this.gui = new GUI(this); // Create gui
        this.minesLeft = this.matrix.getMines(); // Get number of mines
        this.gui.changeMinesLabel(this.minesLeft);
        addComponentListener(new ComponentAdapter() {public void componentResized(ComponentEvent componentEvent) {gui.redimension(getSize(), currentLevel);}});
        setParameters(true); // Set default options
    }

    /**
     * **************** *
     * SERVER FUNCTIONS *
     * **************** *
    **/

    void setOnLine() {
        try {
            // ouverture de la socket et des streams
            socket = new Socket("LOCALHOST",10000);
            sortie = new DataOutputStream(socket.getOutputStream());
            // Create listening thread
            Runnable r = new ThreadClient(socket, this);
            new Thread(r).start();
        } catch (IOException e) {
                e.printStackTrace();
        }
    }

    void setOffLine() {}

    void broadCastString(String txt){try{sortie.writeUTF(txt);}catch(IOException e){e.printStackTrace();}}
    void broadCastInt(int value){try{sortie.writeInt(value);}catch(IOException e){e.printStackTrace();}}

    /**
     * **************** *
     * JFRAME FUNCTIONS *
     * **************** *
    **/

    void setParameters(boolean firstStart) {
        if (firstStart) {
            setContentPane(gui);
            setDefaultCloseOperation(EXIT_ON_CLOSE);
        }
        pack();
        int[] size = gui.screenSize(currentLevel);
        if (size[0] == 0) setExtendedState(JFrame.MAXIMIZED_BOTH); else setSize(size[0], size[1]);
        setVisible(true);
    }
    void setParameters() {this.setParameters(false);}

    void quit() {
        System.out.println("Bye-Bye");
        System.exit(0);
    }

    /**
     * ************** *
     * GAME FUNCTIONS *
     * ************** *
     */

    /**
     * Called when the Case x,y is left clicked
     * @param x coordinate x of the case
     * @param y coordinate y of the case
     */
    public void isClicked(int x, int y) {
        // If game not started yet (first click) -> start the game
        if (gameStarted == 0) this.startGame(x, y);
        // If the game is started
        else {
            // If it is not a mine : decrease casesLeft
            if (!cases[x][y]) casesLeft --;
            // If the game end now (mine found or all cases discovered) -> final message
            if (cases[x][y] || casesLeft == 0) {
                gameStarted = 0;
                String titleMessage = "Game Over";
                String message = "What do you want to do ?";
                if (casesLeft == 0) titleMessage = "You win !!!"; // Change the message if it if a victory
                Object[] options = {"Try again !","Quit...", "Observation"};
                int res = JOptionPane.showOptionDialog(this,message,titleMessage,
                    JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE,null,options, options[0]);
                if(res == -1 || res == 2) { this.authorizedClick = false; this.endGame();} // Cross or observation -> observation (no click possible)
                if(res == 0) {this.newGame(this.currentLevel);} // New game
                if(res == 1) {this.quit();} // Quit game
            }
            // If the game isn't finished yet and a case 0 is discovered -> propagate
            else if (this.matrix.computeMinesNumber(x, y) == 0) {
                for (int i = Math.max(0,x-1); i < Math.min(cases.length, x+2);i ++)
                    for (int j = Math.max(0,y-1); j < Math.min(cases[0].length, y+2);j ++)
                        gui.leftClick(i, j);        
            }
        }
    }

    public int changeBombs(int i) {
        if (minesLeft + i >= 0) {
            minesLeft += i;
            this.gui.changeMinesLabel(minesLeft);
            return minesLeft;
        }
        else return -1;
    }

    void timer() {
        ActionListener taskPerformer = new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                if (gameStarted > 0) seconds ++;
                gui.changeTimer(seconds);
            }
        };
        new Timer(1000, taskPerformer).start();
    }

    void newGame() {
        this.seconds = 0;
        this.gameStarted = 0; // Wait for the first click
        this.authorizedClick = true; // Authorize to click
        this.matrix.newMatrix(currentLevel); // Create a new array in Matrix
        this.minesLeft = this.matrix.getMines(); // Get number of mines
        this.gui.changeMinesLabel(this.minesLeft);
        gui.newGame(currentLevel);
        this.setParameters();
    }
    void newGame(Level level) {this.currentLevel = level;this.newGame();}

        /**
     * Start game with a 0 in the first clicked case
     * @param x coordinate x of the first case
     * @param y coordinate y of the first case
     */
    void startGame(int x, int y) {
        this.gameStarted = 1;
        // Place mines in Matrix and then in each Case
        this.matrix.fillRandomly(x, y);
        this.cases = this.matrix.getCases();
        for (int i = 0; i < this.matrix.getDimX(); i ++)
            for (int j = 0; j < this.matrix.getDimY(); j ++)
                if(this.matrix.isMine(i,j)) this.gui.setMine(i,j);
        // Compute cases left
        this.casesLeft = this.matrix.getDimX() * this.matrix.getDimY() - this.matrix.getMines() - 1;
        // Propagate the first case
        for (int i = Math.max(0,x-1); i < Math.min(cases.length, x+2);i ++)
            for (int j = Math.max(0,y-1); j < Math.min(cases[0].length, y+2);j ++)
                this.gui.leftClick(i, j);
    }

    /**
     * Called when the game end (to reveal all mines)
     */
    void endGame(){
        for(int i = 0; i < cases.length; i ++)
            for(int j = 0; j < cases.length; j ++)
                this.gui.reveal(i,j,casesLeft == 0);
    }

    int computeMinesNumber(int x, int y) {return this.matrix.computeMinesNumber(x, y);}

    /**
     * ***************** *
     * GETTERS / SETTERS *
     * ***************** *
     */

    public String getPseudo() {return this.pseudo;}
    public void setPseudo(String txt) {this.pseudo = txt;}

    public boolean getAuthorizedClick() {return this.authorizedClick;}

    public void addPlayer(String txt) {this.players.add(txt);}
    public boolean containsPlayer(String txt) {return this.players.contains(txt);}
    public void removePlayer(String txt) {this.players.remove(txt);}

    public int getDimX() {return this.matrix.getDimX();}
    public int getDimY() {return this.matrix.getDimY();}

}
