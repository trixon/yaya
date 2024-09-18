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

import java.util.ArrayList;

/**
 *
 * @author Patrik Karlström
 */
public class GameOverDialog {

    public void display(ArrayList<GameOverItem> gameOverItems) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

//    private WebView mWebView;
//    private final Yaya mYaya = Yaya.getInstance();
//
//    public static GameOverDialog getInstance() {
//        return Holder.INSTANCE;
//    }
//
//    private GameOverDialog() {
//        Platform.runLater(() -> {
//            mWebView = new WebView();
//        });
//    }
//
//    public void display(ArrayList<GameOverItem> gameOverItems) {
//        gameOverItems.sort(Comparator.comparing(GameOverItem::score).reversed());
//        for (int i = 0; i < gameOverItems.size(); i++) {
//            gameOverItems.get(i).position = i + 1;
//        }
//
//        var html = html(
//                body(
//                        h1(Dict.Game.RESULT.toString()),
//                        table(
//                                tbody(
//                                        each(gameOverItems, gameOverItem
//                                                -> tr(
//                                                td(Integer.toString(gameOverItem.position)),
//                                                td(gameOverItem.player.getName()).withStyle("width:100%;"),
//                                                td(Integer.toString(gameOverItem.score)).withStyle("text-align: right;")
//                                        ).withStyle("border-bottom: 1px solid #ddd;")
//                                        )
//                                )
//                        ).withStyle("font-size: 180%;width:100%;")
//                ));
//        FxHelper.runLater(() -> display(html.render()));
//    }
//
//    private void display(String message) {
//        mWebView.getEngine().loadContent(message);
//        var newQuickButtonType = new ButtonType(Dict.Game.NEW_ROUND.toString(), ButtonBar.ButtonData.OK_DONE);
//        var newDialogButtonType = new ButtonType(Dict.Game.NEW_ROUND.toString() + "…", ButtonBar.ButtonData.APPLY);
//
//        var dialog = WorkbenchDialog.builder(
//                Dict.Game.GAME_OVER.toString(),
//                mWebView,
//                newDialogButtonType, newQuickButtonType
//        ).onResult(buttonType -> {
//            if (buttonType == newDialogButtonType) {
//                YActions.forId("core", "newround").handle(null);
//            } else if (buttonType == newQuickButtonType) {
//                mYaya.onRequestNewGameStart();
//            }
//        }).build();
//
//        mYaya.getWorkbench().showDialog(dialog);
//    }
    private static class Holder {

        private static final GameOverDialog INSTANCE = new GameOverDialog();
    }
}
