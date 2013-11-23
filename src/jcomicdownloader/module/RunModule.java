/*
 ----------------------------------------------------------------------------------------------------
 Program Name : JComicDownloader
 Authors  : surveyorK
 Last Modified : 2011/10/25
 ----------------------------------------------------------------------------------------------------
 ChangeLog:
 1.14: 修復若沒有下載成功仍會產生空壓縮檔的bug。
 1.12: 部分網站邊解析邊下載，所以不用做最後的整本下載。
 ----------------------------------------------------------------------------------------------------
 */
package jcomicdownloader.module;

import java.util.logging.Level;
import java.util.logging.Logger;
import jcomicdownloader.tools.*;
import jcomicdownloader.*;
import jcomicdownloader.enums.*;

import java.io.*;
import java.util.*;
import javax.swing.JOptionPane;
import jcomicdownloader.Flag;
import jcomicdownloader.SetUp;

/**

 @author user
 */
public class RunModule {

    private String indexName = "";
    private String indexEncodeName = "";

    public RunModule() {
    }

    public synchronized void runMainProcess( ParseOnlineComicSite parse,
        String urlString ) {
        parse.printLogo();

        if ( urlString.matches( "(?s).*.htm" )
            || urlString.matches( "(?s).*.html" )
            || urlString.matches( "(?s).*.php" )
            || urlString.matches( "(?s).*.asp" )
            || urlString.matches( "(?s).*.jsp" )
            || urlString.matches( "(?s).*/" )
            || urlString.matches( "(?s).*\\?(?s).*" ) );
        else {
            urlString += "/";
        }

        Common.debugPrintln( "目前解析位址：" + urlString );

        if ( parse.isSingleVolumePage( urlString ) ) { // ex. http://www.89890.com/comic/7953/
            // the urlString is single volume page
            Common.debugPrintln( "單集頁面（single volume）" );
            Common.isMainPage = false;

            if ( parse.getRunMode() == RunModeEnum.DOWNLOAD_MODE ) {
                if ( parse.getTitle() == null || parse.getTitle().equals( "" ) ) {
                    parse.setTitle( parse.getTitleOnSingleVolumePage( urlString ) );
                }
                Common.debugPrintln( "漫畫名稱: " + parse.getTitle() );
                //Common.nowTitle = parse.getTitle();

                System.out.println( "++" + parse.getRunMode() + "++" );
                //if ( Flag.allowDownloadFlag && !Flag.downloadingFlag )

                runSingle( parse, urlString, new String( parse.getTitle() ) );
            }
        }
        else { // the urlString is main page
            Common.debugPrintln( "全集頁面（main page）" );
            Common.isMainPage = true;

            String allPageString = parse.getAllPageString( urlString );

            if ( allPageString == null || allPageString.equals( "" ) ) {
                Common.errorReport( "網頁沒有內容（網頁下載錯誤）" );
                Flag.downloadErrorFlag = true;
                JOptionPane.showMessageDialog( ComicDownGUI.mainFrame, "解析網頁失敗（網頁無法下載？！）",
                    "提醒訊息", JOptionPane.INFORMATION_MESSAGE );

                return;
            }

            if ( parse.getTitle() == null || parse.getTitle().equals( "" ) ) {
                parse.setTitle( parse.getTitleOnMainPage( urlString, allPageString ) );
            }
            Common.debugPrintln( "漫畫名稱: " + parse.getTitle() );
            //Common.nowTitle = parse.getTitle();

            Common.debugPrint( "開始解析解析各集位址和各集名稱：" );
            List<List<String>> combinationList = null;
            try {
                combinationList = parse.getVolumeTitleAndUrlOnMainPage( urlString, allPageString );
            }
            catch ( Exception ex ) {
                Common.hadleErrorMessage( ex, "解析集數時發生錯誤" );
                Flag.downloadErrorFlag = true;
                JOptionPane.showMessageDialog( ComicDownGUI.mainFrame, "解析集數失敗！",
                    "提醒訊息", JOptionPane.INFORMATION_MESSAGE );
                return;
            }
            if ( combinationList == null ) {
                JOptionPane.showMessageDialog( ComicDownGUI.mainFrame, "此頁面沒有可下載的集數！",
                    "提醒訊息", JOptionPane.INFORMATION_MESSAGE );
                return;
            }
            List<String> volumeList = combinationList.get( 0 );
            List<String> urlList = combinationList.get( 1 );

            Common.debugPrint( "  ......解析各集位址和各集名稱完畢!!" );

            parse.deleteTempFile( parse.getTempFileNames() );

            parse.outputVolumeAndUrlList( volumeList, urlList );

            //if ( Flag.allowDownloadFlag && !Flag.downloadingFlag ) {
            if ( parse.getRunMode() == RunModeEnum.DOWNLOAD_MODE ) {
                runSingle( parse, urlString, new String( parse.getTitle() ) );
                System.out.println( "-------------------------------" );
            }
        }

        if ( Flag.allowDownloadFlag ) {
            System.out.println( "\nAll downloads are done!\n" );
        }
    }

    private synchronized void runSingle( ParseOnlineComicSite parse, String urlString, String titleString ) {

        Common.debugPrintln( "開始處理單一集數" );
        if ( Run.isAlive ) {
            Flag.downloadingFlag = true;

            parse.setURL( urlString );
            parse.setTitle( titleString );

            parse.setParameters();

            if ( parse.siteID == Site.EH || parse.siteID == Site.EX /*
                 // CK小說規則：唯有當下載單一作品時，才用一層資料夾（此時標題名稱與集數名稱相同）
                 || ( parse.siteID == Site.CK_NOVEL && parse.getTitle().equals(
                 parse.getWholeTitle() ) )
                 // EYNY小說規則：唯有當下載單一作品時，才用一層資料夾（此時標題名稱與集數名稱相同）
                 || ( parse.siteID == Site.EYNY_NOVEL &&
                 parse.getTitle().equals( parse.getWholeTitle() ) )
                 || parse.siteID == Site.MYBEST
                 || parse.siteID == Site.WENKU
                 */ ) {
                // 下載資料夾只建立一層: 指定下載資料夾 / 標題名稱 /
                parse.setDownloadDirectory( SetUp.getOriginalDownloadDirectory()
                    + parse.getTitle() + Common.getSlash() );
            }
            else {
                // 下載資料夾建立兩層: 指定下載資料夾 / 標題名稱 / 集數名稱
                parse.setDownloadDirectory( SetUp.getOriginalDownloadDirectory()
                    + parse.getTitle() + Common.getSlash()
                    + parse.getWholeTitle() + Common.getSlash() );
            }

            try {
                // 若已存在同檔名壓縮檔，則不解析下載網址
                if ( isNovelSite( parse.siteID ) || isBlogSite( parse.siteID ) ) { // 針對小說和部落格
                    if ( !existTextFile( parse.getDownloadDirectory() ) ) {
                        parse.parseComicURL(); // 下載網頁檔
                    }
                }
                else { // 針對漫畫
                    if ( !existZipFile( parse.getDownloadDirectory() ) ) {
                        parse.parseComicURL(); // EH在此已經開始下載了。
                    }
                }
            }
            catch ( Exception ex ) {
                Common.hadleErrorMessage( ex, "在解析下載網址或下載過程中發生錯誤" );
                Flag.downloadErrorFlag = true;
            }


            String[] urls = parse.getComicURL();
            String[] refers = parse.getRefers();

            if ( SetUp.getOutputUrlFile() ) {
                Common.debugPrintln( "允許輸出圖片位址" );
                Common.outputUrlFile( urls, parse.getDownloadDirectory() );  // output url file
            }

            if ( !isDownloadBefore( parse.getSiteID() ) && SetUp.getDownloadPicFile() ) {
                System.out.println( "Download Directory : " + parse.getDownloadDirectory() );
                System.out.println( "\nReady to download ...\n" );

                // 如果已經有同檔名壓縮檔存在，就假設已經下載完畢而不下載。
                if ( !existZipFile( parse.getDownloadDirectory() ) ) {
                    Common.debugPrintln( "開始下載整集：" );

                    Common.downloadManyFile( urls, parse.getRefers(), parse.getDownloadDirectory(),
                        SetUp.getPicFrontName(), "jpg" );

                    // 下載過程中若發生錯誤，重新嘗試下載。
                    retry( urls, parse.getDownloadDirectory(), 1 );

                }
            }


            if ( new File( parse.getDownloadDirectory() ).exists() ) // 存在下載圖檔資料夾
            {
                followingWork( parse );
            }
            else {
                if ( isNovelSite( parse.siteID ) || isBlogSite( parse.siteID ) ) {// 下載小說或部落格
                    if ( existTextFile( parse.getDownloadDirectory() ) ) { // 已有壓縮檔當然不用產生資料夾
                        followingWork( parse );
                    }
                    else {
                        Flag.downloadErrorFlag = true; // 發生錯誤
                        Common.errorReport( "ERROR： 沒有產生" + parse.getDownloadDirectory() );
                    }
                }
                else { // 下載漫畫
                    if ( !existZipFile( parse.getDownloadDirectory() ) ) { // 已有壓縮檔當然不用產生資料夾
                        Flag.downloadErrorFlag = true; // 發生錯誤
                        Common.errorReport( "ERROR： 沒有產生" + parse.getDownloadDirectory() );
                    }
                }
            }

            Flag.downloadingFlag = false;
        }
    }

    // 下載過程中若發生錯誤，重新嘗試下載。
    public static void retry( String[] urls, String downloadDirectory, int retryTimes ) {
        for ( int i = 0; i < retryTimes; i++ ) {
            if ( Flag.downloadErrorFlag ) {
                Flag.downloadErrorFlag = false;
                Common.debugPrintln( "由於下載過程中發生錯誤，重新檢查一次" );
                Common.downloadManyFile( urls, downloadDirectory,
                    SetUp.getPicFrontName(), "jpg" );
            }

        }
    }

    // 此任務是否為小說網站
    public static boolean isNovelSite( int siteID ) {
        if ( siteID == Site.CK_NOVEL
            || siteID == Site.EYNY_NOVEL
            || siteID == Site.MYBEST
            || siteID == Site.WENKU
            || siteID == Site.TIANYA_BOOK
            || siteID == Site.EIGHT_NOVEL
            || siteID == Site.QQ_BOOK
            || siteID == Site.QQ_ORIGIN_BOOK
            || siteID == Site.SINA_BOOK
            || siteID == Site.FIVEONE_CTO
            || siteID == Site.UUS8
            || siteID == Site.WENKU8
            || siteID == Site.WENKU7
            || siteID == Site.IFENG_BOOK
            || siteID == Site.XUNLOOK
            || siteID == Site.WOYOUXIAN
            || siteID == Site.SHUNONG ) {
            return true;
        }
        else {
            return false;
        }
    }

    // 此任務是否為部落格網站
    public static boolean isBlogSite( int siteID ) {
        if ( siteID == Site.BLOGSPOT
            || siteID == Site.PIXNET_BLOG
            || siteID == Site.XUITE_BLOG
            || siteID == Site.YAM_BLOG ) {
            return true;
        }
        else {
            return false;
        }
    }

    // 此任務是否為音樂網站
    public static boolean isMusicSite( int siteID ) {
        if ( siteID == Site.SOGOU
            || siteID == Site.TING1
            || siteID == Site.XIAMI ) {
            return true;
        }
        else {
            return false;
        }
    }

    // 有些站在解析圖片網址的同時就在下載了，那就不用再進入到整本下載區
    public boolean isDownloadBefore( int siteID ) {
        if ( siteID == Site.EH
            || siteID == Site.EX
            || siteID == Site.JUMPCN
            || siteID == Site.KUKU
            || siteID == Site.DMEDEN
            || siteID == Site.MANGAFOX
            || siteID == Site.XINDM
            || siteID == Site.WY
            || siteID == Site.GOOGLE_PIC
            || siteID == Site.CITY_MANGA
            || siteID == Site.BAIDU
            || siteID == Site.BENGOU
            || siteID == Site.EMLAND
            || siteID == Site.MOP
            || siteID == Site.DM5
            || siteID == Site.IASK
            || siteID == Site.IMANHUA
            || siteID == Site.VERYIM
            || siteID == Site.SIX_MANGA
            || siteID == Site.COMIC_131
            || siteID == Site.XXBH
            || siteID == Site.TWO_ECY
            || siteID == Site.ONESEVEN_KK
            || siteID == Site.FUMANHUA
            || siteID == Site.CK ) {
            return true;
        }
        else if ( isNovelSite( siteID ) || isBlogSite( siteID ) || isMusicSite( siteID ) ) {
            return true;
        }
        else {
            return false;
        }
    }

    public boolean existZipFile( String downloadPicDirectory ) {
        String file;
        if ( downloadPicDirectory.lastIndexOf( Common.getSlash() ) == downloadPicDirectory.length() - 1 ) {
            file = downloadPicDirectory.substring( 0, downloadPicDirectory.length() - 1 ) + "." + SetUp.getCompressFormat();
        }
        else {
            file = downloadPicDirectory + "." + SetUp.getCompressFormat();
        }

        //Common.debugPrintln( "將處理的壓縮檔名稱：" + file );
        if ( new File( file ).exists() && new File( file ).length() > 1024 ) {
            Common.debugPrintln( file + "已經存在!" );
            return true;
        }
        else {
            return false;
        }
    }

    public boolean existTextFile( String downloadPicDirectory ) {
        String file;
        if ( downloadPicDirectory.lastIndexOf( Common.getSlash() ) == downloadPicDirectory.length() - 1 ) {
            file = downloadPicDirectory.substring( 0, downloadPicDirectory.length() - 1 ) + "." + Common.getDefaultTextExtension();
        }
        else {
            file = downloadPicDirectory + "." + Common.getDefaultTextExtension();
        }

        //Common.debugPrintln( "將處理的文件檔名稱：" + file );
        if ( new File( file ).exists() && new File( file ).length() > 1024 ) {
            Common.debugPrintln( file + "已經存在!" );
            return true;
        }
        else {
            return false;
        }
    }

    public synchronized void followingWork( ParseOnlineComicSite parse ) { // compress or not & delete or not
        Common.debugPrintln( "開始後續動作：" );
        if ( Run.isAlive ) {
            File downloadPath = new File( parse.getDownloadDirectory() );

            if ( SetUp.getRunSingleDoneScript() ) { // 每一集下載完就執行單一任務腳本

                // 先決定執行所在路徑
                String runPath = ""; // 在此路徑中執行
                if ( isNovelSite( parse.siteID ) ) {
                    // 因為小說會在集數目錄外產生合併檔，所以在外一層執行
                    runPath = parse.getParentPath( parse.getDownloadDirectory() );
                }
                else {
                    runPath = parse.getDownloadDirectory();
                }

                ComicDownGUI.stateBar.setText( "單集下載完成，執行腳本中" );
                Common.debugPrintln( "單集下載完成，執行腳本中" );
                Common.runShellScript( SetUp.getSingleDoneScriptFile(), runPath );

                if ( SetUp.getSingleScriptWaitTime() > 0 ) {
                    try {
                        // 因為腳本執行與java主程式不同步，可自行設定等待時間，等腳本執行結束再接續執行壓縮動作
                        ComicDownGUI.stateBar.setText( "腳本執行中，等待" + SetUp.getSingleScriptWaitTime() + "秒..." );
                        Thread.currentThread().sleep( SetUp.getSingleScriptWaitTime() * 1000 );
                    }
                    catch ( InterruptedException ex ) {
                        Logger.getLogger( RunModule.class.getName() ).log( Level.SEVERE, null, ex );
                    }
                }
            }

            if ( isNovelSite( parse.siteID ) || isBlogSite( parse.siteID ) || isMusicSite( parse.siteID ) ) {
                Common.debugPrintln( "小說或部落格類型不產生壓縮檔！" );
            }
            else {
                if ( SetUp.getAutoCompress() ) { // compress to zip file or not

                    File zipFile = new File( downloadPath.getParent() + "/"
                        + parse.getWholeTitle() + "." + SetUp.getCompressFormat() );

                    if ( downloadPath.list().length < 1 ) {
                        Common.debugPrintln( "不產生壓縮檔（" + downloadPath.getAbsolutePath() + "資料夾內沒有任何檔案）" );
                    }
                    else if ( zipFile.exists() && zipFile.length() > 1024 ) {
                        Common.debugPrintln( "不產生壓縮檔（" + zipFile.getAbsolutePath() + "已存在）" );
                    }
                    else {

                        Common.compress( downloadPath, zipFile );
                        System.out.println( zipFile.getAbsolutePath() + " made!" );
                    }

                }

                if ( SetUp.getDeleteOriginalPic() ) { // delete the whole folder or not
                    Common.deleteFolder( parse.getDownloadDirectory() );

                    System.out.println( parse.getDownloadDirectory() + " deletion!" );
                }


            }
        }
    }
}
