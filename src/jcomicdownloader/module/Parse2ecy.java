/*
----------------------------------------------------------------------------------------------------
Program Name : JComicDownloader
Authors  : surveyorK
Last Modified : 2013/6/26
----------------------------------------------------------------------------------------------------
ChangeLog:
*5.17: 修復2ecy解析錯誤的問題。
 5.15: 修復2ecy解析頁數錯誤的問題。
 *  4.07: 1. 新增對2ecy的支援。
----------------------------------------------------------------------------------------------------
 */
package jcomicdownloader.module;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import jcomicdownloader.tools.*;
import jcomicdownloader.enums.*;
import java.util.*;
import jcomicdownloader.SetUp;
import jcomicdownloader.encode.Encoding;
import jcomicdownloader.encode.Zhcode;

public class Parse2ecy extends ParseOnlineComicSite {

    private int radixNumber; // use to figure out the name of pic
    private String jsName;
    protected String indexName;
    protected String indexEncodeName;
    protected String baseURL;

    /**
    
    @author user
     */
    public Parse2ecy() {
        siteID = Site.TWO_ECY;
        siteName = "2ecy";
        indexName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_2ecy_parse_", "html" );
        indexEncodeName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_2ecy_encode_parse_", "html" );

        jsName = "index_2ecy.js";
        radixNumber = 10371; // default value, not always be useful!!

        baseURL = "http://manhua.2ecy.com";
    }

    public Parse2ecy( String webSite, String titleName ) {
        this();
        this.webSite = webSite;
        this.title = titleName;
    }

    @Override
    public void setParameters() {
        Common.debugPrintln( "開始解析各參數 :" );
        Common.debugPrintln( "開始解析title和wholeTitle :" );

        Common.downloadFile( webSite, SetUp.getTempDirectory(), indexName, false, "" );
        Common.newEncodeFile( SetUp.getTempDirectory(), indexName, indexEncodeName );

        if ( getWholeTitle() == null || getWholeTitle().equals( "" ) ) {
            // 因為正常解析不需要用到單集頁面，所以給此兩行放進來
            String allPageString = Common.getFileString( SetUp.getTempDirectory(), indexEncodeName );

            int beginIndex = allPageString.indexOf( "<h1>" );
            beginIndex = allPageString.indexOf( ">", beginIndex ) + 1;
            int endIndex = allPageString.indexOf( "</h1>", beginIndex );
            String tempTitleString = allPageString.substring( beginIndex, endIndex ).trim();

            setWholeTitle( getVolumeWithFormatNumber( Common.getStringRemovedIllegalChar(
                    Common.getTraditionalChinese( tempTitleString.trim() ) ) ) );
        }

        Common.debugPrintln( "作品名稱(title) : " + getTitle() );
        Common.debugPrintln( "章節名稱(wholeTitle) : " + getWholeTitle() );
    }

    @Override
    public void parseComicURL() { // parse URL and save all URLs in comicURL  //

        // 先取得所有的下載伺服器網址
        int beginIndex = 0, endIndex = 0;
        String tempString = "";

        Common.debugPrint( "開始解析這一集有幾頁 : " );
        String allPageString = Common.getFileString( SetUp.getTempDirectory(), indexName );

        beginIndex = allPageString.indexOf( "span class=\"text" );
        beginIndex = allPageString.indexOf( "/", beginIndex ) + 1;
        endIndex = allPageString.indexOf( "<", beginIndex ) - 1;
        tempString = allPageString.substring( beginIndex, endIndex ).trim();

        totalPage = Integer.parseInt( tempString );
        Common.debugPrintln( "共 " + totalPage + " 頁" );
        comicURL = new String[totalPage];

        String pageURL = webSite;
        for ( int i = 1 ; i <= totalPage && Run.isAlive ; i ++ ) {
            
            
            if ( !Common.existPicFile( getDownloadDirectory(), i )
                    || !Common.existPicFile( getDownloadDirectory(), i + 1 ) ) {
                allPageString = getAllPageString( pageURL );
                // 開始第一張圖片位址
                beginIndex = allPageString.indexOf( "class=\"bigpicshow" );
                beginIndex = allPageString.indexOf( " src=", beginIndex );
                beginIndex = allPageString.indexOf( "\"", beginIndex ) + 1;
                endIndex = allPageString.indexOf( "\"", beginIndex );
                String firstPicURL = Common.getFixedChineseURL( allPageString.substring( beginIndex, endIndex ) );
                //Common.debugPrintln( "第一張圖片位址：" + firstPicURL );

                // 每解析一個網址就下載一張圖
                singlePageDownload( getTitle(), getWholeTitle(), firstPicURL, totalPage, i, 0 );
                
            }
            else {
                Common.debugPrintln( "跳過：" + pageURL );
            }
            
            pageURL = pageURL.replace( i + ".html", (i + 1) + ".html" );
            //System.exit( 0 ); // debug
        }
    }

    @Override
    public String getAllPageString( String urlString ) {
        String indexName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_2ecy_", "html" );

        Common.downloadFile( urlString, SetUp.getTempDirectory(), indexName, false, "" );

        return Common.getFileString( SetUp.getTempDirectory(), indexName );
    }

    @Override
    public boolean isSingleVolumePage( String urlString ) {
        // ex. http://manhua.2ecy.com/manhua/8090/52132_2.html
        if ( urlString.matches( "(?s).*\\.html(?s).*" ) ) {
            return true;
        }
        else // ex. http://manhua.2ecy.com/manhua/8090/
        {
            return false;
        }
    }

    public String getMainUrlFromSingleVolumeUrl( String volumeURL ) {
        // ex. http://manhua.2ecy.com/manhua/8090/52132_2.html轉為
        //    http://manhua.2ecy.com/manhua/8090/

        int endIndex = volumeURL.lastIndexOf( "/" ) + 1;

        String mainPageURL = volumeURL.substring( 0, endIndex );

        Common.debugPrintln( "MAIN_URL: " + mainPageURL );

        return mainPageURL;
    }

    @Override
    public String getTitleOnMainPage( String urlString, String allPageString ) {
        int beginIndex = allPageString.indexOf( "add_favorite(" );
        beginIndex = allPageString.indexOf( "'", beginIndex ) + 1;
        int endIndex = allPageString.indexOf( "'", beginIndex );
        String title = allPageString.substring( beginIndex, endIndex ).trim();

        return Common.getStringRemovedIllegalChar( Common.getTraditionalChinese( title ) );
    }

    @Override
    public List<List<String>> getVolumeTitleAndUrlOnMainPage( String urlString, String allPageString ) {
        // combine volumeList and urlList into combinationList, return it.

        List<List<String>> combinationList = new ArrayList<List<String>>();
        List<String> urlList = new ArrayList<String>();
        List<String> volumeList = new ArrayList<String>();

        int beginIndex = allPageString.indexOf( "class=\"tab-content\"" );
        int endIndex = allPageString.indexOf( "class=\"comic", beginIndex );
        String tempString = allPageString.substring( beginIndex, endIndex );

        int volumeCount = tempString.split( " href=" ).length - 1;

        String volumeTitle = "";
        beginIndex = endIndex = 0;
        for ( int i = 0 ; i < volumeCount ; i++ ) {

            // 取得單集位址
            beginIndex = tempString.indexOf( " href=", beginIndex );
            beginIndex = tempString.indexOf( "\"", beginIndex ) + 1;
            endIndex = tempString.indexOf( "\"", beginIndex );
            urlList.add( tempString.substring( beginIndex, endIndex ) );

            // 取得單集名稱
            beginIndex = tempString.indexOf( ">", beginIndex ) + 1;
            endIndex = tempString.indexOf( "</a>", beginIndex );
            volumeTitle = tempString.substring( beginIndex, endIndex );
            volumeList.add( getVolumeWithFormatNumber( Common.getStringRemovedIllegalChar(
                    Common.getTraditionalChinese( volumeTitle.trim() ) ) ) );
        }

        totalVolume = volumeCount;
        Common.debugPrintln( "共有" + totalVolume + "集" );

        combinationList.add( volumeList );
        combinationList.add( urlList );

        return combinationList;
    }
}
