/*
 * Copyright 2024 Patrik Karlström <patrik@trixon.se>.
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

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import org.openide.awt.Actions;

/**
 *
 * @author Patrik Karlström <patrik@trixon.se>
 */
public class PopupManager extends MouseAdapter {

    private final MouseAdapter mMouseAdapter;
    private final JPopupMenu mPopupMenu = new JPopupMenu("YAYA");

    public static PopupManager getInstance() {
        return PopupHolder.INSTANCE;
    }

    private PopupManager() {
        initMenu();
        mMouseAdapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                maybeShowPopup(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                maybeShowPopup(e);
            }

            private void maybeShowPopup(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    mPopupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        };
    }

    public MouseAdapter getMouseAdapter() {
        return mMouseAdapter;
    }

    private void initMenu() {
        var newMenuItem = new JMenuItem();
        var fullscreenMenuItem = new JMenuItem();
        var optionsMenuItem = new JMenuItem();
        var removePlayerMenuItem = new JMenuItem();
        var helpMenuItem = new JMenuItem();
        var aboutMenuItem = new JMenuItem();
        var quitMenuItem = new JMenuItem();

        Actions.connect(newMenuItem, Actions.forID("Game", "se.trixon.yaya.actions.NewRoundAction"), false);
        Actions.connect(removePlayerMenuItem, Actions.forID("Game", "se.trixon.yaya.actions.RemovePlayerAction"), false);
        Actions.connect(optionsMenuItem, Actions.forID("Game", "se.trixon.yaya.actions.OptionsAction"), false);
        Actions.connect(fullscreenMenuItem, Actions.forID("Help", "se.trixon.yaya.actions.FullScreenAction"), false);
        Actions.connect(helpMenuItem, Actions.forID("Help", "se.trixon.yaya.actions.HelpAction"), false);
        Actions.connect(aboutMenuItem, Actions.forID("Help", "se.trixon.yaya.actions.AboutAction"), false);
        Actions.connect(quitMenuItem, Actions.forID("File", "se.trixon.almond.nbp.actions.QuitAction"), false);

        mPopupMenu.add(newMenuItem);
        mPopupMenu.add(fullscreenMenuItem);
        mPopupMenu.add(new JPopupMenu.Separator());
        mPopupMenu.add(optionsMenuItem);
        mPopupMenu.add(removePlayerMenuItem);
        mPopupMenu.add(new JPopupMenu.Separator());
        mPopupMenu.add(helpMenuItem);
        mPopupMenu.add(aboutMenuItem);
        mPopupMenu.add(new JPopupMenu.Separator());
        mPopupMenu.add(quitMenuItem);
    }

    private static class PopupHolder {

        private static final PopupManager INSTANCE = new PopupManager();
    }

}
