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
import jcomicdownloader.tools.Common;

/**
 *
 * @author apple
 */
public class Parse99MangaTC extends Parse99ComicTC
{

    public Parse99MangaTC()
    {
        super();
        enumName = "NINENINE_MANGA_TC";
        parserName=this.getClass().getName();
        regexs=new String[]{"(?s).*dm.99manga.com(?s).*"};
        siteID=Site.formString("NINENINE_MANGA_TC");
        siteName = "dm.99manga.com";

        baseURL = "http://dm.99manga.com";
        jsURL = "http://dm.99manga.com/script/ds.js";
    }

    @Override
    public void parseComicURL()
    { // parse URL and save all URLs in comicURL
        // 先取得後面的下載伺服器網址
        String allPageString = getAllPageString( jsURL );

        int beginIndex = 0;
        int endIndex = 0;
        
        beginIndex = allPageString.indexOf( "var sDS" );
        beginIndex = allPageString.indexOf( "\"", beginIndex ) + 1;
        endIndex = allPageString.indexOf( "\"", beginIndex );

        String tempString = allPageString.substring( beginIndex, endIndex );
        String[] urlTokens = tempString.split( "\\|" );
        
        String basePicURL = urlTokens[urlTokens.length-1];
        Common.debugPrintln( "下載伺服器位址: " + basePicURL );

        allPageString = getAllPageString( webSite );

        beginIndex = 0;
        endIndex = 0;

        beginIndex = allPageString.indexOf( "var sFiles=" );
        beginIndex = allPageString.indexOf( "\"", beginIndex ) + 1;
        endIndex = allPageString.indexOf( "\"", beginIndex );

        tempString = allPageString.substring( beginIndex, endIndex );
        //decode
        urlTokens = Common.unsuan99(tempString);

        // 取得頁數
        comicURL = new String[ urlTokens.length ];
        refers = new String[ urlTokens.length ];
        
        // 取得目錄
        beginIndex = allPageString.indexOf( "var sPath" );
        beginIndex = allPageString.indexOf( "\"", beginIndex ) + 1;
        endIndex = allPageString.indexOf( "\"", beginIndex );
        String dicString = allPageString.substring( beginIndex, endIndex );

        for ( int i = 0; i < comicURL.length; i++ )
        {
            comicURL[i] = basePicURL + dicString + Common.getFixedChineseURL( urlTokens[i] );
            Common.debugPrintln( i + " : " + comicURL[i] );
            refers[i] = webSite;
        }
        //System.exit(0);
    }

    @Override // 下載網址指向的網頁，全部存入String後回傳
    public String getAllPageString( String urlString )
    {
        String indexName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_99_", "html" );
        //String indexEncodeName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_99_encode_", "html" );

        System.out.println( "URL: " + urlString );
        Common.downloadFile( urlString, SetUp.getTempDirectory(), indexName, false, "" );

        //Common.newEncodeFile( SetUp.getTempDirectory(), indexName, indexEncodeName, Zhcode.GBK );
        return Common.getFileString( SetUp.getTempDirectory(), indexName );

    }

    @Override // 從網址判斷是否為單集頁面(true) 還是主頁面(false)
    public boolean isSingleVolumePage( String urlString )
    {
        // ex. http://dm.99manga.com/comic/19866/139139/
        if ( urlString.matches( "(?s).*/comic/\\d+/\\d+/" ) )
        {
            return true;
        }
        else // ex. http://dm.99manga.com/comic/11843/
        {
            return false;
        }
    }

    @Override // 從主頁面取得title(作品名稱)
    public String getTitleOnMainPage( String urlString, String allPageString )
    {
        Common.debugPrintln( "開始由主頁面位址取得title：" );

        int beginIndex = allPageString.indexOf( "wumiiTitle" );
        beginIndex = allPageString.indexOf( "'", beginIndex ) + 1;
        int endIndex = allPageString.indexOf( "'", beginIndex );

        String tempString = allPageString.substring( beginIndex, endIndex );

        return Common.getStringRemovedIllegalChar( Common.getTraditionalChinese( tempString ) );
    }

    @Override // 從主頁面取得所有集數名稱和網址
    public List<List<String>> getVolumeTitleAndUrlOnMainPage( String urlString, String allPageString )
    {

        List<List<String>> combinationList = new ArrayList<List<String>>();
        List<String> urlList = new ArrayList<String>();
        List<String> volumeList = new ArrayList<String>();

        int beginIndex = allPageString.indexOf( "class=\"cVol" );
        int endIndex = allPageString.indexOf( "class=\"cBadge", beginIndex );

         String tempString = allPageString.substring( beginIndex, endIndex );

        totalVolume = tempString.split( "href='http:" ).length - 1;

        beginIndex = endIndex = 0;
        for ( int count = 0; count < totalVolume; count++ )
        {

            beginIndex = tempString.indexOf( "href='http:", beginIndex );
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
}
