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
import java.awt.image.BufferedImage;
import java.util.Locale;
import java.util.ResourceBundle;
import org.openide.awt.Actions;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;
import se.trixon.almond.nbp.Almond;
import se.trixon.almond.util.GlobalState;
import se.trixon.almond.util.PrefsHelper;
import se.trixon.almond.util.SystemHelper;
import se.trixon.almond.util.gson_adapter.AwtColorAdapter;
import se.trixon.almond.util.swing.SwingHelper;
import se.trixon.yaya.actions.BaseAction;
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
    private final ResourceBundle mBundle = NbBundle.getBundle(BaseAction.class);
    private final Options mOptions = Options.getInstance();
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

    private Yaya() {
        mRuleManager.init();
        mYayaPanel = new YayaPanel();
        Almond.getFrame().addMouseListener(PopupManager.getInstance().getMouseAdapter());
        PrefsHelper.inc(mOptions.getPreferences(), Options.KEY_APP_START_COUNTER);
        int gameStartCounter = mOptions.getInt(Options.KEY_GAME_START_COUNTER, 0);
        if (gameStartCounter == 0) {
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

    private static class Holder {

        private static final Yaya INSTANCE = new Yaya();
    }

}
