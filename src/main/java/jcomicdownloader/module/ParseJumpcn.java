/*
----------------------------------------------------------------------------------------------------
Program Name : JComicDownloader
Authors  : surveyorK
Last Modified : 2011/11/2
----------------------------------------------------------------------------------------------------
ChangeLog:
3.12: 1. 修復jumpcn解析標題錯誤的問題。
1.12: 1. 新增對jumpcn的支援。
 *    2. 改成一邊解析網址一邊下載。
----------------------------------------------------------------------------------------------------
 */
package jcomicdownloader.module;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import jcomicdownloader.tools.*;
import jcomicdownloader.enums.*;
import java.util.*;
import jcomicdownloader.SetUp;

public class ParseJumpcn extends ParseOnlineComicSite {

    private int radixNumber; // use to figure out the name of pic
    private String jsName;
    protected String indexName;
    protected String indexEncodeName;

    /**
     *
     * @author user
     */
    public ParseJumpcn() {
        siteID = Site.JUMPCN;
        siteName = "JumpCN";
        indexName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_jumpcn_parse_", "html" );
        indexEncodeName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_jumpcn_encode_parse_", "html" );

        jsName = "index_jumpcn.js";
        radixNumber = 185271; // default value, not always be useful!!
    }

    public ParseJumpcn( String webSite, String titleName ) {
        this();
        this.webSite = webSite;
        this.title = titleName;
    }

    @Override
    public void setParameters() {
        Common.debugPrintln( "開始解析各參數 :" );

        Common.debugPrintln( "開始解析title和wholeTitle :" );

        if ( getWholeTitle() == null || getWholeTitle().equals( "" ) ) {
            String allPageString = getAllPageString( webSite );

            int beginIndex = allPageString.indexOf( "<title>" ) + 7;
            int endIndex = allPageString.indexOf( " ", beginIndex );
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

        String allPageString = getAllPageString( webSite );
        Common.debugPrint( "開始解析這一集有幾頁 : " );

        totalPage = allPageString.split( "option value=" ).length - 1;
        Common.debugPrintln( "共 " + totalPage + " 頁" );
        comicURL = new String[totalPage];

        // ex.http://www.jumpcn.com/m/du-bo-zhui-tian-lv-he-ye/24/
        String volumeURL = webSite.substring( 0, webSite.length() - 2 );

        for ( int p = 1 ; p <= totalPage && Run.isAlive; p++ ) {
            // 檢查下一張圖是否存在同個資料夾，若存在就跳下一張
            if ( !Common.existPicFile( getDownloadDirectory(), p ) ||
                 !Common.existPicFile( getDownloadDirectory(), p + 1 ) ) {
                allPageString = getAllPageString( volumeURL + p + "/" );
                String[] tokens = allPageString.split( "\"|<|>" );

                for ( int i = 0 ; i < tokens.length ; i++ ) {
                    if ( tokens[i].matches( "\\s*img src=" ) ) {
                        comicURL[p - 1] = tokens[i + 1].trim();
                        break;
                    }
                }
                //Common.debugPrintln( p + " " + comicURL[p-1] ); // debug

                // 每解析一個網址就下載一張圖
                singlePageDownload( getTitle(), getWholeTitle(), comicURL[p - 1], totalPage, p, 0 );
            }
        }
        //System.exit(0); // debug
    }

    @Override
    public String getAllPageString( String urlString ) {
        String indexName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_dmeden_", "html" );
        String indexEncodeName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_CC_encode_", "html" );

        Common.downloadFile( urlString, SetUp.getTempDirectory(), indexName, false, "" );
        Common.newEncodeFile( SetUp.getTempDirectory(), indexName, indexEncodeName );

        return Common.getFileString( SetUp.getTempDirectory(), indexEncodeName );
    }

    @Override
    public boolean isSingleVolumePage( String urlString ) {
        if ( urlString.split( "//|/" ).length > 5 ) // ex. http://
        {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String getTitleOnSingleVolumePage( String urlString ) {

        int endIndex = Common.getIndexOfOrderKeyword( urlString, "/", 5 ) + 1;
        String mainPageUrlString = urlString.substring( 0, endIndex );

        return getTitleOnMainPage( mainPageUrlString, getAllPageString( mainPageUrlString ) );
    }

    @Override
    public String getTitleOnMainPage( String urlString, String allPageString ) {
        int beginIndex = allPageString.indexOf( "<h1>" );
        beginIndex = allPageString.indexOf( "href=", beginIndex );
        beginIndex = allPageString.indexOf( ">", beginIndex ) + 1;
        int endIndex = allPageString.indexOf( "<", beginIndex );
        String title = allPageString.substring( beginIndex, endIndex );

        return Common.getStringRemovedIllegalChar( Common.getTraditionalChinese( title ) );
    }

    @Override
    public List<List<String>> getVolumeTitleAndUrlOnMainPage( String urlString, String allPageString ) {
        // combine volumeList and urlList into combinationList, return it.

        List<List<String>> combinationList = new ArrayList<List<String>>();
        List<String> urlList = new ArrayList<String>();
        List<String> volumeList = new ArrayList<String>();

        int beginIndex = allPageString.indexOf( "<div class=\"fn3\"" );
        int endIndex = allPageString.indexOf( "<script", beginIndex );
        String tempString = allPageString.substring( beginIndex, endIndex );
        String[] tokens = tempString.split( "\"|>|<" );

        int volumeCount = 0;
        String baseURL = "http://www.jumpcn.com";

        for ( int i = 0 ; i < tokens.length ; i++ ) {
            if ( tokens[i].matches( "(?s).*href=\\s*" ) ) {
                urlList.add( baseURL + tokens[i + 1] );

                // 取得單集名稱
                String volumeTitle = tokens[i + 7];
                volumeList.add( getVolumeWithFormatNumber( Common.getStringRemovedIllegalChar(
                        Common.getTraditionalChinese( volumeTitle.trim() ) ) ) );

                //Common.debugPrintln( baseURL + tokens[i+1] + " " + volumeTitle );                

                volumeCount++;
            }
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
