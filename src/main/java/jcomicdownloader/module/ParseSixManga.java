/*
 ----------------------------------------------------------------------------------------------------
 Program Name : JComicDownloader
 Authors  : surveyorK
 Last Modified : 2013/11/23
 ----------------------------------------------------------------------------------------------------
 ChangeLog:
5.19:  修復6manga無法下載的問題。
 5.13: 修復6manga無法下載的問題。
 *  4.01: 1. 新增對6manga.com的支援。
 ----------------------------------------------------------------------------------------------------
 */
package jcomicdownloader.module;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import jcomicdownloader.SetUp;
import jcomicdownloader.encode.Encoding;
import jcomicdownloader.enums.Site;
import jcomicdownloader.tools.Common;

public class ParseSixManga extends ParseOnlineComicSite {

    private int radixNumber; // use to figure out the name of pic
    private String jsName;
    protected String indexName;
    protected String indexEncodeName;
    protected String baseURL;

    /**
     *
     * @author user
     */
    public ParseSixManga() {
        siteID = Site.SIX_MANGA;
        siteName = "6Manga";
        indexName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_6manga_parse_", "html" );
        indexEncodeName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_6manga_encode_parse_", "html" );

        jsName = "index_6manga.js";
        radixNumber = 15221471; // default value, not always be useful!!

        baseURL = "http://6manga.com";
    }

    public ParseSixManga( String webSite, String titleName ) {
        this();
        this.webSite = webSite;
        this.title = titleName;
    }

    @Override
    public void setParameters() {
        Common.debugPrintln( "開始解析各參數 :" );

        Common.debugPrintln( "開始解析title和wholeTitle :" );
        Common.downloadFile( webSite, SetUp.getTempDirectory(), indexName, false, "" );
        Common.newEncodeFile( SetUp.getTempDirectory(), indexName, indexEncodeName, Encoding.BIG5 );
        
        if ( getWholeTitle() == null || getWholeTitle().equals( "" ) ) {
            String allPageString = Common.getFileString( SetUp.getTempDirectory(), indexEncodeName );

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

        String allPageString = Common.getFileString( SetUp.getTempDirectory(), indexEncodeName );
        Common.debugPrint( "開始解析這一集有幾頁 : " );

        int beginIndex = allPageString.indexOf( "var pic=" );
        beginIndex = allPageString.indexOf( "'", beginIndex ) + 1;
        int endIndex = allPageString.indexOf( "/", beginIndex ) + 1;
        endIndex = allPageString.indexOf( "/", beginIndex ) + 1;
        endIndex = allPageString.indexOf( "/", beginIndex ) + 1;
        String path = allPageString.substring( beginIndex, endIndex );
        
        endIndex = allPageString.indexOf( "'", beginIndex );
        String tempString = allPageString.substring( beginIndex, endIndex );
        
        String[] tokens = tempString.split( path );

        totalPage = tokens.length - 1;
        Common.debugPrintln( "共 " + totalPage + " 頁" );
        comicURL = new String[totalPage];
        
        //http://6manga.com/comics/13/03/21/02/05452001.jpg
        //http://img4.6manga.com/13/03/21/02/05452001.jpg

        // 開始解析圖片網址
        String picBaseURL = "http://6manga.com/comics/";
        for ( int p = 0; p < totalPage && Run.isAlive; p ++ )
        {
            comicURL[p] = Common.getFixedChineseURL( picBaseURL + path + tokens[p+1] + ".jpg" );
            
            if ( !Common.existPicFile( getDownloadDirectory(), p + 1 ) || 
                 !Common.existPicFile( getDownloadDirectory(), p + 2 ) ) {
                
                //Common.debugPrintln( ( p + 1 ) + " " + comicURL[p] ); // debug

                // 每解析一個網址就下載一張圖
                //singlePageDownload( getTitle(), getWholeTitle(), comicURL[p], totalPage, p + 1, 0 );
                singlePageDownloadUsingSimple( getTitle(), getWholeTitle(), comicURL[p], totalPage, p + 1, "", webSite );
            }
            //System.exit( 0 );
        }
        
        //System.exit( 0 ); // debug
    }

    @Override
    public String getAllPageString( String urlString ) {
        String indexName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_6manga_", "html" );
        String indexEncodeName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_6manga_encode_", "html" );

        Common.simpleDownloadFile( urlString, SetUp.getTempDirectory(), indexName, "" );
        Common.newEncodeFile( SetUp.getTempDirectory(), indexName, indexEncodeName, Encoding.BIG5 );

        return Common.getFileString( SetUp.getTempDirectory(), indexEncodeName );
    }

   @Override
    public boolean isSingleVolumePage( String urlString ) {
        // ex.  http://6manga.com/page/comics/7/0/418_ibitsu.html
        if ( Common.getAmountOfString( urlString, "/" ) > 6 ) {
            return true;
        }
        else {
            return false;
        }
    }

    public String getMainUrlFromSingleVolumeUrl( String volumeURL ) {
        // ex. http://6manga.com/page/comics/5/9/646/_lovely_supergirls_league.html轉為
        //     http://6manga.com/comic/lovely_supergirls_league/

        String allPageString = getAllPageString( volumeURL );
        int beginIndex = allPageString.indexOf( "/js/view.js" );
        beginIndex = allPageString.indexOf( " href=", beginIndex );
        beginIndex = allPageString.indexOf( "\"", beginIndex ) + 1;
        int endIndex = allPageString.indexOf( "\"", beginIndex );

        String mainPageURL = baseURL + allPageString.substring( beginIndex, endIndex );

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

        beginIndex = allPageString.indexOf( "font class=\"comic\"" );
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

        beginIndex = allPageString.indexOf( "id=\"tr" );
        endIndex = allPageString.indexOf( "<script>", beginIndex );

        // 存放集數頁面資訊的字串
        tempString = allPageString.substring( beginIndex, endIndex );

        int volumeCount = tempString.split( " href=" ).length - 1;

        String volumeTitle = "";
        beginIndex = endIndex = 0;
        for ( int i = 0; i < volumeCount; i++ ) {
            // 取得單集位址
            beginIndex = tempString.indexOf( " href=", beginIndex );
            beginIndex = tempString.indexOf( "'", beginIndex ) + 1;
            endIndex = tempString.indexOf( "'", beginIndex );
            urlList.add( Common.getFixedChineseURL( baseURL + tempString.substring( beginIndex, endIndex ) ) );

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
                 volumeTitle.trim() ) ) );

        }

        totalVolume = volumeCount;
        Common.debugPrintln( "共有" + totalVolume + "集" );

        combinationList.add( volumeList );
        combinationList.add( urlList );

        return combinationList;
    }
}


