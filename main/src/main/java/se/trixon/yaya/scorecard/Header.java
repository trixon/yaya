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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import se.trixon.almond.util.GraphicsHelper;
import se.trixon.yaya.Options;
import se.trixon.yaya.ThemeManager;
import se.trixon.yaya.rules.Rule;

/**
 *
 * @author Patrik Karlström
 */
public class Header extends JPanel {

    private Cell[] mLimColumn;
    private final JPanel mLimPanel = new JPanel();
    private Integer[] mLimValues;
    private Cell[] mMaxColumn;
    private final JPanel mMaxPanel = new JPanel();
    private Integer[] mMaxValues;
    private int mNumOfRows;
    private final Options mOptions = Options.getInstance();
    private final Rule mRule;
    private final ScoreCard mScoreCard;
    private final ThemeManager mThemeManager = ThemeManager.getInstance();
    private Cell[] mTitleColumn;
    private final JPanel mTitlePanel = new JPanel();

    public Header(ScoreCard scoreCard, Rule rule) {
        mRule = rule;
        mScoreCard = scoreCard;
        init();
    }

    void applyColors() {
        for (int i = 0; i < mNumOfRows; i++) {
            boolean sum = mTitleColumn[i].getGameCell().isSum() || mTitleColumn[i].getGameCell().isBonus();
            var color = sum ? mThemeManager.getSum() : mThemeManager.getHeader();

            mTitleColumn[i].getLabel().setBackground(color);
            mLimColumn[i].getLabel().setBackground(color);
            mMaxColumn[i].getLabel().setBackground(color);
        }
    }

    int getMaxCellHeight() {
        int maxHeight = Integer.MIN_VALUE;
        for (int i = 0; i < mTitleColumn.length; i++) {
            maxHeight = Math.max(maxHeight, mTitleColumn[i].getLabel().getPreferredSize().height);
            maxHeight = Math.max(maxHeight, mLimColumn[i].getLabel().getPreferredSize().height);
            maxHeight = Math.max(maxHeight, mMaxColumn[i].getLabel().getPreferredSize().height);
        }

        for (var scoreCardRow : mTitleColumn) {
            var d = scoreCardRow.getLabel().getPreferredSize();
            d.height = maxHeight;
            scoreCardRow.getLabel().setPreferredSize(d);
        }
        for (var scoreCardRow : mLimColumn) {
            var d = scoreCardRow.getLabel().getPreferredSize();
            d.height = maxHeight;
            scoreCardRow.getLabel().setPreferredSize(d);
        }
        for (var scoreCardRow : mMaxColumn) {
            var d = scoreCardRow.getLabel().getPreferredSize();
            d.height = maxHeight;
            scoreCardRow.getLabel().setPreferredSize(d);
        }

        return maxHeight;
    }

    void hoverRowEntered(int row) {
        var color = GraphicsHelper.colorAndMask(mThemeManager.getHeader(), 0xEEEEEE);

        mTitleColumn[row].getLabel().setBackground(color);
        mLimColumn[row].getLabel().setBackground(color);
        mMaxColumn[row].getLabel().setBackground(color);
    }

    void hoverRowExited(int row) {
        mTitleColumn[row].getLabel().setBackground(mThemeManager.getHeader());
        mLimColumn[row].getLabel().setBackground(mThemeManager.getHeader());
        mMaxColumn[row].getLabel().setBackground(mThemeManager.getHeader());
    }

    private void init() {
        mLimPanel.setVisible(mOptions.isShowLimColumn());
        mMaxPanel.setVisible(mOptions.isShowMaxColumn());

        initRows();
        initLayout();

        mOptions.getPreferences().addPreferenceChangeListener(pce -> {
            if (pce.getKey().equalsIgnoreCase(Options.KEY_SHOW_LIM_COLUMN)) {
                mLimPanel.setVisible(mOptions.isShowLimColumn());
            } else if (pce.getKey().equalsIgnoreCase(Options.KEY_SHOW_MAX_COLUMN)) {
                mMaxPanel.setVisible(mOptions.isShowMaxColumn());
            }
        });
    }

    private void initLayout() {
        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));

        var titleGridBagLayout = new GridBagLayout();
        var limGridBagLayout = new GridBagLayout();
        var maxGridBagLayout = new GridBagLayout();

        mTitlePanel.setLayout(titleGridBagLayout);
        mLimPanel.setLayout(limGridBagLayout);
        mMaxPanel.setLayout(maxGridBagLayout);

        var gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.BOTH;

        var insets = new Insets(1, 0, 0, 0);

        for (int i = 0; i < mNumOfRows; i++) {
            gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = i;
            gbc.anchor = GridBagConstraints.LINE_START;
            gbc.fill = GridBagConstraints.BOTH;
            gbc.insets = insets;
            gbc.weightx = 1.0;

            titleGridBagLayout.setConstraints(mTitleColumn[i].getLabel(), gbc);
            mTitlePanel.add(mTitleColumn[i].getLabel());

            limGridBagLayout.setConstraints(mLimColumn[i].getLabel(), gbc);
            mLimPanel.add(mLimColumn[i].getLabel());

            maxGridBagLayout.setConstraints(mMaxColumn[i].getLabel(), gbc);
            mMaxPanel.add(mMaxColumn[i].getLabel());
        }

        add(mTitlePanel);
        add(mLimPanel);
        add(mMaxPanel);
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

        int row = mRule.getResultRow();
        mTitleColumn[row].getLabel().setFont(mTitleColumn[row].getLabel().getFont().deriveFont((16.0F)));
    }
}
