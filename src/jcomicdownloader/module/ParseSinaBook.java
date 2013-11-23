/*
----------------------------------------------------------------------------------------------------
Program Name : JComicDownloader
Authors  : surveyorK
Last Modified : 2012/6/11
----------------------------------------------------------------------------------------------------
ChangeLog:
 *  4.09: 1. 新增對book.sina.com的支援。
----------------------------------------------------------------------------------------------------
 */
package jcomicdownloader.module;

import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import jcomicdownloader.SetUp;
import jcomicdownloader.encode.Encoding;
import jcomicdownloader.enums.FileFormatEnum;
import jcomicdownloader.enums.Site;
import jcomicdownloader.tools.Common;

public class ParseSinaBook extends ParseEightNovel {

    private int radixNumber; // use to figure out the name of pic
    private String jsName;
    protected String indexName;
    protected String indexEncodeName;
    protected String baseURL;
    protected int floorCountInOnePage; // 一頁有幾層樓

    /**
    
    @author user
     */
    public ParseSinaBook() {
        siteID = Site.SINA_BOOK;
        siteName = "SinaBook";
        pageExtension = "html"; // 網頁副檔名
        pageCode = Encoding.GB2312; // 網頁預設編碼
        
        indexName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_sinabook_parse_", pageExtension );
        indexEncodeName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_sinabook_encode_parse_", pageExtension );

        jsName = "index_sinabook.js";
        radixNumber = 15154451; // default value, not always be useful!!

        baseURL = "http://book.qq.com/s/book/";
    }


    @Override
    public void parseComicURL() { // parse URL and save all URLs in comicURL  //
        webSite = getRegularURL( webSite );

        Common.downloadFile( webSite, SetUp.getTempDirectory(), indexName, false, "" );
        Common.newEncodeFile( SetUp.getTempDirectory(), indexName, indexEncodeName, pageCode );
        String allPageString = Common.getFileString( SetUp.getTempDirectory(), indexEncodeName );
        Common.debugPrint( "開始解析這一集有幾頁 : " );

        int beginIndex = 0, endIndex = 0;

        beginIndex = allPageString.indexOf( "class=\"tit\"" );
        endIndex = Common.getSmallerIndexOfTwoKeyword( allPageString, beginIndex, "_butLink", "alt=\"VIP" );
        String tempString = allPageString.substring( beginIndex, endIndex ).trim();

        totalPage = tempString.split( "href=\"chapter" ).length - 1;
        Common.debugPrintln( "共 " + totalPage + " 頁" );
        comicURL = new String[totalPage];
        String[] titles = new String[totalPage];

        endIndex = Common.getIndexOfOrderKeyword( webSite, "/", 4 ) + 1;
        baseURL = webSite.substring( 0, endIndex );
        
        // 取得作者名稱
        beginIndex = allPageString.indexOf( "class=\"mess\"" );
        endIndex = allPageString.indexOf( "</a>", beginIndex );
        beginIndex = allPageString.lastIndexOf( ">", endIndex ) + 1;
        String author = "";
        if ( beginIndex > 0 && endIndex > 0 ) {
            author = allPageString.substring( beginIndex, endIndex );
        }
        else {
            System.out.println( beginIndex + " " + endIndex );
            author = "";
        }
        Common.debugPrintln( "作者：" + author );

        // 取得小說網址
        beginIndex = endIndex = 0;
        for ( int i = 0 ; i < totalPage; i++ ) {
            // 取得網址
            beginIndex = tempString.indexOf( "href=\"chapter", beginIndex );
            beginIndex = tempString.indexOf( "\"", beginIndex ) + 1;
            endIndex = tempString.indexOf( "\"", beginIndex );
            comicURL[i] = baseURL + tempString.substring( beginIndex, endIndex ).trim();

            // 取得標題
            beginIndex = tempString.indexOf( ">", beginIndex ) + 1;
            endIndex = tempString.indexOf( "</a>", beginIndex );
            titles[i] = Common.getStringRemovedIllegalChar( Common.getTraditionalChinese( 
                    tempString.substring( beginIndex, endIndex ).trim() ) ) + "." + Common.getDefaultTextExtension();
            
            //Common.debugPrintln( i + " " + titles[i] + " " + comicURL[i] ); // debug
        }
        //System.exit(  0 ); // for debug
        
        
        
        NumberFormat formatter = new DecimalFormat( Common.getZero() );
        
        for ( int i = 0 ; i < totalPage && Run.isAlive ; i++ ) {
        // 每解析一個網址就下載一張圖
            if ( !new File( getDownloadDirectory() + titles[i] ).exists() && Run.isAlive ) {
                singlePageDownload( getTitle(), getWholeTitle(), comicURL[i], totalPage, i + 1, 0 );
                String fileName = formatter.format( i + 1 ) + "." + pageExtension;
                handleSingleNovel( fileName, titles[i] );  // 處理單一小說主函式
            }
            else {
                Common.debugPrintln( titles[i] + "已下載，跳過" );
            }
        }

        handleWholeNovel( titles, webSite, author );

        //System.exit( 0 ); // debug
    }


    // 處理小說網頁，將標籤去除
    @Override
    public String getRegularNovel( String allPageString ) {
        int beginIndex = 0;
        int endIndex = 0;
        String oneFloorText = ""; // 單一樓層（頁）的文字
        beginIndex = allPageString.indexOf( "<h1>" );
        endIndex = allPageString.indexOf( "</h1>", beginIndex );
        String title = allPageString.substring( beginIndex, endIndex );

        beginIndex = allPageString.indexOf( "id=\"contTxt\"", beginIndex );
        beginIndex = allPageString.indexOf( ">", beginIndex ) + 1;
        endIndex = allPageString.indexOf( "</div>", beginIndex );
        oneFloorText = title + "<br><br>" + allPageString.substring( beginIndex, endIndex );

        if ( SetUp.getDefaultTextOutputFormat() == FileFormatEnum.TEXT ) {
            oneFloorText = replaceProcessToText( oneFloorText );
        } else {
            oneFloorText = replaceProcessToHtml( oneFloorText );
        }
        oneFloorText = Common.getTraditionalChinese( oneFloorText ); // 簡轉繁


        return oneFloorText;
    }
    
    // 取得正規網址
    // ex. http://vip.book.sina.com.cn/book/catalog.php?book=47950轉
    //      http://vip.book.sina.com.cn/book/index_47950.html
    @Override
    public String getRegularURL( String url ) {
        System.out.println( "處理：" );
        if ( url.matches( "(?s).*catalog.php\\?book=(?s).*" ) ) {
            System.out.println( "需要處理" );
            url = url.replace( "catalog.php?book=", "index_" ) + ".html";

            return url;
        }
        else {
            return url;
        }
    }


    @Override
    public String getTitleOnMainPage( String urlString, String allPageString ) {
        int beginIndex, endIndex;
        String title = "";

        beginIndex = allPageString.indexOf( "<h1>" ) + 4;
        endIndex = allPageString.indexOf( "</h1>", beginIndex );
        title = allPageString.substring( beginIndex, endIndex ).trim();
        title = title.replace( "：", "_" ); // skydrive不支援「：」....

        return Common.getStringRemovedIllegalChar( Common.getTraditionalChinese( title ) );
    }

    @Override
    public List<List<String>> getVolumeTitleAndUrlOnMainPage( String urlString, String allPageString ) {
        // combine volumeList and urlList into combinationList, return it.

        List<List<String>> combinationList = new ArrayList<List<String>>();
        List<String> urlList = new ArrayList<String>();
        List<String> volumeList = new ArrayList<String>();
        
        urlString = getRegularURL( urlString ); // 取得正規網址

        String volumeTitle = "";
        String volumeURL = "";

        // 取得單集名稱
        volumeTitle = getTitle();
        volumeList.add( Common.getStringRemovedIllegalChar( Common.getTraditionalChinese( volumeTitle.trim() ) ) );

        // 取得單集位址
        urlList.add( urlString );

        totalVolume = 1;
        Common.debugPrintln( "共有" + totalVolume + "集" );

        combinationList.add( volumeList );
        combinationList.add( urlList );

        return combinationList;
    }
}
