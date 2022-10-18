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
package org.nbgames.core.api;

import java.awt.image.BufferedImage;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import org.openide.modules.Modules;
import org.openide.util.NbBundle;
import se.trixon.almond.util.SystemHelper;

/**
 *
 * @author Patrik Karlström
 */
public abstract class GameController {

    private boolean mFirstRun = true;

    public GameController() {
        init();
    }

    public String getBundlePath() {
        return getClass().getPackage().getName().replace(".", "/") + "/" + "News";
    }

    public String getCopyright() {
        return getResource("Game-Copyright");
    }

    public String getCredit() {
        return getResource("Game-Credit");
    }

    public String getDescription() {
        return getResource("Game-Description");
    }

    public String getId() {
        return getClass().getName();
    }

    public BufferedImage getImage() {
        return new BufferedImage(96, 96, BufferedImage.TYPE_INT_ARGB);
    }

    public String getLicense() {
        return getResource("Game-License");
    }

    public String getModuleName() {
        return Modules.getDefault().ownerOf(getClass()).getDisplayName();
    }

    public String getName() {
        return getResource("Game-Name");
    }

    public ResourceBundle getNewsBundle() {
        return null;
    }

    public String getPackageAsPath() {
        return SystemHelper.getPackageAsPath(getClass());
    }

    public String getResource(String key) {
        try {
            return NbBundle.getMessage(getClass(), key);
        } catch (MissingResourceException e) {
            return "";
        }
    }

    public String getShortDescription() {
        return getResource("Game-ShortDescription");
    }

    public BufferedImage getToolBarBackground() {
        return null;
    }

    public String getVersion() {
        return getResource("Game-Version");
    }

    public boolean isFirstRun() {
        return mFirstRun;
    }

    public abstract void onRequestNewGameStart();

    public void setFirstRun(boolean firstRun) {
        mFirstRun = firstRun;
    }

    protected String getHelp(Class cls) {
        return SystemHelper.getLocalizedResourceAsString(cls, "help_%s.html", "help.html");
    }

    private void init() {
    }
}
