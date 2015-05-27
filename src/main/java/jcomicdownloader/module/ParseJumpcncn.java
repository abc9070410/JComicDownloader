/*
 ----------------------------------------------------------------------------------------------------
 Program Name : JComicDownloader
 Authors  : surveyorK
 Last Modified : 2013/4/3
 ----------------------------------------------------------------------------------------------------
 ChangeLog:
 5.16: 修復jumpcn.com.cn解析錯誤的問題。
 4.18: 修復jumpcn.com.cn因網頁改版而下載錯誤的問題。
 1.13: 修復jumpcn.com.cn因置換伺服器而解析錯誤的問題。
 1.10: 增加對於jumpcn.com.cn的支援
----------------------------------------------------------------------------------------------------
 */
package jcomicdownloader.module;

import jcomicdownloader.tools.*;
import jcomicdownloader.enums.*;
import jcomicdownloader.*;

import java.io.*;
import java.util.*;
import java.text.*;

public class ParseJumpcncn extends ParseOnlineComicSite {
    private int radixNumber; // use to figure out the name of pic
    private String jsName;
    protected String indexName;
    protected String indexEncodeName;
    protected String orinialWholeTitle; // 簡體的wholeTitle
    
    /**
 *
 * @author user
 */
    public ParseJumpcncn() {
        siteID = Site.JUMPCNCN;
        siteName = "JumpCNCN";
        indexName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_jumpcncn_parse_", "html" );
        indexEncodeName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_jumpcncn_encode_parse_", "html" );

        jsName = "index_jumpcncn.js";
        radixNumber = 185271; // default value, not always be useful!!
        orinialWholeTitle = "";
        
    }

    public ParseJumpcncn( String webSite, String titleName ) {
        this();
        this.webSite = webSite;
        this.title = titleName;
    }

    @Override
    public void setParameters() { // let all the non-set attributes get values
        Common.debugPrintln( "開始解析各參數 :" );

        Common.debugPrintln( "開始解析title和wholeTitle :" );

        Common.downloadFile( webSite, SetUp.getTempDirectory(), indexName, false, "" );
        Common.newEncodeFile( SetUp.getTempDirectory(), indexName, indexEncodeName );
            
        
        String allPageString = Common.getFileString( SetUp.getTempDirectory(), indexEncodeName );

        String[] tokens = allPageString.split( "\\d*>\\d*|\\d*<\\d*" );

        for ( int i = 0; i < tokens.length; i ++ ) {
            if ( tokens[i].matches( "(?s).*" + webSite + "(?s).*" ) ) {
                tokens[i+1] = tokens[i+1].replaceAll( "\\.", "" ); // 因為之後wholeTitle會變成網址，不能有"."出現

                orinialWholeTitle = tokens[i+1];
                if ( getWholeTitle() == null || getWholeTitle().equals(  "" ) ) 
                    setWholeTitle( getVolumeWithFormatNumber( Common.getStringRemovedIllegalChar( 
                            Common.getTraditionalChinese( orinialWholeTitle ) ) ) );
                
                break;
            }
        }


        Common.debugPrintln( "作品名稱(title) : " + getTitle() );
        Common.debugPrintln( "章節名稱(wholeTitle) : " + getWholeTitle() ); 
    }

    @Override
    public void parseComicURL() { // parse URL and save all URLs in comicURL
        // 先取得前面的下載伺服器網址
        String[] lines = Common.getFileStrings( SetUp.getTempDirectory(), indexName );
        
        // ex. http://www.jumpcn.com.cn/comic/2749/33313/
        
        int beginIndex = Common.getIndexOfOrderKeyword( webSite, "/", 4 ) + 1;
        int endIndex = Common.getIndexOfOrderKeyword( webSite, "/", 5 );
        
        String idString = webSite.substring( beginIndex, endIndex );
        Common.debugPrintln( "漫畫代號為：" + idString );
        
        Common.debugPrint( "開始解析這一集有幾頁 :" );
        String allJsPageString = getAllPageString( webSite + "index.js" );
        String[] jsTokens = allJsPageString.split( "=|;" ); // ex. var total=23;
        for ( int i = 0; i < jsTokens.length && Run.isAlive; i ++ ) {
            if ( jsTokens[i].matches( "(?s).*total" ) ) {
                totalPage = Integer.parseInt( jsTokens[i+1] );
                Common.debugPrintln( "共 " + totalPage + " 頁" );
                comicURL = new String [totalPage]; // totalPage = amount of comic pic
                break;
            }
        }
        
        beginIndex = allJsPageString.indexOf( "'" ) + 1;
        endIndex = allJsPageString.indexOf( "'", beginIndex );
        String volpic = allJsPageString.substring( beginIndex, endIndex );
        
        
        Common.debugPrintln( "取得圖片網址父目錄 :" );
        /*
        String allPageString = Common.getFileString( SetUp.getTempDirectory(), indexName );

        beginIndex = allPageString.indexOf( "/Scripts/picshow.js" );
        endIndex = allPageString.indexOf( "\"", beginIndex );
        String jsURL = "http://www.jumpcn.com.cn/" + allPageString.substring( beginIndex, endIndex ); 
        Common.debugPrintln( "picshow.js的網址 : jsURL" );
        */
        
        allJsPageString = getAllPageString( "http://www.jumpcn.com.cn/Scripts/picshow.js" );
        beginIndex = allJsPageString.indexOf( "http://" );
        endIndex = allJsPageString.indexOf( "'", beginIndex );
        String baseURL =  allJsPageString.substring( beginIndex, endIndex ) + volpic;
        baseURL = baseURL.replaceAll( "\\s", "%20" );
        Common.debugPrintln( "基本位址:" + baseURL );

        Common.debugPrintln( "開始解析每一頁圖片的網址 :" );
        
        // 因為有的檔名是001.jpg有的是1.jpg，所以先對第一張測試連線，檢查是哪種檔名格式
        boolean noNeedToAddZero = false;
        if ( Common.urlIsOK( Common.getFixedChineseURL( baseURL ) + "1.jpg" ) )
            noNeedToAddZero = true;

        for ( int p = 1; p <= totalPage; p ++ ) {
            String frontURL = String.valueOf( p ) + ".jpg";
            
            if ( noNeedToAddZero )
                comicURL[p-1] = Common.getFixedChineseURL( baseURL + frontURL );
            else {
                NumberFormat formatter = new DecimalFormat( "000" );
                String fileName = formatter.format( p ) + ".jpg";
                comicURL[p-1] = Common.getFixedChineseURL( baseURL + fileName );
            }
            //Common.debugPrintln( p + " " + comicURL[p-1] );
        }
        //System.exit(0);
    }

    @Override
    public String getAllPageString( String urlString ) {
        String indexName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_jumpcncn_", "html" );
        String indexEncodeName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_jumpcncn_encode_", "html" );
        
        Common.downloadFile( urlString, SetUp.getTempDirectory(), indexName, false, "" );
        Common.newEncodeFile( SetUp.getTempDirectory(), indexName, indexEncodeName );

        return Common.getFileString( SetUp.getTempDirectory(), indexEncodeName );
    }

    @Override
    public boolean isSingleVolumePage( String urlString ) {
        if ( Common.getAmountOfString( urlString, "/" ) > 5 ) {
            return true;
        }
        else {
            return false;
        }
    }

    @Override
    public String getTitleOnSingleVolumePage( String urlString ) {
        // http://www.jumpcn.com.cn/comic/1/222/轉為http://www.jumpcn.com.cn/comic/1/
        
        int endIndex = Common.getIndexOfOrderKeyword( urlString, "/", 5 ) + 1;
        
        String mainPageUrlString = urlString.substring( 0, endIndex );

        return getTitleOnMainPage( mainPageUrlString, getAllPageString( mainPageUrlString ) );
    }

    @Override
    public String getTitleOnMainPage( String urlString, String allPageString ) {
        
        String[] tokens = allPageString.split( ">|<" );
        String title = "";
        
        for ( int i = 0; i < tokens.length; i ++ ) {
            // ex. <h2 class='fleft blue'> title </h2>
            if ( tokens[i].matches( "(?s).*'fleft blue'(?s).*" ) ) {
                title = tokens[i+1];
                break;
            }
        }

        return Common.getReomvedUnnecessaryWord( Common.getStringRemovedIllegalChar( Common.getTraditionalChinese( title ) ) );
    }

    @Override
    public List<List<String>> getVolumeTitleAndUrlOnMainPage( String urlString, String allPageString ) {
        // combine volumeList and urlList into combinationList, return it.

        List<List<String>> combinationList = new ArrayList<List<String>>();
        List<String> urlList = new ArrayList<String>();
        List<String> volumeList = new ArrayList<String>();

        int beginIndex = allPageString.indexOf( "class=\"bookList\"" );
        int endIndex = allPageString.indexOf( "</table>" );
        String tempString = allPageString.substring( beginIndex, endIndex ); 

        int volumeCount = tempString.split( " href=" ).length - 1;
        
        String baseURL = "http://www.jumpcn.com.cn/";
        
        Common.debugPrintln( tempString );

        String volumeTitle = "";
        beginIndex = endIndex = 0;
        for ( int i = 0; i < volumeCount; i++ ) {
            // 取得單集位址
            beginIndex = tempString.indexOf( " href=", beginIndex );
            beginIndex = tempString.indexOf( "\"", beginIndex ) + 1;
            endIndex = tempString.indexOf( "\"", beginIndex );
            String tempUrlString = urlString + tempString.substring( beginIndex, endIndex ) ;
            urlList.add( tempUrlString );
            Common.debugPrint( tempUrlString + " : " );

            // 取得單集名稱
            
            beginIndex = tempString.indexOf( ">", endIndex ) + 1;
            endIndex = tempString.indexOf( "<", beginIndex );
            String volumeString = tempString.substring( beginIndex, endIndex );
            
            if ( volumeString.matches( "" ) )
            {
                beginIndex = tempString.indexOf( ">", endIndex ) + 1;
                endIndex = tempString.indexOf( "<", beginIndex );
            }
            
            volumeTitle = tempString.substring( beginIndex, endIndex );
            Common.debugPrintln( volumeTitle );

            volumeList.add( getVolumeWithFormatNumber( Common.getStringRemovedIllegalChar(
                Common.getTraditionalChinese( volumeTitle.trim() ) ) ) );

        }

        totalVolume = volumeCount;
        Common.debugPrintln( "共有" + totalVolume + "集" );
        

        combinationList.add( volumeList );
        combinationList.add( urlList );

        return combinationList;
    }

    @Override
    public String getMainUrlFromSingleVolumeUrl( String volumeURL ) {
        throw new UnsupportedOperationException( "Not supported yet." );
    }
}
