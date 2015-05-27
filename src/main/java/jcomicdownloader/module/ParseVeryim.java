/*
 ----------------------------------------------------------------------------------------------------
 Program Name : JComicDownloader
 Authors  : surveyorK
 Last Modified : 2012/11/15
 ----------------------------------------------------------------------------------------------------
 ChangeLog:
 5.07: 修復veryim集數解析不全的問題。
5.01: 1. 修復veryim無法下載的問題。
 *  4.0: 1. 新增對veryim的支援。
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

public class ParseVeryim extends ParseOnlineComicSite {

    private int radixNumber; // use to figure out the name of pic
    private String jsName;
    protected String indexName;
    protected String indexEncodeName;
    protected String baseURL;

    /**

     @author user
     */
    public ParseVeryim() {
        siteID = Site.VERYIM;
        siteName = "Veryim";
        indexName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_veryim_parse_", "html" );
        indexEncodeName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_veryim_encode_parse_", "html" );

        jsName = "index_veryim.js";
        radixNumber = 1591371; // default value, not always be useful!!

        baseURL = "http://www.veryim.com";
    }

    public ParseVeryim( String webSite, String titleName ) {
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
            String allPageString = Common.getFileString( SetUp.getTempDirectory(), indexEncodeName );

            int beginIndex = allPageString.indexOf( "</a>->" ) + 1;
            beginIndex = allPageString.indexOf( "</a>->", beginIndex ) + 6;
            int endIndex = allPageString.indexOf( "->", beginIndex );
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

        int beginIndex = 0;
        int endIndex = 0;
        String tempString = "";

        Common.downloadFile( webSite, SetUp.getTempDirectory(), indexName, false, "" );
        String allPageString = Common.getFileString( SetUp.getTempDirectory(), indexName );


        Common.debugPrint( "開始解析這一集有幾頁 : " );

        // 解析有幾頁
        beginIndex = allPageString.indexOf( "this.totalPage" );
        beginIndex = allPageString.indexOf( "\"", beginIndex ) + 1;
        endIndex = allPageString.indexOf( "\"", beginIndex );
        tempString = allPageString.substring( beginIndex, endIndex );
        totalPage = Integer.parseInt( tempString );
        Common.debugPrintln( "共 " + totalPage + " 頁" );
        comicURL = new String[totalPage];

        
        Common.debugPrintln( "開始解析中間部份的位址" );
        
        // 解析開頭大寫字母
        beginIndex = allPageString.indexOf( "this.letter" );
        beginIndex = allPageString.indexOf( "\"", beginIndex ) + 1;
        endIndex = allPageString.indexOf( "\"", beginIndex );
        String letter = allPageString.substring( beginIndex, endIndex );
        
        // 解析漫畫資料夾名稱
        beginIndex = allPageString.indexOf( "this.comicDir" );
        beginIndex = allPageString.indexOf( "\"", beginIndex ) + 1;
        endIndex = allPageString.indexOf( "\"", beginIndex );
        String comicDir = allPageString.substring( beginIndex, endIndex );
        
        // 解析章節資料夾名稱
        beginIndex = allPageString.indexOf( "this.chapterDir" );
        beginIndex = allPageString.indexOf( "\"", beginIndex ) + 1;
        endIndex = allPageString.indexOf( "\"", beginIndex );
        String chapterDir = allPageString.substring( beginIndex, endIndex );
        
        // 解析圖片副檔名
        beginIndex = allPageString.indexOf( "this.ext" );
        beginIndex = allPageString.indexOf( "\"", beginIndex ) + 1;
        endIndex = allPageString.indexOf( "\"", beginIndex );
        String ext = allPageString.substring( beginIndex, endIndex );
        
        // 解析圖片伺服器位址
        beginIndex = allPageString.indexOf( "this.imgServer" );
        beginIndex = allPageString.indexOf( "\"", beginIndex ) + 1;
        endIndex = allPageString.indexOf( "\"", beginIndex );
        String imgServer = allPageString.substring( beginIndex, endIndex );

        // 解析檔名格式 0:001.jpg  1:001000.jpg
        beginIndex = allPageString.indexOf( "this.fileNameType" );
        beginIndex = allPageString.indexOf( "\"", beginIndex ) + 1;
        endIndex = allPageString.indexOf( "\"", beginIndex );
        int fileNameType = Integer.parseInt( 
            allPageString.substring( beginIndex, endIndex ) );

        
        //String cookie = Common.getCookieString( webSite );
        //System.exit( 0 );


        NumberFormat formatter = new DecimalFormat( "000" );
        String picName = ""; 

        int p = 0; // 目前頁數
        for ( int i = 0; i < totalPage && Run.isAlive; i++ ) {
            if ( fileNameType == 0 ) {
                picName = formatter.format( i + 1 ) + ext;
            }
            else {
                picName = formatter.format( i + 1 ) + formatter.format( i ) + ext;
            }
            
            comicURL[i] = imgServer + "/" + letter + "/" + 
                comicDir + "/" + chapterDir + "/" + picName;

            // 使用最簡下載協定，加入refer始可下載
            singlePageDownloadUsingSimple( getTitle(), getWholeTitle(),
                comicURL[i], totalPage, i + 1, webSite );
           
            //Common.debugPrintln( ( ++ p ) + " " + comicURL[p - 1] ); // debug
        }
        //System.exit( 0 ); // debug
    }

    @Override
    public String getAllPageString( String urlString ) {
        String indexName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_veryim_", "html" );

        Common.downloadFile( urlString, SetUp.getTempDirectory(), indexName, false, "" );

        return Common.getFileString( SetUp.getTempDirectory(), indexName );
    }

    @Override
    public boolean isSingleVolumePage( String urlString ) {
        // ex. http://comic.veryim.com/manhua/guaiwuwangnv/ch_81.html?p=3
        int endIndex = Common.getIndexOfOrderKeyword( urlString, "/", 5 );
        if ( ( endIndex + 2 ) < urlString.length() ) {
            return true;
        }
        else {
            return false;
        }
    }

    public String getMainUrlFromSingleVolumeUrl( String volumeURL ) {
        // ex. http://comic.veryim.com/manhua/guaiwuwangnv/ch_81.html轉為
        //    http://comic.veryim.com/manhua/guaiwuwangnv/

        int endIndex = Common.getIndexOfOrderKeyword( volumeURL, "/", 5 );
        String mainPageURL = volumeURL.substring( 0, endIndex );

        Common.debugPrintln( "MAIN_URL: " + mainPageURL );

        return mainPageURL;
    }

    @Override
    public String getTitleOnMainPage( String urlString, String allPageString ) {
        int beginIndex = allPageString.indexOf( "<h1>" );
        beginIndex = allPageString.indexOf( ">", beginIndex ) + 1;
        int endIndex = allPageString.indexOf( " - ", beginIndex );
        String title = allPageString.substring( beginIndex, endIndex ).trim();

        return Common.getStringRemovedIllegalChar( Common.getTraditionalChinese( title ) );
    }

    @Override
    public List<List<String>> getVolumeTitleAndUrlOnMainPage( String urlString, String allPageString ) {
        // combine volumeList and urlList into combinationList, return it.

        List<List<String>> combinationList = new ArrayList<List<String>>();
        List<String> urlList = new ArrayList<String>();
        List<String> volumeList = new ArrayList<String>();

        int beginIndex = allPageString.indexOf( "id=\"chapters\"" );
        int endIndex = allPageString.indexOf( "</ul>", beginIndex );
        
        if ( allPageString.indexOf( "class=\"ex\"" ) > 0 ) {
            endIndex = allPageString.indexOf( "class=\"ex\"", beginIndex );
            endIndex = allPageString.indexOf( "</ul>", endIndex );
        }

        String tempString = allPageString.substring( beginIndex, endIndex );

        int volumeCount = tempString.split( " href=" ).length - 1;
        totalVolume = volumeCount;
        Common.debugPrintln( "共有" + totalVolume + "集" );

        String volumeTitle = "";
        beginIndex = endIndex = 0;
        for ( int i = 0; i < volumeCount; i++ ) {
            // 取得單集位址
            beginIndex = tempString.indexOf( " href=", beginIndex );
            beginIndex = tempString.indexOf( "\"", beginIndex ) + 1;
            endIndex = tempString.indexOf( "\"", beginIndex );
            urlList.add( tempString.substring( beginIndex, endIndex ) );
            // 取得單集名稱
            beginIndex = tempString.indexOf( ">", beginIndex ) + 1;
            endIndex = tempString.indexOf( "</a>", beginIndex );
            volumeTitle = tempString.substring( beginIndex, endIndex );
            volumeTitle = volumeTitle.replaceAll( "<.*>", "" );
            volumeList.add( getVolumeWithFormatNumber( Common.getStringRemovedIllegalChar(
                Common.getTraditionalChinese( volumeTitle.trim() ) ) ) );
        }

        combinationList.add( volumeList );
        combinationList.add( urlList );

        return combinationList;
    }
}
