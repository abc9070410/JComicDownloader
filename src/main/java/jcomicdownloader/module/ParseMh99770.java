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
// mh.99770.cc 變繁體版了，所以套用cococomic的方式解析
public class ParseMh99770 extends ParseCocoTC
{

    public ParseMh99770()
    {
        super();
        enumName = "NINENINE_MH_99770";
        parserName=this.getClass().getName();
        regexs= new String[]{"(?s).*mh.99770.cc(?s).*"};
        siteID=Site.formString("NINENINE_MH_99770");
        siteName = "mh.99770.cc";
        jsURL = "http://mh.99770.cc/script/viewhtml.js";
    }

    @Override
    public void printLogo()
    {
        System.out.println( " ______________________________" );
        System.out.println( "|                           " );
        System.out.println( "| Run the mh.99770.cc module: " );
        System.out.println( "|_______________________________\n" );
    }
}