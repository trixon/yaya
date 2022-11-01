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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.LinkedList;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import org.apache.commons.lang3.StringUtils;
import se.trixon.almond.util.CircularInt;
import se.trixon.almond.util.Dict;
import se.trixon.almond.util.GraphicsHelper;
import se.trixon.almond.util.icons.material.swing.MaterialIcon;
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
    private CircularInt mCurrentPlayer;
    private final JPanel mFillerPanel = new JPanel();
    private final GameOverDialog mGameOverDialog = GameOverDialog.getInstance();
    private Header mHeaderColumn;
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
    private final RuleManager mRuleManager = RuleManager.getInstance();
    private final JPanel mScoreCardPanel = new JPanel();
    private boolean mShowIndicators;
    private final ThemeManager mThemeManager = ThemeManager.getInstance();
    private AbstractAction mUndoAction;
    private JButton mUndoButton;

    public ScoreCard() {
        mNumOfPlayers = mOptions.getNumOfPlayers();
        mRule = mRuleManager.getRule(mOptions.getRuleId());
        init();
    }

    public Header getHeaderColumn() {
        return mHeaderColumn;
    }

    public int getNumOfRolls() {
        return mNumOfRolls;
    }

    public ScoreCardObservable getObservable() {
        return mObservable;
    }

    public JPanel getPanel() {
        return mPanel;
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

    public void undo() {
        if (mUndoAction.isEnabled()) {
            mUndoAction.setEnabled(false);
            actionPerformedUndo();
        }
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
        mScoreCardPanel.setBackground(mThemeManager.getScorecard());
        mPanel.setBackground(mThemeManager.getBackground());
        mFillerPanel.setBackground(GraphicsHelper.colorAddAlpha(Color.BLACK, mOptions.getOpacity()));

        var imageIcon = MaterialIcon._Content.UNDO.getImageIcon(24, mThemeManager.getUndoIcon());
        mUndoButton.setIcon(imageIcon);

        mHeaderColumn.applyColors();

        for (int i = 0; i < mPlayerColumns.size(); i++) {
            for (int j = 0; j < mNumOfRows; j++) {
                var row = mPlayerColumns.get(i).getRows()[j];
                var gameRow = row.getGameCell();
                boolean sum = gameRow.isSum() || gameRow.isBonus();
                var color = sum ? mThemeManager.getSum() : mThemeManager.getRow();

                row.getLabel().setBackground(color);
                row.setCurrentBackgroundColor(color);
            }
        }

        setVisibleIndicators(mOptions.isShowIndicators());

        mPanel.revalidate();
        mPanel.repaint();
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
        mHeaderColumn = new Header(this, mRule);
        initActions();
        mNumOfRows = mRule.getGameColumn().size();

        initLayout();
        applyColors();

        mOptions.getPreferences().addPreferenceChangeListener(pce -> {
            if (pce.getKey().equalsIgnoreCase(Options.KEY_SHOW_INDICATORS)) {
                setVisibleIndicators(mOptions.isShowIndicators());
            } else if (StringUtils.equalsAny(pce.getKey(), Options.KEY_THEME, Options.KEY_OPACITY)) {
                applyColors();
            }
        });
    }

    private void initActions() {
        mUndoAction = new AbstractAction("") {

            @Override
            public void actionPerformed(ActionEvent evt) {
                mUndoAction.setEnabled(false);
                actionPerformedUndo();
            }
        };

        mUndoAction.setEnabled(false);
        mUndoButton = new JButton(mUndoAction);
        mUndoButton.setBorderPainted(false);
        mUndoButton.setToolTipText(Dict.UNDO.toString());
    }

    private void initLayout() {
        mPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        mFillerPanel.setOpaque(true);
        mFillerPanel.setPreferredSize(new Dimension(1, 1000));
        var borderPanel = new JPanel(new BorderLayout());
        borderPanel.setOpaque(false);
        borderPanel.add(mScoreCardPanel, BorderLayout.CENTER);
        borderPanel.add(mFillerPanel, BorderLayout.SOUTH);
        mPanel.add(borderPanel);
        mPanel.setOpaque(false);
        mScoreCardPanel.setOpaque(true);

        var gridBagLayout = new GridBagLayout();
        mScoreCardPanel.setLayout(gridBagLayout);

        var gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.BOTH;

        var panel = new JPanel(new BorderLayout());
        var titleLabel = new JLabel(mRule.getTitle());
        titleLabel.setPreferredSize(new Dimension(250, 1));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        panel.add(mUndoButton, BorderLayout.WEST);
        panel.add(titleLabel, BorderLayout.CENTER);
        gridBagLayout.setConstraints(panel, gbc);
        mScoreCardPanel.add(panel);

        gbc.gridy = 1;
        gbc.gridheight = GridBagConstraints.REMAINDER;
        gridBagLayout.setConstraints(mHeaderColumn, gbc);
        mScoreCardPanel.add(mHeaderColumn);
        int h = mHeaderColumn.getMaxCellHeight();
        var insets = new Insets(1, 1, 0, 0);

        int startGridX = 1;

        for (int i = 0; i < mNumOfPlayers; i++) {
            mPlayerColumns.add(new PlayerColumn(this, i, mRule));
            Cell[] column = mPlayerColumns.get(i).getRows();

            gbc = new GridBagConstraints();
            gbc.gridx = startGridX + i;
            gbc.anchor = GridBagConstraints.LINE_START;
            gbc.fill = GridBagConstraints.BOTH;
            gbc.gridy = 0;
            gbc.insets = insets;

            var playerLabel = mPlayerColumns.get(i).getLabel();
            gridBagLayout.setConstraints(playerLabel, gbc);
            mScoreCardPanel.add(playerLabel);

            for (int j = 0; j < mNumOfRows; j++) {
                gbc.gridy = j + 1;

                var label = column[j].getLabel();
                var d = label.getPreferredSize();
                d.height = h;
                label.setPreferredSize(d);
                gridBagLayout.setConstraints(label, gbc);
                mScoreCardPanel.add(label);
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
//        mPlayerPositions = (LinkedList<PlayerColumn>) mPlayerColumns.clone();
//        var pcc = new PlayerColumnComparator(PlayerColumnComparator.DESCENDING);
//        Collections.sort(mPlayerPositions, pcc);
//
//        float reducer = 0.F;
//        var font = mHeaderColumn.getRows()[mRule.getResultRow()].getLabel().getFont();
//
//        for (var playerColumn : mPlayerPositions) {
//            var label = playerColumn.getRows()[mRule.getResultRow()].getLabel();
//            label.setFont(font.deriveFont((16.0F - reducer)));
//            reducer += 1.0;
//        }
    }
}
