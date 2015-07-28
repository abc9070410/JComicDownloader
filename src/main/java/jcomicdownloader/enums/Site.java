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
 
}
