import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.io.InputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * MenuScreen class represents the main menu for the Brick Breaker game.
 * This class sets up the menu screen with background, title, and buttons.
 */
public class MenuScreen extends JPanel {
    private Main mainFrame;
    private DifficultyManager difficultyManager;
    private SoundManager soundManager;

    private Map<String, Font> fontCache = new HashMap<>();

    private Color backgroundColor;
    private Color buttonColor;
    private Color buttonHoverColor;
    private Color textColor;

    private Font titleFont;
    private Font buttonFont;
    private Font difficultyFont;

    // Cache for background images
    private static Map<String, Image> backgroundImageCache = new HashMap<>();

    /**
     * Constructor for MenuScreen.
     *
     * @param mainFrame         The main game frame
     * @param difficultyManager The difficulty manager
     * @param soundManager      The sound manager
     */
    public MenuScreen(Main mainFrame, DifficultyManager difficultyManager, SoundManager soundManager) {
        this.mainFrame = mainFrame;
        this.difficultyManager = difficultyManager;
        this.soundManager = soundManager;

        initializeStyleElements();
        createMainMenu();
        setPreferredSize(new Dimension(1280, 720));
    }

    /**
     * Loads the necessary fonts for the game. Fonts are loaded only once and
     * cached.
     */
    private void loadFonts() {
        try {
            // Load the "Press Start 2P" font from the resources only if it's not in the
            // cache
            Font pressStart2P = fontCache.computeIfAbsent("/assets/font/PressStart2P-Regular.ttf",
                    this::loadFontFromFile);

            titleFont = pressStart2P.deriveFont(Font.BOLD, 50);
            buttonFont = pressStart2P.deriveFont(Font.BOLD, 18);
            difficultyFont = pressStart2P.deriveFont(Font.PLAIN, 26);
        } catch (Exception e) {
            e.printStackTrace();
            // Fallback to default fonts if loading fails
            titleFont = new Font("Arial", Font.BOLD, 50);
            buttonFont = new Font("Arial", Font.BOLD, 18);
            difficultyFont = new Font("Arial", Font.PLAIN, 26);
        }
    }

    /**
     * Loads a font from the given file path. This method is used to cache fonts.
     *
     * @param path The file path to the font resource.
     * @return The loaded Font object.
     * @throws IOException         If the file is not found or if there is an I/O
     *                             error.
     * @throws FontFormatException If the file does not contain a valid font format.
     */
    private Font loadFontFromFile(String path) {
        try {
            InputStream is = getClass().getResourceAsStream(path);
            Font font = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(24f);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(font);
            return font;
        } catch (IOException | FontFormatException e) {
            e.printStackTrace();
            // Handle exceptions properly or rethrow them
            return null; // Return null or a default font in case of an error
        }
    }

    /**
     * Initializes style elements like fonts and colors.
     */
    private void initializeStyleElements() {
        // Color scheme
        backgroundColor = new Color(20, 20, 50);
        buttonColor = new Color(51, 50, 50, 200); // Transparent black for normal state
        buttonHoverColor = new Color(51, 50, 50, 128); // More opaque black for hover state
        textColor = new Color(255, 255, 255);

        // Load fonts
        loadFonts();
    }

    /**
     * Creates the main menu panel with all its components.
     */
    private void createMainMenu() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // Title
        JLabel titleLabel = new JLabel("break blocks");
        titleLabel.setFont(titleFont);
        titleLabel.setForeground(textColor);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(40, 0, 40, 0);
        add(titleLabel, gbc);

        // Create buttons
        String[] buttonLabels = {
                "start game",
                "difficulty",
                "settings",
                "help",
                "exit"
        };

        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.ipadx = 40;
        gbc.ipady = 10;
        for (int i = 0; i < buttonLabels.length; i++) {
            final String label = buttonLabels[i]; // Create final copy of button label
            RoundedButton button = new RoundedButton(label, 15, soundManager); // Use the final label copy & pass
            // soundManager
            button.setFont(buttonFont);
            button.setForeground(textColor);
            button.setNormalColor(buttonColor);
            button.setHoverColor(buttonHoverColor);
            button.setPreferredSize(new Dimension(250, 50));
            // Use the final label copy in the ActionListener lambda
            button.addActionListener(e -> handleButtonClick(label));

            gbc.gridy = i + 1;
            add(button, gbc);
        }
    }

    /**
     * Handles button click events from the main menu.
     *
     * @param buttonLabel The label of the clicked button
     */
    private void handleButtonClick(String buttonLabel) {
        switch (buttonLabel) {
            case "start game":
                mainFrame.startGame();
                break;
            case "difficulty":
                showDifficultyMenu();
                break;
            case "settings":
                showSettings();
                break;
            case "help":
                showHelpMenu();
                break;
            case "exit":
                System.exit(0);
                break;
        }
    }

    /**
     * Displays the help menu.
     */
    private void showHelpMenu() {
        HelpScreen helpScreen = new HelpScreen(this, mainFrame, soundManager, buttonColor, buttonHoverColor,
                difficultyFont,
                textColor);
        mainFrame.setContentPane(helpScreen);
        mainFrame.revalidate();
    }

    /**
     * Displays the difficulty selection menu.
     */
    public void showDifficultyMenu() {
        JPanel difficultyPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawImage(g, "/assets/backgrounds/background1.jpeg");
            }
        };

        GridBagConstraints gbc = new GridBagConstraints();

        // Title
        JLabel titleLabel = new JLabel("select difficulty");
        titleLabel.setFont(titleFont);
        titleLabel.setForeground(textColor);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 50, 0);
        difficultyPanel.add(titleLabel, gbc);

        // Difficulty buttons
        String[] difficultyLevels = { "easy", "medium", "hard" };
        gbc.gridx = 0;
        gbc.insets = new Insets(10, 0, 10, 0);
        for (int i = 0; i < difficultyLevels.length; i++) {
            final String level = difficultyLevels[i];
            RoundedButton diffButton = new RoundedButton(level, 15, soundManager);
            diffButton.setFont(difficultyFont);
            diffButton.setForeground(textColor);
            diffButton.setNormalColor(buttonColor);
            diffButton.setHoverColor(buttonHoverColor);
            diffButton.setPreferredSize(new Dimension(220, 45));

            diffButton.addActionListener(e -> {
                DifficultyManager.Difficulty selectedDifficulty = DifficultyManager.Difficulty
                        .valueOf(level.toUpperCase());
                difficultyManager.setDifficulty(selectedDifficulty);
                mainFrame.setContentPane(this);
                mainFrame.revalidate();
            });

            gbc.gridy = i + 1;
            difficultyPanel.add(diffButton, gbc);
        }

        // Back button
        RoundedButton backButton = new RoundedButton("back", 15, soundManager);
        backButton.setFont(buttonFont);
        backButton.setForeground(textColor);
        backButton.setNormalColor(buttonColor);
        backButton.setHoverColor(buttonHoverColor);
        backButton.setPreferredSize(new Dimension(220, 45));
        backButton.addActionListener(e -> {
            mainFrame.setContentPane(this);
            mainFrame.revalidate();
        });

        gbc.gridy = difficultyLevels.length + 1;
        gbc.insets = new Insets(50, 0, 0, 0);
        difficultyPanel.add(backButton, gbc);

        mainFrame.setContentPane(difficultyPanel);
        mainFrame.revalidate();
    }

    /**
     * Displays the settings menu.
     */
    public void showSettings() {
        JPanel settingsPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Draw the background image
                drawImage(g, "/assets/backgrounds/background1.jpeg");
            }
        };
        settingsPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 50, 10);

        JLabel titleLabel = new JLabel("settings");
        titleLabel.setFont(titleFont);
        titleLabel.setForeground(textColor);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        settingsPanel.add(titleLabel, gbc);

        // Volume control
        JLabel volumeLabel = new JLabel("volume:");
        volumeLabel.setFont(buttonFont);
        volumeLabel.setForeground(textColor);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        settingsPanel.add(volumeLabel, gbc);

        JSlider volumeSlider = new JSlider(0, 100, (int) (soundManager.getVolume() * 100));
        volumeSlider.setOpaque(false);
        volumeSlider.setForeground(textColor);
        volumeSlider.addChangeListener(e -> {
            soundManager.setVolume(volumeSlider.getValue() / 100f);
        });
        gbc.gridx = 1;
        gbc.gridy = 1;
        settingsPanel.add(volumeSlider, gbc);

        RoundedButton backButton = new RoundedButton("back", 15, soundManager);
        backButton.setFont(buttonFont);
        backButton.setForeground(textColor);
        backButton.setNormalColor(buttonColor);
        backButton.setHoverColor(buttonHoverColor);
        backButton.setPreferredSize(new Dimension(250, 50));
        backButton.addActionListener(e -> {
            mainFrame.setContentPane(this);
            mainFrame.revalidate();
        });

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        settingsPanel.add(backButton, gbc);

        mainFrame.setContentPane(settingsPanel);
        mainFrame.revalidate();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Draw the background image
        drawImage(g, "/assets/backgrounds/background1.jpeg");
    }

    /**
     * Draws an image on the specified Graphics context.
     *
     * @param g        The Graphics context on which to draw the image.
     * @param imageKey The key of the image in the cache to draw.
     */
    private void drawImage(Graphics g, String imageKey) {
        // Check if the image is already in the cache
        Image backgroundImage = backgroundImageCache.computeIfAbsent(imageKey, key -> {
            try {
                return new ImageIcon(getClass().getResource(key)).getImage();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        });

        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            // Fallback in case image loading fails
            g.setColor(backgroundColor);
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }
}

/**
 * RoundedButton is a JButton with a rounded rectangle shape.
 */
class RoundedButton extends JButton {
    private Color normalColor;
    private Color hoverColor;
    private int arc;
    @SuppressWarnings("unused")
    private SoundManager soundManager;
    private boolean reset = true;

    /**
     * Constructor for RoundedButton.
     *
     * @param text The text to display on the button.
     * @param arc  The arc width and height for the rounded corners.
     */
    public RoundedButton(String text, int arc, SoundManager soundManager) {
        super(text);
        this.arc = arc;
        this.soundManager = soundManager;
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setBackground(hoverColor);
                if (reset) {
                    soundManager.playHoverSound();
                    reset = false;
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setBackground(normalColor);
                reset = true;
            }
        });
    }

    /**
     * Sets the normal background color of the button.
     *
     * @param color The color to set.
     */
    public void setNormalColor(Color color) {
        this.normalColor = color;
        setBackground(normalColor);
    }

    /**
     * Sets the hover background color of the button.
     *
     * @param color The color to set.
     */
    public void setHoverColor(Color color) {
        this.hoverColor = color;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (getModel().isArmed() || getModel().isRollover()) {
            g2.setColor(hoverColor);
        } else {
            g2.setColor(getBackground());
        }

        g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), arc, arc));
        super.paintComponent(g);
    }
}

class HelpScreen extends JPanel {
    private MenuScreen menuScreen;
    private Main mainFrame;
    private SoundManager soundManager;

    private Color backgroundColor;
    private Color buttonColor;
    private Color buttonHoverColor;
    private Font titleFont;
    private Font textFont;
    private Color textColor;

    public HelpScreen(MenuScreen menuScreen, Main mainFrame, SoundManager soundManager, Color buttonColor,
            Color buttonHoverColor, Font textFont, Color textColor) {
        this.menuScreen = menuScreen;
        this.mainFrame = mainFrame;
        this.soundManager = soundManager;
        this.backgroundColor = new Color(51, 50, 50, 200);
        this.buttonColor = buttonColor;
        this.buttonHoverColor = buttonHoverColor;
        this.titleFont = textFont.deriveFont(Font.BOLD, 30);
        this.textFont = textFont;
        this.textColor = textColor;

        setLayout(new GridBagLayout());
        createHelpMenu();
    }

    private void createHelpMenu() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        // Title
        JLabel titleLabel = new JLabel("help", SwingConstants.CENTER);
        titleLabel.setFont(titleFont);
        titleLabel.setForeground(textColor);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1; // Set to 1 for single component in row
        gbc.weightx = 1.0;
        gbc.insets = new Insets(20, 20, 20, 20); // More space for the title
        add(titleLabel, gbc);

        // Create a panel to hold both controls and power-ups
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS)); // Arrange elements vertically
        contentPanel.setOpaque(false); // Make it transparent

        // --- Controls ---
        gbc.insets = new Insets(10, 10, 10, 10);
        String controlsText = "<html><body style='text-align: start; width: 15px;'>" +
                "<b>controls:</b><br><br>" +
                "<font size='4'>\u2190 \u2192 / A D : move</font><br><br>" +
                "<font size='4'>m : mainmenu</font><br><br>" +
                "<font size='4'>esc : pause game</font>" +
                "</body></html>";

        JLabel controlsLabel = new JLabel(controlsText, SwingConstants.CENTER);
        controlsLabel.setFont(textFont);
        controlsLabel.setForeground(textColor);
        controlsLabel.setOpaque(true); // Make background opaque
        controlsLabel.setBackground(buttonColor);
        controlsLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Center align for BoxLayout
        controlsLabel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); // Add padding
        contentPanel.add(controlsLabel);

        // --- Power-ups ---
        String powerupsText = "<html><body style='text-align: start; padding: 15px;'>" + // Set width
                "<b>powerups:</b><br><br>" +
                "<font size='6' color='blue'>●</font><font size='4'> : extend paddle </font><br>" +
                "<font size='6' color='green'>●</font><font size='4'> : slow ball </font><br>" +
                "<font size='6' color='#FF4500'>●</font><font size='4'> : fast ball </font><br>" +
                "<font size='6' color='#00E5EE'>●</font><font size='4'> : shrink ball </font><br>" +
                "<font size='6' color='red'>●</font><font size='4'> : extra life </font><br>" +
                "<font size='6' color='orange'>●</font><font size='4'> : multi balls </font><br>" +
                "<font size='6' color='magenta'>●</font><font size='4'> : score boost </font><br>" +
                "<font size='6' color='yellow'>●</font><font size='4'> : invincibility </font><br>" +
                "</body></html>";

        JLabel powerupsLabel = new JLabel(powerupsText, SwingConstants.CENTER);
        powerupsLabel.setFont(textFont);
        powerupsLabel.setForeground(textColor);
        powerupsLabel.setOpaque(true);
        powerupsLabel.setBackground(buttonColor);
        powerupsLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Center align for BoxLayout
        powerupsLabel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); // Add padding
        contentPanel.add(powerupsLabel);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 1.0; // Allow for horizontal expansion
        gbc.insets = new Insets(10, 20, 10, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(contentPanel, gbc);

        // Back button
        RoundedButton backButton = new RoundedButton("back", 15, soundManager);
        styleButton(backButton);
        backButton.addActionListener(e -> {
            mainFrame.setContentPane(menuScreen);
            mainFrame.revalidate();
        });
        gbc.gridx = 0;
        gbc.gridy = 2; // Increment gridy for next component
        gbc.gridwidth = 1;
        gbc.weightx = 0; // Reset weight
        gbc.weighty = 0; // Reset weighty
        gbc.insets = new Insets(10, 20, 20, 20); // Increased top inset for more space
        gbc.fill = GridBagConstraints.NONE; // Do not fill
        gbc.anchor = GridBagConstraints.CENTER;
        add(backButton, gbc);
    }

    private void styleButton(RoundedButton button) {
        button.setFont(textFont);
        button.setForeground(textColor);
        button.setNormalColor(buttonColor);
        button.setHoverColor(buttonHoverColor);
        button.setPreferredSize(new Dimension(200, 40));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        try {
            Image backgroundImage = new ImageIcon(getClass().getResource("/assets/backgrounds/background1.jpeg"))
                    .getImage();
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        } catch (Exception e) {
            g.setColor(backgroundColor);
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }
}