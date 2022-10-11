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
package org.nbgames.yaya.gamedef;

/**
 *
 * @author Patrik Karlström
 */
import java.util.LinkedList;

public class GameRows extends LinkedList<GameRow> {

    public int[] getLim() {
        int[] values = new int[size()];
        int i = 0;

        for (GameRow gameRow : this) {
            values[i] = gameRow.getLim();
            i++;
        }

        return values;
    }

    public int[] getMax() {
        int[] values = new int[size()];
        int i = 0;

        for (GameRow gameRow : this) {
            values[i] = gameRow.getMax();
            i++;
        }

        return values;
    }
}
