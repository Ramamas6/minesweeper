package src.server;

import src.common.Level;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Class thread, listening one player
 */
public class ThreadPlayer implements Runnable {

    // Static data
    private static Server server;
    private static List<String> players = new ArrayList<String>();
    private static int nbPlayers = 0; // number of players

    // Private data
    private Socket player; // Communication socket
    private DataInputStream entree; // Listening stream
    private int index; // ID of the player in the server
    private boolean run = true;
    private String pseudo = "";
    private boolean alive = true;

    /**
     * Constructor
     * @param socket socket connected the the player
     * @param index id of the player for the server
     */
    public ThreadPlayer(Socket socket, int index) {
        this.player = socket;
        this.index = index;
    }

    /**
     * Set the server for the class thread
     * @param serv server linked to all the threads
     */
    public static void SETSERV(Server serv) {server = serv;}

    /**
     * Main thread run
     */
    public void run () {
        String command = "";
        try {
            // Streams creations
            this.entree = new DataInputStream(player.getInputStream());
            // Add the player
            command = this.entree.readUTF();
            if(command.equals("command_quit")) {this.quit();}
            else if(players.contains(command)) {
                int temp = nbPlayers;
                while(players.contains("Guest" + String.valueOf(temp))) temp ++;
                server.broadcastString("Guest" + String.valueOf(temp), this.index);
                command = "Guest" + String.valueOf(temp);
            } else server.broadcastString("command_ok", this.index);
            this.pseudo = command;

            System.out.println(this.pseudo+" connected");
            players.add(this.pseudo);
            // Send connected players
            server.broadcastInt(nbPlayers, this.index);
            for(int i = 0; i < nbPlayers; i ++) {System.out.println(players.get(i));server.broadcastString(players.get(i), this.index);}
            // Add the new player
            nbPlayers ++;
            server.broadcastAllString(this.pseudo);
            server.broadcastString("command_difficulty_" + server.getDifficulty().getLevel(), this.index);

            // Connected to the game
            while(this.run) {
                command = this.entree.readUTF();
                if(command.equals("command_quit")) {this.quit();}
                // IN PREPARATION
                else if(server.getState() == 0) switch (command) {
                    case "command_difficulty_easy":
                        server.setDifficulty(Level.EASY);
                        break;
                    case "command_difficulty_medium":
                        server.setDifficulty(Level.MEDIUM);
                        break;
                    case "command_difficulty_hard":
                        server.setDifficulty(Level.HARD);
                        break;
                    case "command_difficulty_diabolical":
                        server.setDifficulty(Level.DIABOLICAL);
                        break;
                    case "command_start_game":
                        server.startGame();
                        break;
                    case "command_restart":
                        this.alive = true;
                        break;
                }
                // IN GAME
                else if(server.getState() == 2) {
                    String[] temp = command.split("_");
                    if(temp[0].equals("case") && this.alive) // Player reveal case temp[1], temp[2]
                        this.alive = server.caseRevealed(this.pseudo, Integer.parseInt(temp[1]), Integer.parseInt(temp[2]));
                    else if (temp[0].equals("message")) { // Player temp[1] send message temp[2]
                    }
                }
            }
        } catch(IOException e){
            this.quit();
        }
    }

    /**
     * Close the liaisons and close this thread properly (send the information to the server)
     */
    private void quit() {
        this.run = false;
        server.broadcastString("command_quit", this.index);
        if(!pseudo.equals("")){
            nbPlayers --;
            if(server.getState() == 0) server.broadcastAllString(this.pseudo); // Quit during preparation
            else if (server.getState() > 0) server.broadcastAllString("command_leave_" + this.pseudo); // Quit during waiting time or game
            players.remove(players.indexOf(this.pseudo));
        }
        server.removeSortie(this.index);
        try {this.player.close();this.entree.close();} catch(IOException ee){ee.printStackTrace();}
    }
}
