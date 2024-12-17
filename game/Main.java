import javax.swing.*;
import java.awt.*;

/**
 * Main class for the Brick Breaker game.
 * This class sets up the main game window and manages the different game
 * screens.
 */
public class Main extends JFrame {
    // Game panels
    private MenuScreen menuScreen;
    private Gameplay gamePanel;

    // Managers
    private DifficultyManager difficultyManager;
    private SoundManager soundManager;

    // Preferred window size
    private static final int PREFERRED_WIDTH = 1280;
    private static final int PREFERRED_HEIGHT = 720;

    /**
     * Constructor for the Main class.
     * Sets up the game window and initializes the game components.
     */
    public Main() {
        // Basic frame setup
        setTitle("Block Breaker @exyreams");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(PREFERRED_WIDTH, PREFERRED_HEIGHT));
        setResizable(false);

        // Initialize managers
        difficultyManager = new DifficultyManager();
        difficultyManager.setDifficulty(DifficultyManager.Difficulty.MEDIUM); // Set default difficulty
        soundManager = new SoundManager();

        // Create main menu
        menuScreen = new MenuScreen(this, difficultyManager, soundManager);

        // Set as default content
        setContentPane(menuScreen);

        // Pack and center the frame
        pack();
        setLocationRelativeTo(null);

        // Start background music
        soundManager.playBackgroundMusic();
    }

    /**
     * Starts the game by creating and setting up the Gameplay panel.
     */
    public void startGame() {
        gamePanel = new Gameplay(this, difficultyManager.getCurrentDifficulty(), soundManager);
        setContentPane(gamePanel);
        gamePanel.requestFocusInWindow();
        revalidate();
    }

    /**
     * Displays the difficulty selection menu.
     */
    public void showDifficultyMenu() {
        menuScreen.showDifficultyMenu();
        setContentPane(menuScreen);
        revalidate();
    }

    /**
     * Displays the settings menu.
     */
    public void showSettings() {
        menuScreen.showSettings();
        setContentPane(menuScreen);
        revalidate();
    }

    /**
     * Main method to launch the game.
     * 
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        // Use SwingUtilities to ensure thread safety
        SwingUtilities.invokeLater(() -> {
            try {
                // Set system look and feel
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            new Main().setVisible(true);
        });
    }
}