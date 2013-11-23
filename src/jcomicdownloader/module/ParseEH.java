/*
----------------------------------------------------------------------------------------------------
Program Name : JComicDownloader
Authors  : surveyorK
Last Modified : 2012/12/8
----------------------------------------------------------------------------------------------------
ChangeLog:
 5.10: 修改eh解析機制，使其無須全部頁面解析完畢才開始下載。
 5.10: 修復eh解析頁數錯誤的問題。
4.10: 修復在Substance介面下若跳出輸入視窗必崩潰的問題。
1.16: 新增對EX的支援
1.12: 將下載部分搬移到ParseOnlineComicSite，另作一個singlePageDownload()方法
----------------------------------------------------------------------------------------------------
 */
package jcomicdownloader.module;

import jcomicdownloader.tools.*;
import jcomicdownloader.enums.*;
import jcomicdownloader.*;

import java.io.*;
import java.util.*;
import java.text.*;
import javax.swing.JOptionPane;


public class ParseEH extends ParseOnlineComicSite {

    private String[] lines;
    private String[] comicPageURL;
    private int onePagePicCount;
    private int pageCount;
    protected boolean needCookie; // 是否要設定cookie
    protected String cookieString; // cookie要設定的參數
    protected String baseSiteURL; // 父目錄位址
    protected String collectionMainTitle; // 一次擷取整頁時的集合名稱

    /**
     *
     * @author user
     */
    public ParseEH() {
        siteID = Site.EH;
        siteName = "EH";
        indexName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_e_Hentai_parse_", "html" );
        indexEncodeName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_e_Hentai_encode_parse_", "html" );
        onePagePicCount = 20; // 20 pics on every page

        needCookie = false;
        cookieString = "";
        baseSiteURL = "http://g.e-hentai.org";
        collectionMainTitle = "E-Hentai_Collection";
    }

    public ParseEH( String webSite, String titleName ) {
        this();
        this.webSite = webSite;
        this.title = titleName;
        //Common.newEncodeFile( SetUp.getTempDirectory(), indexName, indexEncodeName );
    }

    @Override
    public void setParameters() { // let all the non-set attributes get values
        
        if ( SetUp.getEhMemberID() != null 
                && !"".equals( SetUp.getEhMemberID() ) 
                && !"0".equals( SetUp.getEhMemberID() ) ) {
            cookieString = "ipb_member_id=" + SetUp.getEhMemberID()
                + ";ipb_pass_hash=" + SetUp.getEhMemberPasswordHash();
            Common.debugPrintln( "有ID和passhash紀錄，使用cookie: " + cookieString );
        }

        Common.slowDownloadFile( webSite, SetUp.getTempDirectory(), indexName, 1000, needCookie, cookieString );

        String allPageString = Common.getFileString( SetUp.getTempDirectory(), indexName );


        if ( allPageString.matches( "(?s).*Content Warning(?s).*View Gallery(?s).*Never Warn Me Again(?s).*" ) ) {
            needCookie = true;
            Common.debugPrintln( "是警告畫面網頁，前去下載真實網頁" );
            cookieString = "nw=session";
            Common.slowDownloadFile( webSite, SetUp.getTempDirectory(), indexName, 1000, true, cookieString );
        }

        lines = Common.getFileStrings( SetUp.getTempDirectory(), indexName );

        Common.debugPrintln( "開始解析各參數 :" );
        Common.debugPrintln( "作品名稱(title) : " + title );
        Common.debugPrintln( "作品+章節名稱(wholeTitle) : " + wholeTitle );


        int beginIndex = 0;
        int endIndex = 0;
        for ( int i = 0 ; i < lines.length ; i++ ) {
            // ex. Showing 41 - 60 of 192 images
            if ( lines[i].matches( "(?s).*Showing(?s).*of(?s).*images(?s).*" ) ) {
                beginIndex = lines[i].indexOf( "Showing", 1 );
                beginIndex = lines[i].indexOf( "of", beginIndex ) + 2;
                endIndex = lines[i].indexOf( "images", beginIndex );

                totalPage = Integer.valueOf( lines[i].substring( beginIndex, endIndex ).replaceAll( ",", "" ).trim() );
                Common.debugPrintln( "總共頁數 : " + totalPage );
            }
        }

        if ( totalPage % onePagePicCount == 0 ) {
            pageCount = totalPage / onePagePicCount;
        } else {
            pageCount = totalPage / onePagePicCount + 1;
        }
        
        Common.debugPrintln( "共分幾個頁面 : " + pageCount );

        comicPageURL = new String[totalPage];
        comicURL = new String[totalPage]; // totalPage = amount of comic pic
        SetUp.setWholeTitle( wholeTitle );

        setDownloadDirectory( getDefaultDownloadDirectory() );
    }

    @Override
    public void parseComicURL() { // parse URL and save all URLs in comicURL
        int beginIndex = 0;
        int endIndex = 0;

        int pageUrlCount = 0; // for page url
        int beginPage = 0; // the page which starts to download
        
        //System.exit( 0);

        // get URLs of pic page
        for ( int pageNumber = 0 ;
                pageNumber < pageCount && Run.isAlive && !getTitle().equals( "Gallery Not Available" ) ;
                pageNumber++ ) {
            
            
            if ( Common.existPicFile( getDownloadDirectory(), pageUrlCount + 1, pageUrlCount + 21 ) ) {
                Common.debugPrintln( "第" + ( pageUrlCount + 1 ) + "到第" + ( pageUrlCount + 20 ) + "已下載，跳過此頁" );
                beginPage = pageUrlCount;
                pageUrlCount += 20;
                continue;
            }

            if ( pageNumber >= 1 ) {
                Common.slowDownloadFile( webSite + "?p=" + pageNumber, SetUp.getTempDirectory(), indexName, 2000, true, cookieString );
                lines = Common.getFileStrings( SetUp.getTempDirectory(), indexName );
            }

            

            int i = 0;
            for ( i = 0 ; i < lines.length ; i++ ) {
                // ex. <div class="gdtm" style="height:170px">
                if ( lines[i].matches( "(?s).*class=\"gdtm\"(?s).*" ) ) {
                    beginIndex = lines[i].indexOf( "class=\"gdtm\"", 1 );
                    break;
                }
            }

            
            Common.debugPrintln( "---------------- 從第" + ( pageUrlCount + 1 ) + "頁開始:" );
            
            
            for ( int count = 0 ; count < onePagePicCount && Run.isAlive ; count++ ) {
                //Common.debugPrintln( "\n" + lines.length + " LINE " + i + " " + beginIndex );//"\n " + lines[i] );
                beginIndex = lines[i].indexOf( baseSiteURL, beginIndex );
                endIndex = lines[i].indexOf( "\"><img alt=\"", beginIndex );

                //System.out.println( count + " = " + beginIndex + "   "  + endIndex );
                if ( beginIndex > 0 && endIndex > 0 && pageUrlCount < totalPage ) {
                    comicPageURL[pageUrlCount] = lines[i].substring( beginIndex, endIndex );
                    Common.debugPrintln( "第" + (pageUrlCount + 1) + "頁網址：" + comicPageURL[pageUrlCount] );
                    pageUrlCount++;
                }
                beginIndex = endIndex;
            }

        

        // get URLs of real pic
        for ( i = beginPage ; i < pageUrlCount && Run.isAlive && !getTitle().equals( "Gallery Not Available" ) ; i++ ) {
            if ( !Common.existPicFile( getDownloadDirectory(), i + 1 ) ||
                 !Common.existPicFile( getDownloadDirectory(), i + 2 ) ) {
                Common.slowDownloadFile( comicPageURL[i], SetUp.getTempDirectory(), indexName, 2000, needCookie, cookieString );
                String line = Common.getFileString( SetUp.getTempDirectory(), indexName );

                lines = line.split( "\"" );
                for ( int j = 0 ; j < lines.length && Run.isAlive; j++ ) {

                    if ( lines[j].matches( "(?s).*http://\\d+.\\d+.\\d+.\\d+(?s).*" ) ) {

                        comicURL[i] = lines[j].replaceAll( "amp;", "" );
                        //Common.debugPrintln( comicURL[i] );
                        
                        // 每解析一個網址就下載一張圖（隔兩秒連線一次）
                        singlePageDownload( getTitle(), null, comicURL[i], totalPage, i + 1, 2000 );

                        break;
                    }
                }
                //System.exit( 0 );
                
            } else {
                Common.debugPrint( i + " " );
            }

        }
        
        }

        if ( Flag.downloadingFlag && Common.withGUI() ) {
            ComicDownGUI.stateBar.setText( title + "下載完畢 !!" );
        }

    }

    @Override
    public String getDefaultDownloadDirectory() {
        String path = title + Common.getSlash();
        //Common.debugPrintln( "目前下載路徑：" + path );
        return path;
    }

    @Override
    public String getAllPageString( String urlString ) {
        String indexName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_EH_", "html" );
        String indexEncodeName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_EH_encode_", "html" );

        Common.slowDownloadFile( urlString, SetUp.getTempDirectory(), indexName, 1000, false, "" );

        return Common.getFileString( SetUp.getTempDirectory(), indexName );
    }

    @Override // 預設EH沒有單集頁面，只有主頁面。這樣可以讓單集也能加入任務
    public boolean isSingleVolumePage( String urlString ) {
        return false;
    }

    // 判斷是否為單集主頁面（就是有單集內容預覽圖的頁面）
    public boolean isRealSingleVolumePage( String urlString ) {
        if ( urlString.matches( "(?s).*hentai.org/g/\\d+(?s).*" ) ) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String getTitleOnSingleVolumePage( String urlString ) {

        String allPageString = getAllPageString( urlString );

        return getTitleOnSingleVolumePageByAllPageString( allPageString );
    }

    public String getTitleOnSingleVolumePageByAllPageString( String allPageString ) {
        
        int beginIndex = allPageString.indexOf( "id=\"gj\"" );
        beginIndex = allPageString.indexOf( ">", beginIndex ) + 1;
        int endIndex = allPageString.indexOf( "</h1>", beginIndex );
        String titleString = allPageString.substring( beginIndex, endIndex ).trim();

        if ( "".equals( titleString ) ) {
            beginIndex = allPageString.indexOf( "<title>" );
            beginIndex = allPageString.indexOf( ">", beginIndex ) + 1;
            if ( siteID == Site.EH ) {
                endIndex = allPageString.indexOf( "E-Hentai", beginIndex ) - 3;
            } else if ( siteID == Site.EX ) {
                endIndex = allPageString.indexOf( "ExHentai", beginIndex ) - 3;
            }
            
            titleString = allPageString.substring( beginIndex, endIndex );
        }

        return Common.getStringRemovedIllegalChar( titleString.replaceAll( "&times;", "×" ) );
    }

    public int getTitleCountOnMainPage( String allPageString ) {
        return allPageString.split( "gtr\\d" ).length - 1;
    }

    @Override
    public List<List<String>> getVolumeTitleAndUrlOnMainPage( String urlString, String allPageString ) {
        // combine volumeList and urlList into combinationList, return it.

        List<List<String>> combinationList = new ArrayList<List<String>>();
        List<String> urlList = new ArrayList<String>();
        List<String> volumeList = new ArrayList<String>();

        if ( isRealSingleVolumePage( urlString ) ) {
            // 網址連結到的是單集主頁面（就是有單集內容預覽圖的頁面）

            urlList.add( urlString );
            String titleString = getTitleOnSingleVolumePageByAllPageString( allPageString );
            if ( titleString.matches( "Gallery Not Available" ) ) {
                
                if ( urlString.matches( "(?s).*exhentai.org(?s).*" ) ) {
                    JOptionPane.showMessageDialog( ComicDownGUI.mainFrame,
                        "解析錯誤，判斷是輸入有問題，因此重置ID和Hash值，請再一次選擇集數！", "提醒訊息", JOptionPane.ERROR_MESSAGE );
                    ParseEX.initialIDandPasswordHash();
                }
                else {
                    JOptionPane.showMessageDialog( ComicDownGUI.mainFrame,
                        "此頁面已被刪除或搬移，無法下載！", "提醒訊息", JOptionPane.ERROR_MESSAGE );
                }
            }

            volumeList.add( getTitleOnSingleVolumePageByAllPageString( allPageString ) );
        } else { // 網址連結到的是搜索頁面或是標籤頁面（就是只有很多作品標題的頁面）
            int beginIndex = allPageString.indexOf( "Published", 0 );
            int titleCount = getTitleCountOnMainPage( allPageString );
            String[] tokenStrings = allPageString.substring( beginIndex, allPageString.length() ).split( "\"|>|<" );

            int mainPageIndex = 0;
            for ( int i = 0 ; i < titleCount ; i++ ) {
                while ( !tokenStrings[mainPageIndex].matches( baseSiteURL + "/g/\\d+/(?s).*" ) ) {
                    mainPageIndex++;
                }

                urlList.add( tokenStrings[mainPageIndex] );

                mainPageIndex++;

                while ( !tokenStrings[mainPageIndex].equals( urlList.get( i ) ) ) {
                    mainPageIndex++;
                }

                volumeList.add( tokenStrings[mainPageIndex + 2].replaceAll( "&times;", "×" ) );

                Common.debugPrint( i + " 標題 : " + volumeList.get( i ) );
                Common.debugPrintln( "     網址 : " + urlList.get( i ) );

                mainPageIndex++;
            }
        }

        combinationList.add( volumeList );
        combinationList.add( urlList );

        return combinationList;
    }

    @Override
    public String getTitleOnMainPage( String urlString, String allPageString ) {
        //setTitle( "E-Hentai" );
        if ( isRealSingleVolumePage( urlString ) ) {
            String title = getTitleOnSingleVolumePageByAllPageString( allPageString );
            setWholeTitle( title );
            return title;
        } else {
            String title = collectionMainTitle;
            setTitle( title );
            return title;
        }
    }

    @Override
    public String getMainUrlFromSingleVolumeUrl( String volumeURL ) {
        throw new UnsupportedOperationException( "Not supported yet." );
    }
}
class ParseEX extends ParseEH {

    public ParseEX() {
        super();
        siteID = Site.EX;
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

        setIDandPasswordHash(); // 輸入id和hash並存入設定檔

        cookieString = "ipb_member_id=" + SetUp.getEhMemberID()
                + ";ipb_pass_hash=" + SetUp.getEhMemberPasswordHash();
        Common.debugPrintln( "使用的cookie: " + cookieString );

        baseSiteURL = "http://exhentai.org";
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
        String indexName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_EX_", "html" );
        String indexEncodeName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_EX_encode_", "html" );

        Common.slowDownloadFile( urlString, SetUp.getTempDirectory(), indexName, 1000, true, cookieString );

        return Common.getFileString( SetUp.getTempDirectory(), indexName );
    }
}