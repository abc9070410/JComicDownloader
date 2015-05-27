/*
----------------------------------------------------------------------------------------------------
Program Name : JComicDownloader
Authors  : surveyorK
Last Modified : 2012/6/11
----------------------------------------------------------------------------------------------------
ChangeLog:
 *  4.09: 1. 新增對book.51cto.com的支援。
----------------------------------------------------------------------------------------------------
 */
package jcomicdownloader.module;

import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import jcomicdownloader.tools.*;
import jcomicdownloader.enums.*;
import java.util.*;
import jcomicdownloader.ComicDownGUI;
import jcomicdownloader.SetUp;
import jcomicdownloader.encode.Encoding;

public class Parse51Cto extends ParseEightNovel {

    private int radixNumber; // use to figure out the name of pic
    private String jsName;
    protected String indexName;
    protected String indexEncodeName;
    protected String baseURL;
    protected int floorCountInOnePage; // 一頁有幾層樓

    /**
    
    @author user
     */
    public Parse51Cto() {
        siteID = Site.FIVEONE_CTO;
        siteName = "51cto";
        pageExtension = "htm"; // 網頁副檔名
        pageCode = Encoding.GB2312; // 網頁預設編碼
        
        indexName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_51cto_parse_", pageExtension );
        indexEncodeName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_51cto_encode_parse_", pageExtension );

        jsName = "index_51cto.js";
        radixNumber = 15124451; // default value, not always be useful!!

        baseURL = "http://book.qq.com/s/book/";
    }


    @Override
    public void parseComicURL() { // parse URL and save all URLs in comicURL  //

        Common.downloadFile( webSite, SetUp.getTempDirectory(), indexName, false, "" );
        Common.newEncodeFile( SetUp.getTempDirectory(), indexName, indexEncodeName, pageCode );
        String allPageString = Common.getFileString( SetUp.getTempDirectory(), indexEncodeName );
        Common.debugPrint( "開始解析這一集有幾頁 : " );

        int beginIndex = 0, endIndex = 0;

        beginIndex = allPageString.indexOf( "class=\"content\"" );
        endIndex = allPageString.lastIndexOf( "id=\"getbookcomment\"" );
        String tempString = allPageString.substring( beginIndex, endIndex ).trim();
        
        totalPage = tempString.split( " href=" ).length - 1;
        Common.debugPrintln( "共 " + totalPage + " 頁" );
        comicURL = new String[totalPage];
        String[] titles = new String[totalPage];
        
         // 取得作者名稱
        beginIndex = allPageString.indexOf( "<center><span class=\"font_blue12\">" );
        beginIndex = allPageString.indexOf( "\">", beginIndex ) + 1;
        endIndex = allPageString.indexOf( "'</span>", beginIndex );
        String author = "";
        if ( beginIndex > 0 && endIndex > 0 ) {
            allPageString.substring( beginIndex, endIndex );
        }
        else {
            author = "";
        }
        Common.debugPrintln( "作者：" + author );
        
        NumberFormat formatter = new DecimalFormat( Common.getZero() );

        // 取得小說網址
        beginIndex = endIndex = 0;
        for ( int i = 0 ; i < totalPage; i++ ) {
            // 取得網址
            beginIndex = tempString.indexOf( " href=", beginIndex );
            beginIndex = tempString.indexOf( "'", beginIndex ) + 1;
            endIndex = tempString.indexOf( "'", beginIndex );
            comicURL[i] = tempString.substring( beginIndex, endIndex ).trim();

            // 取得標題
            beginIndex = tempString.indexOf( ">", beginIndex ) + 1;
            endIndex = tempString.indexOf( "</a>", beginIndex );
            titles[i] = Common.getStringRemovedIllegalChar( Common.getTraditionalChinese( 
                    tempString.substring( beginIndex, endIndex ).trim() ) ) + "." + Common.getDefaultTextExtension();
            
            //Common.debugPrintln( i + " " + titles[i] + " " + comicURL[i] ); // debug
        }
        //System.exit(  0 ); // for debug
        
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
    public String getRegularNovel( String allPageString ) {
        int beginIndex = 0;
        int endIndex = 0;
        String oneFloorText = ""; // 單一樓層（頁）的文字
        beginIndex = endIndex;
        beginIndex = allPageString.indexOf( "<div id=\"content\">", beginIndex );
        endIndex = allPageString.indexOf( "<DIV align=right>", beginIndex );
        oneFloorText = allPageString.substring( beginIndex, endIndex );

        if ( SetUp.getDefaultTextOutputFormat() == FileFormatEnum.TEXT ) {
            oneFloorText = replaceProcessToText( oneFloorText );
        } else {
            oneFloorText = replaceProcessToHtml( oneFloorText );
        }
        oneFloorText = Common.getTraditionalChinese( oneFloorText ); // 簡轉繁


        return oneFloorText;
    }

    @Override
    public String getTitleOnMainPage( String urlString, String allPageString ) {
        int beginIndex, endIndex;
        String title = "";

        beginIndex = allPageString.indexOf( "class=\"font_blue14_bold\"" );
        beginIndex = allPageString.indexOf( ">", beginIndex ) + 1;
        endIndex = allPageString.indexOf( "</span>", beginIndex );
        title = allPageString.substring( beginIndex, endIndex ).trim();

        return Common.getStringRemovedIllegalChar( Common.getTraditionalChinese( title ) );
    }

    @Override
    public List<List<String>> getVolumeTitleAndUrlOnMainPage( String urlString, String allPageString ) {
        // combine volumeList and urlList into combinationList, return it.

        List<List<String>> combinationList = new ArrayList<List<String>>();
        List<String> urlList = new ArrayList<String>();
        List<String> volumeList = new ArrayList<String>();

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

