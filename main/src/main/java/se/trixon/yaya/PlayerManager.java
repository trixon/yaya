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
package se.trixon.yaya;

import com.healthmarketscience.sqlbuilder.BinaryCondition;
import com.healthmarketscience.sqlbuilder.DeleteQuery;
import com.healthmarketscience.sqlbuilder.InsertQuery;
import com.healthmarketscience.sqlbuilder.OrderObject;
import com.healthmarketscience.sqlbuilder.QueryPreparer;
import com.healthmarketscience.sqlbuilder.QueryPreparer.PlaceHolder;
import com.healthmarketscience.sqlbuilder.SelectQuery;
import com.healthmarketscience.sqlbuilder.UpdateQuery;
import com.healthmarketscience.sqlbuilder.dbspec.Constraint;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbColumn;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbConstraint;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbSchema;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbTable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import org.nbgames.core.api.db.Db;
import org.openide.util.Exceptions;
import se.trixon.yaya.Player;

/**
 *
 * @author Patrik Karlström
 */
public class PlayerManager {

    private static final String COL_HANDEDNESS = "handedness";
    private static final String COL_ID = "player_id";
    private static final String COL_NAME = "name";
    private static final String TABLE_NAME = "player";
    private final Db mDb;
    private final DbColumn mHandedness;
    private PlaceHolder mHandednessPlaceHolder;
    private PlaceHolder mHandednessUpdatePlaceHolder;
    private DbColumn mId;
    private PlaceHolder mIdPlaceHolder;
    private PlaceHolder mIdUpdatePlaceHolder;
    private PreparedStatement mInsertPreparedStatement;
    private final DbColumn mName;
    private PlaceHolder mNamePlaceHolder;
    private PlaceHolder mNameUpdatePlaceHolder;
    private PreparedStatement mSelectPreparedStatement;
    private DbTable mTable;
    private PreparedStatement mUpdatePreparedStatement;

    public static PlayerManager getInstance() {
        return Holder.INSTANCE;
    }

    private PlayerManager() {
        mDb = Db.getInstance();

        mTable = getSchema().addTable(TABLE_NAME);
        mId = mTable.addColumn(COL_ID, "IDENTITY", null);
        mName = mTable.addColumn(COL_NAME, "VARCHAR", Integer.MAX_VALUE);
        mHandedness = mTable.addColumn(COL_HANDEDNESS, "INT", null);

        addNotNullConstraint(mName);
    }

    public void addNotNullConstraint(DbColumn column) {
        DbConstraint statusNotNullConstraint = new DbConstraint(column, null, Constraint.Type.NOT_NULL);
        column.addConstraint(statusNotNullConstraint);
    }

    public void create() {
        String indexName;
        indexName = getIndexName(new DbColumn[]{mId}, "pkey");
        DbConstraint primaryKeyConstraint = new DbConstraint(mTable, indexName, Constraint.Type.PRIMARY_KEY, mId);

        indexName = getIndexName(new DbColumn[]{mName}, "key");
        DbConstraint uniqueConstraint = new DbConstraint(mTable, indexName, Constraint.Type.UNIQUE, mName);

        mDb.create(mTable, primaryKeyConstraint, uniqueConstraint);
        try {
            mDb.execute("CREATE TRIGGER PLAYER AFTER INSERT,DELETE ON PLAYER CALL \"org.nbgames.core.ui.PlayerTrigger\";");
        } catch (ClassNotFoundException | SQLException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public DefaultComboBoxModel<Player> getComboBoxModel() {
        DefaultComboBoxModel<Player> players = new DefaultComboBoxModel<>();

        select().forEach((player) -> {
            players.addElement(player);
        });

        return players;
    }

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

    public DefaultListModel<Player> getListModel() {
        DefaultListModel<Player> players = new DefaultListModel<>();

        select().forEach((player) -> {
            players.addElement(player);
        });

        return players;
    }

    public DbSchema getSchema() {
        return mDb.getSpec().getDefaultSchema();

    }

    public DbTable getTable() {
        return mTable;
    }

    public void save(HashSet<Player> changeSet, HashSet<Player> deleteSet) throws ClassNotFoundException, SQLException {
        for (Player player : deleteSet) {
            delete(player);
        }

        for (Player player : changeSet) {
            if (player.getId() == null) {
                insert(player);
            } else {
                update(player);
            }
        }

        deleteSet.clear();
        changeSet.clear();
    }

    public ArrayList<Player> select() {
        if (mSelectPreparedStatement == null) {
//            selectPrepare();
        }

        ArrayList<Player> players = new ArrayList<>();
        players.add(new Player(1L, "Ask", Player.Handedness.LEFT));
        players.add(new Player(2L, "Embla", Player.Handedness.RIGHT));
        if (true) {
            return players;
        }
        try {
            ResultSet rs = mSelectPreparedStatement.executeQuery();
            rs.beforeFirst();

            while (rs.next()) {
                Player player = new Player();
                player.setId(rs.getLong(mId.getName()));
                player.setName(rs.getString(mName.getName()));
                player.setHandedness(Player.Handedness.values()[rs.getInt(mHandedness.getName())]);

                players.add(player);
            }
        } catch (SQLException ex) {
            Exceptions.printStackTrace(ex);
        }

        return players;
    }

    private void delete(Player player) throws ClassNotFoundException, SQLException {
        DeleteQuery deleteQuery = new DeleteQuery(mTable)
                .addCondition(BinaryCondition.equalTo(mId, player.getId()))
                .validate();

        String sql = deleteQuery.toString();

        mDb.execute(sql);
    }

    private void insert(Player player) throws SQLException, ClassNotFoundException {
        if (mInsertPreparedStatement == null) {
            insertPrepare();
        }

        mNamePlaceHolder.setString(player.getName(), mInsertPreparedStatement);
        mHandednessPlaceHolder.setInt(player.getHandedness().ordinal(), mInsertPreparedStatement);

        mInsertPreparedStatement.executeUpdate();
    }

    private void insertPrepare() throws SQLException {
        QueryPreparer preparer = new QueryPreparer();

        mNamePlaceHolder = preparer.getNewPlaceHolder();
        mHandednessPlaceHolder = preparer.getNewPlaceHolder();

        InsertQuery insertQuery = new InsertQuery(mTable)
                .addColumn(mId, mIdPlaceHolder)
                .addColumn(mName, mNamePlaceHolder)
                .addColumn(mHandedness, mHandednessPlaceHolder)
                .validate();

        String sql = insertQuery.toString();

        mInsertPreparedStatement = mDb.getAutoCommitConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
    }

    private void selectPrepare() {
        SelectQuery selectQuery = new SelectQuery()
                .addFromTable(mTable)
                .addAllColumns()
                .addOrdering(mName, OrderObject.Dir.ASCENDING)
                .validate();

        String sql = selectQuery.toString();

        try {
            mSelectPreparedStatement = mDb.getAutoCommitConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        } catch (SQLException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    private void update(Player player) throws SQLException {
        if (mUpdatePreparedStatement == null) {
            updatePrepare();
        }

        mIdUpdatePlaceHolder.setLong(player.getId(), mUpdatePreparedStatement);
        mNameUpdatePlaceHolder.setString(player.getName(), mUpdatePreparedStatement);
        mHandednessUpdatePlaceHolder.setInt(player.getHandedness().ordinal(), mUpdatePreparedStatement);

        mUpdatePreparedStatement.executeUpdate();
    }

    private void updatePrepare() throws SQLException {
        QueryPreparer preparer = new QueryPreparer();

        mIdUpdatePlaceHolder = preparer.getNewPlaceHolder();
        mNameUpdatePlaceHolder = preparer.getNewPlaceHolder();
        mHandednessUpdatePlaceHolder = preparer.getNewPlaceHolder();

        UpdateQuery updateQuery = new UpdateQuery(mTable)
                .addCondition(BinaryCondition.equalTo(mId, mIdUpdatePlaceHolder))
                .addSetClause(mName, mNameUpdatePlaceHolder)
                .addSetClause(mHandedness, mHandednessUpdatePlaceHolder)
                .validate();

        String sql = updateQuery.toString();

        mUpdatePreparedStatement = mDb.getAutoCommitConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
    }

    private static class Holder {

        private static final PlayerManager INSTANCE = new PlayerManager();
    }
}
