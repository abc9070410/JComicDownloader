/*
 ----------------------------------------------------------------------------------------------------
 Program Name : JComicDownloader
 Authors  : surveyorK
 Last Modified : 2012/6/5
 ----------------------------------------------------------------------------------------------------
 ChangeLog:
 *  4.07: 1. 新增對zuiwanju的支援。
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

public class ParseZuiwanju extends ParseOnlineComicSite {

    private int radixNumber; // use to figure out the name of pic
    private String jsName;
    protected String indexName;
    protected String indexEncodeName;
    protected String baseURL;

    /**

     @author user
     */
    public ParseZuiwanju() {
        siteID = Site.ZUIWANJU;
        siteName = "Zuiwanju";
        indexName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_zuiwanju_parse_", "html" );
        indexEncodeName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_zuiwanju_encode_parse_", "html" );

        jsName = "index_zuiwanju.js";
        radixNumber = 1591371; // default value, not always be useful!!

        baseURL = "http://www.zuiwanju.com";
    }

    public ParseZuiwanju( String webSite, String titleName ) {
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

            int beginIndex = allPageString.indexOf( "href='/comic" );
            beginIndex = allPageString.indexOf( "〉", beginIndex ) + 1;
            int endIndex = allPageString.indexOf( "<", beginIndex );
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

        Common.debugPrint( "開始解析圖片位址 : " );
        String allPageString = Common.getFileString( SetUp.getTempDirectory(), indexEncodeName );

        beginIndex = allPageString.indexOf( "qTcms_S_m_murl_e" );
        beginIndex = allPageString.indexOf( "\"", beginIndex ) + 1;
        endIndex = allPageString.indexOf( "\"", beginIndex );

        String qTcms_S_m_murl_e = allPageString.substring( beginIndex, endIndex );
        String qTcms_S_m_murl = base64_decode( qTcms_S_m_murl_e );

        // 取得後方的圖片位址
        String[] backPicURL = qTcms_S_m_murl.split( "\\$qingtiandy\\$" );

        // 圖片伺服器位址
        String basePicURL = "http://zwj.kkcomic.com/";

        totalPage = backPicURL.length;
        Common.debugPrintln( "共 " + totalPage + " 頁" );
        comicURL = new String[totalPage];

        // 取得真實圖片位址
        for ( int i = 0; i < backPicURL.length; i++ ) {
            comicURL[i] = backPicURL[i].replace( "/UploadFiles/", basePicURL );
            //Common.debugPrintln( (i + 1 ) + " " + comicURL[i] ); // for debug
        }
        //System.exit(  0 );
    }

    private String base64_decode( String data ) {
        String b64 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=";
        int o1, o2, o3;
        int h1, h2, h3, h4;
        int bits;
        int i = 0, ac = 0;
        String dec = "";
        String tmp_arr = "";

        if ( data == null ) {
            return data;
        }
        //data += "''";

        System.out.println( data.length() );
        do {
            /*
             h1 = b64.indexOf( String.valueOf( data.charAt( i++ ) ) );
             h2 = b64.indexOf( String.valueOf( data.charAt( i++ ) ) );
             h3 = b64.indexOf( String.valueOf( data.charAt( i++ ) ) );
             h4 = b64.indexOf( String.valueOf( data.charAt( i++ ) ) );
             */

            h1 = b64.indexOf( data.charAt( i++ ) );
            h2 = b64.indexOf( data.charAt( i++ ) );
            h3 = b64.indexOf( data.charAt( i++ ) );
            h4 = b64.indexOf( data.charAt( i++ ) );


            //System.out.println( h1 + " " + h2 + " " + h3 + " " + h4 );

            bits = h1 << 18 | h2 << 12 | h3 << 6 | h4;
            o1 = bits >> 16 & 0xff;
            o2 = bits >> 8 & 0xff;
            o3 = bits & 0xff;
            if ( h3 == 64 ) {
                tmp_arr += Common.fromCharCode( o1 );
            }
            else if ( h4 == 64 ) {
                tmp_arr += Common.fromCharCode( o1, o2 );
            }
            else {
                tmp_arr += Common.fromCharCode( o1, o2, o3 );
            }
        }
        while ( i < data.length() );

        dec += tmp_arr;

        dec = this.utf8_decode( dec );
        return dec;
    }

    private String utf8_decode( String str_data ) {
        String tmp_arr = "";
        int i = 0, ac = 0, c1 = 0, c2 = 0, c3 = 0;
        //str_data += "''";
        while ( i < str_data.length() ) {
            c1 = ( str_data.charAt( i ) );// + 1;
            if ( c1 < 128 ) {
                tmp_arr += Common.fromCharCode( c1 );
                i++;
            }
            else if ( ( c1 > 191 ) && ( c1 < 224 ) ) {
                c2 = ( str_data.charAt( i + 1 ) );// + 1;
                tmp_arr += Common.fromCharCode( ( ( c1 & 31 ) << 6 ) | ( c2 & 63 ) );
                i += 2;
            }
            else {
                c2 = ( str_data.charAt( i + 1 ) );// + 1;
                c3 = ( str_data.charAt( i + 2 ) );// + 1;
                tmp_arr += Common.fromCharCode( ( ( c1 & 15 ) << 12 ) | ( ( c2 & 63 ) << 6 ) | ( c3 & 63 ) );
                i += 3;
            }
        }
        return tmp_arr;
    }

    @Override
    public String getAllPageString( String urlString ) {
        String indexName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_zuiwanju_", "html" );
        String indexEncodeName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_zuiwanju_encode_", "html" );

        Common.downloadFile( urlString, SetUp.getTempDirectory(), indexName, false, "" );
        Common.newEncodeFile( SetUp.getTempDirectory(), indexName, indexEncodeName, Encoding.GB2312 );

        return Common.getFileString( SetUp.getTempDirectory(), indexEncodeName );
    }

    @Override
    public boolean isSingleVolumePage( String urlString ) {
        /*
         String
         qTcms_S_m_murl_e="L1VwbG9hZEZpbGVzLzE5NjUvY29taWNkYXRhX3pxX3pxaHpoczEwMDExNzE4XzEzMzc5OTY1Njg2NjhfMTMzNzk5NjU2ODY2OC5wbmckcWluZ3RpYW5keSQvVXBsb2FkRmlsZXMvMTk2NS9jb21pY2RhdGFfenFfenFoemhzMTAwMTE3MThfMTMzNzk5NjU2ODY2OF8xMzM3OTk2NTY4ODQyLnBuZyRxaW5ndGlhbmR5JC9VcGxvYWRGaWxlcy8xOTY1L2NvbWljZGF0YV96cV96cWh6aHMxMDAxMTcxOF8xMzM3OTk2NTY4NjY4XzEzMzc5OTY1NjkwMTYucG5nJHFpbmd0aWFuZHkkL1VwbG9hZEZpbGVzLzE5NjUvY29taWNkYXRhX3pxX3pxaHpoczEwMDExNzE4XzEzMzc5OTY1Njg2NjhfMTMzNzk5NjU2OTE5Mi5wbmckcWluZ3RpYW5keSQvVXBsb2FkRmlsZXMvMTk2NS9jb21pY2RhdGFfenFfenFoemhzMTAwMTE3MThfMTMzNzk5NjU2ODY2OF8xMzM3OTk2NTY5MzcwLnBuZyRxaW5ndGlhbmR5JC9VcGxvYWRGaWxlcy8xOTY1L2NvbWljZGF0YV96cV96cWh6aHMxMDAxMTcxOF8xMzM3OTk2NTY4NjY4XzEzMzc5OTY1Njk1NDcucG5nJHFpbmd0aWFuZHkkL1VwbG9hZEZpbGVzLzE5NjUvY29taWNkYXRhX3pxX3pxaHpoczEwMDExNzE4XzEzMzc5OTY1Njg2NjhfMTMzNzk5NjU2OTczNy5wbmckcWluZ3RpYW5keSQvVXBsb2FkRmlsZXMvMTk2NS9jb21pY2RhdGFfenFfenFoemhzMTAwMTE3MThfMTMzNzk5NjU2ODY2OF8xMzM3OTk2NTY5OTE2LnBuZyRxaW5ndGlhbmR5JC9VcGxvYWRGaWxlcy8xOTY1L2NvbWljZGF0YV96cV96cWh6aHMxMDAxMTcxOF8xMzM3OTk2NTY4NjY4XzEzMzc5OTY1NzAwOTEucG5nJHFpbmd0aWFuZHkkL1VwbG9hZEZpbGVzLzE5NjUvY29taWNkYXRhX3pxX3pxaHpoczEwMDExNzE4XzEzMzc5OTY1Njg2NjhfMTMzNzk5NjU3MDI3MC5wbmckcWluZ3RpYW5keSQvVXBsb2FkRmlsZXMvMTk2NS9jb21pY2RhdGFfenFfenFoemhzMTAwMTE3MThfMTMzNzk5NjU2ODY2OF8xMzM3OTk2NTcwNDQ2LnBuZyRxaW5ndGlhbmR5JC9VcGxvYWRGaWxlcy8xOTY1L2NvbWljZGF0YV96cV96cWh6aHMxMDAxMTcxOF8xMzM3OTk2NTY4NjY4XzEzMzc5OTY1NzA2MjEucG5nJHFpbmd0aWFuZHkkL1VwbG9hZEZpbGVzLzE5NjUvY29taWNkYXRhX3pxX3pxaHpoczEwMDExNzE4XzEzMzc5OTY1Njg2NjhfMTMzNzk5NjU3MDc5OC5wbmckcWluZ3RpYW5keSQvVXBsb2FkRmlsZXMvMTk2NS9jb21pY2RhdGFfenFfenFoemhzMTAwMTE3MThfMTMzNzk5NjU2ODY2OF8xMzM3OTk2NTcwOTczLnBuZyRxaW5ndGlhbmR5JC9VcGxvYWRGaWxlcy8xOTY1L2NvbWljZGF0YV96cV96cWh6aHMxMDAxMTcxOF8xMzM3OTk2NTY4NjY4XzEzMzc5OTY1NzExNTIucG5nJHFpbmd0aWFuZHkkL1VwbG9hZEZpbGVzLzE5NjUvY29taWNkYXRhX3pxX3pxaHpoczEwMDExNzE4XzEzMzc5OTY1Njg2NjhfMTMzNzk5NjU3MTMyOS5wbmckcWluZ3RpYW5keSQvVXBsb2FkRmlsZXMvMTk2NS9jb21pY2RhdGFfenFfenFoemhzMTAwMTE3MThfMTMzNzk5NjU2ODY2OF8xMzM3OTk2NTcxNTAxLnBuZyRxaW5ndGlhbmR5JC9VcGxvYWRGaWxlcy8xOTY1L2NvbWljZGF0YV96cV96cWh6aHMxMDAxMTcxOF8xMzM3OTk2NTY4NjY4XzEzMzc5OTY1NzE2NzQucG5nJHFpbmd0aWFuZHkkL1VwbG9hZEZpbGVzLzE5NjUvY29taWNkYXRhX3pxX3pxaHpoczEwMDExNzE4XzEzMzc5OTY1Njg2NjhfMTMzNzk5NjU3MTg0Ny5wbmc=";

         String qTcms_S_m_murl=base64_decode(qTcms_S_m_murl_e);	
         String[] urls = qTcms_S_m_murl.split( "$qingtiandy$" );

         System.out.println( qTcms_S_m_murl );
         */

        // ex. http://www.zuiwanju.com/comic-view-248380.html
        if ( urlString.matches( "(?s).*\\.html(?s).*" ) ) {
            return true;
        }
        else // ex. http://www.zuiwanju.com/comic-1631.html
        {
            return false;
        }
    }

    public String getMainUrlFromSingleVolumeUrl( String volumeURL ) {
        // ex. http://www.zuiwanju.com/comic-view-248380.html轉為
        //    http://www.zuiwanju.com/comic-1631.html

        String allPageString = getAllPageString( volumeURL );

        int beginIndex = allPageString.indexOf( "href='/comic" );
        beginIndex = allPageString.indexOf( "'", beginIndex ) + 1;
        int endIndex = allPageString.indexOf( "'", beginIndex );

        String mainPageURL = baseURL + allPageString.substring( beginIndex, endIndex );

        Common.debugPrintln( "MAIN_URL: " + mainPageURL );

        return mainPageURL;
    }

    @Override
    public String getTitleOnMainPage( String urlString, String allPageString ) {
        int beginIndex = allPageString.indexOf( "<h1>" );
        beginIndex = allPageString.indexOf( ">", beginIndex ) + 1;
        int endIndex = allPageString.indexOf( "</h1>", beginIndex );
        String title = allPageString.substring( beginIndex, endIndex ).trim();

        return Common.getStringRemovedIllegalChar( Common.getTraditionalChinese( title ) );
    }

    @Override
    public List<List<String>> getVolumeTitleAndUrlOnMainPage( String urlString, String allPageString ) {
        // combine volumeList and urlList into combinationList, return it.

        List<List<String>> combinationList = new ArrayList<List<String>>();
        List<String> urlList = new ArrayList<String>();
        List<String> volumeList = new ArrayList<String>();

        int beginIndex = allPageString.indexOf( "class=\"plist pnormal\"" );
        int endIndex = allPageString.indexOf( "class=\"blank_8\"", beginIndex );
        String tempString = allPageString.substring( beginIndex, endIndex );

        int volumeCount = tempString.split( " href=" ).length - 1;

        String volumeTitle = "";
        beginIndex = endIndex = 0;
        for ( int i = 0; i < volumeCount; i++ ) {

            // 取得單集位址
            beginIndex = tempString.indexOf( " href=", beginIndex );
            beginIndex = tempString.indexOf( "\"", beginIndex ) + 1;
            endIndex = tempString.indexOf( "\"", beginIndex );
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
