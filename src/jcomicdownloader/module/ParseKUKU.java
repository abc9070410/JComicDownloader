/*
 ----------------------------------------------------------------------------------------------------
 Program Name : JComicDownloader
 Authors  : surveyorK
 Last Modified : 2013/1/29
 ----------------------------------------------------------------------------------------------------
 ChangeLog:
 5.14: 修復kuku解析集數錯誤的問題。
 2.14: 集數基本位址從http://mh.socomic.com改為http://comic.kukudm.com
 2.10: 修復解析少數圖片網址時後面多出">"的問題。
 1.15: 修正編碼為GBK
 1.11: 1. 改成一邊解析網址一邊下載。
 *    2. 修復kuku的美食的俘虜解析網址錯誤的bug。
----------------------------------------------------------------------------------------------------
 */
package jcomicdownloader.module;

import jcomicdownloader.tools.*;
import jcomicdownloader.enums.*;
import jcomicdownloader.*;

import java.util.*;
import java.net.*;
import jcomicdownloader.encode.Encoding;

public class ParseKUKU extends ParseOnlineComicSite {
    private String baseURL;
    private int nowNo;

    private synchronized int getNowNo() {
        return ++ nowNo;
    }

    /**
 *
 * @author user
 */
    public ParseKUKU() {
        siteID = Site.KUKU;
        siteName = "kuku";
        indexName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_kuku_parse_", "html" );
        indexEncodeName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_kuku_encode_parse_", "html" );

        baseURL = "http://cc.kukudm.com/";
    }



    public ParseKUKU( String webSite, String titleName ) {
        this();
        this.webSite = webSite;
        this.title = titleName;
    }


    @Override
    public synchronized void setParameters() {
        Common.downloadFile( webSite, SetUp.getTempDirectory(), indexName, false, "" );
        Common.newEncodeFile( SetUp.getTempDirectory(), indexName, indexEncodeName, Encoding.GBK );

        String tempStr = Common.getFileString( SetUp.getTempDirectory(), indexEncodeName );
        String[] lines = tempStr.split( "\n" );

        for ( int i = 0; i < lines.length; i ++ ) {
            String line = Common.getTraditionalChinese( lines[i] );

            // ".": contain all characters except "\r" and "\n"
            // "(?s).": contain all characters
            if ( line.matches( "(?s).*title(?s).*" ) ) {
                // get title ex.<title>尸錄 4話</title>
                String[] temp = line.split( "<|>" );
                
                if ( getWholeTitle() == null || getWholeTitle().equals(  "" ) )
                    setWholeTitle( Common.getStringRemovedIllegalChar( temp[2] ) );
            }
            else if ( line.matches( "(?s).*page(?s).*" ) ) {
                // get total page ex. | 共34頁 |
                int beginIndex = line.indexOf( Common.getStringUsingDefaultLanguage( "共", "共" ) );
                int endIndex = line.indexOf( Common.getStringUsingDefaultLanguage( "頁", "頁" ) );

                String temp = line.substring( beginIndex + 1, endIndex );
                totalPage = Integer.parseInt( temp );

                break;
            }
        }


        comicURL = new String [totalPage]; // totalPage = amount of comic pic
        SetUp.setWholeTitle( wholeTitle );
    }
    
    @Override
    public synchronized void parseComicURL() {
        System.out.print( "parse the pic URL:" );

        for ( int i = 0; i < totalPage && Run.isAlive; i ++ ) {
            // 檢查下一張圖是否存在同個資料夾，若存在就跳下一張
            if ( !Common.existPicFile( getDownloadDirectory(), i + 2 ) ||
                 !Common.existPicFile( getDownloadDirectory(), i + 1 ) ) {
                int endIndex = webSite.lastIndexOf( "/" );
                String tempWebSite = webSite.substring( 0, endIndex + 1 ) + ( i + 1 ) + ".htm";

                Common.downloadFile( tempWebSite, SetUp.getTempDirectory(), indexName, false, "" );
                Common.newEncodeFile( SetUp.getTempDirectory(), indexName, indexEncodeName, Encoding.GBK );

                String tempStr = Common.getFileString( SetUp.getTempDirectory(), indexEncodeName );
                String[] lines = tempStr.split( "\n" );

                for ( int count = 0; count < lines.length && Run.isAlive; count ++ ) {
                    String line = lines[count];

                    if ( line.matches( "(?s).*document.write(?s).*" ) ) {
                        String[] temp = line.split( "'\"|\"|'|>" );
                        
                        System.out.println( baseURL + temp[3] );
                        // replace %20 from white space in URL
                        String frontURL = temp[3].replaceAll( "\\s", "%20" );
                        comicURL[i] = Common.getFixedChineseURL( baseURL + frontURL );
                        //Common.debugPrintln( i + " " + comicURL[i] ); // debug

                        // 每解析一個網址就下載一張圖
                        singlePageDownload( getTitle(), getWholeTitle(), comicURL[i], totalPage, i + 1, 0 );

                        break;
                    }
                }
            }
        }
        //System.exit( 0 ); // debug
    }
    
    @Override
    public boolean isSingleVolumePage( String urlString ) {
        return urlString.matches( "(?s).*\\d+\\.htm(?s).*" );
    }
    
    @Override
    public String getAllPageString( String urlString ) {
        String indexName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_KUKU_", "html" );
        String indexEncodeName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_KUKU_encode_", "html" );
        
        Common.downloadFile( urlString, SetUp.getTempDirectory(), indexName, false, "" );
        Common.newEncodeFile( SetUp.getTempDirectory(), indexName, indexEncodeName, Encoding.GBK );

        return Common.getFileString( SetUp.getTempDirectory(), indexEncodeName );
    }
    
    @Override
    public String getTitleOnSingleVolumePage( String urlString ) {      
        return getTitle();
    }
    
    @Override
    public String getTitleOnMainPage( String urlString, String allPageString ) {
        String[] lines = allPageString.split( "\n" );

        int beginIndex = lines[0].indexOf( "<title>", 1 ) + 7;
        int endIndex = lines[0].indexOf( "_", beginIndex ) - 4;
        
        return Common.getStringRemovedIllegalChar( Common.getTraditionalChinese( lines[0].substring( beginIndex, endIndex ) ) );
    }
    
    @Override
    public List<List<String>> getVolumeTitleAndUrlOnMainPage( String urlString, String allPageString ) {
        // combine volumeList and urlList into combinationList, return it.
    
        List<List<String>> combinationList = new ArrayList<List<String>>();
        List<String> urlList = new ArrayList<String>();
        List<String> volumeList = new ArrayList<String>();
        
        String[] lines = allPageString.split( "\n" );
        
        int beginIndex = 0;
        int endIndex = 0;
        String volumeURL = "";
        
        beginIndex = allPageString.indexOf( "id='comiclistn'" );
        endIndex = allPageString.indexOf( "</table>", beginIndex );
        String tempString = allPageString.substring( beginIndex, endIndex );

        int volumeCount = tempString.split( "<dd>" ).length - 1;
        
        // 單集位址的網域名稱（有四組，可置換）
        String baseVolumeURL = "http://comic.kukudm.com"; 
        beginIndex = endIndex = 0;
        for ( int i = 0; i < volumeCount; i ++ ) {
            // 取得單集位址
            beginIndex = tempString.indexOf( "<dd>", beginIndex ) + 1;
            beginIndex = tempString.indexOf( "'", beginIndex ) + 1;
            endIndex = tempString.indexOf( "'", beginIndex );
            volumeURL = tempString.substring( beginIndex, endIndex );
            if ( volumeURL.matches( "http.*" ) )
            {
                urlList.add( tempString.substring( beginIndex, endIndex ) );
            }
            else
            {
                urlList.add( baseVolumeURL + tempString.substring( beginIndex, endIndex ) );
            }
            
            
            // 取得單集名稱
            beginIndex = tempString.indexOf( ">", beginIndex ) + 1;
            endIndex = tempString.indexOf( "<", beginIndex );
            volumeList.add( getVolumeWithFormatNumber( Common.getStringRemovedIllegalChar(
                                Common.getTraditionalChinese( tempString.substring( beginIndex, endIndex ).trim() ) ) ) );
            
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

