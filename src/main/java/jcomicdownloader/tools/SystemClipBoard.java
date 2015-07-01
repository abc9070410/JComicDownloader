/*
----------------------------------------------------------------------------------------------------
Program Name : JComicDownloader
Authors  : surveyorK
Last Modified : 2011/10/25
----------------------------------------------------------------------------------------------------
ChangeLog:

----------------------------------------------------------------------------------------------------
 */
package jcomicdownloader.tools;

import java.io.*;
import java.awt.Toolkit;
import java.awt.datatransfer.*;

/**
 *
 * 監控系統剪貼簿
 */
public class SystemClipBoard implements ClipboardOwner {

    private Clipboard sysClipBoard;
    private Transferable clipcontent;
    private String clipString;

    public SystemClipBoard() {
        super();
        initialize();
        clipString = "";
    }

    private void initialize() {
        sysClipBoard = Toolkit.getDefaultToolkit().getSystemClipboard(); //獲取系統剪貼簿
        clipcontent = sysClipBoard.getContents( null ); //取得剪貼簿內容
        sysClipBoard.setContents( clipcontent, this ); //設定剪貼簿內容並註冊擁有者		
        //擁有者可以用lostOwnership方法取得剪貼簿改變消息
    }

    public void lostOwnership( Clipboard clipboard, Transferable contents ) {
        /*
        try {
        Thread.sleep(20);   //讓執行緒小睡，等待剪貼簿準備好，這行很重要!!
        clipcontent = clipboard.getContents(null);	//再次獲得剪貼板內容
        clipboard.setContents(clipcontent, this);	//要一直監聽所以要在註冊一次
        } catch (Exception e) {
        System.out.println("Exception: " + e);
        }
         */
    }

    // 取得剪貼簿字串
    public String getClipString() {
        try {
            if ( clipcontent.isDataFlavorSupported( DataFlavor.stringFlavor ) ) {
                clipString = (String) clipcontent.getTransferData( DataFlavor.stringFlavor );
                //Common.debugPrintln( "系統剪貼簿內容:" + clipString );
            }
        } catch ( Exception e ) {
            System.out.println( "Exception: " + e );
        }
        return clipString;
    }

    public static void main( String[] args ) {
        SystemClipBoard thisClass = new SystemClipBoard();
    }
}
