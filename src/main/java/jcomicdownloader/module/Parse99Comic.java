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
public class Parse99Comic extends ParseNINENINE
{
    public Parse99Comic()
    {
        super();
//        regexs =new String[]{"(?s).*99comic.com(?s).*"};
        enumName = "NINENINE_COMIC";
        parserName=this.getClass().getName();
        siteID=Site.formString("NINENINE_COMIC");
        siteName = "99 comic";
    }
}
