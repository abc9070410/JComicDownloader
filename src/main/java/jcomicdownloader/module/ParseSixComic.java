/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jcomicdownloader.module;

import java.util.ArrayList;
import java.util.List;
import jcomicdownloader.enums.Site;
import jcomicdownloader.tools.Common;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

/**
 *
 * @author apple
 */
public class ParseSixComic extends ParseEC {
    
    
    public ParseSixComic(){
        super();
        enumName = "SIX_COMIC";
	parserName=this.getClass().getName();
        downloadBefore=true;
        siteID=Site.formString("SIX_COMIC");
        siteName = "SIX_COMIC";
        regexs= new String[]{"(?s).*6comic.com(?s).*"};
    }

    @Override
    public String getTitleOnSingleVolumePage( String urlString ) {
        // http://www.8comic.com/love/drawing-7853.html?ch=1 轉為
        // http://www.6comic.com/comic/manga-7853.html

        String[] splitURLs = urlString.split( "-|\\?" );

        String baseURL = "http://www.6comic.com/comic/manga-";
        String mainPageUrlString = baseURL + splitURLs[1];

        return getTitleOnMainPage( mainPageUrlString, getAllPageString( mainPageUrlString ) );
    }
    
    @Override
    public String getTitleOnMainPage( String urlString, String allPageString ) {
        Document nodes = Parser.parse(allPageString, urlString);
        String ret = nodes.getElementsByTag("title").text().split(" ")[0];

        if (ret.length() == 0)
            Common.errorReport("取得標題失敗！值為空");

        return ret;
    }
    
    @Override
    public List<List<String>> getVolumeTitleAndUrlOnMainPage( String urlString, String allPageString ) {
        // combine volumeList and urlList into combinationList, return it.

        List<List<String>> combinationList = new ArrayList<List<String>>();
        List<String> urlList = new ArrayList<String>();
        List<String> volumeList = new ArrayList<String>();
//
//        int beginIndex = allPageString.indexOf( "comicview.js\"" );
//        int endIndex = allPageString.indexOf( "id=\"tb_anime\"", beginIndex );
//        String tempString = allPageString.substring( beginIndex, endIndex );
//
//        totalVolume = tempString.split( "onmouseover=" ).length - 1;
//        Common.debugPrintln( "共有" + totalVolume + "集" );
//
//
//        String idString = ""; // ex. 7853
//        String volumeNoString = ""; // ex. 3
//        String volumeTitle = "";
//
//        beginIndex = endIndex = 0;
//        for ( int i = 0 ; i < totalVolume ; i++ ) {
//            // 取得單集位址
//            beginIndex = tempString.indexOf( "onmouseover=", beginIndex );
//            beginIndex = tempString.indexOf( "'", beginIndex ) + 1;
//            endIndex = tempString.indexOf( "'", beginIndex );
//            idString = tempString.substring( beginIndex, endIndex );
//
//            beginIndex = tempString.indexOf( "'", endIndex + 1 ) + 1;
//            endIndex = tempString.indexOf( "'", beginIndex );
//            volumeNoString = tempString.substring( beginIndex, endIndex );
//
//            urlList.add( getSinglePageURL( idString, volumeNoString ) );
//
//
//            // 取得單集名稱
//            beginIndex = tempString.indexOf( ">", endIndex ) + 1;
//            endIndex = tempString.indexOf( "<", beginIndex );
//
//            volumeTitle = getVolumeWithFormatNumber( Common.getStringRemovedIllegalChar(
//                    Common.getTraditionalChinese( tempString.substring( beginIndex, endIndex ).trim() ) ) );
//            volumeList.add( getVolumeWithFormatNumber( volumeTitle ) );
//
//        }
        Document doc = Jsoup.parse(allPageString);
        Elements ems =doc.getElementById("rp_ctl00_comiclist1_dl").getElementsByTag("a");
        for( Element e : ems){
            volumeList.add(getVolumeWithFormatNumber( Common.getStringRemovedIllegalChar(
                    Common.getTraditionalChinese(e.text()))));
            urlList.add( urlString +e.attr("href"));
        }
        combinationList.add( volumeList );
        combinationList.add( urlList );

        return combinationList;
    }

    // 取得單集頁面的網址
    public String getSinglePageURL( String idString, String volumeNoString ) {

        String baseMainURL = "http://www.8comic.com/love/drawing-";
        String volumeString = "?ch=" + volumeNoString;

        return baseMainURL + idString + ".html" + volumeString;
    }

    @Override
    public void printLogo() {
        System.out.println( " ____________________________________" );
        System.out.println( "|                                 " );
        System.out.println( "| Run the 6comic module: " );
        System.out.println( "|_____________________________________\n" );
    }
}
