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

import com.dlsc.workbenchfx.Workbench;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.controlsfx.control.ToggleSwitch;
import org.controlsfx.control.action.ActionUtils;
import se.trixon.almond.util.Dict;
import se.trixon.almond.util.SystemHelper;
import se.trixon.almond.util.fx.FxHelper;
import se.trixon.almond.util.swing.DelayedResetRunner;
import se.trixon.yaya.actions.YActions;
import se.trixon.yaya.themes.Theme;

/**
 *
 * @author Patrik Karlström <patrik@trixon.se>
 */
public class SystemDrawer extends BorderPane {

    private final ResourceBundle mBundle = SystemHelper.getBundle(App.class, "Bundle");
    private final ToggleSwitch mDiceReversedToggleSwitch = new ToggleSwitch(mBundle.getString("diceRreversedDirection"));
    private final Options mOptions = Options.getInstance();
    private final ToggleSwitch mPlaySoundToggleSwitch = new ToggleSwitch(Dict.Game.PLAY_SOUND.toString());
    private final ToggleSwitch mScoreCardIndicatorToggleSwitch = new ToggleSwitch(mBundle.getString("scorecardIndicators"));
    private final ToggleSwitch mScoreCardLimToggleSwitch = new ToggleSwitch(mBundle.getString("scorecardLim"));
    private final ToggleSwitch mScoreCardMaxToggleSwitch = new ToggleSwitch(mBundle.getString("scorecardMax"));
    private final Label mScorecardSizeLabel = new Label(Dict.SIZE.toString());
    private final Slider mScorecardSizeSlider = new Slider(8, 72, mOptions.getFontSize());
    private ComboBox<Theme> mThemeComboBox;
    private final ThemeManager mThemeManager = ThemeManager.getInstance();

    public SystemDrawer() {
        getStylesheets().add(Workbench.class.getResource("css/main.css").toExternalForm());
        getStyleClass().add("navigation-drawer");

        createUI();
        initListeners();
        initBindings();
    }

    private void bindWidths(Region parent, ObservableList<Node> children) {
        for (var node : children) {
            if (node instanceof Region region) {
                region.prefWidthProperty().bind(parent.widthProperty());
            }
        }
    }

    private void createUI() {
        var drawerBox = new VBox();
        drawerBox.getStyleClass().add("drawer-box");

        var header = new BorderPane();
        header.getStyleClass().add("header");

        var backIconShape = new StackPane();
        backIconShape.getStyleClass().add("shape");

        var backButton = new Button("", backIconShape);
        backButton.getStyleClass().add("icon");
        backButton.setId("back-button");
        backButton.setOnAction(evt -> Yaya.getInstance().getWorkbench().hideDrawer());

        header.setTop(backButton);
        drawerBox.getChildren().addAll(header);

        setPrefWidth(FxHelper.getUIScaled(300));
        mThemeComboBox = new ComboBox<>();
        var optionsBox = new VBox(FxHelper.getUIScaled(8));
        optionsBox.getChildren().addAll(
                mScorecardSizeLabel,
                mScorecardSizeSlider,
                new Label(Dict.THEME.toString()),
                mThemeComboBox,
                mPlaySoundToggleSwitch,
                mScoreCardLimToggleSwitch,
                mScoreCardMaxToggleSwitch,
                mScoreCardIndicatorToggleSwitch,
                mDiceReversedToggleSwitch
        );
        optionsBox.setPadding(FxHelper.getUIScaledInsets(8));

        mThemeComboBox.getItems().addAll(mThemeManager.getItems());
        mThemeComboBox.setValue(mThemeManager.getTheme());

        mThemeComboBox.valueProperty().addListener((p, o, n) -> {
            mThemeManager.setTheme(n);
            mOptions.setThemeId(n.getId());
        });

        var fontResetRunner = new DelayedResetRunner(50, () -> {
            mOptions.setFontSize((int) mScorecardSizeSlider.getValue());
        });

        mScorecardSizeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            fontResetRunner.reset();
        });
        setTop(drawerBox);
        setCenter(optionsBox);

        var buttonBox = new VBox(FxHelper.getUIScaled(16));
        var removePlayerButton = new Button(mBundle.getString("removePlayerTitle"));
        var helpButton = ActionUtils.createButton(YActions.forId("core", "help"));
        var aboutButton = ActionUtils.createButton(YActions.forId("core", "about"));
        var quitButton = ActionUtils.createButton(YActions.forId("core", "quit"));
        buttonBox.getChildren().addAll(
                removePlayerButton,
                helpButton,
                aboutButton,
                quitButton
        );
        buttonBox.setPadding(FxHelper.getUIScaledInsets(32));

        bindWidths(optionsBox, optionsBox.getChildren());
        bindWidths(buttonBox, buttonBox.getChildren());

        setBottom(buttonBox);
        setStyle("-fx-background-color: wheat;");
    }

    private void initBindings() {
        mScoreCardLimToggleSwitch.selectedProperty().bindBidirectional(mOptions.displayLimProperty());
        mScoreCardMaxToggleSwitch.selectedProperty().bindBidirectional(mOptions.displayMaxProperty());
        mScoreCardIndicatorToggleSwitch.selectedProperty().bindBidirectional(mOptions.displayIndicatorsProperty());
        mPlaySoundToggleSwitch.selectedProperty().bindBidirectional(mOptions.playSoundProperty());
        mDiceReversedToggleSwitch.selectedProperty().bindBidirectional(mOptions.diceReversedProperty());
    }

    private void initListeners() {
    }
}
