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
package org.nbgames.yaya.scorecard;

import java.util.Comparator;

/**
 *
 * @author Patrik Karlström
 */
public class PlayerColumnComparator implements Comparator<PlayerColumn> {

    public static final int ASCENDING = 1;
    public static final int DESCENDING = -1;
    private int sortOrder = ASCENDING;

    public PlayerColumnComparator() {
    }

    public PlayerColumnComparator(int aSortOrder) {
        this.sortOrder = aSortOrder;
    }

    @Override
    public int compare(PlayerColumn o1, PlayerColumn o2) {
        int result = 0;
        if (o1.getCurrentScore() < o2.getCurrentScore()) {
            result = -1 * getSortOrder();
        } else if (o1.getCurrentScore() > o2.getCurrentScore()) {
            result = 1 * getSortOrder();
        }
        return result;
    }

    public int getSortOrder() {
        return sortOrder;
    }
}
