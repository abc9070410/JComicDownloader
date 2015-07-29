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
public class ParseCoco extends ParseNINENINE
{

    public ParseCoco()
    {
        super();
        enumName = "NINENINE_COCO";
        parserName=this.getClass().getName();
        regexs=new String[]{"(?s).*cococomic.com(?s).*"};
        siteID=Site.formString("NINENINE_COCO");
        siteName = "cococomic";

        jsURL = "http://cococomic.com/v3/i3.js";
    }
}