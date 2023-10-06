package src;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JFrame;

/**
 * Programme nul
 * @author Clément Dhalluin
 * @version 0.0
 */
public class Main extends JFrame{
    GUI gui;

    Main() {
        gui = new GUI(this); // Create gui
        addComponentListener(new ComponentAdapter() {public void componentResized(ComponentEvent componentEvent) {gui.redimension(getSize());}});
        setParameters(); // Set default options
    }
    /**
     * The Begin of the life
     * @param args not used
     */
    public static void main(String[] args) {
        new Main();
    }

    void setParameters() {
        setContentPane(gui);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        pack();
        int[] size = gui.screenSize();
        if (size[0] == 0) setExtendedState(JFrame.MAXIMIZED_BOTH); else setSize(size[0], size[1]);
        setVisible(true);
    }
    void setParametersAgain() {
        pack();
        int[] size = gui.screenSize();
        if (size[0] == 0) setExtendedState(JFrame.MAXIMIZED_BOTH); else setSize(size[0], size[1]);
    }

    void quit() {
        System.out.println("Bye-Bye");
        System.exit(0);
    }

}
