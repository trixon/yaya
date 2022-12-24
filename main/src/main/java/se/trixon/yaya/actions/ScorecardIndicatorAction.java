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

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import org.controlsfx.control.action.ActionCheck;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Patrik Karlström
 */
@YAction.Description(category = "core", id = "indicator")
@ServiceProvider(service = YAction.class)
@ActionCheck
public class ScorecardIndicatorAction extends YAction {

    public ScorecardIndicatorAction() {
        super(NbBundle.getMessage(YActions.class, "indicator"));
        var keyCodeCombination = new KeyCodeCombination(KeyCode.F7);
        setAccelerator(keyCodeCombination);

        setEventHandler(eventHandler -> {
            mOptions.invert(mOptions.displayIndicatorsProperty());
        });

        setPostInitRunnable(() -> {
            setAcceleratorForStage(keyCodeCombination);
            addTooltipKeyCode(keyCodeCombination);
        });
    }
}
