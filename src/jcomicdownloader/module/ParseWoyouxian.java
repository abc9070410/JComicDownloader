/*
 ----------------------------------------------------------------------------------------------------
 Program Name : JComicDownloader
 Authors  : surveyorK
 Last Modified : 2012/11/24
 ----------------------------------------------------------------------------------------------------
 ChangeLog:
 5.09: 新增對woyouxian.com的支援。
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

public class ParseWoyouxian extends ParseEightNovel
{

    protected int radixNumber; // use to figure out the name of pic
    protected String jsName;
    protected String indexName;
    protected String indexEncodeName;
    protected String baseURL;
    protected int floorCountInOnePage; // 一頁有幾層樓

    /**

     @author user
     */
    public ParseWoyouxian()
    {
        siteID = Site.WOYOUXIAN;
        siteName = "Woyouxian";
        pageExtension = "html"; // 網頁副檔名
        pageCode = Encoding.UTF8; // 網頁預設編碼

        indexName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_Woyouxian_parse_", pageExtension );
        indexEncodeName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_Woyouxian_encode_parse_", pageExtension );

        jsName = "index_Woyouxian.js";
        radixNumber = 159451; // default value, not always be useful!!

        baseURL = "http://www.woyouxian.com/";
    }

    @Override
    public void parseComicURL()
    { // parse URL and save all URLs in comicURL  //

        webSite = getRegularURL( webSite );

        Common.downloadFile( webSite, SetUp.getTempDirectory(), indexName, false, "" );
        //Common.newEncodeFile( SetUp.getTempDirectory(), indexName, indexEncodeName, pageCode );
        String allPageString = Common.getFileString( SetUp.getTempDirectory(), indexName );

        Common.debugPrint( "開始解析這一集有幾頁 : " );

        int beginIndex = 0, endIndex = 0;

        beginIndex = allPageString.indexOf( "<h1>" );
        endIndex = allPageString.indexOf( "</div>", beginIndex );
        String tempString = allPageString.substring( beginIndex, endIndex ).trim();

        totalPage = tempString.split( " href=" ).length - 1;
        Common.debugPrintln( "共 " + totalPage + " 頁" );
        comicURL = new String[ totalPage ];
        String[] titles = new String[ totalPage ];


        // 取得作者名稱
        beginIndex = allPageString.indexOf( "<h1>" );
        beginIndex = allPageString.indexOf( " title=", beginIndex );
        beginIndex = allPageString.indexOf( "\"", beginIndex ) + 1;
        endIndex = allPageString.indexOf( " ", beginIndex );

        String author = "";
        if ( beginIndex > 0 && endIndex > 0 )
        {
            author = allPageString.substring( beginIndex, endIndex );
            author = Common.getStringRemovedIllegalChar( author );
        }
        else
        {
            author = getTitle();
        }
        Common.debugPrintln( "作者：" + author );

        NumberFormat formatter = new DecimalFormat( Common.getZero() );

        // 取得小說網址
        beginIndex = endIndex = 0;
        for ( int i = 0; i < totalPage; i++ )
        {
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

            Common.debugPrintln( i + " " + titles[i] + " " + comicURL[i] ); // debug
        }
        //System.exit(  0 ); // for debug

        for ( int i = 0; i < totalPage && Run.isAlive; i++ )
        {
            // 每解析一個網址就下載一張圖
            if ( !new File( getDownloadDirectory() + titles[i] ).exists() && Run.isAlive )
            {
                singlePageDownload( getTitle(), getWholeTitle(), comicURL[i], totalPage, i + 1, 0 );
                String fileName = formatter.format( i + 1 ) + "." + pageExtension;
                handleSingleNovel( fileName, titles[i] );  // 處理單一小說主函式
            }
            else
            {
                Common.debugPrintln( titles[i] + "已下載，跳過" );
            }
        }

        handleWholeNovel( titles, webSite, author );

        //System.exit( 0 ); // debug
    }

    // 處理小說網頁，將標籤去除
    public String getRegularNovel( String allPageString )
    {
        int beginIndex = 0;
        int endIndex = 0;
        String oneFloorText = ""; // 單一樓層（頁）的文字

        beginIndex = allPageString.indexOf( "<h1>" );
        endIndex = allPageString.indexOf( "</h1>", beginIndex );
        String tempString = allPageString.substring( beginIndex, endIndex );

        tempString = tempString.split( ">" )[tempString.split( ">" ).length - 1];

        // 先取得章節名稱
        oneFloorText += "    " + tempString + " <br><br>";

        beginIndex = allPageString.indexOf( "<div ", beginIndex );
        endIndex = allPageString.indexOf( "<br class", beginIndex );


        //Common.debugPrintln( beginIndex + "______" + endIndex );
        //System.exit( 0 );

        oneFloorText += allPageString.substring( beginIndex, endIndex );

        if ( SetUp.getDefaultTextOutputFormat() == FileFormatEnum.HTML_WITHOUT_PIC
                || SetUp.getDefaultTextOutputFormat() == FileFormatEnum.HTML_WITH_PIC )
        {
            oneFloorText = replaceProcessToHtml( oneFloorText );
        }
        else
        {
            oneFloorText = replaceProcessToText( oneFloorText );
        }
        oneFloorText = Common.getTraditionalChinese( oneFloorText ); // 簡轉繁


        return oneFloorText;
    }

    public String getRegularURL( String url )
    {
        return url.replaceAll( "\\d+.html", "index.html" );
    }

    @Override
    public String getTitleOnMainPage( String urlString, String allPageString )
    {
        int beginIndex, endIndex;
        String title = "";

        beginIndex = allPageString.indexOf( "<h1" );
        beginIndex = allPageString.indexOf( ">", beginIndex ) + 1;
        endIndex = allPageString.indexOf( "</h1>", beginIndex );
        title = allPageString.substring( beginIndex, endIndex ).split( "<" )[0].trim();
        title = title.replace( "：", "-" ); // skydrive不支援「：」....

        return Common.getStringRemovedIllegalChar( Common.getTraditionalChinese( title ) );
    }

    @Override
    public List<List<String>> getVolumeTitleAndUrlOnMainPage( String urlString, String allPageString )
    {
        // combine volumeList and urlList into combinationList, return it.

        List<List<String>> combinationList = new ArrayList<List<String>>();
        List<String> urlList = new ArrayList<String>();
        List<String> volumeList = new ArrayList<String>();

        String volumeTitle = "";
        String volumeURL = "";
        int amount = 0;

        if ( urlString.matches( "(?s).*/author/(?s).*" ) )
        {
            int beginIndex, endIndex;

            beginIndex = allPageString.indexOf( "name =\"list\"" );
            endIndex = allPageString.indexOf( "name =\"intro\"", beginIndex );
            String tempString = allPageString.substring( beginIndex, endIndex );
            
            amount = tempString.split( " href=" ).length - 1;

            beginIndex = endIndex = 0;
            for ( int i = 0; i < amount; i++ )
            {
                // 取得單集位址
                beginIndex = tempString.indexOf( " href=", beginIndex );
                beginIndex = tempString.indexOf( "\"", beginIndex ) + 1;
                endIndex = tempString.indexOf( "\"", beginIndex );
                volumeURL = baseURL + tempString.substring( beginIndex, endIndex ).trim();
                urlList.add( volumeURL );

                // 取得單集名稱
                beginIndex = tempString.indexOf( ">", beginIndex ) + 1;
                endIndex = tempString.indexOf( "</a>", beginIndex );
                volumeTitle = tempString.substring( beginIndex, endIndex ).trim();
                volumeList.add( Common.getStringRemovedIllegalChar( Common.getTraditionalChinese( volumeTitle ) ) );

                Common.debugPrintln( i + " " + volumeTitle + " " + volumeURL ); // for debug
            }
        }
        else
        {
            amount = 1;

            // 取得單集名稱
            volumeTitle = getTitle();
            volumeList.add( Common.getStringRemovedIllegalChar( Common.getTraditionalChinese( volumeTitle.trim() ) ) );

            // 取得單集位址
            volumeURL = urlString;
            urlList.add( volumeURL );
        }

        totalVolume = amount;
        Common.debugPrintln( "共有" + totalVolume + "集" );

        combinationList.add( volumeList );
        combinationList.add( urlList );

        return combinationList;
    }
}
