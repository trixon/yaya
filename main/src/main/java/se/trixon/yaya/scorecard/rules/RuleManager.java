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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.openide.util.Lookup;
import se.trixon.yaya.Options;

/**
 *
 * @author Patrik Karlström
 */
public class RuleManager {

    private final ObjectProperty<ObservableList<Rule>> mItemsProperty = new SimpleObjectProperty<>();
    private final Options mOptions = Options.getInstance();
    private final ObjectProperty<Rule> mRuleProperty = new SimpleObjectProperty<>();
    private ArrayList<Rule> mRules;
    private final transient StringProperty mTitleProperty = new SimpleStringProperty();

    public static RuleManager getInstance() {
        return Holder.INSTANCE;
    }

    private RuleManager() {
        mItemsProperty.set(FXCollections.observableArrayList());
        mRuleProperty.addListener((p, o, n) -> {
            mOptions.setRuleId(n.getId());
            mTitleProperty.set(n.getTitle());
        });
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
            index = getIndexForId(Options.DEFAULT_RULE_ID);
        }

        return index;
    }

    public ObservableList<Rule> getItems() {
        return mItemsProperty.get();
    }

    public Rule getRule(String id) {
        for (var rule : mRules) {
            if (rule.getId().equalsIgnoreCase(id)) {
                return rule;
            }
        }

        //TODO Throw something?
        return null;
    }

    public Rule getRule() {
        return mRuleProperty.get();
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

    public StringProperty titleProperty() {
        return mTitleProperty;
    }

    public String[] getTitles() {
        return mRules.stream().map(k -> k.getTitle()).toArray(String[]::new);
    }

    public void init() {
        mRules = new ArrayList<>();

        for (var ruleProvider : Lookup.getDefault().lookupAll(RuleProvider.class)) {
            var rule = ruleProvider.load();
            mRules.add(rule);
            getItems().add(rule);
        }

        FXCollections.sort(getItems(), Comparator.comparing(Rule::getTitle));
        Collections.sort(mRules, Comparator.comparing(Rule::getTitle));
        setRule(getRule(mOptions.getRuleId()));
    }

    public ObjectProperty<ObservableList<Rule>> itemsProperty() {
        return mItemsProperty;
    }

    public ObjectProperty<Rule> ruleProperty() {
        return mRuleProperty;
    }

    public void setRule(Rule rule) {
        mRuleProperty.set(rule);
    }

    private static class Holder {

        private static final RuleManager INSTANCE = new RuleManager();
    }
}
