/*
 ----------------------------------------------------------------------------------------------------
 Program Name : JComicDownloader
 Authors  : surveyorK
 Last Modified : 2012/11/15
 ----------------------------------------------------------------------------------------------------
 ChangeLog:
 5.07: 修復veryim集數解析不全的問題。
5.01: 1. 修復veryim無法下載的問題。
 *  4.0: 1. 新增對veryim的支援。
 ----------------------------------------------------------------------------------------------------
 */
package jcomicdownloader.module;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import jcomicdownloader.tools.*;
import jcomicdownloader.enums.*;
import java.util.*;
import jcomicdownloader.SetUp;
import jcomicdownloader.encode.Zhcode;

public class ParseVeryim extends ParseOnlineComicSite {

    private int radixNumber; // use to figure out the name of pic
    private String jsName;
    protected String indexName;
    protected String indexEncodeName;
    protected String baseURL;

    /**

     @author user
     */
    public ParseVeryim() {
        regexs= new String[]{"(?s).*veryim.com(?s).*", "(?s).*veryim.net(?s).*" };
        enumName = "VERYIM";
	parserName=this.getClass().getName();
        downloadBefore=true;
        siteID=Site.formString("VERYIM");
        siteName = "Veryim";
        indexName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_veryim_parse_", "html" );
        indexEncodeName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_veryim_encode_parse_", "html" );

        jsName = "index_veryim.js";
        radixNumber = 1591371; // default value, not always be useful!!

        baseURL = "http://www.veryim.net";
    }

    public ParseVeryim( String webSite, String titleName ) {
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
    
    private int getCharIndex(char c)
    {
        int index = -1;
        
        if (c >= '0' && c <= '9')
        {
            index = Integer.parseInt("" + c);
        }
        else if (c >= 'a' && c <= 'z')
        {
            index = 10 + (c - 'a');
        }
        else if (c >= 'A' && c <= 'Z')
        {
            index = 10 + 26 + (c - 'A');
        }
        
        return index;
    }
    
    private int getEncodeIndex(char c, char nextC)
    {
        int cIndex = getCharIndex(c);
        int nextCIndex = getCharIndex(nextC);
        
        if (cIndex >= 0 && nextCIndex >= 0)
        {
            return (10 + 26 + 26) * cIndex + nextCIndex;
        }
        
        return cIndex;
    }
    
    private String[] getComicUrls(String allPageString)
    {
        // return p}('8 i=\'h\';8 g=\'["2:\\/\\/1.5.0:4\\/\\/y\\/7\\/6\\/k.3","2:\\/\\/1.5.0:4\\/\\/y\\/7\\/6\\/m.3","2:\\/\\/1.5.0:4\\/\\/y\\/7\\/6\\/l.3","2:\\/\\/1.5.0:4\\/\\/y\\/7\\/6\\/n.3","2:\\/\\/1.5.0:4\\/\\/y\\/7\\/6\\/f.3","2:\\/\\/1.5.0:4\\/\\/y\\/7\\/6\\/a.3","2:\\/\\/1.5.0:4\\/\\/y\\/7\\/6\\/9.3","2:\\/\\/1.5.0:4\\/\\/y\\/7\\/6\\/c.3","2:\\/\\/1.5.0:4\\/\\/y\\/7\\/6\\/e.3","2:\\/\\/1.5.0:4\\/\\/y\\/7\\/6\\/d.3","2:\\/\\/1.5.0:4\\/\\/y\\/7\\/6\\/b.3","2:\\/\\/1.5.0:4\\/\\/y\\/7\\/6\\/j.3","2:\\/\\/1.5.0:4\\/\\/y\\/7\\/6\\/v.3","2:\\/\\/1.5.0:4\\/\\/y\\/7\\/6\\/z.3","2:\\/\\/1.5.0:4\\/\\/y\\/7\\/6\\/x.3","2:\\/\\/1.5.0:4\\/\\/y\\/7\\/6\\/B.3","2:\\/\\/1.5.0:4\\/\\/y\\/7\\/6\\/o.3","2:\\/\\/1.5.0:4\\/\\/y\\/7\\/6\\/C.3","2:\\/\\/1.5.0:4\\/\\/y\\/7\\/6\\/D.3","2:\\/\\/1.5.0:4\\/\\/y\\/7\\/6\\/A.3","2:\\/\\/1.5.0:4\\/\\/y\\/7\\/6\\/w.3","2:\\/\\/1.5.0:4\\/\\/y\\/7\\/6\\/q.3","2:\\/\\/1.5.0:4\\/\\/y\\/7\\/6\\/p.3","2:\\/\\/1.5.0:4\\/\\/y\\/7\\/6\\/r.3","2:\\/\\/1.5.0:4\\/\\/y\\/7\\/6\\/s.3","2:\\/\\/1.5.0:4\\/\\/y\\/7\\/6\\/u.3","2:\\/\\/1.5.0:4\\/\\/y\\/7\\/6\\/t.3"]\';',40,40,'net|imgn1|http|jpg|8090|veryim|ch_33|yxobjn4sdx|var|0007|0006|0011|0008|0010|0009|0005|imageData|27|total|0012|0001|0003|0002|0004|0017|0023|0022|0024|0025|0027|0026|0013|0021|0015||0014|0020|0016|0018|0019'.split('|')))
        
        int beginIndex = 0;
        int endIndex = 0;
        
        beginIndex = allPageString.indexOf( "return p" );
        beginIndex = allPageString.indexOf( "[\"", beginIndex ) + 2;
        endIndex = allPageString.indexOf( "\"]", beginIndex );
        
        String[] encodeUrls = allPageString.substring(beginIndex, endIndex).split("\",\"");
        String[] decodeUrls = new String[encodeUrls.length];
        
        beginIndex = allPageString.indexOf( ",'", endIndex) + 2;
        endIndex = allPageString.indexOf( "'.split", beginIndex );
        
        String[] tokens = allPageString.substring(beginIndex, endIndex).split("\\|");
        
        for (int i = 0; i < encodeUrls.length; i++)
        {
            String encode = encodeUrls[i].replaceAll("\\\\", "");
            String decode = "";

            for (int j = 0; j < encode.length(); j++)
            {
                char c = encode.charAt(j);
                char nextC = '$';
                
                if (j + 1 < encode.length())
                {
                    nextC = encode.charAt(j + 1);
                }
                
                int index = getEncodeIndex(c, nextC);
                
                //Common.debugPrintln(c + "_" + nextC + "_" + index);
                
                if (index >= 0)
                {
                    if (index > tokens.length || tokens[index].matches(""))
                    {
                        decode += c;
                    }
                    else
                    {
                        decode += tokens[index];
                    }
                }
                else
                {
                    decode += c;
                }
               
                if (index >= (10 + 26 + 26))
                {
                    j++;
                }
            }
            
            Common.debugPrintln(i + " encode:" + encode);
            Common.debugPrintln(i + " decode:" + decode);
            
            decodeUrls[i] = decode;
        }
        
        //System.exit(0);
        
        return decodeUrls;
    }

    @Override
    public void parseComicURL() { // parse URL and save all URLs in comicURL  //
        // 先取得前面的下載伺服器網址

        int beginIndex = 0;
        int endIndex = 0;
        String tempString = "";

        Common.downloadFile( webSite, SetUp.getTempDirectory(), indexName, false, "" );
        String allPageString = Common.getFileString( SetUp.getTempDirectory(), indexName );


        Common.debugPrint( "開始解析這一集有幾頁 : " );

        
        comicURL = getComicUrls(allPageString);
        
        totalPage = comicURL.length;
        
        for (int i = 0; i < totalPage; i++)
        {
            singlePageDownloadUsingSimple( getTitle(), getWholeTitle(),
                comicURL[i], totalPage, i + 1, webSite );
        }
        
        //System.exit(0);
        
        /*
        
        // 解析有幾頁
        beginIndex = allPageString.indexOf( "var totalPage" );
        beginIndex = allPageString.indexOf( "=", beginIndex ) + 1;
        endIndex = allPageString.indexOf( ";", beginIndex );
        tempString = allPageString.substring( beginIndex, endIndex ).trim();
        totalPage = Integer.parseInt( tempString );
        Common.debugPrintln( "共 " + totalPage + " 頁" );
        //comicURL = new String[totalPage];

        
        Common.debugPrintln( "開始解析中間部份的位址" );
        
        // 解析開頭大寫字母
        beginIndex = allPageString.indexOf( "this.letter" );
        beginIndex = allPageString.indexOf( "\"", beginIndex ) + 1;
        endIndex = allPageString.indexOf( "\"", beginIndex );
        String letter = allPageString.substring( beginIndex, endIndex );
        
        // 解析漫畫資料夾名稱
        beginIndex = allPageString.indexOf( "this.comicDir" );
        beginIndex = allPageString.indexOf( "\"", beginIndex ) + 1;
        endIndex = allPageString.indexOf( "\"", beginIndex );
        String comicDir = allPageString.substring( beginIndex, endIndex );
        
        // 解析章節資料夾名稱
        beginIndex = allPageString.indexOf( "this.chapterDir" );
        beginIndex = allPageString.indexOf( "\"", beginIndex ) + 1;
        endIndex = allPageString.indexOf( "\"", beginIndex );
        String chapterDir = allPageString.substring( beginIndex, endIndex );
        
        // 解析圖片副檔名
        beginIndex = allPageString.indexOf( "this.ext" );
        beginIndex = allPageString.indexOf( "\"", beginIndex ) + 1;
        endIndex = allPageString.indexOf( "\"", beginIndex );
        String ext = allPageString.substring( beginIndex, endIndex );
        
        // 解析圖片伺服器位址
        beginIndex = allPageString.indexOf( "this.imgServer" );
        beginIndex = allPageString.indexOf( "\"", beginIndex ) + 1;
        endIndex = allPageString.indexOf( "\"", beginIndex );
        String imgServer = allPageString.substring( beginIndex, endIndex );

        // 解析檔名格式 0:001.jpg  1:001000.jpg
        beginIndex = allPageString.indexOf( "this.fileNameType" );
        beginIndex = allPageString.indexOf( "\"", beginIndex ) + 1;
        endIndex = allPageString.indexOf( "\"", beginIndex );
        int fileNameType = Integer.parseInt( 
            allPageString.substring( beginIndex, endIndex ) );

        
        //String cookie = Common.getCookieString( webSite );
        //System.exit( 0 );


        NumberFormat formatter = new DecimalFormat( "000" );
        String picName = ""; 

        int p = 0; // 目前頁數
        for ( int i = 0; i < totalPage && Run.isAlive; i++ ) {
            if ( fileNameType == 0 ) {
                picName = formatter.format( i + 1 ) + ext;
            }
            else {
                picName = formatter.format( i + 1 ) + formatter.format( i ) + ext;
            }
            
            comicURL[i] = imgServer + "/" + letter + "/" + 
                comicDir + "/" + chapterDir + "/" + picName;

            // 使用最簡下載協定，加入refer始可下載
            singlePageDownloadUsingSimple( getTitle(), getWholeTitle(),
                comicURL[i], totalPage, i + 1, webSite );
           
            //Common.debugPrintln( ( ++ p ) + " " + comicURL[p - 1] ); // debug
        }
        //System.exit( 0 ); // debug
        */
    }

    @Override
    public String getAllPageString( String urlString ) {
        String indexName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_veryim_", "html" );

        Common.downloadFile( urlString, SetUp.getTempDirectory(), indexName, false, "" );

        return Common.getFileString( SetUp.getTempDirectory(), indexName );
    }

    @Override
    public boolean isSingleVolumePage( String urlString ) {
        // ex. http://comic.veryim.com/manhua/guaiwuwangnv/ch_81.html?p=3
        int endIndex = Common.getIndexOfOrderKeyword( urlString, "/", 5 );
        if ( ( endIndex + 2 ) < urlString.length() ) {
            return true;
        }
        else {
            return false;
        }
    }

    public String getMainUrlFromSingleVolumeUrl( String volumeURL ) {
        // ex. http://comic.veryim.com/manhua/guaiwuwangnv/ch_81.html轉為
        //    http://comic.veryim.com/manhua/guaiwuwangnv/

        int endIndex = Common.getIndexOfOrderKeyword( volumeURL, "/", 5 );
        String mainPageURL = volumeURL.substring( 0, endIndex );

        Common.debugPrintln( "MAIN_URL: " + mainPageURL );

        return mainPageURL;
    }

    @Override
    public String getTitleOnMainPage( String urlString, String allPageString ) {
        int beginIndex = allPageString.indexOf( "<title>" );
        beginIndex = allPageString.indexOf( ">", beginIndex ) + 1;
        int endIndex = allPageString.indexOf( " - ", beginIndex );
        String title = allPageString.substring( beginIndex, endIndex ).trim();

        return Common.getStringRemovedIllegalChar( Common.getTraditionalChinese( title ) );
    }

    @Override
    public List<List<String>> getVolumeTitleAndUrlOnMainPage( String urlString, String allPageString ) {
        // combine volumeList and urlList into combinationList, return it.

        List<List<String>> combinationList = new ArrayList<List<String>>();
        List<String> urlList = new ArrayList<String>();
        List<String> volumeList = new ArrayList<String>();

        int beginIndex = allPageString.indexOf( "class=\"chapters\"" );
        int endIndex = allPageString.indexOf( "class=\"related\"", beginIndex );
        
        /*
        if ( allPageString.indexOf( "class=\"ex\"" ) > 0 ) {
            endIndex = allPageString.indexOf( "class=\"ex\"", beginIndex );
            endIndex = allPageString.indexOf( "</ul>", endIndex );
        }
        */

        String tempString = allPageString.substring( beginIndex, endIndex );

        int volumeCount = tempString.split( " href=" ).length - 1;
        totalVolume = volumeCount;
        Common.debugPrintln( "共有" + totalVolume + "集" );

        String volumeTitle = "";
        String volumeUrl = "";
        beginIndex = endIndex = 0;
        for ( int i = 0; i < volumeCount; i++ ) {
            // 取得單集位址
            beginIndex = tempString.indexOf( " href=", beginIndex );
            beginIndex = tempString.indexOf( "\"", beginIndex ) + 1;
            endIndex = tempString.indexOf( "\"", beginIndex );
            volumeUrl = baseURL + tempString.substring( beginIndex, endIndex );
            urlList.add( volumeUrl );
            // 取得單集名稱
            beginIndex = tempString.indexOf( ">", beginIndex ) + 1;
            endIndex = tempString.indexOf( "</a>", beginIndex );
            volumeTitle = tempString.substring( beginIndex, endIndex );
            volumeTitle = volumeTitle.replaceAll( "<.*>", "" );
            volumeList.add( getVolumeWithFormatNumber( Common.getStringRemovedIllegalChar(
                Common.getTraditionalChinese( volumeTitle.trim() ) ) ) );
                
            Common.debugPrintln("title:" + volumeTitle + " : " + volumeUrl);
        }
        
        //System.exit(0);

        combinationList.add( volumeList );
        combinationList.add( urlList );

        return combinationList;
    }
}
