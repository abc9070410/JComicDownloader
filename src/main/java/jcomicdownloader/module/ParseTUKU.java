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

        if ( getWholeTitle() == null || getWholeTitle().equals( "" ) ) {
            // 因為正常解析不需要用到單集頁面，所以給此兩行放進來
            Common.downloadFile( webSite, SetUp.getTempDirectory(), indexName, false, "" );
            Common.newEncodeFile( SetUp.getTempDirectory(), indexName, indexEncodeName );

            String allPageString = Common.getFileString( SetUp.getTempDirectory(), indexEncodeName );

            int beginIndex = allPageString.indexOf( "</a>->" ) + 1;
            beginIndex = allPageString.indexOf( "</a>->", beginIndex ) + 6;
            int endIndex = allPageString.indexOf( "->", beginIndex );
            String tempTitleString = allPageString.substring( beginIndex, endIndex ).replaceAll( "&nbsp;", "" );

            setWholeTitle( getVolumeWithFormatNumber( Common.getStringRemovedIllegalChar(
                Common.getTraditionalChinese( tempTitleString.trim() ) ) ) );
        }

        Common.debugPrintln( "作品名稱(title) : " + getTitle() );
        Common.debugPrintln( "章節名稱(wholeTitle) : " + getWholeTitle() );
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

        String[] tokens = allPageString.split( "=|;" );

        String picMidURL1 = ""; // 圖片網址中間的部份 ex. 100
        String picMidURL2 = ""; // 圖片網址中間的部份 ex. 全职猎人/第297话 
        int zeroAmount = 0; // 正規化檔名中補零的數量

        for ( int i = 0; i < tokens.length; i++ ) {
            if ( tokens[i].matches( "(?s).*var\\s*total\\s*" ) ) {
                totalPage = Integer.parseInt( tokens[i + 1].trim() );
            }
            else if ( tokens[i].matches( "(?s).*var\\s*volpic\\s*" ) ) {
                picMidURL2 = tokens[i + 1].trim().replaceAll( "'", "" ) + "/";
            }
            else if ( tokens[i].matches( "(?s).*var\\s*tpf\\s*" ) ) {
                zeroAmount = Integer.parseInt( tokens[i + 1].trim() );
            }
            else if ( tokens[i].matches( "(?s).*var\\s*tpf2\\s*" ) ) {
                picMidURL1 = tokens[i + 1].trim() + "00/";
            }
        }

        // 圖片基本位址
        String baseURL1 = "http://pic.tuku.cc/";
        String baseURL2 = "http://pic1.tuku.cc/";
        String baseURL3 = "http://pic2.tuku.cc/";
        String baseURL4 = "http://pic3.tuku.cc/";

        Common.debugPrintln( "共 " + totalPage + " 頁" );
        comicURL = new String[totalPage];

        NumberFormat formatter = new DecimalFormat( Common.getZero( zeroAmount + 1 ) );
        String fileName = "";

        int p = 0; // 目前頁數
        for ( int i = 1; i <= totalPage && Run.isAlive; i++ ) {
            comicURL[p++] = baseURL2 + picMidURL1
                + Common.getFixedChineseURL( picMidURL2 )
                + formatter.format( i ) + "." + "jpg"; // 存入每一頁的網頁網址
            //Common.debugPrintln( p + " " + comicURL[p - 1] ); // debug

        }
        //System.exit( 0 ); // debug
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
