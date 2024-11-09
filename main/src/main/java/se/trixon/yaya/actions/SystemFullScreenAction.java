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
import org.apache.commons.lang3.SystemUtils;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.awt.Actions;
import org.openide.util.NbBundle;

/**
 *
 * @author Patrik Karlström
 */
@ActionID(
        category = "Help",
        id = "se.trixon.yaya.actions.FullScreenAction"
)
@ActionRegistration(displayName = "#CTL_FullScreenAction")
@ActionReference(path = "Shortcuts", name = "F")
@NbBundle.Messages("CTL_FullScreenAction=Full screen")
public class SystemFullScreenAction extends BaseAction {

    @Override
    public void actionPerformed(ActionEvent e) {
        if (SystemUtils.IS_OS_MAC) {
            Actions.forID("Almond", "se.trixon.almond.nbp.osx.actions.ToggleFullScreenAction").actionPerformed(null);
        } else {
            Actions.forID("Window", "org.netbeans.core.windows.actions.ToggleFullScreenAction").actionPerformed(null);
        }
    }
}
