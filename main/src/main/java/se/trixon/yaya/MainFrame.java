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
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.prefs.BackingStoreException;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.openide.LifecycleManager;
import org.openide.awt.Mnemonics;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.util.NbPreferences;
import se.trixon.almond.nbp.core.ModuleHelper;
import se.trixon.almond.util.AlmondAction;
import se.trixon.almond.util.AlmondUI;
import se.trixon.almond.util.Dict;
import se.trixon.almond.util.PomInfo;
import se.trixon.almond.util.PrefsHelper;
import se.trixon.almond.util.SystemHelper;
import se.trixon.almond.util.swing.AboutModel;
import se.trixon.almond.util.swing.DelayedResetRunner;
import se.trixon.almond.util.swing.SwingHelper;
import se.trixon.almond.util.swing.dialogs.HtmlPanel;
import se.trixon.almond.util.swing.dialogs.about.AboutPanel;

public final class MainFrame extends JFrame {

    private JMenuItem mAboutMenuItem;
    private ActionManager mActionManager = ActionManager.getInstance();
    private final AlmondUI mAlmondUI = AlmondUI.getInstance();
    private final ResourceBundle mBundle = NbBundle.getBundle(MainFrame.class);
    private JMenu mColorsMenu;
    private JMenu mDiceMenu;
    private JCheckBoxMenuItem mFullscreenCheckBoxMenuItem;
    private JCheckBoxMenuItem mPlaySoundCheckBoxMenuItem;
    private JMenuItem mHelpMenuItem;
    private JCheckBoxMenuItem mIndicatorCheckBoxMenuItem;
    private JCheckBoxMenuItem mLimCheckBoxMenuItem;
    private JPanel mMainPanel;
    private JCheckBoxMenuItem mMaxCheckBoxMenuItem;
    private final NewGamePanel mNewGamePanel = new NewGamePanel();
    private JMenuItem mNewMenuItem;
    private JCheckBoxMenuItem mNightModeCheckBoxMenuItem;
    private final Options mOptions = Options.getInstance();
    private JPopupMenu mPopupMenu;
    private JMenuItem mQuitMenuItem;
    private JMenuItem mRemovePlayerMenuItem;
    private JCheckBoxMenuItem mReverseDiceDirectionCheckBoxMenuItem;
    private JMenu mScorecardMenu;
    private JMenu mSystemMenu;
    private final ThemeManager mThemeManager = ThemeManager.getInstance();
    private final HashSet<Component> mUIComponents = new HashSet<>();
    private final Yaya mYaya = Yaya.getInstance();

    public MainFrame() {
        createUI();
        loadSettings();
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("logo.png")));

        PrefsHelper.inc(mOptions.getPreferences(), Options.KEY_APP_START_COUNTER);
        int gameStartCounter = mOptions.getPreferences().getInt(Options.KEY_GAME_START_COUNTER, 0);
        if (gameStartCounter == 0) {
            SwingHelper.runLaterDelayed(200, () -> {
                mActionManager.getAction(ActionManager.HELP).actionPerformed(null);
                mActionManager.getAction(ActionManager.NEW).actionPerformed(null);
            });
        } else {
            mYaya.onRequestNewGameStart();
        }
    }

    private void createUI() {
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(org.openide.util.NbBundle.getMessage(MainFrame.class, "MainFrame.title")); // NOI18N
        setMinimumSize(SwingHelper.getUIScaledDim(100, 50));

        mMainPanel = new JPanel(new BorderLayout());
        mMainPanel.add(mYaya.getPanel(), BorderLayout.CENTER);

        getContentPane().add(mMainPanel, BorderLayout.CENTER);

        mUIComponents.add(this);
        mUIComponents.add(mNewGamePanel);

        initMenu();
        initActions();
        initListeners();
    }

    private void displayHelp() {
        var htmlPanel = new HtmlPanel(new Help().getHelp());
        htmlPanel.setPreferredSize(SwingHelper.getUIScaledDim(720, 740));
        htmlPanel.getScrollPane().setBorder(null);
        String[] buttons = {Dict.CLOSE.toString()};
        var defaultBorder = (EmptyBorder) UIManager.get("OptionPane.border");
        UIManager.put("OptionPane.border", new EmptyBorder(0, 0, 0, 0));
        JOptionPane.showOptionDialog(this, htmlPanel, Dict.HELP.toString(), JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, buttons, Dict.CLOSE.toString());
        UIManager.put("OptionPane.border", defaultBorder);
    }

    private void displayNewGame() {
        mNewGamePanel.load();

        String[] buttons = {Dict.CANCEL.toString(), Dict.PLAY.toString()};
        var result = JOptionPane.showOptionDialog(this, mNewGamePanel, Dict.Game.NEW_ROUND.toString(), JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, buttons, Dict.PLAY.toString());
        if (result == 1) {
            mNewGamePanel.save();
            mYaya.onRequestNewGameStart();
        }
    }

    private void displayRemovePlayer() {
        var players = mOptions.getAllPlayers();
        var removedPlayer = (String) JOptionPane.showInputDialog(
                this,
                mBundle.getString("removePlayerInfo"),
                mBundle.getString("removePlayerTitle"),
                JOptionPane.PLAIN_MESSAGE,
                null,
                players,
                "");

        if (StringUtils.isNotBlank(removedPlayer)) {
            var remainingPlayers = ArrayUtils.removeElement(players, removedPlayer);

            if (remainingPlayers.length == 0) {
                mOptions.put(Options.KEY_PLAYERS_ALL, Options.DEFAULT_PLAYERS_ALL);
                mOptions.put(Options.KEY_PLAYERS, Options.DEFAULT_PLAYERS);
            } else {
                mOptions.put(Options.KEY_PLAYERS_ALL, String.join(";", remainingPlayers));

                var defaultFillPlayer = remainingPlayers[0];
                var contenders = mOptions.get(Options.KEY_PLAYERS, Options.DEFAULT_PLAYERS);
                contenders = StringUtils.replace(contenders, removedPlayer, "");
                contenders = StringUtils.replace(contenders, ";;", ";%s;".formatted(defaultFillPlayer));

                if (StringUtils.startsWith(contenders, ";")) {
                    contenders = defaultFillPlayer + contenders;
                }

                if (StringUtils.endsWith(contenders, ";")) {
                    contenders = contenders + defaultFillPlayer;
                }

                mOptions.put(Options.KEY_PLAYERS, contenders);
            }
        }
    }

    private void initActions() {
        //about
        var pomInfo = new PomInfo(MainFrame.class, "se.trixon.yaya", "main");
        var aboutModel = new AboutModel(SystemHelper.getBundle(MainFrame.class, "about"), SystemHelper.getResourceAsImageIcon(MainFrame.class, "logo.png"));
        aboutModel.setAppVersion(pomInfo.getVersion());
        aboutModel.setAppDate(ModuleHelper.getBuildTime(MainFrame.class));
        var aboutPanel = new AboutPanel(aboutModel);
        var action = AboutPanel.getAction(MainFrame.this, aboutPanel);
        getRootPane().getActionMap().put(ActionManager.ABOUT, action);
        mUIComponents.add(aboutPanel);

        mActionManager.init(getRootPane().getActionMap(), getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW));

        mNewMenuItem.setAction(mActionManager.getAction(ActionManager.NEW));
        mFullscreenCheckBoxMenuItem.setAction(mActionManager.getAction(ActionManager.FULLSCREEN));
        mNightModeCheckBoxMenuItem.setAction(mActionManager.getAction(ActionManager.NIGHT_MODE));
        mLimCheckBoxMenuItem.setAction(mActionManager.getAction(ActionManager.SHOW_LIM));
        mMaxCheckBoxMenuItem.setAction(mActionManager.getAction(ActionManager.SHOW_MAX));
        mIndicatorCheckBoxMenuItem.setAction(mActionManager.getAction(ActionManager.SHOW_INDICATORS));
        mPlaySoundCheckBoxMenuItem.setAction(mActionManager.getAction(ActionManager.PLAY_SOUND));
        mAboutMenuItem.setAction(action);
        mHelpMenuItem.setAction(mActionManager.getAction(ActionManager.HELP));
        mQuitMenuItem.setAction(mActionManager.getAction(ActionManager.QUIT));
    }

    private void initListeners() {
        mActionManager.addAppListener((action, actionEvent) -> {
            var actionId = StringUtils.defaultString((String) action.getValue(AlmondAction.ALMOND_KEY), "");
            switch (actionId) {
                case ActionManager.NEW ->
                    displayNewGame();

                case ActionManager.SHOW_LIM ->
                    mOptions.setShowLimColumn(!mOptions.isShowLimColumn());

                case ActionManager.SHOW_MAX ->
                    mOptions.setShowMaxColumn(!mOptions.isShowMaxColumn());

                case ActionManager.SHOW_INDICATORS ->
                    mOptions.setShowIndicators(!mOptions.isShowIndicators());

                case ActionManager.FULLSCREEN -> {
                    mOptions.setFullscreen(!mOptions.isFullscreen());
                    SwingHelper.setFullScreen(mOptions.isFullscreen() ? this : null);
                }

                case ActionManager.PLAY_SOUND -> {
                    mOptions.put(Options.KEY_PLAY_SOUND, !mOptions.is(Options.KEY_PLAY_SOUND, Options.DEFAULT_PLAY_SOUND));
                }

                case ActionManager.NIGHT_MODE -> {
                    String laf;
                    mOptions.setNightMode(!mOptions.isNightMode());
                    if (mOptions.isNightMode()) {
                        laf = "com.formdev.flatlaf.FlatDarkLaf";
                    } else {
                        laf = "com.formdev.flatlaf.FlatLightLaf";
                    }
                    var key = "laf";

                    var preferences = NbPreferences.root().node("laf");
                    preferences.put(key, laf);

                    try {
                        UIManager.setLookAndFeel(laf);
                        for (var component : mUIComponents) {
                            SwingUtilities.updateComponentTreeUI(component);
                        }
                    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }

                case ActionManager.UNDO -> {
                    mYaya.getPanel().undo();
                }

                case ActionManager.HELP -> {
                    displayHelp();
                }

                case ActionManager.QUIT -> {
                    processWindowEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
                }

                default ->
                    System.out.println("Unhandled action: " + actionId);
            }
        });

        mOptions.getPreferences().addPreferenceChangeListener(pce -> {
            switch (pce.getKey()) {
                case Options.KEY_FULL_SCREEN ->
                    mFullscreenCheckBoxMenuItem.setSelected(mOptions.isFullscreen());
                case Options.KEY_NIGHT_MODE ->
                    mNightModeCheckBoxMenuItem.setSelected(mOptions.isNightMode());
                case Options.KEY_SHOW_INDICATORS ->
                    mIndicatorCheckBoxMenuItem.setSelected(mOptions.isShowIndicators());
                case Options.KEY_SHOW_LIM_COLUMN ->
                    mLimCheckBoxMenuItem.setSelected(mOptions.isShowLimColumn());
                case Options.KEY_SHOW_MAX_COLUMN ->
                    mMaxCheckBoxMenuItem.setSelected(mOptions.isShowMaxColumn());
                case Options.KEY_PLAY_SOUND ->
                    mPlaySoundCheckBoxMenuItem.setSelected(mOptions.is(Options.KEY_PLAY_SOUND, Options.DEFAULT_PLAY_SOUND));
            }
        });

        mReverseDiceDirectionCheckBoxMenuItem.addActionListener(actionEvent -> {
            mOptions.setReverseDirection(mReverseDiceDirectionCheckBoxMenuItem.isSelected());
        });

        mRemovePlayerMenuItem.addActionListener(actionEvent -> {
            displayRemovePlayer();
        });

        var popupListener = new PopupListener();
        addMouseListener(popupListener);
        mYaya.getPanel().addMouseListener(popupListener);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent evt) {
                if (!mOptions.isFullscreen()) {
                    SwingHelper.frameStateSave(mOptions.getPreferences(), MainFrame.this);
                    try {
                        NbPreferences.forModule(Options.class).sync();
                    } catch (BackingStoreException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
                MainFrame.this.setVisible(false);
                LifecycleManager.getDefault().exit();
            }
        });

        Yaya.getGlobalState().addListener(gsce -> {
            MouseEvent e = gsce.getValue();
            if (e.isPopupTrigger()) {
                mPopupMenu.show(e.getComponent(), e.getX(), e.getY());
            }
        }, "CellMouseEvent");
    }

    private void initMenu() {
        mPopupMenu = new JPopupMenu();
        mNewMenuItem = new JMenuItem();
        mSystemMenu = new JMenu();
        mFullscreenCheckBoxMenuItem = new JCheckBoxMenuItem();
        mNightModeCheckBoxMenuItem = new JCheckBoxMenuItem();
        mRemovePlayerMenuItem = new JMenuItem(mBundle.getString("removePlayerTitle"));
        mPlaySoundCheckBoxMenuItem = new JCheckBoxMenuItem();

        mScorecardMenu = new JMenu();
        mColorsMenu = new JMenu();
        mLimCheckBoxMenuItem = new JCheckBoxMenuItem();
        mMaxCheckBoxMenuItem = new JCheckBoxMenuItem();
        mIndicatorCheckBoxMenuItem = new JCheckBoxMenuItem();

        mDiceMenu = new JMenu();
        mReverseDiceDirectionCheckBoxMenuItem = new JCheckBoxMenuItem();

        mHelpMenuItem = new JMenuItem();
        mAboutMenuItem = new JMenuItem();
        mQuitMenuItem = new JMenuItem();

        mSystemMenu.setText(Dict.SYSTEM.toString());
        Mnemonics.setLocalizedText(mScorecardMenu, NbBundle.getMessage(MainFrame.class, "MainFrame.scorecardMenu.text")); // NOI18N
        Mnemonics.setLocalizedText(mColorsMenu, NbBundle.getMessage(MainFrame.class, "MainFrame.colorsMenu.text")); // NOI18N
        Mnemonics.setLocalizedText(mDiceMenu, NbBundle.getMessage(MainFrame.class, "MainFrame.diceMenu.text")); // NOI18N
        Mnemonics.setLocalizedText(mReverseDiceDirectionCheckBoxMenuItem, NbBundle.getMessage(MainFrame.class, "MainFrame.reverseDiceDirectionCheckBoxMenuItem.text")); // NOI18N

        mPopupMenu.add(mNewMenuItem);
        mPopupMenu.add(new JPopupMenu.Separator());

        mPopupMenu.add(mSystemMenu);
        mSystemMenu.add(mFullscreenCheckBoxMenuItem);
        mSystemMenu.add(mNightModeCheckBoxMenuItem);
        mSystemMenu.add(mPlaySoundCheckBoxMenuItem);
        mSystemMenu.add(new JSeparator());
        mSystemMenu.add(mRemovePlayerMenuItem);

        mPopupMenu.add(mScorecardMenu);
        mScorecardMenu.add(mColorsMenu);
        mScorecardMenu.add(mLimCheckBoxMenuItem);
        mScorecardMenu.add(mMaxCheckBoxMenuItem);
        mScorecardMenu.add(mIndicatorCheckBoxMenuItem);

        mPopupMenu.add(mDiceMenu);
        mDiceMenu.add(mReverseDiceDirectionCheckBoxMenuItem);

        mPopupMenu.add(new JPopupMenu.Separator());
        mPopupMenu.add(mHelpMenuItem);
        mPopupMenu.add(mAboutMenuItem);
        mPopupMenu.add(new JPopupMenu.Separator());
        mPopupMenu.add(mQuitMenuItem);

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

        mUIComponents.add(mPopupMenu);
    }

    private void loadSettings() {
//        mFullscreenCheckBoxMenuItem.setSelected(mOptions.isFullscreen());
        mNightModeCheckBoxMenuItem.setSelected(mOptions.isNightMode());
        mIndicatorCheckBoxMenuItem.setSelected(mOptions.isShowIndicators());
        mLimCheckBoxMenuItem.setSelected(mOptions.isShowLimColumn());
        mMaxCheckBoxMenuItem.setSelected(mOptions.isShowMaxColumn());
        mPlaySoundCheckBoxMenuItem.setSelected(mOptions.is(Options.KEY_PLAY_SOUND, Options.DEFAULT_PLAY_SOUND));

        mReverseDiceDirectionCheckBoxMenuItem.setSelected(mOptions.isReverseDirection());
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
