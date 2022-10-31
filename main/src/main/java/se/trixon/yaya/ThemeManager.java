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

import java.awt.Color;
import java.util.ArrayList;

/**
 *
 * @author Patrik Karlström <patrik@trixon.se>
 */
public class ThemeManager {

    private final ArrayList<Theme> mItems = new ArrayList<>();
    private final Options mOptions = Options.getInstance();

    public static ThemeManager getInstance() {
        return Holder.INSTANCE;
    }

    private ThemeManager() {
        initThemes();
    }

    public Color getBackground() {
        return getCurrentTheme().getBackground();
    }

    public Theme getCurrentTheme() {
//        return mItems.get(0);
        return getTheme(mOptions.getTheme());
    }

    public Color getHeader() {
        return getCurrentTheme().getHeader();
    }

    public Color getIndicatorHi() {
        return getCurrentTheme().getIndicatorHi();
    }

    public Color getIndicatorLo() {
        return getCurrentTheme().getIndicatorLo();
    }

    public ArrayList<Theme> getItems() {
        return mItems;
    }

    public Color getRow() {
        return getCurrentTheme().getRow();
    }

    public Color getScorecard() {
        return getCurrentTheme().getScorecard();
    }

    public Color getSum() {
        return getCurrentTheme().getSum();
    }

    public Theme getTheme(String name) {
        for (var theme : mItems) {
            if (theme.getName().equalsIgnoreCase(name)) {
                return theme;
            }
        }

        return mItems.get(0);
    }

    public Color getUndoIcon() {
        return getCurrentTheme().getUndoIcon();
    }

    private void initThemes() {
        var legacy = new Theme("Legacy");
        legacy.setBackground(Color.decode("#333333"));
        legacy.setHeader(Color.decode("#FFC800"));
        legacy.setIndicatorHi(Color.decode("#BBEEBB"));
        legacy.setIndicatorLo(Color.decode("#EEBBBB"));
        legacy.setRow(Color.decode("#FFFFFF"));
        legacy.setScorecard(Color.decode("#666666"));
        legacy.setSum(Color.decode("#FFFF00"));
        legacy.setUndoIcon(Color.RED);
        mItems.add(legacy);

        var solarized = new Theme("Solarized");
        solarized.setBackground(Color.decode("#333333"));
        solarized.setHeader(Color.decode("#b58900"));
        solarized.setIndicatorHi(Color.decode("#859900"));
        solarized.setIndicatorLo(Color.decode("#dc322f"));
        solarized.setRow(Color.decode("#fdf6e3"));
        solarized.setScorecard(Color.decode("#666666"));
        solarized.setSum(Color.decode("#073642"));
        solarized.setUndoIcon(Color.YELLOW);
        mItems.add(solarized);

    }

    private static class Holder {

        private static final ThemeManager INSTANCE = new ThemeManager();
    }
}
