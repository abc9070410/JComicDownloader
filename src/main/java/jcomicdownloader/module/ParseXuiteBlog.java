/*
----------------------------------------------------------------------------------------------------
Program Name : JComicDownloader
Authors  : surveyorK
Last Modified : 2012/5/31
----------------------------------------------------------------------------------------------------
ChangeLog:
 *  4.05: 1. 新增對blog.xuite.net的支援。
----------------------------------------------------------------------------------------------------
 */
package jcomicdownloader.module;

import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import jcomicdownloader.tools.*;
import jcomicdownloader.enums.*;
import java.util.*;
import jcomicdownloader.SetUp;

public class ParseXuiteBlog extends ParsePixnetBlog {

    private int radixNumber; // use to figure out the name of pic
    private String jsName;
    protected String indexName;
    protected String indexEncodeName;
    protected String baseURL;
    protected int floorCountInOnePage; // 一頁有幾層樓

    /**
     *
     * @author user
     */
    public ParseXuiteBlog() {
        siteID = Site.XUITE_BLOG;
        siteName = "Xuite Blog";
        indexName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_xuite_parse_", "html" );
        indexEncodeName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_xuite_encode_parse_", "html" );

        jsName = "index_xuite.js";
        radixNumber = 1513961; // default value, not always be useful!!

        baseURL = "http://blog.xuite.net"; // 在之後會被換掉

        floorCountInOnePage = 10; // 一頁有幾層樓
    }

    @Override
    public void parseComicURL() { // parse URL and save all URLs in comicURL  //
        // 先取得前面的下載伺服器網址

        Common.downloadFile( webSite, SetUp.getTempDirectory(), indexName, false, "" );
        String allPageString = Common.getFileString( SetUp.getTempDirectory(), indexName );
        Common.debugPrint( "開始解析這一集有幾頁 : " );

        int beginIndex = 0, endIndex = 0;
        String tempString = "";
        boolean realizeAmountOfPage = false;
        int firstPageCount = 0;

        endIndex = getWholeTitle().lastIndexOf( ")" );
        beginIndex = getWholeTitle().lastIndexOf( "(", endIndex ) + 1;
        
        if ( beginIndex > 0 && endIndex > 0 && beginIndex != endIndex ) {
            tempString = getWholeTitle().substring( beginIndex, endIndex );
            totalPage = Integer.parseInt( tempString );
            
            realizeAmountOfPage = true; // 知道有幾頁
        }
        else { 
            Common.debugPrintln( "標籤無顯示數量！！" );
            totalPage =  0;
            firstPageCount = allPageString.split( "class=\"titlename\"" ).length - 1;
        }

        Common.debugPrintln( "共 " + totalPage + " 頁" );
        comicURL = new String[totalPage];
        //String[] titles = new String[totalPage];
        List<String> titleList = new ArrayList<String>();

        NumberFormat formatter = new DecimalFormat( Common.getZero() );

        if ( webSite.matches( "(?s).*/" ) ) {
            webSite = webSite.substring( 0, webSite.length() - 1 );
        }
        
        String pageURL = webSite; // 分類頁面的網址
        String articleURL = ""; // 單篇文章網址
        String articleTitle = ""; // 單篇文章標題
        int classPage = 1; // 分類頁面
        
        for ( int p = 0 ; p < totalPage + firstPageCount && Run.isAlive ; ) {
            allPageString = getAllPageString( pageURL );
            int nowPageCount = allPageString.split( "class=\"titlename\"" ).length - 1;
            
            if ( firstPageCount != 0 ) // 不知道全部頁面數量才用此招
                totalPage += nowPageCount;
            
            beginIndex = endIndex = 0;
            for ( int j = 0 ; j < nowPageCount && p < totalPage && Run.isAlive ; j++ ) {
                // 取得單篇文章網址
                beginIndex = allPageString.indexOf( "class=\"titlename\"", beginIndex );
                beginIndex = allPageString.indexOf( " href=", beginIndex );
                beginIndex = allPageString.indexOf( "\"", beginIndex ) + 1;
                endIndex = allPageString.indexOf( "\"", beginIndex );
                articleURL = allPageString.substring( beginIndex, endIndex );

                // 取得此單篇文章的標題
                beginIndex = allPageString.indexOf( ">", beginIndex ) + 1;
                endIndex = allPageString.indexOf( "</a>", beginIndex );
                articleTitle = allPageString.substring( beginIndex, endIndex );
                articleTitle = Common.getStringRemovedIllegalChar( 
                        Common.getTraditionalChinese( articleTitle ) ); // 轉為繁體字

                titleList.add( articleTitle + "." + Common.getDefaultTextExtension() );
                
                Common.debugPrint( (p + 1) + " " + titleList.get( p ) + "\t" ); // debug

                // 每解析一個網址就下載一張圖
                if ( !new File( getDownloadDirectory() + titleList.get( p ) ).exists() ) {
                    singlePageDownload( getTitle(), getWholeTitle(), articleURL, totalPage, p + 1, 0 );
                    String fileName = formatter.format( p + 1 ) + ".html";
                    hadleSingleNovel( fileName, titleList.get( p ), articleURL );  // 處理小說主函式
                }
                else {
                    Common.debugPrintln( titleList.get( p ) + "已下載，跳過" );
                }

                p++;
            }

            // 取得下一頁的網址
            pageURL = webSite + "&p=" + ( ++ classPage );
             Common.debugPrintln( "下一頁面網址：" + pageURL );
        }

        //System.exit( 0 ); // debug
    }

    // 處理小說主函式
    public void hadleSingleNovel( String fileName, String title, String url ) {
        String allPageString = "";
        String allNovelText = getInformation( title, url ); // 全部頁面加起來小說文字
        String tempText = "";

        allPageString = Common.getFileString( getDownloadDirectory(), fileName );
        Common.deleteFile( getDownloadDirectory(), fileName ); // 刪掉utf8編碼的暫存檔

        tempText = getRegularNovel( allPageString ); // 本文作正規處理
        
        if ( SetUp.getDefaultTextOutputFormat() == FileFormatEnum.HTML_WITH_PIC ) {
            tempText = hadlePicInHtml( tempText, title ); //  看是否對於圖片進行處理(下載)
        }
        
        allNovelText += Common.getTraditionalChinese( tempText ); // 轉為繁體字
        Common.outputFile( allNovelText, getDownloadDirectory(), title );
    }

    // 處理小說網頁，將標籤去除
    public String getRegularNovel( String allPageString ) {
        int beginIndex = 0;
        int endIndex = 0;
        int amountOfFloor = 10; // 一頁有幾樓
        String oneFloorText = ""; // 單一樓層的文字
        String allFloorText = ""; // 所有樓層的文字加總
        beginIndex = endIndex;
        beginIndex = allPageString.indexOf( "<h3 class=\"title\">", beginIndex );
        if ( beginIndex < 0 ) {
            beginIndex = allPageString.indexOf( "<div class=\"titlename\">", beginIndex );
        }
        endIndex = allPageString.indexOf( "<div class=\"rank\">", beginIndex );
        if ( endIndex < 0 ) {
            endIndex = allPageString.indexOf( "<div id=\"facebook-like-bottom\" >", beginIndex );
        }
        
        oneFloorText = allPageString.substring( beginIndex, endIndex );

        if ( SetUp.getDefaultTextOutputFormat() == FileFormatEnum.TEXT ) {
            oneFloorText = replaceProcessToText( oneFloorText );
        } else {
            oneFloorText = replaceProcessToHtml( oneFloorText );
        }

        return oneFloorText;
    }

    @Override // 因為原檔就是utf8了，所以無須轉碼
    public String getAllPageString( String urlString ) {
        String indexName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_xuite_", "html" );
        Common.downloadFile( urlString, SetUp.getTempDirectory(), indexName, false, "" );

        return Common.getFileString( SetUp.getTempDirectory(), indexName );
    }

    @Override
    public String getTitleOnMainPage( String urlString, String allPageString ) {
        int beginIndex, endIndex;
        beginIndex = allPageString.indexOf( "class=\"blogname\"" );
        beginIndex = allPageString.indexOf( "<", beginIndex );
        endIndex = allPageString.indexOf( "</h1>", beginIndex );

        String title = allPageString.substring( beginIndex, endIndex ).trim();
        title = Common.replaceTag(  title );

        return Common.getStringRemovedIllegalChar( title );
    }

    @Override
    public List<List<String>> getVolumeTitleAndUrlOnMainPage( String urlString, String allPageString ) {
        // combine volumeList and urlList into combinationList, return it.

        List<List<String>> combinationList = new ArrayList<List<String>>();
        List<String> urlList = new ArrayList<String>();
        List<String> volumeList = new ArrayList<String>();

        int beginIndex = 0;
        int endIndex = 0;
        String tempString = "";
        String volumeTitle = "";
        
        endIndex = Common.getIndexOfOrderKeyword( urlString, "/", 3 );
        baseURL = urlString.substring( 0, endIndex ); // 取得基本頁面位址

        beginIndex = allPageString.indexOf( "class=\"categoryTitle\"" );
        if ( beginIndex > 0 ) { // 代表有標籤分類
            Common.debugPrintln( "有標籤分類" );
            beginIndex = allPageString.indexOf( "label:", beginIndex );
            endIndex = allPageString.indexOf( "</script>", beginIndex );
            tempString = allPageString.substring( beginIndex, endIndex );
            int labelCount = tempString.split( " label:" ).length - 1; //計算有幾種標籤

            beginIndex = endIndex = 0;
            for ( int i = 0 ; i < labelCount ; i++ ) {
                // 取得單集名稱
                beginIndex = tempString.indexOf( " label:", beginIndex );
                beginIndex = tempString.indexOf( "\"", beginIndex ) + 1;
                endIndex = tempString.indexOf( "\"", beginIndex );
                volumeTitle = tempString.substring( beginIndex, endIndex );
                volumeTitle = Common.replaceTag(  volumeTitle );
                volumeList.add( Common.getStringRemovedIllegalChar( volumeTitle.trim() ) );
                
                // 取得單集位址
                beginIndex = tempString.indexOf( " href:", beginIndex );
                beginIndex = tempString.indexOf( "\"", beginIndex ) + 1;
                endIndex = tempString.indexOf( "\"", beginIndex );
                urlList.add( tempString.substring( beginIndex, endIndex ) );

                totalVolume++;
            }
        }


        Common.debugPrintln( "共有" + totalVolume + "集" );

        combinationList.add( volumeList );
        combinationList.add( urlList );

        return combinationList;
    }
}

