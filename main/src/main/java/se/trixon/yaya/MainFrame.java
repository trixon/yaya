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
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSlider;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;
import org.openide.LifecycleManager;
import org.openide.awt.Actions;
import se.trixon.almond.util.Dict;
import se.trixon.almond.util.PrefsHelper;
import se.trixon.almond.util.swing.DelayedResetRunner;
import se.trixon.almond.util.swing.SwingHelper;

public final class MainFrame extends JFrame {

    private JMenuItem mAboutMenuItem;
    private JMenu mColorsMenu;
    private JMenu mDiceMenu;
    private JCheckBoxMenuItem mFullscreenCheckBoxMenuItem;
    private JMenuItem mHelpMenuItem;
    private JCheckBoxMenuItem mIndicatorCheckBoxMenuItem;
    private JCheckBoxMenuItem mLimCheckBoxMenuItem;
    private JPanel mMainPanel;
    private JCheckBoxMenuItem mMaxCheckBoxMenuItem;
    private JMenuItem mNewMenuItem;
    private final Options mOptions = Options.getInstance();
    private JMenuItem mOptionsMenuItem;
    private JPopupMenu mPopupMenu;
    private JMenuItem mQuitMenuItem;
    private JCheckBoxMenuItem mReverseDiceDirectionCheckBoxMenuItem;
    private JMenu mScorecardMenu;
    private final ThemeManager mThemeManager = ThemeManager.getInstance();
    private final Yaya mYaya = Yaya.getInstance();

    public MainFrame() {
        initComponents();
        initMenu();
        initListeners();
        createUI();

        PrefsHelper.inc(mOptions.getPreferences(), Options.KEY_APP_START_COUNTER);
        int gameStartCounter = mOptions.getPreferences().getInt(Options.KEY_GAME_START_COUNTER, 0);
        if (gameStartCounter == 0) {
            SwingHelper.runLaterDelayed(200, () -> {
                Actions.forID("Yaya", "se.trixon.yaya.actions.HelpAction").actionPerformed(null);
                Actions.forID("Yaya", "se.trixon.yaya.actions.NewGameAction").actionPerformed(null);
            });
        } else {
            mYaya.onRequestNewGameStart();
        }

        mOptions.getPreferences().addPreferenceChangeListener(pce -> {
            switch (pce.getKey()) {
                case Options.KEY_FULL_SCREEN ->
                    mFullscreenCheckBoxMenuItem.setSelected(mOptions.isFullscreen());
                case Options.KEY_SHOW_INDICATORS ->
                    mIndicatorCheckBoxMenuItem.setSelected(mOptions.isShowIndicators());
                case Options.KEY_SHOW_LIM_COLUMN ->
                    mLimCheckBoxMenuItem.setSelected(mOptions.isShowLimColumn());
                case Options.KEY_SHOW_MAX_COLUMN ->
                    mMaxCheckBoxMenuItem.setSelected(mOptions.isShowMaxColumn());
            }
        });
    }

    private void createUI() {
        mMainPanel.removeAll();
        mMainPanel.add(mYaya.getPanel());
        mMainPanel.repaint();
        mMainPanel.revalidate();

        var popupListener = new PopupListener();
        addMouseListener(popupListener);
        mYaya.getPanel().addMouseListener(popupListener);

        Actions.connect(mNewMenuItem, Actions.forID("Yaya", "se.trixon.yaya.actions.NewGameAction"), true);
        Actions.connect(mOptionsMenuItem, Actions.forID("Yaya", "se.trixon.yaya.actions.OptionsAction"), true);
        Actions.connect(mAboutMenuItem, Actions.forID("Yaya", "se.trixon.yaya.actions.AboutAction"), true);
        Actions.connect(mHelpMenuItem, Actions.forID("Yaya", "se.trixon.yaya.actions.HelpAction"), true);
        Actions.connect(mQuitMenuItem, Actions.forID("Yaya", "se.trixon.yaya.actions.QuitAction"), true);

        mFullscreenCheckBoxMenuItem.setSelected(mOptions.isFullscreen());
        mIndicatorCheckBoxMenuItem.setSelected(mOptions.isShowIndicators());
        mLimCheckBoxMenuItem.setSelected(mOptions.isShowLimColumn());
        mMaxCheckBoxMenuItem.setSelected(mOptions.isShowMaxColumn());

        mReverseDiceDirectionCheckBoxMenuItem.setSelected(mOptions.isReverseDirection());

        var buttonGroup = new ButtonGroup();
        for (var theme : mThemeManager.getItems()) {
            var radioButtonMenuItem = new JRadioButtonMenuItem(theme.getName());
            radioButtonMenuItem.addActionListener(actionEvent -> {
                mThemeManager.setTheme(theme);
                mOptions.setThemeId(theme.getId());
            });
            buttonGroup.add(radioButtonMenuItem);
            mColorsMenu.add(radioButtonMenuItem);
            radioButtonMenuItem.setSelected(mOptions.getThemeId().equalsIgnoreCase(theme.getId()));
        }

        var fontMenuItem = new JMenuItem(Dict.SIZE.toString());
        fontMenuItem.setEnabled(false);
        mScorecardMenu.add(fontMenuItem);

        var fontSlider = new JSlider(8, 72, mOptions.getFontSize());
        var fontResetRunner = new DelayedResetRunner(50, () -> {
            mOptions.setFontSize(fontSlider.getValue());
        });

        fontSlider.addChangeListener(changeEvent -> {
            fontResetRunner.reset();
        });
        mScorecardMenu.add(fontSlider);

        mOptionsMenuItem.setVisible(false);
    }

    private void formWindowClosing(WindowEvent evt) {
        SwingHelper.frameStateSave(mOptions.getPreferences(), this);
        LifecycleManager.getDefault().exit();
    }

    private void fullscreenCheckBoxMenuItemActionPerformed(ActionEvent evt) {
        Actions.forID("Yaya", "se.trixon.yaya.actions.FullscreenAction").actionPerformed(evt);
    }

    private void indicatorCheckBoxMenuItemActionPerformed(ActionEvent evt) {
        Actions.forID("Yaya", "se.trixon.yaya.actions.ShowIndicatorsAction").actionPerformed(evt);
    }

    private void initComponents() {
        mMainPanel = new JPanel();

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(org.openide.util.NbBundle.getMessage(MainFrame.class, "MainFrame.title")); // NOI18N
        setMinimumSize(new Dimension(100, 50));

        mMainPanel.setLayout(new BorderLayout());
        getContentPane().add(mMainPanel, BorderLayout.CENTER);
    }

    private void initListeners() {
        mLimCheckBoxMenuItem.addActionListener(actionEvent -> {
            limCheckBoxMenuItemActionPerformed(actionEvent);
        });

        mMaxCheckBoxMenuItem.addActionListener(actionEvent -> {
            maxCheckBoxMenuItemActionPerformed(actionEvent);
        });

        mIndicatorCheckBoxMenuItem.addActionListener(actionEvent -> {
            indicatorCheckBoxMenuItemActionPerformed(actionEvent);
        });

        mReverseDiceDirectionCheckBoxMenuItem.addActionListener(actionEvent -> {
            reverseDiceDirectionCheckBoxMenuItemActionPerformed(actionEvent);
        });

        mFullscreenCheckBoxMenuItem.addActionListener(actionEvent -> {
            fullscreenCheckBoxMenuItemActionPerformed(actionEvent);
        });

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
    }

    private void initMenu() {
        mPopupMenu = new JPopupMenu();
        mNewMenuItem = new JMenuItem();
        mScorecardMenu = new JMenu();
        mColorsMenu = new JMenu();
        mLimCheckBoxMenuItem = new JCheckBoxMenuItem();
        mMaxCheckBoxMenuItem = new JCheckBoxMenuItem();
        mIndicatorCheckBoxMenuItem = new JCheckBoxMenuItem();
        mDiceMenu = new JMenu();
        mReverseDiceDirectionCheckBoxMenuItem = new JCheckBoxMenuItem();
        mFullscreenCheckBoxMenuItem = new JCheckBoxMenuItem();
        mOptionsMenuItem = new JMenuItem();
        mHelpMenuItem = new JMenuItem();
        mAboutMenuItem = new JMenuItem();
        mQuitMenuItem = new JMenuItem();

        mPopupMenu.add(mNewMenuItem);
        mPopupMenu.add(new JPopupMenu.Separator());

        org.openide.awt.Mnemonics.setLocalizedText(mScorecardMenu, org.openide.util.NbBundle.getMessage(MainFrame.class, "MainFrame.scorecardMenu.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(mColorsMenu, org.openide.util.NbBundle.getMessage(MainFrame.class, "MainFrame.colorsMenu.text")); // NOI18N
        mScorecardMenu.add(mColorsMenu);

        mLimCheckBoxMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));
        mLimCheckBoxMenuItem.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(mLimCheckBoxMenuItem, org.openide.util.NbBundle.getMessage(MainFrame.class, "MainFrame.limCheckBoxMenuItem.text")); // NOI18N
        mScorecardMenu.add(mLimCheckBoxMenuItem);

        mMaxCheckBoxMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F6, 0));
        mMaxCheckBoxMenuItem.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(mMaxCheckBoxMenuItem, org.openide.util.NbBundle.getMessage(MainFrame.class, "MainFrame.maxCheckBoxMenuItem.text")); // NOI18N
        mScorecardMenu.add(mMaxCheckBoxMenuItem);

        mIndicatorCheckBoxMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F7, 0));
        mIndicatorCheckBoxMenuItem.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(mIndicatorCheckBoxMenuItem, org.openide.util.NbBundle.getMessage(MainFrame.class, "MainFrame.indicatorCheckBoxMenuItem.text")); // NOI18N
        mScorecardMenu.add(mIndicatorCheckBoxMenuItem);

        mPopupMenu.add(mScorecardMenu);

        org.openide.awt.Mnemonics.setLocalizedText(mDiceMenu, org.openide.util.NbBundle.getMessage(MainFrame.class, "MainFrame.diceMenu.text")); // NOI18N

        mReverseDiceDirectionCheckBoxMenuItem.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(mReverseDiceDirectionCheckBoxMenuItem, org.openide.util.NbBundle.getMessage(MainFrame.class, "MainFrame.reverseDiceDirectionCheckBoxMenuItem.text")); // NOI18N
        mDiceMenu.add(mReverseDiceDirectionCheckBoxMenuItem);

        mPopupMenu.add(mDiceMenu);

        mFullscreenCheckBoxMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F11, 0));
        mFullscreenCheckBoxMenuItem.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(mFullscreenCheckBoxMenuItem, org.openide.util.NbBundle.getMessage(MainFrame.class, "MainFrame.fullscreenCheckBoxMenuItem.text")); // NOI18N

        mPopupMenu.add(mFullscreenCheckBoxMenuItem);
        mPopupMenu.add(mOptionsMenuItem);
        mPopupMenu.add(new JPopupMenu.Separator());
        mPopupMenu.add(mHelpMenuItem);
        mPopupMenu.add(mAboutMenuItem);
        mPopupMenu.add(new JPopupMenu.Separator());
        mPopupMenu.add(mQuitMenuItem);
    }

    private void limCheckBoxMenuItemActionPerformed(ActionEvent evt) {
        Actions.forID("Yaya", "se.trixon.yaya.actions.ShowLimAction").actionPerformed(evt);
    }

    private void maxCheckBoxMenuItemActionPerformed(ActionEvent evt) {
        Actions.forID("Yaya", "se.trixon.yaya.actions.ShowMaxAction").actionPerformed(evt);
    }

    private void reverseDiceDirectionCheckBoxMenuItemActionPerformed(ActionEvent evt) {
        mOptions.setReverseDirection(((JCheckBoxMenuItem) evt.getSource()).isSelected());
    }

    class PopupListener extends MouseAdapter {

        @Override
        public void mousePressed(MouseEvent e) {
            maybeShowPopup(e);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            maybeShowPopup(e);
        }

        private void maybeShowPopup(MouseEvent e) {
            if (e.isPopupTrigger()) {
                mPopupMenu.show(e.getComponent(), e.getX(), e.getY());
            }
        }
    }
}
