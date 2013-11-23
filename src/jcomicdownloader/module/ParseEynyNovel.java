/*
----------------------------------------------------------------------------------------------------
Program Name : JComicDownloader
Authors  : surveyorK
Last Modified : 2012/5/31
----------------------------------------------------------------------------------------------------
ChangeLog:
 *  4.06: 1. 修復分級頁面無法下載的問題。
 *  4.05: 1. 新增對eyny的支援。
----------------------------------------------------------------------------------------------------
 */
package jcomicdownloader.module;

import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import jcomicdownloader.tools.*;
import jcomicdownloader.enums.*;
import java.util.*;
import jcomicdownloader.ComicDownGUI;
import jcomicdownloader.SetUp;
import jcomicdownloader.encode.Encoding;

public class ParseEynyNovel extends ParseCKNovel {

    private int radixNumber; // use to figure out the name of pic
    private String jsName;
    protected String indexName;
    protected String indexEncodeName;
    protected String baseURL;
    protected int floorCountInOnePage; // 一頁有幾層樓
    protected String cookie;

    /**
    
    @author user
     */
    public ParseEynyNovel() {
        siteID = Site.EYNY_NOVEL;
        siteName = "EynyNovel";
        indexName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_eyny_novel_parse_", "html" );
        indexEncodeName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_eyny_novel_encode_parse_", "html" );

        jsName = "index_eyny_novel.js";
        radixNumber = 156661; // default value, not always be useful!!

        baseURL = "http://www03.eyny.com";

        floorCountInOnePage = 10; // 一頁有幾層樓
        
        cookie = "djAX_e8d7_agree=6; "; // 因應分級頁面
    }

    @Override
    public void parseComicURL() { // parse URL and save all URLs in comicURL  //
        // 先取得前面的下載伺服器網址

        Common.debugPrintln( "無法得知有幾頁，需一步步檢查" );
        String allPageString = "";
        String tempString = "";
        String pageURL = changeToArchiverPage( webSite ); // 將原本頁面轉為庫存頁面;
        int beginIndex = 0;
        int endIndex = 0;
        int p = 1; // 目前頁數
        totalPage = p;
        
        
        // 取得作者名稱
        String author = "";
        endIndex = getWholeTitle().indexOf( "-" );
        if ( endIndex > 0 ) { // 有標示作者
           author = getWholeTitle().substring( 0, endIndex );
        }
        Common.debugPrintln( "作者：" + author );
        

        NumberFormat formatter = new DecimalFormat( Common.getZero() );
        while ( Run.isAlive ) {
            if ( !new File( getDownloadDirectory() + formatter.format( p + 1 ) + ".html" ).exists() ) {
                singlePageDownload( getTitle(), getWholeTitle(), pageURL, null, totalPage, p, 0, true, cookie, "", false ); // 每解析一個網址就下載一張圖
            }
            else {
                Common.debugPrintln( "第" + p + "頁已存在，跳過" );
            }
            allPageString = Common.getFileString( getDownloadDirectory(), formatter.format( p ) + ".html" );
            p++;

            // 先取得頁面位址的內容
            beginIndex = endIndex = 0;
            beginIndex = allPageString.indexOf( "class=\"page\"", beginIndex );
            beginIndex = allPageString.indexOf( "</strong>", beginIndex );
            endIndex = allPageString.indexOf( "</div>", beginIndex );
            tempString = allPageString.substring( beginIndex, endIndex );

            beginIndex = tempString.indexOf( " href=" );
            if ( beginIndex < 0 ) { // 代表已經到了最後一頁
                Common.debugPrintln( "已經到尾頁了" );
                break;
            }
            else { // 取得下一頁的位址
                beginIndex = tempString.indexOf( "\"", beginIndex ) + 1;
                endIndex = tempString.indexOf( "\"", beginIndex );
                pageURL = baseURL + "/archiver/" + tempString.substring( beginIndex, endIndex );
                Common.debugPrintln( "繼續下一頁：" + p + "  " + pageURL );

                endIndex = tempString.lastIndexOf( "</a>" );
                beginIndex = tempString.lastIndexOf( ">", endIndex ) + 1;
                totalPage = Integer.parseInt( tempString.substring( beginIndex, endIndex ) );
            }
        }

        if ( Run.isAlive ) {
            hadleWholeNovel( webSite, author );  // 處理小說主函式
        }
    }

    // 處理小說主函式
    public void hadleWholeNovel( String url, String author ) {
        String allPageString = "";
        String allNovelText = getInformation( title, url ); // 全部頁面加起來小說文字

        String[] fileList = new File( getDownloadDirectory() ).list(); // 取得下載資料夾內所有網頁名稱清單
        Arrays.sort( fileList ); // 對檔案清單作排序

        // int lastPage = fileList.length - 1;
        Common.debugPrintln( "共有" + (fileList.length) + "頁" );
        for ( int i = 0 ; i < fileList.length ; i++ ) {
            Common.debugPrintln( "處理第" + (i + 1) + "頁: " + fileList[i] );
            allPageString = Common.getFileString( getDownloadDirectory(), fileList[i] );

            allNovelText += getRegularNovel( allPageString, i ); // 每一頁處理過都加總起來 

            ComicDownGUI.stateBar.setText( getTitle()
                    + "合併中: " + (i + 1) + " / " + fileList.length );
        }

        String tempString = getDownloadDirectory();
        tempString = tempString.substring( 0, tempString.length() - 1 );
        int endIndex = tempString.lastIndexOf( Common.getSlash() ) + 1;

        String textOutputDirectory = tempString.substring( 0, endIndex ); // 放在外面

        //Common.debugPrintln( "OLD: " + getDownloadDirectory() );
        //Common.debugPrintln( "NEW: " + textOutputDirectory );

        //if ( SetUp.getDeleteOriginalPic() ) { // 若有勾選原檔就刪除原始未合併文件
            Common.deleteFolder( getDownloadDirectory() ); // 刪除存放原始網頁檔的資料夾
        //}
        Common.outputFile( allNovelText, textOutputDirectory, getWholeTitle() + "." + Common.getDefaultTextExtension() );

        textFilePath = textOutputDirectory + getWholeTitle() + "." + Common.getDefaultTextExtension();
    
        if ( SetUp.getDownloadNovelCover() ) {
            downloadCover( getWholeTitle(), author ); // 下載封面
        }
    }

    // 處理小說網頁，將標籤去除
    public String getRegularNovel( String allPageString, int nowPage ) {
        int beginIndex = 0;
        int endIndex = 0;
        int amountOfFloor = 10; // 一頁有幾樓
        String oneFloorText = ""; // 單一樓層的文字
        String allFloorText = ""; // 所有樓層的文字加總

        endIndex = allPageString.indexOf( "<div id=\"footer\">" );
        allPageString = allPageString.substring( 0, endIndex ); //拿掉最後面的eyny字樣
        allPageString = replaceLockString( allPageString ); // 拿掉要求註冊的字樣
        allPageString = allPageString.replaceAll( "本帖最後由[^\n]+\n", "" ); // 拿掉最後編輯的字樣

        String[] floorTexts = allPageString.split( "<p class=\"author\">" );

        for ( int i = 1 ; i < floorTexts.length ; i++ ) {
            oneFloorText = floorTexts[i];
            beginIndex = oneFloorText.indexOf( "</p>" );
            oneFloorText = oneFloorText.substring( beginIndex, oneFloorText.length() ); // 拿掉發表人和發表日期
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
        return allFloorText;
    }

    // 將要求註冊的字樣拿掉
    public String replaceLockString( String text ) {
        //Common.debugPrintln( "拿掉要求註冊的字樣" );
        int beginIndex = 0;
        int endIndex = 0;
        while ( true ) {
            beginIndex = text.indexOf( "&lt;div", beginIndex ) - 4;
            endIndex = text.indexOf( "			", beginIndex );

            if ( beginIndex > 0 && endIndex > 0 ) { // 拿掉中間的部份
                text = text.substring( 0, beginIndex ) + text.substring( endIndex, text.length() );

            }
            else {
                break;
            }
        }
        return text;
    }

    // 將原本頁面轉為庫存頁面
    public String changeToArchiverPage( String urlString ) {
        if ( !urlString.matches( "(?s).*/archiver/(?s).*" ) ) { // 不是庫頁存檔的網址
            if ( urlString.matches( "(?s).*forum\\.php\\?(?s).*" ) ) { // 經過資料庫處理的位址
                if ( urlString.matches( "(?s).*&tid=(?s).*" ) ) { // 文章頁面      
                    // 先取得tid
                    int beginIndex = urlString.indexOf( "&tid=" );
                    beginIndex = urlString.indexOf( "=", beginIndex ) + 1;
                    int endIndex = urlString.indexOf( "&", beginIndex );
                    String tid = urlString.substring( beginIndex, endIndex );

                    // 再取得page
                    beginIndex = urlString.indexOf( "page%" );
                    beginIndex = urlString.indexOf( "%", beginIndex ) + 1;
                    endIndex = urlString.indexOf( "%", beginIndex );
                    String page = urlString.substring( beginIndex, endIndex );

                    urlString = baseURL + "/archiver/tid-" + tid + "-" + page + ".html";
                }
                else if ( urlString.matches( "(?s).*&fid=(?s).*" ) ) { // 清單頁面    
                    // 因為經過處理的清單頁面沒有對應的庫頁存檔頁面，因此不轉換
                }

            }
            else {
                urlString = urlString.replaceAll( "/forum-", "/archiver/fid-" ); // 更換清單頁面的網址
                urlString = urlString.replaceAll( "/thread-", "/archiver/tid-" ); // 更換閱讀頁面的網址
            }
        }

        return urlString;
    }

    @Override // 因為原檔就是utf8了，所以無須轉碼
    public String getAllPageString( String urlString ) {
        urlString = changeToArchiverPage( urlString ); // 將原本頁面轉為庫存頁面

        String indexName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_eyny_", "html" );
        Common.downloadFile( urlString, SetUp.getTempDirectory(), indexName, true, cookie );

        return Common.getFileString( SetUp.getTempDirectory(), indexName );
    }

    @Override
    public String getTitleOnMainPage( String urlString, String allPageString ) {
        int beginIndex, endIndex;

        if ( urlString.matches( "(?s).*/tid-(?s).*" ) || urlString.matches( "(?s).*/thread-(?s).*" ) ) { // 網址為文章頁面
            beginIndex = allPageString.indexOf( "name=\"keywords\"" );
            beginIndex = allPageString.indexOf( "content=", beginIndex );
            beginIndex = allPageString.indexOf( "\"", beginIndex ) + 1;
            endIndex = allPageString.indexOf( "\"", beginIndex );
        }
        else { // 網址為名單頁面 ex. http://www03.eyny.com/archiver/fid-1777-1.html
            beginIndex = allPageString.indexOf( "<title>" );
            beginIndex = allPageString.indexOf( ">", beginIndex ) + 1;
            endIndex = allPageString.indexOf( "</title>", beginIndex );
        }

        String title = allPageString.substring( beginIndex, endIndex ).trim();
        title = getRegularFileName( title );

        return Common.getStringRemovedIllegalChar( 
                Common.getTraditionalChinese( title ) );
    }

    // 設置基本位址
    public void setBaseURL( String urlString ) {
        // 因為不一定是www03.eyny.com，故隨當前位址轉換基本位址

        int endIndex = urlString.indexOf( "eyny.com" );
        endIndex = urlString.indexOf( "/", endIndex );
        baseURL = urlString.substring( 0, endIndex );
    }

    @Override
    public List<List<String>> getVolumeTitleAndUrlOnMainPage( String urlString, String allPageString ) {
        // combine volumeList and urlList into combinationList, return it.

        List<List<String>> combinationList = new ArrayList<List<String>>();
        List<String> urlList = new ArrayList<String>();
        List<String> volumeList = new ArrayList<String>();

        int beginIndex = 0, endIndex = 0;

        setBaseURL( urlString ); // 設置基本位址

        if ( urlString.matches( "(?s).*/tid-(?s).*" )
                || urlString.matches( "(?s).*/thread-(?s).*" )
                || urlString.matches( "(?s).*&tid=(?s).*" ) ) { // 網址為文章頁面 
            // 取得單集名稱
            String volumeTitle = getTitle();
            volumeTitle = getRegularFileName( volumeTitle );
            volumeList.add( Common.getStringRemovedIllegalChar( volumeTitle.trim() ) );

            // 取得單集位址
            urlList.add( urlString );

            totalVolume = 1;
        }
        else {
            if ( urlString.matches( "(?s).*&fid=(?s).*" ) ) { // 動態產生的清單頁面

                beginIndex = allPageString.indexOf( "id=\"separatorline\"" );
                endIndex = allPageString.lastIndexOf( "</td><td class=\"by\">" );
                String tempString = allPageString.substring( beginIndex, endIndex );

                totalVolume = tempString.split( "class=\"xst\"" ).length - 1;
                beginIndex = endIndex = 0;
                String pageName = ""; // 頁面名稱
                String volumeTitle = "";
                for ( int i = 0 ; i < totalVolume ; i++ ) {

                    // 取得單集名稱
                    beginIndex = tempString.indexOf( "class=\"xst\"", beginIndex );
                    beginIndex = tempString.indexOf( ">", beginIndex ) + 1;
                    endIndex = tempString.indexOf( "</a>", beginIndex );
                    volumeTitle = tempString.substring( beginIndex, endIndex );
                    volumeList.add( Common.getStringRemovedIllegalChar( volumeTitle.trim() ) );

                     // 取得單集位址
                    beginIndex = tempString.lastIndexOf( " href=", beginIndex );
                    beginIndex = tempString.indexOf( "\"", beginIndex ) + 1;
                    endIndex = tempString.indexOf( "\"", beginIndex );
                    pageName = tempString.substring( beginIndex, endIndex );
                    pageName = pageName.replaceAll( "amp;", "" );
                    urlList.add( baseURL + "/" + pageName );
                    
                    beginIndex = tempString.indexOf( "class=\"xst\"", beginIndex ) + 1;
                }
            }
            else { // 靜態的清單頁面

                beginIndex = allPageString.indexOf( "<li>" );
                endIndex = allPageString.lastIndexOf( "</ul>" );
                String tempString = allPageString.substring( beginIndex, endIndex );

                totalVolume = tempString.split( " href=" ).length - 1;
                beginIndex = endIndex = 0;
                String pageName = ""; // 頁面名稱
                String volumeTitle = "";
                for ( int i = 0 ; i < totalVolume ; i++ ) {
                    // 取得單集位址
                    beginIndex = tempString.indexOf( " href=", beginIndex );
                    beginIndex = tempString.indexOf( "\"", beginIndex ) + 1;
                    endIndex = tempString.indexOf( "\"", beginIndex );
                    pageName = tempString.substring( beginIndex, endIndex );
                    urlList.add( baseURL + "/archiver/" + pageName );

                    // 取得單集名稱
                    beginIndex = tempString.indexOf( ">", beginIndex ) + 1;
                    endIndex = tempString.indexOf( "</a>", beginIndex );
                    volumeTitle = tempString.substring( beginIndex, endIndex );
                    volumeList.add( Common.getStringRemovedIllegalChar( 
                            Common.getTraditionalChinese( volumeTitle.trim() ) ) );
                }
            }


        }
        Common.debugPrintln( "共有" + totalVolume + "集" );

        combinationList.add( volumeList );
        combinationList.add( urlList );

        return combinationList;
    }
}
