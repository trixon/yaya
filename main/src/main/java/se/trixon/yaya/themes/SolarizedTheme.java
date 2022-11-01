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
package se.trixon.yaya.themes;

import java.awt.Color;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Patrik Karlström <patrik@trixon.se>
 */
@NbBundle.Messages("THEME_Solarized=Solarized")
@ServiceProvider(service = Theme.class)
public class SolarizedTheme extends Theme {

    @Override
    public Color getBackground() {
        return Color.decode("#333333");
    }

    @Override
    public Color getHeader() {
        return Color.decode("#b58900");
    }

    @Override
    public String getId() {
        return "solarized";
    }

    @Override
    public Color getIndicatorHi() {
        return Color.decode("#859900");
    }

    @Override
    public Color getIndicatorLo() {
        return Color.decode("#dc322f");
    }

    @Override
    public String getName() {
        return Bundle.THEME_Solarized();
    }

    @Override
    public Color getRow() {
        return Color.decode("#fdf6e3");
    }

    @Override
    public Color getScorecard() {
        return Color.decode("#666666");
    }

    @Override
    public Color getSum() {
        return Color.decode("#073642");
    }

    @Override
    public Color getUndoIcon() {
        return Color.YELLOW;
    }

}
