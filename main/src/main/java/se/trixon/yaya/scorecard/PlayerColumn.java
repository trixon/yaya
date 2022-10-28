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

import java.util.LinkedList;
import java.util.Stack;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import se.trixon.yaya.Player;
import se.trixon.yaya.rules.Rule;

/**
 *
 * @author Patrik Karlström
 */
public class PlayerColumn {

    private boolean mActive;
    private int mCurrentScore;
    private LinkedList<Integer> mDice;
    private final JLabel mLabel = new JLabel("XXX");
    private int mNumOfRolls;
    private int mPlayOrder;
    private Player mPlayer;
    private final Stack<Integer> mRowStack = new Stack<>();
    private ScoreCardRow[] mRows;
    private final Rule mRule;
    private final ScoreCard mScoreCard;

    public PlayerColumn(ScoreCard scoreCard, int playOrder, Rule rule) {
        mScoreCard = scoreCard;
        mPlayOrder = playOrder;
        mRule = rule;
        init();
    }

    public void clearPreview() {
        for (var row : mRows) {
            row.clearPreview();
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

        for (var row : mRows) {
            row.newGame();
        }

        setRollCounterLabel();
    }

    public void parse(LinkedList<Integer> values) {
        mDice = values;

        for (var row : mRows) {
            if (row.getGameRow().isRollCounter()) {
                String rolls = Integer.toString(getNumOfRolls());
                int maxRolls = mRule.getNumOfRolls();
                if (maxRolls > 0) {
                    rolls += " (" + mScoreCard.getNumOfRolls() + "/" + maxRolls + ")";
                }
                row.getLabel().setText(rolls);
            }

            String formula = row.getGameRow().getFormula();
            if (!formula.isEmpty() && !row.isRegistered()) {
                row.setPreview(FormulaParser.parseFormula(formula, mDice, row.getGameRow()));
            }
            row.enableInput();
        }
    }

    public void register() {
        updateSums();
        setEnabled(false);
    }

    public void setEnabled(boolean aState) {
        mActive = aState;
        String text = (mNumOfRolls == 0) ? "0" : Integer.toString(mNumOfRolls);

        for (var row : mRows) {
            row.setEnabled(aState);
            if (row.getGameRow().isRollCounter()) {
                row.getLabel().setText(text);
            }
        }
    }

    public void setNumOfRolls(int numOfRolls) {
        mNumOfRolls = numOfRolls;
    }

    public void setPlayOrder(int playOrder) {
        mPlayOrder = playOrder;
    }

    public void setPlayer(Player player) {
        mPlayer = player;
        mLabel.setText(mPlayer.getName());
    }

    public void setText() {
        for (var row : mRows) {
            row.setText();
        }
    }

    public void setVisibleIndicators(boolean visible) {
        for (var row : mRows) {
            row.setVisibleIndicator(visible);
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

        for (var row : mRows) {
            row.enableHover();
        }
    }

    private void init() {
        var rowsRule = mRule.getRows();
        mRows = new ScoreCardRow[rowsRule.size()];
        var d = mLabel.getPreferredSize();
        d.width = 90;
        mLabel.setPreferredSize(d);
        mLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mLabel.setOpaque(true);

        for (int i = 0; i < mRows.length; i++) {
            var rowRule = rowsRule.get(i);
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
        for (var row : mRows) {
            if (row.getGameRow().getSumSet() != null) {
                if (row.getGameRow().isBonus()) {
                    int sum = 0;

                    for (var rowValue : row.getGameRow().getSumSet()) {
                        sum += mRows[rowValue].getValue();
                    }

                    if (sum >= row.getGameRow().getLim()) {
                        int bonus = row.getGameRow().getMax();
                        row.getLabel().setText(Integer.toString(bonus));
                        row.setValue(bonus);
                    }
                }

                if (row.getGameRow().isSum()) {
                    int sum = 0;

                    for (var rowValue : row.getGameRow().getSumSet()) {
                        sum += mRows[rowValue].getValue();
                    }

                    if (row.getGameRow().isResult()) {
                        row.setValue(sum);
                        mCurrentScore = sum;
                    }

                    if (!row.getGameRow().isBonus()) {
                        row.getLabel().setText(Integer.toString(sum));
                    }
                }
            }
        }
    }
}
