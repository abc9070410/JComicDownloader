/*
 ----------------------------------------------------------------------------------------------------
 Program Name : JComicDownloader
 Authors  : surveyorK
 Last Modified : 2011/11/1
 ----------------------------------------------------------------------------------------------------
 ChangeLog:

----------------------------------------------------------------------------------------------------
 */
package jcomicdownloader.table;

import jcomicdownloader.enums.*;
import javax.swing.table.*;
import java.util.*;

/**
 *
 * 定義書籤頁面的表格
 */
public class BookmarkTableModel extends DefaultTableModel {

    public BookmarkTableModel(Vector columnNames, int rowCount) {
        super( columnNames, rowCount) ;
    }
    public boolean isCellEditable(int row, int col) { // set unchanged item
        //Note that the data/cell address is constant,
        //no matter where the cell appears onscreen.
        if ( col == BookmarkTableEnum.COMMENT ) {
            // only could change value of checkbox
            return true;
        } else {
            return false;
        }
    }

    public Class getColumnClass(int c) { // boolean turn into checkbox
        return super.getColumnClass( c );
    }

    public Object getValueAt( int row, int col ) {
        if ( super.getRowCount() == 0 ) {
            // 列數為0，代表目前沒有資料，故只回傳一個空物件
            return new Object();
        }
        else
            return super.getValueAt( row, col );
    }

}
