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
package se.trixon.yaya.rules;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import se.trixon.yaya.Yaya;

/**
 *
 * @author Patrik Karlström
 */
public class Rule {

    @SerializedName("author")
    private String mAuthor;
    @SerializedName("date")
    private String mDate;
    @SerializedName("default_variant")
    private int mDefaultVariant;
    @SerializedName("id")
    private String mId;
    @SerializedName("locals")
    private final HashMap<String, String> mLocals = new HashMap<>();
    @SerializedName("dice")
    private int mNumOfDice;
    @SerializedName("rolls")
    private int mNumOfRolls;
    @SerializedName("rows")
    private GameRows mRows = new GameRows();
    @SerializedName("title")
    private String mTitle;
    @SerializedName("variants")
    private ArrayList<GameVariant> mVariants;

    public Rule() {
    }

    public String getAuthor() {
        return mAuthor;
    }

    public String getDate() {
        return mDate;
    }

    public int getDefaultVariant() {
        return mDefaultVariant;
    }

    public String getId() {
        return mId;
    }

    public int getLocalizedIndexForVariantId(String id) {
        int index = -1;
        String[] localizedVariants = getLocalizedVariants().clone();
        Arrays.sort(localizedVariants);
        String localizedVariant = GameVariant.valueOf(id.toUpperCase()).getLocalized();

        for (int i = 0; i < localizedVariants.length; i++) {
            if (localizedVariant.equalsIgnoreCase(localizedVariants[i])) {
                index = i;
                break;
            }
        }

        return index;
    }

    public String[] getLocalizedVariants() {
        String[] localized = new String[mVariants.size()];

        for (int i = 0; i < mVariants.size(); i++) {
            localized[i] = mVariants.get(i).getLocalized();
        }

        return localized;
    }

    public HashMap<String, String> getLocals() {
        return mLocals;
    }

    public int getNumOfDice() {
        return mNumOfDice;
    }

    public int getNumOfRolls() {
        return mNumOfRolls;
    }

    public int getResultRow() {
        int row = -1;
        for (int i = 0; i < mRows.size(); i++) {
            if (mRows.get(i).isResult()) {
                row = i;
                break;
            }
        }

        return row;
    }

    public GameRows getRows() {
        return mRows;
    }

    public String getTitle() {
        return mLocals.getOrDefault("title" + Yaya.getLanguageSuffix(), mTitle);
    }

    public String getVariantByTitle(String title) {
        String result = "standard";

        for (GameVariant variant : mVariants) {
            if (variant.getLocalized().equalsIgnoreCase(title)) {
                result = variant.name().toLowerCase();
            }
        }

        return result;
    }

    public String[] getVariants() {
        return mVariants.toArray(String[]::new);
    }

    public void postLoad() {
        mRows.forEach(row -> {
            row.postLoad();
        });
    }

    public void setAuthor(String author) {
        mAuthor = author;
    }

    public void setDate(String date) {
        mDate = date;
    }

    public void setDefaultVariant(int defaultVariant) {
        mDefaultVariant = defaultVariant;
    }

    public void setId(String id) {
        mId = id;
    }

    public void setNumOfDice(int numOfDice) {
        mNumOfDice = numOfDice;
    }

    public void setNumOfRolls(int numOfRolls) {
        mNumOfRolls = numOfRolls;
    }

    public void setRows(GameRows rows) {
        mRows = rows;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public void setVariants(ArrayList<GameVariant> variants) {
        mVariants = variants;
    }

    @Override
    public String toString() {
        return Yaya.GSON.toJson(this);
    }

}
