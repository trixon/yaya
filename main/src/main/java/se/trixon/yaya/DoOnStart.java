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

import java.awt.EventQueue;
import java.util.prefs.BackingStoreException;
import org.openide.modules.OnStart;
import org.openide.util.NbPreferences;
import se.trixon.almond.nbp.dialogs.NbOptionalDialog;
import se.trixon.almond.util.AlmondUI;
import se.trixon.almond.util.PrefsHelper;
import se.trixon.almond.util.SystemHelper;
import se.trixon.almond.util.swing.SwingHelper;
import se.trixon.yaya.rules.RuleManager;

/**
 *
 * @author Patrik Karlström <patrik@trixon.se>
 */
@OnStart
public class DoOnStart implements Runnable {

    private final Options mOptions = Options.getInstance();

    static {
//        FlatDarkLaf.setup();
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
        SystemHelper.runLaterDelayed(100, () -> {
            EventQueue.invokeLater(() -> {
                var preferences = NbPreferences.forModule(DoOnStart.class);
                RuleManager.getInstance().init();
                var frame = new MainFrame();
                AlmondUI.getInstance().addWindowWatcher(preferences, frame);
                SwingHelper.setFullScreen(fullscreen ? frame : null);
                frame.setVisible(true);
            });
        });
    }

}
