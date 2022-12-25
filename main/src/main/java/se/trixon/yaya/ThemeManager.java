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
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.openide.util.Lookup;
import se.trixon.yaya.themes.Theme;
import se.trixon.yaya.themes.ThemeProvider;
import se.trixon.yaya.themes.ThemeProviderDefault;

/**
 *
 * @author Patrik Karlström <patrik@trixon.se>
 */
public class ThemeManager {

    private final ObjectProperty<ObservableList<Theme>> mItemsProperty = new SimpleObjectProperty<>();
    private final Options mOptions = Options.getInstance();
    private final ObjectProperty<Theme> mThemeProperty = new SimpleObjectProperty<>();

    public static ThemeManager getInstance() {
        return Holder.INSTANCE;
    }

    private ThemeManager() {
        mItemsProperty.set(FXCollections.observableArrayList());

        mThemeProperty.addListener((p, o, n) -> {
            mOptions.setThemeId(n.getId());
        });

        for (var themeProvider : Lookup.getDefault().lookupAll(ThemeProvider.class)) {
            getItems().add(themeProvider.load());
        }

        FXCollections.sort(getItems(), Comparator.comparing(Theme::getName));

        loadTheme();
    }

    public ObservableList<Theme> getItems() {
        return mItemsProperty.get();
    }

    public Theme getTheme() {
        return mThemeProperty.get();
    }

    public ObjectProperty<ObservableList<Theme>> itemsProperty() {
        return mItemsProperty;
    }

    public ObjectProperty<Theme> themeProperty() {
        return mThemeProperty;
    }

    private void loadTheme() {
        Theme theme = null;

        for (var t : getItems()) {
            if (t.getId().equalsIgnoreCase(mOptions.getThemeId())) {
                theme = t;
                break;
            }
        }

        if (theme == null) {
            theme = new ThemeProviderDefault().load();
        }

        mThemeProperty.set(theme);
    }

    private static class Holder {

        private static final ThemeManager INSTANCE = new ThemeManager();
    }
}
