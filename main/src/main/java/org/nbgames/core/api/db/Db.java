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

import com.healthmarketscience.sqlbuilder.CreateTableQuery;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbConstraint;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbSpec;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbTable;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.openide.util.Exceptions;

/**
 *
 * @author Patrik Karlström
 */
public class Db {

    private Connection mAutoCommitConnection = null;
//    private final SystemOptions mOptions = SystemOptions.getInstance();
    private final String mConnStringEmbedded = String.format("jdbc:h2:%s;DEFRAG_ALWAYS=true", "mOptions.getDbFile().getAbsolutePath()");
    private final String mConnStringServer = String.format("jdbc:h2:tcp://localhost/%s;DEFRAG_ALWAYS=true", "mOptions.getDbFile().getAbsolutePath()");
    private final DbSpec mSpec;
    private boolean mTryEmbeddedMode = true;

    public static Db getInstance() {
        return Holder.INSTANCE;
    }

    private Db() {
        mSpec = new DbSpec();
        init();
    }

    public boolean create(DbTable table, DbConstraint... constraints) {
        boolean tableCreated;

        try ( Statement statement = getAutoCommitConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
            if (table.getConstraints().isEmpty()) {
                for (DbConstraint constraint : constraints) {
                    table.addConstraint(constraint);
                }
            }

            String sql = new CreateTableQuery(table, true).validate().toString();
            tableCreated = statement.execute(sql);
        } catch (SQLException ex) {
            System.err.println("Table creation failed. " + table.getName());
            Exceptions.printStackTrace(ex);
            tableCreated = false;
        }

        return tableCreated;
    }

    public void drop(DbTable table, boolean cascade) throws ClassNotFoundException, SQLException {
        try ( Statement statement = getAutoCommitConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
            String sql = String.format("DROP TABLE IF EXISTS %s %s;", table.getName(), cascade ? "CASCADE" : "");
            //System.out.println(sql);
            statement.execute(sql);
        }
    }

    public void dropAllObjects() throws ClassNotFoundException, SQLException {
        try ( Statement statement = getAutoCommitConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
            String sql = "DROP ALL OBJECTS;";
            System.out.println(sql);
            statement.execute(sql);
        }
    }

    public void execute(String sql) throws ClassNotFoundException, SQLException {
        try ( Statement statement = getAutoCommitConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
            statement.execute(sql);
        }
    }

    public boolean exists() {
        return new File("mOptions.getDbFile().getAbsolutePath()" + ".mv.db").exists();
    }

    public Connection getAutoCommitConnection() {
        try {
            if (mAutoCommitConnection == null || mAutoCommitConnection.isClosed()) {
                Class.forName("org.h2.Driver");
                mAutoCommitConnection = initConnection(mConnStringEmbedded);
//                mAutoCommitConnection = initConnection(mConnStringServer);
            }
        } catch (ClassNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        } catch (SQLException ex) {
            if (mTryEmbeddedMode) {
                mTryEmbeddedMode = false;
                try {
                    mAutoCommitConnection = initConnection(mConnStringEmbedded);
                } catch (SQLException ex1) {
                    Exceptions.printStackTrace(ex1);
                }
            } else {
                Exceptions.printStackTrace(ex);
            }
        }

        return mAutoCommitConnection;
    }

    public DbSpec getSpec() {
        return mSpec;
    }

    private void init() {
        mSpec.addDefaultSchema();
    }

    private Connection initConnection(String connectionString) throws SQLException {
        System.out.println(connectionString);
        return DriverManager.getConnection(connectionString, "sa", "");
    }

    private static class Holder {

        private static final Db INSTANCE = new Db();
    }
}
