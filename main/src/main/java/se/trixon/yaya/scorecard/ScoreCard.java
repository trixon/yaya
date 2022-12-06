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
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import org.apache.commons.lang3.StringUtils;
import se.trixon.almond.util.CircularInt;
import se.trixon.almond.util.Dict;
import se.trixon.almond.util.GlobalState;
import se.trixon.almond.util.icons.material.swing.MaterialIcon;
import se.trixon.almond.util.swing.SwingHelper;
import se.trixon.yaya.GameOverDialog;
import se.trixon.yaya.GameOverItem;
import se.trixon.yaya.Options;
import se.trixon.yaya.ThemeManager;
import se.trixon.yaya.scorecard.rules.Rule;
import se.trixon.yaya.scorecard.rules.RuleManager;
import se.trixon.yaya.themes.Theme;

/**
 *
 * @author Patrik Karlström
 */
public class ScoreCard {

    private int mActivePlayer;
    private CircularInt mCurrentPlayer;
    private final JPanel mFillerPanel = new JPanel();
    private final GameOverDialog mGameOverDialog = GameOverDialog.getInstance();
    private JLabel mGameTitleLabel;
    private final GlobalState mGlobalState;
    private Header mHeader;
    private final int mNumOfPlayers;
    private int mNumOfRolls;
    private int mNumOfRows;
    private final Options mOptions = Options.getInstance();
    private final JPanel mPanel = new JPanel();
    private final ArrayList<PlayerColumn> mPlayerColumns = new ArrayList<>();
    private ArrayList<PlayerColumn> mPlayerPositions;
    private boolean mRegisterable;
    private final Rule mRule;
    private final RuleManager mRuleManager = RuleManager.getInstance();
    private final JPanel mScoreCardPanel = new JPanel();
    private boolean mShowIndicators;
    private Theme mTheme;
    private final ThemeManager mThemeManager = ThemeManager.getInstance();
    private AbstractAction mUndoAction;
    private JButton mUndoButton;
    private JPanel mUndoPanel;

    public ScoreCard(GlobalState globalState) {
        mGlobalState = globalState;
        mNumOfPlayers = mOptions.getNumOfPlayers();
        mRule = mRuleManager.getRule(mOptions.getRuleId());
        init();
    }

    public Header getHeader() {
        return mHeader;
    }

    public int getNumOfRolls() {
        return mNumOfRolls;
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
        mScoreCardPanel.revalidate();
        mScoreCardPanel.repaint();
    }

    public void parseDice(ArrayList<Integer> diceValues) {
        setEnabledRegister(true);
        mNumOfRolls++;
        getActivePlayerColumn().incNumOfRolls();
        getActivePlayerColumn().parse(diceValues);
        getActivePlayerColumn().setVisibleIndicators(mOptions.isShowIndicators());
        mScoreCardPanel.revalidate();
        mScoreCardPanel.repaint();
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
                mGlobalState.put(ScoreCardEvent.class.getName(), ScoreCardEvent.GAME_OVER);
                gameOver();
            } else {
                mUndoAction.setEnabled(true);
                mGlobalState.put(ScoreCardEvent.class.getName(), ScoreCardEvent.REGISTER);

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

        mGlobalState.put(ScoreCardEvent.class.getName(), ScoreCardEvent.UNDO);
    }

    private void applyColors() {
        mTheme = mThemeManager.getTheme();

        mPanel.setOpaque(mTheme.isOpaqueWindow());
        mScoreCardPanel.setOpaque(mTheme.isOpaqueScorecard());
        mScoreCardPanel.setBackground(mTheme.getBgScorecard());
        mPanel.setBackground(mTheme.getBgWindow());
        mUndoPanel.setBackground(mTheme.getBgHeaderRow());
        mGameTitleLabel.setForeground(mTheme.getFgHeaderRow());
        mFillerPanel.setBackground(mTheme.getBgScorecardFiller());

        applyUndoButtonStyle();
        mHeader.applyColors();

        for (int i = 0; i < mPlayerColumns.size(); i++) {
            var playerLabel = mPlayerColumns.get(i).getLabel();
            playerLabel.setBackground(mTheme.getBgHeaderRow());
            playerLabel.setForeground(mTheme.getFgHeaderRow());

            for (int j = 0; j < mNumOfRows; j++) {
                var row = mPlayerColumns.get(i).getRows()[j];
                var gameRow = row.getGameCell();
                boolean sum = gameRow.isSum() || gameRow.isBonus();
                var colorBG = sum ? mTheme.getBgHeaderSum() : mTheme.getBgScoreCell();
                var colorFG = sum ? mTheme.getFgHeaderSum() : mTheme.getFgScoreCell();

                var label = row.getLabel();
                label.setBackground(colorBG);
                label.setForeground(colorFG);
                row.setCurrentBackgroundColor(colorBG);
            }
        }

        setVisibleIndicators(mOptions.isShowIndicators());

        mPanel.revalidate();
        mPanel.repaint();
    }

    private void applyFontSize(Container container, float size) {
        for (var component : container.getComponents()) {
            if (component instanceof JLabel label) {
                label.setFont(label.getFont().deriveFont(size));
                var d = label.getPreferredSize();
                d.height = (int) (size * 1.2);
                d.width = 1;
                label.setMinimumSize(d);
            } else if (component instanceof Container subContainer) {
                applyFontSize(subContainer, size);
            }
        }
    }

    private void applyUndoButtonStyle() {
        var imageIcon = MaterialIcon._Content.UNDO.getImageIcon(mOptions.getScaledFontSize(), mTheme.getIconUndo());
        mUndoButton.setIcon(imageIcon);
    }

    private void gameOver() {
        int stackSize = mPlayerColumns.get(0).getRowStack().size();
        int cellCount = stackSize * mPlayerColumns.size();
        long totalTime = 200L;
        long cellDelay = totalTime / cellCount;

        new Thread(() -> {
            for (int i = 0; i < stackSize; i++) {
                var ii = i;
                for (var playerColumn : mPlayerColumns) {
                    SwingHelper.runAndWait((long) (cellDelay * 1), () -> {
                        playerColumn.getRows()[playerColumn.getRowStack().get(ii)].getLabel().setVisible(false);
                        mScoreCardPanel.revalidate();
                        mScoreCardPanel.repaint();
                    });
                }
            }

            for (int i = 0; i < stackSize; i++) {
                var ii = i;
                for (var playerColumn : mPlayerColumns) {
                    SwingHelper.runAndWait((long) (cellDelay * 2), () -> {
                        playerColumn.getRows()[playerColumn.getRowStack().get(ii)].getLabel().setVisible(true);
                        mScoreCardPanel.revalidate();
                        mScoreCardPanel.repaint();
                    });
                }
            }

            var gameOverItems = new ArrayList<GameOverItem>();
            for (var playerColumn : mPlayerColumns) {
                var gameOverItem = new GameOverItem(playerColumn.getPlayer(), playerColumn.getCurrentScore());
                gameOverItems.add(gameOverItem);
            }

            mGameOverDialog.display(gameOverItems);
        }).start();
    }

    private PlayerColumn getActivePlayerColumn() {
        return mPlayerColumns.get(mActivePlayer);
    }

    private void init() {
        mPlayerColumns.clear();
        mHeader = new Header(this, mRule);
        initActions();
        mNumOfRows = mRule.getGameColumn().size();

        initLayout();
        applyColors();
        applyFontSize(mPanel, mOptions.getScaledFontSize());

        mOptions.getPreferences().addPreferenceChangeListener(pce -> {
            if (pce.getKey().equalsIgnoreCase(Options.KEY_SHOW_INDICATORS)) {
                setVisibleIndicators(mOptions.isShowIndicators());
            } else if (StringUtils.equalsAny(pce.getKey(), Options.KEY_THEME)) {
                applyColors();
            } else if (pce.getKey().equalsIgnoreCase(Options.KEY_FONT_SIZE)) {
                applyFontSize(mPanel, mOptions.getScaledFontSize());
                applyUndoButtonStyle();
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
        mUndoButton.setContentAreaFilled(false);
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

        var layout = new GridBagLayout();
        mScoreCardPanel.setLayout(layout);

        var constraints = new GridBagConstraints();
        var headerInsets = new Insets(1, 0, 0, 0);
        var zeroInsets = new Insets(0, 0, 0, 0);
        var cellInsets = new Insets(1, 1, 0, 0);
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.anchor = GridBagConstraints.LINE_START;
        constraints.gridwidth = 3;
        constraints.fill = GridBagConstraints.BOTH;

        mUndoPanel = new JPanel(new BorderLayout());
        mGameTitleLabel = new JLabel(mRule.getTitle());
        mGameTitleLabel.setPreferredSize(new Dimension(200 / 12 * mOptions.getScaledFontSize(), 1));
        mGameTitleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        mUndoPanel.add(mUndoButton, BorderLayout.WEST);
        mUndoPanel.add(mGameTitleLabel, BorderLayout.CENTER);

        layout.setConstraints(mUndoPanel, constraints);
        mScoreCardPanel.add(mUndoPanel);

        constraints.gridwidth = 1;
        constraints.anchor = GridBagConstraints.LINE_START;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.insets = headerInsets;

        for (int i = 0; i < mNumOfRows; i++) {
            constraints.gridy = i + 1;

            constraints.gridx = 0;
            constraints.weightx = 1.0;
            var titleLabel = mHeader.getTitleColumn()[i].getLabel();
            layout.setConstraints(titleLabel, constraints);
            mScoreCardPanel.add(titleLabel);
            constraints.weightx = 0.0;

            constraints.gridx = 1;
            var limLabel = mHeader.getLimColumn()[i].getLabel();
            layout.setConstraints(limLabel, constraints);
            mScoreCardPanel.add(limLabel);

            constraints.gridx = 2;
            var maxLabel = mHeader.getMaxColumn()[i].getLabel();
            layout.setConstraints(maxLabel, constraints);
            mScoreCardPanel.add(maxLabel);
        }

        int startGridX = 3;

        constraints = new GridBagConstraints();
        for (int i = 0; i < mNumOfPlayers; i++) {
            mPlayerColumns.add(new PlayerColumn(this, i, mRule));
            Cell[] column = mPlayerColumns.get(i).getRows();

            constraints.gridx = startGridX + i;
            constraints.anchor = GridBagConstraints.LINE_START;
            constraints.fill = GridBagConstraints.BOTH;
            constraints.gridy = 0;
            constraints.insets = zeroInsets;

            var playerLabel = mPlayerColumns.get(i).getLabel();
            layout.setConstraints(playerLabel, constraints);
            mScoreCardPanel.add(playerLabel);

            constraints.insets = cellInsets;
            for (int j = 0; j < mNumOfRows; j++) {
                constraints.gridy = j + 1;
                var label = column[j].getLabel();
                layout.setConstraints(label, constraints);
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
//        mPlayerPositions = (ArrayList<PlayerColumn>) mPlayerColumns.clone();
//        var pcc = new PlayerColumnComparator(PlayerColumnComparator.DESCENDING);
//        Collections.sort(mPlayerPositions, pcc);
//
//        float reducer = 0.F;
//        var font = mHeader.getRows()[mRule.getResultRow()].getLabel().getFont();
//
//        for (var playerColumn : mPlayerPositions) {
//            var label = playerColumn.getRows()[mRule.getResultRow()].getLabel();
//            label.setFont(font.deriveFont((16.0F - reducer)));
//            reducer += 1.0;
//        }
    }
}
