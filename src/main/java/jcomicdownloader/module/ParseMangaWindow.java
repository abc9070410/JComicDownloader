/*
 ----------------------------------------------------------------------------------------------------
 Program Name : JComicDownloader
 Authors  : surveyorK
 Last Modified : 2012/5/9
 ----------------------------------------------------------------------------------------------------
 ChangeLog:
 * 3.18: 增加對mangawindow的支援。
 ----------------------------------------------------------------------------------------------------
 */
package jcomicdownloader.module;

import java.util.ArrayList;
import java.util.List;
import jcomicdownloader.SetUp;
import jcomicdownloader.enums.Site;
import jcomicdownloader.tools.Common;

public class ParseMangaWindow extends ParseOnlineComicSite {

    private int radixNumber; // use to figure out the name of pic
    private String jsName;
    protected String indexName;
    protected String indexEncodeName;
    protected String baseURL;

    /**

     @author user
     */
    public ParseMangaWindow() {
        siteID = Site.MANGA_WINDOW;
        siteName = "Manga Window";
        indexName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_mangaWindow_parse_", "html" );
        indexEncodeName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_mangaWindow_encode_parse_", "html" );

        baseURL = "http://www.mangawindow.com";
        jsName = "index_mangaWindow.js";
        radixNumber = 18528871; // default value, not always be useful!!
    }

    public ParseMangaWindow( String webSite, String titleName ) {
        this();
        this.webSite = webSite;
        this.title = titleName;
    }

    @Override
    public void setParameters() {
        Common.debugPrintln( "開始解析各參數 :" );

        Common.debugPrintln( "開始解析title和wholeTitle :" );

        if ( getWholeTitle() == null || getWholeTitle().equals( "" ) ) {
            setWholeTitle( "NULL_VOLUME_TITLE" );
        }

        Common.debugPrintln( "作品名稱(title) : " + getTitle() );
        Common.debugPrintln( "章節名稱(wholeTitle) : " + getWholeTitle() );
    }

    @Override
    public void parseComicURL() { // parse URL and save all URLs in comicURL  //
        // 先取得前面的下載伺服器網址

        String allPageString = getAllPageString( webSite );
        Common.debugPrint( "開始解析這一集有幾頁 : " );
        int beginIndex = allPageString.indexOf( "<div class=\"page\">" );
        beginIndex = allPageString.indexOf( "<option value=", beginIndex );
        beginIndex = allPageString.indexOf( "/", beginIndex ) + 1;
        int endIndex = allPageString.indexOf( "</option>", beginIndex );
        totalPage = Integer.parseInt( allPageString.substring( beginIndex, endIndex ).trim() );
        Common.debugPrintln( "共 " + totalPage + " 頁" );
        comicURL = new String[totalPage];

        // ex. http://www.mangafox.com/manga/kingdom_hearts/

        // 找出第一張圖 ex. http://i.mwfile.com/manga/p/4/4/0/1/b/81721/19.jpg
        beginIndex = allPageString.indexOf( "<img src=", beginIndex );
        beginIndex = allPageString.indexOf( "\"", beginIndex ) + 1;
        endIndex = allPageString.indexOf( "\"", beginIndex );
        String firstPicURL = allPageString.substring( beginIndex, endIndex );
        Common.debugPrintln( "第一張圖片位址：" + firstPicURL );

        // 找出基本位址 ex. http://i.mwfile.com/manga/p/4/4/0/1/b/81721/
        endIndex = firstPicURL.lastIndexOf( "/" ) + 1;
        String basePicURL = firstPicURL.substring( 0, endIndex );
        Common.debugPrintln( "基本圖片位址：" + basePicURL );

        // 找出圖片副檔名 ex. jpg
        beginIndex = firstPicURL.lastIndexOf( "." ) + 1;
        String extensionName = firstPicURL.substring( beginIndex, firstPicURL.length() );
        Common.debugPrintln( "圖片副檔名：" + extensionName + beginIndex );
        
        for ( int p = 1; p <= totalPage; p++ ) {

            comicURL[p - 1] = basePicURL + p + "." + extensionName;
            //Common.debugPrintln( p + " " + comicURL[p - 1] ); // debug

        }
        //System.exit(0); // debug
    }

    @Override
    public String getAllPageString( String urlString ) {
        String indexName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_mangaFox_", "html" );

        Common.downloadFile( urlString, SetUp.getTempDirectory(), indexName, false, "" );

        return Common.getFileString( SetUp.getTempDirectory(), indexName );
    }

    @Override
    public boolean isSingleVolumePage( String urlString ) {
        // ex. http://www.mangawindow.com/manga/alien-nine/v4/c1/18
        if ( urlString.split( "/" ).length > 5 ) {
            return true;
        }
        else { // ex. http://www.mangawindow.com/manga/alien-nine
            return false;
        }
    }

    @Override
    public String getTitleOnSingleVolumePage( String urlString ) {
        int endIndex = Common.getIndexOfOrderKeyword( urlString, "/", 5 ) + 1;
        String mainUrlString = urlString.substring( 0, endIndex );

        return getTitleOnMainPage( mainUrlString, getAllPageString( mainUrlString ) );
    }

    @Override
    public String getTitleOnMainPage( String urlString, String allPageString ) {

        int beginIndex = allPageString.indexOf( "<title>" ) + 7;
        int endIndex = allPageString.indexOf( "-", beginIndex );
        String englishTitle = allPageString.substring( beginIndex, endIndex ).trim();

        String title = englishTitle; // + "(" + japanTitle + ")"; // 不加了，因為日文資料夾打不開...

        return Common.getStringRemovedIllegalChar( title );
    }

    @Override
    public List<List<String>> getVolumeTitleAndUrlOnMainPage( String urlString, String allPageString ) {
        // combine volumeList and urlList into combinationList, return it.

        List<List<String>> combinationList = new ArrayList<List<String>>();
        List<String> urlList = new ArrayList<String>();
        List<String> volumeList = new ArrayList<String>();

        int beginIndex = allPageString.indexOf( "class=\"list\"" );
        int endIndex = allPageString.indexOf( "<footer", beginIndex );
        String tempString = allPageString.substring( beginIndex, endIndex );

        int totalVolume = tempString.split( " href=" ).length - 1;
        Common.debugPrintln( "共有" + totalVolume + "集" );

        String volumeTitle = "";
        beginIndex = endIndex = 0;
        for ( int i = 0; i < totalVolume; i++ ) {
            // 取得單集位址
            beginIndex = tempString.indexOf( " href=", beginIndex );
            beginIndex = tempString.indexOf( "\"", beginIndex ) + 1;
            endIndex = tempString.indexOf( "\"", beginIndex );
            urlList.add( baseURL + tempString.substring( beginIndex, endIndex ) );



            // 取得單集名稱
            beginIndex = tempString.indexOf( ">", beginIndex ) + 1;
            endIndex = tempString.indexOf( "</span>", beginIndex );
            volumeTitle = tempString.substring( beginIndex, endIndex );
            volumeTitle = volumeTitle.replaceAll( "</a>", "" );
            volumeTitle = volumeTitle.replaceAll( "\\s*:\\s*", "_" );

            volumeList.add( getVolumeWithFormatNumber(
                Common.getStringRemovedIllegalChar( volumeTitle.trim() ) ) );

        }

        combinationList.add( volumeList );
        combinationList.add( urlList );

        return combinationList;

    }

    @Override
    public String getMainUrlFromSingleVolumeUrl( String volumeURL ) {
        throw new UnsupportedOperationException( "Not supported yet." );
    }
}
