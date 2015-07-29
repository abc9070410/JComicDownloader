/*
 ----------------------------------------------------------------------------------------------------
 Program Name : JComicDownloader
 Authors  : surveyorK
 Last Modified : 2011/10/25
 ----------------------------------------------------------------------------------------------------
 ChangeLog:

----------------------------------------------------------------------------------------------------
 */
package jcomicdownloader.enums;

import static jcomicdownloader.enums.EnumGenerator.addSiteEnum;
import jcomicdownloader.module.ParseOnlineComicSite;
import jcomicdownloader.tools.Common;

/**
 * 網站的代號，當作Enum用
 * */
public enum Site {
      UNKNOWN("",false,false,false,false);
//    CC(1,"ParseCC"),
//    KUKU(2,"ParseKUKU"),
//    EH(3,"ParseEH"),
//    NINENINE_MANGA(4,"Parse99Manga"),
//    NINENINE_COMIC(5,"Parse99Comic"),
//    NINENINE_99770(6,"Parse99770"),
//    NINENINE_COCO(7,"ParseCoco"),
//    NINENINE_MH(8,"Parse99Mh"),
//    NINENINE_1MH(9,"Parse1Mh"),
//    NINENINE_3G(10,"Parse3G"),
//    ONE_SEVEN_EIGHT(11,"Parse178"),
//    EIGHT_COMIC(11,"ParseEC"),
//    EIGHT_COMIC_PHOTO(12,"ParseECphoto"),
//    JUMPCNCN(13,"ParseJumpcncn"),
//    DMEDEN(14,"ParseDmeden"),
//    JUMPCN(15,"ParseJumpcn"),
//    MANGAFOX(16,"ParseMangaFox"),
//    MANMANKAN(17,"ParseManmankan"), 
//    XINDM(18,"ParseXindm"), 
//    EX(19,"ParseEX"), 
//    WY(20,""), 
//    GOOGLE_PIC(21,"ParseGooglePic"),
//    BING_PIC(22,""),
//    BAIDU_PIC(23,""),
//    NANA(24,"ParseNANA"),
//    CITY_MANGA(25,"ParseCityManga"),
//    IIBQ(26,"ParseIIBQ"),
//    BAIDU(27,"ParseBAIDU"),
//    SF(28,"ParseSF"),
//    KKKMH(29,"ParseKKKMH"),
//    SIX_COMIC(30,"ParseSixComic"),
//    MANHUA_178(31,"Parse178"),
//    KANGDM(32,"ParseKangdm"),
//    BENGOU(33,"ParseBengou"),
//    EMLAND(34,"ParseEmland"),
//    MOP(35,"ParseMOP"),
//    DM5(36,"ParseDM5"),
//    CK(37,"ParseCK"),
//    TUKU(38,"ParseTUKU"),
//    HH(39,"ParseHH"),
//    IASK(40,"ParseIASK"),
//    NINENINE_MH_99770(41,"ParseMh99770"),
//    JM(42,"ParseJM"),
//    //DM5_ENGLISH(43,""),
//    NINENINE_COMIC_TC(44,"Parse99ComicTC"),
//    NINENINE_MANGA_TC(45,"Parse99MangaTC"),
//    MANGA_WINDOW(46,"ParseMangaWindow"),
//    CK_NOVEL(47,"ParseCKNovel"),
//    MYBEST(48,"ParseMyBest"),
//    IMANHUA(49,"ParseImanhua"),
//    VERYIM(50,"ParseVeryim"),
//    WENKU(51,"ParseWenku"),
//    FUMANHUA(52,"ParseFumanhua"),
//    SIX_MANGA(53,"ParseSixManga"),
//    NINENINE_COCO_TC(54,"ParseCocoTC"),
//    XXBH(55,"ParseXXBH"),
//    COMIC_131(56,"Parse131"), 
//    BLOGSPOT(57,"ParseBlogspot"), // 
//    PIXNET_BLOG(58,"ParsePixnetBlog"),
//    XUITE_BLOG(59,"ParseXuiteBlog"),
//    YAM_BLOG(60,"ParseYamBlog"),
//    EYNY_NOVEL(61,"ParseEynyNovel"),
//    //t KKMH(62),
//    ZUIWANJU(63,"ParseZuiwanju"), // zuiwanju.
//    TWO_ECY(64,"Parse2ecy"), 
//    TIANYA_BOOK(65,"ParseTianyaBook"), 
//    NINENINE_MANGA_WWW(66,"Parse99MangaWWW"), 
//    EIGHT_NOVEL(67,"ParseEightNovel"), 
//    QQ_BOOK(68,"ParseQQBook"), 
//    SINA_BOOK(69,"ParseSinaBook"), 
//    FIVEONE_CTO(70,"Parse51Cto"),
//    ONESEVEN_KK(71,"Parse17KK"),
//    QQ_ORIGIN_BOOK(72,"ParseQQOriginBook"), 
//    UUS8(73,"ParseUUS8"), 
//    WENKU8(74,"ParseWenku8"), 
//    IFENG_BOOK(75,"ParseIfengBook"), 
//    XUNLOOK(76,"ParseXunlook"), 
//    WENKU7(77,"WENKU7"),  // 
//    WOYOUXIAN(78,"WOYOUXIAN"),  // 
//    SHUNONG(79,"ParseShunong"),
//    SOGOU(80,"ParseSogou"),
//    TING1(81,"Parse1Ting"),
//    XIAMI(82,"ParseXiami"),
//    WIKI(83,"ParseWiki"),
//    PTT(84,"ParsePtt"),
//    ISHUHUI(85,"ParseIshuhui"); // ishuhui
//     
    
    private final String parserName;//for dynamic enum
    private final boolean novelSite;
    private final boolean musicSite;
    private final boolean blogSite;
    private final boolean downloadBefore;
 
    public static Site formString(String s){
        for (Site value : Site.values()){
            if (value.name().equals(s)) return value;
        }
        return Site.UNKNOWN;
    }
    
    private Site(String parserName,Boolean novelSite,Boolean musicSite,Boolean blogSite,Boolean downloadBefore){
        this.parserName = parserName;
        this.novelSite = novelSite;
        this.musicSite = musicSite;
        this.blogSite = blogSite; 
        this.downloadBefore = downloadBefore;
    }
 
    public boolean isNovelSite(){ return this.novelSite; }
    public boolean isMusicSite(){ return this.musicSite; }
    public boolean isBlogSite(){ return this.blogSite; }
    public boolean isDownloadBefore(){ return this.downloadBefore; }
   
    public String getParserName(){ return this.parserName; }   
     
    public static Site detectSiteID(final String webSite){
        ParseOnlineComicSite s;
        
        String[] parserModules =        
            {"jcomicdownloader.module.ParseKUKU",
            "jcomicdownloader.module.ParseEH",
            "jcomicdownloader.module.Parse99Manga",
            "jcomicdownloader.module.Parse99Comic",
            "jcomicdownloader.module.Parse99770",
            "jcomicdownloader.module.ParseCocoTC",//need place before coco
            "jcomicdownloader.module.ParseCoco",
            "jcomicdownloader.module.Parse99Mh",
            "jcomicdownloader.module.Parse1Mh",
            "jcomicdownloader.module.Parse3G",
            "jcomicdownloader.module.Parse178",            
            "jcomicdownloader.module.ParseECphoto",//need place before EC
            "jcomicdownloader.module.ParseEC",
            "jcomicdownloader.module.ParseJumpcncn",
            "jcomicdownloader.module.ParseDmeden",
            "jcomicdownloader.module.ParseJumpcn",
            "jcomicdownloader.module.ParseMangaFox",
            "jcomicdownloader.module.ParseManmankan",
            "jcomicdownloader.module.ParseXindm",
            "jcomicdownloader.module.ParseEX",
            "jcomicdownloader.module.ParseGooglePic",
            "jcomicdownloader.module.ParseNANA",
            "jcomicdownloader.module.ParseCityManga",
            "jcomicdownloader.module.ParseIIBQ",
            "jcomicdownloader.module.ParseBAIDU",
            "jcomicdownloader.module.ParseSF",
            "jcomicdownloader.module.ParseKKKMH",
            "jcomicdownloader.module.ParseSixComic",
            "jcomicdownloader.module.Parse178",
            "jcomicdownloader.module.ParseKangdm",
            "jcomicdownloader.module.ParseBengou",
            "jcomicdownloader.module.ParseEmland",
            "jcomicdownloader.module.ParseMOP",
            "jcomicdownloader.module.ParseDM5",
            "jcomicdownloader.module.ParseCK",
            "jcomicdownloader.module.ParseTUKU",
//            "jcomicdownloader.module.ParseHH",
            "jcomicdownloader.module.ParseIASK",
            "jcomicdownloader.module.ParseMh99770",
            "jcomicdownloader.module.ParseJM",
            "jcomicdownloader.module.Parse99ComicTC",
            "jcomicdownloader.module.Parse99MangaTC",
            "jcomicdownloader.module.ParseMangaWindow",
            "jcomicdownloader.module.ParseCKNovel",
            "jcomicdownloader.module.ParseMyBest",
            "jcomicdownloader.module.ParseImanhua",
            "jcomicdownloader.module.ParseVeryim",
            "jcomicdownloader.module.ParseWenku",
            "jcomicdownloader.module.ParseFumanhua",
            "jcomicdownloader.module.ParseSixManga",
            "jcomicdownloader.module.ParseXXBH",
            "jcomicdownloader.module.Parse131",
            "jcomicdownloader.module.ParseBlogspot",
            "jcomicdownloader.module.ParsePixnetBlog",
            "jcomicdownloader.module.ParseXuiteBlog",
            "jcomicdownloader.module.ParseYamBlog",
            "jcomicdownloader.module.ParseEynyNovel",
            "jcomicdownloader.module.ParseZuiwanju",
            "jcomicdownloader.module.Parse2ecy",
            "jcomicdownloader.module.ParseTianyaBook",
            "jcomicdownloader.module.Parse99MangaWWW",
            "jcomicdownloader.module.ParseEightNovel",
            "jcomicdownloader.module.ParseQQBook",
            "jcomicdownloader.module.ParseSinaBook",
            "jcomicdownloader.module.Parse51Cto",
            "jcomicdownloader.module.Parse17KK",
            "jcomicdownloader.module.ParseQQOriginBook",
            "jcomicdownloader.module.ParseUUS8",
            "jcomicdownloader.module.ParseWenku8",
            "jcomicdownloader.module.ParseIfengBook",
            "jcomicdownloader.module.ParseXunlook",
            "jcomicdownloader.module.Parse7Wenku",
            "jcomicdownloader.module.ParseWoyouxian",
            "jcomicdownloader.module.ParseShunong",
            "jcomicdownloader.module.ParseSogou",
            "jcomicdownloader.module.Parse1Ting",
            "jcomicdownloader.module.ParseXiami",
            "jcomicdownloader.module.ParseWiki",
            "jcomicdownloader.module.ParsePtt",
            "jcomicdownloader.module.ParseIshuhui"};
        
        for (String name:parserModules){
            try{                
                s = (ParseOnlineComicSite ) Class.forName(name).newInstance();
                if (s.canParserHandle(webSite)) return s.getSiteID();
        }catch(Exception e){
                Common.debugPrintln("Module "+name+" load fail");
            }
        }        
        
        
//        if ( webSite.matches( "(?s).*89890.com(?s).*" ) )
//        {
//            return Site.formString("CC");
//        }
//        else if ( webSite.matches( "(?s).*kukudm.com(?s).*" )
//                || webSite.matches( "(?s).*socomic.com(?s).*" )
//                || webSite.matches( "(?s).*socomic.net(?s).*" ) )
//        {
//            return Site.formString("KUKU");
//        }
//        else if ( webSite.matches( "(?s).*e-hentai(?s).*" ) )
//        {
//            return Site.formString("EH");
//        }
//        else if ( webSite.matches( "(?s).*exhentai.org(?s).*" ) )
//        {
//            return Site.formString("EX");
//        }
//
//        else if ( webSite.matches( "(?s).*dm.99manga.com(?s).*" ) )
//        {
//            return Site.formString("NINENINE_MANGA_TC");
//        }
//        /*else if ( webSite.matches( "(?s).*www.99manga.com(?s).*" ) )
//        {
//            return Site.formString("NINENINE_MANGA_WWW");
//        }*/
//        else if ( webSite.matches( "(?s).*99manga.com(?s).*" ) )
//        {
//            return Site.formString("NINENINE_MANGA");
//        }
//        else if ( webSite.matches( "(?s).*www.99comic.com(?s).*" ) )
//        {
//            return Site.formString("NINENINE_COMIC_TC");
//        }
//        /*else if ( webSite.matches( "(?s).*99comic.com(?s).*" ) )
//        {
//            return Site.formString("NINENINE_COMIC");
//        }*/
//        else if ( webSite.matches( "(?s).*99mh.com(?s).*" ) )
//        {
//            return Site.formString("NINENINE_MH");
//        }
//        else if ( webSite.matches( "(?s).*mh.99770.cc(?s).*" ) )
//        {
//            return Site.formString("NINENINE_MH_99770");
//        }
//        /*else if ( webSite.matches( "(?s).*99770.cc(?s).*" ) )
//        {
//            return Site.formString("NINENINE_99770");
//        }*/
//        else if ( webSite.matches( "(?s).*www.cococomic.com(?s).*" ) )
//        {
//            return Site.formString("NINENINE_COCO_TC");
//        }
//        else if ( webSite.matches( "(?s).*cococomic.com(?s).*" ) )
//        {
//            return Site.formString("NINENINE_COCO");
//        }
//        /*else if ( webSite.matches( "(?s).*1mh.com(?s).*" ) )
//        {
//            return Site.formString("NINENINE_1MH");
//        }
//        else if ( webSite.matches( "(?s).*3gmanhua.com(?s).*" ) )
//        {
//            return Site.formString("NINENINE_3G");
//        }
//        */
//        //else if ( webSite.matches( "(?s).*\\.178.com(?s).*" ) )
//        //    return Site.formString("ONE_SEVEN_EIGHT");
//        else if ( webSite.matches( "(?s).*\\.8comic.com(?s).*" ) ||
//                  webSite.matches( "(?s).*comicvip.com(?s).*") )
//        {
//            if ( webSite.matches( "(?s).*photo(?s).*" )
//                    || webSite.matches( "(?s).*PHOTO(?s).*" )
//                    || webSite.matches( "(?s).*Photo(?s).*" ) ) // 圖集
//            {
//                return Site.formString("EIGHT_COMIC_PHOTO");
//            }
//            else // 漫畫
//            {
//                return Site.formString("EIGHT_COMIC");
//            }
//        }
//        else if ( webSite.matches( "(?s).*\\.jumpcn.com.cn(?s).*" ) )
//        {
//            return Site.formString("JUMPCNCN");
//        }
//        else if ( webSite.matches( "(?s).*dmeden\\.(?s).*" ) )
//        {
//            return Site.formString("DMEDEN");
//        }
//        else if ( webSite.matches( "(?s).*\\.jumpcn.com/(?s).*" ) )
//        {
//            return Site.formString("JUMPCN");
//        }
//        else if ( webSite.matches( "(?s).*mangafox.me/(?s).*" ) )
//        {
//            return Site.formString("MANGAFOX");
//        }
//        else if ( webSite.matches( "(?s).*\\.manmankan.com/(?s).*" ) )
//        {
//            return Site.formString("MANMANKAN");
//        }
//        else if ( webSite.matches( "(?s).*www.xindm.cn/(?s).*" ) )
//        {
//            return Site.formString("XINDM");
//        }
//        else if ( webSite.matches( "(?s).*google.com(?s).*" ) )
//        {
//            return Site.formString("GOOGLE_PIC");
//        }
//        else if ( webSite.matches( "(?s).*nanadm.com(?s).*" ) )
//        {
//            return Site.formString("NANA");
//        }
//        else if ( webSite.matches( "(?s).*citymanga.com(?s).*" ) )
//        {
//            return Site.formString("CITY_MANGA");
//        }
//        else if ( webSite.matches( "(?s).*iibq.com(?s).*" ) )
//        {
//            return Site.formString("IIBQ");
//        }
//        else if ( webSite.matches( "(?s).*baidu.com(?s).*" ) )
//        {
//            return Site.formString("BAIDU");
//        }
//        else if ( webSite.matches( "(?s).*sfacg.com(?s).*" ) )
//        {
//            return Site.formString("SF");
//        }
//        else if ( webSite.matches( "(?s).*kkkmh.com(?s).*" ) )
//        {
//            return Site.formString("KKKMH");
//        }
//        else if ( webSite.matches( "(?s).*6comic.com(?s).*" ) )
//        {
//            return Site.formString("SIX_COMIC");
//        }
//        else if ( webSite.matches( "(?s).*178.com(?s).*" ) || 
//                  webSite.matches( "(?s).*dmzj.com(?s).*" ) )
//        {
//            return Site.formString("MANHUA_178");
//        }
//        else if ( webSite.matches( "(?s).*kangdm.com(?s).*" ) || 
//                  webSite.matches( "(?s).*kyo.cn(?s).*" ) )
//        {
//            return Site.formString("KANGDM");
//        }
//        else if ( webSite.matches( "(?s).*bengou.com(?s).*" ) )
//        {
//            return Site.formString("BENGOU");
//        }
//        else if ( webSite.matches( "(?s).*emland.net(?s).*" ) )
//        {
//            return Site.formString("EMLAND");
//        }
//        else if ( webSite.matches( "(?s).*game.mop.com(?s).*" ) )
//        {
//            return Site.formString("MOP");
//        }
//        else if ( webSite.matches( "(?s).*dm5.com(?).*" ) )
//        {
//            return Site.formString("DM5");
//        }
//        else if ( webSite.matches( "(?s).*comic101.com(?s).*" ) || 
//                  webSite.matches( "(?s).*comic.101.com(?s).*" ) || 
//                  webSite.matches( "(?s).*mh.ck101.com(?s).*" ) ||
//                  webSite.matches( "(?s).*comic.ck101.com(?s).*" ) ||
//                  webSite.matches( "(?s).*.com/vols/\\d+/\\d+(?s).*" ) // 應付全部ck101的位址....
//                   )
//        {
//            return Site.formString("CK");
//        }
//        else if ( webSite.matches( "(?s).*tuku.cc(?s).*" ) )
//        {
//            return Site.formString("TUKU");
//        }
//        /*
//        else if ( webSite.matches( "(?s).*hhcomic.com(?s).*" ) || webSite.matches( "(?s).*3348.net(?s).*" ) )
//        {
//            return Site.formString("HH");
//        }
//        */
//        else if ( webSite.matches( "(?s).*iask.sina.com(?s).*" ) )
//        {
//            return Site.formString("IASK");
//        }
//        else if ( webSite.matches( "(?s).*jmymh.com(?s).*" ) )
//        {
//            return Site.formString("JM");
//        }
//        else if ( webSite.matches( "(?s).*mangawindow.com(?s).*" ) )
//        {
//            return Site.formString("MANGA_WINDOW");
//        }
//        else if ( webSite.matches( "(?s).*ck101.com(?s).*" ) )
//        {
//            return Site.formString("CK_NOVEL");
//        }
//        else if ( webSite.matches( "(?s).*mybest.com(?s).*" )
//                || webSite.matches( "(?s).*catcatbox.com(?s).*" ) )
//        {
//            return Site.formString("MYBEST");
//        }
//        else if ( webSite.matches( "(?s).*imanhua.com(?s).*" ) )
//        {
//            return Site.formString("IMANHUA");
//        }
//        else if ( webSite.matches( "(?s).*veryim.com(?s).*" ) )
//        {
//            return Site.formString("VERYIM");
//        }
//        else if ( webSite.matches( "(?s).*\\.wenku.com(?s).*" ) )
//        {
//            return Site.formString("WENKU");
//        }
//        /*
//        else if ( webSite.matches( "(?s).*fumanhua.com(?s).*" ) )
//        {
//            return Site.formString("FUMANHUA");
//        }
//        */
//        else if ( webSite.matches( "(?s).*6manga.com(?s).*" ) )
//        {
//            return Site.formString("SIX_MANGA");
//        }
//        else if ( webSite.matches( "(?s).*xxbh.net(?s).*" ) )
//        {
//            return Site.formString("XXBH");
//        }
//        else if ( webSite.matches( "(?s).*comic.131.com(?s).*" ) )
//        {
//            return Site.formString("COMIC_131");
//        }
//        else if ( webSite.matches( "(?s).*blogspot\\.(?s).*" ) )
//        {
//            return Site.formString("BLOGSPOT");
//        }
//        else if ( webSite.matches( "(?s).*pixnet.net(?s).*" ) )
//        {
//            return Site.formString("PIXNET_BLOG");
//        }
//        else if ( webSite.matches( "(?s).*blog.xuite.net(?s).*" ) )
//        {
//            return Site.formString("XUITE_BLOG");
//        }
//        else if ( webSite.matches( "(?s).*blog.yam.com(?s).*" ) )
//        {
//            return Site.formString("YAM_BLOG");
//        }
//        else if ( webSite.matches( "(?s).*eyny.com(?s).*" ) )
//        {
//            return Site.formString("EYNY_NOVEL");
//        }
//        else if ( webSite.matches( "(?s).*wikipedia.org/(?s).*" ) )
//        {
//            return Site.formString("WIKI");
//        }
//        else if ( webSite.matches( "(?s).*zuiwanju.com(?s).*" ) )
//        {
//            return Site.formString("ZUIWANJU");
//        }
//        else if ( webSite.matches( "(?s).*www.2ecy.com(?s).*" ) )
//        {
//            return Site.formString("TWO_ECY");
//        }
//        else if ( webSite.matches( "(?s).*tianyabook.com(?s).*" ) )
//        {
//            return Site.formString("TIANYA_BOOK");
//        }
//        else if ( webSite.matches( "(?s).*8novel.com/books/(?s).*" ) )
//        {
//            return Site.formString("EIGHT_NOVEL");
//        }
//        else if ( webSite.matches( "(?s).*book.qq.com/s/book/(?s).*" ) )
//        {
//            return Site.formString("QQ_BOOK");
//        }
//        else if ( webSite.matches( "(?s).*book.qq.com/origin/book/(?s).*" ) )
//        {
//            return Site.formString("QQ_ORIGIN_BOOK");
//        }
//        else if ( webSite.matches( "(?s).*book.sina.com.cn/book/(?s).*" ) )
//        {
//            return Site.formString("SINA_BOOK");
//        }
//        else if ( webSite.matches( "(?s).*book.51cto.com/art(?s).*" ) )
//        {
//            return Site.formString("FIVEONE_CTO");
//        }
//        else if ( webSite.matches( "(?s).*17kk.cc/(?s).*" ) )
//        {
//            return Site.formString("ONESEVEN_KK");
//        }
//        else if ( webSite.matches( "(?s).*uus8.com.*/" )
//                || webSite.matches( "(?s).*uus8.com.*/\\d+" )
//                || webSite.matches( "(?s).*uus8.com/book/display(?s).*" ) )
//        {
//            return Site.formString("UUS8");
//        }
//        else if ( webSite.matches( "(?s).*wenku8.cn/modules/article/reader.php(?s).*" )
//                || webSite.matches( "(?s).*wenku8.cn/novel/(?s).*" )
//                || webSite.matches( "(?s).*wenku8.cn/modules/article/articleinfo.php(?s).*" ) )
//        {
//            return Site.formString("WENKU8");
//        }
//        else if ( webSite.matches( "(?s).*book.ifeng.com/(?s).*" ) )
//        {
//            return Site.formString("IFENG_BOOK");
//        }
//        else if ( webSite.matches( "(?s).*xunlook.com/(?s).*" ) )
//        {
//            return Site.formString("XUNLOOK");
//        }
//        else if ( webSite.matches( "(?s).*7tianshi.com/(?s).*" ) )
//        {
//            return Site.formString("WENKU7");
//        }
//        else if ( webSite.matches( "(?s).*woyouxian.com/(?s).*" ) )
//        {
//            return Site.formString("WOYOUXIAN");
//        }
//        else if ( webSite.matches( "(?s).*shunong.com/(?s).*" ) )
//        {
//            return Site.formString("SHUNONG");
//        }
//        else if ( webSite.matches( "(?s).*music.sogou.com/(?s).*" ) )
//        {
//            return Site.formString("SOGOU");
//        }
//        else if ( webSite.matches( "(?s).*1ting.com/(?s).*" ) )
//        {
//            return Site.formString("TING1");
//        }
//        else if ( webSite.matches( "(?s).*xiami.com/(?s).*" ) )
//        {
//            return Site.formString("XIAMI");
//        }
//        else if ( webSite.matches( "(?s).*ptt.cc/(?s).*" ) )
//        {
//            return Site.formString("PTT");
//        }
//        else if ( webSite.matches( "(?s).*ishuhui.com(?s).*" ) )
//        {
//            return Site.formString("ISHUHUI");
//        }
        return Site.UNKNOWN;
    }
}    
        
    
