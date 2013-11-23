/*
 ----------------------------------------------------------------------------------------------------
 Program Name : JComicDownloader
 Authors  : surveyorK
 Last Modified : 2012/12/30
 ----------------------------------------------------------------------------------------------------
 ChangeLog:
 5.13: 修復hhcomic無法下載的問題。
 5.02: 1. 修復hhcomic因網址改變而下載錯誤的問題。
 *  4.16: 1. 修復hhcomic因變更伺服器而下載錯誤的問題。
 3.18: 1. 修復hhcomic因網站改版而下載錯誤的問題。
 *  3.06: 1. 新增對hhcomic.com的支援。
 *  4.15: 1. 修復hhcomic因網站改版而無法下載的問題。
 ----------------------------------------------------------------------------------------------------
 */
package jcomicdownloader.module;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import jcomicdownloader.tools.*;
import jcomicdownloader.enums.*;
import java.util.*;
import jcomicdownloader.SetUp;
import jcomicdownloader.encode.Zhcode;

public class ParseHH extends ParseOnlineComicSite {

    private int radixNumber; // use to figure out the name of pic
    private String jsName;
    protected String indexName;
    protected String indexEncodeName;
    protected String baseURL;

    /**

     @author user
     */
    public ParseHH() {
        siteID = Site.HH;
        siteName = "HH";
        indexName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_hh_parse_", "html" );
        indexEncodeName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_hh_encode_parse_", "html" );

        jsName = "index_hh.js";
        radixNumber = 15913371; // default value, not always be useful!!

        baseURL = "http://www.hhcomic.com";
    }

    public ParseHH( String webSite, String titleName ) {
        this();
        this.webSite = webSite;
        this.title = titleName;
    }

    @Override
    public void setParameters() {
        Common.debugPrintln( "開始解析各參數 :" );
        Common.debugPrintln( "開始解析title和wholeTitle :" );

        if ( getWholeTitle() == null || getWholeTitle().equals( "" ) ) {
            // 因為正常解析不需要用到單集頁面，所以給此兩行放進來
            Common.downloadFile( webSite, SetUp.getTempDirectory(), indexName, false, "" );
            Common.newEncodeFile( SetUp.getTempDirectory(), indexName, indexEncodeName );

            String allPageString = Common.getFileString( SetUp.getTempDirectory(), indexEncodeName );

            int beginIndex = allPageString.indexOf( "content=" );
            beginIndex = allPageString.indexOf( "\"", beginIndex ) + 1;
            int endIndex = allPageString.indexOf( "\"", beginIndex );
            String tempTitleString = allPageString.substring( beginIndex, endIndex ).replaceAll( "&nbsp;", "" );

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
        String jsURL = baseURL + "/hh/hhh.js";
        String allPageString = getAllPageString( jsURL );

        int amountOfServer = allPageString.split( "]=\"" ).length - 1;
        String[] serverStrings = new String[amountOfServer];
        int serverCount = 0;
        for ( int i = 0; i < amountOfServer; i++ ) {
            beginIndex = allPageString.indexOf( "\"", beginIndex ) + 1;
            endIndex = allPageString.indexOf( "\"", beginIndex );
            serverStrings[i] = allPageString.substring( beginIndex, endIndex );
            //Common.debugPrintln( "server " + i + ": " + serverStrings[i] );
            beginIndex = endIndex + 1;
        }

        // 取得此集數的伺服器編號
        int serverNo = Integer.parseInt( webSite.split( "=" )[1].replace( "/", "" ) );

        // 取得此集數的伺服器位址
        String serverURL = serverStrings[serverNo - 1];


        Common.debugPrint( "開始解析這一集有幾頁 : " );
        allPageString = getAllPageString( webSite );

        beginIndex = allPageString.indexOf( "PicListUrl" );
        beginIndex = allPageString.indexOf( "\"", beginIndex ) + 1;
        endIndex = allPageString.indexOf( "\"", beginIndex );

        String tempString = allPageString.substring( beginIndex, endIndex );
        String[] urlStrings = tempString.split( "\\|" );
        totalPage = urlStrings.length;

        Common.debugPrintln( "共 " + totalPage + " 頁" );
        comicURL = new String[totalPage];
        refers = new String[totalPage];


        int p = 0; // 目前頁數
        for ( int i = 1; i <= totalPage && Run.isAlive; i++ ) {
            comicURL[p] = serverURL + urlStrings[i - 1]; // 存入每一頁的網頁網址
            
            refers[p] = webSite;

            p++;
            //Common.debugPrintln( p + " " + comicURL[p - 1] ); // debug
            /*
             // 檢查下一張圖是否存在同個資料夾，若存在就跳下一張
             if ( !Common.existPicFile( getDownloadDirectory(), p ) ||
             !Common.existPicFile( getDownloadDirectory(), p + 1 ) ) {
             // 每解析一個網址就下載一張圖
             singlePageDownloadUsingRefer( getTitle(), getWholeTitle(),
             comicURL[p - 1], totalPage, p, 0, webSite );
             }
             */
        }
        //System.exit( 0 ); // debug
    }

    @Override
    public String getAllPageString( String urlString ) {
        String indexName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_hh_", "html" );
        String indexEncodeName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_hh_encode_", "html" );

        Common.downloadFile( urlString, SetUp.getTempDirectory(), indexName, false, "" );
        Common.newEncodeFile( SetUp.getTempDirectory(), indexName, indexEncodeName );

        return Common.getFileString( SetUp.getTempDirectory(), indexEncodeName );
    }

    @Override
    public boolean isSingleVolumePage( String urlString ) {
        // ex. http://hhcomic.com/page/188346/hh92137.htm?s=10
        if ( urlString.matches( ".*\\?s=.*" ) ) {
            return true;
        }
        else {
            return false;
        }
    }

    public String getMainUrlFromSingleVolumeUrl( String volumeURL ) {
        // ex. http://hhcomic.com/page/188346/hh92137.htm?s=10轉為
        //    http://hhcomic.com/comic/188346/

        int endIndex = volumeURL.lastIndexOf( "s=" );
        endIndex = volumeURL.lastIndexOf( "/", endIndex ) + 1;
        String mainPageURL = volumeURL.substring( 0, endIndex ).replace( "/page/", "/comic/" );

        Common.debugPrintln( "MAIN_URL: " + mainPageURL );

        return mainPageURL;
    }

    @Override
    public String getTitleOnMainPage( String urlString, String allPageString ) {
        int beginIndex = allPageString.indexOf( "img src=\"http://" );
        beginIndex = allPageString.indexOf( "alt=\"", beginIndex ) + 1;
        beginIndex = allPageString.indexOf( "\"", beginIndex ) + 1;
        int endIndex = allPageString.indexOf( "\"", beginIndex );
        String title = allPageString.substring( beginIndex, endIndex ).trim();

        return Common.getStringRemovedIllegalChar( Common.getTraditionalChinese( title ) );
    }

    @Override
    public List<List<String>> getVolumeTitleAndUrlOnMainPage( String urlString, String allPageString ) {
        // combine volumeList and urlList into combinationList, return it.

        List<List<String>> combinationList = new ArrayList<List<String>>();
        List<String> urlList = new ArrayList<String>();
        List<String> volumeList = new ArrayList<String>();

        int beginIndex = allPageString.indexOf( "<ul " );
        int endIndex = allPageString.indexOf( "</ul>", beginIndex );

        String tempString = allPageString.substring( beginIndex, endIndex );

        int volumeCount = tempString.split( "<li><a href" ).length - 1;

        String volumeTitle = "";
        beginIndex = endIndex = 0;
        for ( int i = 0; i < volumeCount; i++ ) {

            // 取得單集位址
            beginIndex = tempString.indexOf( "<li><a href", beginIndex );
            beginIndex = tempString.indexOf( "=", beginIndex ) + 1;
            endIndex = tempString.indexOf( " ", beginIndex );
            String tempURL = tempString.substring( beginIndex, endIndex );

            if ( !tempURL.matches( "http://.*" ) ) {
                tempURL = baseURL + tempURL;
            }
            urlList.add( tempURL );

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
