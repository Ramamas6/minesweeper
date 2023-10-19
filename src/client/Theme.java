package src.client;

import java.awt.Color;

public enum Theme {

    DEFAULT(),

    GOOGLE("Google", new Color(74,117,44), new Color(255,255,255), new Color(0,0,0), // General (name, background, backgroundMenu, backgroundText)
        new Color(170,215,81), new Color(162,209,73), new Color(229,194,159), new Color(215,184,153), // Uncover & Discover
        new Color(25,118,210), new Color(56,142,60), new Color(211,47,47), new Color(123,31,162), // 1 - 4
        new Color(19,34,110), new Color(219,204,0), new Color(214,131,0), new Color(0,0,0)), // 5 - 8

    GRAY("Gray", new Color(190,190,190), new Color(190,190,190), new Color(0,0,0), // General (name, background, backgroundMenu, backgroundText)
        new Color(150,150,150), new Color(190,190,190)), // Uncover & Discover

    DARK("Dark", new Color(0,0,0), new Color(0,0,0), new Color(255,255,255), new Color(50,50,50), new Color(80,80,80)),

    LIGHT("Light", new Color(238,238,238), new Color(255,255,255), new Color(0,0,0), // General (name, background, backgroundMenu, backgroundText)
        new Color(220,220,220), new Color(170,170,170),new Color(238,238,238),new Color(170,170,170)); // Uncover & Discover
    
    private final String THEME;
    private final Color BACKGROUND;
    private final Color BACKGROUNDMENU;
    private final Color BACKGROUNDTEXT;
    private final Color UNCOVER;
    private final Color UNCOVER_BIS;
    private final Color UNCOVER_LINE;
    private final Color DISCOVER;
    private final Color DISCOVER_BIS;
    private final Color DISCOVER_LINE;
    private final Color[] NUMBERS = new Color[8];
    
    /**
     * General constructor
     */
    private Theme(String theme, Color background, Color backgroundMenu, Color backgroundText,
        Color uncover, Color uncover_bis, Color uncover_line, Color discover, Color discover_bis, Color discover_line,
        Color one, Color two, Color three, Color four, Color five, Color six, Color seven, Color eight) {

        this.THEME = theme;
        this.BACKGROUND = background;
        this.BACKGROUNDMENU = backgroundMenu;
        this.BACKGROUNDTEXT = backgroundText;
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
    /** Constructor without numbers
     */
    private Theme(String theme, Color background, Color backgroundMenu, Color backgroundText,
                    Color uncover, Color uncover_bis, Color uncover_line,
                    Color discover, Color discover_bis, Color discover_line) {
        this(theme, background, backgroundMenu, backgroundText, uncover, uncover_bis, uncover_line, discover, discover_bis, discover_line,
        new Color(0,0,0),new Color(0,0,0),new Color(0,0,0),new Color(0,0,0),
        new Color(0,0,0),new Color(0,0,0),new Color(0,0,0),new Color(0,0,0));
    }
    /** Constructor without lines
     */
    private Theme(String theme, Color background, Color backgroundMenu, Color backgroundText,
                Color uncover, Color uncover_bis, Color discover, Color discover_bis,
                Color one, Color two, Color three, Color four, Color five, Color six, Color seven, Color eight) {
        this(theme, background, backgroundMenu, backgroundText, uncover, uncover_bis, null, discover, discover_bis, null,
        one,two,three,four,five,six,seven,eight);
    }

    /** Simple constructor (without numbers and bis)
     */
    private Theme(String theme, Color background, Color backgroundMenu, Color backgroundText,
                Color uncover, Color uncover_line, Color discover, Color discover_line) {
        this(theme, background, backgroundMenu, backgroundText, uncover, uncover, uncover_line, discover, discover, discover_line,
        new Color(0,0,0),new Color(0,0,0),new Color(0,0,0),new Color(0,0,0),
        new Color(0,0,0),new Color(0,0,0),new Color(0,0,0),new Color(0,0,0));
    }
    /** Small constructor
     */
    private Theme(String theme, Color background, Color backgroundMenu, Color backgroundText, Color uncover, Color discover) {
        this(theme, background, backgroundMenu, backgroundText, uncover, uncover, new Color(0,0,0), discover, discover, new Color(0,0,0),
        new Color(0,0,0),new Color(0,0,0),new Color(0,0,0),new Color(0,0,0),
        new Color(0,0,0),new Color(0,0,0),new Color(0,0,0),new Color(0,0,0));
    }
    /** Default constructor (for now -> google)
     */
    private Theme() {
        this("Default", new Color(74,117,44), new Color(255,255,255), new Color(0,0,0),
        new Color(170,215,81), new Color(162,209,73), new Color(229,194,159), new Color(215,184,153),
        new Color(25,118,210), new Color(56,142,60), new Color(211,47,47), new Color(123,31,162),
        new Color(19,34,110), new Color(219,204,0), new Color(214,131,0), new Color(0,0,0));
    }

    public String getTheme(){return this.THEME;}
    public Color getBackground(){return this.BACKGROUND;}
    public Color getBackgroundMenu(){return this.BACKGROUNDMENU;}
    public Color textColor(){return this.BACKGROUNDTEXT;}
    public Color getUncover(){return this.UNCOVER;}
    public Color getUncoverBis(){return this.UNCOVER_BIS;}
    public Color getUncoverLine(){return this.UNCOVER_LINE;}
    public Color getDiscover(){return this.DISCOVER;}
    public Color getDiscoverBis(){return this.DISCOVER_BIS;}
    public Color getDiscoverLine(){return this.DISCOVER_LINE;}
    public Color getNumber(int n){
        if(n > 0 && n < 9) return this.NUMBERS[n-1];
        else return new Color(0,0,0);
    }
}