/*
----------------------------------------------------------------------------------------------------
Program Name : JComicDownloader
Authors  : surveyorK
Last Modified : 2013/4/21
----------------------------------------------------------------------------------------------------
ChangeLog:
* 5.16: 修復citymanga後面幾頁無法解析的問題。
 *  2.02: 1. 新增對www.citymanga.com的支援。
----------------------------------------------------------------------------------------------------
 */
package jcomicdownloader.module;

import jcomicdownloader.tools.*;
import jcomicdownloader.enums.*;
import java.util.*;
import jcomicdownloader.SetUp;

public class ParseCityManga extends ParseOnlineComicSite {

    private int radixNumber; // use to figure out the name of pic
    private String jsName;
    protected String indexName;
    protected String indexEncodeName;
    protected String baseURL;

    /**
     *
     * @author user
     */
    public ParseCityManga() {
        siteID = Site.CITY_MANGA;
        siteName = "CityManga";
        indexName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_city_manga_parse_", "html" );
        indexEncodeName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_city_manga_encode_parse_", "html" );

        jsName = "index_city_manga.js";
        radixNumber = 1564371; // default value, not always be useful!!

        baseURL = "http://www.citymanga.com";
    }

    public ParseCityManga( String webSite, String titleName ) {
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

            int beginIndex = allPageString.lastIndexOf( "<h1>" ) + 4;
            int endIndex = allPageString.indexOf( "</h1>", beginIndex );
            String tempTitleString = allPageString.substring( beginIndex, endIndex );

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

        int beginIndex = allPageString.lastIndexOf( "pageselector" );
        int endIndex = allPageString.indexOf( "</select>", beginIndex );
        String tempString = allPageString.substring( beginIndex, endIndex );

        totalPage = tempString.split( "</option>" ).length - 1;
        Common.debugPrintln( "共 " + totalPage + " 頁" );
        comicURL = new String[totalPage];

        // 開始取得每一頁的網址
        beginIndex = allPageString.lastIndexOf( "pageselector" );
        endIndex = allPageString.indexOf( "</select>", beginIndex );
        tempString = allPageString.substring( beginIndex, endIndex );
        
        beginIndex = endIndex = 0;
        int pageCount = tempString.split( "value=" ).length - 1;
        int count = 0;
        String[] pageURL = new String[totalPage + 1];
        for ( int i = 0; i < pageCount; i ++ )
        {
            beginIndex = tempString.indexOf( "value=", beginIndex );
            beginIndex = tempString.indexOf( "\"", beginIndex ) + 1;
            endIndex = tempString.indexOf( "\"", beginIndex );
            String numString = tempString.substring( beginIndex, endIndex );
            pageURL[count++] = webSite + numString + "/";
            //Common.debugPrintln( count + " " + pageURL[count - 1]  ); // debug
            beginIndex = endIndex;
        }
        
        //System.exit( 0 );
        

        for ( int p = 1 ; p <= totalPage && Run.isAlive; p++ ) {
            // 檢查下一張圖是否存在同個資料夾，若存在就跳下一張
            if ( !Common.existPicFile( getDownloadDirectory(), p ) ||
                 !Common.existPicFile( getDownloadDirectory(), p + 1 ) ) {
                Common.downloadFile( pageURL[p - 1], SetUp.getTempDirectory(), indexName, false, "" );
                allPageString = Common.getFileString( SetUp.getTempDirectory(), indexName );

                // 開始取得第一頁網址 
                beginIndex = allPageString.lastIndexOf( "<img src=" );
                beginIndex = allPageString.indexOf( "/", beginIndex );
                endIndex = allPageString.indexOf( "\"", beginIndex );
                comicURL[p - 1] = baseURL + allPageString.substring( beginIndex, endIndex ).trim();
                singlePageDownload( getTitle(), getWholeTitle(), comicURL[p - 1], totalPage, p, 0 );

                //Common.debugPrintln( p + " " + comicURL[p - 1]  ); // debug
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
        String indexName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_city_manga_", "html" );

        Common.downloadFile( urlString, SetUp.getTempDirectory(), indexName, false, "" );

        return Common.getFileString( SetUp.getTempDirectory(), indexName );
    }

    @Override
    public boolean isSingleVolumePage( String urlString ) {
        // ex. http://www.citymanga.com/sexy_commando_gaiden_sugoiyo_masaru-san/chapter-71/
        if ( urlString.matches( "(?).*/chapter(?).*" ) ) {
            return true;
        } else {
            return false;
        }
    }

    public String getMainUrlFromSingleVolumeUrl( String volumeURL ) {
        // ex. http://www.citymanga.com/sexy_commando_gaiden_sugoiyo_masaru-san/chapter-71/轉為
        //    http://www.citymanga.com/sexy_commando_gaiden_sugoiyo_masaru-san/
        int endIndex = volumeURL.substring( 0, volumeURL.length() - 2 ).lastIndexOf( "/" ) + 1;
        String mainPageURL = volumeURL.substring( 0, endIndex );

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
        int beginIndex = allPageString.indexOf( "asubtitle" );
        beginIndex = allPageString.indexOf( ">", beginIndex ) + 1;
        int endIndex = allPageString.indexOf( "<", beginIndex );
        String title = allPageString.substring( beginIndex, endIndex );

        return Common.getStringRemovedIllegalChar( Common.getTraditionalChinese( title ) );
    }

    @Override
    public List<List<String>> getVolumeTitleAndUrlOnMainPage( String urlString, String allPageString ) {
        // combine volumeList and urlList into combinationList, return it.

        List<List<String>> combinationList = new ArrayList<List<String>>();
        List<String> urlList = new ArrayList<String>();
        List<String> volumeList = new ArrayList<String>();

        int beginIndex = allPageString.indexOf( "sclinfo" );
        int endIndex = allPageString.lastIndexOf( "class=\"rss\"" );
        String tempString = allPageString.substring( beginIndex, endIndex );
        String[] tokens = tempString.split( "\"" );

        int volumeCount = 0;

        for ( int i = 0 ; i < tokens.length ; i++ ) {
            if ( tokens[i].matches( "(?s).*http://(?s).*" ) ) {
                urlList.add( tokens[i] );

                // 取得單集名稱
                String volumeTitle = tokens[i + 2];
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
    public void outputVolumeAndUrlList( List<String> volumeList, List<String> urlList ) {
        Common.outputFile( volumeList, SetUp.getTempDirectory(), Common.tempVolumeFileName );
        Common.outputFile( urlList, SetUp.getTempDirectory(), Common.tempUrlFileName );
    }

    @Override
    public String[] getTempFileNames() {
        return new String[] { indexName, indexEncodeName, jsName };
    }
}