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

import java.awt.event.ActionEvent;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle;
import se.trixon.yaya.Options;

/**
 *
 * @author Patrik Karlström
 */
@ActionID(
        category = "Game",
        id = "se.trixon.yaya.actions.IndicatorAction"
)
@ActionRegistration(displayName = "#CTL_IndicatorAction")
@ActionReference(path = "Shortcuts", name = "F7")
@NbBundle.Messages("CTL_IndicatorAction=Indicators")
public class ScorecardIndicatorAction extends BaseAction {

    @Override
    public void actionPerformed(ActionEvent e) {
        mOptions.invert(Options.KEY_SHOW_INDICATORS, Options.DEFAULT_SHOW_INDICATORS);
    }
}
