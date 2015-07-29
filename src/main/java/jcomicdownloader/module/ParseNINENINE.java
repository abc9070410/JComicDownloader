/*
 ----------------------------------------------------------------------------------------------------
 Program Name : JComicDownloader
 Authors  : surveyorK, icearea(Latest)
 Last Modified : 2015/5/27
 ----------------------------------------------------------------------------------------------------
 ChangeLog:
 5.19: 修復99mh無法下載的問題。
 5.16: 修復99mh無法下載的問題。
 5.14: 修復99mh無法下載的問題。
 5.13: 修復99manga下載錯誤的問題。
 5.01: 1. 修復dm.99manga.com因改版而解析錯誤的問題。
 4.18: 1. 修復cococomic解析錯誤的問題。
 4.15: 1. 修復cococomic部份位址解析錯誤的問題。
 4.12: 1. 修復mh.99770.cc和www.cococomic.com部份位址解析錯誤的問題。
 4.01: 1. 修復cococomic簡體版頁面的集數解析問題。
 3.19: 1. 修復99manga繁體版因改版而解析錯誤的問題。
 3.17: 1. 修復99770和cococomic繁體版頁面的集數解析問題。
 2. 修復99manga簡體版和繁體版頁面的集數解析問題。
 3. 修復1mh的集數解析問題。
 3.16: 1. 增加對www.99comic.com繁體版的支援。
 3.12: 1. 修復cococomic因網站改版而無法下載的問題。
 2. 修復99770因網站改版而無法下載的問題。
 ----------------------------------------------------------------------------------------------------
 */
package jcomicdownloader.module;

import java.util.ArrayList;
import java.util.List;
import jcomicdownloader.SetUp;
import jcomicdownloader.encode.Zhcode;
import jcomicdownloader.enums.Site;
import jcomicdownloader.tools.Common;

public class ParseNINENINE extends ParseOnlineComicSite
{

    protected String indexName;
    protected String indexEncodeName;
    protected String jsName;
    protected int serverNo; // 下載伺服器的編號
    protected String baseURL; // 首頁網址 ex. http://dm.99manga.com
    protected String jsURL; // 存放下載伺服器網址的.js檔的網址

    /**

     @author user
     */
    public ParseNINENINE()
    {
        siteName = "99";
        indexName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_99_parse_", "html" );
        indexEncodeName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_99_encode_parse_", "html" );
        jsName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_99_parse_", "js" );
    }

    public ParseNINENINE( String webSite, String titleName )
    {
        this();
        this.webSite = webSite;
        this.title = titleName;
    }

    @Override
    public void setParameters()
    { // let all the non-set attributes get values
        Common.downloadFile( webSite, SetUp.getTempDirectory(), indexName, false, "" );
        Common.newEncodeFile( SetUp.getTempDirectory(), indexName, indexEncodeName );
        String allString = Common.getFileString( SetUp.getTempDirectory(), indexEncodeName );

        Common.debugPrintln( "開始解析各參數 :" );


        String[] tempStrings = webSite.split( "=|/" );
        int serverNo = Integer.parseInt( tempStrings[tempStrings.length - 1] );
        setServerNo( serverNo );

        int beginIndex = allString.indexOf( "script src=" );
        beginIndex = allString.indexOf( "=", beginIndex ) + 1;
        int endIndex = allString.indexOf( ">", beginIndex );

        int endIndexOfBaseURL = Common.getIndexOfOrderKeyword( webSite, "/", 3 );
        if(baseURL == null)
            baseURL = webSite.substring( 0, endIndexOfBaseURL );
        if(jsURL == null)
            jsURL = baseURL + allString.substring( beginIndex, endIndex );

        Common.debugPrintln( "基本位址: " + baseURL );
        Common.debugPrintln( "JS檔位址: " + jsURL );


        Common.debugPrintln( "開始解析title和wholeTitle :" );


        Common.debugPrintln( "作品名稱(title) : " + getTitle() );

        if ( getWholeTitle() == null || getWholeTitle().equals( "" ) )
        {
            String[] tokens = allString.split( "\"|," );

            for ( int i = 0; i < tokens.length && Run.isAlive; i++ )
            {
                if ( tokens[i].equals( "keywords" ) )
                {
                    wholeTitle = Common.getStringRemovedIllegalChar( Common.getTraditionalChinese( tokens[i + 2].trim() ) );
                    break;
                }
            }
            setWholeTitle( wholeTitle );
        }

        Common.debugPrintln( "作品+章節名稱(wholeTitle) : " + getWholeTitle() );

        totalPage = getHowManyKeyWordInString( allString, "|" ) + 1;
        comicURL = new String[ totalPage ]; // totalPage = amount of comic pic
        refers = new String[ totalPage ];

        SetUp.setWholeTitle( wholeTitle );
    }

    @Override
    public void parseComicURL()
    { // parse URL and save all URLs in comicURL
        // 先取得前面的下載伺服器網址
        Common.downloadFile( jsURL, SetUp.getTempDirectory(), jsName, false, "" );
        String allJsString = Common.getFileString( SetUp.getTempDirectory(), jsName );

        int index = 0;
        for ( int i = 0; i < serverNo; i++ )
        {
            index = allJsString.indexOf( "ServerList[", index ) + 1;
        }

        int beginIndex = allJsString.indexOf( "\"", index ) + 1;
        int endIndex = allJsString.indexOf( "\"", beginIndex + 1 );

        String baseDownloadURL = allJsString.substring( beginIndex, endIndex );
        Common.debugPrintln( "下載伺服器位址: " + baseDownloadURL );
        beginIndex = allJsString.indexOf( "unsuan(", endIndex );
        beginIndex = allJsString.indexOf( "\"", beginIndex ) + 1;
        endIndex = allJsString.indexOf( "\"", beginIndex + 1 );
        String keyString = allJsString.substring(beginIndex, endIndex);

        // 再取得後面的圖片目錄網址
        String allPageString = getAllPageString( webSite );

        beginIndex = 0;
        endIndex = 0;

        beginIndex = allPageString.indexOf( "var PicListUrl =" );
        if(beginIndex == -1)
            beginIndex = allPageString.indexOf( "var PicListsUrl =" );
        beginIndex = allPageString.indexOf( "\"", beginIndex ) + 1;
        endIndex = allPageString.indexOf( "\"", beginIndex );

        String tempString = allPageString.substring( beginIndex, endIndex );
        //decode
        String [] urlTokens = Common.unsuan99(tempString, keyString);

        // 取得頁數
        comicURL = new String[ urlTokens.length ];
        refers = new String[ urlTokens.length ];

        for ( int i = 0; i < comicURL.length && Run.isAlive; i++ )
        {
            comicURL[i] = baseDownloadURL + urlTokens[i];
            Common.debugPrintln( i + " : " + comicURL[i] );
            refers[i] = webSite;
        }
    }

    @Override // 下載網址指向的網頁，全部存入String後回傳
    public String getAllPageString( String urlString )
    {
        String indexName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_99_", "html" );
        String indexEncodeName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_99_encode_", "html" );

        System.out.println( "URL: " + urlString );
        Common.downloadFile( urlString, SetUp.getTempDirectory(), indexName, false, "" );

        Common.newEncodeFile( SetUp.getTempDirectory(), indexName, indexEncodeName, Zhcode.GBK );
        return Common.getFileString( SetUp.getTempDirectory(), indexEncodeName );

    }

    @Override // 從網址判斷是否為單集頁面(true) 還是主頁面(false)
    public boolean isSingleVolumePage( String urlString )
    {
        if ( urlString.matches( "(?s).*\\.htm\\?s(?s).*" ) ) // ex. dm.99manga.com/manga/4142/61660.htm?s=4
        {
            return true;
        }
        else // ex. dm.99manga.com/comic/4142/
        {
            return false;
        }
    }

    // 設定下載伺服器的編號
    public void setServerNo( int no )
    {
        serverNo = no;
    }
    // 取得下載伺服器的編號

    public int getServerNo()
    {
        return serverNo;
    }
    
    @Override // 從單集頁面取得title(作品名稱)
    public String getTitleOnSingleVolumePage( String urlString )
    {

        Common.debugPrintln( "開始由單集頁面位址取得title：" );
        int tempIndex = urlString.indexOf( "s=" );
        int endIndex = urlString.substring( 0, tempIndex ).lastIndexOf( "/" );
        String mainPageURL = urlString.substring( 0, endIndex );

        Common.debugPrintln( "替換前URL: " + mainPageURL );
        // 目前共三種轉換情形，單集頁面與主頁面的資料夾名稱差異
        mainPageURL = mainPageURL.replaceAll( "manhua", "comic" );
        mainPageURL = mainPageURL.replaceAll( "Manhua", "Comic" );
        mainPageURL = mainPageURL.replaceAll( "manga", "comic" );
        mainPageURL = mainPageURL.replaceAll( "dm.99comic.com", "dm.99manga.com" ); // 再把http://dm.99manga.com轉回來
        mainPageURL = mainPageURL.replaceAll( "3gcomic.com", "3gmanhua.com" ); // 再把http://3gmanhua.com轉回來
        Common.debugPrintln( "替換後URL: " + mainPageURL );

        return getTitleOnMainPage( mainPageURL, getAllPageString( mainPageURL ) );
    }

    @Override // 從主頁面取得title(作品名稱)
    public String getTitleOnMainPage( String urlString, String allPageString )
    {
        Common.debugPrintln( "開始由主頁面位址取得title：" );

        String[] tokens = allPageString.split( "\\s*=\\s+|\"" );
        String title = "";

        int index = 0;
        for ( ; index < tokens.length; index++ )
        {
            if ( tokens[index].matches( "(?s).*wumiiTitle\\s*" ) )
            { // ex. var wumiiTitle = "食夢者";
                title = tokens[index + 2];
                break;
            }
        }

        // 第一種方法找不到的時候 ex.http://1mh.com/comic/8308/
        if ( title.equals( "" ) )
        {
            int beginIndex = allPageString.indexOf( "<title>" ) + 7;
            int endIndex = Common.getSmallerIndexOfTwoKeyword( allPageString, beginIndex, "<", " " );

            title = allPageString.substring( beginIndex, endIndex );
        }

        return Common.getStringRemovedIllegalChar( Common.getTraditionalChinese( title ) );
    }

    // 取得在string中有"幾個"符合keyword的字串
    public static int getHowManyKeyWordInString( String string, String keyword )
    {
        int index = 0;
        int keywordCount = 0;
        while ( (index = string.indexOf( keyword, index )) >= 0 )
        {
            keywordCount++;
            index++;
        }

        return keywordCount;
    }

    @Override // 從主頁面取得所有集數名稱和網址
    public List<List<String>> getVolumeTitleAndUrlOnMainPage( String urlString, String allPageString )
    {
        // combine volumeList and urlList into combinationList, return it.
        // ex. <li><a href=/manga/4142/84144.htm?s=4 target=_blank>bakuman151集</a>
        //     <a href="javascript:ShowA(4142,84144,4);" class=Showa>加速A</a>
        //     <a href="javascript:ShowB(4142,84144,4);" class=Showb>加速B</a></li>

        List<List<String>> combinationList = new ArrayList<List<String>>();
        List<String> urlList = new ArrayList<String>();
        List<String> volumeList = new ArrayList<String>();

        int endIndexOfBaseURL = Common.getIndexOfOrderKeyword( urlString, "/", 3 );
        String baseURL = urlString.substring( 0, endIndexOfBaseURL );

        int totalVolume = getHowManyKeyWordInString( allPageString, "ShowA" );

        if ( totalVolume == 0 )
        {
            Common.debugPrintln( "找不到任何集數！" );
            return null;
        }

        int index = 0;
        for ( int count = 0; count < totalVolume; count++ )
        {
            index = allPageString.indexOf( "href=/", index );

            int urlBeginIndex = allPageString.indexOf( "/", index );
            int urlEndIndex = Common.getSmallerIndexOfTwoKeyword( allPageString, index, " ", ">" );

            urlList.add( baseURL + allPageString.substring( urlBeginIndex, urlEndIndex ) );

            int volumeBeginIndex = allPageString.indexOf( ">", index ) + 1;
            int volumeEndIndex = allPageString.indexOf( "<", volumeBeginIndex );

            String title = allPageString.substring( volumeBeginIndex, volumeEndIndex );

            volumeList.add( getVolumeWithFormatNumber(
                    Common.getStringRemovedIllegalChar(
                    Common.getTraditionalChinese( title ) ) ) );

            index = volumeEndIndex;
        }

        combinationList.add( volumeList );
        combinationList.add( urlList );

        return combinationList;
    }

    @Override
    public void printLogo()
    {
        System.out.println( " __________________________________" );
        System.out.println( "|                              " );
        System.out.println( "| Run the " + siteName + " module: " );
        System.out.println( "|__________________________________\n" );
    }

    @Override
    public String getMainUrlFromSingleVolumeUrl( String volumeURL )
    {
        throw new UnsupportedOperationException( "Not supported yet." );
    }
}