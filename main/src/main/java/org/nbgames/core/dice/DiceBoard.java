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

import java.util.LinkedList;
import java.util.Observable;
import javax.swing.JPanel;
import org.nbgames.core.api.Player.Handedness;

/**
 *
 * @author Patrik Karlström
 */
public class DiceBoard extends Observable {

    private final LinkedList<Die> mDice;
    private final DiceBoardPanel mDiceBoardPanel;
    private boolean mDiceOnFloor = false;
    private Thread mDieWatcherThread;
    private Handedness mHandedness = Handedness.RIGHT;
    private int mMaxRollCount = 3;
    private int mNumOfDice;
    private int mNumOfRolls = 0;
    private final Painter mPainter;
    private boolean mPlaySound = true;
    private final Roller mRoller;

    public DiceBoard(int numOfDice) {
        mRoller = new Roller(this);
        mPainter = new Painter(this);
        mDice = new LinkedList<>();
        mDiceBoardPanel = new DiceBoardPanel();
        setNumOfDice(numOfDice);
        init();
    }

    public void gameOver() {
        mPainter.setRollable(false);
        mPainter.setSelectable(false);
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
        int result = 0;

        for (Die die : mDice) {
            if (die.isSelected()) {
                result++;
            }
        }

        mRoller.setImage(result);

        return result;
    }

    public JPanel getPanel() {
        return mDiceBoardPanel;
    }

    public LinkedList<Integer> getValues() {
        LinkedList<Integer> values = new LinkedList<>();

        mDice.forEach((die) -> {
            values.add(die.getValue());
        });

        return values;
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
        mRoller.roll();
        mDiceOnFloor = false;

        mDice.forEach((die) -> {
            die.roll();
        });

        mRoller.setImage(getNumOfSelectedDice());
        mDieWatcherThread = new Thread(new DieWatchRunner());
        mDieWatcherThread.start();
    }

    public void setDiceTofloor(int frequency) {
        mDice.forEach((die) -> {
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

        mDice.forEach((die) -> {
            die.setVisible(true);
            die.setEnabled(false);
        });

        mDiceBoardPanel.repaint();
        setChanged();
        notifyObservers(RollEvent.POST_ROLL);
    }

    private void endOfTurn() {
        mDice.forEach((die) -> {
            die.park();
            die.setEnabled(false);
        });
    }

    private void init() {
        mDiceBoardPanel.add(mPainter);
    }

    private void reset() {
        mDice.forEach((die) -> {
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

    LinkedList<Die> getDice() {
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

    boolean isDiceOnFloor() {
        return mDiceOnFloor;
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

        setChanged();
        notifyObservers(RollEvent.POST_ROLL);
    }

    void rollPreOp() {
        setChanged();
        notifyObservers(RollEvent.PRE_ROLL);
    }

    void setDiceOnFloor(boolean diceOnFloor) {
        mDiceOnFloor = diceOnFloor;
    }

    public enum RollEvent {

        PRE_ROLL,
        POST_ROLL;
    }

    private class DieWatchRunner implements Runnable {

        @Override
        public void run() {
            mDice.forEach((die) -> {
                try {
                    die.getAnimator().join();
                } catch (InterruptedException ex) {
                }
            });

            if (!mDiceOnFloor) {
                rollPostOp();
            }
        }
    }
}
