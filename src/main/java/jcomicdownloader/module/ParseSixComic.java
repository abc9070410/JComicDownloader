/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jcomicdownloader.module;

import java.util.ArrayList;
import java.util.List;
import jcomicdownloader.tools.Common;

/**
 *
 * @author apple
 */
public class ParseSixComic extends ParseEC {

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
    public List<List<String>> getVolumeTitleAndUrlOnMainPage( String urlString, String allPageString ) {
        // combine volumeList and urlList into combinationList, return it.

        List<List<String>> combinationList = new ArrayList<List<String>>();
        List<String> urlList = new ArrayList<String>();
        List<String> volumeList = new ArrayList<String>();

        int beginIndex = allPageString.indexOf( "comicview.js\"" );
        int endIndex = allPageString.indexOf( "id=\"tb_anime\"", beginIndex );
        String tempString = allPageString.substring( beginIndex, endIndex );

        totalVolume = tempString.split( "onmouseover=" ).length - 1;
        Common.debugPrintln( "共有" + totalVolume + "集" );


        String idString = ""; // ex. 7853
        String volumeNoString = ""; // ex. 3
        String volumeTitle = "";

        beginIndex = endIndex = 0;
        for ( int i = 0 ; i < totalVolume ; i++ ) {
            // 取得單集位址
            beginIndex = tempString.indexOf( "onmouseover=", beginIndex );
            beginIndex = tempString.indexOf( "'", beginIndex ) + 1;
            endIndex = tempString.indexOf( "'", beginIndex );
            idString = tempString.substring( beginIndex, endIndex );

            beginIndex = tempString.indexOf( "'", endIndex + 1 ) + 1;
            endIndex = tempString.indexOf( "'", beginIndex );
            volumeNoString = tempString.substring( beginIndex, endIndex );

            urlList.add( getSinglePageURL( idString, volumeNoString ) );


            // 取得單集名稱
            beginIndex = tempString.indexOf( ">", endIndex ) + 1;
            endIndex = tempString.indexOf( "<", beginIndex );

            volumeTitle = getVolumeWithFormatNumber( Common.getStringRemovedIllegalChar(
                    Common.getTraditionalChinese( tempString.substring( beginIndex, endIndex ).trim() ) ) );
            volumeList.add( getVolumeWithFormatNumber( volumeTitle ) );

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

/**
 * The Java Class of translated JavaScript file "http://comicvip.com//js/nview.js"
 * @author hkgsherlock
 */
class NView_Java {
    /**
     * Storing the result URL to the image of that page of manga.
     */
    private String urlResult;

    /**
     * Number of chapters available in this manga.
     */
    private final int chs;

    /**
     * The numeric identifier (ID) of the manga.
     */
    private final int ti;

    /**
     * Compiled String stored in JavaScript variable exactly named as "cs" in Page View page.
     */
    private final String cs;
    /**
     * String of chapter, also found on URL param "ch". Format: "1", "1-3", "153-12"
     */
    private String ch;

    public NView_Java(int chapters, int mangaId, String compiledString, String chapter) {
        this.chs = chapters;
        this.ti = mangaId;
        this.cs = compiledString;
        this.ch = chapter;

        if (ch.indexOf('-') > 0) {
            p = Integer.parseInt(ch.split("-")[1]);
            ch = ch.split("-")[0];
        }
        
        this.sp();
    }

    public NView_Java(int chapters, int mangaId, String compiledString, int chapter, int page) {
        this.chs = chapters;
        this.ti = mangaId;
        this.cs = compiledString;
        this.ch = chapter + "";
        this.p = page;
        
        this.sp();
    }

    /**
     * Parse all sort of stuffs as a URL to the manga page image.
     * @return The image of that page of a manga.
     */
    public String parse() {
        si(c);
        return this.urlResult;
    }

    /**
     * Get pages available for this volume of manga.
     * <p><em>Note:</em> set the </p>
     * @return Count of pages of the volume.
     */
    public int getPagesCount() {
        String strCnt = ss(c, 7, 3);
        return Integer.parseInt(strCnt);
    }

    /**
     * Set the chapter for the following work.
     * @param chapter The number of chapter to work.
     */
    public void setChapter(int chapter) {
        this.ch = chapter + "";
    }

    /**
     * Set the page to work.
     * @param page The number of page to work.
     */
    public void setPage(int page) {
        this.p = page;
    }

    private String c = "";
    private final int f = 50;
    private int p = 1;

    /**
     * The first function called by the 8comic view page, which provides ability to decode the "cs"
     * (compiled string) in the page js to a image link hyper-referenced by img#TheImg DOM.
     */
    private void sp() {

        int cc = cs.length();
        for (int i = 0; i < cc / f; i++) {
            if (ss(cs, i * f, 4).equals(ch)) {
                c = ss(cs, i * f, f, f);
                break;
            }
        }
        if (c.equals("")) {
            c = ss(cs, cc - f, f);
            ch = chs + "";
        }
    }

    private String ss(String a, int b, int c) {
        return ss(a, b, c, null);
    }

    private String ss(String a, int b, int c, Object d) {
        String e = a.substring(b, b + c);
        return (d == null) ? e.replaceAll("[a-z]*", "") : e;
    }

    /**
     * Padding zero for a integer, letting the string of number becomes one which length of 3.
     */
    private String nn(int n) {
        return String.format("%03d", n);
    }

    private int mm(int p) {
        //noinspection RedundantCast
        return ((int)((p - 1) / 10) % 10) + (((p - 1) % 10) * 3);
    }

    private void si(String c) {
        this.urlResult = "http://img" + ss(c, 4, 2) + ".8comic.com/" + ss(c, 6, 1) + "/" + ti + "/" + ss(c, 0, 4) + "/" + nn(p) + "_" + ss(c, mm(p) + 10, 3, f) + ".jpg";
    }
}

