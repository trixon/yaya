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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.prefs.BackingStoreException;
import javax.swing.JFrame;
import javax.swing.UIManager;
import org.openide.awt.Actions;
import org.openide.awt.HtmlBrowser;
import org.openide.modules.OnStart;
import org.openide.util.Exceptions;
import org.openide.util.NbPreferences;
import org.openide.windows.WindowManager;
import se.trixon.almond.nbp.Almond;
import se.trixon.almond.nbp.dialogs.NbOptionalDialog;
import se.trixon.almond.util.PrefsHelper;
import se.trixon.almond.util.SystemHelper;

/**
 *
 * @author Patrik Karlström <patrik@trixon.se>
 */
@OnStart
public class DoOnStart implements Runnable {

    private final Options mOptions = Options.getInstance();

    static {
        UIManager.put("NbMainWindow.showCustomBackground", Boolean.TRUE);
        System.setProperty("netbeans.winsys.status_line.path", "");
        System.setProperty("netbeans.winsys.no_help_in_dialogs", "true");
        System.setProperty("netbeans.winsys.no_toolbars", "true");

        try {
            var key = "laf";
            var defaultLAF = "com.formdev.flatlaf.FlatDarkLaf";
            var preferences = NbPreferences.root().node("laf");
            PrefsHelper.putIfAbsent(preferences, key, defaultLAF);
        } catch (BackingStoreException ex) {
            //Exceptions.printStackTrace(ex);
        }

        NbOptionalDialog.setPreferences(NbPreferences.forModule(NbOptionalDialog.class).node("optionalDialogState"));
    }

    @Override
    public void run() {
        boolean fullscreen = mOptions.isFullscreen();

        SystemHelper.setDesktopBrowser(url -> {
            try {
                HtmlBrowser.URLDisplayer.getDefault().showURL(new URL(url));
            } catch (MalformedURLException ex) {
                Exceptions.printStackTrace(ex);
            }
        });

        var windowManager = WindowManager.getDefault();
        windowManager.invokeWhenUIReady(() -> {
            var frame = (JFrame) windowManager.getMainWindow();
            Almond.setFrame(frame);

            if (fullscreen) {
                Actions.forID("Window", "org.netbeans.core.windows.actions.ToggleFullScreenAction").actionPerformed(null);
            }

        });
    }

}
