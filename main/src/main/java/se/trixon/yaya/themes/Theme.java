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

import com.google.gson.annotations.SerializedName;
import java.awt.Color;
import java.util.HashMap;
import se.trixon.yaya.Yaya;

/**
 *
 * @author Patrik Karlström <patrik@trixon.se>
 */
public class Theme {

    @SerializedName("BG_HeaderColumn")
    private Color mBgHeaderColumn;
    @SerializedName("BG_HeaderRow")
    private Color mBgHeaderRow;
    @SerializedName("BG_HeaderSum")
    private Color mBgHeaderSum;
    @SerializedName("BG_IndicatorHi")
    private Color mBgIndicatorHi;
    @SerializedName("BG_IndicatorLo")
    private Color mBgIndicatorLo;
    @SerializedName("BG_ScoreCell")
    private Color mBgScoreCell;
    @SerializedName("BG_Scorecard")
    private Color mBgScorecard;
    @SerializedName("BG_ScorecardFiller")
    private Color mBgScorecardFiller;
    @SerializedName("BG_Window")
    private Color mBgWindow;
    @SerializedName("FG_HeaderColumn")
    private Color mFgHeaderColumn;
    @SerializedName("FG_HeaderRow")
    private Color mFgHeaderRow;
    @SerializedName("FG_HeaderSum")
    private Color mFgHeaderSum;
    @SerializedName("FG_IndicatorHi")
    private Color mFgIndicatorHi;
    @SerializedName("FG_IndicatorLo")
    private Color mFgIndicatorLo;
    @SerializedName("FG_ScoreCell")
    private Color mFgScoreCell;
    @SerializedName("ICON_Undo")
    private Color mIconUndo;
    @SerializedName("id")
    private String mId;
    @SerializedName("locals")
    private final HashMap<String, String> mLocals = new HashMap<>();
    @SerializedName("name")
    private String mName;
    @SerializedName("isOpagueScorecard")
    private boolean mOpaqueScorecard;
    @SerializedName("isOpagueWindow")
    private boolean mOpaqueWindow;

    public Color getBgHeaderColumn() {
        return mBgHeaderColumn;
    }

    public Color getBgHeaderRow() {
        return mBgHeaderRow;
    }

    public Color getBgHeaderSum() {
        return mBgHeaderSum;
    }

    public Color getBgIndicatorHi() {
        return mBgIndicatorHi;
    }

    public Color getBgIndicatorLo() {
        return mBgIndicatorLo;
    }

    public Color getBgScoreCell() {
        return mBgScoreCell;
    }

    /**
     * The background color behind the scorecard, mostly invisible
     *
     * @return
     */
    public Color getBgScorecard() {
        return mBgScorecard;
    }

    public Color getBgScorecardFiller() {
        return mBgScorecardFiller;
    }

    /**
     * The background color of the windows upper section, usually covered by the
     * wooden surface
     *
     * @return
     */
    public Color getBgWindow() {
        return mBgWindow;
    }

    public Color getFgHeaderColumn() {
        return mFgHeaderColumn;
    }

    public Color getFgHeaderRow() {
        return mFgHeaderRow;
    }

    public Color getFgHeaderSum() {
        return mFgHeaderSum;
    }

    public Color getFgIndicatorHi() {
        return mFgIndicatorHi;
    }

    public Color getFgIndicatorLo() {
        return mFgIndicatorLo;
    }

    public Color getFgScoreCell() {
        return mFgScoreCell;
    }

    public Color getIconUndo() {
        return mIconUndo;
    }

    public String getId() {
        return mId;
    }

    public HashMap<String, String> getLocals() {
        return mLocals;
    }

    public String getName() {
        return mLocals.getOrDefault("name" + Yaya.getLanguageSuffix(), mName);
    }

    public boolean isOpaqueScorecard() {
        return mOpaqueScorecard;
    }

    public boolean isOpaqueWindow() {
        return mOpaqueWindow;
    }

    @Override
    public String toString() {
        return getName();
    }
}
