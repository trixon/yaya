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

import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.ResourceBundle;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.KeyStroke;
import org.openide.util.NbBundle;
import se.trixon.almond.util.AlmondAction;
import se.trixon.almond.util.AlmondActionListener;
import se.trixon.almond.util.AlmondActionManager;
import se.trixon.almond.util.Dict;
import se.trixon.almond.util.SystemHelper;

/**
 *
 * @author Patrik Karlström <patrik@trixon.se>
 */
public class ActionManager extends AlmondActionManager {

    public static final String NIGHT_MODE = "system.nightMode";
    public static final String PLAY_SOUND = "playSound";
    public static final String SHOW_INDICATORS = "show.indicators";
    public static final String SHOW_LIM = "show.lim";
    public static final String SHOW_MAX = "show.max";

    private final HashSet<AlmondActionListener> mAppListeners = new HashSet<>();
    private final ResourceBundle mBundle = NbBundle.getBundle(MainFrame.class);

    public static ActionManager getInstance() {
        return Holder.INSTANCE;
    }

    private ActionManager() {
    }

    public void addAppListener(AlmondActionListener appListener) {
        mAppListeners.add(appListener);
    }

    public void init(ActionMap actionMap, InputMap inputMap) {
        mActionMap = actionMap;
        mInputMap = inputMap;
        AlmondAction action;
        KeyStroke keyStroke;
        int commandMask = SystemHelper.getCommandMask();

        //new
        keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_N, 0);
        action = createAction(Dict.Game.NEW_ROUND.toString());
        initAction(action, NEW, keyStroke, null, true);

        //undo
        keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_Z, commandMask);
        action = createAction(Dict.UNDO.toString());
        initAction(action, UNDO, keyStroke, null, true);

        //fullscreen
        keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_F11, 0);
        action = createAction(Dict.FULL_SCREEN.toString());
        initAction(action, FULLSCREEN, keyStroke, null, true);
        action.setEnabled(GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().isFullScreenSupported());

        //night mode
        action = createAction(Dict.NIGHT_MODE.toString());
        initAction(action, NIGHT_MODE, null, null, true);

        //play sound
        action = createAction(Dict.Game.PLAY_SOUND.toString());
        initAction(action, PLAY_SOUND, null, null, true);

        //show lim
        keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0);
        action = createAction(mBundle.getString("MainFrame.limCheckBoxMenuItem.text"));
        initAction(action, SHOW_LIM, keyStroke, null, true);

        //show max
        keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_F6, 0);
        action = createAction(mBundle.getString("MainFrame.maxCheckBoxMenuItem.text"));
        initAction(action, SHOW_MAX, keyStroke, null, true);

        //show indictors
        keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_F7, 0);
        action = createAction(mBundle.getString("MainFrame.indicatorCheckBoxMenuItem.text"));
        initAction(action, SHOW_INDICATORS, keyStroke, null, true);

        //help
        keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0);
        action = createAction(Dict.HELP.toString());
        initAction(action, HELP, keyStroke, null, true);

        //quit
        keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_Q, commandMask);
        action = createAction(Dict.QUIT.toString());
        initAction(action, QUIT, keyStroke, null, true);
    }

    private AlmondAction createAction(String name) {
        var action = new AlmondAction(name) {

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                mAppListeners.forEach(appActionListener -> {
                    try {
                        appActionListener.onAction(this, actionEvent);
                    } catch (Exception exception) {
                        System.err.println(exception.getMessage());
                    }
                });
            }
        };

        return action;
    }

    private static class Holder {

        private static final ActionManager INSTANCE = new ActionManager();
    }

}
