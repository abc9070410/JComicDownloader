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

            if ( pw.getSiteID() == Site.CC )
            {
                ParseOnlineComicSite parse = new ParseCC();
                runSingleParseModule( parse );
            }
            else if ( pw.getSiteID() == Site.KUKU )
            {
                ParseOnlineComicSite parse = new ParseKUKU();
                runSingleParseModule( parse );
            }
            else if ( pw.getSiteID() == Site.EH )
            {
                ParseOnlineComicSite parse = new ParseEH();
                runSingleParseModule( parse );
            }
            // 九九系列網站
            else if ( pw.getSiteID() == Site.NINENINE_COMIC )
            {
                ParseOnlineComicSite parse = new Parse99Comic();
                runSingleParseModule( parse );
            }
            else if ( pw.getSiteID() == Site.NINENINE_COMIC_TC )
            {
                ParseOnlineComicSite parse = new Parse99ComicTC();
                runSingleParseModule( parse );
            }
            else if ( pw.getSiteID() == Site.NINENINE_MANGA )
            {
                ParseOnlineComicSite parse = new Parse99Manga();
                runSingleParseModule( parse );
            }
            else if ( pw.getSiteID() == Site.NINENINE_MANGA_TC )
            {
                ParseOnlineComicSite parse = new Parse99MangaTC();
                runSingleParseModule( parse );
            }
            else if ( pw.getSiteID() == Site.NINENINE_MANGA_WWW )
            {
                ParseOnlineComicSite parse = new Parse99MangaWWW();
                runSingleParseModule( parse );
            }
            else if ( pw.getSiteID() == Site.NINENINE_99770 )
            {
                ParseOnlineComicSite parse = new Parse99770();
                runSingleParseModule( parse );
            }
            else if ( pw.getSiteID() == Site.NINENINE_MH_99770 )
            {
                ParseOnlineComicSite parse = new ParseMh99770();
                runSingleParseModule( parse );
            }
            else if ( pw.getSiteID() == Site.NINENINE_MH )
            {
                ParseOnlineComicSite parse = new Parse99Mh();
                runSingleParseModule( parse );
            }
            else if ( pw.getSiteID() == Site.NINENINE_COCO )
            {
                ParseOnlineComicSite parse = new ParseCoco();
                runSingleParseModule( parse );
            }
            else if ( pw.getSiteID() == Site.NINENINE_COCO_TC )
            {
                ParseOnlineComicSite parse = new ParseCocoTC();
                runSingleParseModule( parse );
            }
            else if ( pw.getSiteID() == Site.NINENINE_1MH )
            {
                ParseOnlineComicSite parse = new Parse1Mh();
                runSingleParseModule( parse );
            }
            else if ( pw.getSiteID() == Site.NINENINE_3G )
            {
                ParseOnlineComicSite parse = new Parse3G();
                runSingleParseModule( parse );
            }
            /*
             else if ( pw.getSiteID() == Site.ONE_SEVEN_EIGHT ) {
             ParseOnlineComicSite parse = new Parse178();
             runSingleParseModule( parse );
             }
             */
            else if ( pw.getSiteID() == Site.EIGHT_COMIC )
            {
                ParseOnlineComicSite parse = new ParseEC();
                runSingleParseModule( parse );
            }
            else if ( pw.getSiteID() == Site.EIGHT_COMIC_PHOTO )
            {
                ParseOnlineComicSite parse = new ParseECphoto();
                runSingleParseModule( parse );
            }
            else if ( pw.getSiteID() == Site.JUMPCNCN )
            {
                ParseOnlineComicSite parse = new ParseJumpcncn();
                runSingleParseModule( parse );
            }
            else if ( pw.getSiteID() == Site.DMEDEN )
            {
                ParseOnlineComicSite parse = new ParseDmeden();
                runSingleParseModule( parse );
            }
            else if ( pw.getSiteID() == Site.JUMPCN )
            {
                ParseOnlineComicSite parse = new ParseJumpcn();
                runSingleParseModule( parse );
            }
            else if ( pw.getSiteID() == Site.MANGAFOX )
            {
                ParseOnlineComicSite parse = new ParseMangaFox();
                runSingleParseModule( parse );
            }
            else if ( pw.getSiteID() == Site.MANMANKAN )
            {
                ParseOnlineComicSite parse = new ParseManmankan();
                runSingleParseModule( parse );
            }
            else if ( pw.getSiteID() == Site.XINDM )
            {
                ParseOnlineComicSite parse = new ParseXindm();
                runSingleParseModule( parse );
            }
            else if ( pw.getSiteID() == Site.EX )
            {
                ParseOnlineComicSite parse = new ParseEX();
                runSingleParseModule( parse );
            }
            else if ( pw.getSiteID() == Site.GOOGLE_PIC )
            {
                ParseOnlineComicSite parse = new ParseGooglePic();
                runSingleParseModule( parse );
            }
            else if ( pw.getSiteID() == Site.NANA )
            {
                ParseOnlineComicSite parse = new ParseNANA();
                runSingleParseModule( parse );
            }
            else if ( pw.getSiteID() == Site.CITY_MANGA )
            {
                ParseOnlineComicSite parse = new ParseCityManga();
                runSingleParseModule( parse );
            }
            else if ( pw.getSiteID() == Site.IIBQ )
            {
                ParseOnlineComicSite parse = new ParseIIBQ();
                runSingleParseModule( parse );
            }
            else if ( pw.getSiteID() == Site.BAIDU )
            {
                ParseOnlineComicSite parse = new ParseBAIDU();
                runSingleParseModule( parse );
            }
            else if ( pw.getSiteID() == Site.SF )
            {
                ParseOnlineComicSite parse = new ParseSF();
                runSingleParseModule( parse );
            }
            else if ( pw.getSiteID() == Site.KKKMH )
            {
                ParseOnlineComicSite parse = new ParseKKKMH();
                runSingleParseModule( parse );
            }
            else if ( pw.getSiteID() == Site.SIX_COMIC )
            {
                ParseOnlineComicSite parse = new ParseSixComic();
                runSingleParseModule( parse );
            }
            else if ( pw.getSiteID() == Site.MANHUA_178 )
            {
                ParseOnlineComicSite parse = new Parse178();
                runSingleParseModule( parse );
            }
            else if ( pw.getSiteID() == Site.KANGDM )
            {
                ParseOnlineComicSite parse = new ParseKangdm();
                runSingleParseModule( parse );
            }
            else if ( pw.getSiteID() == Site.BENGOU )
            {
                ParseOnlineComicSite parse = new ParseBengou();
                runSingleParseModule( parse );
            }
            else if ( pw.getSiteID() == Site.EMLAND )
            {
                ParseOnlineComicSite parse = new ParseEmland();
                runSingleParseModule( parse );
            }
            else if ( pw.getSiteID() == Site.MOP )
            {
                ParseOnlineComicSite parse = new ParseMOP();
                runSingleParseModule( parse );
            }
            else if ( pw.getSiteID() == Site.DM5 )
            {
                ParseOnlineComicSite parse = new ParseDM5();
                runSingleParseModule( parse );
            }
            else if ( pw.getSiteID() == Site.CK )
            {
                ParseOnlineComicSite parse = new ParseCK();
                runSingleParseModule( parse );
            }
            else if ( pw.getSiteID() == Site.TUKU )
            {
                ParseOnlineComicSite parse = new ParseTUKU();
                runSingleParseModule( parse );
            }
            else if ( pw.getSiteID() == Site.HH )
            {
                ParseOnlineComicSite parse = new ParseHH();
                runSingleParseModule( parse );
            }
            else if ( pw.getSiteID() == Site.IASK )
            {
                ParseOnlineComicSite parse = new ParseIASK();
                runSingleParseModule( parse );
            }
            else if ( pw.getSiteID() == Site.JM )
            {
                ParseOnlineComicSite parse = new ParseJM();
                runSingleParseModule( parse );
            }
            else if ( pw.getSiteID() == Site.MANGA_WINDOW )
            {
                ParseOnlineComicSite parse = new ParseMangaWindow();
                runSingleParseModule( parse );
            }
            else if ( pw.getSiteID() == Site.CK_NOVEL )
            {
                ParseOnlineComicSite parse = new ParseCKNovel();
                runSingleParseModule( parse );
            }
            else if ( pw.getSiteID() == Site.MYBEST )
            {
                ParseOnlineComicSite parse = new ParseMyBest();
                runSingleParseModule( parse );
            }
            else if ( pw.getSiteID() == Site.IMANHUA )
            {
                ParseOnlineComicSite parse = new ParseImanhua();
                runSingleParseModule( parse );
            }
            else if ( pw.getSiteID() == Site.VERYIM )
            {
                ParseOnlineComicSite parse = new ParseVeryim();
                runSingleParseModule( parse );
            }
            else if ( pw.getSiteID() == Site.WENKU )
            {
                ParseOnlineComicSite parse = new ParseWenku();
                runSingleParseModule( parse );
            }
            else if ( pw.getSiteID() == Site.FUMANHUA )
            {
                ParseOnlineComicSite parse = new ParseFumanhua();
                runSingleParseModule( parse );
            }
            else if ( pw.getSiteID() == Site.SIX_MANGA )
            {
                ParseOnlineComicSite parse = new ParseSixManga();
                runSingleParseModule( parse );
            }
            else if ( pw.getSiteID() == Site.XXBH )
            {
                ParseOnlineComicSite parse = new ParseXXBH();
                runSingleParseModule( parse );
            }
            else if ( pw.getSiteID() == Site.COMIC_131 )
            {
                ParseOnlineComicSite parse = new Parse131();
                runSingleParseModule( parse );
            }
            else if ( pw.getSiteID() == Site.BLOGSPOT )
            {
                ParseOnlineComicSite parse = new ParseBlogspot();
                runSingleParseModule( parse );
            }
            else if ( pw.getSiteID() == Site.PIXNET_BLOG )
            {
                ParseOnlineComicSite parse = new ParsePixnetBlog();
                runSingleParseModule( parse );
            }
            else if ( pw.getSiteID() == Site.XUITE_BLOG )
            {
                ParseOnlineComicSite parse = new ParseXuiteBlog();
                runSingleParseModule( parse );
            }
            else if ( pw.getSiteID() == Site.YAM_BLOG )
            {
                ParseOnlineComicSite parse = new ParseYamBlog();
                runSingleParseModule( parse );
            }
            else if ( pw.getSiteID() == Site.EYNY_NOVEL )
            {
                ParseOnlineComicSite parse = new ParseEynyNovel();
                runSingleParseModule( parse );
            }
            else if ( pw.getSiteID() == Site.ZUIWANJU )
            {
                ParseOnlineComicSite parse = new ParseZuiwanju();
                runSingleParseModule( parse );
            }
            else if ( pw.getSiteID() == Site.TWO_ECY )
            {
                ParseOnlineComicSite parse = new Parse2ecy();
                runSingleParseModule( parse );
            }
            else if ( pw.getSiteID() == Site.TIANYA_BOOK )
            {
                ParseOnlineComicSite parse = new ParseTianyaBook();
                runSingleParseModule( parse );
            }
            else if ( pw.getSiteID() == Site.EIGHT_NOVEL )
            {
                ParseOnlineComicSite parse = new ParseEightNovel();
                runSingleParseModule( parse );
            }
            else if ( pw.getSiteID() == Site.QQ_BOOK )
            {
                ParseOnlineComicSite parse = new ParseQQBook();
                runSingleParseModule( parse );
            }
            else if ( pw.getSiteID() == Site.QQ_ORIGIN_BOOK )
            {
                ParseOnlineComicSite parse = new ParseQQOriginBook();
                runSingleParseModule( parse );
            }
            else if ( pw.getSiteID() == Site.SINA_BOOK )
            {
                ParseOnlineComicSite parse = new ParseSinaBook();
                runSingleParseModule( parse );
            }
            else if ( pw.getSiteID() == Site.FIVEONE_CTO )
            {
                ParseOnlineComicSite parse = new Parse51Cto();
                runSingleParseModule( parse );
            }
            else if ( pw.getSiteID() == Site.ONESEVEN_KK )
            {
                ParseOnlineComicSite parse = new Parse17KK();
                runSingleParseModule( parse );
            }
            else if ( pw.getSiteID() == Site.UUS8 )
            {
                ParseOnlineComicSite parse = new ParseUUS8();
                runSingleParseModule( parse );
            }
            else if ( pw.getSiteID() == Site.WENKU8 )
            {
                ParseOnlineComicSite parse = new ParseWenku8();
                runSingleParseModule( parse );
            }
            else if ( pw.getSiteID() == Site.IFENG_BOOK )
            {
                ParseOnlineComicSite parse = new ParseIfengBook();
                runSingleParseModule( parse );
            }
            else if ( pw.getSiteID() == Site.XUNLOOK )
            {
                ParseOnlineComicSite parse = new ParseXunlook();
                runSingleParseModule( parse );
            }
            else if ( pw.getSiteID() == Site.WENKU7 )
            {
                ParseOnlineComicSite parse = new Parse7Wenku();
                runSingleParseModule( parse );
            }
            else if ( pw.getSiteID() == Site.WOYOUXIAN )
            {
                ParseOnlineComicSite parse = new ParseWoyouxian();
                runSingleParseModule( parse );
            }
            else if ( pw.getSiteID() == Site.SHUNONG )
            {
                ParseOnlineComicSite parse = new ParseShunong();
                runSingleParseModule( parse );
            }
            else if ( pw.getSiteID() == Site.SOGOU )
            {
                ParseOnlineComicSite parse = new ParseSogou();
                runSingleParseModule( parse );
            }
            else if ( pw.getSiteID() == Site.TING1 )
            {
                ParseOnlineComicSite parse = new Parse1Ting();
                runSingleParseModule( parse );
            }
            else if ( pw.getSiteID() == Site.XIAMI )
            {
                ParseOnlineComicSite parse = new ParseXiami();
                runSingleParseModule( parse );
            }
            else // Site.UNKNOWN
            {
                Common.urlIsUnknown = true;
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
