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

import java.util.Random;
import java.util.TreeSet;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import org.apache.commons.lang3.StringUtils;
import se.trixon.almond.util.Dict;
import se.trixon.almond.util.fx.FxHelper;
import se.trixon.yaya.scorecard.rules.Rule;
import se.trixon.yaya.scorecard.rules.RuleManager;

/**
 *
 * @author Patrik Karlström <patrik@trixon.se>
 */
public class NewGamePane extends GridPane {

    private static final int MAX_NUM_OF_PLAYERS = 8;
    private final ComboBox<Rule> mGameComboBox = new ComboBox<>();
    private final ComboBox<String>[] mNameComboBoxes = new ComboBox[MAX_NUM_OF_PLAYERS];
    private final Options mOptions = Options.getInstance();
    private final Spinner<Integer> mPlayerSpinner = new Spinner<>(1, MAX_NUM_OF_PLAYERS, 2);
    private final RuleManager mRuleManager = RuleManager.getInstance();
    private final Button mShuffleButton = new Button(Dict.Game.SHUFFLE.toString());

    public NewGamePane() {
        setGridLinesVisible(true);
        createUI();
        initListeners();
    }

    public void load() {
        initCombos();
        mGameComboBox.setValue(mRuleManager.getRule());
        String storedNames = mOptions.get(Options.KEY_PLAYERS, Options.DEFAULT_PLAYERS);

        if (!StringUtils.isBlank(storedNames)) {
            var names = StringUtils.splitPreserveAllTokens(storedNames, ';');
            for (int i = 0; i < mNameComboBoxes.length; i++) {
                var comboBox = mNameComboBoxes[i];
                comboBox.setValue(names[i]);
            }
        }

        mPlayerSpinner.getValueFactory().setValue(mOptions.getNumOfPlayers());
    }

    public void save() {
        mRuleManager.ruleProperty().set(mGameComboBox.getValue());

        var names = new String[MAX_NUM_OF_PLAYERS];
        var allNames = new TreeSet<String>();

        for (int i = 0; i < mNameComboBoxes.length; i++) {
            var comboBox = mNameComboBoxes[i];
            names[i] = StringUtils.defaultIfBlank(comboBox.getValue(), "-");
            allNames.add(StringUtils.defaultIfBlank(comboBox.getEditor().getText(), "-"));

            for (int j = 0; j < comboBox.getItems().size(); j++) {
                String name = comboBox.getItems().get(j);
                allNames.add(StringUtils.defaultIfBlank(name, "-"));
            }
        }

        mOptions.put(Options.KEY_PLAYERS, String.join(";", names));
        mOptions.put(Options.KEY_PLAYERS_ALL, String.join(";", allNames));
        mOptions.setNumOfPlayers(mPlayerSpinner.getValue());
        mOptions.setPlayers(mOptions.createPlayers());
    }

    private void createUI() {
        var gameLabel = new Label(Dict.Game.GAME.toString());
        var playersLabel = new Label(Dict.Game.PLAYERS.toString());

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

        var leftBox = new VBox();
        var rightBox = new VBox();
        for (int i = 0; i < MAX_NUM_OF_PLAYERS; i++) {
            var comboBox = new ComboBox<String>();
            comboBox.setEditable(true);
            mNameComboBoxes[i] = comboBox;
            var parent = (i & 1) == 0 ? leftBox : rightBox;
            parent.getChildren().add(mNameComboBoxes[i]);

        }
        int col = 0;
        int row = 0;
//        gameLabel.setPrefWidth(9999);
        add(gameLabel, col, row, REMAINING, 1);
        add(mGameComboBox, col, ++row, REMAINING, 1);
        add(playersLabel, col, ++row, REMAINING, 1);
        add(mPlayerSpinner, 0, ++row, 1, 1);
        add(mShuffleButton, 1, row, 1, 1);
        add(leftBox, 0, ++row, 1, 1);
        add(rightBox, 1, row, 1, 1);

        FxHelper.autoSizeColumn(this, 2);
    }

    private void initCombos() {
        var names = StringUtils.splitPreserveAllTokens(mOptions.get(Options.KEY_PLAYERS_ALL, Options.DEFAULT_PLAYERS_ALL), ";");

        for (var comboBox : mNameComboBoxes) {
            comboBox.getItems().setAll(names);
            comboBox.setEditable(true);
        }
    }

    private void initListeners() {
        mPlayerSpinner.valueProperty().addListener((p, o, n) -> {
            for (int i = 0; i < MAX_NUM_OF_PLAYERS; i++) {
                mNameComboBoxes[i].setDisable(i > n - 1);
            }
        });

        mShuffleButton.setOnAction(actionEvent -> {
            int numOfPlayers = mPlayerSpinner.getValue();
            var r = new Random();

            for (int i = 0; i < numOfPlayers; i++) {
                int swapWith = r.nextInt(numOfPlayers);
                var name0 = mNameComboBoxes[i];
                var name1 = mNameComboBoxes[swapWith];
                var editor0 = name0.getEditor();
                var editor1 = name1.getEditor();
                String temp = (String) editor0.getText();
                name0.setValue(editor1.getText());
                name1.setValue(temp);
            }
        });
    }

}
