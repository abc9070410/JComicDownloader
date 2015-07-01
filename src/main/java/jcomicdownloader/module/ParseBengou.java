/*
----------------------------------------------------------------------------------------------------
Program Name : JComicDownloader
Authors  : surveyorK
Last Modified : 2011/12/27
----------------------------------------------------------------------------------------------------
ChangeLog:
 5.13: 修復bengou因網頁編碼改變而無法下載的問題。
    5.02: 修復bengou因網頁改版而解析失敗的問題。 
    5.0: 1. 修復bengou因網頁改版而解析失敗的問題。
 * 2.12: 1. 新增對www.bengou.com的支援。
----------------------------------------------------------------------------------------------------
 */
package jcomicdownloader.module;

import java.util.ArrayList;
import java.util.List;
import jcomicdownloader.SetUp;
import jcomicdownloader.enums.Site;
import jcomicdownloader.tools.Common;

public class ParseBengou extends ParseOnlineComicSite {

    private int radixNumber; // use to figure out the name of pic
    private String jsName;
    protected String indexName;
    protected String indexEncodeName;
    protected String baseURL;

    /**
     *
     * @author user
     */
    public ParseBengou() {
        siteID = Site.BENGOU;
        siteName = "Bengou";
        indexName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_bengou_parse_", "html" );
        indexEncodeName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_bengou_encode_parse_", "html" );

        jsName = "index_bengou.js";
        radixNumber = 15961371; // default value, not always be useful!!

        baseURL = "http://www.bengou.com";
    }

    public ParseBengou( String webSite, String titleName ) {
        this();
        this.webSite = webSite;
        this.title = titleName;
    }

    @Override
    public void setParameters() {
        Common.debugPrintln( "開始解析各參數 :" );

        Common.debugPrintln( "開始解析title和wholeTitle :" );

         Common.downloadGZIPInputStreamFile( webSite, SetUp.getTempDirectory(), indexName, false, "" );
        //Common.downloadFile( webSite, SetUp.getTempDirectory(), indexName, false, "" );

        if ( getWholeTitle() == null || getWholeTitle().equals( "" ) ) {
            String allPageString = Common.getFileString( SetUp.getTempDirectory(), indexName );
            String indexName = webSite.substring( webSite.lastIndexOf( "/" ) + 1, webSite.length() );
            
            int beginIndex = allPageString.indexOf( "/" + indexName ) + 1;
            beginIndex = allPageString.indexOf( "/" + indexName ) + 1;
            beginIndex = allPageString.indexOf( ">" + indexName ) + 1;
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
        // 先取得前面的下載伺服器網址

        String allPageString = Common.getFileString( SetUp.getTempDirectory(), indexName );
        Common.debugPrint( "開始解析這一集有幾頁 : " );

        int beginIndex = 0;
        int endIndex = 0;
        
        // 取得基本位址
        beginIndex = allPageString.indexOf( "pic_base" );
        beginIndex = allPageString.indexOf( "'", beginIndex ) + 1;
        endIndex = allPageString.indexOf( "'", beginIndex );
        
        String basePicURL = allPageString.substring( beginIndex, endIndex );
        
        beginIndex = allPageString.indexOf( "var picTree" ) + 1;
        beginIndex = allPageString.indexOf( "[", beginIndex ) + 1;
        beginIndex = allPageString.indexOf( "\"", beginIndex ) + 1;
        endIndex = allPageString.indexOf( "]", beginIndex ) - 1;
        
        String[] picNames = allPageString.substring( beginIndex, endIndex ).split( "\",\"" );
        

        totalPage = picNames.length;
        Common.debugPrintln( "共 " + totalPage + " 頁" );
        comicURL = new String[totalPage];
        
        /*
        // 取得js位址
        beginIndex = allPageString.indexOf( "var pictree" ) + 1;
        beginIndex = allPageString.indexOf( "[", beginIndex ) + 1;
        endIndex = allPageString.indexOf( "]", beginIndex );
        
        String tempString = allPageString.substring( beginIndex, endIndex );


        String[] urlTokens = tempString.split( "," );
        
        // 之後擷取出來的圖片網址都要在前面加上basePicURL才是真正圖片網址
        beginIndex = allPageString.indexOf( "class=\"center comicpic\"" );
        beginIndex = allPageString.indexOf( "src=", beginIndex );
        beginIndex = allPageString.indexOf( "\"", beginIndex ) + 1;
        endIndex = allPageString.indexOf( "\"", beginIndex );
        String firstPicURL = allPageString.substring( beginIndex, endIndex ); // 取得第一張圖片位址
        String extensionName = firstPicURL.split( "\\." )[firstPicURL.split( "\\." ).length - 1]; // 取得圖片附檔名
        String basePicURL = firstPicURL.substring( 0, firstPicURL.lastIndexOf( "/" ) + 1 ); // 取得基本圖片位址
        */
        String picURL = "";
        int p = 0; // 目前頁數
        for ( int i = 0 ; i < picNames.length && Run.isAlive; i++ ) {
            /*
            picURL = basePicURL + urlTokens[i].replaceAll( "'|\\.html", "" ) + "." + extensionName;;
            if ( !Common.urlIsOK( picURL ) ) // 只猜jpg和png兩種，若有其他種副檔名就會下載失敗......
                picURL = picURL.replaceAll( "\\.jpg", ".png" );
            */
            
            comicURL[p++] = basePicURL + picNames[i];
            Common.debugPrintln( p + " 解析位址: " + comicURL[p-1]  ); // debug
            //System.exit( 0 ); // debug
            
            // 每解析一個網址就下載一張圖
            singlePageDownload( getTitle(), getWholeTitle(), comicURL[p - 1], totalPage, p, 0 );
            
            //System.exit( 0 ); // debug
        }

        //System.exit( 0 ); // debug
    }

    @Override
    public String getAllPageString( String urlString ) {
        String indexName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_bengou_", "html" );

        //Common.downloadFile( urlString, SetUp.getTempDirectory(), indexName, false, "" );
        Common.downloadGZIPInputStreamFile( urlString, SetUp.getTempDirectory(), indexName, false, "" );

        return Common.getFileString( SetUp.getTempDirectory(), indexName );
    }

    @Override
    public boolean isSingleVolumePage( String urlString ) {
        // ex. http://www.bengou.com/100311/xymsl10031113/1268286243720/1268286244826.html
        if ( Common.getAmountOfString( urlString, "/" ) > 5 ) {
            return true;
        } else {
            return false;
        }
    }

    public String getMainUrlFromSingleVolumeUrl( String volumeURL ) {
        // ex. http://www.bengou.com/100311/xymsl10031113/1271645238922/1271645238922.html轉為
        //    http://www.bengou.com/100311/xymsl10031113/index.html

        int endIndex = volumeURL.lastIndexOf( "/" );
        endIndex = volumeURL.lastIndexOf( "/", endIndex - 1 ) + 1;
        String mainPageURL = volumeURL.substring( 0, endIndex ) + "index.html";

        Common.debugPrintln( "MAIN_URL: " + mainPageURL );

        return mainPageURL;
    }

    @Override
    public String getTitleOnMainPage( String urlString, String allPageString ) {
        int beginIndex = allPageString.indexOf( "<h1" );
        beginIndex = allPageString.indexOf( ">", beginIndex ) + 1;
        int endIndex = allPageString.indexOf( "<", beginIndex );
        String title = allPageString.substring( beginIndex, endIndex ).trim();

        return Common.getStringRemovedIllegalChar( Common.getTraditionalChinese( title ) );
    }

    @Override
    public List<List<String>> getVolumeTitleAndUrlOnMainPage( String urlString, String allPageString ) {
        // combine volumeList and urlList into combinationList, return it.

        List<List<String>> combinationList = new ArrayList<List<String>>();
        List<String> urlList = new ArrayList<String>();
        List<String> volumeList = new ArrayList<String>();

        int beginIndex = allPageString.indexOf( "class=\"section-list all\"" );
        int endIndex = allPageString.indexOf( "class=\"section-hd", beginIndex );
        String tempString = allPageString.substring( beginIndex, endIndex );

        int volumeCount = tempString.split( "href=\"http" ).length - 1;
       
        // 擷取單集網址後要加上baseVolumeURL才是完整位址
        //String baseVolumeURL = urlString.substring( 0, urlString.lastIndexOf( "/" ) + 1 );

        String volumeTitle = "";
        beginIndex = endIndex = 0;
        for ( int i = 0 ; i < volumeCount ; i++ ) {
            // 取得單集位址
            beginIndex = tempString.indexOf( "href=\"http", beginIndex ) + 6;
            endIndex = tempString.indexOf( "\"", beginIndex );
            urlList.add( tempString.substring( beginIndex, endIndex ) );

            // 取得單集名稱
            beginIndex = tempString.indexOf( ">", beginIndex ) + 1;
            endIndex = tempString.indexOf( "</a>", beginIndex );
            volumeTitle = tempString.substring( beginIndex, endIndex );
            volumeTitle = volumeTitle.replaceAll( "</span>", "" );
            volumeTitle = volumeTitle.replaceAll( "<span.*>", "" );

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
