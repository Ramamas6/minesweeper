package src.client;

import src.common.Level;
import src.common.Matrix;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.Timer;

public class Main extends JFrame{

    // General
    int port = 10000;
    String host = "LOCALHOST";
    private Socket socket;
    private DataOutputStream sortie;
    private GUI gui;
    private Matrix matrix;

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
    private Map<String, Player> players = new HashMap<String, Player>();

    public static void main(String[] args) {if (args.length > 0) new Main(args[0]);else new Main("");}
    private Main(String s) {
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

    public void changeConnectionSettings() {
        JTextField hostField = new JTextField(this.host);
        JTextField portField = new JTextField(String.valueOf(this.port));
        Object[] message = {"Host:", hostField,"Port:", portField};
        int connection = JOptionPane.showConfirmDialog(this, message, "Connection to server", JOptionPane.OK_CANCEL_OPTION);
        if(connection == JOptionPane.OK_OPTION) { // If OK pressed, get host and port
            this.host = hostField.getText();
            this.port = Integer.parseInt(portField.getText());
        }
    }

    /**
     * Called when the button connection is pressed
     * Try to connect, or launch a timer to try again every seconds if the connection failed
     */
    public void switchOnline() {
        if(!tryConnect()) {
            ActionListener tryConnection = new ActionListener() {
                public void actionPerformed(ActionEvent evt) {if(tryConnect()) ((Timer)evt.getSource()).stop();}
            }; new Timer(1000, tryConnection).start();
        }
    }
    /**
     * Try to connect to the server
     * @return wether the connection succed or not
     */
    private boolean tryConnect() {
        try{
            // Open socket and streams
            socket = new Socket(host,port);
            sortie = new DataOutputStream(socket.getOutputStream());
            // Create listening thread
            Runnable r = new ThreadClient(socket, this);
            new Thread(r).start();
            this.online = true;
            this.gui.switchOnline(true);
            return true;
        } catch (IOException e) {return false;}
    }


    public void switchOffline() {
        this.online = false;
        this.gui.switchOnline(false);
    }

    public void broadCastString(String txt){try{sortie.writeUTF(txt);}catch(IOException e){e.printStackTrace();}}
    public void broadCastInt(int value){try{sortie.writeInt(value);}catch(IOException e){e.printStackTrace();}}

    /**
     * **************** *
     * JFRAME FUNCTIONS *
     * **************** *
    **/

    private void setParameters(boolean firstStart) {
        if (firstStart) {
            setContentPane(gui);
            setDefaultCloseOperation(EXIT_ON_CLOSE);
        }
        pack();
        int[] size = gui.screenSize(currentLevel);
        if (size[0] == 0) setExtendedState(JFrame.MAXIMIZED_BOTH); else setSize(size[0], size[1]);
        setVisible(true);
    }
    private void setParameters() {this.setParameters(false);}
    public void quit() {
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
                if (!this.matrix.isMine(x, y)) casesLeft --;
                // If the game end now (mine found or all cases discovered) -> final message
                if (this.matrix.isMine(x, y) || casesLeft == 0) {
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
                    for (int i = Math.max(0,x-1); i < Math.min(this.matrix.getDimX(), x+2);i ++)
                        for (int j = Math.max(0,y-1); j < Math.min(this.matrix.getDimY(), y+2);j ++)
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
        this.gui.changeScore(player,n); // Actualise 1rst score in gui
        this.players.get(player).score1 += n; // Actualise 1rst score
        this.players.get(player).score2 ++; // Actualise 2nd score (for equalities)
    }

    public void loses(String player) {
        this.players.get(player).alive = false;
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

    private void timer() {
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
    public void newGame() {
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
    public void newGame(int dimx, int dimy, int minesNumber) {
        this.authorizedClick = false;
        this.gameStarted = 2;
        this.seconds = 0;
        this.matrix.newMatrix(currentLevel, dimx, dimy, minesNumber);
        this.minesLeft = minesNumber;
        for (Map.Entry<String,Player> entry : this.players.entrySet()) entry.getValue().reset();
        this.gui.changeMinesLabel(minesNumber);
        this.gui.newGame();
        this.setParameters();
    }

    public void changeDifficulty(Level level) {
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
    public void startGame(int x, int y) {
        // Case offline
        if(!this.online) {
            this.gameStarted = 1;
            // Place mines in Matrix and then in each Case
            this.matrix.fillRandomly(x, y);
            for (int i = 0; i < this.matrix.getDimX(); i ++)
                for (int j = 0; j < this.matrix.getDimY(); j ++)
                    if(this.matrix.isMine(i,j)) this.gui.setMine(i,j);
            // Compute cases left
            this.casesLeft = this.matrix.getDimX() * this.matrix.getDimY() - this.matrix.getMines() - 1;
            // Propagate the first case
            for (int i = Math.max(0,x-1); i < Math.min(this.matrix.getDimX(), x+2);i ++)
                for (int j = Math.max(0,y-1); j < Math.min(this.matrix.getDimY(), y+2);j ++)
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
    public void endGame() {
        this.gameStarted = 0;
        this.authorizedClick = false;
        // Reveal all the cases
        for(int i = 0; i < this.matrix.getDimX(); i ++)
            for(int j = 0; j < this.matrix.getDimY(); j ++)
                this.gui.reveal(i,j,casesLeft == 0);
        // Case online -> display classement
        this.gui.displayClassement();
    }

    public int computeMinesNumber(int x, int y) {return this.matrix.computeMinesNumber(x, y);}

    private void showCase(int x, int y, int n) {this.gui.showCase(x,y,n);}

    public void changePseudo() {
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

    public boolean getAuthorizedClick() {return this.authorizedClick;}

    public void addPlayer(String txt) {
        this.players.put(txt, new Player(txt));
        this.gui.addPlayer(txt);
    }
    public boolean containsPlayer(String txt) {return this.players.containsKey(txt);}
    public void removePlayer(String txt) {
        this.players.remove(txt);
        if(this.online) this.gui.removePlayer(txt);
    }
    public Player getPlayer(String txt) {return this.players.get(txt);}
    public ArrayList<Player> getAllPlayers() {return new ArrayList<>(this.players.values());}

    public int getDimX() {return this.matrix.getDimX();}
    public int getDimY() {return this.matrix.getDimY();}

    public int getGameState() {return this.gameStarted;}

}
