/*
 ----------------------------------------------------------------------------------------------------
 Program Name : JComicDownloader
 Authors  : surveyorK
 Last Modified : 2012/11/21
 ----------------------------------------------------------------------------------------------------
 ChangeLog:
 5.08: 修復冗餘設定檔檢查的問題。
 2.03: 增加下載失敗後重新嘗試次數(retryTimes)的選項
 1.12: 修復tempDirectory最後出現兩個斜線的bug。
 1.09: 加入是否保留書籤和記錄的選項
 ----------------------------------------------------------------------------------------------------
 */
package jcomicdownloader;

import java.awt.Color;
import java.awt.Font;
import jcomicdownloader.tools.*;

import java.io.*;
import java.util.Locale;
import jcomicdownloader.enums.FileFormatEnum;
import jcomicdownloader.enums.LanguageEnum;
import jcomicdownloader.frame.OptionFrame;

/**
 預設值的設置，讀寫設定檔

 */
public class SetUp
{ // read setup file, and then setup

    private static String downloadDirectory;
    private static String tempDirectory;
    private static String recordFileDirectory; // 設定檔和記錄檔的資料夾
    private static String originalDownloadDirectory;
    private static String picFrontName;
    private static String volume; // which volume the comic page is
    private static String wholeTitle; // title name and volume or chapter
    private static int fileNameLength; // length of file name
    private static String skinClassName; // 預設版面的類別名稱
    private static boolean outputUrlFile; // 是否輸出位址檔案(*.txt)
    private static boolean downloadPicFile; // 是否下載圖片
    private static boolean openDebugMessageWindow; // 是否開啟除錯訊息視窗
    private static boolean showDoneMessageAtSystemTray; // 縮小在工具列時是否顯示下載完成訊息
    private static boolean keepUndoneDownloadMission; // 是否保留未完成任務到下一次開啟
    private static boolean keepDoneDownloadMission; // 是否保留已完成任務到下一次開啟
    private static boolean autoAddMission; // 是否複製網址後就自動解析並詢問欲下載集數
    private static boolean keepBookmark; // 是否保留書籤
    private static boolean keepRecord; // 是否保留記錄
    private static boolean choiceAllVolume; // 是否預設勾選全部集數
    private static String defaultFontName; // 使用者字型名稱（若沒設就直接用系統預設）
    private static int defaultFontSize; // 使用者字型大小（若沒設就直接用系統預設）
    private static String ehMemberID; // EH會員ID
    private static String ehMemberPasswordHash; // EH會員密碼Hash
    private static int timeoutTimer; // 逾時計時器的倒數時間
    private static int retryTimes; // 下載失敗重試次數
    private static String openPicFileProgram; // 預設開啟圖片檔的程式
    private static String openTextFileProgram; // 預設開啟文件檔的程式
    private static String openZipFileProgram; // 預設開啟壓縮檔的程式
    public static boolean assignDownloadPath;
    public static boolean autoCompress;
    public static boolean deleteOriginalPic;
    public static int beginVolume;
    public static int endVolume;
    private static String proxyServer; // Http Proxy Server
    private static String proxyPort; // Http Proxy Server Port
    public static boolean addSchedule;
    public static boolean isError;
    // 音效
    public static boolean playAllDoneAudio; // 是否播放全部任務完成音效
    public static boolean playSingleDoneAudio; // 是否播放單一任務完成音效
    public static String allDoneAudioFile; // 在全部任務完成後播放的外部音效檔
    public static String singleDoneAudioFile; // 在單一任務完成後播放的外部音效檔
    // 腳本
    public static boolean runAllDoneScript; // 是否播放全部任務完成音效
    public static boolean runSingleDoneScript; // 是否播放單一任務完成音效
    public static String allDoneScriptFile; // 在全部任務完成後執行的腳本檔案
    public static String singleDoneScriptFile; // 在單一任務完成後執行的腳本檔
    // 是否使用背景圖片
    public static boolean usingBackgroundPicOfMainFrame;
    public static boolean usingBackgroundPicOfInformationFrame;
    public static boolean usingBackgroundPicOfOptionFrame;
    public static boolean usingBackgroundPicOfChoiceFrame;
    // 使用背景圖片時的字體顏色搭配
    private static Color choiceFrameOtherMouseEnteredColor;
    private static Color choiceFrameOtherDefaultColor;
    private static Color choiceFrameTableMouseEnteredColor;
    private static Color choiceFrameTableDefaultColor;
    private static Color choiceFrameTableFileExistedColor;
    private static Color optionFrameOtherMouseEnteredColor;
    private static Color optionFrameOtherDefaultColor;
    private static Color informationFrameOtherMouseEnteredColor;
    private static Color informationFrameOtherDefaultColor;
    private static Color mainFrameOtherMouseEnteredColor;
    private static Color mainFrameOtherDefaultColor;
    private static Color mainFrameTableMouseEnteredColor;
    private static Color mainFrameTableDefaultColor;
    private static Color mainFrameMenuItemDefaultColor;
    // 背景圖片的存放路徑
    private static String backgroundPicPathOfMainFrame;
    private static String backgroundPicPathOfInformationFrame;
    private static String backgroundPicPathOfOptionFrame;
    private static String backgroundPicPathOfChoiceFrame;
    // 壓縮檔的格式
    private static String compressFormat;
    // 預設介面語言
    private static int defaultLanguage;
    // 預設輸出文字檔格式
    private static int defaultTextOutputFormat;
    // 腳本shell種類
    private static String defaultShellScript;
    // 等待單一任務腳本執行的時間（秒數）
    private static int singleScriptWaitTime;
    // 是否下載小說封面圖
    private static boolean downloadNovelCover;
    // 要從幾張圖裡面挑選適合的小說封面圖
    private static int coverSelectAmountIndex;

    /**

     @author user
     */
    public SetUp()
    {

        // 預設值
        downloadDirectory = new String( Common.downloadDirectory );
        originalDownloadDirectory = new String( Common.downloadDirectory );
        tempDirectory = new String( Common.tempDirectory );
        recordFileDirectory = new String( Common.recordDirectory ); // 預設設定檔放在同個資料夾
        picFrontName = "";
        volume = "0";
        fileNameLength = 3;
        assignDownloadPath = false;
        autoCompress = true;
        deleteOriginalPic = false;

        if ( Common.isUnix() )
        {
            skinClassName = "com.sun.java.swing.plaf.gtk.GTKLookAndFeel";
        }
        else
        {
            skinClassName = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
        }

        outputUrlFile = false;
        downloadPicFile = true;
        openDebugMessageWindow = false;

        showDoneMessageAtSystemTray = true;
        keepUndoneDownloadMission = true;
        keepDoneDownloadMission = false;
        autoAddMission = false; // 是否複製網址後就自動解析並詢問欲下載集數
        keepBookmark = true; // 是否保留書籤
        keepRecord = true; // 是否保留記錄
        choiceAllVolume = false; // 是否預設勾選全部集數
        retryTimes = 0;
        openPicFileProgram = ""; // 預設開啟圖片檔的程式
        openTextFileProgram = ""; // 預設開啟文件檔的程式
        openZipFileProgram = ""; // 預設開啟壓縮檔的程式

        proxyServer = ""; // 預設沒有掛上代理伺服器
        proxyPort = "";

        addSchedule = false;
        isError = false;

        beginVolume = 1;
        endVolume = 1;
        defaultFontName = new Font( null ).getName(); // 使用者字型名稱
        defaultFontSize = 18; // 使用者字型大小

        ehMemberID = "0";
        ehMemberPasswordHash = "NULL";

        timeoutTimer = 0;

        playAllDoneAudio = false; // 是否播放全部任務完成音效
        playSingleDoneAudio = false; // 是否播放單一任務完成音效
        allDoneAudioFile = Common.defaultAllDoneAudio; // 在全部任務完成後播放的外部音效檔
        singleDoneAudioFile = Common.defaultSingleDoneAudio; // 在單一任務完成後播放的外部音效檔

        runAllDoneScript = false; // 是否執行全部任務完成腳本
        runSingleDoneScript = false; // 是否執行單一任務完成腳本
        allDoneScriptFile = ""; // 在全部任務完成後執行的外部腳本檔
        singleDoneScriptFile = ""; // 在單一任務完成後執行的外部腳本檔

        Common.closeHttpProxy(); // 預設為關閉代理伺服器。

        // 是否使用背景圖片
        usingBackgroundPicOfMainFrame = false;
        usingBackgroundPicOfInformationFrame = false;
        usingBackgroundPicOfOptionFrame = false;
        usingBackgroundPicOfChoiceFrame = false;

        // 背景圖片的存放路徑
        backgroundPicPathOfMainFrame = "";
        backgroundPicPathOfInformationFrame = "";
        backgroundPicPathOfOptionFrame = "";
        backgroundPicPathOfChoiceFrame = "";

        // 使用背景圖片時的字體顏色搭配
        choiceFrameOtherMouseEnteredColor = Color.GREEN;
        choiceFrameOtherDefaultColor = Color.BLACK;
        choiceFrameTableMouseEnteredColor = Color.RED;
        choiceFrameTableDefaultColor = Color.BLACK;
        choiceFrameTableFileExistedColor = Color.GRAY;
        optionFrameOtherMouseEnteredColor = Color.GREEN;
        optionFrameOtherDefaultColor = Color.WHITE;
        informationFrameOtherMouseEnteredColor = Color.GREEN;
        informationFrameOtherDefaultColor = Color.WHITE;
        mainFrameOtherMouseEnteredColor = Color.GREEN;
        mainFrameOtherDefaultColor = Color.WHITE;
        mainFrameTableMouseEnteredColor = Color.RED;
        mainFrameTableDefaultColor = Color.WHITE;
        mainFrameMenuItemDefaultColor = Color.GRAY;

        // 預設壓縮檔格式
        compressFormat = "zip";
        
        Common.debugPrintln( "您的國家代碼是: " + Locale.getDefault().getCountry() );

        // 預設介面語言
        if ( Locale.getDefault().getCountry().matches( "CN" ) ) // 國家為中國
        {
            defaultLanguage = LanguageEnum.SIMPLIFIED_CHINESE; // 預設簡體中文
        }
        else if ( Locale.getDefault().getCountry().matches( "TW" ) )
        {
            defaultLanguage = LanguageEnum.TRADITIONAL_CHINESE; // 預設正體中文
        }
        else
        {
            defaultLanguage = LanguageEnum.ENGLISH; // 預設英文
        }

        defaultTextOutputFormat = FileFormatEnum.TEXT; // 預設輸出文字檔格式為.txt

        defaultShellScript = "bash"; // 腳本shell種類
        singleScriptWaitTime = 0; // 等待單一任務腳本執行的時間（秒數）


        downloadNovelCover = false;  // 是否下載小說封面圖
        coverSelectAmountIndex = 1; // 要從幾張圖裡面挑選適合的小說封面圖
    }

    // 將目前的設定寫入到設定檔(set.ini)
    public static void writeSetFile()
    {
        Common.debugPrintln( "寫入新的設定檔" );
        String setString = "\n# 下載位置"
                + "\ndownloadDirectory = " + originalDownloadDirectory
                + "\n# 暫存檔位置"
                + "\ntempDirectory = " + tempDirectory
                + "\n# 紀錄檔位置"
                + "\nrecordFileDirectory = " + recordFileDirectory
                + "\n# 下載完畢後自動產生壓縮檔"
                + "\nautoCompress = " + autoCompress
                + "\n# 自動刪除原始圖檔？"
                + "\ndeleteOriginalPic = " + deleteOriginalPic
                + "\n# 檔名的基本長度（不足補零）"
                + "\nfileNameLength = " + fileNameLength
                + "\n# 版面名稱"
                + "\nskinClassName = " + skinClassName
                + "\n# 是否輸出圖片位址？（輸出資料夾與下載位置相同）"
                + "\noutputUrlFile = " + outputUrlFile
                + "\n# 是否下載圖片？"
                + "\ndownloadPicFile = " + downloadPicFile
                + "\n# 是否一併開啟除錯訊息視窗？"
                + "\nopenDebugMessageWindow = " + openDebugMessageWindow
                + "\n# 縮小在工具列時是否顯示下載完成訊息？"
                + "\nshowDoneMessageAtSystemTray = " + showDoneMessageAtSystemTray
                + "\n# 是否保留未完成任務到下一次開啟？"
                + "\nkeepUndoneDownloadMission = " + keepUndoneDownloadMission
                + "\n# 是否保留已完成任務到下一次開啟？"
                + "\nkeepDoneDownloadMission = " + keepDoneDownloadMission
                + "\n# 是否複製網址後就自動解析並詢問欲下載集數？"
                + "\nautoAddMission = " + autoAddMission
                + "\n# 是否保留書籤？"
                + "\nkeepBookmark = " + keepBookmark
                + "\n# 是否保留記錄？"
                + "\nkeepRecord = " + keepRecord
                + "\n# 是否預設勾選全部集數？"
                + "\nchoiceAllVolume = " + choiceAllVolume
                + "\n# 代理伺服器位址（proxy sever address）"
                + "\nproxyServer = " + proxyServer
                + "\n# 代理伺服器連接阜（proxy server port）"
                + "\nproxyPort = " + proxyPort
                + "\n# 預設字型名稱"
                + "\ndefaultFontName = " + defaultFontName
                + "\n# 預設字體大小"
                + "\ndefaultFontSize = " + defaultFontSize
                + "\n# EH會員ID"
                + "\nehMemberID = " + ehMemberID
                + "\n# EH會員密碼Hash"
                + "\nehMemberPasswordHash = " + ehMemberPasswordHash
                + "\n# 逾時計時器倒數秒數（0代表不限）"
                + "\ntimeoutTimer = " + timeoutTimer
                + "\n# 下載失敗重新嘗試下載的次數"
                + "\nretryTimes = " + retryTimes
                + "\n# 預設開啟圖片檔的程式"
                + "\nopenPicFileProgram = " + openPicFileProgram
                + "\n# 預設開啟文件檔的程式"
                + "\nopenTextFileProgram = " + openTextFileProgram
                + "\n# 預設開啟壓縮檔的程式"
                + "\nopenZipFileProgram = " + openZipFileProgram
                + "\n# 是否播放全部任務完成音效"
                + "\nplaySingleDoneAudio = " + playSingleDoneAudio
                + "\n# 是否播放單一任務完成音效"
                + "\nplayAllDoneAudio = " + playAllDoneAudio
                + "\n# 在全部任務完成後播放的外部音效檔"
                + "\nsingleDoneAudioFile = " + singleDoneAudioFile
                + "\n# 在單一任務完成後播放的外部音效檔"
                + "\nallDoneAudioFile = " + allDoneAudioFile
                + "\n# 是否設置主視窗的背景圖片"
                + "\nusingBackgroundPicOfMainFrame = " + usingBackgroundPicOfMainFrame
                + "\n# 是否設置資訊視窗的背景圖片"
                + "\nusingBackgroundPicOfInformationFrame = " + usingBackgroundPicOfInformationFrame
                + "\n# 是否設置選項視窗的背景圖片"
                + "\nusingBackgroundPicOfOptionFrame = " + usingBackgroundPicOfOptionFrame
                + "\n# 是否設置選擇集數視窗的背景圖片"
                + "\nusingBackgroundPicOfChoiceFrame = " + usingBackgroundPicOfChoiceFrame
                + "\n# 主視窗的背景圖片存放路徑"
                + "\nbackgroundPicPathOfMainFrame = " + backgroundPicPathOfMainFrame
                + "\n# 資訊視窗的背景圖片存放路徑"
                + "\nbackgroundPicPathOfInformationFrame = " + backgroundPicPathOfInformationFrame
                + "\n# 選項視窗的背景圖片存放路徑"
                + "\nbackgroundPicPathOfOptionFrame = " + backgroundPicPathOfOptionFrame
                + "\n# 選擇集數視窗的背景圖片存放路徑"
                + "\nbackgroundPicPathOfChoiceFrame = " + backgroundPicPathOfChoiceFrame
                + "\n# 主視窗一般介面的正常字體顏色"
                + "\nmainFrameOtherDefaultColor = " + mainFrameOtherDefaultColor.toString()
                + "\n# 主視窗一般介面在滑鼠碰觸時的字體顏色"
                + "\nmainFrameOtherMouseEnteredColor = " + mainFrameOtherMouseEnteredColor.toString()
                + "\n# 主視窗表格內容的正常字體顏色"
                + "\nmainFrameTableDefaultColor = " + mainFrameTableDefaultColor.toString()
                + "\n# 主視窗表格內容在滑鼠碰觸時的字體顏色"
                + "\nmainFrameTableMouseEnteredColor = " + mainFrameTableMouseEnteredColor.toString()
                + "\n# 主視窗右鍵選單的正常字體顏色"
                + "\nmainFrameMenuItemDefaultColor = " + mainFrameMenuItemDefaultColor.toString()
                + "\n# 資訊視窗一般介面的正常字體顏色"
                + "\ninformationFrameOtherDefaultColor = " + informationFrameOtherDefaultColor.toString()
                + "\n# 資訊視窗一般介面在滑鼠碰觸時的字體顏色"
                + "\ninformationFrameOtherMouseEnteredColor = " + informationFrameOtherMouseEnteredColor.toString()
                + "\n# 選項視窗一般介面的正常字體顏色"
                + "\noptionFrameOtherDefaultColor = " + optionFrameOtherDefaultColor.toString()
                + "\n# 選項視窗一般介面在滑鼠碰觸時的字體顏色"
                + "\noptionFrameOtherMouseEnteredColor = " + optionFrameOtherMouseEnteredColor.toString()
                + "\n# 選擇集數視窗一般介面的正常字體顏色"
                + "\nchoiceFrameOtherDefaultColor = " + choiceFrameOtherDefaultColor.toString()
                + "\n# 選擇集數視窗一般介面在滑鼠碰觸時的字體顏色"
                + "\nchoiceFrameOtherMouseEnteredColor = " + choiceFrameOtherMouseEnteredColor.toString()
                + "\n# 選擇集數視窗表格內容的正常字體顏色"
                + "\nchoiceFrameTableDefaultColor = " + choiceFrameTableDefaultColor.toString()
                + "\n# 選擇集數視窗表格中已下載集數的字體顏色"
                + "\nchoiceFrameTableFileExistedColor = " + choiceFrameTableFileExistedColor.toString()
                + "\n# 選擇集數視窗視窗表格內容在滑鼠碰觸時的字體顏色"
                + "\nchoiceFrameTableMouseEnteredColor = " + choiceFrameTableMouseEnteredColor.toString()
                + "\n# 預設壓縮檔格式"
                + "\ncompressFormat = " + compressFormat
                + "\n# 預設介面語言（0: 繁體中文　1: 簡體中文）"
                + "\ndefaultLanguage = " + defaultLanguage
                + "\n# 預設輸出文字檔格式（0: txt　1: html）"
                + "\ndefaultTextOutputFormat = " + defaultTextOutputFormat
                + "\n# 是否執行全部任務完成腳本"
                + "\nrunSingleDoneScript = " + runSingleDoneScript
                + "\n# 是否執行單一任務完成腳本"
                + "\nrunAllDoneScript = " + runAllDoneScript
                + "\n# 在全部任務完成後執行的外部腳本檔"
                + "\nsingleDoneScriptFile = " + singleDoneScriptFile
                + "\n# 在單一任務完成後執行的外部腳本檔"
                + "\nallDoneScriptFile = " + allDoneScriptFile
                + "\n# 執行腳本的Shell名稱"
                + "\ndefaultShellScript = " + defaultShellScript
                + "\n# 等待單一任務腳本執行的時間（秒數）"
                + "\nsingleScriptWaitTime = " + singleScriptWaitTime
                + "\n# 是否下載小說封面圖"
                + "\ndownloadNovelCover = " + downloadNovelCover
                + "\n# 蒐集小說封面圖的數目索引"
                + "\ncoverSelectAmountIndex = " + coverSelectAmountIndex
                + "\n";

        Common.outputFile( setString, Common.getNowAbsolutePath(), Common.setFileName );
    }

    // 印出目前設定值，除錯用
    public void showSetUpParameter()
    {
        Common.debugPrintln( "-----------------------" );
        Common.debugPrintln( "downloadDirectory = " + originalDownloadDirectory );
        Common.debugPrintln( "tempDirectory = " + tempDirectory );
        Common.debugPrintln( "recordFileDirectory = " + recordFileDirectory );
        Common.debugPrintln( "autoCompress = " + autoCompress );
        Common.debugPrintln( "deleteOriginalPic = " + deleteOriginalPic );
        Common.debugPrintln( "fileNameLength = " + fileNameLength );
        Common.debugPrintln( "lookAndFeel = " + skinClassName );
        Common.debugPrintln( "outputUrlFile = " + outputUrlFile );
        Common.debugPrintln( "downloadPicFile = " + downloadPicFile );
        Common.debugPrintln( "openDebugMessageWindow = " + openDebugMessageWindow );
        Common.debugPrintln( "showDoneMessageAtSystemTray = " + showDoneMessageAtSystemTray );
        Common.debugPrintln( "keepUndoneDownloadMission = " + keepUndoneDownloadMission );
        Common.debugPrintln( "autoAddMission = " + autoAddMission );
        Common.debugPrintln( "keepBookmark = " + keepBookmark );
        Common.debugPrintln( "keepRecord = " + keepRecord );
        Common.debugPrintln( "choiceAllVolume = " + choiceAllVolume );
        Common.debugPrintln( "proxyServer = " + proxyServer );
        Common.debugPrintln( "proxyPort = " + proxyPort );
        Common.debugPrintln( "defaultFontName = " + defaultFontName );
        Common.debugPrintln( "defaultFontSize = " + defaultFontSize );
        Common.debugPrintln( "ehMemberID = " + ehMemberID );
        Common.debugPrintln( "ehMemberPasswordHash = " + ehMemberPasswordHash );
        Common.debugPrintln( "timeoutTimer = " + timeoutTimer );
        Common.debugPrintln( "retryTimes = " + retryTimes );
        Common.debugPrintln( "openPicFileProgram = " + openPicFileProgram );
        Common.debugPrintln( "openTextFileProgram = " + openTextFileProgram );
        Common.debugPrintln( "openZipFileProgram = " + openZipFileProgram );

        // 音效
        Common.debugPrintln( "playSingleDoneAudio = " + playSingleDoneAudio );
        Common.debugPrintln( "playAllDoneAudio = " + playAllDoneAudio );
        Common.debugPrintln( "singleDoneAudioFile = " + singleDoneAudioFile );
        Common.debugPrintln( "allDoneAudioFile = " + allDoneAudioFile );


        // 是否使用背景圖片 
        Common.debugPrintln( "UsingBackgroundPicOfMainFrame = " + usingBackgroundPicOfMainFrame );
        Common.debugPrintln( "UsingBackgroundPicOfInformationFrame = " + usingBackgroundPicOfInformationFrame );
        Common.debugPrintln( "UsingBackgroundPicOfOptionFrame = " + usingBackgroundPicOfOptionFrame );
        Common.debugPrintln( "UsingBackgroundPicOfChoiceFrame = " + usingBackgroundPicOfChoiceFrame );

        // 背景圖片的存放路徑
        Common.debugPrintln( "BackgroundPicPathOfMainFrame = " + backgroundPicPathOfMainFrame );
        Common.debugPrintln( "BackgroundPicPathOfInformationFrame = " + backgroundPicPathOfInformationFrame );
        Common.debugPrintln( "BackgroundPicPathOfOptionFrame = " + backgroundPicPathOfOptionFrame );
        Common.debugPrintln( "BackgroundPicPathOfChoiceFrame = " + backgroundPicPathOfChoiceFrame );

        // 使用背景圖片時的字體顏色搭配
        Common.debugPrintln( "ChoiceFrameOtherMouseEnteredColor = " + choiceFrameOtherMouseEnteredColor.toString() );
        Common.debugPrintln( "ChoiceFrameOtherDefaultColor = " + choiceFrameOtherDefaultColor.toString() );
        Common.debugPrintln( "ChoiceFrameTableMouseEnteredColor = " + choiceFrameTableMouseEnteredColor.toString() );
        Common.debugPrintln( "ChoiceFrameTableDefaultColor = " + choiceFrameTableDefaultColor.toString() );
        Common.debugPrintln( "choiceFrameTableFileExistedColor = " + choiceFrameTableFileExistedColor.toString() );
        Common.debugPrintln( "OptionFrameOtherMouseEnteredColor = " + optionFrameOtherMouseEnteredColor.toString() );
        Common.debugPrintln( "OptionFrameOtherDefaultColor = " + optionFrameOtherDefaultColor.toString() );
        Common.debugPrintln( "InformationFrameOtherMouseEnteredColor = " + informationFrameOtherMouseEnteredColor.toString() );
        Common.debugPrintln( "InformationFrameOtherDefaultColor = " + informationFrameOtherDefaultColor.toString() );
        Common.debugPrintln( "MainFrameOtherMouseEnteredColor = " + mainFrameOtherMouseEnteredColor.toString() );
        Common.debugPrintln( "MainFrameOtherDefaultColor = " + mainFrameOtherDefaultColor.toString() );
        Common.debugPrintln( "MainFrameTableMouseEnteredColor = " + mainFrameTableMouseEnteredColor.toString() );
        Common.debugPrintln( "MainFrameTableDefaultColor = " + mainFrameTableDefaultColor.toString() );
        Common.debugPrintln( "MainFrameMenuItemDefaultColor = " + mainFrameMenuItemDefaultColor.toString() );

        Common.debugPrintln( "CompressFormat = " + compressFormat );
        Common.debugPrintln( "DefaultLanguage = " + defaultLanguage );
        Common.debugPrintln( "DefaultTextOutputFormat = " + defaultTextOutputFormat );

        // 腳本
        Common.debugPrintln( "runSingleDoneScript = " + runSingleDoneScript );
        Common.debugPrintln( "runAllDoneScript = " + runAllDoneScript );
        Common.debugPrintln( "singleDoneScriptFile = " + singleDoneScriptFile );
        Common.debugPrintln( "allDoneScriptFile = " + allDoneScriptFile );

        Common.debugPrintln( "defaultShellScript = " + defaultShellScript ); // 執行腳本的Shell名稱
        Common.debugPrintln( "singleScriptWaitTime = " + singleScriptWaitTime ); // 等待單一任務腳本執行的時間（秒數）

        // 小說封面
        Common.debugPrintln( "downloadNovelCover = " + downloadNovelCover );
        Common.debugPrintln( "coverSelectAmountIndex = " + coverSelectAmountIndex );

        Common.debugPrintln( "-----------------------" );
    }

    // 讀入設定檔並依讀入資料來更新目前設定
    public void readSetFile()
    {
        Common.debugPrintln( "SET路徑：" + Common.getNowAbsolutePath() + Common.setFileName );
        if ( !new File( Common.getNowAbsolutePath() + Common.setFileName ).exists() )
        {
            Common.debugPrintln( "找不到set.ini，故自動產生" );
            writeSetFile();
        }

        String[] lines = Common.getFileStrings( getRecordFileDirectory(), Common.setFileName );

        // 為了讓之前的版本也能產生新的set.ini所做的修改
        boolean existKeepBookmark = false;
        boolean existKeepRecord = false;
        boolean existAutoAddMission = false;
        boolean existProxyServer = false;
        boolean existProxyPort = false;
        boolean existDefaultFontName = false;
        boolean existDefaultFontSize = false;
        boolean existEhMemberID = false;
        boolean existEhMemberPasswordHash = false;
        //boolean existSettingFileDirectory = false;
        boolean existTimeoutTimer = false;
        boolean existChoiceAllVolume = false;
        boolean existRetryTimes = false;
        boolean existOpenPicFileProgram = false;
        boolean existOpenTextFileProgram = false;
        boolean existOpenZipFileProgram = false;
        boolean existPlayAllDoneAudio = false;
        boolean existPlaySingleDoneAudio = false;
        boolean existAllDoneAudioFile = false;
        boolean existSingleDoneAudioFile = false;

        // 是否存在腳本設定
        boolean existRunAllDoneScript = false;
        boolean existRunSingleDoneScript = false;
        boolean existAllDoneScriptFile = false;
        boolean existSingleDoneScriptFile = false;


        // 是否使用背景圖片 
        boolean existUsingBackgroundPicOfMainFrame = false;
        boolean existUusingBackgroundPicOfInformationFrame = false;
        boolean existUusingBackgroundPicOfOptionFrame = false;
        boolean existUusingBackgroundPicOfChoiceFrame = false;

        // 背景圖片的存放路徑
        boolean existBackgroundPicPathOfMainFrame = false;
        boolean existBackgroundPicPathOfInformationFrame = false;
        boolean existBackgroundPicPathOfOptionFrame = false;
        boolean existBackgroundPicPathOfChoiceFrame = false;

        // 使用背景圖片時的字體顏色搭配
        boolean existChoiceFrameOtherMouseEnteredColor = false;
        boolean existChoiceFrameOtherDefaultColor = false;
        boolean existChoiceFrameTableMouseEnteredColor = false;
        boolean existChoiceFrameTableDefaultColor = false;
        boolean existChoiceFrameTableFileExistedColor = false;
        boolean existOptionFrameOtherMouseEnteredColor = false;
        boolean existOptionFrameOtherDefaultColor = false;
        boolean existInformationFrameOtherMouseEnteredColor = false;
        boolean existInformationFrameOtherDefaultColor = false;
        boolean existMainFrameOtherMouseEnteredColor = false;
        boolean existMainFrameOtherDefaultColor = false;
        boolean existMainFrameTableMouseEnteredColor = false;
        boolean existMainFrameTableDefaultColor = false;
        boolean existMainFrameMenuItemDefaultColor = false;

        boolean existCompressFormat = false;  // 預設壓縮格式
        boolean existDefaultLanguage = false; // 預設介面語言
        boolean existDefaultTextOutputFormat = false; // 預設輸出文字檔格式

        boolean existDefaultShellScript = false; // 預設執行腳本的Shell名稱
        boolean existSingleScriptWaitTime = false; // 等待單一任務腳本執行的時間（秒數）

        boolean existDownloadNovelCover = false; // 是否下載小說封面圖
        boolean existCoverSelectAmountIndex = false; // 蒐集小說封面圖的數量索引


        for ( int i = 0; i < lines.length; i++ )
        {
            try
            {
                if ( lines[i].length() > 2 && !lines[i].matches( "(?s).*#(?s).*" ) )
                {
                    String[] split = lines[i].split( "\\s*=\\s*" );

                    if ( split[0].equals( "downloadDirectory" ) )
                    {
                        String path = "";
                        // 如果最後已經有斜線，就不另外加斜線了
                        if ( Common.getAbsolutePath( split[1] ).matches( "(?s).*" + Common.getRegexSlash() ) )
                        {
                            path = Common.getAbsolutePath( split[1] );
                        }
                        else
                        {
                            path = Common.getAbsolutePath( split[1] ) + Common.getSlash();
                        }

                        originalDownloadDirectory = downloadDirectory = path;
                    }
                    else if ( split[0].equals( "autoCompress" ) )
                    {
                        autoCompress = (new Boolean( split[1] )).booleanValue();
                    }
                    else if ( split[0].equals( "deleteOriginalPic" ) )
                    {
                        deleteOriginalPic = (new Boolean( split[1] )).booleanValue();
                    }
                    else if ( split[0].equals( "fileNameLength" ) )
                    {
                        try
                        {
                            // 只接受1~9
                            fileNameLength = Integer.parseInt( String.valueOf( split[1].charAt( 0 ) ) );
                        }
                        catch ( Exception ex )
                        {
                            fileNameLength = 3; // 預設值
                            ex.printStackTrace();
                        }
                    }
                    else if ( split[0].equals( "skinClassName" ) )
                    {
                        int skinOrder = new CommonGUI().getSkinOrderBySkinClassName( split[1] );
                        if ( skinOrder != -1 )
                        {
                            skinClassName = split[1];
                            //Common.debugPrintln( "將讀取" + split[1] + "介面" );
                        }
                        else
                        {
                            Common.debugPrintln( "找不到" + split[1] + "介面 !!" );
                        }

                        /*
                         if ( CommonGUI.getGTKSkinOrder() != -1 ) //
                         若有gtk就優先選gtk版面
                         {
                         skinClassName =
                         "com.sun.java.swing.plaf.gtk.GTKLookAndFeel";
                         }
                         */

                        //ComicDownGUI.setDefaultSkinClassName( skinClassName );
                    }
                    else if ( split[0].equals( "outputUrlFile" ) )
                    {
                        if ( new Boolean( split[1] ).booleanValue() )
                        {
                            outputUrlFile = Boolean.valueOf( split[1] );
                        }
                    }
                    else if ( split[0].equals( "downloadPicFile" ) )
                    {
                        //if ( new Boolean( split[1] ).booleanValue() )
                        downloadPicFile = Boolean.valueOf( split[1] );
                    }
                    else if ( split[0].equals( "openDebugMessageWindow" ) )
                    {
                        if ( new Boolean( split[1] ).booleanValue() )
                        {
                            openDebugMessageWindow = Boolean.valueOf( split[1] );
                        }
                    }
                    else if ( split[0].equals( "showDoneMessageAtSystemTray" ) )
                    {
                        if ( split[1].matches( "(?s).*true(?s).*" ) )
                        {
                            setShowDoneMessageAtSystemTray( true );
                        }
                        else
                        {
                            setShowDoneMessageAtSystemTray( false );
                        }
                    }
                    else if ( split[0].equals( "keepUndoneDownloadMission" ) )
                    {
                        if ( split[1].matches( "(?s).*true(?s).*" ) )
                        {
                            setKeepUndoneDownloadMission( true );
                        }
                        else
                        {
                            setKeepUndoneDownloadMission( false );
                        }
                    }
                    else if ( split[0].equals( "keepDoneDownloadMission" ) )
                    {
                     
                        if ( split[1].matches( "(?s).*true(?s).*" ) )
                        {
                            setKeepDoneDownloadMission( true );
                        }
                        else
                        {
                            setKeepDoneDownloadMission( false );
                        }
                    }
                    else if ( split[0].equals( "proxyServer" ) )
                    {
                        existProxyServer = true;
                        if ( split.length > 1 )
                        {
                            setProxyServer( split[1] );
                        }
                    }
                    else if ( split[0].equals( "proxyPort" ) )
                    {
                        existProxyPort = true;
                        if ( split.length > 1 && split[1].matches( "\\s*\\d+\\s*" ) )
                        {
                            setProxyPort( split[1] );
                        }
                        else
                        {
                            setProxyServer( "" );
                        }
                    }
                    else if ( split[0].equals( "autoAddMission" ) )
                    {
                        existAutoAddMission = true;
                        if ( split[1].matches( "(?s).*true(?s).*" ) )
                        {
                            setAutoAddMission( true );
                        }
                        else
                        {
                            setAutoAddMission( false );
                        }
                    }
                    else if ( split[0].equals( "keepBookmark" ) )
                    {
                        existKeepBookmark = true;
                        if ( split[1].matches( "(?s).*true(?s).*" ) )
                        {
                            setKeepBookmark( true );
                        }
                        else
                        {
                            setKeepBookmark( false );
                        }
                    }
                    else if ( split[0].equals( "keepRecord" ) )
                    {
                        existKeepRecord = true;
                        if ( split[1].matches( "(?s).*true(?s).*" ) )
                        {
                            setKeepRecord( true );
                        }
                        else
                        {
                            setKeepRecord( false );
                        }
                    }
                    else if ( split[0].equals( "choiceAllVolume" ) )
                    {
                        existChoiceAllVolume = true;
                        if ( split[1].matches( "(?s).*true(?s).*" ) )
                        {
                            setChoiceAllVolume( true );
                        }
                        else
                        {
                            setChoiceAllVolume( false );
                        }
                    }
                    else if ( split[0].equals( "defaultFontName" ) )
                    {
                        existDefaultFontName = true;
                        setDefaultFontName( split[1] );
                    }
                    else if ( split[0].equals( "defaultFontSize" ) )
                    {
                        existDefaultFontSize = true;
                        setDefaultFontSize( Integer.parseInt( split[1] ) );
                    }
                    else if ( split[0].equals( "ehMemberID" ) )
                    {
                        existEhMemberID = true;
                        setEhMemberID( split[1] );
                    }
                    else if ( split[0].equals( "ehMemberPasswordHash" ) )
                    {
                        existEhMemberPasswordHash = true;
                        setEhMemberPasswordHash( split[1] );
                    }
                    else if ( split[0].equals( "timeoutTimer" ) )
                    {
                        existTimeoutTimer = true;
                        setTimeoutTimer( Integer.parseInt( split[1] ) );
                    }
                    else if ( split[0].equals( "retryTimes" ) )
                    {
                        existRetryTimes = true;
                        setRetryTimes( Integer.parseInt( split[1] ) );
                    }
                    else if ( split[0].equals( "openPicFileProgram" ) )
                    {
                        existOpenPicFileProgram = true;
                        if ( split.length > 1 )
                        {
                            setOpenPicFileProgram( split[1] );

                        }
                    }
                    else if ( split[0].equals( "openTextFileProgram" ) )
                    {
                        existOpenTextFileProgram = true;
                        if ( split.length > 1 )
                        {
                            setOpenTextFileProgram( split[1] );

                        }
                    }
                    else if ( split[0].equals( "openZipFileProgram" ) )
                    {
                        existOpenZipFileProgram = true;
                        if ( split.length > 1 )
                        {
                            setOpenZipFileProgram( split[1] );

                        }
                    }
                    else if ( split[0].equals( "playSingleDoneAudio" ) )
                    {
                        existPlaySingleDoneAudio = true;
                        if ( split.length > 1 )
                        {
                            boolean value = split[1].matches( "(?s).*true(?s).*" ) ? true : false;
                            setPlaySingleDoneAudio( value );

                        }
                    }
                    else if ( split[0].equals( "playAllDoneAudio" ) )
                    {
                        existPlayAllDoneAudio = true;
                        if ( split.length > 1 )
                        {
                            boolean value = split[1].matches( "(?s).*true(?s).*" ) ? true : false;
                            setPlayAllDoneAudio( value );

                        }
                    }
                    else if ( split[0].equals( "singleDoneAudioFile" ) )
                    {
                        existSingleDoneAudioFile = true;
                        if ( split.length > 1 )
                        {
                            setSingleDoneAudioFile( split[1] );

                        }
                    }
                    else if ( split[0].equals( "allDoneAudioFile" ) )
                    {
                        existAllDoneAudioFile = true;
                        if ( split.length > 1 )
                        {
                            setAllDoneAudioFile( split[1] );

                        }
                    }
                    // 是否使用背景圖片
                    else if ( split[0].equals( "usingBackgroundPicOfMainFrame" ) )
                    {
                        existUsingBackgroundPicOfMainFrame = true;
                        if ( split.length > 1 )
                        {
                            boolean value = Boolean.valueOf( split[1] );
                            setUsingBackgroundPicOfMainFrame( value );

                        }
                    }
                    else if ( split[0].equals( "usingBackgroundPicOfInformationFrame" ) )
                    {
                        existUusingBackgroundPicOfInformationFrame = true;
                        if ( split.length > 1 )
                        {
                            boolean value = Boolean.valueOf( split[1] );
                            setUsingBackgroundPicOfInformationFrame( value );

                        }
                    }
                    else if ( split[0].equals( "usingBackgroundPicOfOptionFrame" ) )
                    {
                        existUusingBackgroundPicOfOptionFrame = true;
                        if ( split.length > 1 )
                        {
                            boolean value = Boolean.valueOf( split[1] );
                            setUsingBackgroundPicOfOptionFrame( value );

                        }
                    }
                    else if ( split[0].equals( "usingBackgroundPicOfChoiceFrame" ) )
                    {
                        existUusingBackgroundPicOfChoiceFrame = true;
                        if ( split.length > 1 )
                        {
                            boolean value = Boolean.valueOf( split[1] );
                            setUsingBackgroundPicOfChoiceFrame( value );

                        }
                    }
                    // 背景圖片的存放路徑
                    else if ( split[0].equals( "backgroundPicPathOfMainFrame" ) )
                    {
                        existBackgroundPicPathOfMainFrame = true;
                        if ( split.length > 1 )
                        {
                            setBackgroundPicPathOfMainFrame( split[1] );

                        }
                    }
                    else if ( split[0].equals( "backgroundPicPathOfInformationFrame" ) )
                    {
                        existBackgroundPicPathOfInformationFrame = true;
                        if ( split.length > 1 )
                        {
                            setBackgroundPicPathOfInformationFrame( split[1] );

                        }
                    }
                    else if ( split[0].equals( "backgroundPicPathOfOptionFrame" ) )
                    {
                        existBackgroundPicPathOfOptionFrame = true;
                        if ( split.length > 1 )
                        {
                            setBackgroundPicPathOfOptionFrame( split[1] );

                        }
                    }
                    else if ( split[0].equals( "backgroundPicPathOfChoiceFrame" ) )
                    {
                        existBackgroundPicPathOfChoiceFrame = true;
                        if ( split.length > 1 )
                        {
                            setBackgroundPicPathOfChoiceFrame( split[1] );

                        }
                    }
                    // 使用背景圖片時的字體顏色搭配
                    else if ( split[0].equals( "mainFrameOtherDefaultColor" ) )
                    {
                        existMainFrameOtherDefaultColor = true;
                        if ( split.length > 1 )
                        {
                            int beginIndex = lines[i].indexOf( "=" ) + 1;
                            String colorString =
                                    lines[i].substring( beginIndex, lines[i].length() );

                            Color color = Common.getColor( colorString );
                            setMainFrameOtherDefaultColor( color );

                        }
                    }
                    else if ( split[0].equals( "mainFrameOtherMouseEnteredColor" ) )
                    {
                        existMainFrameOtherMouseEnteredColor = true;
                        if ( split.length > 1 )
                        {
                            int beginIndex = lines[i].indexOf( "=" ) + 1;
                            String colorString =
                                    lines[i].substring( beginIndex, lines[i].length() );

                            Color color = Common.getColor( colorString );
                            setMainFrameOtherMouseEnteredColor( color );

                        }
                    }
                    else if ( split[0].equals( "mainFrameTableDefaultColor" ) )
                    {
                        existMainFrameTableDefaultColor = true;
                        if ( split.length > 1 )
                        {
                            int beginIndex = lines[i].indexOf( "=" ) + 1;
                            String colorString =
                                    lines[i].substring( beginIndex, lines[i].length() );

                            Color color = Common.getColor( colorString );
                            setMainFrameTableDefaultColor( color );

                        }
                    }
                    else if ( split[0].equals( "mainFrameTableMouseEnteredColor" ) )
                    {
                        existMainFrameTableMouseEnteredColor = true;
                        if ( split.length > 1 )
                        {
                            int beginIndex = lines[i].indexOf( "=" ) + 1;
                            String colorString =
                                    lines[i].substring( beginIndex, lines[i].length() );

                            Color color = Common.getColor( colorString );
                            setMainFrameTableMouseEnteredColor( color );

                        }
                    }
                    else if ( split[0].equals( "mainFrameMenuItemDefaultColor" ) )
                    {
                        existMainFrameMenuItemDefaultColor = true;
                        if ( split.length > 1 )
                        {
                            int beginIndex = lines[i].indexOf( "=" ) + 1;
                            String colorString =
                                    lines[i].substring( beginIndex, lines[i].length() );

                            Color color = Common.getColor( colorString );
                            setMainFrameMenuItemDefaultColor( color );

                        }
                    }
                    else if ( split[0].equals( "informationFrameOtherDefaultColor" ) )
                    {
                        existInformationFrameOtherDefaultColor = true;
                        if ( split.length > 1 )
                        {
                            int beginIndex = lines[i].indexOf( "=" ) + 1;
                            String colorString =
                                    lines[i].substring( beginIndex, lines[i].length() );

                            Color color = Common.getColor( colorString );
                            setInformationFrameOtherDefaultColor( color );

                        }
                    }
                    else if ( split[0].equals( "informationFrameOtherMouseEnteredColor" ) )
                    {
                        existInformationFrameOtherMouseEnteredColor = true;
                        if ( split.length > 1 )
                        {
                            int beginIndex = lines[i].indexOf( "=" ) + 1;
                            String colorString =
                                    lines[i].substring( beginIndex, lines[i].length() );

                            Color color = Common.getColor( colorString );
                            setInformationFrameOtherMouseEnteredColor( color );

                        }
                    }
                    else if ( split[0].equals( "optionFrameOtherDefaultColor" ) )
                    {
                        existOptionFrameOtherDefaultColor = true;
                        if ( split.length > 1 )
                        {
                            int beginIndex = lines[i].indexOf( "=" ) + 1;
                            String colorString =
                                    lines[i].substring( beginIndex, lines[i].length() );

                            Color color = Common.getColor( colorString );
                            setOptionFrameOtherDefaultColor( color );

                        }
                    }
                    else if ( split[0].equals( "optionFrameOtherMouseEnteredColor" ) )
                    {
                        existOptionFrameOtherMouseEnteredColor = true;
                        if ( split.length > 1 )
                        {
                            int beginIndex = lines[i].indexOf( "=" ) + 1;
                            String colorString =
                                    lines[i].substring( beginIndex, lines[i].length() );

                            Color color = Common.getColor( colorString );
                            setOptionFrameOtherMouseEnteredColor( color );

                        }
                    }
                    else if ( split[0].equals( "choiceFrameOtherDefaultColor" ) )
                    {
                        existChoiceFrameOtherDefaultColor = true;
                        if ( split.length > 1 )
                        {
                            int beginIndex = lines[i].indexOf( "=" ) + 1;
                            String colorString =
                                    lines[i].substring( beginIndex, lines[i].length() );

                            Color color = Common.getColor( colorString );
                            setChoiceFrameOtherDefaultColor( color );

                        }
                    }
                    else if ( split[0].equals( "choiceFrameOtherMouseEnteredColor" ) )
                    {
                        existChoiceFrameOtherMouseEnteredColor = true;
                        if ( split.length > 1 )
                        {
                            int beginIndex = lines[i].indexOf( "=" ) + 1;
                            String colorString =
                                    lines[i].substring( beginIndex, lines[i].length() );

                            Color color = Common.getColor( colorString );
                            setChoiceFrameOtherMouseEnteredColor( color );

                        }
                    }
                    else if ( split[0].equals( "choiceFrameTableDefaultColor" ) )
                    {
                        existChoiceFrameTableDefaultColor = true;
                        if ( split.length > 1 )
                        {
                            int beginIndex = lines[i].indexOf( "=" ) + 1;
                            String colorString =
                                    lines[i].substring( beginIndex, lines[i].length() );

                            Color color = Common.getColor( colorString );
                            setChoiceFrameTableDefaultColor( color );

                        }
                    }
                    else if ( split[0].equals( "choiceFrameTableFileExistedColor" ) )
                    {
                        existChoiceFrameTableFileExistedColor = true;
                        if ( split.length > 1 )
                        {
                            int beginIndex = lines[i].indexOf( "=" ) + 1;
                            String colorString =
                                    lines[i].substring( beginIndex, lines[i].length() );

                            Color color = Common.getColor( colorString );
                            setChoiceFrameTableFileExistedColor( color );

                        }
                    }
                    else if ( split[0].equals( "choiceFrameTableMouseEnteredColor" ) )
                    {
                        existChoiceFrameTableMouseEnteredColor = true;
                        if ( split.length > 1 )
                        {
                            int beginIndex = lines[i].indexOf( "=" ) + 1;
                            String colorString =
                                    lines[i].substring( beginIndex, lines[i].length() );

                            Color color = Common.getColor( colorString );
                            setChoiceFrameTableMouseEnteredColor( color );

                        }
                    }
                    else if ( split[0].equals( "compressFormat" ) )
                    {
                        existCompressFormat = true;
                        if ( split.length > 1 )
                        {
                            setCompressFormat( split[1] );

                        }
                    }
                    else if ( split[0].equals( "defaultLanguage" ) )
                    {
                        existDefaultLanguage = true;
                        if ( split.length > 1 )
                        {

                            setDefaultLanguage( Integer.parseInt( split[1] ) );
                        }
                    }
                    else if ( split[0].equals( "defaultTextOutputFormat" ) )
                    {
                        existDefaultTextOutputFormat = true;
                        if ( split.length > 1 )
                        {

                            setDefaultTextOutputFormat( Integer.parseInt( split[1] ) );
                        }
                    }
                    // 寫入腳本設定
                    else if ( split[0].equals( "runSingleDoneScript" ) )
                    {
                        existRunSingleDoneScript = true;
                        if ( split.length > 1 )
                        {
                            boolean value = split[1].matches( "(?s).*true(?s).*" ) ? true : false;
                            setRunSingleDoneScript( value );

                        }
                    }
                    else if ( split[0].equals( "runAllDoneScript" ) )
                    {
                        existRunAllDoneScript = true;
                        if ( split.length > 1 )
                        {
                            boolean value = split[1].matches( "(?s).*true(?s).*" ) ? true : false;
                            setRunAllDoneScript( value );

                        }
                    }
                    else if ( split[0].equals( "singleDoneScriptFile" ) )
                    {
                        existSingleDoneScriptFile = true;
                        if ( split.length > 1 )
                        {
                            setSingleDoneScriptFile( split[1] );

                        }
                    }
                    else if ( split[0].equals( "allDoneScriptFile" ) )
                    {
                        existAllDoneScriptFile = true;
                        if ( split.length > 1 )
                        {
                            setAllDoneScriptFile( split[1] );

                        }
                    }
                    else if ( split[0].equals( "defaultShellScript" ) )
                    {
                        existDefaultShellScript = true;
                        if ( split.length > 1 )
                        {
                            setDefaultShellScript( split[1] );

                        }
                    }
                    else if ( split[0].equals( "singleScriptWaitTime" ) )
                    {
                        existSingleScriptWaitTime = true;
                        if ( split.length > 1 )
                        {
                            setSingleScriptWaitTime( Integer.parseInt( split[1] ) );

                        }
                    }
                    else if ( split[0].equals( "downloadNovelCover" ) )
                    {
                        existDownloadNovelCover = true;
                        if ( split.length > 1 )
                        {
                            boolean value = split[1].matches( "(?s).*true(?s).*" ) ? true : false;
                            setDownloadNovelCover( value );

                        }
                    }
                    else if ( split[0].equals( "coverSelectAmountIndex" ) )
                    {
                        existCoverSelectAmountIndex = true;
                        if ( split.length > 1 )
                        {
                            setCoverSelectAmountIndex( Integer.parseInt( split[1] ) );
                        }
                    }
                }
            }
            catch ( Exception ex )
            {
                Common.debugPrintln( "讀取設定檔發生錯誤! 套用預設值!" );
                ex.printStackTrace();
                writeSetFile(); // 以目前設定值對設定檔進行覆寫
            }
        }

        if ( existAutoAddMission
                && existKeepBookmark && existKeepRecord
                && existProxyServer && existProxyPort
                && existDefaultFontName && existDefaultFontSize
                && existEhMemberID && existEhMemberPasswordHash
                //&& existSettingFileDirectory 
                && existTimeoutTimer
                && existChoiceAllVolume && existRetryTimes
                && existOpenPicFileProgram && existOpenZipFileProgram && existOpenTextFileProgram
                && existPlayAllDoneAudio
                && existPlaySingleDoneAudio
                && existAllDoneAudioFile
                && existSingleDoneAudioFile
                // 是否使用背景圖片 
                && existUsingBackgroundPicOfMainFrame
                && existUusingBackgroundPicOfInformationFrame
                && existUusingBackgroundPicOfOptionFrame
                && existUusingBackgroundPicOfChoiceFrame
                // 背景圖片的存放路徑
                && existBackgroundPicPathOfMainFrame
                && existBackgroundPicPathOfInformationFrame
                && existBackgroundPicPathOfOptionFrame
                && existBackgroundPicPathOfChoiceFrame
                // 使用背景圖片時的字體顏色搭配
                && existChoiceFrameOtherMouseEnteredColor
                && existChoiceFrameOtherDefaultColor
                && existChoiceFrameTableMouseEnteredColor
                && existChoiceFrameTableDefaultColor
                && existChoiceFrameTableFileExistedColor
                && existOptionFrameOtherMouseEnteredColor
                && existOptionFrameOtherDefaultColor
                && existInformationFrameOtherMouseEnteredColor
                && existInformationFrameOtherDefaultColor
                && existMainFrameOtherMouseEnteredColor
                && existMainFrameOtherDefaultColor
                && existMainFrameTableMouseEnteredColor
                && existMainFrameTableDefaultColor
                && existMainFrameMenuItemDefaultColor
                && existCompressFormat // 預設壓縮格式
                && existDefaultLanguage // 預設介面語言
                && existDefaultTextOutputFormat // 預設文字檔輸出格式 

                // 是否存在腳本設定
                && existRunAllDoneScript
                && existRunSingleDoneScript
                && existAllDoneScriptFile
                && existSingleDoneScriptFile
                && existDefaultShellScript // 是否存在執行腳本的Shell名稱
                && existSingleScriptWaitTime // 等待單一任務腳本執行的時間（秒數）
                )
        {
            Common.debugPrintln( "設定檔全部讀取完畢" );
        }
        else
        {
            Common.debugPrintln( "設定檔缺乏新版參數! 套用預設值!" );
            writeSetFile(); // 以目前設定值對設定檔進行覆寫
        }
        //showSetUpParameter(); // 顯示設定參數，除錯用
    }

    // 設置下載集數，GUI版用不到，目前已廢棄...
    public static void setDownloadVolume( String begin, String end )
    {
        beginVolume = Integer.parseInt( begin );
        endVolume = Integer.parseInt( end );
    }

    // 設置新的起始路徑（預設是./down/）
    public static void setOriginalDownloadDirectory( String newOriginalDownloadDirectory )
    {
        originalDownloadDirectory = newOriginalDownloadDirectory;
    }

    public static String getOriginalDownloadDirectory()
    {
        return originalDownloadDirectory;
    }

    // 設置新的圖片存放路徑
    //public static void setDownloadDirectory( String dir ) {
    //    downloadDirectory = originalDownloadDirectory;
    //    downloadDirectory += dir;
    //}
    public static String getDownloadDirectory()
    {
        return downloadDirectory;
    }

    public static String getRecordFileDirectory()
    {
        return Common.getNowAbsolutePath();
    }

    public static String getTempDirectory()
    {
        // 回傳 本身目錄/temp/
        return Common.getNowAbsolutePath() + "temp" + Common.getSlash();
    }

    // 設置版面
    public static void setSkinClassName( String newSkinClassName )
    {
        skinClassName = newSkinClassName;
        //Common.debugPrintln( "預設介面設置為：" + skinClassName );
    }

    public static String getSkinClassName()
    {
        return skinClassName;
    }

    // 設置是否自動產生壓縮檔
    public static void setAutoCompress( boolean newAutoCompress )
    {
        autoCompress = newAutoCompress;
    }

    public static boolean getAutoCompress()
    {
        return autoCompress;
    }

    // 設置是否自動刪除圖檔
    public static void setDeleteOriginalPic( boolean newDeleteOriginalPic )
    {
        deleteOriginalPic = newDeleteOriginalPic;
    }

    public static boolean getDeleteOriginalPic()
    {
        return deleteOriginalPic;
    }

    // 設置是否自動輸出圖檔位址
    public static void setOutputUrlFile( boolean newOutputUrlFile )
    {
        outputUrlFile = newOutputUrlFile;
    }

    public static boolean getOutputUrlFile()
    {
        return outputUrlFile;
    }

    // 是否下載圖片
    public static void setDownloadPicFile( boolean newDownloadPicFile )
    {
        downloadPicFile = newDownloadPicFile;
    }

    public static boolean getDownloadPicFile()
    {
        return downloadPicFile;
    }

    // 設置是否一併開啟除錯訊息視窗
    public static void setOpenDebugMessageWindow( boolean newOpenDebugMessageWindow )
    {
        openDebugMessageWindow = newOpenDebugMessageWindow;
    }

    public static boolean getOpenDebugMessageWindow()
    {
        return openDebugMessageWindow;
    }

    // 縮小在工具列時是否顯示下載完成訊息
    public static void setShowDoneMessageAtSystemTray( boolean newShowDoneMessageAtSystemTray )
    {
        showDoneMessageAtSystemTray = newShowDoneMessageAtSystemTray;
    }

    public static boolean getShowDoneMessageAtSystemTray()
    {
        return showDoneMessageAtSystemTray;
    }

    // 是否保留未完成任務到下一次開啟
    public static void setKeepUndoneDownloadMission( boolean newKeepUndoneDownloadMission )
    {
        keepUndoneDownloadMission = newKeepUndoneDownloadMission;
    }

    public static boolean getKeepUndoneDownloadMission()
    {
        return keepUndoneDownloadMission;
    }

    // 是否保留已完成任務到下一次開啟
    public static void setKeepDoneDownloadMission( boolean newKeepDoneDownloadMission )
    {
        keepDoneDownloadMission = newKeepDoneDownloadMission;
    }

    public static boolean getKeepDoneDownloadMission()
    {
        return keepDoneDownloadMission;
    }

    // 是否自動加入任務（實驗功能）
    public static void setAutoAddMission( boolean newAutoAddMission )
    {
        autoAddMission = newAutoAddMission;
    }

    public static boolean getAutoAddMission()
    {
        return autoAddMission;
    }

    // 是否儲存書籤
    public static void setKeepBookmark( boolean newKeepBookmark )
    {
        keepBookmark = newKeepBookmark;
    }

    public static boolean getKeepBookmark()
    {
        return keepBookmark;
    }

    // 是否儲存記錄
    public static void setKeepRecord( boolean newKeepRecord )
    {
        keepRecord = newKeepRecord;
    }

    public static boolean getKeepRecord()
    {
        return keepRecord;
    }

    // 是否預設勾選全部集數
    public static void setChoiceAllVolume( boolean newChoiceAllVolume )
    {
        choiceAllVolume = newChoiceAllVolume;
    }

    public static boolean getChoiceAllVolume()
    {
        return choiceAllVolume;
    }

    // 設定代理伺服器
    public static String getProxyServer()
    {
        return proxyServer;
    }

    public static void setProxyServer( String server )
    {
        proxyServer = server;
    }

    public static String getProxyPort()
    {
        return proxyPort;
    }

    public static void setProxyPort( String port )
    {
        proxyPort = port;
    }

    public static String getDefaultFontName()
    {
        return defaultFontName;
    }

    public static void setDefaultFontName( String fontName )
    {
        defaultFontName = fontName;
    }

    public static Font getDefaultFont()
    {
        return new Font( getDefaultFontName(), Font.PLAIN, getDefaultFontSize() );
    }

    public static Font getDefaultBoldFont()
    {
        return new Font( getDefaultFontName(), Font.BOLD, getDefaultFontSize() );
    }

    public static Font getDefaultFont( int offset )
    { // 畢竟主界面按鈕名稱的字體要比較大......
        return new Font( getDefaultFontName(), Font.PLAIN, getDefaultFontSize() + offset );
    }

    public static int getDefaultFontSize()
    {
        return defaultFontSize;
    }

    public static void setDefaultFontSize( int fontSize )
    {
        defaultFontSize = fontSize;
    }

    public static String getEhMemberID()
    {
        return ehMemberID;
    }

    public static void setEhMemberID( String id )
    {
        ehMemberID = id;
    }

    public static String getEhMemberPasswordHash()
    {
        return ehMemberPasswordHash;
    }

    public static void setEhMemberPasswordHash( String hash )
    {
        ehMemberPasswordHash = hash;
    }

    public static int getTimeoutTimer()
    {
        return timeoutTimer;
    }

    public static void setTimeoutTimer( int timer )
    {
        timeoutTimer = timer;
    }

    public static int getRetryTimes()
    {
        return retryTimes;
    }

    public static void setRetryTimes( int times )
    {
        retryTimes = times;
    }

    public static String getOpenPicFileProgram()
    {
        return openPicFileProgram;
    }

    public static void setOpenPicFileProgram( String program )
    {
        openPicFileProgram = program;
    }

    public static String getOpenTextFileProgram()
    {
        return openTextFileProgram;
    }

    public static void setOpenTextFileProgram( String program )
    {
        openTextFileProgram = program;
    }

    public static String getOpenZipFileProgram()
    {
        return openZipFileProgram;
    }

    public static void setOpenZipFileProgram( String program )
    {
        openZipFileProgram = program;
    }

    // 設置音效
    public static boolean getPlaySingleDoneAudio()
    {
        return playSingleDoneAudio;
    }

    public static void setPlaySingleDoneAudio( boolean value )
    {
        playSingleDoneAudio = value;
    }

    public static boolean getPlayAllDoneAudio()
    {
        return playAllDoneAudio;
    }

    public static void setPlayAllDoneAudio( boolean value )
    {
        playAllDoneAudio = value;
    }

    public static String getSingleDoneAudioFile()
    {
        return singleDoneAudioFile;
    }

    public static void setSingleDoneAudioFile( String value )
    {
        singleDoneAudioFile = value;
    }

    public static String getAllDoneAudioFile()
    {
        return allDoneAudioFile;
    }

    public static void setAllDoneAudioFile( String value )
    {
        allDoneAudioFile = value;
    }

    // 設置腳本
    public static boolean getRunSingleDoneScript()
    {
        return runSingleDoneScript;
    }

    public static void setRunSingleDoneScript( boolean value )
    {
        runSingleDoneScript = value;
    }

    public static boolean getRunAllDoneScript()
    {
        return runAllDoneScript;
    }

    public static void setRunAllDoneScript( boolean value )
    {
        runAllDoneScript = value;
    }

    public static String getSingleDoneScriptFile()
    {
        return singleDoneScriptFile;
    }

    public static void setSingleDoneScriptFile( String value )
    {
        singleDoneScriptFile = value;
    }

    public static String getAllDoneScriptFile()
    {
        return allDoneScriptFile;
    }

    public static void setAllDoneScriptFile( String value )
    {
        allDoneScriptFile = value;
    }

    // 設置背景圖片後的字體顏色搭配
    public static Color getMainFrameMenuItemDefaultColor()
    {
        return mainFrameMenuItemDefaultColor;
    }

    public static void setMainFrameMenuItemDefaultColor( Color color )
    {
        mainFrameMenuItemDefaultColor = color;
    }

    public static Color getMainFrameTableDefaultColor()
    {
        return mainFrameTableDefaultColor;
    }

    public static void setMainFrameTableDefaultColor( Color color )
    {
        mainFrameTableDefaultColor = color;
    }

    public static Color getMainFrameTableMouseEnteredColor()
    {
        return mainFrameTableMouseEnteredColor;
    }

    public static void setMainFrameTableMouseEnteredColor( Color color )
    {
        mainFrameTableMouseEnteredColor = color;
    }

    public static Color getMainFrameOtherDefaultColor()
    {
        return mainFrameOtherDefaultColor;
    }

    public static void setMainFrameOtherDefaultColor( Color color )
    {
        mainFrameOtherDefaultColor = color;
    }

    public static Color getMainFrameOtherMouseEnteredColor()
    {
        return mainFrameOtherMouseEnteredColor;
    }

    public static void setMainFrameOtherMouseEnteredColor( Color color )
    {
        mainFrameOtherMouseEnteredColor = color;
    }

    public static Color getInformationFrameOtherDefaultColor()
    {
        return informationFrameOtherDefaultColor;
    }

    public static void setInformationFrameOtherDefaultColor( Color color )
    {
        informationFrameOtherDefaultColor = color;
    }

    public static Color getInformationFrameOtherMouseEnteredColor()
    {
        return informationFrameOtherMouseEnteredColor;
    }

    public static void setInformationFrameOtherMouseEnteredColor( Color color )
    {
        informationFrameOtherMouseEnteredColor = color;
    }

    public static Color getOptionFrameOtherDefaultColor()
    {
        return optionFrameOtherDefaultColor;
    }

    public static void setOptionFrameOtherDefaultColor( Color color )
    {
        optionFrameOtherDefaultColor = color;
    }

    public static Color getOptionFrameOtherMouseEnteredColor()
    {
        return optionFrameOtherMouseEnteredColor;
    }

    public static void setOptionFrameOtherMouseEnteredColor( Color color )
    {
        optionFrameOtherMouseEnteredColor = color;
    }

    public static Color getChoiceFrameTableDefaultColor()
    {
        return choiceFrameTableDefaultColor;
    }

    public static void setChoiceFrameTableDefaultColor( Color color )
    {
        choiceFrameTableDefaultColor = color;
    }

    public static Color getChoiceFrameTableFileExistedColor()
    {
        return choiceFrameTableFileExistedColor;
    }

    public static void setChoiceFrameTableFileExistedColor( Color color )
    {
        choiceFrameTableFileExistedColor = color;
    }

    public static Color getChoiceFrameTableMouseEnteredColor()
    {
        return choiceFrameTableMouseEnteredColor;
    }

    public static void setChoiceFrameTableMouseEnteredColor( Color color )
    {
        choiceFrameTableMouseEnteredColor = color;
    }

    public static Color getChoiceFrameOtherDefaultColor()
    {
        return choiceFrameOtherDefaultColor;
    }

    public static void setChoiceFrameOtherDefaultColor( Color color )
    {
        choiceFrameOtherDefaultColor = color;
    }

    public static Color getChoiceFrameOtherMouseEnteredColor()
    {
        return choiceFrameOtherMouseEnteredColor;
    }

    public static void setChoiceFrameOtherMouseEnteredColor( Color color )
    {
        choiceFrameOtherMouseEnteredColor = color;
    }

    // 是否設置背景圖片
    public static boolean getUsingBackgroundPicOfMainFrame()
    {
        return usingBackgroundPicOfMainFrame;
    }

    public static void setUsingBackgroundPicOfMainFrame( boolean choose )
    {
        usingBackgroundPicOfMainFrame = choose;
    }

    public static boolean getUsingBackgroundPicOfInformationFrame()
    {
        return usingBackgroundPicOfInformationFrame;
    }

    public static void setUsingBackgroundPicOfInformationFrame( boolean choose )
    {
        usingBackgroundPicOfInformationFrame = choose;
    }

    public static boolean getUsingBackgroundPicOfOptionFrame()
    {
        return usingBackgroundPicOfOptionFrame;
    }

    public static void setUsingBackgroundPicOfOptionFrame( boolean choose )
    {
        usingBackgroundPicOfOptionFrame = choose;
    }

    public static boolean getUsingBackgroundPicOfChoiceFrame()
    {
        return usingBackgroundPicOfChoiceFrame;
    }

    public static void setUsingBackgroundPicOfChoiceFrame( boolean choose )
    {
        usingBackgroundPicOfChoiceFrame = choose;
    }

    // 背景圖片的目錄
    public static String getBackgroundPicPathOfMainFrame()
    {
        return backgroundPicPathOfMainFrame;
    }

    public static void setBackgroundPicPathOfMainFrame( String path )
    {
        backgroundPicPathOfMainFrame = path;
    }

    public static String getBackgroundPicPathOfInformationFrame()
    {
        return backgroundPicPathOfInformationFrame;
    }

    public static void setBackgroundPicPathOfInformationFrame( String path )
    {
        backgroundPicPathOfInformationFrame = path;
    }

    public static String getBackgroundPicPathOfOptionFrame()
    {
        return backgroundPicPathOfOptionFrame;
    }

    public static void setBackgroundPicPathOfOptionFrame( String path )
    {
        backgroundPicPathOfOptionFrame = path;
    }

    public static String getBackgroundPicPathOfChoiceFrame()
    {
        return backgroundPicPathOfChoiceFrame;
    }

    public static void setBackgroundPicPathOfChoiceFrame( String path )
    {
        backgroundPicPathOfChoiceFrame = path;
    }

    public static String getCompressFormat()
    {
        return compressFormat;
    }

    public static void setCompressFormat( String format )
    {
        compressFormat = format;
    }

    public static int getDefaultLanguage()
    {
        return defaultLanguage;
    }

    public static void setDefaultLanguage( int language )
    {
        defaultLanguage = language;
    }

    public static int getDefaultTextOutputFormat()
    {
        return defaultTextOutputFormat;
    }

    public static void setDefaultTextOutputFormat( int outputFormat )
    {
        defaultTextOutputFormat = outputFormat;
    }

    public static int getDefaultShellScriptIndex()
    {
        String[] scripts = new CommonGUI().getShellScriptStrings();
        int index = 0;

        for ( int i = 0; i < scripts.length; i++ )
        {
            if ( defaultShellScript.equals( scripts[i] ) )
            {
                index = i;
            }
        }

        return index;
    }
    // 執行腳本的Shell名稱

    public static String getDefaultShellScript()
    {
        return defaultShellScript;
    }

    public static void setDefaultShellScript( String shell )
    {
        defaultShellScript = shell;
    }

    // 等待單一任務腳本執行的時間（秒數）
    public static int getSingleScriptWaitTime()
    {
        return singleScriptWaitTime;
    }

    public static void setSingleScriptWaitTime( int time )
    {
        singleScriptWaitTime = time;
    }

    // ----------------------------------------------------------------
    public static void setPicFrontName( String front )
    {
        picFrontName = front;
    }

    public static String getPicFrontName()
    {
        return picFrontName;
    }

    public static void setVolume( String v )
    {
        volume = v;
    }

    public static void setWholeTitle( String t )
    {
        wholeTitle = t;
    }

    public static String getVolume()
    {
        return volume;
    }

    public static String getWholeTitle()
    {
        return wholeTitle;
    }

    public static int getFileNameLength()
    {
        return fileNameLength;
    }

    // 蒐集小說封面圖的數量索引
    public static int getCoverSelectAmountIndex()
    {
        return coverSelectAmountIndex;
    }

    public static void setCoverSelectAmountIndex( int amount )
    {
        coverSelectAmountIndex = amount;
    }

    public static boolean getDownloadNovelCover()
    {
        return downloadNovelCover;
    }

    public static void setDownloadNovelCover( boolean download )
    {
        downloadNovelCover = download;
    }
}
