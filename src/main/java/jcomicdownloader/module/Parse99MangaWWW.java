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
public class Parse99MangaWWW extends Parse99MangaTC
{

    public Parse99MangaWWW()
    {
        super();
        enumName = "NINENINE_MANGA_WWW";
		parserName=this.getClass().getName();

		siteID=Site.formString("NINENINE_MANGA_WWW");
        siteName = "www.99manga.com";

        baseURL = "http://www.99manga.com";
    }

    @Override // 從網址判斷是否為單集頁面(true) 還是主頁面(false)
    public boolean isSingleVolumePage( String urlString )
    {
        // ex. http://www.99manga.com/manhua/5506z70473/
        if ( urlString.matches( "(?s).*/manhua/(?s).*" ) )
        {
            return true;
        }
        else // ex. http://www.99manga.com/Comic/5506/
        {
            return false;
        }
    }

    @Override // 下載網址指向的網頁，全部存入String後回傳
    public String getAllPageString( String urlString )
    {
        String indexName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_manga_www_", "html" );

        System.out.println( "URL: " + urlString );
        Common.downloadFile( urlString, SetUp.getTempDirectory(), indexName, false, "" );

        // 網頁為簡體版utf8，無須轉碼
        return Common.getFileString( SetUp.getTempDirectory(), indexName );

    }

    @Override // 從主頁面取得所有集數名稱和網址
    public List<List<String>> getVolumeTitleAndUrlOnMainPage( String urlString, String allPageString )
    {

        List<List<String>> combinationList = new ArrayList<List<String>>();
        List<String> urlList = new ArrayList<String>();
        List<String> volumeList = new ArrayList<String>();

        int beginIndex = allPageString.indexOf( "class=\"cVol\"" );
        int endIndex = allPageString.indexOf( "Vol_list", beginIndex );

        String tempString = allPageString.substring( beginIndex, endIndex );

        totalVolume = tempString.split( " href=" ).length - 1;

        beginIndex = endIndex = 0;
        for ( int count = 0; count < totalVolume; count++ )
        {

            beginIndex = tempString.indexOf( " href=", beginIndex );
            beginIndex = tempString.indexOf( "'", beginIndex ) + 1;
            endIndex = tempString.indexOf( "'", beginIndex );
            urlList.add( baseURL + tempString.substring( beginIndex, endIndex ) );

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

