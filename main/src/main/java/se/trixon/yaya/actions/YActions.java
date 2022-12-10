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
package se.trixon.yaya.actions;

import org.apache.commons.lang3.StringUtils;
import org.openide.util.Lookup;

/**
 *
 * @author Patrik Karlström
 */
public class YActions {

    public static YAction forId(String category, String id) {
        for (var action : Lookup.getDefault().lookupAll(YAction.class)) {
            if (StringUtils.equalsIgnoreCase(action.category(), category) && StringUtils.equalsIgnoreCase(action.id(), id)) {
                return action;
            }
        }

        System.err.println(String.format("Action not found: %s/%s", category, id));
        return null;
    }
}
