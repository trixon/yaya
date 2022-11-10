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

import static j2html.TagCreator.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.ResourceBundle;
import org.apache.commons.lang3.StringUtils;
import se.trixon.almond.util.SystemHelper;
import se.trixon.yaya.rules.RuleManager;

public final class Help {

    private final ResourceBundle mBundle = SystemHelper.getBundle(Help.class, "Help");
    private final Options mOptions = Options.getInstance();

    public String getHelp() {
        var ruleManager = RuleManager.getInstance();
        var rule = ruleManager.getRule(mOptions.getRuleId());
        var titles = new ArrayList<String>(Arrays.asList(ruleManager.getTitles()));
        var variants = rule.getVariants();
        var cellRules = new ArrayList<CellRule>();

        for (var gameCell : rule.getGameColumn()) {
            var args = gameCell.getFormula();
            var desc = args;

            if (gameCell.isRollCounter()) {
                desc = mBundle.getString("help_rule_counter");
                continue;
            } else if (StringUtils.startsWith(args, "sum ")) { //NOI18N
                var arg = StringUtils.split(args);
                if (arg.length == 2) {
                    desc = mBundle.getString("help_rule_sum_all").formatted(gameCell.getTitle().toLowerCase(Locale.getDefault()));
                } else {
                    desc = mBundle.getString("help_rule_sum_fixed").formatted(arg[2], arg[1]);
                }
            } else if (StringUtils.startsWith(args, "sum_n ")) { //NOI18N
                var arg = StringUtils.split(args);
                desc = mBundle.getString("help_rule_sumn").formatted(arg[1]);
            } else if (StringUtils.startsWith(args, "sequence ")) { //NOI18N
                var arg = StringUtils.split(args);
                desc = mBundle.getString("help_rule_sequence").formatted(arg[4], arg[1]);
            } else if (gameCell.isBonus()) {
                desc = mBundle.getString("help_rule_bonus").formatted(gameCell.getMax(), gameCell.getSumRows(), gameCell.getLim());
            } else if (!gameCell.isBonus() && !gameCell.isRollCounter() && gameCell.isSum()) {
                desc = mBundle.getString("help_rule_sum_of_rows").formatted(gameCell.getSumRows().replace(",", " & "));
            } else if (StringUtils.startsWith(args, "duplicates ")) { //NOI18N
                if (gameCell.getMax() == gameCell.getLim()) {
                    desc = mBundle.getString("help_rule_duplicates_fixed").formatted(gameCell.getMax(), StringUtils.split(args)[1]);
                } else {
                    desc = mBundle.getString("help_rule_duplicates").formatted(StringUtils.split(args)[1]);
                }
            } else if (StringUtils.startsWith(args, "pair ")) { //NOI18N
                desc = mBundle.getString("help_rule_pair").formatted(StringUtils.split(args)[1]);
            } else if (StringUtils.startsWith(args, "straight ")) { //NOI18N
                desc = mBundle.getString("help_rule_straight").formatted(gameCell.getSumRows());
            } else if (StringUtils.startsWith(args, "house ")) { //NOI18N
                var arg = StringUtils.split(args);
                if (gameCell.getMax() == gameCell.getLim()) {
                    desc = mBundle.getString("help_rule_house_fixed").formatted(gameCell.getMax(), arg[1], arg[2]);
                } else {
                    desc = mBundle.getString("help_rule_house").formatted(arg[1], arg[2]);
                }
            } else if (StringUtils.equals(args, "sum")) { //NOI18N
                desc = mBundle.getString("help_rule_sum");
            } else if (StringUtils.startsWith(args, "custom_crag")) { //NOI18N
                desc = mBundle.getString("help_rule_custom_crag").formatted(StringUtils.split(args)[1]);
            } else if (StringUtils.startsWith(args, "equals")) { //NOI18N
                var arg = StringUtils.split(args);
                desc = mBundle.getString("help_rule_equals").formatted(arg[2], arg[1]);
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
                                        th(mBundle.getString("help_rule_header_row")),
                                        th(mBundle.getString("help_rule_header_max")),
                                        th(mBundle.getString("help_rule_header_score"))
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
