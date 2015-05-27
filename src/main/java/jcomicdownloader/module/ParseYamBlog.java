/*
----------------------------------------------------------------------------------------------------
Program Name : JComicDownloader
Authors  : surveyorK
Last Modified : 2013/4/4
----------------------------------------------------------------------------------------------------
ChangeLog:
* 5.16: 1. 修復blog.yam.com圖片無法下載的問題。
 *  4.11: 1. 修復blog.yam.com無法取得全部標籤的問題。
 *           2. 修復blog.yam.com沒有標示日期的問題。
 *  4.05: 1. 新增對blog.yam.com的支援。
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

public class ParseYamBlog extends ParsePixnetBlog {

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
    public ParseYamBlog() {
        siteID = Site.YAM_BLOG;
        siteName = "Yam Blog";
        indexName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_yam_parse_", "html" );
        indexEncodeName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_yam_encode_parse_", "html" );

        jsName = "index_yam.js";
        radixNumber = 155961; // default value, not always be useful!!

        baseURL = "http://blog.yam.com"; // 在之後會被換掉

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
            firstPageCount = allPageString.split( "class=\"post_title\"" ).length - 1;
        }

        Common.debugPrintln( "共 " + totalPage + " 頁" );
        comicURL = new String[totalPage];
        //String[] titles = new String[totalPage];
        List<String> titleList = new ArrayList<String>();

        NumberFormat formatter = new DecimalFormat( Common.getZero() );

        if ( !webSite.matches( "(?s).*/" ) ) {
            webSite += "/";
        }
        
        String pageURL = webSite; // 分類頁面的網址
        String articleURL = ""; // 單篇文章網址
        String articleTitle = ""; // 單篇文章標題
        int classPage = 1; // 分類頁面
        
        String titleKeyword = "class=\"post_title\"";
        
        for ( int p = 0 ; p < totalPage + firstPageCount && Run.isAlive ; ) {
            allPageString = getAllPageString( pageURL );
            int nowPageCount = allPageString.split( titleKeyword ).length - 1;
            if ( nowPageCount <= 0 ) {
                titleKeyword = "class=\"articleTitle\"";
                nowPageCount = allPageString.split( titleKeyword ).length - 1;
            }
            Common.debugPrintln( "此頁面共有" + nowPageCount + "篇文章" );
            
            if ( firstPageCount != 0 ) // 不知道全部頁面數量才用此招
                totalPage += nowPageCount;
            
            beginIndex = endIndex = 0;
            for ( int j = 0 ; j < nowPageCount && p < totalPage && Run.isAlive ; j++ ) {
                // 取得單篇文章網址
                beginIndex = allPageString.indexOf( titleKeyword, beginIndex );
                beginIndex = allPageString.indexOf( "href=", beginIndex );
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
            pageURL = webSite + "page=" + ( ++ classPage );
             Common.debugPrintln( "此頁已經處理完畢，換下一頁面網址：" + pageURL );
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
            Common.debugPrintln( "REFER: " + url );
            tempText = hadlePicInHtml( tempText, title, url ); //  看是否對於圖片進行處理(下載)
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
        beginIndex = allPageString.indexOf( "<div class=\"articleTitleDiv\">", beginIndex );
        if ( beginIndex < 0 ) {
            beginIndex = allPageString.indexOf( "<div class=\"post_body\"", 0 );
            if ( beginIndex < 0 ) {
                beginIndex = allPageString.indexOf( "<div class=\"post\"", 0 );
            }
        }

        endIndex = allPageString.indexOf( "<style type='text/css'>", beginIndex );
        
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
        String indexName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_yam_", "html" );
        Common.downloadFile( urlString, SetUp.getTempDirectory(), indexName, false, "" );

        return Common.getFileString( SetUp.getTempDirectory(), indexName );
    }

    @Override
    public String getTitleOnMainPage( String urlString, String allPageString ) {
        int beginIndex, endIndex;
        beginIndex = allPageString.indexOf( " title=" );
        beginIndex = allPageString.indexOf( "\"", beginIndex ) + 1;
        endIndex = allPageString.indexOf( "\"", beginIndex );

        String title = allPageString.substring( beginIndex, endIndex ).trim();
        if ( title.matches( "BLOG:(?s).*") ) {
            title = title.substring( 5, title.length() );
        }
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

        beginIndex = allPageString.indexOf( "class=\"filetree\"" );
        if ( beginIndex > 0 ) { // 代表有標籤分類
            Common.debugPrintln( "有標籤分類" );
            endIndex = allPageString.lastIndexOf( "/category/" );
            endIndex = allPageString.indexOf( "</ul>", endIndex );
            tempString = allPageString.substring( beginIndex, endIndex );
            int labelCount = tempString.split( "class=\"file\"" ).length - 1; //計算有幾種標籤

            beginIndex = endIndex = 0;
            for ( int i = 0 ; i < labelCount ; i++ ) {
                // 取得單集位址
                beginIndex = tempString.indexOf( "class=\"file\"", beginIndex );
                beginIndex = tempString.indexOf( " href=", beginIndex );
                beginIndex = tempString.indexOf( "\"", beginIndex ) + 1;
                endIndex = tempString.indexOf( "\"", beginIndex );
                urlList.add( tempString.substring( beginIndex, endIndex ) );
                
                // 取得單集名稱
                beginIndex = tempString.indexOf( ">", beginIndex ) + 1;
                endIndex = tempString.indexOf( "</a>", beginIndex );
                volumeTitle = tempString.substring( beginIndex, endIndex );
                volumeTitle = Common.replaceTag(  volumeTitle );
                volumeList.add( Common.getStringRemovedIllegalChar( volumeTitle.trim() ) );

                totalVolume++;
            }
        }


        Common.debugPrintln( "共有" + totalVolume + "集" );

        combinationList.add( volumeList );
        combinationList.add( urlList );

        return combinationList;
    }
}

