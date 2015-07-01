/*
 ----------------------------------------------------------------------------------------------------
 Program Name : JComicDownloader
 Authors  : surveyorK
 Last Modified : 2012/12/12
 ----------------------------------------------------------------------------------------------------
 ChangeLog:
 5.11 : 1. 新增對1ting的支援。
 ----------------------------------------------------------------------------------------------------
 */
package jcomicdownloader.module;

import java.io.File;
import jcomicdownloader.tools.*;
import jcomicdownloader.enums.*;
import java.util.*;
import jcomicdownloader.SetUp;
import jcomicdownloader.encode.Encoding;

public class Parse1Ting extends ParseSogou
{

    private int radixNumber; // use to figure out the name of pic
    private String jsName;
    protected String indexName;
    protected String indexEncodeName;
    protected String baseURL;

    /**

     @author user
     */
    public Parse1Ting()
    {
        siteID = Site.TING1;
        siteName = "1ting";
        pageExtension = "html";
        pageCode = Encoding.UTF8;
        indexName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_" + siteName + "_parse_", "html" );
        indexEncodeName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_" + siteName + "_encode_parse_", "html" );

        jsName = "index_" + siteName + ".js";
        radixNumber = 151661; // default value, not always be useful!!

        baseURL = "http://www.1ting.com";
    }

    public Parse1Ting( String webSite, String titleName )
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
        Common.newEncodeFile( SetUp.getTempDirectory(), indexName, indexEncodeName, pageCode );

        Common.debugPrintln( "作品名稱(title) : " + getTitle() );
        Common.debugPrintln( "章節名稱(wholeTitle) : " + getWholeTitle() );
    }

    @Override
    public void parseComicURL()
    { // parse URL and save all URLs in comicURL  //

        // 先取得所有的下載伺服器網址
        int beginIndex = 0, endIndex = 0;
        String tempString = "";

        Common.debugPrint( "開始解析這一集有幾頁 : " );
        String allPageString = Common.getFileString( SetUp.getTempDirectory(), indexEncodeName );

        beginIndex = allPageString.indexOf( "id=\"song-list\"" );
        endIndex = allPageString.indexOf( "</tbody>", beginIndex );
        tempString = allPageString.substring( beginIndex, endIndex );

        // 找出有幾頁
        totalPage = tempString.split( "class=\"songId\"" ).length - 1;
        Common.debugPrintln( "共 " + totalPage + " 首" );
        comicURL = new String[ totalPage ];

        int tempIndex = 0;
        String[] tags = getCommonTag( allPageString );
        String songPageURL = "";
        String songPageString = "";
        String fileName = "";
        beginIndex = endIndex = 0;
        for ( int i = 1; i <= totalPage && Run.isAlive; i++ )
        {
            beginIndex = tempIndex;

            // 取得音樂編號
            beginIndex = tempString.indexOf( "class=\"songId\"", beginIndex );
            beginIndex = tempString.indexOf( ">", beginIndex ) + 1;
            endIndex = tempString.indexOf( "</td>", beginIndex );
            tags[TagEnum.TRACK] = tempString.substring( beginIndex, endIndex ).trim();


            // 取得音樂標題
            beginIndex = tempString.indexOf( "class=\"songName\"", beginIndex );
            beginIndex = tempString.indexOf( ">", beginIndex ) + 1;
            endIndex = tempString.indexOf( "<", beginIndex );
            tags[TagEnum.TITLE] = Common.getStringRemovedIllegalChar( Common.getTraditionalChinese(
                    tempString.substring( beginIndex, endIndex ) ) ).replaceAll( "　", " " ).trim();

            // 取得音樂下載頁面位址
            beginIndex = tempString.indexOf( "class=\"songAction\"", beginIndex );
            beginIndex = tempString.indexOf( " href=", beginIndex ) + 1;
            beginIndex = tempString.indexOf( "\"", beginIndex ) + 1;
            endIndex = tempString.indexOf( "\"", beginIndex );
            songPageURL = baseURL + tempString.substring( beginIndex, endIndex ).trim();

            tempIndex = endIndex;


            fileName = tags[TagEnum.TRACK] + "." + tags[TagEnum.TITLE];

            String songURL = getSongURL( songPageURL );

            Common.debugPrintln( tags[TagEnum.TRACK] + " " + tags[TagEnum.TITLE] + " : " + songURL );
            //System.exit( 0 );
            String cookie = "PIN=cnGQFFDHubSu+M62gt4AAg==; __utma=162156220.1369328117.1355266509.1355266509.1355272810.2; Hm_lvt_32c12acc9a2efc3fa896bb3ebcd47ee7=1355266500,1355272817; __utmz=162156220.1355272810.2.2.utmcsr=1ting.com|utmccn=(referral)|utmcmd=referral|utmcct=/player/f7/player_442901.html; __utmb=162156220.1.10.1355272810";
            downloadMusic( songURL, fileName, i, tags, cookie );

            //System.exit( 0 ); // debug
        }
    }

    // 從播放頁面取得音樂檔網址
    public String getSongURL( String songPageURL )
    {
        int beginIndex = 0;
        int endIndex = 0;
        String songPageString = getAllPageString( songPageURL );

        // 開始解析音樂網址
        beginIndex = songPageString.indexOf( ".html\",\"", beginIndex ) + 1;
        beginIndex = songPageString.indexOf( ".html\",\"", beginIndex ) + 1;
        beginIndex = songPageString.indexOf( ".html\",\"", beginIndex ) + 1;
        beginIndex = songPageString.indexOf( "\",\"", beginIndex ) + 2;
        beginIndex = songPageString.indexOf( "\"", beginIndex ) + 1;
        endIndex = songPageString.indexOf( "\"", beginIndex );
        String songURL = "http://f.1ting.com" + Common.getRegularURL(
                songPageString.substring( beginIndex, endIndex ).replaceAll( "\\\\", "" ).trim() );
        Common.debugPrintln( "解析到的音樂檔位址: " + songURL );
        //System.exit( 0 );
        return songURL;
    }

    // 回傳已經設置共通tag的tags
    public String[] getCommonTag( String allPageString )
    {
        int beginIndex = 0;
        int endIndex = 0;
        String tempString = "";
        String[] tags = new String[ TagEnum.TAG_LENGTH ];

        beginIndex = allPageString.indexOf( "class=\"albumName\"" );
        beginIndex = allPageString.indexOf( " href=", beginIndex );
        beginIndex = allPageString.indexOf( ">", beginIndex ) + 1;
        endIndex = allPageString.indexOf( "<", beginIndex );
        tempString = allPageString.substring( beginIndex, endIndex ).trim();
        tags[TagEnum.ALBUM] = Common.getTraditionalChinese( tempString );

        beginIndex = allPageString.indexOf( "class=\"singer\"" );
        beginIndex = allPageString.indexOf( " href=", beginIndex );
        beginIndex = allPageString.indexOf( ">", beginIndex ) + 1;
        endIndex = allPageString.indexOf( "<", beginIndex );
        tempString = allPageString.substring( beginIndex, endIndex ).trim();
        tags[TagEnum.ARTIST] = Common.getTraditionalChinese( tempString );


        beginIndex = allPageString.indexOf( "class=\"pubdate\"" );
        beginIndex = allPageString.indexOf( " href=", beginIndex );
        beginIndex = allPageString.indexOf( ">", beginIndex ) + 1;
        endIndex = allPageString.indexOf( "<", beginIndex );
        tempString = allPageString.substring( beginIndex, endIndex ).trim();
        tags[TagEnum.YEAR] = Common.getTraditionalChinese( tempString );


        beginIndex = allPageString.indexOf( "class=\"language\"" );
        beginIndex = allPageString.indexOf( "<dd>", beginIndex );
        beginIndex = allPageString.indexOf( ">", beginIndex ) + 1;
        endIndex = allPageString.indexOf( "<", beginIndex );
        tempString = allPageString.substring( beginIndex, endIndex ).trim();
        tags[TagEnum.LANGUAGE] = Common.getTraditionalChinese( tempString );

        beginIndex = allPageString.indexOf( "class=\"style\"" );
        beginIndex = allPageString.indexOf( " href=", beginIndex );
        beginIndex = allPageString.indexOf( ">", beginIndex ) + 1;
        endIndex = allPageString.indexOf( "<", beginIndex );
        tempString = allPageString.substring( beginIndex, endIndex ).trim();
        tags[TagEnum.GENRE] = Common.getTraditionalChinese( tempString );

        beginIndex = allPageString.indexOf( "class=\"description\"" );
        beginIndex = allPageString.indexOf( ">", beginIndex ) + 1;
        endIndex = allPageString.indexOf( "</div>", beginIndex );
        tempString = allPageString.substring( beginIndex, endIndex ).trim();
        tempString = tempString.replaceAll( "<br />", "" );
        tempString = tempString.replaceAll( "<BR>", "" );
        tags[TagEnum.COMMENT] = Common.getTraditionalChinese( tempString );

        // 下載封面
        beginIndex = allPageString.indexOf( "class=\"albumPic" );
        beginIndex = allPageString.indexOf( "<img src=", beginIndex ) + 1;
        beginIndex = allPageString.indexOf( "\"", beginIndex ) + 1;
        endIndex = allPageString.indexOf( "\"", beginIndex );
        String coverURL = allPageString.substring( beginIndex, endIndex );
        String coverFileName = "cover.jpg";
        Common.downloadFile( coverURL, getDownloadDirectory(), coverFileName, false, "" );
        tags[TagEnum.COVER] = getDownloadDirectory() + coverFileName;


        Common.debugPrintln( "專輯名稱 : " + tags[TagEnum.ALBUM] );
        Common.debugPrintln( "演唱者 : " + tags[TagEnum.ARTIST] );
        Common.debugPrintln( "類型 : " + tags[TagEnum.GENRE] );
        Common.debugPrintln( "語言 : " + tags[TagEnum.LANGUAGE] );
        Common.debugPrintln( "年分 : " + tags[TagEnum.YEAR] );
        Common.debugPrintln( "封面 : " + tags[TagEnum.COVER] );
        Common.debugPrintln( "註解 : " + tags[TagEnum.COMMENT] );
        //Common.debugPrintln( "歌名 : " + tags[TagEnum.TITLE] );
        //Common.debugPrintln( "順序 : " + tags[TagEnum.TRACK] );


        return tags;
    }

    @Override
    public boolean isSingleVolumePage( String urlString )
    {
        if ( urlString.matches( "(?s).*/player/(?s).*" ) )
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public String getAlbumPageURL( String allPageString )
    {

        int beginIndex = allPageString.indexOf( "/album/\"" );
        beginIndex = allPageString.lastIndexOf( "\"", beginIndex ) + 1;
        int endIndex = allPageString.indexOf( "\"", beginIndex );
        String tempString = allPageString.substring( beginIndex, endIndex );
        String albumPageURL = baseURL + tempString;
        Common.debugPrintln( "解析到的全專輯頁面: " + albumPageURL );

        return albumPageURL;
    }

    @Override
    public String getTitleOnMainPage( String urlString, String allPageString )
    {


        int beginIndex = allPageString.indexOf( "<h2>" );
        beginIndex = allPageString.indexOf( ">", beginIndex ) + 1;
        beginIndex = allPageString.indexOf( ">", beginIndex ) + 1;
        int endIndex = allPageString.indexOf( "<", beginIndex );
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

        int beginIndex = 0;
        int endIndex = 0;
        String tempString = "";
        String volumeTitle = "";
        String volumeURL = "";

        // 先取得全專輯頁面的內容
        urlString = getAlbumPageURL( allPageString );
        allPageString = getAllPageString( urlString );

        beginIndex = allPageString.indexOf( "class=\"albumList\"" );
        endIndex = allPageString.indexOf( "</div>", beginIndex );
        tempString = allPageString.substring( beginIndex, endIndex );

        int volumeCount = tempString.split( "class=\"albumLink\"" ).length - 1;
        beginIndex = endIndex = 0;
        for ( int i = 0; i < volumeCount; i++ )
        {

            beginIndex = allPageString.indexOf( "class=\"albumLink\"", beginIndex );
            beginIndex = allPageString.lastIndexOf( " href=", beginIndex );
            beginIndex = allPageString.indexOf( "\"", beginIndex ) + 1;
            endIndex = allPageString.indexOf( "\"", beginIndex );
            volumeURL = baseURL + allPageString.substring( beginIndex, endIndex );
            urlList.add( volumeURL );

            // 取得專輯名稱
            beginIndex = allPageString.indexOf( "class=\"albumName\"", beginIndex );
            beginIndex = allPageString.indexOf( ">", beginIndex ) + 1;
            endIndex = allPageString.indexOf( "<", beginIndex );
            volumeTitle = allPageString.substring( beginIndex, endIndex );
            // 取得發行日期
            beginIndex = allPageString.indexOf( "class=\"albumDate\"", beginIndex );
            beginIndex = allPageString.indexOf( ">", beginIndex ) + 1;
            beginIndex = allPageString.indexOf( ">", beginIndex ) + 1;
            endIndex = allPageString.indexOf( "<", beginIndex );
            volumeTitle = allPageString.substring( beginIndex, endIndex ) + " " + volumeTitle;
            volumeList.add( getVolumeWithFormatNumber( Common.getStringRemovedIllegalChar(
                    Common.getTraditionalChinese( volumeTitle ) ) ) );

        }

        totalVolume = urlList.size();
        Common.debugPrintln( "共有" + totalVolume + "集" );

        combinationList.add( volumeList );
        combinationList.add( urlList );

        return combinationList;
    }
}