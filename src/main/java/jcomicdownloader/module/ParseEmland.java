/*
 ----------------------------------------------------------------------------------------------------
 Program Name : JComicDownloader
 Authors  : surveyorK
 Last Modified : 2011/12/27
 ----------------------------------------------------------------------------------------------------
 ChangeLog:
 *  2.13: 1. 新增對mh.emland.net的支援。
 ----------------------------------------------------------------------------------------------------
 */
package jcomicdownloader.module;

import jcomicdownloader.tools.*;
import jcomicdownloader.enums.*;
import java.util.*;
import jcomicdownloader.SetUp;
import jcomicdownloader.encode.Encoding;

public class ParseEmland extends ParseOnlineComicSite {

    private int radixNumber; // use to figure out the name of pic
    private String jsName;
    protected String indexName;
    protected String indexEncodeName;
    protected String baseURL;

    /**

     @author user
     */
    public ParseEmland() {
        siteID = Site.EMLAND;
        siteName = "Emland";
        indexName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_emland_parse_", "html" );
        indexEncodeName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_emland_encode_parse_", "html" );

        jsName = "index_emland.js";
        radixNumber = 15961471; // default value, not always be useful!!

        baseURL = "http://mh.emland.net/";
    }

    public ParseEmland( String webSite, String titleName ) {
        this();
        this.webSite = webSite;
        this.title = titleName;
    }

    @Override
    public void setParameters() {
        Common.debugPrintln( "開始解析各參數 :" );

        Common.debugPrintln( "開始解析title和wholeTitle :" );

        if ( getWholeTitle() == null || getWholeTitle().equals( "" ) ) {
            String allPageString = Common.getFileString( SetUp.getTempDirectory(), indexEncodeName );

            int beginIndex = allPageString.indexOf( "content=" ) + 1;
            beginIndex = allPageString.indexOf( "content=", beginIndex ) + 1;
            beginIndex = allPageString.indexOf( "\"", beginIndex ) + 1;
            int endIndex = allPageString.indexOf( ",", beginIndex );
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

        String allPageString = Common.getFileString( SetUp.getTempDirectory(), indexEncodeName );
        Common.debugPrint( "開始解析這一集有幾頁 : " );

        int beginIndex = allPageString.indexOf( "<select" ) + 1;
        beginIndex = allPageString.indexOf( ">", beginIndex ) + 1;
        int endIndex = allPageString.indexOf( "</select>", beginIndex );

        String tempString = allPageString.substring( beginIndex, endIndex );

        totalPage = tempString.split( "<option" ).length - 1;
        Common.debugPrintln( "共 " + totalPage + " 頁" );
        comicURL = new String[totalPage];

        String picURL = "";
        int p = 0; // 目前頁數
        for ( int i = 0; i < totalPage && Run.isAlive; i++ ) {
            if ( !Common.existPicFile( getDownloadDirectory(), i + 1 )
                || !Common.existPicFile( getDownloadDirectory(), i + 2 ) ) {
                beginIndex = allPageString.indexOf( "var nowpath" );
                beginIndex = allPageString.indexOf( "\"", beginIndex ) + 1;
                endIndex = allPageString.indexOf( "\"", beginIndex );

                comicURL[p++] = allPageString.substring( beginIndex, endIndex );
                //Common.debugPrintln( p + " " + comicURL[p-1]  ); // debug

                // 每解析一個網址就下載一張圖
                singlePageDownload( getTitle(), getWholeTitle(), comicURL[p - 1], totalPage, p, 0 );

                if ( p < totalPage ) {
                    beginIndex = allPageString.indexOf( "</select>" );
                    beginIndex = allPageString.indexOf( "href=\"", beginIndex ) + 6;
                    endIndex = allPageString.indexOf( "\"", beginIndex );

                    String nextPageURL = baseURL + allPageString.substring( beginIndex, endIndex );
                    allPageString = getAllPageString( nextPageURL );
                }
            }
            else {
                p ++;
            }
        }

        //System.exit( 0 ); // debug
    }

    @Override
    public String getAllPageString( String urlString ) {
        Common.downloadFile( urlString, SetUp.getTempDirectory(), indexName, false, "" );
        Common.newEncodeFile( SetUp.getTempDirectory(), indexName, indexEncodeName, Encoding.GBK );

        return Common.getFileString( SetUp.getTempDirectory(), indexEncodeName );
    }

    @Override
    public boolean isSingleVolumePage( String urlString ) {
        // ex. http://mh.emland.net/pic2165928.html
        String allPageString = getAllPageString( urlString );

        if ( allPageString.indexOf( "name=\"description\"" ) < 0 ) {
            return true;
        }
        else {
            return false;
        }
    }

    public String getMainUrlFromSingleVolumeUrl( String volumeURL ) {
        // ex. http://mh.emland.net/pic2165928.html轉為
        //    http://mh.emland.net/manga34390.html

        String allPageString = getAllPageString( volumeURL );

        int beginIndex = allPageString.lastIndexOf( "<a href=" );
        beginIndex = allPageString.lastIndexOf( "=", beginIndex ) + 1;
        int endIndex = allPageString.lastIndexOf( ">", beginIndex );
        String mainPageURL = baseURL + volumeURL.substring( beginIndex, endIndex ).trim();

        Common.debugPrintln( "MAIN_URL: " + mainPageURL );

        return mainPageURL;
    }

    @Override
    public String getTitleOnMainPage( String urlString, String allPageString ) {
        int beginIndex = allPageString.indexOf( "class=\"atitle\"" );
        beginIndex = allPageString.indexOf( ">", beginIndex ) + 1;
        int endIndex = allPageString.indexOf( "<", beginIndex );
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
        if ( ( beginIndex = allPageString.indexOf( "class=\"normal f12\"" ) ) > 0 ) {
            beginIndex = allPageString.indexOf( "href=", beginIndex ) + 5;
            endIndex = allPageString.indexOf( ">", beginIndex );

            String moreVolumeURL = baseURL + allPageString.substring( beginIndex, endIndex );

            allPageString = getAllPageString( moreVolumeURL );

            beginIndex = allPageString.indexOf( "class=\"lm720maintxt" );
            endIndex = allPageString.indexOf( "class=\"f18\"", beginIndex );

            // 存放集數頁面資訊的字串
            tempString = allPageString.substring( beginIndex, endIndex );
        }
        else {
            beginIndex = allPageString.indexOf( "class=\"he3\"" );
            endIndex = allPageString.indexOf( "</ul>", beginIndex );

            // 存放集數頁面資訊的字串
            tempString = allPageString.substring( beginIndex, endIndex );
        }

        int volumeCount = tempString.split( "href=" ).length - 1;

        String volumeTitle = "";
        beginIndex = endIndex = 0;
        for ( int i = 0; i < volumeCount; i++ ) {
            // 取得單集位址
            beginIndex = tempString.indexOf( "href=", beginIndex ) + 5;
            endIndex = tempString.indexOf( " ", beginIndex );
            urlList.add( baseURL + tempString.substring( beginIndex, endIndex ) );

            // 取得單集名稱
            beginIndex = tempString.indexOf( ">", beginIndex ) + 1;
            endIndex = tempString.indexOf( "<", beginIndex );
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
