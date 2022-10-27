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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import org.openide.util.Lookup;
import se.trixon.yaya.Yaya;

/**
 *
 * @author Patrik Karlström
 */
public class RuleManager {

    private ArrayList<Rule> mRules;

    public static RuleManager getInstance() {
        return Holder.INSTANCE;
    }

    private RuleManager() {
    }

    public String[] getIdArray() {
        return mRules.stream().map(k -> k.getId()).toArray(String[]::new);
//        String[] result = new String[mRules.size()];
//
//        for (int i = 0; i < result.length; i++) {
//            result[i] = mRules.get(i).getId();
//        }
//
//        return result;
    }

    public String getIdForIndex(int index) {
        return mRules.get(index).getId();
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
        for (var rule : mRules) {
            if (rule.getId().equalsIgnoreCase(id)) {
                return rule.getTitle();
            }
        }
        // TODO Throw something?
        return "";
    }

    public String[] getTitles() {
        return mRules.stream().map(k -> k.getTitle()).toArray(String[]::new);
    }

    public Rule getType(String id) {
        for (var rule : mRules) {
            if (rule.getId().equalsIgnoreCase(id)) {
                return rule;
            }
        }

        //TODO Throw something?
        return null;
    }

    public void init() {
        mRules = new ArrayList<>();

        for (var ruleProvider : Lookup.getDefault().lookupAll(RuleProvider.class)) {
            Yaya.outln(Yaya.LOG_TITLE, String.format("Found GameLoader in %s.", ruleProvider.getId()));
            mRules.add(ruleProvider.load());
        }

        Collections.sort(mRules, Comparator.comparing(Rule::getTitle));
    }

    private static class Holder {

        private static final RuleManager INSTANCE = new RuleManager();
    }
}
