/*
----------------------------------------------------------------------------------------------------
Program Name : JComicDownloader
Authors  : surveyorK
Last Modified : 2012/6/2
----------------------------------------------------------------------------------------------------
ChangeLog:
 * 4.06: 1. 修復非php頁面無法解析的問題。
         2. 修復編碼問題，將GB2312->UTF8改為GBK->UTF8。
 * 4.03: 1. 修改Wenku模組，使其可輸出單回文字檔與合併文字檔。
 *  4.01: 1. 新增對Wenku的支援。
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

public class ParseWenku extends ParseOnlineComicSite {

    private int radixNumber; // use to figure out the name of pic
    private String jsName;
    protected String indexName;
    protected String indexEncodeName;
    protected String baseURL;
    protected int floorCountInOnePage; // 一頁有幾層樓

    /**
    
    @author user
     */
    public ParseWenku() {
        siteID = Site.WENKU;
        siteName = "wenku";
        indexName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_wenku_parse_", "html" );
        indexEncodeName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_wenku_encode_parse_", "html" );

        jsName = "index_wenku.js";
        radixNumber = 1512961; // default value, not always be useful!!

        baseURL = "http://www.wenku.com/";

        floorCountInOnePage = 10; // 一頁有幾層樓
    }

    public ParseWenku( String webSite, String titleName ) {
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
        
        webSite = getPhpURL( webSite );

        Common.downloadFile( webSite, SetUp.getTempDirectory(), indexName, false, "" );
        Common.newEncodeFile( SetUp.getTempDirectory(), indexName, indexEncodeName, Encoding.GBK );
        String allPageString = Common.getFileString( SetUp.getTempDirectory(), indexEncodeName );
        Common.debugPrint( "開始解析這一集有幾頁 : " );

        int beginIndex = 0, endIndex = 0;

        beginIndex = allPageString.indexOf( "color='#000000'" );
        beginIndex = allPageString.indexOf( "</table>", beginIndex ) + 1;
        endIndex = allPageString.indexOf( "</table>", beginIndex );
        String tempString = allPageString.substring( beginIndex, endIndex ).trim();

        totalPage = tempString.split( " href=" ).length - 1;
        Common.debugPrintln( "共 " + totalPage + " 頁" );
        comicURL = new String[totalPage];

        String[] titles = new String[totalPage];

        NumberFormat formatter = new DecimalFormat( Common.getZero() );

        // 取得漫畫位址
        beginIndex = endIndex = 0;
        String pageURL = webSite;
        for ( int i = 0 ; i < totalPage && Run.isAlive ; i++ ) {
            beginIndex = tempString.indexOf( " href=", beginIndex );
            beginIndex = tempString.indexOf( "/", beginIndex ) + 1;
            endIndex = tempString.indexOf( ">", beginIndex );
            comicURL[i] = baseURL + tempString.substring( beginIndex, endIndex ).trim();;
            //Common.debugPrintln( i + " " + comicURL[i] ); // debug
            beginIndex = endIndex + 1;
            endIndex = tempString.indexOf( "</a>", beginIndex );
            titles[i] = Common.getStringRemovedIllegalChar(
                    Common.getTraditionalChinese(
                    tempString.substring( beginIndex, endIndex ).trim() ) ) + "." + Common.getDefaultTextExtension();
            // 每解析一個網址就下載一張圖
            if ( !new File( getDownloadDirectory() + titles[i] ).exists() && Run.isAlive ) {
                singlePageDownload( getTitle(), getWholeTitle(), comicURL[i], totalPage, i + 1, 0 );
                String fileName = formatter.format( i + 1 ) + ".htm";
                hadleSingleNovel( fileName, titles[i] );  // 處理單一小說主函式
            }
            else {
                Common.debugPrintln( titles[i] + "已下載，跳過" );
            }

        }

        handleWholeNovel( titles, webSite );

        //System.exit( 0 ); // debug
    }

    // 處理全部小說的主函式
    public void handleWholeNovel( String[] titles, String url ) {
        String allNovelText = getInformation( title, url ); // 全部頁面加起來小說文字

        Common.debugPrintln( "開始執行合併程序：" );
        Common.debugPrintln( "共有" + (titles.length) + "篇" );
        for ( int i = 0 ; i < titles.length && Run.isAlive ; i++ ) {
            Common.debugPrintln( "合併第" + (i + 1) + "篇: " + titles[i] );
            allNovelText += Common.getFileString( getDownloadDirectory(), titles[i] );

            if ( SetUp.getDefaultTextOutputFormat() == FileFormatEnum.HTML_WITHOUT_PIC ||
                 SetUp.getDefaultTextOutputFormat() == FileFormatEnum.HTML_WITH_PIC ) {
                allNovelText += "<br>" + (i + 1) + "<br><hr><br>"; // 每一樓的文字加總起來
            }
            else {
                allNovelText += "\n\n---------------------------" + (i + 1) + "\n\n";
            }
            ComicDownGUI.stateBar.setText( getTitle()
                    + "合併中: " + (i + 1) + " / " + titles.length );
        }

        String tempString = getDownloadDirectory();
        tempString = tempString.substring( 0, tempString.length() - 1 );
        int endIndex = tempString.lastIndexOf( Common.getSlash() ) + 1;

        String textOutputDirectory = tempString.substring( 0, endIndex ); // 放在外面

        Common.outputFile( allNovelText, textOutputDirectory, getWholeTitle() + "." + Common.getDefaultTextExtension() );

        textFilePath = textOutputDirectory + getWholeTitle() + "." + Common.getDefaultTextExtension();
    }

    // 處理單一本小說主函式
    public void hadleSingleNovel( String fileName, String title ) {
        String allPageString = "";
        String allNovelText = ""; // 全部頁面加起來小說文字

        Common.newEncodeFile( getDownloadDirectory(),
                fileName, "utf8_" + fileName, Encoding.GBK );

        allPageString = Common.getFileString( getDownloadDirectory(), "utf8_" + fileName );
        
        Common.deleteFile( getDownloadDirectory(), fileName ); // 刪掉utf8編碼的暫存檔
        Common.deleteFile( getDownloadDirectory(), "utf8_" + fileName ); // 刪掉utf8編碼的暫存檔
        
        if ( SetUp.getDefaultTextOutputFormat() == FileFormatEnum.HTML_WITHOUT_PIC ||
             SetUp.getDefaultTextOutputFormat() == FileFormatEnum.HTML_WITH_PIC ) {
            allNovelText = getCharsetInformation();
        }
        
        allNovelText += getRegularNovel( allPageString ); // 每一頁處理過都加總起來 
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
        beginIndex = allPageString.indexOf( "<span class='max'>", beginIndex );
        beginIndex = allPageString.indexOf( ">", beginIndex ) + 1;
        endIndex = allPageString.indexOf( "<div align='center'>", beginIndex );
        oneFloorText = allPageString.substring( beginIndex, endIndex );

        if ( SetUp.getDefaultTextOutputFormat() == FileFormatEnum.TEXT ) {
            oneFloorText = replaceProcessToText( oneFloorText );
        } else {
            oneFloorText = replaceProcessToHtml( oneFloorText );
        }
        oneFloorText = Common.getTraditionalChinese( oneFloorText ); // 簡轉繁


        return oneFloorText;
    }

    @Override // 因為原檔就是utf8了，所以無須轉碼
    public String getAllPageString( String urlString ) {
        urlString = getPhpURL( urlString ); // 若是一般htm網頁，轉為php網頁

        String indexName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_wenku_", "html" );
        String indexEncodeName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_wenku_encode_", "html" );

        Common.downloadFile( urlString, SetUp.getTempDirectory(), indexName, false, "" );
        // 雖然網頁註明GB2312，但實質上是GBK編碼
        Common.newEncodeFile( SetUp.getTempDirectory(), indexName, indexEncodeName, Encoding.GBK );

        return Common.getFileString( SetUp.getTempDirectory(), indexEncodeName );
    }

    // index.htm
    public String getPhpURL( String url ) {
        if ( url.matches( "(?s).*index.htm" ) ) {
            int beginIndex = url.indexOf( "index.htm" );
            int endIndex = url.lastIndexOf( "/", beginIndex );
            beginIndex = url.lastIndexOf( "/", endIndex - 1 ) + 1;
            String num = url.substring( beginIndex, endIndex );

            url = "http://www.wenku.com/articleinfo.php?id=" + num;
            Common.debugPrintln( "網址轉為：" + url );
        }

        return url;
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
    public String getTitleOnMainPage( String urlString, String allPageString ) {
        int beginIndex, endIndex;
        String title = "";

        if ( urlString.matches( "(?s).*bookroom.php(?s).*" ) ) { // 搜尋頁或分類頁面
            endIndex = allPageString.lastIndexOf( "->" );
            beginIndex = allPageString.lastIndexOf( ">", endIndex ) + 1;
            title = allPageString.substring( beginIndex, endIndex ).trim();

            endIndex = allPageString.indexOf( "</b>/<b>", beginIndex );
            beginIndex = allPageString.lastIndexOf( "<b>", endIndex ) + 3;

            title += "第" + allPageString.substring( beginIndex, endIndex ).trim() + "頁";
        }
        else {

            beginIndex = allPageString.indexOf( " -> " );
            beginIndex = allPageString.indexOf( "<font", beginIndex );
            beginIndex = allPageString.indexOf( ">", beginIndex ) + 1;
            endIndex = allPageString.indexOf( "<", beginIndex );
            title = allPageString.substring( beginIndex, endIndex ).trim();

            beginIndex = allPageString.indexOf( "<font", beginIndex );
            beginIndex = allPageString.indexOf( ">", beginIndex ) + 1;
            endIndex = allPageString.indexOf( "<", beginIndex );
            title += "-" + allPageString.substring( beginIndex, endIndex ).trim();
            /*
            else { // 第二種小說頁面
            beginIndex = allPageString.indexOf( "<title>" );
            beginIndex = allPageString.indexOf( ">", beginIndex ) + 1;
            endIndex = allPageString.indexOf( "--", beginIndex );
            title = allPageString.substring( beginIndex, endIndex ).trim();
            }
             * 
             */
        }

        return Common.getStringRemovedIllegalChar(
                Common.getTraditionalChinese( title ) );
    }

    @Override
    public List<List<String>> getVolumeTitleAndUrlOnMainPage( String urlString, String allPageString ) {
        // combine volumeList and urlList into combinationList, return it.

        List<List<String>> combinationList = new ArrayList<List<String>>();
        List<String> urlList = new ArrayList<String>();
        List<String> volumeList = new ArrayList<String>();

        String volumeTitle = "";
        String volumeURL = "";

        if ( urlString.matches( "(?s).*bookroom.php(?s).*" ) ) { // 搜尋頁或分類頁面
            int beginIndex = allPageString.lastIndexOf( "->" );
            int endIndex = allPageString.indexOf( "name='frmjumppage'", beginIndex );
            String tempString = allPageString.substring( beginIndex, endIndex );

            int volumeCount = tempString.split( "href='articleinfo" ).length - 1;

            beginIndex = endIndex = 0;
            for ( int i = 0 ; i < volumeCount ; i++ ) {
                // 取得單集位址
                beginIndex = tempString.indexOf( "href='articleinfo", beginIndex );
                beginIndex = tempString.indexOf( "'", beginIndex ) + 1;
                endIndex = tempString.indexOf( "'", beginIndex );
                volumeURL = baseURL + tempString.substring( beginIndex, endIndex );
                urlList.add( volumeURL );

                // 取得單集名稱
                beginIndex = tempString.indexOf( ">", beginIndex ) + 1;
                endIndex = tempString.indexOf( "<", beginIndex );
                volumeTitle = tempString.substring( beginIndex, endIndex ).trim();
                volumeList.add( Common.getStringRemovedIllegalChar(
                        Common.getTraditionalChinese( volumeTitle.trim() ) ) );
            }

            totalVolume = volumeCount;
        }
        else {
            // 取得單集名稱
            volumeTitle = getTitle();
            volumeList.add( Common.getStringRemovedIllegalChar(
                    Common.getTraditionalChinese( volumeTitle.trim() ) ) );

            // 取得單集位址
            urlList.add( urlString );

            totalVolume = 1;
        }
        Common.debugPrintln( "共有" + totalVolume + "集" );

        combinationList.add( volumeList );
        combinationList.add( urlList );

        return combinationList;
    }

}
