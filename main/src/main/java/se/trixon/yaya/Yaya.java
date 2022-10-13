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

import org.nbgames.core.api.options.NbgOptionsPanel;

/**
 *
 * @author Patrik Karlström <patrik@trixon.se>
 */
public class Yaya {

    public static final String LOG_TITLE = "Yaya";
    private GamePanel mGamePanel;
    private NewGamePanel mNewGamePanel;
    private OptionsPanel mOptionPanel;

    public static Yaya getInstance() {
        return Holder.INSTANCE;
    }

    private Yaya() {
    }

    public NewGamePanel getNewGamePanel() {
        if (mNewGamePanel == null) {
            mNewGamePanel = new NewGamePanel();
            mNewGamePanel.load();
        }

        return mNewGamePanel;
    }

    public NbgOptionsPanel getOptionsPanel() {
        if (mOptionPanel == null) {
            mOptionPanel = new OptionsPanel();
        }

        return mOptionPanel;
    }

    public GamePanel getPanel() {
        if (mGamePanel == null) {
            mGamePanel = new GamePanel();
        }

        return mGamePanel;
    }

    public void onRequestNewGameStart() {
        getPanel().newGame();
    }

    private static class Holder {

        private static final Yaya INSTANCE = new Yaya();
    }
}
