/*
----------------------------------------------------------------------------------------------------
Program Name : JComicDownloader
Authors  : surveyorK
Last Modified : 2012/5/26
----------------------------------------------------------------------------------------------------
ChangeLog:
 *  4.03: 1. 新增對blogspot.com的支援。
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

public class ParseBlogspot extends ParseOnlineComicSite {

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
    public ParseBlogspot() {
        siteID = Site.BLOGSPOT;
        siteName = "BlogspotBlog";
        indexName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_blogspot_parse_", "html" );
        indexEncodeName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_blogspot_encode_parse_", "html" );

        jsName = "index_blogspot.js";
        radixNumber = 151261; // default value, not always be useful!!

        baseURL = "http://blogspot.com";

        floorCountInOnePage = 10; // 一頁有幾層樓
    }

    public ParseBlogspot( String webSite, String titleName ) {
        this();
        this.webSite = webSite;
        this.title = titleName;
    }

    @Override
    public void setParameters() {
        Common.debugPrintln( "開始解析各參數 :" );


        Common.debugPrintln( "作品名稱(title) : " + getTitle() );
        Common.debugPrintln( "章節名稱(wholeTitle) : " + getWholeTitle() );


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

        beginIndex = getWholeTitle().indexOf( "(" ) + 1;
        endIndex = getWholeTitle().indexOf( ")", beginIndex );
        
        if ( beginIndex > 0 && endIndex > 0 ) {
            tempString = getWholeTitle().substring( beginIndex, endIndex );
            totalPage = Integer.parseInt( tempString );
            
            realizeAmountOfPage = true; // 知道有幾頁
        }
        else { // 不知道有幾頁，只能一步步往後掃描
            Common.debugPrintln( "標籤無顯示數量，不知道總共幾頁，需一步步往後掃描" );
            totalPage =  0;
            firstPageCount = allPageString.split( "class='post-title entry-title'" ).length - 1;
        }

        Common.debugPrintln( "共 " + totalPage + " 頁" );
        comicURL = new String[totalPage];
        //String[] titles = new String[totalPage];
        List<String> titleList = new ArrayList<String>();

        NumberFormat formatter = new DecimalFormat( Common.getZero() );

        String pageURL = webSite; // 分類頁面的網址
        String articleURL = ""; // 單篇文章網址
        String articleTitle = ""; // 單篇文章標題
        
        for ( int p = 0 ; p < totalPage + firstPageCount && Run.isAlive ; ) {
            allPageString = getAllPageString( pageURL );
            int nowPageCount = allPageString.split( "class='post-title" ).length - 1;
            totalPage += nowPageCount;
            
            beginIndex = endIndex = 0;
            for ( int j = 0 ; j < nowPageCount && p < totalPage && Run.isAlive ; j++ ) {
                // 取得單篇文章網址
                beginIndex = allPageString.indexOf( "class='post-title", beginIndex );
                beginIndex = allPageString.indexOf( " href=", beginIndex );
                beginIndex = allPageString.indexOf( "'", beginIndex ) + 1;
                endIndex = allPageString.indexOf( "'", beginIndex );
                articleURL = allPageString.substring( beginIndex, endIndex );
                if ( !articleURL.matches( "(?s).*\\.html" ) ) { // 代表連結不是連向文章
                    int newBeginIndex = beginIndex;
                    endIndex = allPageString.indexOf( "rel='bookmark'", newBeginIndex );
                    newBeginIndex = allPageString.lastIndexOf( "href='", endIndex );
                    newBeginIndex = allPageString.indexOf( "'", newBeginIndex ) + 1;
                    endIndex = allPageString.indexOf( "'", newBeginIndex );

                    if ( beginIndex > 0 && endIndex > 0 ) {
                        articleURL = allPageString.substring( newBeginIndex, endIndex );
                    }
                }

                //comicURL[p] = articleURL;
                //Common.debugPrint( ( p + 1 ) + " " + comicURL[p] ); // debug

                // 取得此單篇文章的標題
                beginIndex = allPageString.indexOf( ">", beginIndex ) + 1;
                endIndex = allPageString.indexOf( "</a>", beginIndex );
                articleTitle = allPageString.substring( beginIndex, endIndex );
                articleTitle = replaceProcessToText( articleTitle ); // 拿掉html編碼的字元
                articleTitle = Common.getStringRemovedIllegalChar( 
                        Common.getTraditionalChinese( articleTitle ) ); // 轉為繁體字

                titleList.add( articleTitle + "." + Common.getDefaultTextExtension() );
                
                Common.debugPrint( (p + 1) + " " + titleList.get( p ) ); // debug

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
            beginIndex = allPageString.indexOf( "class='blog-pager-older-link'" );
            beginIndex = allPageString.indexOf( " href=", beginIndex );
            beginIndex = allPageString.indexOf( "'", beginIndex ) + 1;
            endIndex = allPageString.indexOf( "'", beginIndex );

            if ( beginIndex < 0 || endIndex < 0 ) {
                break;
            }

            pageURL = allPageString.substring( beginIndex, endIndex );

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
        beginIndex = allPageString.indexOf( "<div class='post-header-line-1'>", beginIndex );
        if ( beginIndex < 0 ) {
            beginIndex = allPageString.indexOf( "<div id='post-inner'>", beginIndex );
        }
        endIndex = allPageString.indexOf( "<div class='post-footer'>", beginIndex );
        
        oneFloorText = allPageString.substring( beginIndex, endIndex );

        if ( SetUp.getDefaultTextOutputFormat() == FileFormatEnum.TEXT ) {
            oneFloorText = replaceProcessToText( oneFloorText );
        } else {
            oneFloorText = replaceProcessToHtml( oneFloorText );
        }

        return oneFloorText;
    }

    public void showParameters() { // for debug
        Common.debugPrintln( "----------" );
        Common.debugPrintln( "totalPage = " + totalPage );
        Common.debugPrintln( "webSite = " + webSite );
        Common.debugPrintln( "----------" );
    }

    @Override // 因為原檔就是utf8了，所以無須轉碼
    public String getAllPageString( String urlString ) {
        String indexName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_blogspot_", "html" );
        Common.downloadFile( urlString, SetUp.getTempDirectory(), indexName, false, "" );

        return Common.getFileString( SetUp.getTempDirectory(), indexName );
    }

    @Override
    public boolean isSingleVolumePage( String urlString ) {
        // ex. http://ck101.com/thread-2081113-1-1.html
        // 都判斷為主頁，並直接下載。
        return false;
    }

    public String getMainUrlFromSingleVolumeUrl( String volumeURL ) {
        return volumeURL;
    }

    @Override
    public String getTitleOnSingleVolumePage( String urlString ) {
        String mainUrlString = getMainUrlFromSingleVolumeUrl( urlString );

        return getTitleOnMainPage( mainUrlString, getAllPageString( mainUrlString ) );
    }

    @Override
    public String getTitleOnMainPage( String urlString, String allPageString ) {
        int beginIndex, endIndex;
        beginIndex = allPageString.indexOf( "title=\"" );
        beginIndex = allPageString.indexOf( "\"", beginIndex ) + 1;
        endIndex = allPageString.indexOf( "- Atom" );

        String title = allPageString.substring( beginIndex, endIndex ).trim();
        title = title.replaceAll( "</a>", "" );
        title = title.replaceAll( "<a href=.*>", "" );

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

        beginIndex = allPageString.indexOf( "id='Label" );
        if ( beginIndex > 0 ) { // 代表有標籤分類
            Common.debugPrintln( "有標籤分類" );

            endIndex = allPageString.indexOf( "class='clear'", beginIndex );
            tempString = allPageString.substring( beginIndex, endIndex );
            int labelCount = tempString.split( " href=" ).length - 1; //計算有幾種標籤

            beginIndex = endIndex = 0;
            for ( int i = 0 ; i < labelCount ; i++ ) {
                // 取得單集位址
                beginIndex = tempString.indexOf( " href=", beginIndex );
                beginIndex = tempString.indexOf( "'", beginIndex ) + 1;
                endIndex = tempString.indexOf( "'", beginIndex );
                urlList.add( tempString.substring( beginIndex, endIndex ) );

                // 取得單集名稱
                beginIndex = tempString.indexOf( ">", beginIndex ) + 1;
                endIndex = tempString.indexOf( "</a>", beginIndex );
                volumeTitle = tempString.substring( beginIndex, endIndex );
                System.out.println( volumeTitle );

                // 取得此標籤有幾篇文章
                int tempBeginIndex = tempString.indexOf( "dir='ltr'", beginIndex );
                tempBeginIndex = tempString.indexOf( ">", tempBeginIndex ) + 1;
                int tempEndIndex = tempString.indexOf( "</span>", tempBeginIndex );
                if ( tempBeginIndex > 0 && tempEndIndex > 0 ) {
                    volumeTitle += tempString.substring( tempBeginIndex, tempEndIndex );
                    
                    beginIndex = tempBeginIndex;
                }
                System.out.println( volumeTitle );

                volumeTitle = replaceProcessToText( volumeTitle ); // 拿掉html編碼的字元
                volumeTitle = Common.getTraditionalChinese( volumeTitle );

                volumeList.add( Common.getStringRemovedIllegalChar( volumeTitle.trim() ) );

                totalVolume++;
            }
        }

        beginIndex = allPageString.indexOf( "id='BlogArchive1'" );
        if ( beginIndex > 0 ) { // 代表有日期存檔
            Common.debugPrintln( "存檔頁面的第一種解析模式" );
            Common.debugPrintln( "有月份分類" );

            endIndex = allPageString.indexOf( "class='clear'", beginIndex );
            tempString = allPageString.substring( beginIndex, endIndex );

            int yearCount = tempString.split( "search\\?updated" ).length - 1; // 計算看看有幾年
            Common.debugPrintln( "共" + yearCount + "年" );
            beginIndex = endIndex = 0;
            String yearString = "";
            for ( int i = 0 ; i < yearCount ; i++ ) {
                beginIndex = tempString.indexOf( "search?updated", beginIndex ) + 1;
                beginIndex = tempString.indexOf( ">", beginIndex ) + 1;
                endIndex = tempString.indexOf( "</a>", beginIndex );
                yearString = tempString.substring( beginIndex, endIndex ); // 取得年份名稱
                endIndex = tempString.indexOf( "search?updated", beginIndex );
                if ( endIndex < 0 ) {
                    endIndex = tempString.length(); // 代表到最後一個了。
                }

                String tempString2 = tempString.substring( beginIndex, endIndex );
                int archiveCount = tempString2.split( "archive\\.html" ).length - 1; //計算這一年有幾月
                Common.debugPrintln( yearString + "中共有" + archiveCount + "個月份有發表文章" );

                int beginIndex2 = 0;
                int endIndex2 = 0;
                for ( int j = 0 ; j < archiveCount ; j++ ) {

                    // 取得單集位址
                    beginIndex2 = tempString2.indexOf( "archive.html", beginIndex2 );
                    beginIndex2 = tempString2.lastIndexOf( "'", beginIndex2 ) + 1;
                    endIndex2 = tempString2.indexOf( "'", beginIndex2 );
                    urlList.add( tempString2.substring( beginIndex2, endIndex2 ) );
                    //Common.debugPrint( tempString2.substring( beginIndex2, endIndex2 ) + " : " );
                    
                    // 取得單集名稱
                    beginIndex2 = tempString2.indexOf( ">", beginIndex2 ) + 1;
                    endIndex2 = tempString2.indexOf( "</a>", beginIndex2 );
                    volumeTitle = yearString + "_" + tempString2.substring( beginIndex2, endIndex2 );
                    //Common.debugPrintln( volumeTitle );

                    // 取得此標籤有幾篇文章
                    beginIndex2 = tempString2.indexOf( "dir='ltr'", beginIndex2 );
                    beginIndex2 = tempString2.indexOf( ">", beginIndex2 ) + 1;
                    endIndex2 = tempString2.indexOf( "</span>", beginIndex2 );
                    if ( beginIndex2 > 0 && endIndex2 > 0 ) {
                        volumeTitle += tempString2.substring( beginIndex2, endIndex2 );
                    }

                    volumeList.add( Common.getStringRemovedIllegalChar( volumeTitle.trim() ) );

                    totalVolume++;
                }
            }
        }
        else if ( allPageString.indexOf( "class=\"archive-list\"" ) > 0 ) { // 另一種可能，直接顯示月份存檔
            Common.debugPrintln( "存檔頁面的第二種解析模式" );
            beginIndex = allPageString.indexOf( "class=\"archive-list\"" );
            tempString = allPageString.substring( beginIndex, allPageString.length() );
            int archiveCount = tempString.split( "archive\\.html" ).length - 1; //計算這一年有幾月
            Common.debugPrintln( "共有" + archiveCount + "個月份有發表文章" );
            
            beginIndex = endIndex = 0;
            for ( int i = 0 ; i < archiveCount ; i++ ) {
                // 取得單集位址
                    beginIndex = tempString.indexOf( "archive.html", beginIndex );
                    beginIndex = tempString.lastIndexOf( "\"", beginIndex ) + 1;
                    endIndex = tempString.indexOf( "\"", beginIndex );
                    urlList.add( tempString.substring( beginIndex, endIndex ) );

                    // 取得單集名稱
                    beginIndex = tempString.indexOf( ">", beginIndex ) + 1;
                    endIndex = tempString.indexOf( "</a>", beginIndex );
                    volumeTitle = tempString.substring( beginIndex, endIndex );
                    
                    volumeList.add( Common.getStringRemovedIllegalChar( volumeTitle.trim() ) );
                    totalVolume++;
            }
            
        }

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
