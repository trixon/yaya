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
import java.util.Locale;
import java.util.ResourceBundle;
import org.apache.commons.lang3.StringUtils;
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
        htmlPanel.setPreferredSize(new Dimension(680, 740));
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
        var variants = rule.getVariants();
        var cellRules = new ArrayList<CellRule>();

        for (var gameCell : rule.getGameColumn()) {
            var args = gameCell.getFormula();
            var desc = args;

            if (gameCell.isRollCounter()) {
                desc = "The roll counter";
            } else if (StringUtils.startsWith(args, "sum ")) {
                var arg = StringUtils.split(args);
                if (arg.length == 2) {
                    desc = "The sum of all %s".formatted(gameCell.getTitle().toLowerCase(Locale.getDefault()));
                } else {
                    desc = "%s points for a total of %s".formatted(arg[2], arg[1]);
                }
            } else if (gameCell.isBonus()) {
                desc = "%d points if the sum of rows %s is greater than %d".formatted(gameCell.getMax(), gameCell.getSumRows(), gameCell.getLim());
            } else if (!gameCell.isBonus() && !gameCell.isRollCounter() && gameCell.isSum()) {
                desc = "The sum of rows %s".formatted(gameCell.getSumRows().replace(",", " & "));
            } else if (StringUtils.startsWith(args, "duplicates ")) {
                if (gameCell.getMax() == gameCell.getLim()) {
                    desc = "%d points if %s dice are showing the same number".formatted(gameCell.getMax(), StringUtils.split(args)[1]);
                } else {
                    desc = "The sum of %s dice showing the same number".formatted(StringUtils.split(args)[1]);
                }
            } else if (StringUtils.startsWith(args, "pair ")) {
                desc = "The sum of dice in %s different pairs of dice".formatted(StringUtils.split(args)[1]);
            } else if (StringUtils.startsWith(args, "straight ")) {
                desc = "The sum of all dice for sequence %s".formatted(gameCell.getSumRows());
            } else if (StringUtils.startsWith(args, "house ")) {
                var arg = StringUtils.split(args);
                desc = "The sum of all dice for a set of %s and a different %s".formatted(arg[1], arg[2]);
            } else if (StringUtils.equals(args, "sum")) {
                desc = "The sum of all dice";
            } else if (StringUtils.startsWith(args, "custom_crag")) {
                desc = "%s points for a total of 13 and a pair".formatted(StringUtils.split(args)[1]);
            } else if (StringUtils.startsWith(args, "equals")) {
                var arg = StringUtils.split(args);
                desc = "%s points for %s".formatted(arg[2], arg[1]);
            }

            var max = Integer.toString(gameCell.getMax());
            var cellRule = new CellRule(gameCell.getTitle(), max, desc);
            cellRules.add(cellRule);
        }

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
                        p(mBundle.getString("help_rules").formatted(rule.getTitle(), rule.getNumOfDice(), rule.getNumOfRolls())),
                        hr(),
                        table(
                                tr(
                                        th(Dict.NAME.toString()),
                                        th("Max"),
                                        th("Score")
                                ),
                                tbody(
                                        each(cellRules, cellRule
                                                -> tr(
                                                td(cellRule.title),
                                                td(cellRule.max),
                                                td(cellRule.desc())
                                        )
                                        )
                                ),
                                p()
                        ),
                        p(mBundle.getString("help_header_variants")),
                        hr(),
                        ul(
                                each(variants, variant
                                        -> li(variant.getLocalized()))
                        ),
                        hr()
                ));

        return html.render();
    }

    private record CellRule(String title, String max, String desc) {

    }
}
