# Block Breaker

## Overview

Block Breaker is a classic arcade game implemented in Java using Swing for the graphical user interface. The objective of the game is to break all the blocks on the screen using a paddle and a ball. The game features multiple difficulty levels, various power-ups, and a visually appealing user interface.

This implementation is inspired by the classic Block Breaker game, enhanced with modern design elements and sound effects. It serves as an excellent example of a simple yet engaging game built using core Java libraries.

## Gameplay Demo:

### Screenshots

![game-menu](https://github.com/user-attachments/assets/4ab36406-7685-41ab-99a2-dae73c93aeca)
> A screenshot of the game's Main Menu.

![difficulty-menu](https://github.com/user-attachments/assets/694c52f8-2ba5-4a64-8632-036101fb226c)
> A screenshot of the games of Difficulty menu.

![help-menu](https://github.com/user-attachments/assets/9b1217de-4010-4d97-8100-993b33ec54d3)
> A screenshot of the game's Help Menu.

## Features

- Multiple difficulty levels (Easy, Medium, Hard).

- Power-ups including:

- Extend Paddle

- Slow Ball

- Fast Ball

- Shrink Ball

- Extra Life

- Multi Balls

- Score Boost

- Invincibility

- Sound effects for game events like paddle hit, block break, and game over.

- Background music and menu hover sounds.

- Customizable block patterns (Checkerboard, Striped, Triangle, Diamond, ZigZag, Double Triangle, Wave & Random Pattern).

- Pause and resume functionality.

- Game statistics display (score and remaining lives).

- Customizable Paddle (color, width, roundness).

## Prerequisites

- Java Development Kit (JDK) 8 or later.

- An IDE (e.g., Eclipse, IntelliJ IDEA) or a simple text editor and terminal/command prompt for compilation and execution.

## Project Structure

The project is structured into several Java files, each responsible for different aspects of the game:

- **Main.java:** The entry point of the game. It sets up the game window and main game loop.

- **Gameplay.java:** Manages the main game logic, including rendering the game elements, handling user input, and updating game states.

- **BlockGenerator.java:** Responsible for generating and rendering the blocks.

- **DifficultyManager.java:** Handles the different difficulty settings affecting game parameters like ball speed, initial lives, and paddle width.

- **MenuScreen.java:** Implements the main menu screen, including navigation and settings.

- **RoundedButton.java:** Defines a custom rounded button used in menus.

- **HelpScreen.java:** Displays help and game instructions to the user.

- **SoundManager.java:** Manages sound effects and background music.

- **PowerUpManager.java:** Controls the behavior and effects of various power-ups.

- **Constants.java:** Contains global constants used throughout the game.

### Assets:

The `/assets/` directory contains game assets including images for the backgrounds and blocks, fonts, and sounds.

- **`/assets/backgrounds/`:** Background images used for different levels.

- **`/assets/blocks/`:** Block images.

- **`/assets/font/`:** Custom font files.

- **`/assets/icons/`:** Icons used in the game UI.

- **`/assets/sounds/`:** Sound effects and background music.

## Compilation and Execution

### Using an IDE:

1. Open the project in your IDE.

2. Build the project using the IDE's build tools.

3. Run `Main.java` as the main class.

### Using the Command Line (Windows, macOS, Linux):

1. Clone this repository
```bash

git clone https://github.com/exyreams/block-breaker.git

```

2. Navigate to the project directory:

```bash

cd block-breaker

```

3. Compile all `.java` files:

```bash

javac game/*.java

```

4. Run the game using the `Main` class:

```bash

java -cp game Main

```

**Note**:

- In other OS, make sure to replace the `.;game` appropriately for defining classpath with multiple directory.

- On Linux & MacOS `javac game/*.java` might give error like `javac: file not found: game/*.java`, so, in order to make that work, we can simply execute:

```bash

find . -name "*.java"  > sources.txt && javac  @sources.txt

```

## Controls

- **Move Paddle:** Left and right arrow keys (or 'A' and 'D' keys).

- **Start/Restart Game:** 'Enter' key.

- **Pause/Resume:** 'Esc' key.

- **Main Menu:** 'M' key (from the gameplay screen).

## Customization

- **Adding New Block Patterns:**

- Create new methods in `BlockGenerator.java` to define new patterns.

- Update `initializeBlocks()` to include your new patterns.

- **Modifying Difficulty Levels:**

- Adjust the parameters in the `Difficulty` enum in `DifficultyManager.java`.

## Troubleshooting

- If the game does not start, ensure that your classpath is set correctly, especially if you have resources like images and fonts in external directories.

## Contributing

Contributions to enhance the game, add new features, or improve existing ones are welcome. Please feel free to fork the repository, make your changes, and submit a pull request.

## License

This project is open-sourced under the [MIT License](LICENSE). Feel free to use, modify, and distribute the code as per the license terms. (Note: You need to have a License file added in your project, that describes what a user can & cannot do regarding modifying the project for personal/commercial usage. For more info regarding it, you can check choosealicense.com)

## Acknowledgments

- Inspired by the classic Block Breaker game.

- Special thanks to the Java open-source community for providing extensive documentation and examples.

## Contact

If you have any questions, suggestions, or comments about the game, feel free to reach out to me at (Note: It would be good to use contact information from a different source rather than personal ones like emails from google, etc)

- **Email:** `exyreams@gmail.com`

- **GitHub:** `exyreams` (or project-specific GitHub Issues page link if you have)

I hope you enjoy playing Block Breaker!
