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
package org.nbgames.core.api.ui;

import se.trixon.yaya.Player;

/**
 *
 * @author Patrik Karlström
 */
public class GameOverItem {

    private Player mPlayer;
    private int mScore;

    public GameOverItem() {
    }

    public GameOverItem(Player player, int score) {
        mScore = score;
        mPlayer = player;
    }

    public Player getPlayer() {
        return mPlayer;
    }

    public int getScore() {
        return mScore;
    }

    public void setPlayer(Player player) {
        mPlayer = player;
    }

    public void setScore(int score) {
        mScore = score;
    }
}
