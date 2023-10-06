package src;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class ThreadClient implements Runnable {
    private Socket serv; // Communication socket
    private Main main;

    ThreadClient(Socket socket, Main main) {
        serv = socket;
        this.main = main;
    }

    public void run () {
        String txt;
        try {
        // Stream creations
        DataInputStream entree = new DataInputStream(serv.getInputStream());
        // Send pseudo
        JFrame frame = new JFrame();
        txt = main.getPseudo();
        if(txt.equals("")) txt = JOptionPane.showInputDialog(frame,"Enter your pseudo:");
        main.broadCastString(txt);
        while(entree.readInt() == 0) {
            txt = JOptionPane.showInputDialog(frame,"Enter your pseudo:");
            main.broadCastString(txt);
        }
        main.setPseudo(txt);
        // Get first information
        int nbrPlayers = entree.readInt(); // Number of players currently connected
        if (nbrPlayers > 0) System.out.println(nbrPlayers + " players connected :");
        else System.out.println("You are the first player");
        for(int i = 0; i < nbrPlayers; i ++) {
            txt = entree.readUTF();
            main.addPlayer(txt);
            System.out.println(txt);
        }
        // Waiting for start game
        txt = entree.readUTF();
        while(! txt.equals("command_starting")) {
            if (txt.equals(main.getPseudo())) {
                System.out.println("You joined the game as " + txt);
            }
            else if(main.containsPlayer(txt)) {
                main.removePlayer(txt);
                System.out.println(txt + " left the game...");
            } else {
                main.addPlayer(txt);
                System.out.println(txt + " joined the game !");
            }
            txt = entree.readUTF();
        }
        // Starting game
        int dimx = entree.readInt(); // Dimension x
        int dimy = entree.readInt(); // Dimension y
        System.out.println("x: " + dimx + " | y: " + dimy);
        // End
        entree.close();
        serv.close();
        } catch(IOException e){}
    }
}
