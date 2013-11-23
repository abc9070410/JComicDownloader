/*
 ----------------------------------------------------------------------------------------------------
 Program Name : JComicDownloader
 Authors  : surveyorK
 Last Modified : 2013/7/1
 ----------------------------------------------------------------------------------------------------
 ChangeLog:
 5.17: 修復xxbh解析錯誤的問題。
 5.16: 修復xxbh位址解析錯誤的問題。
 5.14: 修復xxbh伺服器位址失效的問題。
 5.13: 修復xxbh無法下載的問題。
修復xxbh圖片伺服器位址解析錯誤的問題。
 5.12: 修復xxbh解析錯誤的問題。
 5.10: 1. 修復xxbh解析錯誤的問題。
 5.01: 1. 修復xxbh因網站改版而無法解析的問題。
 4.14: 1. 修復xxbh因網站改版而無法解析的問題。
 *  4.03: 1. 新增對xxbh的支援。
 ----------------------------------------------------------------------------------------------------
 */
package jcomicdownloader.module;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import jcomicdownloader.tools.*;
import jcomicdownloader.enums.*;
import java.util.*;
import jcomicdownloader.SetUp;
import jcomicdownloader.encode.Encoding;
import jcomicdownloader.encode.Zhcode;

public class ParseXXBH extends ParseOnlineComicSite
{

    private int radixNumber; // use to figure out the name of pic
    private String jsName;
    protected String indexName;
    protected String indexEncodeName;
    protected String baseURL;

    /**

     @author user
     */
    public ParseXXBH()
    {
        siteID = Site.XXBH;
        siteName = "xxbh";
        indexName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_xxbh_parse_", "html" );
        indexEncodeName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_xxbh_encode_parse_", "html" );

        jsName = "index_xxbh.js";
        radixNumber = 1591371; // default value, not always be useful!!

        baseURL = "http://xxbh.net";
    }

    public ParseXXBH( String webSite, String titleName )
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

        Common.downloadFile( webSite, SetUp.getTempDirectory(), indexName, false, "" );

        if ( getWholeTitle() == null || getWholeTitle().equals( "" ) )
        {
            // 因為正常解析不需要用到單集頁面，所以給此兩行放進來

            String allPageString = Common.getFileString( SetUp.getTempDirectory(), indexName );

            int beginIndex = allPageString.indexOf( "</a>->" ) + 1;
            beginIndex = allPageString.indexOf( "</a>->", beginIndex ) + 6;
            int endIndex = allPageString.indexOf( "->", beginIndex );
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
        // 先取得前面的下載伺服器網址

        int beginIndex = 0;
        int endIndex = 0;
        String tempString = "";
        String allJSPageString = "";

        String allPageString = Common.getFileString( SetUp.getTempDirectory(), indexName );


        // 找出全部的伺服器位址
        Common.debugPrintln( "開始解析全部的伺服器位址" );


        // 首先要下載第二份js檔

        /*
         beginIndex = Common.getIndexOfOrderKeyword( allPageString, " src=", 5 );
         beginIndex = allPageString.indexOf( "\"", beginIndex ) + 1;
         endIndex = allPageString.indexOf( "\"", beginIndex );
         String jsURL2 = allPageString.substring( beginIndex, endIndex );
         */
        
        // 取得v3_cont_v130404.js
        beginIndex = allPageString.indexOf( "_cont_" );
        beginIndex = allPageString.lastIndexOf( "http:", beginIndex );
        endIndex = allPageString.indexOf( "\"", beginIndex );
        String tempURL = allPageString.substring( beginIndex, endIndex );
        
        Common.debugPrintln( "第1個js位址: " + tempURL );
        
        allJSPageString = getAllPageString( tempURL );
        
        // 從v3_cont_v130404.js中取得記載伺服器位址的js, ex. http://img_v1.dm08.com/img_v1/fdc_130404.js
        beginIndex = allJSPageString.indexOf( "/fdc_" );
        beginIndex = allJSPageString.lastIndexOf( "http:", beginIndex );
        endIndex = allJSPageString.indexOf( "'", beginIndex );
        String jsURL2 = allJSPageString.substring( beginIndex, endIndex );
        
        Common.debugPrintln( "第2個js位址: " + jsURL2 );


        // 開始解析js檔案
        allJSPageString = getAllPageString( jsURL2 );

        // 取出全部的前面位址（伺服器位址+資料夾位置）
        int serverAmount = allJSPageString.split( "http://" ).length - 1;
        String[] frontPicURLs = new String[ serverAmount ];

        beginIndex = endIndex = 0;
        for ( int i = 0; i < serverAmount; i++ )
        {
            beginIndex = allJSPageString.indexOf( "http://", beginIndex );
            endIndex = allJSPageString.indexOf( "\"", beginIndex );
            frontPicURLs[i] = allJSPageString.substring( beginIndex, endIndex );
            beginIndex = endIndex;
        }

        //System.exit( 0 );

        //Common.debugPrint( "開始解析這一集有幾頁 : " );

        // 首先要下載js檔
        beginIndex = allPageString.indexOf( "/coojs/" );
        beginIndex = allPageString.lastIndexOf( "\"", beginIndex ) + 1;
        endIndex = allPageString.indexOf( "\"", beginIndex );
        String jsURL = allPageString.substring( beginIndex, endIndex );

        // 開始解析js檔案
        Common.debugPrintln( "開始解析後面部份的位址" );
        String referURL = webSite + "?page=1";
        Common.simpleDownloadFile( jsURL, SetUp.getTempDirectory(), indexName, referURL );
        allJSPageString = Common.getFileString( SetUp.getTempDirectory(), indexName );
        
        //String[] picNames = getPicNames( allJSPageString );
        
        String decodeJS = getDecodeJS( allJSPageString );
        Common.debugPrintln( "DECODE: " + decodeJS );
        
        //System.exit( 0 );
        
        beginIndex = decodeJS.indexOf( " msg" );
        beginIndex = decodeJS.indexOf( "'", beginIndex ) + 1;
        endIndex = decodeJS.indexOf( "'", beginIndex );
        tempString = decodeJS.substring( beginIndex, endIndex );
        

        // 取得每張圖片網址的後面部份
        String[] backPicURLs = tempString.split( "\\|" );

        // 看有幾張圖片
        totalPage = backPicURLs.length;
        Common.debugPrintln( "共 " + totalPage + " 頁" );
        comicURL = new String[ totalPage ];

        Common.debugPrintln( "開始解析前面部份的位址" );
        beginIndex = decodeJS.indexOf( " img_s" );
        beginIndex = decodeJS.indexOf( "=", beginIndex ) + 1;
        endIndex = decodeJS.indexOf( ";", beginIndex );
        tempString = decodeJS.substring( beginIndex, endIndex ).trim();
        int serverId = Integer.parseInt( tempString.trim() );

        Common.debugPrintln( "第一張圖片位址：" + frontPicURLs[serverId - 1] + backPicURLs[0] );

        beginIndex = endIndex = 0;
        for ( int p = 0; p < totalPage && Run.isAlive; p++ )
        {

            comicURL[p] = frontPicURLs[serverId - 1] + backPicURLs[p];

            //使用最簡下載協定，加入refer始可下載
            referURL = webSite + "?page=" + (p + 1);
            singlePageDownloadUsingSimple( getTitle(), getWholeTitle(), comicURL[p], totalPage, p + 1, referURL );
            if ( !Common.existPicFile( getDownloadDirectory(), p )
                    || !Common.existPicFile( getDownloadDirectory(), p + 1 ) )
            {
                //singlePageDownloadUsingRefer( getTitle(), getWholeTitle(), comicURL[p], totalPage, p + 1, 0, referURL );
                singlePageDownloadUsingSimple( getTitle(), getWholeTitle(), comicURL[p], totalPage, p + 1, referURL );
            }
            //Common.debugPrintln( ( p + 1 ) + " " + comicURL[p] + " " + referURL ); // debug
        }
        //System.exit( 0 ); // debug
    }
    
    
    // char's ascii code -> String .
public String fromCharCode(int... codePoints) {
    return new String(codePoints, 0, codePoints.length);
}
 
// ex. 10 -> a .
public String e( int c, int radix )
{
    int a = 62; //radix;
    return (c < a ? "" : e( ( c / a ), a ) ) + ((c = c % a) > 35 ? fromCharCode(c + 29) : Long.toString( (long) c, 36));
}
 
// ex. a -> 10 .
public int getIndexOfE( String eStr, int radix )
{
    for ( int i = 0; i < radix; i ++ )
    {
        String s = e( i, radix );
        //Common.debugPrintln( "-> " + s )
        if ( eStr.equals( s ) )
        {
            return i;
        }
        else
        {
            //Common.debugPrintln( " " + eStr + " : " + e( i ) );
        }
    }
    //Common.debugPrintln( "not found: " + eStr );
    return -1;
}
 
// ex. 0/i.1 ... -> 201306/220131103le3senmlyq.jpg ...
public String getDecode( String[] codes, String encode )
{
    String decode = "";
 
    //for ( int i = 0; i < encode.length(); i ++ )
    
    int i = 0;
    while ( i < encode.length() )
    {   
        String a = encode.substring( i, i + 1 );
        String b = "";
        if ( i + 2 < encode.length() )
            b = encode.substring( i + 1, i + 2 );
        else
            b = "++++++++++++";
        boolean decodeA = false;
        boolean decodeB = false;
        
        i++;
        
        if ( ( a.charAt( 0 ) >= '0' && a.charAt( 0 ) <= '9' ) ||
             ( a.charAt( 0 ) >= 'a' && a.charAt( 0 ) <= 'z' ) ||
             ( a.charAt( 0 ) >= 'A' && a.charAt( 0 ) <= 'Z' ) )
        {
            decodeA = true;
        }
        if ( ( b.charAt( 0 ) >= '0' && b.charAt( 0 ) <= '9' ) ||
             ( b.charAt( 0 ) >= 'a' && b.charAt( 0 ) <= 'z' ) ||
             ( b.charAt( 0 ) >= 'A' && b.charAt( 0 ) <= 'Z' ) )
        {
            decodeB = true;
        }
        
        if ( decodeA && decodeB )
        {
            decode += codes[getIndexOfE( a + b, codes.length )];   
            i++;
        }
        else if ( decodeA )
        {
            decode += codes[getIndexOfE( a, codes.length )];    
        }
        else
        {
            decode += a;
        }
    }
    
    return decode;
}

public String getDecodeJS( String data )
{
    int beginIndex, endIndex;
    
    Common.debugPrintln( "DATA: " + data );
     
    beginIndex = data.indexOf( "}(" );
    beginIndex = data.indexOf( "'", beginIndex ) + 1;
    endIndex = data.indexOf( ",", beginIndex );
    endIndex = data.lastIndexOf( "'", endIndex );
    String encodePic = data.substring( beginIndex, endIndex );
    Common.debugPrintln( "ENCODE: " + encodePic );
    
    endIndex = data.indexOf( "split", beginIndex );
    endIndex = data.lastIndexOf( "'", endIndex );
    beginIndex = data.lastIndexOf( "'", endIndex - 1 ) + 1;
    String[] codes = data.substring( beginIndex, endIndex ).split( "\\|" );
    for ( int i = 0; i < codes.length; i ++ )
    {
        //Common.debugPrintln( "" + i+ " " + codes[i] );
    }
    return getDecode( codes, encodePic );    
}


    @Override
    public String getAllPageString( String urlString )
    {
        String indexName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_xxbh_", "html" );
        //String indexEncodeName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_xxbh_encode_", "html" );

        Common.downloadFile( urlString, SetUp.getTempDirectory(), indexName, false, "" );
        Common.newEncodeFile( SetUp.getTempDirectory(), indexName, indexEncodeName, Encoding.GB2312 );

        return Common.getFileString( SetUp.getTempDirectory(), indexEncodeName );
    }

    @Override
    public boolean isSingleVolumePage( String urlString )
    {
        // ex. http://comic.xxbh.net/201205/223578.html
        if ( Common.getAmountOfString( urlString, "/" ) > 4 )
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public String getMainUrlFromSingleVolumeUrl( String volumeURL )
    {
        // ex. http://comic.xxbh.net/201205/223578.html轉為
        //    http://comic.xxbh.net/colist_223560.html

        String allPageString = getAllPageString( volumeURL );

        int beginIndex = allPageString.indexOf( "<h1>" );
        beginIndex = allPageString.indexOf( "\"", beginIndex ) + 1;
        int endIndex = allPageString.indexOf( "\"", beginIndex );
        String mainPageURL = baseURL + allPageString.substring( beginIndex, endIndex );

        Common.debugPrintln( "MAIN_URL: " + mainPageURL );

        return mainPageURL;
    }

    @Override
    public String getTitleOnSingleVolumePage( String urlString )
    {
        String mainUrlString = getMainUrlFromSingleVolumeUrl( urlString );

        return getTitleOnMainPage( mainUrlString, getAllPageString( mainUrlString ) );
    }

    @Override
    public String getTitleOnMainPage( String urlString, String allPageString )
    {
        int beginIndex = allPageString.indexOf( "class=\"l21\"" );
        beginIndex = allPageString.indexOf( "alt=", beginIndex );
        beginIndex = allPageString.indexOf( "\"", beginIndex ) + 1;
        int endIndex = allPageString.indexOf( "\"", beginIndex );
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

        int beginIndex = allPageString.indexOf( "class=\"b-d-div\"" );
        int endIndex = allPageString.indexOf( "</div>", beginIndex );

        String tempString = allPageString.substring( beginIndex, endIndex );

        int volumeCount = tempString.split( " href=" ).length - 1;
        totalVolume = volumeCount;
        Common.debugPrintln( "共有" + totalVolume + "集" );

        String volumeURL = "";
        String volumeTitle = "";
        beginIndex = endIndex = 0;
        for ( int i = 0; i < volumeCount; i++ )
        {
            // 取得單集位址
            beginIndex = tempString.indexOf( " href=", beginIndex );
            beginIndex = tempString.indexOf( "\"", beginIndex ) + 1;
            endIndex = tempString.indexOf( "\"", beginIndex );
            volumeURL = tempString.substring( beginIndex, endIndex );

            if ( !volumeURL.matches( ".*/s/.*" ) && volumeURL.matches( "/(?s).*" ) )
            { // 代表有非集數的網址在亂入
                urlList.add( baseURL + volumeURL );
                // 取得單集名稱
                beginIndex = tempString.indexOf( ">", beginIndex ) + 1;
                endIndex = tempString.indexOf( "</a>", beginIndex );
                volumeTitle = tempString.substring( beginIndex, endIndex );
                volumeTitle = volumeTitle.replaceAll( "<.*>", "" );
                volumeList.add( getVolumeWithFormatNumber( Common.getStringRemovedIllegalChar(
                        Common.getTraditionalChinese( volumeTitle.trim() ) ) ) );
            }
        }

        combinationList.add( volumeList );
        combinationList.add( urlList );

        return combinationList;
    }
}
