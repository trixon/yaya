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
import javax.swing.border.EmptyBorder;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle;
import se.trixon.almond.util.Dict;
import se.trixon.almond.util.swing.SwingHelper;
import se.trixon.yaya.OptionsPanel;

/**
 *
 * @author Patrik Karlström
 */
@ActionID(
        category = "Game",
        id = "se.trixon.yaya.actions.OptionsAction"
)
@ActionRegistration(
        displayName = "#CTL_OptionsAction"
)
@ActionReference(path = "Shortcuts", name = "O")
@NbBundle.Messages("CTL_OptionsAction=Options")
public final class RootOptionsAction extends BaseAction {

    private final OptionsPanel mOptionsPanel = new OptionsPanel();

    public RootOptionsAction() {
        var borderSize = SwingHelper.getUIScaled(16);
        mOptionsPanel.setBorder(new EmptyBorder(borderSize, borderSize, 0, borderSize));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        var d = new NotifyDescriptor(
                mOptionsPanel,
                Dict.OPTIONS.toString(),
                NotifyDescriptor.DEFAULT_OPTION,
                NotifyDescriptor.PLAIN_MESSAGE,
                new String[]{Dict.CLOSE.toString()},
                Dict.CLOSE.toString());
        DialogDisplayer.getDefault().notify(d);
    }
}
