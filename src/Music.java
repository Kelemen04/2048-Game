import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class Music {
    private Clip clip;  // Clip for background music
    private Clip clip2; // Clip for click sound
    private FloatControl fc; // Control to adjust volume of background music
    private float volume; // Volume level for the music

    public Music() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        // Load the background music
        File file = new File("resources/music.wav");
        AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);
        clip = AudioSystem.getClip();
        clip.open(audioStream);
        fc = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN); // Get volume control for the background music

        // Load the click sound
        File file2 = new File("resources/click.wav");
        AudioInputStream audioStream2 = AudioSystem.getAudioInputStream(file2);
        clip2 = AudioSystem.getClip();
        clip2.open(audioStream2);
    }

    // Starts the background music and sets it to loop continuously
    public void start() {
        clip.loop(Clip.LOOP_CONTINUOUSLY);
        clip.start();
    }

    // Stops the background music
    public void stop() {
        clip.stop();
    }

    // Increases the volume of the background music
    public void volumeUp() {
        if (volume < 6.0f) {
            volume += 5.0f;
        } else {
            volume = 6.0f; // Max volume limit
        }
        fc.setValue(volume); // Apply the volume setting
    }

    // Decreases the volume of the background music
    public void volumeDown() {
        if (volume > -80.0f) {
            volume -= 5.0f;
        } else {
            volume = -80.0f; // Min volume limit (mute)
        }
        fc.setValue(volume); // Apply the volume setting
    }

    // Plays the click sound, resetting the position to the start before playing
    public void clickSound() {
        if (clip2.isRunning()) {
            clip2.stop(); // Stop if the sound is already playing
        }
        clip2.setFramePosition(0); // Reset to the start
        clip2.start(); // Play the click sound
    }
}
