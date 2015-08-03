/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jcomicdownloader.module;

import java.util.ArrayList;
import java.util.List;
import jcomicdownloader.SetUp;
import jcomicdownloader.enums.Site;
import static jcomicdownloader.module.ParseNINENINE.getHowManyKeyWordInString;
import jcomicdownloader.tools.Common;

/**
 *
 * @author apple
 */
// cococomic變繁體版了，解析方法全面翻新
public class ParseCocoTC extends ParseNINENINE
{

    public ParseCocoTC()
    {
        super();
        enumName = "NINENINE_COCO_TC";
        parserName=this.getClass().getName();
        regexs= new String[]{"(?s).*www.cococomic.com(?s).*"};
	siteID=Site.formString("NINENINE_COCO_TC");
        siteName = "www.cococomic.com";

        jsURL = "http://www.cococomic.com/script/ds.js";
    }

    @Override // 下載網址指向的網頁，全部存入String後回傳
    public String getAllPageString( String urlString )
    {
        String indexName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_99_", "html" );
        String indexEncodeName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_99_encode_", "html" );

        System.out.println( "URL: " + urlString );
        Common.downloadFile( urlString, SetUp.getTempDirectory(), indexName, false, "" );

        // 網頁為繁體版utf8，無須轉碼
        return Common.getFileString( SetUp.getTempDirectory(), indexName );

    }

    @Override // 從網址判斷是否為單集頁面(true) 還是主頁面(false)
    public boolean isSingleVolumePage( String urlString )
    {
        // ex. http://www.cococomic.com/comic/6613/89503/
        if ( Common.getAmountOfString( urlString, "/" ) > 5 )
        {
            return true;
        }
        else // ex. http://www.cococomic.com/comic/6613/
        {
            return false;
        }
    }

    @Override // 從主頁面取得title(作品名稱)
    public String getTitleOnMainPage( String urlString, String allPageString )
    {
        Common.debugPrintln( "開始由主頁面位址取得title：" );

        int beginIndex = allPageString.indexOf( "class=\"cTitle\"" );
        beginIndex = allPageString.indexOf( ">", beginIndex ) + 1;
        int endIndex = allPageString.indexOf( "<", beginIndex );

        title = allPageString.substring( beginIndex, endIndex ).trim().split( "\\n" )[0];


        return Common.getStringRemovedIllegalChar( Common.getTraditionalChinese( title ) );
    }

    @Override // 從主頁面取得所有集數名稱和網址
    public List<List<String>> getVolumeTitleAndUrlOnMainPage( String urlString, String allPageString )
    {

        List<List<String>> combinationList = new ArrayList<List<String>>();
        List<String> urlList = new ArrayList<String>();
        List<String> volumeList = new ArrayList<String>();

        int beginIndex = allPageString.indexOf( "class=\"cVol\"" );
        int endIndex = allPageString.indexOf( "class=\"cAreaTitle\"", beginIndex );

        String tempString = allPageString.substring( beginIndex, endIndex );

        String keyword = "href='http";
        totalVolume = tempString.split( keyword ).length - 1;

        beginIndex = endIndex = 0;
        for ( int count = 0; count < totalVolume; count++ )
        {

            beginIndex = tempString.indexOf( keyword, beginIndex );
            beginIndex = tempString.indexOf( "'", beginIndex ) + 1;
            endIndex = tempString.indexOf( "'", beginIndex );
            urlList.add( tempString.substring( beginIndex, endIndex ) );

            beginIndex = tempString.indexOf( ">", beginIndex ) + 1;
            endIndex = tempString.indexOf( "<", beginIndex );
            String title = tempString.substring( beginIndex, endIndex );

            volumeList.add( getVolumeWithFormatNumber(
                    Common.getStringRemovedIllegalChar(
                    Common.getTraditionalChinese( title.trim() ) ) ) );

        }

        combinationList.add( volumeList );
        combinationList.add( urlList );

        return combinationList;
    }

    @Override
    public void setParameters()
    { // let all the non-set attributes get values

        Common.debugPrintln( "開始解析各參數 :" );

        Common.debugPrintln( "基本位址: " + baseURL );
        Common.debugPrintln( "JS檔位址: " + jsURL );


        Common.debugPrintln( "開始解析title和wholeTitle :" );


        Common.debugPrintln( "作品名稱(title) : " + getTitle() );


        Common.debugPrintln( "作品+章節名稱(wholeTitle) : " + getWholeTitle() );


    }

    @Override
    public void parseComicURL()
    { // parse URL and save all URLs in comicURL
        // 先取得後面的下載伺服器網址

        String allPageString = getAllPageString( webSite );

        int beginIndex = 0;
        int endIndex = 0;

        beginIndex = allPageString.indexOf( "var sFiles" );
        beginIndex = allPageString.indexOf( "\"", beginIndex ) + 1;
        endIndex = allPageString.indexOf( "\"", beginIndex );

        String tempString = allPageString.substring( beginIndex, endIndex );
        String[] urlTokens = tempString.split( "\\|" );

        // 取得頁數
        comicURL = new String[ urlTokens.length ];


        // 再取得後面的下載伺服器網址
        beginIndex = allPageString.indexOf( "var sPath", beginIndex );
        beginIndex = allPageString.indexOf( "\"", beginIndex ) + 1;
        endIndex = allPageString.indexOf( "\"", beginIndex );
        serverNo = Integer.parseInt(
                allPageString.substring( beginIndex, endIndex ) );
        Common.debugPrintln( "server No = " + serverNo );

        // 取得JS位址
        beginIndex = allPageString.indexOf( "c.js\"></script>", beginIndex );
        if ( beginIndex > 0 )
        { // 新定義的js檔
            endIndex = beginIndex + 4;
            beginIndex = allPageString.lastIndexOf( "\"", endIndex - 2 ) + 1;
            Common.debugPrintln( beginIndex + " " + endIndex );
            jsURL = "http://www.cococomic.com" + allPageString.substring( beginIndex, endIndex );


            Common.downloadFile( jsURL, SetUp.getTempDirectory(), jsName, false, "" );
            String allJsString = Common.getFileString( SetUp.getTempDirectory(), jsName );

            String tempToken = "[" + (serverNo - 1);
            beginIndex = allJsString.indexOf( tempToken );
            beginIndex = allJsString.indexOf( "\"", beginIndex ) + 1;
            endIndex = allJsString.indexOf( "\"", beginIndex );
            baseURL = allJsString.substring( beginIndex, endIndex );
        }
        else
        {
            Common.downloadFile( jsURL, SetUp.getTempDirectory(), jsName, false, "" );
            String allJsString = Common.getFileString( SetUp.getTempDirectory(), jsName );

            beginIndex = allJsString.indexOf( "\"" ) + 1;
            endIndex = allJsString.indexOf( "\"", beginIndex );

            tempString = allJsString.substring( beginIndex, endIndex );
            String[] serverTokens = tempString.split( "\\|" );
            baseURL = serverTokens[serverNo - 1];
        }

        Common.debugPrintln( "JS位址：" + jsURL );
        Common.debugPrintln( "下載伺服器位址: " + baseURL );

        for ( int i = 0; i < comicURL.length; i++ )
        {
            comicURL[i] = baseURL + Common.getFixedChineseURL( urlTokens[i] );
            Common.debugPrintln( i + " : " + comicURL[i] );

        }
        //System.exit(0);
    }
}

