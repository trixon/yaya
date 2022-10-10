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
package org.nbgames.core.api.db;

import java.sql.SQLException;
import org.nbgames.core.api.db.manager.BaseManager;
import org.nbgames.core.api.db.manager.PlayerManager;
import org.openide.util.Exceptions;

/**
 *
 * @author Patrik Karlström
 */
public class DbCreator {

    private final Db mDb = Db.getInstance();

    public static DbCreator getInstance() {
        return Holder.INSTANCE;
    }

    private DbCreator() {
    }

    public void initDb() {
        init(PlayerManager.getInstance());
    }

    private void init(BaseManager manager) {
        try {
            mDb.drop(manager.getTable(), true);
        } catch (ClassNotFoundException | SQLException ex) {
            Exceptions.printStackTrace(ex);
        }

        manager.create();
    }

    private static class Holder {

        private static final DbCreator INSTANCE = new DbCreator();
    }
}
