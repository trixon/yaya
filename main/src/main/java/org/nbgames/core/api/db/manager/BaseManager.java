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
package org.nbgames.core.api.db.manager;

import com.healthmarketscience.sqlbuilder.QueryPreparer.PlaceHolder;
import com.healthmarketscience.sqlbuilder.dbspec.Constraint;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbColumn;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbConstraint;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbSchema;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbTable;
import java.sql.PreparedStatement;
import org.nbgames.core.api.db.Db;

/**
 *
 * @author Patrik Karlström
 */
public abstract class BaseManager {

    protected final Db mDb;
    protected DbColumn mId;
    protected PlaceHolder mIdPlaceHolder;
    protected PreparedStatement mInsertPreparedStatement;
    protected PreparedStatement mSelectPreparedStatement;
    protected DbTable mTable;
    protected PreparedStatement mUpdatePreparedStatement;

    public BaseManager() {
        mDb = Db.getInstance();
    }

    public void addNotNullConstraint(DbColumn column) {
        DbConstraint statusNotNullConstraint = new DbConstraint(column, null, Constraint.Type.NOT_NULL);
        column.addConstraint(statusNotNullConstraint);
    }

    public abstract void create();

    public DbColumn getId() {
        return mId;
    }

    public String getIndexName(DbColumn[] dbColumns, String suffix) {
        StringBuilder builder = new StringBuilder(mTable.getName()).append("_");

        if (dbColumns != null) {
            for (DbColumn dbColumn : dbColumns) {
                builder.append(dbColumn.getName()).append("_");
            }
        }

        if (builder.lastIndexOf("_") != builder.length() - 1) {
            builder.append("_");
        }

        builder.append(suffix);

        return builder.toString();
    }

    public DbSchema getSchema() {
        return mDb.getSpec().getDefaultSchema();

    }

    public DbTable getTable() {
        return mTable;
    }

}
