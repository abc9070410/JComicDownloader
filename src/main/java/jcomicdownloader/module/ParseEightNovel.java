/*
 ----------------------------------------------------------------------------------------------------
 Program Name : JComicDownloader
 Authors  : surveyorK
 Last Modified : 2012/11/25
 ----------------------------------------------------------------------------------------------------
 ChangeLog:
     5.09: 修復第一小節標題位置錯誤的問題。
     5.04: 修復8novel少數下載不完全的問題。
 *  4.09: 1. 新增對8novel的支援。
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

public class ParseEightNovel extends ParseOnlineComicSite {

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
    public ParseEightNovel() {
        siteID = Site.EIGHT_NOVEL;
        siteName = "8Novel";
        pageExtension = "html"; // 網頁副檔名
        pageCode = Encoding.BIG5; // 網頁預設編碼
        indexName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_8novel_parse_", pageExtension );
        indexEncodeName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_8novel_encode_parse_", pageExtension );

        jsName = "index_8novel.js";
        radixNumber = 151251; // default value, not always be useful!!

        baseURL = "http://8novel.com";
    }

    public ParseEightNovel( String webSite, String titleName ) {
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

        Common.downloadFile( webSite, SetUp.getTempDirectory(), indexName, false, "" );
        Common.newEncodeFile( SetUp.getTempDirectory(), indexName, indexEncodeName, pageCode );
        String allPageString = Common.getFileString( SetUp.getTempDirectory(), indexEncodeName );
        Common.debugPrint( "開始解析這一集有幾頁 : " );

        int beginIndex = 0, endIndex = 0;

        beginIndex = allPageString.indexOf( "class=\"episodelist\"" );
        endIndex = allPageString.lastIndexOf( "class=\"episodelist\"" );
        endIndex = allPageString.indexOf( "</table>", endIndex );
        String tempString = allPageString.substring( beginIndex, endIndex ).trim();

        totalPage = tempString.split( " href=" ).length - 1;
        Common.debugPrintln( "共 " + totalPage + " 頁" );
        comicURL = new String[ totalPage ];
        String[] titles = new String[ totalPage ];

        // 取得作者名稱
        beginIndex = allPageString.indexOf( "作者:<" );
        beginIndex = allPageString.indexOf( ">", beginIndex ) + 1;
        endIndex = allPageString.indexOf( "<", beginIndex );
        String author = "";
        if ( beginIndex > 0 && endIndex > 0 ) {
            author = allPageString.substring( beginIndex, endIndex );
        }
        else {
            author = "";
        }
        Common.debugPrintln( "作者：" + author );

        NumberFormat formatter = new DecimalFormat( Common.getZero() );

        // 取得小說網址
        beginIndex = endIndex = 0;
        for ( int i = 0; i < totalPage; i++ ) {
            // 取得網址
            beginIndex = tempString.indexOf( " href=", beginIndex );
            beginIndex = tempString.indexOf( "'", beginIndex ) + 1;
            endIndex = tempString.indexOf( "'", beginIndex );
            comicURL[i] = baseURL + tempString.substring( beginIndex, endIndex ).trim();

            // 取得標題
            beginIndex = tempString.indexOf( ">", beginIndex ) + 1;
            endIndex = tempString.indexOf( "</a>", beginIndex );
            titles[i] = Common.getStringRemovedIllegalChar(
                    tempString.substring( beginIndex, endIndex ).trim() ) + "." + Common.getDefaultTextExtension();

            //Common.debugPrintln( i + " " + titles[i] + " " + comicURL[i] ); // debug
        }

        for ( int i = 0; i < totalPage && Run.isAlive; i++ ) {
            // 每解析一個網址就下載一張圖
            if ( !new File( getDownloadDirectory() + titles[i] ).exists() && Run.isAlive ) {
                singlePageDownload( getTitle(), getWholeTitle(), comicURL[i], totalPage, i + 1, 0 );
                String fileName = formatter.format( i + 1 ) + "." + pageExtension;
                handleSingleNovel( fileName, titles[i] );  // 處理單一小說主函式
            } else {
                Common.debugPrintln( titles[i] + "已下載，跳過" );
            }
        }

        handleWholeNovel( titles, webSite, author );

        //System.exit( 0 ); // debug
    }

    // 處理全部小說的主函式
    public void handleWholeNovel( String[] titles, String url, String author ) {
        String allNovelText = getInformation( title, url ); // 全部頁面加起來小說文字

        Common.debugPrintln( "開始執行合併程序：" );
        Common.debugPrintln( "共有" + ( titles.length ) + "篇" );
        for ( int i = 0; i < titles.length && Run.isAlive; i++ ) {
            Common.debugPrintln( "合併第" + ( i + 1 ) + "篇: " + titles[i] );
            String tempText = Common.getFileString( getDownloadDirectory(), titles[i] );


            if ( SetUp.getDefaultTextOutputFormat() == FileFormatEnum.HTML_WITHOUT_PIC ||
                 SetUp.getDefaultTextOutputFormat() == FileFormatEnum.HTML_WITH_PIC ) {
                allNovelText += "<br>" + ( i + 1 ) + "<br><hr><br>"; // 每一樓的文字加總起來
            } else {
                if ( isRegularTableTitle( tempText ) ) {
                     allNovelText += "\n\n----------------------" + ( i + 1 ) + "\n\n";
                }
                else {
                    
                    if ( i < ( titles.length - 1 ) ) {
                        allNovelText += "\n\n第" + ( i + 1 ) + "小節：";
                    }
                    
                }
            }
            allNovelText += tempText;

            
            ComicDownGUI.stateBar.setText( getTitle()
                    + "合併中: " + ( i + 1 ) + " / " + titles.length );
        }

        // 取得上一層的目錄
        String textOutputDirectory = getParentPath( getDownloadDirectory() );

        Common.outputFile( allNovelText, textOutputDirectory, getWholeTitle() + "." + Common.getDefaultTextExtension() );

        textFilePath = textOutputDirectory + getWholeTitle() + "." + Common.getDefaultTextExtension();

        if ( SetUp.getDownloadNovelCover() ) {
            downloadCover( getWholeTitle(), author ); // 下載封面
        }
    }
    
    // 檢查此小節的標題是否為正規的標題寫法（意思是轉epub時可以分割為單一章節）
    public boolean isRegularTableTitle( String text ) {
        // 楔子|內容簡介|正文|序|序言|序章|跋
        boolean isRegular = false;
        
        if ( text.matches( "第(?s).*" ) || 
             text.matches( "楔子(?s).*" ) || 
             text.matches( "內容簡介(?s).*" ) || 
             text.matches( "正文(?s).*" ) || 
             text.matches( "序(?s).*" ) || 
            text.matches( "前言(?s).*" ) || 
            text.matches( "後記(?s).*" ) || 
             text.matches( "序言(?s).*" ) || 
             text.matches( "序章(?s).*" ) || 
             text.matches( "跋(?s).*" ) ) {
            isRegular = true;
        }
        
        return isRegular;
        
    }
    
    public void handleSingleNovel( String fileName, String title ) {
        handleSingleNovel( fileName, title, false );
    }

    // 處理單一本小說主函式
    // addTitle: 若為true，代表需將title加入本文中
    public void handleSingleNovel( String fileName, String title, boolean addTitle ) {
        String allPageString = "";
        String allNovelText = ""; // 全部頁面加起來小說文字

        Common.newEncodeFile( getDownloadDirectory(),
                fileName, "utf8_" + fileName, pageCode );

        allPageString = Common.getFileString( getDownloadDirectory(), "utf8_" + fileName );

        Common.deleteFile( getDownloadDirectory(), fileName ); // 刪掉utf8編碼的暫存檔
        Common.deleteFile( getDownloadDirectory(), "utf8_" + fileName ); // 刪掉utf8編碼的暫存檔

        if ( SetUp.getDefaultTextOutputFormat() == FileFormatEnum.HTML_WITHOUT_PIC ||
             SetUp.getDefaultTextOutputFormat() == FileFormatEnum.HTML_WITH_PIC ) {
            allNovelText = getCharsetInformation(); // html開頭要放編碼資訊，否則可能瀏覽器開啟成亂碼
        }
        
        if ( addTitle ) { // 將標題加入到本文中
            allNovelText += title + "\n\n";
        }

        allNovelText += getRegularNovel( allPageString ); // 每一頁處理過都加總起來 

        Common.outputFile( allNovelText, getDownloadDirectory(), title );

    }

    // 處理小說網頁，將標籤去除
    public String getRegularNovel( String allPageString ) {
        int beginIndex = 0;
        int endIndex = 0;
        String oneFloorText = ""; // 單一樓層（頁）的文字
        beginIndex = allPageString.indexOf( "id=\"contenttitle\"" );
        beginIndex = allPageString.indexOf( ">", beginIndex ) + 1;
        endIndex = allPageString.indexOf( "<", beginIndex );
        String title = allPageString.substring( beginIndex, endIndex );

        beginIndex = allPageString.indexOf( "<p class=\"content\">", beginIndex );
        beginIndex = allPageString.indexOf( ">", beginIndex ) + 1;
        endIndex = allPageString.indexOf( "</table>", beginIndex );
        oneFloorText = title + "<br><br>" + allPageString.substring( beginIndex, endIndex );
        oneFloorText = oneFloorText.replaceAll( "<P>", "<br><br>" );

        if ( SetUp.getDefaultTextOutputFormat() == FileFormatEnum.TEXT ) {
            oneFloorText = replaceProcessToText( oneFloorText );
        } else {
            oneFloorText = replaceProcessToHtml( oneFloorText );
        }
        //oneFloorText = Common.getTraditionalChinese( oneFloorText ); // 簡轉繁

        return oneFloorText;
    }

    public String getRegularURL( String url ) {
        return url;
    }

    @Override
    public String getAllPageString( String urlString ) {
        urlString = getRegularURL( urlString ); // 轉為普通位址

        String indexName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_" + siteName + "_", pageExtension );
        String indexEncodeName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_" +siteName + "_encode_", pageExtension );

        Common.downloadFile( urlString, SetUp.getTempDirectory(), indexName, false, "" );
        Common.newEncodeFile( SetUp.getTempDirectory(), indexName, indexEncodeName, pageCode );

        return Common.getFileString( SetUp.getTempDirectory(), indexEncodeName );
    }

    @Override
    public boolean isSingleVolumePage( String urlString ) {
        // 都判斷為主頁，並直接下載。（每部只有一"集"）
        return false;
    }

    public String getMainUrlFromSingleVolumeUrl( String volumeURL ) {
        return volumeURL;
    }

    @Override
    public String getTitleOnMainPage( String urlString, String allPageString ) {
        int beginIndex, endIndex;
        String title = "";

        beginIndex = allPageString.indexOf( "href=\"#\"" );
        beginIndex = allPageString.indexOf( ">", beginIndex ) + 1;
        endIndex = allPageString.indexOf( "</a>", beginIndex );
        title = allPageString.substring( beginIndex, endIndex ).trim();

        return Common.getStringRemovedIllegalChar( title );
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
        volumeList.add( Common.getStringRemovedIllegalChar( volumeTitle.trim() ) );

        // 取得單集位址
        urlList.add( urlString );

        totalVolume = 1;

        Common.debugPrintln( "共有" + totalVolume + "集" );

        combinationList.add( volumeList );
        combinationList.add( urlList );

        return combinationList;
    }
}
