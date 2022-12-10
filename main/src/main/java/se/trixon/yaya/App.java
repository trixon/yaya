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

import de.jangassen.MenuToolkit;
import java.util.Arrays;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
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
import se.trixon.almond.util.fx.AlmondFx;
import se.trixon.almond.util.fx.FxHelper;
import se.trixon.almond.util.fx.dialogs.about.AboutPane;
import se.trixon.almond.util.icons.material.MaterialIcon;
import se.trixon.yaya.actions.YActions;

/**
 *
 * @author Patrik Karlström
 */
public class App extends Application {

    public static final String APP_TITLE = "Yaya";
    private static final boolean IS_MAC = SystemUtils.IS_OS_MAC;
    private Action mAboutAction;
    private final AlmondFx mAlmondFX = AlmondFx.getInstance();
    private AppForm mAppForm;
    private ContextMenu mContextMenu;
    private final Options mOptions = Options.getInstance();
    private BorderPane mRoot;
    private Stage mStage;
    private final ThemeManager mThemeManager = ThemeManager.getInstance();
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
        stage.getIcons().add(new Image(App.class.getResourceAsStream("logo.png")));

        mAlmondFX.addStageWatcher(stage, App.class);
        createUI();

        if (IS_MAC) {
            initMac();
        }
        mYaya.setApplication(this);
        mYaya.setStage(stage);

        updateNightMode();

        mStage.setTitle(APP_TITLE);
        FxHelper.removeSceneInitFlicker(mStage);

        mStage.show();
        initAccelerators();
        initListeners();
        mAppForm.initAccelerators();

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

        updateNightMode();
    }

    @Override
    public void stop() throws Exception {
        LifecycleManager.getDefault().exit();
    }

    private void createUI() {
        mStage.setMinHeight(FxHelper.getUIScaled(200));
        mStage.setMinWidth(FxHelper.getUIScaled(200));

        //about
        var pomInfo = new PomInfo(App.class, "se.trixon.yaya", "main");
        var aboutModel = new AboutModel(SystemHelper.getBundle(App.class, "about"), SystemHelperFx.getResourceAsImageView(App.class, "logo.png"));
        aboutModel.setAppVersion(pomInfo.getVersion());
        aboutModel.setAppDate(ModuleHelper.getBuildTime(MainFrame.class));
        mAboutAction = AboutPane.getAction(mStage, aboutModel);

        mRoot = new BorderPane(mAppForm = new AppForm());
        mStage.setScene(new Scene(mRoot));

        initMenu();
        initMenuColor();
    }

    private void initAccelerators() {
        var accelerators = mStage.getScene().getAccelerators();

        accelerators.put(new KeyCodeCombination(KeyCode.Q, KeyCombination.SHORTCUT_DOWN), () -> {
            mStage.fireEvent(new WindowEvent(mStage, WindowEvent.WINDOW_CLOSE_REQUEST));
        });
    }

    private void initListeners() {
        mOptions.nightModeProperty().addListener((observable, oldValue, newValue) -> {
            updateNightMode();
        });
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
        var actions = Arrays.asList(
                YActions.forId("core", "newround"),
                ActionUtils.ACTION_SEPARATOR,
                new ActionGroup(Dict.SYSTEM.toString(),
                        YActions.forId("core", "fullscreen"),
                        YActions.forId("core", "nightmode"),
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
        mContextMenu = ActionUtils.createContextMenu(actions);

        mStage.getScene().setOnMousePressed(mouseEvent -> {
            if (mouseEvent.isSecondaryButtonDown()) {
                mContextMenu.show(mRoot, mouseEvent.getScreenX(), mouseEvent.getScreenY());
            } else if (mouseEvent.isPrimaryButtonDown()) {
                mContextMenu.hide();
            }
        });
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

        var sysMenu = (Menu) mContextMenu.getItems().get(3);
        sysMenu.getItems().add(0, colorMenu);
    }

    private void updateNightMode() {
        MaterialIcon.setDefaultColor(mOptions.isNightMode() ? Color.LIGHTGRAY : Color.BLACK);

        FxHelper.setDarkThemeEnabled(mOptions.isNightMode());
        if (mOptions.isNightMode()) {
            FxHelper.loadDarkTheme(mStage.getScene());
        } else {
            FxHelper.unloadDarkTheme(mStage.getScene());
        }
    }
}
