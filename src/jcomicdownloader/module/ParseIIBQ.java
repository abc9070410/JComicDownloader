/*
----------------------------------------------------------------------------------------------------
Program Name : JComicDownloader
Authors  : surveyorK
Last Modified : 2012/3/25
----------------------------------------------------------------------------------------------------
ChangeLog:
 * 4.09: 1. 修復對www.iibq.com的支援。
    3.11 :1. 修復對www.iibq.com的支援。
 *  2.03 :1. 新增新增對www.iibq.com的支援。
----------------------------------------------------------------------------------------------------
 */
package jcomicdownloader.module;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import jcomicdownloader.tools.*;
import jcomicdownloader.enums.*;
import java.util.*;
import jcomicdownloader.SetUp;

public class ParseIIBQ extends ParseOnlineComicSite {

    private int radixNumber; // use to figure out the name of pic
    private String jsName;
    protected String indexName;
    protected String indexEncodeName;
    protected String baseURL;
    
    protected String firstServerURL = "http://comic.jmydm.net/"; // 電信一
    protected String secondServerURL = "http://zj.jmydm.net/"; // 電信二
    protected String thirdServerURL = "http://lt.jmydm.net:2012/"; // 網通

    /**
     *
     * @author user
     */
    public ParseIIBQ() {
        siteID = Site.IIBQ;
        siteName = "IIBQ";
        indexName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_iibq_parse_", "html" );
        indexEncodeName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_iibq_encode_parse_", "html" );

        jsName = "index_iibq.js";
        radixNumber = 1859271; // default value, not always be useful!!

        baseURL = "http://www.iibq.com";
    }

    public ParseIIBQ( String webSite, String titleName ) {
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
            int endIndex = allPageString.indexOf( "</title>", beginIndex );
            String tempTitleString = allPageString.substring( beginIndex, endIndex ).trim().split( "\\s" )[0];

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

        int beginIndex = allPageString.indexOf( "<script>" );
        beginIndex = allPageString.indexOf( "\"", beginIndex ) + 1;
        int endIndex = allPageString.indexOf( "\"", beginIndex );
        String tempString = allPageString.substring( beginIndex, endIndex ).trim();
        
        String[] urlTokens = tempString.split( "\\|" );

        totalPage = urlTokens.length; 
        Common.debugPrintln( "共 " + totalPage + " 頁" );
        comicURL = new String[totalPage];

        // 開始取得中間網址 
        beginIndex = allPageString.indexOf( "\"", endIndex + 1 ) + 1;
        endIndex = allPageString.indexOf( "\"", beginIndex );
        String middleURL = allPageString.substring( beginIndex, endIndex );
        
        // 開始取得伺服器位址
        String jsURL = "http://www.iibq.com/script/ds.js";
        allPageString = getAllPageString( jsURL );
        beginIndex = allPageString.lastIndexOf( "|" ) + 1;
        endIndex = Common.getSmallerIndexOfTwoKeyword( allPageString, beginIndex, "\"", "^" );
        String basePicURL = allPageString.substring( beginIndex, endIndex );

        for ( int p = 1 ; p <= totalPage && Run.isAlive; p++ ) {
            comicURL[p - 1] = basePicURL + middleURL + urlTokens[p-1];
            //System.out.println( comicURL[p - 1] ); // debug
        }
        //System.exit(0); // debug
    }

    @Override
    public String getAllPageString( String urlString ) {
        String indexName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_iibq_", "html" );
        String indexEncodeName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_iibq_encode_", "html" );

        Common.downloadFile( urlString, SetUp.getTempDirectory(), indexName, false, "" );
        //Common.newEncodeFile( SetUp.getTempDirectory(), indexName, indexEncodeName );

        return Common.getFileString( SetUp.getTempDirectory(), indexName );
    }

    @Override
    public boolean isSingleVolumePage( String urlString ) {
        // ex. http://www.iibq.com/comic/82012138003/viewcomic199629/
        Common.debugPrintln( "判斷:" + urlString.split( "/" ).length );
        if ( urlString.split( "/" ).length > 5 ) 
        {
            return true;
        } else {
            return false;
        }
    }

    public String getMainUrlFromSingleVolumeUrl( String volumeURL ) {
        // ex. http://www.iibq.com/comic/82012138003/viewcomic199629/轉為
        //     http://www.iibq.com/comic/82012138003/

        int endIndex = volumeURL.lastIndexOf( "/", volumeURL.length() - 3 ) + 1;
        String mainPageURL = volumeURL.substring( 0, endIndex );;

        return mainPageURL;
    }

    @Override
    public String getTitleOnMainPage( String urlString, String allPageString ) {
        int beginIndex = allPageString.indexOf( "class=\"cTitle\"" );
        beginIndex = allPageString.indexOf( ">", beginIndex ) + 1;
        int endIndex = allPageString.indexOf( "</h1>", beginIndex );
        String title = allPageString.substring( beginIndex, endIndex ).trim().split( "\\s" )[0];

        return Common.getStringRemovedIllegalChar( Common.getTraditionalChinese( title ) );
    }

    @Override
    public List<List<String>> getVolumeTitleAndUrlOnMainPage( String urlString, String allPageString ) {
        // combine volumeList and urlList into combinationList, return it.

        List<List<String>> combinationList = new ArrayList<List<String>>();
        List<String> urlList = new ArrayList<String>();
        List<String> volumeList = new ArrayList<String>();

        int beginIndex = allPageString.indexOf( "class=\"cVol\"" );
        int endIndex = allPageString.indexOf( "class=\"cCRHtm\"", beginIndex );
        String tempString = allPageString.substring( beginIndex, endIndex );
        
        endIndex = beginIndex = 0;
        int volumeCount = tempString.split( "href='" ).length - 1;
        String tempURL = "";
        String tempTitle = "";
        for ( int i = 0 ; i < volumeCount ; i++ ) {
            // 取得單集位址
            beginIndex = tempString.indexOf( "href='", beginIndex ) + 6;
            endIndex = tempString.indexOf( "'", beginIndex );
            tempURL = tempString.substring( beginIndex, endIndex );
            urlList.add( tempURL );
            
            // 取得單集名稱
            beginIndex = tempString.indexOf( ">", beginIndex ) + 1;
            endIndex = tempString.indexOf( "<", beginIndex );
            tempTitle = tempString.substring( beginIndex, endIndex );            
            volumeList.add( getVolumeWithFormatNumber( 
                Common.getStringRemovedIllegalChar(
                Common.getTraditionalChinese( tempTitle.trim() ) ) ) );

        }
        
        totalVolume = volumeCount;
        Common.debugPrintln( "共有" + totalVolume + "集" );

        combinationList.add( volumeList );
        combinationList.add( urlList );

        return combinationList;
    }

}
