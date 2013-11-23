/*
 ----------------------------------------------------------------------------------------------------
 Program Name : JComicDownloader
 Authors  : surveyorK
 Last Modified : 2013/7/22
 ----------------------------------------------------------------------------------------------------
 ChangeLog:
 5.17:修復8comic改變位址的問題。
 5.16:修復ck101改變位址的問題。
 5.16:修復kangdm.com改變網址的問題。
 5.09:修復ck101網址不全的問題。
 5.04: 修復wenku8無法下載的問題。
 ----------------------------------------------------------------------------------------------------
 */
package jcomicdownloader.module;

import jcomicdownloader.enums.*;
import jcomicdownloader.*;
import jcomicdownloader.tools.Common;

/**
 解析網址是屬於哪一個網站
 * */
public class ParseWebPage
{

    private String webSite;
    private int siteID;

    /**
     @author user
     */
    private ParseWebPage()
    {
    }

    public ParseWebPage( String webSite )
    {
        this();
        this.webSite = webSite;

        parseSiteID();
    }

    private void downFile()
    {
    }

    private void parseSiteID()
    {
        if ( webSite.matches( "(?s).*89890.com(?s).*" ) )
        {
            siteID = Site.CC;
        }
        else if ( webSite.matches( "(?s).*kukudm.com(?s).*" )
                || webSite.matches( "(?s).*socomic.com(?s).*" )
                || webSite.matches( "(?s).*socomic.net(?s).*" ) )
        {
            siteID = Site.KUKU;
        }
        else if ( webSite.matches( "(?s).*e-hentai(?s).*" ) )
        {
            siteID = Site.EH;
        }
        else if ( webSite.matches( "(?s).*exhentai.org(?s).*" ) )
        {
            siteID = Site.EX;
        }
        /*
        else if ( webSite.matches( "(?s).*dm.99manga.com(?s).*" ) )
        {
            siteID = Site.NINENINE_MANGA_TC;
        }
        else if ( webSite.matches( "(?s).*www.99manga.com(?s).*" ) )
        {
            siteID = Site.NINENINE_MANGA_WWW;
        }
        else if ( webSite.matches( "(?s).*99manga.com(?s).*" ) )
        {
            siteID = Site.NINENINE_MANGA;
        }
        else if ( webSite.matches( "(?s).*www.99comic.com(?s).*" ) )
        {
            siteID = Site.NINENINE_COMIC_TC;
        }
        else if ( webSite.matches( "(?s).*99comic.com(?s).*" ) )
        {
            siteID = Site.NINENINE_COMIC;
        }
        else if ( webSite.matches( "(?s).*99mh.com(?s).*" ) )
        {
            siteID = Site.NINENINE_MH;
        }
        else if ( webSite.matches( "(?s).*mh.99770.cc(?s).*" ) )
        {
            siteID = Site.NINENINE_MH_99770;
        }
        else if ( webSite.matches( "(?s).*99770.cc(?s).*" ) )
        {
            siteID = Site.NINENINE_99770;
        }
        else if ( webSite.matches( "(?s).*www.cococomic.com(?s).*" ) )
        {
            siteID = Site.NINENINE_COCO_TC;
        }
        else if ( webSite.matches( "(?s).*cococomic.com(?s).*" ) )
        {
            siteID = Site.NINENINE_COCO;
        }
        else if ( webSite.matches( "(?s).*1mh.com(?s).*" ) )
        {
            siteID = Site.NINENINE_1MH;
        }
        else if ( webSite.matches( "(?s).*3gmanhua.com(?s).*" ) )
        {
            siteID = Site.NINENINE_3G;
        }
        */
        //else if ( webSite.matches( "(?s).*\\.178.com(?s).*" ) )
        //    siteID = Site.ONE_SEVEN_EIGHT;
        else if ( webSite.matches( "(?s).*\\.8comic.com(?s).*" ) ||
                  webSite.matches( "(?s).*\\.comicvip.com(?s).*") )
        {
            if ( webSite.matches( "(?s).*photo(?s).*" )
                    || webSite.matches( "(?s).*PHOTO(?s).*" )
                    || webSite.matches( "(?s).*Photo(?s).*" ) ) // 圖集
            {
                siteID = Site.EIGHT_COMIC_PHOTO;
            }
            else // 漫畫
            {
                siteID = Site.EIGHT_COMIC;
            }
        }
        else if ( webSite.matches( "(?s).*\\.jumpcn.com.cn(?s).*" ) )
        {
            siteID = Site.JUMPCNCN;
        }
        /*
        else if ( webSite.matches( "(?s).*dmeden\\.(?s).*" ) )
        {
            siteID = Site.DMEDEN;
        }
        */
        else if ( webSite.matches( "(?s).*\\.jumpcn.com/(?s).*" ) )
        {
            siteID = Site.JUMPCN;
        }
        else if ( webSite.matches( "(?s).*mangafox.me/(?s).*" ) )
        {
            siteID = Site.MANGAFOX;
        }
        else if ( webSite.matches( "(?s).*\\.manmankan.com/(?s).*" ) )
        {
            siteID = Site.MANMANKAN;
        }
        else if ( webSite.matches( "(?s).*www.xindm.cn/(?s).*" ) )
        {
            siteID = Site.XINDM;
        }
        else if ( webSite.matches( "(?s).*google.com(?s).*" ) )
        {
            siteID = Site.GOOGLE_PIC;
        }
        else if ( webSite.matches( "(?s).*nanadm.com(?s).*" ) )
        {
            siteID = Site.NANA;
        }
        else if ( webSite.matches( "(?s).*citymanga.com(?s).*" ) )
        {
            siteID = Site.CITY_MANGA;
        }
        else if ( webSite.matches( "(?s).*iibq.com(?s).*" ) )
        {
            siteID = Site.IIBQ;
        }
        else if ( webSite.matches( "(?s).*baidu.com(?s).*" ) )
        {
            siteID = Site.BAIDU;
        }
        else if ( webSite.matches( "(?s).*sfacg.com(?s).*" ) )
        {
            siteID = Site.SF;
        }
        else if ( webSite.matches( "(?s).*kkkmh.com(?s).*" ) )
        {
            siteID = Site.KKKMH;
        }
        else if ( webSite.matches( "(?s).*6comic.com(?s).*" ) )
        {
            siteID = Site.SIX_COMIC;
        }
        else if ( webSite.matches( "(?s).*178.com(?s).*" ) || 
                  webSite.matches( "(?s).*dmzj.com(?s).*" ) )
        {
            siteID = Site.MANHUA_178;
        }
        else if ( webSite.matches( "(?s).*kangdm.com(?s).*" ) || 
                  webSite.matches( "(?s).*kyo.cn(?s).*" ) )
        {
            siteID = Site.KANGDM;
        }
        else if ( webSite.matches( "(?s).*bengou.com(?s).*" ) )
        {
            siteID = Site.BENGOU;
        }
        else if ( webSite.matches( "(?s).*emland.net(?s).*" ) )
        {
            siteID = Site.EMLAND;
        }
        else if ( webSite.matches( "(?s).*game.mop.com(?s).*" ) )
        {
            siteID = Site.MOP;
        }
        else if ( webSite.matches( "(?s).*dm5.com(?).*" ) )
        {
            siteID = Site.DM5;
        }
        else if ( webSite.matches( "(?s).*comic101.com(?s).*" ) || 
                  webSite.matches( "(?s).*comic.101.com(?s).*" ) || 
                  webSite.matches( "(?s).*mh.ck101.com(?s).*" ) ||
                  webSite.matches( "(?s).*comic.ck101.com(?s).*" ) ||
                  webSite.matches( "(?s).*.com/vols/\\d+/\\d+(?s).*" ) // 應付全部ck101的位址....
                   )
        {
            siteID = Site.CK;
        }
        else if ( webSite.matches( "(?s).*tuku.cc(?s).*" ) )
        {
            siteID = Site.TUKU;
        }
        /*
        else if ( webSite.matches( "(?s).*hhcomic.com(?s).*" ) || webSite.matches( "(?s).*3348.net(?s).*" ) )
        {
            siteID = Site.HH;
        }
        */
        else if ( webSite.matches( "(?s).*iask.sina.com(?s).*" ) )
        {
            siteID = Site.IASK;
        }
        else if ( webSite.matches( "(?s).*jmymh.com(?s).*" ) )
        {
            siteID = Site.JM;
        }
        else if ( webSite.matches( "(?s).*mangawindow.com(?s).*" ) )
        {
            siteID = Site.MANGA_WINDOW;
        }
        else if ( webSite.matches( "(?s).*ck101.com(?s).*" ) )
        {
            siteID = Site.CK_NOVEL;
        }
        else if ( webSite.matches( "(?s).*mybest.com(?s).*" )
                || webSite.matches( "(?s).*catcatbox.com(?s).*" ) )
        {
            siteID = Site.MYBEST;
        }
        else if ( webSite.matches( "(?s).*imanhua.com(?s).*" ) )
        {
            siteID = Site.IMANHUA;
        }
        else if ( webSite.matches( "(?s).*veryim.com(?s).*" ) )
        {
            siteID = Site.VERYIM;
        }
        else if ( webSite.matches( "(?s).*\\.wenku.com(?s).*" ) )
        {
            siteID = Site.WENKU;
        }
        /*
        else if ( webSite.matches( "(?s).*fumanhua.com(?s).*" ) )
        {
            siteID = Site.FUMANHUA;
        }
        */
        else if ( webSite.matches( "(?s).*6manga.com(?s).*" ) )
        {
            siteID = Site.SIX_MANGA;
        }
        else if ( webSite.matches( "(?s).*xxbh.net(?s).*" ) )
        {
            siteID = Site.XXBH;
        }
        else if ( webSite.matches( "(?s).*comic.131.com(?s).*" ) )
        {
            siteID = Site.COMIC_131;
        }
        else if ( webSite.matches( "(?s).*blogspot\\.(?s).*" ) )
        {
            siteID = Site.BLOGSPOT;
        }
        else if ( webSite.matches( "(?s).*pixnet.net(?s).*" ) )
        {
            siteID = Site.PIXNET_BLOG;
        }
        else if ( webSite.matches( "(?s).*blog.xuite.net(?s).*" ) )
        {
            siteID = Site.XUITE_BLOG;
        }
        else if ( webSite.matches( "(?s).*blog.yam.com(?s).*" ) )
        {
            siteID = Site.YAM_BLOG;
        }
        else if ( webSite.matches( "(?s).*eyny.com(?s).*" ) )
        {
            siteID = Site.EYNY_NOVEL;
        }
        else if ( webSite.matches( "(?s).*zuiwanju.com(?s).*" ) )
        {
            siteID = Site.ZUIWANJU;
        }
        else if ( webSite.matches( "(?s).*www.2ecy.com(?s).*" ) )
        {
            siteID = Site.TWO_ECY;
        }
        else if ( webSite.matches( "(?s).*tianyabook.com(?s).*" ) )
        {
            siteID = Site.TIANYA_BOOK;
        }
        else if ( webSite.matches( "(?s).*8novel.com/books/(?s).*" ) )
        {
            siteID = Site.EIGHT_NOVEL;
        }
        else if ( webSite.matches( "(?s).*book.qq.com/s/book/(?s).*" ) )
        {
            siteID = Site.QQ_BOOK;
        }
        else if ( webSite.matches( "(?s).*book.qq.com/origin/book/(?s).*" ) )
        {
            siteID = Site.QQ_ORIGIN_BOOK;
        }
        else if ( webSite.matches( "(?s).*book.sina.com.cn/book/(?s).*" ) )
        {
            siteID = Site.SINA_BOOK;
        }
        else if ( webSite.matches( "(?s).*book.51cto.com/art(?s).*" ) )
        {
            siteID = Site.FIVEONE_CTO;
        }
        else if ( webSite.matches( "(?s).*17kk.cc/(?s).*" ) )
        {
            siteID = Site.ONESEVEN_KK;
        }
        else if ( webSite.matches( "(?s).*uus8.com.*/" )
                || webSite.matches( "(?s).*uus8.com.*/\\d+" )
                || webSite.matches( "(?s).*uus8.com/book/display(?s).*" ) )
        {
            siteID = Site.UUS8;
        }
        else if ( webSite.matches( "(?s).*wenku8.cn/modules/article/reader.php(?s).*" )
                || webSite.matches( "(?s).*wenku8.cn/novel/(?s).*" )
                || webSite.matches( "(?s).*wenku8.cn/modules/article/articleinfo.php(?s).*" ) )
        {
            siteID = Site.WENKU8;
        }
        else if ( webSite.matches( "(?s).*book.ifeng.com/(?s).*" ) )
        {
            siteID = Site.IFENG_BOOK;
        }
        else if ( webSite.matches( "(?s).*xunlook.com/(?s).*" ) )
        {
            siteID = Site.XUNLOOK;
        }
        else if ( webSite.matches( "(?s).*7tianshi.com/(?s).*" ) )
        {
            siteID = Site.WENKU7;
        }
        else if ( webSite.matches( "(?s).*woyouxian.com/(?s).*" ) )
        {
            siteID = Site.WOYOUXIAN;
        }
        else if ( webSite.matches( "(?s).*shunong.com/(?s).*" ) )
        {
            siteID = Site.SHUNONG;
        }
        else if ( webSite.matches( "(?s).*music.sogou.com/(?s).*" ) )
        {
            siteID = Site.SOGOU;
        }
        else if ( webSite.matches( "(?s).*1ting.com/(?s).*" ) )
        {
            siteID = Site.TING1;
        }
        else if ( webSite.matches( "(?s).*xiami.com/(?s).*" ) )
        {
            siteID = Site.XIAMI;
        }
        else
        {
            siteID = Site.UNKNOWN;
            
            Common.debugPrintln( "有未知的位址:" + webSite);
            Flag.downloadErrorFlag = true;
        }
    }

    public int getSiteID()
    {
        return siteID;
    }
}
