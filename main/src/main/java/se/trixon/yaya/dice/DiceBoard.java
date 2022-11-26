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

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.JPanel;
import se.trixon.almond.util.GlobalState;
import se.trixon.yaya.Player.Handedness;

/**
 *
 * @author Patrik Karlström
 */
public class DiceBoard {

    private final AtomicBoolean mAnyOnFloor = new AtomicBoolean(false);

    private final ArrayList<Die> mDice;
    private final DiceBoardPanel mDiceBoardPanel;
    private Thread mDieWatcherThread;
    private final GlobalState mGlobalState;
    private Handedness mHandedness = Handedness.RIGHT;
    private int mMaxRollCount = 3;
    private int mNumOfDice;
    private int mNumOfRolls = 0;
    private final Painter mPainter;
    private boolean mPlaySound = true;
    private final Roller mRoller;

    public DiceBoard(GlobalState globalState, int numOfDice) {
        mGlobalState = globalState;
        mRoller = new Roller(this);
        mPainter = new Painter(this);
        mDice = new ArrayList<>();
        mDiceBoardPanel = new DiceBoardPanel();
        setNumOfDice(numOfDice);
        init();
    }

    public void gameOver() {
        mPainter.setRollable(false);
        mPainter.setSelectable(false);
    }

    public AtomicBoolean getAnyOnFloor() {
        return mAnyOnFloor;
    }

    public int getMaxRollCount() {
        return mMaxRollCount;
    }

    public int getNumOfDice() {
        return mNumOfDice;
    }

    public int getNumOfRolls() {
        return mNumOfRolls;
    }

    public synchronized int getNumOfSelectedDice() {
        int selectionCount = (int) mDice.stream().filter(die -> die.isSelected()).count();
        mRoller.setImage(selectionCount);

        return selectionCount;
    }

    public JPanel getPanel() {
        return mDiceBoardPanel;
    }

    public ArrayList<Integer> getValues() {
        return new ArrayList<>(mDice.stream().map(d -> d.getValue()).toList());
    }

    public void newTurn() {
        mNumOfRolls = 0;
        mDiceBoardPanel.repaint();

        mPainter.setSelectable(false);
        mPainter.setRollable(true);
        reset();
        mRoller.setImage(getNumOfDice());
    }

    public void roll() {
        mPainter.setRollable(false);
        mPainter.setSelectable(false);
        mAnyOnFloor.set(false);
        mRoller.roll();

        for (var die : mDice) {
            if (die.isSelected()) {
                die.roll();
            } else {
                die.setWasSelected(false);
            }
        }

        mRoller.setImage(getNumOfSelectedDice());
        mDieWatcherThread = new Thread(new DieWatchRunner());
        mDieWatcherThread.start();
    }

    public void setDiceTofloor(int frequency) {
        mDice.forEach(die -> {
            die.setDiceTofloor(frequency);
        });
    }

    public void setHandMode(Handedness handMode) {
        mHandedness = handMode;
    }

    public void setMaxRollCount(int maxRollCount) {
        mMaxRollCount = maxRollCount;
    }

    public void setPlaySound(boolean playSound) {
        mPlaySound = playSound;
    }

    public void undo() {
        mPainter.setRollable(false);
        mPainter.setSelectable(false);

        mDice.forEach(die -> {
            die.setVisible(true);
            die.setEnabled(false);
        });

        mDiceBoardPanel.repaint();
        mGlobalState.put(RollEvent.class.getName(), RollEvent.POST_ROLL);
    }

    ArrayList<Die> getDice() {
        return mDice;
    }

    Roller getDiceRoller() {
        return mRoller;
    }

    Handedness getHandedness() {
        return mHandedness;
    }

    Painter getPainter() {
        return mPainter;
    }

    boolean isPlaySound() {
        return mPlaySound;
    }

    void rollPostOp() {
        mNumOfRolls++;
        boolean enable = false;

        if (mNumOfRolls == mMaxRollCount) {
            endOfTurn();
        } else {
            enable = true;
        }

        mPainter.setRollable(false);
        mPainter.setSelectable(enable);

        mGlobalState.put(RollEvent.class.getName(), RollEvent.POST_ROLL);
    }

    void rollPreOp() {
        if (mAnyOnFloor.get()) {
            roll();
        } else {
            mGlobalState.put(RollEvent.class.getName(), RollEvent.PRE_ROLL);
        }
    }

    private void endOfTurn() {
        mDice.forEach(die -> {
            die.setEnabled(false);
        });
    }

    private void init() {
        mDiceBoardPanel.add(mPainter);
    }

    private void reset() {
        mDice.forEach(die -> {
            die.reset();
        });
    }

    private void setNumOfDice(int numOfDice) {
        mNumOfDice = numOfDice;
        mDice.clear();

        for (int i = 0; i < numOfDice; i++) {
            mDice.add(new Die(this, i));
        }
    }

    private class DieWatchRunner implements Runnable {

        @Override
        public void run() {
            mDice.forEach(die -> {
                try {
                    die.getRollThread().join();
                } catch (InterruptedException ex) {
                    //
                }
            });

            if (mAnyOnFloor.get()) {
                onFloor();
            } else {
                rollPostOp();
            }
        }

        private void onFloor() {
            mDice.stream()
                    .filter(die -> die.wasSelected())
                    .forEachOrdered(die -> {
                        if (die.isOnFloor()) {
                            die.moveInFromTop();
                        } else {
                            die.moveToTop();
                        }
                    });

            mDice.forEach(die -> {
                try {
                    die.getMoveInFromTopThread().join();
                    die.getMoveToTopThread().join();
                } catch (InterruptedException ex) {
                    //
                }
            });

            mDice.forEach(die -> {
                die.setSelected(die.wasSelected(), false);
                mPainter.setRollable(true);
                mPainter.calcRollable();
            });
        }
    }
}
