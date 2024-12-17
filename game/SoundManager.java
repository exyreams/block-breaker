import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * SoundManager class handles all sound effects and background music for the
 * game. This version of SoundManager includes optimized clip management for
 * better handling of multiple sound playbacks.
 */
public class SoundManager {
    // Sound file paths
    private static final String BRICK_BREAK_SOUND = "/assets/sounds/brick_break.wav";
    private static final String PADDLE_HIT_SOUND = "/assets/sounds/paddle_hit.wav";
    private static final String GAME_OVER_SOUND = "/assets/sounds/game_over.wav";
    private static final String BACKGROUND_MUSIC = "/assets/sounds/background_music.wav";
    private static final String HOVER_SOUND = "/assets/sounds/hover_sound.wav";

    // Sound effect clips
    private Map<String, Clip> clips;

    // Background music clip
    private Clip backgroundMusicClip;

    // Volume control
    private FloatControl volumeControl;
    private float currentVolume = 1.0f;

    /**
     * Constructor for SoundManager.
     * Initializes the clip map.
     */
    public SoundManager() {
        clips = new HashMap<>();
        preloadSound(HOVER_SOUND);
    }

    /**
     * Preloads a sound into the clip map.
     * 
     * @param soundFile Path to the sound file.
     */
    private void preloadSound(String soundFile) {
        try {
            URL soundURL = getClass().getResource(soundFile);
            if (soundURL == null) {
                System.err.println("Sound file not found: " + soundFile);
                return;
            }

            AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundURL);
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            clips.put(soundFile, clip);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads an audio clip from a file if not already loaded.
     *
     * @param soundFile Path to the sound file
     * @return Loaded Clip object
     */
    private Clip loadClip(String soundFile) {
        if (!clips.containsKey(soundFile)) {
            preloadSound(soundFile);
        }
        return clips.get(soundFile);
    }

    /**
     * Plays the brick break sound.
     */
    public void playBrickBreakSound() {
        playSound(BRICK_BREAK_SOUND);
    }

    /**
     * Plays the paddle hit sound.
     */
    public void playPaddleHitSound() {
        playSound(PADDLE_HIT_SOUND);
    }

    /**
     * Plays the game over sound.
     */
    public void playGameOverSound() {
        playSound(GAME_OVER_SOUND);
    }

    /**
     * Plays the hover sound.
     */
    public void playHoverSound() {
        playSound(HOVER_SOUND);
    }

    /**
     * Starts playing the background music.
     */
    public void playBackgroundMusic() {
        if (backgroundMusicClip == null || !backgroundMusicClip.isRunning()) {
            backgroundMusicClip = loadClip(BACKGROUND_MUSIC);
            if (backgroundMusicClip != null) {
                setVolume(currentVolume);
                backgroundMusicClip.loop(Clip.LOOP_CONTINUOUSLY);
            }
        }
    }

    /**
     * Stops the background music.
     */
    public void stopBackgroundMusic() {
        if (backgroundMusicClip != null) {
            backgroundMusicClip.stop();
        }
    }

    /**
     * Plays a sound clip.
     *
     * @param soundFile Path to the sound file
     */
    private void playSound(String soundFile) {
        Clip clip = loadClip(soundFile);
        if (clip != null) {
            // Stop the clip before replaying
            if (clip.isRunning()) {
                clip.stop();
            }
            clip.setFramePosition(0);
            clip.start();
            // Set volume if control is available
            setVolume(currentVolume);
        }
    }

    /**
     * Sets the volume for all sound effects.
     *
     * @param volume Volume level (0.0 to 1.0)
     */
    public void setVolume(float volume) {
        currentVolume = volume;
        if (backgroundMusicClip != null && backgroundMusicClip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            volumeControl = (FloatControl) backgroundMusicClip.getControl(FloatControl.Type.MASTER_GAIN);
            float range = volumeControl.getMaximum() - volumeControl.getMinimum();
            float gain = (range * volume) + volumeControl.getMinimum();
            volumeControl.setValue(gain);
        }
        clips.forEach((key, value) -> {
            try {
                if (value != null && value.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                    FloatControl gainControl = (FloatControl) value.getControl(FloatControl.Type.MASTER_GAIN);
                    float range = gainControl.getMaximum() - gainControl.getMinimum();
                    float gain = (range * volume) + gainControl.getMinimum();
                    gainControl.setValue(gain);
                }
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        });
    }

    public float getVolume() {
        return currentVolume;
    }
}