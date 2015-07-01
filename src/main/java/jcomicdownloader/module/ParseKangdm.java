/*
----------------------------------------------------------------------------------------------------
Program Name : JComicDownloader
Authors  : surveyorK
Last Modified : 2011/12/23
----------------------------------------------------------------------------------------------------
ChangeLog:
 *  2.11: 1. 新增對www.kangdm.com的支援。
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

public class ParseKangdm extends ParseOnlineComicSite {

    private int radixNumber; // use to figure out the name of pic
    private String jsName;
    protected String indexName;
    protected String indexEncodeName;
    protected String baseURL;

    /**
     *
     * @author user
     */
    public ParseKangdm() {
        siteID = Site.KANGDM;
        siteName = "kangdm";
        indexName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_kangdm_parse_", "html" );
        indexEncodeName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_kangdm_encode_parse_", "html" );

        jsName = "index_kangdm.js";
        radixNumber = 1591371; // default value, not always be useful!!

        baseURL = "http://1.kangdm.com/comic_img/";
    }

    public ParseKangdm( String webSite, String titleName ) {
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

            int beginIndex = allPageString.indexOf( "<title>" ) + 7;
            int endIndex = allPageString.indexOf( "</title>", beginIndex );
            String tempTitleString = allPageString.substring( beginIndex, endIndex ).replaceAll( "&nbsp;", "" );

            setWholeTitle( getVolumeWithFormatNumber( Common.getStringRemovedIllegalChar(
                    Common.getTraditionalChinese( tempTitleString.trim() ) ) ) );
        }

        Common.debugPrintln( "作品名稱(title) : " + getTitle() );
        Common.debugPrintln( "章節名稱(wholeTitle) : " + getWholeTitle() );
    }

    @Override
    public void parseComicURL() { // parse URL and save all URLs in comicURL  //
        // 先取得前面的下載伺服器網址

        if ( !webSite.matches( "(?s).*/" ) ) {
            webSite += "/";
        }

        String allPageString = getAllPageString( webSite + "index.js" );
        Common.debugPrint( "開始解析這一集有幾頁 : " );

        String[] tokens = allPageString.split( "=|;" );

        String picMidURL = ""; // 圖片網址中間的部份
        int zeroAmount = 0; // 正規化檔名中補零的數量

        for ( int i = 0 ; i < tokens.length ; i++ ) {
            if ( tokens[i].matches( "(?s).*var\\s*total\\s*" ) ) {
                totalPage = Integer.parseInt( tokens[i + 1].trim() );
            } else if ( tokens[i].matches( "(?s).*var\\s*volpic\\s*" ) ) {
                picMidURL = tokens[i + 1].trim().replaceAll( "'", "" );
            } else if ( tokens[i].matches( "(?s).*var\\s*tpf\\s*" ) ) {
                zeroAmount = Integer.parseInt( tokens[i + 1].trim() );
            }
        }

        Common.debugPrintln( "共 " + totalPage + " 頁" );
        comicURL = new String[totalPage];

        NumberFormat formatter = new DecimalFormat( Common.getZero( zeroAmount + 1 ) );
        String fileName = "";

        int p = 0; // 目前頁數
        for ( int i = 1 ; i <= totalPage && Run.isAlive; i++ ) {
            comicURL[p++] = baseURL + Common.getFixedChineseURL( picMidURL )
                    + formatter.format( i ) + "." + "jpg"; // 存入每一頁的網頁網址
            //Common.debugPrintln( p + " " + comicURL[p - 1] ); // debug

        }
        //System.exit( 0 ); // debug
    }

    @Override
    public String getAllPageString( String urlString ) {
        String indexName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_kangdm_", "html" );
        String indexEncodeName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_kangdm_encode_", "html" );

        Common.downloadFile( urlString, SetUp.getTempDirectory(), indexName, false, "" );
        Common.newEncodeFile( SetUp.getTempDirectory(), indexName, indexEncodeName );

        return Common.getFileString( SetUp.getTempDirectory(), indexEncodeName );
    }

    @Override
    public boolean isSingleVolumePage( String urlString ) {
        // ex. http://www.kangdm.com/comic/10256/siwangbijitongren/
        if ( Common.getAmountOfString( urlString, "/" ) > 5 ) {
            return true;
        } else {
            return false;
        }
    }

    public String getMainUrlFromSingleVolumeUrl( String volumeURL ) {
        // ex. http://www.kangdm.com/comic/10256/siwangbijitongren/轉為
        //    http://www.kangdm.com/comic/10256/

        int endIndex = volumeURL.substring( 0, volumeURL.length() - 1 ).lastIndexOf( "/" ) + 1;
        String mainPageURL = volumeURL.substring( 0, endIndex );

        Common.debugPrintln( "MAIN_URL: " + mainPageURL );

        return mainPageURL;
    }

    @Override
    public String getTitleOnMainPage( String urlString, String allPageString ) {
        int beginIndex = allPageString.indexOf( "id=\"mhtitle\"" );
        beginIndex = allPageString.indexOf( ">", beginIndex ) + 1;
        int endIndex = Common.getSmallerIndexOfTwoKeyword( allPageString, beginIndex, "<", "-" );
        String title = allPageString.substring( beginIndex, endIndex );

        System.out.println( "XXXX:" + title );

        return Common.getStringRemovedIllegalChar( Common.getTraditionalChinese( title ) );
    }

    @Override
    public List<List<String>> getVolumeTitleAndUrlOnMainPage( String urlString, String allPageString ) {
        // combine volumeList and urlList into combinationList, return it.

        List<List<String>> combinationList = new ArrayList<List<String>>();
        List<String> urlList = new ArrayList<String>();
        List<String> volumeList = new ArrayList<String>();

        int beginIndex = allPageString.indexOf( "id=\"djydmhzj\"" );
        int endIndex = allPageString.indexOf( "<!--", beginIndex );

        String tempString = allPageString.substring( beginIndex, endIndex );

        int volumeCount = tempString.split( "href=\"" ).length - 1;

        if ( !urlString.matches( "(?s).*/" ) ) {
            urlString += "/";
        }

        String volumeTitle = "";
        beginIndex = endIndex = 0;
        for ( int i = 0 ; i < volumeCount ; i++ ) {
            // 取得單集位址
            beginIndex = tempString.indexOf( "href=\"", beginIndex ) + 6;
            endIndex = tempString.indexOf( "\"", beginIndex );
            urlList.add( urlString + tempString.substring( beginIndex, endIndex ) );

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
