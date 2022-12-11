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
import com.dlsc.workbenchfx.view.controls.ToolbarItem;
import de.jangassen.MenuToolkit;
import java.util.Arrays;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCombination;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.apache.commons.lang3.SystemUtils;
import org.controlsfx.control.action.Action;
import org.controlsfx.control.action.ActionGroup;
import org.controlsfx.control.action.ActionUtils;
import org.openide.LifecycleManager;
import org.openide.util.NbBundle;
import se.trixon.almond.nbp.core.ModuleHelper;
import se.trixon.almond.util.Dict;
import se.trixon.almond.util.PomInfo;
import se.trixon.almond.util.PrefsHelper;
import se.trixon.almond.util.SystemHelper;
import se.trixon.almond.util.SystemHelperFx;
import se.trixon.almond.util.fx.AboutModel;
import se.trixon.almond.util.fx.FxHelper;
import se.trixon.almond.util.fx.dialogs.about.AboutPane;
import se.trixon.almond.util.swing.DelayedResetRunner;
import se.trixon.yaya.actions.YActions;

/**
 *
 * @author Patrik Karlström
 */
public class App extends Application {

    public static final String APP_TITLE = "Yaya";
    private Action mAboutAction;
    private AppModule mAppModule;
    private final Options mOptions = Options.getInstance();
    private Stage mStage;
    private final ThemeManager mThemeManager = ThemeManager.getInstance();
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
        mStage.setFullScreenExitKeyCombination(KeyCombination.keyCombination("F11"));
        int minWidth = FxHelper.getUIScaled(200);
        mStage.setMinWidth(minWidth);
        int minHeight = FxHelper.getUIScaled(200);
        mStage.setMinHeight(minHeight);
        StageManager.install(mStage, mOptions.getPreferences().node("stage").absolutePath(), minWidth, minHeight);

        //about
        var pomInfo = new PomInfo(App.class, "se.trixon.yaya", "main");
        var aboutModel = new AboutModel(SystemHelper.getBundle(App.class, "about"), SystemHelperFx.getResourceAsImageView(App.class, "logo.png"));
        aboutModel.setAppVersion(pomInfo.getVersion());
        try {
            aboutModel.setAppDate(ModuleHelper.getBuildTime(App.class));
        } catch (Exception e) {
            System.out.println("not run as a platform");
        }
        mAboutAction = AboutPane.getAction(mStage, aboutModel);

        mAppModule = new AppModule();
        mWorkbench = Workbench.builder(mAppModule)
                .toolbarLeft(
                        new ToolbarItem("Crag/Standard")
                ).build();

        mWorkbench.getStylesheets().add(AppModule.class.getResource("customTheme.css").toExternalForm());

        var scene = new Scene(mWorkbench);
        scene.setFill(Color.web("#bb6624"));
        FxHelper.applyFontScale(scene);
        mStage.setScene(scene);
        initWorkbenchDrawer();

        if (SystemUtils.IS_OS_MAC) {
            initMac();
        }

//        initListeners();
//        initMenu();
//        initMenuColor();
//        initMenuSize();
        mYaya.setStage(mStage);
    }

    private void initListeners() {
    }

    private void initMac() {
        var menuToolkit = MenuToolkit.toolkit();
        var applicationMenu = menuToolkit.createDefaultApplicationMenu(APP_TITLE);
        menuToolkit.setApplicationMenu(applicationMenu);

        applicationMenu.getItems().remove(0);
        var aboutMenuItem = new MenuItem(String.format(Dict.ABOUT_S.toString(), APP_TITLE));
        aboutMenuItem.setOnAction(mAboutAction);

        applicationMenu.getItems().add(0, aboutMenuItem);

        int cnt = applicationMenu.getItems().size();
        applicationMenu.getItems().get(cnt - 1).setText(String.format("%s %s", Dict.QUIT.toString(), APP_TITLE));
    }

    private void initMenu() {
        var fullscreenAction = YActions.forId("core", "fullScreen");

        var actions = Arrays.asList(YActions.forId("core", "newround"),
                ActionUtils.ACTION_SEPARATOR,
                new ActionGroup(Dict.SYSTEM.toString(),
                        fullscreenAction,
                        YActions.forId("core", "playSound"),
                        ActionUtils.ACTION_SEPARATOR,
                        YActions.forId("core", "removePlayer")
                ),
                new ActionGroup(NbBundle.getMessage(YActions.class, "scorecard"),
                        YActions.forId("core", "lim"),
                        YActions.forId("core", "max"),
                        YActions.forId("core", "indicator")
                ),
                new ActionGroup(NbBundle.getMessage(YActions.class, "dice"),
                        YActions.forId("core", "reverse-dice")
                ),
                ActionUtils.ACTION_SEPARATOR,
                YActions.forId("core", "help"),
                mAboutAction,
                ActionUtils.ACTION_SEPARATOR,
                YActions.forId("core", "quit")
        );
    }

    private void initMenuColor() {
        var colorMenu = new Menu(NbBundle.getMessage(YActions.class, "colors"));
        var toggleGroup = new ToggleGroup();

        for (var theme : mThemeManager.getItems()) {
            var rmi = new RadioMenuItem(theme.getName());
            rmi.setToggleGroup(toggleGroup);
            rmi.setSelected(mOptions.getThemeId().equalsIgnoreCase(theme.getId()));
            rmi.setOnAction(ae -> {
                mThemeManager.setTheme(theme);
                mOptions.setThemeId(theme.getId());
            });

            colorMenu.getItems().add(rmi);
        }

//        var scorecardMenu = (Menu) mContextMenu.getItems().get(3);
//        scorecardMenu.getItems().add(0, colorMenu);
    }

    private void initMenuSize() {
//        var scorecardMenu = (Menu) mContextMenu.getItems().get(3);

        var fontMenuItem = new MenuItem(Dict.SIZE.toString());
        fontMenuItem.setDisable(true);
//        scorecardMenu.getItems().add(fontMenuItem);

        var fontSlider = new Slider(8, 72, mOptions.getFontSize());
        var fontResetRunner = new DelayedResetRunner(50, () -> {
            mOptions.setFontSize((int) fontSlider.getValue());
        });

        fontSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            fontResetRunner.reset();
        });

        var customMenuItem = new CustomMenuItem(fontSlider);
        customMenuItem.setHideOnClick(false);
//        scorecardMenu.getItems().add(customMenuItem);
    }

    private void initWorkbenchDrawer() {
        mWorkbench.getNavigationDrawerItems().setAll(
                ActionUtils.createMenuItem(YActions.forId("core", "quit"))
        );
    }

}
