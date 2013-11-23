/*
----------------------------------------------------------------------------------------------------
Program Name : JComicDownloader
Authors  : surveyorK
Last Modified : 2012/5/13
----------------------------------------------------------------------------------------------------
ChangeLog:
 *  4.03: 1. 修復imanhua部份下載錯誤的問題。
 *  4.0: 1. 新增對imanhua的支援。
----------------------------------------------------------------------------------------------------
 */
package jcomicdownloader.module;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import jcomicdownloader.tools.*;
import jcomicdownloader.enums.*;
import java.util.*;
import jcomicdownloader.SetUp;
import jcomicdownloader.encode.Zhcode;

public class ParseImanhua extends ParseOnlineComicSite {

    private int radixNumber; // use to figure out the name of pic
    private String jsName;
    protected String indexName;
    protected String indexEncodeName;
    protected String baseURL;

    /**
    
    @author user
     */
    public ParseImanhua() {
        siteID = Site.IMANHUA;
        siteName = "Imanhua";
        indexName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_imanhua_parse_", "html" );
        indexEncodeName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_imanhua_encode_parse_", "html" );

        jsName = "index_imanhua.js";
        radixNumber = 1591371; // default value, not always be useful!!

        baseURL = "http://www.imanhua.com";
    }

    public ParseImanhua( String webSite, String titleName ) {
        this();
        this.webSite = webSite;
        this.title = titleName;
    }

    @Override
    public void setParameters() {
        Common.debugPrintln( "開始解析各參數 :" );
        Common.debugPrintln( "開始解析title和wholeTitle :" );

        if ( getWholeTitle() == null || getWholeTitle().equals( "" ) ) {
            // 因為正常解析不需要用到單集頁面，所以給此兩行放進來
            Common.downloadFile( webSite, SetUp.getTempDirectory(), indexName, false, "" );
            Common.newEncodeFile( SetUp.getTempDirectory(), indexName, indexEncodeName );

            String allPageString = Common.getFileString( SetUp.getTempDirectory(), indexEncodeName );

            int beginIndex = allPageString.indexOf( "<h1" );
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
    public void parseComicURL() { // parse URL and save all URLs in comicURL  //
        // 先取得前面的下載伺服器網址

        int beginIndex = 0;
        int endIndex = 0;
        String tempString = "";

        Common.downloadFile( webSite, SetUp.getTempDirectory(), indexName, false, "" );
        Common.newEncodeFile( SetUp.getTempDirectory(), indexName, indexEncodeName );
        String allPageString = Common.getFileString( SetUp.getTempDirectory(), indexEncodeName );


        Common.debugPrint( "開始解析這一集有幾頁 : " );



        String[] picNames; // 存放此集所有圖片檔名

        if ( allPageString.indexOf( "|" ) > 0 ) {
            // 第一種格式，副檔名與檔名分開
            // ex. http://www.imanhua.com/comic/1034/list_33255.html

            // 先找每張圖片的位置
            beginIndex = allPageString.indexOf( "[\"", beginIndex ) + 1;
            endIndex = allPageString.indexOf( "\"]", beginIndex ) + 1;
            tempString = allPageString.substring( beginIndex, endIndex );
            String[] strings = tempString.split( "," );

            int[] indexList = getPicIndexList( strings ); // 取得圖片位置陣列
            int[] extensionList = getExtensionList( strings ); // 取得副檔名類別陣列

            // 再擷取檔名表
            beginIndex = allPageString.indexOf( "|", beginIndex );
            beginIndex = allPageString.lastIndexOf( "'", beginIndex ) + 1;
            endIndex = allPageString.indexOf( "'.split", beginIndex );
            tempString = allPageString.substring( beginIndex, endIndex );
            String[] picList = tempString.split( "\\|" ); // 取得圖片檔名表列

            picNames = new String[indexList.length];
            for ( int i = 0 ; i < indexList.length ; i++ ) {
                picNames[i] = picList[indexList[i]] + "." + picList[extensionList[i]];
                //Common.debugPrintln( picNames[i] ); // for debug
            }
        }
        else {
            // 第一種格式，副檔名與檔名放在一起

            beginIndex = allPageString.indexOf( "[\"", beginIndex ) + 1;
            endIndex = allPageString.indexOf( "\"]", beginIndex ) + 1;
            tempString = allPageString.substring( beginIndex, endIndex );
            String[] strings = tempString.split( "," );
            
            picNames = new String[strings.length];
            for ( int i = 0; i < strings.length; i ++ ) {
                picNames[i] = strings[i].replace( "\"", "" );
            }
            
        }

        totalPage = picNames.length;
        Common.debugPrintln( "共 " + totalPage + " 頁" );
        comicURL = new String[totalPage];

        Common.debugPrintln( "開始解析中間部份的位址" );

        String midURL = "";
        // ex.中間網址是pictures，而非Files/Images/
        if ( allPageString.indexOf( "|pictures|" ) > 0 ) {
            midURL = "pictures/";
        }
        else {
            midURL = "Files/Images/";
        }

        // 先解析第一個數字 
        beginIndex = webSite.indexOf( "comic/" );
        beginIndex = webSite.indexOf( "/", beginIndex ) + 1;
        endIndex = webSite.indexOf( "/", beginIndex );
        String firstNumber = webSite.substring( beginIndex, endIndex );

        // 再解析第二個數字
        beginIndex = webSite.indexOf( "list_" );
        beginIndex = webSite.indexOf( "_", beginIndex ) + 1;
        endIndex = webSite.indexOf( ".", beginIndex );
        String secondNumber = webSite.substring( beginIndex, endIndex );

        // 圖片基本位址
        String baseURL1 = "http://t4.imanhua.com/";
        String baseURL2 = "http://t5.imanhua.com/";
        String baseURL3 = "http://t6.imanhua.com/";


        int p = 0; // 目前頁數
        for ( int i = 0 ; i < totalPage && Run.isAlive ; i++ ) {
            if ( picNames[i].matches( ".*/.*" ) ) { // 檔名已包含後方位址
                // ex. http://www.imanhua.com/comic/69/list_5707.html
                comicURL[i] = baseURL1 + picNames[i];
            }
            else {
                comicURL[i] = baseURL1 + midURL
                        + firstNumber + "/" + secondNumber + "/" + picNames[i];
            }

            // 使用最簡下載協定，加入refer始可下載
            singlePageDownloadUsingSimple( getTitle(), getWholeTitle(),
                    comicURL[i], totalPage, i + 1, comicURL[i] );

            //Common.debugPrintln( (++p) + " " + comicURL[p - 1] ); // debug
        }
        //System.exit( 0 ); // debug
    }

    // 取得副檔名類別陣列
    public int[] getExtensionList( String[] strings ) {
        int[] list = new int[strings.length];

        String tempString = "";
        int tempIndex = 0;
        int beginIndex = 0;
        int endIndex = 0;
        for ( int i = 0 ; i < strings.length ; i++ ) {
            beginIndex = strings[i].indexOf( "." ) + 1;
            endIndex = strings[i].indexOf( "\"", beginIndex );
            tempString = strings[i].substring( beginIndex, endIndex ); // 取出extension index字串
            list[i] = Integer.parseInt( tempString );
        }

        return list;
    }

    // 取得位置陣列
    public int[] getPicIndexList( String[] strings ) {
        int[] list = new int[strings.length];
        int aIndex = "a".hashCode();
        int AIndex = "A".hashCode();
        int oneIndex = "1".hashCode();

        String tempString = "";
        int tempIndex = 0;
        int beginIndex = 0;
        int endIndex = 0;
        
        boolean decimalMode = true; // 十進位表示
        
        for ( int i = 0 ; i < strings.length ; i++ ) {
            if ( strings[i].matches( "\"[a-z]\\.\\d\"") ) {
                decimalMode = false; // 會有英文字母來表示
            }
        }
        
        
        for ( int i = 0 ; i < strings.length ; i++ ) {
            beginIndex = strings[i].indexOf( "\"" ) + 1;
            endIndex = strings[i].indexOf( ".", beginIndex );
            tempString = strings[i].substring( beginIndex, endIndex ); // 取出index字串
            
            if ( tempString.matches( "[\\d]+" ) ) { // 全數字
                int diff = 0; // 需要額外加的數，預設為零
                if ( !decimalMode && tempString.length() > 1 ) {
                    int ten = Integer.parseInt( tempString.substring( 0, 1 ) );
                    diff = ten * 62; 
                    tempString = tempString.substring( 1, 2 );
                }
                
                list[i] = diff + Integer.parseInt( tempString );
            }
            else { // 需轉換為數字
                int diff = 0; // 需要額外加的數，預設為零
                if ( tempString.length() > 1 ) {
                    int ten = Integer.parseInt( tempString.substring( 0, 1 ) );
                    
                    diff = ten * 62; 
                    
                    tempString = tempString.substring( 1, 2 );
                }
                
                tempIndex = tempString.hashCode();

                if ( tempIndex >= aIndex ) {
                    list[i] = diff + 10 + tempIndex - aIndex;
                }
                else if ( tempIndex >= AIndex ) {
                    list[i] = diff + 36 + tempIndex - AIndex;
                    
                }
                else if ( tempIndex >= oneIndex ) {
                    list[i] = tempIndex - oneIndex;
                }
                
               // System.out.println( tempString + " : " + list[i] + "=" + tempIndex + " - " + "" );
            }
        }

        return list;
    }

    @Override
    public String getAllPageString( String urlString ) {
        String indexName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_imanhua_", "html" );
        String indexEncodeName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_imanhua_encode_", "html" );

        Common.downloadFile( urlString, SetUp.getTempDirectory(), indexName, false, "" );
        Common.newEncodeFile( SetUp.getTempDirectory(), indexName, indexEncodeName, Zhcode.GB2312 );

        return Common.getFileString( SetUp.getTempDirectory(), indexEncodeName );
    }

    @Override
    public boolean isSingleVolumePage( String urlString ) {
        // ex. http://www.imanhua.com/comic/1034/list_33204.html
        if ( urlString.matches( "(?).*/list_(?).*" ) ) {
            return true;
        }
        else {
            return false;
        }
    }

    public String getMainUrlFromSingleVolumeUrl( String volumeURL ) {
        // ex. http://www.imanhua.com/comic/1034/list_33204.html轉為
        //    http://www.imanhua.com/comic/1034/

        int endIndex = Common.getIndexOfOrderKeyword( volumeURL, "/", 5 );
        String mainPageURL = volumeURL.substring( 0, endIndex );

        Common.debugPrintln( "MAIN_URL: " + mainPageURL );

        return mainPageURL;
    }

    @Override
    public String getTitleOnSingleVolumePage( String urlString ) {
        String mainUrlString = getMainUrlFromSingleVolumeUrl( urlString );

        return getTitleOnMainPage( mainUrlString, getAllPageString( mainUrlString ) );
    }

    @Override
    public String getTitleOnMainPage( String urlString, String allPageString ) {
        int beginIndex = allPageString.indexOf( "<h1>" );
        beginIndex = allPageString.indexOf( ">", beginIndex ) + 1;
        int endIndex = allPageString.indexOf( "</h1>", beginIndex );
        String title = allPageString.substring( beginIndex, endIndex ).trim();

        return Common.getStringRemovedIllegalChar( Common.getTraditionalChinese( title ) );
    }

    @Override
    public List<List<String>> getVolumeTitleAndUrlOnMainPage( String urlString, String allPageString ) {
        // combine volumeList and urlList into combinationList, return it.

        List<List<String>> combinationList = new ArrayList<List<String>>();
        List<String> urlList = new ArrayList<String>();
        List<String> volumeList = new ArrayList<String>();

        int beginIndex = allPageString.indexOf( "id='subBookList'" );
        int endIndex = allPageString.indexOf( "</ul>", beginIndex );

        String tempString = allPageString.substring( beginIndex, endIndex );

        int volumeCount = tempString.split( " href=" ).length - 1;
        totalVolume = volumeCount;
        Common.debugPrintln( "共有" + totalVolume + "集" );

        String volumeTitle = "";
        beginIndex = endIndex = 0;
        for ( int i = 0 ; i < volumeCount ; i++ ) {
            // 取得單集位址
            beginIndex = tempString.indexOf( " href=", beginIndex );
            beginIndex = tempString.indexOf( "\"", beginIndex ) + 1;
            endIndex = tempString.indexOf( "\"", beginIndex );
            urlList.add( baseURL + tempString.substring( beginIndex, endIndex ) );
            // 取得單集名稱
            beginIndex = tempString.indexOf( ">", beginIndex ) + 1;
            endIndex = tempString.indexOf( "</a>", beginIndex );
            volumeTitle = tempString.substring( beginIndex, endIndex );
            volumeTitle = volumeTitle.replaceAll( "<.*>", "" );
            volumeList.add( getVolumeWithFormatNumber( Common.getStringRemovedIllegalChar(
                    Common.getTraditionalChinese( volumeTitle.trim() ) ) ) );
        }

        combinationList.add( volumeList );
        combinationList.add( urlList );

        return combinationList;
    }
}
