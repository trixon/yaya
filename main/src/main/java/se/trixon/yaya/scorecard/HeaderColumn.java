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

    private ScoreCardRow[] mHiScoreColumn;
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

    public ScoreCardRow[] getHiScoreColumn() {
        return mHiScoreColumn;
    }

    public ScoreCardRow[] getMaxColumn() {
        return mMaxColumn;
    }

    public ScoreCardRow[] getRows() {
        return mRows;
    }

    public void setVisibleColumnHiscore(boolean visible) {
        for (int i = 0; i < mNumOfRows; i++) {
            mHiScoreColumn[i].getLabel().setVisible(visible);
        }
    }

    public void setVisibleColumnMax(boolean visible) {
        for (int i = 0; i < mNumOfRows; i++) {
            mMaxColumn[i].getLabel().setVisible(visible);
        }
    }

    private void init() {
        initRows();
        initLabelTexts();

        mOptions.getPreferences().addPreferenceChangeListener(pce -> {
            if (pce.getKey().equalsIgnoreCase(Options.KEY_SHOW_TOP_COLUMN)) {
                setVisibleColumnHiscore(mOptions.isShowingTopColumn());
            } else if (pce.getKey().equalsIgnoreCase(Options.KEY_SHOW_MAX_COLUMN)) {
                setVisibleColumnMax(mOptions.isShowingMaxColumn());
            }
        });
    }

    private void initLabelTexts() {
        for (int i = 0; i < mRule.getRows().size(); i++) {
            mRows[i].getLabel().setText(mRule.getRows().get(i).getTitle());
        }
    }

    private void initRows() {
        boolean showMaxColumn = mOptions.isShowingMaxColumn();
        boolean showHiScoreColumn = mOptions.isShowingTopColumn();

        var rowsRule = mRule.getRows();
        mLimValues = rowsRule.getLim();
        mMaxValues = rowsRule.getMax();

        mNumOfRows = rowsRule.size();
        mRows = new ScoreCardRow[mNumOfRows];
        mMaxColumn = new ScoreCardRow[mNumOfRows];
        mHiScoreColumn = new ScoreCardRow[mNumOfRows];

        for (int i = 0; i < mNumOfRows; i++) {
            var gameRow = rowsRule.get(i);

            mRows[i] = new ScoreCardRow(mScoreCard, gameRow, i, true);
            mMaxColumn[i] = new ScoreCardRow(mScoreCard, gameRow, i, true);
            mHiScoreColumn[i] = new ScoreCardRow(mScoreCard, gameRow, i, true);

            mRows[i].getLabel().setHorizontalAlignment(SwingConstants.LEADING);
            mMaxColumn[i].getLabel().setText(Integer.toString(mMaxValues[i]));
            mHiScoreColumn[i].getLabel().setText(Integer.toString(mLimValues[i]));

            mMaxColumn[i].getLabel().setVisible(showMaxColumn);
            mHiScoreColumn[i].getLabel().setVisible(showHiScoreColumn);

//            String toolTip = mRows[i].getGameRow().getToolTip();
//            mRows[i].getLabel().setToolTipText(toolTip);
//            mMaxColumn[i].getLabel().setToolTipText(toolTip);
//            mHiScoreColumn[i].getLabel().setToolTipText(toolTip);
        }

        int row = mRule.getResultRow();
        mRows[row].getLabel().setFont(mRows[row].getLabel().getFont().deriveFont((16.0F)));
    }
}
