/*
 ----------------------------------------------------------------------------------------------------
 Program Name : JComicDownloader
 Authors  : surveyorK
 Last Modified : 2012/6/12
 ----------------------------------------------------------------------------------------------------
 ChangeLog:
     5.04: 1. 修復17kk下載錯誤的問題。
 *  4.10: 1. 新增對17kk的支援。
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

public class Parse17KK extends ParseOnlineComicSite {

    private int radixNumber; // use to figure out the name of pic
    private String jsName;
    protected String indexName;
    protected String indexEncodeName;
    protected String baseURL;

    /**

     @author user
     */
    public Parse17KK() {
        siteID = Site.ONESEVEN_KK;
        siteName = "17kk";
        pageExtension = "htm";
        pageCode = Encoding.GB2312;
        indexName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_17kk_parse_", "html" );
        indexEncodeName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_17kk_encode_parse_", "html" );

        jsName = "index_17kk.js";
        radixNumber = 1037661; // default value, not always be useful!!

        baseURL = "http://www.17kk.cc";
    }

    public Parse17KK( String webSite, String titleName ) {
        this();
        this.webSite = webSite;
        this.title = titleName;
    }

    @Override
    public void setParameters() {
        Common.debugPrintln( "開始解析各參數 :" );
        Common.debugPrintln( "開始解析title和wholeTitle :" );

        Common.downloadFile( webSite, SetUp.getTempDirectory(), indexName, false, "" );
        Common.newEncodeFile( SetUp.getTempDirectory(), indexName, indexEncodeName, pageCode );

        if ( getWholeTitle() == null || getWholeTitle().equals( "" ) ) {
            // 因為正常解析不需要用到單集頁面，所以給此兩行放進來
            String allPageString = Common.getFileString( SetUp.getTempDirectory(), indexEncodeName );

            int beginIndex = allPageString.indexOf( "jiename" );
            beginIndex = allPageString.indexOf( "\"", beginIndex ) + 1;
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

        // 先取得所有的下載伺服器網址
        int beginIndex = 0, endIndex = 0;
        String tempString = "";

        Common.debugPrint( "開始解析這一集有幾頁 : " );
        String allPageString = Common.getFileString( SetUp.getTempDirectory(), indexEncodeName );

        // 找出有幾頁
        beginIndex = allPageString.indexOf( " imgallpage" );
        beginIndex = allPageString.indexOf( "\"", beginIndex ) + 1;
        endIndex = allPageString.indexOf( "\"", beginIndex );
        tempString = allPageString.substring( beginIndex, endIndex ).trim();

        totalPage = Integer.parseInt( tempString );
        Common.debugPrintln( "共 " + totalPage + " 頁" );
        comicURL = new String[totalPage];

        // 找出jieid
        beginIndex = allPageString.indexOf( " jieid" );
        beginIndex = allPageString.indexOf( "\"", beginIndex ) + 1;
        endIndex = allPageString.indexOf( "\"", beginIndex );
        String jieid = allPageString.substring( beginIndex, endIndex ).trim();

        // 找出頁面目錄
        String basePageURL = baseURL + "/intro_view/" + jieid + "/";


        String pageURL = webSite;
        beginIndex = endIndex = 0;
        for ( int i = 1; i <= totalPage && Run.isAlive; i++ ) {
            allPageString = getAllPageString( pageURL );
            
            if ( !Common.existPicFile( getDownloadDirectory(), i )
                || !Common.existPicFile( getDownloadDirectory(), i + 1 ) ) {
                
                // 開始第一張圖片位址
                beginIndex = allPageString.indexOf( "id=\"viewimg\"" );
                beginIndex = allPageString.indexOf( "src=", beginIndex );
                beginIndex = allPageString.indexOf( "=", beginIndex ) + 1;
                endIndex = allPageString.indexOf( ">", beginIndex );
                String picURL = allPageString.substring( beginIndex, endIndex ).trim();
                picURL = getRealPicURL( picURL );
                //Common.debugPrintln( "第一張圖片位址：" + firstPicURL );
                Common.debugPrint( i + " " );
                // 每解析一個網址就下載一張圖
                singlePageDownload( getTitle(), getWholeTitle(), picURL, totalPage, i, 0 );

            }
            else {
                Common.debugPrintln( "跳過：" + pageURL );
            }
            
            // 取得下一張頁面位址
            beginIndex = allPageString.indexOf( "class=\"vidwPage\"" );
            endIndex = allPageString.indexOf( "</div>", beginIndex );
            endIndex = allPageString.lastIndexOf( "\"", endIndex );
            beginIndex = allPageString.lastIndexOf( "\"", endIndex - 1 ) + 1;
            String nextPageName = allPageString.substring( beginIndex, endIndex );
            pageURL = basePageURL + nextPageName; // 下一頁的位址
            
            //Common.debugPrintln( "下一頁位址：" + pageURL ); // for debug
            
            //System.exit( 0 ); // debug
        }
    }

    // 取得真實的圖片位址
    private String getRealPicURL( String url ) {
        url = url.replace( "www.17kk.net", "image.17kk.cc/image1.17kk" );
        url = url.replace( "image.17kk.net", "image.17kk.cc/image1.17kk" );
        url = url.replace( "image3.17kk.net", "image.17kk.cc/image3.17kk" );

        url = url.replace( "image4.17kk.net", "image.17kk.cc/image6.17kk" );
        url = url.replace( "image6.17kk.net", "image.17kk.cc/image6.17kk" );
        url = url.replace( "image0.17kk.net", "image.17kk.cc/image0.17kk" );
        url = url.replace( "image2.17kk.net", "image.17kk.cc/image2.17kk" );

        url = url.replace( "image7.17kk.net", "image.17kk.cc/image7.17kk" );
        url = url.replace( "comiclist.17kk.net", "image.17kk.cc" );
        
        return url;
    }

    @Override
    public boolean isSingleVolumePage( String urlString ) {
        // ex. http://www.17kk.cc/intro_view/36066/1_1405.htm
        if ( urlString.matches( "(?s).*/intro_view/(?s).*" ) ||
             urlString.matches( "(?s).*/asp_net/(?s).*" ) ) {
            return true;
        }
        else // ex. http://www.17kk.cc/intro/17953.htm
        {
            return false;
        }
    }

    public String getMainUrlFromSingleVolumeUrl( String volumeURL ) {
        // ex. http://www.17kk.cc/intro_view/36066/1_1405.htm轉為
        //    http://www.17kk.cc/intro/17953.htm

        String allPageString = getAllPageString( volumeURL );

        int beginIndex = allPageString.indexOf( " bookid" );
        beginIndex = allPageString.indexOf( "\"", beginIndex ) + 1;
        int endIndex = allPageString.indexOf( "\"" );
        String bookid = allPageString.substring( beginIndex, endIndex );


        String mainPageURL = baseURL + "/intro/" + bookid + "." + pageExtension;

        Common.debugPrintln( "MAIN_URL: " + mainPageURL );

        return mainPageURL;
    }

    @Override
    public String getAllPageString( String urlString ) {

        String indexName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_17kk_", pageExtension );
        String indexEncodeName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_17kk_encode_", pageExtension );

        Common.downloadFile( urlString, SetUp.getTempDirectory(), indexName, false, "" );
        Common.newEncodeFile( SetUp.getTempDirectory(), indexName, indexEncodeName, pageCode );

        return Common.getFileString( SetUp.getTempDirectory(), indexEncodeName );
    }

    @Override
    public String getTitleOnMainPage( String urlString, String allPageString ) {
        int beginIndex = allPageString.indexOf( " bookname" );
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

        int beginIndex = allPageString.indexOf( "id=\"listread\"" );
        int endIndex = allPageString.indexOf( "id=\"bbstitle\"", beginIndex );
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
            endIndex = tempString.indexOf( "</a>", beginIndex );
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
