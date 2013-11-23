/*
 ----------------------------------------------------------------------------------------------------
 Program Name : JComicDownloader
 Authors  : surveyorK
 Last Modified : 2013/2/24
 ----------------------------------------------------------------------------------------------------
 ChangeLog:
 5.15: 將輸出位址文件檔檔名後面加_urls＞
 5.13: 增加以simpleDownload下載的mandFile download
 5.12: 
 1. 修正連線錯誤時stateBar的文字顯示。
 2. 修正下載機制，若連線錯誤則至少重試一次。
 3. 增加暫停時倒數秒數的文字顯示。
 5.07: 修復在windows系統下執行腳本若有錯誤輸出時會阻塞的問題。
 2.11: 1. 修改下載機制，增加讀取GZIPInputStreamCommon.getStringUsingDefaultLanguage( 串流的選項（178.com專用）
 *   2. 修復重試後無法下載中間漏頁的問題。（ex. 5.jpg 7.jpg 8.jpg，中間遺漏6.jpg）
 2.01: 1. 修改下載機制，不下載青蛙圖（檔案大小10771 bytes）
 2.0 : 1. 加入下載快速模式，專用於google圖片下載
 1.09: 1. 加入書籤表格和紀錄表格相關的公用方法
 *    2. 以getReomvedUnnecessaryWord()拿掉多餘標題字尾
 1.08: 若下載圖檔時發現檔案只有21或22kb，則懷疑是盜連警示圖片，於一秒後重新連線一次
 ----------------------------------------------------------------------------------------------------
 */
package jcomicdownloader.tools;

import java.awt.Color;
import java.awt.Font;
import jcomicdownloader.table.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import jcomicdownloader.encode.*;
import jcomicdownloader.module.*;
import jcomicdownloader.*;

import java.io.*;
import java.lang.reflect.Method;
import java.util.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.*;
import java.util.zip.*;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;
import jcomicdownloader.enums.*;
import jcomicdownloader.frame.InformationFrame;

/**

 大部分的通用方法都放在這邊，全宣告為靜態，方便使用。
 */
public class Common
{

    public static String recordDirectory = Common.getNowAbsolutePath();
    public static String tempDirectory = Common.getNowAbsolutePath() + "temp" + getSlash();
    public static String downloadDirectory = Common.getNowAbsolutePath() + "down" + getSlash();
    public static String tempVolumeFileName = "temp_volume.txt";
    public static String tempUrlFileName = "temp_url.txt";
    public static String tempVolumeInformationFileName = "temp_volume_information.txt";
    public static boolean isMainPage = false;
    public static int missionCount = 0; // 目前任務總數
    public static int bookmarkCount = 0; // 目前書籤總數
    public static int recordCount = 0; // 目前記錄總數
    public static boolean downloadLock = false;
    public static Thread downloadThread;
    public static boolean urlIsUnknown = false;
    public static String prevClipString; // 用來檢查剪貼簿，若沒有變化就不要貼上輸入欄了
    public static String consoleThreadName = "Thread-console-version";
    public static String setFileName = "set.ini";
    public static int reconnectionTimes = 3; // 嘗試重新連線的最高次數
    public static String defaultAudioString = "使用預設音效";
    public static String defaultSingleDoneAudio = "single_done.wav";
    public static String defaultAllDoneAudio = "all_done.wav";
    public static String mainIcon = "main_icon.png";
    public static String playAudioPic = "play.png";
    public static String NULL = "NULL";

    public static String getZero()
    {
        int length = SetUp.getFileNameLength();

        String zero = "";
        for ( int i = 0; i < length; i++ )
        {
            zero += "0";
        }

        return zero;
    }

    public static String getZero( int zeroAmount )
    {
        String zero = "";
        for ( int i = 0; i < zeroAmount; i++ )
        {
            zero += "0";
        }

        return zero;
    }

    public static void errorReport( String errorString )
    {
        Common.debugPrintln( "ERROR: " + errorString );
        Run.isLegal = false;
        try
        {
            throw new Exception( errorString ); // 自動產生例外訊息，以供輸出
        }
        catch ( Exception ex )
        {
            ex.printStackTrace();
            outputErrorMessage( ex, "ERROR: " + errorString + "\n" );
        }
    }

    public static void debugPrintln( String print )
    { // for debug
        print = Common.getStringUsingDefaultLanguage( print, print ); // 使用預設語言 

        if ( Debug.debugMode )
        {
            System.out.println( print );
        }
    }

    public static void debugPrint( String print )
    { // for debug
        print = Common.getStringUsingDefaultLanguage( print, print ); // 使用預設語言 

        if ( Debug.debugMode )
        {
            System.out.print( print );
        }
    }

    public static void processPrintln( String print )
    { // for debug
        Common.debugPrintln( print );
    }

    public static void processPrint( String print )
    { // for debug
        Common.debugPrint( print );
    }

    public static void checkDirectory( String dir )
    {
        // check if dir exists or not, if not exist, create one.
        if ( !new File( dir ).exists() )
        {
            new File( dir ).mkdirs();
        }
    }

    /*
     // 批次下載需要refer[i]參考webSite[i]的檔案，存於outputDirectory，並取名為titles[i]
     public static void downloadManyFile2( String[] webSite, String[] refer,
     String[] titles, String outputDirectory )
     {

     // if we want to check "\", cannot use [\\], should use [\\\\] ...
     String[] pathStrings = outputDirectory.split( "[\\\\]|/" );
     String nowDownloadTitle = pathStrings[pathStrings.length - 2];

     String mainMessage = "下載 " + nowDownloadTitle + " / ";
     String extension = Common.getDefaultTextExtension(); // 取得預設的文件儲存格式（副檔名）

     String tempName = "temp";
     String fileName = "";
     String nextFileName = "";
     for ( int i = 0; i < webSite.length && Run.isAlive; i++ )
     {
     fileName = titles[i];
     if ( i + 1 < webSite.length )
     {
     nextFileName = titles[i + 1];
     }

     if ( webSite[i] != null )
     {
     // if not all download, the last file needs to re-download
     if ( !new File( outputDirectory + nextFileName ).exists()
     || !new File( outputDirectory + fileName ).exists() )
     {
     CommonGUI.stateBarMainMessage = mainMessage;
     CommonGUI.stateBarDetailMessage = " : " + "共" + webSite.length + "頁"
     + "，第" + (i + 1) + "頁下載中";

     if ( Common.withGUI() && ComicDownGUI.trayIcon != null )
     {
     ComicDownGUI.trayIcon.setToolTip( CommonGUI.stateBarMainMessage
     + CommonGUI.stateBarDetailMessage );
     }

     CommonGUI.stateBarDetailMessage += " : " + fileName;

     if ( refer == null ) {
     Common.downloadFile( webSite[i], outputDirectory, tempName, false, "", "",
     false, SetUp.getRetryTimes(), false, false );
     System.exit( 0 );
     }
     else {

     Common.simpleDownloadFile( webSite[i], outputDirectory, tempName, refer[i]
     );
     Common.debugPrintln( "XXXXXXXXXXX" );
     System.exit( 0 );
     }
     }
     Common.debugPrint( (i + 1) + " " );
     }
     else
     {
     Common.errorReport( "缺少第" + (i + 1) + "個章節（" + titles[i] + "）的位址" );
     }
     }
     }
     */
    // 批次下載webSite[i]的檔案，存於outputDirectory，並取名為titles[i]
    public static void downloadManyFile( String[] webSite, String outputDirectory,
                                         String picFrontName, String extensionName )
    {
        downloadManyFile( webSite, null, outputDirectory, picFrontName, extensionName );
    }

    public static void downloadManyFile( String[] webSite, String[] refers, String outputDirectory,
                                         String picFrontName, String extensionName )
    {
        NumberFormat formatter = new DecimalFormat( Common.getZero() );

        // if we want to check "\", cannot use [\\], should use [\\\\] ...
        String[] pathStrings = outputDirectory.split( "[\\\\]|/" );
        String nowDownloadTitle = pathStrings[pathStrings.length - 2];
        String nowDownloadVolume = pathStrings[pathStrings.length - 1];

        String mainMessage = "下載 " + nowDownloadTitle + " / " + nowDownloadVolume + " ";

        for ( int i = 1; i <= webSite.length && Run.isAlive; i++ )
        {
            // 察知此圖片的副檔名(因為會呼叫downloadManyFile的都是下載圖片)
            String[] tempStrings = webSite[i - 1].split( "/|\\." );

            if ( tempStrings[tempStrings.length - 1].length() == 3 || // ex. jgp, png
                    tempStrings[tempStrings.length - 1].length() == 4 ) // ex. jpeg
            {
                extensionName = tempStrings[tempStrings.length - 1];
            }

            String fileName = picFrontName + formatter.format( i ) + "." + extensionName;
            String nextFileName = picFrontName + formatter.format( i + 1 ) + "." + extensionName;
            if ( webSite[i - 1] != null )
            {
                // if not all download, the last file needs to re-download
                if ( !new File( outputDirectory + nextFileName ).exists()
                        || !new File( outputDirectory + fileName ).exists() )
                {
                    CommonGUI.stateBarMainMessage = mainMessage;
                    CommonGUI.stateBarDetailMessage = "  :  " + "共" + webSite.length + "頁"
                            + "，第" + i + "頁下載中";

                    if ( Common.withGUI() && ComicDownGUI.trayIcon != null )
                    {
                        ComicDownGUI.trayIcon.setToolTip( CommonGUI.stateBarMainMessage
                                + CommonGUI.stateBarDetailMessage );
                    }

                    CommonGUI.stateBarDetailMessage += " : " + fileName;

                    if ( refers == null )
                    {
                        Common.downloadFile( webSite[i - 1], outputDirectory, fileName, false, "", "", false, SetUp.getRetryTimes(), false, false );
                        //System.exit( 0 );
                    }
                    else
                    {
                        Common.simpleDownloadFile( webSite[i - 1], outputDirectory, fileName, refers[i - 1] );
                        //Common.debugPrintln( "XXXX" );
                        //System.exit( 0 );
                    }
                }
                Common.debugPrint( i + " " );
            }
        }
    }

    public static void sleep( int delayMillisecond, String message )
    {
        int delaySecond = delayMillisecond / 1000;
        String realMessage = " " + message + " " + delaySecond + " 秒";

        try
        {
            Common.debugPrintln( realMessage );
            ComicDownGUI.stateBar.setText( realMessage );
            Thread.currentThread().sleep( 1000 );

        }
        catch ( InterruptedException ex )
        {
            Common.hadleErrorMessage( ex, "進行「" + message + "」時發生錯誤" );
        }

        if ( --delaySecond > 0 ) // 以遞迴方式處理暫停
        {
            Common.sleep( delayMillisecond - 1000, message );
        }
    }

    public static void slowDownloadFile( String webSite, String outputDirectory, String outputFileName,
                                         int delayMillisecond, boolean needCookie, String cookieString )
    {
        Common.sleep( delayMillisecond, "下載倒數:" );

        downloadFile( webSite, outputDirectory, outputFileName, needCookie, cookieString, "", false, SetUp.getRetryTimes(), false, false );

    }

    public static String[] getCookieStringsTest( String urlString, String postString )
    {
        String[] tempCookieStrings = null;

        try
        {
            URL url = new URL( urlString );
            HttpURLConnection connection = ( HttpURLConnection ) url.openConnection();
            connection.setRequestProperty( "User-Agent", "User-Agent: Mozilla/5.0 (Windows; U; Windows NT 6.1; zh-TW; rv:1.9.2.8) Gecko/20100722 Firefox/3.6.8" );
            connection.setDoInput( true );
            connection.setDoOutput( true );
            connection.setRequestMethod( "POST" );
            connection.getOutputStream().write( postString.getBytes() );
            connection.getOutputStream().flush();
            connection.getOutputStream().close();
            int code = connection.getResponseCode();
            Common.debugPrintln( "code   " + code );

            tempCookieStrings = tryConnect( connection );
        }
        catch ( Exception ex )
        {
            Common.hadleErrorMessage( ex, "無法正確設置connection" );
        }

        String[] cookieStrings = tempCookieStrings;
        int cookieCount = 0;
        if ( tempCookieStrings != null )
        {
            for ( int i = 0; i < tempCookieStrings.length; i++ )
            {
                if ( tempCookieStrings[i] != null )
                {
                    cookieStrings[cookieCount++] = tempCookieStrings[i]; // 把cookie都集中到前面
                    Common.debugPrintln( cookieCount + " " + tempCookieStrings[i] );
                }
            }
        }

        return cookieStrings;
    }

    public static String getCookieString( String urlString )
    {
        return getCookieString( urlString, null );
    }

    // 將所有cookies串起來
    public static String getCookieString( String urlString, String referURL )
    {
        String[] cookies = getCookieStrings( urlString, referURL );

        String cookie = "";
        for ( int i = 0; i < cookies.length && cookies[i] != null; i++ )
        {
            cookie += cookies[i] + "; ";
        }
        return cookie;
    }

    public static String[] getCookieStrings( String urlString, String referURL )
    {
        String[] tempCookieStrings = null;

        try
        {
            URL url = new URL( urlString );
            HttpURLConnection connection = ( HttpURLConnection ) url.openConnection();
            connection.setRequestProperty( "User-Agent", "User-Agent: Mozilla/5.0 (Windows; U; Windows NT 6.1; zh-TW; rv:1.9.2.8) Gecko/20100722 Firefox/3.6.8" );

            if ( referURL != null )
            {
                connection.setRequestProperty( "Referer", referURL );
            }
            tempCookieStrings = tryConnect( connection );
        }
        catch ( Exception ex )
        {
            Common.hadleErrorMessage( ex, "無法正確設置connection" );
        }



        String[] cookieStrings = tempCookieStrings;
        int cookieCount = 0;
        if ( tempCookieStrings != null )
        {

            for ( int i = 0; i < tempCookieStrings.length; i++ )
            {
                if ( tempCookieStrings[i] != null )
                {
                    cookieStrings[cookieCount] = tempCookieStrings[i]; // 把cookie都集中到前面
                    Common.debugPrintln( cookieCount + ": " + tempCookieStrings[i] );
                    cookieCount++;
                }
            }
            Common.debugPrintln( "共有" + cookieCount + "串cookie" );
        }

        return cookieStrings;
    }

    // 普通下載模式，連線失敗會嘗試再次連線
    public static void downloadFile( String webSite, String outputDirectory, String outputFileName,
                                     boolean needCookie, String cookieString )
    {
        downloadFile( webSite, outputDirectory, outputFileName, needCookie, cookieString, "", false, SetUp.getRetryTimes(), false, false );
    }

    // 普通下載模式，連線失敗會嘗試再次連線
    public static void downloadFile( String webSite, String outputDirectory, String outputFileName,
                                     boolean needCookie, String cookieString, String referURL )
    {
        downloadFile( webSite, outputDirectory, outputFileName, needCookie, cookieString, referURL, false, SetUp.getRetryTimes(), false, false );
    }

    // 解壓縮下載模式，連線失敗會嘗試再次連線，且收取資料串流後會以Gzip解壓縮
    public static void downloadGZIPInputStreamFile( String webSite, String outputDirectory, String outputFileName,
                                                    boolean needCookie, String cookieString )
    {
        downloadFile( webSite, outputDirectory, outputFileName, needCookie, cookieString, "", false, SetUp.getRetryTimes(), true, false );
    }

    // 直接下載模式，不管Run.isAlive值為何都可以直接下載
    public static void downloadFileByForce( String webSite, String outputDirectory, String outputFileName,
                                            boolean needCookie, String cookieString )
    {
        downloadFile( webSite, outputDirectory, outputFileName, needCookie, cookieString, "", false, SetUp.getRetryTimes(), false, true );
    }

    // 加速下載模式，連線失敗就跳過
    public static void downloadFileFast( String webSite, String outputDirectory, String outputFileName,
                                         boolean needCookie, String cookieString )
    {
        downloadFile( webSite, outputDirectory, outputFileName, needCookie, cookieString, "", true, SetUp.getRetryTimes(), false, false );
    }

    // 查看此url是否有轉向其他url
    public static void testConnection( String url )
    {
        try
        {
            URLConnection con = new URL( url ).openConnection();
            Common.debugPrintln( "orignal url: " + con.getURL() );
            con.connect();
            Common.debugPrintln( "connected url: " + con.getURL() );
            InputStream is = con.getInputStream();
            Common.debugPrintln( "redirected url: " + con.getURL() );
            is.close();
        }
        catch ( Exception ex )
        {
            Common.hadleErrorMessage( ex, "無法正確設置connection" );
        }


        try
        {
            HttpURLConnection con = ( HttpURLConnection ) (new URL( url ).openConnection());
            con.setInstanceFollowRedirects( false );
            con.connect();
            int responseCode = con.getResponseCode();
            Common.debugPrintln( "" + responseCode );
            String location = con.getHeaderField( "Location" );
            Common.debugPrintln( location );
        }
        catch ( Exception ex )
        {
            Common.hadleErrorMessage( ex, "無法正確設置connection" );
        }

    }

    public static void downloadFile( String webSite, String outputDirectory, String outputFileName,
                                     boolean needCookie, String cookieString, String referURL, boolean fastMode, int retryTimes,
                                     boolean gzipEncode, boolean forceDownload )
    {
        // downlaod file by URL

        int fileGotSize = 0;

        if ( CommonGUI.stateBarDetailMessage == null )
        {
            CommonGUI.stateBarMainMessage = "下載網頁進行分析 : ";
            CommonGUI.stateBarDetailMessage = outputFileName + " ";
        }

        if ( Run.isAlive || forceDownload )
        { // 當允許下載或強制下載時才執行連線程序
            try
            {

                ComicDownGUI.stateBar.setText( webSite + " 連線中..." );

                // google圖片下載時因為有些連線很久沒回應，所以要設置計時器，預防連線時間過長
                Timer timer = new Timer();
                if ( SetUp.getTimeoutTimer() > 0 )
                {
                    // 預設(getTimeoutTimer()*1000)秒會timeout
                    timer.schedule( new TimeoutTask(), SetUp.getTimeoutTimer() * 1000 );
                }

                URL url = new URL( webSite );
                HttpURLConnection connection = ( HttpURLConnection ) url.openConnection();

                // 偽裝成瀏覽器
                //connection.setRequestProperty( "User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows 2000)" );
                connection.setRequestProperty( "User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.97 Safari/537.11" );
                //connection.setRequestProperty( "User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 6.1; zh-TW; rv:1.9.2.8) Gecko/20100722 Firefox/3.6.8" );

                // connection.setRequestMethod( "GET" ); // 默认是GET 
                //connection.setRequestProperty( "User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows 2000)" );
                //connection.setRequestProperty("User-Agent","Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; InfoPath.1; CIBA)");
                //connection.setFollowRedirects( true );
                //connection.setDoOutput( true ); // 需要向服务器写数据
                connection.setDoInput( true ); //

                // dm5加這一行無法下載...
                //connection.setUseCaches( false ); // // Post 请求不能使用缓存 
                connection.setAllowUserInteraction( false );

                //connection.setInstanceFollowRedirects( false ); // 不轉址


                if ( referURL != null && !referURL.equals( "" ) )
                {
                    //Common.debugPrintln( "設置Referer=" + referURL );
                    connection.setRequestProperty( "Referer", "referURL" );
                }

                // 设定传送的内容类型是可序列化的java对象   
                // (如果不设此项,在传送序列化对象时,当WEB服务默认的不是这种类型时可能抛java.io.EOFException)
                connection.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded" );
                //connection.setRequestProperty( "Accept-Language", "zh-cn" );
                // connection.setRequestProperty("Content-Length", ""+data.length());
                connection.setRequestProperty( "Cache-Control", "no-cache" );
                connection.setRequestProperty( "Pragma", "no-cache" );
                //connection.setRequestProperty( "Host", "biz.finance.sina.com.cn" );
                connection.setRequestProperty( "Accept", "text/html, image/gif, image/jpeg, *; q=.2, */*; q=.2" );
                connection.setRequestProperty( "Connection", "keep-alive" );

                connection.setConnectTimeout( 10000 ); // 與主機連接時間不能超過十秒


                if ( needCookie )
                {
                    connection.setRequestMethod( "GET" );
                    connection.setDoOutput( true );
                    connection.setRequestProperty( "Cookie", cookieString );
                }

                int responseCode = 0;

                // 快速模式不下載青蛙圖！（其檔案大小就是10771......）
                if ( (fastMode && connection.getResponseCode() != 200)
                        || (fastMode && connection.getContentLength() == 10771) )
                {
                    return;
                }

                tryConnect( connection );


                int fileSize = connection.getContentLength() / 1000;

                if ( Common.isPicFileName( outputFileName )
                        && (fileSize == 21 || fileSize == 22) )
                { // 連到99系列的盜連圖
                    Common.debugPrintln( "似乎連到盜連圖，停一秒後重新連線......" );

                    Common.sleep( 1000, "重新下載倒數:" ); // 每次暫停一秒再重新連線

                    tryConnect( connection );
                }
                // 內部伺服器發生錯誤，讀取getErrorStream() 
                if ( connection.getResponseCode() == 500 )
                {
                }
                else if ( connection.getResponseCode() != 200 )
                {
                    //Common.debugPrintln( "第二次失敗，不再重試!" );
                    String errorMessage = "錯誤回傳碼(responseCode): " + connection.getResponseCode();
                    Common.errorReport( errorMessage + " : " + webSite );
                    ComicDownGUI.stateBar.setText( errorMessage + " ----> 無法下載" + webSite );
                    return;
                }

                Common.checkDirectory( outputDirectory ); // 檢查有無目標資料夾，若無則新建一個　

                //OutputStream os = response.getOutputStream();
                OutputStream os = new FileOutputStream( outputDirectory + outputFileName );
                InputStream is = null;

                if ( connection.getResponseCode() == 500 )
                {
                    is = connection.getErrorStream(); // xindm
                }
                else if ( gzipEncode && fileSize < 17 ) // 178漫畫小於17kb就認定為已經壓縮過的
                {
                    try
                    {
                        is = new GZIPInputStream( connection.getInputStream() ); // ex. 178.com
                    }
                    catch ( IOException ex )
                    {
                        is = connection.getInputStream(); // 其他漫畫網
                    }
                }
                else
                {
                    is = connection.getInputStream(); // 其他漫畫網
                }
                Common.debugPrint( "(" + fileSize + " k) " );
                String fileSizeString = fileSize > 0 ? "" + fileSize : " ? ";

                byte[] r = new byte[ 1024 ];
                int len = 0;

                while ( (len = is.read( r )) > 0 && (Run.isAlive || forceDownload) )
                {
                    // 快速模式下，檔案小於1mb且連線超時 -> 切斷連線
                    if ( fileSize > 1024 || !Flag.timeoutFlag ) // 預防卡住的機制
                    {
                        os.write( r, 0, len );
                    }
                    else
                    {
                        break;
                    }

                    fileGotSize += (len / 1000);

                    if ( Common.withGUI() )
                    {
                        int percent = 100;
                        String downloadText = "";
                        if ( fileSize > 0 )
                        {
                            percent = (fileGotSize * 100) / fileSize;
                            downloadText = fileSizeString + "Kb ( " + percent + "% ) ";
                        }
                        else
                        {
                            downloadText = fileSizeString + " Kb ( " + fileGotSize + "Kb ) ";
                        }

                        ComicDownGUI.stateBar.setText( CommonGUI.stateBarMainMessage
                                + CommonGUI.stateBarDetailMessage
                                + " : " + downloadText );
                    }
                }

                is.close();
                os.flush();
                os.close();

                if ( Common.withGUI() )
                {
                    ComicDownGUI.stateBar.setText( CommonGUI.stateBarMainMessage
                            + CommonGUI.stateBarDetailMessage
                            + " : " + fileSizeString + "Kb ( 100% ) " );
                }

                connection.disconnect();


                // 若真實下載檔案大小比預估來得小，則視設定值決定要重新嘗試幾次
                int realFileGotSize = ( int ) new File( outputDirectory + outputFileName ).length() / 1000;
                if ( realFileGotSize + 1 < fileGotSize && retryTimes > 0 )
                {
                    String messageString = realFileGotSize + " < " + fileGotSize
                            + " -> 新下載" + outputFileName + "（" + retryTimes
                            + "/" + SetUp.getRetryTimes() + "） 倒數:";

                    Common.sleep( 2000, messageString ); // 每次暫停兩秒再重新連線

                    downloadFile( webSite, outputDirectory, outputFileName,
                                  needCookie, cookieString, referURL, fastMode, retryTimes - 1, gzipEncode, forceDownload );


                }

                if ( fileSize < 1024 && Flag.timeoutFlag )
                {
                    new File( outputDirectory + outputFileName ).delete();
                    Common.debugPrintln( "刪除不完整檔案：" + outputFileName );

                    ComicDownGUI.stateBar.setText( "下載逾時，跳過" + outputFileName );

                }

                timer.cancel(); // 連線結束同時也關掉計時器

                Flag.timeoutFlag = false; // 歸回初始值

                Common.debugPrintln( webSite + " downloads successful!" ); // for debug

            }
            catch ( Exception e )
            {
                Common.hadleErrorMessage( e, "無法正確下載" + webSite );

                if ( ++retryTimes > 0 )
                { // 即使retryTimes設零，也會重傳一次
                    Flag.downloadErrorFlag = false;
                    downloadFile( webSite, outputDirectory, outputFileName,
                                  needCookie, cookieString, referURL, fastMode, retryTimes - 1, gzipEncode, forceDownload );
                }
                Flag.downloadErrorFlag = true;
            }

            CommonGUI.stateBarDetailMessage = null;
        }
    }

    public static boolean urlIsOK( String urlString )
    {

        boolean isOK = false;
        try
        {

            URL url = new URL( urlString );

            HttpURLConnection connection = ( HttpURLConnection ) url.openConnection();
            connection.setRequestProperty( "User-Agent", "User-Agent: Mozilla/5.0 (Windows; U; Windows NT 6.1; zh-TW; rv:1.9.2.8) Gecko/20100722 Firefox/3.6.8" );

            connection.connect();

            if ( connection.getResponseCode() == HttpURLConnection.HTTP_OK )
            {
                isOK = true;
                Common.debugPrintln( urlString + " 測試連線結果: OK" );
            }
            else
            {
                isOK = false;
                Common.debugPrintln( urlString + " 測試連線結果: 不OK ( " + connection.getResponseCode() + " )" );
            }
            connection.disconnect();

        }
        catch ( Exception e )
        {
            Common.hadleErrorMessage( e, "無法正確設置connection" );
        }
        return isOK;
    }

    public static String[] tryConnect( HttpURLConnection connection )
    {
        return tryConnect( connection, null );
    }

    public static String[] tryConnect( HttpURLConnection connection, String postString )
    {
        String[] cookieStrings = new String[ 100 ];
        try
        {
            connection.connect();
            String headerName = "";
            for ( int i = 1; (headerName = connection.getHeaderFieldKey( i )) != null; i++ )
            {
                if ( headerName.equals( "Set-Cookie" ) )
                {

                    cookieStrings[i - 1] = new String( connection.getHeaderField( i ) );
                    //Common.debugPrintln( i + " " + cookieStrings[i-1] );
                }
                else
                {
                    if ( headerName.matches( "Content-Length" ) )
                    {
                        Common.debugPrintln( headerName + " = " + connection.getHeaderField( i ) );
                    }
                    //Common.debugPrintln( headerName + " = " + connection.getHeaderField( i ) );
                }
            }
        }
        catch ( Exception ex )
        {
            try
            {
                if ( connection.getResponseCode() != 200 && !Flag.timeoutFlag )
                {
                    Common.sleep( 1000, "重新嘗試連線倒數:" ); // 每次暫停一秒再重新連線

                    if ( Common.withGUI() )
                    {
                        ComicDownGUI.stateBar.setText( "重新嘗試連線......" );
                        connection.connect(); // 第二次嘗試連線
                    }
                }
            }
            catch ( Exception exx )
            {
                exx.printStackTrace();
            }
        }
        return cookieStrings;
    }

    public static int getDownloadFileLength( String fileURL )
    {
        URL url;
        int length = 0;

        try
        {
            url = new URL( fileURL );
            HttpURLConnection connection = ( HttpURLConnection ) url.openConnection();

            length = connection.getContentLength();

            Common.debugPrintln( fileURL + " 的長度: " + length );
        }
        catch ( Exception ex )
        {
            Logger.getLogger( Common.class.getName() ).log( Level.SEVERE, null, ex );
        }

        return length;
    }

    public static boolean isLegalURL( String webSite )
    {

        boolean theURLisLegal = true;

        try
        {
            URL url = new URL( webSite );
        }
        catch ( MalformedURLException ex )
        {
            theURLisLegal = false;
        }

        //String regex = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+\\+&@!#/%=~_|]";

        if ( theURLisLegal )
        {
            return true;
        }
        else
        {
            return false;
        }

    }

    // -----------------------------------
    public static void compress( File source, File destination )
    { //  compress to zip
        try
        {
            // Deflater.NO_COMPRESSION: 沒有壓縮，僅儲存
            compress( source, destination, null, Deflater.NO_COMPRESSION );
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
    }

    public static void compress( File source, File destination,
                                 String comment, int level ) throws IOException
    {
        ZipOutputStream zos = new ZipOutputStream( new FileOutputStream( destination ) );
        zos.setComment( comment );
        zos.setLevel( level );
        compress( zos, source.getParent(), source );
        zos.flush();
        zos.close();
    }

    private static void compress( ZipOutputStream zos, String rootpath,
                                  File source ) throws IOException
    {
        // 下面這行原本用來取得壓縮檔中的圖片資料夾名稱，但會有亂碼，所以直接放外面。
        //String filename = source.toString().substring(rootpath.length() + 1);
        if ( source.isFile() )
        {
            ZipEntry zipEntry = new ZipEntry( source.getName() );//filename );
            zos.putNextEntry( zipEntry );
            FileInputStream fis = new FileInputStream( source );
            byte[] buffer = new byte[ 1024 ];
            for ( int length; (length = fis.read( buffer )) > 0; )
            {
                zos.write( buffer, 0, length );
            }
            fis.close();
            zos.closeEntry();
        }
        else if ( source.isDirectory() )
        {
            // 下面這三行是把資料夾加入到壓縮檔裡面，因為有亂碼，所以拿掉。
            //ZipEntry zipEntry = new ZipEntry( filename + "/" );
            //zos.putNextEntry( zipEntry );
            //zos.closeEntry();
            File[] files = source.listFiles();
            for ( File file : files )
            {
                compress( zos, rootpath, file );
            }
        }
    }

    public static void deleteFolder( String folderPath )
    {
        Common.debugPrintln( "刪除資料夾：" + folderPath );

        try
        {
            deleteAllFile( folderPath ); // delete all the file in dir
            String filePath = folderPath;
            filePath = filePath.toString();
            File myFilePath = new File( filePath );
            myFilePath.delete(); // delete empty dir
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
    }

    public static boolean deleteAllFile( String path )
    {
        boolean flag = false;
        File file = new File( path );
        if ( !file.exists() )
        {
            return flag;
        }

        if ( !file.isDirectory() )
        {
            return flag;
        }

        String[] tempList = file.list();
        File temp = null;
        for ( int i = 0; i < tempList.length; i++ )
        {
            if ( path.endsWith( File.separator ) )
            {
                temp = new File( path + tempList[i] );
            }
            else
            {
                temp = new File( path + File.separator + tempList[i] );
            }

            if ( temp.isFile() )
            {
                temp.delete();
            }
            if ( temp.isDirectory() )
            {
                deleteAllFile( path + "/" + tempList[i] ); // first delete all files in dir
                deleteFolder( path + "/" + tempList[i] ); // and then delete the dir
                flag = true;
            }
        }
        return flag;
    }

    public static BufferedReader getBufferedReader( String filePath ) throws IOException
    {
        FileReader fr = new FileReader( filePath );
        return new BufferedReader( fr );
    }

    public static void outputFile( String outputText, String outputFile )
    {
        int index = outputFile.lastIndexOf( Common.getSlash() );
        String filePath = outputFile.substring( 0, index + 1 );
        String fileName = outputFile.substring( index + 1, outputFile.length() );

        outputFile( outputText, filePath, fileName );
    }

    public static void outputFile( String ouputText, String filePath, String fileName )
    {
        checkDirectory( filePath );

        try
        {
            FileOutputStream fout = new FileOutputStream( filePath + fileName );
            DataOutputStream dataout = new DataOutputStream( fout );
            byte[] data1 = ouputText.getBytes( "UTF-8" );
            dataout.write( data1 );
            fout.close();
            Common.debugPrintln( "寫出 " + filePath + fileName + " 檔案" );
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }
    }

    public static void outputFile( String[] outputStrings, String filePath, String fileName )
    {

        if ( outputStrings != null )
        {
            StringBuffer sb = new StringBuffer();
            for ( int i = 0; i < outputStrings.length; i++ )
            {
                sb.append( outputStrings[i] + "\n" );
            }

            outputFile( sb.toString(), filePath, fileName );
        }
    }

    public static void outputFile( List outputList, String filePath, String fileName )
    {
        StringBuffer sb = new StringBuffer();
        for ( int i = 0; i < outputList.size(); i++ )
        {
            sb.append( outputList.get( i ) + "\n" );
        }

        outputFile( sb.toString(), filePath, fileName );
    }

    public static void outputUrlFile( String[] urlStrings, String oldDownloadPath )
    {
        String[] dirStrings = oldDownloadPath.split( "[\\\\]|/" );

        String urlFileName = dirStrings[dirStrings.length - 1] + "_urls.txt";
        String downloadPath = "";

        for ( int i = 0; i < dirStrings.length - 1; i++ )
        {
            downloadPath += dirStrings[i] + "/";
        }

        Common.processPrint( "輸出位址文件檔: " + urlFileName );
        outputFile( urlStrings, downloadPath, urlFileName );
    }

    // 讀取file的全部文字內容並回傳
    public static String getFileString( String file )
    {
        int index = file.lastIndexOf( Common.getSlash() );
        String filePath = file.substring( 0, index + 1 );
        String fileName = file.substring( index + 1, file.length() );

        return getFileString( filePath, fileName );
    }

    public static String getFileString( String filePath, String fileName )
    {
        String str = "";
        StringBuffer sb = new StringBuffer( "" );

        if ( new File( filePath + fileName ).exists() )
        {
            try
            {
                FileInputStream fileInputStream = new FileInputStream( filePath + fileName );

                InputStreamReader inputStreamReader = new InputStreamReader( fileInputStream, "UTF8" );

                int ch = 0;
                while ( (ch = inputStreamReader.read()) != -1 )
                {
                    sb.append( ( char ) ch );
                }

                fileInputStream.close(); // 加這句才能讓official.html刪除，還在實驗中

            }
            catch ( IOException e )
            {
                Common.hadleErrorMessage( e, "無法讀入" + filePath + fileName );
                e.printStackTrace();
            }
        }
        else
        {
            Common.debugPrintln( "沒有找到" + filePath + fileName + "此一檔案" );
        }

        return sb.toString();
    }

    public static String[] getFileStrings( String filePath, String fileName )
    {
        String[] tempStrings = getFileString( filePath, fileName ).split( "\\n|\\r" );

        return tempStrings;
        //return correctStrings;
    }

    public static String GBK2Unicode( String str )
    {
        StringBuffer result = new StringBuffer();
        for ( int i = 0; i < str.length(); i++ )
        {
            char chr1 = str.charAt( i );
            if ( !isNeedConvert( chr1 ) )
            {
                result.append( chr1 );
                continue;
            }
            result.append( "&#x" + Integer.toHexString( ( int ) chr1 ) + ";" );
        }
        return result.toString();
    }

    public static boolean isNeedConvert( char para )
    {
        return ((para & (0x00FF)) != para);
    }

    // 尚未做完英文介面 先留著
    //public static String getStringUsingDefaultLanguage( String string ) {
    //    return Common.getStringUsingDefaultLanguage( string, string ) ;
    //}
    public static String getStringUsingDefaultLanguage( String string, String enString )
    {
        if ( SetUp.getDefaultLanguage() == LanguageEnum.ENGLISH )
        {
            return enString;
        }
        else if ( SetUp.getDefaultLanguage() == LanguageEnum.SIMPLIFIED_CHINESE )
        {
            return Common.getSimplifiedChinese( string );
        }
        else
        {
            return string;
        }
    }

    // v4.10: 不知道為什麼破折號都會顯示為��，因此做了暫時的補救措施......
    public static String getTraditionalChinese( String gbString )
    {
        // Simplified Chinese To Traditional Chinese
        Zhcode mycode = new Zhcode();

        if ( SetUp.getDefaultLanguage() == LanguageEnum.TRADITIONAL_CHINESE )
        {
            return mycode.convertString( gbString, mycode.GB2312, mycode.BIG5 ).replaceAll( "[\\\\]ufffd", "_" ).replaceAll( "��", "——" );
        }
        else
        {
            return gbString;
        }
    }

    public static String getSimplifiedChinese( String big5String )
    {
        Zhcode mycode = new Zhcode();
        String gbString = mycode.convertString( big5String, mycode.BIG5, mycode.GB2312 );
        return gbString.replace( "\\u51ea", "止" ).replace( "\\u9ed2", "黑" );
    }

    public static String getUtf8toUnicode( String utf8 )
    {
        Zhcode mycode = new Zhcode();
        return mycode.convertString( utf8, mycode.UTF8, mycode.UNICODE );
    }

    public static String getUtf8toBig5( String utf8 )
    {
        Zhcode mycode = new Zhcode();
        return mycode.convertString( utf8, mycode.UTF8, mycode.BIG5 );
    }

    public static String getUtf8toGB2312( String utf8 )
    {
        Zhcode mycode = new Zhcode();
        return mycode.convertString( utf8, mycode.UTF8, mycode.GB2312 );
    }

    public static String getBig5toUtf8( String big5 )
    {
        Zhcode mycode = new Zhcode();
        return mycode.convertString( big5, mycode.BIG5, mycode.UTF8 );
    }

    public static String getGB2312toUtf8( String gb )
    {
        Zhcode mycode = new Zhcode();
        return mycode.convertString( gb, mycode.BIG5, mycode.UTF8 );
    }

    public static void newEncodeFile( String directory, String fileName, String encodeFileName )
    {
        Common.newEncodeFile( directory, fileName, encodeFileName, Zhcode.GB2312 );
    }

    public static void newEncodeFile( String directory, String fileName, String encodeFileName, int inputCode )
    {


        Zhcode mycode = new Zhcode();
        mycode.convertFile( directory + fileName,
                            directory + encodeFileName,
                            inputCode,
                            mycode.UTF8 );
    }

    public static void newEncodeFile( String directory, String fileName,
                                      String encodeFileName, int inputCode, int outputCode )
    {
        Zhcode mycode = new Zhcode();
        mycode.convertFile( directory + fileName,
                            directory + encodeFileName,
                            inputCode,
                            outputCode );
    }

    public static String getConnectStrings( String[] strings )
    {
        String str = "";

        for ( int i = 0; i < strings.length; i++ )
        {
            str += strings[i] + "####";
        }

        return str;
    }

    public static String[] getSeparateStrings( String connectString )
    {
        return connectString.split( "####" );
    }

    public static int getTrueCountFromStrings( String[] strings )
    {
        int count = 0;
        for ( String str : strings )
        {
            if ( str.equals( "true" ) )
            {
                count++;
            }
        }
        return count;
    }

    public static String[] getCopiedStrings( String[] copiedStrings )
    {
        String[] newStrings = new String[ copiedStrings.length ];

        for ( int i = 0; i < copiedStrings.length; i++ )
        {
            newStrings[i] = copiedStrings[i];
        }

        return newStrings;
    }

    public static String getStringReplaceHttpCode( String oldString )
    {
        String string = oldString;
        string = string.replace( "&#039;", "'" );
        string = string.replace( "&lt;", "＜" );
        string = string.replace( "&gt;", "＞" );
        string = string.replace( "&amp;", "&" );
        string = string.replace( "&nbsp;", "　" );
        string = string.replace( "&quot;", "''" );
        string = string.replaceAll( "&#8226;", "•" );
        string = string.replaceAll( "&#9829;", "♥" );

        return string;
    }

    public static String getStringRemovedIllegalChar( String oldString )
    {
        // "\/:*?"<>|"
        oldString = getStringReplaceHttpCode( oldString ); // 先經過html字符編碼轉換
        String newString = "";

        for ( int i = 0; i < oldString.length(); i++ )
        {
            if ( oldString.charAt( i ) == '\\'
                    || oldString.charAt( i ) == '/'
                    || oldString.charAt( i ) == '*'
                    || oldString.charAt( i ) == '"'
                    || oldString.charAt( i ) == '<'
                    || oldString.charAt( i ) == '>'
                    || oldString.charAt( i ) == '|' )
            {

                newString += String.valueOf( '_' );
            }
            else if ( oldString.charAt( i ) == '.' )
            {
                newString += String.valueOf( '‧' );
            }
            else if ( oldString.charAt( i ) == '?'
                    || oldString.charAt( i ) == ':' )
            {
                newString += String.valueOf( ' ' );
            }
            else if ( oldString.charAt( i ) == '&' )
            {
                newString += String.valueOf( '＆' );
            }
            else if ( oldString.charAt( i ) == '\'' )
            {
            }
            else
            {
                newString += String.valueOf( oldString.charAt( i ) );
            }
        }

        return Common.getReomvedUnnecessaryWord( newString );
    }

    public static String getReomvedUnnecessaryWord( String title )
    {

        if ( title.matches( "(?s).+九九漫畫" ) ) // 拿掉多餘字尾
        {
            title = title.substring( 0, title.length() - 4 );
        }
        else if ( title.matches( "(?s).+手機漫畫" ) ) // 拿掉多餘字尾
        {
            title = title.substring( 0, title.length() - 4 );
        }
        else if ( title.matches( "(?s).+第一漫畫" ) ) // 拿掉多餘字尾
        {
            title = title.substring( 0, title.length() - 4 );
        }
        else if ( title.matches( "(?s).+漫畫" ) ) // 拿掉[漫畫]字尾
        {
            title = title.substring( 0, title.length() - 2 );
        }
        else if ( title.matches( "在線 (?s).+" ) ) // 拿掉[在線 ]字尾
        {
            title = title.substring( 3, title.length() );
        }

        return title.trim();
    }

    public static boolean withGUI()
    { // check the running app is GUI version or console version
        if ( Thread.currentThread().getName().equals( consoleThreadName ) )
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    public static String getStoredFileName( String outputDirectory,
                                            String defaultFileName,
                                            String defaultExtensionName )
    {
        int indexNameNo = 0;
        boolean over = false;
        while ( over )
        {
            File tempFile = new File( outputDirectory + defaultFileName
                    + indexNameNo + "." + defaultExtensionName );
            if ( tempFile.exists() && (!tempFile.canRead() || !tempFile.canWrite()) )
            {
                indexNameNo++;
            }
            else
            {
                over = true;
            }
        }

        return defaultFileName + indexNameNo + "." + defaultExtensionName;
    }

    public static String getAbsolutePath( String relativePath )
    {
        return new File( relativePath ).getAbsolutePath();
    }

    public static boolean isWindows()
    { // windows
        String os = System.getProperty( "os.name" ).toLowerCase();
        return (os.indexOf( "win" ) >= 0);
    }

    public static boolean isMac()
    { // Mac
        String os = System.getProperty( "os.name" ).toLowerCase();
        return (os.indexOf( "mac" ) >= 0);
    }

    public static boolean isUnix()
    { // linux or unix
        String os = System.getProperty( "os.name" ).toLowerCase();
        return (os.indexOf( "nix" ) >= 0 || os.indexOf( "nux" ) >= 0);

    }

    public static String getSlash()
    {
        if ( Common.isWindows() )
        {
            return "\\";
        }
        else
        {
            return "/";
        }
    }

    public static String getRegexSlash()
    { // \\要轉為\\\\
        if ( Common.isWindows() )
        {
            return "\\\\";
        }
        else
        {
            return "/";
        }
    }

    // 取得string中第order個keyword的位置
    public static int getIndexOfOrderKeyword( String string, String keyword, int order )
    {
        int index = 0;
        for ( int i = 0; i < order && index >= 0; i++ )
        {
            index++;
            index = string.indexOf( keyword, index );
        }

        return index;
    }

    // 取得string中從beginIndex開始數起來第order個keyword的位置
    public static int getIndexOfOrderKeyword( String string, String keyword, int order, int beginIndex )
    {
        String newString = string.substring( beginIndex, string.length() );

        int index = 0;
        for ( int i = 0; i < order && index >= 0; i++ )
        {
            index++;
            index = newString.indexOf( keyword, index );
        }

        return index;
    }

    // 找出從beginIndex開始，keyword1和keyword2在string中的位置（index），並回傳較小的index
    public static int getSmallerIndexOfTwoKeyword( String string, int beginIndex, String keyword1, String keyword2 )
    {
        int index1 = string.indexOf( keyword1, beginIndex );
        int index2 = string.indexOf( keyword2, beginIndex );

        if ( index1 < 0 )
        { // 沒找到keyword1
            return index2;
        }
        else if ( index2 < 0 )
        { // 沒找到keyword2
            return index1;
        }
        else
        {
            return index1 < index2 ? index1 : index2;
        }
    }

    // 找出keyword1和keyword2在string中的位置（index），並回傳較大的index
    public static int getBiggerIndexOfTwoKeyword( String string, String keyword1, String keyword2 )
    {
        int index1 = string.lastIndexOf( keyword1 );
        int index2 = string.lastIndexOf( keyword2 );

        if ( index1 < 0 )
        {
            return index2;
        }
        else if ( index2 < 0 )
        {
            return index1;
        }
        else
        {
            return index1 > index2 ? index1 : index2;
        }
    }

    // 寫出目前的下載任務清單
    public static void outputDownTableFile( DownloadTableModel downTableModel )
    {
        StringBuffer sb = new StringBuffer();
        for ( int row = 0; row < Common.missionCount; row++ )
        {
            // 有勾選下載才會儲存！ -> 即使沒有勾選還是儲存！
            //if ( downTableModel.getValueAt( row, DownTableEnum.YES_OR_NO ).toString().equals( "true" ) ) {
            if ( SetUp.getKeepUndoneDownloadMission() )
            { // 保存未完成任務
                if ( !downTableModel.getValueAt( row, DownTableEnum.STATE ).toString().equals( "下載完畢" ) )
                {
                    for ( int col = 0; col < ComicDownGUI.getDownloadColumns().size(); col++ )
                    {
                        sb.append( downTableModel.getRealValueAt( row, col ).toString() );
                        sb.append( "@@@@@@" );
                    }
                    sb.append( ComicDownGUI.downTableUrlStrings[row] );
                    sb.append( "%%%%%%" );
                }
            }
            if ( SetUp.getKeepDoneDownloadMission() )
            { // 保存已完成任務
                if ( downTableModel.getValueAt( row, DownTableEnum.STATE ).toString().equals( "下載完畢" ) )
                {
                    for ( int col = 0; col < ComicDownGUI.getDownloadColumns().size(); col++ )
                    {
                        sb.append( downTableModel.getRealValueAt( row, col ).toString() );
                        sb.append( "@@@@@@" );
                    }
                    sb.append( ComicDownGUI.downTableUrlStrings[row] );
                    sb.append( "%%%%%%" );
                }
            }
            //}
        }

        sb.append( "_OVER_" );
        outputFile( sb.toString(), SetUp.getRecordFileDirectory(), "downloadList.dat" );
    }

    // 寫出目前的書籤清單
    public static void outputBookmarkTableFile( BookmarkTableModel bookmarkTableModel )
    {
        StringBuffer sb = new StringBuffer();
        for ( int row = 0; row < Common.bookmarkCount; row++ )
        {
            if ( SetUp.getKeepBookmark() )
            { // 保存書籤
                for ( int col = 0; col < ComicDownGUI.getBookmarkColumns().size(); col++ )
                {
                    sb.append( bookmarkTableModel.getValueAt( row, col ).toString() );
                    sb.append( "@@@@@@" );
                }
                sb.append( String.valueOf( bookmarkTableModel.getValueAt( row, RecordTableEnum.URL ) ) );
                sb.append( "%%%%%%" );
            }
        }

        sb.append( "_OVER_" );
        outputFile( sb.toString(), SetUp.getRecordFileDirectory(), "bookmarkList.dat" );
    }

    // 寫出目前的記錄清單
    public static void outputRecordTableFile( RecordTableModel recordTableModel )
    {
        StringBuffer sb = new StringBuffer();
        for ( int row = 0; row < Common.recordCount; row++ )
        {
            if ( SetUp.getKeepRecord() )
            { // 保存記錄
                for ( int col = 0; col < ComicDownGUI.getRecordColumns().size(); col++ )
                {
                    sb.append( recordTableModel.getValueAt( row, col ).toString() );
                    sb.append( "@@@@@@" );
                }
                sb.append( recordTableModel.getValueAt( row, RecordTableEnum.URL ).toString() );
                sb.append( "%%%%%%" );
            }
        }

        sb.append( "_OVER_" );
        outputFile( sb.toString(), SetUp.getRecordFileDirectory(), "recordList.dat" );
    }

    // 讀入之前儲存的下載任務清單
    public static DownloadTableModel inputDownTableFile()
    {
        String dataString = getFileString( SetUp.getRecordFileDirectory(), "downloadList.dat" );

        if ( !dataString.matches( "\\s*_OVER_\\s*" ) )
        { // 之前有記錄下載清單
            String[] rowStrings = dataString.split( "%%%%%%" );
            Common.debugPrint( "將讀入下載任務數量: " + (rowStrings.length - 1) );
            DownloadTableModel downTableModel = new DownloadTableModel( ComicDownGUI.getDownloadColumns(),
                                                                        rowStrings.length - 1 );
            try
            {
                for ( int row = 0; row < rowStrings.length - 1; row++ )
                {
                    String[] colStrings = rowStrings[row].split( "@@@@@@" );

                    for ( int col = 0; col < ComicDownGUI.getDownloadColumns().size(); col++ )
                    {
                        if ( col == DownTableEnum.YES_OR_NO )
                        {
                            downTableModel.setValueAt( Boolean.valueOf( colStrings[col] ), row, col );
                        }
                        else if ( col == DownTableEnum.ORDER )
                        {
                            downTableModel.setValueAt( new Integer( row + 1 ), row, col );
                        }
                        else
                        {
                            downTableModel.setValueAt( colStrings[col], row, col );
                        }
                    }
                    ComicDownGUI.downTableUrlStrings[row] = colStrings[ComicDownGUI.getDownloadColumns().size()];
                    Common.missionCount++;

                }
                Common.debugPrintln( "   ... 讀入完畢!!" );
            }
            catch ( Exception ex )
            {
                Common.hadleErrorMessage( ex, "無法讀入下載清單" );
                cleanDownTable();
                new File( "downloadList.dat" ).delete();
            }


            return downTableModel;
        }
        else
        {
            return new DownloadTableModel( ComicDownGUI.getDownloadColumns(), 0 );
        }

    }

    // 讀入之前儲存的書籤清單
    public static BookmarkTableModel inputBookmarkTableFile()
    {
        String dataString = getFileString( SetUp.getRecordFileDirectory(), "bookmarkList.dat" );

        if ( !dataString.matches( "\\s*_OVER_\\s*" ) )
        { // 之前有記錄下載清單
            String[] rowStrings = dataString.split( "%%%%%%" );
            Common.debugPrint( "將讀入書籤數量: " + (rowStrings.length - 1) );
            BookmarkTableModel tableModel = new BookmarkTableModel( ComicDownGUI.getBookmarkColumns(),
                                                                    rowStrings.length - 1 );
            try
            {
                for ( int row = 0; row < rowStrings.length - 1; row++ )
                {
                    String[] colStrings = rowStrings[row].split( "@@@@@@" );

                    for ( int col = 0; col < ComicDownGUI.getBookmarkColumns().size(); col++ )
                    {
                        //Common.debugPrint( colStrings[col] + " " );
                        if ( col == BookmarkTableEnum.ORDER )
                        {
                            tableModel.setValueAt( new Integer( row + 1 ), row, col );
                        }
                        else
                        {
                            tableModel.setValueAt( colStrings[col], row, col );
                        }
                    }
                    Common.bookmarkCount++;

                    //Common.debugPrintln( " 讀取OK！" );
                }
                Common.debugPrintln( "   ... 讀入完畢!!" );
            }
            catch ( Exception ex )
            {
                Common.hadleErrorMessage( ex, "無法讀入書籤清單" );
            }

            return tableModel;
        }
        else
        {
            return new BookmarkTableModel( ComicDownGUI.getBookmarkColumns(), 0 );
        }

    }

    // 讀入之前儲存的記錄清單
    public static RecordTableModel inputRecordTableFile()
    {
        String dataString = getFileString( SetUp.getRecordFileDirectory(), "recordList.dat" );

        if ( !dataString.matches( "\\s*_OVER_\\s*" ) )
        { // 之前有記錄下載清單
            String[] rowStrings = dataString.split( "%%%%%%" );
            Common.debugPrint( "將讀入記錄數量: " + (rowStrings.length - 1) );
            RecordTableModel tableModel = new RecordTableModel( ComicDownGUI.getRecordColumns(),
                                                                rowStrings.length - 1 );
            try
            {
                for ( int row = 0; row < rowStrings.length - 1; row++ )
                {
                    String[] colStrings = rowStrings[row].split( "@@@@@@" );

                    for ( int col = 0; col < ComicDownGUI.getRecordColumns().size(); col++ )
                    {
                        //Common.debugPrint( colStrings[col] + " " );

                        if ( col == RecordTableEnum.ORDER )
                        {
                            tableModel.setValueAt( new Integer( row + 1 ), row, col );
                        }
                        else
                        {
                            tableModel.setValueAt( colStrings[col], row, col );
                        }
                    }
                    Common.recordCount++;

                    //Common.debugPrintln( " 讀取OK！" );
                }
                Common.debugPrintln( "   ... 讀入完畢!!" );
            }
            catch ( Exception ex )
            {
                Common.hadleErrorMessage( ex, "無法讀入紀錄清單" );
            }

            return tableModel;
        }
        else
        {
            return new RecordTableModel( ComicDownGUI.getRecordColumns(), 0 );
        }

    }

    public static void deleteFile( String filePath, String fileName )
    {
        File file = new File( filePath + fileName );

        if ( file.exists() && file.isFile() )
        {
            Common.debugPrintln( "刪除暫存檔案：" + fileName );
            file.delete();
        }
    }

    public static void deleteFile( String fileName )
    {
        File file = new File( fileName );

        if ( file.exists() && file.isFile() )
        {
            Common.debugPrintln( "刪除暫存檔案：" + fileName );
            file.delete();
        }
    }

    public static void setHttpProxy()
    {
        if ( SetUp.getProxyServer() != null
                && !SetUp.getProxyServer().equals( "" )
                && SetUp.getProxyPort() != null
                && !SetUp.getProxyPort().equals( "" ) )
        {
            Common.setHttpProxy( SetUp.getProxyServer(), SetUp.getProxyPort() );
            Common.debugPrintln( "設定代理伺服器："
                    + SetUp.getProxyServer() + " "
                    + SetUp.getProxyPort() );
        }
        else
        {
            Common.closeHttpProxy();
            Common.debugPrintln( "代理伺服器資訊欠缺位址或連接阜，因此不加入" );
        }
    }

    public static void setHttpProxy( String proxyServer, String proxyPort )
    {
        Properties systemProperties = System.getProperties();
        systemProperties.setProperty( "proxySet", "true" );
        systemProperties.setProperty( "http.proxyHost", proxyServer );
        systemProperties.setProperty( "http.proxyPort", proxyPort );
    }

    public static void closeHttpProxy()
    {
        Properties systemProperties = System.getProperties();
        systemProperties.setProperty( "proxySet", "false" );
    }

    public static boolean isPicFileName( String fileName )
    {
        if ( fileName.matches( "(?s).*\\.jpg" )
                || fileName.matches( "(?s).*\\.JPG" )
                || fileName.matches( "(?s).*\\.png" )
                || fileName.matches( "(?s).*\\.PNG" )
                || fileName.matches( "(?s).*\\.gif" )
                || fileName.matches( "(?s).*\\.GIF" )
                || fileName.matches( "(?s).*\\.jpeg" )
                || fileName.matches( "(?s).*\\.JPEG" )
                || fileName.matches( "(?s).*\\.bmp" )
                || fileName.matches( "(?s).*\\.BMP" ) )
        {
            return true;
        }
        else
        {
            return false;
        }

    }

    // direcotory裏面第p張圖片是否存在
    public static boolean existPicFile( String directory, int p )
    {
        NumberFormat formatter = new DecimalFormat( Common.getZero() );
        String fileName = formatter.format( p );
        if ( new File( directory + fileName + ".jpg" ).exists()
                || new File( directory + fileName + ".JPG" ).exists()
                || new File( directory + fileName + ".png" ).exists()
                || new File( directory + fileName + ".PNG" ).exists()
                || new File( directory + fileName + ".gif" ).exists()
                || new File( directory + fileName + ".GIF" ).exists()
                || new File( directory + fileName + ".jpeg" ).exists()
                || new File( directory + fileName + ".JPEG" ).exists()
                || new File( directory + fileName + ".bmp" ).exists()
                || new File( directory + fileName + ".BMP" ).exists() )
        {
            return true;
        }
        else
        {
            return false;
        }

    }

    // direcotory裏面第begin張到第end張圖片是否存在
    public static boolean existPicFile( String directory, int begin, int end )
    {
        for ( int i = begin; i <= end; i++ )
        {
            if ( !Common.existPicFile( directory, i ) )
            {
                return false;
            }
        }
        return true;
    }

    public static void cleanDownTable()
    {
        DefaultTableModel table = ComicDownGUI.downTableModel;
        if ( table != null )
        {
            int downListCount = table.getRowCount();
            while ( table.getRowCount() > 1 )
            {
                table.removeRow( table.getRowCount() - 1 );
                Common.missionCount--;
            }
            if ( Common.missionCount > 0 )
            {
                table.removeRow( 0 );
            }
            ComicDownGUI.mainFrame.repaint(); // 重繪

            Common.missionCount = 0;
            Common.processPrintln( "因讀入錯誤，將全部任務清空" );
            ComicDownGUI.stateBar.setText( "下載任務檔格式錯誤，無法讀取!!" );
        }
    }

    public static String getHtmlStringWithColor( String string, String color )
    {
        return "<html><font color=" + color + string + "</font></html>";
    }

    // 字串A裡面有幾個字串B
    public static int getAmountOfString( String aString, String bString )
    {
        int bLength = bString.length();

        int conformTimes = 0; // 符合次數
        for ( int i = 0; i < aString.length(); i += bLength )
        {
            if ( aString.substring( i, i + bLength ).equals( bString ) )
            {
                conformTimes++;
            }
        }

        //Common.debugPrintln( bString + "符合次數: " + conformTimes );

        return conformTimes;
    }

    public static String getNowAbsolutePath()
    {
        if ( Common.isUnix() )
        {

            String apath = Common.class.getProtectionDomain().getCodeSource().getLocation().getPath();
            try
            {
                apath = URLDecoder.decode( apath, "UTF-8" );
            }
            catch ( UnsupportedEncodingException ex )
            {
                Common.hadleErrorMessage( ex, "無法將網址轉為utf8編碼" );
            }


            String absolutePath;
            if ( apath.endsWith( ".jar" ) )
            {
                absolutePath = apath.replaceAll( "([^/\\\\]+).jar$", "" );
            }
            else
            {
                absolutePath = new File( "" ).getAbsolutePath() + Common.getSlash();
            }

            return absolutePath;
        }
        else
        {
            return new File( "" ).getAbsolutePath() + getSlash();
        }

    }

    // 播放音效
    public static void playSingleDoneAudio()
    {
        playSingleDoneAudio( SetUp.getSingleDoneAudioFile() );
    }

    public static void playSingleDoneAudio( String fileString )
    {
        if ( new File( fileString ).exists() )
        {
            playAudio( fileString, false );
        }
        else
        {
            playAudio( Common.defaultSingleDoneAudio, true );
        }
    }

    public static void playAllDoneAudio()
    {
        playAllDoneAudio( SetUp.getAllDoneAudioFile() );
    }

    public static void playAllDoneAudio( String fileString )
    {
        if ( new File( fileString ).exists() )
        {
            playAudio( fileString, false );
        }
        else
        {
            playAudio( Common.defaultAllDoneAudio, true );
        }
    }

    // 播放音效
    private static void playAudio( final String audioFileString, final boolean defaultResource )
    {
        Thread playThread = new Thread( new Runnable()
        {

            public void run()
            {
                try
                {
                    AudioInputStream ais;

                    if ( defaultResource )
                    { // 預設音效
                        URL audioFileURL = new CommonGUI().getResourceURL( audioFileString );
                        ais = AudioSystem.getAudioInputStream( audioFileURL );
                    }
                    else
                    { // 外部音效
                        File audioFile = new File( audioFileString );
                        ais = AudioSystem.getAudioInputStream( audioFile );
                    }

                    AudioFormat af = ais.getFormat();
                    DataLine.Info inf = new DataLine.Info( SourceDataLine.class, af );
                    SourceDataLine sdl = ( SourceDataLine ) AudioSystem.getLine( inf );
                    sdl.open( af );
                    sdl.start();
                    byte[] buf = new byte[ 65536 ];
                    for ( int n = 0; (n = ais.read( buf, 0, buf.length )) > 0; )
                    {
                        sdl.write( buf, 0, n );
                    }
                    sdl.drain();
                    sdl.close();
                }
                catch ( Exception e )
                {
                    Common.hadleErrorMessage( e, "無法播放" + audioFileString );
                }
            }
        } );
        playThread.start();
    }

    static public String getRegularURL( String url )
    {


        try
        {
            url = URLEncoder.encode( url, "UTF-8" );
        }
        catch ( UnsupportedEncodingException ex )
        {
            Common.errorReport( "無法轉換網址：" + url );
        }
        return Common.unescape( url );
    }

    static public String getFixedChineseURL( String url )
    {
        // ex. "收?的十二月" should be changed into
        //     "%E6%94%B6%E8%8E%B7%E7%9A%84%E5%8D%81%E4%BA%8C%E6%9C%88"

        try
        {
            String temp = "";

            for ( int k = 0; k < url.length(); k++ )
            {
                // \u0080-\uFFFF -> 中日韓3byte以上的字符
                if ( url.substring( k, k + 1 ).matches( "(?s).*[\u0080-\uFFFF]+(?s).*" ) )
                {
                    temp += URLEncoder.encode( url.substring( k, k + 1 ), "UTF-8" );
                }
                else
                {
                    temp += url.substring( k, k + 1 );
                }

            }
            url = temp;
        }
        catch ( Exception e )
        {
            Common.hadleErrorMessage( e, "無法將中文網址轉為正確網址編碼" );
        }

        url = url.replaceAll( "\\s", "%20" );
        //url = fixSpecialCase( url );

        return url;
    }

    // 只用於非Windows系統的執行程式
    public static void runSimpleCmd( String cmd, String path )
    {
        try
        {

            String[] cmds = new String[]
            {
                cmd, path
            };
            Runtime.getRuntime().exec( cmds, null, new File( Common.getNowAbsolutePath() ) );
        }
        catch ( IOException ex )
        {
            Common.hadleErrorMessage( ex, "在非Windows系統下無法開啟檔案" );
        }
    }

    // 非windows系統時的操作
    // openFileManger: 開啟檔案總管
    public static void runCmd( String program, String file, boolean openFileManger )
    {
        String path = file;
        String cmd = program;

        // 檔案不存在就只顯示訊息而不繼續操作
        if ( !new File( file ).exists() )
        {
            String nowSkinName = UIManager.getLookAndFeel().getName(); // 目前使用中的面板名稱
            String colorString = "blue";
            if ( nowSkinName.equals( "HiFi" ) || nowSkinName.equals( "Noire" ) )
            {
                colorString = "yellow";
            }

            CommonGUI.showMessageDialog( ComicDownGUI.mainFrame, "<html><font color=" + colorString + ">"
                    + file + "</font>" + "不存在，無法開啟</html>",
                                         "提醒訊息", JOptionPane.INFORMATION_MESSAGE );
            return;
        }

        String[] fileList = new File( file ).list();
        Common.debugPrintln( file );

        String firstCompressFileName = "";
        boolean existCompressFile = false;
        for ( int i = 0; i < fileList.length; i++ )
        {
            //Common.debugPrintln( "FILE: " + fileList[i] );
            if ( fileList[i].matches( "(?s).*\\.zip" )
                    || fileList[i].matches( "(?s).*\\.cbz" ) )
            {
                firstCompressFileName = fileList[i];
                existCompressFile = true;
                break;
            }
        }

        if ( !openFileManger )
        {
            if ( existCompressFile )
            {
                // 資料夾內存在壓縮檔
                path = file + Common.getSlash() + firstCompressFileName;
            }
            else
            {
                String[] picList = new File( file + Common.getSlash() + fileList[0] ).list();
                String firstPicFileInFirstVolume = "";

                if ( picList != null )
                {
                    firstPicFileInFirstVolume = picList[0];
                }

                path = file + Common.getSlash() + fileList[0]
                        + Common.getSlash() + firstPicFileInFirstVolume;
            }
        }

        Common.debugPrintln( "開啟命令：" + cmd + " " + path );

        try
        {
            String[] cmds = new String[]
            {
                cmd, path
            };
            Runtime.getRuntime().exec( cmds, null, new File( Common.getNowAbsolutePath() ) );
            //Runtime.getRuntime().exec(cmd + path);

        }
        catch ( IOException ex )
        {
            Common.hadleErrorMessage( ex, "無法執行此命令：" + cmd + " " + path );
        }
    }

    // 在runDirectory此資料夾內執行腳本
    public static void runShellScript( String scriptFile, String runDirectory )
    {
        if ( !new File( scriptFile ).exists() )
        {
            Common.errorReport( "無法執行！找不到此腳本：" + scriptFile );

            return;
        }

        if ( Common.isWindows() )
        { // windows系統下的執行程序
            String cmd = SetUp.getDefaultShellScript(); // 設置預設腳本語言 ex. powershell

            String[] cmds = null;
            if ( cmd.equals( "powershell" ) )
            { // 執行ps1檔
                cmds = new String[]
                {
                    cmd, scriptFile
                };
            }
            else
            {
                // 執行bat檔
                cmds = new String[]
                {
                    scriptFile
                };
                runWindowsCmd( cmds, runDirectory );
            }


        }
        else
        { // 非windows系統下的執行程序
            String cmd = SetUp.getDefaultShellScript(); // 設置預設腳本語言 ex. bash
            String path = scriptFile;
            try
            {
                String[] cmds = new String[]
                {
                    cmd, path
                };
                Common.debugPrintln( "\n在[" + runDirectory + "]資料夾內執行腳本：" + cmds[0] + " " + cmds[1] );
                Process p = Runtime.getRuntime().exec( cmds, null, new File( runDirectory ) );

                try
                {
                    p.waitFor(); // 等待到腳本執行結束
                }
                catch ( InterruptedException e )
                {
                    Common.hadleErrorMessage( e, "無法等待腳本執行" );
                }
            }
            catch ( IOException ex )
            {
                Common.hadleErrorMessage( ex, "無法執行此命令：" + cmd + " " + path );
            }
        }
    }

    // 執行window的命令，目前僅用於執行腳本
    public static void runWindowsCmd( String[] cmd, String runDirectory )
    {
        String cmdString = ""; // 顯示用的命令串
        for ( int i = 0; i < cmd.length; i++ )
        {
            cmdString += cmd[i] + " ";
        }

        Map<String, String> newEnv = new HashMap<String, String>();
        newEnv.putAll( System.getenv() );
        String[] i18n = new String[ cmd.length + 2 ];
        i18n[0] = "cmd";
        i18n[1] = "/C";
        i18n[2] = cmd[0];
        for ( int counter = 1; counter < cmd.length; counter++ )
        {
            String envName = "JENV_" + counter;
            i18n[counter + 2] = "%" + envName + "%";
            newEnv.put( envName, cmd[counter] );
        }
        cmd = i18n;

        BufferedReader stdout = null;
        ProcessBuilder pb = new ProcessBuilder( cmd );
        File dirFile = new File( runDirectory );
        pb.directory( dirFile ); //設置執行資料夾

        Map<String, String> env = pb.environment();
        env.putAll( newEnv );

        // 暫時將資料夾移動到預設下載目錄下的tmepScript資料夾內
        String tempRunScriptDirectory = SetUp.getDownloadDirectory() + "tempScript" + Common.getSlash();

        if ( true )
        { //new File( runDirectory ).renameTo( new File( tempRunScriptDirectory ) ) ) {
            //Common.debugPrintln( "\n將尚未執行腳本完成的資料夾移動回到" + tempRunScriptDirectory );
            tempRunScriptDirectory = runDirectory;
            pb.directory( new File( tempRunScriptDirectory ) ); //設置執行資料夾
            try
            { // 原本的資料夾無法執行腳本，那就改在暫存資料夾執行。
                Common.debugPrintln( "嘗試在[" + tempRunScriptDirectory + "]資料夾內執行命令：" + cmdString );
                pb.redirectErrorStream( true );
                final Process p = pb.start();

                //read the standard output 
                stdout = new BufferedReader( new InputStreamReader( p.getInputStream() ) );
                String line = "";
                while ( (line = stdout.readLine()) != null )
                {
                    Common.debugPrintln( line );
                }

                try
                {
                    int ret = p.waitFor(); // 等待到腳本執行結束
                    Common.debugPrintln( "回傳值為 " + ret );
                }
                catch ( InterruptedException e )
                {
                    Common.hadleErrorMessage( e, "無法等待腳本執行" );
                }

                stdout.close();

                if ( new File( tempRunScriptDirectory ).renameTo( new File( runDirectory ) ) )
                {
                    Common.debugPrintln( "將已執行腳本完成的資料夾移動回到" + runDirectory );
                }
                else
                {
                    Common.debugPrintln( "無法將已執行腳本完成的資料夾移動回到" + runDirectory );
                }

            }
            catch ( IOException ex1 )
            {
                Common.hadleErrorMessage( ex1, "無法在[" + tempRunScriptDirectory + "]資料夾內執行此命令：" + cmd );
            }
        }
        else
        {
            Common.debugPrintln( "無法將尚未執行腳本完成的資料夾 " + runDirectory + " 移動到 " + tempRunScriptDirectory );
        }
    }

    // 解決非ANSI字會變成？而無法使用程式開啟的問題
    // 出處：http://stackoverflow.com/questions/1876507/java-runtime-exec-on-windows-fails-with-unicode-in-arguments
    public static void runUnansiCmd( String program, String file )
    {
        if ( !new File( file ).exists() )
        {
            //String nowSkinName = UIManager.getLookAndFeel().getName(); // 目前使用中的面板名稱
            // 取得介面設定值（不用UIManager.getLookAndFeel().getName()是因為這樣才能讀到_之後的參數）
            String nowSkinName = SetUp.getSkinClassName();
            String colorString = "blue";
            if ( CommonGUI.isDarkSytleSkin( nowSkinName ) )
            {
                colorString = "yellow"; // 暗色風格界面用黃色比較看得清楚
            }

            CommonGUI.showMessageDialog( ComicDownGUI.mainFrame, "<FONT color=" + colorString + ">"
                    + file + "</FONT>" + "不存在，無法開啟",
                                         "提醒訊息", JOptionPane.INFORMATION_MESSAGE );
            return;
        }

        file = "\"" + file + "\"";

        String[] cmd = new String[]
        {
            program, file
        };
        Map<String, String> newEnv = new HashMap<String, String>();
        newEnv.putAll( System.getenv() );
        String[] i18n = new String[ cmd.length + 2 ];
        i18n[0] = "cmd";
        i18n[1] = "/C";
        i18n[2] = cmd[0];
        for ( int counter = 1; counter < cmd.length; counter++ )
        {
            String envName = "JENV_" + counter;
            i18n[counter + 2] = "%" + envName + "%";
            newEnv.put( envName, cmd[counter] );
        }
        cmd = i18n;

        ProcessBuilder pb = new ProcessBuilder( cmd );
        Map<String, String> env = pb.environment();
        env.putAll( newEnv );
        try
        {
            Common.debugPrintln( "執行命令：" + program + " " + file );
            final Process p = pb.start();
        }
        catch ( IOException ex )
        {
            Common.hadleErrorMessage( ex, "無法執行此命令：" + cmd );
        }
    }

    public static void downloadPost( String webSite, String outputDirectory,
                                     String outputFileName, boolean needCookie, String cookieString, String postString, String referURL )
    {
        // downlaod file by URL

        boolean gzipEncode = false;
        int retryTimes = 0;
        boolean forceDownload = false;
        boolean fastMode = false;

        int fileGotSize = 0;


        if ( CommonGUI.stateBarDetailMessage == null )
        {
            CommonGUI.stateBarMainMessage = "下載網頁進行分析 : ";
            CommonGUI.stateBarDetailMessage = outputFileName + " ";
        }

        if ( Run.isAlive || forceDownload )
        { // 當允許下載或強制下載時才執行連線程序
            try
            {

                ComicDownGUI.stateBar.setText( webSite + " 連線中..." );

                URL url = new URL( webSite );
                HttpURLConnection connection = ( HttpURLConnection ) url.openConnection();

                // 偽裝成瀏覽器

                connection.setDoOutput( true );
                connection.setDoInput( true );
                (( HttpURLConnection ) connection).setRequestMethod( "POST" );
                connection.setUseCaches( false );
                connection.setAllowUserInteraction( true );
                HttpURLConnection.setFollowRedirects( true );
                connection.setInstanceFollowRedirects( true );

                connection.setRequestProperty(
                        "User-agent",
                        "Mozilla/5.0 (Windows; U; Windows NT 6.0; zh-TW; rv:1.9.1.2) "
                        + "Gecko/20090729 Firefox/3.5.2 GTB5 (.NET CLR 3.5.30729)" );
                connection.setRequestProperty( "Accept",
                                               "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8" );
                connection.setRequestProperty( "Accept-Language",
                                               "zh-tw,en-us;q=0.7,en;q=0.3" );
                connection.setRequestProperty( "Accept-Charse",
                                               "Big5,utf-8;q=0.7,*;q=0.7" );
                if ( cookieString != null )
                {
                    connection.setRequestProperty( "Cookie", cookieString );
                }
                if ( referURL != null )
                {
                    connection.setRequestProperty( "Referer", referURL );
                }

                connection.setRequestProperty( "Content-Type",
                                               "application/x-www-form-urlencoded" );


                connection.setRequestProperty( "Content-Length", String.valueOf( postString.getBytes().length ) );

                // google圖片下載時因為有些連線很久沒回應，所以要設置計時器，預防連線時間過長
                Timer timer = new Timer();
                if ( SetUp.getTimeoutTimer() > 0 )
                {
                    // 預設(getTimeoutTimer()*1000)秒會timeout
                    timer.schedule( new TimeoutTask(), SetUp.getTimeoutTimer() * 1000 );
                }

                Common.checkDirectory( outputDirectory ); // 檢查有無目標資料夾，若無則新建一個　

                //OutputStream os = response.getOutputStream();
                OutputStream os = new FileOutputStream( outputDirectory + outputFileName );
                InputStream is = null;


                java.io.DataOutputStream dos = new java.io.DataOutputStream(
                        connection.getOutputStream() );
                dos.writeBytes( postString );
                dos.close();

                //tryConnect( connection );

                if ( connection.getResponseCode() != 200 )
                {
                    //Common.debugPrintln( "第二次失敗，不再重試!" );
                    Common.errorReport( "錯誤回傳碼(responseCode): "
                            + connection.getResponseCode() + " : " + webSite );
                    return;
                }

                is = connection.getInputStream();

                int fileSize = connection.getContentLength() / 1000;
                Common.debugPrint( "(" + fileSize + " k) " );
                String fileSizeString = fileSize > 0 ? "" + fileSize : " ? ";

                byte[] r = new byte[ 1024 ];
                int len = 0;

                while ( (len = is.read( r )) > 0 && (Run.isAlive || forceDownload) )
                {
                    // 快速模式下，檔案小於1mb且連線超時 -> 切斷連線
                    if ( fileSize > 1024 || !Flag.timeoutFlag ) // 預防卡住的機制
                    {
                        os.write( r, 0, len );
                    }
                    else
                    {
                        break;
                    }

                    fileGotSize += (len / 1000);

                    if ( Common.withGUI() )
                    {
                        int percent = 100;
                        String downloadText = "";
                        if ( fileSize > 0 )
                        {
                            percent = (fileGotSize * 100) / fileSize;
                            downloadText = fileSizeString + "Kb ( " + percent + "% ) ";
                        }
                        else
                        {
                            downloadText = fileSizeString + " Kb ( " + fileGotSize + "Kb ) ";
                        }

                        ComicDownGUI.stateBar.setText( CommonGUI.stateBarMainMessage
                                + CommonGUI.stateBarDetailMessage
                                + " : " + downloadText );
                    }
                }

                is.close();
                os.flush();
                os.close();




                if ( Common.withGUI() )
                {
                    ComicDownGUI.stateBar.setText( CommonGUI.stateBarMainMessage
                            + CommonGUI.stateBarDetailMessage
                            + " : " + fileSizeString + "Kb ( 100% ) " );
                }

                connection.disconnect();


                // 若真實下載檔案大小比預估來得小，則視設定值決定要重新嘗試幾次
                int realFileGotSize = ( int ) new File( outputDirectory + outputFileName ).length() / 1000;
                if ( realFileGotSize + 1 < fileGotSize && retryTimes > 0 )
                {
                    String messageString = realFileGotSize + " < " + fileGotSize
                            + " -> 重新嘗試下載" + outputFileName + "（" + retryTimes
                            + "/" + SetUp.getRetryTimes() + "） 倒數:";
                    Common.sleep( 2000, messageString ); // 每次暫停兩秒再重新連線

                    downloadFile( webSite, outputDirectory, outputFileName,
                                  needCookie, cookieString, referURL, fastMode, retryTimes - 1, gzipEncode, false );


                }

                if ( fileSize < 1024 && Flag.timeoutFlag )
                {
                    new File( outputDirectory + outputFileName ).delete();
                    Common.debugPrintln( "刪除不完整檔案：" + outputFileName );

                    ComicDownGUI.stateBar.setText( "下載逾時，跳過" + outputFileName );

                }

                timer.cancel(); // 連線結束同時也關掉計時器

                Flag.timeoutFlag = false; // 歸回初始值

                Common.debugPrintln( webSite + " downloads successful!" ); // for debug

            }
            catch ( Exception e )
            {
                Common.hadleErrorMessage( e, "無法正確下載" + webSite );
            }

            CommonGUI.stateBarDetailMessage = null;
        }
    }

    public static void urlConnection( String urlString )
    {
        //urlString = "http://www.coderanch.com/t/207232/sockets/java/httpURLConnection-content-length-always";

        try
        {
            URL url = new URL( urlString );
            URLConnection yc = url.openConnection();
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                    yc.getInputStream() ) );
            String inputLine;



            while ( (inputLine = in.readLine()) != null )
            {
                Common.debugPrintln( inputLine );
            }
            in.close();

            Common.debugPrintln( "連線結束" );

        }
        catch ( Exception ex )
        {
            Common.errorReport( "無法連線" );
        }

    }

    public static int simpleDownloadFile( String webSite,
                                          String outputDirectory, String outputFileName )
    {
        return simpleDownloadFile( webSite, outputDirectory, outputFileName, null, null );
    }

    public static int simpleDownloadFile( String webSite,
                                          String outputDirectory, String outputFileName, String referString )
    {
        return simpleDownloadFile( webSite, outputDirectory, outputFileName, null, referString );
    }

    public static int simpleDownloadFile( String webSite,
                                          String outputDirectory, String outputFileName, String cookieString, String referString )
    {
        int responseCode = 0;
        try
        {
            URL url = new URL( webSite );
            HttpURLConnection connection = ( HttpURLConnection ) url.openConnection();

            connection.setRequestMethod( "GET" );
            connection.setDoOutput( true );

            connection.setRequestProperty( "If-None-Match", "\"c4d553cc945cd1:0\"" );
            connection.setRequestProperty( "Host", "pic.fumanhua.com" );

            //connection.setRequestProperty( "Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8" );
            //connection.setRequestProperty( "Accept-Charset", "Big5,utf-8;q=0.7,*;q=0.3" );
            //connection.setRequestProperty( "Accept-Encoding", "gzip,deflate,sdch" );
            //connection.setRequestProperty( "Accept-Language", "zh-TW,zh;q=0.8,en-US;q=0.6,en;q=0.4" );
            //connection.setRequestProperty( "Cache-Control", "max-age=0" );
            //connection.setRequestProperty( "Connection", "keep-alive" );
            //connection.setRequestProperty( "Host", "f1.xiami.net" );
            connection.setRequestProperty( "User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.97 Safari/537.11" );


            if ( referString != null && !"".equals( referString ) )
            { // 設置refer
                connection.setRequestProperty( "Referer", referString );
            }

            if ( cookieString != null && !"".equals( cookieString ) )
            { // 設置cookie
                connection.setRequestProperty( "Cookie", cookieString );
            }

            ComicDownGUI.stateBar.setText( webSite + " 連線中..." );

            //tryConnect( connection );

            int fileSize = connection.getContentLength() / 1000;

            responseCode = connection.getResponseCode();
            if ( responseCode != 200 )
            {
                //Common.debugPrintln( "第二次失敗，不再重試!" );
                Common.errorReport( "錯誤回傳碼(responseCode): "
                        + responseCode + " : " + webSite );

                return responseCode;
            }

            Common.checkDirectory( outputDirectory ); // 檢查有無目標資料夾，若無則新建一個　

            //OutputStream os = response.getOutputStream();
            OutputStream os = new FileOutputStream( outputDirectory + outputFileName );
            InputStream is = null;


            is = connection.getInputStream(); // 其他漫畫網

            Common.debugPrint( "(" + fileSize + " k) " );
            String fileSizeString = fileSize > 0 ? "" + fileSize : " ? ";



            byte[] r = new byte[ 1024 ];
            int len = 0;

            int fileGotSize = 0;
            while ( (len = is.read( r )) > 0 && (Run.isAlive) )
            {
                // 快速模式下，檔案小於1mb且連線超時 -> 切斷連線
                if ( fileSize > 1024 || !Flag.timeoutFlag ) // 預防卡住的機制
                {
                    os.write( r, 0, len );
                }
                else
                {
                    break;
                }

                fileGotSize += (len / 1000);

                if ( Common.withGUI() )
                {
                    int percent = 100;
                    String downloadText = "";
                    if ( fileSize > 0 )
                    {
                        percent = (fileGotSize * 100) / fileSize;
                        downloadText = fileSizeString + "Kb ( " + percent + "% ) ";
                    }
                    else
                    {
                        downloadText = fileSizeString + " Kb ( " + fileGotSize + "Kb ) ";
                    }

                    ComicDownGUI.stateBar.setText( CommonGUI.stateBarMainMessage
                            + CommonGUI.stateBarDetailMessage
                            + " : " + downloadText );
                }
            }

            is.close();
            os.flush();
            os.close();

            if ( Common.withGUI() )
            {
                ComicDownGUI.stateBar.setText( CommonGUI.stateBarMainMessage
                        + CommonGUI.stateBarDetailMessage
                        + " : " + fileSizeString + "Kb ( 100% ) " );
            }

            connection.disconnect();

            Flag.timeoutFlag = false; // 歸回初始值

            Common.debugPrintln( webSite + " downloads successful!" ); // for debug

        }
        catch ( MalformedURLException e )
        {
            Common.hadleErrorMessage( e, "無法正確下載" + webSite );
        }
        catch ( IOException e )
        {
            // TODO Auto-generated catch block
            Common.hadleErrorMessage( e, "無法正確下載" + webSite );
        }


        return responseCode;
    }

    // 從java.awt.Color[r=255,g=175,b=175]轉為Color
    public static Color getColor( String colorString )
    {

        String[] tempStrings = colorString.split( "=|,|\\[|\\]" );

        int r = Integer.parseInt( tempStrings[2] );
        int g = Integer.parseInt( tempStrings[4] );
        int b = Integer.parseInt( tempStrings[6] );

        //Common.debugPrintln( "取得色碼(r, g, b): " + r + " " + g + " " + b );

        return new Color( r, g, b );
    }

    // 測試印出
    public static void print( String... string )
    {
        for ( String s : string )
        {
            Common.debugPrintln( s );
        }

        System.exit( 0 );
    }

    // 回傳文字檔案預設輸出格式的副檔名
    public static String getDefaultTextExtension()
    {
        if ( SetUp.getDefaultTextOutputFormat() == FileFormatEnum.HTML_WITHOUT_PIC
                || SetUp.getDefaultTextOutputFormat() == FileFormatEnum.HTML_WITH_PIC )
        {
            return "html";
        }
        else
        {
            return "txt";
        }

    }

    // 將<>標籤都拿掉
    public static String replaceTag( String text )
    {
        return text.replaceAll( "<[^<>]+>", "" ); // 將所有標籤去除
    }

    // 處理錯誤訊息的步驟
    public static void hadleErrorMessage( Exception ex, String tipString )
    {
        tipString += "！\n\n";
        System.err.println( tipString );
        ex.printStackTrace();
        Common.outputErrorMessage( ex, tipString );
    }

    // 輸出錯誤訊息到檔案
    public static void outputErrorMessage( Exception ex, String tipString )
    {
        String timeString = new Date().toString(); // 取得當前時間的字串
        timeString = Common.getStringRemovedIllegalChar( timeString ); // 拿掉不合法字元
        String outputFileName = "error_report_" + timeString + ".txt";
        String outputPath = Common.getNowAbsolutePath() + "ErrorRecord" + Common.getSlash();
        String outputMessage = "錯誤提示：\n" + tipString;

        if ( ex != null )
        {
            outputMessage += "錯誤原因：\n" + ex.getMessage() + "\n\n"
                    + "錯誤發生地點：\n";

            outputMessage = Common.getStringUsingDefaultLanguage( outputMessage, ex.getMessage() );

            StackTraceElement[] stack = ex.getStackTrace();
            for ( int i = 0; i < stack.length; i++ )
            {
                outputMessage += stack[i].toString() + "\n";
            }
        }

        Common.outputFile( outputMessage, outputPath, outputFileName );
    }

    // 模擬javascript的涵式功能
    public static String fromCharCode( int... codePoints )
    {
        StringBuilder builder = new StringBuilder( codePoints.length );
        for ( int codePoint : codePoints )
        {
            builder.append( Character.toChars( codePoint ) );
        }
        return builder.toString();
    }

    public static void copyFile( String srFile, String dtFile )
    {
        try
        {
            File f1 = new File( srFile );
            File f2 = new File( dtFile );
            InputStream in = new FileInputStream( f1 );

            //For Append the file.
//  OutputStream out = new FileOutputStream(f2,true);

            //For Overwrite the file.
            OutputStream out = new FileOutputStream( f2 );

            byte[] buf = new byte[ 1024 ];
            int len;
            while ( (len = in.read( buf )) > 0 )
            {
                out.write( buf, 0, len );
            }
            in.close();
            out.close();
            Common.debugPrintln( "File copied." );
        }
        catch ( FileNotFoundException ex )
        {
            String message = ex.getMessage() + " in the specified directory.";
            Common.errorReport( message );
            //System.exit( 0 );
        }
        catch ( IOException e )
        {
            Common.errorReport( e.getMessage() );
        }
    }

    // 重新啟動程式
    public static void restart()
    {
        Common.startJARandExit( Common.getThisFileName() );
    }

    // 執行指定jar檔並結束目前程式
    public static void startJARandExit( String jarFileName )
    {
        if ( Common.isWindows() )
        {
            Common.runUnansiCmd( "java -jar ", Common.getNowAbsolutePath() + jarFileName );
        }
        else
        {
            Common.runCmd( "java -jar", Common.getNowAbsolutePath() + jarFileName, false );
        }

        ComicDownGUI.exit();
    }

    // 取得目前程式檔名
    public static String getThisFileName()
    {
        return ComicDownGUI.versionString.replaceAll( "  ", "_" ) + ".jar";
    }

    // 下載j單一個ar檔，下載完畢自動重啟
    public static void downloadJarFile( final String fileURL, final String fileName )
    {
        Common.downloadJarFiles( new String[]
                {
                    fileURL
                }, new String[]
                {
                    fileName
                } );
    }

    // 下載多個jar檔，下載完畢自動重啟
    public static void downloadJarFiles( final String[] fileURL, final String[] fileName )
    {
        boolean backupValue = Run.isAlive; // 備份原值
        String fileDir = Common.getNowAbsolutePath() + "lib" + Common.getSlash(); // 存於lib資料夾內

        Run.isAlive = true;
        for ( int i = 0; i < fileURL.length; i++ )
        {
            Common.downloadFile( fileURL[i], fileDir, fileName[i], false, "" );
        }

        Run.isAlive = backupValue; // 還原原值

        Thread downThread = new Thread( new Runnable()
        {

            public void run()
            {
                CommonGUI.showMessageDialog( ComicDownGUI.mainFrame,
                                             fileName + "下載完畢，請注意，程式即將重新啟動! ",
                                             "提醒訊息", JOptionPane.INFORMATION_MESSAGE );
                //Common.restartApplication(); // 重新開啟程式
                Common.restart();
            }
        } );
        downThread.start();
    }

    public static void setMp3Tag( String filePath, String fileName, String[] tags )
    {
        /*
         */

        //String jarFileName = "jaudiotagger-2.0.4-20111207.115108-15.jar";
        String jarClassName = "jaudiotagger-2.0.4-20111207.115108-15.jar";

        // 若jar檔不存在 就下載
        if ( !Common.existJAR( jarClassName ) )
        {
            String jarFileURL = "https://sites.google.com/site/jcomicdownloader/release/jaudiotagger-2.0.4-20111207.115108-15.jar?attredirects=0&d=1";

            Common.downloadJarFile( jarFileURL, jarClassName );
        }

        Class AudioFile = CommonGUI.getOuterClass( "org.jaudiotagger.audio.AudioFile", jarClassName );
        Class AudioFileIO = CommonGUI.getOuterClass( "org.jaudiotagger.audio.AudioFileIO", jarClassName );
        Class Tag = CommonGUI.getOuterClass( "org.jaudiotagger.tag.Tag", jarClassName );
        Class FieldKey = CommonGUI.getOuterClass( "org.jaudiotagger.tag.FieldKey", jarClassName );
        Class ID3v23Tag = CommonGUI.getOuterClass( "org.jaudiotagger.tag.id3.ID3v23Tag", jarClassName );
        Class Artwork = CommonGUI.getOuterClass( "org.jaudiotagger.tag.images.Artwork", jarClassName );
        Class StandardArtwork = CommonGUI.getOuterClass( "org.jaudiotagger.tag.images.StandardArtwork", jarClassName );

        //Object f = CommonGUI.getNewInstanceFromClass( AudioFile );

        //f = AudioFileIO.read( new File( "日久見人心.mp3" ) );

        try
        {
            Method read = AudioFileIO.getDeclaredMethod( "read", new Class[]
                    {
                        File.class
                    } );

            Object f = read.invoke( null, new Object[]
                    {
                        new File( filePath + fileName )
                    } ); // static method可以將object指向null

            if ( f == null )
            {
                Common.debugPrintln( fileName + " 無法讀取!" );
                return;
            }

            Object tag;
            if ( fileName.matches( "(?s).*\\.wma" ) ) // 若是wma就不能做ID3v23Tag
            {
                //Tag tag = f.getTag();
                Method getTag = AudioFile.getDeclaredMethod( "getTag" );
                tag = getTag.invoke( AudioFile.cast( f ) );
            }
            else
            {
                // 每次都重新製作標籤
                Common.debugPrintln( fileName + " 標籤重新製作" );
                //tag = new ID3v23Tag();
                tag = CommonGUI.getNewInstanceFromClass( ID3v23Tag );
            }


            //Artwork art = StandardArtwork.createArtworkFromFile( new File( "123.jpg" ));
            Method createArtworkFromFile = StandardArtwork.getDeclaredMethod( "createArtworkFromFile", new Class[]
                    {
                        File.class
                    } );
            Object art = createArtworkFromFile.invoke( null, new Object[]
                    {
                        new File( tags[TagEnum.COVER] )
                    } );

            //tag.setField( art );
            Method setField = Tag.getDeclaredMethod( "setField", new Class[]
                    {
                        Artwork
                    } );
            setField.invoke( Tag.cast( tag ), new Object[]
                    {
                        Artwork.cast( art )
                    } );

            //tag.setField( FieldKey.valueof( ARTIST ), "梁靜茹" );
            Method valueof = FieldKey.getDeclaredMethod( "valueOf", new Class[]
                    {
                        String.class
                    } );
            setField = Tag.getDeclaredMethod( "setField", new Class[]
                    {
                        FieldKey, String.class
                    } );

            Object ARTIST = valueof.invoke( null, new Object[]
                    {
                        "ARTIST"
                    } );
            setField.invoke( Tag.cast( tag ), new Object[]
                    {
                        FieldKey.cast( ARTIST ), tags[TagEnum.ARTIST]
                    } );
            Object ALBUM = valueof.invoke( null, new Object[]
                    {
                        "ALBUM"
                    } );
            setField.invoke( Tag.cast( tag ), new Object[]
                    {
                        FieldKey.cast( ALBUM ), tags[TagEnum.ALBUM]
                    } );

            Object GENRE = valueof.invoke( null, new Object[]
                    {
                        "GENRE"
                    } );
            setField.invoke( Tag.cast( tag ), new Object[]
                    {
                        FieldKey.cast( GENRE ), tags[TagEnum.GENRE]
                    } );
            Object LANGUAGE = valueof.invoke( null, new Object[]
                    {
                        "LANGUAGE"
                    } );
            setField.invoke( Tag.cast( tag ), new Object[]
                    {
                        FieldKey.cast( LANGUAGE ), tags[TagEnum.LANGUAGE]
                    } );

            //Object LYRICS = valueof.invoke(null, new Object[]{"LYRICS"});
            //setField.invoke(Tag.cast(tag), new Object[]{ FieldKey.cast( LYRICS ), tags[TagEnum.LYRICS]});
            /*
             Object COMMENT = valueof.invoke( null, new Object[]
             {
             "COMMENT"
             } );
             setField.invoke( Tag.cast( tag ), new Object[]
             {
             FieldKey.cast( COMMENT ), tags[TagEnum.COMMENT]
             } );
             */
            Object TITLE = valueof.invoke( null, new Object[]
                    {
                        "TITLE"
                    } );
            setField.invoke( Tag.cast( tag ), new Object[]
                    {
                        FieldKey.cast( TITLE ), tags[TagEnum.TITLE]
                    } );
            Object TRACK = valueof.invoke( null, new Object[]
                    {
                        "TRACK"
                    } );
            setField.invoke( Tag.cast( tag ), new Object[]
                    {
                        FieldKey.cast( TRACK ), tags[TagEnum.TRACK]
                    } );
            Object YEAR = valueof.invoke( null, new Object[]
                    {
                        "YEAR"
                    } );
            setField.invoke( Tag.cast( tag ), new Object[]
                    {
                        FieldKey.cast( YEAR ), tags[TagEnum.YEAR]
                    } );

            //f.setTag( tag );
            Method setTag = AudioFile.getDeclaredMethod( "setTag", new Class[]
                    {
                        Tag
                    } );
            setTag.invoke( AudioFile.cast( f ), new Object[]
                    {
                        Tag.cast( tag )
                    } );

            // f.commit();
            Method commit = AudioFile.getDeclaredMethod( "commit" );
            commit.invoke( AudioFile.cast( f ) );
            Common.debugPrintln( fileName + " 加入標籤成功 ! " );

        }
        catch ( Exception ex )
        {

            Logger.getLogger( CommonGUI.class.getName() ).log( Level.SEVERE, null, ex );
            Common.debugPrintln( "出錯啦!" );
        }


        //tag.setField( FieldKey.ALBUM, "日久見人心專輯" );

        //tag.setField( FieldKey.TITLE, "日久見人心" );

    }

    // 檢查是否存在可直接掛載的jar檔
    public static boolean existJAR( String jarFileName )
    {
        if ( new File( Common.getNowAbsolutePath() + jarFileName ).exists()
                || new File( Common.getNowAbsolutePath() + "lib" + Common.getSlash() + jarFileName ).exists() )
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public static String unescape( String src )
    {
        StringBuffer tmp = new StringBuffer();
        tmp.ensureCapacity( src.length() );
        int lastPos = 0, pos = 0;
        char ch;
        while ( lastPos < src.length() )
        {
            pos = src.indexOf( "%", lastPos );
            if ( pos == lastPos )
            {
                if ( src.charAt( pos + 1 ) == 'u' )
                {
                    ch = ( char ) Integer.parseInt( src.substring( pos + 2, pos + 6 ), 16 );
                    tmp.append( ch );
                    lastPos = pos + 6;
                }
                else
                {
                    ch = ( char ) Integer.parseInt( src.substring( pos + 1, pos + 3 ), 16 );
                    tmp.append( ch );
                    lastPos = pos + 3;
                }
            }
            else
            {
                if ( pos == -1 )
                {
                    tmp.append( src.substring( lastPos ) );
                    lastPos = src.length();
                }
                else
                {
                    tmp.append( src.substring( lastPos, pos ) );
                    lastPos = pos;
                }
            }
        }
        return tmp.toString();
    }
}

class TimeoutTask extends TimerTask
{

    public void run()
    {
        Common.debugPrintln( "超過下載時限，終止此次連線!" );
        Flag.timeoutFlag = true;
    }
}
