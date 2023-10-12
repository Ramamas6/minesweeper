package src;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ThreadPlayer implements Runnable {
    private static Server server;
    private Socket player; // Communication socket
    private int index;
    private boolean run = true;
    

    private static List<String> players = new ArrayList<String>();
    private static int nbPlayers = 0; // number of players

    private String pseudo = "";
    private boolean alive = true;
    private String command = ""; // Command

    ThreadPlayer(Socket socket, int index) {
        player = socket;
        this.index = index;
    }

    public static void SETSERV(Server serv) {server = serv;}

    public void run () {
        try {
        // Streams creations
        DataInputStream entree = new DataInputStream(player.getInputStream());
        // Add the player
        command = entree.readUTF();
        if(command.equals("command_quit")) {this.quit();}
        else if(players.contains(command)) {
            int temp = nbPlayers;
            while(players.contains("Guest" + String.valueOf(temp))) temp ++;
            server.broadcastString("Guest" + String.valueOf(temp), index);
            command = "Guest" + String.valueOf(temp);
        } else server.broadcastString("command_ok", index);
        pseudo = command;

        System.out.println(pseudo+" connected");
        players.add(pseudo);
        // Send connected players
        server.broadcastInt(nbPlayers, index);
        for(int i = 0; i < nbPlayers; i ++) server.broadcastString(players.get(i), index);
        // Add the new player
        nbPlayers ++;
        server.broadcastAllString(pseudo);
        server.broadcastString("command_difficulty_" + server.getDifficulty().getLevel(), this.index);

        // Connected to the game
        while(run) {
            command = entree.readUTF();
            // IN PREPARATION
            if(server.getState() == 0) switch (command) {
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
                case "command_quit":
                    this.quit();
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
        // End
        entree.close();
        player.close();
        } catch(IOException e){
            this.quit();
        }

    }

    public void quit() {
        this.run = false;
        server.removeSortie(index);
        if(!pseudo.equals("")) {
            nbPlayers --;
            players.remove(players.indexOf(pseudo));
            server.broadcastAllString(pseudo);
        }
        try {player.close();} catch(IOException ee){ee.printStackTrace();}
    }
}
