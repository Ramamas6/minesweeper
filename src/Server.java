package src;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Server {
    // Connections
    final static int PORT = 10000;
    private Map<Integer, DataOutputStream> sorties = new HashMap<Integer, DataOutputStream>();
    private int nbrPlayers = 0;
    private int index = 0;

    // Game
    boolean run = true;
    private Matrix matrix;
    private Level currentLevel = Level.MEDIUM;

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
        matrix = new Matrix(currentLevel);
        broadcastAllString("starting");
        broadcastAllInt(matrix.getDimX());
        broadcastAllInt(matrix.getDimY());
        broadcastAllString("start");
    }

    public void removeSortie (int index) {sorties.remove(index);}

    public void changeDifficulty (Level level) {
        currentLevel = level;
        broadcastAllString("command_difficulty_" + level.getLevel());
    }

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
