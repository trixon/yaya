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

import javax.swing.SwingConstants;
import se.trixon.yaya.Options;
import se.trixon.yaya.rules.Rule;

/**
 *
 * @author Patrik Karlström
 */
public class HeaderColumn {

    private ScoreCardRow[] mLimColumn;
    private Integer[] mLimValues;
    private ScoreCardRow[] mMaxColumn;
    private Integer[] mMaxValues;
    private int mNumOfRows;
    private final Options mOptions = Options.getInstance();
    private ScoreCardRow[] mRows;
    private final Rule mRule;
    private final ScoreCard mScoreCard;

    public HeaderColumn(ScoreCard scoreCard, Rule rule) {
        mRule = rule;
        mScoreCard = scoreCard;
        init();
    }

    public ScoreCardRow[] getLimColumn() {
        return mLimColumn;
    }

    public ScoreCardRow[] getMaxColumn() {
        return mMaxColumn;
    }

    public ScoreCardRow[] getRows() {
        return mRows;
    }

    public void setVisibleColumnLim(boolean visible) {
        for (var scoreCardRow : mLimColumn) {
            scoreCardRow.getLabel().setVisible(visible);
        }
    }

    public void setVisibleColumnMax(boolean visible) {
        for (var scoreCardRow : mMaxColumn) {
            scoreCardRow.getLabel().setVisible(visible);
        }
    }

    private void init() {
        initRows();
        initLabelTexts();

        mOptions.getPreferences().addPreferenceChangeListener(pce -> {
            if (pce.getKey().equalsIgnoreCase(Options.KEY_SHOW_LIM_COLUMN)) {
                setVisibleColumnLim(mOptions.isShowLimColumn());
            } else if (pce.getKey().equalsIgnoreCase(Options.KEY_SHOW_MAX_COLUMN)) {
                setVisibleColumnMax(mOptions.isShowMaxColumn());
            }
        });
    }

    private void initLabelTexts() {
        for (int i = 0; i < mRule.getRows().size(); i++) {
            mRows[i].getLabel().setText(mRule.getRows().get(i).getTitle());
        }
    }

    private void initRows() {
        boolean showMaxColumn = mOptions.isShowMaxColumn();
        boolean showHiScoreColumn = mOptions.isShowLimColumn();

        var rowsRule = mRule.getRows();
        mLimValues = rowsRule.getLim();
        mMaxValues = rowsRule.getMax();

        mNumOfRows = rowsRule.size();
        mRows = new ScoreCardRow[mNumOfRows];
        mMaxColumn = new ScoreCardRow[mNumOfRows];
        mLimColumn = new ScoreCardRow[mNumOfRows];

        for (int i = 0; i < mNumOfRows; i++) {
            var gameRow = rowsRule.get(i);

            mRows[i] = new ScoreCardRow(mScoreCard, gameRow, i, true);
            mMaxColumn[i] = new ScoreCardRow(mScoreCard, gameRow, i, true);
            mLimColumn[i] = new ScoreCardRow(mScoreCard, gameRow, i, true);

            mRows[i].getLabel().setHorizontalAlignment(SwingConstants.LEADING);
            mMaxColumn[i].getLabel().setText(Integer.toString(mMaxValues[i]));
            mLimColumn[i].getLabel().setText(Integer.toString(mLimValues[i]));

            mMaxColumn[i].getLabel().setVisible(showMaxColumn);
            mLimColumn[i].getLabel().setVisible(showHiScoreColumn);

            String toolTip = "<html><h1>%s</h1></html>".formatted(Integer.toString(mLimValues[i]));
            mRows[i].getLabel().setToolTipText(toolTip);
//            mMaxColumn[i].getLabel().setToolTipText(toolTip);
//            mLimColumn[i].getLabel().setToolTipText(toolTip);
        }

        int row = mRule.getResultRow();
        mRows[row].getLabel().setFont(mRows[row].getLabel().getFont().deriveFont((16.0F)));
    }
}
