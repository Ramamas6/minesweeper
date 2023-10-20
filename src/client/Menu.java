package src.client;

import src.common.Level;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;


/**
 * Menu object
 */
public class Menu extends JMenuBar implements ActionListener {

    private Main main;
    private JLabel[] separators = new JLabel[4];

    // Online button
    private JButton onlineMenu;
    private JButton onLine;
    private JButton offLine;

    // Difficulty
    private JMenu difficultyMenu;
    private List<JMenuItem> difficultiesMenuItem;
    private Level[] lvls = { Level.EASY, Level.MEDIUM, Level.HARD, Level.DIABOLICAL }; // Possible levels

    // Settings
    private JMenu settingsMenu;
    private JMenuItem connectionMenu;
    private List<JMenuItem> themesMenuItem;
    private Theme[] themes = { Theme.DEFAULT, Theme.GOOGLE, Theme.GRAY, Theme.LIGHT, Theme.DARK }; // Possible themes

    // New game
    private JButton newMenu;

    // Player
    private JButton iconUser;
    private JButton menuPseudo;



    /**
     * Constructor: creates the menu
     * @param main menu-related Main object
     */
    public Menu(Main main) {
        this.main = main;
        // Online menu
        Color color = new Color(80,80,80);
        onlineMenu = new JButton("  ONLINE");
            onlineMenu.setBackground(color);
            onlineMenu.setBorderPainted(false);
            onlineMenu.setFocusable(false);
            onlineMenu.addActionListener(this);
            onlineMenu.setBorder(BorderFactory.createEmptyBorder());
            onlineMenu.setMaximumSize(new Dimension(75, 26));
        this.add(onlineMenu);
        onLine = new JButton("   ");
            onLine.setBackground(color);
            onLine.setBorderPainted(false);
            onLine.setFocusable(false);
            onLine.addActionListener(this);
            onLine.setMaximumSize(new Dimension(58, 26));
        this.add(onLine);
        offLine = new JButton("OFF");
            offLine.setBackground(color);
            offLine.setBorderPainted(false);
            offLine.setFocusable(false);
            offLine.addActionListener(this);
            offLine.setMaximumSize(new Dimension(58, 26));
        this.add(offLine);
        separators[0] = new JLabel("   | ");
        this.add(separators[0]);
        // Difficulty
        difficultyMenu = new JMenu("Difficulty Medium");
        difficultiesMenuItem = new ArrayList<JMenuItem>();
        for (int i = 0; i < lvls.length; i ++) {
            difficultiesMenuItem.add(new JMenuItem(lvls[i].getLevel()));
            difficultyMenu.add(difficultiesMenuItem.get(i));
            difficultiesMenuItem.get(i).addActionListener(this);
        }
        this.add(difficultyMenu);
        separators[1] = new JLabel(" | ");
        this.add(separators[1]);
        // Settings
        settingsMenu = new JMenu("Settings");
            // Colors
            JMenu themeMenu = new JMenu("Theme");
            themesMenuItem = new ArrayList<JMenuItem>();
            for (int i = 0; i < themes.length; i ++) {
                themesMenuItem.add(new JMenuItem(themes[i].getTheme()));
                themeMenu.add(themesMenuItem.get(i));
                themesMenuItem.get(i).addActionListener(this);
            }
            settingsMenu.add(themeMenu);
            // Connection
            connectionMenu = new JMenuItem("Connection settings");
            connectionMenu.addActionListener(this);
            settingsMenu.add(connectionMenu);
        this.add(settingsMenu);
        separators[2] = new JLabel("   | ");
        this.add(separators[2]);
        // New game
        newMenu = new JButton("New game");
        newMenu.setContentAreaFilled(false);
        newMenu.setBorderPainted(false);
        newMenu.setFocusable(false);
        newMenu.addActionListener(this);
        this.add(newMenu);
        separators[3] = new JLabel("|");
        this.add(separators[3]);
        // ICON PSEUDO
        this.add(Box.createHorizontalGlue());
        iconUser = new JButton(new ImageIcon("./assets/user.png"));
        iconUser.setBorderPainted(false);
        iconUser.setBorder(BorderFactory.createEmptyBorder());
        iconUser.setFocusable(false);
        iconUser.setContentAreaFilled(false);
        iconUser.addActionListener(this);
        this.add(iconUser);
        // PSEUDO
        menuPseudo = new JButton("Guest");
        menuPseudo.setBorderPainted(false);
        menuPseudo.setFocusable(false);
        menuPseudo.setContentAreaFilled(false);
        menuPseudo.addActionListener(this);
        this.add(menuPseudo);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // New game
        if(e.getSource() == newMenu) this.main.newGame();
        // Online mode activated/desactivated
        else if(e.getSource() == onLine || e.getSource() == offLine || e.getSource() == onlineMenu) {
            if(this.main.getOnline()) this.main.switchOffline();
            else this.main.switchOnline();
        }
        // Change connection settings
        else if(e.getSource() == connectionMenu) this.main.changeConnectionSettings();
        // Change pseudo
        else if(e.getSource() == iconUser || e.getSource() == menuPseudo) this.main.changePseudo();
        // New game with different level
        else if(difficultiesMenuItem.contains(e.getSource())) {
            Level level = lvls[difficultiesMenuItem.indexOf(e.getSource())];
            this.main.changeDifficulty(level);
        }
        // Change color
        else if(themesMenuItem.contains(e.getSource())) {
            Theme theme = themes[themesMenuItem.indexOf(e.getSource())];
            this.main.changeTheme(theme);
        }
    }

    /**
     * Change the theme of the menu
     * @param theme new theme to display
     */
    public void changeTheme(Theme theme) {
        this.setBackground(theme.getBackgroundMenu());
        this.onlineMenu.setForeground(theme.textColor());
        this.onLine.setForeground(theme.textColor());
        this.offLine.setForeground(theme.textColor());
        this.difficultyMenu.setForeground(theme.textColor());
        this.settingsMenu.setForeground(theme.textColor());
        this.newMenu.setForeground(theme.textColor());
        this.menuPseudo.setForeground(theme.textColor());
        for (int i = 0; i < separators.length; i ++) separators[i].setForeground(theme.textColor());
        // User picture
        Color color = theme.getBackgroundMenu();
        if(color.getBlue() + color.getGreen() + color.getRed() < 100)
            iconUser.setIcon(new ImageIcon("./assets/user2.png"));
        else iconUser.setIcon(new ImageIcon("./assets/user.png"));
    }

    /**
     * Change the graphics when switching online
     * @param isOnline true the game switch online, false if the game switch offline
     */
    public void switchOnline(boolean isOnline) {
        // Change switch button
        Color color = new Color(80,80,80);
        if(isOnline) {
            color = new Color(0,220,50);
            onLine.setText("ON");
            offLine.setText("   ");
        } else {
            onLine.setText("   ");
            offLine.setText("OFF");
        }
        onlineMenu.setBackground(color);
        offLine.setBackground(color);
        onLine.setBackground(color);
    }

    /**
     * Change the difficulty label
     * @param level new difficulty to display
     */
    public void changeDifficulty(String level) {difficultyMenu.setText("Difficulty " + level);}

    /**
     * Change the pseudo label
     * @param newPseudo new pseudo to display
     */
    public void changePseudo(String newPseudo) {this.menuPseudo.setText(newPseudo);}


}
