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

import java.util.Comparator;
import java.util.HashMap;
import org.openide.util.Lookup;
import se.trixon.yaya.themes.Theme;
import se.trixon.yaya.themes.ThemeProvider;

/**
 *
 * @author Patrik Karlström <patrik@trixon.se>
 */
public class ThemeManager {

    private final HashMap<String, Theme> mIdToTheme = new HashMap<>();
    private final Options mOptions = Options.getInstance();
    private final Theme[] mThemes;

    public static ThemeManager getInstance() {
        return Holder.INSTANCE;
    }

    private ThemeManager() {
        mThemes = Lookup.getDefault().lookupAll(ThemeProvider.class).stream()
                .map(provider -> provider.load())
                .filter(theme -> {
                    mIdToTheme.put(theme.getId(), theme);
                    return true;
                })
                .sorted(Comparator.comparing(Theme::getName)).toArray(Theme[]::new);
    }

    public Theme getTheme() {
        return mIdToTheme.get(mOptions.getThemeId());
    }

    public Theme[] getThemes() {
        return mThemes;
    }

    private static class Holder {

        private static final ThemeManager INSTANCE = new ThemeManager();
    }
}
