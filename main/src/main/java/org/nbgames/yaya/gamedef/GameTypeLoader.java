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

import java.util.Collections;
import java.util.LinkedList;
import org.nbgames.core.api.NbGames;
import org.nbgames.yaya.Yaya;
import org.nbgames.yaya.api.YayaGameProvider;
import org.openide.util.Lookup;

/**
 *
 * @author Patrik Karlström
 */
public class GameTypeLoader {

    private LinkedList<GameType> mGameTypes;

    public static GameTypeLoader getInstance() {
        return Holder.INSTANCE;
    }

    private GameTypeLoader() {
    }

    public String[] getIdArray() {
        String[] result = new String[mGameTypes.size()];

        for (int i = 0; i < result.length; i++) {
            result[i] = mGameTypes.get(i).getId();
        }

        return result;
    }

    public String getIdForIndex(int index) {
        return mGameTypes.get(index).getId();
    }

    public int getIndexForId(String id) {
        int index = -1;

        for (int i = 0; i < getIdArray().length; i++) {
            if (id.equalsIgnoreCase(getIdArray()[i])) {
                index = i;
                break;
            }
        }

        if (index == -1) {
            index = getIndexForId("default");
        }

        return index;
    }

    public String getTitle(String id) {
        for (GameType gameType : mGameTypes) {
            if (gameType.getId().equalsIgnoreCase(id)) {
                return gameType.getTitle();
            }
        }
        // TODO Throw something?
        return "";
    }

    public String[] getTitleArray() {
        String[] result = new String[mGameTypes.size()];

        for (int i = 0; i < result.length; i++) {
            result[i] = mGameTypes.get(i).getTitle();
        }

        return result;
    }

    public GameType getType(String id) {
        for (GameType gameType : mGameTypes) {
            if (gameType.getId().equalsIgnoreCase(id)) {
                return gameType;
            }
        }

        //TODO Throw something?
        return null;
    }

    public void init() {
        mGameTypes = new LinkedList<>();

        for (YayaGameProvider gameProvider : Lookup.getDefault().lookupAll(YayaGameProvider.class)) {
            NbGames.outln(Yaya.LOG_TITLE, String.format("Found GameLoader in %s.", gameProvider.getId()));
            final GameType gameType = GameType.restore(gameProvider.getDefinition());
            gameType.postRestore();
            mGameTypes.add(gameType);
        }

        Collections.sort(mGameTypes, GameType.NameComparator);
    }

    private static class Holder {

        private static final GameTypeLoader INSTANCE = new GameTypeLoader();
    }
}
