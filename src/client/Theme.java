package src.client;
import java.awt.Color;

/**
 * Different possible themes
 */
public enum Theme {

    /**
     * Default theme
     */
    DEFAULT(),
    /**
     * Google theme
     */
    GOOGLE("Google", new Color(74,117,44), new Color(255,255,255), new Color(0,0,0), // General (name, background, backgroundMenu, textColor)
        new Color(170,215,81), new Color(162,209,73), new Color(229,194,159), new Color(215,184,153), // Uncover & Discover
        new Color(25,118,210), new Color(56,142,60), new Color(211,47,47), new Color(123,31,162), // 1 - 4
        new Color(19,34,110), new Color(219,204,0), new Color(214,131,0), new Color(0,0,0)), // 5 - 8
    /**
     * Gray theme
     */
    GRAY("Gray", new Color(190,190,190), new Color(190,190,190), new Color(0,0,0), // General (name, background, backgroundMenu, textColor)
        new Color(150,150,150), new Color(190,190,190)), // Uncover & Discover
    /**
     * Dark theme
     */
    DARK("Dark", new Color(0,0,0), new Color(0,0,0), new Color(255,255,255), new Color(50,50,50), new Color(80,80,80)),
    /**
     * Light theme
     */
    LIGHT("Light", new Color(238,238,238), new Color(255,255,255), new Color(0,0,0), // General (name, background, backgroundMenu, textColor)
        new Color(220,220,220), new Color(170,170,170),new Color(238,238,238),new Color(170,170,170)); // Uncover & Discover
    
    private final String THEME; // Name of the theme
    private final Color BACKGROUND; // Background color
    private final Color BACKGROUNDMENU; // Background menu color
    private final Color TEXTCOLOR; // Text color
    private final Color UNCOVER; // Uncovered cases color (even cases)
    private final Color UNCOVER_BIS; // Uncovered cases color (odd cases)
    private final Color UNCOVER_LINE; // Uncovered lines color
    private final Color DISCOVER; // Discovered cases color (even cases)
    private final Color DISCOVER_BIS; // Discovered cases color (odd cases)
    private final Color DISCOVER_LINE; // Discovered lines color
    private final Color[] NUMBERS = new Color[8]; // Number color
    
    /**
     * General constructor
     * @param theme name of the theme
     * @param background background color
     * @param backgroundMenu background menu color
     * @param textColor text color
     * @param uncover uncovered cases color (even cases)
     * @param uncover_bis uncovered cases color (odd cases)
     * @param uncover_line uncovered lines color
     * @param discover discovered cases color (even cases)
     * @param discover_bis discovered cases color (odd cases)
     * @param discover_line discovered lines color
     * @param one numeral one color
     * @param two numeral two color
     * @param three numeral three color
     * @param four numeral four color
     * @param five numeral five color
     * @param six numeral six color
     * @param seven numeral seven color
     * @param eight numeral eight color
     */
    private Theme(String theme, Color background, Color backgroundMenu, Color textColor,
        Color uncover, Color uncover_bis, Color uncover_line, Color discover, Color discover_bis, Color discover_line,
        Color one, Color two, Color three, Color four, Color five, Color six, Color seven, Color eight) {
        this.THEME = theme;
        this.BACKGROUND = background;
        this.BACKGROUNDMENU = backgroundMenu;
        this.TEXTCOLOR = textColor;
        this.UNCOVER = uncover;
        this.UNCOVER_BIS = uncover_bis;
        this.UNCOVER_LINE = uncover_line;
        this.DISCOVER = discover;
        this.DISCOVER_BIS = discover_bis;
        this.DISCOVER_LINE = discover_line;
        this.NUMBERS[0] = one;
        this.NUMBERS[1] = two;
        this.NUMBERS[2] = three;
        this.NUMBERS[3] = four;
        this.NUMBERS[4] = five;
        this.NUMBERS[5] = six;
        this.NUMBERS[6] = seven;
        this.NUMBERS[7] = eight;
    }
    /**
     * Constructor without numbers
     * @param theme name of the theme
     * @param background background color
     * @param backgroundMenu background menu color
     * @param textColor text color
     * @param uncover uncovered cases color (even cases)
     * @param uncover_bis uncovered cases color (odd cases)
     * @param uncover_line uncovered lines color
     * @param discover discovered cases color (even cases)
     * @param discover_bis discovered cases color (odd cases)
     * @param discover_line discovered lines color
     */
    private Theme(String theme, Color background, Color backgroundMenu, Color textColor,
                    Color uncover, Color uncover_bis, Color uncover_line,
                    Color discover, Color discover_bis, Color discover_line) {
        this(theme, background, backgroundMenu, textColor, uncover, uncover_bis, uncover_line, discover, discover_bis, discover_line,
        new Color(0,0,0),new Color(0,0,0),new Color(0,0,0),new Color(0,0,0),
        new Color(0,0,0),new Color(0,0,0),new Color(0,0,0),new Color(0,0,0));
    }
    /**
     * Constructor without lines
     * @param theme name of the theme
     * @param background background color
     * @param backgroundMenu background menu color
     * @param textColor text color
     * @param uncover uncovered cases color (even cases)
     * @param uncover_bis uncovered cases color (odd cases)
     * @param discover discovered cases color (even cases)
     * @param discover_bis discovered cases color (odd cases)
     * @param one numeral one color
     * @param two numeral two color
     * @param three numeral three color
     * @param four numeral four color
     * @param five numeral five color
     * @param six numeral six color
     * @param seven numeral seven color
     * @param eight numeral eight color
     */
    private Theme(String theme, Color background, Color backgroundMenu, Color textColor,
                Color uncover, Color uncover_bis, Color discover, Color discover_bis,
                Color one, Color two, Color three, Color four, Color five, Color six, Color seven, Color eight) {
        this(theme, background, backgroundMenu, textColor, uncover, uncover_bis, null, discover, discover_bis, null,
        one,two,three,four,five,six,seven,eight);
    }
    /**
     * Simple constructor (without numbers and bis)
     * @param theme name of the theme
     * @param background background color
     * @param backgroundMenu background menu color
     * @param textColor text color
     * @param uncover uncovered cases color (even cases)
     * @param uncover_line uncovered lines color
     * @param discover discovered cases color (even cases)
     * @param discover_line discovered lines color
     */
    private Theme(String theme, Color background, Color backgroundMenu, Color textColor,
                Color uncover, Color uncover_line, Color discover, Color discover_line) {
        this(theme, background, backgroundMenu, textColor, uncover, uncover, uncover_line, discover, discover, discover_line,
        new Color(0,0,0),new Color(0,0,0),new Color(0,0,0),new Color(0,0,0),
        new Color(0,0,0),new Color(0,0,0),new Color(0,0,0),new Color(0,0,0));
    }
    /**
     * Small constructor
     * @param theme name of the theme
     * @param background background color
     * @param backgroundMenu background menu color
     * @param textColor text color
     * @param uncover uncovered cases color (even cases)
     * @param discover discovered cases color (even cases)
     */
    private Theme(String theme, Color background, Color backgroundMenu, Color textColor, Color uncover, Color discover) {
        this(theme, background, backgroundMenu, textColor, uncover, uncover, new Color(0,0,0), discover, discover, new Color(0,0,0),
        new Color(0,0,0),new Color(0,0,0),new Color(0,0,0),new Color(0,0,0),
        new Color(0,0,0),new Color(0,0,0),new Color(0,0,0),new Color(0,0,0));
    }
    /**
     * Default theme (Current: google theme)
     */
    private Theme() {
        this("Default", new Color(74,117,44), new Color(255,255,255), new Color(0,0,0),
        new Color(170,215,81), new Color(162,209,73), new Color(229,194,159), new Color(215,184,153),
        new Color(25,118,210), new Color(56,142,60), new Color(211,47,47), new Color(123,31,162),
        new Color(19,34,110), new Color(219,204,0), new Color(214,131,0), new Color(0,0,0));
    }

    /**
     * Get the name of the theme
     * @return name of the theme
     */
    public String getTheme(){return this.THEME;}
    /**
     * Get background color of the theme
     * @return background color
     */
    public Color getBackground(){return this.BACKGROUND;}
    /**
     * Get background menu color of the theme
     * @return background menu color
     */
    public Color getBackgroundMenu(){return this.BACKGROUNDMENU;}
    /**
     * Get text color of the theme
     * @return text color
     */
    public Color textColor(){return this.TEXTCOLOR;}
    /**
     * Get uncovered cases color (even cases) of the theme
     * @return uncovered cases color (even cases)
     */
    public Color getUncover(){return this.UNCOVER;}
    /**
     * Get uncovered cases color (odd cases) of the theme
     * @return uncovered cases color (odd cases)
     */
    public Color getUncoverBis(){return this.UNCOVER_BIS;}
    /**
     * Get uncovered lines color of the theme
     * @return uncovered lines color
     */
    public Color getUncoverLine(){return this.UNCOVER_LINE;}
    /**
     * Get discovered cases color (even cases) of the theme
     * @return discovered cases color (even cases)
     */
    public Color getDiscover(){return this.DISCOVER;}
    /**
     * Get discovered cases color (odd cases) of the theme
     * @return discovered cases color (odd cases)
     */
    public Color getDiscoverBis(){return this.DISCOVER_BIS;}
    /**
     * Get discovered lines color of the theme
     * @return discovered lines color
     */
    public Color getDiscoverLine(){return this.DISCOVER_LINE;}
    /**
     * Get a numeral color
     * @param n numeral value (from 1 to 8 included)
     * @return the color of this value
     */
    public Color getNumber(int n){
        if(n > 0 && n < 9) return this.NUMBERS[n-1];
        else return new Color(0,0,0);
    }
}