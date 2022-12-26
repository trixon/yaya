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
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import org.openide.util.lookup.ServiceProvider;
import se.trixon.almond.nbp.core.ModuleHelper;
import se.trixon.almond.util.Dict;
import se.trixon.almond.util.PomInfo;
import se.trixon.almond.util.SystemHelper;
import se.trixon.almond.util.SystemHelperFx;
import se.trixon.almond.util.fx.AboutModel;
import se.trixon.almond.util.fx.FxHelper;
import se.trixon.almond.util.fx.dialogs.about.AboutPane;
import se.trixon.yaya.App;

/**
 *
 * @author Patrik Karlström
 */
@YAction.Description(category = "core", id = "about")
@ServiceProvider(service = YAction.class)
public class RootAboutAction extends YAction {

    public RootAboutAction() {
        super(Dict.ABOUT.toString());

        setEventHandler(eventHandler -> {
            var pomInfo = new PomInfo(App.class, "se.trixon.yaya", "main");
            var aboutModel = new AboutModel(
                    SystemHelper.getBundle(App.class, "about"),
                    SystemHelperFx.getResourceAsImageView(App.class, "logo.png")
            );
            aboutModel.setAppVersion(pomInfo.getVersion());
            try {
                aboutModel.setAppDate(ModuleHelper.getBuildTime(App.class));
            } catch (Exception e) {
                //nvm
            }

            var aboutPane = new AboutPane(aboutModel);

            double scaledFontSize = FxHelper.getScaledFontSize();
            var appLabel = new Label(aboutModel.getAppName());
            appLabel.setFont(new Font(scaledFontSize * 1.8));
            var verLabel = new Label(String.format("%s %s", Dict.VERSION.toString(), aboutModel.getAppVersion()));
            verLabel.setFont(new Font(scaledFontSize * 1.2));
            var dateLabel = new Label(aboutModel.getAppDate());
            dateLabel.setFont(new Font(scaledFontSize * 1.2));

            var box = new VBox(appLabel, verLabel, dateLabel);
            box.setAlignment(Pos.CENTER_LEFT);
            box.setPadding(new Insets(0, 0, 0, 22));
            var topBorderPane = new BorderPane(box);
            topBorderPane.setLeft(aboutModel.getImageView());
            topBorderPane.setPadding(new Insets(22));
            var mainBorderPane = new BorderPane(aboutPane);
            mainBorderPane.setTop(topBorderPane);
            mainBorderPane.setPrefHeight(400);
            getWorkbench().hideDrawer();
            var dialog = WorkbenchDialog.builder(Dict.ABOUT.toString(), mainBorderPane, ButtonType.CLOSE).build();
            getWorkbench().showDialog(dialog);

        });

        setPostInitRunnable(() -> {
//            setAcceleratorForStage(keyCodeCombination);
//            addTooltipKeyCode(keyCodeCombination);
        });
    }

}
