/*
----------------------------------------------------------------------------------------------------
Program Name : JComicDownloader
Authors  : surveyorK
Last Modified : 2011/12/18
----------------------------------------------------------------------------------------------------
ChangeLog:
 *  2.09: 1. 新增新增對www.kkkmh.com/的支援。
----------------------------------------------------------------------------------------------------
 */
package jcomicdownloader.module;

import jcomicdownloader.tools.*;
import jcomicdownloader.enums.*;
import java.util.*;
import jcomicdownloader.SetUp;
import jcomicdownloader.module.ParseOnlineComicSite;

public class ParseKKKMH extends ParseOnlineComicSite {

    private int radixNumber; // use to figure out the name of pic
    private String jsName;
    protected String indexName;
    protected String indexEncodeName;
    protected String baseURL;
    protected String serverURL1;
    protected String serverURL2;
    protected String serverURL3;
    protected String serverURL4;

    /**
     *
     * @author user
     */
    public ParseKKKMH() {
        siteID = Site.KKKMH;
        siteName = "kkkmh";
        indexName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_kkkmh_parse_", "html" );
        indexEncodeName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_kkkmh_encode_parse_", "html" );

        jsName = "index_kkkmh.js";
        radixNumber = 159371; // default value, not always be useful!!

        baseURL = "http://www.kkkmh.com";
        
        serverURL1 = "http://mhauto.kkkmh.com"; // 智能
        serverURL2 = "http://mht1.kkkmh.com"; // 電信1
        serverURL3 = "http://mht2.kkkmh.com"; // 電信2
        serverURL4 = "http://mhc1.kkkmh.com"; // 網通
    }

    public ParseKKKMH( String webSite, String titleName ) {
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

            int beginIndex = allPageString.indexOf( "chapter_name" );
            beginIndex = allPageString.indexOf( "'", beginIndex ) + 1;
            int endIndex = allPageString.indexOf( "'", beginIndex );
            String tempTitleString = allPageString.substring( beginIndex, endIndex );

            setWholeTitle( Common.getStringRemovedIllegalChar(
                    Common.getTraditionalChinese( tempTitleString.trim() ) ) );
        }

        Common.debugPrintln( "作品名稱(title) : " + getTitle() );
        Common.debugPrintln( "章節名稱(wholeTitle) : " + getWholeTitle() );
    }

    @Override
    public void parseComicURL() { // parse URL and save all URLs in comicURL  //
        // 先取得前面的下載伺服器網址

        String allPageString = Common.getFileString( SetUp.getTempDirectory(), indexName );
        Common.debugPrint( "開始解析這一集有幾頁 : " );

        // 取得所有位址編碼
        int beginIndex = allPageString.indexOf( "Array()" );
        int endIndex = allPageString.indexOf( ";recentVisitUpdate", beginIndex );

        String allCodeString = allPageString.substring( beginIndex, endIndex );

        totalPage = allCodeString.split( ";" ).length - 1;
        Common.debugPrintln( "共 " + totalPage + " 頁" );
        comicURL = new String[totalPage];

        String[] codeTokens = allCodeString.split( "'" );
        
        int p = 0; // 目前頁數
        for ( int i = 0 ; i < codeTokens.length && Run.isAlive; i++ ) {
            //System.out.println( "CODE: " + codeTokens[i] );
            if ( !codeTokens[i].matches( "(?s).*\\[\\d+\\](?s).*" ) ) {
                //Common.debugPrintln( "CODE: " + codeTokens[i] ); // debug
                comicURL[p++] = serverURL1 + getDecodeURL( codeTokens[i] ); // 存入每一頁的網頁網址
                //Common.debugPrintln( p + " " + comicURL[p-1]  ); // debug
            }
             
        }
        //System.exit( 0 ); // debug
    }

    private String getDecodeURL( String code ) {
        StringBuilder decodeBuilder = new StringBuilder();
        int charCode = 0;
        for ( int i = 0 ; i < code.length() ; i += 2 ) {
            charCode = Integer.parseInt( code.substring( i, i + 2 ), 16 );
            //System.out.print( i + " " + (i + 1) + " : " + charCode + " #\t" );
            decodeBuilder.append( Character.toChars( charCode ) );
        }
        
        return decodeBuilder.toString();
    }

    @Override
    public String getAllPageString( String urlString ) {
        String indexName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_kkkmh_", "html" );

        Common.downloadFile( urlString, SetUp.getTempDirectory(), indexName, false, "" );

        return Common.getFileString( SetUp.getTempDirectory(), indexName );
    }

    @Override
    public boolean isSingleVolumePage( String urlString ) {
        // ex. http://www.kkkmh.com/manhua/0804/9119/65867.html
        if ( Common.getAmountOfString( urlString, "/" ) > 5 ) {
            return true;
        } else {
            return false;
        }
    }

    public String getMainUrlFromSingleVolumeUrl( String volumeURL ) {
        // ex. http://www.kkkmh.com/manhua/0804/ji-qiao-tong-zi.html轉為
        //    http://www.kkkmh.com/manhua/0804/ji-qiao-tong-zi.html

        String allPageString = getAllPageString( volumeURL );

        int beginIndex = allPageString.indexOf( "comic_url" );
        beginIndex = allPageString.indexOf( "'", beginIndex ) + 1;
        int endIndex = allPageString.indexOf( "'", beginIndex );
        String mainPageURL = baseURL + allPageString.substring( beginIndex, endIndex );

        Common.debugPrintln( "MAIN_URL: " + mainPageURL );

        return mainPageURL;
    }

    @Override
    public String getTitleOnMainPage( String urlString, String allPageString ) {
        int beginIndex = allPageString.indexOf( "<h1>" ) + 4;
        int endIndex = allPageString.indexOf( "</h1>", beginIndex );
        String title = allPageString.substring( beginIndex, endIndex );

        return Common.getStringRemovedIllegalChar( Common.getTraditionalChinese( title ) );
    }

    @Override
    public List<List<String>> getVolumeTitleAndUrlOnMainPage( String urlString, String allPageString ) {
        // combine volumeList and urlList into combinationList, return it.

        List<List<String>> combinationList = new ArrayList<List<String>>();
        List<String> urlList = new ArrayList<String>();
        List<String> volumeList = new ArrayList<String>();

        int beginIndex = allPageString.indexOf( "class=\"fav\"" );
        int endIndex = allPageString.indexOf( "class=\"box\"", beginIndex );

        String tempString = allPageString.substring( beginIndex, endIndex );

        int volumeCount = tempString.split( "href=\"" ).length - 1;

        String volumeTitle = "";
        beginIndex = endIndex = 0;
        for ( int i = 0 ; i < volumeCount ; i++ ) {
            // 取得單集位址
            beginIndex = tempString.indexOf( "href=\"", beginIndex ) + 6;
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
