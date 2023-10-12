package src;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Server {
    // Connections
    final static int PORT = 10001;
    private Map<Integer, DataOutputStream> sorties = new HashMap<Integer, DataOutputStream>();
    private int nbrPlayers = 0;
    private int index = 0;

    // Game
    boolean run = true;
    private Matrix matrix = new Matrix();
    private Level currentLevel = Level.MEDIUM;
    private int gameState = 0;
    private int casesLeft = 0;
    private int alivePlayers = 0;

    Server() {
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

    public static void main (String [] args) {
        System.out.println("Server Started");
        new Server();
    }

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
        // Wait 3 secondes
        try {TimeUnit.SECONDS.sleep(1);} catch (InterruptedException e) {e.printStackTrace();}
        // Start game
        this.gameState = 2;
        this.matrix.display();
        broadcastAllString("command_start");
    }

    /**
     * 
     * @param player
     * @param x
     * @param y
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

    public void endGame() {
        this.gameState = 0;
        this.broadcastAllString("command_endgame");
    }

    public void removeSortie (int index) {sorties.remove(index);}

    public void setDifficulty (Level level) {
        if(this.gameState < 1) {
            currentLevel = level;
            broadcastAllString("command_difficulty_" + level.getLevel());
        }
    }
    public Level getDifficulty () {return this.currentLevel;}

    public int getState() {return this.gameState;}
    public void setState(int state) {this.gameState = state;}

    /**
     * ********* *
     * BROADCAST *
     * ********* *
     */
    public void broadcastAllInt (int value) {
            for (Map.Entry<Integer, DataOutputStream> me : sorties.entrySet())
                try {me.getValue().writeInt(value);} catch (IOException e) {}        
    }
    public void broadcastAllString (String txt) {
            for (Map.Entry<Integer, DataOutputStream> me : sorties.entrySet())
                try {me.getValue().writeUTF(txt);} catch (IOException e) {}    
    }
    public void broadcastInt (int value, int player){
        try {if(sorties.containsKey((Integer) player))
            sorties.get(player).writeInt(value);} catch (IOException e) {}
    }
    public void broadcastString (String txt, int player) {
        try {if(sorties.containsKey((Integer) player))
            sorties.get(player).writeUTF(txt);} catch (IOException e) {}
    }
}
