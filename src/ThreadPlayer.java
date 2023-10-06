package src;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ThreadPlayer implements Runnable {
    final static int PORT = 10000; // Port
    private int index;
    private Socket player; // Communication socket
    private boolean run = true;
    private static Server server;

    private static List<String> players = new ArrayList<String>();
    public static int number1 = 0; // int to print

    private String nomJoueur = "";
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
        while(!command.equals("command_quit") && players.contains(command)){
            server.broadcastInt(0, index);
            command = entree.readUTF();
        }
        if(command.equals("command_quit")) {this.quit();}
        nomJoueur = command;
        server.broadcastInt(1, index);

        System.out.println(nomJoueur+" connected");
        players.add(nomJoueur);
        // Send connected players
        server.broadcastInt(number1, index);
        for(int i = 0; i < number1; i ++) server.broadcastString(players.get(i), index);
        // Add the new player
        number1 ++;
        server.broadcastAllString(nomJoueur);


        while(run) {
            command = entree.readUTF();
            switch (command) {
                case "command_difficulty_easy":
                    server.changeDifficulty(Level.EASY);
                    break;
                case "command_difficulty_medium":
                    server.changeDifficulty(Level.MEDIUM);
                    break;
                case "command_difficulty_hard":
                    server.changeDifficulty(Level.HARD);
                    break;
                case "command_difficulty_diabolical":
                    server.changeDifficulty(Level.DIABOLICAL);
                    break;
                case "command_start_game":
                    server.startGame();
                    break;
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
        server.removeSortie(index);
        if(!nomJoueur.equals("")) {
            number1 --;
            players.remove(players.indexOf(nomJoueur));
            server.broadcastAllString(nomJoueur);
        }
        try {player.close();} catch(IOException ee){ee.printStackTrace();}
    }
}
