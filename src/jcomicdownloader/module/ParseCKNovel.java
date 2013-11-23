/*
----------------------------------------------------------------------------------------------------
Program Name : JComicDownloader
Authors  : surveyorK
Last Modified : 2013/1/11
----------------------------------------------------------------------------------------------------
ChangeLog:
 5.13: 修復ck101小說下載錯誤的問題。
5/12: 修復ck101動態加載部分未收錄的問題。
5.02: 1. 修復ck101小說部分合併卡住的問題。
4.16: 1. 修復ck101小說無法批次下載的問題。
4.10: 1. 修復ck101動態頁面只下載第一頁的問題。
4.06: 1. 修復ck101動態頁面解析錯誤的問題。
 *  3.19: 1. 新增對ck101的支援。
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

public class ParseCKNovel extends ParseOnlineComicSite {

    private int radixNumber; // use to figure out the name of pic
    private String jsName;
    protected String indexName;
    protected String indexEncodeName;
    protected String baseURL;
    protected int floorCountInOnePage; // 一頁有幾層樓

    /**
     *
     * @author user
     */
    public ParseCKNovel() {
        siteID = Site.CK_NOVEL;
        siteName = "CK101_Novel";
        indexName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_ck_novel_parse_", "html" );
        indexEncodeName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_ck_novel_encode_parse_", "html" );

        jsName = "index_ck_novel.js";
        radixNumber = 151261; // default value, not always be useful!!

        baseURL = "http://ck101.com";

        floorCountInOnePage = 10; // 一頁有幾層樓
    }

    public ParseCKNovel( String webSite, String titleName ) {
        this();
        this.webSite = webSite;
        this.title = titleName;
    }

    @Override
    public void setParameters() {
        Common.debugPrintln( "開始解析各參數 :" );


        Common.debugPrintln( "作品名稱(title) : " + getTitle() );
        Common.debugPrintln( "章節名稱(wholeTitle) : " + getWholeTitle() );


    }

    @Override
    public void parseComicURL() { // parse URL and save all URLs in comicURL  //
        // 先取得前面的下載伺服器網址
        webSite = getRegularURL( webSite );

        Common.downloadFile( webSite, SetUp.getTempDirectory(), indexName, false, "" );
        String allPageString = Common.getFileString( SetUp.getTempDirectory(), indexName );
        Common.debugPrint( "開始解析這一集有幾頁 : " );

        // 將
        //setDownloadDirectory( SetUp.getOriginalDownloadDirectory() + getTitle() + Common.getSlash() );
        System.out.println( getDownloadDirectory() );

        int beginIndex = 0, endIndex = 0;

        beginIndex = allPageString.indexOf( "class=\"last\"" );

        if ( beginIndex > 0 ) { // 代表超過一面( > 10 )
            beginIndex = allPageString.indexOf( " ", beginIndex ) + 1;
            endIndex = allPageString.indexOf( "<", beginIndex );
            String tempString = allPageString.substring( beginIndex, endIndex ).trim();
            totalPage = Integer.parseInt( tempString );
        }
        else {
            beginIndex = allPageString.indexOf( "class=\"pgt\"" );
            endIndex = allPageString.indexOf( "class=\"nxt\"", beginIndex );

            if ( endIndex > 0 ) { // 超過一頁
                String tempString = allPageString.substring( beginIndex, endIndex );
                totalPage = tempString.split( "a href=" ).length - 1;
            }
            else {
                totalPage = 1; // 只有一頁
            }
        }
        Common.debugPrintln( "共 " + totalPage + " 頁" );
        comicURL = new String[totalPage];

        // 取得作者名稱
        String author = "";
        beginIndex = getWholeTitle().indexOf( "作者" );
        if ( beginIndex > 0 ) { // 有標示作者
            endIndex = Common.getSmallerIndexOfTwoKeyword(
                    getWholeTitle(), beginIndex, "(", "（" );
            if ( endIndex < 0 ) {
                endIndex = getWholeTitle().length();
            }
            author = getWholeTitle().substring( beginIndex + 3, endIndex );
        }
        Common.debugPrintln( "作者：" + author );

        String pageURL = webSite;
        int p = 1; // 目前頁數

        if ( !pageURL.matches( "(?s).*/thread-(?s).*" ) ) {
            pageURL += "&page=0"; // 動態頁面第一頁原本沒有page，給予後方便之後作替換動作
        }

        for ( int i = 0 ; i < totalPage && Run.isAlive ; i++ ) {
            if ( pageURL.matches( "(?s).*/thread-(?s).*" ) ) { // 靜態頁面
                pageURL = pageURL.replaceAll( "-" + i + "-", "-" + p + "-" );
                
            }
            else { // 動態產生的頁面
                pageURL = pageURL.replace( "&page=" + (i), "&page=" + (i + 1) );
            }

            comicURL[i] = pageURL;
            Common.debugPrintln( i + " " + comicURL[i] ); // debug
            
            // 每解析一個網址就下載一張圖
            // 因應ck的第一頁會有動態讀取，所以需要將動態讀取的部分插入到第二頁，之後往後挪
            if ( i == 0 ) {
                //singlePageDownload( getTitle(), getWholeTitle(), comicURL[i], totalPage + 1, p, 0 );
                
                if ( comicURL[i].indexOf( "thread-" ) > 0 )
                {
                    beginIndex = comicURL[i].indexOf( "thread-" );
                    beginIndex = comicURL[i].indexOf( "-", beginIndex ) + 1;
                    endIndex = comicURL[i].indexOf( "-", beginIndex );
                }
                else
                {
                    beginIndex = comicURL[i].lastIndexOf( "tid=" ) + 4;
                    endIndex = comicURL[i].length();
                }
                String tempString = comicURL[i].substring( beginIndex, endIndex );
                String extraURL = "http://ck101.com/forum.php?mod=threadlazydata&tid=" + tempString;
                
                Common.debugPrintln( "第一頁: " + extraURL );
                singlePageDownload( getTitle(), getWholeTitle(), extraURL, totalPage + 1, p, 0 );
            }
            else 
            {
                singlePageDownload( getTitle(), getWholeTitle(), comicURL[i], totalPage + 1, p, 0 );
            }
            
            
            p++;
            //Common.downloadFile( comicURL[p - 1], "", p + ".jpg", false, "" );

        }

        // 檢查有無封面圖
        String coverURL = null;
        beginIndex = allPageString.indexOf( "/attachments/" );
        if ( beginIndex > 0 ) {
            endIndex = allPageString.indexOf( "\"", beginIndex );
            beginIndex = allPageString.lastIndexOf( "\"", endIndex - 1 ) + 1;
            String tempString = allPageString.substring( beginIndex, endIndex ).trim();
            
            if ( tempString.matches( "http://(?s).*") ) {
                coverURL = tempString;
            }
        }
        
        if ( coverURL != null ) {
            Common.debugPrintln( "有封面圖：" + coverURL );

            hadleWholeNovel( webSite, author, coverURL );  // 處理小說主函式
        }
        else {
            hadleWholeNovel( webSite, author, null );  // 處理小說主函式
        }
        //System.exit( 0 ); // debug
    }

    // 處理小說主函式
    public void hadleWholeNovel( String url, String author, String coverURL ) {
        String allPageString = "";
        String allNovelText = getInformation( title, url ); // 全部頁面加起來小說文字

        String[] fileList = new File( getDownloadDirectory() ).list(); // 取得下載資料夾內所有網頁名稱清單
        Arrays.sort( fileList ); // 對檔案清單作排序

        int hundredCount = 0;
        // int lastPage = fileList.length - 1;
        Common.debugPrintln( "共有" + (fileList.length) + "頁" );
        for ( int i = 0 ; i < fileList.length && Run.isAlive ; i++ ) {
            Common.debugPrintln( "處理第" + (i + 1) + "頁: " + fileList[i] );
            allPageString = Common.getFileString( getDownloadDirectory(), fileList[i] );

            allNovelText += getRegularNovel( allPageString, i ); // 每一頁處理過都加總起來 
            //Common.debugPrintln( "處理第" + (i + 1) + "頁: " + fileList[i] );
            
            ComicDownGUI.stateBar.setText( getTitle()
                    + "合併中: " + (i + 1) + " / " + fileList.length );
      
        }

        // 取得上一層的目錄
        String textOutputDirectory = getParentPath( getDownloadDirectory() );

        //Common.debugPrintln( "OLD: " + getDownloadDirectory() );
        //Common.debugPrintln( "NEW: " + textOutputDirectory );

        if ( SetUp.getDeleteOriginalPic() ) { // 若有勾選原檔就刪除原始未合併文件
            Common.deleteFolder( getDownloadDirectory() ); // 刪除存放原始網頁檔的資料夾
        }
        Common.outputFile( allNovelText, textOutputDirectory, getWholeTitle() + "." + Common.getDefaultTextExtension() );

        textFilePath = textOutputDirectory + getWholeTitle() + "." + Common.getDefaultTextExtension();



        if ( SetUp.getDownloadNovelCover() ) {
            if ( coverURL != null ) { // 下載樓主提供的封面
                String coverFile = author + "_" + getWholeTitle() + ".jpg";
                Common.downloadFile( coverURL, textOutputDirectory, coverFile, false, "" );
            }
            else {
                downloadCover( getWholeTitle(), author ); // 下載Google搜尋到的封面
            }
        }
    }

    // 處理小說網頁，將標籤去除
    public String getRegularNovel( String allPageString, int nowPage ) {
        int beginIndex = 0;
        int endIndex = 0;
        int amountOfFloor = 10; // 一頁有幾樓
        String oneFloorText = ""; // 單一樓層的文字
        String allFloorText = ""; // 所有樓層的文字加總

        Common.debugPrintln( "共有" + amountOfFloor + "樓高" );
        for ( int i = 0 ; i < amountOfFloor ; i++ ) {
            beginIndex = endIndex;
            beginIndex = allPageString.indexOf( "class=\"t_fsz\"", beginIndex );
            if ( beginIndex > 0 ) {
                beginIndex = allPageString.indexOf( "<table", beginIndex );
                endIndex = allPageString.indexOf( "</table>", beginIndex );
                oneFloorText = allPageString.substring( beginIndex, endIndex );
                oneFloorText = Common.getTraditionalChinese( oneFloorText ); // 簡轉繁

                if ( SetUp.getDefaultTextOutputFormat() == FileFormatEnum.HTML_WITHOUT_PIC ||
                     SetUp.getDefaultTextOutputFormat() == FileFormatEnum.HTML_WITH_PIC ) {
                    oneFloorText = replaceProcessToHtml( oneFloorText );
                    allFloorText += oneFloorText
                            + "<br><br>" + (i + nowPage * floorCountInOnePage) + "<br><hr><br>"; // 每一樓的文字加總起來
                }
                else {
                    oneFloorText = replaceProcessToText( oneFloorText );
                    allFloorText += oneFloorText
                            + "\n\n--------------------------------------------"
                            + (i + nowPage * floorCountInOnePage) + "\n"; // 每一樓的文字加總起來
                }
                //Common.debugPrintln( "\n\n第" + i +  "樓\n\n" );
                //Common.debugPrintln( oneFloorText );



                Common.debugPrint( i + " " );

            }
        }

        return allFloorText;
    }

    public void showParameters() { // for debug
        Common.debugPrintln( "----------" );
        Common.debugPrintln( "totalPage = " + totalPage );
        Common.debugPrintln( "webSite = " + webSite );
        Common.debugPrintln( "----------" );
    }

    @Override // 因為原檔就是utf8了，所以無須轉碼
    public String getAllPageString( String urlString ) {
        urlString = getRegularURL( urlString ); // 轉為普通位址

        String indexName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_ck_", "html" );
        Common.downloadFile( urlString, SetUp.getTempDirectory(), indexName, false, "" );

        return Common.getFileString( SetUp.getTempDirectory(), indexName );
    }

    @Override
    public boolean isSingleVolumePage( String urlString ) {
        // ex. http://ck101.com/thread-2081113-1-1.html
        // 都判斷為主頁，並直接下載。

        return false;
    }

    public String getMainUrlFromSingleVolumeUrl( String volumeURL ) {
        return volumeURL;
    }

    @Override
    public String getTitleOnSingleVolumePage( String urlString ) {
        String mainUrlString = getMainUrlFromSingleVolumeUrl( urlString );

        return getTitleOnMainPage( mainUrlString, getAllPageString( mainUrlString ) );
    }

    // 庫頁存檔頁面位址轉為普通頁面位址
    public String getRegularURL( String url ) {
        if ( url.matches( "(?s).*/archiver/(?s).*" ) ) {
            url = url.replace( "archiver/tid", "thread" );
            url = url.replace( ".html", "-1-1.html" );

            return url;
        }
        else {
            return url;
        }
    }

    @Override
    public String getTitleOnMainPage( String urlString, String allPageString ) {
        int beginIndex, endIndex;

        urlString = getRegularURL( urlString ); // 轉為普通位址

        if ( urlString.matches( "(?s).*thread-(?s).*" ) ) { // 網址為文章頁面
            beginIndex = allPageString.indexOf( "name=\"keywords\"" );
            beginIndex = allPageString.indexOf( "content=", beginIndex );
            beginIndex = allPageString.indexOf( "\"", beginIndex ) + 1;
            endIndex = allPageString.indexOf( "\"", beginIndex );
        }
        else { // 網址為名單頁面 ex. http://ck101.com/forum-1288-1.html
            beginIndex = allPageString.indexOf( "<title>" );
            beginIndex = allPageString.indexOf( ">", beginIndex ) + 1;
            endIndex = allPageString.indexOf( "</title>", beginIndex );
            endIndex = allPageString.lastIndexOf( "-", endIndex );
        }

        String title = allPageString.substring( beginIndex, endIndex ).trim();
        title = getRegularFileName( title );

        return Common.getStringRemovedIllegalChar(
                Common.getTraditionalChinese( title ) );
    }

    public String getRegularFileName( String title ) {
        //title = title.replaceAll( " ", "" );
        title = title.replaceAll( "\\(", "（" );
        title = title.replaceAll( "\\)", "）" );
        title = title.replaceAll( ":", "：" );
        return title;
    }

    @Override
    public List<List<String>> getVolumeTitleAndUrlOnMainPage( String urlString, String allPageString ) {
        // combine volumeList and urlList into combinationList, return it.

        urlString = getRegularURL( urlString ); // 轉為普通位址

        List<List<String>> combinationList = new ArrayList<List<String>>();
        List<String> urlList = new ArrayList<String>();
        List<String> volumeList = new ArrayList<String>();

        if ( urlString.matches( "(?s).*thread-(?s).*" )
                || urlString.matches( "(?s).*&tid=(?s).*" ) ) { // 網址為文章頁面
            // 取得單集名稱
            String volumeTitle = getTitle();
            volumeList.add( Common.getStringRemovedIllegalChar( volumeTitle.trim() ) );

            // 取得單集位址
            urlList.add( urlString );

            totalVolume = 1;
        }
        else {
            int beginIndex = 0, endIndex = 0;

            beginIndex = allPageString.indexOf( "class=\"threadrow\"" );
            endIndex = allPageString.lastIndexOf( "class=\"num\"" );
            String tempString = allPageString.substring( beginIndex, endIndex );

            totalVolume = tempString.split( "class=\"threadrow\"" ).length - 1;
            beginIndex = endIndex = 0;
            String pageName = ""; // 頁面名稱
            String volumeTitle = "";
            for ( int i = 0 ; i < totalVolume ; i++ ) {
                // 取得單集位址
                beginIndex = tempString.indexOf( "class=\"threadrow\"", beginIndex );
                beginIndex = tempString.indexOf( " href=", beginIndex );
                beginIndex = tempString.indexOf( "\"", beginIndex ) + 1;
                endIndex = tempString.indexOf( "\"", beginIndex );
                pageName = tempString.substring( beginIndex, endIndex );
                urlList.add( baseURL + "/" + pageName.replaceAll( "amp;", "" ) );

                // 取得單集名稱
                beginIndex = tempString.indexOf( pageName, beginIndex + 1 );
                beginIndex = tempString.indexOf( ">", beginIndex ) + 1;
                endIndex = tempString.indexOf( "</a>", beginIndex );
                volumeTitle = tempString.substring( beginIndex, endIndex );
                volumeTitle = getRegularFileName( volumeTitle );
                volumeList.add( Common.getStringRemovedIllegalChar(
                        Common.getTraditionalChinese( volumeTitle.trim() ) ) );
            }


        }
        Common.debugPrintln( "共有" + totalVolume + "集" );

        combinationList.add( volumeList );
        combinationList.add( urlList );

        return combinationList;
    }

    @Override
    public void outputVolumeAndUrlList( List<String> volumeList, List<String> urlList ) {
        Common.outputFile( volumeList, SetUp.getTempDirectory(), Common.tempVolumeFileName );
        Common.outputFile( urlList, SetUp.getTempDirectory(), Common.tempUrlFileName );
    }

    @Override
    public String[] getTempFileNames() {
        return new String[] { indexName, indexEncodeName, jsName };
    }
}
