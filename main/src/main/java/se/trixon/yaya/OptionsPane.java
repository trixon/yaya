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

import java.util.Arrays;
import java.util.ResourceBundle;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.VBox;
import org.controlsfx.control.action.ActionGroup;
import org.controlsfx.control.action.ActionUtils;
import org.openide.util.NbBundle;
import se.trixon.almond.util.Dict;
import se.trixon.almond.util.SystemHelper;
import se.trixon.almond.util.swing.DelayedResetRunner;
import se.trixon.yaya.actions.YActions;
import se.trixon.yaya.themes.Theme;

/**
 *
 * @author Patrik Karlström <patrik@trixon.se>
 */
public class OptionsPane extends VBox {

    private final ResourceBundle mBundle = SystemHelper.getBundle(App.class, "Bundle");
    private final CheckBox mPlaySoundCheckBox;
    private final CheckBox mDiceReversedCheckBox = new CheckBox(mBundle.getString("diceRreversedDirection"));
    private final CheckBox mScoreCardLimCheckBox = new CheckBox(mBundle.getString("scorecardLim"));
    private final CheckBox mScoreCardMaxCheckBox = new CheckBox(mBundle.getString("scorecardMax"));
    private final CheckBox mScoreCardIndicatorCheckBox = new CheckBox(mBundle.getString("scorecardIndicators"));
    private final Button mRemovePlayerButton;
    private final ComboBox<Theme> mThemeComboBox;
    private final Label mScorecardSizeLabel = new Label(Dict.SIZE.toString());
    private final Options mOptions = Options.getInstance();
    private final Slider mScorecardSizeSlider = new Slider(8, 72, mOptions.getFontSize());
    private final ThemeManager mThemeManager = ThemeManager.getInstance();

    public OptionsPane() {
        mPlaySoundCheckBox = new CheckBox(Dict.Game.PLAY_SOUND.toString());
        mRemovePlayerButton = new Button(mBundle.getString("removePlayerTitle"));
        mThemeComboBox = new ComboBox<>();

        getChildren().addAll(
                mThemeComboBox,
                mScoreCardLimCheckBox,
                mScoreCardMaxCheckBox,
                mScoreCardIndicatorCheckBox,
                mScorecardSizeLabel,
                mScorecardSizeSlider,
                mDiceReversedCheckBox,
                mPlaySoundCheckBox,
                mRemovePlayerButton
        );

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

        var actions = Arrays.asList(
                new ActionGroup(Dict.SYSTEM.toString(),
                        YActions.forId("core", "playSound"),
                        ActionUtils.ACTION_SEPARATOR,
                        YActions.forId("core", "removePlayer")
                ),
                new ActionGroup(NbBundle.getMessage(YActions.class, "scorecard"),
                        YActions.forId("core", "lim"),
                        YActions.forId("core", "max"),
                        YActions.forId("core", "indicator")
                ),
                new ActionGroup(NbBundle.getMessage(YActions.class, "dice"),
                        YActions.forId("core", "reverse-dice")
                )
        );
    }
}
