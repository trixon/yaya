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
package se.trixon.yaya;

import com.dlsc.gemsfx.util.StageManager;
import com.dlsc.workbenchfx.Workbench;
import com.dlsc.workbenchfx.model.WorkbenchDialog;
import com.dlsc.workbenchfx.view.controls.ToolbarItem;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;
import org.openide.LifecycleManager;
import se.trixon.almond.util.PrefsHelper;
import se.trixon.almond.util.fx.FxHelper;
import se.trixon.yaya.actions.YActions;
import se.trixon.yaya.scorecard.rules.RuleManager;

/**
 *
 * @author Patrik Karlström
 */
public class App extends Application {

    public static final String APP_TITLE = "Yaya";
    public static final int ICON_SIZE_TOOLBAR = FxHelper.getUIScaled(40);
    private AppModule mAppModule;
    private final Options mOptions = Options.getInstance();
    private final RuleManager mRuleManager = RuleManager.getInstance();
    private Stage mStage;
    private Workbench mWorkbench;
    private final Yaya mYaya = Yaya.getInstance();

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        mStage = stage;
        createUI();
        mStage.show();

        PrefsHelper.inc(mOptions.getPreferences(), Options.KEY_APP_START_COUNTER);
        int gameStartCounter = mOptions.getPreferences().getInt(Options.KEY_GAME_START_COUNTER, 0);
        if (gameStartCounter == 0) {
            FxHelper.runLaterDelayed(200, () -> {
                YActions.forId("core", "newround").handle(null);
                YActions.forId("core", "help").handle(null);
            });
        } else {
            mYaya.onRequestNewGameStart();
        }
    }

    @Override
    public void stop() throws Exception {
        LifecycleManager.getDefault().exit();
    }

    private void createUI() {
        mYaya.setApplication(this);

        mStage.getIcons().add(new Image(App.class.getResourceAsStream("logo.png")));
        mStage.setTitle(APP_TITLE);
        mStage.setFullScreenExitHint("");
        int minWidth = FxHelper.getUIScaled(200);
        mStage.setMinWidth(minWidth);
        int minHeight = FxHelper.getUIScaled(200);
        mStage.setMinHeight(minHeight);
        StageManager.install(mStage, mOptions.getPreferences().node("stage").absolutePath(), minWidth, minHeight);

        mAppModule = new AppModule();

        var newGameToolbarItem = new ToolbarItem(new FontIcon(FontAwesomeSolid.PLAY), event -> {
            YActions.forId("core", "newround").handle(null);
        });
        newGameToolbarItem.textProperty().bind(mRuleManager.getRule().nameProperty());

        var optionsToolbarItem = new ToolbarItem(new FontIcon(FontAwesomeSolid.BARS), event -> {
            YActions.forId("core", "options").handle(null);
        });

        var fullScreenToolbarItem = new ToolbarItem(new FontIcon(FontAwesomeSolid.EXPAND), event -> {
            YActions.forId("core", "fullScreen").handle(null);
        });

        mStage.fullScreenProperty().addListener((observable, oldValue, newValue) -> {
            fullScreenToolbarItem.setGraphic(new FontIcon(newValue ? FontAwesomeSolid.COMPRESS : FontAwesomeSolid.EXPAND));
        });
        YActions.forId("core", "quit");
        mWorkbench = Workbench.builder(mAppModule)
                .toolbarLeft(
                        optionsToolbarItem,
                        newGameToolbarItem
                )
                .toolbarRight(
                        fullScreenToolbarItem
                )
                .build();

        mWorkbench.getStylesheets().add(AppModule.class.getResource("customTheme.css").toExternalForm());
        mYaya.setWorkbench(mWorkbench);

        var scene = new Scene(mWorkbench);
        scene.setFill(Color.web("#bb6624"));
        FxHelper.applyFontScale(scene);
        mStage.setScene(scene);

        initListeners();
        mYaya.setStage(mStage);

        optionsToolbarItem.setTooltip(new Tooltip(YActions.forId("core", "options").getLongText()));
        newGameToolbarItem.setTooltip(new Tooltip(YActions.forId("core", "newround").getLongText()));
        fullScreenToolbarItem.setTooltip(new Tooltip(YActions.forId("core", "fullScreen").getLongText()));
    }

    private void initListeners() {
        mStage.addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, windowEvent -> {
            if (false) {//TODO Add condition
                var dialog = WorkbenchDialog.builder("title", "message", WorkbenchDialog.Type.CONFIRMATION).onResult(buttonType -> {
                    if (buttonType.getButtonData() != ButtonBar.ButtonData.YES) {
                        windowEvent.consume();
                    }
                }).blocking(true).build();
                mWorkbench.showDialog(dialog);
            }
        });
    }
}
