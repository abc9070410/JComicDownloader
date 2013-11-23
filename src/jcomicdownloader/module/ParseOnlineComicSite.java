/*
 ----------------------------------------------------------------------------------------------------
 Program Name : JComicDownloader
 Authors  : surveyorK
 Last Modified : 2011/10/25
 ----------------------------------------------------------------------------------------------------
 ChangeLog:
 5.04: 修復部分圖片附檔名錯誤的問題。
 2.06: 修復集數名稱數字格式化的bug。singlePageDownload
 2.04: 修改集數名稱命名機制，將裡面的數字格式化（ex. 第3回 -> 第003回），以方便排序。
 2.02: 拿掉轉網址碼的編碼修正
 ----------------------------------------------------------------------------------------------------
 */
package jcomicdownloader.module;

import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import jcomicdownloader.ComicDownGUI;
import jcomicdownloader.SetUp;
import jcomicdownloader.enums.FileFormatEnum;
import jcomicdownloader.enums.Site;
import jcomicdownloader.tools.Common;
import jcomicdownloader.tools.CommonGUI;

/**

 解析網站的類別(ParseXXX)都會繼承此一類別
 */
abstract public class ParseOnlineComicSite {

    protected int siteID; // 網站編號
    protected String siteName; // 網站名稱
    protected String pageExtension; // 網頁副檔名
    protected int pageCode; //  網頁預設編碼
    protected String title;
    protected String wholeTitle;
    protected String webSite; // web page
    protected String[] comicURL; // all comic pic url
    protected String[] refers;
    //protected String[] novelURLs; // 存放小說的每一頁章節位址
    //protected String[] novelTitles; // 存放小說的每一頁章節名稱
    protected int totalPage; // how many pages
    protected int totalVolume; // how many volumes
    protected String indexName; // temp stored file
    protected String indexEncodeName; // temp stored file encoding to UTF-8
    protected String downloadDirectory;
    protected int runMode; // 只分析、只下載或分析加下載
    protected String textFilePath; // 小說檔案完整目錄位置，包含檔名（只有下載小說時才有用）

    abstract public void setParameters(); // 須取得title和wholeTitle（title可用getTitle()）

    abstract public void parseComicURL(); // 必須解析出下載位址並傳給comicURL

    abstract public boolean isSingleVolumePage( String urlString ); // 檢查是否為單集頁面

    abstract public String getAllPageString( String urlString ); // 取得此網址指向的網頁原始碼字串

    abstract public String getTitleOnMainPage( String urlString, String allPageString ); // 從主頁面中取得title

    abstract public List<List<String>> getVolumeTitleAndUrlOnMainPage( String urlString, String allPageString ); // 從主頁面取得所有集數名稱和位址

    abstract public String getMainUrlFromSingleVolumeUrl( String volumeURL ); // 由單集位址轉為全集位址

    // 顯示目前解析的漫畫網站名稱
    public void printLogo() {
        System.out.println( " ______________________________" );
        System.out.println( "|                            " );
        System.out.println( "| Run the " + siteName + " module:     " );
        System.out.println( "|_______________________________\n" );
    }

    // 從單集頁面中取得title
    public String getTitleOnSingleVolumePage( String urlString ) {
        String mainUrlString = getMainUrlFromSingleVolumeUrl( urlString );
        return getTitleOnMainPage( mainUrlString, getAllPageString( mainUrlString ) );
    }

    public void outputVolumeAndUrlList( List<String> volumeList, List<String> urlList ) {
        Common.outputFile( volumeList, SetUp.getTempDirectory(), Common.tempVolumeFileName );
        Common.outputFile( urlList, SetUp.getTempDirectory(), Common.tempUrlFileName );
    }

    public void showParameters() { // for debug
        Common.debugPrintln( "----------" );
        Common.debugPrintln( "totalPage = " + totalPage );
        Common.debugPrintln( "webSite = " + webSite );
        Common.debugPrintln( "----------" );
    }

    // 取得暫存檔名稱
    public String[] getTempFileNames() {
        return new String[]{indexName, indexEncodeName};
    }
    
    public String[] getRefers() {
        return refers;
    }

    public void setURL( String url ) {
        this.webSite = url;
    }

    public void setTitle( String title ) {
        this.title = title;
    }

    public void setWholeTitle( String wholeTitle ) {
        this.wholeTitle = wholeTitle;
    }

    public void setDownloadDirectory( String downloadDirectory ) {
        this.downloadDirectory = downloadDirectory;
    }

    public void setRunMode( int runMode ) {
        this.runMode = runMode;
    }

    public int getRunMode() {
        return runMode;
    }

    public String[] getComicURL() {
        return comicURL;
    }

    public int getSiteID() {
        return siteID;
    }

    public String getTitle() {
        return title;
    }

    public String getWholeTitle() {
        return wholeTitle;
    }

    public String getDownloadDirectory() {
        return downloadDirectory;
    }

    public String getDefaultDownloadDirectory() {
        return title + Common.getSlash() + wholeTitle + Common.getSlash();
    }

    public void deleteTempFile( String[] tempFileNames ) {
        for ( int i = 0; i < tempFileNames.length; i++ ) {
            Common.deleteFile( SetUp.getTempDirectory(), tempFileNames[i] );
        }
    }

    public String fixSpecialCase( String url ) {
        /*
         拿掉修正似乎就沒有問題了......奇怪當初怎麼需要修正勒..... //
         第一數（%E6%95%B8）要改成第一話（%E8%A9%B1）...不曉得是否為特例... url =
         url.replaceAll("%E6%95%B8", "%E8%A9%B1"); //
         話（%E6%95%B8）要改成?（%E8%AF%9D）...不曉得是否為特例... url =
         url.replaceAll("%A9%B1", "%AF%9D"); //
         石黑正?（%EF%BF%BD）要改成石黑正數（%E6%95%B8）...不曉得是否為特例... url =
         url.replaceAll("%EF%BF%BD", "%E6%95%B8"); // ex.
         http://kukudm.com/comiclist/1247/23363/1.htm

         * // 數數E6%95%B8%E6%95%B8% 改成繪漢E7%B9%AA%E6%BC%A2% url =
         url.replaceAll("E6%95%B8%E6%95%B8%", "E7%B9%AA%E6%BC%A2%");

         * // 數瞄（%E6%95%B8%E7%9E%84）改成掃瞄（%E6%8E%83%E7%9E%84） url =
         url.replaceAll("%E6%95%B8%E7%9E%84", "%E6%8E%83%E7%9E%84");

         * // 117话改為117話 ex. http://kukudm.com/comiclist/774/21545/1.htm //url
         = url.replaceAll("%E8%AF%9D", "%E8%A9%B1");
         */
        return url;
    }

    // 只下載單一張圖片（因應部份網站無法一次解得所有圖片網址，只能每下載一張網頁，從中解析得到網址才下載）
    protected void singlePageDownload( String title, String wholeTitle, String url,
        int totalPage, int nowPageNumber, int delayTime ) {
        singlePageDownload( title, wholeTitle, url, null, totalPage, nowPageNumber,
            delayTime, false, "", "", false );
    }

    // 只下載單一張圖片，並指定副檔名
    protected void singlePageDownload( String title, String wholeTitle, String url, String extension,
        int totalPage, int nowPageNumber, int delayTime ) {
        singlePageDownload( title, wholeTitle, url, extension, totalPage, nowPageNumber,
            delayTime, false, "", "", false );
    }

    // 需要用到refer檔頭
    protected void singlePageDownloadUsingRefer( String title, String wholeTitle, String url,
        int totalPage, int nowPageNumber, int delayTime, String referURL ) {
        Common.debugPrintln( "REFER可能沒用: " + referURL );
        
        singlePageDownload( title, wholeTitle, url, null, totalPage, nowPageNumber,
            delayTime, false, "", referURL, false );
    }
    
    // 用最簡單的方式下載（不包含cookie) 使用Common.simpleDownloadFile
    protected void singlePageDownloadUsingSimple( String title, String wholeTitle, String url,
        int totalPage, int nowPageNumber, String referURL ) {
        singlePageDownload( title, wholeTitle, url, null, totalPage, nowPageNumber,
            0, false, "", referURL, true );
    }

    // 用最簡單的方式下載（包含cookie) 使用Common.simpleDownloadFile
    protected void singlePageDownloadUsingSimple( String title, String wholeTitle, String url,
        int totalPage, int nowPageNumber, String cookieString, String referURL ) {
        singlePageDownload( title, wholeTitle, url, null, totalPage, nowPageNumber,
            0, true, cookieString, referURL, true );
    }

    // 只下載單一張圖片（因應部份網站無法一次解得所有圖片網址，只能每下載一張網頁，從中解析得到網址才下載）
    protected void singlePageDownload( String title, String wholeTitle, String url, 
        String extension, int totalPage, int nowPageNumber, int delayTime, boolean needCookie,
        String cookieString, String referURL, boolean simpleDownload ) {
        
        if ( !SetUp.getDownloadPicFile() ) { // 分析後不下載圖檔
            return;
        }
        
        if ( wholeTitle == null ) {
            CommonGUI.stateBarMainMessage = title + " : ";
        }
        else {
            CommonGUI.stateBarMainMessage = title + "/" + wholeTitle + " : ";
        }
        CommonGUI.stateBarDetailMessage = "共" + totalPage + "頁，第" + ( nowPageNumber ) + "頁下載中";

        if ( SetUp.getShowDoneMessageAtSystemTray() && Common.withGUI() ) {
            ComicDownGUI.trayIcon.setToolTip( CommonGUI.stateBarMainMessage
                + CommonGUI.stateBarDetailMessage );
        }

        String extensionName = "";

        if ( extension != null && !extension.equals( "" ) ) { // 若有指定副檔名，優先使用
            extensionName = extension;
        }
        else if ( url.matches( "(?s).*\\.\\w+" ) ) {
            extensionName = url.split( "\\." )[url.split( "\\." ).length - 1]; // 取得圖片附檔名
        }
        else {
            if ( RunModule.isNovelSite( siteID ) || RunModule.isBlogSite( siteID ) ) {
                extensionName = "html"; // 因為是網頁，所以副檔名給html
            }
            else {
                if ( url.matches( "(?).*\\.png\\?(?).*") ) {
                    extensionName = "png";  //  dm5的圖片位址後面有接參數
                }
                else {
                extensionName = "jpg"; // 因應WY沒有附檔名，只好都給jpg
                }
            }
        }
        NumberFormat formatter = new DecimalFormat( Common.getZero() );
        String fileName = formatter.format( nowPageNumber ) + "." + extensionName;
        String nextFileName = formatter.format( nowPageNumber + 1 ) + "." + extensionName;

        CommonGUI.stateBarDetailMessage += ": [" + fileName + "]";

        // 下載第n張之前，先檢查第n+1張圖是否存在，若是則跳下一張

        if ( Run.isAlive
            && ( !new File( getDownloadDirectory() + fileName ).exists()
            || !new File( getDownloadDirectory() + nextFileName ).exists() ) ) {
            Common.debugPrint( nowPageNumber + " " );

            if ( simpleDownload ) {
                Common.simpleDownloadFile( url, getDownloadDirectory(), fileName, cookieString, referURL );
            }
            else if ( delayTime == 0 ) {
                //Common.print( url, getDownloadDirectory(), fileName, needCookie + "", cookieString );
                Common.downloadFile( url, getDownloadDirectory(), fileName, needCookie, cookieString, referURL );
            }
            else {
                Common.slowDownloadFile( url, getDownloadDirectory(), fileName, delayTime, needCookie, cookieString );
            }
        }

    }

    // 只下載單一張圖片（因應部份網站無法一次解得所有圖片網址，只能每下載一張網頁，從中解析得到網址才下載）,圖片名稱指定
    protected void singlePageDownload( String title, String wholeTitle, String url, int totalPage, int nowPageNumber, String fileName, int delayTime, boolean fastMode ) {
        singlePageDownload( title, wholeTitle, url, totalPage, nowPageNumber, fileName, delayTime, false, "", "", fastMode );
    }

    // 需要設定refer才能下載
    protected void singlePageDownload( String title, String wholeTitle, String url, int totalPage, int nowPageNumber, String fileName,
        int delayTime, boolean needCookie, String cookieString, String referURL ) {
        singlePageDownload( title, wholeTitle, url, totalPage, nowPageNumber, fileName, delayTime, false, "", referURL, false );
    }

    protected void singlePageDownload( String title, String wholeTitle, String url, int totalPage, int nowPageNumber, String fileName,
        int delayTime, boolean needCookie, String cookieString ) {
        singlePageDownload( title, wholeTitle, url, totalPage, nowPageNumber, fileName, delayTime, false, "", "", false );
    }

    // 只下載單一張圖片（因應部份網站無法一次解得所有圖片網址，只能每下載一張網頁，從中解析得到網址才下載）,圖片名稱指定
    protected void singlePageDownload( String title, String wholeTitle, String url, int totalPage, int nowPageNumber, String fileName,
        int delayTime, boolean needCookie, String cookieString, String referURL, boolean fastMode ) {
        
        
        if ( !SetUp.getDownloadPicFile() ) { // 分析後不下載圖檔
            return;
        }
        
        
        if ( wholeTitle == null ) {
            CommonGUI.stateBarMainMessage = title + " : ";
        }
        else {
            CommonGUI.stateBarMainMessage = title + "/" + wholeTitle + " : ";
        }
        CommonGUI.stateBarDetailMessage = "共" + totalPage + "頁，第" + ( nowPageNumber ) + "頁下載中";

        if ( SetUp.getShowDoneMessageAtSystemTray() && Common.withGUI() ) {
            ComicDownGUI.trayIcon.setToolTip( CommonGUI.stateBarMainMessage
                + CommonGUI.stateBarDetailMessage );
        }

        CommonGUI.stateBarDetailMessage += ": [" + fileName + "]";

        if ( delayTime == 0 ) {
            Common.downloadFile( url, getDownloadDirectory(), fileName, needCookie, cookieString, referURL,
                fastMode, SetUp.getRetryTimes(), false, false );
        }
        else {
            Common.slowDownloadFile( url, getDownloadDirectory(), fileName, delayTime, needCookie, cookieString );
        }
    }

    // 只下載單一張圖片（因應部份網站無法一次解得所有圖片網址，只能每下載一張網頁，從中解析得到網址才下載）,圖片名稱指定
    protected void singlePagePost( String title, String wholeTitle, String url, 
        String queryString, int totalPage, int nowPageNumber, String fileName, 
        boolean needCookie, String cookieString, String referURL ) {
        if ( wholeTitle == null ) {
            CommonGUI.stateBarMainMessage = title + " : ";
        }
        else {
            CommonGUI.stateBarMainMessage = title + "/" + wholeTitle + " : ";
        }
        CommonGUI.stateBarDetailMessage = "共" + totalPage + "頁，第" + ( nowPageNumber ) + "頁下載中";

        if ( SetUp.getShowDoneMessageAtSystemTray() && Common.withGUI() ) {
            ComicDownGUI.trayIcon.setToolTip( CommonGUI.stateBarMainMessage
                + CommonGUI.stateBarDetailMessage );
        }

        CommonGUI.stateBarDetailMessage += ": [" + fileName + "]";

        Common.downloadPost( url, getDownloadDirectory(),
            fileName, false, "", queryString, url );

    }

    // 將集數名稱的數字部份格式化（ex. 第3回 -> 第003回），方便排序之用
    protected String getVolumeWithFormatNumber( String volume ) {
        String formatVolume = "";

        try {

            int beginIndex = -1;
            for ( int i = 0; i < volume.length(); i++ ) {
                if ( volume.substring( i, i + 1 ).matches( "\\d" ) ) {
                    beginIndex = i;
                    break;
                }
            }
            //System.out.println( beginIndex + " -> " + volume );

            int endIndex = volume.length();
            for ( int i = beginIndex; i < volume.length() && beginIndex >= 0; i++ ) {
                if ( volume.substring( i, i + 1 ).matches( "\\D" ) ) {
                    endIndex = i;
                    break;
                }
                else {
                    //System.out.println( volume.substring(i,i+1) + " " );
                }
            }
            

            if ( endIndex < 0 || beginIndex < 0 ) {
                //System.out.println( "無法格式化: " + volume + " " + beginIndex + " " + endIndex );
                formatVolume = volume;
            }
            else {
                //System.out.println( volume + " " + beginIndex + " " + endIndex + " 數字部份：" + volume.substring( beginIndex, endIndex ) );

                String originalNumber = volume.substring( beginIndex, endIndex );
                NumberFormat formatter = new DecimalFormat( "000" );
                String formatNumber = formatter.format( Integer.parseInt( originalNumber ) );

                formatVolume = volume.replaceFirst( originalNumber, formatNumber );
            }

        }
        catch ( Exception ex ) {
            formatVolume = volume;
            Common.hadleErrorMessage( ex, "集數名稱的數字規格化處理發生錯誤" );
        }

        return formatVolume;
    }

    //  將numeric character references全部還原
    public String replaceNCR( String text ) {
        int beginIndex = 0;
        int endIndex = 0;

        // 先將非數字的character references進行替換
        text = text.replaceAll( "&nbsp;", " " );
        text = text.replaceAll( "&quot;", "\"" );
        text = text.replaceAll( "&amp;", "&" );
        text = text.replaceAll( "&hellip;", "..." );
        text = text.replaceAll( "&gt;", ">" );
        text = text.replaceAll( "&lt;", "<" );
        text = text.replaceAll( "&rdquo;", "”" );
        text = text.replaceAll( "&ldquo;", "“" );

        String ncrString = ""; // 存放numeric character references字串 ex. &#65289;
        String numberString = ""; // 存放numeric character references的數字部份 ex. 65289
        String decode = ""; // 存放已經解碼的字元 ex. ）

        int times = 0;
        while ( true && times < 300 ) {
            Common.debugPrint( " " + times ++ );
            beginIndex = text.indexOf( "&#", beginIndex );
            endIndex = text.indexOf( ";", beginIndex ) + 1;

            if ( beginIndex >= 0 && endIndex >= 0 && beginIndex < endIndex ) {
                ncrString = text.substring( beginIndex, endIndex );
                numberString = ncrString.substring( 2, ncrString.length() - 1 );
                //Common.debugPrint( "轉換前：" + ncrString );

                if ( numberString.matches( "\\d+" ) ) {
                    char decodeChar = ( char ) Integer.parseInt( numberString );
                    decode = String.valueOf( decodeChar );
                    //Common.debugPrintln( "　轉換後：" + decode );

                    text = text.replaceAll( ncrString, decode );
                }
            }
            else {
                break;
            }

        }

        return text;
    }

    // 將換行tag轉換為換行字元
    public String replaceNewLine( String text ) {
        text = text.replaceAll( "\r\n", "" ); // 若原始檔為big5編碼，可能換行是採用\r\n的格式（windows換行機制）
        text = text.replaceAll( "\n", "" ); // 拿掉非windows換行機制的換行字元

        // 開始替換

        text = text.replaceAll( "</p><p>", "\r\n\r\n" );
        text = text.replaceAll( "<br />", "\r\n" );
        text = text.replaceAll( "<br>", "\r\n" );
        text = text.replaceAll( "<BR>", "\r\n" );
        text = text.replaceAll( "<br[^<>]+>", "\r\n" );
        text = text.replaceAll( "</p>", "\r\n" );
        text = text.replaceAll( "</P>", "\r\n" );
        text = text.replaceAll( "</h1>", "\r\n" );
        text = text.replaceAll( "</H1>", "\r\n" );
        text = text.replaceAll( "<tr>", "\r\n\r\n" );
        text = text.replaceAll( "<TR>", "\r\n\r\n" );

        return text;
    }

    // 將<img.*>標籤拿掉，只保留其中的圖片網址
    public String replaceImg( String text ) {
        int start = 0;
        int beginIndex = 0;
        int endIndex = 0;
        String picURL = ""; // 圖片網址
        String picName = ""; // 圖片名稱
        int times = 0;
        int count = 0;
        while ( true && times < 40 ) {
            //Common.debugPrint( " " + count );
            start = beginIndex = text.indexOf( "<img ", beginIndex );
            if ( beginIndex >= 0 ) {
                beginIndex = text.indexOf( "src=", beginIndex );
                beginIndex = text.indexOf( "http", beginIndex );
                endIndex = text.indexOf( " ", beginIndex ) - 1;

                if ( beginIndex > 0 && endIndex > 0 ) {
                    picURL = text.substring( beginIndex, endIndex );
                    System.out.println( picURL + " -> " + picName );

                    text = text.substring( 0, start ) + "\n" + picURL + "\n" + text.substring( start, text.length() );
                    beginIndex = endIndex + picURL.length();
                }
                else {
                    beginIndex = start += 10;
                }
            }
            else {
                break;
            }
            times++;
        }

        return text;
    }

    // 拿掉<script 到 </script>之間的內容
    public String replaceJS( String text ) {
        
        String scriptFontTag = "<script";
        if ( text.indexOf( "<SCRIPT>") > 0 ) {
            scriptFontTag = "<script>";
        }
        String scriptBackTag = "</script>";
        
        if ( text.indexOf( scriptFontTag ) < 0 ) {
            scriptFontTag = scriptFontTag.toUpperCase();
        }
         if ( text.indexOf( scriptBackTag ) < 0 ) {
            scriptBackTag = scriptBackTag.toUpperCase();
        }
         
         Common.debugPrintln( "拿掉JS: " + scriptFontTag + " " + scriptBackTag );
        
        int beginIndex = 0;
        int endIndex = 0;
        int times = 0;
        while ( true && times < 10 ) {
            beginIndex = text.indexOf( scriptFontTag, beginIndex );
            endIndex = text.indexOf( scriptBackTag, beginIndex );
            endIndex = text.indexOf( ">", endIndex ) + 1;

            if ( beginIndex > 0 && endIndex > 0 ) { // 拿掉中間的部份
                text = text.substring( 0, beginIndex ) + text.substring( endIndex, text.length() );
            }
            else {
                break;
            }
            times++;
        }
        return text;
    }

    // 拿掉<style 到 </style>之間的內容
    public String replaceStyle( String text ) {
        int beginIndex = 0;
        int endIndex = 0;
        int times = 0;
        
        String styleFontTag = "<style";
        String styleBackTag = "</style>";
        
        //  若找不到小寫sytle tag 就轉大寫
        if ( text.indexOf( styleFontTag ) < 0 ) {
            styleFontTag = styleFontTag.toUpperCase();
            styleBackTag = styleBackTag.toUpperCase();
        }
        
        
        while ( true && times < 10 ) {
            beginIndex = text.indexOf( styleFontTag, beginIndex );
            endIndex = text.indexOf( styleBackTag, beginIndex );
            endIndex = text.indexOf( ">", endIndex ) + 1;

            if ( beginIndex > 0 && endIndex > 0 ) { // 拿掉中間的部份
                text = text.substring( 0, beginIndex ) + text.substring( endIndex, text.length() );
            }
            else {
                break;
            }

            times++;
        }
        return text;
    }

    // 拿掉<!-- 到 -->之間的內容
    public String replaceComment( String text ) {
        int beginIndex = 0;
        int endIndex = 0;
        int times = 0;
        while ( true && times < 10 ) {
            beginIndex = text.indexOf( "<!--", beginIndex );
            endIndex = text.indexOf( "-->", beginIndex );
            endIndex = text.indexOf( ">", endIndex ) + 1;

            if ( beginIndex > 0 && endIndex > 0 ) { // 拿掉中間的部份
                text = text.substring( 0, beginIndex ) + text.substring( endIndex, text.length() );
            }
            else {
                break;
            }

            times++;
        }
        return text;
    }

    // 將html的tag拿掉，且將numeric character references還原回原本的字元。
    public String replaceProcessToText( String text ) {
        Common.debugPrint( "輸出txt格式:" );
        
        text = replaceNCR( text ); //  將numeric character references全部還原
        text = replaceNewLine( text ); // 將換行tag轉換為換行字元
        text = replaceImg( text ); // 將圖片標籤拿掉，只保留圖片網址
        //text = text.replaceAll( "<script[^(scrpit)]+[(/script>)]{1}", "" ); // 拿掉js
        text = replaceJS( text ); // 拿掉JS
        text = replaceComment( text ); // 拿掉註解
        text = replaceStyle( text ); // 拿掉style
        text = text.replaceAll( "<[^<>]+>", "" ); // 將所有標籤去除
        
        Common.debugPrintln( "html轉txt處理完畢" );
        return text;
    }
    
    public String hadlePicInHtml( String text, String articleTitle )
    {
        return hadlePicInHtml( text, articleTitle, "" );
    }
    
    //  下載<img ~ > 標籤裡面的圖片，作編號，並修正text的圖片位址
    public String hadlePicInHtml( String text, String articleTitle, String referURL ) {
        int beginIndex = 0;
        int endIndex = 0;
        
        String[] tempTokens = text.split( "<img" );
        String[] picURLs = new String[tempTokens.length - 1];
        
        Common.debugPrintln( articleTitle + " 共有 " + picURLs.length + " 張圖片" );
        
        String downloadDirectory = articleTitle + "_pics" + Common.getSlash();
        
        String oldDownloadDirectory = getDownloadDirectory();
        setDownloadDirectory( getDownloadDirectory() + downloadDirectory );
        
        text = text.replaceAll( "data-src", "src" ); // for yam blog .

        
        for ( int i = 0; i < picURLs.length; i ++ ) {
            Common.debugPrintln( "TEMP: " + tempTokens[i+1] );
            
            beginIndex = tempTokens[i+1].indexOf( "src=" ) + 4;
            beginIndex = Common.getSmallerIndexOfTwoKeyword( tempTokens[i+1], beginIndex, "\"", "'" ) + 1;
            endIndex = Common.getSmallerIndexOfTwoKeyword( tempTokens[i+1], beginIndex, "\"", "'" ) + 1;
            
            picURLs[i] = tempTokens[i+1].substring( beginIndex, endIndex ).replaceAll( "\"", "" );
            
            Common.debugPrintln( i + " 解析圖片網址:" + picURLs[i] );
            
            if ( Common.isLegalURL( picURLs[i] ) ) {

                beginIndex = picURLs[i].lastIndexOf( "/" ) + 1;
                String picName = picURLs[i].substring( beginIndex, picURLs[i].length() );
                String extensionName = "";
                
                if ( picName.split( "\\." ).length > 1 ) {
                    extensionName = picName.split( "\\." )[1];
                }
                
                NumberFormat formatter = new DecimalFormat( Common.getZero() );
                String fileName = formatter.format( i ) + "." + extensionName;
                
                
                
                // 每解析一個網址就下載一張圖
                if ( referURL.matches( "" ) )
                {
                    singlePageDownload( getTitle(), getWholeTitle() + downloadDirectory, picURLs[i], picURLs.length, i, 0 );
                }
                else
                {
                    singlePageDownloadUsingSimple( getTitle(), getWholeTitle() + downloadDirectory, picURLs[i], picURLs.length, i, "", referURL );
                }
                
                text = text.replace( picURLs[i], downloadDirectory + fileName ); // 位址取代為下載目錄
            }
        }
        
        setDownloadDirectory( oldDownloadDirectory );
        
        return text;
    }

    // 由純文字轉為html格式
    public String replaceProcessToHtml( String text ) {
        Common.debugPrintln( "輸出html格式:" );
        
        //text = replaceNCR( text ); //  將numeric character references全部還原
        text = replaceNewLine( text ); // 將換行tag轉換為換行字元
        text = replaceJS( text ); // 拿掉JS
        text = replaceStyle( text ); // 拿掉style
        //text = text.replaceAll( "<[^(img)|^(a)|^(/a)|^(b)|(/b)]{1}[^<>]+>", "" ); // 將所有標籤去除，只保留圖片標籤和超連結
        //text = text.replaceAll( "<[^(img)|^(a)|^(/a)]{1}[^<>]+>", "" ); // 將所有標籤去除，只保留圖片標籤和超連結
        //text = text.replaceAll( "</span>|</div>|</wbr>", "" ); // 將多餘的標籤去除
        text = text.replaceAll( "\n", "<br>" ); // 將換行符號還原回換行標籤
        
        return text;
    }

    // 取得網頁編碼資訊
    public String getCharsetInformation() {
        return "<meta content='text/html; charset=UTF-8' http-equiv='Content-Type'/>";
    }

    // 取得文章前言，提供標題和網址的資訊
    public String getInformation( String title, String url ) {
        String aheadText = ""; // 文章前言，提供標題和網址的資訊

        if ( SetUp.getDefaultTextOutputFormat() == FileFormatEnum.HTML_WITHOUT_PIC ||
             SetUp.getDefaultTextOutputFormat() == FileFormatEnum.HTML_WITH_PIC ) {
            aheadText = getCharsetInformation(); // 取得網頁編碼資訊
            aheadText += "原文標題：" + title.replaceAll( "\\.html", "" ) + "\n";
            aheadText += "原文地址：" + "<a href=\"" + url + "\" target=_blank>" + url + "</a>";
            aheadText += "<hr>\n";
            aheadText = aheadText.replaceAll( "\n", "<br>" );
        }
        else {
            aheadText += "原文標題：" + title.replaceAll( "\\.txt", "" ) + "\r\n";
            aheadText += "原文地址：" + url + "\r\n\r\n";
        }

        return aheadText;
    }

    // 取得上一層目錄 ex. /media/disk/ -> /media/
    public String getParentPath( String path ) {
        String tempString = path;
        tempString = tempString.substring( 0, tempString.length() - 1 );
        int endIndex = tempString.lastIndexOf( Common.getSlash() ) + 1;

        return tempString.substring( 0, endIndex ); // 放在外面;
    }

    // 建立黑名單，若url為黑名單，則不在下載封面的考慮範圍內
    public boolean allowDownloadCoverSite( String url ) {
        if ( url.matches( "(?s).*docstoc.com(?s).*" )
            || url.matches( "(?s).*docstoccdn.com(?s).*" )
            || url.matches( "(?s).*reading.cdns.com.tw(?s).*" )
            || url.matches( "(?s).*www.huachenghotel.com(?s).*" )
            || url.matches( "(?s).*sdfilm.com.cn(?s).*" )
            || url.matches( "(?s).*www.zk268.com(?s).*" )
            || url.matches( "(?s).*shangxueba.com(?s).*" )
            || url.matches( "(?s).*ugo1688.com(?s).*" )
            || url.matches( "(?s).*simplecd.org(?s).*" )
            || url.matches( "(?s).*vcimg.com(?s).*" )
            || url.matches( "(?s).*blogcn.com(?s).*" )
            || url.matches( "(?s).*newsworld.cna.com.tw(?s).*" ) ) {
            return false;
        }
        else {
            return true;
        }
    }

    // 下載書的封面（由google搜尋取得）
    public void downloadCover( String title, String author ) {
        String keyword = title.replaceAll( "_", " " );
        author = Common.getTraditionalChinese( author );

        // 設定封面圖片存放路徑、封面圖片檔名
        String storePath = getParentPath( getDownloadDirectory() );
        String storeCoverFile = author + "_" + title + ".jpg";

        if ( new File( storePath + storeCoverFile ).exists() ) {
            Common.debugPrintln( storePath + storeCoverFile + "已經存在，無須下載" );
            return; // 如果圖片已存在，那就不用下載了 
        }

        if ( author != null
            && !"".matches( author )
            && keyword.indexOf( author ) < 0 ) {
            keyword += "+" + author; // 有作者資訊，加入到關鍵字裡面
        }

        String url = getGooglePicSearchURL( keyword ); // 取得google圖片搜尋title的位址
        // 下載此搜尋後的頁面
        String tempFile = "search_cover_from_google.html";
        Common.downloadFile( url, SetUp.getTempDirectory(), tempFile, false, "", "" );
        String allPageString = Common.getFileString( SetUp.getTempDirectory(), tempFile );

        // 預設從前n張圖去挑選最適合的封面
        int n = 5;
        String[] coverStrings = new CommonGUI().getCoverStrings();
        int index = SetUp.getCoverSelectAmountIndex();
        if ( coverStrings.length > index ) {
            n = Integer.parseInt( coverStrings[index].replaceAll( "\\D", "" ) ); // 只取數字的部份
        }
        List<String> coverList = getCoverURL( getCoverList( allPageString, n ) );

        if ( coverList.size() < 1 || coverList == null ) {
            Common.errorReport( "無法取得封面圖片網址" );
            return;
        }

        // 無法下載就換下一張，直至下載成功或全部測試過為止
        for ( int i = 0; i < coverList.size(); i++ ) {
            Common.downloadFile( coverList.get( i ),
                storePath, storeCoverFile, false, "", "" );

            if ( new File( storePath + storeCoverFile ).exists() ) {
                break;
            }
        }

    }

    // 取得google圖片搜尋title的位址
    public String getGooglePicSearchURL( String keyword ) {

        if ( keyword.length() > 22 ) {
            keyword = keyword.substring( 0, 22 );
        }

        String url = "https://www.google.com/search?tbm=isch&q=" + keyword + "&hl=zh-TW&tbm=isch&biw=1600&bih=804"; // &sout=1
        url = Common.getFixedChineseURL( url );
        //url = Common.getRegularURL( url );
        Common.debugPrintln( "圖片搜尋頁面的網址長度：" + url.length() );

        return url;
    }

    // 從n張圖片資訊中找出最符合條件的封面
    public List<String> getCoverURL( List<List<String>> combinationList ) {
        List<String> coverList = new ArrayList<String>();

        if ( combinationList != null ) {
            List<String> urlList = combinationList.get( 0 );
            List<String> widthList = combinationList.get( 1 );
            List<String> heightList = combinationList.get( 2 );

            Common.debugPrintln( "將圖片作排序動作：" );

            for ( int j = 0; j < urlList.size(); j++ ) {
                int index = 0; // 最終選擇的封面編號
                int heightest = 0;
                for ( int i = 0; i < urlList.size(); i++ ) {
                    // 只有高度長於寬度的圖片才納入考量，且長寬需小於2000
                    if ( !urlList.get( i ).equals( Common.NULL )
                        && Integer.parseInt( widthList.get( i ) ) <= Integer.parseInt( heightList.get( i ) )
                        && Integer.parseInt( widthList.get( i ) ) < 2000
                        && Integer.parseInt( heightList.get( i ) ) < 2000 ) {

                        // 找出高度最長的圖，即最大的圖
                        if ( heightest < Integer.parseInt( heightList.get( i ) ) ) {
                            heightest = Integer.parseInt( heightList.get( i ) );
                            index = i;
                        }
                    }
                }

                // for debug
                Common.debugPrintln( index + " " + urlList.get( index ) + " "
                    + widthList.get( index ) + " " + heightList.get( index ) );

                coverList.add( urlList.get( index ) );
                urlList.set( index, Common.NULL );
                heightest = 0;
            }
        }
        else {
            Common.errorReport( "無法獲取封面圖片資訊" );
        }

        return coverList;
    }

    // 從google搜尋頁面中取得前n張圖片的資料，並挑選最適合的封面圖，回傳其位址
    public List<List<String>> getCoverList( String allPageString, int n ) {

        List<List<String>> combinationList = new ArrayList<List<String>>();
        List<String> urlList = new ArrayList<String>();
        List<String> widthList = new ArrayList<String>();
        List<String> heightList = new ArrayList<String>();

        int beginIndex = 0;
        int endIndex = 0;
        String tempString = "";
        for ( int i = 0; i < n; i++ ) {
            // 存入網址
            beginIndex = allPageString.indexOf( "imgurl=", beginIndex );
            if ( beginIndex < 0 ) {
                break;
            }
            beginIndex = allPageString.indexOf( "=", beginIndex ) + 1;
            endIndex = Common.getSmallerIndexOfTwoKeyword( allPageString, beginIndex, "&amp;", "%3F" );
            tempString = allPageString.substring( beginIndex, endIndex );

            if ( allowDownloadCoverSite( tempString ) ) {
                urlList.add( tempString );
            }
            else {
                i--;
                continue;
            }

            // 存入圖片高度
            beginIndex = allPageString.indexOf( ";h=", beginIndex ) + 3;
            endIndex = allPageString.indexOf( "&amp;", beginIndex );
            heightList.add( allPageString.substring( beginIndex, endIndex ) );

            // 存入圖片寬度
            beginIndex = allPageString.indexOf( ";w=", beginIndex ) + 3;
            endIndex = allPageString.indexOf( "&amp;", beginIndex );
            widthList.add( allPageString.substring( beginIndex, endIndex ) );

            // for debug
            Common.debugPrintln( i + " " + urlList.get( i ) + " " + widthList.get( i ) + " " + heightList.get( i ) );
        }

        // 合併存入圖片的位址、寬度和高度
        combinationList.add( urlList );
        combinationList.add( widthList );
        combinationList.add( heightList );

        return combinationList;
    }
}