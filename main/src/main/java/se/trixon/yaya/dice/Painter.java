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
package se.trixon.yaya.dice;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.HierarchyBoundsListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.AffineTransform;
import javax.swing.JPanel;
import javax.swing.event.MouseInputAdapter;
import se.trixon.yaya.Player.Handedness;
import se.trixon.yaya.Options;

/**
 *
 * @author Patrik Karlström
 */
class Painter extends JPanel {

    private final Options mOptions = Options.getInstance();

    private static final int MARGIN_X_DICE_SET = 160;
    static final int DIE_CELL_WIDTH = 140;
    static final int MARGIN_X_ROLLER = 10;
    static final int MARGIN_Y_ROLLER = 20;

    private final DiceBoard mDiceBoard;
    private Roller mDiceRoller;
    private int mDiceSetX;
    private int mDieAreaWidth;
    private Graphics2D mG2;
    private boolean mRollable;
    private boolean mSelectable;

    Painter(DiceBoard diceBoard) {
        mDiceBoard = diceBoard;
        init();
    }

    @Override
    public void paint(Graphics g) {
        mG2 = (Graphics2D) g;
        AffineTransform originalAffineTransform = mG2.getTransform();

        if (mDiceBoard.getHandedness() == Handedness.RIGHT) {
            AffineTransform affineTransform = originalAffineTransform;
            affineTransform.scale(-1, 1);
            affineTransform.translate(-getWidth(), 0);
            mG2.setTransform(affineTransform);
        }

        paintDiceRoller();
        paintDice();
        //paintGrid();

        mG2.setTransform(originalAffineTransform);
    }

    @Override
    public void update(Graphics g) {
        paint(g);
    }

    private void init() {
        addMouseListener(new DiceMouseInputAdapter());
        addMouseMotionListener(new MouseInputAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                Point point = translateMousePoint(e.getPoint());
                int x = point.x;

                if (isRollable() && mDiceRoller != null) {
                    if (x <= mDiceRoller.getImage().getWidth() && mDiceRoller.isVisible() == false) {
                        mDiceRoller.slideIn();
                    }

                    if (x >= mDiceRoller.getImage().getWidth() && mDiceRoller.isVisible() == true) {
                        mDiceRoller.slideOut();
                    }
                }
            }
        });

        addMouseWheelListener(new MouseAdapter() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {

                if (isSelectable() && isDiceStoped()) {
                    int dir = e.getWheelRotation();
                    boolean select = mOptions.isReverseDirection() ? dir == -1 : dir == 1;

                    for (Die die : mDiceBoard.getDice()) {
                        die.setSelected(select);
                    }
                }
            }
        });

        addHierarchyBoundsListener(new HierarchyBoundsListener() {

            @Override
            public void ancestorMoved(HierarchyEvent evt) {
            }

            @Override
            public void ancestorResized(HierarchyEvent evt) {
                calcGrid();
            }
        });

        calcGrid();
    }

    private boolean isDiceStoped() {
        boolean stopped = true;

        for (Die die : mDiceBoard.getDice()) {
            if (die.getAnimator().isAlive()) {
                stopped = false;
                break;
            }
        }

        return stopped;
    }

    private void paintDice() {
        for (Die die : mDiceBoard.getDice()) {
            if (die.isVisible()) {
                mG2.drawImage(die.getImage(), die.getX(), die.getY(), this);
            }
        }
    }

    private void paintDiceRoller() {
        mDiceRoller = mDiceBoard.getDiceRoller();

        if (mDiceRoller == null) {
            return;
        }

        mG2.drawImage(mDiceRoller.getImage(), mDiceRoller.getX(), mDiceRoller.getY(), this);
    }

    private void paintGrid() {
        paintVertical(MARGIN_X_DICE_SET);
        paintVertical(MARGIN_X_DICE_SET + 5);
        paintVertical(mDiceSetX);

        for (int i = 0; i < mDiceBoard.getNumOfDice() + 1; i++) {
            paintVertical(mDiceSetX + i * DIE_CELL_WIDTH);
            try {
                mG2.setColor(Color.red);
                paintVertical(mDiceBoard.getDice().get(i).getCenter());
                mG2.setColor(Color.black);
            } catch (Exception e) {
            }
        }
    }

    private void paintVertical(int x) {
        mG2.drawLine(x, 0, x, getHeight());
    }

    private Point translateMousePoint(Point aPoint) {
        Point point = aPoint;

        if (mDiceBoard.getHandedness() == Handedness.RIGHT) {
            point.x = getWidth() - point.x;
        }

        return point;
    }

    void calcGrid() {
        int diceSetWidth = DIE_CELL_WIDTH * mDiceBoard.getNumOfDice();
        mDieAreaWidth = getWidth() - MARGIN_X_DICE_SET;
        mDiceSetX = (mDieAreaWidth - diceSetWidth) / 2 + MARGIN_X_DICE_SET;

        for (int i = 0; i < mDiceBoard.getNumOfDice(); i++) {
            try {
                mDiceBoard.getDice().get(i).setCenter(mDiceSetX + i * DIE_CELL_WIDTH + DIE_CELL_WIDTH / 2);
            } catch (NullPointerException e) {
            }
        }
    }

    synchronized void calcRollable() {
        setRollable(mDiceBoard.getNumOfSelectedDice() > 0);
    }

    boolean isRollable() {
        return mRollable;
    }

    boolean isSelectable() {
        return mSelectable;
    }

    void setRollable(boolean rollable) {
        mRollable = rollable;
    }

    void setSelectable(boolean selectable) {
        mSelectable = selectable;
    }

    private class DiceMouseInputAdapter extends MouseInputAdapter {

        @Override
        public void mouseExited(MouseEvent e) {
            if (mDiceRoller.isVisible() == true) {
                mDiceRoller.slideOut();
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {
            Point point = translateMousePoint(e.getPoint());
            int x = point.x;

            if (e.getButton() == MouseEvent.BUTTON1) {

                if (x <= mDiceRoller.getImage().getWidth() && isRollable()) {
                    mDiceRoller.shake(true);
                    return;
                }

                if (isSelectable()) {
                    for (Die die : mDiceBoard.getDice()) {
                        if ((x - mDiceSetX >= DIE_CELL_WIDTH * die.getColumn()) && (x - mDiceSetX <= DIE_CELL_WIDTH * (die.getColumn() + 1))) {
                            die.setSelected(!die.isSelected());
                            break;
                        }
                    }
                }
            }

            if (e.getButton() == MouseEvent.BUTTON3) {
                if (isSelectable() && isDiceStoped()) {
                    for (Die die : mDiceBoard.getDice()) {
                        if ((x - mDiceSetX >= DIE_CELL_WIDTH * die.getColumn()) && (x - mDiceSetX <= DIE_CELL_WIDTH * (die.getColumn() + 1))) {
                        } else {
                            die.setSelected(!die.isSelected());
                        }
                    }
                }
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            Point point = translateMousePoint(e.getPoint());
            int x = point.x;

            if (mDiceRoller.getShakeThread().isAlive()) {
                mDiceRoller.getShakeThread().interrupt();
                mDiceRoller.shake(false);

                if (x <= mDiceRoller.getImage().getWidth() && isRollable()) {
                    mDiceBoard.rollPreOp();
                } else {
                    mDiceRoller.slideOut();
                }
            }
        }
    }
}
