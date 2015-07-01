/*
 ----------------------------------------------------------------------------------------------------
 Program Name : JComicDownloader
 Authors  : surveyorK
 Last Modified : 2011/11/2
 ----------------------------------------------------------------------------------------------------
 ChangeLog:
 4.14: 修復manmankancom因網站改版而無法解析的問題。
 1.14: 新增對manmankancom的支援
----------------------------------------------------------------------------------------------------
 */
package jcomicdownloader.module;

import java.util.ArrayList;
import java.util.List;
import jcomicdownloader.SetUp;
import jcomicdownloader.encode.Encoding;
import jcomicdownloader.enums.Site;
import jcomicdownloader.tools.Common;

public class ParseManmankan extends ParseOnlineComicSite {
    private int radixNumber; // use to figure out the name of pic
    private String jsName;
    protected String indexName;
    protected String indexEncodeName;
    
    /**
 *
 * @author user
 */
    public ParseManmankan() {
        siteID = Site.MANMANKAN;
        siteName = "Manmankan";
        indexName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_manmankan_parse_", "html" );
        indexEncodeName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_manmankan_encode_parse_", "html" );

        jsName = "index_manmankan.js";
        radixNumber = 185273; // default value, not always be useful!!
    }

    public ParseManmankan( String webSite, String titleName ) {
        this();
        this.webSite = webSite;
        this.title = titleName;
    }

    @Override
    public void setParameters() {
        Common.debugPrintln( "開始解析各參數 :" );

        Common.debugPrintln( "開始解析title和wholeTitle :" );
        
        if ( getWholeTitle() == null || getWholeTitle().equals(  "" ) ) {
            String allPageString = getAllPageString( webSite );
            
            int beginIndex = allPageString.indexOf( "<h1>" ) + 4;
            int endIndex = allPageString.indexOf( "</h1>", beginIndex );
            setWholeTitle( allPageString.substring( beginIndex, endIndex ) );
        }

        Common.debugPrintln( "作品名稱(title) : " + getTitle() );
        Common.debugPrintln( "章節名稱(wholeTitle) : " + getWholeTitle() ); 
    }

    @Override
    public void parseComicURL() { // parse URL and save all URLs in comicURL  //
        // 先取得前面的下載伺服器網址
        
        String allPageString = getAllPageString( webSite );
        Common.debugPrint( "開始解析這一集有幾頁 : " );
        
        int beginIndex = allPageString.indexOf( "Array(" ) + 6;
        int endIndex = allPageString.indexOf( ");", beginIndex );
        String[] urlListTokes = allPageString.substring( beginIndex, endIndex ).split( "," );
        
        totalPage = urlListTokes.length;
        Common.debugPrintln( "共 " + totalPage + " 頁" );
        comicURL = new String[totalPage];
        
        // ex. http://76.manmankan.com/2011/201111/1916/43124/001.jpg
        String baseURL = "http://76.manmankan.com";
        
        for ( int i = 0; i < urlListTokes.length && Run.isAlive; i ++ ) {
            String fontURL = urlListTokes[i].trim().substring( 1, urlListTokes[i].length() - 1 );
            comicURL[i] = baseURL + fontURL;
            //Common.debugPrintln( ( i + 1 ) + " " + comicURL[i] ); // debug
        }
        //System.exit(0); // debug
    }

    @Override
    public String getAllPageString( String urlString ) {
        String indexName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_manmankan_", "html" );
        String indexEncodeName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_manmankan_encode_", "html" );
        Common.downloadFile( urlString, SetUp.getTempDirectory(), indexName, false, "" );
        Common.newEncodeFile( SetUp.getTempDirectory(), indexName, indexEncodeName, Encoding.GBK );

        return Common.getFileString( SetUp.getTempDirectory(), indexEncodeName );
    }

    @Override
    public boolean isSingleVolumePage( String urlString ) {
        // ex.http://www.manmankan.com/html/1916/index.asp
        if ( urlString.matches( "(?s).*manhua.manmankan.com(?s).*") ) 
            return true;
        else
            return false;
    }
    
    public String getMainUrlFromSingleVolumeUrl( String volumeURL ) {
        // ex. http://manhua.manmankan.com/html/1916/42388.asp轉為
        //     http://www.manmankan.com/html/1916/index.asp
        
        int endIndex = Common.getIndexOfOrderKeyword( volumeURL, "/", 5 ) + 1;
        String mainURL = volumeURL.substring( 0, endIndex ) + "index.asp";
        
        return mainURL.replaceAll( "manhua.manmankan.com", "www.manmankan.com" );
    }

    @Override
    public String getTitleOnMainPage( String urlString, String allPageString ) {
        
        String[] tokens = allPageString.split( ">|<" );
        
        int beginIndex = allPageString.indexOf( "<title>" ) + 7;
        int endIndex = allPageString.indexOf( "</title>", beginIndex );
        String title = allPageString.substring( beginIndex, endIndex ).split( " " )[0];
        
        return Common.getStringRemovedIllegalChar( Common.getTraditionalChinese( title ) );
    }

    @Override
    public List<List<String>> getVolumeTitleAndUrlOnMainPage( String urlString, String allPageString ) {
        // combine volumeList and urlList into combinationList, return it.

        List<List<String>> combinationList = new ArrayList<List<String>>();
        List<String> urlList = new ArrayList<String>();
        List<String> volumeList = new ArrayList<String>();
        
        int beginIndex = allPageString.indexOf( "class=\"ComicList\"" );
        int endIndex = allPageString.indexOf( "</div>", beginIndex );
        String listString = allPageString.substring( beginIndex, endIndex );
        
        String[] tokens = listString.split( "'|>|<" );
        
        int volumeCount = 0; // 計算總共集數
        
        for ( int i = 0; i < tokens.length; i ++ ) {
            
            if ( tokens[i].matches( "(?s).*href=" ) ) {
                // 取得單集位址
                urlList.add( tokens[i+1] );
                
                // 取得單集名稱
                volumeList.add( getVolumeWithFormatNumber( Common.getStringRemovedIllegalChar( 
                        Common.getTraditionalChinese( tokens[i+5].trim() ) ) ) );
            }
        }
        
        Common.debugPrintln( "共有" + volumeCount + "集" );

        combinationList.add( volumeList );
        combinationList.add( urlList );

        return combinationList;
    }
}

