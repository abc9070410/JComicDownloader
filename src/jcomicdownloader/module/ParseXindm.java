/*
 ----------------------------------------------------------------------------------------------------
 Program Name : JComicDownloader
 Authors  : surveyorK
 Last Modified : 2011/12/16
 ----------------------------------------------------------------------------------------------------
 ChangeLog:
 4.18: 恢復對xindm的支援。
 3.02: 修復xindm部份漫畫無法下載的bug。
 2.08: 修復xindm解析錯誤的問題。
 1.15: 新增對xindm.cn的支援
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

public class ParseXindm extends ParseOnlineComicSite {

    private int radixNumber; // use to figure out the name of pic
    private String jsName;
    protected String indexName;
    protected String indexEncodeName;

    /**

     @author user
     */
    public ParseXindm() {
        siteID = Site.XINDM;
        siteName = "Xindm";
        indexName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_xindm_parse_", "html" );
        indexEncodeName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_xindm_encode_parse_", "html" );

        jsName = "index_xindm.js";
        radixNumber = 185273; // default value, not always be useful!!
    }

    public ParseXindm( String webSite, String titleName ) {
        this();
        this.webSite = webSite;
        this.title = titleName;
    }

    @Override
    public void setParameters() {
        Common.debugPrintln( "開始解析各參數 :" );

        Common.debugPrintln( "開始解析title和wholeTitle :" );

        if ( getWholeTitle() == null || getWholeTitle().equals( "" ) ) {
            String allPageString = getAllPageString( webSite );
            int beginIndex = Common.getIndexOfOrderKeyword( allPageString, ">>", 4 ) + 2;
            int endIndex = allPageString.indexOf( "<", beginIndex );
            String title = allPageString.substring( beginIndex, endIndex ).trim();

            setWholeTitle( Common.getStringRemovedIllegalChar( Common.getTraditionalChinese( title ) ) );
        }

        Common.debugPrintln( "作品名稱(title) : " + getTitle() );
        Common.debugPrintln( "章節名稱(wholeTitle) : " + getWholeTitle() );
    }

    @Override
    public void parseComicURL() { // parse URL and save all URLs in comicURL  //
        // 先取得前面的下載伺服器網址

        String allPageString = getAllPageString( webSite );
        Common.debugPrint( "開始解析這一集有幾頁 : " );

        

        String baseURL = "http://mh2.xindm.cn";

        int beginIndex = allPageString.indexOf( "Array(" );
        beginIndex = allPageString.indexOf( "\"", beginIndex );
        int endIndex = allPageString.indexOf( ");", beginIndex );
        String tempPicString = allPageString.substring( beginIndex, endIndex );
        String[] picURLs = tempPicString.split( "," );
        
        totalPage = picURLs.length;
        Common.debugPrintln( "共 " + totalPage + " 頁" );
        comicURL = new String[totalPage];
        
        for ( int i = 0; i < picURLs.length; i ++ ) {
            comicURL[i] = baseURL + picURLs[i].replaceAll( "\"", "" );
            Common.debugPrintln( "第" + ( i + 1 ) + "頁網址：" + comicURL[i] );
        }
        

        // 須取得cookie才能下載圖片（防盜連專家....）
        String[] cookies = Common.getCookieStrings( webSite, null );
        String cookieString = "";
        int cookieCount = 0; // 取得前兩組cookie就可以了
        if ( cookies[0] != null ) {
           
            cookieString = "Hm_lvt_016bf6f495d44a067f569423ad894560=1337210178886; " + cookies[0].split( ";" )[0];
        }
        Common.debugPrintln( "取得cookies：" + cookieString );
        
        for ( int p = 1; p <= totalPage && Run.isAlive; p++ ) {

            String referURL = webSite + "?p=" + p;
            // 每解析一個網址就下載一張圖
            singlePageDownloadUsingSimple( getTitle(), getWholeTitle(), comicURL[p - 1],
                totalPage, p, cookieString, referURL );
            
            Common.debugPrintln( (p) + " " + comicURL[p - 1] + " " + referURL ); // debug
        }
        //System.exit(1); // debug
    }

    @Override
    public String getAllPageString( String urlString ) {
        String indexName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_xindm_", "html" );
        String indexEncodeName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_xindm_encode_", "html" );
        Common.downloadFile( urlString, SetUp.getTempDirectory(), indexName, false, "" );
        Common.newEncodeFile( SetUp.getTempDirectory(), indexName, indexEncodeName );

        return Common.getFileString( SetUp.getTempDirectory(), indexEncodeName );
    }

    @Override
    public boolean isSingleVolumePage( String urlString ) {
        // ex.http://www.xindm.cn/mh/mofazhanjinaiyeForce/57204.html
        if ( urlString.matches( "(?s).*.html" ) ) {
            return true;
        }
        else {
            return false;
        }
    }

    public String getMainUrlFromSingleVolumeUrl( String volumeURL ) {
        // ex. http://mh2.xindm.cn/display.asp?id=55200轉為
        //     http://xindm.cn/type.asp?typeid=5114 // 太難做不到

        return "";
    }

    @Override
    public String getTitleOnSingleVolumePage( String urlString ) {
        String allPageString = getAllPageString( urlString );
        int beginIndex = Common.getIndexOfOrderKeyword( allPageString, ">>", 3 ) + 2;
        int endIndex = Common.getSmallerIndexOfTwoKeyword( allPageString, beginIndex, "[", ">>" );
        String title = allPageString.substring( beginIndex, endIndex ).trim();

        return Common.getStringRemovedIllegalChar( Common.getTraditionalChinese( title ) );
    }

    @Override
    public String getTitleOnMainPage( String urlString, String allPageString ) {
        int beginIndex = allPageString.indexOf( "valign=\"middle\"><b>" );
        beginIndex = allPageString.indexOf( "<b>", beginIndex ) + 3;
        int endIndex = allPageString.indexOf( "</b>", beginIndex );
        String title = allPageString.substring( beginIndex, endIndex ).trim();

        return Common.getStringRemovedIllegalChar( Common.getTraditionalChinese( title ) );
    }

    @Override
    public List<List<String>> getVolumeTitleAndUrlOnMainPage( String urlString, String allPageString ) {
        // combine volumeList and urlList into combinationList, return it.

        List<List<String>> combinationList = new ArrayList<List<String>>();
        List<String> urlList = new ArrayList<String>();
        List<String> volumeList = new ArrayList<String>();

        int beginIndex = allPageString.indexOf( "<li><a href=\"http://www" ) - 1;
        int endIndex = allPageString.indexOf( "</table>", beginIndex );
        String listString = allPageString.substring( beginIndex, endIndex );

        totalVolume = allPageString.split( "<li><a href=\"http://www" ).length - 1;
        
        beginIndex = endIndex = 0;
        for ( int i = 0; i < totalVolume; i++ ) {
            
            // 取得單集位址
            beginIndex = listString.indexOf( "http://www", beginIndex );
            endIndex = listString.indexOf( "\"", beginIndex );
            urlList.add( listString.substring( beginIndex, endIndex ) );

            // 取得單集名稱
            beginIndex = listString.indexOf( "<span", beginIndex ) + 1;
            beginIndex = listString.indexOf( ">", beginIndex ) + 1;
            endIndex = listString.indexOf( "<", beginIndex );
            String volumeTitle = listString.substring( beginIndex, endIndex );

            volumeList.add( getVolumeWithFormatNumber( Common.getStringRemovedIllegalChar(
                Common.getTraditionalChinese( volumeTitle.trim() ) ) ) );
        }

        Common.debugPrintln( "共有" + totalVolume + "集" );

        combinationList.add( volumeList );
        combinationList.add( urlList );

        return combinationList;
    }
}
