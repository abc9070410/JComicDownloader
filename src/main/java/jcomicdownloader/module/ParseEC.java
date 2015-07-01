/*
----------------------------------------------------------------------------------------------------
Program Name : JComicDownloader
Authors  : surveyorK
Last Modified : 2013/7/22
----------------------------------------------------------------------------------------------------
ChangeLog:
5.17: 修復8comic改變位址的問題。
5.16: 修復8comic解析失敗的問題。
5.06: 修復8comic因網站改版而解析錯誤的問題。
5.02: 修復8comic因網站改版而解析錯誤的問題。
2.09: 新增對6comic.com的支援。
1.17: 修復集數名稱後面數字會消失的bug。
1.08: 增加對於8comic的支援，包含免費漫畫和圖庫
----------------------------------------------------------------------------------------------------
 */
package jcomicdownloader.module;

import jcomicdownloader.tools.*;
import jcomicdownloader.enums.*;
import jcomicdownloader.*;

import java.io.*;
import java.util.*;
import java.text.*;
import jcomicdownloader.encode.Encoding;
import jcomicdownloader.encode.Zhcode;


public class ParseEC extends ParseOnlineComicSite {

    protected int radixNumber; // use to figure out the name of pic
    protected String volpic; // pic url: http://~/2011/111/dagui/06/
    protected int tpf2; // pic url: http://pic"tpf2".89890.com/~
    protected int tpf; // length of pic name: (tpf+1)*2
    protected String jsName;
    protected String indexName;
    protected String indexEncodeName;
    private String volumeNoString; // 每一集都有數字編號
    private String itemid; // 每本漫畫的編號

    public ParseEC() {
        siteID = Site.EIGHT_COMIC;
        siteName = "8comic";
        indexName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_8comic_parse_", "html" );
        indexEncodeName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_8comic_encode_parse_", "html" );

        jsName = "index_8comic.js";
        radixNumber = 18527345; // default value, not always be useful!!
        volumeNoString = "";
        itemid = "";
    }

    public ParseEC( String webSite, String titleName ) {
        this();
        this.webSite = webSite;
        this.title = titleName;
    }

    @Override
    public void setParameters() { // let all the non-set attributes get values
        Common.debugPrintln( "開始解析各參數 :" );

        Common.debugPrintln( "開始解析title和wholeTitle :" );

        Common.downloadFile( webSite, SetUp.getTempDirectory(), indexName, false, "", "" );
        Common.simpleDownloadFile( webSite, SetUp.getTempDirectory(), indexName, webSite );
        Common.newEncodeFile( SetUp.getTempDirectory(), indexName, indexEncodeName, Zhcode.BIG5 );
        String allPageString = Common.getFileString( SetUp.getTempDirectory(), indexEncodeName );
        
        // ex. http://www.8comic.com/love/drawing-8170.html?ch=3
        volumeNoString = webSite.split( "/|=" )[webSite.split( "/|=" ).length - 1];

        if ( getWholeTitle() == null || getWholeTitle().equals( "" ) ) {
            setWholeTitle( getTitle() + volumeNoString );
        }

        Common.debugPrintln( "作品名稱(title) : " + getTitle() );
        Common.debugPrintln( "章節名稱(wholeTitle) : " + getWholeTitle() );
    }

    @Override
    public void parseComicURL() { // parse URL and save all URLs in comicURL
        
        //取得ch
        int beginIndex = 0;
        int endIndex = 0;
        
        String ch = "1";
        if ( webSite.indexOf( "=" ) > 0 )
        {
            beginIndex = webSite.indexOf( "=" ) + 1;
            endIndex = webSite.length();
            ch = webSite.substring( beginIndex, endIndex );
        }
        Common.debugPrintln( "ch: " + ch );

        String allPageString = Common.getFileString( SetUp.getTempDirectory(), indexEncodeName );
        
        // 取得chs
        beginIndex = allPageString.indexOf( "var chs" );
        beginIndex = allPageString.indexOf( "=", beginIndex ) + 1;
        endIndex = allPageString.indexOf( ";", beginIndex );
        String chs = allPageString.substring( beginIndex, endIndex );
        Common.debugPrintln( "chs: " + chs );
        
        // 取得itemid
        beginIndex = allPageString.indexOf( "var ti" );
        beginIndex = allPageString.indexOf( "=", beginIndex ) + 1;
        endIndex = allPageString.indexOf( ";", beginIndex );
        String itemid = allPageString.substring( beginIndex, endIndex );
        Common.debugPrintln( "itemid(ti): " + itemid );
        
        // 取得圖片編碼
        beginIndex = allPageString.indexOf( "var cs", beginIndex );
        beginIndex = allPageString.indexOf( "\'", beginIndex ) + 1;
        endIndex = allPageString.indexOf( "\'", beginIndex );
        String allcodes = allPageString.substring( beginIndex, endIndex );

        NView_Java nv = new NView_Java(Integer.parseInt(chs), Integer.parseInt(itemid), allcodes, ch);
        this.comicURL = new String[nv.getPagesCount()];
        nv.setPage(1);
        for (int d = 1; d <= nv.getPagesCount(); nv.setPage(++d)) {
            this.comicURL[d - 1] = nv.parse();
        }
    }

    @Override
    public String getAllPageString( String urlString ) {
        String indexName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_8comic_", "html" );
        String indexEncodeName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_8comic_encode_", "html" );
        Common.downloadFile( urlString, SetUp.getTempDirectory(), indexName, false, "" );
        Common.newEncodeFile( SetUp.getTempDirectory(), indexName, indexEncodeName, Zhcode.BIG5 );

        return Common.getFileString( SetUp.getTempDirectory(), indexEncodeName ).replace( "&#22338;", "阪" );
    }

    @Override
    public boolean isSingleVolumePage( String urlString ) {
        if ( urlString.matches( "(?s).*/show/(?s).*" ) ) // ex. http://www.8comic.com/love/drawing-2245.html?ch=51
        {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String getTitleOnSingleVolumePage( String urlString ) {
        // http://www.8comic.com/love/drawing-8170.html?ch=2轉為http://www.8comic.com/html/8170.html

        String[] splitURLs = urlString.split( "://|/|-|\\?" );

        String baseURL = "http://www.8comic.com/html/";
        String mainPageUrlString = baseURL + splitURLs[4];

        return getTitleOnMainPage( mainPageUrlString, getAllPageString( mainPageUrlString ) );
    }

    @Override
    public String getTitleOnMainPage( String urlString, String allPageString ) {
        int beginIndex = allPageString.indexOf( "addhistory(" );
        beginIndex = allPageString.indexOf( ",", beginIndex );
        beginIndex = allPageString.indexOf( "\"", beginIndex ) + 1;
        int endIndex = allPageString.indexOf( "\"", beginIndex );

        String titleString = allPageString.substring( beginIndex, endIndex ).trim();
        
        System.out.println( titleString );

        return Common.getStringRemovedIllegalChar( Common.getTraditionalChinese( titleString ) );
    }

    @Override
    public List<List<String>> getVolumeTitleAndUrlOnMainPage( String urlString, String allPageString ) {
        // combine volumeList and urlList into combinationList, return it.

        List<List<String>> combinationList = new ArrayList<List<String>>();
        List<String> urlList = new ArrayList<String>();
        List<String> volumeList = new ArrayList<String>();

        totalVolume = allPageString.split( "onclick=\"cview" ).length - 1;
        Common.debugPrintln( "共有" + totalVolume + "集" );

        int beginIndex = 0;
        int endIndex = 0;
        
        for ( int i = 0 ; i < totalVolume ; i++ ) {
            
            // ex. cview('2245-49.html' 取 2245-49.html
            beginIndex = allPageString.indexOf( "onclick=\"cview", beginIndex );
            beginIndex = allPageString.indexOf( "'", beginIndex ) + 1;
            endIndex = allPageString.indexOf( "'", beginIndex );
            String idAndVolume = allPageString.substring( beginIndex, endIndex );
            
            // ex.cview('104-97.html',8) -> 取8
            beginIndex = allPageString.indexOf( ",", beginIndex ) + 1;
            endIndex = allPageString.indexOf( ")", beginIndex ); 
            String catid = allPageString.substring( beginIndex, endIndex ).trim();

            // 取得單集位址
            String idString = idAndVolume.split( "-|\\." )[0];
            String volumeNoString = idAndVolume.split( "-|\\." )[1];
            String tempURL = getSinglePageURL( idString, volumeNoString, catid );
            urlList.add( tempURL );
            
            // 取得單集名稱
            beginIndex = allPageString.indexOf( ">", beginIndex ) + 1;
            endIndex = allPageString.indexOf( "<", beginIndex ); 
            String volumeTitle = allPageString.substring( beginIndex, endIndex ).trim();
            
            if ( volumeTitle == null || volumeTitle.matches( "" )  ) 
            {
                beginIndex = allPageString.indexOf( ">", beginIndex ) + 1;
                endIndex = allPageString.indexOf( "<", beginIndex ); 
                volumeTitle = allPageString.substring( beginIndex, endIndex ).trim();
            }
            
            volumeTitle = getVolumeWithFormatNumber( Common.getStringRemovedIllegalChar(
                    Common.getTraditionalChinese( volumeTitle.trim() ) ) );
            volumeList.add( getVolumeWithFormatNumber( volumeTitle ) );
            
            
            //Common.debugPrintln( tempURL + " " + volumeTitle );

        }

        combinationList.add( volumeList );
        combinationList.add( urlList );

        return combinationList;
    }

    // 取得單集頁面的網址
    public String getSinglePageURL( String idString, String volumeNoString, String catidString ) {

        String baseurl= "";
        String volumeString = "?ch=" + volumeNoString;
        
        int catid = Integer.parseInt( catidString );
        
        if(catid==4 || catid==6 || catid==12 ||catid==22 ) 
            baseurl="http://new.comicvip.com/show/cool-";
        if(catid==1 || catid==17 || catid==19 || catid==21) 
            baseurl="http://new.comicvip.com/show/cool-";
        if(catid==2 || catid==5 || catid==7 || catid==9)  
            baseurl="http://new.comicvip.com/show/cool-";
        if(catid==10 || catid==11 || catid==13 || catid==14) 
            baseurl="http://new.comicvip.com/show/best-manga-";
        if(catid==3 || catid==8 || catid==15 || catid==16 ||catid==18 ||catid==20)
            baseurl="http://new.comicvip.com/show/best-manga-";
        
        
        return baseurl + idString + ".html" + volumeString;
    }

    @Override
    public void printLogo() {
        System.out.println( " _____________________________" );
        System.out.println( "|                          " );
        System.out.println( "| Run the 8comic module: " );
        System.out.println( "|______________________________\n" );
    }

    @Override
    public String getMainUrlFromSingleVolumeUrl( String volumeURL ) {
        throw new UnsupportedOperationException( "Not supported yet." );
    }
}

class ParseECphoto extends ParseEC {

    public ParseECphoto() {
        siteID = Site.EIGHT_COMIC_PHOTO;
        indexName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_8comic_photo_parse_", "html" );
        indexEncodeName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_8comic_photo_encode_parse_", "html" );

        jsName = "index_8comic_photo.js";
    }

    @Override
    public void setParameters() { // let all the non-set attributes get values
        Common.debugPrintln( "開始解析各參數 :" );

        Common.debugPrintln( "開始解析title和wholeTitle :" );

        String allPageString = getAllPageString( webSite );

        if ( getWholeTitle() == null || getWholeTitle().equals( "" ) ) {
            int beginIndex = allPageString.indexOf( "<title>" ) + 7;
            int endIndex = allPageString.indexOf( "</title>", beginIndex );
            String titleString = allPageString.substring( beginIndex, endIndex );

            setWholeTitle( getVolumeWithFormatNumber( 
                    Common.getStringRemovedIllegalChar( titleString ) ) );
        }

        Common.debugPrintln( "作品名稱(title) : " + getTitle() );
        Common.debugPrintln( "章節名稱(wholeTitle) : " + getWholeTitle() );
    }

    @Override
    public void parseComicURL() { // parse URL and save all URLs in comicURL
        // 先取得前面的下載伺服器網址
        String allPageString = getAllPageString( webSite );

        Common.debugPrint( "開始解析這一集有幾頁 :" );
        totalPage = allPageString.split( "\\.jpe'" ).length - 1;
        comicURL = new String[totalPage];
        Common.debugPrint( "共" + totalPage + "頁" );

        String[] tokens = allPageString.split( "'|\\." );

        int page = 0;
        for ( int i = 0 ; i < tokens.length ; i++ ) {
            if ( tokens[i].equals( "jpe" ) ) {
                comicURL[page++] = "http://www.8comic.com" + tokens[i - 1] + ".jpg";
                //Common.debugPrintln( (page-1) + " " + comicURL[page-1] );
            }
        }

        //System.exit(0);
    }

    @Override
    public boolean isSingleVolumePage( String urlString ) {
        if ( urlString.matches( "(?s).*\\d+-\\d+.html(?s).*" ) ) // ex. http://www.8comic.com/photo/1-1.html
        {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public String getTitleOnSingleVolumePage( String urlString ) {
        return "8comic圖集";
    }

    @Override
    public String getTitleOnMainPage( String urlString, String allPageString ) {
        return "8comic圖集";
    }

    @Override
    public List<List<String>> getVolumeTitleAndUrlOnMainPage( String urlString, String allPageString ) {
        // combine volumeList and urlList into combinationList, return it.

        List<List<String>> combinationList = new ArrayList<List<String>>();
        List<String> urlList = new ArrayList<String>();
        List<String> volumeList = new ArrayList<String>();

        int beginIndex = allPageString.indexOf( "id=\"newphoto_dl" );
        int endIndex = allPageString.indexOf( "id=\"newphoto_pager", beginIndex );
        String tempString = allPageString.substring( beginIndex, endIndex );
        String[] tempStrings = tempString.split( "\\d*>\\d*|\\d*<\\d*|\"" );

        totalVolume = tempString.split( "href=" ).length - 1;
        Common.debugPrintln( "共有" + totalVolume + "個圖集" );

        int nowVolume = 0;
        for ( int i = 0 ; i < tempStrings.length ; i++ ) {
            if ( tempStrings[i].matches( "(?s).*href=(?s).*" ) ) {
                urlList.add( "http://www.8comic.com" + tempStrings[i + 1] );
            } else if ( tempStrings[i].matches( "(?s).*br.*" ) ) {
                volumeList.add( tempStrings[i + 1].trim() );
            }
        }

        combinationList.add( volumeList );
        combinationList.add( urlList );

        return combinationList;
    }
}

class ParseSixComic extends ParseEC {

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
 * The Java Class of translated JavaScript file "http://new.comicvip.com//js/nview.js"
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
