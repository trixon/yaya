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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import se.trixon.almond.util.CircularInt;
import se.trixon.almond.util.Dict;
import se.trixon.almond.util.GraphicsHelper;
import se.trixon.yaya.GameOverDialog;
import se.trixon.yaya.GameOverItem;
import se.trixon.yaya.Options;
import se.trixon.yaya.ThemeManager;
import se.trixon.yaya.rules.Rule;
import se.trixon.yaya.rules.RuleManager;
import se.trixon.yaya.scorecard.ScoreCardObservable.ScoreCardEvent;

/**
 *
 * @author Patrik Karlström
 */
public class ScoreCard {

    private int mActivePlayer;
    private final JPanel mBasePanel = new JPanel();
    private CircularInt mCurrentPlayer;
    private final RuleManager mRuleManager = RuleManager.getInstance();
    private HeaderColumn mHeaderColumn;
    private final int mNumOfPlayers;
    private int mNumOfRolls;
    private int mNumOfRows;
    private final ScoreCardObservable mObservable = new ScoreCardObservable();
    private final Options mOptions = Options.getInstance();
    private final JPanel mPanel = new JPanel();
    private final LinkedList<PlayerColumn> mPlayerColumns = new LinkedList<>();
    private LinkedList<PlayerColumn> mPlayerPositions;
    private boolean mRegisterable;
    private final Rule mRule;
    private boolean mShowIndicators;
    private final ThemeManager mThemeManager = ThemeManager.getInstance();
    private AbstractAction mUndoAction;
    private JButton mUndoButton;
    private final GameOverDialog mGameOverDialog = GameOverDialog.getInstance();

    public ScoreCard() {
        mNumOfPlayers = mOptions.getNumOfPlayers();
        mRule = mRuleManager.getRule(mOptions.getRuleId());
        init();
    }

    public JPanel getCard() {
        return mBasePanel;
    }

    public HeaderColumn getHeaderColumn() {
        return mHeaderColumn;
    }

    public int getNumOfRolls() {
        return mNumOfRolls;
    }

    public ScoreCardObservable getObservable() {
        return mObservable;
    }

    public AbstractAction getUndoAction() {
        return mUndoAction;
    }

    public boolean isRollable() {
        return true;
    }

    public boolean isShowingIndicators() {
        return mShowIndicators;
    }

    public void newGame() {
        mRegisterable = false;
        mUndoAction.setEnabled(false);
        mActivePlayer = 0;
        mNumOfRolls = 0;
        mCurrentPlayer = new CircularInt(0, mNumOfPlayers - 1);

        int i = 0;

        for (var playerColumn : mPlayerColumns) {
            playerColumn.newGame();
            playerColumn.setPlayer(mOptions.getPlayers()[i]);
            i++;
        }

        getActivePlayerColumn().setEnabled(true);
    }

    public void newRoll() {
        getActivePlayerColumn().clearPreview();
    }

    public void parseDice(LinkedList<Integer> values) {
        setEnabledRegister(true);
        mNumOfRolls++;
        getActivePlayerColumn().incNumOfRolls();
        getActivePlayerColumn().parse(values);
        getActivePlayerColumn().setVisibleIndicators(mOptions.isShowIndicators());
    }

    public void setEnabledRegister(boolean enabled) {
        mRegisterable = enabled;
    }

    public void setEnabledUndo(boolean enabled) {
        mUndoAction.setEnabled(enabled);
    }

    public void setVisibleIndicators(boolean visible) {
        mShowIndicators = visible;
        getActivePlayerColumn().setVisibleIndicators(visible);
    }

    void hoverRowEntered(int row) {
        var activeColor = GraphicsHelper.colorAndMask(mThemeManager.getHeader(), 0xEEEEEE);

        mHeaderColumn.getRows()[row].getLabel().setBackground(activeColor);
        mHeaderColumn.getLimColumn()[row].getLabel().setBackground(activeColor);
        mHeaderColumn.getMaxColumn()[row].getLabel().setBackground(activeColor);
    }

    void hoverRowExited(int row) {
        mHeaderColumn.getRows()[row].getLabel().setBackground(mThemeManager.getHeader());
        mHeaderColumn.getLimColumn()[row].getLabel().setBackground(mThemeManager.getHeader());
        mHeaderColumn.getMaxColumn()[row].getLabel().setBackground(mThemeManager.getHeader());
    }

    void register() {
        if (mRegisterable) {
            mRegisterable = false;
            mNumOfRolls = 0;
            getActivePlayerColumn().register();

            updatePolePosition();

            if (isGameOver()) {
                mObservable.notify(ScoreCardEvent.GAME_OVER);
                gameOver();
            } else {
                mUndoAction.setEnabled(true);
                mObservable.notify(ScoreCardEvent.REGISTER);

                mActivePlayer = mCurrentPlayer.inc();
                getActivePlayerColumn().setEnabled(true);
            }
        }
    }

    private void actionPerformedUndo() {
        mRegisterable = true;
        getActivePlayerColumn().setEnabled(false);
        mActivePlayer = mCurrentPlayer.dec();
        getActivePlayerColumn().undo();

        mObservable.notify(ScoreCardEvent.UNDO);
    }

    private void applyColors() {
        mPanel.setBackground(mThemeManager.getScorecard());
        mBasePanel.setBackground(mThemeManager.getBackground());
        Color color;

        for (int i = 0; i < mNumOfRows; i++) {
            if (mHeaderColumn.getRows()[i].getGameRow().isSum() || mHeaderColumn.getRows()[i].getGameRow().isBonus()) {
                color = mThemeManager.getSum();
            } else {
                color = mThemeManager.getHeader();
            }

            mHeaderColumn.getRows()[i].getLabel().setBackground(color);
            mHeaderColumn.getLimColumn()[i].getLabel().setBackground(color);
            mHeaderColumn.getMaxColumn()[i].getLabel().setBackground(color);
        }

        for (int i = 0; i < mPlayerColumns.size(); i++) {
            for (int j = 0; j < mNumOfRows; j++) {
                if (mPlayerColumns.get(i).getRows()[j].getGameRow().isSum() || mHeaderColumn.getRows()[j].getGameRow().isBonus()) {
                    color = mThemeManager.getSum();
                } else {
                    color = mThemeManager.getRow();
                }
                mPlayerColumns.get(i).getRows()[j].getLabel().setBackground(color);
                mPlayerColumns.get(i).getRows()[j].getLabel().setCurrentBackgroundColor(color);
            }
        }

        setVisibleIndicators(mOptions.isShowIndicators());
    }

    private void gameOver() {
        //TODO Make scorecard fireworks

        var gameOverItems = new ArrayList<GameOverItem>();
        for (var playerColumn : mPlayerColumns) {
            var gameOverItem = new GameOverItem(playerColumn.getPlayer(), playerColumn.getCurrentScore());
            gameOverItems.add(gameOverItem);
        }

        mGameOverDialog.display(gameOverItems);
    }

    private PlayerColumn getActivePlayerColumn() {
        return mPlayerColumns.get(mActivePlayer);
    }

    private void init() {
        mPlayerColumns.clear();
        mHeaderColumn = new HeaderColumn(this, mRule);
        initActions();
        mNumOfRows = mRule.getRows().size();

        initLayout();
        applyColors();

        mOptions.getPreferences().addPreferenceChangeListener(pce -> {
            if (pce.getKey().equalsIgnoreCase(Options.KEY_SHOW_INDICATORS)) {
                setVisibleIndicators(mOptions.isShowIndicators());
            } else if (pce.getKey().equals(Options.KEY_THEME)) {
                applyColors();
            }
        });
    }

    private void initActions() {
        mUndoAction = new AbstractAction(Dict.UNDO.toString()) {

            @Override
            public void actionPerformed(ActionEvent evt) {
                mUndoAction.setEnabled(false);
                actionPerformedUndo();
            }
        };
        mUndoAction.setEnabled(false);
        mUndoButton = new JButton(mUndoAction);
    }

    private void initLayout() {
        mBasePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mBasePanel.add(mPanel);

        mBasePanel.setOpaque(false);
        mPanel.setOpaque(true);

        var gridBagLayout = new GridBagLayout();
        mPanel.setLayout(gridBagLayout);
        GridBagConstraints gridBagConstraints;

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = GridBagConstraints.LINE_START;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagLayout.setConstraints(mUndoButton, gridBagConstraints);
        mPanel.add(mUndoButton);

        var insets = new Insets(1, 0, 0, 0);

        for (int i = 0; i < mNumOfRows; i++) {
            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.anchor = GridBagConstraints.LINE_START;
            gridBagConstraints.fill = GridBagConstraints.BOTH;
            gridBagConstraints.insets = insets;

            gridBagConstraints.gridy = i + 1;
            gridBagLayout.setConstraints(mHeaderColumn.getRows()[i].getLabel(), gridBagConstraints);
            mPanel.add(mHeaderColumn.getRows()[i].getLabel());

            gridBagConstraints.gridx = 1;
            gridBagLayout.setConstraints(mHeaderColumn.getLimColumn()[i].getLabel(), gridBagConstraints);
            mPanel.add(mHeaderColumn.getLimColumn()[i].getLabel());

            gridBagConstraints.gridx = 2;
            gridBagLayout.setConstraints(mHeaderColumn.getMaxColumn()[i].getLabel(), gridBagConstraints);
            mPanel.add(mHeaderColumn.getMaxColumn()[i].getLabel());
        }

        int startGridX = 3;
        insets.left = 1;

        for (int i = 0; i < mNumOfPlayers; i++) {
            mPlayerColumns.add(new PlayerColumn(this, i, mRule));
            ScoreCardRow[] column = mPlayerColumns.get(i).getRows();

            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = startGridX + i;
            gridBagConstraints.anchor = GridBagConstraints.LINE_START;
            gridBagConstraints.fill = GridBagConstraints.BOTH;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.insets = insets;

            gridBagLayout.setConstraints(mPlayerColumns.get(i).getLabel(), gridBagConstraints);
            mPanel.add(mPlayerColumns.get(i).getLabel());

            for (int j = 0; j < mNumOfRows; j++) {
                gridBagConstraints.gridy = j + 1;

                gridBagLayout.setConstraints(column[j].getLabel(), gridBagConstraints);
                mPanel.add(column[j].getLabel());
            }
        }
    }

    private boolean isGameOver() {
        boolean gameOver;
        if (mActivePlayer == mNumOfPlayers - 1) {
            gameOver = true;

            for (int i = 0; i < mPlayerColumns.get(mNumOfPlayers - 1).getRows().length; i++) {
                var scoreCardRow = mPlayerColumns.get(mNumOfPlayers - 1).getRows()[i];

                if (scoreCardRow.isPlayable() && !scoreCardRow.isRegistered()) {
                    gameOver = false;
                    break;
                }
            }
        } else {
            gameOver = false;
        }

        return gameOver;
    }

    private void updatePolePosition() {
        mPlayerPositions = (LinkedList<PlayerColumn>) mPlayerColumns.clone();
        var pcc = new PlayerColumnComparator(PlayerColumnComparator.DESCENDING);
        Collections.sort(mPlayerPositions, pcc);

        float reducer = 0.F;
        var font = mHeaderColumn.getRows()[mRule.getResultRow()].getLabel().getFont();

        for (var playerColumn : mPlayerPositions) {
            var label = playerColumn.getRows()[mRule.getResultRow()].getLabel();
            label.setFont(font.deriveFont((16.0F - reducer)));
            reducer += 1.0;
        }
    }
}
