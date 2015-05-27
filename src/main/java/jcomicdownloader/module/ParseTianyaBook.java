/*
 ----------------------------------------------------------------------------------------------------
 Program Name : JComicDownloader
 Authors  : surveyorK
 Last Modified : 2012/11/23
 ----------------------------------------------------------------------------------------------------
 ChangeLog:
 5.08: 1. 新增對tianyabook的支援。
 *  4.12: 1. 新增對uus8.com的支援。
 ----------------------------------------------------------------------------------------------------
 */
package jcomicdownloader.module;

import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import jcomicdownloader.SetUp;
import jcomicdownloader.encode.Encoding;
import jcomicdownloader.enums.FileFormatEnum;
import jcomicdownloader.enums.Site;
import jcomicdownloader.tools.Common;

public class ParseTianyaBook extends ParseEightNovel
{

    protected int radixNumber; // use to figure out the name of pic
    protected String jsName;
    protected String indexName;
    protected String indexEncodeName;
    protected String baseURL;
    protected int floorCountInOnePage; // 一頁有幾層樓
    protected String nowTitle;

    /**

     @author user
     */
    public ParseTianyaBook()
    {
        siteID = Site.UUS8;
        siteName = "TianyaBook";
        pageExtension = "html"; // 網頁副檔名
        pageCode = Encoding.GB2312; // 網頁預設編碼

        indexName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_tianyaBook_parse_", pageExtension );
        indexEncodeName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_tianyaBook_encode_parse_", pageExtension );

        jsName = "index_tianyaBook.js";
        radixNumber = 15123451; // default value, not always be useful!!

        baseURL = "http://www.tianyabook.com";
    }

    @Override
    public void parseComicURL()
    { // parse URL and save all URLs in comicURL  //

        webSite = getRegularURL( webSite ); // 將全集頁面轉為正規的全集頁面位址

        Common.downloadFile( webSite, SetUp.getTempDirectory(), indexName, false, "" );
        Common.newEncodeFile( SetUp.getTempDirectory(), indexName, indexEncodeName, pageCode );
        String allPageString = Common.getFileString( SetUp.getTempDirectory(), indexEncodeName );
        Common.debugPrint( "開始解析這一集有幾頁 : " );

        allPageString = Common.getTraditionalChinese( allPageString );

        List<String> urlList = new ArrayList<String>();
        List<String> volumeList = new ArrayList<String>();

        String hrefString = "href=";
        String tempString = "";
        String volumeURL = "";
        String volumeTitle = "";
        String baseTempURL = webSite.substring( 0, webSite.lastIndexOf( "/" ) + 1 );

        int beginIndex = 0;
        int endIndex = 0;
        int amount = 0;

        while ( true )
        {
            // 先找出每個超連結網址
            beginIndex = Common.getSmallerIndexOfTwoKeyword( allPageString, beginIndex, hrefString, hrefString.toUpperCase() );
            endIndex = Common.getSmallerIndexOfTwoKeyword( allPageString, beginIndex, ">", " " );

            if ( beginIndex < 0 || endIndex < 0 )
            {
                break;
            }

            tempString = allPageString.substring( beginIndex + 5, endIndex ).replaceAll( "\"", "" ).trim();

            Common.debugPrintln( "找到的連結: " + tempString );

            // 代表有下層的目錄網址
            if ( unRedundantURL( tempString ) )
            {

                // 檢查是否為完整網址
                if ( tempString.matches( "http://(?s).*" ) )
                {
                    volumeURL = tempString;
                }
                else
                {
                    volumeURL = baseTempURL + tempString;
                }

                // 位址不重複 才加入
                if ( !urlList.contains( volumeURL ) )
                {
                    // 取得單集位址
                    urlList.add( volumeURL );


                    // 然後取單集名稱
                    do
                    {
                        beginIndex = allPageString.indexOf( ">", beginIndex ) + 1;
                        endIndex = allPageString.indexOf( "<", beginIndex );
                        volumeTitle = allPageString.substring( beginIndex, endIndex ).trim();
                    }
                    while ( volumeTitle.matches( "" ) );

                    volumeList.add( Common.getStringRemovedIllegalChar( Common.getTraditionalChinese( volumeTitle ) ) );

                    Common.debugPrintln( " " + volumeURL + " " + volumeTitle );

                    amount++;
                }
            }
            beginIndex = endIndex;
        }
        //System.exit( 0 );
        totalPage = amount;

        comicURL = new String[ totalPage ];
        String[] titles = new String[ totalPage ];

        for ( int i = 0; i < totalPage; i++ )
        {
            comicURL[i] = urlList.get( i );
            titles[i] = volumeList.get( i ) + "." + Common.getDefaultTextExtension();
        }


        // 取得作者名稱
        String author = "";

        if ( (beginIndex = allPageString.indexOf( "作者：" )) > 0 )
        {
            beginIndex += 3;
            endIndex = allPageString.indexOf( "<", beginIndex );
            author = allPageString.substring( beginIndex, endIndex );
        }
        else if ( (beginIndex = allPageString.indexOf( "作者:" )) > 0 )
        {
            beginIndex += 3;
            endIndex = allPageString.indexOf( "<", beginIndex );
            author = allPageString.substring( beginIndex, endIndex );
        }
        else
        {
            Common.debugPrintln( "此站無法取得作者訊息" );
            author = getTitle();
        }
        
        author = Common.getStringRemovedIllegalChar( author );
        
        Common.debugPrintln( "作者名稱: " + author );

        NumberFormat formatter = new DecimalFormat( Common.getZero() );

        // 取得小說網址
        beginIndex = endIndex = 0;
        String tempTitle = "";
        String tempURL = "";


        try
        {

            for ( int i = 0; i < totalPage && Run.isAlive; i++ )
            {
                // 每解析一個網址就下載一張圖
                if ( !new File( getDownloadDirectory() + titles[i] ).exists() && Run.isAlive )
                {

                    singlePageDownload( getTitle(), getWholeTitle(), comicURL[i], totalPage, i + 1, 0 );
                    pageExtension = comicURL[i].substring( comicURL[i].lastIndexOf( "." ) + 1, comicURL[i].length() );
                    String fileName = formatter.format( i + 1 ) + "." + pageExtension;

                    nowTitle = titles[i].substring( 0, titles[i].lastIndexOf( "." ) );
                    handleSingleNovel( fileName, titles[i] );  // 處理單一小說主函式


                }
                else
                {
                    Common.debugPrintln( titles[i] + "已下載，跳過" );
                }

                //System.exit( 0 );
            }

            handleWholeNovel( titles, webSite, author );

        }
        catch ( Exception ex )
        {

            Common.hadleErrorMessage( ex, "處理下載文字檔發生問題" );
            try
            {
                throw new Exception();
            }
            catch ( Exception ex1 )
            {
                Logger.getLogger( ParseTianyaBook.class.getName() ).log( Level.SEVERE, null, ex1 );
            }
        }

        //System.exit( 0 ); // debug
    }

    public boolean unRedundantURL( String url )
    {
        if ( !url.matches( "(?s).*\\.\\./(?s).*" )
                && !url.matches( "(?s).*zip" )
                && !url.matches( "(?s).*@(?s).*" )
                && !url.matches( "http://www.tianyabook.com/kehuan.htm" )
                && !url.matches( "http://www.tianyabook.com/ztxs/zhentan.html" )
                && !url.matches( "http:///" ) 
                && !url.matches( "javascript:(?s).*" )
                && !url.matches( "http://www.tianyabook.com/" )
                && !url.matches( baseURL )
                
                
                )
        {
            return true;
        }
        else
        {
            return false;
        }

    }

    // 處理小說網頁，將標籤去除
    public String getRegularNovel( String allPageString )
    {

        String oneFloorText = ""; // 單一樓層（頁）的文字

        allPageString = Common.getTraditionalChinese( allPageString );

        // 先取得章節名稱
        oneFloorText = "    " + nowTitle + " <br><br>";

        oneFloorText += allPageString;

        //Common.debugPrintln( oneFloorText );

        if ( SetUp.getDefaultTextOutputFormat() == FileFormatEnum.TEXT )
        {
            oneFloorText = replaceProcessToText( oneFloorText );
        }
        else
        {
            oneFloorText = replaceProcessToHtml( oneFloorText );
        }
        oneFloorText = Common.getTraditionalChinese( oneFloorText ); // 簡轉繁

        return replaceRedundant( oneFloorText );
    }

    // 拿掉多餘的字串
    public String replaceRedundant( String text )
    {
        text = text.replaceAll( "-網絡小說-現代文學-外國文學-學術論文-武俠小說-宗教-歷史-經濟-軍事-人物傳記-偵探小說-古典文學-哲學-", "" );
        text = text.replaceAll( "天涯在線書庫 整理", "" );
        text = text.replaceAll( "天涯在線書庫搜集整理", "" );
        text = text.replaceAll( "本站由天涯搜集整理﹒技術維護", "" );
        text = text.replaceAll( "回目錄 回首頁", "" );
        text = text.replaceAll( "支持本書作者，請購買正式出版物", "" );
        text = text.replaceAll( "天涯在線書庫", "" );
        text = text.replaceAll( "後一頁\\s", "" );
        text = text.replaceAll( "前一頁\\s", "" );
        text = text.replaceAll( "回目錄\\s", "" );
        text = text.replaceAll( "背景色：", "" );
        text = text.replaceAll( "科幻小說\\s+添入收藏夾\\s+暴笑網文", "" );
        text = text.replaceAll( "古典文學\\s+現代文學\\s+外國文學\\s+武俠小說\\s+詩詞歌賦\\s+網絡小說\\s+言情小說\\s+偵探小說", "" );
        
        text = text.replaceAll( "\\s{10,}版權所有 &copy;\\s{10,}", "" );
        text = text.replaceAll( "\\s{10,}>>\\s{10,}", "" );
        text = text.replaceAll( "\\s{10,}返回\\s{10,}", "" );
        text = text.replaceAll( "http://www.tianyabook.com/46080.js\"></td", "" );

        return text;
    }

    // ex. http://www.uus8.com/book/display.asp?id=16282 轉為
    //     http://www.uus8.com/a/72/195/
    public String getRegularURL( String url )
    {
        return url;
    }

    @Override
    public boolean isSingleVolumePage( String urlString )
    {
        // ex. http://www.tianyabook.com/renwu2005/js/t/tanghaoming/yf/001.htm
        // ex. http://www.tianyabook.com/renwu2005/js/t/tanghaoming/index.html
        // 凡沒有href="../index.html"的都不是主頁，就不能加入到任務中

        String allPageString = getAllPageString( urlString );

        allPageString = replaceComment( allPageString );
        allPageString = replaceStyle( allPageString );

        String allText = allPageString.replaceAll( "<[^<>]+>", "" ).replaceAll( "\\s", "" );

        Common.debugPrintln( "內文長度: " + allText.length() );

        //Common.debugPrintln( allText );

        if ( allText.length() > 2000 )
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    @Override
    public String getTitleOnMainPage( String urlString, String allPageString )
    {
        int beginIndex, endIndex;
        String title = "";

        beginIndex = endIndex = 0;

        String titleBackTag = "/title>";
        if ( allPageString.indexOf( titleBackTag ) < 0 )
        {
            titleBackTag = titleBackTag.toUpperCase();
        }

        if ( (beginIndex = allPageString.indexOf( "<font face=" )) > 0 )
        {
            beginIndex = allPageString.indexOf( ">", beginIndex ) + 1;
            while ( allPageString.substring( beginIndex, beginIndex + 1 ).equals( "<" ) )
            {
                beginIndex = allPageString.indexOf( ">", beginIndex ) + 1;
            }


            endIndex = allPageString.indexOf( "<", beginIndex );

        }
        else if ( (beginIndex = allPageString.indexOf( "<center><span style=" )) > 0 )
        {
            beginIndex = allPageString.indexOf( ">", beginIndex ) + 1;
            beginIndex = allPageString.indexOf( ">", beginIndex ) + 1;
            while ( allPageString.substring( beginIndex, beginIndex + 1 ).equals( "<" ) )
            {
                beginIndex = allPageString.indexOf( ">", beginIndex ) + 1;
            }


            endIndex = allPageString.indexOf( "<", beginIndex );
        }
        else if ( (beginIndex = allPageString.indexOf( "<td align=\"center\" class=\"xt\">" )) > 0 )
        {
            beginIndex = allPageString.indexOf( ">", beginIndex ) + 1;
            while ( allPageString.substring( beginIndex, beginIndex + 1 ).equals( "<" ) )
            {
                beginIndex = allPageString.indexOf( ">", beginIndex ) + 1;
            }

            endIndex = allPageString.indexOf( "<", beginIndex );
        }
        else if ( (beginIndex = allPageString.indexOf( "<FONT SIZE=" )) > 0 )
        {
            beginIndex = allPageString.indexOf( ">", beginIndex ) + 1;
            while ( allPageString.substring( beginIndex, beginIndex + 1 ).equals( "<" ) )
            {
                beginIndex = allPageString.indexOf( ">", beginIndex ) + 1;
            }

            endIndex = allPageString.indexOf( "<", beginIndex );
        }
        else if ( (endIndex = allPageString.indexOf( titleBackTag )) > 0 )
        {
            beginIndex = allPageString.lastIndexOf( ">", endIndex ) + 1;

            // 站名放前面
            if ( allPageString.substring( beginIndex, endIndex ).matches( "" ) )
            {
                beginIndex = allPageString.lastIndexOf( "-", endIndex ) + 1;
            }
            else
            {
                // 站名放後面      
                endIndex = allPageString.indexOf( "-", beginIndex );
            }

        }
        else
        {
            Common.debugPrintln( "找不到標題 !!" );
        }

        title = allPageString.substring( beginIndex, endIndex ).trim();

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

        String hrefString = "href=";
        String tempString = "";
        int beginIndex = 0;
        int endIndex = 0;
        int amount = 0;

        while ( true )
        {
            // 先找出每個超連結網址
            beginIndex = Common.getSmallerIndexOfTwoKeyword( allPageString, beginIndex, hrefString, hrefString.toUpperCase() );
            endIndex = Common.getSmallerIndexOfTwoKeyword( allPageString, beginIndex, ">", " " );

            if ( beginIndex < 0 || endIndex < 0 )
            {
                break;
            }

            tempString = allPageString.substring( beginIndex + 5, endIndex ).replaceAll( "\"", "" ).trim();

            Common.debugPrintln( tempString );

            // 代表有下層的目錄網址
            if ( tempString.matches( "(?s).*\\w/index.htm(?s).*" ) )
            {
                amount++;

                // 檢查是否為完整網址
                if ( tempString.matches( "http://(?s).*" ) )
                {
                    volumeURL = tempString;
                }
                else
                {
                    volumeURL = urlString.substring( 0, urlString.lastIndexOf( "/" ) + 1 ) + tempString;
                }

                // 取得單集位址
                urlList.add( volumeURL );


                // 然後取單集名稱
                do
                {
                    beginIndex = allPageString.indexOf( ">", beginIndex ) + 1;
                    endIndex = allPageString.indexOf( "<", beginIndex );
                    volumeTitle = allPageString.substring( beginIndex, endIndex ).trim();
                }
                while ( volumeTitle.matches( "" ) );

                volumeList.add( Common.getStringRemovedIllegalChar( Common.getTraditionalChinese( volumeTitle ) ) );
            }
            else
            {
                beginIndex = endIndex;
            }

        }

        if ( amount == 0 )
        {
            amount++;

            // 取得單集名稱
            volumeTitle = getTitle();
            volumeList.add( volumeTitle.trim() );

            // 取得單集位址
            urlList.add( urlString );
        }

        totalVolume = amount;

        combinationList.add( volumeList );
        combinationList.add( urlList );

        return combinationList;
    }
}
