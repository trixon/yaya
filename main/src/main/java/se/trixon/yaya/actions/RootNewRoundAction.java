/*
 * Copyright 2024 pata.
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
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;
import se.trixon.almond.util.Dict;
import se.trixon.yaya.NewGamePanel;

@ActionID(
        category = "Game",
        id = "se.trixon.yaya.actions.NewRoundAction"
)
@ActionRegistration(
        displayName = "#CTL_NewRoundAction"
)
@ActionReference(path = "Shortcuts", name = "N")
@Messages("CTL_NewRoundAction=New round")
public final class RootNewRoundAction extends YAction2 implements ActionListener {

    private final NewGamePanel mNewGamePanel = new NewGamePanel();

    public RootNewRoundAction() {
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        mNewGamePanel.load();

        var d = new NotifyDescriptor(
                mNewGamePanel,
                Dict.Game.NEW_ROUND.toString(),
                NotifyDescriptor.OK_CANCEL_OPTION,
                NotifyDescriptor.PLAIN_MESSAGE,
                new String[]{Dict.CANCEL.toString(), Dict.PLAY.toString()},
                Dict.PLAY.toString());

        if (Dict.PLAY.toString() == DialogDisplayer.getDefault().notify(d)) {
            mNewGamePanel.save();
            mYaya.onRequestNewGameStart();
        }
    }
}
