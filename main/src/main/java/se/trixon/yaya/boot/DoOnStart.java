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
package se.trixon.yaya.boot;

import com.formdev.flatlaf.FlatLightLaf;
import java.awt.Color;
import javax.swing.JFrame;
import javax.swing.UIManager;
import org.openide.modules.OnStart;
import org.openide.windows.WindowManager;
import se.trixon.almond.nbp.Almond;
import se.trixon.almond.nbp.NbHelper;
import se.trixon.almond.util.GraphicsHelper;
import se.trixon.yaya.Options;
import se.trixon.yaya.Yaya;

/**
 *
 * @author Patrik Karlström <patrik@trixon.se>
 */
@OnStart
public class DoOnStart implements Runnable {

    public static final Color BACKGROUND_COLOR = Color.decode("#A0522D");

    private final Options mOptions = Options.getInstance();

    static {
        System.setProperty("netbeans.winsys.no_help_in_dialogs", "true");
        System.setProperty("netbeans.winsys.no_toolbars", "true");

        NbHelper.setLafDefault("Light");

        var accent = GraphicsHelper.colorToString(Color.ORANGE);
        NbHelper.setLafAccentColor(accent);
        FlatLightLaf.setup();

        var foreground = Color.WHITE;
        var buttonBackground = Color.ORANGE;
        UIManager.put("background", BACKGROUND_COLOR);
        UIManager.put("Frame.background", BACKGROUND_COLOR);
        UIManager.put("RootPane.background", BACKGROUND_COLOR);

        UIManager.put("Component.background", BACKGROUND_COLOR);
        UIManager.put("Component.focusWidth", 2);

        UIManager.put("Button.arc", 999);
        UIManager.put("Button.background", buttonBackground);
        UIManager.put("Button.default.background", buttonBackground);
        UIManager.put("Button.default.focusedBackground", buttonBackground);
        UIManager.put("Button.focusedBackground", buttonBackground);
        UIManager.put("Button.pressedBackground", buttonBackground.darker());
        UIManager.put("Button.selectedBackground", buttonBackground);

        UIManager.put("CheckBox.background", BACKGROUND_COLOR.darker());
        UIManager.put("CheckBox.foreground", foreground);
        UIManager.put("CheckBox.icon.background", BACKGROUND_COLOR.darker());
        UIManager.put("CheckBox.icon.checkmarkColor", Color.BLACK);
        UIManager.put("CheckBox.icon.selectedBackground", buttonBackground);

        UIManager.put("ComboBox.background", BACKGROUND_COLOR);
        UIManager.put("ComboBox.disabledBackground", BACKGROUND_COLOR.darker());
        UIManager.put("ComboBox.disabledForeground", foreground);
        UIManager.put("ComboBox.foreground", foreground);
        UIManager.put("ComboBox.buttonBackground", buttonBackground);
        UIManager.put("ComboBox.buttonEditableBackground", buttonBackground);

        UIManager.put("Label.foreground", foreground);

        UIManager.put("MenuItem.acceleratorForeground", foreground);
        UIManager.put("MenuItem.foreground", foreground);

        UIManager.put("EditorPane.background", BACKGROUND_COLOR);
        UIManager.put("EditorPane.foreground", foreground);

        UIManager.put("OptionPane.background", BACKGROUND_COLOR);

        UIManager.put("Panel.background", BACKGROUND_COLOR);

        UIManager.put("PopupMenu.background", BACKGROUND_COLOR);
        UIManager.put("PopupMenu.foreground", foreground);

        UIManager.put("ScrollBar.track", BACKGROUND_COLOR.darker());
        UIManager.put("ScrollBar.thumb", buttonBackground);

        UIManager.put("Spinner.background", BACKGROUND_COLOR);
        UIManager.put("Spinner.foreground", foreground);
        UIManager.put("Spinner.buttonBackground", buttonBackground);

        UIManager.put("TabRenderer.selectedActivatedBackground", Color.BLUE);

        UIManager.put("TabbedPane.tabArc", 999);
        UIManager.put("TabbedPane.background", BACKGROUND_COLOR.darker());
        UIManager.put("TabbedPane.focusColor", BACKGROUND_COLOR);
        UIManager.put("TabbedPane.foreground", foreground);
        UIManager.put("TabbedPane.hoverColor", BACKGROUND_COLOR.brighter());
        UIManager.put("TabbedPane.selectedBackground", BACKGROUND_COLOR);

        UIManager.put("Table.background", BACKGROUND_COLOR);
        UIManager.put("Table.foreground", foreground);

        UIManager.put("TableHeader.background", BACKGROUND_COLOR);
        UIManager.put("TableHeader.foreground", foreground);

        UIManager.put("TextArea.foreground", foreground);

        UIManager.put("TitledBorder.titleColor", foreground);
    }

    @Override
    public void run() {
        var windowManager = WindowManager.getDefault();
        windowManager.invokeWhenUIReady(() -> {
            Almond.setFrame((JFrame) windowManager.getMainWindow());

            var frame = (JFrame) windowManager.getMainWindow();
            var contentPane = frame.getRootPane().getContentPane();
            contentPane.removeAll();
            contentPane.setBackground(BACKGROUND_COLOR);
            frame.setBackground(BACKGROUND_COLOR);
            contentPane.add(Yaya.getInstance().getPanel());
        });
    }
}
