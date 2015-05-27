/*
 ----------------------------------------------------------------------------------------------------
 Program Name : JComicDownloader
 Authors  : surveyorK
 Last Modified : 2012/11/14
 ----------------------------------------------------------------------------------------------------
 ChangeLog:
 5.07: 1. 修復iask解析錯誤的問題。
 3.09: 1. 新增對iask的支援。
 ----------------------------------------------------------------------------------------------------
 */
package jcomicdownloader.module;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import jcomicdownloader.tools.*;
import jcomicdownloader.enums.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import jcomicdownloader.ComicDownGUI;
import jcomicdownloader.SetUp;
import jcomicdownloader.encode.Encoding;

public class ParseIASK extends ParseOnlineComicSite
{

    private int radixNumber; // use to figure out the name of pic
    private String jsName;
    protected String indexName;
    protected String indexEncodeName;
    protected String baseURL;

    /**

     @author user
     */
    public ParseIASK()
    {
        siteID = Site.IASK;
        siteName = "IASK";
        indexName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_iask_parse_", "html" );
        indexEncodeName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_iask_encode_parse_", "html" );

        jsName = "index_iask.js";
        radixNumber = 15061471; // default value, not always be useful!!

        baseURL = "http://iask.sina.com.cn";

    }

    public ParseIASK( String webSite, String titleName )
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

        Common.debugPrintln( "作品名稱(title) : " + getTitle() );
        Common.debugPrintln( "章節名稱(wholeTitle) : " + getWholeTitle() );
    }

    @Override
    public void parseComicURL()
    { // parse URL and save all URLs in comicURL  //
        webSite = getCorrentURL( webSite );

        Common.downloadFile( webSite, SetUp.getTempDirectory(), indexName, false, "" );
        Common.newEncodeFile( SetUp.getTempDirectory(), indexName, indexEncodeName, Encoding.GB2312 );
        String allPageString = Common.getFileString( SetUp.getTempDirectory(), indexEncodeName );
        Common.debugPrint( "開始解析這一集有幾頁 : " );

        int beginIndex = allPageString.indexOf( "<INPUT type =\"text\"" );
        beginIndex = allPageString.indexOf( "value=", beginIndex );
        beginIndex = allPageString.indexOf( "\"", beginIndex ) + 1;
        int endIndex = allPageString.indexOf( "\"", beginIndex );

        String tempString = allPageString.substring( beginIndex, endIndex );

        totalPage = Integer.parseInt( tempString );
        Common.debugPrintln( "共 " + totalPage + " 個文件檔案" );
        comicURL = new String[ totalPage ];

        int pageAmount = totalPage / 20;
        // 若不是剛好20的倍數，那代表會多一頁不滿20個檔案的頁面。 
        if ( (totalPage % 20) != 0 )
        {
            pageAmount++;
        }


        String picURL = "";
        int p = 1; // 目前檔案數
        int fileCountOnSinglePage = 0; // 單一頁面的檔案數
        String fileURL = "";
        String fileName = "";
        int filePrice = 0;

        for ( int i = 0; i < pageAmount && Run.isAlive; i++ )
        {
            allPageString = getAllPageString( webSite + "&page=" + i );

            beginIndex = allPageString.indexOf( "class=\"ml20\"" );
            endIndex = allPageString.indexOf( "</form>", beginIndex );
            tempString = allPageString.substring( beginIndex, endIndex );

            fileCountOnSinglePage = tempString.split( " href=" ).length;

            beginIndex = endIndex = 0;
            for ( int j = 0; j < fileCountOnSinglePage; j++ )
            {
                // 取得檔案網址
                beginIndex = tempString.indexOf( " href=", beginIndex );
                beginIndex = tempString.indexOf( "\"", beginIndex ) + 1;
                endIndex = tempString.indexOf( "\"", beginIndex );
                fileURL = tempString.substring( beginIndex, endIndex );

                // 取得檔案名稱
                beginIndex = tempString.indexOf( " title=", beginIndex );
                beginIndex = tempString.indexOf( "\"", beginIndex ) + 1;
                endIndex = tempString.indexOf( "\"", beginIndex );
                fileName = Common.getTraditionalChinese(
                        tempString.substring( beginIndex, endIndex ) );

                Common.debugPrintln( fileName + " : " + fileURL );

                // 取得下載所需積分
                beginIndex = tempString.indexOf( "img src=", beginIndex );
                beginIndex = tempString.indexOf( ">", beginIndex ) + 1;
                endIndex = tempString.indexOf( "分", beginIndex );
                filePrice = Integer.parseInt(
                        tempString.substring( beginIndex, endIndex ).trim() );

                if ( filePrice <= 0 && Run.isAlive && !new File( getDownloadDirectory() + fileName ).exists() )
                {
                    downloadIASK( fileName, fileURL, p ); // 下載主函式 
                }
                p++;
            }

        }

        //System.exit( 0 ); // debug
    }

    // 下載主函式  
    public void downloadIASK( String fileName, String fileURL, int p )
    {
        String fileID = "";

        int beginIndex = fileURL.lastIndexOf( "/" ) + 1;
        int endIndex = fileURL.indexOf( ".", beginIndex );
        fileID = fileURL.substring( beginIndex, endIndex );

        String queryString = getQueryString( fileID, fileURL );
        String cookieString = Common.getCookieString( fileURL );

        String realURL = "http://ishare.games.sina.com.cn/download.php?fileid=" + fileID;

        Common.debugPrintln( "嘗試下載「" + getDownloadDirectory() + fileName + "」" );
        do
        {
            CommonGUI.stateBarMainMessage = wholeTitle + " : ";
            CommonGUI.stateBarDetailMessage = "共" + totalPage + "個，第" + p
                    + "個下載中 : " + fileName;

            Common.downloadPost( realURL, getDownloadDirectory(), fileName, true,
                                 cookieString, queryString, fileURL );

            try
            {
                Thread.sleep( 1000 );
            }
            catch ( InterruptedException ex )
            {
                Logger.getLogger( ParseIASK.class.getName() ).log( Level.SEVERE, null, ex );
            }

        }
        while ( new File( getDownloadDirectory() + fileName ).length() < 1000 );

    }

    public String getQueryString( String fileID, String fileURL )
    {
        String allPageString = getAllPageString( fileURL );

        int hiddenQueryAmount = allPageString.split( "input type=\"hidden\"" ).length - 1;
        int beginIndex = 0, endIndex = 0;
        String queryString = "fileid=16938679&";
        for ( int i = 0; i < hiddenQueryAmount; i++ )
        {
            beginIndex = allPageString.indexOf( "input type=\"hidden\"", beginIndex );

            if ( beginIndex > 0 )
            {
                beginIndex = allPageString.indexOf( "name=", beginIndex );
                beginIndex = allPageString.indexOf( "\"", beginIndex ) + 1;
                endIndex = allPageString.indexOf( "\"", beginIndex );
                String name = allPageString.substring( beginIndex, endIndex );

                if ( !name.equals( "format" ) )
                {
                    queryString += name + "=";
                    beginIndex = allPageString.indexOf( "value=", beginIndex );
                    beginIndex = allPageString.indexOf( "\"", beginIndex ) + 1;
                    endIndex = allPageString.indexOf( "\"", beginIndex );
                    String value = allPageString.substring( beginIndex, endIndex );
                    try
                    {
                        queryString += URLEncoder.encode( value, "UTF-8" ) + "&";
                    }
                    catch ( UnsupportedEncodingException ex )
                    {
                        Logger.getLogger( ComicDownGUI.class.getName() ).log( Level.SEVERE, null, ex );
                    }
                }
            }
        }

        queryString += "fileid=" + fileID;

        return queryString;
    }

    public void showParameters()
    { // for debug
        Common.debugPrintln( "----------" );
        Common.debugPrintln( "totalPage = " + totalPage );
        Common.debugPrintln( "webSite = " + webSite );
        Common.debugPrintln( "----------" );
    }

    @Override
    public String getAllPageString( String urlString )
    {
        urlString = getCorrentURL( urlString );

        String indexName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_iask_", "html" );
        String indexEncodeName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_iask_encode_", "html" );

        Common.downloadFile( urlString, SetUp.getTempDirectory(), indexName, false, "" );
        Common.newEncodeFile( SetUp.getTempDirectory(), indexName, indexEncodeName, Encoding.GB2312 );

        return Common.getFileString( SetUp.getTempDirectory(), indexEncodeName );
    }

    // 因為用戶分享主頁若沒有加?folderid=0會下載錯誤
    // ex. http://iask.sina.com.cn/u/1321395000/ish
    public String getCorrentURL( String urlString )
    {
        if ( urlString.matches( ".*?/ish/.*" ) )
        {
            urlString = urlString.substring( 0, urlString.length() - 1 );
            //urlString += "?folderid=0";
            Common.debugPrintln( "改過的網址：" + urlString );
        }

        return urlString;
    }

    @Override
    public boolean isSingleVolumePage( String urlString )
    {
        // ex. http://ishare.iask.sina.com.cn/f/16938924.html
        return false;
    }

    public String getMainUrlFromSingleVolumeUrl( String volumeURL )
    {
        // ex. http://ishare.iask.sina.com.cn/f/16938924.html轉為

        return volumeURL; // google搜尋永遠都是全集頁面
    }

    @Override
    public String getTitleOnSingleVolumePage( String urlString )
    {
        return "iASK愛問共享資料";
    }

    @Override
    public String getTitleOnMainPage( String urlString, String allPageString )
    {
        return "iASK愛問共享資料";
    }

    @Override
    public List<List<String>> getVolumeTitleAndUrlOnMainPage( String urlString, String allPageString )
    {
        // combine volumeList and urlList into combinationList, return it.

        List<List<String>> combinationList = new ArrayList<List<String>>();
        List<String> urlList = new ArrayList<String>();
        List<String> volumeList = new ArrayList<String>();

        //urlList.add( urlString );
        //volumeList.add( "父目錄" );

        // 以遞迴方式找到此位址下所有目錄的位址和名稱
        findAllFolder( "", urlString, urlList, volumeList );



        totalVolume = 1;
        Common.debugPrintln( "共有" + totalVolume + "集" );

        combinationList.add( volumeList );
        combinationList.add( urlList );

        return combinationList;
    }

    private void findAllFolder( String parentFolder, String urlString,
                                List<String> urlList, List<String> volumeList )
    {

        String allPageString = getAllPageString( urlString );

        int beginIndex, endIndex;

        beginIndex = allPageString.indexOf( "class=\"v1_mydoc_tit" );
        beginIndex = allPageString.indexOf( ">", beginIndex ) + 1;
        endIndex = allPageString.indexOf( "<", beginIndex );
        //System.out.println( "OUT:" + beginIndex + "_" + endIndex );
        String title = allPageString.substring( beginIndex, endIndex );

        //if ( !"".equals( parentFolder ) )
        //    parentFolder += "_";

        String userID = "";
        if ( urlString.indexOf( "folderid=" ) < 0 )
        { // 首頁
            endIndex = urlString.lastIndexOf( "/" );
            endIndex = urlString.lastIndexOf( "/", endIndex - 1 );
            beginIndex = urlString.lastIndexOf( "/", endIndex - 1 ) + 1;
            userID = urlString.substring( beginIndex, endIndex );

            parentFolder = userID;
        }
        else
        { // 資料夾頁面
            parentFolder += "_";
        }

        String volumeTitle = parentFolder + title;
        
        Common.debugPrintln( " ----------> TITLE: " + volumeTitle );
        //System.exit( 0 );


        volumeList.add( Common.getStringRemovedIllegalChar(
                Common.getTraditionalChinese( volumeTitle.trim() ) ) );

        // 原本：http://iask.sina.com.cn/u/1321395000/ish?folderid=402119&page=0
        // 轉為：http://iask.sina.com.cn/u/1321395000/ish?folderid=402119
        if ( urlString.split( "&page=" ).length > 1 )
        {
            urlString = urlString.split( "&page=" )[0]; // 回歸最單純的網址
        }
        urlList.add( urlString );

        int folderAmount = allPageString.split( "class=\"colfloat\"" ).length - 1;

        String tempURL = "";
        beginIndex = 0;
        for ( int i = 0; i < folderAmount; i++ )
        {
            beginIndex = allPageString.indexOf( "class=\"colfloat\"", beginIndex );
            beginIndex = allPageString.indexOf( "href=\"", beginIndex ) + 6;
            endIndex = allPageString.indexOf( "\"", beginIndex );
            //System.out.println( "OUT:" + beginIndex + "_" + endIndex );

            tempURL = baseURL + allPageString.substring( beginIndex, endIndex );
            findAllFolder( volumeTitle, tempURL, urlList, volumeList );

        }
    }

    @Override
    public void outputVolumeAndUrlList( List<String> volumeList, List<String> urlList )
    {
        Common.outputFile( volumeList, SetUp.getTempDirectory(), Common.tempVolumeFileName );
        Common.outputFile( urlList, SetUp.getTempDirectory(), Common.tempUrlFileName );
    }

    @Override
    public String[] getTempFileNames()
    {
        return new String[]
                {
                    indexName, indexEncodeName, jsName
                };
    }
}
