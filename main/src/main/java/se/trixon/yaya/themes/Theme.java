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

/**
 *
 * @author Patrik Karlström <patrik@trixon.se>
 */
public abstract class Theme {

    public Color getBackground() {
        return Color.decode("#333333");
    }

    public Color getHeader() {
        return Color.decode("#FFC800");
    }

    public abstract String getId();

    public Color getIndicatorHi() {
        return Color.decode("#BBEEBB");
    }

    public Color getIndicatorLo() {
        return Color.decode("#EEBBBB");
    }

    public abstract String getName();

    public Color getRow() {
        return Color.decode("#FFFFFF");
    }

    public Color getScorecard() {
        return Color.decode("#666666");
    }

    public Color getSum() {
        return Color.decode("#FFFF00");
    }

    public Color getUndoIcon() {
        return Color.RED;
    }

}
