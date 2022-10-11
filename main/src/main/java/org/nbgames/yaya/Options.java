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
package org.nbgames.yaya;

import java.awt.Color;
import java.util.prefs.Preferences;
import org.nbgames.core.api.Player;
import org.nbgames.core.api.options.NbgOptions;
import org.nbgames.yaya.gamedef.GameVariant;
import org.openide.util.NbPreferences;
import se.trixon.almond.util.GraphicsHelper;

/**
 *
 * @author Patrik Karlström
 */
public class Options extends NbgOptions {

    public static final String KEY_GAME_TYPE_ID = "gameType";
    public static final String KEY_NUM_OF_PLAYERS = "numOfPlayers";
    public static final String KEY_SHOW_INDICATORS = "showIndicators";
    public static final String KEY_SHOW_MAX_COLUMN = "showMaxColumn";
    public static final String KEY_SHOW_SYMBOLS = "showSymbols";
    public static final String KEY_SHOW_TOP_COLUMN = "showTopColumn";
    public static final String KEY_OPACITY = "opacity";
    private static final String DEFAULT_COLOR_BACKGROUND = "#333333";
    private static final String DEFAULT_COLOR_HEADER = "#FFC800";
    private static final String DEFAULT_COLOR_INDICATOR_HI = "#BBEEBB";
    private static final String DEFAULT_COLOR_INDICATOR_LO = "#EEBBBB";
    private static final String DEFAULT_COLOR_ROW = "#FFFFFF";
    private static final String DEFAULT_COLOR_SCORECARD = "#666666";
    private static final String DEFAULT_COLOR_SUM = "#FFFF00";
    private static final String DEFAULT_GAME_TYPE_ID = "default";
    private static final String DEFAULT_GAME_VARIANT = "standard";
    private static final int DEFAULT_NUM_OF_PLAYERS = 2;
    private static final int DEFAULT_OPACITY = 255;
    private static final boolean DEFAULT_SHOW_INDICATORS = true;
    private static final boolean DEFAULT_SHOW_MAX_COLUMN = false;
    private static final boolean DEFAULT_SHOW_TOP_COLUMN = false;
    private static final boolean DEFAULT_USE_SYMBOLS = false;
    private Player[] mPlayers;
    private Preferences mPreferencesColors = NbPreferences.forModule(getClass()).node("colors");

    public static Options getInstance() {
        return Holder.INSTANCE;
    }

    private Options() {
        mPreferences = NbPreferences.forModule(getClass());
        init();
    }

    public Color getColor(ColorItem colorItem) {
        return Color.decode(mPreferencesColors.get(colorItem.getKey(), colorItem.getDefaultColorAsString()));
    }

    public String getGameTypeId() {
        return mPreferences.get(KEY_GAME_TYPE_ID, DEFAULT_GAME_TYPE_ID);
    }

    public String getGameVariant(String type) {
        return mPreferences.get(GameVariant.PREFIX + type, DEFAULT_GAME_VARIANT);
    }

    public int getNumOfPlayers() {
        return mPreferences.getInt(KEY_NUM_OF_PLAYERS, DEFAULT_NUM_OF_PLAYERS);
    }

    public int getOpacity() {
        return mPreferences.getInt(KEY_OPACITY, DEFAULT_OPACITY);
    }

    public Player[] getPlayers() {
        return mPlayers;
    }

    public Preferences getPreferencesColors() {
        return mPreferencesColors;
    }

    public boolean isShowingSymbols() {
        return mPreferences.getBoolean(KEY_SHOW_SYMBOLS, DEFAULT_USE_SYMBOLS);
    }

    public boolean isShowingIndicators() {
        return mPreferences.getBoolean(KEY_SHOW_INDICATORS, DEFAULT_SHOW_INDICATORS);
    }

    public boolean isShowingMaxColumn() {
        return mPreferences.getBoolean(KEY_SHOW_MAX_COLUMN, DEFAULT_SHOW_MAX_COLUMN);
    }

    public boolean isShowingTopColumn() {
        return mPreferences.getBoolean(KEY_SHOW_TOP_COLUMN, DEFAULT_SHOW_TOP_COLUMN);
    }

    public void setColor(ColorItem colorItem, Color color) {
        mPreferencesColors.put(colorItem.getKey(), GraphicsHelper.colorToString(color));
    }

    public void setGameTypeId(String typeId) {
        mPreferences.put(KEY_GAME_TYPE_ID, typeId);
    }

    public void setGameVariant(String type, String variant) {
        mPreferences.put(GameVariant.PREFIX + type, variant);
    }

    public void setNumOfPlayers(int players) {
        mPreferences.putInt(KEY_NUM_OF_PLAYERS, players);
    }

    public void setOpacity(int value) {
        mPreferences.putInt(KEY_OPACITY, value);
    }

    public void setPlayers(Player[] players) {
        mPlayers = players;
    }

    public void setShowIndicators(boolean state) {
        mPreferences.putBoolean(KEY_SHOW_INDICATORS, state);
    }

    public void setShowMaxColumn(boolean state) {
        mPreferences.putBoolean(KEY_SHOW_MAX_COLUMN, state);
    }

    public void setShowSymbols(boolean state) {
        mPreferences.putBoolean(KEY_SHOW_SYMBOLS, state);
    }

    public void setShowTopColumn(boolean state) {
        mPreferences.putBoolean(KEY_SHOW_TOP_COLUMN, state);
    }

    private void init() {
    }

    public enum ColorItem {

        BACKGROUND(DEFAULT_COLOR_BACKGROUND),
        HEADER(DEFAULT_COLOR_HEADER),
        INDICATOR_HI(DEFAULT_COLOR_INDICATOR_HI),
        INDICATOR_LO(DEFAULT_COLOR_INDICATOR_LO),
        ROW(DEFAULT_COLOR_ROW),
        SCORECARD(DEFAULT_COLOR_SCORECARD),
        SUM(DEFAULT_COLOR_SUM);

        private final String mDefaultColor;

        ColorItem(String defaultColor) {
            mDefaultColor = defaultColor;
        }

        public Color getDefaultColor() {
            return Color.decode(mDefaultColor);
        }

        public String getDefaultColorAsString() {
            return mDefaultColor;
        }

        public String getKey() {
            return name().toLowerCase();
        }
    }

    private static class Holder {

        private static final Options INSTANCE = new Options();
    }
}
