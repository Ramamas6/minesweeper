package src.client;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class Case extends JPanel implements MouseListener {

    private static int DIMX;
    private static int DIMY;
    public static void RESIZE(int dimx, int dimy) {DIMX = dimx; DIMY = dimy;}

    private static Theme THEME = Theme.DEFAULT;
    public static void CHANGETHEME(Theme theme) {THEME = theme;}

    private int txtInt = 0;
    private GUI gui;
    private int x;
    private int y;
    private boolean isMine = false;
    private int isClicked = 0; // 0 (unclicked) , 1 (bomb), 2 (don't know), 3 (uncover)
    BufferedImage image;
    
    public Case (GUI gui, int i, int j) {
        this.gui = gui;
        x = i;
        y = j;
        addMouseListener(this); // ajout listener souris
    }

    public int getClicked(){return this.isClicked;}
    public void setMine(){this.isMine = true;}

    @Override
    public void paintComponent(Graphics gc) {
        // Main paint
        super.paintComponent(gc);
        setPreferredSize(new Dimension(DIMX, DIMY));
        // Background
        if(isClicked == 3) gc.setColor((x+y)%2 == 0 ? THEME.getDiscover():THEME.getDiscoverBis());
        else gc.setColor((x+y)%2 == 0 ? THEME.getUncover():THEME.getUncoverBis());
        gc.fillRect(0,0, DIMX, DIMY);
        // Rectangle
        if(isClicked == 3 && THEME.getDiscoverLine() != null) {
            gc.setColor(THEME.getDiscoverLine());
            gc.drawRect(0, 0, DIMX, DIMY);
        } else if(isClicked != 3 && THEME.getUncoverLine() != null) {
            gc.setColor(THEME.getUncoverLine());
            gc.drawRect(0, 0, DIMX, DIMY);
        }
        // Draw text (case discovered but not mine)
        if (isClicked == 3 && !isMine) {
            gc.setFont(new Font("TimesRoman", Font.PLAIN, getHeight()));
            gc.setColor(THEME.getNumber(txtInt));
            String txt = (txtInt == 0 ? " ":String.valueOf(txtInt));
            int textSize = gc.getFont().getSize();
            int textHeight = (int) getStringBounds(gc, txt).getHeight();
            int dimX = (this.getWidth() - textSize/2)/2;
            int dimY = this.getHeight() / 2 + textHeight / 2 - 1;
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
    private Rectangle getStringBounds(Graphics g, String str)
    {
    Graphics2D g2 = (Graphics2D) g;
    FontRenderContext frc = g2.getFontRenderContext();
    GlyphVector gv = g2.getFont().createGlyphVector(frc, str);
    Rectangle r = gv.getPixelBounds(null, 0, 0);
    return r;
    }

    /**
     * Reveal the case if it was not set correctly
     * @param win : 1 if the game is won, 0 otherwise
     */
    public void reveal(boolean win){
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

    public void showCase(int n) {
        this.isClicked = 3;
        txtInt = n;
        repaint();
    }

    public void leftClick() {
        if(!this.gui.getOnline()) {
            if (isClicked == 1) gui.changeBombs(1);
            isClicked = 3; // Discover
            if (isMine) {
                try {image = ImageIO.read(new File("./assets/explosion.png"));} catch (IOException e) {e.printStackTrace();}
            }
            else txtInt = gui.computeMinesNumber(x,y);
            repaint();
            this.gui.isClicked(x, y);
        }
        // Case online
        else {
            this.gui.isClicked(x, y);
        }
    }

    public void rightClick() {
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
    public void mouseClicked(MouseEvent me) {}
    @Override
    public void mousePressed(MouseEvent me) {
        if (this.gui.getAuthorizedClick() && isClicked != 3) {
            if(me.getButton() == MouseEvent.BUTTON1) {leftClick();}
            if(me.getButton() == MouseEvent.BUTTON3) {rightClick();}
        }
    }
    @Override
    public void mouseReleased(MouseEvent e) {}
    @Override
    public void mouseEntered(MouseEvent e) {}
    @Override
    public void mouseExited(MouseEvent e) {}

}
