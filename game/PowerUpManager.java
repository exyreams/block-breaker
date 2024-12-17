import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * PowerUpManager class handles the creation, management, and application of
 * power-ups in the game.
 */
public class PowerUpManager {
    private Random random = new Random();
    private static final int POWER_UP_SIZE = 20;
    @SuppressWarnings("unused")
    private Gameplay gameplay;

    public PowerUpManager(Gameplay gameplay) {
        this.gameplay = gameplay;
    }

    /**
     * PowerUp class represents a power-up in the game.
     */
    public static abstract class PowerUp {
        protected int x, y;
        protected PowerUpType type;
        protected String name;
        protected Color color;

        /**
         * Constructor for PowerUp.
         * 
         * @param x    X-coordinate of the power-up
         * @param y    Y-coordinate of the power-up
         * @param type Type of the power-up
         */
        public PowerUp(int x, int y, PowerUpType type, String name, Color color) {
            this.x = x;
            this.y = y;
            this.type = type;
            this.name = name;
            this.color = color;
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

        public Color getColor() {
            return color;
        }

        public String getName() {
            return name;
        }

        public PowerUpType getType() {
            return type;
        }

        // Abstract method to apply the power-up effect
        public abstract void applyPowerUp(Gameplay gameplay);

        // Method to draw the power-up
        public void draw(Graphics g) {
            g.setColor(color);
            g.fillOval(x, y, POWER_UP_SIZE, POWER_UP_SIZE);
        }
    }

    /**
     * Enum representing different types of power-ups.
     */
    public enum PowerUpType {
        EXTEND_PADDLE,
        SLOW_BALL,
        FAST_BALL,
        EXTRA_LIFE,
        MULTI_BALLS,
        SCORE_BOOST,
        INVINCIBILITY,
        SHRINK_BALL
    }

    public class ExtendPaddlePowerUp extends PowerUp {
        public ExtendPaddlePowerUp(int x, int y) {
            super(x, y, PowerUpType.EXTEND_PADDLE, "Extend Paddle", Color.BLUE);
        }

        @Override
        public void applyPowerUp(Gameplay gameplay) {
            gameplay.extendPaddle();
        }
    }

    public class SlowBallPowerUp extends PowerUp {
        public SlowBallPowerUp(int x, int y) {
            super(x, y, PowerUpType.SLOW_BALL, "Slow Ball", Color.GREEN);
        }

        @Override
        public void applyPowerUp(Gameplay gameplay) {
            gameplay.slowBall();
        }
    }

    // New Power-up: Increase ball speed
    public class FastBallPowerUp extends PowerUp {
        public FastBallPowerUp(int x, int y) {
            super(x, y, PowerUpType.FAST_BALL, "Fast Ball", new Color(255, 69, 0)); // OrangeRed color
        }

        @Override
        public void applyPowerUp(Gameplay gameplay) {
            gameplay.fastBall();
        }
    }

    public class ExtraLifePowerUp extends PowerUp {
        public ExtraLifePowerUp(int x, int y) {
            super(x, y, PowerUpType.EXTRA_LIFE, "Extra Life", Color.RED);
        }

        @Override
        public void applyPowerUp(Gameplay gameplay) {
            gameplay.addExtraLife();
        }
    }

    public class MultiBallsPowerUp extends PowerUp {
        public MultiBallsPowerUp(int x, int y) {
            super(x, y, PowerUpType.MULTI_BALLS, "Multi Balls", Color.ORANGE);
        }

        @Override
        public void applyPowerUp(Gameplay gameplay) {
            gameplay.createMultiBalls();
        }
    }

    public class ScoreBoostPowerUp extends PowerUp {
        public ScoreBoostPowerUp(int x, int y) {
            super(x, y, PowerUpType.SCORE_BOOST, "Score Boost", Color.MAGENTA);
        }

        @Override
        public void applyPowerUp(Gameplay gameplay) {
            gameplay.activateScoreBoost();
        }
    }

    public class InvincibilityPowerUp extends PowerUp {
        public InvincibilityPowerUp(int x, int y) {
            super(x, y, PowerUpType.INVINCIBILITY, "Invincibility", Color.YELLOW);
        }

        @Override
        public void applyPowerUp(Gameplay gameplay) {
            gameplay.activateInvincibility();
        }
    }

    // New Power-up: Shrink ball
    public class ShrinkBallPowerUp extends PowerUp {
        public ShrinkBallPowerUp(int x, int y) {
            super(x, y, PowerUpType.SHRINK_BALL, "Shrink Ball", new Color(0, 229, 238)); // Neon Blue
        }

        @Override
        public void applyPowerUp(Gameplay gameplay) {
            gameplay.shrinkBall();
        }
    }

    /**
     * Generates a power-up at the specified coordinates.
     * 
     * @param x X-coordinate for the power-up
     * @param y Y-coordinate for the power-up
     * @return A new PowerUp object, or null if no power-up is generated
     */
    public PowerUp generatePowerUp(int x, int y) {
        if (random.nextInt(100) < 20) { // 20% chance to generate a power-up
            PowerUpType type = PowerUpType.values()[random.nextInt(PowerUpType.values().length)];
            switch (type) {
                case EXTEND_PADDLE:
                    return new ExtendPaddlePowerUp(x, y);
                case SLOW_BALL:
                    return new SlowBallPowerUp(x, y);
                case FAST_BALL:
                    return new FastBallPowerUp(x, y);
                case EXTRA_LIFE:
                    return new ExtraLifePowerUp(x, y);
                case MULTI_BALLS:
                    return new MultiBallsPowerUp(x, y);
                case SCORE_BOOST:
                    return new ScoreBoostPowerUp(x, y);
                case INVINCIBILITY:
                    return new InvincibilityPowerUp(x, y);
                case SHRINK_BALL:
                    return new ShrinkBallPowerUp(x, y);
                default:
                    return null;
            }
        }
        return null;
    }

    /**
     * Checks for collision between the paddle and power-ups.
     * 
     * @param paddleRect Rectangle representing the paddle
     * @param powerUps   List of active power-ups
     * @return The type of power-up collided with, or null if no collision
     */
    public PowerUp checkPowerUpCollision(Rectangle paddleRect, List<PowerUp> powerUps) {
        PowerUp collidedPowerUp = null;
        for (PowerUp powerUp : powerUps) {
            if (paddleRect.intersects(new Rectangle(powerUp.getX(), powerUp.getY(), POWER_UP_SIZE, POWER_UP_SIZE))) {
                collidedPowerUp = powerUp;
                break;
            }
        }

        if (collidedPowerUp != null) {
            powerUps.remove(collidedPowerUp);
            return collidedPowerUp;
        }

        return null;
    }

    /**
     * Updates the positions of active power-ups and removes out-of-bounds ones.
     * 
     * @param powerUps List of active power-ups
     * @return Updated list of active power-ups
     */
    public List<PowerUp> updatePowerUps(List<PowerUp> powerUps) {
        List<PowerUp> updatedPowerUps = new ArrayList<>();
        for (PowerUp powerUp : powerUps) {
            powerUp.setY(powerUp.getY() + 1); // Move power-up down
            if (powerUp.getY() < Constants.WINDOW_HEIGHT) { // Keep power-up if it's still on screen
                updatedPowerUps.add(powerUp);
            }
        }
        return updatedPowerUps;
    }

    /**
     * Renders the power-ups on the game screen.
     * 
     * @param g        Graphics object to draw on
     * @param powerUps List of active power-ups to render
     */
    public void renderPowerUps(Graphics g, List<PowerUp> powerUps) {
        for (PowerUp powerUp : powerUps) {
            powerUp.draw(g);
        }
    }

    /**
     * Interface for gameplay actions that can be triggered by power-ups.
     */
    public interface GameplayInterface {
        void extendPaddle();

        void slowBall();

        void fastBall();

        void addExtraLife();

        void createMultiBalls();

        void activateScoreBoost();

        void activateInvincibility();

        void shrinkBall();

        void showPowerUpActivatedMessage(String message);
    }
}