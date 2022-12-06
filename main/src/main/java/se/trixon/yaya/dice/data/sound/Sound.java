/*
 * Copyright 2022 Patrik Karlström <patrik@trixon.se>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package se.trixon.yaya.dice.data.sound;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import org.openide.util.Exceptions;
import se.trixon.almond.util.SystemHelper;

/**
 *
 * @author Patrik Karlström
 */
public class Sound {

    private Clip mClip;
    private final ConcurrentHashMap<String, Clip> mClipCache = new ConcurrentHashMap<>();
    private final Random mRandom = new Random();

    public Sound(String clipName) {
        load(clipName);
    }

    public Sound(String clipName, boolean autoPlay, boolean loop) {
        load(clipName);

        if (loop) {
            mClip.loop(Clip.LOOP_CONTINUOUSLY);
        }

        if (autoPlay) {
            play();
        }
    }

    public Clip getClip() {
        return mClip;
    }

    public void play() {
        new Thread(() -> {
            mClip.start();
        }).start();
    }

    public void play(int maxDelay) {
        SystemHelper.runLaterDelayed(mRandom.nextInt(maxDelay), () -> {
            play();
        });
    }

    public void stop(int delay) {
        SystemHelper.runLaterDelayed(delay, () -> {
            stop();
        });
    }

    public void stop() {
        mClip.stop();
        mClip.close();
    }

    private void load(String clipName) {
        mClip = mClipCache.computeIfAbsent(clipName, k -> {
            var path = SystemHelper.getPackageAsPath(Sound.class) + clipName;
            var inputStream = Sound.class.getClassLoader().getResourceAsStream(path);

            try {
                var audioInputStream = AudioSystem.getAudioInputStream(inputStream);
                var clip = AudioSystem.getClip();
                clip.open(audioInputStream);
                audioInputStream.close();
                inputStream.close();

                return clip;
            } catch (UnsupportedAudioFileException | IOException | LineUnavailableException ex) {
                Exceptions.printStackTrace(ex);
            }

            return null;
        });
    }
}
