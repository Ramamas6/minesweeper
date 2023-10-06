package src;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class Case extends JPanel implements MouseListener {

    private static int DIMX;
    private static int DIMY;
    private String txt = " ";
    private GUI gui;
    private int x;
    private int y;
    private boolean isMine = false;
    private int isClicked = 0; // 0 (unclicked) , 1 (bomb), 2 (don't know), 3 (uncover)
    BufferedImage image;
    
    Case (GUI gui, int i, int j) {
        this.gui = gui;
        x = i;
        y = j;
        addMouseListener(this); // ajout listener souris
    }

    int getClicked(){return this.isClicked;}
    void setMine(){this.isMine = true;}
    public static void RESIZE(int dimx, int dimy) {DIMX = dimx; DIMY = dimy;}

    @Override
    public void paintComponent(Graphics gc) {
        // Main paint
        super.paintComponent(gc);
        setPreferredSize(new Dimension(DIMX, DIMY));
        // Background
        if (isClicked == 3) gc.setColor(new Color(215+15*((x+y)%2), 184+10*((x+y)%2), 153+6*((x+y)%2)));
        else gc.setColor(new Color(0, 190+20*((x+y)%2), 50));
        // Fill
        gc.fillRect(0,0, DIMX, DIMY);
        gc.setColor(Color.black);
        gc.drawRect(0, 0, DIMX, DIMY);
        // Draw text (case discovered but not mine)
        if (isClicked == 3 && !isMine) {
                gc.setFont(new Font("TimesRoman", Font.PLAIN, getHeight()));
                int textSize = gc.getFont().getSize();
                int textHeight = getFontMetrics(getFont()).getHeight();
                int dimX = (this.getWidth() - textSize/2)/2;
                int dimY = this.getHeight() - textHeight / 2;
                gc.drawString(txt,dimX,dimY);
        }
        // Draw image
        else if (isClicked != 0) {
            int dim = Math.min(DIMX, DIMY) * 9 / 10;
            int dimx = (DIMX - dim) / 2;
            int dimy = (DIMY - dim) / 2;
            gc.drawImage(this.image, dimx, dimy, dim, dim, this);
        }
    }

    /**
     * Reveal the case if it was not set correctly
     * @param win : 1 if the game is won, 0 otherwise
     */
    void reveal(boolean win){
        if (win){
            if(isMine){
                try {image = ImageIO.read(new File("./assets/disarmed.png"));} catch (IOException e) {e.printStackTrace();}
                isClicked = 1;
                repaint();
            }
        } else {
            if(isMine && isClicked != 1){
                try {image = ImageIO.read(new File("./assets/bombe.png"));} catch (IOException e) {e.printStackTrace();}
                isClicked = 1;
                repaint();
            } else if(!isMine && isClicked == 1) {
                try {image = ImageIO.read(new File("./assets/croix.png"));} catch (IOException e) {e.printStackTrace();}
                repaint();
            }
        }
    }

    void leftClick() {
        isClicked = 3; // Discover
        // Change display
        if (isMine) {
            try {image = ImageIO.read(new File("./assets/explosion.png"));} catch (IOException e) {e.printStackTrace();}
        }
        else {
            int n = gui.computeMinesNumber(x,y);
            if (n > 0) txt = String.valueOf(n);
            else txt = "";
        }
        // Repaint
        repaint();
        // Call clicked function of gui
        gui.isClicked(x, y);
    }

    void rightClick() {
        // Change isClicked
        if (isClicked == 0) {
            isClicked = 1;
            try {image  = ImageIO.read(new File("./assets/flag.png"));} catch (IOException e) {e.printStackTrace();}
            if(gui.changeBombs(-1) >= 0) repaint();
        }
        else if (isClicked == 1) {
            isClicked = 2;
            try {image  = ImageIO.read(new File("./assets/question.png"));} catch (IOException e) {e.printStackTrace();}
            gui.changeBombs(1);
            repaint();
        }
        else if (isClicked == 2) {
            isClicked = 0;
            repaint();
        }
        
    }

    @Override
    public void mouseClicked(MouseEvent me) {
        if (this.gui.getAuthorizedClick() && isClicked != 3) {
            if(me.getButton() == MouseEvent.BUTTON1) {leftClick();}
            if(me.getButton() == MouseEvent.BUTTON3) {rightClick();}
        }
    }
    @Override
    public void mousePressed(MouseEvent e) {}
    @Override
    public void mouseReleased(MouseEvent e) {}
    @Override
    public void mouseEntered(MouseEvent e) {}
    @Override
    public void mouseExited(MouseEvent e) {}

}
