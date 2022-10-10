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

import org.nbgames.core.api.options.OptionsCategory;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Patrik Karlström
 */
public enum GameCategory {
    ARCADE,
    BOARD,
    CARD,
    DICE,
    LOGIC,
    STRATEGY;

    private GameCategory() {
    }

    public OptionsCategory getOptionsCategory() {
        Map<GameCategory, OptionsCategory> categories = new HashMap<>();
        categories.put(GameCategory.ARCADE, OptionsCategory.ARCADE);
        categories.put(GameCategory.BOARD, OptionsCategory.BOARD);
        categories.put(GameCategory.CARD, OptionsCategory.CARD);
        categories.put(GameCategory.DICE, OptionsCategory.DICE);
        categories.put(GameCategory.LOGIC, OptionsCategory.LOGIC);
        categories.put(GameCategory.STRATEGY, OptionsCategory.STRATEGY);

        return categories.get(this);
    }

    public String getString() {
        return NbGames.getBundle().getString("category_" + name().toLowerCase());
    }
}
