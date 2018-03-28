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
// www.99comic.com 變繁體版了，所以套用cococomic的方式解析
public class Parse99ComicTC extends ParseCocoTC
{

    public Parse99ComicTC()
    {
        super();
        enumName = "NINENINE_COMIC_TC";
	parserName=this.getClass().getName();
        regexs=new String[]{"(?s).*www.99comic.com(?s).*", "(?s).*www.999comic(?s).*"};
	siteID=Site.formString("NINENINE_COMIC_TC");
        siteName = "www.99comic.com";
        //downloadBefore=true;

        jsURL = "http://www.99comic.com/script/viewhtml.js";
        baseURL = "http://www.999comic.com";
    }

    @Override // 從網址判斷是否為單集頁面(true) 還是主頁面(false)
    public boolean isSingleVolumePage( String urlString )
    {
        // ex. http://www.999comic.com/comic/12110/3d503dfa080ce43fb756686877b21e56.html
        if ( urlString.indexOf( ".html" ) > 0 )
        {
            return true;
        }
        else // ex. http://www.999comic.com/comic/12110/
        {
            return false;
        }
    }

    @Override // 從主頁面取得title(作品名稱)
    public String getTitleOnMainPage( String urlString, String allPageString )
    {
        Common.debugPrintln( "開始由主頁面位址取得title：" );

        int beginIndex = allPageString.indexOf( "<h1>" );
        beginIndex = allPageString.indexOf( " href=", beginIndex ) + 1;
        beginIndex = allPageString.indexOf( ">", beginIndex ) + 1;
        int endIndex = allPageString.indexOf( "</a>", beginIndex );

        title = allPageString.substring( beginIndex, endIndex ).trim(); // .split( "\\s" )[0];


        return Common.getStringRemovedIllegalChar( Common.getTraditionalChinese( title ) );
    }

    @Override // 從主頁面取得所有集數名稱和網址
    public List<List<String>> getVolumeTitleAndUrlOnMainPage( String urlString, String allPageString )
    {

        List<List<String>> combinationList = new ArrayList<List<String>>();
        List<String> urlList = new ArrayList<String>();
        List<String> volumeList = new ArrayList<String>();

        int beginIndex = allPageString.indexOf( "chapter-list" );
        int endIndex = allPageString.indexOf( "similar-list", beginIndex );

        String tempString = allPageString.substring( beginIndex, endIndex );

        totalVolume = tempString.split( "href=\"" ).length - 1;

        beginIndex = endIndex = 0;
        for ( int count = 0; count < totalVolume; count++ )
        {

            beginIndex = tempString.indexOf( "href=", beginIndex );
            beginIndex = tempString.indexOf( "\"", beginIndex ) + 1;
            endIndex = tempString.indexOf( "\"", beginIndex );
            urlList.add( baseURL + tempString.substring( beginIndex, endIndex ) );
            
            beginIndex = tempString.indexOf( "title=", beginIndex );
            beginIndex = tempString.indexOf( "\"", beginIndex ) + 1;
            endIndex = tempString.indexOf( "\"", beginIndex );
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

        beginIndex = allPageString.indexOf( "var cInfo" );
        beginIndex = allPageString.indexOf( "[", beginIndex ) + 1;
        endIndex = allPageString.indexOf( "]", beginIndex );

        String tempString = allPageString.substring( beginIndex, endIndex );
        String[] urlTokens = tempString.split( "," );
        
        Common.debugPrintln( "共有 " + urlTokens.length + " 頁" );

        // 取得頁數
        comicURL = new String[ urlTokens.length ];

        if (false)
        {
            // 再取得後面的下載伺服器網址
            baseURL = "http://www.999comic.com/g.php?";

            Common.debugPrintln( "下載伺服器位址: " + baseURL );

            for ( int i = 0; i < comicURL.length; i++ )
            {            
                comicURL[i] = baseURL + Common.getFixedChineseURL(urlTokens[i].replaceAll("'", "").replaceAll(".webp", "?"));
                Common.debugPrintln( i + " : " + comicURL[i] );
            }
        }
        else
        {
            Common.debugPrintln("site:" + webSite );
            
            int begin = webSite.lastIndexOf("/") + 1;
            int end = webSite.lastIndexOf(".");
            
            String partURL = webSite.substring(begin, end);
            
            baseURL = "https://i1.999comics.com/" + partURL + "/";
            
            for ( int i = 0; i < comicURL.length; i++ )
            {
                comicURL[i] = baseURL + Common.getFixedChineseURL(urlTokens[i].replaceAll("'", "").replace("E677572603D2C21991", "D17063D3")) + ".jpg";
                Common.debugPrintln( i + " : " + comicURL[i] );
            }
        }    
        
        //System.exit(0);
    }
}

