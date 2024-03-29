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
package se.trixon.yaya.scorecard.rules;

import com.google.gson.annotations.SerializedName;
import java.util.HashMap;
import java.util.TreeSet;
import se.trixon.almond.util.StringHelper;
import se.trixon.yaya.Yaya;

/**
 *
 * @author Patrik Karlström
 */
public class GameCell {

    @SerializedName("bonus")
    private boolean mBonus;
    @SerializedName("formula")
    private String mFormula;
    @SerializedName("id")
    private String mId;
    @SerializedName("lim")
    private int mLim = 0;
    @SerializedName("locals")
    private final HashMap<String, String> mLocals = new HashMap<>();
    @SerializedName("max")
    private int mMax = 0;
    @SerializedName("isPlayable")
    private boolean mPlayable;
    @SerializedName("isResult")
    private boolean mResult;
    @SerializedName("isRollCounter")
    private boolean mRollCounter;
    @SerializedName("section")
    private GameSection mSection;
    @SerializedName("isSum")
    private boolean mSum;
    @SerializedName("sum_rows")
    private String mSumRows;
    private transient TreeSet<Integer> mSumSet;
    @SerializedName("title")
    private String mTitle;

    public GameCell() {
    }

    public String getFormula() {
        return mFormula;
    }

    public String getId() {
        return mId;
    }

    public int getLim() {
        return mLim;
    }

    public HashMap<String, String> getLocals() {
        return mLocals;
    }

    public int getMax() {
        return mMax;
    }

    public GameSection getSection() {
        return mSection;
    }

    public String getSumRows() {
        return mSumRows;
    }

    public TreeSet<Integer> getSumSet() {
        return mSumSet;
    }

    public String getTitle() {
        return mLocals.getOrDefault("title" + Yaya.getLanguageSuffix(), mTitle);
    }

    public boolean isBonus() {
        return mBonus;
    }

    public boolean isPlayable() {
        return mPlayable;
    }

    public boolean isResult() {
        return mResult;
    }

    public boolean isRollCounter() {
        return mRollCounter;
    }

    public boolean isSum() {
        return mSum;
    }

    public void postLoad() {
        if (mFormula == null) {
            mFormula = "";
        }

        if (getSumRows() != null) {
            setSumSet(getSumRows());
        }
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

    public void setResult(boolean result) {
        mResult = result;
    }

    public void setRollCounter(boolean rollCounter) {
        mRollCounter = rollCounter;
    }

    public void setSection(GameSection section) {
        mSection = section;
    }

    public void setSum(boolean sum) {
        mSum = sum;
    }

    public void setSumRows(String sumRows) {
        mSumRows = sumRows;
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

}
