/*
 ----------------------------------------------------------------------------------------------------
 Program Name : JComicDownloader
 Authors  : surveyorK
 Last Modified : 2012/6/15
 ----------------------------------------------------------------------------------------------------
 ChangeLog:
 *  4.12: 1. 新增對uus8.com的支援。
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

public class ParseUUS8 extends ParseEightNovel {

    protected int radixNumber; // use to figure out the name of pic
    protected String jsName;
    protected String indexName;
    protected String indexEncodeName;
    protected String baseURL;
    protected int floorCountInOnePage; // 一頁有幾層樓

    /**

     @author user
     */
    public ParseUUS8() {
        siteID = Site.UUS8;
        siteName = "UUS8";
        pageExtension = "htm"; // 網頁副檔名
        pageCode = Encoding.GB2312; // 網頁預設編碼

        indexName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_uus8_parse_", pageExtension );
        indexEncodeName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_uus8_encode_parse_", pageExtension );

        jsName = "index_uus8.js";
        radixNumber = 15124451; // default value, not always be useful!!

        baseURL = "http://book.qq.com/s/book/";
    }

    @Override
    public void parseComicURL() { // parse URL and save all URLs in comicURL  //
        
        webSite = getRegularURL( webSite ); // 將全集頁面轉為正規的全集頁面位址

        Common.downloadFile( webSite, SetUp.getTempDirectory(), indexName, false, "" );
        Common.newEncodeFile( SetUp.getTempDirectory(), indexName, indexEncodeName, pageCode );
        String allPageString = Common.getFileString( SetUp.getTempDirectory(), indexEncodeName );
        Common.debugPrint( "開始解析這一集有幾頁 : " );

        int beginIndex = 0, endIndex = 0;

        beginIndex = allPageString.indexOf( "id=\"youli2\"" );
        endIndex = allPageString.lastIndexOf( "</ul>" );
        String tempString = allPageString.substring( beginIndex, endIndex ).trim();

        totalPage = tempString.split( " href=" ).length - 1;
        Common.debugPrintln( "共 " + totalPage + " 頁" );
        comicURL = new String[totalPage];
        String[] titles = new String[totalPage];


        // 取得作者名稱
        String author = "";
        Common.debugPrintln( "此站無法取得作者訊息" );
        beginIndex = allPageString.indexOf( "id=\"youli2\"" );
        endIndex = allPageString.lastIndexOf( "</ul>" );



        NumberFormat formatter = new DecimalFormat( Common.getZero() );

        // 取得小說網址
        beginIndex = endIndex = 0;
        for ( int i = 0; i < totalPage; i++ ) {
            // 取得網址
            beginIndex = tempString.indexOf( " href=", beginIndex );
            beginIndex = tempString.indexOf( "\"", beginIndex ) + 1;
            endIndex = tempString.indexOf( "\"", beginIndex );
            comicURL[i] = webSite + tempString.substring( beginIndex, endIndex ).trim();

            // 取得標題
            beginIndex = tempString.indexOf( ">", beginIndex ) + 1;
            endIndex = tempString.indexOf( "</a>", beginIndex );
            titles[i] = Common.getStringRemovedIllegalChar( Common.getTraditionalChinese(
                tempString.substring( beginIndex, endIndex ).trim() ) ) + "." + Common.getDefaultTextExtension();

            //Common.debugPrintln( i + " " + titles[i] + " " + comicURL[i] ); // debug
        }
        //System.exit(  0 ); // for debug

        for ( int i = 0; i < totalPage && Run.isAlive; i++ ) {
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
        // 先取得章節名稱
        beginIndex = allPageString.indexOf( "<h2>" ) + 4;
        endIndex = allPageString.indexOf( "</h2>", beginIndex );
        oneFloorText = "    " + allPageString.substring( beginIndex, endIndex ).trim() + " <br><br>";

        // 再取得本文內容
        beginIndex = allPageString.indexOf( "<div class=\"cbg2\">", beginIndex );
        endIndex = allPageString.indexOf( "<a href=", beginIndex );
        oneFloorText += allPageString.substring( beginIndex, endIndex );
        oneFloorText = oneFloorText.replaceAll( "<br />", "<br><br>" );

        if ( SetUp.getDefaultTextOutputFormat() == FileFormatEnum.TEXT ) {
            oneFloorText = replaceProcessToText( oneFloorText );
        } else {
            oneFloorText = replaceProcessToHtml( oneFloorText );
        }
        oneFloorText = Common.getTraditionalChinese( oneFloorText ); // 簡轉繁


        return oneFloorText;
    }

    // ex. http://www.uus8.com/book/display.asp?id=16282 轉為
    //     http://www.uus8.com/a/72/195/
    public String getRegularURL( String url ) {

        if ( url.matches( "(?s).*uus8.com/book/display(?s).*" ) ) {
            Common.downloadFile( url, SetUp.getTempDirectory(), indexName, false, "" );
            Common.newEncodeFile( SetUp.getTempDirectory(), indexName, indexEncodeName, pageCode );
            String allPageString = Common.getFileString( SetUp.getTempDirectory(), indexEncodeName );

            int beginIndex = allPageString.indexOf( "<h1>" );
            beginIndex = allPageString.indexOf( "=", beginIndex ) + 1;
            int endIndex = allPageString.indexOf( "title=", beginIndex );
            url = allPageString.substring( beginIndex, endIndex ).trim() + "/";
            Common.debugPrintln( "轉為正規全集頁面位址：" + url );
        }

        return url;
    }

    @Override
    public String getTitleOnMainPage( String urlString, String allPageString ) {
        int beginIndex, endIndex;
        String title = "";

        beginIndex = allPageString.indexOf( "<h2>" );
        beginIndex = allPageString.indexOf( ">", beginIndex ) + 1;
        endIndex = allPageString.indexOf( "</h2>", beginIndex );
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

        if ( allPageString.indexOf( "index.html\" target=\"_blank\"" ) < 0 ) {
            // 只有單集

            // 取得單集名稱
            volumeTitle = getTitle();
            volumeList.add( volumeTitle.trim() );

            // 取得單集位址
            urlList.add( urlString );

            totalVolume = 1;
            Common.debugPrintln( "共有" + totalVolume + "集" );
        }
        else {
            // 此為合集

            int beginIndex = allPageString.indexOf( "id=\"youli2\"" );
            int endIndex = allPageString.lastIndexOf( "</ul>" );
            String tempString = allPageString.substring( beginIndex, endIndex ).trim();

            int amount = tempString.split( "index.html\"" ).length - 1;

            beginIndex = endIndex = 0;
            for ( int i = 0; i < amount; i++ ) {
                // 取得單集位址
                beginIndex = tempString.indexOf( "index.html\"", beginIndex );
                beginIndex = tempString.lastIndexOf( "\"", beginIndex ) + 1;
                endIndex = tempString.indexOf( "\"", beginIndex );
                volumeURL = urlString + tempString.substring( beginIndex, endIndex ).trim();
                volumeURL = volumeURL.replace( "index.html", "" );
                volumeURL = volumeURL.replace( "\\", "/" );
                urlList.add( volumeURL );


                // 取得單集名稱
                beginIndex = tempString.indexOf( ">", beginIndex ) + 1;
                endIndex = tempString.indexOf( "</a>", beginIndex );
                volumeTitle = tempString.substring( beginIndex, endIndex ).trim();
                volumeList.add( volumeTitle );
                
                Common.debugPrintln( i + " " + volumeTitle + " " + volumeURL);
            }


        }




        combinationList.add( volumeList );
        combinationList.add( urlList );

        return combinationList;
    }
}
