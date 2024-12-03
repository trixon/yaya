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
import java.util.stream.Stream;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle;
import se.trixon.yaya.Options;

/**
 *
 * @author Patrik Karlström
 */
@ActionID(
        category = "Game",
        id = "se.trixon.yaya.actions.RemovePlayerAction"
)
@ActionRegistration(displayName = "#CTL_RemovePlayerAction")
@NbBundle.Messages("CTL_RemovePlayerAction=Remove player")
public final class SystemRemovePlayerAction extends BaseAction {

    @Override
    public void actionPerformed(ActionEvent e) {
        var players = mOptions.getAllPlayers();
        var items = Stream.of(players)
                .map(player -> new NotifyDescriptor.QuickPick.Item(player, null))
                .toList();

        var d = new NotifyDescriptor.QuickPick(
                mBundle.getString("removePlayerInfo") + " ".repeat(35),
                Bundle.CTL_RemovePlayerAction(),
                items,
                true);

        if (DialogDescriptor.OK_OPTION == DialogDisplayer.getDefault().notify(d)) {
            var playersToRemove = items.stream()
                    .filter(item -> item.isSelected())
                    .map(item -> item.getLabel())
                    .toArray(String[]::new);

            var remainingPlayers = ArrayUtils.removeElements(players, playersToRemove);

            if (remainingPlayers.length == 0) {
                mOptions.put(Options.KEY_PLAYERS_ALL, Options.DEFAULT_PLAYERS_ALL);
                mOptions.put(Options.KEY_PLAYERS, Options.DEFAULT_PLAYERS);
            } else {
                mOptions.put(Options.KEY_PLAYERS_ALL, String.join(";", remainingPlayers));

                var defaultFillPlayer = remainingPlayers[0];
                var contenders = mOptions.get(Options.KEY_PLAYERS, Options.DEFAULT_PLAYERS);
                for (var removedPlayer : playersToRemove) {
                    contenders = StringUtils.replace(contenders, removedPlayer, "");
                }
                contenders = StringUtils.replace(contenders, ";;", ";%s;".formatted(defaultFillPlayer));

                if (StringUtils.startsWith(contenders, ";")) {
                    contenders = defaultFillPlayer + contenders;
                }

                if (StringUtils.endsWith(contenders, ";")) {
                    contenders = contenders + defaultFillPlayer;
                }

                mOptions.put(Options.KEY_PLAYERS, contenders);
            }
        }
    }
}
