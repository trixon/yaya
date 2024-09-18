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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import org.openide.awt.Actions;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;
import org.openide.util.NbPreferences;
import se.trixon.almond.util.Dict;
import se.trixon.almond.util.GlobalState;
import se.trixon.almond.util.PrefsHelper;
import se.trixon.almond.util.SystemHelper;
import se.trixon.almond.util.gson_adapter.AwtColorAdapter;
import se.trixon.almond.util.swing.SwingHelper;
import se.trixon.yaya.actions.YAction;
import se.trixon.yaya.scorecard.rules.RuleManager;

/**
 *
 * @author Patrik Karlström <patrik@trixon.se>
 */
public class Yaya {

    public static final Gson GSON = new GsonBuilder()
            .setVersion(1.0)
            .serializeNulls()
            .setPrettyPrinting()
            .registerTypeAdapter(Color.class, new AwtColorAdapter())
            .create();
    public static final boolean IN_DEVELOPMENT = true;
    public static final String KEY_GAME_STATE = "gamestate";
    public static final String LOG_TITLE = "Yaya";
    private static final GlobalState sGlobalState = new GlobalState();
    private final ResourceBundle mBundle = NbBundle.getBundle(YAction.class);
    private final Options mOptions = Options.getInstance();
    private JPopupMenu mPopupMenu;
    private final RuleManager mRuleManager = RuleManager.getInstance();
    private final YayaPanel mYayaPanel;

    public static void errln(String name, String message) {
        System.err.println(message);
    }

    public static GlobalState getGlobalState() {
        return sGlobalState;
    }

    public static BufferedImage getImage(Class c, String imagePath) {
        return (BufferedImage) ImageUtilities.loadImage(SystemHelper.getPackageAsPath(c) + imagePath);
    }

    public static BufferedImage getImage(String imagePath) {
        return getImage(Yaya.class, imagePath);
    }

    public static Yaya getInstance() {
        return Holder.INSTANCE;
    }

    public static String getLanguageSuffix() {
        return "-" + Locale.getDefault().getLanguage();
    }

    public static void outln(String name, String message) {
        System.out.println(message);
    }

//    private void postStart() {
//        PrefsHelper.inc(mOptions.getPreferences(), Options.KEY_APP_START_COUNTER);
//        int gameStartCounter = mOptions.getPreferences().getInt(Options.KEY_GAME_START_COUNTER, 0);
//        if (gameStartCounter == 0) {
//            FxHelper.runLaterDelayed(200, () -> {
//                YActions.forId("core", "newround").handle(null);
//                YActions.forId("core", "help").handle(null);
//            });
//        } else {
//            mYaya.onRequestNewGameStart();
//        }
//
//    }
    private Yaya() {
        mRuleManager.init();
        mYayaPanel = new YayaPanel();
        initMenu();
        var popupListener = new PopupListener();
//        addMouseListener(popupListener);
        mYayaPanel.addMouseListener(popupListener);
        PrefsHelper.inc(mOptions.getPreferences(), Options.KEY_APP_START_COUNTER);
        int gameStartCounter = mOptions.getInt(Options.KEY_GAME_START_COUNTER, 0);
        if (gameStartCounter == 0 || true) {
            SwingHelper.runLaterDelayed(200, () -> {
                Actions.forID("Help", "se.trixon.yaya.actions.HelpAction").actionPerformed(new ActionEvent(mYayaPanel, 0, ""));
                Actions.forID("Game", "se.trixon.yaya.actions.NewRoundAction").actionPerformed(null);
            });
        } else {
            onRequestNewGameStart();
        }
    }

    public YayaPanel getPanel() {
        return mYayaPanel;
    }

    public void onRequestNewGameStart() {
        getPanel().newGame();
    }

    private void initMenu() {
        mPopupMenu = new JPopupMenu();
        var newMenuItem = new JMenuItem();
        var systemMenu = new JMenu(Dict.SYSTEM.toString());
//        mFullscreenCheckBoxMenuItem = new JCheckBoxMenuItem();
//        mNightModeCheckBoxMenuItem = new JCheckBoxMenuItem();
        var removePlayerMenuItem = new JMenuItem();
//        mPlaySoundCheckBoxMenuItem = new JCheckBoxMenuItem();
//
        var scorecardMenu = new JMenu(mBundle.getString("scorecard"));
//        mColorsMenu = new JMenu();
//        mLimCheckBoxMenuItem = new JCheckBoxMenuItem();
//        mMaxCheckBoxMenuItem = new JCheckBoxMenuItem();
//        mIndicatorCheckBoxMenuItem = new JCheckBoxMenuItem();
//

        var diceMenu = new JMenu(mBundle.getString("dice"));
        var reverseDiceDirectionCheckBoxMenuItem = new JCheckBoxMenuItem();
//
        var helpMenuItem = new JMenuItem();
        var aboutMenuItem = new JMenuItem();
        var quitMenuItem = new JMenuItem();
//
//        systemMenu.setText(Dict.SYSTEM.toString());
//        Mnemonics.setLocalizedText(mScorecardMenu, NbBundle.getMessage(MainFrame.class, "MainFrame.scorecardMenu.text")); // NOI18N
//        Mnemonics.setLocalizedText(mColorsMenu, NbBundle.getMessage(MainFrame.class, "MainFrame.colorsMenu.text")); // NOI18N
//        Mnemonics.setLocalizedText(mDiceMenu, NbBundle.getMessage(MainFrame.class, "MainFrame.diceMenu.text")); // NOI18N
//        Mnemonics.setLocalizedText(mReverseDiceDirectionCheckBoxMenuItem, NbBundle.getMessage(MainFrame.class, "MainFrame.reverseDiceDirectionCheckBoxMenuItem.text")); // NOI18N
        var reverseDiceDirectionAction = Actions.checkbox(
                NbPreferences.forModule(Yaya.class).absolutePath(),
                Options.KEY_REVERSE_DIRECTION,
                mBundle.getString("reverseDiceDirection"),
                "",
                true
        );
        Actions.connect(reverseDiceDirectionCheckBoxMenuItem, reverseDiceDirectionAction, false);
        Actions.connect(newMenuItem, Actions.forID("Game", "se.trixon.yaya.actions.NewRoundAction"), false);
        Actions.connect(removePlayerMenuItem, Actions.forID("Game", "se.trixon.yaya.actions.RemovePlayerAction"), false);
        Actions.connect(helpMenuItem, Actions.forID("Help", "se.trixon.yaya.actions.HelpAction"), false);
        Actions.connect(aboutMenuItem, Actions.forID("Help", "se.trixon.yaya.actions.AboutAction"), false);
        Actions.connect(quitMenuItem, Actions.forID("File", "se.trixon.almond.nbp.actions.QuitAction"), false);
        mPopupMenu.add(newMenuItem);
        mPopupMenu.add(new JPopupMenu.Separator());
        mPopupMenu.add(systemMenu);
//        systemMenu.add(mFullscreenCheckBoxMenuItem);
//        systemMenu.add(mNightModeCheckBoxMenuItem);
//        systemMenu.add(mPlaySoundCheckBoxMenuItem);
        systemMenu.add(new JSeparator());
        systemMenu.add(removePlayerMenuItem);
//
        mPopupMenu.add(scorecardMenu);
//        mScorecardMenu.add(mColorsMenu);
//        mScorecardMenu.add(mLimCheckBoxMenuItem);
//        mScorecardMenu.add(mMaxCheckBoxMenuItem);
//        mScorecardMenu.add(mIndicatorCheckBoxMenuItem);
//
        mPopupMenu.add(diceMenu);
        diceMenu.add(reverseDiceDirectionCheckBoxMenuItem);
//
        mPopupMenu.add(new JPopupMenu.Separator());
        mPopupMenu.add(helpMenuItem);
        mPopupMenu.add(aboutMenuItem);
        mPopupMenu.add(new JPopupMenu.Separator());
        mPopupMenu.add(quitMenuItem);
//
//        var buttonGroup = new ButtonGroup();
//        for (var theme : mThemeManager.getItems()) {
//            var radioButtonMenuItem = new JRadioButtonMenuItem(theme.getName());
//            radioButtonMenuItem.addActionListener(actionEvent -> {
//                mThemeManager.setTheme(theme);
//                mOptions.setThemeId(theme.getId());
//            });
//            buttonGroup.add(radioButtonMenuItem);
//            mColorsMenu.add(radioButtonMenuItem);
//            radioButtonMenuItem.setSelected(mOptions.getThemeId().equalsIgnoreCase(theme.getId()));
//        }
//
//        var fontMenuItem = new JMenuItem(Dict.SIZE.toString());
//        fontMenuItem.setEnabled(false);
//        mScorecardMenu.add(fontMenuItem);
//
//        var fontSlider = new JSlider(8, 72, mOptions.getFontSize());
//        var fontResetRunner = new DelayedResetRunner(50, () -> {
//            mOptions.setFontSize(fontSlider.getValue());
//        });
//
//        fontSlider.addChangeListener(changeEvent -> {
//            fontResetRunner.reset();
//        });
//
//        mScorecardMenu.add(fontSlider);
//        mUIComponents.add(mPopupMenu); NO USE
    }

    private void loadSettings() {
////        mFullscreenCheckBoxMenuItem.setSelected(mOptions.isFullscreen());
//        mNightModeCheckBoxMenuItem.setSelected(mOptions.isNightMode());
//        mIndicatorCheckBoxMenuItem.setSelected(mOptions.isShowIndicators());
//        mLimCheckBoxMenuItem.setSelected(mOptions.isShowLimColumn());
//        mMaxCheckBoxMenuItem.setSelected(mOptions.isShowMaxColumn());
//        mPlaySoundCheckBoxMenuItem.setSelected(mOptions.is(Options.KEY_PLAY_SOUND, Options.DEFAULT_PLAY_SOUND));
//
//        mReverseDiceDirectionCheckBoxMenuItem.setSelected(mOptions.isReverseDirection());
    }

    private static class Holder {

        private static final Yaya INSTANCE = new Yaya();
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
