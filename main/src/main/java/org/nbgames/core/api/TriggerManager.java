/* 
 * Copyright 2018 Patrik Karlström.
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

import java.util.prefs.Preferences;
import org.openide.util.NbPreferences;

/**
 *
 * @author Patrik Karlström
 */
public class TriggerManager {

    private final Preferences mPreferences = NbPreferences.forModule(TriggerManager.class);

    public static TriggerManager getInstance() {
        return Holder.INSTANCE;
    }

    private TriggerManager() {
    }

    public Preferences getPreferences() {
        return mPreferences;
    }

    public void update(Class cls) {
        mPreferences.putLong(cls.getName(), System.currentTimeMillis());
    }

    private static class Holder {

        private static final TriggerManager INSTANCE = new TriggerManager();
    }
}
