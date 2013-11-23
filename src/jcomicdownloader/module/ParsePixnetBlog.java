/*
----------------------------------------------------------------------------------------------------
Program Name : JComicDownloader
Authors  : surveyorK
Last Modified : 2012/5/29
----------------------------------------------------------------------------------------------------
ChangeLog:
 *  4.11: 1. 修復pixnet.net無法取得全部標籤的問題。
 *  4.04: 1. 新增對pixnet.net的支援。
----------------------------------------------------------------------------------------------------
 */
package jcomicdownloader.module;

import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import jcomicdownloader.SetUp;
import jcomicdownloader.enums.FileFormatEnum;
import jcomicdownloader.enums.Site;
import jcomicdownloader.tools.Common;

public class ParsePixnetBlog extends ParseOnlineComicSite {

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
    public ParsePixnetBlog() {
        siteID = Site.PIXNET_BLOG;
        siteName = "Pixnet Blog";
        indexName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_pixnet_parse_", "html" );
        indexEncodeName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_pixnet_encode_parse_", "html" );

        jsName = "index_pixnet.js";
        radixNumber = 15131261; // default value, not always be useful!!

        baseURL = "http://pixnet.net"; // 在之後會被換掉

        floorCountInOnePage = 10; // 一頁有幾層樓
    }

    public ParsePixnetBlog( String webSite, String titleName ) {
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
        
        if ( beginIndex > 0 && endIndex > 0 && beginIndex != endIndex ) {
            tempString = getWholeTitle().substring( beginIndex, endIndex );
            totalPage = Integer.parseInt( tempString );
            
            realizeAmountOfPage = true; // 知道有幾頁
        }
        else { 
            Common.debugPrintln( "標籤無顯示數量！！" );
            totalPage =  0;
            firstPageCount = allPageString.split( "class=\"article\"" ).length - 1;
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
            int nowPageCount = allPageString.split( "class=\"article\"" ).length - 1;
            
            if ( firstPageCount != 0 ) // 不知道全部頁面數量才用此招
                totalPage += nowPageCount;
            
            beginIndex = endIndex = 0;
            for ( int j = 0 ; j < nowPageCount && p < totalPage && Run.isAlive ; j++ ) {
                // 取得單篇文章網址
                beginIndex = allPageString.indexOf( "class=\"article\"", beginIndex );
                beginIndex = allPageString.indexOf( " href=", beginIndex );
                beginIndex = allPageString.indexOf( "\"", beginIndex ) + 1;
                endIndex = allPageString.indexOf( "\"", beginIndex );
                articleURL = allPageString.substring( beginIndex, endIndex );

                //comicURL[p] = articleURL;
                //Common.debugPrint( ( p + 1 ) + " " + comicURL[p] ); // debug

                // 取得此單篇文章的標題
                beginIndex = allPageString.indexOf( ">", beginIndex ) + 1;
                endIndex = allPageString.indexOf( "</a>", beginIndex );
                articleTitle = allPageString.substring( beginIndex, endIndex );
                //articleTitle = replaceProcessToText( articleTitle ); // 拿掉html編碼的字元
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
            endIndex = allPageString.indexOf( "class=\"next\"" );
            endIndex = allPageString.lastIndexOf( "\"", endIndex );
            beginIndex = allPageString.lastIndexOf( "\"", endIndex - 1 ) + 1;

            if ( beginIndex < 0 || endIndex < 0 ) {
                break;
            }

            pageURL = allPageString.substring( beginIndex, endIndex );

        }
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
        beginIndex = allPageString.indexOf( "<div id=\"article-box\">", beginIndex );
        if ( beginIndex < 0 ) {
            beginIndex = allPageString.indexOf( "<div class=\"article-body\">", beginIndex );
        }
        endIndex = allPageString.indexOf( "<div class=\"forward\">", beginIndex );
        if ( endIndex < 0 ) {
            endIndex = allPageString.indexOf( "<div class=\"article-footer\">", beginIndex );
        }
        
        oneFloorText = allPageString.substring( beginIndex, endIndex );

        if ( SetUp.getDefaultTextOutputFormat() == FileFormatEnum.HTML_WITHOUT_PIC ||
             SetUp.getDefaultTextOutputFormat() == FileFormatEnum.HTML_WITH_PIC ) {
            Common.debugPrintln( "輸出html格式" );
            oneFloorText = replaceProcessToHtml( oneFloorText );
        }
        else {
            Common.debugPrintln( "輸出text格式" );
            oneFloorText = replaceProcessToText( oneFloorText );
        }

        return oneFloorText;
    }

    @Override // 因為原檔就是utf8了，所以無須轉碼
    public String getAllPageString( String urlString ) {
        String indexName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_pixnet_", "html" );
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
        beginIndex = allPageString.indexOf( "rel=\"search\"" );
        beginIndex = allPageString.indexOf( "title=", beginIndex );
        beginIndex = allPageString.indexOf( "\"", beginIndex ) + 1;
        endIndex = allPageString.indexOf( "\"", beginIndex );

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
        
        endIndex = Common.getIndexOfOrderKeyword( urlString, "/", 3 );
        baseURL = urlString.substring( 0, endIndex ); // 取得基本頁面位址

        beginIndex = allPageString.indexOf( "id=\"category\"" );
        if ( beginIndex > 0 ) { // 代表有標籤分類
            Common.debugPrintln( "有標籤分類" );
            beginIndex = allPageString.indexOf( "<ul", beginIndex );
            endIndex = allPageString.indexOf( "#category", beginIndex );
            tempString = allPageString.substring( beginIndex, endIndex );
            int labelCount = tempString.split( " href=" ).length - 1; //計算有幾種標籤

            beginIndex = endIndex = 0;
            for ( int i = 0 ; i < labelCount ; i++ ) {
                // 取得單集位址
                beginIndex = tempString.indexOf( " href=", beginIndex );
                beginIndex = tempString.indexOf( "\"", beginIndex ) + 1;
                endIndex = tempString.indexOf( "\"", beginIndex );
                urlList.add( baseURL + "/" + tempString.substring( beginIndex, endIndex ) );

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
}

