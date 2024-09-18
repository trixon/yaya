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
import java.awt.event.ActionListener;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle;
import se.trixon.almond.util.Dict;
import se.trixon.almond.util.swing.SwingHelper;
import se.trixon.almond.util.swing.dialogs.HtmlPanel;
import se.trixon.yaya.Help;

/**
 *
 * @author Patrik Karlström
 */
@ActionID(
        category = "Help",
        id = "se.trixon.yaya.actions.HelpAction"
)
@ActionRegistration(
        displayName = "#CTL_HelpAction"
)
@ActionReference(path = "Shortcuts", name = "F1")
@NbBundle.Messages("CTL_HelpAction=Help")
public final class RootHelpAction extends YAction2 implements ActionListener {

    private final HtmlPanel mHtmlPanel = new HtmlPanel(new Help().getHelp());

    @Override
    public void actionPerformed(ActionEvent e) {
        mHtmlPanel.setPreferredSize(SwingHelper.getUIScaledDim(720, 740));
        mHtmlPanel.getScrollPane().setBorder(null);
        var defaultBorder = (EmptyBorder) UIManager.get("OptionPane.border");
        UIManager.put("OptionPane.border", new EmptyBorder(0, 0, 0, 0));
        var d = new NotifyDescriptor(
                mHtmlPanel,
                Dict.HELP.toString(),
                NotifyDescriptor.DEFAULT_OPTION,
                NotifyDescriptor.PLAIN_MESSAGE,
                new String[]{Dict.CLOSE.toString()},
                Dict.CLOSE.toString());
        DialogDisplayer.getDefault().notify(d);
        UIManager.put("OptionPane.border", defaultBorder);
    }
}
