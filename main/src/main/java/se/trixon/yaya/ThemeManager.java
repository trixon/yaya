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

import java.util.ArrayList;
import java.util.Comparator;
import org.openide.util.Lookup;
import se.trixon.yaya.themes.Theme;
import se.trixon.yaya.themes.DefaultTheme;

/**
 *
 * @author Patrik Karlström <patrik@trixon.se>
 */
public class ThemeManager {

    private final ArrayList<Theme> mItems;
    private final Options mOptions = Options.getInstance();
    private Theme mTheme;

    public static ThemeManager getInstance() {
        return Holder.INSTANCE;
    }

    private ThemeManager() {
        mItems = new ArrayList<>(Lookup.getDefault().lookupAll(Theme.class));
        mItems.sort(Comparator.comparing(Theme::getName));

        loadTheme();
    }

    public ArrayList<Theme> getItems() {
        return mItems;
    }

    public Theme getTheme() {
        return mTheme;
    }

    public void setTheme(Theme theme) {
        mTheme = theme;
    }

    private void loadTheme() {
        mTheme = null;

        for (var theme : mItems) {
            if (theme.getId().equalsIgnoreCase(mOptions.getThemeId())) {
                mTheme = theme;
                break;
            }
        }

        if (mTheme == null) {
            mTheme = new DefaultTheme();
        }
    }

    private static class Holder {

        private static final ThemeManager INSTANCE = new ThemeManager();
    }
}
