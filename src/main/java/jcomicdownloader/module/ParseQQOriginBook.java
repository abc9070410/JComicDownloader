/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jcomicdownloader.module;

import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import jcomicdownloader.SetUp;
import jcomicdownloader.encode.Encoding;
import jcomicdownloader.enums.FileFormatEnum;
import jcomicdownloader.enums.Site;
import jcomicdownloader.tools.Common;

/**
 *
 * @author apple
 */
public class ParseQQOriginBook extends ParseQQBook {

    /**
    
    @author user
     */
    public ParseQQOriginBook() {
        enumName = "QQ_ORIGIN_BOOK";
        regexs= new String[]{"(?s).*book.qq.com/origin/book/(?s).*"};
        novelSite=true;
        parserName=this.getClass().getName();
        siteID=Site.formString("QQ_ORIGIN_BOOK");
        siteName = "QQOriginBook";
        pageExtension = "html"; // 網頁副檔名
        pageCode = Encoding.GB2312; // 網頁預設編碼
        
        indexName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_qqOriginbook_parse_", pageExtension );
        indexEncodeName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_qqOriginbookencode_parse_", pageExtension );

        jsName = "index_qqOriginbook.js";
        radixNumber = 151234451; // default value, not always be useful!!

        baseURL = "http://bookapp.book.qq.com";
    }


    @Override
    public void parseComicURL() { // parse URL and save all URLs in comicURL  //

        Common.downloadFile( webSite, SetUp.getTempDirectory(), indexName, false, "" );
        Common.newEncodeFile( SetUp.getTempDirectory(), indexName, indexEncodeName, pageCode );
        String allPageString = Common.getFileString( SetUp.getTempDirectory(), indexEncodeName );
        Common.debugPrint( "開始解析這一集有幾頁 : " );

        int beginIndex = 0, endIndex = 0;

        beginIndex = allPageString.indexOf( "<ol class=\"clearfix\">" );
        endIndex = allPageString.lastIndexOf( "</ol>" );
        String tempString = allPageString.substring( beginIndex, endIndex ).trim();

        if ( tempString.indexOf( "display:1\">Vip" ) > 0 ) {
            endIndex = tempString.indexOf( "display:1\">Vip" );
            endIndex = tempString.lastIndexOf( "<td>", endIndex );
            
            tempString = tempString.substring( 0, endIndex ).trim();
        }
        
        totalPage = tempString.split( " href=" ).length - 1;
        Common.debugPrintln( "共 " + totalPage + " 頁" );
        comicURL = new String[totalPage];
        String[] titles = new String[totalPage];
        
        
         // 取得作者名稱
        beginIndex = allPageString.indexOf( "class=\"co_1\"" );
        beginIndex = allPageString.indexOf( ">", beginIndex ) + 1;
        endIndex = allPageString.indexOf( "<", beginIndex );
        String author = "";
        if ( beginIndex > 0 && endIndex > 0 ) {
            author = allPageString.substring( beginIndex, endIndex ).trim();
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
        beginIndex = allPageString.indexOf( "class=\"ctitle\"", beginIndex );
        beginIndex = allPageString.indexOf( "<b>", beginIndex );
        endIndex = allPageString.indexOf( "</p>", beginIndex );
        oneFloorText = allPageString.substring( beginIndex, endIndex ) + "<br><br>";
        
        beginIndex = allPageString.indexOf( "<div id=\"content\">", beginIndex );
        endIndex = allPageString.indexOf( "<script language=\"javascript\">", beginIndex );
        oneFloorText += allPageString.substring( beginIndex, endIndex );

        if ( SetUp.getDefaultTextOutputFormat() == FileFormatEnum.TEXT ) {
            oneFloorText = replaceProcessToText( oneFloorText );
        } else {
            oneFloorText = replaceProcessToHtml( oneFloorText );
        }
        oneFloorText = Common.getTraditionalChinese( oneFloorText ); // 簡轉繁


        return oneFloorText;
    }
}
