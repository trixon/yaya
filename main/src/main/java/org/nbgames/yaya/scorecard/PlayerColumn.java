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
package org.nbgames.yaya.scorecard;

import java.awt.Dimension;
import java.util.LinkedList;
import java.util.Stack;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import org.nbgames.core.api.Player;
import org.nbgames.yaya.gamedef.GameRow;
import org.nbgames.yaya.gamedef.GameRows;
import org.nbgames.yaya.gamedef.GameType;

/**
 *
 * @author Patrik Karlström
 */
public class PlayerColumn {

    private boolean mActive;
    private int mCurrentScore;
    private LinkedList<Integer> mDice;
    private GameType mGameType;
    private JLabel mLabel = new JLabel("XXX");
    private String mName;
    private int mNumOfRolls;
    private int mPlayOrder;
    private Player mPlayer;
    private Stack<Integer> mRowStack = new Stack<Integer>();
    private ScoreCardRow[] mRows;
    private ScoreCard mScoreCard;

    public PlayerColumn(ScoreCard scoreCard, int playOrder, GameType gameType) {
        mScoreCard = scoreCard;
        mPlayOrder = playOrder;
        mGameType = gameType;
        init();
    }

    public void clearPreview() {
        for (ScoreCardRow scoreCardRow : mRows) {
            scoreCardRow.clearPreview();
        }
    }

    public void decNumOfRolls() {
        mNumOfRolls--;
    }

    public int getCurrentScore() {
        return mCurrentScore;
    }

    public JLabel getLabel() {
        return mLabel;
    }

    public String getName() {
        return "player_" + mPlayOrder;
    }

    public int getNumOfRolls() {
        return mNumOfRolls;
    }

    public int getPlayOrder() {
        return mPlayOrder;
    }

    public Player getPlayer() {
        return mPlayer;
    }

    public Stack<Integer> getRowStack() {
        return mRowStack;
    }

    public ScoreCardRow[] getRows() {
        return mRows;
    }

    public void incNumOfRolls() {
        mNumOfRolls++;
    }

    public void newGame() {
        mNumOfRolls = 0;
        mRowStack.clear();
        setEnabled(false);

        for (ScoreCardRow scoreCardRow : mRows) {
            scoreCardRow.newGame();
        }

        setRollCounterLabel();
    }

    public void parse(LinkedList<Integer> values) {
        mDice = values;
        for (ScoreCardRow scoreCardRow : mRows) {
            if (scoreCardRow.getGameRow().isRollCounter()) {
                String rolls = Integer.toString(getNumOfRolls());
                int maxRolls = mGameType.getNumOfRolls();
                if (maxRolls > 0) {
                    rolls += " (" + mScoreCard.getNumOfRolls() + "/" + maxRolls + ")";
                }
                scoreCardRow.getLabel().setText(rolls);
            }

            String formula = scoreCardRow.getGameRow().getFormula();
            if (!formula.isEmpty() && !scoreCardRow.isRegistered()) {
                scoreCardRow.setPreview(FormulaParser.parseFormula(formula, mDice, scoreCardRow.getGameRow()));
            }
            scoreCardRow.enableInput();
        }
    }

    public void register() {
        updateSums();
        setEnabled(false);
    }

    public void setEnabled(boolean aState) {
        mActive = aState;
        String text = (mNumOfRolls == 0) ? "0" : Integer.toString(mNumOfRolls);

        for (ScoreCardRow scoreCardRow : mRows) {
            scoreCardRow.setEnabled(aState);
            if (scoreCardRow.getGameRow().isRollCounter()) {
                scoreCardRow.getLabel().setText(text);
            }
        }
    }

    public void setNumOfRolls(int numOfRolls) {
        this.mNumOfRolls = numOfRolls;
        Integer i;
    }

    public void setPlayOrder(int playOrder) {
        this.mPlayOrder = playOrder;
    }

    public void setPlayer(Player player) {
        mPlayer = player;
        mLabel.setText(mPlayer.getName());
    }

    public void setText() {
        for (ScoreCardRow scoreCardRow : mRows) {
            scoreCardRow.setText();
        }
    }

    public void setVisibleIndicators(boolean visible) {
        for (ScoreCardRow scoreCardRow : mRows) {
            scoreCardRow.setVisibleIndicator(visible);
        }
    }

    public void undo() {
        int undoRow = mRowStack.pop();
        mRows[undoRow].setRegistered(false);
        mRows[undoRow].setValue(0);
        mRows[undoRow].getLabel().setText("");
        decNumOfRolls();
        updateSums();
        setEnabled(true);

        for (ScoreCardRow scoreCardRow : mRows) {
            scoreCardRow.enableHover();
        }
    }

    private void init() {
        GameRows rowsRule = mGameType.getRows();
        mRows = new ScoreCardRow[rowsRule.size()];
        Dimension d = mLabel.getPreferredSize();
        d.width = 90;
        mLabel.setPreferredSize(d);
        mLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mLabel.setOpaque(true);

        for (int i = 0; i < mRows.length; i++) {
            GameRow rowRule = rowsRule.get(i);
            mRows[i] = new ScoreCardRow(mScoreCard, this, rowRule, i);

            if (i == 0) {
                mRows[i].getLabel().setHorizontalAlignment(SwingConstants.CENTER);
            }
        }
    }

    private void setRollCounterLabel() {
        if (mActive) {
        } else {
        }
    }

    private void updateSums() {
        for (ScoreCardRow scoreCardRow : mRows) {
            if (scoreCardRow.getGameRow().getSumSet() != null) {

                if (scoreCardRow.getGameRow().isBonus()) {
                    int sum = 0;

                    for (Integer row : scoreCardRow.getGameRow().getSumSet()) {
                        sum += mRows[row].getValue();
                    }

                    if (sum >= scoreCardRow.getGameRow().getLim()) {
                        int bonus = scoreCardRow.getGameRow().getMax();
                        scoreCardRow.getLabel().setText(Integer.toString(bonus));
                        scoreCardRow.setValue(bonus);
                    }
                }

                if (scoreCardRow.getGameRow().isSum()) {
                    int sum = 0;

                    for (Integer row : scoreCardRow.getGameRow().getSumSet()) {
                        sum += mRows[row].getValue();
                    }

                    if (scoreCardRow.getRow() == mGameType.getResultRow()) {
                        scoreCardRow.setValue(sum);
                        mCurrentScore = sum;
                    }

                    if (!scoreCardRow.getGameRow().isBonus()) {
                        scoreCardRow.getLabel().setText(Integer.toString(sum));
                    }

                }
            }
        }
    }
}
