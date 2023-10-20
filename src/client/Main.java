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

/**
 * Main client class
 */
public class Main extends JFrame{

    // General
    private GUI gui;
    private Menu menu;
    private Matrix matrix;
    private String pseudo = "";

    // Game
    private int gameStarted = 0; // Wether the game is started (0 = no, 1 = yes offline, 2 = yes online)
    private boolean authorizedClick = true; // Wether click on cases is othorized
    private Level currentLevel = Level.MEDIUM;
    private int casesLeft; // Number of non-mines left
    private int seconds = 0; // Timer

    // Online mode
    int port = 10000;
    String host = "LOCALHOST";
    private Socket socket;
    private DataOutputStream sortie;
    private boolean online = false;
    private Map<String, Player> players = new HashMap<String, Player>();

    /**
     * Automaticaly used to launch the client
     * @param args you can pass the pseudo of the player in the first case
     */
    public static void main(String[] args) {if (args.length > 0) new Main(args[0]);else new Main("");}

    /**
     * Constructor
     * @param s pseudo of the player - Default: ""
     */
    private Main(String s) {
        this.timer(); // Start timer
        this.matrix = new Matrix();
        this.pseudo = s;
        if(this.pseudo == null || this.pseudo.isBlank() || !this.pseudo.matches("[a-zA-Z1-9]+")) this.pseudo = "guest";
        this.gui = new GUI(this, this.matrix.getMines()); // Create gui
        this.menu = new Menu(this); // Create menu
        setJMenuBar(this.menu);
        setParameters(true); // Set default options
    }

    /**
     * Change the theme of the client
     * @param theme new theme
     */
    public void changeTheme(Theme theme){
        this.setBackground(theme.getBackground());
        this.menu.changeTheme(theme);
        this.gui.changeTheme(theme);
    }

    /**
     * **************** *
     * SERVER FUNCTIONS *
     * **************** *
    **/

    /**
     * Called when changing connection settings
     */
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
     * Called when the button connection is pressed to connect to the server.
     * Try to connect, or launch a timer to try again every seconds if the connection failed.
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
            gui.switchOnline(true);
            menu.switchOnline(true);
            return true;
        } catch (IOException e) {return false;}
    }

    /**
     * Called when the button connection is pressed to disconnect to the server - Default: true
     * @param broadcastServer whether to broadcast to the server that the client disconected
     */
    public void switchOffline(boolean broadcastServer) {
        this.online = false;
        if(broadcastServer) this.broadCastString("command_quit");
        try{
            socket.close();
            sortie.close();
        } catch (IOException e) {}
        gui.switchOnline(false);
        menu.switchOnline(false);
    }

    /**
     * Called when the button connection is pressed to disconnect to the server.
     * Broadcast to the server that the client disconected
     */
    public void switchOffline(){this.switchOffline(true);}

    /**
     * Broadcast a string to the server
     * @param txt string to broadcast
     */
    public void broadCastString(String txt){try{sortie.writeUTF(txt);}catch(IOException e){e.printStackTrace();}}

    /**
     * Broadcast an integer to the server
     * @param value integer to broadcast
     */
    public void broadCastInt(int value){try{sortie.writeInt(value);}catch(IOException e){e.printStackTrace();}}

    /**
     * **************** *
     * JFRAME FUNCTIONS *
     * **************** *
    **/

    /**
     * Set general graphic parameters
     * @param firstStart true only at the creation - Default: false
     */
    private void setParameters(boolean firstStart) {
        if (firstStart) {
            addComponentListener(new ComponentAdapter() {public void componentResized(ComponentEvent componentEvent) {gui.redimension(getSize(), currentLevel);}});
            setContentPane(gui);
            setDefaultCloseOperation(EXIT_ON_CLOSE);
        }
        pack();
        int[] size = gui.screenSize(currentLevel);
        if (size[0] == 0) setExtendedState(JFrame.MAXIMIZED_BOTH); else setSize(size[0], size[1]);
        setVisible(true);
    }

    /**
     * Set general graphic parameters
     */
    private void setParameters() {this.setParameters(false);}

    /**
     * Quit the game, close the windows and stop the client
     */
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
        int i = 0;
        int k = 0;
        for (Map.Entry<String, Player> entry : players.entrySet()) {
            if(entry.getKey().equals(player)) i = k;
            else k ++;
        }
        this.gui.showCase(x,y,n,i); // Display the case
        this.players.get(player).addScore(n); // Actualise 1rst score
        this.players.get(player).addTieBreakScore(1); // Actualise 2nd score (for equalities)
        this.gui.changeScore(player,this.players.get(player).getScore()); // Actualise 1rst score in gui
    }

    /**
     * Called on online mode, when a player loses
     * @param player pseudo of the player
     */
    public void loses(String player) {
        this.players.get(player).setAlive(false);
        this.gui.loses(player);
    }

    /**
     * Start the timer (called only at the creation)
     */
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
     * Called when the "new game" button is pressed.
     * Solo: start a new game with the same difficulty
     * Online: broadcast to the server the beginning of a new game (if one insn't already in progress)
     */
    public void newGame() {
        // Case Offline
        if(!this.online) {
            this.seconds = 0;
            this.gameStarted = 0; // Wait for the first click
            this.authorizedClick = true; // Authorize to click
            this.matrix.newMatrix(currentLevel); // Create a new array in Matrix
            gui.newGame(this.matrix.getMines());
            this.setParameters();
        }
        // Case start game Online
        else if (this.gameStarted < 2) {
            this.broadCastString("command_start_game");
        } else {} // Do nothing : online, but game already started
    }

    /**
     * Creates (prepares) a new Online game
     * @param dimx dimension x (width) of the matrix
     * @param dimy dimension y (height) of the matrix
     * @param minesNumber number of mines
     */
    public void newGame(int dimx, int dimy, int minesNumber) {
        this.authorizedClick = false;
        this.gameStarted = 2;
        this.seconds = 0;
        this.matrix.newMatrix(currentLevel, dimx, dimy, minesNumber);
        for (Map.Entry<String,Player> entry : this.players.entrySet()) entry.getValue().reset();
        this.gui.newGame(minesNumber);
        this.setParameters();
    }

    /**
     * Change the current difficulty.
     * Solo: start imediately a new game with this difficulty
     * Online: set the difficulty for the next game (if one insn't already in progress)
     * @param level new level
     */
    public void changeDifficulty(Level level) {
        // Case Offline
        if(!this.online) {
            this.currentLevel = level;
            menu.changeDifficulty(level.getLevel());
            this.newGame();
        }
        // Case Online
        else if (this.gameStarted < 2) {
            if(this.currentLevel != level) {
                this.currentLevel = level;
                menu.changeDifficulty(this.currentLevel.getLevel());
                this.broadCastString("command_difficulty_" + level.getLevel());
            }
        } else {menu.changeDifficulty(this.currentLevel.getLevel());} // Do nothing : online, but game already started
    }

    /**
     * Starts a game with a 0 in the first clicked case
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
     * Called when the game end (to reveal all mines and display rankings in online)
     */
    public void endGame() {
        this.gameStarted = 0;
        this.authorizedClick = false;
        // Reveal all the cases
        for(int i = 0; i < this.matrix.getDimX(); i ++)
            for(int j = 0; j < this.matrix.getDimY(); j ++)
                this.gui.reveal(i,j,casesLeft == 0);
        // Case online -> display classement
        if(this.online) this.gui.displayClassement();
    }

    /**
     * Get the value of a case
     * @param x coordinate x of the case
     * @param y coordinate y of the case
     * @return the value of a case
     */
    public int computeMinesNumber(int x, int y) {return this.matrix.computeMinesNumber(x, y);}

    /**
     * Called when the used clicks on the change pseudo button
     * Does not work during an online game
     */
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
                    if(this.online) gui.changePlayer(txt, this.pseudo);  // If online: change the pseudo for the scores
                    menu.changePseudo(txt);
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

    /**
     * Get the pseudo of this client
     * @return pseudo
     */
    public String getPseudo() {return this.pseudo;}

    /**
     * Set the pseudo of this client
     * @param pseudo new pseudo to set
     */
    public void setPseudo(String pseudo) {this.pseudo = pseudo;}

    /**
     * Get if this client is currently online
     * @return true if this client is currently online, false otherwise
     */
    public boolean getOnline() {return this.online;}

    /**
     * Get whether this client is authorized to click or not
     * @return true if this client is authorized to click, false otherwise
     */
    public boolean getAuthorizedClick() {return this.authorizedClick;}

    /**
     * Adds a player (online game only)
     * @param pseudo pseudo of the player to add
     */
    public void addPlayer(String pseudo) {
        this.players.put(pseudo, new Player(pseudo));
        this.gui.addPlayer(pseudo);
    }

    /**
     * Check if a pseudo is in the list of players (online game only)
     * @param pseudo pseudo to search
     * @return true if the pseudo is in the list of players, false otherwise
     */
    public boolean containsPlayer(String pseudo) {return this.players.containsKey(pseudo);}

    /**
     * Remove a player from the list of players (online game only)
     * @param pseudo pseudo of the player to be removed
     */
    public void removePlayer(String pseudo) {
        this.players.remove(pseudo);
        if(this.online) this.gui.removePlayer(pseudo);
    }

    /**
     * Get a player from the list of players (online game only)
     * @param pseudo pseudo of the player
     * @return the player found
     */
    public Player getPlayer(String pseudo) {return this.players.get(pseudo);}

    /**
     * Get a list of all the current players (online game only)
     * @return all the players
     */
    public ArrayList<Player> getAllPlayers() {return new ArrayList<>(this.players.values());}

    /**
     * Get the current number of players (online game only)
     * @return current number of players
     */
    public int getPlayerNumber() {return this.players.size();}

    /**
     * Get the dimension x (width) of the matrix
     * @return width of the matrix
     */
    public int getDimX() {return this.matrix.getDimX();}

    /**
     * Get the dimension y (height) of the matrix
     * @return height of the matrix
     */
    public int getDimY() {return this.matrix.getDimY();}

    /**
     * Get the current state of the game
     * @return 0 for not started, 1 for started in solo or in preparation online, 2 for started online
     */
    public int getGameState() {return this.gameStarted;}

    /**
     * Get the height of the menu
     * @return height of the menu
     */
    public int getMenuHeight() {return this.menu.getHeight();}

}