/*
 ----------------------------------------------------------------------------------------------------
 Program Name : JComicDownloader
 Authors  : surveyorK
 Last Modified : 2011/11/4
 ----------------------------------------------------------------------------------------------------
 ChangeLog:
 1.11: 新增volumeTitle，傳入runModule可避免重新解析單集名稱不一致的問題
 ----------------------------------------------------------------------------------------------------
 */
package jcomicdownloader.module;

import jcomicdownloader.tools.*;
import jcomicdownloader.enums.*;
import jcomicdownloader.*;

/**
 @author user 執行主類別
 */
public class Run extends Thread
{ // main class to run whole program

    private String[] args;
    private String webSite;
    private String title; // 下載網址指定已知漫畫名稱，避免重複分析
    private String volumeTitle; // 單集名稱，避免重複分析（實驗功能）
    private int runMode; // 只分析、只下載或分析兼下載
    public static boolean isAlive;
    public static boolean isLegal;

    public Run( int runMode )
    {
        isAlive = true;
        isLegal = true;
        this.runMode = runMode;
    }

    public Run( String[] originalArgs, int runMode )
    {
        this( runMode );

        args = originalArgs;

        if ( !Common.withGUI() )
        {
            SetUp set = new SetUp();
            set.readSetFile(); // set up the file name and directory
        }
        //test( args );

        webSite = "";
    }

    public Run( String onlyURL, String volumeTitle, String title, int runMode )
    {
        this( runMode );

        this.title = title;
        this.volumeTitle = volumeTitle;
        this.runMode = runMode;

        String[] url = new String[ 1 ];
        url[0] = onlyURL;

        args = url;

        SetUp set = new SetUp();
        set.readSetFile(); // 讀入設置檔的設置參數

        //test( args );

        webSite = "";
    }

    public void resetArgs( String[] newArgs )
    {
        args = newArgs;
    }

    public String getTitle()
    {
        return title;
    }

    public String getVolumeTitle()
    {
        return volumeTitle;
    }

    // 解析輸入網址並做後續處理
    public void run()
    {
        Common.debugPrintln( "開始解析單一位址：" );
        if ( args.length == 0 )
        {
            Common.errorReport( "WRONG: No URL of comic !!" );
        }
        else if ( args.length > 4 )
        {
            Common.errorReport( "WRONG: Too many args !!" );
        }
        else if ( Common.isLegalURL( args[0] ) )
        {
            if ( args.length == 1 )
            { // ComicDown URL
                webSite = args[0];
            }
            else if ( args.length == 2
                    && args[1].equals( "add" ) )
            { // ComicDown URL add
                webSite = args[0];
                SetUp.addSchedule = true;
            }
            else if ( args.length == 3
                    && args[1].matches( "\\d+" )
                    && args[2].matches( "\\d+" ) )
            { // ComicDown URL beginVolume endVolume
                webSite = args[0];
                SetUp.setDownloadVolume( args[1], args[2] );
            }
            else if ( args.length == 4
                    && args[1].matches( "\\d+" )
                    && args[2].matches( "\\d+" )
                    && args[3].equals( "add" ) )
            { // ComicDown URL beginVolume endVolume
                webSite = args[0];
                SetUp.setDownloadVolume( args[1], args[2] );
                SetUp.addSchedule = true;
            }
            else
            {
                Common.errorReport( "WRONG: illegal parameters !!" );
            }
        }
        else
        {
            Common.errorReport( "WRONG: illegal URL :  [" + args[0] + "]" );
        }


        if ( isAlive && isLegal )
        {
            ParseWebPage pw = new ParseWebPage( webSite );
            Site siteID = pw.getSiteID();
            String parserName = siteID.getParserName();
            Common.debugPrintln("Using Parser: "+parserName);
            if ( siteID== Site.UNKNOWN ){
                Common.urlIsUnknown = true;
            }
            else{
                try{
                    ParseOnlineComicSite parse =((ParseOnlineComicSite)Class.forName(parserName).newInstance());
                    runSingleParseModule( parse );
                }catch(ClassNotFoundException ex){
                    Common.urlIsUnknown = true;
                }catch(Exception e){
                    Common.urlIsUnknown = true;
                }
            }
        }
    }

    // 執行單一模組的固定程序
    public void runSingleParseModule( ParseOnlineComicSite parse )
    {
        parse.setTitle( title );
        parse.setWholeTitle( getVolumeTitle() );
        parse.setRunMode( runMode );
        new RunModule().runMainProcess( parse, webSite );
        title = parse.getTitle();
    }

    public void test( String[] args )
    {
    }
}
