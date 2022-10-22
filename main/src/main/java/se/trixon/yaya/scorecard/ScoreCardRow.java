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

import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import se.trixon.almond.util.GraphicsHelper;
import se.trixon.yaya.Options;
import se.trixon.yaya.ThemeManager;
import se.trixon.yaya.gamedef.GameRow;

/**
 *
 * @author Patrik Karlström
 */
public class ScoreCardRow {

    private final int COLOR_MASK = 0xEEEEEE;
    private GameRow mGameRow;
    private boolean mHeader = false;
    private PlayerColumn mPlayerColumn;
    private int mPreview;
    private boolean mRegistered;
    private int mRow;
    private RowLabel mRowLabel = new RowLabel();
    private ScoreCard mScoreCard;
    private int mValue;
    private final Options mOptions = Options.getInstance();
    private final ThemeManager mThemeManager = ThemeManager.getInstance();

    ScoreCardRow(ScoreCard scoreCard, PlayerColumn playerColumn, GameRow gameRow, int row) {
        mScoreCard = scoreCard;
        mPlayerColumn = playerColumn;
        mGameRow = gameRow;
        mRow = row;
        init();
    }

    ScoreCardRow(ScoreCard scoreCard, GameRow gameRow, int row, boolean isHeader) {
        mScoreCard = scoreCard;
        mGameRow = gameRow;
        mRow = row;
        mHeader = isHeader;
        init();
    }

    public void clearPreview() {
        if (isPlayable() && !isRegistered()) {
            mRowLabel.setText("");
            mRowLabel.setCurrentBackgroundColor(mThemeManager.getRow());

            mRowLabel.setBackground();
        }
    }

    public void enableHover() {
        if (isPlayable() && !isRegistered()) {
            mRowLabel.addMouseListener(new MouseAdapter() {

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
            mRowLabel.addMouseListener(new MouseAdapter() {

                @Override
                public void mousePressed(MouseEvent evt) {
                    mousePressedEvent(evt);
                }
            });
        }
    }

    public GameRow getGameRow() {
        return mGameRow;
    }

    public RowLabel getLabel() {
        return mRowLabel;
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
        return mGameRow.isPlayable();
    }

    public boolean isRegistered() {
        return mRegistered;
    }

    public void newGame() {
        mRegistered = false;
        mRowLabel.setCurrentBackgroundColor(mThemeManager.getRow());
        mRowLabel.setText(null);
        mPreview = 0;
        mValue = 0;
        if (mGameRow.isRollCounter()) {
            mRowLabel.setText("0");
        }
    }

    public void register() {
        mValue = mPreview;
        mRegistered = true;
    }

    public void setEnabled(boolean aState) {
        if (mGameRow.isPlayable()) {
            mRowLabel.setFont(mRowLabel.getFont().deriveFont(Font.PLAIN));
            mRowLabel.setCurrentBackgroundColor(mThemeManager.getRow());
        }

        if (aState) {
            if (mGameRow.isSum() || mGameRow.isBonus()) {
                mRowLabel.setBackground(mThemeManager.getSum());
            } else {
                mRowLabel.setBackground(mThemeManager.getRow());
            }

            if (mGameRow.isRollCounter()) {
                mRowLabel.setFont(mRowLabel.getFont().deriveFont(Font.BOLD));
            }

            enableHover();

        } else {
            if (mGameRow.isSum() || mGameRow.isBonus()) {
                mRowLabel.setBackground(GraphicsHelper.colorAndMask(mThemeManager.getSum(), COLOR_MASK));
            } else {
                mRowLabel.setBackground(GraphicsHelper.colorAndMask(mThemeManager.getRow(), COLOR_MASK));
            }

            if (mGameRow.isRollCounter()) {
                mRowLabel.setFont(mRowLabel.getFont().deriveFont(Font.PLAIN));
            }

            MouseListener[] mouseListeners = (MouseListener[]) (mRowLabel.getListeners(MouseListener.class));

            for (MouseListener mouseListener : mouseListeners) {
                mRowLabel.removeMouseListener(mouseListener);
            }
        }
    }

    public void setPreview(int preview) {
        this.mPreview = preview;
    }

    public void setRegistered(boolean registered) {
        this.mRegistered = registered;
    }

    public void setText() {
        String text = "";
        if (isPlayable()) {
            mRowLabel.setFont(mRowLabel.getFont().deriveFont(Font.PLAIN));
            if (isRegistered()) {
                mRowLabel.setHorizontalAlignment(SwingConstants.TRAILING);
                text = (mValue == 0) ? "0" : Integer.toString(mValue);
            }
            mRowLabel.setText(text);
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

        if (visible) {
            text = Integer.toString(mPreview);
            mRowLabel.setFont(mRowLabel.getFont().deriveFont(Font.BOLD));
            mRowLabel.setHorizontalAlignment(SwingConstants.LEADING);

            if (mPreview < mGameRow.getLim()) {
                mRowLabel.setCurrentBackgroundColor(mThemeManager.getIndicatorLo());
            } else {
                mRowLabel.setCurrentBackgroundColor(mThemeManager.getIndicatorHi());
            }
        } else {
            mRowLabel.setCurrentBackgroundColor(mThemeManager.getRow());
        }

        mRowLabel.setText(text);
        mRowLabel.setBackground();
    }

    private void init() {
        mRowLabel.setOpaque(true);
        mRowLabel.setHorizontalAlignment(SwingConstants.TRAILING);
        mRowLabel.setBorder(new EmptyBorder(2, 10, 2, 10));

        if (mGameRow.isSum() || mGameRow.isBonus()) {
            mRowLabel.setFont(mRowLabel.getFont().deriveFont(Font.BOLD));
        }
    }

    private void mouseEnteredEvent(MouseEvent evt) {
        mRowLabel.setBackground(GraphicsHelper.colorAndMask(mRowLabel.getCurrentBackgroundColor(), COLOR_MASK));
        mScoreCard.hoverRowEntered(mRow);
    }

    private void mouseExitedEvent(MouseEvent evt) {
        mRowLabel.setBackground(mRowLabel.getCurrentBackgroundColor());
        mScoreCard.hoverRowExited(mRow);
    }

    private void mousePressedEvent(MouseEvent evt) {
        if (evt.getButton() == MouseEvent.BUTTON1) {
            mPlayerColumn.getRowStack().push(mRow);
            mScoreCard.hoverRowExited(mRow);

            MouseListener[] mouseListeners = (MouseListener[]) (mRowLabel.getListeners(MouseListener.class));

            for (MouseListener mouseListener : mouseListeners) {
                mRowLabel.removeMouseListener(mouseListener);
            }

            register();
            mPlayerColumn.setText();
            mScoreCard.register();
        }
    }
}
