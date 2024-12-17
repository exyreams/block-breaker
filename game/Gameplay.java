import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.io.InputStream;
import java.io.IOException;
import java.awt.geom.RoundRectangle2D;

/**
 * Gameplay class handles the main game logic and rendering.
 */
public class Gameplay extends JPanel implements KeyListener, ActionListener,
        PowerUpManager.GameplayInterface {
    private boolean play = false;
    private int score = 0;
    private int lives;
    private int totalBricks;
    private Timer timer;

    // Paddle
    private int playerX;
    private int paddleWidth;
    private int paddleYOffset; // The paddle's vertical position from the bottom of the screen.
    private int paddleXOffset; // The paddle's horizontal position from the left side of the screen

    // Ball
    private List<Ball> balls = new ArrayList<>();
    private static final int BALL_SIZE = 20;

    // Map, Difficulty, and Power-ups
    private BlockGenerator map;
    private DifficultyManager.Difficulty difficulty;
    private PowerUpManager powerUpManager;
    private List<PowerUpManager.PowerUp> activePowerUps;

    // Sound manager
    private SoundManager soundManager;

    // Effects
    private boolean isInvincible = false;
    private long invincibilityStartTime = 0;
    private int scoreMultiplier = 1;
    private String powerUpActivatedMessage = "";
    private long powerUpMessageDisplayTime = 0;

    // Background Image
    private Image backgroundImage;
    private Random random = new Random();

    // Add a field to keep track of the background index:
    private int backgroundIndex;

    // Pause Functionality
    private boolean paused = false;
    private RoundedButton pauseButton;
    private RoundedButton continueButton;
    private RoundedButton mainMenuButton;
    @SuppressWarnings("unused")
    private Font pauseButtonFont;
    private Font buttonFont;
    private Font titleFont;

    // Difficulty manager instance
    private DifficultyManager difficultyManager;

    private Main mainFrame;

    // Paddle customization
    private Color paddleColor = Color.WHITE;
    private int paddleArcWidth = 8;
    private int paddleArcHeight = 8;

    /**
     * Constructor for the Gameplay class.
     *
     * @param mainFrame    Reference to the main game frame
     * @param difficulty   Current game difficulty
     * @param soundManager The sound manager
     */
    public Gameplay(Main mainFrame, DifficultyManager.Difficulty difficulty,
            SoundManager soundManager) {
        this.mainFrame = mainFrame;
        this.difficulty = difficulty;
        this.difficultyManager = new DifficultyManager();
        this.difficultyManager.setDifficulty(difficulty);
        this.lives = difficulty.getInitialLives();
        this.totalBricks = difficulty.getBrickRows() * Constants.NUM_COLS;
        this.soundManager = soundManager;
        this.paddleWidth = difficultyManager.adjustPaddleWidth(Constants.DEFAULT_PADDLE_WIDTH);
        this.paddleYOffset = Constants.PADDLE_Y_OFFSET;
        this.paddleXOffset = Constants.PADDLE_X_OFFSET;

        loadRandomBackground();
        map = new BlockGenerator(difficulty, backgroundIndex, true);

        powerUpManager = new PowerUpManager(this);
        activePowerUps = new ArrayList<>();

        // Initialize the first ball
        balls.add(new Ball(600, 350, -1, -2, BALL_SIZE)); // Adjusted for top spacing

        addKeyListener(this);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);

        timer = new Timer(Constants.TIMER_DELAY, this);
        timer.start();

        loadFonts();
    }

    // Method to dynamically initialize PADDLE_Y after the component has been
    // rendered
    public void initializePaddleY() {
        playerX = getWidth() / 2 - paddleWidth / 2;
    }

    /**
     * Loads a random background image and selects a random index for it.
     * This method chooses a random image from a predefined set of background images
     * and sets it as the current background image for the game. The selected image
     * is loaded using its resource path.
     */
    private void loadRandomBackground() {
        backgroundIndex = random.nextInt(6) + 1;
        String imagePath = Constants.BACKGROUND_IMAGE_PATH_PREFIX + backgroundIndex
                + Constants.BACKGROUND_IMAGE_PATH_SUFFIX;
        backgroundImage = new ImageIcon(getClass().getResource(imagePath)).getImage();
    }

    /**
     * Loads and registers the custom game font.
     * This method attempts to load a TrueType font from the game's resource
     * directory.
     * If successful, it registers the font with the graphics environment, making it
     * available
     * for use in the game. If the font cannot be loaded, it defaults to using Arial
     * Bold font.
     */
    private void loadFonts() {
        try {
            InputStream is = getClass().getResourceAsStream(Constants.FONT_PATH);
            Font pressStart2P = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(24f);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(pressStart2P);
            titleFont = pressStart2P.deriveFont(Font.BOLD, 20);
            buttonFont = pressStart2P.deriveFont(Font.BOLD, 18);
            pauseButtonFont = pressStart2P.deriveFont(Font.BOLD, 20);
        } catch (IOException | FontFormatException e) {
            e.printStackTrace();
            // Fallback to default fonts if loading fails
            titleFont = new Font("Arial", Font.BOLD, 20);
            buttonFont = new Font("Arial", Font.BOLD, 18);
        }
    }

    /**
     * Initializes the pause menu components.
     * This method sets up the buttons used in the pause menu, including their
     * appearance and actions.
     * It handles loading icons, setting colors, and defining the behavior for each
     * button.
     */
    private void initializePauseComponents() {
        try {
            // Load the pause icon
            ImageIcon pauseIcon = new ImageIcon(getClass().getResource(Constants.PAUSE_ICON_PATH));
            pauseButton = new RoundedButton("", 15);
            pauseButton.setIcon(pauseIcon);
            pauseButton.setFont(buttonFont);
            pauseButton.setNormalColor(new Color(51, 50, 50, 200));
            pauseButton.setHoverColor(new Color(51, 50, 50, 128));
            pauseButton.setPreferredSize(new Dimension(50, 50));
            pauseButton.setVisible(true);
            add(pauseButton);

            continueButton = new RoundedButton(Constants.CONTINUE_BUTTON_TEXT, 15);
            continueButton.setFont(buttonFont);
            continueButton.setNormalColor(new Color(51, 50, 50, 200));
            continueButton.setHoverColor(new Color(51, 50, 50, 128));
            continueButton.setPreferredSize(new Dimension(200, 50));
            continueButton.setVisible(false);
            add(continueButton);

            mainMenuButton = new RoundedButton(Constants.MAIN_MENU_BUTTON_TEXT, 15);
            mainMenuButton.setFont(buttonFont);
            mainMenuButton.setNormalColor(new Color(51, 50, 50, 200));
            mainMenuButton.setHoverColor(new Color(51, 50, 50, 128));
            mainMenuButton.setPreferredSize(new Dimension(200, 50));
            mainMenuButton.setVisible(false);
            add(mainMenuButton);

            // Action listeners for buttons
            pauseButton.addActionListener(e -> {
                togglePause();
                repaint();
            });

            continueButton.addActionListener(e -> {
                togglePause();
                repaint();
            });

            mainMenuButton.addActionListener(e -> {
                timer.stop();
                mainFrame.setContentPane(new MenuScreen(mainFrame, difficultyManager,
                        soundManager));
                mainFrame.revalidate();
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Toggles the pause state of the game.
     * This method alternates the game's pause state between paused and unpaused.
     * It stops or starts the game timer accordingly and updates the visibility of
     * pause-related buttons.
     */
    private void togglePause() {
        paused = !paused;
        if (paused) {
            timer.stop();
            pauseButton.setVisible(false);
            continueButton.setVisible(true);
            mainMenuButton.setVisible(true);
        } else {
            timer.start();
            pauseButton.setVisible(true);
            continueButton.setVisible(false);
            mainMenuButton.setVisible(false);
        }
    }

    /**
     * Paints the game components on the panel.
     *
     * @param g Graphics object for drawing
     */
    @Override
    public void paint(Graphics g) {
        super.paint(g);

        // Initialize PADDLE_Y inside the paint method
        if (paddleYOffset == 0) {
            initializePaddleY();
        }

        // Set the background color to black (single background)
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());

        // Background image (single background)
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }

        // Drawing map
        map.draw((Graphics2D) g);

        // Ensure g is a Graphics2D object
        if (!(g instanceof Graphics2D)) {
            System.err.println("Graphics context is not an instance of Graphics2D");
            return;
        }
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        // Set the background color for the combined score and lives display
        Color backgroundColor = new Color(51, 50, 50, 200);

        // Calculate the position and dimensions for the combined stats background
        int statsX = getWidth() - 250; // Moved further to the left
        int statsY = Constants.TOP_SPACING + 15; // Reduced top gap
        int statsWidth = Math.max(
                g.getFontMetrics(titleFont).stringWidth(Constants.SCORE_TEXT + score),
                g.getFontMetrics(titleFont).stringWidth(Constants.LIVES_TEXT + lives)) + 40;
        // Add padding
        int statsHeight = 90; // Increased height to accommodate both stats

        // Draw rounded rectangle for combined stats
        g2d.setColor(backgroundColor);
        g2d.fillRoundRect(statsX, statsY, statsWidth, statsHeight, 15, 15);

        // Scores and Lives
        g.setColor(Color.white);
        g.setFont(titleFont);
        g.drawString(Constants.SCORE_TEXT + score, statsX + 20, statsY + 30);
        g.drawString(Constants.LIVES_TEXT + lives, statsX + 20, statsY + 70);

        // Custom Paddle drawing
        drawCustomPaddle(g2d);

        // Draw all balls
        for (Ball ball : balls) {
            g.setColor(Color.WHITE); // Set ball color to white
            g.fillOval(ball.getX(), ball.getY(), ball.getSize(), ball.getSize());

            // Add a black border around the ball
            g.setColor(Color.BLACK);
            g.drawOval(ball.getX(), ball.getY(), ball.getSize(), ball.getSize());
        }

        // Draw power-ups
        powerUpManager.renderPowerUps(g, activePowerUps);

        // Game state messages
        if (play && totalBricks == 0) {
            drawEndGameMessage(g, "won");
            play = false; // Ensure play is set to false when the game is won
        } else if (!play && lives <= 0) {
            drawEndGameMessage(g, "over");
        }

        // Draw invincibility effect
        if (isInvincible) {
            g.setColor(new Color(255, 255, 0, 100)); // Semi-transparent yellow
            g.fillRect(playerX, getHeight() - paddleYOffset - 8, paddleWidth, 8);
        }

        // Draw power-up activated text
        drawPowerUpText(g);

        // Initialize pause components if not already done
        if (pauseButton == null) {
            initializePauseComponents();
        }

        // Update pause button visibility and position
        pauseButton.setVisible(play && !paused);
        pauseButton.setBounds(20, Constants.TOP_SPACING + 15, 50, 50); // Moved to left side

        // Update continue and main menu buttons
        continueButton.setVisible(paused);
        mainMenuButton.setVisible(paused);
        continueButton.setBounds(getWidth() / 2 - 100, getHeight() / 2 - 60, 200,
                50);
        mainMenuButton.setBounds(getWidth() / 2 - 100, getHeight() / 2 + 20, 200,
                50);

        // Draw pause message
        if (paused) {
            drawPauseMessage(g2d);
        }

        g.dispose();
    }

    /**
     * Draws a custom paddle with rounded corners and a shadow effect.
     * The paddle is drawn with a white fill and a black border.
     *
     * @param g2d Graphics2D object for advanced drawing operations.
     */
    private void drawCustomPaddle(Graphics2D g2d) {
        int paddleHeight = 12;

        // Draw a shadow effect under the paddle
        g2d.setColor(new Color(0, 0, 0, 50));
        g2d.fillRoundRect(playerX + 3, getHeight() - paddleYOffset - (paddleHeight - 8) + 3, paddleWidth, paddleHeight,
                paddleArcWidth, paddleArcHeight);

        // Draw the main paddle with a white fill
        g2d.setColor(paddleColor);
        g2d.fillRoundRect(playerX, getHeight() - paddleYOffset - (paddleHeight - 8), paddleWidth, paddleHeight,
                paddleArcWidth, paddleArcHeight);

        // Draw a black border around the paddle
        g2d.setStroke(new BasicStroke(3));
        g2d.setColor(Color.BLACK);
        g2d.drawRoundRect(playerX - (3 / 2), getHeight() - paddleYOffset - (paddleHeight - 8) - (3 / 2),
                paddleWidth + 3, paddleHeight + 3, paddleArcWidth, paddleArcHeight);
    }

    /**
     * Draws the end game message (win or lose).
     * Displays a message based on whether the player has won or lost the game.
     * The message includes the game's title, score, and an instruction to restart.
     *
     * @param g    Graphics object used for drawing.
     * @param type String indicating whether the game is "won" or "over".
     */
    private void drawEndGameMessage(Graphics g, String type) {
        // Stop the game if not stopped till now
        if (!play)
            timer.stop();

        if (type == "won") {
            // Game Won title
            g.setColor(Color.GREEN);
            g.setFont(titleFont.deriveFont(40f));
            g.drawString(Constants.WIN_TITLE_TEXT, getWidth() / 2 - 180, getHeight() / 2
                    - 50);

            // Score
            g.setColor(Color.WHITE);
            g.setFont(titleFont.deriveFont(25f));
            g.drawString(Constants.SCORE_TEXT + score, getWidth() / 2 - 70, getHeight() /
                    2);

            // Restart instruction
            g.setFont(titleFont.deriveFont(20f));
            g.drawString(Constants.RESTART_TEXT, getWidth() / 2 - 140, getHeight() / 2 +
                    50);

        } else if (type == "over") {
            // Game Over title
            g.setColor(Color.WHITE);
            g.setFont(titleFont.deriveFont(40f));
            g.drawString(Constants.GAME_OVER_TITLE_TEXT, getWidth() / 2 - 120,
                    getHeight() / 2 - 50);

            // Score
            g.setColor(Color.WHITE);
            g.setFont(titleFont.deriveFont(25f));
            g.drawString(Constants.SCORE_TEXT + score, getWidth() / 2 - 70, getHeight() /
                    2);

            // Restart instruction
            g.setFont(titleFont.deriveFont(20f));
            g.drawString(Constants.RESTART_TEXT, getWidth() / 2 - 140, getHeight() / 2 +
                    50);
        }
    }

    /**
     * Draws the pause message when the game is paused.
     * The message is centered on the screen and displayed within a rounded
     * rectangle.
     *
     * @param g2d Graphics2D object used for drawing.
     */
    private void drawPauseMessage(Graphics2D g2d) {
        String pauseMessage = Constants.PAUSE_MESSAGE_TEXT;
        FontMetrics fm = g2d.getFontMetrics(titleFont);
        int messageWidth = fm.stringWidth(pauseMessage);
        int messageHeight = fm.getHeight();

        int messageX = (getWidth() - messageWidth) / 2;
        int messageY = (getHeight() - messageHeight) / 2;

        // Draw background for pause message
        g2d.setColor(new Color(51, 50, 50, 200));
        g2d.fillRoundRect(messageX - 20, messageY - 30, messageWidth + 30,
                messageHeight + 30, 15, 15);

        // Draw pause message
        g2d.setColor(Color.WHITE);
        g2d.setFont(titleFont);
        g2d.drawString(pauseMessage, messageX, messageY);
    }

    /**
     * Draws the message for the currently active power-up.
     * The message is displayed for a predefined duration and is centered on the
     * screen.
     *
     * @param g Graphics object used for drawing.
     */
    private void drawPowerUpText(Graphics g) {
        if (System.currentTimeMillis() - powerUpMessageDisplayTime < Constants.POWER_UP_MESSAGE_DURATION) {
            g.setColor(Color.WHITE);
            g.setFont(titleFont.deriveFont(Constants.POWER_UP_MESSAGE_FONT_SIZE)); // Set font size to 15
            FontMetrics fm = g.getFontMetrics();
            int textWidth = fm.stringWidth(powerUpActivatedMessage);
            int textX = (getWidth() - textWidth) / 2;
            int textY = getHeight() / 2 + 60; // Adjust Y position as needed
            g.drawString(powerUpActivatedMessage, textX, textY);
        }
    }

    /**
     * Handles the game logic on each timer tick.
     * This includes updating the ball's position, checking for collisions,
     * and managing power-ups. The game logic is only executed if the game is
     * currently being played and not paused.
     *
     * @param e ActionEvent object representing the timer tick event.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (play && !paused) {
            List<Ball> ballsToRemove = new ArrayList<>();
            for (Ball ball : balls) {
                // Ball-Paddle interaction
                if (new Rectangle(ball.getX(), ball.getY(), ball.getSize(), ball.getSize())
                        .intersects(new Rectangle(playerX, getHeight() - paddleYOffset, paddleWidth,
                                8))) {
                    ball.setYDir(-ball.getYDir());

                    // Change ball direction based on paddle hit position
                    int paddleCenterX = playerX + paddleWidth / 2;
                    int ballCenterX = ball.getX() + ball.getSize() / 2;
                    int hitPosition = ballCenterX - paddleCenterX;
                    ball.setXDir(getBallXDir(hitPosition));
                    soundManager.playPaddleHitSound();
                }

                // Ball-Brick interaction
                handleBallBrickInteraction(ball);

                // Ball movement and boundary checks
                handleBallMovement(ball, ballsToRemove);
            }
            balls.removeAll(ballsToRemove);

            // Respawn ball if all balls are removed and lives > 0
            if (balls.isEmpty() && lives > 0) {
                resetBall();
                lives--; // Decrement lives when the ball is reset
                if (lives <= 0) {
                    play = false;
                    soundManager.playGameOverSound();
                }
            }

            // Power-up collision check
            handlePowerUpCollision();

            // Update power-ups
            activePowerUps = powerUpManager.updatePowerUps(activePowerUps);

            // Check if invincibility has expired
            checkInvincibility();
        }
        repaint();
    }

    /**
     * Calculates the X direction of the ball based on the paddle hit position.
     * The direction is determined by the relative position of the ball's impact
     * on the paddle. A hit on the left side of the paddle results in a leftward
     * direction, while a hit on the right side results in a rightward direction.
     * If the ball hits the center of the paddle, a random direction (-1 or 1) is
     * chosen.
     *
     * @param hitPosition The relative hit position of the ball on the paddle.
     * @return The new X direction for the ball.
     */
    private int getBallXDir(int hitPosition) {
        int maxOffset = paddleWidth / 2;
        int xDir = (int) (4 * ((double) hitPosition / maxOffset));

        if (xDir == 0) {
            xDir = random.nextBoolean() ? 1 : -1; // Randomly choose -1 or 1 if hit in the center
        }

        return xDir;
    }

    /**
     * Handles the interaction between the ball and the bricks, implementing
     * continuous collision detection.
     * This method calculates the ball's path from its current to the next position
     * and determines if it intersects with any active brick. It uses line-line
     * intersection
     * to accurately calculate the collision point and adjusts the ball's trajectory
     * accordingly.
     *
     * @param ball The ball object.
     */
    private void handleBallBrickInteraction(Ball ball) {
        // Predict the ball's path from current to the next position
        Point currentPosition = new Point(ball.getX(), ball.getY());
        Point nextPosition = new Point(ball.getX() + ball.getXDir(), ball.getY() + ball.getYDir());

        // Iterate through all bricks to check for potential collision
        for (int i = 0; i < map.map.length; i++) {
            for (int j = 0; j < map.map[i].length; j++) {
                if (map.map[i][j] > 0) {
                    // Calculate brick's rectangle
                    int brickX = j * map.getBrickWidth() + Constants.HORIZONTAL_MARGIN;
                    int brickY = i * map.getBrickHeight() + Constants.VERTICAL_MARGIN + Constants.TOP_SPACING;
                    Rectangle brickRect = new Rectangle(brickX, brickY, map.getBrickWidth(), map.getBrickHeight());

                    // Check if the ball's path intersects with the brick
                    if (intersects(currentPosition, nextPosition, brickRect)) {
                        // Handle the collision
                        handleBrickCollision(i, j, ball, brickRect, currentPosition, nextPosition);
                        return; // Exit after handling one collision
                    }
                }
            }
        }
    }

    /**
     * Determines if the path of the ball intersects with a given brick.
     * This method calculates the intersection of the ball's path (a line segment
     * from
     * its current position to its next position) with each of the brick's edges.
     *
     * @param currentPosition The current position of the ball.
     * @param nextPosition    The next position of the ball.
     * @param brickRect       The rectangle representing the brick.
     * @return True if the ball's path intersects with the brick, false otherwise.
     */
    private boolean intersects(Point currentPosition, Point nextPosition, Rectangle brickRect) {
        // Check for intersection with each edge of the brick
        return lineIntersectsLine(currentPosition, nextPosition, new Point(brickRect.x, brickRect.y),
                new Point(brickRect.x + brickRect.width, brickRect.y)) || // Top edge
                lineIntersectsLine(currentPosition, nextPosition,
                        new Point(brickRect.x, brickRect.y + brickRect.height),
                        new Point(brickRect.x + brickRect.width, brickRect.y + brickRect.height))
                || // Bottom edge
                lineIntersectsLine(currentPosition, nextPosition, new Point(brickRect.x, brickRect.y),
                        new Point(brickRect.x, brickRect.y + brickRect.height))
                || // Left edge
                lineIntersectsLine(currentPosition, nextPosition, new Point(brickRect.x + brickRect.width, brickRect.y),
                        new Point(brickRect.x + brickRect.width, brickRect.y + brickRect.height)); // Right edge
    }

    /**
     * Checks if two line segments intersect.
     * This method uses a standard line-line intersection algorithm, determining if
     * the line segment defined by points A and B intersects with the line segment
     * defined by points C and D.
     *
     * @param a The start point of the first line segment.
     * @param b The end point of the first line segment.
     * @param c The start point of the second line segment.
     * @param d The end point of the second line segment.
     * @return True if the line segments intersect, false otherwise.
     */
    private boolean lineIntersectsLine(Point a, Point b, Point c, Point d) {
        // Calculate the direction vectors of the lines AB and CD
        float det = (b.x - a.x) * (d.y - c.y) - (b.y - a.y) * (d.x - c.x);

        // If the determinant is zero, the lines are parallel
        if (det == 0)
            return false;

        // Calculate the parameters for the intersection points on each line segment
        float u = ((c.x - a.x) * (d.y - c.y) - (c.y - a.y) * (d.x - c.x)) / det;
        float v = ((c.x - a.x) * (b.y - a.y) - (c.y - a.y) * (b.x - a.x)) / det;

        // Check if the intersection parameters are within the bounds of the line
        // segments
        return (u >= 0 && u <= 1 && v >= 0 && v <= 1);
    }

    /**
     * Handles the collision between a ball and a brick.
     * This method updates the brick's state, adjusts the score, and determines the
     * ball's new direction based on the intersection point with the brick.
     *
     * @param i               Row index of the brick.
     * @param j               Column index of the brick.
     * @param ball            The ball object involved in the collision.
     * @param brickRect       The rectangle representing the brick's position and
     *                        size.
     * @param currentPosition The current position of the ball.
     * @param nextPosition    The next predicted position of the ball.
     */
    private void handleBrickCollision(int i, int j, Ball ball, Rectangle brickRect, Point currentPosition,
            Point nextPosition) {
        map.setBrickValue(0, i, j);
        totalBricks--;
        score += 5 * scoreMultiplier;
        soundManager.playBrickBreakSound();

        // Determine which side of the brick the ball collided with
        Point intersectionPoint = calculateIntersectionPoint(currentPosition, nextPosition, brickRect);
        adjustBallDirection(ball, brickRect, intersectionPoint);

        // Generate a power-up at the brick's location, if applicable
        PowerUpManager.PowerUp powerUp = powerUpManager.generatePowerUp(
                j * map.getBrickWidth() + Constants.HORIZONTAL_MARGIN,
                i * map.getBrickHeight() + Constants.VERTICAL_MARGIN + Constants.TOP_SPACING);
        if (powerUp != null) {
            activePowerUps.add(powerUp);
        }
    }

    /**
     * Calculates the exact intersection point of the ball's path with the brick's
     * edge.
     * This method determines which edge of the brick (top, bottom, left, or right)
     * the ball intersects with and calculates the exact point of intersection.
     *
     * @param currentPosition The current position of the ball.
     * @param nextPosition    The next position of the ball.
     * @param brickRect       The rectangle representing the brick.
     * @return The point of intersection between the ball's path and the brick's
     *         edge.
     */
    private Point calculateIntersectionPoint(Point currentPosition, Point nextPosition, Rectangle brickRect) {
        // Check intersection with each edge and find the closest intersection point
        Point closestIntersection = null;
        double minDistance = Double.MAX_VALUE;

        // Check top edge
        Point topIntersection = lineLineIntersection(currentPosition, nextPosition, new Point(brickRect.x, brickRect.y),
                new Point(brickRect.x + brickRect.width, brickRect.y));
        if (topIntersection != null) {
            double distance = topIntersection.distance(currentPosition);
            if (distance < minDistance) {
                minDistance = distance;
                closestIntersection = topIntersection;
            }
        }

        // Check bottom edge
        Point bottomIntersection = lineLineIntersection(currentPosition, nextPosition,
                new Point(brickRect.x, brickRect.y + brickRect.height),
                new Point(brickRect.x + brickRect.width, brickRect.y + brickRect.height));
        if (bottomIntersection != null) {
            double distance = bottomIntersection.distance(currentPosition);
            if (distance < minDistance) {
                minDistance = distance;
                closestIntersection = bottomIntersection;
            }
        }

        // Check left edge
        Point leftIntersection = lineLineIntersection(currentPosition, nextPosition,
                new Point(brickRect.x, brickRect.y),
                new Point(brickRect.x, brickRect.y + brickRect.height));
        if (leftIntersection != null) {
            double distance = leftIntersection.distance(currentPosition);
            if (distance < minDistance) {
                minDistance = distance;
                closestIntersection = leftIntersection;
            }
        }

        // Check right edge
        Point rightIntersection = lineLineIntersection(currentPosition, nextPosition,
                new Point(brickRect.x + brickRect.width, brickRect.y),
                new Point(brickRect.x + brickRect.width, brickRect.y + brickRect.height));
        if (rightIntersection != null) {
            double distance = rightIntersection.distance(currentPosition);
            if (distance < minDistance) {
                minDistance = distance;
                closestIntersection = rightIntersection;
            }
        }

        return closestIntersection;
    }

    /**
     * Calculates the intersection point of two line segments if they intersect.
     * The method uses the formula for finding the intersection point of two
     * lines defined by their start and end points.
     *
     * @param line1Start The start point of the first line segment.
     * @param line1End   The end point of the first line segment.
     * @param line2Start The start point of the second line segment.
     * @param line2End   The end point of the second line segment.
     * @return The intersection point if the segments intersect, null otherwise.
     */
    private Point lineLineIntersection(Point line1Start, Point line1End, Point line2Start, Point line2End) {
        // Calculate the determinant of the lines
        double det = (line1End.x - line1Start.x) * (line2End.y - line2Start.y)
                - (line1End.y - line1Start.y) * (line2End.x - line2Start.x);
        if (det == 0) {
            return null; // Lines are parallel, no intersection
        }

        // Calculate the intersection parameters for both line segments
        double t = ((line2Start.x - line1Start.x) * (line2End.y - line2Start.y)
                - (line2Start.y - line1Start.y) * (line2End.x - line2Start.x)) / det;
        double u = -((line1Start.x - line2Start.x) * (line1End.y - line1Start.y)
                - (line1Start.y - line2Start.y) * (line1End.x - line1Start.x)) / det;

        // Check if the intersection parameters are within the bounds of the line
        // segments
        if (t >= 0 && t <= 1 && u >= 0 && u <= 1) {
            // Calculate the intersection point
            int x = (int) (line1Start.x + t * (line1End.x - line1Start.x));
            int y = (int) (line1Start.y + t * (line1End.y - line1Start.y));
            return new Point(x, y);
        }

        return null; // No intersection within the line segments
    }

    /**
     * Adjusts the ball's direction based on the point of intersection with the
     * brick.
     * Depending on whether the ball hits the brick from the top, bottom, left, or
     * right,
     * this method changes the ball's x or y direction accordingly.
     *
     * @param ball              The ball object.
     * @param brickRect         The rectangle representing the brick.
     * @param intersectionPoint The point where the ball intersects with the brick.
     */
    private void adjustBallDirection(Ball ball, Rectangle brickRect, Point intersectionPoint) {
        if (isInvincible)
            return;

        // Adjust the ball's direction based on the intersection point
        if (intersectionPoint != null) {
            // Check if the intersection point is closer to the top or bottom edges
            if (Math.abs(intersectionPoint.y - brickRect.y) < 1e-5 ||
                    Math.abs(intersectionPoint.y - (brickRect.y + brickRect.height)) < 1e-5) {
                ball.setYDir(-ball.getYDir());
            }
            // Check if the intersection point is closer to the left or right edges
            else if (Math.abs(intersectionPoint.x - brickRect.x) < 1e-5 ||
                    Math.abs(intersectionPoint.x - (brickRect.x + brickRect.width)) < 1e-5) {
                ball.setXDir(-ball.getXDir());
            }

            // Make sure ball won't get stuck to the brick by slightly adjusting position
            // according to its direction.
            if (ball.getYDir() > 0)
                ball.setY(ball.getY() + 1);
            else
                ball.setY(ball.getY() - 1);

            if (ball.getXDir() > 0)
                ball.setX(ball.getX() + 1);
            else
                ball.setX(ball.getX() - 1);
        }
    }

    /**
     * Handles the ball's movement and its collision with the game boundaries.
     * The ball reverses its direction when it hits the left, right, or top
     * boundaries.
     * If the ball goes beyond the bottom boundary, it is marked for removal.
     *
     * @param ball          The ball object.
     * @param ballsToRemove A list to keep track of balls that need to be removed.
     */
    private void handleBallMovement(Ball ball, List<Ball> ballsToRemove) {
        ball.move();

        if (ball.getX() <= 0 || ball.getX() >= getWidth() - ball.getSize()) {
            ball.setXDir(-ball.getXDir());
        }
        if (ball.getY() <= Constants.TOP_SPACING) {
            ball.setYDir(-ball.getYDir());
        }

        // Ball out of bounds
        if (ball.getY() > getHeight()) {
            ballsToRemove.add(ball);
        }
    }

    /**
     * Handles collision with power-ups.
     * This method checks if the paddle has collided with any active power-ups.
     * If a collision is detected, it applies the power-up effect and displays
     * a message.
     */
    private void handlePowerUpCollision() {
        PowerUpManager.PowerUp collidedPowerUp = powerUpManager
                .checkPowerUpCollision(new Rectangle(playerX, getHeight() - paddleYOffset,
                        paddleWidth, 8),
                        activePowerUps);
        if (collidedPowerUp != null) {
            collidedPowerUp.applyPowerUp(this);
            showPowerUpActivatedMessage(collidedPowerUp.getName());
        }
    }

    /**
     * Shows the power-up activation message for a set duration.
     *
     * @param message The message to be displayed.
     */
    public void showPowerUpActivatedMessage(String message) {
        powerUpActivatedMessage = message;
        powerUpMessageDisplayTime = System.currentTimeMillis();
    }

    /**
     * Checks and updates the invincibility status.
     * Invincibility is turned off when its duration has expired.
     */
    private void checkInvincibility() {
        if (isInvincible && System.currentTimeMillis() - invincibilityStartTime > Constants.INVINCIBILITY_DURATION) {
            isInvincible = false;
        }
    }

    /**
     * Resets the ball to the initial position and adds a new one if needed.
     * If there are no balls left and the player still has lives, a new ball is
     * created.
     */
    private void resetBall() {
        int newBallSize = BALL_SIZE;
        if (balls.isEmpty() && lives > 0) {
            balls.add(new Ball(playerX + paddleWidth / 2 - newBallSize / 2,
                    getHeight() - paddleYOffset - newBallSize, -1, -2, newBallSize));
        }
    }

    /**
     * Handles key press events for game control.
     * This method processes key presses to control the paddle movement,
     * restart the game, pause/unpause the game, and return to the main menu.
     *
     * @param e The KeyEvent object representing the key pressed.
     */
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D) {
            // Move the paddle to the right if the right arrow key or 'D' is pressed
            if (playerX >= getWidth() - paddleWidth - paddleXOffset) {
                playerX = getWidth() - paddleWidth - paddleXOffset;
            } else {
                moveRight();
            }
        } else if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A) {
            // Move the paddle to the left if the left arrow key or 'A' is pressed
            if (playerX < paddleXOffset) {
                playerX = paddleXOffset;
            } else {
                moveLeft();
            }
        } else if (e.getKeyCode() == KeyEvent.VK_ENTER && (!play || lives <= 0 || totalBricks <= 0)) {
            // Restart the game if 'Enter' is pressed when the game is over or not yet
            // started
            resetGame();
        } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            // Toggle pause state if 'Escape' is pressed
            togglePause();
            repaint();
        } else if (e.getKeyCode() == KeyEvent.VK_M) {
            // Return to the main menu if 'M' is pressed
            timer.stop(); // Stop the game timer

            // Set the main menu screen as the content pane of the main frame
            mainFrame.setContentPane(new MenuScreen(mainFrame, difficultyManager, soundManager));
            mainFrame.revalidate(); // Revalidate the main frame to update the display
        }
    }

    /**
     * Resets the game state to start a new game.
     * This method reinitializes the score, lives, total bricks, paddle position,
     * and
     * ball.
     * It also loads a new random background and creates a new set of blocks.
     */
    private void resetGame() {
        // Reset the game state
        play = true;
        score = 0;
        lives = difficulty.getInitialLives();
        totalBricks = difficulty.getBrickRows() * Constants.NUM_COLS;
        paddleWidth = difficultyManager.adjustPaddleWidth(Constants.DEFAULT_PADDLE_WIDTH);
        playerX = getWidth() / 2 - paddleWidth / 2;

        // Clear existing balls and add a new one
        balls.clear();
        balls.add(new Ball(playerX + paddleWidth / 2, getHeight() - paddleYOffset - BALL_SIZE, -1, -2, BALL_SIZE));

        // Load a new random background and create a new block map
        loadRandomBackground();
        map = new BlockGenerator(difficulty, backgroundIndex, true);

        // Reset power-up states
        isInvincible = false;
        scoreMultiplier = 1;
        activePowerUps.clear();

        // Repaint the panel to reflect changes and start the timer.
        repaint();
        timer.start();
    }

    /**
     * Moves the paddle to the right.
     * Movement is restricted within the bounds of the game panel.
     */
    public void moveRight() {
        play = true;
        playerX += 20;
    }

    /**
     * Moves the paddle to the left.
     * Movement is restricted within the bounds of the game panel.
     */
    public void moveLeft() {
        play = true;
        playerX -= 20;
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    // PowerUpManager.GameplayInterface implementations

    /**
     * Extends the paddle width within limits.
     */
    @Override
    public void extendPaddle() {
        paddleWidth = Math.min(paddleWidth + Constants.PADDLE_WIDTH_INCREMENT,
                Constants.MAX_PADDLE_WIDTH);
    }

    /**
     * Reduces the speed of all balls.
     */
    @Override
    public void slowBall() {
        for (Ball ball : balls) {
            ball.setXDir(
                    (int) Math.max(ball.getXDir() * Constants.BALL_SPEED_REDUCTION_FACTOR,
                            -Constants.MAX_BALL_SPEED));
            ball.setYDir(
                    (int) Math.max(ball.getYDir() * Constants.BALL_SPEED_REDUCTION_FACTOR,
                            -Constants.MAX_BALL_SPEED));
        }
    }

    /**
     * Increase the speed of all balls.
     */
    @Override
    public void fastBall() {
        for (Ball ball : balls) {
            ball.setXDir(
                    (int) Math.min(ball.getXDir() * Constants.BALL_SPEED_INCREASE_FACTOR,
                            Constants.MAX_BALL_SPEED));
            ball.setYDir(
                    (int) Math.min(ball.getYDir() * Constants.BALL_SPEED_INCREASE_FACTOR,
                            Constants.MAX_BALL_SPEED));
        }
    }

    /**
     * Adds an extra life to the game.
     */
    @Override
    public void addExtraLife() {
        lives = Math.min(lives + 1, 5); // Assuming a maximum of 5 lives
    }

    /**
     * Activates the multi-ball feature by adding new balls.
     */
    @Override
    public void createMultiBalls() {
        if (balls.size() < Constants.MAX_BALLS) {
            Ball originalBall = balls.get(0);
            int newBallXDir = -originalBall.getXDir();
            int newBallYDir = originalBall.getYDir();

            balls.add(new Ball(originalBall.getX() - 30, originalBall.getY(),
                    newBallXDir, newBallYDir, BALL_SIZE));
            balls.add(new Ball(originalBall.getX() + 30, originalBall.getY(),
                    newBallXDir, newBallYDir, BALL_SIZE));
        }
    }

    /**
     * Activates a score boost and sets a timer to reset it.
     */
    @Override
    public void activateScoreBoost() {
        scoreMultiplier *= 2;
        Timer scoreBoostTimer = new Timer(Constants.SCORE_MULTIPLIER_DURATION, e -> {
            scoreMultiplier = 1;
            ((Timer) e.getSource()).stop();
        });
        scoreBoostTimer.setRepeats(false);
        scoreBoostTimer.start();
    }

    /**
     * Activates invincibility for a set duration.
     */
    @Override
    public void activateInvincibility() {
        isInvincible = true;
        invincibilityStartTime = System.currentTimeMillis();
    }

    /**
     * Shrink the size of the ball.
     */
    @Override
    public void shrinkBall() {
        for (Ball ball : balls) {
            ball.setSize((int) (ball.getSize() * Constants.SHRINK_BALL_FACTOR));
        }
    }

    /**
     * Inner class to represent a ball with its position and direction.
     */
    class Ball {
        private int x, y;
        private int xDir, yDir;
        private int size;

        /**
         * Constructor for Ball.
         *
         * @param x    Initial X-coordinate
         * @param y    Initial Y-coordinate
         * @param xDir Initial X-direction
         * @param yDir Initial Y-direction
         */
        public Ball(int x, int y, int xDir, int yDir, int size) {
            this.x = x;
            this.y = y;
            this.xDir = xDir;
            this.yDir = yDir;
            this.size = size;
        }

        /**
         * Moves the ball by updating its position based on direction.
         */
        public void move() {
            x += xDir;
            y += yDir;
        }

        // Getters and setters
        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }

        public int getXDir() {
            return xDir;
        }

        public int getYDir() {
            return yDir;
        }

        public void setXDir(int xDir) {
            this.xDir = xDir;
        }

        public void setYDir(int yDir) {
            this.yDir = yDir;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }
    }

    class RoundedButton extends JButton {
        private Color normalColor;
        private Color hoverColor;
        private int arc;
        private boolean reset = true;

        /**
         * Constructor for RoundedButton.
         * Creates a new rounded button with specified text and arc.
         *
         * @param text The text to be displayed on the button.
         * @param arc  The arc radius for the rounded corners.
         */
        public RoundedButton(String text, int arc) {
            super(text);
            this.arc = arc;
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
         * @param color The color to be set as the normal background color.
         */
        public void setNormalColor(Color color) {
            this.normalColor = color;
            setBackground(normalColor);
        }

        /**
         * Sets the hover background color of the button.
         *
         * @param color The color to be set as the hover background color.
         */
        public void setHoverColor(Color color) {
            this.hoverColor = color;
        }

        /**
         * Paints the component with a rounded rectangle shape.
         * The button changes color when hovered or armed.
         *
         * @param g The Graphics object used for painting.
         */
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            if (getModel().isArmed() || getModel().isRollover()) {
                g2.setColor(hoverColor);
            } else {
                g2.setColor(getBackground());
            }

            g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), arc,
                    arc));
            super.paintComponent(g);
        }
    }
}