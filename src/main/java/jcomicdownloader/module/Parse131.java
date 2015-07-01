/*
 ----------------------------------------------------------------------------------------------------
 Program Name : JComicDownloader
 Authors  : surveyorK
 Last Modified : 2013/4/3
 ----------------------------------------------------------------------------------------------------
 ChangeLog:
 5.16: 修復comic131無法下載的問題。
 5.14: 修復comic131名稱解析錯誤的問題。
 5.13: 修復comic131解析位址錯誤的問題。
 5.13: 修復comic131無效章節的問題。
修復comic131漫畫名稱解析錯誤的問題。
     5.02: 1. 修復comic131因改版而解析錯誤的問題。
 *  4.17: 1. 修復comic.131.com因改版而解析錯誤的問題。
 *  4.02: 1. 新增對comic.131.com的支援。

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

public class Parse131 extends ParseOnlineComicSite {

    private int radixNumber; // use to figure out the name of pic
    private String jsName;
    protected String indexName;
    protected String indexEncodeName;
    protected String baseURL;

    /**

     @author user
     */
    public Parse131() {
        siteID = Site.COMIC_131;
        siteName = "131";
        indexName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_131_parse_", "html" );
        indexEncodeName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_131_encode_parse_", "html" );

        jsName = "index_131.js";
        radixNumber = 15221471; // default value, not always be useful!!

        baseURL = "http://comic.131.com";
    }

    public Parse131( String webSite, String titleName ) {
        this();
        this.webSite = webSite;
        this.title = titleName;
    }

    @Override
    public void setParameters() {
        Common.debugPrintln( "開始解析各參數 :" );

        Common.debugPrintln( "開始解析title和wholeTitle :" );
        Common.downloadFile( webSite, SetUp.getTempDirectory(), indexName, false, "" );

        if ( getWholeTitle() == null || getWholeTitle().equals( "" ) ) {
            String allPageString = Common.getFileString( SetUp.getTempDirectory(), indexName );

            int beginIndex = allPageString.indexOf( "title=\"" ) + 7;
            int endIndex = allPageString.indexOf( "\"", beginIndex );
            String tempTitleString = allPageString.substring( beginIndex, endIndex ).trim();

            setWholeTitle( getVolumeWithFormatNumber( Common.getStringRemovedIllegalChar(
                Common.getTraditionalChinese( tempTitleString.trim() ) ) ) );
        }

        Common.debugPrintln( "作品名稱(title) : " + getTitle() );
        Common.debugPrintln( "章節名稱(wholeTitle) : " + getWholeTitle() );
    }

    @Override
    public void parseComicURL() { // parse URL and save all URLs in comicURL  //
        // 先取得前面的下載伺服器網址

        String allPageString = Common.getFileString( SetUp.getTempDirectory(), indexName );
        Common.debugPrint( "開始解析這一集有幾頁 : " );

        int beginIndex = allPageString.indexOf( "</em>/" ) + 5;
        beginIndex = allPageString.indexOf( "/", beginIndex ) + 1;
        int endIndex = allPageString.indexOf( "</p>", beginIndex );
        String tempString = allPageString.substring( beginIndex, endIndex );

        totalPage = Integer.parseInt( tempString );
        Common.debugPrintln( "共 " + totalPage + " 頁" );
        comicURL = new String[totalPage + 1];

        // 取得當前網頁目錄
        endIndex = webSite.lastIndexOf( "/" ) + 1;
        String volumeURL = webSite.substring( 0, endIndex );

        String pageURL = ""; // 每一頁的網址
        beginIndex = endIndex = 0;
        String picURL = ""; // 圖片網址
        for ( int i = 0; i < totalPage && Run.isAlive; ) {
            if ( !Common.existPicFile( getDownloadDirectory(), i + 1 )
                || !Common.existPicFile( getDownloadDirectory(), i + 2 ) ) {

                pageURL = volumeURL + ( i + 1 ) + ".html";
                allPageString = getAllPageString( pageURL );

                // 開始解析第一張圖片網址
                beginIndex = allPageString.indexOf( "id=\"comicBigPic\"" );
                beginIndex = allPageString.indexOf( "src=", beginIndex );
                beginIndex = allPageString.indexOf( "\"", beginIndex ) + 1;
                endIndex = allPageString.indexOf( "\"", beginIndex );
                picURL = allPageString.substring( beginIndex, endIndex );
                
                Common.debugPrintln( i + "解析圖片位址:" + picURL ); // debug
                
                //System.exit( 0 );

                comicURL[i++] = Common.getFixedChineseURL( picURL );
                // 每解析一個網址就下載一張圖
                singlePageDownload( getTitle(), getWholeTitle(), comicURL[i - 1], totalPage, i, 0 );
                
                /*
                // 檢查有無後面兩張圖片
                if ( allPageString.indexOf( "src=\"\"" ) < 0 ) {
                    beginIndex = allPageString.indexOf( "var comicurl" );
                    beginIndex = allPageString.indexOf( "<img src=", beginIndex ) + 1;
                    beginIndex = allPageString.indexOf( "\"", beginIndex ) + 1;
                    endIndex = allPageString.indexOf( "\"", beginIndex );
                    picURL = allPageString.substring( beginIndex, endIndex );

                    if ( i < totalPage ) {
                        comicURL[i++] = Common.getFixedChineseURL( picURL );
                        // 每解析一個網址就下載一張圖
                        singlePageDownload( getTitle(), getWholeTitle(), comicURL[i - 1], totalPage, i, 0 );
                        //Common.debugPrintln( ( p + 1 ) + " " + comicURL[p] ); // debug
                    }

                    beginIndex = allPageString.indexOf( "<img src=", beginIndex ) + 1;
                    beginIndex = allPageString.indexOf( "\"", beginIndex ) + 1;
                    endIndex = allPageString.indexOf( "\"", beginIndex );
                    picURL = allPageString.substring( beginIndex, endIndex );

                    if ( i < totalPage ) {
                        comicURL[i++] = Common.getFixedChineseURL( picURL );
                        // 每解析一個網址就下載一張圖
                        singlePageDownload( getTitle(), getWholeTitle(), comicURL[i - 1], totalPage, i, 0 );
                        //Common.debugPrintln( ( p + 1 ) + " " + comicURL[p] ); // debug
                    }
                }
                */
            }
            else {
                i++;
            }

        }

        //System.exit( 0 ); // debug
    }

    public void showParameters() { // for debug
        Common.debugPrintln( "----------" );
        Common.debugPrintln( "totalPage = " + totalPage );
        Common.debugPrintln( "webSite = " + webSite );
        Common.debugPrintln( "----------" );
    }

    @Override
    public String getAllPageString( String urlString ) {
        String indexName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_131_", "html" );

        Common.downloadFile( urlString, SetUp.getTempDirectory(), indexName, false, "" );

        return Common.getFileString( SetUp.getTempDirectory(), indexName );
    }

    @Override
    public boolean isSingleVolumePage( String urlString ) {
        // ex. http://comic.131.com/content/15798/152652/1.html
        if ( Common.getAmountOfString( urlString, "/" ) > 5 ) {
            return true;
        }
        else {
            return false;
        }
    }

    public String getMainUrlFromSingleVolumeUrl( String volumeURL ) {
        // ex. http://comic.131.com/content/15798/152652/1.html轉為
        //     http://comic.131.com/content/shaonian/15798.html

        String allPageString = getAllPageString( volumeURL );
        int beginIndex = allPageString.indexOf( "class=\"mh_szwz2\"" );
        beginIndex = allPageString.indexOf( "href=\"http", beginIndex );
        beginIndex = allPageString.indexOf( "\"", beginIndex ) + 1;
        int endIndex = allPageString.indexOf( "\"", beginIndex );

        String mainPageURL = allPageString.substring( beginIndex, endIndex );

        Common.debugPrintln( "MAIN_URL: " + mainPageURL );

        return mainPageURL;
    }

    @Override
    public String getTitleOnSingleVolumePage( String urlString ) {
        String mainUrlString = getMainUrlFromSingleVolumeUrl( urlString );

        return getTitleOnMainPage( mainUrlString, getAllPageString( mainUrlString ) );
    }

    @Override
    public String getTitleOnMainPage( String urlString, String allPageString ) {
        int beginIndex, endIndex;

        endIndex = allPageString.lastIndexOf( "</h1>" );
        beginIndex = allPageString.lastIndexOf( ">", endIndex ) + 1;
        
        while ( allPageString.substring( beginIndex, endIndex ).trim().equals( "" ) )
        {
            endIndex = allPageString.lastIndexOf( "<", endIndex - 1 );
            beginIndex = allPageString.lastIndexOf( ">", endIndex ) + 1;
        }
            
            
        
        //String tempString = allPageString.substring( beginIndex, endIndex );
        
        
        String title = allPageString.substring( beginIndex, endIndex ).trim();

        return Common.getStringRemovedIllegalChar( Common.getTraditionalChinese( title ) );
    }

    @Override
    public List<List<String>> getVolumeTitleAndUrlOnMainPage( String urlString, String allPageString ) {
        // combine volumeList and urlList into combinationList, return it.

        List<List<String>> combinationList = new ArrayList<List<String>>();
        List<String> urlList = new ArrayList<String>();
        List<String> volumeList = new ArrayList<String>();

        String tempString = "";
        int beginIndex, endIndex;

        beginIndex = allPageString.indexOf( "class=\"mh_fj\"" );
        endIndex = allPageString.indexOf( "class=\"cinnerlink\"", beginIndex );

        // 存放集數頁面資訊的字串
        tempString = allPageString.substring( beginIndex, endIndex );

        int volumeCount = tempString.split( " href=" ).length - 1;

        String volumeTitle = "";
        beginIndex = endIndex = 0;
        for ( int i = 0; i < volumeCount; i++ ) {
            // 取得單集位址
            beginIndex = tempString.indexOf( " href=", beginIndex );
            beginIndex = tempString.indexOf( "\"", beginIndex ) + 1;
            endIndex = tempString.indexOf( "\"", beginIndex );
            urlList.add( tempString.substring( beginIndex, endIndex ) );

            // 取得單集名稱
            beginIndex = tempString.indexOf( ">", beginIndex ) + 1;
            endIndex = tempString.indexOf( "<", beginIndex );
            if ( beginIndex == endIndex ) {
                endIndex = tempString.indexOf( "<", endIndex + 1 );
            }
            volumeTitle = tempString.substring( beginIndex, endIndex );
            volumeTitle = volumeTitle.replaceFirst( "<font.*>", "" );
            volumeTitle = volumeTitle.replaceFirst( "<img.*>", "" );

            volumeList.add( getVolumeWithFormatNumber( Common.getStringRemovedIllegalChar(
                Common.getTraditionalChinese( volumeTitle.trim() ) ) ) );

        }

        totalVolume = volumeCount;
        Common.debugPrintln( "共有" + totalVolume + "集" );

        combinationList.add( volumeList );
        combinationList.add( urlList );

        return combinationList;
    }

    @Override
    public void outputVolumeAndUrlList( List<String> volumeList, List<String> urlList ) {
        Common.outputFile( volumeList, SetUp.getTempDirectory(), Common.tempVolumeFileName );
        Common.outputFile( urlList, SetUp.getTempDirectory(), Common.tempUrlFileName );
    }

    @Override
    public String[] getTempFileNames() {
        return new String[]{indexName, indexEncodeName, jsName};
    }
}
