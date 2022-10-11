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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.SerializedName;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import org.apache.commons.io.FileUtils;
import org.nbgames.core.api.NbGames;

/**
 *
 * @author Patrik Karlström
 */
public class GameType {

    public static Comparator<GameType> NameComparator = (GameType type1, GameType type2) -> type1.getTitle().compareTo(type2.getTitle());
    private static final int FILE_FORMAT_VERSION = 1;
    private static final Gson GSON = new GsonBuilder()
            .setVersion(1.0)
            .serializeNulls()
            .setPrettyPrinting()
            .create();
    @SerializedName("author")
    private String mAuthor;
    @SerializedName("default_variant")
    private int mDefaultVariant;
    @SerializedName("format_version")
    private int mFileFormatVersion;
    @SerializedName("rows")
    private GameRows mRows = new GameRows();
    @SerializedName("id")
    private String mId;
    @SerializedName("dice")
    private int mNumOfDice;
    @SerializedName("rolls")
    private int mNumOfRolls;
    @SerializedName("result_row")
    private int mResultRow;
    @SerializedName("title")
    private String mTitle;
    @SerializedName("variants")
    private ArrayList<GameVariant> mVariants;
    @SerializedName("version_date")
    private String mVersionDate;
    @SerializedName("version_name")
    private String mVersionName;
    @SerializedName("i10n")
    private final HashMap<String, String> mI10n = new HashMap<>();

    public static GameType restore(String json) throws JsonSyntaxException {
        System.out.println(json);
        GameType gameType = GSON.fromJson(json, GameType.class);

        if (gameType.mFileFormatVersion != FILE_FORMAT_VERSION) {
            //TODO Handle file format version change
        }

        return gameType;
    }

    public GameType() {
    }

    public HashMap<String, String> getI10n() {
        return mI10n;
    }

    @Override
    public String toString() {
        return GSON.toJson(this);
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

    public GameRows getRows() {
        return mRows;
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

    public int getNumOfDice() {
        return mNumOfDice;
    }

    public int getNumOfRolls() {
        return 99;//mNumOfRolls; //TODO Remove me
    }

    public int getResultRow() {
        return mResultRow;
    }

    public String getTitle() {
        return mI10n.getOrDefault("title" + NbGames.getLanguageSuffix(), mTitle);
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
        return mVariants.toArray(new String[0]);
    }

    public String getVersionDate() {
        return mVersionDate;
    }

    public String getVersionName() {
        return mVersionName;
    }

    public void save(File file) throws IOException {
        mFileFormatVersion = FILE_FORMAT_VERSION;
        FileUtils.writeStringToFile(file, toString(), Charset.defaultCharset());
    }

    public void setAuthor(String author) {
        mAuthor = author;
    }

    public void setDefaultVariant(int defaultVariant) {
        mDefaultVariant = defaultVariant;
    }

    public void setRows(GameRows rows) {
        mRows = rows;
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

    void postRestore() {
        mRows.forEach((row) -> {
            row.postRestore();
        });
    }
}
