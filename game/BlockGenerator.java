import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * BlockGenerator class creates and manages the brick layout.
 * It supports various brick patterns and colors, enhancing the game's visual
 * appeal.
 * Bricks are generated based on predefined patterns, and their colors are
 * customizable.
 */
public class BlockGenerator {
    public int map[][];
    private int numRows;
    private int numCols;
    private Image blockImage; // Image for the blocks
    private static final int TOP_SPACING = Constants.TOP_SPACING;
    private boolean useImage; // Flag to determine if an image should be used

    // Static cache for block images
    private static Map<Integer, Image> blockImageCache = new HashMap<>();

    // Cached brick size
    private static int cachedBrickWidth = -1;
    private static int cachedBrickHeight = -1;

    // Default colors for bricks in different rows
    private static final Color[] DEFAULT_COLORS = {
            Color.RED,
            new Color(255, 165, 0), // Orange
            new Color(255, 255, 0), // Yellow
            new Color(0, 255, 0), // Green
            new Color(0, 128, 255), // Light Blue
            new Color(0, 0, 255), // Blue
            new Color(128, 0, 255) // Violet
    };

    /**
     * Constructor for BlockGenerator.
     *
     * @param difficulty      Difficulty level, affecting the number of rows and
     *                        columns.
     * @param backgroundIndex Index of the background image, used to determine the
     *                        block image.
     * @param useImage        Flag to indicate whether to use an image for the
     *                        blocks.
     */
    public BlockGenerator(DifficultyManager.Difficulty difficulty, int backgroundIndex, boolean useImage) {
        numRows = difficulty.getBrickRows();
        numCols = Constants.NUM_COLS;
        map = new int[numRows][numCols];
        this.useImage = useImage;

        // Load and cache the block image if necessary
        if (useImage) {
            if (!blockImageCache.containsKey(backgroundIndex)) {
                loadBlockImage(backgroundIndex);
            }
            blockImage = blockImageCache.get(backgroundIndex);
        }

        initializeBlocks();

        // Calculate brick size only once
        if (cachedBrickWidth == -1) {
            calculateBrickSize();
        }
    }

    /**
     * Loads the block image corresponding to the selected background and caches it.
     *
     * @param backgroundIndex Index of the background image.
     */
    private void loadBlockImage(int backgroundIndex) {
        String imagePath = Constants.BLOCK_IMAGE_PATH_PREFIX + backgroundIndex + Constants.BLOCK_IMAGE_PATH_SUFFIX;
        Image originalImage = new ImageIcon(getClass().getResource(imagePath)).getImage();

        // Scale the image to be square
        int side = Math.max(originalImage.getWidth(null), originalImage.getHeight(null));
        BufferedImage squareImage = new BufferedImage(side, side, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = squareImage.createGraphics();

        // Center the original image on the square canvas
        int x = (side - originalImage.getWidth(null)) / 2;
        int y = (side - originalImage.getHeight(null)) / 2;
        g2d.drawImage(originalImage, x, y, null);
        g2d.dispose();

        blockImageCache.put(backgroundIndex, squareImage);
    }

    /**
     * Initializes the blocks based on predefined patterns.
     * The method randomly selects a pattern and generates the blocks accordingly.
     */
    private void initializeBlocks() {
        // Randomly choose a pattern index
        Random random = new Random();
        int patternIndex = random.nextInt(8); // Now using 8 as there are 8 patterns

        switch (patternIndex) {
            case 0:
                generateCheckerboardPattern();
                break;
            case 1:
                generateStripedPattern();
                break;
            case 2:
                generateTrianglePattern();
                break;
            case 3:
                generateDiamondPattern();
                break;
            case 4:
                generateZigZagPattern();
                break;
            case 5:
                generateDoubleTrianglePattern();
                break;
            case 6:
                generateWavePattern();
                break;
            case 7:
                generateRandomPattern();
                break;
        }
    }

    /**
     * Generates a checkerboard pattern of blocks.
     * The pattern alternates between filled and empty blocks in a grid.
     */
    private void generateCheckerboardPattern() {
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                map[i][j] = (i + j) % 2 == 0 ? 1 : 0; // Checkerboard pattern
            }
        }
    }

    /**
     * Generates a striped pattern of blocks.
     * The pattern consists of alternating rows of filled and empty blocks.
     */
    private void generateStripedPattern() {
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                map[i][j] = i % 2 == 0 ? 1 : 0; // Striped pattern
            }
        }
    }

    /**
     * Generates a triangular pattern of blocks.
     * The pattern forms a triangle shape with filled blocks.
     */
    private void generateTrianglePattern() {
        for (int i = 0; i < numRows; i++) {
            // Calculate the start and end columns for the current row
            int startCol = numCols / 2 - i;
            int endCol = numCols / 2 + i;

            for (int j = 0; j < numCols; j++) {
                // Fill blocks within the calculated range for each row
                if (j >= startCol && j <= endCol) {
                    map[i][j] = 1; // Triangle pattern
                } else {
                    map[i][j] = 0; // Empty outside the triangle
                }
            }
        }
    }

    /**
     * Generates a diamond pattern of blocks.
     * The pattern creates a diamond shape with filled blocks in the center.
     */
    private void generateDiamondPattern() {
        for (int i = 0; i < numRows; i++) {
            int center = numCols / 2;
            int width = i < numRows / 2 ? i : numRows - 1 - i;

            // Set blocks to 1 for columns that fall within the diamond's range for this row
            for (int j = center - width; j <= center + width; j++) {
                map[i][j] = 1;
            }
        }
    }

    /**
     * Generates a Zig-Zag pattern of blocks.
     * Each row of blocks alternates its starting position to create a zig-zag
     * effect.
     */
    private void generateZigZagPattern() {
        for (int i = 0; i < numRows; i++) {
            // Alternate the starting position for each row
            int start = (i % 2 == 0) ? 0 : 1;
            for (int j = start; j < numCols; j += 2) {
                map[i][j] = 1; // Zig-zag pattern
            }
        }
    }

    /**
     * Generates a pattern with two inverted triangles.
     * This pattern divides the block area into two triangles facing opposite
     * directions.
     */
    private void generateDoubleTrianglePattern() {
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                // Create two inverted triangles
                if (i <= j && i + j < numCols) {
                    map[i][j] = 1; // Upper triangle
                } else if (i > j && i + j >= numCols) {
                    map[i][j] = 1; // Lower triangle
                } else {
                    map[i][j] = 0;
                }
            }
        }
    }

    /**
     * Generates a wave-like pattern of blocks.
     * The blocks are arranged in a sine wave pattern across the rows.
     */
    private void generateWavePattern() {
        for (int i = 0; i < numRows; i++) {
            double angle = (2 * Math.PI * i) / numRows; // Calculate angle for sine wave
            int start = (int) (numCols / 2 + numCols / 3 * Math.sin(angle));
            int end = start + numCols / 4;

            for (int j = start; j <= end && j < numCols; j++) {
                if (j >= 0) { // Ensure j is within array bounds
                    map[i][j] = 1; // Wave pattern
                }
            }
        }
    }

    /**
     * Generates a random pattern of blocks.
     * Each block has a 50% chance of being set as active or inactive, creating
     * a randomized block layout.
     */
    private void generateRandomPattern() {
        Random random = new Random();
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                map[i][j] = random.nextBoolean() ? 1 : 0; // Random pattern
            }
        }
    }

    /**
     * Calculates the size of each brick, ensuring they are square.
     * This is now done only once and the result is cached.
     */
    private void calculateBrickSize() {
        int windowWidth = Constants.WINDOW_WIDTH;
        int windowHeight = Constants.WINDOW_HEIGHT;
        int horizontalMargin = Constants.HORIZONTAL_MARGIN;
        int blockAreaWidth = windowWidth - 2 * horizontalMargin;
        int blockAreaHeight = windowHeight / 3; // Allocate 1/3 of the window height for blocks

        // Calculate brick size based on the smaller dimension to ensure square bricks
        cachedBrickWidth = Math.min(blockAreaWidth / numCols, blockAreaHeight / numRows);
        cachedBrickHeight = cachedBrickWidth; // Ensure height is the same as width for square bricks
    }

    /**
     * Draws the blocks using either an image or a solid color based on the
     * useImage flag.
     * Each row of blocks can have a different color, defined by the DEFAULT_COLORS
     * array.
     * If images are used, the corresponding image is drawn. Otherwise, blocks are
     * drawn
     * as rectangles with a black border for better visibility.
     *
     * @param g Graphics2D object for drawing.
     */
    public void draw(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                if (map[i][j] > 0) {
                    int x = j * cachedBrickWidth + Constants.HORIZONTAL_MARGIN;
                    int y = i * cachedBrickHeight + Constants.VERTICAL_MARGIN + TOP_SPACING;

                    if (useImage) {
                        // Draw the brick image
                        g.drawImage(blockImage, x, y, cachedBrickWidth, cachedBrickHeight, null);
                    } else {
                        // Select color based on the row
                        g.setColor(DEFAULT_COLORS[i % DEFAULT_COLORS.length]);
                        // Draw a filled rectangle as the brick
                        g.fillRect(x, y, cachedBrickWidth, cachedBrickHeight);
                        // Draw a black border around each brick
                        g.setColor(Color.BLACK);
                        g.drawRect(x, y, cachedBrickWidth, cachedBrickHeight);
                    }
                }
            }
        }
    }

    /**
     * Sets the value of a specific brick.
     *
     * @param value Value to set (0 for broken, 1 for intact)
     * @param row   Row of the brick
     * @param col   Column of the brick
     */
    public void setBrickValue(int value, int row, int col) {
        map[row][col] = value;
    }

    // Getter methods for brick width and height
    public int getBrickWidth() {
        return cachedBrickWidth;
    }

    public int getBrickHeight() {
        return cachedBrickHeight;
    }
}