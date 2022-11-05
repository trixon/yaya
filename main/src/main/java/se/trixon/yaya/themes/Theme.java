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

    protected Color mForeground = Color.BLACK;

    public int getAlpha() {
        return 150;
    }

    /**
     * The background color of the windows upper section, usually covered by the
     * wooden surface
     *
     * @return
     */
    public Color getBackground() {
        return Color.decode("#333333");
    }

    public Color getBgHeaderColumn() {
        return Color.decode("#fdf5e6");
    }

    public Color getBgHeaderRow() {
        return Color.decode("#8b4513");
    }

    public Color getBgHeaderSum() {
        return Color.decode("#c19a6b");
    }

    public Color getBgScoreCell() {
        return Color.WHITE;
    }

    public Color getFgHeaderColumn() {
        return mForeground;
    }

    public Color getFgHeaderRow() {
        return Color.lightGray;
    }

    public Color getFgHeaderSum() {
        return mForeground;
    }

    public Color getFgScoreCell() {
        return mForeground;
    }

    public abstract String getId();

    public Color getIndicatorHi() {
        return Color.decode("#BBEEBB");
    }

    public Color getIndicatorLo() {
        return Color.decode("#EEBBBB");
    }

    public abstract String getName();

    /**
     * The background color behind the scorecard, mostly invisible
     *
     * @return
     */
    public Color getScorecard() {
        return Color.decode("#666666");
    }

    public Color getUndoIcon() {
        return Color.RED;
    }
}
