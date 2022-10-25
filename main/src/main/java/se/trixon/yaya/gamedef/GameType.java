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
package se.trixon.yaya.gamedef;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import se.trixon.yaya.Yaya;

/**
 *
 * @author Patrik Karlström
 */
public class GameType {

    @SerializedName("author")
    private String mAuthor;
    @SerializedName("default_variant")
    private int mDefaultVariant;
    @SerializedName("format_version")
    private int mFileFormatVersion;
    @SerializedName("id")
    private String mId;
    @SerializedName("locals")
    private final HashMap<String, String> mLocals = new HashMap<>();
    @SerializedName("dice")
    private int mNumOfDice;
    @SerializedName("rolls")
    private int mNumOfRolls;
    @SerializedName("result_row")
    private int mResultRow;
    @SerializedName("rows")
    private GameRows mRows = new GameRows();
    @SerializedName("title")
    private String mTitle;
    @SerializedName("variants")
    private ArrayList<GameVariant> mVariants;
    @SerializedName("version_date")
    private String mVersionDate;
    @SerializedName("version_name")
    private String mVersionName;

    public GameType() {
    }

    public String getAuthor() {
        return mAuthor;
    }

    public int getDefaultVariant() {
        return mDefaultVariant;
    }

    public int getFileFormatVersion() {
        return mFileFormatVersion;
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
        return mResultRow;
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

    public String getVersionDate() {
        return mVersionDate;
    }

    public String getVersionName() {
        return mVersionName;
    }

    public void setAuthor(String author) {
        mAuthor = author;
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

    public void setResultRow(int resultRow) {
        mResultRow = resultRow;
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

    public void setVersionDate(String versionDate) {
        mVersionDate = versionDate;
    }

    public void setVersionName(String versionName) {
        mVersionName = versionName;
    }

    @Override
    public String toString() {
        return Yaya.GSON.toJson(this);
    }

    void postRestore() {
        mRows.forEach(row -> {
            row.postRestore();
        });
    }
}
