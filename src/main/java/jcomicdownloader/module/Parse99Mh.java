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
public class Parse99Mh extends ParseNINENINE
{

    public Parse99Mh()
    {
        super();
        enumName = "NINENINE_MH";
        parserName=this.getClass().getName();
        regexs=new String[]{"(?s).*99mh.com(?s).*"};
        siteID=Site.formString("NINENINE_MH");
        siteName = "99mh";

        jsURL = "http://99mh.com/script/ds.js";
    }
    
    @Override
    public void parseComicURL()
    {  
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
        //System.exit( 0 );

        allPageString = getAllPageString( webSite );

        beginIndex = 0;
        endIndex = 0;

        beginIndex = allPageString.indexOf( "var sFiles" );
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
        
        //System.exit( 0 );
    }
    @Override 
    public String getTitleOnMainPage( String urlString, String allPageString )
    {
        Common.debugPrintln( "開始由主頁面位址取得title：" );

        String[] tokens = allPageString.split( "\\s*=\\s+|\'" );
        String title = "";

        int index = 0;
        for ( ; index < tokens.length; index++ )
        {
            if ( tokens[index].matches( "(?s).*wumiiTitle\\s*" ) )
            { // ex. var wumiiTitle = "食夢者";
                title = tokens[index + 2];
                break;
            }
        }
        return Common.getStringRemovedIllegalChar( Common.getTraditionalChinese( title ) );
    }
    @Override // 下載網址指向的網頁，全部存入String後回傳
    public String getAllPageString( String urlString )
    {
        String indexName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_99_", "html" );
        String indexEncodeName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_99_encode_", "html" );

        System.out.println( "URL: " + urlString );
        Common.downloadFile( urlString, SetUp.getTempDirectory(), indexName, false, "" );

        //Common.newEncodeFile( SetUp.getTempDirectory(), indexName, indexEncodeName, Zhcode.GBK );
        return Common.getFileString( SetUp.getTempDirectory(), indexName );

    }

    @Override // 從網址判斷是否為單集頁面(true) 還是主頁面(false)
    public boolean isSingleVolumePage( String urlString )
    {
        // ex. http://99mh.com/comic/6342/119116/
        if ( Common.getAmountOfString( urlString, "/" ) > 5 )
        {
            return true;
        }
        else // ex. http://99mh.com/comic/6342/
        {
            return false;
        }
    }

    @Override // 從主頁面取得所有集數名稱和網址
    public List<List<String>> getVolumeTitleAndUrlOnMainPage( String urlString, String allPageString )
    {

        List<List<String>> combinationList = new ArrayList<List<String>>();
        List<String> urlList = new ArrayList<String>();
        List<String> volumeList = new ArrayList<String>();

        int beginIndex = 0;
        int endIndex = 0;
        String volumeString = "";

        beginIndex = allPageString.indexOf( "subBookList" );
        endIndex = allPageString.indexOf( "cCRHtm", beginIndex );

        String tempString = allPageString.substring( beginIndex, endIndex );


        totalVolume = tempString.split( "href='http:" ).length - 1;
        beginIndex = endIndex = 0;

        for ( int i = 0; i < totalVolume; i++ )
        {
            // 取得位址
            beginIndex = tempString.indexOf( "href='http:", beginIndex );
            beginIndex = tempString.indexOf( "'", beginIndex ) + 1;
            endIndex = tempString.indexOf( "'", beginIndex );
            urlList.add( tempString.substring( beginIndex, endIndex ) );

            beginIndex = tempString.indexOf( ">", endIndex ) + 1;
            endIndex = tempString.indexOf( "</a>", beginIndex );
            volumeString = tempString.substring( beginIndex, endIndex );
            //trim dummy
            endIndex = volumeString.indexOf( "<img src",0);
            if(endIndex != -1)
                volumeString = volumeString.substring(0,endIndex);
            volumeList.add( getVolumeWithFormatNumber(
                    Common.getStringRemovedIllegalChar(
                    Common.getTraditionalChinese( volumeString.trim() ) ) ) );
        }

        combinationList.add( volumeList );
        combinationList.add( urlList );

        return combinationList;
    }
}
