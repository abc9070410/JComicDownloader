
/*
----------------------------------------------------------------------------------------------------
Program Name : JComicDownloader
Authors  : surveyorK
Last Modified : 2012/5/24
----------------------------------------------------------------------------------------------------
ChangeLog:
    4.12: 1. 修復fumanhua因伺服器位址更換而無法下載的問題。
    4.02: 1. 修復fumanhua解析集數錯誤的問題。
 *  4.01: 1. 新增對fumanhua的支援。
----------------------------------------------------------------------------------------------------
 */
package jcomicdownloader.module;

import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import jcomicdownloader.tools.*;
import jcomicdownloader.enums.*;
import java.util.*;
import jcomicdownloader.SetUp;
import jcomicdownloader.encode.Encoding;
import jcomicdownloader.encode.Zhcode;

public class ParseFumanhua extends ParseOnlineComicSite {

    private int radixNumber; // use to figure out the name of pic
    private String jsName;
    protected String indexName;
    protected String indexEncodeName;
    protected String baseURL;

    /**
    
    @author user
     */
    public ParseFumanhua() {
        enumName = "FUMANHUA";
	parserName=this.getClass().getName();
        regexs= new String[]{"(?s).*fumanhua.net(?s).*", "(?s).*fumanhua.com(?s).*", "(?s).*fmhua.com(?s).*" };
        downloadBefore=true;
	siteID=Site.formString("FUMANHUA");
        siteName = "Fumanhua";
        indexName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_fumanhua_parse_", "html" );
        indexEncodeName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_fumanhua_encode_parse_", "html" );

        jsName = "index_fumanhua.js";
        radixNumber = 1591371; // default value, not always be useful!!

        baseURL = "http://mh.fumanhua.net";
    }

    public ParseFumanhua( String webSite, String titleName ) {
        this();
        this.webSite = webSite;
        this.title = titleName;
    }

    @Override
    public void setParameters() {
        Common.debugPrintln( "開始解析各參數 :" );
        Common.debugPrintln( "開始解析title和wholeTitle :" );

        //Common.downloadFile( webSite, SetUp.getTempDirectory(), indexName, false, "" );
        Common.simpleDownloadFile( webSite, SetUp.getTempDirectory(), indexName, webSite );
        //Common.newEncodeFile( SetUp.getTempDirectory(), indexName, indexEncodeName );

        if ( getWholeTitle() == null || getWholeTitle().equals( "" ) ) {
            // 因為正常解析不需要用到單集頁面，所以給此兩行放進來
            String allPageString = Common.getFileString( SetUp.getTempDirectory(), indexName );

            int beginIndex = allPageString.indexOf( "href='/comic" );
            beginIndex = allPageString.indexOf( "〉", beginIndex ) + 1;
            int endIndex = allPageString.indexOf( "<", beginIndex );
            String tempTitleString = allPageString.substring( beginIndex, endIndex ).trim();

            setWholeTitle( getVolumeWithFormatNumber( Common.getStringRemovedIllegalChar(
                    Common.getTraditionalChinese( tempTitleString.trim() ) ) ) );
        }

        Common.debugPrintln( "作品名稱(title) : " + getTitle() );
        Common.debugPrintln( "章節名稱(wholeTitle) : " + getWholeTitle() );
    }

    @Override
    public void parseComicURL() { // parse URL and save all URLs in comicURL  //

        // 先取得所有的下載伺服器網址
        int beginIndex = 0, endIndex = 0;
        String tempString = "";

        Common.debugPrint( "開始解析這一集有幾頁 : " );
        String allPageString = Common.getFileString( SetUp.getTempDirectory(), indexName );
        
        
        
        beginIndex = allPageString.indexOf( "qTcms_S_m_murl_e" );
        
        if (beginIndex > 0)
        {
            beginIndex = allPageString.indexOf( "\"", beginIndex ) + 1;
            endIndex = allPageString.indexOf( "\"", beginIndex );

            String qTcms_S_m_murl_e = allPageString.substring( beginIndex, endIndex );
            String qTcms_S_m_murl = base64_decode( qTcms_S_m_murl_e );
            
            Common.debugPrintln("qTcms_S_m_murl:" + qTcms_S_m_murl);
            
            comicURL = qTcms_S_m_murl.split( "\\$qingtiandy\\$" );
            totalPage = comicURL.length;
            Common.debugPrintln( "共 " + totalPage + " 頁" );
            //System.exit(0);
        }
        else
        {
        
            totalPage = allPageString.split( "</option>" ).length - 1;
            Common.debugPrintln( "共 " + totalPage + " 頁" );
            comicURL = new String[totalPage];
            
            // 設定伺服器位址
            String[] serverURLs = {
                "http://bd.kfxxgc.com",
                "http://pic2.fumanhua.net",
                "http://pic.fumanhua.net"
                };

            String serverURL = "";

            // 開始第一張圖片位址
            beginIndex = allPageString.indexOf( "var imgurl" );
            beginIndex = allPageString.indexOf( "'", beginIndex ) + 1;
            endIndex = allPageString.indexOf( "'", beginIndex );
            String firestPicBackURL = allPageString.substring( beginIndex, endIndex );
            
            String firstPicURL = "";
           
            // 測試多組伺服器
            for (int i = 0; i < serverURLs.length; i++)
            {
                firstPicURL = serverURLs[i] + firestPicBackURL;
                
                if ( Common.urlIsOK( firstPicURL ) ) {
                    Common.debugPrintln( "使用伺服器位址：" + serverURLs[i] );
                    break;
                }
            }

            Common.debugPrintln( "第一張圖片位址：" + firstPicURL );
            
            // 取得圖片副檔名
            beginIndex = firstPicURL.lastIndexOf( "." ) + 1;
            String extension = firstPicURL.substring( beginIndex, firstPicURL.length() );
            Common.debugPrintln( "圖片副檔名：" + extension );
            
            // 取得圖片檔名（不包含副檔名）
            endIndex = beginIndex - 1;
            beginIndex = firstPicURL.lastIndexOf( "/" ) + 1;
            String picName = firstPicURL.substring( beginIndex, endIndex );
            Common.debugPrintln( "圖片檔名：" + picName );
            
            int firstPageNo = Integer.parseInt(picName);
            
            String zeroString = "";
            for ( int i = 0; i < picName.length(); i ++ ) {
                if (picName.substring(i, i+1).matches("\\d+"))
                {
                    zeroString += "0";
                }
            }
            Common.debugPrintln( "零格式：" + zeroString );
            
            NumberFormat formatter = new DecimalFormat( zeroString ); // 此站預設三個零，之後若有變數再說

            int p = 0; // 目前頁數
            String picURL = firstPicURL; // 每張圖片位址
            for ( int i = firstPageNo ; i < totalPage + firstPageNo; i++ ) {
                String nowFileName = formatter.format( i ) + "." + extension;
                String nextFileName = formatter.format( i + 1 ) + "." + extension;

                comicURL[p] = picURL; // 存入每一頁的網頁網址
                Common.debugPrintln( p + " " + comicURL[p] ); // debug
                
                p ++;

                picURL = picURL.replaceAll( nowFileName, nextFileName ); // 換下一張圖片
            }
        }
        
        String cookie = "Hm_lvt_a0d6e70519c7610ccd1d37c3ebb0434b=1341308909421; Hm_lpvt_a0d6e70519c7610ccd1d37c3ebb0434b=1341308909421";
        
        for ( int i = 0 ; i < totalPage && Run.isAlive; i++ ) {
            singlePageDownload( getTitle(), getWholeTitle(), comicURL[i], null,
                totalPage, i, 0, true, cookie, webSite, true );
        }
        //System.exit( 0 ); // debug
    }

    @Override
    public String getAllPageString( String urlString ) {
        String indexName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_fumanhua_", "html" );
        String indexEncodeName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_fumanhua_encode_", "html" );

        Common.downloadFile( urlString, SetUp.getTempDirectory(), indexName, false, "" );
        Common.newEncodeFile( SetUp.getTempDirectory(), indexName, indexEncodeName, Encoding.GB2312 );

        return Common.getFileString( SetUp.getTempDirectory(), indexEncodeName );
    }

    @Override
    public boolean isSingleVolumePage( String urlString ) {
        // ex. http://www.fumanhua.com/comic-view-248380.html
        if ( urlString.matches( "(?s).*\\.html(?s).*" ) ) {
            return true;
        }
        else // ex. http://www.fumanhua.com/comic-1631.html
        {
            return false;
        }
    }

    public String getMainUrlFromSingleVolumeUrl( String volumeURL ) {
        // ex. http://www.fumanhua.com/comic-view-248380.html轉為
        //    http://www.fumanhua.com/comic-1631.html

        String allPageString = getAllPageString( volumeURL );

        int beginIndex = allPageString.indexOf( "href='/comic" );
        beginIndex = allPageString.indexOf( "'", beginIndex ) + 1;
        int endIndex = allPageString.indexOf( "'", beginIndex );

        String mainPageURL = baseURL + allPageString.substring( beginIndex, endIndex );

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

        int beginIndex = allPageString.indexOf( "class=\"plist pnormal\"" );
        int endIndex = allPageString.indexOf( "class=\"blank_8\"", beginIndex );
        String tempString = allPageString.substring( beginIndex, endIndex );

        int volumeCount = tempString.split( " href=" ).length - 1;
        
        endIndex = urlString.indexOf("/manhua");
        String base = urlString.substring(0, endIndex);

        String volumeTitle = "";
        beginIndex = endIndex = 0;
        for ( int i = 0 ; i < volumeCount ; i++ ) {

            // 取得單集位址
            beginIndex = tempString.indexOf( " href=", beginIndex );
            beginIndex = tempString.indexOf( "\"", beginIndex ) + 1;
            endIndex = tempString.indexOf( "\"", beginIndex );
            String volumeUrl = base + tempString.substring( beginIndex, endIndex );
            urlList.add( volumeUrl );

            // 取得單集名稱
            beginIndex = tempString.indexOf( ">", beginIndex ) + 1;
            endIndex = tempString.indexOf( "<", beginIndex );
            volumeTitle = tempString.substring( beginIndex, endIndex );
            volumeList.add( getVolumeWithFormatNumber( Common.getStringRemovedIllegalChar(
                    Common.getTraditionalChinese( volumeTitle.trim() ) ) ) );
                    
            Common.debugPrintln(volumeTitle + " : " + volumeUrl );
        }

        totalVolume = volumeCount;
        Common.debugPrintln( "共有" + totalVolume + "集" );

        combinationList.add( volumeList );
        combinationList.add( urlList );

        return combinationList;
    }
}
