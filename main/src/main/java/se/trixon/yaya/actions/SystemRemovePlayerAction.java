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

import com.dlsc.workbenchfx.model.WorkbenchDialog;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;
import se.trixon.almond.util.fx.FxHelper;
import se.trixon.yaya.Options;

/**
 *
 * @author Patrik Karlström
 */
@YAction.Description(category = "core", id = "removePlayer")
@ServiceProvider(service = YAction.class)
public class SystemRemovePlayerAction extends YAction {

    public SystemRemovePlayerAction() {
        super(NbBundle.getMessage(YActions.class, "removePlayerTitle"));

        setEventHandler(eventHandler -> {
            var players = mOptions.getAllPlayers();
            var comboBox = new ComboBox<String>();
            comboBox.getItems().setAll(players);
            comboBox.getSelectionModel().select(0);
            comboBox.setMinWidth(FxHelper.getUIScaled(250));

            var dialog = WorkbenchDialog.builder(
                    mBundle.getString("removePlayerTitle"),
                    comboBox,
                    ButtonType.OK, ButtonType.CANCEL
            ).onResult(buttonType -> {
                if (buttonType == ButtonType.OK) {
                    var removedPlayer = comboBox.getValue();
                    var remainingPlayers = ArrayUtils.removeElement(players, removedPlayer);

                    if (remainingPlayers.length == 0) {
                        mOptions.put(Options.KEY_PLAYERS_ALL, Options.DEFAULT_PLAYERS_ALL);
                        mOptions.put(Options.KEY_PLAYERS, Options.DEFAULT_PLAYERS);
                    } else {
                        mOptions.put(Options.KEY_PLAYERS_ALL, String.join(";", remainingPlayers));

                        var defaultFillPlayer = remainingPlayers[0];
                        var contenders = mOptions.get(Options.KEY_PLAYERS, Options.DEFAULT_PLAYERS);
                        contenders = StringUtils.replace(contenders, removedPlayer, "");
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
            }).build();

            getWorkbench().showDialog(dialog);
        });
    }
}
