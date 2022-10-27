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

import org.apache.commons.lang3.StringUtils;
import org.openide.util.NbPreferences;
import se.trixon.almond.util.OptionsBase;
import static se.trixon.almond.util.OptionsBase.DEFAULT_FULL_SCREEN;
import static se.trixon.almond.util.OptionsBase.KEY_FULL_SCREEN;
import se.trixon.yaya.rules.GameVariant;

/**
 *
 * @author Patrik Karlström
 */
public class Options extends OptionsBase {

    public static final String DEFAULT_PLAYERS = "Ask;Embla;Ask;Embla;Ask;Embla;Ask;Embla";
    public static final String DEFAULT_PLAYERS_ALL = "Ask;Embla";
    public static final String KEY_NUM_OF_PLAYERS = "numOfPlayers";
    public static final String KEY_OPACITY = "opacity";
    public static final String KEY_PLAYERS = "players";
    public static final String KEY_PLAYERS_ALL = "players.all";
    public static final String KEY_REVERSE_DIRECTION = "reverseDirection";
    public static final String KEY_RULE_ID = "ruleId";
    public static final String KEY_SHOW_INDICATORS = "showIndicators";
    public static final String KEY_SHOW_MAX_COLUMN = "showMaxColumn";
    public static final String KEY_SHOW_SYMBOLS = "showSymbols";
    public static final String KEY_SHOW_TOP_COLUMN = "showTopColumn";
    public static final String KEY_THEME = "theme";
    private static final String DEFAULT_GAME_VARIANT = "standard";
    private static final int DEFAULT_NUM_OF_PLAYERS = 2;
    private static final int DEFAULT_OPACITY = 255;
    private static final boolean DEFAULT_REVERSE_DIRECTION = false;
    private static final String DEFAULT_RULE_ID = "default";
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

    public Player[] createPlayers() {
        int numOfPlayers = getNumOfPlayers();
        var players = new Player[numOfPlayers];

        String storedNames = get(Options.KEY_PLAYERS, Options.DEFAULT_PLAYERS);
        var names = StringUtils.split(storedNames, ";");

        for (int i = 0; i < numOfPlayers; i++) {
            players[i] = new Player(-1L, names[i], Player.Handedness.RIGHT);
        }

        return players;
    }

    public String getGameVariant(String type) {
        return get(GameVariant.PREFIX + type, DEFAULT_GAME_VARIANT);
    }

    public int getNumOfPlayers() {
        return getInt(KEY_NUM_OF_PLAYERS, DEFAULT_NUM_OF_PLAYERS);
    }

    public int getOpacity() {
        return getInt(KEY_OPACITY, DEFAULT_OPACITY);
    }

    public Player[] getPlayers() {
        if (mPlayers == null) {
            setPlayers(createPlayers());
        }

        return mPlayers;
    }

    public String getRuleId() {
        return get(KEY_RULE_ID, DEFAULT_RULE_ID);
    }

    public String getTheme() {
        return get(KEY_THEME, DEFAULT_THEME);
    }

    public boolean isFullscreen() {
        return is(KEY_FULL_SCREEN, DEFAULT_FULL_SCREEN);
    }

    public boolean isReverseDirection() {
        return is(KEY_REVERSE_DIRECTION, DEFAULT_REVERSE_DIRECTION);
    }

    public boolean isShowingIndicators() {
        return is(KEY_SHOW_INDICATORS, DEFAULT_SHOW_INDICATORS);
    }

    public boolean isShowingMaxColumn() {
        return is(KEY_SHOW_MAX_COLUMN, DEFAULT_SHOW_MAX_COLUMN);
    }

    public boolean isShowingSymbols() {
        return is(KEY_SHOW_SYMBOLS, DEFAULT_USE_SYMBOLS);
    }

    public boolean isShowingTopColumn() {
        return is(KEY_SHOW_TOP_COLUMN, DEFAULT_SHOW_TOP_COLUMN);
    }

    public void setFullscreen(boolean value) {
        put(KEY_FULL_SCREEN, value);
    }

    public void setGameVariant(String type, String variant) {
        put(GameVariant.PREFIX + type, variant);
    }

    public void setNumOfPlayers(int players) {
        put(KEY_NUM_OF_PLAYERS, players);
    }

    public void setOpacity(int value) {
        put(KEY_OPACITY, value);
    }

    public void setPlayers(Player[] players) {
        mPlayers = players;
    }

    public void setReverseDirection(boolean state) {
        put(KEY_REVERSE_DIRECTION, state);
    }

    public void setRuleId(String typeId) {
        put(KEY_RULE_ID, typeId);
    }

    public void setShowIndicators(boolean state) {
        put(KEY_SHOW_INDICATORS, state);
    }

    public void setShowMaxColumn(boolean state) {
        put(KEY_SHOW_MAX_COLUMN, state);
    }

    public void setShowSymbols(boolean state) {
        put(KEY_SHOW_SYMBOLS, state);
    }

    public void setShowTopColumn(boolean state) {
        put(KEY_SHOW_TOP_COLUMN, state);
    }

    public void setTheme(String theme) {
        put(KEY_THEME, theme);
    }

    private void init() {
    }

    private static class Holder {

        private static final Options INSTANCE = new Options();
    }

}
