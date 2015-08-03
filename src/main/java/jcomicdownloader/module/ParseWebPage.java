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
    private Site siteID;

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
        this.siteID  = Site.detectSiteID(webSite);//Decouple to Site enum
        
        if (siteID == Site.UNKNOWN){
            Common.debugPrintln( "有未知的位址:" + webSite);
            Flag.downloadErrorFlag = true;
        }
    }

    public Site getSiteID()
    {
        return siteID;
    }
}
