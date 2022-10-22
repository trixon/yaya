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

import org.openide.util.NbPreferences;
import se.trixon.almond.util.OptionsBase;
import static se.trixon.almond.util.OptionsBase.DEFAULT_FULL_SCREEN;
import static se.trixon.almond.util.OptionsBase.KEY_FULL_SCREEN;
import se.trixon.yaya.gamedef.GameVariant;

/**
 *
 * @author Patrik Karlström
 */
public class Options extends OptionsBase {

    public static final String KEY_GAME_TYPE_ID = "gameType";
    public static final String KEY_NUM_OF_PLAYERS = "numOfPlayers";
    public static final String KEY_OPACITY = "opacity";
    public static final String KEY_REVERSE_DIRECTION = "reverseDirection";
    public static final String KEY_SHOW_INDICATORS = "showIndicators";
    public static final String KEY_SHOW_MAX_COLUMN = "showMaxColumn";
    public static final String KEY_SHOW_SYMBOLS = "showSymbols";
    public static final String KEY_SHOW_TOP_COLUMN = "showTopColumn";
    public static final String KEY_THEME = "theme";
    private static final String DEFAULT_GAME_TYPE_ID = "default";
    private static final String DEFAULT_GAME_VARIANT = "standard";
    private static final int DEFAULT_NUM_OF_PLAYERS = 2;
    private static final int DEFAULT_OPACITY = 255;
    private static final boolean DEFAULT_REVERSE_DIRECTION = false;
    private static final boolean DEFAULT_SHOW_INDICATORS = true;
    private static final boolean DEFAULT_SHOW_MAX_COLUMN = false;
    private static final boolean DEFAULT_SHOW_TOP_COLUMN = false;
    private static final String DEFAULT_THEME = "Legacy";
    private static final boolean DEFAULT_USE_SYMBOLS = false;
    private Player[] mPlayers;

    public static Options getInstance() {
        return Holder.INSTANCE;
    }

    private Options() {
        mPreferences = NbPreferences.forModule(getClass());
        init();
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

    public String getTheme() {
        return mPreferences.get(KEY_THEME, DEFAULT_THEME);
    }

    public boolean isFullscreen() {
        return mPreferences.getBoolean(KEY_FULL_SCREEN, DEFAULT_FULL_SCREEN);
    }

    public boolean isReverseDirection() {
        return mPreferences.getBoolean(KEY_REVERSE_DIRECTION, DEFAULT_REVERSE_DIRECTION);
    }

    public boolean isShowingIndicators() {
        return mPreferences.getBoolean(KEY_SHOW_INDICATORS, DEFAULT_SHOW_INDICATORS);
    }

    public boolean isShowingMaxColumn() {
        return mPreferences.getBoolean(KEY_SHOW_MAX_COLUMN, DEFAULT_SHOW_MAX_COLUMN);
    }

    public boolean isShowingSymbols() {
        return mPreferences.getBoolean(KEY_SHOW_SYMBOLS, DEFAULT_USE_SYMBOLS);
    }

    public boolean isShowingTopColumn() {
        return mPreferences.getBoolean(KEY_SHOW_TOP_COLUMN, DEFAULT_SHOW_TOP_COLUMN);
    }

    public void setFullscreen(boolean value) {
        mPreferences.putBoolean(KEY_FULL_SCREEN, value);
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

    public void setReverseDirection(boolean state) {
        mPreferences.putBoolean(KEY_REVERSE_DIRECTION, state);
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

    public void setTheme(String theme) {
        mPreferences.put(KEY_THEME, theme);
    }

    private void init() {
    }

    private static class Holder {

        private static final Options INSTANCE = new Options();
    }

}
