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
import java.util.Comparator;
import javax.swing.JOptionPane;
import se.trixon.almond.util.AlmondUI;
import se.trixon.almond.util.Dict;
import se.trixon.almond.util.swing.SwingHelper;
import se.trixon.almond.util.swing.dialogs.HtmlPanel;

/**
 *
 * @author Patrik Karlström
 */
public class GameOverDialog {

    private final HtmlPanel mHtmlPanel = new HtmlPanel();
    private final Yaya mYaya = Yaya.getInstance();

    public static GameOverDialog getInstance() {
        return Holder.INSTANCE;
    }

    private GameOverDialog() {
        mHtmlPanel.setPreferredSize(SwingHelper.getUIScaledDim(400, 490));
        mHtmlPanel.getScrollPane().setBorder(null);
    }

    public void display(ArrayList<GameOverItem> gameOverItems) {
        gameOverItems.sort(Comparator.comparing(GameOverItem::score).reversed());
        for (int i = 0; i < gameOverItems.size(); i++) {
            gameOverItems.get(i).position = i + 1;
        }

        var html = html(
                body(
                        h1(Dict.Game.RESULT.toString()),
                        table(
                                tbody(
                                        each(gameOverItems, gameOverItem
                                                -> tr(
                                                td(Integer.toString(gameOverItem.position)),
                                                td(gameOverItem.player.getName()).withStyle("width:100%;"),
                                                td(Integer.toString(gameOverItem.score)).withStyle("text-align: right;")
                                        ).withStyle("border-bottom: 1px solid #ddd;")
                                        )
                                )
                        ).withStyle("font-size: 180%;width:100%;")
                ));

        display(html.render());
    }

    private void display(String message) {
        mHtmlPanel.setHtml(message);
        var newDialog = Dict.Game.NEW_ROUND.toString() + "…";
        var newQuick = Dict.Game.NEW_ROUND.toString();

        var buttons = new String[]{newDialog, newQuick};
        var result = JOptionPane.showOptionDialog(AlmondUI.getInstance().getFrame(), mHtmlPanel, Dict.Game.GAME_OVER.toString(), JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, buttons, newQuick);

        if (result == 0) {
            ActionManager.getInstance().getAction(ActionManager.NEW).actionPerformed(null);
        } else if (result == 1) {
            mYaya.onRequestNewGameStart();
        }
    }

    private static class Holder {

        private static final GameOverDialog INSTANCE = new GameOverDialog();
    }
}
