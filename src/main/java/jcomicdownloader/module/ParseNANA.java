/*
----------------------------------------------------------------------------------------------------
Program Name : JComicDownloader
Authors  : surveyorK
Last Modified : 2011/11/2
----------------------------------------------------------------------------------------------------
ChangeLog:
5.19: 修復nanadm無法下載的問題。
    3.03: 1. 修復nanadm無法下載的問題。
    2.01: 1. 修復無法解析粗體字集數名稱的bug。
    2.0 : 1. 新增新增對www.nanadm.com的支援。
----------------------------------------------------------------------------------------------------
 */
package jcomicdownloader.module;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import jcomicdownloader.SetUp;
import jcomicdownloader.enums.Site;
import jcomicdownloader.tools.Common;

public class ParseNANA extends ParseOnlineComicSite {

    private int radixNumber; // use to figure out the name of pic
    private String jsName;
    protected String indexName;
    protected String indexEncodeName;
    protected String baseURL;

    /**
     *
     * @author user
     */
    public ParseNANA() {
        siteID = Site.NANA;
        siteName = "Nana";
        indexName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_nana_parse_", "html" );
        indexEncodeName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_nana_encode_parse_", "html" );

        jsName = "index_nana.js";
        radixNumber = 18522371; // default value, not always be useful!!

        baseURL = "http://www.nanadm.com";
    }

    public ParseNANA( String webSite, String titleName ) {
        this();
        this.webSite = webSite;
        this.title = titleName;
    }

    @Override
    public void setParameters() {
        Common.debugPrintln( "開始解析各參數 :" );

        Common.debugPrintln( "開始解析title和wholeTitle :" );

        Common.downloadFile( webSite, SetUp.getTempDirectory(), indexName, false, "" );

        if ( getWholeTitle() == null || getWholeTitle().equals( "" ) ) {
            String allPageString = Common.getFileString( SetUp.getTempDirectory(), indexName );

            int beginIndex = allPageString.indexOf( "<h2>" ) + 4;
            int endIndex = allPageString.indexOf( "</h2>", beginIndex );
            String tempTitleString = allPageString.substring( beginIndex, endIndex );

            setWholeTitle( getVolumeWithFormatNumber( Common.getStringRemovedIllegalChar(
                    Common.getTraditionalChinese( tempTitleString.trim() ) ) ) );
        }

        Common.debugPrintln( "作品名稱(title) : " + getTitle() );
        Common.debugPrintln( "章節名稱(wholeTitle) : " + getWholeTitle() );
    }

    @Override
    public void parseComicURL() { // parse URL and save all URLs in comicURL  //
        // 先取得前面的下載伺服器網址

        String allPageString = Common.getFileString( SetUp.getTempDirectory(), indexName );
        Common.debugPrint( "開始解析這一集有幾頁 : " );

        int beginIndex, endIndex;

        totalPage = allPageString.split( "</option>" ).length - 1;

        Common.debugPrintln( "共 " + totalPage + " 頁" );
        comicURL = new String[totalPage];

        // 開始取得第一頁網址 
        
        beginIndex = allPageString.indexOf( "<img src=\"http" );
        beginIndex = allPageString.indexOf( "http", beginIndex );
        endIndex = allPageString.indexOf( "\"", beginIndex );
        String firstPageURL = Common.getFixedChineseURL( allPageString.substring( beginIndex, endIndex ) );

        String extensionName = firstPageURL.split( "\\." )[firstPageURL.split( "\\." ).length - 1]; // 取得檔案副檔名
        NumberFormat formatter = new DecimalFormat( "000" ); // 預設001.jpg ~ xxx.jpg

        for ( int p = 1 ; p <= totalPage && Run.isAlive; p++ ) {
            String fileNameBefore = formatter.format( p - 1 ) + "." + extensionName;
            String fileName = formatter.format( p ) + "." + extensionName;
            firstPageURL = comicURL[p - 1] = firstPageURL.replaceAll( fileNameBefore, fileName );
            //System.out.println( fileName + " " + fileNameBefore + " " +  comicURL[p-1] ); // debug
        }
        //System.exit(0); // debug
    }

    @Override
    public String getAllPageString( String urlString ) {
        String indexName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_nana_", "html" );

        Common.downloadFile( urlString, SetUp.getTempDirectory(), indexName, false, "" );

        return Common.getFileString( SetUp.getTempDirectory(), indexName );
    }

    @Override
    public boolean isSingleVolumePage( String urlString ) {
        if ( urlString.matches( "(?s).*/\\d+\\.html(?s).*" )
                || urlString.matches( "(?s).*\\.php(?s).*" ) ) // ex. http://www.nanadm.com/fgw/3151/32021.html
        {
            return true;
        } else {
            return false;
        }
    }

    public String getMainUrlFromSingleVolumeUrl( String volumeURL ) {
        // ex. http://www.nanadm.com/fgw/3151/32021.html轉為
        //     http://www.nanadm.com/fgw/3151/

        int endIndex = volumeURL.lastIndexOf( "/" ) + 1;
        String mainPageURL = volumeURL.substring( 0, endIndex );;

        return mainPageURL;
    }

    @Override
    public String getTitleOnSingleVolumePage( String urlString ) {
        String mainUrlString = "";
        
        if ( urlString.matches( "(?s).*/\\d+\\.html(?s).*" ) )
            mainUrlString = getMainUrlFromSingleVolumeUrl( urlString );
        else {
            String allPageString = getAllPageString( urlString );
            int beginIndex = allPageString.indexOf( "<H1>" );
            beginIndex = allPageString.indexOf( "\"", beginIndex ) + 1;
            int endIndex = allPageString.indexOf( "\"", beginIndex );
            
            mainUrlString = baseURL + allPageString.substring( beginIndex, endIndex );
        }
                
        return getTitleOnMainPage( mainUrlString, getAllPageString( mainUrlString ) );
    }

    @Override
    public String getTitleOnMainPage( String urlString, String allPageString ) {
        int beginIndex = allPageString.indexOf( "id=\"nryhw\"" );
        beginIndex = allPageString.indexOf( "</b>", beginIndex ) + 4;
        int endIndex = allPageString.indexOf( "</li>", beginIndex );
        String title = allPageString.substring( beginIndex, endIndex ).replaceAll( "：", "" ).trim();

        return Common.getStringRemovedIllegalChar( Common.getTraditionalChinese( title ) );
    }

    @Override
    public List<List<String>> getVolumeTitleAndUrlOnMainPage( String urlString, String allPageString ) {
        // combine volumeList and urlList into combinationList, return it.

        List<List<String>> combinationList = new ArrayList<List<String>>();
        List<String> urlList = new ArrayList<String>();
        List<String> volumeList = new ArrayList<String>();

        int beginIndex = allPageString.indexOf( "id=\"zaixianmanhua\"" );
        beginIndex = allPageString.indexOf( "<ul>", beginIndex );
        int endIndex = allPageString.indexOf( "</ul>", beginIndex );
        String tempString = allPageString.substring( beginIndex, endIndex );
        String[] tokens = tempString.split( ">|<|'" );

        int volumeCount = 0;

        for ( int i = 0 ; i < tokens.length ; i++ ) {
            if ( tokens[i].matches( "(?s).*\\.html(?s).*" ) || tokens[i].matches( "(?s).*\\.php(?s).*" ) ) {
                urlList.add( baseURL + tokens[i] );

                // 取得單集名稱
                String volumeTitle = tokens[i + 2];

                if ( volumeTitle.equals( "" ) ) // 中間插了一個<strong>
                {
                    volumeTitle = tokens[i + 4];
                }

                volumeList.add( getVolumeWithFormatNumber( Common.getStringRemovedIllegalChar(
                        Common.getTraditionalChinese( volumeTitle.trim() ) ) ) );

                volumeCount++;
            }
        }

        totalVolume = volumeCount;
        Common.debugPrintln( "共有" + totalVolume + "集" );

        combinationList.add( volumeList );
        combinationList.add( urlList );

        return combinationList;
    }
}
