/*
 ----------------------------------------------------------------------------------------------------
 Program Name : JComicDownloader
 Authors  : surveyorK
 Last Modified : 2012/12/30
 ----------------------------------------------------------------------------------------------------
 ChangeLog:
 5.13: 修復wenku8部份章節無法下載的問題。
  5.02: 1. 修復wenku8部分章節下載錯誤的問題。
 4.14: 1. 修復wenku8繁體頁面下載錯誤的問題。
 4.13: 1. 修正wenku8的封面與插圖下載。
 *  4.12: 1. 新增對wenku8的支援。
 ----------------------------------------------------------------------------------------------------
 */
package jcomicdownloader.module;

import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import jcomicdownloader.SetUp;
import jcomicdownloader.encode.Encoding;
import jcomicdownloader.enums.FileFormatEnum;
import jcomicdownloader.enums.Site;
import jcomicdownloader.tools.Common;
import jcomicdownloader.tools.CommonGUI;

public class ParseWenku8 extends ParseEightNovel {

    protected int radixNumber; // use to figure out the name of pic
    protected String jsName;
    protected String indexName;
    protected String indexEncodeName;
    protected String baseURL;
    protected int floorCountInOnePage; // 一頁有幾層樓

    /**
     *
     * @author user
     */
    public ParseWenku8() {
        siteID = Site.WENKU8;
        siteName = "wenku8";
        pageExtension = "htm"; // 網頁副檔名
        pageCode = Encoding.GBK; // 網頁預設編碼

        indexName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_wenku8_parse_", pageExtension );
        indexEncodeName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_wenku8_encode_parse_", pageExtension );

        jsName = "index_wenku8.js";
        radixNumber = 15128451; // default value, not always be useful!!

        baseURL = "http://www.wenku8.cn";
    }

    @Override
    public void parseComicURL() { // parse URL and save all URLs in comicURL  //

        webSite = getRegularURL( webSite ); // 將全集頁面轉為正規的全集頁面位址

        Common.downloadFile( webSite, SetUp.getTempDirectory(), indexName, false, "" );
        Common.newEncodeFile( SetUp.getTempDirectory(), indexName, indexEncodeName, pageCode );
        String allPageString = Common.getFileString( SetUp.getTempDirectory(), indexEncodeName );

        // 先解析要下載哪一集

        Common.debugPrint( "開始解析這一集有幾頁 : " );

        int beginIndex = 0, endIndex = 0;

        allPageString = Common.getTraditionalChinese( allPageString );
        
        String titleString = getWholeTitle();
        if ( titleString.matches( ".*_.*" ) )
        {
            // 避免改變字元後無法找出位置
            titleString = titleString.split( "_" )[1];
        }
        
        if ( getWholeTitle().matches( ".*‧.*" ) ) {
            titleString = titleString.split( "‧" )[1]; 
        }
        
        if ( getWholeTitle().matches( ".*＆.*" ) ) {
            titleString = titleString.split( "＆" )[0]; 
        }

        beginIndex = allPageString.indexOf( titleString );
        endIndex = Common.getSmallerIndexOfTwoKeyword(
                allPageString, beginIndex, "class=\"vcss\"", "</table>" );
        
        Common.debugPrintln( "____" + beginIndex + "__" + endIndex + " " + titleString );
        String tempString = allPageString.substring( beginIndex, endIndex ).trim();

        totalPage = tempString.split( " href=" ).length - 1;
        Common.debugPrintln( "共 " + totalPage + " 頁" );
        comicURL = new String[ totalPage ];
        String[] titles = new String[ totalPage ];


        // 取得作者名稱
        String author = "";
        beginIndex = allPageString.indexOf( "id=\"info\"" );
        beginIndex = allPageString.indexOf( ">", beginIndex ) + 4;
        endIndex = allPageString.indexOf( "</div>", beginIndex );
        author = allPageString.substring( beginIndex, endIndex );
        Common.debugPrintln( "作者名稱：" + author );



        NumberFormat formatter = new DecimalFormat( Common.getZero() );

        // 取得小說網址
        beginIndex = endIndex = 0;
        for ( int i = 0; i < totalPage; i++ ) {
            // 取得網址
            beginIndex = tempString.indexOf( " href=", beginIndex );
            beginIndex = tempString.indexOf( "\"", beginIndex ) + 1;
            endIndex = tempString.indexOf( "\"", beginIndex );
            String pageName = tempString.substring( beginIndex, endIndex );
            if ( pageName.matches( "http://.*" ) ) {
                comicURL[i] = pageName;
            }
            else {
                comicURL[i] = webSite.replace( "index.htm", pageName );
            }

            // 取得標題
            beginIndex = tempString.indexOf( ">", beginIndex ) + 1;
            endIndex = tempString.indexOf( "</a>", beginIndex );
            titles[i] = Common.getStringRemovedIllegalChar(
                    tempString.substring( beginIndex, endIndex ).trim() ) + "." + Common.getDefaultTextExtension();

            Common.debugPrintln( i + " " + titles[i] + " " + comicURL[i] ); // debug
        }
        //System.exit(  0 ); // for debug

        for ( int i = 0; i < totalPage && Run.isAlive; i++ ) {
            // 每解析一個網址就下載一個頁面
            if ( !new File( getDownloadDirectory() + titles[i] ).exists() && Run.isAlive ) {
                singlePageDownload( getTitle(), getWholeTitle(), comicURL[i], pageExtension, totalPage, i + 1, 0 );
                String fileName = formatter.format( i + 1 ) + "." + pageExtension;
                handleSingleNovel( fileName, titles[i] );  // 處理單一小說主函式
            } else {
                Common.debugPrintln( titles[i] + "已下載，跳過" );
            }
        }

        String coverFile = getDownloadDirectory() + "0.jpg";

        // 若第一章圖存在，則拷貝到外面當作封面圖
        if ( new File( coverFile ).exists() ) {
            String copyCoverFile = getParentPath( getDownloadDirectory() )
                    + Common.getTraditionalChinese( author )
                    + "_" + getWholeTitle() + ".jpg";
            Common.copyFile( coverFile, copyCoverFile );
        }

        handleWholeNovel( titles, webSite, author );

        //System.exit( 0 ); // debug
    }

    // 處理小說網頁，將標籤去除
    public String getRegularNovel( String allPageString ) {
        int beginIndex = 0;
        int endIndex = 0;
        String oneFloorText = ""; // 單一樓層（頁）的文字
        // 先取得章節名稱
        beginIndex = allPageString.indexOf( "id=\"title\"" );
        beginIndex = allPageString.indexOf( ">", beginIndex ) + 1;
        endIndex = allPageString.indexOf( "</div>", beginIndex );
        oneFloorText = "    " + allPageString.substring( beginIndex, endIndex ).trim() + " <br><br>";

        // 再取得本文內容
        beginIndex = allPageString.indexOf( "</ul>" );
        endIndex = allPageString.indexOf( "<ul id=\"contentdp\">", beginIndex );
        
        oneFloorText += allPageString.substring( beginIndex, endIndex );
        oneFloorText = oneFloorText.replaceAll( "<br />", "<br>" );

        if ( SetUp.getDefaultTextOutputFormat() == FileFormatEnum.TEXT ) {
            oneFloorText = replaceProcessToText( oneFloorText );
        } else {
            oneFloorText = replaceProcessToHtml( oneFloorText );
        }
        oneFloorText = Common.getTraditionalChinese( oneFloorText ); // 簡轉繁


        downloadIllustrations( oneFloorText );


        return oneFloorText;
    }

    // wendu8每本都有插圖，故下載之
    private void downloadIllustrations( String allPageString ) {
        // 圖片儲存位置
        String picStorePath = getParentPath( getDownloadDirectory() );

        String picBaseURL = "http://pic.wenku8.cn/pictures/";
        int picAmount = allPageString.split( picBaseURL ).length - 1;

        int beginIndex = 0;
        int endIndex = 0;
        String tempURL = "";
        for ( int i = 0; i < picAmount; i++ ) {
            beginIndex = allPageString.indexOf( picBaseURL, endIndex );
            endIndex = Common.getSmallerIndexOfTwoKeyword(
                    allPageString, beginIndex, "\n", "\"" );
            tempURL = allPageString.substring( beginIndex, endIndex );

            Common.debugPrintln( "解析得到的插圖：" + tempURL );
            CommonGUI.stateBarDetailMessage = "共" + picAmount + "頁插圖，第" + ( i + 1 ) + "頁下載中";
            Common.downloadFile( tempURL, getDownloadDirectory(), i + ".jpg", false, "" );
        }
    }

    // ex. http://www.wenku8.cn/modules/article/articleinfo.php?id=186轉為
    //     http://www.wenku8.cn/novel/0/186/index.htm
    public String getRegularURL( String url ) {

        if ( url.matches( "(?s).*/articleinfo.php(?s).*" ) ) {
            Common.downloadFile( url, SetUp.getTempDirectory(), indexName, false, "" );
            Common.newEncodeFile( SetUp.getTempDirectory(), indexName, indexEncodeName, pageCode );
            String allPageString = Common.getFileString( SetUp.getTempDirectory(), indexEncodeName );

            int beginIndex = allPageString.indexOf( "style=\"text-align:center\"" );
            beginIndex = allPageString.indexOf( " href=", beginIndex );
            beginIndex = allPageString.indexOf( "\"", beginIndex ) + 1;
            int endIndex = allPageString.indexOf( "\"", beginIndex );
            url = allPageString.substring( beginIndex, endIndex ).trim();
            Common.debugPrintln( "轉為正規全集頁面位址：" + url );
        }

        return url;
    }

    @Override
    public String getTitleOnMainPage( String urlString, String allPageString ) {
        int beginIndex, endIndex;
        String title = "";

        beginIndex = allPageString.indexOf( "id=\"title\"" );
        beginIndex = allPageString.indexOf( ">", beginIndex ) + 1;
        endIndex = allPageString.indexOf( "</div>", beginIndex );
        title = allPageString.substring( beginIndex, endIndex ).trim();

        return Common.getStringRemovedIllegalChar( Common.getTraditionalChinese( title ) );
    }

    @Override
    public List<List<String>> getVolumeTitleAndUrlOnMainPage( String urlString, String allPageString ) {
        // combine volumeList and urlList into combinationList, return it.

        List<List<String>> combinationList = new ArrayList<List<String>>();
        List<String> urlList = new ArrayList<String>();
        List<String> volumeList = new ArrayList<String>();

        String volumeTitle = "";
        String volumeURL = "";

        urlString = getRegularURL( urlString );


        // 此為合集

        int beginIndex = 0;
        int endIndex = 0;
        String tempString = allPageString;

        int amount = tempString.split( "class=\"vcss\"" ).length - 1;

        beginIndex = endIndex = 0;
        for ( int i = 0; i < amount; i++ ) {

            // 取得單集名稱
            beginIndex = tempString.indexOf( "class=\"vcss\"", beginIndex );
            beginIndex = tempString.indexOf( ">", beginIndex ) + 1;
            endIndex = tempString.indexOf( "</td>", beginIndex );
            volumeTitle = tempString.substring( beginIndex, endIndex ).trim();
            volumeList.add( Common.getStringRemovedIllegalChar(
                    Common.getTraditionalChinese( volumeTitle ) ) );

            // 取得單集位址(每一集的位址都一樣......)
            urlList.add( urlString );

            //Common.debugPrintln( i + " " + volumeTitle + " " + volumeURL); for debug
        }

        combinationList.add( volumeList );
        combinationList.add( urlList );

        return combinationList;
    }
}
