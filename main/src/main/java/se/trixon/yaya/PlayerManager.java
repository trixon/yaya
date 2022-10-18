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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;

/**
 *
 * @author Patrik Karlström
 */
public class PlayerManager {

    private static final String COL_HANDEDNESS = "handedness";
    private static final String COL_ID = "player_id";
    private static final String COL_NAME = "name";
    private static final String TABLE_NAME = "player";

    public static PlayerManager getInstance() {
        return Holder.INSTANCE;
    }

    private PlayerManager() {
    }

    public DefaultComboBoxModel<Player> getComboBoxModel() {
        DefaultComboBoxModel<Player> players = new DefaultComboBoxModel<>();

        select().forEach((player) -> {
            players.addElement(player);
        });

        return players;
    }

    public DefaultListModel<Player> getListModel() {
        DefaultListModel<Player> players = new DefaultListModel<>();

        select().forEach((player) -> {
            players.addElement(player);
        });

        return players;
    }

    public void save(HashSet<Player> changeSet, HashSet<Player> deleteSet) throws ClassNotFoundException, SQLException {
        for (Player player : deleteSet) {
        }

        for (Player player : changeSet) {
            if (player.getId() == null) {
            } else {
            }
        }

        deleteSet.clear();
        changeSet.clear();
    }

    public ArrayList<Player> select() {
        ArrayList<Player> players = new ArrayList<>();
        players.add(new Player(1L, "Ask", Player.Handedness.LEFT));
        players.add(new Player(2L, "Embla", Player.Handedness.RIGHT));

        return players;
    }

    private static class Holder {

        private static final PlayerManager INSTANCE = new PlayerManager();
    }
}
