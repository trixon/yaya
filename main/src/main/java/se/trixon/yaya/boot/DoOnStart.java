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
import javax.swing.JFrame;
import javax.swing.UIManager;
import org.openide.modules.OnStart;
import org.openide.windows.WindowManager;
import se.trixon.almond.nbp.Almond;
import se.trixon.almond.nbp.NbHelper;
import se.trixon.almond.util.swing.SwingHelper;
import se.trixon.yaya.Options;
import se.trixon.yaya.Yaya;

/**
 *
 * @author Patrik Karlström <patrik@trixon.se>
 */
@OnStart
public class DoOnStart implements Runnable {

    private final Options mOptions = Options.getInstance();

    static {
//        FlatLaf.registerCustomDefaultsSource("se.trixon.yaya.themes");
        FlatLightLaf.setup();
        System.setProperty("netbeans.winsys.no_help_in_dialogs", "true");
        System.setProperty("netbeans.winsys.no_toolbars", "true");

        NbHelper.setLafDefault("Light");
        NbHelper.setLafAccentColor("#FF00FF");
        UIManager.put("Button.arc", 999);
        UIManager.put("Label.background", "#ff0000");
        UIManager.put("Button.background", "#ff0000");
        UIManager.put("PopupMenu.background", "#ff0000");
        UIManager.put("Menu.background", "#ff0000");
        UIManager.put("MenuItem.background", "#ff0000");
//        UIManager.put("Frame.background", "#ff0000");
//        UIManager.put("Component.background", "#00ff00");
        UIManager.put("Panel.background", "#00ff00");
//        UIManager.put("background", "#0000ff");
    }

    @Override
    public void run() {
//        FlatLaf.registerCustomDefaultsSource("se.trixon.yaya.themes");
//        FlatDarkLaf.setup();
        var windowManager = WindowManager.getDefault();
        windowManager.invokeWhenUIReady(() -> {
            Almond.setFrame((JFrame) windowManager.getMainWindow());

            var frame = (JFrame) windowManager.getMainWindow();
//            frame.setVisible(false);
            var contentPane = frame.getRootPane().getContentPane();

            contentPane.removeAll();
            SwingHelper.runLaterDelayed(0, () -> {
                contentPane.add(Yaya.getInstance().getPanel());
            });
//            frame.repaint();
//            frame.revalidate();
//            frame.setVisible(true);

            UIManager.put("Button.arc", 999);
//            UIManager.put("Label.background", "#ff0000");
            UIManager.put("Button.background", "#ff0000");
//            UIManager.put("PopupMenu.background", "#ff0000");
//            UIManager.put("Menu.background", "#ff0000");
//            UIManager.put("MenuItem.background", "#ff0000");
//        UIManager.put("Frame.background", "#ff0000");
//        UIManager.put("Component.background", "#00ff00");
//            UIManager.put("Panel.background", "#00ff00");
//        UIManager.put("background", "#0000ff");

        });
    }

}
