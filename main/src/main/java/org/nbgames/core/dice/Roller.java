/* 
 * Copyright 2018 Patrik Karlström.
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
package org.nbgames.core.dice;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.Random;
import org.nbgames.core.dice.data.image.DiceImage;
import se.trixon.almond.util.GraphicsHelper;

/**
 *
 * @author Patrik Karlström
 */
class Roller {

    private BufferedImage mBufferedImage;
    private final DiceBoard mDiceBoard;
    private int mNumOfDice;
    private final Random mRandom;
    private Thread mReleaseThread;
    private Thread mShakeThread;
    private Thread mSlideThread;
    private boolean mVisible;
    private int mX;
    private int mY;

    Roller(DiceBoard diceBoard) {
        mSlideThread = new Thread();
        mShakeThread = new Thread();
        mReleaseThread = new Thread();
        mRandom = new Random();
        mDiceBoard = diceBoard;
        init();
    }

    private void init() {
        setImage(5);
        mX = -500;
        mY = Painter.MARGIN_Y_ROLLER;
    }

    BufferedImage getImage() {
        return GraphicsHelper.flipBufferedImageX(mBufferedImage);
    }

    int getNumOfDice() {
        return mNumOfDice;
    }

    Thread getShakeThread() {
        return mShakeThread;
    }

    int getX() {
        return mX;
    }

    int getY() {
        return mY;
    }

    boolean isVisible() {
        return mVisible;
    }

    void roll() {
        mReleaseThread = new Thread(new ReleaseRunner());
        mReleaseThread.start();
    }

    void setImage(int rollCount) {
        int variant = mRandom.nextInt(2) + 1;
        rollCount = Math.min(rollCount, 6);
        mBufferedImage = DiceImage.get(String.format("hand/closed/%02d_%02d.png", rollCount, variant));
    }

    void setNumOfDice(int numOfDice) {
        mNumOfDice = numOfDice;
    }

    void setVisible(boolean visible) {
        mVisible = visible;
    }

    void shake(boolean state) {
        mVisible = true;
        if (state) {
            mSlideThread.interrupt();
            mShakeThread = new Thread(new ShakeRunner());
            mShakeThread.start();
        } else {
            mShakeThread.interrupt();
        }
    }

    void slideIn() {
        mSlideThread.interrupt();
        mShakeThread.interrupt();
        mSlideThread = new Thread(new SlideInRunner());
        mSlideThread.start();
    }

    void slideOut() {
        mSlideThread.interrupt();
        mShakeThread.interrupt();
        mSlideThread = new Thread(new SlideOutRunner());
        mSlideThread.start();
    }

    class ReleaseRunner implements Runnable {

        @Override
        public void run() {
            try {
                Thread.sleep(250);
            } catch (InterruptedException ex) {
            }
            slideOut();
        }
    }

    class ShakeRunner implements Runnable {

        @Override
        public void run() {
            BufferedImage originalBufferedImage;
            AffineTransformOp originalAffineTransformOp = new AffineTransformOp(mBufferedImage.createGraphics().getTransform(), AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
            originalBufferedImage = originalAffineTransformOp.filter(mBufferedImage, null);

            while (!Thread.interrupted()) {
                mX = Painter.MARGIN_X_ROLLER + mRandom.nextInt(10);
                mY = Painter.MARGIN_Y_ROLLER + mRandom.nextInt(10);

                AffineTransform affineTransform = mBufferedImage.createGraphics().getTransform();
                double theta = 0.03;
                affineTransform.rotate(-theta + 2 * theta * mRandom.nextDouble());

                AffineTransformOp affineTransformOp = new AffineTransformOp(affineTransform, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
                mBufferedImage = affineTransformOp.filter(mBufferedImage, null);

                mDiceBoard.getPanel().repaint();

                try {
                    Thread.sleep(60);
                } catch (InterruptedException ex) {
                    mBufferedImage = originalAffineTransformOp.filter(originalBufferedImage, null);
                    break;
                }

                mBufferedImage = originalAffineTransformOp.filter(originalBufferedImage, null);

            }
        }
    }

    class SlideInRunner implements Runnable {

        @Override
        public void run() {

            while (!Thread.interrupted() && mX < 0 + Painter.MARGIN_X_ROLLER) {
                mX += 4 - mX / 5;

                try {
                    Thread.sleep(30);
                } catch (InterruptedException ex) {
                    break;
                }

                mDiceBoard.getPanel().repaint();
            }

            mVisible = true;
        }
    }

    class SlideOutRunner implements Runnable {

        @Override
        public void run() {
            double accel = 1.011;
            int i = 0;

            while (!Thread.interrupted() && mX >= -mBufferedImage.getWidth()) {
                mX -= 8 + accel * i;
                i++;
                try {
                    Thread.sleep(30);
                } catch (InterruptedException ex) {
                    break;
                }
                mDiceBoard.getPanel().repaint();
            }

            mVisible = false;
        }
    }
}
