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
package org.nbgames.core.api.ui;

import java.util.ArrayList;
import java.util.Comparator;
import javax.swing.JButton;
import javax.swing.SwingUtilities;
import org.apache.commons.lang3.StringUtils;
import org.nbgames.core.api.GameController;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import se.trixon.almond.nbp.Almond;
import se.trixon.almond.util.Dict;
import se.trixon.almond.util.icons.material.MaterialIcon;
import se.trixon.almond.util.swing.dialogs.HtmlPanel;

/**
 *
 * @author Patrik Karlström
 */
public class GameOverDialog {

    private final DialogButtonManager mButtonManager = DialogButtonManager.getInstance();
    private final JButton mNewGameButton;
    private final NotifyDescriptor mNotifyDescriptor;
    private final JButton mQuickNewGameButton;

    public static GameOverDialog getInstance() {
        return Holder.INSTANCE;
    }

    private GameOverDialog() {
        mNewGameButton = new JButton(MaterialIcon._Av.PLAY_ARROW.getImageIcon(Almond.ICON_LARGE));
        mQuickNewGameButton = new JButton(MaterialIcon._Av.PLAY_CIRCLE_OUTLINE.getImageIcon(Almond.ICON_LARGE));

        JButton[] options = new JButton[]{mButtonManager.getCancel(), mNewGameButton, mQuickNewGameButton};
        mNotifyDescriptor = new NotifyDescriptor(
                null,
                Dict.Game.GAME_OVER.toString(),
                NotifyDescriptor.PLAIN_MESSAGE,
                NotifyDescriptor.DEFAULT_OPTION,
                options,
                mQuickNewGameButton);

    }

    public void display(GameController gameController, String s) {
        Object message = s;
        if (StringUtils.startsWithIgnoreCase(s, "<html>")) {
            message = new HtmlPanel(s.toString());
        }

        display(gameController, message);
    }

    public void display(GameController gameController, ArrayList<GameOverItem> gameOverItems) {
        gameOverItems.sort(new Comparator<GameOverItem>() {
            @Override
            public int compare(GameOverItem o1, GameOverItem o2) {
                return new Integer(o2.getScore()).compareTo(new Integer(o1.getScore()));
            }
        });

        StringBuilder cssBuilder = new StringBuilder("<html>");
        cssBuilder.append("<head><style>");
        cssBuilder.append("h1 { font-size: x-large; margin-bottom: 0px; }");
        cssBuilder.append("h2 { font-size: large; margin-bottom: 0px; }");
        cssBuilder.append("body {margin-left: 16px;margin-right: 16px; font-size: xx-large; font-family:courier; }");
        cssBuilder.append("p {margin-bottom: 4px;margin-top: 4px;}");
        cssBuilder.append("ul { margin-left: 16px; }");
        cssBuilder.append("li { }");
        cssBuilder.append("table { width:100%; }");
        cssBuilder.append("td { text-align: right; }");
        cssBuilder.append("</style></head>");

        StringBuilder builder = new StringBuilder(cssBuilder);
        builder.append("<html><table>");
        int position = 0;
        for (GameOverItem gameOverItem : gameOverItems) {
            final String name = gameOverItem.getPlayer().getName();
            final int score = gameOverItem.getScore();

            builder.append("<tr>");
            builder.append("<td>").append(++position).append("</td>");
            builder.append("<td style=\"text-align: left;\">").append(name).append("</td>");
            builder.append("<td>").append(score).append("</td>");
            builder.append("</tr>");
        }
        builder.append("</table></html>");

        display(gameController, builder.toString());
    }

    private void display(GameController gameController, Object message) {
        mNotifyDescriptor.setMessage(message);
        Object retVal = DialogDisplayer.getDefault().notify(mNotifyDescriptor);

        SwingUtilities.invokeLater(() -> {
            if (retVal == mNewGameButton) {
//                NbGamesTopComponent nbGamesTopComponent = (NbGamesTopComponent) WindowManager.getDefault().findTopComponent("NbGamesTopComponent");
//                nbGamesTopComponent.displayNewGameDialog(gameController);
            } else if (retVal == mQuickNewGameButton) {
                gameController.onRequestNewGameStart();
            }
        });
    }

    private static class Holder {

        private static final GameOverDialog INSTANCE = new GameOverDialog();
    }
}
