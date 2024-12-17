import java.awt.Color;

/**
 * This class defines various constants used throughout the Brick Breaker game.
 * It is not intended to be instantiated and only provides static constants.
 */
@SuppressWarnings("unused")
public final class Constants {

    // Window Size
    public static final int WINDOW_WIDTH = 1280;
    public static final int WINDOW_HEIGHT = 720;

    // Block Image Settings
    public static final String BLOCK_IMAGE_PATH_PREFIX = "/assets/blocks/block";
    public static final String BLOCK_IMAGE_PATH_SUFFIX = ".jpg";

    // Menu Image Settings
    public static final String BACKGROUND_IMAGE_PATH_PREFIX = "/assets/backgrounds/background";
    public static final String BACKGROUND_IMAGE_PATH_SUFFIX = ".jpeg";

    // Block Grid and Spacing
    public static final int NUM_COLS = 23;
    public static final int HORIZONTAL_MARGIN = 80;
    public static final int VERTICAL_MARGIN = 50;
    public static final int TOP_SPACING = 30;

    // Paddle Settings
    public static final int DEFAULT_PADDLE_WIDTH = 100;
    public static final int MAX_PADDLE_WIDTH = WINDOW_WIDTH / 4;
    public static final int PADDLE_Y_OFFSET = 10 + TOP_SPACING;
    public static final int PADDLE_X_OFFSET = 10;
    public static final int PADDLE_WIDTH_INCREMENT = 20;

    // Ball Settings
    public static final int MAX_BALL_SPEED = 15;
    public static final double BALL_SPEED_REDUCTION_FACTOR = 0.75;
    public static final double BALL_SPEED_INCREASE_FACTOR = 1.50;
    public static final double SHRINK_BALL_FACTOR = 0.5;
    public static final int MAX_BALLS = 10;

    // Timer Settings
    public static final int TIMER_DELAY = 8;
    public static final int SCORE_MULTIPLIER_DURATION = 10000;
    public static final int INVINCIBILITY_DURATION = 5000;

    // Font Settings
    public static final String FONT_PATH = "/assets/font/PressStart2P-Regular.ttf";

    // Button Text and Pause Message
    public static final String PAUSE_ICON_PATH = "/assets/icons/pause.png";
    public static final String CONTINUE_BUTTON_TEXT = "continue";
    public static final String MAIN_MENU_BUTTON_TEXT = "main menu";
    public static final String PAUSE_MESSAGE_TEXT = "game is paused. press 'esc' to resume.";

    // Text Constants
    public static final String SCORE_TEXT = "score: ";
    public static final String LIVES_TEXT = "lives: ";
    public static final String WIN_TITLE_TEXT = "congrats! you've won!";
    public static final String GAME_OVER_TITLE_TEXT = "game over!";
    public static final String RESTART_TEXT = "press 'enter' to restart";

    // PowerUp Settings
    public static final long POWER_UP_MESSAGE_DURATION = 1500;
    public static final float POWER_UP_MESSAGE_FONT_SIZE = 15;

    /**
     * Private constructor to prevent instantiation.
     */
    private Constants() {
        throw new AssertionError("Cannot instantiate Constants class");
    }
}