/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jcomicdownloader.module;

import jcomicdownloader.enums.Site;

/**
 *
 * @author apple
 */
public class Parse1Mh extends Parse99ComicTC
{
    public Parse1Mh()
    {
        super();
        enumName = "NINENINE_1MH";
        parserName=this.getClass().getName();
        siteID=Site.formString("NINENINE_1MH");
        siteName = "1mh.com";
        regexs =new String[]{"(?s).*1mh.com(?s).*"};
        baseURL = "http://1mh.com";
    }

    @Override // 從網址判斷是否為單集頁面(true) 還是主頁面(false)
    public boolean isSingleVolumePage( String urlString )
    {
        // ex. http://1mh.com/page/11842l98825/
        if ( urlString.matches( "(?s).*/page/(?s).*" ) )
        {
            return true;
        }
        else // ex. http://1mh.com/mh/mh11842/
        {
            return false;
        }
    }
}

