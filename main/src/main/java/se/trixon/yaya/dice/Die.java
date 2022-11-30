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
package se.trixon.yaya.dice;

import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import javax.swing.SwingUtilities;
import org.openide.util.Exceptions;
import se.trixon.yaya.dice.data.image.DiceImage;
import se.trixon.yaya.dice.data.sound.Sound;

/**
 *
 * @author Patrik Karlström
 */
class Die {

    private static final int MARGIN_Y = 20;
    private static final int MAX_DR_1 = 4;
    private static final int MAX_DR_2 = 20;
    private static final int MAX_DR_3 = 4;
    private BufferedImage mBufferedImage;
    private int mCenter;
    private final int mColumn;
    private final DiceBoard mDiceBoard;
    private int mDiceToFloor = 0;
    private String mImagePath;
    private MoveInFromTopThread mMoveInFromTopThread = new MoveInFromTopThread();
    private MoveToTopThread mMoveToTopThread = new MoveToTopThread();
    private int mOffsetX;
    private boolean mOnFloor = false;
    private final Random mRandom = new Random();
    private RollThread mRollThread = new RollThread();
    private SelectThread mSelectThread = new SelectThread();
    private boolean mSelected = true;
    private int mStoredY;
    private int mValue;
    private boolean mVisible;
    private boolean mWasSelected = true;
    private int mX;
    private int mY;

    Die(DiceBoard diceBoard, int column) {
        mDiceBoard = diceBoard;
        mColumn = column;
        init();
    }

    public MoveInFromTopThread getMoveInFromTopThread() {
        return mMoveInFromTopThread;
    }

    public MoveToTopThread getMoveToTopThread() {
        return mMoveToTopThread;
    }

    public RollThread getRollThread() {
        return mRollThread;
    }

    public boolean isOnFloor() {
        return mOnFloor;
    }

    public void setOnFloor(boolean onFloor) {
        mOnFloor = onFloor;
    }

    public void setWasSelected(boolean wasSelected) {
        mWasSelected = wasSelected;
    }

    public boolean wasSelected() {
        return mWasSelected;
    }

    int getCenter() {
        return mCenter;
    }

    int getColumn() {
        return mColumn;
    }

    BufferedImage getImage() {
        return mBufferedImage;
    }

    int getValue() {
        return mValue;
    }

    int getX() {
        return mCenter + mOffsetX;
    }

    int getY() {
        return mY;
    }

    void interruptAllThreads() {
        mRollThread.interrupt();
        mSelectThread.interrupt();
        mMoveToTopThread.interrupt();
        mMoveInFromTopThread.interrupt();

        try {
            mRollThread.join();
            mSelectThread.join();
            mMoveToTopThread.join();
            mMoveInFromTopThread.join();
        } catch (InterruptedException ex) {
            Exceptions.printStackTrace(ex);
        }

    }

    boolean isSelected() {
        return mSelected;
    }

    boolean isVisible() {
        return mVisible;
    }

    void moveInFromTop() {
        mMoveInFromTopThread = new MoveInFromTopThread();
        mMoveInFromTopThread.start();
    }

    void moveToTop() {
        mMoveToTopThread = new MoveToTopThread();
        mMoveToTopThread.start();
    }

    void reset() {
        setVisible(false);
        mSelected = true;
        mWasSelected = false;
    }

    void roll() {
        if (mRollThread.isAlive()) {
            return;
        }

        interruptAllThreads();

        mOnFloor = false;
        mWasSelected = true;
        mRollThread = new RollThread();
        mRollThread.start();

        mSelected = false;
    }

    void setCenter(int centerValue) {
        mCenter = centerValue;
    }

    void setDiceTofloor(int diceToFlorFrequence) {
        mDiceToFloor = diceToFlorFrequence;
    }

    void setEnabled(boolean b) {
    }

    void setSelected(boolean selected, boolean withAnimation) {
        if (mRollThread.isAlive() || mSelectThread.isAlive()) {
            return;
        }

        mSelected = selected;

        if (withAnimation) {
            mSelectThread = new SelectThread();
            mSelectThread.start();
        }
    }

    void setVisible(boolean visible) {
        mVisible = visible;
    }

    private int generateValue() {
        mValue = mRandom.nextInt(6) + 1;
        int variant = mRandom.nextInt(2) + 1;
        int mode = 0;
        mImagePath = String.format("dice/%d_%02d_%02d.png", mode, mValue, variant);

        setBufferedImage(mImagePath);

        return mValue;
    }

    private String getThreadName(Thread t) {
        return "%s: %d".formatted(t.getClass().getSimpleName(), mColumn);
    }

    private void init() {
    }

    private void repaintDiceBoard() {
        SwingUtilities.invokeLater(() -> {
            mDiceBoard.getPanel().repaint();
        });
    }

    private void rotate(double theta) {
        var affineTransform = mBufferedImage.createGraphics().getTransform();
        affineTransform.rotate(theta, mBufferedImage.getWidth() / 2, mBufferedImage.getHeight() / 2);
        var affineTransformOp = new AffineTransformOp(affineTransform, AffineTransformOp.TYPE_BICUBIC);

        mBufferedImage = affineTransformOp.filter(mBufferedImage, null);
    }

    private void setBufferedImage(String imagePath) {
        mBufferedImage = DiceImage.get(imagePath);
    }

    private void startThread(Thread t, Runnable r) {
        if (t.isAlive()) {
            t.interrupt();
        }

        t = new Thread(r, "%s: %d".formatted(r.getClass().getSimpleName(), mColumn));
        t.start();
    }

    class MoveInFromTopThread extends Thread {

        public MoveInFromTopThread() {
            setName(getThreadName(this));
        }

        @Override
        public void run() {
            mY = -100;
            while (mY < -20) {
                mY += 10;
                mY = Math.min(0, mY);

                setBufferedImage(mImagePath);

                repaintDiceBoard();

                try {
                    TimeUnit.MILLISECONDS.sleep(80);
                } catch (InterruptedException ex) {
                }
            }
        }
    }

    class MoveToTopThread extends Thread {

        public MoveToTopThread() {
            setName(getThreadName(this));
        }

        @Override
        public void run() {
            while (mY > 0) {
                mY -= 10;
                mY = Math.max(0, mY);
                setBufferedImage(mImagePath);

                repaintDiceBoard();
                try {
                    TimeUnit.MILLISECONDS.sleep(80);
                } catch (InterruptedException ex) {
                }
            }
            Thread.currentThread().interrupt();
        }
    }

    class RollThread extends Thread {

        private int mBaseX;
        private int mBaseY;
        private int mLoops;

        public RollThread() {
            setName(getThreadName(this));
        }

        @Override
        public void run() {
            /*
            * This random delay manage the start order of the dice.
             */
            try {
                TimeUnit.MILLISECONDS.sleep(mRandom.nextInt(300 / (mColumn + 1)));
            } catch (InterruptedException ex) {
            }

            mX = 0;
            /*
             * Roll the die from the hand to its column.
             */
            rollOut();
            /*
            * ...continue to generate values and a new positions.
             */
            spin();

            if (mY < 30 && mDiceToFloor > 0) {
                if (!mDiceBoard.getAnyOnFloor().get() && mRandom.nextInt(mDiceToFloor) == mRandom.nextInt(mDiceToFloor)) {
                    mDiceBoard.getAnyOnFloor().set(true);
                    diceToFloor();
                } else {
                    vibrate();
                }
            } else {
                /*
                * ...final wobble, don't generate a new mValue.
                 */
                vibrate();
            }
        }

        private void diceToFloor() {
            mOnFloor = true;

            if (mDiceBoard.isPlaySound()) {
                new Sound("dtf.au", true, false);
            }

            while (mY < 200) {
                mY += 10;
                setBufferedImage(mImagePath);

                repaintDiceBoard();
                try {
                    TimeUnit.MILLISECONDS.sleep(60);
                } catch (InterruptedException ex) {
                }
            }
        }

        private void rollOut() {
            Sound sound = null;

            if (mDiceBoard.isPlaySound()) {
                int variant = mRandom.nextInt(MAX_DR_2) + 1;
                sound = new Sound(String.format("dr_2_%02d.au", variant), false, true);
                sound.play(100);
            }

            while (mX < mCenter - 75) {
                mX += mRandom.nextInt(95) + 25;
                mY = MARGIN_Y + mRandom.nextInt(60);
                mOffsetX = mX - mCenter;

                mValue = generateValue();
                rotate(2 * Math.PI * mRandom.nextDouble());
                if (!isVisible()) {
                    setVisible(true);
                }

                repaintDiceBoard();

                try {
                    TimeUnit.MILLISECONDS.sleep(30);
                } catch (InterruptedException ex) {
                }
            }

            if (sound != null) {
                sound.stop();
            }
        }

        private void spin() {
            mBaseY = mY;
            mLoops = mRandom.nextInt(7) + 5;
            Sound sound = null;

            if (mDiceBoard.isPlaySound()) {
                int variant = mRandom.nextInt(MAX_DR_2) + 1;
                sound = new Sound(String.format("dr_2_%02d.au", variant), false, true);
                sound.play(200);
            }

            for (int i = 0; i < mLoops; i++) {
                mValue = generateValue();
                double factor = 1.2;
                double theta = factor * mRandom.nextDouble();
                rotate(-factor + 2 * factor * theta);

                int randomSize = Math.max(1, Painter.DIE_CELL_WIDTH - mBufferedImage.getWidth());
                mX = (mCenter - Painter.DIE_CELL_WIDTH / 2) + mRandom.nextInt(randomSize);
                mY = mBaseY + mRandom.nextInt(30) - 10;
                mOffsetX = mX - mCenter;

                repaintDiceBoard();

                try {
                    TimeUnit.MILLISECONDS.sleep(mRandom.nextInt(40) + 120);
                } catch (InterruptedException ex) {
                }
            }

            if (sound != null) {
                sound.stop();
            }
        }

        private void vibrate() {
            mLoops = mRandom.nextInt(10) + 3;
            Sound sound = null;

            if (mDiceBoard.isPlaySound()) {
                int variant = mRandom.nextInt(MAX_DR_3) + 1;
                sound = new Sound(String.format("dr_3_%02d.au", variant));
                sound.play(100);
            }

            mBaseX = mX;
            mBaseY = mY;

            for (int i = 0; i < mLoops; i++) {
                mX = mBaseX + mRandom.nextInt(6) - 3;
                mY = mBaseY + mRandom.nextInt(6) - 3;
                mOffsetX = mX - mCenter;

                setBufferedImage(mImagePath);

                double factor = 0.2;
                double theta = factor * mRandom.nextDouble();
                rotate(-factor + 2 * factor * theta);

                repaintDiceBoard();

                try {
                    TimeUnit.MILLISECONDS.sleep(30);
                } catch (InterruptedException ex) {
                }
            }

            setBufferedImage(mImagePath);
            repaintDiceBoard();
            mStoredY = mY;

            if (sound != null) {
                sound.stop();
            }
        }
    }

    class SelectThread extends Thread {

        public SelectThread() {
            setName(getThreadName(this));
        }

        @Override
        public void run() {
            int endPos = mDiceBoard.getPanel().getHeight();

            if (mSelected) {
                while (mY < endPos) {
                    mY = Math.min((int) (mY * 1.3), endPos);
                    setBufferedImage(mImagePath);

                    repaintDiceBoard();

                    try {
                        TimeUnit.MILLISECONDS.sleep(30);
                    } catch (InterruptedException ex) {
                    }
                }
            } else {
                while (mY > mStoredY) {
                    mY = Math.max(mY - (int) (mY * 0.3), mStoredY);
                    setBufferedImage(mImagePath);

                    repaintDiceBoard();

                    try {
                        TimeUnit.MILLISECONDS.sleep(30);
                    } catch (InterruptedException ex) {
                    }
                }
            }
            mDiceBoard.getPainter().calcRollable();
        }
    }
}
