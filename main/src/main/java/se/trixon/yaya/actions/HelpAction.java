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
package se.trixon.yaya.actions;

import static j2html.TagCreator.*;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle;
import org.openide.util.NbBundle.Messages;
import se.trixon.almond.util.Dict;
import se.trixon.almond.util.swing.dialogs.HtmlPanel;
import se.trixon.yaya.Options;
import se.trixon.yaya.rules.GameVariant;
import se.trixon.yaya.rules.RuleManager;

@ActionID(
        category = "Yaya",
        id = "se.trixon.yaya.actions.HelpAction"
)
@ActionRegistration(
        displayName = "#CTL_HelpAction"
)
@ActionReferences({
    @ActionReference(path = "Shortcuts", name = "F1")
})
@Messages("CTL_HelpAction=Help")
public final class HelpAction implements ActionListener {

    private final ResourceBundle mBundle = NbBundle.getBundle(HelpAction.class);
    private final Options mOptions = Options.getInstance();

    @Override
    public void actionPerformed(ActionEvent e) {
        String[] options = {Dict.CLOSE.toString()};

        var htmlPanel = new HtmlPanel(getHelp());
        htmlPanel.setPreferredSize(new Dimension(600, 740));
        var d = new DialogDescriptor(
                htmlPanel,
                Dict.HELP.toString(),
                true,
                options,
                Dict.CLOSE.toString(),
                0,
                null,
                null
        );

        d.setMessageType(DialogDescriptor.PLAIN_MESSAGE);
        d.setClosingOptions(options);

        DialogDisplayer.getDefault().notify(d);
    }

    private String getHelp() {
        var ruleManager = RuleManager.getInstance();
        var rule = ruleManager.getRule(mOptions.getRuleId());
        var titles = new ArrayList<String>(Arrays.asList(ruleManager.getTitles()));
        var variants = new ArrayList<GameVariant>(Arrays.asList(GameVariant.values()));

        var html = html(
                body(
                        h1(mBundle.getString("help_intro")),
                        p(mBundle.getString("help_header_quickstart")),
                        hr(),
                        ul(
                                li(mBundle.getString("help_bullit_5")),
                                li(mBundle.getString("help_bullit_1")),
                                li(mBundle.getString("help_bullit_2")),
                                li(mBundle.getString("help_bullit_3")),
                                li(mBundle.getString("help_bullit_4"))
                        ),
                        p(mBundle.getString("help_header_games")),
                        hr(),
                        ul(
                                each(titles, title
                                        -> li(title))
                        ),
                        p(mBundle.getString("help_header_variants")),
                        hr(),
                        ul(
                                each(variants, variant
                                        -> li(variant.getLocalized()))
                        ),
                        p(mBundle.getString("help_rules").formatted(rule.getTitle())),
                        hr(),
                        //TODO Populate rules section
                        p()
                ),
                hr()
        );

        return html.render();
    }

}
