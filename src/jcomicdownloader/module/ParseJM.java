/*
 ----------------------------------------------------------------------------------------------------
 Program Name : JComicDownloader
 Authors  : surveyorK
 Last Modified : 2012/11/25
 ----------------------------------------------------------------------------------------------------
 ChangeLog:
 5.09: 修復jmymh解析位址錯誤的問題。
 4.13: 1. 修復jmymh解析集數錯誤的問題。
 4.01: 1. 修復jmymh無法下載的問題。（加入伺服器檢查機制）
 *  3.15: 1. 新增對www.jmymh.com的支援。
 ----------------------------------------------------------------------------------------------------
 */
package jcomicdownloader.module;

import java.util.ArrayList;
import java.util.List;
import jcomicdownloader.SetUp;
import jcomicdownloader.enums.Site;
import jcomicdownloader.tools.Common;

public class ParseJM extends ParseOnlineComicSite
{

    private int radixNumber; // use to figure out the name of pic
    private String jsName;
    protected String indexName;
    protected String indexEncodeName;
    protected String baseURL;

    /**

     @author user
     */
    public ParseJM()
    {
        siteID = Site.JM;
        siteName = "JM";
        indexName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_jm_parse_", "html" );
        indexEncodeName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_jm_encode_parse_", "html" );

        jsName = "index_jm.js";
        radixNumber = 1591371; // default value, not always be useful!!

        baseURL = "http://www.jmymh.com";
    }

    public ParseJM( String webSite, String titleName )
    {
        this();
        this.webSite = webSite;
        this.title = titleName;
    }

    @Override
    public void setParameters()
    {
        Common.debugPrintln( "開始解析各參數 :" );
        Common.debugPrintln( "開始解析title和wholeTitle :" );

        if ( getWholeTitle() == null || getWholeTitle().equals( "" ) )
        {
            // 因為正常解析不需要用到單集頁面，所以給此兩行放進來
            Common.downloadFile( webSite, SetUp.getTempDirectory(), indexName, false, "" );
            //Common.newEncodeFile( SetUp.getTempDirectory(), indexName, indexEncodeName );

            String allPageString = Common.getFileString( SetUp.getTempDirectory(), indexName );

            int beginIndex = allPageString.indexOf( "id='spt2'" );
            beginIndex = allPageString.indexOf( ">", beginIndex ) + 1;
            int endIndex = allPageString.indexOf( "<", beginIndex );
            String tempTitleString = allPageString.substring( beginIndex, endIndex ).replaceAll( "&nbsp;", "" );

            setWholeTitle( getVolumeWithFormatNumber( Common.getStringRemovedIllegalChar(
                    Common.getTraditionalChinese( tempTitleString.trim() ) ) ) );
        }

        Common.debugPrintln( "作品名稱(title) : " + getTitle() );
        Common.debugPrintln( "章節名稱(wholeTitle) : " + getWholeTitle() );
    }

    @Override
    public void parseComicURL()
    { // parse URL and save all URLs in comicURL  //

        // 先取得所有的下載伺服器網址
        int beginIndex = 0, endIndex = 0;



        Common.debugPrint( "開始解析這一集有幾頁 : " );
        String allPageString = getAllPageString( webSite );

        beginIndex = allPageString.indexOf( "var sFiles" );
        beginIndex = allPageString.indexOf( "\"", beginIndex ) + 1;
        endIndex = allPageString.indexOf( "\"", beginIndex );

        String tempString = allPageString.substring( beginIndex, endIndex );

        String[] urlStrings = tempString.split( "\\|" );
        totalPage = urlStrings.length;

        Common.debugPrintln( "共 " + totalPage + " 頁" );
        comicURL = new String[ totalPage ];

        // 開始解析中間位址
        beginIndex = allPageString.indexOf( "var sPath" );
        beginIndex = allPageString.indexOf( "\"", beginIndex ) + 1;
        endIndex = allPageString.indexOf( "\"", beginIndex );
        String midPicURL = allPageString.substring( beginIndex, endIndex );

        // 設定伺服器位址
        String serverURL = "http://mh.jmymh.jmmh.net:2012/";
        String serverURL1 = "http://ltsvr.jmydm.jmmh.net:2012/";
        String serverURL2 = "http://mhsvr.jmmh.net:2012/";
        String serverURL3 = "http://wt.jmydm.net:2012/";
        String nowServerURL = ""; // 這次要使用的伺服器

        if ( Common.urlIsOK( serverURL + midPicURL + urlStrings[0] ) )
        {
            nowServerURL = serverURL;
        }
        else if ( Common.urlIsOK( serverURL1 + midPicURL + urlStrings[0] ) )
        {
            nowServerURL = serverURL1;
        }
        else if ( Common.urlIsOK( serverURL2 + midPicURL + urlStrings[0] ) )
        {
            nowServerURL = serverURL2;
        }
        else if ( Common.urlIsOK( serverURL3 + midPicURL + urlStrings[0] ) )
        {
            nowServerURL = serverURL3;
        }


        int p = 0; // 目前頁數
        for ( int i = 1; i <= totalPage && Run.isAlive; i++ )
        {
            comicURL[p++] = nowServerURL + midPicURL + urlStrings[i - 1]; // 存入每一頁的網頁網址
            //Common.debugPrintln( p + " " + comicURL[p - 1] ); // debug

        }
        //System.exit( 0 ); // debug
    }

    @Override
    public String getAllPageString( String urlString )
    {
        String indexName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_jm_", "html" );
        //String indexEncodeName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_hh_encode_", "html" );

        Common.downloadFile( urlString, SetUp.getTempDirectory(), indexName, false, "" );
        //Common.newEncodeFile( SetUp.getTempDirectory(), indexName, indexEncodeName );

        return Common.getFileString( SetUp.getTempDirectory(), indexName );
    }

    @Override
    public boolean isSingleVolumePage( String urlString )
    {
        // ex. http://www.jmymh.com/jmymhcomic/jm2AEBE43CD4CCA753/jv488BB99ED733DC09/
        if ( Common.getAmountOfString( urlString, "/" ) > 5 )
        {
            return true;
        }
        else // ex. http://www.jmymh.com/jmymhcomic/jm2AEBE43CD4CCA753/
        {
            return false;
        }
    }

    public String getMainUrlFromSingleVolumeUrl( String volumeURL )
    {
        // ex. http://www.jmymh.com/jmymhcomic/jm2AEBE43CD4CCA753/jv488BB99ED733DC09/轉為
        //    http://www.jmymh.com/jmymhcomic/jm2AEBE43CD4CCA753/

        // 將最後面的/拿掉
        if ( volumeURL.matches( "(?).*/" ) )
        {
            volumeURL = volumeURL.substring( 0, volumeURL.length() - 1 );
        }

        int endIndex = volumeURL.lastIndexOf( "/" ) + 1;
        String mainPageURL = volumeURL.substring( 0, endIndex );

        Common.debugPrintln( "MAIN_URL: " + mainPageURL );

        return mainPageURL;
    }

    @Override
    public String getTitleOnMainPage( String urlString, String allPageString )
    {
        int beginIndex = allPageString.indexOf( "<h1>" ) + 5;
        beginIndex = allPageString.indexOf( ">", beginIndex ) + 1;
        int endIndex = allPageString.indexOf( "</a>", beginIndex );
        String title = allPageString.substring( beginIndex, endIndex ).trim();

        return Common.getStringRemovedIllegalChar( Common.getTraditionalChinese( title ) );
    }

    @Override
    public List<List<String>> getVolumeTitleAndUrlOnMainPage( String urlString, String allPageString )
    {
        // combine volumeList and urlList into combinationList, return it.

        List<List<String>> combinationList = new ArrayList<List<String>>();
        List<String> urlList = new ArrayList<String>();
        List<String> volumeList = new ArrayList<String>();

        int beginIndex = allPageString.indexOf( "class=\"cVol\"" );
        int endIndex = allPageString.indexOf( "class=\"main block Vol_list\"", beginIndex );

        String tempString = allPageString.substring( beginIndex, endIndex );

        int volumeCount = tempString.split( "href='/" ).length - 1;

        String volumeTitle = "";
        beginIndex = endIndex = 0;
        for ( int i = 0; i < volumeCount; i++ )
        {

            // 取得單集位址
            beginIndex = tempString.indexOf( "href='/", beginIndex );
            beginIndex = tempString.indexOf( "'", beginIndex ) + 1;
            endIndex = tempString.indexOf( "'", beginIndex );
            urlList.add( baseURL + tempString.substring( beginIndex, endIndex ) );

            // 取得單集名稱
            beginIndex = tempString.indexOf( ">", beginIndex ) + 1;

            endIndex = tempString.indexOf( "<", beginIndex );
            volumeTitle = tempString.substring( beginIndex, endIndex );
            volumeList.add( getVolumeWithFormatNumber( Common.getStringRemovedIllegalChar(
                    Common.getTraditionalChinese( volumeTitle.trim() ) ) ) );
        }

        totalVolume = volumeCount;
        Common.debugPrintln( "共有" + totalVolume + "集" );

        combinationList.add( volumeList );
        combinationList.add( urlList );

        return combinationList;
    }
}
