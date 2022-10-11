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

import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Patrik Karlström
 */
public class PlayerTableModel extends AbstractTableModel {

    private String[] columns = {"active", "name", "lefty"};
    private Object[][] rows = {
        {new Boolean(true), "Pata", new Boolean(false)},
        {new Boolean(false), "Egon", new Boolean(true)}
    };

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return super.getColumnClass(columnIndex);
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public String getColumnName(int column) {
        return super.getColumnName(column);
    }

    @Override
    public int getRowCount() {
        return rows.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return rows[rowIndex][columnIndex];
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        if (columnIndex < 2) {
            return false;
        } else {
            return true;
        }

//        return super.isCellEditable(rowIndex, columnIndex);
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
//        super.setValueAt(aValue, rowIndex, columnIndex);
        rows[rowIndex][columnIndex] = aValue;
        fireTableCellUpdated(rowIndex, columnIndex);

    }
}
