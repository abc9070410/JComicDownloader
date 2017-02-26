/*
 ----------------------------------------------------------------------------------------------------
 Program Name : JComicDownloader
 Authors  : surveyorK
 Last Modified : 2012/2/12
 ----------------------------------------------------------------------------------------------------
 ChangeLog:
 *  3.05: 1. 新增對www.tuku.cc的支援。
 ----------------------------------------------------------------------------------------------------
 */
package jcomicdownloader.module;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import jcomicdownloader.SetUp;
import jcomicdownloader.encode.NewEncoding;
import jcomicdownloader.enums.Site;
import jcomicdownloader.tools.Common;

public class ParseTUKU extends ParseOnlineComicSite {

    private int radixNumber; // use to figure out the name of pic
    private String jsName;
    protected String indexName;
    protected String indexEncodeName;
    protected String baseURL;

    /**

     @author user
     */
    public ParseTUKU() {
        enumName = "TUKU";
        parserName=this.getClass().getName();
        regexs= new String[]{"(?s).*tuku.cc(?s).*"};
        siteID=Site.formString("TUKU");
        siteName = "Tuku";
        indexName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_tuku_parse_", "html" );
        indexEncodeName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_tuku_encode_parse_", "html" );

        jsName = "index_tuku.js";
        radixNumber = 1591371; // default value, not always be useful!!

        baseURL = "http://www.tuku.cc/";
    }

    public ParseTUKU( String webSite, String titleName ) {
        this();
        this.webSite = webSite;
        this.title = titleName;
    }

    @Override
    public void setParameters() {
        Common.debugPrintln( "開始解析各參數 :" );
        Common.debugPrintln( "開始解析title和wholeTitle :" );
//
//        if ( getWholeTitle() == null || getWholeTitle().equals( "" ) ) {
//            // 因為正常解析不需要用到單集頁面，所以給此兩行放進來
//            Common.downloadFile( webSite, SetUp.getTempDirectory(), indexName, false, "" );
//            Common.newEncodeFile( SetUp.getTempDirectory(), indexName, indexEncodeName );
//
//            String allPageString = Common.getFileString( SetUp.getTempDirectory(), indexEncodeName );
//
//            int beginIndex = allPageString.indexOf( "<h1>" ) + 1;
//            beginIndex = allPageString.indexOf( "<h1>", beginIndex ) + 6;
//            int endIndex = allPageString.indexOf( "</h1>", beginIndex );
//            String tempTitleString = allPageString.substring( beginIndex, endIndex ).replaceAll( "&nbsp;", "" );
//
//            setWholeTitle( getVolumeWithFormatNumber( Common.getStringRemovedIllegalChar(
//                Common.getTraditionalChinese( tempTitleString.trim() ) ) ) );
//        }

        Common.debugPrintln( "作品名稱(title) : " + getTitle() );
        Common.debugPrintln( "章節名稱(wholeTitle) : " + getWholeTitle() );
    }

    @Override
    public String getTitle(){
        if (this.webSite == null){
            return null;
        }
        if (this.title == null || this.title.equals("")) {
            Common.downloadFile(webSite, SetUp.getTempDirectory(), indexName, false, "");
            String allPageString = Common.getFileString(SetUp.getTempDirectory(), indexName);
            int beginIndex = allPageString.indexOf("id=\"titleValue\" value=\"") + 23;
            int endIndex = allPageString.indexOf("\"/>", beginIndex);
            String tempTitleString = allPageString.substring(beginIndex, endIndex).replaceAll("&nbsp;", "");
            this.title= this.wholeTitle = tempTitleString;
        }
        return this.title;
    }

    @Override
    public String getWholeTitle(){
        if (this.webSite == null){
            return null;
        }
        if (this.wholeTitle == null || this.wholeTitle.equals("")) {
            Common.downloadFile(webSite, SetUp.getTempDirectory(), indexName, false, "");
            String allPageString = Common.getFileString(SetUp.getTempDirectory(), indexName);
            int beginIndex = allPageString.indexOf("id=\"titleValue\" value=\"") + 23;
            int endIndex = allPageString.indexOf("\"/>", beginIndex);
            String tempTitleString = allPageString.substring(beginIndex, endIndex).replaceAll("&nbsp;", "");
            this.wholeTitle= this.title = tempTitleString;
        }
        return this.wholeTitle;
    }

    @Override
    public void parseComicURL() { // parse URL and save all URLs in comicURL  //
        // 先取得前面的下載伺服器網址

        if ( !webSite.matches( "(?s).*/" ) ) {
            webSite += "/";
        }

        int endIndex = Common.getIndexOfOrderKeyword( webSite, "/", 6 ) + 1;
        String jsURL = webSite.substring( 0, endIndex ) + "index.js";

        String allPageString = getAllPageString( jsURL );
        Common.debugPrint( "開始解析這一集有幾頁 : " );

        String[] tokens = allPageString.split("[|][|][|]var")[1].split("'[.]split")[0].split("[|]");
//        int ii=0;
//        for (String token:tokens){
//            System.out.println(ii+":" + token);
//            ii++;
//        }
//        String tukucc = tokens[4];
//        String tkpic = tokens[6];
//        String tkdate = tokens[28];
//        String tkvar1 = tokens[32];
//        String tkvar2 = tokens[34];
//        String tkext = tokens[43];

        org.jsoup.nodes.Document dom = org.jsoup.Jsoup.parse(allPageString);
        int totalPage=dom.getElementsByTag("select").get(0).getElementsByTag("option").size();

        Common.debugPrintln( "共 " + totalPage + " 頁" );
        comicURL = new String[totalPage];
 
        int p = 0; // 目前頁數
        for ( int i = 1; i <= totalPage && Run.isAlive; i++ ) {
            comicURL[p++] = String.format("http://%s.%s.com/%s/%s/%s/%03d.jpg", 
            new Object[]{tokens[6],tokens[4],tokens[32],tokens[28],tokens[34],i});
            //Common.debugPrintln( p + " " + comicURL[p - 1] ); // debug
        }
//        System.exit( 0 ); // debug
    }

    @Override
    public String getAllPageString( String urlString ) {
        String indexName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_tuku_", "html" );
//        String indexEncodeName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_tuku_encode_", "html" );

        Common.downloadFile( urlString, SetUp.getTempDirectory(), indexName, false, "" );
//        Common.newEncodeFile( SetUp.getTempDirectory(), indexName, indexEncodeName );

        return Common.getFileString( SetUp.getTempDirectory(), indexName );
    }

    @Override
    public boolean isSingleVolumePage( String urlString ) {
        // ex. http://www.tuku.cc/comic/48/297/?12
        if ( Common.getAmountOfString( urlString, "/" ) > 5 ) {
            return true;
        }
        else {
            return false;
        }
    }

    public String getMainUrlFromSingleVolumeUrl( String volumeURL ) {
        // ex. http://www.tuku.cc/comic/48/297/?12轉為
        //    http://www.tuku.cc/comic/48

        int endIndex = Common.getIndexOfOrderKeyword( volumeURL, "/", 5 );
        String mainPageURL = volumeURL.substring( 0, endIndex );

        Common.debugPrintln( "MAIN_URL: " + mainPageURL );

        return mainPageURL;
    }
    
    @Override
    public String getTitleOnMainPage( String urlString, String allPageString ) {
        int beginIndex = allPageString.indexOf( "<title>" );
        beginIndex = allPageString.indexOf( ">", beginIndex ) + 1;
        int endIndex = allPageString.indexOf( "_", beginIndex );
        String title = allPageString.substring( beginIndex, endIndex ).trim();
        return Common.getStringRemovedIllegalChar( NewEncoding.StoT(title));
    }

    @Override
    public List<List<String>> getVolumeTitleAndUrlOnMainPage( String urlString, String allPageString ) {
        // combine volumeList and urlList into combinationList, return it.

        List<List<String>> combinationList = new ArrayList<List<String>>();
        List<String> urlList = new ArrayList<String>();
        List<String> volumeList = new ArrayList<String>();
        String baseURL = urlString.split("/")[0] +"//"+urlString.split("/")[2];
        org.jsoup.nodes.Document doc= org.jsoup.Jsoup.parse(allPageString);
        org.jsoup.nodes.Element div = doc.getElementById("charpter_content");
        org.jsoup.select.Elements li = div.getElementsByTag("li");
        for (org.jsoup.nodes.Element e: li){
            String volumeTitle =  e.getElementsByClass("t").get(0).text();
            String volumeUrl =  e.getElementsByTag("a").get(0).attr("href");
            volumeList.add(volumeTitle);
            urlList.add(baseURL+volumeUrl);            
        }
                
        totalVolume =volumeList.size();
        Common.debugPrintln( "共有" + totalVolume + "集" );

        combinationList.add( volumeList );
        combinationList.add( urlList );

        return combinationList;
    }
}
