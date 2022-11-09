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

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSlider;
import org.openide.awt.Actions;
import se.trixon.almond.util.Dict;
import se.trixon.almond.util.PrefsHelper;
import se.trixon.almond.util.swing.DelayedResetRunner;
import se.trixon.almond.util.swing.SwingHelper;

public final class MainTopComponent extends JFrame {

    private final Options mOptions = Options.getInstance();
    private final ThemeManager mThemeManager = ThemeManager.getInstance();
    private final Yaya mYaya = Yaya.getInstance();

    public MainTopComponent() {
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

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        popupMenu = new javax.swing.JPopupMenu();
        newMenuItem = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        scorecardMenu = new javax.swing.JMenu();
        colorsMenu = new javax.swing.JMenu();
        limCheckBoxMenuItem = new javax.swing.JCheckBoxMenuItem();
        maxCheckBoxMenuItem = new javax.swing.JCheckBoxMenuItem();
        indicatorCheckBoxMenuItem = new javax.swing.JCheckBoxMenuItem();
        diceMenu = new javax.swing.JMenu();
        reverseDiceDirectionCheckBoxMenuItem = new javax.swing.JCheckBoxMenuItem();
        fullscreenCheckBoxMenuItem = new javax.swing.JCheckBoxMenuItem();
        optionsMenuItem = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        helpMenuItem = new javax.swing.JMenuItem();
        aboutMenuItem = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        quitMenuItem = new javax.swing.JMenuItem();
        mainPanel = new javax.swing.JPanel();

        popupMenu.add(newMenuItem);
        popupMenu.add(jSeparator1);

        org.openide.awt.Mnemonics.setLocalizedText(scorecardMenu, org.openide.util.NbBundle.getMessage(MainTopComponent.class, "MainTopComponent.scorecardMenu.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(colorsMenu, org.openide.util.NbBundle.getMessage(MainTopComponent.class, "MainTopComponent.colorsMenu.text")); // NOI18N
        scorecardMenu.add(colorsMenu);

        limCheckBoxMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F5, 0));
        limCheckBoxMenuItem.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(limCheckBoxMenuItem, org.openide.util.NbBundle.getMessage(MainTopComponent.class, "MainTopComponent.limCheckBoxMenuItem.text")); // NOI18N
        limCheckBoxMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                limCheckBoxMenuItemActionPerformed(evt);
            }
        });
        scorecardMenu.add(limCheckBoxMenuItem);

        maxCheckBoxMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F6, 0));
        maxCheckBoxMenuItem.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(maxCheckBoxMenuItem, org.openide.util.NbBundle.getMessage(MainTopComponent.class, "MainTopComponent.maxCheckBoxMenuItem.text")); // NOI18N
        maxCheckBoxMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                maxCheckBoxMenuItemActionPerformed(evt);
            }
        });
        scorecardMenu.add(maxCheckBoxMenuItem);

        indicatorCheckBoxMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F7, 0));
        indicatorCheckBoxMenuItem.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(indicatorCheckBoxMenuItem, org.openide.util.NbBundle.getMessage(MainTopComponent.class, "MainTopComponent.indicatorCheckBoxMenuItem.text")); // NOI18N
        indicatorCheckBoxMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                indicatorCheckBoxMenuItemActionPerformed(evt);
            }
        });
        scorecardMenu.add(indicatorCheckBoxMenuItem);

        popupMenu.add(scorecardMenu);

        org.openide.awt.Mnemonics.setLocalizedText(diceMenu, org.openide.util.NbBundle.getMessage(MainTopComponent.class, "MainTopComponent.diceMenu.text")); // NOI18N

        reverseDiceDirectionCheckBoxMenuItem.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(reverseDiceDirectionCheckBoxMenuItem, org.openide.util.NbBundle.getMessage(MainTopComponent.class, "MainTopComponent.reverseDiceDirectionCheckBoxMenuItem.text")); // NOI18N
        reverseDiceDirectionCheckBoxMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reverseDiceDirectionCheckBoxMenuItemActionPerformed(evt);
            }
        });
        diceMenu.add(reverseDiceDirectionCheckBoxMenuItem);

        popupMenu.add(diceMenu);

        fullscreenCheckBoxMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F11, 0));
        fullscreenCheckBoxMenuItem.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(fullscreenCheckBoxMenuItem, org.openide.util.NbBundle.getMessage(MainTopComponent.class, "MainTopComponent.fullscreenCheckBoxMenuItem.text")); // NOI18N
        fullscreenCheckBoxMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
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

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(org.openide.util.NbBundle.getMessage(MainTopComponent.class, "MainTopComponent.title")); // NOI18N
        setMinimumSize(new java.awt.Dimension(100, 50));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        mainPanel.setLayout(new java.awt.GridLayout(1, 0));
        getContentPane().add(mainPanel, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void indicatorCheckBoxMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_indicatorCheckBoxMenuItemActionPerformed
        Actions.forID("Yaya", "se.trixon.yaya.actions.ShowIndicatorsAction").actionPerformed(evt);
    }//GEN-LAST:event_indicatorCheckBoxMenuItemActionPerformed

    private void fullscreenCheckBoxMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fullscreenCheckBoxMenuItemActionPerformed
        Actions.forID("Yaya", "se.trixon.yaya.actions.FullscreenAction").actionPerformed(evt);
    }//GEN-LAST:event_fullscreenCheckBoxMenuItemActionPerformed

    private void limCheckBoxMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_limCheckBoxMenuItemActionPerformed
        Actions.forID("Yaya", "se.trixon.yaya.actions.ShowLimAction").actionPerformed(evt);
    }//GEN-LAST:event_limCheckBoxMenuItemActionPerformed

    private void maxCheckBoxMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_maxCheckBoxMenuItemActionPerformed
        Actions.forID("Yaya", "se.trixon.yaya.actions.ShowMaxAction").actionPerformed(evt);
    }//GEN-LAST:event_maxCheckBoxMenuItemActionPerformed

    private void reverseDiceDirectionCheckBoxMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reverseDiceDirectionCheckBoxMenuItemActionPerformed
        mOptions.setReverseDirection(((JCheckBoxMenuItem) evt.getSource()).isSelected());
    }//GEN-LAST:event_reverseDiceDirectionCheckBoxMenuItemActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        SwingHelper.frameStateSave(mOptions.getPreferences(), this);
    }//GEN-LAST:event_formWindowClosing

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem aboutMenuItem;
    private javax.swing.JMenu colorsMenu;
    private javax.swing.JMenu diceMenu;
    private javax.swing.JCheckBoxMenuItem fullscreenCheckBoxMenuItem;
    private javax.swing.JMenuItem helpMenuItem;
    private javax.swing.JCheckBoxMenuItem indicatorCheckBoxMenuItem;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JCheckBoxMenuItem limCheckBoxMenuItem;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JCheckBoxMenuItem maxCheckBoxMenuItem;
    private javax.swing.JMenuItem newMenuItem;
    private javax.swing.JMenuItem optionsMenuItem;
    private javax.swing.JPopupMenu popupMenu;
    private javax.swing.JMenuItem quitMenuItem;
    private javax.swing.JCheckBoxMenuItem reverseDiceDirectionCheckBoxMenuItem;
    private javax.swing.JMenu scorecardMenu;
    // End of variables declaration//GEN-END:variables

    void writeProperties(java.util.Properties p) {
        // better to version settings since initial version as advocated at
        // http://wiki.apidesign.org/wiki/PropertyFiles
        p.setProperty("version", "1.0");
        // TODO store your settings
    }

    void readProperties(java.util.Properties p) {
        String version = p.getProperty("version");
        // TODO read your settings according to their version
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
