/*
 ----------------------------------------------------------------------------------------------------
 Program Name : JComicDownloader
 Authors  : surveyorK
 Last Modified : 2011/12/16
 ----------------------------------------------------------------------------------------------------
 ChangeLog:
 2.08: 修復部份89890解析錯誤的bug。
 ----------------------------------------------------------------------------------------------------
 */
package jcomicdownloader.module;

import jcomicdownloader.tools.*;
import jcomicdownloader.enums.*;
import jcomicdownloader.*;

import java.io.*;
import java.util.*;
import java.text.*;
import jcomicdownloader.encode.Encoding;
import jcomicdownloader.encode.Zhcode;

public class ParseCC extends ParseOnlineComicSite {

    private int radixNumber; // use to figure out the name of pic
    private String volpic; // pic url: http://~/2011/111/dagui/06/
    private int tpf2; // pic url: http://pic"tpf2".89890.com/~
    private int tpf; // length of pic name: (tpf+1)*2
    private String jsName;
    private String indexName;
    private String indexEncodeName;
    protected String baseURL;

    /**
     @author user
     */
    public ParseCC() {
        siteID = Site.CC;
        siteName = "CC";
        indexName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_kuku_parse_", "html" );
        indexEncodeName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_kuku_encode_parse_", "html" );

        jsName = "index_cc.js";
        radixNumber = 18527; // 大部份的值，後來也發現有少數19527

        baseURL = "http://www.89890.com";
    }

    public ParseCC( String webSite, String titleName ) {
        this();
        this.webSite = webSite;
        this.title = titleName;
    }

    @Override
    public void setParameters() { // let all the non-set attributes get values
        Common.debugPrintln( "開始解析各參數 :" );
        Common.downloadFile( webSite, SetUp.getTempDirectory(), indexName, false, "" );
        Common.newEncodeFile( SetUp.getTempDirectory(), indexName, indexEncodeName );

        if ( getWholeTitle() == null || getWholeTitle().equals( "" ) ) {
            Common.debugPrintln( "開始解析title和wholeTitle :" );
            String allPageString = Common.getFileString( SetUp.getTempDirectory(), indexEncodeName );

            int beginIndex = allPageString.indexOf( "<h1>" ) + 4;
            int endIndex = allPageString.indexOf( "</h1>", beginIndex );
            String tempTitleString = allPageString.substring( beginIndex, endIndex ).trim();

            tempTitleString = tempTitleString.replaceAll( "<span>|</span>", "" );

            setWholeTitle( getVolumeWithFormatNumber( Common.getStringRemovedIllegalChar(
                Common.getTraditionalChinese( tempTitleString.trim() ) ) ) );
        }

        Common.debugPrintln( "作品名稱(title) : " + getTitle() );
        Common.debugPrintln( "章節名稱(wholeTitle) : " + getWholeTitle() );

    }

    @Override
    public void parseComicURL() { // parse URL and save all URLs in comicURL

        String allPageString = Common.getFileString( SetUp.getTempDirectory(), indexEncodeName );
        Common.debugPrint( "開始解析這一集有幾頁 : " );

        int beginIndex = 0, endIndex = 0;

        // 先找出comicid
        beginIndex = allPageString.indexOf( "var comicid" );
        beginIndex = allPageString.indexOf( "'", beginIndex ) + 1;
        endIndex = allPageString.indexOf( "'", beginIndex );
        String comicid = allPageString.substring( beginIndex, endIndex );

        // 找出chapterid
        beginIndex = allPageString.indexOf( "var chapterid", beginIndex );
        beginIndex = allPageString.indexOf( "'", beginIndex ) + 1;
        endIndex = allPageString.indexOf( "'", beginIndex );
        String chapterid = allPageString.substring( beginIndex, endIndex );

        // 找出pics
        beginIndex = allPageString.indexOf( "var pics", beginIndex );
        beginIndex = allPageString.indexOf( "'", beginIndex ) + 1;
        endIndex = allPageString.indexOf( "'", beginIndex );
        String pics = allPageString.substring( beginIndex, endIndex );

        // 找出副檔名
        int pictype = 0; // 預設值0，也就是jpg
        beginIndex = allPageString.indexOf( "var pictype", beginIndex );
        
        if ( beginIndex > 0 ) {
            beginIndex = allPageString.indexOf( "'", beginIndex ) + 1;
            endIndex = allPageString.indexOf( "'", beginIndex );
            pictype = Integer.parseInt(
                allPageString.substring( beginIndex, endIndex ).trim() );
        }

        String extension = getExtension( pictype ); // 取得副檔名

        String id = "" + ( Integer.parseInt( comicid ) / 1000 );
        String basePicURL = "http://pics1.89890.com";

        totalPage = Integer.parseInt( pics.trim() );
        comicURL = new String[totalPage];

        for ( int i = 1; i <= totalPage && Run.isAlive; i++ ) {

            comicURL[i - 1] = basePicURL + "/"
                + id + "/"
                + comicid + "/"
                + chapterid + "/" + i + "." + extension;
            //Common.debugPrintln( comicURL[i-1] );
        }
        //System.exit( 1 );
    }

    // 從pictype值取得副檔名
    private String getExtension( int pictype ) {
        String mame = "jpg";
        if ( pictype != 0 ) {
            switch ( pictype ) {
                case 1:
                    mame = "jpeg";
                    break;
                case 2:
                    mame = "gif";
                    break;
                case 3:
                    mame = "png";
                    break;
                case 4:
                    mame = "bmp";
                    break;
                default:
                    mame = "jpg";
                    break;
            }
        }

        return mame;
    }

    @Override
    public String getAllPageString( String urlString ) {
        String indexName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_CC_", "html" );
        String indexEncodeName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_CC_encode_", "html" );

        Common.downloadFile( urlString, SetUp.getTempDirectory(), indexName, false, "" );
        Common.newEncodeFile( SetUp.getTempDirectory(), indexName, indexEncodeName, Zhcode.GB2312 );

        return Common.getFileString( SetUp.getTempDirectory(), indexEncodeName );
    }

    @Override
    public boolean isSingleVolumePage( String urlString ) {
        if ( urlString.split( "/" ).length == 6 ) // ex. http://www.89890.com/comic/7953/
        {
            return true;
        }
        else {
            return false;
        }
    }

    @Override
    public String getTitleOnSingleVolumePage( String urlString ) {
        String[] splitURL = urlString.split( "/" );

        String newUrlString = "";
        for ( int i = 0; i < 5; i++ ) {
            newUrlString += splitURL[i] + "/";
        }

        return getTitleOnMainPage( urlString, getAllPageString( newUrlString ) );
    }

    @Override
    public String getTitleOnMainPage( String urlString, String allPageString ) {


        String[] lines = allPageString.split( "\n" );

        int titleIndex = 0;
        while ( !lines[titleIndex].matches( "(?s).*title(?s).*" ) ) {
            titleIndex++;
        }

        int beginIndex = lines[titleIndex].indexOf( "<title>", 1 );
        int endIndex = lines[titleIndex].indexOf( "_", beginIndex );

        return Common.getStringRemovedIllegalChar( Common.getTraditionalChinese( lines[titleIndex].substring( beginIndex + 8, endIndex ) ) );
    }

    @Override
    public List<List<String>> getVolumeTitleAndUrlOnMainPage( String urlString, String allPageString ) {
        // combine volumeList and urlList into combinationList, return it.

        List<List<String>> combinationList = new ArrayList<List<String>>();
        List<String> urlList = new ArrayList<String>();
        List<String> volumeList = new ArrayList<String>();

        int beginIndex = allPageString.indexOf( "class=\"booklist\"" );
        int endIndex = allPageString.indexOf( "</ul>", beginIndex );
        String tempString = allPageString.substring( beginIndex, endIndex );
        int volumeCount = tempString.split( " href=" ).length - 1;

        String tempURL = "";
        String tempVolume = "";

        for ( int i = 0; i < volumeCount; i++ ) {

            // 取得單集位址
            beginIndex = allPageString.indexOf( " href=", beginIndex );
            beginIndex = allPageString.indexOf( "\"", beginIndex ) + 1;
            endIndex = allPageString.indexOf( "\"", beginIndex );
            tempURL = baseURL + allPageString.substring( beginIndex, endIndex );
            urlList.add( tempURL.trim() );


            // 取得單集名稱
            beginIndex = allPageString.indexOf( ">", beginIndex ) + 1;
            endIndex = allPageString.indexOf( "<", beginIndex );
            tempVolume = allPageString.substring( beginIndex, endIndex );
            volumeList.add( getVolumeWithFormatNumber( Common.getStringRemovedIllegalChar(
                Common.getTraditionalChinese( tempVolume.trim() ) ) ) );

            //Common.debugPrintln( i + " : " + tempVolume );
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
