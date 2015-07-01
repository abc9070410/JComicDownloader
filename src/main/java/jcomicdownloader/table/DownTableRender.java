/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jcomicdownloader.table;

// 給表格內容改變字體顏色
import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import jcomicdownloader.SetUp;
import jcomicdownloader.enums.FrameEnum;
import jcomicdownloader.tools.CommonGUI;

public class DownTableRender extends DefaultTableCellRenderer {

    private String title; // 漫畫名稱
    private String url; // 漫畫位址
    DefaultTableModel tableModel; // 選擇表格的內容
    private Color defaultColor; // 基本列表顏色
    private Color mouseEnteredColor; // 滑鼠移進來的列表顏色

    public DownTableRender( DefaultTableModel tableModel, int whichFrame ) {
        this( tableModel );

        if ( whichFrame == FrameEnum.MAIN_FRAME ) {
            defaultColor = SetUp.getMainFrameTableDefaultColor();
            mouseEnteredColor = SetUp.getMainFrameTableMouseEnteredColor();
        }
        else if ( whichFrame == FrameEnum.CHOICE_FRAME ) {
            defaultColor = SetUp.getChoiceFrameTableDefaultColor();
            mouseEnteredColor = SetUp.getChoiceFrameTableMouseEnteredColor();
        }
        else {
        }


    }

    public DownTableRender( DefaultTableModel tableModel ) {
        super();
        this.title = title;
        this.url = url;
        this.tableModel = tableModel;
    }

    public Component getTableCellRendererComponent(
        JTable table,
        Object value,
        boolean isSelected,
        boolean hasFocus,
        int row,
        int column ) {
        Component cell =
            super.getTableCellRendererComponent(
            table,
            value,
            isSelected,
            hasFocus,
            row,
            column );
        
        cell.setFont( SetUp.getDefaultFont(-3)); // 隨設定而改變字體大小

        if ( SetUp.getUsingBackgroundPicOfMainFrame() ) {
            if ( isSelected || CommonGUI.nowMouseAtRow == row ) {
                cell.setForeground( mouseEnteredColor );
            }
            else {
                cell.setForeground( defaultColor );
                
                
            }
        }

        return cell;
    }
}
