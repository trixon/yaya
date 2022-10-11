/*
 * Copyright 2018 Patrik Karlström.
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
package org.nbgames.yaya.gamedef;

import com.google.gson.annotations.SerializedName;
import java.util.HashMap;
import java.util.TreeSet;
import org.nbgames.core.api.NbGames;
import se.trixon.almond.util.StringHelper;

/**
 *
 * @author Patrik Karlström
 */
public class GameRow {

    @SerializedName("bonus")
    private boolean mBonus;
    @SerializedName("formula")
    private String mFormula;
    @SerializedName("i10n")
    private final HashMap<String, String> mI10n = new HashMap<>();
    @SerializedName("id")
    private String mId;
    @SerializedName("lim")
    private int mLim = 0;
    @SerializedName("max")
    private int mMax = 0;
    @SerializedName("playable")
    private boolean mPlayable;
    @SerializedName("roll_counter")
    private boolean mRollCounter;
    @SerializedName("sum")
    private boolean mSum;
    private transient TreeSet<Integer> mSumSet;
    @SerializedName("title")
    private String mTitle;
    @SerializedName("title_symbol")
    private String mTitleSymbol;

    public GameRow() {
    }

    public String getFormula() {
        return mFormula;
    }

    public HashMap<String, String> getI10n() {
        return mI10n;
    }

    public String getId() {
        return mId;
    }

    public int getLim() {
        return mLim;
    }

    public int getMax() {
        return mMax;
    }

    public TreeSet<Integer> getSumSet() {
        return mSumSet;
    }

    public String getTitle() {
        return mI10n.getOrDefault("title" + NbGames.getLanguageSuffix(), mTitle);
    }

    public String getTitleSymbol() {
        return mTitleSymbol;
    }

    public boolean isBonus() {
        return mBonus;
    }

    public boolean isPlayable() {
        return mPlayable;
    }

    public boolean isRollCounter() {
        return mRollCounter;
    }

    public boolean isSum() {
        return mSum;
    }

    public void setBonus(boolean bonus) {
        mBonus = bonus;
    }

    public void setFormula(String formula) {
        mFormula = formula;
    }

    public void setId(String id) {
        mId = id;
    }

    public void setLim(int lim) {
        mLim = lim;
    }

    public void setMax(int max) {
        mMax = max;
    }

    public void setPlayable(boolean playable) {
        mPlayable = playable;
    }

    public void setRollCounter(boolean rollCounter) {
        mRollCounter = rollCounter;
    }

    public void setSum(boolean sum) {
        mSum = sum;
    }

    public void setSumSet(TreeSet<Integer> sumSet) {
        mSumSet = sumSet;
    }

    public void setSumSet(String sumSet) {
        setSumSet(StringHelper.convertStringToIntSet(sumSet));
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public void setTitleSymbol(String titleSymbol) {
        mTitleSymbol = titleSymbol;
    }

    void postRestore() {
        if (mFormula == null) {
            mFormula = "";
        }
    }
}
