/*
 ----------------------------------------------------------------------------------------------------
 Program Name : JComicDownloader
 Authors  : surveyorK
 Last Modified : 2012/12/12
 ----------------------------------------------------------------------------------------------------
 ChangeLog:
 5.11 : 新增對sogou的支援。
 ----------------------------------------------------------------------------------------------------
 */
package jcomicdownloader.module;

import java.io.File;
import jcomicdownloader.tools.*;
import jcomicdownloader.enums.*;
import java.util.*;
import jcomicdownloader.ComicDownGUI;
import jcomicdownloader.SetUp;
import jcomicdownloader.encode.Encoding;

public class ParseSogou extends ParseOnlineComicSite
{

    private int radixNumber; // use to figure out the name of pic
    private String jsName;
    protected String indexName;
    protected String indexEncodeName;
    protected String baseURL;

    /**

     @author user
     */
    public ParseSogou()
    {
        siteID = Site.SOGOU;
        siteName = "sogou";
        pageExtension = "html";
        pageCode = Encoding.GB2312;
        indexName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_" + siteName + "_parse_", "html" );
        indexEncodeName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_" + siteName + "_encode_parse_", "html" );

        jsName = "index_" + siteName + ".js";
        radixNumber = 154661; // default value, not always be useful!!

        baseURL = "http://music.sogou.com";
    }

    public ParseSogou( String webSite, String titleName )
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

        // 找出有幾頁
        totalPage = allPageString.split( "window.open" ).length - 1;
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

            // 先取得音樂下載頁面位址
            beginIndex = allPageString.indexOf( "window.open", beginIndex );
            beginIndex = allPageString.indexOf( "'", beginIndex ) + 1;
            endIndex = allPageString.indexOf( "'", beginIndex );
            songPageURL = allPageString.substring( beginIndex, endIndex ).trim();

            tempIndex = endIndex;

            // 取得音樂編號
            beginIndex = allPageString.lastIndexOf( "class=\"num\"", beginIndex );
            beginIndex = allPageString.indexOf( ">", beginIndex ) + 1;
            endIndex = allPageString.indexOf( "</td>", beginIndex );
            tags[TagEnum.TRACK] = Common.getTraditionalChinese(
                    allPageString.substring( beginIndex, endIndex ).trim() );

            // 取得音樂標題
            beginIndex = allPageString.indexOf( "title=\"", beginIndex );
            beginIndex = allPageString.indexOf( "\"", beginIndex ) + 1;
            endIndex = allPageString.indexOf( "\"", beginIndex );
            tags[TagEnum.TITLE] = Common.getStringRemovedIllegalChar( Common.getTraditionalChinese(
                    allPageString.substring( beginIndex, endIndex ).trim() ) );

            fileName = tags[TagEnum.TRACK] + "." + tags[TagEnum.TITLE];

            String songURL = getSongURL( songPageURL );

            Common.debugPrintln( tags[TagEnum.TRACK] + " " + tags[TagEnum.TITLE] + " : " + songURL );

            downloadMusic( songURL, fileName, i, tags );

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
        beginIndex = songPageString.indexOf( "class=\"linkbox\"" );
        beginIndex = songPageString.indexOf( "href=\"", beginIndex );
        beginIndex = songPageString.indexOf( "\"", beginIndex ) + 1;
        endIndex = songPageString.indexOf( "\"", beginIndex );
        String songURL = Common.getRegularURL(
                songPageString.substring( beginIndex, endIndex ).trim() );

        return songURL;
    }

    public void downloadMusic( String songURL, String fileName, int i, String[] tags )
    {
        downloadMusic( songURL, fileName, i, tags, null );
    }

    // 下載音樂的處理
    public void downloadMusic( String songURL, String fileName, int i, String[] tags, String cookie )
    {
        String tempSongURL = songURL.toLowerCase(); // 防止有某些網站附檔名亂用

        if ( tempSongURL.matches( "(?s).*\\.mp3(?s).*" ) )
        {
            fileName += ".mp3";
        }
        else if ( tempSongURL.matches( "(?s).*\\.wma(?s).*" ) )
        {
            fileName += ".wma";
        }
        else
        {
            fileName += ".music";
        }

        File file = new File( getDownloadDirectory() + fileName );
        Common.debugPrintln( file + " 的長度: " + file.length() );

        if ( !Common.isLegalURL( songURL ) )
        {
            Common.debugPrintln( songURL + " " + fileName + " 位址錯誤! 跳過!" );
            return;
        }
        else if ( !file.exists()
                // || (file.length() > Common.getDownloadFileLength( songURL ))
                || (Common.getDownloadFileLength( songURL ) - file.length() > 10000) )
        {
            CommonGUI.stateBarMainMessage = wholeTitle + " : ";
            CommonGUI.stateBarDetailMessage = "共" + totalPage + "首，第" + i
                    + "首下載中 : " + fileName;

            if ( SetUp.getShowDoneMessageAtSystemTray() && Common.withGUI() )
            {
                ComicDownGUI.trayIcon.setToolTip( CommonGUI.stateBarMainMessage
                        + CommonGUI.stateBarDetailMessage );
            }

            // 下載音樂
            if ( cookie == null )
            {
                Common.downloadFile( songURL, getDownloadDirectory(), fileName, false, "" );
            }
            else
            {
                Common.downloadFile( songURL, getDownloadDirectory(), fileName, true, cookie );
            }
        }

        if ( new File( getDownloadDirectory() + fileName ).exists() )
        {
            // 處理tag
            Common.setMp3Tag( getDownloadDirectory(), fileName, tags );
        }
        else
        {
            Common.debugPrintln( fileName + " 不存在!" );
        }
    }

    // 回傳已經設置共通tag的tags
    public String[] getCommonTag( String allPageString )
    {
        int beginIndex = 0;
        int endIndex = 0;
        String tempString = "";
        String[] tags = new String[ TagEnum.TAG_LENGTH ];

        beginIndex = allPageString.indexOf( "<h3>", beginIndex );
        beginIndex = allPageString.indexOf( ">", beginIndex ) + 1;
        endIndex = allPageString.indexOf( "</h3>", beginIndex );
        tempString = allPageString.substring( beginIndex, endIndex );
        tags[TagEnum.ALBUM] = Common.getTraditionalChinese( tempString );

        beginIndex = allPageString.indexOf( "href=", beginIndex );
        beginIndex = allPageString.indexOf( ">", beginIndex ) + 1;
        endIndex = allPageString.indexOf( "</a>", beginIndex );
        tempString = allPageString.substring( beginIndex, endIndex );
        tags[TagEnum.ARTIST] = Common.getTraditionalChinese( tempString );

        beginIndex = allPageString.indexOf( "<td", beginIndex );
        beginIndex = allPageString.indexOf( "：", beginIndex ) + 1;
        endIndex = allPageString.indexOf( "</td>", beginIndex );
        tempString = allPageString.substring( beginIndex, endIndex );
        tags[TagEnum.YEAR] = Common.getTraditionalChinese( tempString );

        beginIndex = allPageString.indexOf( "<td>", beginIndex );
        beginIndex = allPageString.indexOf( "：", beginIndex ) + 1;
        endIndex = allPageString.indexOf( "</td>", beginIndex );
        tempString = allPageString.substring( beginIndex, endIndex );
        tags[TagEnum.LANGUAGE] = Common.getTraditionalChinese( tempString );

        beginIndex = allPageString.indexOf( "曲风：", beginIndex );
        beginIndex = allPageString.indexOf( "：", beginIndex ) + 1;
        endIndex = allPageString.indexOf( "</td>", beginIndex );
        tempString = allPageString.substring( beginIndex, endIndex );
        tags[TagEnum.GENRE] = Common.getTraditionalChinese( tempString );

        beginIndex = allPageString.indexOf( "id=\"fullAlbumIntro\"", beginIndex );
        beginIndex = allPageString.indexOf( ">", beginIndex ) + 1;
        endIndex = allPageString.indexOf( "</span>", beginIndex );
        tempString = allPageString.substring( beginIndex, endIndex );
        tags[TagEnum.COMMENT] = Common.getTraditionalChinese( tempString );

        // 下載封面
        beginIndex = allPageString.indexOf( "class=\"cmain\"" );
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

        if ( urlString.matches( "(?s).*/singer/(?s).*" )
                || urlString.matches( "(?s).*/all_album(?s).*" ) )
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    public String getMainUrlFromSingleVolumeUrl( String volumeURL )
    {
        return volumeURL;
    }

    @Override
    public String getAllPageString( String urlString )
    {

        String indexName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_" + siteName + "_", pageExtension );
        String indexEncodeName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_" + siteName + "_encode_", pageExtension );

        Common.downloadFile( urlString, SetUp.getTempDirectory(), indexName, false, "" );
        Common.newEncodeFile( SetUp.getTempDirectory(), indexName, indexEncodeName, pageCode );

        return Common.getFileString( SetUp.getTempDirectory(), indexEncodeName );
    }

    @Override
    public String getTitleOnMainPage( String urlString, String allPageString )
    {
        String tempString = "";
        int beginIndex = allPageString.indexOf( "<h3>" );
        beginIndex = allPageString.indexOf( ">", beginIndex ) + 1;
        int endIndex = allPageString.indexOf( "</h3>", beginIndex );
        String title = allPageString.substring( beginIndex, endIndex ).trim();

        beginIndex = endIndex;
        endIndex = allPageString.indexOf( "</tr>", beginIndex );
        if ( endIndex < 0 ) // 歌手全專輯頁面
        {
            beginIndex = allPageString.indexOf( "listenAlbum(", beginIndex );
            beginIndex = allPageString.indexOf( "'", beginIndex ) + 1;
            endIndex = allPageString.indexOf( "'", beginIndex );
            title = allPageString.substring( beginIndex, endIndex ).trim();
        }
        else
        {
            tempString = allPageString.substring( beginIndex, endIndex );
        }

        // 代表是專輯頁面
        if ( tempString.indexOf( "href=\"/singer/" ) > 0 )
        {
            beginIndex = allPageString.indexOf( "href=\"/singer/", beginIndex );
            beginIndex = allPageString.indexOf( ">", beginIndex ) + 1;
            endIndex = allPageString.indexOf( "</a>", beginIndex );

            title = allPageString.substring( beginIndex, endIndex ).trim();
        }
        else // 代表是歌手頁面 
        {
        }

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

        // 先取得所有專輯的頁面
        beginIndex = allPageString.indexOf( "/all_album.so" );
        endIndex = allPageString.indexOf( "\"", beginIndex );
        String allAlbumURL = baseURL + allPageString.substring( beginIndex, endIndex ).trim();
        allPageString = getAllPageString( allAlbumURL );

        setList( allPageString, urlList, volumeList );


        // 代表有超過十張專輯，需要翻頁
        if ( allPageString.indexOf( "class=\"box3page\"" ) > 0 )
        {
            int pageCount = allPageString.split( "&page=" ).length - 2;

            for ( int i = 2; i <= pageCount; i++ )
            {
                allPageString = getAllPageString( allAlbumURL + "&page=" + i );

                setList( allPageString, urlList, volumeList );
            }
        }


        totalVolume = urlList.size();
        Common.debugPrintln( "共有" + totalVolume + "集" );

        combinationList.add( volumeList );
        combinationList.add( urlList );

        return combinationList;
    }

    public void setList( String allPageString, List<String> urlList, List<String> volumeList )
    {
        int beginIndex = 0;
        int endIndex = 0;
        String tempString = "";
        String volumeTitle = "";

        //  取得此頁專輯數目
        int albumCount = allPageString.split( "class=\"spcoverboxz\"" ).length - 1;
        beginIndex = endIndex = 0;

        // 開始解析專輯名稱和位址，並存入list
        for ( int i = 0; i < albumCount; i++ )
        {
            beginIndex = allPageString.indexOf( "class=\"spcoverboxz\"", beginIndex );
            beginIndex = allPageString.indexOf( "href=\"", beginIndex );
            beginIndex = allPageString.indexOf( "\"", beginIndex ) + 1;
            endIndex = allPageString.indexOf( "\"", beginIndex );
            tempString = allPageString.substring( beginIndex, endIndex ).trim();
            urlList.add( baseURL + tempString );

            // 取得專輯名稱
            beginIndex = allPageString.indexOf( "title=", beginIndex );
            beginIndex = allPageString.indexOf( "\"", beginIndex ) + 1;
            endIndex = allPageString.indexOf( "\"", beginIndex );
            volumeTitle = allPageString.substring( beginIndex, endIndex ).trim();

            // 取得發行日期
            beginIndex = allPageString.indexOf( "时间：", beginIndex );
            beginIndex = allPageString.indexOf( "：", beginIndex ) + 1;
            endIndex = allPageString.indexOf( "</p>", beginIndex );
            volumeTitle = allPageString.substring( beginIndex, endIndex ).trim() + " " + volumeTitle;

            volumeList.add( getVolumeWithFormatNumber( Common.getStringRemovedIllegalChar(
                    Common.getTraditionalChinese( volumeTitle ) ) ) );

        }
    }
}
