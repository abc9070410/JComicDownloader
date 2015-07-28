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

/**
 * 網站的代號，當作Enum用
 * */
public enum Site {
    UNKNOWN(0,""),
    CC(1,"ParseCC"),
    KUKU(2,"ParseKUKU"),
    EH(3,"ParseEH"),
    NINENINE_MANGA(4,"Parse99Manga"),
    NINENINE_COMIC(5,"Parse99Comic"),
    NINENINE_99770(6,"Parse99770"),
    NINENINE_COCO(7,"ParseCoco"),
    NINENINE_MH(8,"Parse99Mh"),
    NINENINE_1MH(9,"Parse1Mh"),
    NINENINE_3G(10,"Parse3G"),
    ONE_SEVEN_EIGHT(11,"Parse178"),
    EIGHT_COMIC(11,"ParseEC"),
    EIGHT_COMIC_PHOTO(12,"ParseECphoto"),
    JUMPCNCN(13,"ParseJumpcncn"),
    DMEDEN(14,"ParseDmeden"),
    JUMPCN(15,"ParseJumpcn"),
    MANGAFOX(16,"ParseMangaFox"),
    MANMANKAN(17,"ParseManmankan"), 
    XINDM(18,"ParseXindm"), 
    EX(19,"ParseEX"), 
    WY(20,""), 
    GOOGLE_PIC(21,"ParseGooglePic"),
    BING_PIC(22,""),
    BAIDU_PIC(23,""),
    NANA(24,"ParseNANA"),
    CITY_MANGA(25,"ParseCityManga"),
    IIBQ(26,"ParseIIBQ"),
    BAIDU(27,"ParseBAIDU"),
    SF(28,"ParseSF"),
    KKKMH(29,"ParseKKKMH"),
    SIX_COMIC(30,"ParseSixComic"),
    MANHUA_178(31,"Parse178"),
    KANGDM(32,"ParseKangdm"),
    BENGOU(33,"ParseBengou"),
    EMLAND(34,"ParseEmland"),
    MOP(35,"ParseMOP"),
    DM5(36,"ParseDM5"),
    CK(37,"ParseCK"),
    TUKU(38,"ParseTUKU"),
    HH(39,"ParseHH"),
    IASK(40,"ParseIASK"),
    NINENINE_MH_99770(41,"ParseMh99770"),
    JM(42,"ParseJM"),
    //DM5_ENGLISH(43,""),
    NINENINE_COMIC_TC(44,"Parse99ComicTC"),
    NINENINE_MANGA_TC(45,"Parse99MangaTC"),
    MANGA_WINDOW(46,"ParseMangaWindow"),
    CK_NOVEL(47,"ParseCKNovel"),
    MYBEST(48,"ParseMyBest"),
    IMANHUA(49,"ParseImanhua"),
    VERYIM(50,"ParseVeryim"),
    WENKU(51,"ParseWenku"),
    FUMANHUA(52,"ParseFumanhua"),
    SIX_MANGA(53,"ParseSixManga"),
    NINENINE_COCO_TC(54,"ParseCocoTC"),
    XXBH(55,"ParseXXBH"),
    COMIC_131(56,"Parse131"), 
    BLOGSPOT(57,"ParseBlogspot"), // 
    PIXNET_BLOG(58,"ParsePixnetBlog"),
    XUITE_BLOG(59,"ParseXuiteBlog"),
    YAM_BLOG(60,"ParseYamBlog"),
    EYNY_NOVEL(61,"ParseEynyNovel"),
    //t KKMH(62),
    ZUIWANJU(63,"ParseZuiwanju"), // zuiwanju.
    TWO_ECY(64,"Parse2ecy"), 
    TIANYA_BOOK(65,"ParseTianyaBook"), 
    NINENINE_MANGA_WWW(66,"Parse99MangaWWW"), 
    EIGHT_NOVEL(67,"ParseEightNovel"), 
    QQ_BOOK(68,"ParseQQBook"), 
    SINA_BOOK(69,"ParseSinaBook"), 
    FIVEONE_CTO(70,"Parse51Cto"),
    ONESEVEN_KK(71,"Parse17KK"),
    QQ_ORIGIN_BOOK(72,"ParseQQOriginBook"), 
    UUS8(73,"ParseUUS8"), 
    WENKU8(74,"ParseWenku8"), 
    IFENG_BOOK(75,"ParseIfengBook"), 
    XUNLOOK(76,"ParseXunlook"), 
    WENKU7(77,"WENKU7"),  // 
    WOYOUXIAN(78,"WOYOUXIAN"),  // 
    SHUNONG(79,"ParseShunong"),
    SOGOU(80,"ParseSogou"),
    TING1(81,"Parse1Ting"),
    XIAMI(82,"ParseXiami"),
    WIKI(83,"ParseWiki"),
    PTT(84,"ParsePtt"),
    ISHUHUI(85,"ParseIshuhui"); // ishuhui
    
    private final int value;
    private final String parserName;
    
    public int intValue(){
        return this.value;
    }
    
    private Site(int value, String parserName){
        this.parserName = parserName;
        this.value=value;
    }
    
    public String getParserName(){
        return this.parserName;
    }    
    
    public static Site detectSiteID(final String webSite){
        
        if ( webSite.matches( "(?s).*89890.com(?s).*" ) )
        {
            return Site.CC;
        }
        else if ( webSite.matches( "(?s).*kukudm.com(?s).*" )
                || webSite.matches( "(?s).*socomic.com(?s).*" )
                || webSite.matches( "(?s).*socomic.net(?s).*" ) )
        {
            return Site.KUKU;
        }
        else if ( webSite.matches( "(?s).*e-hentai(?s).*" ) )
        {
            return Site.EH;
        }
        else if ( webSite.matches( "(?s).*exhentai.org(?s).*" ) )
        {
            return Site.EX;
        }

        else if ( webSite.matches( "(?s).*dm.99manga.com(?s).*" ) )
        {
            return Site.NINENINE_MANGA_TC;
        }
        /*else if ( webSite.matches( "(?s).*www.99manga.com(?s).*" ) )
        {
            return Site.NINENINE_MANGA_WWW;
        }*/
        else if ( webSite.matches( "(?s).*99manga.com(?s).*" ) )
        {
            return Site.NINENINE_MANGA;
        }
        else if ( webSite.matches( "(?s).*www.99comic.com(?s).*" ) )
        {
            return Site.NINENINE_COMIC_TC;
        }
        /*else if ( webSite.matches( "(?s).*99comic.com(?s).*" ) )
        {
            return Site.NINENINE_COMIC;
        }*/
        else if ( webSite.matches( "(?s).*99mh.com(?s).*" ) )
        {
            return Site.NINENINE_MH;
        }
        else if ( webSite.matches( "(?s).*mh.99770.cc(?s).*" ) )
        {
            return Site.NINENINE_MH_99770;
        }
        /*else if ( webSite.matches( "(?s).*99770.cc(?s).*" ) )
        {
            return Site.NINENINE_99770;
        }*/
        else if ( webSite.matches( "(?s).*www.cococomic.com(?s).*" ) )
        {
            return Site.NINENINE_COCO_TC;
        }
        else if ( webSite.matches( "(?s).*cococomic.com(?s).*" ) )
        {
            return Site.NINENINE_COCO;
        }
        /*else if ( webSite.matches( "(?s).*1mh.com(?s).*" ) )
        {
            return Site.NINENINE_1MH;
        }
        else if ( webSite.matches( "(?s).*3gmanhua.com(?s).*" ) )
        {
            return Site.NINENINE_3G;
        }
        */
        //else if ( webSite.matches( "(?s).*\\.178.com(?s).*" ) )
        //    return Site.ONE_SEVEN_EIGHT;
        else if ( webSite.matches( "(?s).*\\.8comic.com(?s).*" ) ||
                  webSite.matches( "(?s).*comicvip.com(?s).*") )
        {
            if ( webSite.matches( "(?s).*photo(?s).*" )
                    || webSite.matches( "(?s).*PHOTO(?s).*" )
                    || webSite.matches( "(?s).*Photo(?s).*" ) ) // 圖集
            {
                return Site.EIGHT_COMIC_PHOTO;
            }
            else // 漫畫
            {
                return Site.EIGHT_COMIC;
            }
        }
        else if ( webSite.matches( "(?s).*\\.jumpcn.com.cn(?s).*" ) )
        {
            return Site.JUMPCNCN;
        }
        else if ( webSite.matches( "(?s).*dmeden\\.(?s).*" ) )
        {
            return Site.DMEDEN;
        }
        else if ( webSite.matches( "(?s).*\\.jumpcn.com/(?s).*" ) )
        {
            return Site.JUMPCN;
        }
        else if ( webSite.matches( "(?s).*mangafox.me/(?s).*" ) )
        {
            return Site.MANGAFOX;
        }
        else if ( webSite.matches( "(?s).*\\.manmankan.com/(?s).*" ) )
        {
            return Site.MANMANKAN;
        }
        else if ( webSite.matches( "(?s).*www.xindm.cn/(?s).*" ) )
        {
            return Site.XINDM;
        }
        else if ( webSite.matches( "(?s).*google.com(?s).*" ) )
        {
            return Site.GOOGLE_PIC;
        }
        else if ( webSite.matches( "(?s).*nanadm.com(?s).*" ) )
        {
            return Site.NANA;
        }
        else if ( webSite.matches( "(?s).*citymanga.com(?s).*" ) )
        {
            return Site.CITY_MANGA;
        }
        else if ( webSite.matches( "(?s).*iibq.com(?s).*" ) )
        {
            return Site.IIBQ;
        }
        else if ( webSite.matches( "(?s).*baidu.com(?s).*" ) )
        {
            return Site.BAIDU;
        }
        else if ( webSite.matches( "(?s).*sfacg.com(?s).*" ) )
        {
            return Site.SF;
        }
        else if ( webSite.matches( "(?s).*kkkmh.com(?s).*" ) )
        {
            return Site.KKKMH;
        }
        else if ( webSite.matches( "(?s).*6comic.com(?s).*" ) )
        {
            return Site.SIX_COMIC;
        }
        else if ( webSite.matches( "(?s).*178.com(?s).*" ) || 
                  webSite.matches( "(?s).*dmzj.com(?s).*" ) )
        {
            return Site.MANHUA_178;
        }
        else if ( webSite.matches( "(?s).*kangdm.com(?s).*" ) || 
                  webSite.matches( "(?s).*kyo.cn(?s).*" ) )
        {
            return Site.KANGDM;
        }
        else if ( webSite.matches( "(?s).*bengou.com(?s).*" ) )
        {
            return Site.BENGOU;
        }
        else if ( webSite.matches( "(?s).*emland.net(?s).*" ) )
        {
            return Site.EMLAND;
        }
        else if ( webSite.matches( "(?s).*game.mop.com(?s).*" ) )
        {
            return Site.MOP;
        }
        else if ( webSite.matches( "(?s).*dm5.com(?).*" ) )
        {
            return Site.DM5;
        }
        else if ( webSite.matches( "(?s).*comic101.com(?s).*" ) || 
                  webSite.matches( "(?s).*comic.101.com(?s).*" ) || 
                  webSite.matches( "(?s).*mh.ck101.com(?s).*" ) ||
                  webSite.matches( "(?s).*comic.ck101.com(?s).*" ) ||
                  webSite.matches( "(?s).*.com/vols/\\d+/\\d+(?s).*" ) // 應付全部ck101的位址....
                   )
        {
            return Site.CK;
        }
        else if ( webSite.matches( "(?s).*tuku.cc(?s).*" ) )
        {
            return Site.TUKU;
        }
        /*
        else if ( webSite.matches( "(?s).*hhcomic.com(?s).*" ) || webSite.matches( "(?s).*3348.net(?s).*" ) )
        {
            return Site.HH;
        }
        */
        else if ( webSite.matches( "(?s).*iask.sina.com(?s).*" ) )
        {
            return Site.IASK;
        }
        else if ( webSite.matches( "(?s).*jmymh.com(?s).*" ) )
        {
            return Site.JM;
        }
        else if ( webSite.matches( "(?s).*mangawindow.com(?s).*" ) )
        {
            return Site.MANGA_WINDOW;
        }
        else if ( webSite.matches( "(?s).*ck101.com(?s).*" ) )
        {
            return Site.CK_NOVEL;
        }
        else if ( webSite.matches( "(?s).*mybest.com(?s).*" )
                || webSite.matches( "(?s).*catcatbox.com(?s).*" ) )
        {
            return Site.MYBEST;
        }
        else if ( webSite.matches( "(?s).*imanhua.com(?s).*" ) )
        {
            return Site.IMANHUA;
        }
        else if ( webSite.matches( "(?s).*veryim.com(?s).*" ) )
        {
            return Site.VERYIM;
        }
        else if ( webSite.matches( "(?s).*\\.wenku.com(?s).*" ) )
        {
            return Site.WENKU;
        }
        /*
        else if ( webSite.matches( "(?s).*fumanhua.com(?s).*" ) )
        {
            return Site.FUMANHUA;
        }
        */
        else if ( webSite.matches( "(?s).*6manga.com(?s).*" ) )
        {
            return Site.SIX_MANGA;
        }
        else if ( webSite.matches( "(?s).*xxbh.net(?s).*" ) )
        {
            return Site.XXBH;
        }
        else if ( webSite.matches( "(?s).*comic.131.com(?s).*" ) )
        {
            return Site.COMIC_131;
        }
        else if ( webSite.matches( "(?s).*blogspot\\.(?s).*" ) )
        {
            return Site.BLOGSPOT;
        }
        else if ( webSite.matches( "(?s).*pixnet.net(?s).*" ) )
        {
            return Site.PIXNET_BLOG;
        }
        else if ( webSite.matches( "(?s).*blog.xuite.net(?s).*" ) )
        {
            return Site.XUITE_BLOG;
        }
        else if ( webSite.matches( "(?s).*blog.yam.com(?s).*" ) )
        {
            return Site.YAM_BLOG;
        }
        else if ( webSite.matches( "(?s).*eyny.com(?s).*" ) )
        {
            return Site.EYNY_NOVEL;
        }
        else if ( webSite.matches( "(?s).*wikipedia.org/(?s).*" ) )
        {
            return Site.WIKI;
        }
        else if ( webSite.matches( "(?s).*zuiwanju.com(?s).*" ) )
        {
            return Site.ZUIWANJU;
        }
        else if ( webSite.matches( "(?s).*www.2ecy.com(?s).*" ) )
        {
            return Site.TWO_ECY;
        }
        else if ( webSite.matches( "(?s).*tianyabook.com(?s).*" ) )
        {
            return Site.TIANYA_BOOK;
        }
        else if ( webSite.matches( "(?s).*8novel.com/books/(?s).*" ) )
        {
            return Site.EIGHT_NOVEL;
        }
        else if ( webSite.matches( "(?s).*book.qq.com/s/book/(?s).*" ) )
        {
            return Site.QQ_BOOK;
        }
        else if ( webSite.matches( "(?s).*book.qq.com/origin/book/(?s).*" ) )
        {
            return Site.QQ_ORIGIN_BOOK;
        }
        else if ( webSite.matches( "(?s).*book.sina.com.cn/book/(?s).*" ) )
        {
            return Site.SINA_BOOK;
        }
        else if ( webSite.matches( "(?s).*book.51cto.com/art(?s).*" ) )
        {
            return Site.FIVEONE_CTO;
        }
        else if ( webSite.matches( "(?s).*17kk.cc/(?s).*" ) )
        {
            return Site.ONESEVEN_KK;
        }
        else if ( webSite.matches( "(?s).*uus8.com.*/" )
                || webSite.matches( "(?s).*uus8.com.*/\\d+" )
                || webSite.matches( "(?s).*uus8.com/book/display(?s).*" ) )
        {
            return Site.UUS8;
        }
        else if ( webSite.matches( "(?s).*wenku8.cn/modules/article/reader.php(?s).*" )
                || webSite.matches( "(?s).*wenku8.cn/novel/(?s).*" )
                || webSite.matches( "(?s).*wenku8.cn/modules/article/articleinfo.php(?s).*" ) )
        {
            return Site.WENKU8;
        }
        else if ( webSite.matches( "(?s).*book.ifeng.com/(?s).*" ) )
        {
            return Site.IFENG_BOOK;
        }
        else if ( webSite.matches( "(?s).*xunlook.com/(?s).*" ) )
        {
            return Site.XUNLOOK;
        }
        else if ( webSite.matches( "(?s).*7tianshi.com/(?s).*" ) )
        {
            return Site.WENKU7;
        }
        else if ( webSite.matches( "(?s).*woyouxian.com/(?s).*" ) )
        {
            return Site.WOYOUXIAN;
        }
        else if ( webSite.matches( "(?s).*shunong.com/(?s).*" ) )
        {
            return Site.SHUNONG;
        }
        else if ( webSite.matches( "(?s).*music.sogou.com/(?s).*" ) )
        {
            return Site.SOGOU;
        }
        else if ( webSite.matches( "(?s).*1ting.com/(?s).*" ) )
        {
            return Site.TING1;
        }
        else if ( webSite.matches( "(?s).*xiami.com/(?s).*" ) )
        {
            return Site.XIAMI;
        }
        else if ( webSite.matches( "(?s).*ptt.cc/(?s).*" ) )
        {
            return Site.PTT;
        }
        else if ( webSite.matches( "(?s).*ishuhui.com(?s).*" ) )
        {
            return Site.ISHUHUI;
        }
        return Site.UNKNOWN;
    }
}    
        
    
