/*
 * Copyright 2022 Patrik Karlström.
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
package se.trixon.yaya.actions;

import com.dlsc.workbenchfx.model.WorkbenchDialog;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import org.openide.util.lookup.ServiceProvider;
import se.trixon.almond.util.Dict;
import se.trixon.yaya.NewGamePane;

/**
 *
 * @author Patrik Karlström
 */
@YAction.Description(category = "core", id = "newround")
@ServiceProvider(service = YAction.class)
public class RootNewRoundAction extends YAction {

    public RootNewRoundAction() {
        super(Dict.Game.NEW_ROUND.toString());
        var keyCodeCombination = new KeyCodeCombination(KeyCode.N);
        setAccelerator(keyCodeCombination);

        setEventHandler(eventHandler -> {
            var playButtonType = new ButtonType(Dict.PLAY.toString(), ButtonBar.ButtonData.OK_DONE);
            var newGamePane = new NewGamePane();
            newGamePane.load();

            var dialog = WorkbenchDialog.builder(
                    Dict.Game.NEW_ROUND.toString(),
                    newGamePane,
                    playButtonType, ButtonType.CANCEL
            ).onResult(buttonType -> {
                if (buttonType == playButtonType) {
                    newGamePane.save();

                    mYaya.onRequestNewGameStart();
                }
            }).build();

            getWorkbench().showDialog(dialog);

        });

        setPostInitRunnable(() -> {
            setAcceleratorForStage(keyCodeCombination);
            addTooltipKeyCode(keyCodeCombination);
        });
    }
}
