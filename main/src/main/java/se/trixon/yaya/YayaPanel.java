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
import java.awt.event.HierarchyBoundsListener;
import java.awt.event.HierarchyEvent;
import java.awt.image.BufferedImage;
import java.util.Observable;
import java.util.Observer;
import javax.swing.JComponent;
import javax.swing.JPanel;
import se.trixon.yaya.dice.DiceBoard;
import se.trixon.yaya.dice.DiceBoard.RollEvent;
import se.trixon.yaya.rules.Rule;
import se.trixon.yaya.rules.RuleManager;
import se.trixon.yaya.scorecard.ScoreCard;
import se.trixon.yaya.scorecard.ScoreCardObservable.ScoreCardEvent;

/**
 *
 * @author Patrik Karlström
 */
public class YayaPanel extends JPanel implements Observer {

    private DiceBoard mDiceBoard;
    private boolean mRollable = true;
    private ScoreCard mScoreCard;
    private final Options mOptions = Options.getInstance();
    private Rule mRule;
    private BufferedImage mBackgroundImage;

    /**
     * Creates new form YayaPanel
     */
    public YayaPanel() {
        init();
        initComponents();
        initInitialLayout();
        setBackgroundImage(Yaya.getImage("images/wood_panel1.jpg"));
    }

    public void centerInParent() {
        var topPanel = (JComponent) getParent();
        if (topPanel != null && topPanel.getHeight() > 0 && topPanel.getWidth() > 0) {
        }
    }

    public String getGameTitle() {
        return mRule.getTitle();
    }

    @Override
    public void update(Graphics g) {
        paint(g);
    }

    private void init() {
        addHierarchyBoundsListener(new HierarchyBoundsListener() {

            @Override
            public void ancestorMoved(HierarchyEvent evt) {
            }

            @Override
            public void ancestorResized(HierarchyEvent evt) {
                centerInParent();
            }
        });
    }

    @Override
    public void update(Observable observable, Object object) {
        if (object instanceof RollEvent rollEvent) {
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
        } else if (object instanceof ScoreCardEvent scoreCardEvent) {
            switch (scoreCardEvent) {
                case GAME_OVER ->
                    mDiceBoard.gameOver();

                case REGISTER ->
                    mDiceBoard.newTurn();

                case UNDO ->
                    mDiceBoard.undo();
            }
        }
    }

    private void initComponents() {
        setBackground(new java.awt.Color(204, 255, 153));
        setOpaque(false);
        setLayout(new java.awt.BorderLayout());
    }

    public void newGame() {
//        if (numOfPlayers != settings.getNumOfPlayers()) {
//            numOfPlayers = settings.getNumOfPlayers();
//            initRule(settings.getRule());
//        }
        initGame();
        mScoreCard.newGame();
        mDiceBoard.newTurn();

        for (int i = 0; i < mScoreCard.getHeaderColumn().getRows().length; i++) {
            var row = mScoreCard.getHeaderColumn().getRows()[i];
            System.out.println("%d\t%s".formatted(i, row.getLabel().getText()));
        }
    }

    private void initDiceBoard() {
        mDiceBoard.addObserver(this);
        mDiceBoard.setDiceTofloor(1000);
        mDiceBoard.setMaxRollCount(mRule.getNumOfRolls());
        add(mDiceBoard.getPanel(), BorderLayout.SOUTH);
    }

    private void initGame() {
        removeAll();
        mRule = RuleManager.getInstance().getRule(mOptions.getRuleId());
        mDiceBoard = new DiceBoard(mRule.getNumOfDice());
        mScoreCard = new ScoreCard();
        initScoreCard();
        initDiceBoard();
    }

    private void initInitialLayout() {
        add(new DiceBoard(0).getPanel(), BorderLayout.SOUTH);
    }

    private void initScoreCard() {
        mScoreCard.getObservable().addObserver(this);
        add(mScoreCard.getCard(), BorderLayout.CENTER);
    }

    public BufferedImage getBackgroundImage() {
        return mBackgroundImage;
    }

    public void setBackgroundImage(BufferedImage backgroundImage) {
        mBackgroundImage = backgroundImage;
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
}
