/*
 ----------------------------------------------------------------------------------------------------
 Program Name : JComicDownloader
 Authors  : surveyorK
 Last Modified : 2012/12/15
 ----------------------------------------------------------------------------------------------------
 ChangeLog:
 5.12 : 1. 新增對xiami的支援。
 ----------------------------------------------------------------------------------------------------
 */
package jcomicdownloader.module;

import java.io.File;
import jcomicdownloader.tools.*;
import jcomicdownloader.enums.*;
import java.util.*;
import jcomicdownloader.SetUp;
import jcomicdownloader.encode.Encoding;

public class ParseXiami extends ParseSogou
{

    private int radixNumber; // use to figure out the name of pic
    private String jsName;
    protected String indexName;
    protected String indexEncodeName;
    protected String baseURL;

    /**

     @author user
     */
    public ParseXiami()
    {
        siteID = Site.XIAMI;
        siteName = "xiami";
        pageExtension = "html";
        pageCode = Encoding.UTF8;
        indexName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_" + siteName + "_parse_", "html" );
        indexEncodeName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_" + siteName + "_encode_parse_", "html" );

        jsName = "index_" + siteName + ".js";
        radixNumber = 152661; // default value, not always be useful!!

        baseURL = "http://www.xiami.com";
    }

    public ParseXiami( String webSite, String titleName )
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

        beginIndex = allPageString.indexOf( "class=\"track_list\"" );
        endIndex = allPageString.indexOf( "class=\"ctrl_play\"", beginIndex );
        tempString = allPageString.substring( beginIndex, endIndex );

        // 找出有幾頁
        totalPage = tempString.split( "class=\"trackid\"" ).length - 1;
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
            beginIndex = tempString.indexOf( "class=\"trackid\"", beginIndex );
            beginIndex = tempString.indexOf( ">", beginIndex ) + 1;
            endIndex = tempString.indexOf( "</td>", beginIndex );
            tags[TagEnum.TRACK] = tempString.substring( beginIndex, endIndex ).trim();


            // 取得音樂標題
            beginIndex = tempString.indexOf( " title=", beginIndex );
            beginIndex = tempString.indexOf( ">", beginIndex ) + 1;
            endIndex = tempString.indexOf( "<", beginIndex );
            tags[TagEnum.TITLE] = Common.getStringRemovedIllegalChar( Common.getTraditionalChinese(
                    tempString.substring( beginIndex, endIndex ) ) ).replaceAll( "　", " " ).trim();

            // 取得音樂下載頁面位址
            beginIndex = tempString.indexOf( "play('", beginIndex );
            beginIndex = tempString.indexOf( "(", beginIndex ) + 1;
            endIndex = tempString.indexOf( ")", beginIndex );
            String[] playParameters = getPlayParameters( tempString.substring( beginIndex, endIndex ) );

            songPageURL = "http://www.xiami.com/song/playlist/id/"
                    + playParameters[0]
                    + "/object_name/"
                    + playParameters[1]
                    + "/object_id/"
                    + playParameters[2];

            tempIndex = endIndex;


            fileName = tags[TagEnum.TRACK] + "." + tags[TagEnum.TITLE];

            String songURL = getSongURL( songPageURL );

            Common.debugPrintln( tags[TagEnum.TRACK] + " " + tags[TagEnum.TITLE] + " : " + songURL );
            //System.exit( 0 );
            String cookie = "";
            downloadMusic( songURL, fileName, i, tags, cookie );

            //System.exit( 0 ); // debug
        }
    }

    // 取得 play function的參數
    private String[] getPlayParameters( String tempString )
    {
        String[] playParameters = new String[ 3 ];

        String[] tempStrings = tempString.split( "," );
        if ( tempStrings.length == 3 ) // 三個參數都有
        {
            playParameters[0] = tempStrings[0].substring( 1, tempStrings[0].length() - 1 );
            playParameters[1] = tempStrings[1].substring( 1, tempStrings[1].length() - 1 );
            playParameters[2] = tempStrings[2].substring( 1, tempStrings[2].length() - 1 );
        }
        else if ( tempStrings.length == 2 ) // 只有前兩個參數
        {
            playParameters[0] = tempStrings[0].substring( 1, tempStrings[0].length() - 1 );
            playParameters[1] = tempStrings[1].substring( 1, tempStrings[1].length() - 1 );
            playParameters[2] = "0";
        }
        else if ( tempStrings.length == 1 ) // 只有第一個參數
        {
            playParameters[0] = tempStrings[0].substring( 1, tempStrings[0].length() - 1 );
            playParameters[1] = "default";
            playParameters[2] = "0";
        }

        return playParameters;
    }

    // 從播放頁面取得音樂檔網址
    public String getSongURL( String songPageURL )
    {
        int beginIndex = 0;
        int endIndex = 0;
        String songPageString = getAllPageString( songPageURL );

        // 開始解析音樂網址
        beginIndex = songPageString.indexOf( "<location>", beginIndex ) + 1;
        beginIndex = songPageString.indexOf( ">", beginIndex ) + 1;
        endIndex = songPageString.indexOf( "</location>", beginIndex );
        String songURL = getMusicURL(
                songPageString.substring( beginIndex, endIndex ) );
        Common.debugPrintln( "解析到的音樂檔位址: " + songURL );
        //System.exit( 0 );
        return songURL;
    }
    
    // 從location轉為位址
    private String getMusicURL( String location )
    {
        int loc10 = 0;
        int loc2 = Integer.parseInt( location.substring( 0, 1 ) );
        String loc3 = location.substring( 1, location.length() );
        int loc4 = loc3.length() / loc2;
        int loc5 = loc3.length() % loc2;
        //String[] loc6 = new String[ 100 ];
        java.util.List<String> loc6 = new ArrayList<String>();
        int loc7 = 0;

        int beginIndex = 0;
        int endIndex = 0;

        while ( loc7 < loc5 )
        {
            beginIndex = (loc4 + 1) * loc7;
            endIndex = beginIndex + (loc4 + 1);
            loc6.add( loc3.substring( beginIndex, endIndex ) );
            Common.debugPrintln( "loc6[" + loc7 + "] = " + loc6.get( loc7 ) );

            loc7++;
        }
        loc7 = loc5;

        Common.debugPrintln( "loc7 = " + loc7 );
        while ( loc7 < loc2 )
        {
            beginIndex = loc4 * (loc7 - loc5) + (loc4 + 1) * loc5;
            endIndex = beginIndex + loc4;

            loc6.add( loc3.substring( beginIndex, endIndex ) );
            Common.debugPrintln( "loc6[" + loc7 + "] = " + loc6.get( loc7 ) );

            loc7++;
        }
        //Common.debugPrintln( "-----------------" );
        //Common.debugPrintln( "loc6.size = " + loc6.size() );

        String loc8 = "";
        loc7 = 0;
        while ( loc7 < loc6.get( 0 ).length() )
        {
            loc10 = 0;
            while ( loc10 < (loc6.size()) )
            {
                String temp = loc6.get( loc10 );
                //Common.debugPrintln( "loc6[" + loc10 + "] = " + temp );
                //Common.debugPrintln( "loc6[" + loc10 + "].length() = " + temp.length() );
                //Common.debugPrintln( "loc7 = " + loc7 );
                if ( loc7 < temp.length() )
                {
                    loc8 += temp.substring( loc7, loc7 + 1 );
                }
                loc10++;
            }

            //Common.debugPrintln( "loc7 = " + loc7 );
            //Common.debugPrintln( "loc8 = " + loc8 );

            loc7++;
        }

        Common.debugPrintln( "-----------------" );

        loc8 = Common.unescape( loc8 );

        String loc9 = "";
        loc7 = 0;

        while ( loc7 < loc8.length() )
        {
            if ( loc8.substring( loc7, loc7 + 1 ).equals( "^" ) )
            {
                loc9 += "0";
            }
            else
            {
                loc9 += loc8.substring( loc7, loc7 + 1 );
            }

            loc7++;
        }

        loc9 = loc9.replaceAll( "\\+", " " );

        return Common.getRegularURL( loc9 );

    }

    // 回傳已經設置共通tag的tags
    public String[] getCommonTag( String allPageString )
    {
        int beginIndex = 0;
        int endIndex = 0;
        String tempString = "";
        String[] tags = new String[ TagEnum.TAG_LENGTH ];

        beginIndex = allPageString.indexOf( "<h1" ) + 1;
        beginIndex = allPageString.indexOf( "<h1", beginIndex ) + 1;
        beginIndex = allPageString.indexOf( ">", beginIndex ) + 1;
        endIndex = allPageString.indexOf( "<", beginIndex );
        tempString = allPageString.substring( beginIndex, endIndex ).trim();
        tags[TagEnum.ALBUM] = Common.getTraditionalChinese( tempString );

        beginIndex = allPageString.indexOf( "class=\"item\"" );
        beginIndex = allPageString.indexOf( " href=", beginIndex );
        beginIndex = allPageString.indexOf( ">", beginIndex ) + 1;
        endIndex = allPageString.indexOf( "<", beginIndex );
        tempString = allPageString.substring( beginIndex, endIndex ).trim();
        tags[TagEnum.ARTIST] = Common.getTraditionalChinese( tempString );


        beginIndex = allPageString.indexOf( "class=\"item\"", beginIndex );
        beginIndex = allPageString.indexOf( "<td", beginIndex );
        beginIndex = allPageString.indexOf( ">", beginIndex ) + 1;
        endIndex = allPageString.indexOf( "<", beginIndex );
        tempString = allPageString.substring( beginIndex, endIndex ).trim();
        tags[TagEnum.LANGUAGE] = Common.getTraditionalChinese( tempString );

        // 此處取唱片公司
        beginIndex = allPageString.indexOf( "class=\"item\"", beginIndex );
        beginIndex = allPageString.indexOf( " href=", beginIndex );
        beginIndex = allPageString.indexOf( ">", beginIndex ) + 1;
        endIndex = allPageString.indexOf( "<", beginIndex );
        tempString = allPageString.substring( beginIndex, endIndex ).trim();
        tags[TagEnum.COMMENT] = Common.getTraditionalChinese( "唱片公司: " + tempString );


        beginIndex = allPageString.indexOf( "class=\"item\"", beginIndex );
        beginIndex = allPageString.indexOf( "<td", beginIndex );
        beginIndex = allPageString.indexOf( ">", beginIndex ) + 1;
        endIndex = allPageString.indexOf( "<", beginIndex );
        tempString = allPageString.substring( beginIndex, endIndex ).trim();
        tags[TagEnum.YEAR] = Common.getTraditionalChinese( tempString );



        beginIndex = allPageString.indexOf( "class=\"item\"", beginIndex );
        beginIndex = allPageString.indexOf( "<td", beginIndex );
        beginIndex = allPageString.indexOf( ">", beginIndex ) + 1;
        endIndex = allPageString.indexOf( "<", beginIndex );
        tempString = allPageString.substring( beginIndex, endIndex ).trim();
        tags[TagEnum.GENRE] = Common.getTraditionalChinese( tempString );


        // 下載封面
        beginIndex = allPageString.indexOf( "id=\"album_cover\"" );
        beginIndex = allPageString.indexOf( " href=", beginIndex ) + 1;
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
        //System.exit( 0 );

        return tags;
    }

    @Override
    public boolean isSingleVolumePage( String urlString )
    {
        if ( urlString.matches( "(?s).*/song/play(?s).*" ) )
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

        int beginIndex = allPageString.indexOf( "/artist/album/" );
        int endIndex = allPageString.indexOf( "\"", beginIndex );
        String tempString = allPageString.substring( beginIndex, endIndex );
        String albumPageURL = baseURL + tempString;
        Common.debugPrintln( "解析到的全專輯頁面: " + albumPageURL );

        return albumPageURL;
    }

    @Override
    public String getTitleOnMainPage( String urlString, String allPageString )
    {


        int beginIndex = allPageString.indexOf( "name:\"" );
        int endIndex = 0;

        if ( beginIndex > 0 ) // 解析方法一
        {
            beginIndex = allPageString.indexOf( "\"", beginIndex ) + 1;
            endIndex = allPageString.indexOf( "\"", beginIndex );
        }
        else if ( urlString.matches( "(?s).*song/detail/(?s).*" ) ) {
            beginIndex = allPageString.indexOf( "href=\"/artist/" ) + 1;
            beginIndex = allPageString.indexOf( "href=\"/artist/", beginIndex ) + 1;
            beginIndex = allPageString.indexOf( ">", beginIndex ) + 1;
            endIndex = allPageString.indexOf( "<", beginIndex );
        }
        else // 解析方法二
        {
            beginIndex = allPageString.indexOf( "<title>" );
            beginIndex = allPageString.indexOf( ">", beginIndex ) + 1;

            endIndex = allPageString.indexOf( "</title>", beginIndex );
            endIndex = allPageString.lastIndexOf( "的", endIndex );
        }
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

        int pageCount = allPageString.split( "/page/" ).length - 1;
        String[] pageURLs;
        
        String artistPageURL = urlString.replaceAll( "album/id/", "" );

        if ( pageCount > 0 )
        {
            pageURLs = new String[ pageCount ];
            pageURLs[0] = urlString;
            for ( int i = 1; i < pageCount; i++ )
            {
                beginIndex = allPageString.indexOf( "/page/", beginIndex );
                beginIndex = allPageString.lastIndexOf( "\"", beginIndex ) + 1;
                endIndex = allPageString.indexOf( "\"", beginIndex );
                tempString = allPageString.substring( beginIndex, endIndex );
                beginIndex = endIndex;
                pageURLs[i] = baseURL + tempString;
            }
        }
        else
        {
            // 只有一頁
            pageCount = 1;
            pageURLs = new String[ 1 ];
            pageURLs[0] = urlString;
        }
        
        for ( int i = 0; i < pageURLs.length; i ++ ) 
        {
            Common.debugPrintln( "專輯第" + i + "頁: " + pageURLs[i] );
        }
        
        
                urlList.add( artistPageURL );
                volumeList.add( getVolumeWithFormatNumber( Common.getStringRemovedIllegalChar(
                        Common.getTraditionalChinese( getTitle() + "的熱門歌曲" ) ) ) );

        for ( int j = 0; j < pageCount; j++ )
        {
            int volumeCount = (allPageString.split( "class=\"cover\"" ).length - 1) / 2;
            beginIndex = endIndex = 0;
            for ( int i = 0; i < volumeCount; i++ )
            {
                // 取得專輯位址
                beginIndex = allPageString.indexOf( "class=\"cover\"", beginIndex );
                beginIndex = allPageString.indexOf( " href=", beginIndex );
                beginIndex = allPageString.indexOf( "\"", beginIndex ) + 1;
                endIndex = allPageString.indexOf( "\"", beginIndex );
                volumeURL = baseURL + allPageString.substring( beginIndex, endIndex );
                urlList.add( volumeURL );

                // 取得專輯名稱
                beginIndex = allPageString.indexOf( " title=", beginIndex );
                beginIndex = allPageString.indexOf( "\"", beginIndex ) + 1;
                endIndex = allPageString.indexOf( "\"", beginIndex );
                volumeTitle = allPageString.substring( beginIndex, endIndex );

                // 取得發行日期
                beginIndex = allPageString.indexOf( "class=\"year\"", beginIndex );
                beginIndex = allPageString.indexOf( ">", beginIndex ) + 1;
                endIndex = allPageString.indexOf( "<", beginIndex );
                volumeTitle = allPageString.substring( beginIndex, endIndex ) + " " + volumeTitle;
                volumeList.add( getVolumeWithFormatNumber( Common.getStringRemovedIllegalChar(
                        Common.getTraditionalChinese( volumeTitle ) ) ) );

            }

            if ( j + 1 < pageCount )
            {
                Common.debugPrintln( "接著分析下一頁: " );
                allPageString = getAllPageString( pageURLs[j + 1] );
            }

        }

        totalVolume = urlList.size();
        Common.debugPrintln( "共有" + totalVolume + "集" );

        combinationList.add( volumeList );
        combinationList.add( urlList );

        return combinationList;
    }
}