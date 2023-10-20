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

/**
 * Graphic case
 */
public class Case extends JPanel implements MouseListener {

    // Static variables
    private static int DIMX;
    private static int DIMY;
    private static Theme THEME = Theme.DEFAULT;

    // private variables
    private int txtInt = 0;
    private GUI gui;
    private int x;
    private int y;
    private boolean isMine = false;
    private int isClicked = 0; // 0 (unclicked) , 1 (bomb), 2 (don't know), 3 (discover)
    private Color color = new Color(0,0,0);
    private BufferedImage image;
    
    /**
     * Constructor
     * @param gui Gui linked to the cases
     * @param x coordinate x of the case in the gui
     * @param y coordinate y of the case in the gui
     */
    public Case (GUI gui, int x, int y) {
        this.gui = gui;
        this.x = x;
        this.y = y;
        addMouseListener(this); // ajout listener souris
    }

    /**
     * Resize all the cases
     * @param dimx dimension x of the cases
     * @param dimy dimension y of the cases
     */
    public static void RESIZE(int dimx, int dimy) {DIMX = dimx; DIMY = dimy;}

    /**
     * Change the theme of all the cases
     * @param theme new theme for the cases
     */
    public static void CHANGETHEME(Theme theme) {THEME = theme;}

    /**
     * Get the clicked value of the case
     * @return 0 for not clicked, 1 for flaged, 2 for questionned, 3 for discovered
     */
    public int getClicked(){return this.isClicked;}

    /**
     * Set this case as a mine
     */
    public void setMine(){this.isMine = true;}

    @Override
    /**
     * Repaint the case
     */
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
            if(gui.getOnline()) gc.setColor(this.color);
            else gc.setColor(THEME.getNumber(txtInt));
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

    /**
     * Used to know the real size of a text
     * @param g Graphics of the paintComponent
     * @param str string to mesure
     * @return a rectangle around the text (x0, y0, width, height)
     */
    private Rectangle getStringBounds(Graphics g, String str)
    {
    Graphics2D g2 = (Graphics2D) g;
    FontRenderContext frc = g2.getFontRenderContext();
    GlyphVector gv = g2.getFont().createGlyphVector(frc, str);
    Rectangle r = gv.getPixelBounds(null, 0, 0);
    return r;
    }

    /**
     * Reveal the case at the end of a game if it was not set correctly
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
    
    /**
     * Display a case
     * @param n value of the case
     */
    public void showCase(int n) {
        this.isClicked = 3;
        txtInt = n;
        repaint();
    }

    /**
     * Display a case (online mode)
     * @param n value of the case
     * @param i id of the player who discovered the case
     */
    public void showCase(int n, int i) {
        this.color = THEME.getNumber(i);
        this.showCase(n);
    }

    /**
     * Called when the player left click the case
     * In solo: reveal the case
     * Online: send the information to the server
     */
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

    /**
     * Called when the player right click the case
     * Set a flag, question, or unset
     */
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
    /**
     * Event taken into account
     */
    public void mousePressed(MouseEvent me) {
        if (this.gui.getAuthorizedClick() && isClicked != 3) {
            if(me.getButton() == MouseEvent.BUTTON1) {leftClick();}
            if(me.getButton() == MouseEvent.BUTTON3) {rightClick();}
        }
    }

    @Override
    /**
     * Not used
     */
    public void mouseClicked(MouseEvent me) {}

    @Override
    /**
     * Not used
     */
    public void mouseReleased(MouseEvent e) {}

    @Override
    /**
     * Not used
     */
    public void mouseEntered(MouseEvent e) {}

    @Override
    /**
     * Not used
     */
    public void mouseExited(MouseEvent e) {}

}
