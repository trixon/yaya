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
package se.trixon.yaya;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;
import se.trixon.almond.util.GlobalState;
import se.trixon.almond.util.PrefsHelper;
import se.trixon.yaya.dice.DiceBoard;
import se.trixon.yaya.dice.RollEvent;
import se.trixon.yaya.rules.Rule;
import se.trixon.yaya.rules.RuleManager;
import se.trixon.yaya.scorecard.ScoreCard;
import se.trixon.yaya.scorecard.ScoreCardEvent;

/**
 *
 * @author Patrik Karlström
 */
public class YayaPanel extends JPanel {

    private BufferedImage mBackgroundImage;
    private DiceBoard mDiceBoard;
    private final GlobalState mGlobalState;
    private final Options mOptions = Options.getInstance();
    private boolean mRollable = true;
    private Rule mRule;
    private ScoreCard mScoreCard;

    /**
     * Creates new form YayaPanel
     */
    public YayaPanel() {
        mGlobalState = Yaya.getGlobalState();
        init();
        initComponents();
        initListeners();
        initInitialLayout();
        setBackgroundImage(Yaya.getImage("images/wood_panel1.jpg"));
    }

    public BufferedImage getBackgroundImage() {
        return mBackgroundImage;
    }

    public String getGameTitle() {
        return mRule.getTitle();
    }

    public void newGame() {
        PrefsHelper.inc(mOptions.getPreferences(), Options.KEY_GAME_START_COUNTER);

        initGame();
        mScoreCard.newGame();
        mDiceBoard.newTurn();
    }

    @Override
    public void paint(Graphics g) {
        var g2 = (Graphics2D) g;
        if (mBackgroundImage != null) {
            for (int i = 0; i < (getHeight() / mBackgroundImage.getHeight()) + 1; i++) {
                if (getWidth() < mBackgroundImage.getWidth()) {
                    g2.drawImage(mBackgroundImage, 0, i * mBackgroundImage.getHeight(), null);
                } else {
                    g2.drawImage(mBackgroundImage, 0, i * mBackgroundImage.getHeight(), getWidth(), mBackgroundImage.getHeight(), null);
                }
            }
        }

        super.paint(g);
        g2.dispose();
    }

    public void setBackgroundImage(BufferedImage backgroundImage) {
        mBackgroundImage = backgroundImage;
    }

    public void undo() {
        mScoreCard.undo();
    }

    @Override
    public void update(Graphics g) {
        paint(g);
    }

    private void init() {
    }

    private void initComponents() {
        setBackground(new java.awt.Color(204, 255, 153));
        setOpaque(false);
        setLayout(new java.awt.BorderLayout());
    }

    private void initDiceBoard() {
        mDiceBoard.setDiceTofloor(75);
        mDiceBoard.setMaxRollCount(mRule.getNumOfRolls());
        add(mDiceBoard.getPanel(), BorderLayout.SOUTH);
    }

    private void initGame() {
        removeAll();
        mRule = RuleManager.getInstance().getRule(mOptions.getRuleId());
        mDiceBoard = new DiceBoard(mGlobalState, mRule.getNumOfDice());
        mScoreCard = new ScoreCard(mGlobalState);
        initScoreCard();
        initDiceBoard();
    }

    private void initInitialLayout() {
        add(new DiceBoard(mGlobalState, 0).getPanel(), BorderLayout.SOUTH);
    }

    private void initListeners() {
        mGlobalState.addListener(gsce -> {
            RollEvent rollEvent = gsce.getValue();
            switch (rollEvent) {
                case PRE_ROLL -> {
                    mScoreCard.setEnabledUndo(false);
                    mRollable = mScoreCard.isRollable();
                    if (mRollable) {
                        mScoreCard.newRoll();
                        mDiceBoard.roll();
                    }
                }

                case POST_ROLL ->
                    mScoreCard.parseDice(mDiceBoard.getValues());
            }
        }, RollEvent.class.getName());

        mGlobalState.addListener(gsce -> {
            ScoreCardEvent scoreCardEvent = gsce.getValue();
            switch (scoreCardEvent) {
                case GAME_OVER ->
                    mDiceBoard.gameOver();

                case REGISTER ->
                    mDiceBoard.newTurn();

                case UNDO ->
                    mDiceBoard.undo();
            }
        }, ScoreCardEvent.class.getName());
    }

    private void initScoreCard() {
        add(mScoreCard.getPanel(), BorderLayout.CENTER);
    }

}
