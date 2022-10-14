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
package se.trixon.yaya.dice;

import org.nbgames.core.api.options.NbgOptions;
import org.openide.util.NbPreferences;

/**
 *
 * @author Patrik Karlström
 */
public class NbgDiceOptions extends NbgOptions {

    public static final String KEY_REVERSE_DIRECTION = "reverseDirection";
    private static final boolean DEFAULT_REVERSE_DIRECTION = false;

    public static NbgDiceOptions getInstance() {
        return Holder.INSTANCE;
    }

    private NbgDiceOptions() {
        mPreferences = NbPreferences.forModule(getClass());
    }

    public boolean isReverseDirection() {
        return mPreferences.getBoolean(KEY_REVERSE_DIRECTION, DEFAULT_REVERSE_DIRECTION);
    }

    public void setReverseDirection(boolean state) {
        mPreferences.putBoolean(KEY_REVERSE_DIRECTION, state);
    }

    private static class Holder {

        private static final NbgDiceOptions INSTANCE = new NbgDiceOptions();
    }
}
