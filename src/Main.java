package src;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.Timer;


public class Main extends JFrame{

    // General
    final static int PORT = 10001;
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
    private boolean online = false;
    private String pseudo = "";
    private Map<String, Integer> players = new HashMap<String, Integer>();

    public static void main(String[] args) {if (args.length > 0) new Main(args[0]);else new Main("");}
    Main(String s) {
        this.timer(); // Start timer
        this.matrix = new Matrix();
        this.pseudo = s;
        if(this.pseudo == null || this.pseudo.isBlank() || !this.pseudo.matches("[a-zA-Z1-9]+")) this.pseudo = "guest";
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

    void switchOnline() {
        try {
            // ouverture de la socket et des streams
            socket = new Socket("LOCALHOST",PORT);
            sortie = new DataOutputStream(socket.getOutputStream());
            // Create listening thread
            Runnable r = new ThreadClient(socket, this);
            new Thread(r).start();
        } catch (IOException e) {
                this.online = false;
                this.gui.switchOnline(false);
                e.printStackTrace();
        }
    }
    void passOnline(){
        this.online = true;
        this.gui.switchOnline(true);
    }

    void switchOffline() {
        this.online = false;
        this.gui.switchOnline(false);
    }

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
     * Called when the Case x,y is left clicked by the player
     * @param x coordinate x of the case
     * @param y coordinate y of the case
     */
    public void isClicked(int x, int y) {
        if(this.online && this.gameStarted == 2) {this.broadCastString("case_" + x + "_" + y);}
        else {
            // If game not started yet (first click) -> start the game
            if (gameStarted == 0) this.startGame(x, y);
            // If the game is started
            else {
                // If it is not a mine : decrease casesLeft
                if (!cases[x][y]) casesLeft --;
                // If the game end now (mine found or all cases discovered) -> final message
                if (cases[x][y] || casesLeft == 0) {
                    this.gameStarted = 0;
                    String titleMessage = "Game Over";
                    String message = "What do you want to do ?";
                    ImageIcon icon = new ImageIcon("./assets/gameover.png");
                    if (casesLeft == 0) {
                        titleMessage = "You win !!!"; // Change the message if it if a victory
                        icon = new ImageIcon("./assets/win.png"); // Change the icon if it if a victory
                    }
                    Object[] options = {"Try again !","Quit...", "Observation"};
                    int res = JOptionPane.showOptionDialog(this,message,titleMessage,
                        JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE,icon,options, options[0]);
                    if(res == -1 || res == 2) {this.endGame();} // Cross or observation -> observation (no click possible)
                    if(res == 0) {this.newGame();} // New game
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
    }

    /**
     * Called on online mode, when a player clicked on a case
     * @param player pseudo of the player
     * @param x coordinate x
     * @param y coordinate y
     * @param n value of the case
     */
    public void isClicked(String player, int x, int y, int n) {
        this.showCase(x,y,n); // Display the case
        this.gui.changeScore(player,n); // Actualise 1rst score
        this.players.replace(player, this.players.get(player) + 1);// Actualise 2nd score (for equalities)
    }

    public void loses(String player) {
        this.gui.loses(player);
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

    /**
     * Called when "new game" is pressed
     */
    void newGame() {
        // Case Offline
        if(!this.online) {
            this.seconds = 0;
            this.gameStarted = 0; // Wait for the first click
            this.authorizedClick = true; // Authorize to click
            this.matrix.newMatrix(currentLevel); // Create a new array in Matrix
            this.minesLeft = this.matrix.getMines(); // Get number of mines
            this.gui.changeMinesLabel(this.minesLeft);
            gui.newGame();
            this.setParameters();
        }
        // Case start game Online
        else if (this.gameStarted < 2) {
            this.broadCastString("command_start_game");
        } else {} // Do nothing : online, but game already started
    }

    /**
     * Start a new Online game
     */ 
    void newGame(int dimx, int dimy, int minesNumber) {
        this.authorizedClick = false;
        this.gameStarted = 2;
        this.seconds = 0;
        this.matrix.newMatrix(currentLevel, dimx, dimy, minesNumber);
        this.cases = this.matrix.getCases();
        this.minesLeft = minesNumber;
        this.gui.changeMinesLabel(minesNumber);
        this.gui.newGame();
        this.setParameters();
    }

    void changeDifficulty(Level level) {
        // Case Offline
        if(!this.online) {
            this.currentLevel = level;
            this.gui.changeDifficulty(level.getLevel());
            this.newGame();
        }
        // Case Online
        else if (this.gameStarted < 2) {
            if(this.currentLevel != level) {
                this.currentLevel = level;
                this.gui.changeDifficulty(this.currentLevel.getLevel());
                this.broadCastString("command_difficulty_" + level.getLevel());
            }
        } else {this.gui.changeDifficulty(this.currentLevel.getLevel());} // Do nothing : online, but game already started
    }

        /**
     * Start game with a 0 in the first clicked case
     * @param x coordinate x of the first case
     * @param y coordinate y of the first case
     */
    void startGame(int x, int y) {
        // Case offline
        if(!this.online) {
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
        // Case online
        else {
            this.gui.newGame(x, y);
            this.authorizedClick = true;
        }
    }

    /**
     * Called when the game end (to reveal all mines)
     */
    void endGame() {
        this.gameStarted = 0;
        this.authorizedClick = false;
        // Reveal all the cases
        int x = this.matrix.getDimX();
        int y = this.matrix.getDimY();
        for(int i = 0; i < x; i ++)
            for(int j = 0; j < y; j ++)
                this.gui.reveal(i,j,casesLeft == 0);
        // Case online -> display classement
        this.gui.displayClassement();
    }

    int computeMinesNumber(int x, int y) {return this.matrix.computeMinesNumber(x, y);}

    public void showCase(int x, int y, int n) {this.gui.showCase(x,y,n);}

    void changePseudo() {
        // Case offline
        if(!this.online) {
            boolean run = true;
            String message = "Enter your pseudo:";
            while(run) {
                String txt = JOptionPane.showInputDialog(this,message,"",JOptionPane.QUESTION_MESSAGE);
                if(txt == null) {run = false;}
                else if (txt.isEmpty()) {message = "Pseudo cannot be empty.\nPlease enter a valid pseudo:";}
                else if (txt.isBlank()) {message = "Pseudo cannot be blank.\nPlease enter a valid pseudo:";}
                else if (txt.matches("[a-zA-Z1-9]+")) {
                    run = false;
                    this.gui.changePseudo(txt, this.pseudo);
                    this.pseudo = txt;
                } else {message = "Pseudo cannot contains special characters.\nPlease enter a valid pseudo:";}
            }
        }
        // Case Online
        else if (this.gameStarted < 2) {
        } else {} // Do nothing : online, but game already started
    }

    /**
     * ***************** *
     * GETTERS / SETTERS *
     * ***************** *
     */

    public String getPseudo() {return this.pseudo;}
    public void setPseudo(String txt) {this.pseudo = txt;}

    public boolean getOnline() {return this.online;}
    public void setOnline(boolean online) {this.online = online;}

    public boolean getAuthorizedClick() {return this.authorizedClick;}

    public void addPlayer(String txt) {
        this.players.put(txt, 0);
        this.gui.addPlayer(txt);
    }
    public boolean containsPlayer(String txt) {return this.players.containsKey(txt);}
    public void removePlayer(String txt) {
        this.players.remove(txt);
        if(this.online) this.gui.removePlayer(txt);
    }
    public int getPlayer(String txt) {return this.players.get(txt);}

    public int getDimX() {return this.matrix.getDimX();}
    public int getDimY() {return this.matrix.getDimY();}

    public int getGameState() {return this.gameStarted;}

}
