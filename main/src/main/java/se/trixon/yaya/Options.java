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

import org.openide.util.NbPreferences;
import se.trixon.almond.util.OptionsBase;

/**
 *
 * @author Patrik Karlström <patrik@trixon.se>
 */
public class Options extends OptionsBase {

    public static Options getInstance() {
        return Holder.INSTANCE;
    }

    private Options() {
        setPreferences(NbPreferences.forModule(Options.class));
    }

    public boolean isFullscreen() {
        return mPreferences.getBoolean(KEY_FULL_SCREEN, DEFAULT_FULL_SCREEN);
    }

    public void setFullscreen(boolean value) {
        mPreferences.putBoolean(KEY_FULL_SCREEN, value);
    }

    private static class Holder {

        private static final Options INSTANCE = new Options();
    }
}
