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
package se.trixon.yaya.scorecard;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import se.trixon.almond.util.GraphicsHelper;
import se.trixon.yaya.ThemeManager;
import se.trixon.yaya.rules.GameCell;

/**
 *
 * @author Patrik Karlström
 */
public class Cell {

    private final int COLOR_MASK = 0xEEEEEE;
    private Color mCurrentBackgroundColor;
    private final GameCell mGameCell;
    private boolean mHeader = false;
    private final Header mHeaderColumn;
    private final JLabel mLabel = new JLabel();
    private PlayerColumn mPlayerColumn;
    private int mPreview;
    private boolean mRegistered;
    private final int mRow;
    private final ScoreCard mScoreCard;
    private final ThemeManager mThemeManager = ThemeManager.getInstance();
    private int mValue;

    Cell(ScoreCard scoreCard, PlayerColumn playerColumn, GameCell gameCell, int row) {
        mScoreCard = scoreCard;
        mHeaderColumn = mScoreCard.getHeader();
        mPlayerColumn = playerColumn;
        mGameCell = gameCell;
        mRow = row;

        init();
    }

    Cell(ScoreCard scoreCard, GameCell gameRow, int row, boolean isHeader) {
        mScoreCard = scoreCard;
        mHeaderColumn = mScoreCard.getHeader();
        mGameCell = gameRow;
        mRow = row;
        mHeader = isHeader;

        init();
    }

    public void clearPreview() {
        if (isPlayable() && !isRegistered()) {
            mLabel.setText("");
            setCurrentBackgroundColor(mThemeManager.getTheme().getBgScoreCell());
            setBackground();
        }
    }

    public void enableHover() {
        if (isPlayable() && !isRegistered()) {
            mLabel.addMouseListener(new MouseAdapter() {

                @Override
                public void mouseEntered(MouseEvent evt) {
                    mouseEnteredEvent(evt);
                }

                @Override
                public void mouseExited(MouseEvent evt) {
                    mouseExitedEvent(evt);
                }
            });
        }
    }

    public void enableInput() {
        if (isPlayable() && !isRegistered()) {
            mLabel.addMouseListener(new MouseAdapter() {

                @Override
                public void mousePressed(MouseEvent evt) {
                    mousePressedEvent(evt);
                }
            });
        }
    }

    public GameCell getGameCell() {
        return mGameCell;
    }

    public JLabel getLabel() {
        return mLabel;
    }

    public int getRow() {
        return mRow;
    }

    public int getValue() {
        return mValue;
    }

    public boolean isHeader() {
        return mHeader;
    }

    public boolean isPlayable() {
        return mGameCell.isPlayable();
    }

    public boolean isRegistered() {
        return mRegistered;
    }

    public void newGame() {
        mRegistered = false;
        setCurrentBackgroundColor(mThemeManager.getTheme().getBgScoreCell());
        mLabel.setText("");
        mPreview = 0;
        mValue = 0;

        if (mGameCell.isRollCounter()) {
            mLabel.setText("0");
        }
    }

    public void register() {
        mValue = mPreview;
        mRegistered = true;
    }

    public void setBackground() {
        mLabel.setBackground(mCurrentBackgroundColor);
    }

    public void setCurrentBackgroundColor(Color color) {
        mCurrentBackgroundColor = color;
    }

    public void setEnabled(boolean aState) {
        var theme = mThemeManager.getTheme();

        if (mGameCell.isPlayable()) {
            mLabel.setFont(mLabel.getFont().deriveFont(Font.PLAIN));
            setCurrentBackgroundColor(theme.getBgScoreCell());
        }

        if (aState) {
            if (mGameCell.isSum() || mGameCell.isBonus()) {
                mLabel.setBackground(theme.getBgHeaderSum());
            } else {
                mLabel.setBackground(theme.getBgScoreCell());
            }

            if (mGameCell.isRollCounter()) {
                mLabel.setFont(mLabel.getFont().deriveFont(Font.BOLD));
            }

            enableHover();
        } else {
            if (mGameCell.isSum() || mGameCell.isBonus()) {
                mLabel.setBackground(GraphicsHelper.colorAndMask(theme.getBgHeaderSum(), COLOR_MASK));
            } else {
                mLabel.setBackground(GraphicsHelper.colorAndMask(theme.getBgScoreCell(), COLOR_MASK));
            }

            if (mGameCell.isRollCounter()) {
                mLabel.setFont(mLabel.getFont().deriveFont(Font.PLAIN));
            }

            var mouseListeners = (MouseListener[]) (mLabel.getListeners(MouseListener.class));

            for (var mouseListener : mouseListeners) {
                mLabel.removeMouseListener(mouseListener);
            }
        }
    }

    public void setPreview(int preview) {
        mPreview = preview;
    }

    public void setRegistered(boolean registered) {
        mRegistered = registered;
    }

    public void setText() {
        String text = "";

        if (isPlayable()) {
            mLabel.setFont(mLabel.getFont().deriveFont(Font.PLAIN));
            if (isRegistered()) {
                mLabel.setHorizontalAlignment(SwingConstants.TRAILING);
                text = (mValue == 0) ? "0" : Integer.toString(mValue);
            }
            mLabel.setText(text);
        }
    }

    public void setValue(int value) {
        mValue = value;
    }

    public void setVisibleIndicator(boolean visible) {
        if (mPreview == 0 || isRegistered()) {
            return;
        }

        String text = "";

        var theme = mThemeManager.getTheme();

        if (visible) {
            text = Integer.toString(mPreview);
            mLabel.setFont(mLabel.getFont().deriveFont(Font.BOLD));
            mLabel.setHorizontalAlignment(SwingConstants.LEADING);

            if (mPreview < mGameCell.getLim()) {
                setCurrentBackgroundColor(theme.getBgIndicatorLo());
            } else {
                setCurrentBackgroundColor(theme.getBgIndicatorHi());
            }
        } else {
            setCurrentBackgroundColor(theme.getBgScoreCell());
        }

        mLabel.setText(text);
        setBackground();
    }

    private void init() {
        mLabel.setOpaque(true);
        mLabel.setHorizontalAlignment(SwingConstants.TRAILING);
        mLabel.setBorder(new EmptyBorder(2, 10, 2, 10));

        if (mGameCell.isSum() || mGameCell.isBonus()) {
            mLabel.setFont(mLabel.getFont().deriveFont(Font.BOLD));
        }
    }

    private void mouseEnteredEvent(MouseEvent evt) {
        mLabel.setBackground(GraphicsHelper.colorAndMask(mCurrentBackgroundColor, COLOR_MASK));
        mHeaderColumn.hoverRowEntered(mRow);
    }

    private void mouseExitedEvent(MouseEvent evt) {
        mLabel.setBackground(mCurrentBackgroundColor);
        mHeaderColumn.hoverRowExited(mRow);
    }

    private void mousePressedEvent(MouseEvent evt) {
        if (evt.getButton() == MouseEvent.BUTTON1) {
            mPlayerColumn.getRowStack().push(mRow);
            mHeaderColumn.hoverRowExited(mRow);

            var mouseListeners = (MouseListener[]) (mLabel.getListeners(MouseListener.class));

            for (var mouseListener : mouseListeners) {
                mLabel.removeMouseListener(mouseListener);
            }

            register();
            mPlayerColumn.setText();
            mScoreCard.register();
        }
    }
}
