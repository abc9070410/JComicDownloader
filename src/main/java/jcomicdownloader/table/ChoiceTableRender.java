/*
 ----------------------------------------------------------------------------------------------------
 Program Name : JComicDownloader
 Authors  : surveyorK
 Last Modified : 2012/11/4
 ----------------------------------------------------------------------------------------------------
 ChangeLog:
 5.05: 加入辨識rar壓縮檔為已下載集數。
 ----------------------------------------------------------------------------------------------------
 */
package jcomicdownloader.table;

import java.awt.Color;
import java.awt.Component;
import java.io.File;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import jcomicdownloader.SetUp;
import jcomicdownloader.enums.ChoiceTableEnum;
import jcomicdownloader.frame.ChoiceFrame;
import jcomicdownloader.tools.Common;
import jcomicdownloader.tools.CommonGUI;

/**

 @author surveyork
 */
public class ChoiceTableRender extends DefaultTableCellRenderer {

    private String title; // 漫畫名稱
    private String url; // 漫畫位址
    DownloadTableModel tableModel; // 選擇表格的內容

    public ChoiceTableRender( String title, String url, DownloadTableModel tableModel ) {
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
        
       cell.setFont( SetUp.getDefaultFont(-5)); // 隨設定而改變字體大小

        // if ( hasFocus ) {
        //     cell.setBackground( Color.green );
        //     cell.setForeground( Color.black );
        // } else {

        // 取得介面設定值（不用UIManager.getLookAndFeel().getName()是因為這樣才能讀到_之後的參數）
        String nowSkinName = SetUp.getSkinClassName();

        if ( existsFileOnThisRow( ChoiceFrame.volumeTable.convertRowIndexToModel( row ) ) ) { // 若存在就顯示淺黑色
            //cell.setBackground( Color.gray );
            //if ( nowSkinName.equals( "HiFi" ) || nowSkinName.equals( "Noire" ) ) {

            if ( SetUp.getUsingBackgroundPicOfChoiceFrame() ) {
                if ( isSelected || CommonGUI.nowMouseAtRow == row ) {
                    cell.setForeground( SetUp.getChoiceFrameTableMouseEnteredColor() );
                }
                else {
                    cell.setForeground( SetUp.getChoiceFrameTableFileExistedColor() );
                }
            }
            else if ( CommonGUI.isDarkSytleSkin( nowSkinName ) ) {
                cell.setForeground( Color.black );
            }
            else {
                cell.setForeground( Color.lightGray );
            }
        }
        else { // 若不存在則顯示正常黑色
            //cell.setBackground( Color.white );
            //if ( nowSkinName.equals( "HiFi" ) || nowSkinName.equals( "Noire" ) ) {
            if ( SetUp.getUsingBackgroundPicOfChoiceFrame() ) {
                if ( isSelected || CommonGUI.nowMouseAtRow == row ) {
                    cell.setForeground( SetUp.getChoiceFrameTableMouseEnteredColor() );
                }
                else {
                    cell.setForeground( SetUp.getChoiceFrameTableDefaultColor() );
                }
            }
            else if ( CommonGUI.isDarkSytleSkin( nowSkinName ) ) {
                cell.setForeground( Color.lightGray );
            }
            else {
                cell.setForeground( Color.black );
            }
        }

        // }
        return cell;

    }

    // 檢查第row列的單集漫畫是否已經存在於下載資料夾（只要有資料夾或壓縮檔都算）
    private boolean existsFileOnThisRow( int row ) {
        String volumeTitle = tableModel.getValueAt( row, ChoiceTableEnum.VOLUME_TITLE ).toString();

        File dirFile = null;
        File zipFile = null;
        File cbzFile = null;
        File rarFile = null;
        if ( this.url.matches( "(?s).*hentai.org(?s).*" ) ) { // 讓EH和EX也能判斷是否已經下載
            dirFile = new File( SetUp.getOriginalDownloadDirectory() + volumeTitle );
            zipFile = new File( SetUp.getOriginalDownloadDirectory() + volumeTitle + ".zip" );
            cbzFile = new File( SetUp.getOriginalDownloadDirectory() + volumeTitle + ".cbz" );
            rarFile = new File( SetUp.getOriginalDownloadDirectory() + volumeTitle + ".rar" );
        }
        else {
            dirFile = new File( SetUp.getOriginalDownloadDirectory() + this.title + Common.getSlash() + volumeTitle );
            zipFile = new File( SetUp.getOriginalDownloadDirectory() + this.title + Common.getSlash() + volumeTitle + ".zip" );
            cbzFile = new File( SetUp.getOriginalDownloadDirectory() + this.title + Common.getSlash() + volumeTitle + ".cbz" );
            rarFile = new File( SetUp.getOriginalDownloadDirectory() + this.title + Common.getSlash() + volumeTitle + ".rar" );
        }

        if ( dirFile.exists() || zipFile.exists() || cbzFile.exists() || rarFile.exists() ) {
            return true;
        }
        else {
            return false;
        }
    }
}

