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

import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.util.StringConverter;
import se.trixon.almond.util.Dict;
import se.trixon.yaya.scorecard.rules.Rule;
import se.trixon.yaya.scorecard.rules.RuleManager;

/**
 *
 * @author Patrik Karlström <patrik@trixon.se>
 */
public class NewGamePane extends GridPane {

    private final ComboBox<Rule> mGameComboBox = new ComboBox<>();
    private final Options mOptions = Options.getInstance();
    private final RuleManager mRuleManager = RuleManager.getInstance();

    public NewGamePane() {
        setGridLinesVisible(true);
        createUI();
    }

    public void load() {
        mGameComboBox.setValue(mRuleManager.getRule());
//        selectPlayersPanel.load();
    }

    public void save() {
        mRuleManager.ruleProperty().set(mGameComboBox.getValue());
//        selectPlayersPanel.save();
//        mOptions.setPlayers(mOptions.createPlayers());
    }

    private void createUI() {
        var gameLabel = new Label(Dict.Game.GAME.toString());
        mGameComboBox.itemsProperty().bind(mRuleManager.itemsProperty());
        mGameComboBox.prefWidthProperty().bind(widthProperty());
        mGameComboBox.setConverter(new StringConverter<Rule>() {
            @Override
            public Rule fromString(String string) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public String toString(Rule rule) {
                return rule != null ? rule.getTitle() : "?";
            }
        });

        int col = 0;
        int row = 0;
//        gameLabel.setPrefWidth(9999);
        add(gameLabel, col, row, REMAINING, 1);
        add(mGameComboBox, col, ++row, REMAINING, 1);
    }

}
