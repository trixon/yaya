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
import com.sandec.mdfx.MarkdownView;
import com.vladsch.flexmark.html2md.converter.FlexmarkHtmlConverter;
import java.util.ResourceBundle;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import org.openide.util.lookup.ServiceProvider;
import se.trixon.almond.util.Dict;
import se.trixon.almond.util.SystemHelper;
import se.trixon.yaya.AppModule;
import se.trixon.yaya.Help;

/**
 *
 * @author Patrik Karlström
 */
@YAction.Description(category = "core", id = "help")
@ServiceProvider(service = YAction.class)
public class RootHelpAction extends YAction {

    private final ResourceBundle mBundle = SystemHelper.getBundle(Help.class, "Help");

    public RootHelpAction() {
        super(Dict.HELP.toString());
        var keyCodeCombination = new KeyCodeCombination(KeyCode.F1, KeyCombination.SHORTCUT_ANY);
        setAccelerator(keyCodeCombination);
        setEventHandler(eventHandler -> {
            var help = new Help();
            var markdownView = new MarkdownView(FlexmarkHtmlConverter.builder().build().convert(help.getHelp()));
            markdownView.getStylesheets().add(AppModule.class.getResource("mdfx.css").toExternalForm());
            var scrollPane = new ScrollPane(markdownView);
            scrollPane.setFitToWidth(true);
            scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

            if (!isDialogShowing()) {
                setDialogShowing(true);
                getWorkbench().hideDrawer();
                getWorkbench().showDialog(WorkbenchDialog.builder(mBundle.getString("help_intro"), scrollPane, WorkbenchDialog.Type.INFORMATION)
                        .maximized(true)
                        .showButtonsBar(false)
                        .onResult(buttonType -> {
                            setDialogShowing(false);
                        })
                        .build()
                );
            }
        });

        setPostInitRunnable(() -> {
            setAcceleratorForStage(keyCodeCombination);
            addTooltipKeyCode(keyCodeCombination);
        });
    }

}
