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
import java.awt.image.BufferedImage;
import java.util.Locale;
import org.openide.util.ImageUtilities;
import se.trixon.almond.util.GlobalState;
import se.trixon.almond.util.SystemHelper;
import se.trixon.almond.util.gson_adapter.AwtColorAdapter;

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
    private YayaPanel mYayaPanel;

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
    }

    public YayaPanel getPanel() {
        if (mYayaPanel == null) {
            mYayaPanel = new YayaPanel();
        }

        return mYayaPanel;
    }

    public void onRequestNewGameStart() {
        getPanel().newGame();
    }

    private static class Holder {

        private static final Yaya INSTANCE = new Yaya();
    }
}
