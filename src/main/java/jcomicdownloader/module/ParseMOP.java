/*
 ----------------------------------------------------------------------------------------------------
 Program Name : JComicDownloader
 Authors  : surveyorK
 Last Modified : 2012/1/10
 ----------------------------------------------------------------------------------------------------
 ChangeLog:
 *  2.17: 1. 新增對dm.game.mop.com的支援。
 ----------------------------------------------------------------------------------------------------
 */
package jcomicdownloader.module;

import jcomicdownloader.tools.*;
import jcomicdownloader.enums.*;
import java.util.*;
import jcomicdownloader.SetUp;
import jcomicdownloader.encode.Encoding;

public class ParseMOP extends ParseOnlineComicSite {

    private int radixNumber; // use to figure out the name of pic
    private String jsName;
    protected String indexName;
    protected String indexEncodeName;
    protected String baseURL;

    /**
     *
     * @author user
     */
    public ParseMOP() {
        siteID = Site.MOP;
        siteName = "MOP";
        indexName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_mop_parse_", "html" );
        indexEncodeName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_mop_encode_parse_", "html" );

        jsName = "index_mop.js";
        radixNumber = 15221471; // default value, not always be useful!!

        baseURL = "http://dm.game.mop.com";
    }

    public ParseMOP( String webSite, String titleName ) {
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

            int beginIndex = allPageString.indexOf( "<title>" ) + 7;
            int endIndex = allPageString.indexOf( "|", beginIndex );
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

        int beginIndex = allPageString.indexOf( "name=\"selectb\"" );
        beginIndex = allPageString.indexOf( ">", beginIndex ) + 1;
        int endIndex = allPageString.indexOf( "</select>", beginIndex );

        String tempString = allPageString.substring( beginIndex, endIndex );

        totalPage = tempString.split( "<option" ).length - 1;
        Common.debugPrintln( "共 " + totalPage + " 頁" );
        comicURL = new String[totalPage];

        String[] comicPageURL = new String[totalPage];
        beginIndex = endIndex = 0;
        for ( int i = 0; i < totalPage && Run.isAlive; i++ ) {
            beginIndex = allPageString.indexOf( "value=", beginIndex );
            beginIndex = allPageString.indexOf( "\"", beginIndex ) + 1;
            endIndex = allPageString.indexOf( "\"", beginIndex );

            comicPageURL[i] = baseURL + allPageString.substring( beginIndex, endIndex );
        }

        String picURL = "";
        for ( int p = 0; p < totalPage; p++ ) {
            if ( !Common.existPicFile( getDownloadDirectory(), p + 1 ) || 
                 !Common.existPicFile( getDownloadDirectory(), p + 2 ) ) {
                allPageString = getAllPageString( comicPageURL[p] );

                beginIndex = allPageString.indexOf( "id=picwin" );
                beginIndex = allPageString.indexOf( "src=", beginIndex );
                beginIndex = allPageString.indexOf( "\"", beginIndex ) + 1;
                endIndex = allPageString.indexOf( "\"", beginIndex );

                comicURL[p] = Common.getFixedChineseURL( 
                    allPageString.substring( beginIndex, endIndex ) );
                //Common.debugPrintln( ( p + 1 ) + " " + comicURL[p] ); // debug

                // 每解析一個網址就下載一張圖
                singlePageDownload( getTitle(), getWholeTitle(), comicURL[p], totalPage, p + 1, 0 );
            }
        }

        //System.exit( 0 ); // debug
    }
    
    @Override
    public String getAllPageString( String urlString ) {
        String indexName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_mop_parse", "html" );
        String indexEncodeName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_mop_encode_parse", "html" );

        Common.downloadFile( urlString, SetUp.getTempDirectory(), indexName, false, "" );
        Common.newEncodeFile( SetUp.getTempDirectory(), indexName, indexEncodeName, Encoding.GBK );

        return Common.getFileString( SetUp.getTempDirectory(), indexEncodeName );
    }

    @Override
    public boolean isSingleVolumePage( String urlString ) {
        // ex. http://dm.game.mop.com/primary/45489.html#pic

        if ( urlString.matches( ".*/primary/.*" ) ) {
            return true;
        }
        else {
            return false;
        }
    }

    public String getMainUrlFromSingleVolumeUrl( String volumeURL ) {
        // ex. http://dm.game.mop.com/primary/45489.html#pic或
        //     http://dm.game.mop.com/primary/45489/1.html#pic轉為
        //     http://dm.game.mop.com/fengmian/4827.html


        int beginIndex = volumeURL.indexOf( "primary" );
        beginIndex = volumeURL.indexOf( "/", beginIndex ) + 1;
        int endIndex = volumeURL.indexOf( "/", beginIndex );
        if ( endIndex < 0 ) {
            endIndex = volumeURL.indexOf( "/", beginIndex );
        }

        String mainPageURL = volumeURL.substring( 0, endIndex ) + ".html";

        Common.debugPrintln( "MAIN_URL: " + mainPageURL );

        return mainPageURL;
    }

    @Override
    public String getTitleOnMainPage( String urlString, String allPageString ) {
        int beginIndex, endIndex;
        
        beginIndex = urlString.indexOf( "mop" );
        beginIndex = urlString.indexOf( "/", beginIndex ) + 1;
        
        String backMainURL = "";
        if ( urlString.matches( ".*\\?id=.*" ) ) {
            String tempString = urlString.substring( beginIndex, urlString.length() );
            tempString = tempString.replaceAll( "\\.jsp\\?id=", "/" );
            
            backMainURL = tempString + ".html";
        }
        else
            backMainURL = urlString.substring( beginIndex, urlString.length() );

        beginIndex = allPageString.indexOf( backMainURL + "\"" );
        beginIndex = allPageString.indexOf( ">", beginIndex ) + 1;
        endIndex = allPageString.indexOf( "<", beginIndex );
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

        beginIndex = allPageString.indexOf( "class=\"plie\"" );
        endIndex = allPageString.indexOf( "</ul>", beginIndex );

        // 存放集數頁面資訊的字串
        tempString = allPageString.substring( beginIndex, endIndex );

        int volumeCount = tempString.split( "href=" ).length - 1;

        String volumeTitle = "";
        beginIndex = endIndex = 0;
        for ( int i = 0; i < volumeCount; i++ ) {
            // 取得單集位址
            beginIndex = tempString.indexOf( "href=", beginIndex );
            beginIndex = tempString.indexOf( "\"", beginIndex ) + 1;
            endIndex = tempString.indexOf( "\"", beginIndex );
            urlList.add( baseURL + tempString.substring( beginIndex, endIndex ) );

            // 取得單集名稱
            beginIndex = tempString.indexOf( ">", beginIndex ) + 1;
            endIndex = tempString.indexOf( "</a>", beginIndex );
            volumeTitle = tempString.substring( beginIndex, endIndex );
            volumeTitle = volumeTitle.replaceFirst( "<br\\s+/{0,1}>", "" );

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
