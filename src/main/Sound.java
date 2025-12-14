package main;

import javax.sound.sampled.*;
import java.net.URL;

public class Sound {

    private static final int POOL_SIZE = 5;

    private Clip[][] clips = new Clip[30][POOL_SIZE];
    private int[] clipIndex = new int[30];
    private URL[] soundURL = new URL[30];

    // ✅ TRACK CURRENT BGM
    public int currentMusic = -1;
    private boolean fading = false;

    public Sound() {
        soundURL[0] = getClass().getResource("/sound/menumusic2.wav");
        soundURL[1] = getClass().getResource("/sound/laser.wav");
        soundURL[2] = getClass().getResource("/sound/debris.wav");
        soundURL[3] = getClass().getResource("/sound/laser-boss.wav");
        soundURL[4] = getClass().getResource("/sound/hit.wav");
        soundURL[5] = getClass().getResource("/sound/enemy-hit.wav");
        soundURL[6] = getClass().getResource("/sound/gamemusic.wav");
        soundURL[7] = getClass().getResource("/sound/bossbackground.wav");
        soundURL[8] = getClass().getResource("/sound/gameover.wav");
        soundURL[9] = getClass().getResource("/sound/pickup.wav");

        preloadAll();
    }

    private void preloadAll() {
        for (int i = 0; i < soundURL.length; i++) {
            if (soundURL[i] == null) continue;

            try {
                for (int j = 0; j < POOL_SIZE; j++) {
                    AudioInputStream ais = AudioSystem.getAudioInputStream(soundURL[i]);
                    clips[i][j] = AudioSystem.getClip();
                    clips[i][j].open(ais);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // 🔊 SFX (overlapping)
    public void play(int i) {
        Clip clip = clips[i][clipIndex[i]];
        if (clip == null) return;

        clip.stop();
        clip.setFramePosition(0);
        clip.start();

        clipIndex[i] = (clipIndex[i] + 1) % POOL_SIZE;
    }

    // 🎵 SAFE MUSIC SWITCH (NO RESTARTS)
    public void switchMusic(int i) {
        if (currentMusic == i) return;

        if (currentMusic != -1) {
            stop(currentMusic);
        }

        loop(i);
        currentMusic = i;
    }

    // 🎵 LOOP (internal use only)
    public void loop(int i) {
        Clip clip = clips[i][0];
        if (clip == null) return;

        clip.stop();
        clip.setFramePosition(0);
        clip.loop(Clip.LOOP_CONTINUOUSLY);
    }

    // ⛔ STOP ALL INSTANCES
    public void stop(int i) {
        for (int j = 0; j < POOL_SIZE; j++) {
            if (clips[i][j] != null) {
                clips[i][j].stop();
            }
        }
        if (currentMusic == i) {
            currentMusic = -1;
        }
    }

    // 🎚 SAFE FADE + SWITCH
    public void fadeTo(int nextMusic, int durationMs) {
        if (fading || currentMusic == nextMusic) return;
        fading = true;

        Clip clip = (currentMusic != -1) ? clips[currentMusic][0] : null;

        new Thread(() -> {
            try {
                if (clip != null && clip.isRunning()) {
                    FloatControl gain =
                            (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);

                    float start = gain.getValue();
                    float end = gain.getMinimum();
                    int steps = 30;
                    float step = (start - end) / steps;
                    int sleep = durationMs / steps;

                    for (int j = 0; j < steps; j++) {
                        gain.setValue(start - step * j);
                        Thread.sleep(sleep);
                    }

                    clip.stop();
                    gain.setValue(start);
                }

                switchMusic(nextMusic);

            } catch (Exception ignored) {
            } finally {
                fading = false;
            }
        }).start();
    }
}
