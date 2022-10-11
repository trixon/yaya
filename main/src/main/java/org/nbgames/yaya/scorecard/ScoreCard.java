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

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.Collections;
import java.util.LinkedList;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.nbgames.yaya.Options;
import org.nbgames.yaya.gamedef.GameTypeLoader;
import org.nbgames.yaya.gamedef.GameType;
import org.nbgames.yaya.scorecard.ScoreCardObservable.ScoreCardEvent;
import se.trixon.almond.util.CircularInt;
import se.trixon.almond.util.Dict;
import se.trixon.almond.util.GraphicsHelper;

/**
 *
 * @author Patrik Karlström
 */
public class ScoreCard {

    private int mActivePlayer;
    private final JPanel mBasePanel = new JPanel();
    private CircularInt mCurrentPlayer;
    private final GameTypeLoader mGameDef = GameTypeLoader.getInstance();
    private final GameType mGameType;
    private HeaderColumn mHeaderColumn;
    private int mNumOfPlayers;
    private int mNumOfRolls;
    private int mNumOfRows;
    private final ScoreCardObservable mObservable = new ScoreCardObservable();
    private final Options mOptions = Options.getInstance();
    private final JPanel mPanel = new JPanel();
    private LinkedList<PlayerColumn> mPlayerPositions;
    private LinkedList<PlayerColumn> mPlayers = new LinkedList<>();
    private boolean mRegisterable;
    private boolean mShowIndicators;
    private AbstractAction mUndoAction;
    private JButton mUndoButton;

    public ScoreCard() {
        mNumOfPlayers = mOptions.getNumOfPlayers();
        mGameType = mGameDef.getType(mOptions.getGameTypeId());
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

        for (PlayerColumn playerColumn : mPlayers) {
            playerColumn.newGame();
            playerColumn.setPlayer(mOptions.getPlayers()[i]);
            i++;
        }

        mPlayers.get(mActivePlayer).setEnabled(true);
    }

    public void newRoll() {
        mPlayers.get(mActivePlayer).clearPreview();
    }

    public void parseDice(LinkedList<Integer> values) {
        setEnabledRegister(true);
        mNumOfRolls++;
        mPlayers.get(mActivePlayer).incNumOfRolls();
        mPlayers.get(mActivePlayer).parse(values);

        mPlayers.get(mActivePlayer).setVisibleIndicators(mOptions.isShowingIndicators());
    }

    public void setEnabledRegister(boolean enabled) {
        mRegisterable = enabled;
    }

    public void setEnabledUndo(boolean enabled) {
        mUndoAction.setEnabled(enabled);
    }

    public void setVisibleIndicators(boolean visible) {
        mShowIndicators = visible;
        mPlayers.get(mActivePlayer).setVisibleIndicators(visible);
    }

    private void actionPerformedUndo() {
        mRegisterable = true;
        mPlayers.get(mActivePlayer).setEnabled(false);
        mActivePlayer = mCurrentPlayer.dec();
        mPlayers.get(mActivePlayer).undo();

        mObservable.notify(ScoreCardEvent.UNDO);
    }

    private void applyColors() {
        mPanel.setBackground(mOptions.getColor(Options.ColorItem.SCORECARD));
        mBasePanel.setBackground(mOptions.getColor(Options.ColorItem.BACKGROUND));
        Color color;

        for (int i = 0; i < mNumOfRows; i++) {

            if (mHeaderColumn.getRows()[i].getGameRow().isSum() || mHeaderColumn.getRows()[i].getGameRow().isBonus()) {
                color = mOptions.getColor(Options.ColorItem.SUM);
            } else {
                color = mOptions.getColor(Options.ColorItem.HEADER);
            }

            mHeaderColumn.getRows()[i].getLabel().setBackground(color);
            mHeaderColumn.getHiScoreColumn()[i].getLabel().setBackground(color);
            mHeaderColumn.getMaxColumn()[i].getLabel().setBackground(color);
        }

        for (int i = 0; i < mPlayers.size(); i++) {
            for (int j = 0; j < mNumOfRows; j++) {
                if (mPlayers.get(i).getRows()[j].getGameRow().isSum() || mHeaderColumn.getRows()[j].getGameRow().isBonus()) {
                    color = mOptions.getColor(Options.ColorItem.SUM);
                } else {
                    color = mOptions.getColor(Options.ColorItem.ROW);
                }
                mPlayers.get(i).getRows()[j].getLabel().setBackground(color);
                mPlayers.get(i).getRows()[j].getLabel().setCurrentBackgroundColor(color);
            }
        }

        setVisibleIndicators(mOptions.isShowingIndicators());
    }

    private void gameOver() {

//        while (!mPlayers.get(mNumOfPlayers - 1).getRowStack().empty()) {
//
//            for (PlayerColumn playerColumn : mPlayers) {
//                int row = playerColumn.getRowStack().pop();
//            }
//        }
//        AGameOver gameOver = new AGameOver();
//        gameOver.getUI().setPreferredSize(new Dimension(300, 280));
//        gameOver.getUI().pack();
//        GridBagLayout gridBagLayout = new GridBagLayout();
//        JPanel gameOverPanel = new JPanel(gridBagLayout);
//
//        GridBagConstraints gridBagConstraints;
//
//        Font font = new Font("Dialog", Font.BOLD, 26);
//        for (PlayerColumn playerColumn : playerPositions) {
//            JLabel label = new JLabel(playerColumn.getName());
//            label.setFont(font);
//
//            gridBagConstraints = new GridBagConstraints();
//            gridBagConstraints.gridx = 0;
//            gridBagConstraints.ipadx = 50;
//            gridBagConstraints.gridy = GridBagConstraints.RELATIVE;
//            gridBagConstraints.anchor = GridBagConstraints.LINE_START;
//
//            gridBagLayout.setConstraints(label, gridBagConstraints);
//            gameOverPanel.add(label, gridBagConstraints);
//
//            label = new JLabel(Integer.toString(playerColumn.getCurrentScore()));
//            label.setFont(font);
//
//            gridBagConstraints = new GridBagConstraints();
//            gridBagConstraints.gridx = 1;
//            gridBagConstraints.gridy = GridBagConstraints.RELATIVE;
//            gridBagConstraints.anchor = GridBagConstraints.LINE_END;
//
//            gridBagLayout.setConstraints(label, gridBagConstraints);
//            gameOverPanel.add(label, gridBagConstraints);
//        }
//
//        gameOver.setPanel(gameOverPanel);
//        gameOver.getUI().centerInOwner();
    }

    private void init() {
        mPlayers.clear();
        mHeaderColumn = new HeaderColumn(this, mGameType);
        initActions();
        mNumOfRows = mGameType.getRows().size();

        initLayout();
        applyColors();

        mOptions.getPreferences().addPreferenceChangeListener(new PreferenceChangeListener() {

            @Override
            public void preferenceChange(PreferenceChangeEvent evt) {
                if (evt.getKey().equalsIgnoreCase(Options.KEY_SHOW_INDICATORS)) {
                    setVisibleIndicators(mOptions.isShowingIndicators());
                }
            }
        });
        mOptions.getPreferencesColors().addPreferenceChangeListener(new PreferenceChangeListener() {

            @Override
            public void preferenceChange(PreferenceChangeEvent evt) {
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

        GridBagLayout gridBagLayout = new GridBagLayout();
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

        Insets insets = new Insets(1, 0, 0, 0);

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
            gridBagLayout.setConstraints(mHeaderColumn.getMaxColumn()[i].getLabel(), gridBagConstraints);
            mPanel.add(mHeaderColumn.getMaxColumn()[i].getLabel());

            gridBagConstraints.gridx = 2;
            gridBagLayout.setConstraints(mHeaderColumn.getHiScoreColumn()[i].getLabel(), gridBagConstraints);
            mPanel.add(mHeaderColumn.getHiScoreColumn()[i].getLabel());
        }

        int startGridX = 3;
        insets.left = 1;
        for (int i = 0; i < mNumOfPlayers; i++) {
            mPlayers.add(new PlayerColumn(this, i, mGameType));
            ScoreCardRow[] column = mPlayers.get(i).getRows();

            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = startGridX + i;
            gridBagConstraints.anchor = GridBagConstraints.LINE_START;
            gridBagConstraints.fill = GridBagConstraints.BOTH;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.insets = insets;

            gridBagLayout.setConstraints(mPlayers.get(i).getLabel(), gridBagConstraints);
            mPanel.add(mPlayers.get(i).getLabel());
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
            for (int i = 0; i < mPlayers.get(mNumOfPlayers - 1).getRows().length; i++) {
                ScoreCardRow scoreCardRow = mPlayers.get(mNumOfPlayers - 1).getRows()[i];

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
        mPlayerPositions = (LinkedList<PlayerColumn>) mPlayers.clone();
        PlayerColumnComparator pcc = new PlayerColumnComparator(PlayerColumnComparator.DESCENDING);
        Collections.sort(mPlayerPositions, pcc);

        float reducer = 0.F;
        Font font = mHeaderColumn.getRows()[mGameType.getResultRow()].getLabel().getFont();

        for (PlayerColumn playerColumn : mPlayerPositions) {
            JLabel label = playerColumn.getRows()[mGameType.getResultRow()].getLabel();
            label.setFont(font.deriveFont((16.0F - reducer)));
            reducer += 1.0;
        }
    }

    void hoverRowEntered(int row) {
        Color activeColor = GraphicsHelper.colorAndMask(mOptions.getColor(Options.ColorItem.HEADER), 0xEEEEEE);

        mHeaderColumn.getRows()[row].getLabel().setBackground(activeColor);
        mHeaderColumn.getHiScoreColumn()[row].getLabel().setBackground(activeColor);
        mHeaderColumn.getMaxColumn()[row].getLabel().setBackground(activeColor);
    }

    void hoverRowExited(int row) {
        mHeaderColumn.getRows()[row].getLabel().setBackground(mOptions.getColor(Options.ColorItem.HEADER));
        mHeaderColumn.getHiScoreColumn()[row].getLabel().setBackground(mOptions.getColor(Options.ColorItem.HEADER));
        mHeaderColumn.getMaxColumn()[row].getLabel().setBackground(mOptions.getColor(Options.ColorItem.HEADER));
    }

    void register() {
        if (mRegisterable) {
            mRegisterable = false;
            mNumOfRolls = 0;
            mPlayers.get(mActivePlayer).register();

            updatePolePosition();

            if (isGameOver()) {
                mObservable.notify(ScoreCardEvent.GAME_OVER);
                gameOver();

            } else {
                mUndoAction.setEnabled(true);
                mObservable.notify(ScoreCardEvent.REGISTER);

                mActivePlayer = mCurrentPlayer.inc();
                mPlayers.get(mActivePlayer).setEnabled(true);
            }
        }
    }
}
