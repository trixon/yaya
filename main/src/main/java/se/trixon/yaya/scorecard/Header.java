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
import se.trixon.almond.util.GraphicsHelper;
import se.trixon.yaya.Options;
import se.trixon.yaya.ThemeManager;
import se.trixon.yaya.rules.Rule;
import se.trixon.yaya.themes.Theme;

/**
 *
 * @author Patrik Karlström
 */
public class Header {

    private Cell[] mLimColumn;
    private Integer[] mLimValues;
    private Cell[] mMaxColumn;
    private Integer[] mMaxValues;
    private int mNumOfRows;
    private final Options mOptions = Options.getInstance();
    private final Rule mRule;
    private final ScoreCard mScoreCard;
    private Theme mTheme;
    private final ThemeManager mThemeManager = ThemeManager.getInstance();
    private Cell[] mTitleColumn;

    public Header(ScoreCard scoreCard, Rule rule) {
        mRule = rule;
        mScoreCard = scoreCard;
        init();
    }

    public Cell[] getLimColumn() {
        return mLimColumn;
    }

    public Cell[] getMaxColumn() {
        return mMaxColumn;
    }

    public Cell[] getTitleColumn() {
        return mTitleColumn;
    }

    void applyColors() {
        mTheme = mThemeManager.getTheme();

        for (int i = 0; i < mNumOfRows; i++) {
            boolean sum = mTitleColumn[i].getGameCell().isSum() || mTitleColumn[i].getGameCell().isBonus();
            var color = sum ? mTheme.getSum() : mTheme.getHeader();

            mTitleColumn[i].getLabel().setBackground(color);
            mLimColumn[i].getLabel().setBackground(color);
            mMaxColumn[i].getLabel().setBackground(color);
        }
    }

    void hoverRowEntered(int row) {
        var color = GraphicsHelper.colorAndMask(mTheme.getHeader(), 0xEEEEEE);

        mTitleColumn[row].getLabel().setBackground(color);
        mLimColumn[row].getLabel().setBackground(color);
        mMaxColumn[row].getLabel().setBackground(color);
    }

    void hoverRowExited(int row) {
        mTitleColumn[row].getLabel().setBackground(mTheme.getHeader());
        mLimColumn[row].getLabel().setBackground(mTheme.getHeader());
        mMaxColumn[row].getLabel().setBackground(mTheme.getHeader());
    }

    private void init() {
        initRows();

        setVisible(mOptions.isShowLimColumn(), mLimColumn);
        setVisible(mOptions.isShowMaxColumn(), mMaxColumn);

        mOptions.getPreferences().addPreferenceChangeListener(pce -> {
            if (pce.getKey().equalsIgnoreCase(Options.KEY_SHOW_LIM_COLUMN)) {
                setVisible(mOptions.isShowLimColumn(), mLimColumn);
            } else if (pce.getKey().equalsIgnoreCase(Options.KEY_SHOW_MAX_COLUMN)) {
                setVisible(mOptions.isShowMaxColumn(), mMaxColumn);
            }
        });
    }

    private void initRows() {
        var gameColumn = mRule.getGameColumn();
        mLimValues = gameColumn.getLim();
        mMaxValues = gameColumn.getMax();

        mNumOfRows = gameColumn.size();
        mTitleColumn = new Cell[mNumOfRows];
        mMaxColumn = new Cell[mNumOfRows];
        mLimColumn = new Cell[mNumOfRows];

        for (int i = 0; i < mNumOfRows; i++) {
            var gameRow = gameColumn.get(i);

            mTitleColumn[i] = new Cell(mScoreCard, gameRow, i, true);
            mMaxColumn[i] = new Cell(mScoreCard, gameRow, i, true);
            mLimColumn[i] = new Cell(mScoreCard, gameRow, i, true);

            var titleLabel = mTitleColumn[i].getLabel();
            titleLabel.setHorizontalAlignment(SwingConstants.LEADING);
            titleLabel.setText(mRule.getGameColumn().get(i).getTitle());

            var maxLabel = mMaxColumn[i].getLabel();
            maxLabel.setText(Integer.toString(mMaxValues[i]));

            var limLabel = mLimColumn[i].getLabel();
            limLabel.setText(Integer.toString(mLimValues[i]));

            String toolTip = "<html><h1>%s</h1></html>".formatted(Integer.toString(mLimValues[i]));
            titleLabel.setToolTipText(toolTip);
            maxLabel.setToolTipText(toolTip);
            limLabel.setToolTipText(toolTip);
        }
    }

    private void setVisible(boolean visible, Cell[] cells) {
        for (var cell : cells) {
            cell.getLabel().setVisible(visible);
        }
    }
}
