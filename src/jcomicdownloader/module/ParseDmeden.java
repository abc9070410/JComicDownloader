/*
----------------------------------------------------------------------------------------------------
Program Name : JComicDownloader
Authors  : surveyorK
Last Modified : 2012/11/24
----------------------------------------------------------------------------------------------------
ChangeLog:
 5.09: 修復dmeden.net解析位址錯誤的問題。
 5.08: 修復dmeden.net解析位址錯誤的問題。
3.17: 1. 修復dmeden標題名稱解析錯誤的問題。
3.09: 1. 修復對dmeden.net的支援。
3.04: 1. 修復dmeden標題名稱解析不全的bug。
2.03: 1. 對於dmeden轉移位址後做解析修正（dmeden.net <-> www.dmeden.com）
1.12: 1. 改成一邊解析網址一邊下載。
1.11: 1. 新增對dmeden.net的支援。
----------------------------------------------------------------------------------------------------
 */
package jcomicdownloader.module;

import jcomicdownloader.tools.*;
import jcomicdownloader.enums.*;
import java.util.*;
import jcomicdownloader.SetUp;

public class ParseDmeden extends ParseOnlineComicSite {

    private int radixNumber; // use to figure out the name of pic
    private String jsName;
    protected String indexName;
    protected String indexEncodeName;
    protected String baseURL1, baseURL2;

    /**
     *
     * @author user
     */
    public ParseDmeden() {
        siteID = Site.DMEDEN;
        siteName = "Dmeden";
        indexName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_dmeden_parse_", "html" );
        indexEncodeName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_dmeden_encode_parse_", "html" );

        baseURL1 = "http://www.dmeden.net";
        baseURL2 = "http://dmeden.net";
        jsName = "index_dmeden.js";
        radixNumber = 185271; // default value, not always be useful!!
    }

    public ParseDmeden( String webSite, String titleName ) {
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
            String tempTitleString = allPageString.substring( beginIndex, endIndex );

            setWholeTitle( getVolumeWithFormatNumber( Common.getStringRemovedIllegalChar(
                    Common.getTraditionalChinese( tempTitleString.split( "\\d*-\\d*" )[0].trim() ) ) ) );
        }

        Common.debugPrintln( "作品名稱(title) : " + getTitle() );
        Common.debugPrintln( "章節名稱(wholeTitle) : " + getWholeTitle() );
    }

    @Override
    public void parseComicURL() { // parse URL and save all URLs in comicURL  //
        // 先取得前面的下載伺服器網址

        String allPageString = Common.getFileString( SetUp.getTempDirectory(), indexName );
        Common.debugPrint( "開始解析這一集有幾頁 : " );

        int beginIndex = allPageString.indexOf( "\"hdPageCount\"" );
        int endIndex = allPageString.indexOf( "/>", beginIndex );
        String tempString = allPageString.substring( beginIndex, endIndex );
        String[] pageTokens = tempString.split( "\"" );
        totalPage = Integer.parseInt( pageTokens[pageTokens.length - 2] );
        Common.debugPrintln( "共 " + totalPage + " 頁" );
        comicURL = new String[totalPage];

        // http://dmeden.net/comichtml/84461/1.html?s=10
        String[] parameterTokens = webSite.split( "\\?|=|&" );
        String id = "";
        String s = "";

        beginIndex = webSite.indexOf( ".html" );
        endIndex = webSite.substring( 0, beginIndex ).lastIndexOf( "/" ) + 1;
        String fontURL = webSite.substring( 0, endIndex );
        String backURL = webSite.substring( beginIndex, webSite.length() );

        for ( int p = 1 ; p <= totalPage && Run.isAlive; p++ ) {
            // 檢查下一張圖是否存在同個資料夾，若存在就跳下一張
            if ( !Common.existPicFile( getDownloadDirectory(), p ) ||
                 !Common.existPicFile( getDownloadDirectory(), p + 1 ) ) {
                String url = fontURL + p + backURL;
                Common.downloadFile( url, SetUp.getTempDirectory(), indexName, false, "" );
                allPageString = Common.getFileString( SetUp.getTempDirectory(), indexName );

                beginIndex = allPageString.indexOf( "<img id=\"" );
                endIndex = allPageString.indexOf( "onload=", beginIndex );
                String tempUrlString = allPageString.substring( beginIndex, endIndex );

                String[] urlTokens = tempUrlString.split( "\"" );

                for ( int i = 0 ; i < urlTokens.length && Run.isAlive; i++ ) {
                    if ( urlTokens[i].matches( "\\s*src=\\s*" ) ) {
                        comicURL[p - 1] = Common.getFixedChineseURL( urlTokens[i + 1] );
                        // 每解析一個網址就下載一張圖
                        singlePageDownload( getTitle(), getWholeTitle(), comicURL[p - 1], totalPage, p, 0 );

                        break;
                    }
                }
            }
            //Common.debugPrintln( p + " " + comicURL[p-1] ); // debug
        }
        //System.exit(0); // debug
    }

    @Override
    public String getAllPageString( String urlString ) {
        String indexName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_dmeden_", "html" );

        Common.downloadFile( urlString, SetUp.getTempDirectory(), indexName, false, "" );

        return Common.getFileString( SetUp.getTempDirectory(), indexName );
    }

    @Override
    public boolean isSingleVolumePage( String urlString ) {
        if ( !urlString.matches( "(?s).*/comicinfo/(?s).*" ) ) // ex. http://dmeden.net/comichtml/84462/1.html?s=10
        {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String getTitleOnSingleVolumePage( String urlString ) {
        String allPageString = getAllPageString( urlString );

        int beginIndex = 0;
        int endIndex = 0;
        beginIndex = allPageString.indexOf( "href=\"/comicinfo/" ) + 6;
        endIndex = allPageString.indexOf( "\">", beginIndex );
        
        
        // 1:繁體　2:簡體
        String baseURL = urlString.matches( baseURL1 + "(?s).*" ) ? baseURL1 : baseURL2;
        String mainUrlString = baseURL + allPageString.substring( beginIndex, endIndex );
        
        System.out.println( allPageString.substring( beginIndex, endIndex ) );

        return getTitleOnMainPage( mainUrlString, getAllPageString( mainUrlString ) );
    }

    @Override
    public String getTitleOnMainPage( String urlString, String allPageString ) {

        int beginIndex = allPageString.indexOf( "<h1>" ) + 4;
        int endIndex = Common.getSmallerIndexOfTwoKeyword( 
            allPageString, beginIndex, "</h1>", "<div" );
        String tempTitleString = allPageString.substring( beginIndex, endIndex );

        //System.out.println( tempTitleString );

        String title = tempTitleString.trim();

        return Common.getStringRemovedIllegalChar( Common.getTraditionalChinese( title ) );
    }

    @Override
    public List<List<String>> getVolumeTitleAndUrlOnMainPage( String urlString, String allPageString ) {
        // combine volumeList and urlList into combinationList, return it.

        List<List<String>> combinationList = new ArrayList<List<String>>();
        List<String> urlList = new ArrayList<String>();
        List<String> volumeList = new ArrayList<String>();

        int beginIndex = 0;
        int endIndex = 0;
        
        if ( urlString.matches( ".*dmeden.net/.*" ) ) {
            beginIndex = allPageString.indexOf( "class=\"cVolList\"" );
            endIndex = allPageString.indexOf( "class=\"cCRHtm\"", beginIndex );
        }
        else {
            beginIndex = allPageString.indexOf( "class=\"list_s\"" );
            endIndex = allPageString.indexOf( "</ul>", beginIndex );
        }
        
        String tempString = allPageString.substring( beginIndex, endIndex );
        String[] tokens = tempString.split( "'" );

        int volumeCount = 0;
        
        // 1:繁體　2:簡體
        String baseURL = urlString.matches( baseURL1 + "(?s).*" ) ? baseURL1 : baseURL2;
        String baseSingleURL = baseURL + "/comichtml/";

        for ( int i = 0 ; i < tokens.length ; i++ ) {
            if ( tokens[i].matches( "(?s).*/comic/checkview(?s).*" ) ) {

                String tempUrl = baseURL + tokens[i];

                String[] parameterTokens = tempUrl.split( "\\?|=|&" );
                String id = "";
                String s = "";

                for ( int j = 0 ; j < parameterTokens.length ; j++ ) {
                    if ( parameterTokens[j].equals( "ID" ) ) {
                        id = parameterTokens[j + 1];
                    } else if ( parameterTokens[j].equals( "s" ) ) {
                        s = parameterTokens[j + 1];
                    }
                }

                String url = baseSingleURL + id + "/1.html?s=" + s;
                System.out.println( url );
                urlList.add( url );

                //System.out.println( urlString + idString );

                // 取得單集名稱
                String volumeTitle = tokens[i + 4];
                volumeList.add( getVolumeWithFormatNumber( Common.getStringRemovedIllegalChar(
                        Common.getTraditionalChinese( volumeTitle.trim() ) ) ) );

                volumeCount++;
            }
        }

        totalVolume = volumeCount;
        Common.debugPrintln( "共有" + totalVolume + "集" );

        combinationList.add( volumeList );
        combinationList.add( urlList );

        return combinationList;
    }

    @Override
    public String getMainUrlFromSingleVolumeUrl( String volumeURL ) {
        throw new UnsupportedOperationException( "Not supported yet." );
    }
}
