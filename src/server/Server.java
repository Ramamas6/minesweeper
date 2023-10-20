package src.server;

import src.common.Level;
import src.common.Matrix;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Server needed to launch an online game
 */
public class Server {
    // Connections
    final static int PORT = 10000;
    private Map<Integer, DataOutputStream> sorties = new HashMap<Integer, DataOutputStream>();
    private int nbrPlayers = 0;
    private int index = 0;

    // Game
    boolean run = true;
    private Matrix matrix = new Matrix();
    private Level currentLevel = Level.HARD;
    private int gameState = 0;
    private int casesLeft = 0;
    private int alivePlayers = 0;

    /**
     * Constructor
     */
    private Server() {
        try {
            // Server creation
            ServerSocket serv = new ServerSocket(PORT);

            // Client search
            while(run) {
                // Get client
                System.out.println("Wait for client");
                Socket socket = serv.accept();
                System.out.println("New client accepted");
                // Create client
                sorties.put(index, new DataOutputStream(socket.getOutputStream()));
                Runnable r = new ThreadPlayer(socket, index); // create thread
                new Thread(r).start(); // lancement du thread
                index ++;
                if(nbrPlayers == 0) ThreadPlayer.SETSERV(this);
                nbrPlayers ++;
            }
            // Close server
            broadcastAllString("server closed");
            serv.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * Automaticaly used to launch the server
     * @param args not used
     */
    public static void main (String [] args) {
        System.out.println("Server Started");
        new Server();
    }
    /**
     * Function called when a new online game starts
     */
    public void startGame () {
        // Set first case discovered
        this.gameState = 1;
        Random generator = new Random();
        int x = 1 + generator.nextInt(this.currentLevel.getDimX() - 2); // +1 and -2 so the case is not in the edge
        int y = 1 + generator.nextInt(this.currentLevel.getDimY() - 2); // +1 and -2 so the case is not in the edge
        this.matrix.newMatrix(currentLevel, x, y);
        this.casesLeft = this.matrix.getDimX() * this.matrix.getDimY() - this.matrix.getMines() - 1;
        this.alivePlayers = this.sorties.size();
        broadcastAllString("command_starting");
        // Send dimensions
        broadcastAllInt(this.matrix.getDimX());
        broadcastAllInt(this.matrix.getDimY());
        // Send mines number
        broadcastAllInt(this.matrix.getMines());
        // Send first case (it's always a 0)
        broadcastAllInt(x);
        broadcastAllInt(y);
        // Wait some secondes
        try {TimeUnit.SECONDS.sleep(1);} catch (InterruptedException e) {e.printStackTrace();}
        // Start game
        this.gameState = 2;
        this.matrix.display();
        broadcastAllString("command_start");
    }
    /** 
     * Function called when a case is revealed by a player
     * @param player pseudo of the player
     * @param x coordinate x of the case
     * @param y coordinate y of the case
     * @return true if it was safe, not otherwise
     */
    public boolean caseRevealed(String player, int x, int y) {
        // Case it is a mine
        if(this.matrix.isMine(x, y)) {
            this.broadcastAllString("case_" + player + "_loses");
            this.alivePlayers --;
            if(this.alivePlayers == 0) this.endGame();
            return false;
        }
        // Case it is safe
        else if (!this.matrix.isDiscovered(x, y)) {
            this.matrix.setDiscovered(x, y);
            this.broadcastAllString("case_" + player + "_" + x + "_" + y + "_" + this.matrix.computeMinesNumber(x, y));
            this.casesLeft --;
            if(this.casesLeft == 0) this.endGame();
            return true;
        }
        else return true;
    }
    /**
     * Function called at the end of a game
     */
    private void endGame() {
        this.gameState = 0;
        this.broadcastAllString("command_endgame");
    }
    /**
     * Remove a player from the server
     * @param index id of the player in the server
     */
    public void removeSortie (int index) {sorties.remove(index);}
    /**
     * Set the difficulty of the next game (only callable if the game isn't launched)
     * @param level difficulty aimed
     */
    public void setDifficulty (Level level) {
        if(this.gameState < 1) {
            currentLevel = level;
            broadcastAllString("command_difficulty_" + level.getLevel());
        }
    }
    /**
     * Send the current difficulty of the game
     * @return current difficulty
     */
    public Level getDifficulty () {return this.currentLevel;}
    /**
     * Set the current state of the game
     * @param state 0 for in preparation, 1 for starting, 2 for currently in game
     */
    public void setState(int state) {this.gameState = state;}
    /**
     * Get the current state of the game
     * @return 0 for in preparation, 1 for starting, 2 for currently in game
     */
    public int getState() {return this.gameState;}


    /**
     * ********* *
     * BROADCAST *
     * ********* *
     */
    /**
     * Broadcast an integer to all players
     * @param value integer to broadcast
     */
    public void broadcastAllInt (int value) {
            for (Map.Entry<Integer, DataOutputStream> me : sorties.entrySet())
                try {me.getValue().writeInt(value);} catch (IOException e) {}        
    }
    /**
     * Broadcast an string to all players
     * @param txt string to broadcast
     */
    public void broadcastAllString (String txt) {
            for (Map.Entry<Integer, DataOutputStream> me : sorties.entrySet())
                try {me.getValue().writeUTF(txt);} catch (IOException e) {}    
    }
    /**
     * Broadcast an integer to one players
     * @param value integer to broadcast
     * @param player (index) id of the player to broadcast
     */
    public void broadcastInt (int value, int player){
        try {if(sorties.containsKey((Integer) player))
            sorties.get(player).writeInt(value);} catch (IOException e) {}
    }
    /**
     * Broadcast an string to all players
     * @param txt string to broadcast
     * @param player (index) id of the player to broadcast
     */
    public void broadcastString (String txt, int player) {
        try {if(sorties.containsKey((Integer) player))
            sorties.get(player).writeUTF(txt);} catch (IOException e) {}
    }
}
