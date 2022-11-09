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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

    private JMenuItem aboutMenuItem;
    private JMenu colorsMenu;
    private JMenu diceMenu;
    private JCheckBoxMenuItem fullscreenCheckBoxMenuItem;
    private JMenuItem helpMenuItem;
    private JCheckBoxMenuItem indicatorCheckBoxMenuItem;
    private JPopupMenu.Separator jSeparator1;
    private JPopupMenu.Separator jSeparator2;
    private JPopupMenu.Separator jSeparator3;
    private JCheckBoxMenuItem limCheckBoxMenuItem;
    private final Options mOptions = Options.getInstance();
    private final ThemeManager mThemeManager = ThemeManager.getInstance();
    private final Yaya mYaya = Yaya.getInstance();
    private JPanel mainPanel;
    private JCheckBoxMenuItem maxCheckBoxMenuItem;
    private JMenuItem newMenuItem;
    private JMenuItem optionsMenuItem;
    private JPopupMenu popupMenu;
    private JMenuItem quitMenuItem;
    private JCheckBoxMenuItem reverseDiceDirectionCheckBoxMenuItem;
    private JMenu scorecardMenu;

    public MainFrame() {
        initComponents();

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
                    fullscreenCheckBoxMenuItem.setSelected(mOptions.isFullscreen());
                case Options.KEY_SHOW_INDICATORS ->
                    indicatorCheckBoxMenuItem.setSelected(mOptions.isShowIndicators());
                case Options.KEY_SHOW_LIM_COLUMN ->
                    limCheckBoxMenuItem.setSelected(mOptions.isShowLimColumn());
                case Options.KEY_SHOW_MAX_COLUMN ->
                    maxCheckBoxMenuItem.setSelected(mOptions.isShowMaxColumn());
            }
        });
    }

    private void createUI() {
        mainPanel.removeAll();
        mainPanel.add(mYaya.getPanel());
        mainPanel.repaint();
        mainPanel.revalidate();

        var popupListener = new PopupListener();
        addMouseListener(popupListener);
        mYaya.getPanel().addMouseListener(popupListener);

        Actions.connect(newMenuItem, Actions.forID("Yaya", "se.trixon.yaya.actions.NewGameAction"), true);
        Actions.connect(optionsMenuItem, Actions.forID("Yaya", "se.trixon.yaya.actions.OptionsAction"), true);
        Actions.connect(aboutMenuItem, Actions.forID("Yaya", "se.trixon.yaya.actions.AboutAction"), true);
        Actions.connect(helpMenuItem, Actions.forID("Yaya", "se.trixon.yaya.actions.HelpAction"), true);
        Actions.connect(quitMenuItem, Actions.forID("Yaya", "se.trixon.yaya.actions.QuitAction"), true);

        fullscreenCheckBoxMenuItem.setSelected(mOptions.isFullscreen());
        indicatorCheckBoxMenuItem.setSelected(mOptions.isShowIndicators());
        limCheckBoxMenuItem.setSelected(mOptions.isShowLimColumn());
        maxCheckBoxMenuItem.setSelected(mOptions.isShowMaxColumn());

        reverseDiceDirectionCheckBoxMenuItem.setSelected(mOptions.isReverseDirection());

        var buttonGroup = new ButtonGroup();
        for (var theme : mThemeManager.getItems()) {
            var radioButtonMenuItem = new JRadioButtonMenuItem(theme.getName());
            radioButtonMenuItem.addActionListener(actionEvent -> {
                mThemeManager.setTheme(theme);
                mOptions.setThemeId(theme.getId());
            });
            buttonGroup.add(radioButtonMenuItem);
            colorsMenu.add(radioButtonMenuItem);
            radioButtonMenuItem.setSelected(mOptions.getThemeId().equalsIgnoreCase(theme.getId()));
        }

        var fontMenuItem = new JMenuItem(Dict.SIZE.toString());
        fontMenuItem.setEnabled(false);
        scorecardMenu.add(fontMenuItem);

        var fontSlider = new JSlider(8, 72, mOptions.getFontSize());
        var fontResetRunner = new DelayedResetRunner(50, () -> {
            mOptions.setFontSize(fontSlider.getValue());
        });

        fontSlider.addChangeListener(changeEvent -> {
            fontResetRunner.reset();
        });
        scorecardMenu.add(fontSlider);

        optionsMenuItem.setVisible(false);
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

        popupMenu = new JPopupMenu();
        newMenuItem = new JMenuItem();
        jSeparator1 = new JPopupMenu.Separator();
        scorecardMenu = new JMenu();
        colorsMenu = new JMenu();
        limCheckBoxMenuItem = new JCheckBoxMenuItem();
        maxCheckBoxMenuItem = new JCheckBoxMenuItem();
        indicatorCheckBoxMenuItem = new JCheckBoxMenuItem();
        diceMenu = new JMenu();
        reverseDiceDirectionCheckBoxMenuItem = new JCheckBoxMenuItem();
        fullscreenCheckBoxMenuItem = new JCheckBoxMenuItem();
        optionsMenuItem = new JMenuItem();
        jSeparator2 = new JPopupMenu.Separator();
        helpMenuItem = new JMenuItem();
        aboutMenuItem = new JMenuItem();
        jSeparator3 = new JPopupMenu.Separator();
        quitMenuItem = new JMenuItem();
        mainPanel = new JPanel();

        popupMenu.add(newMenuItem);
        popupMenu.add(jSeparator1);

        org.openide.awt.Mnemonics.setLocalizedText(scorecardMenu, org.openide.util.NbBundle.getMessage(MainFrame.class, "MainFrame.scorecardMenu.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(colorsMenu, org.openide.util.NbBundle.getMessage(MainFrame.class, "MainFrame.colorsMenu.text")); // NOI18N
        scorecardMenu.add(colorsMenu);

        limCheckBoxMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));
        limCheckBoxMenuItem.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(limCheckBoxMenuItem, org.openide.util.NbBundle.getMessage(MainFrame.class, "MainFrame.limCheckBoxMenuItem.text")); // NOI18N
        limCheckBoxMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                limCheckBoxMenuItemActionPerformed(evt);
            }
        });
        scorecardMenu.add(limCheckBoxMenuItem);

        maxCheckBoxMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F6, 0));
        maxCheckBoxMenuItem.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(maxCheckBoxMenuItem, org.openide.util.NbBundle.getMessage(MainFrame.class, "MainFrame.maxCheckBoxMenuItem.text")); // NOI18N
        maxCheckBoxMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                maxCheckBoxMenuItemActionPerformed(evt);
            }
        });
        scorecardMenu.add(maxCheckBoxMenuItem);

        indicatorCheckBoxMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F7, 0));
        indicatorCheckBoxMenuItem.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(indicatorCheckBoxMenuItem, org.openide.util.NbBundle.getMessage(MainFrame.class, "MainFrame.indicatorCheckBoxMenuItem.text")); // NOI18N
        indicatorCheckBoxMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                indicatorCheckBoxMenuItemActionPerformed(evt);
            }
        });
        scorecardMenu.add(indicatorCheckBoxMenuItem);

        popupMenu.add(scorecardMenu);

        org.openide.awt.Mnemonics.setLocalizedText(diceMenu, org.openide.util.NbBundle.getMessage(MainFrame.class, "MainFrame.diceMenu.text")); // NOI18N

        reverseDiceDirectionCheckBoxMenuItem.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(reverseDiceDirectionCheckBoxMenuItem, org.openide.util.NbBundle.getMessage(MainFrame.class, "MainFrame.reverseDiceDirectionCheckBoxMenuItem.text")); // NOI18N
        reverseDiceDirectionCheckBoxMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                reverseDiceDirectionCheckBoxMenuItemActionPerformed(evt);
            }
        });
        diceMenu.add(reverseDiceDirectionCheckBoxMenuItem);

        popupMenu.add(diceMenu);

        fullscreenCheckBoxMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F11, 0));
        fullscreenCheckBoxMenuItem.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(fullscreenCheckBoxMenuItem, org.openide.util.NbBundle.getMessage(MainFrame.class, "MainFrame.fullscreenCheckBoxMenuItem.text")); // NOI18N
        fullscreenCheckBoxMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                fullscreenCheckBoxMenuItemActionPerformed(evt);
            }
        });
        popupMenu.add(fullscreenCheckBoxMenuItem);
        popupMenu.add(optionsMenuItem);
        popupMenu.add(jSeparator2);
        popupMenu.add(helpMenuItem);
        popupMenu.add(aboutMenuItem);
        popupMenu.add(jSeparator3);
        popupMenu.add(quitMenuItem);

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(org.openide.util.NbBundle.getMessage(MainFrame.class, "MainFrame.title")); // NOI18N
        setMinimumSize(new java.awt.Dimension(100, 50));
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        mainPanel.setLayout(new java.awt.GridLayout(1, 0));
        getContentPane().add(mainPanel, java.awt.BorderLayout.CENTER);
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
                popupMenu.show(e.getComponent(), e.getX(), e.getY());
            }
        }
    }
}
