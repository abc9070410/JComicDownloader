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
    UNKNOWN(0),
    CC(1),
    KUKU(2),
    EH(3),
    NINENINE_MANGA(4),
    NINENINE_COMIC(5),
    NINENINE_99770(6),
    NINENINE_COCO(7),
    NINENINE_MH(8),
    NINENINE_1MH(9),
    NINENINE_3G(10),
    ONE_SEVEN_EIGHT(11),
    EIGHT_COMIC(11),
    EIGHT_COMIC_PHOTO(12),
    JUMPCNCN(13),
    DMEDEN(14),
    JUMPCN(15),
    MANGAFOX(16),
    MANMANKAN(17), 
    XINDM(18), 
    EX(19), 
    WY(20), 
    GOOGLE_PIC(21),
    BING_PIC(22),
    BAIDU_PIC(23),
    NANA(24),
    CITY_MANGA(25),
    IIBQ(26),
    BAIDU(27),
    SF(28),
    KKKMH(29),
    SIX_COMIC(30),
    MANHUA_178(31),
    KANGDM(32),
    BENGOU(33),
    EMLAND(34),
    MOP(35),
    DM5(36),
    CK(37),
    TUKU(38),
    HH(39),
    IASK(40),
    NINENINE_MH_99770(41),
    JM(42),
    DM5_ENGLISH(43),
    NINENINE_COMIC_TC(44),
    NINENINE_MANGA_TC(45),
    MANGA_WINDOW(46),
    CK_NOVEL(47),
    MYBEST(48),
    IMANHUA(49),
    VERYIM(50),
    WENKU(51),
    FUMANHUA(52),
    SIX_MANGA(53),
    NINENINE_COCO_TC(54),
    XXBH(55),
    COMIC_131(56), 
    BLOGSPOT(57), // 
    PIXNET_BLOG(58),
    XUITE_BLOG(59),
    YAM_BLOG(60),
    EYNY_NOVEL(61),
    //t KKMH(62),
    ZUIWANJU(63), // zuiwanju.
    TWO_ECY(64), 
    TIANYA_BOOK(65), 
    NINENINE_MANGA_WWW(66), 
    EIGHT_NOVEL(67), 
    QQ_BOOK(68), 
    SINA_BOOK(69), 
    FIVEONE_CTO(70),
    ONESEVEN_KK(71),
    QQ_ORIGIN_BOOK(72), 
    UUS8(73), 
    WENKU8(74), 
    IFENG_BOOK(75), 
    XUNLOOK(76), 
    WENKU7(77),  // 
    WOYOUXIAN(78),  // 
    SHUNONG(79),
    SOGOU(80),
    TING1(81),
    XIAMI(82),
    WIKI(83),
    PTT(84),
    ISHUHUI(85); // ishuhui
    
    private int value;

    private Site(int value){
        this.value=value;
    }
    
 
}
