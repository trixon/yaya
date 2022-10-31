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

/**
 *
 * @author Patrik Karlström <patrik@trixon.se>
 */
public class Theme {

    private Color mBackground;
    private Color mHeader;
    private Color mIndicatorHi;
    private Color mIndicatorLo;
    private String mName;
    private Color mRow;
    private Color mScorecard;
    private Color mSum;
    private Color mUndoIcon;

    public Theme(String name) {
        mName = name;
    }

    public Color getBackground() {
        return mBackground;
    }

    public Color getHeader() {
        return mHeader;
    }

    public Color getIndicatorHi() {
        return mIndicatorHi;
    }

    public Color getIndicatorLo() {
        return mIndicatorLo;
    }

    public String getName() {
        return mName;
    }

    public Color getRow() {
        return mRow;
    }

    public Color getScorecard() {
        return mScorecard;
    }

    public Color getSum() {
        return mSum;
    }

    public Color getUndoIcon() {
        return mUndoIcon;
    }

    public void setBackground(Color background) {
        mBackground = background;
    }

    public void setHeader(Color header) {
        mHeader = header;
    }

    public void setIndicatorHi(Color indicatorHi) {
        mIndicatorHi = indicatorHi;
    }

    public void setIndicatorLo(Color indicatorLo) {
        mIndicatorLo = indicatorLo;
    }

    public void setName(String name) {
        mName = name;
    }

    public void setRow(Color row) {
        mRow = row;
    }

    public void setScorecard(Color scorecard) {
        mScorecard = scorecard;
    }

    public void setSum(Color sum) {
        mSum = sum;
    }

    public void setUndoIcon(Color undoIcon) {
        mUndoIcon = undoIcon;
    }

}
