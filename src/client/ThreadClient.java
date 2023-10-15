package src.client;

import src.common.Level;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class ThreadClient implements Runnable {
    private Socket serv; // Communication socket
    private DataInputStream entree;
    private Main main;
    private boolean run = true;

    ThreadClient(Socket socket, Main main) {
        serv = socket;
        this.main = main;
    }

    public void run () {
        String txt;
        try {
            // Stream creations
            this.entree = new DataInputStream(this.serv.getInputStream());
            // Send pseudo
            txt = this.main.getPseudo();
            this.main.broadCastString(txt);
            txt = this.entree.readUTF();
            if(!txt.equals("command_ok")) this.main.setPseudo(txt);
            // Get first information
            int nbrPlayers = this.entree.readInt(); // Number of players currently connected
            //if (nbrPlayers > 0) System.out.println(nbrPlayers + " players connected :");
            //else System.out.println("You are the first player");
            for(int i = 0; i < nbrPlayers; i ++) {
                txt = this.entree.readUTF();
                main.addPlayer(txt);
                //System.out.println(txt);
            }

            // IN GAME
            while(this.run){
                txt = this.entree.readUTF();
                // Quit
                if(txt.equals("command_quit")) this.quit(); 
                // Waiting for start game
                else if(this.main.getGameState() < 2) {
                    // Case starting game
                    if(txt.equals("command_starting")) {
                        // Starting game
                        int dimx = entree.readInt(); // Dimension x
                        int dimy = entree.readInt(); // Dimension y
                        int minesNumber = entree.readInt(); // Number of mines
                        int x = entree.readInt(); // First case x
                        int y = entree.readInt(); // First case y
                        this.main.newGame(dimx, dimy, minesNumber);
                        while(! txt.equals("command_start")) {txt = entree.readUTF();}
                        this.main.startGame(x, y);
                    }
                    // Case command
                    else if(txt.startsWith("command_")) {
                        switch (txt) {
                            case "command_difficulty_easy":
                                this.main.changeDifficulty(Level.EASY);
                                break;
                            case "command_difficulty_medium":
                                this.main.changeDifficulty(Level.MEDIUM);
                                break;
                            case "command_difficulty_hard":
                                this.main.changeDifficulty(Level.HARD);
                                break;
                            case "command_difficulty_diabolical":
                                this.main.changeDifficulty(Level.DIABOLICAL);
                                break;
                        }
                    }
                    // Case player joins/leaves
                    else {
                        // Case you join
                        if (txt.equals(main.getPseudo())) {
                            main.addPlayer(txt);
                            System.out.println("You joined the game as " + txt);
                        }
                        // Case player leaves
                        else if(main.containsPlayer(txt)) {
                            main.removePlayer(txt);
                            System.out.println(txt + " left the game...");
                        }
                        // Case player joins
                        else {
                            main.addPlayer(txt);
                            System.out.println(txt + " joined the game !");
                        }
                    }                       
                }
                // In game
                else if (this.main.getGameState() == 2) {
                    String[] temp = txt.split("_");
                    // CASE CLICKED
                    if(temp[0].equals("case")) {
                        // temp[1] loses
                        if(temp[2].equals("loses"))
                            this.main.loses(temp[1]);
                        // temp[1] clicked the case temp[2], temp[3] with temp[4] value
                        else this.main.isClicked(temp[1], Integer.parseInt(temp[2]), Integer.parseInt(temp[3]), Integer.parseInt(temp[4]));
                    }
                    // END GAME
                    else if(temp[0].equals("command") && temp[1].equals("endgame")) {
                        this.main.broadCastString("command_restart");
                        this.main.endGame();
                    }
                    // MESSAGE SEND
                    else if(temp[0].equals("message")) {
                        
                    }
                }
            }
        } catch(IOException e){this.quit();}
    }

    private void quit() {
        this.run = false;
        this.main.switchOffline(false);
        try{
            this.entree.close();
            this.serv.close();
        } catch (IOException e) {e.printStackTrace();}
    }
}
