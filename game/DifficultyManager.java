/**
 * DifficultyManager class manages game difficulty settings.
 */
public class DifficultyManager {
    /**
     * Enum representing different difficulty levels.
     */
    public enum Difficulty {
        EASY(1, 5, 4, 150), // Speed multiplier, initial lives, brick rows, paddle width
        MEDIUM(2, 3, 4, 130),
        HARD(3, 2, 4, 100);

        private final double speedMultiplier;
        private final int initialLives;
        private final int brickRows;
        private final int paddleWidth;

        /**
         * Constructor for Difficulty enum.
         * 
         * @param speedMultiplier Ball speed multiplier
         * @param initialLives    Initial number of lives
         * @param brickRows       Number of rows of bricks
         * @param paddleWidth     Initial paddle width
         */
        Difficulty(double speedMultiplier, int initialLives, int brickRows, int paddleWidth) {
            this.speedMultiplier = speedMultiplier;
            this.initialLives = initialLives;
            this.brickRows = brickRows;
            this.paddleWidth = paddleWidth;
        }

        public double getSpeedMultiplier() {
            return speedMultiplier;
        }

        public int getInitialLives() {
            return initialLives;
        }

        public int getBrickRows() {
            return brickRows;
        }

        public int getPaddleWidth() {
            return paddleWidth;
        }
    }

    // Current difficulty
    private Difficulty currentDifficulty;

    /**
     * Sets the current difficulty.
     * 
     * @param difficulty Difficulty to set
     */
    public void setDifficulty(Difficulty difficulty) {
        this.currentDifficulty = difficulty;
    }

    /**
     * Gets the current difficulty.
     * 
     * @return Current difficulty
     */
    public Difficulty getCurrentDifficulty() {
        return currentDifficulty;
    }

    /**
     * Adjusts ball speed based on current difficulty.
     * 
     * @param originalSpeed Original ball speed
     * @return Adjusted ball speed
     */
    public int adjustBallSpeed(int originalSpeed) {
        return (int) (originalSpeed * currentDifficulty.getSpeedMultiplier());
    }

    /**
     * Adjusts the paddle width based on the current difficulty.
     * 
     * @param originalPaddleWidth The default paddle width
     * @return The adjusted paddle width
     */
    public int adjustPaddleWidth(int originalPaddleWidth) {
        return currentDifficulty.getPaddleWidth();
    }
}