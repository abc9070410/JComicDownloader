/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jcomicdownloader.module;

import javax.swing.JOptionPane;
import jcomicdownloader.ComicDownGUI;
import jcomicdownloader.SetUp;
import jcomicdownloader.enums.Site;
import jcomicdownloader.tools.Common;
import jcomicdownloader.tools.CommonGUI;

/**
 *
 * @author apple
 */
public class ParseEX extends ParseEH {
    boolean isSetID=false;
    
    public ParseEX() {
        super();
        enumName = "EX";
        regexs= new String[]{"(?s).*exhentai.org(?s).*"};
        parserName=this.getClass().getName();
        downloadBefore=true;
        siteID=Site.formString("EX");
        siteName = "EX";
        indexName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_ex_Hentai_parse_", "html" );
        indexEncodeName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_ex_Hentai_encode_parse_", "html" );

        needCookie = true;
        /*
        int memberID = 0;
        if ( SetUp.getEhMemberID() != 0 )
        memberID = SetUp.getEhMemberID();
        else
        memberID = (int) ( Math.random() * 789000 + 100 );
         */
//         setIDandPasswordHash(); // 輸入id和hash並存入設定檔

        cookieString = "ipb_member_id=" + SetUp.getEhMemberID()
                + ";ipb_pass_hash=" + SetUp.getEhMemberPasswordHash();
        Common.debugPrintln( "使用的cookie: " + cookieString );

        baseSiteURL = "https://exhentai.org";
        collectionMainTitle = "EX-Hentai_Collection";
    }

    public ParseEX( String webSite, String titleName ) {
        this();
        this.webSite = webSite;
        this.title = titleName;
        //Common.newEncodeFile( SetUp.getTempDirectory(), indexName, indexEncodeName );
    }

    public static void initialIDandPasswordHash() { // 重置id和hash並存入設定檔
        SetUp.setEhMemberID( "0" );
        SetUp.setEhMemberPasswordHash( "NULL" );
        SetUp.writeSetFile(); // 將目前的設定存入設定檔（set.ini）
    }

    public void setIDandPasswordHash() { // 輸入id和hash並存入設定檔
        enterMemberID(); // 輸入id
        enterMemberPasswordHash(); // 輸入密碼hash
        SetUp.writeSetFile(); // 將目前的設定存入設定檔（set.ini）
    }

    public void enterMemberID() { // 輸入id
        if ( SetUp.getEhMemberID().equals( "0" ) || 
            !SetUp.getEhMemberID().matches( "\\d+" )  ) {
            CommonGUI.showInputDialogValue = "InitialValue";
            String idString = CommonGUI.showInputDialog( ComicDownGUI.mainFrame,
                    "請輸入e-hentai的ipb_member_id", "輸入視窗", JOptionPane.INFORMATION_MESSAGE );
            
            System.out.println( "輸入：" + idString );
            if ( idString.matches( "\\d+" ) ) {
                SetUp.setEhMemberID( idString );
                //break;
            } else {
                CommonGUI.showMessageDialog( ComicDownGUI.mainFrame,
                        "輸入錯誤！（必須全為數字），請重新輸入", "提醒訊息", JOptionPane.ERROR_MESSAGE );
                return;
            }
        }
    }

    public void enterMemberPasswordHash() { // 輸入密碼hash
        if ( SetUp.getEhMemberPasswordHash().equals( "NULL" ) || 
                SetUp.getEhMemberPasswordHash().equals( "null" ) ||
             SetUp.getEhMemberPasswordHash().equals( "InitialValue" ) ) {
            CommonGUI.showInputDialogValue = "InitialValue";
            String hashString = CommonGUI.showInputDialog( ComicDownGUI.mainFrame,
                    "請輸入e-hentai的ipb_pass_hash", "輸入視窗", JOptionPane.INFORMATION_MESSAGE );
            System.out.println( "輸入：" + hashString );
            SetUp.setEhMemberPasswordHash( hashString );
            
        }
    }

    @Override
    public String getAllPageString( String urlString ) {
        if (!isSetID){
            setIDandPasswordHash(); // 輸入id和hash並存入設定檔
            isSetID=true;
        }
        String indexName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_EX_", "html" );
        String indexEncodeName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_EX_encode_", "html" );

        Common.slowDownloadFile( urlString, SetUp.getTempDirectory(), indexName, 1000, true, cookieString );

        return Common.getFileString( SetUp.getTempDirectory(), indexName );
    }
}