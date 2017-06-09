/*
----------------------------------------------------------------------------------------------------
Program Name : JComicDownloader
Authors  : surveyorK
Last Modified : 2011/12/11
----------------------------------------------------------------------------------------------------
ChangeLog:
5.19: 修復baidu無法下載原始圖片的問題。
      5.02: 修復baidu下載錯誤的問題。
     5.01: 修復baidu因改版而解析錯誤的問題。
*   4.16: 1. 修復baidu因網站改版而下載不完全的問題。
 *  2.06 : 1. 新增對baidu的支援。
----------------------------------------------------------------------------------------------------
 */
package jcomicdownloader.module;

import java.io.File;
import jcomicdownloader.tools.*;
import jcomicdownloader.enums.*;
import java.util.*;
import javax.swing.JOptionPane;
import jcomicdownloader.ComicDownGUI;
import jcomicdownloader.SetUp;
//import jcomicdownloader.encode.Zhcode;

public class ParseBAIDU extends ParseOnlineComicSite {

 //   private int radixNumber; // use to figure out the name of pic
    private String jsName;
    protected String indexName;
    protected String indexEncodeName;
    protected String baseURL;
    protected String firstServerURL = "http://comic.jmydm.net/"; // 電信一
    protected String secondServerURL = "http://zj.jmydm.net/"; // 電信二
    protected String thirdServerURL = "http://wt.jmydm.net:2012/"; // 網通

    /**
     *
     * @author user
     */
    public ParseBAIDU() {
        enumName = "BAIDU";
	    parserName=this.getClass().getName();
        regexs= new String[]{"(?s).*baidu.com(?s).*"};
        downloadBefore=true;
	    siteID=Site.formString("BAIDU");
        siteName = "Baidu";
        indexName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_baidu_parse_", "html" );
        indexEncodeName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_baidu_encode_parse_", "html" );

        jsName = "index_baidu.js";
  //      radixNumber = 1844271; // default value, not always be useful!!

        baseURL = "http://tieba.baidu.com";
    }

    public ParseBAIDU( String webSite, String titleName ) {
        this();
        this.webSite = webSite;
        this.title = titleName;
    }

    @Override
    public void setParameters() {
        Common.debugPrintln( "開始解析各參數 :" );

        Common.debugPrintln( "開始解析title和wholeTitle :" );

        if ( getWholeTitle() == null || getWholeTitle().equals( "" ) ) {
            Common.errorReport( "不可能有這種情形" );
        }

        Common.debugPrintln( "作品名稱(title) : " + getTitle() );
        Common.debugPrintln( "章節名稱(wholeTitle) : " + getWholeTitle() );
    }

    @Override
    public void parseComicURL() { 
        // parse URL and save all URLs in comicURL  //
        // 先取得前面的下載伺服器網址

        String allPageString;
        if ( existsTempFile() ) {
            allPageString = Common.getFileString( SetUp.getTempDirectory(), indexName );
        } else {
            allPageString = getAllPageString( webSite );
        }

        Common.debugPrint( "開始解析這一貼子有幾個分頁 : " );

        int beginIndex = allPageString.indexOf( "<span class=\"tP\">" );
        int endIndex = allPageString.indexOf( "</li>", beginIndex );

        String tempString = "";
        String basePageURL = "";
        int pageAmount = 0;
        if ( beginIndex > 0 ) { // 有很多頁
            tempString = allPageString.substring( beginIndex, endIndex );

            beginIndex = tempString.lastIndexOf( "href=\"" ) + 6;
            endIndex = tempString.indexOf( "\"", beginIndex );
            System.out.println( beginIndex + " " + endIndex + " " + tempString.substring( beginIndex, endIndex ) );
            basePageURL = baseURL + tempString.substring( beginIndex, endIndex ).split( "=" )[0] + "=";
            pageAmount = Integer.parseInt( tempString.substring( beginIndex, endIndex ).split( "=" )[1] );
        } else {
            pageAmount = 1; // 只有一頁
        }
        Common.debugPrintln( "此貼共有" + pageAmount + "頁" );

        int pageCount = 0;
        int choice = -1;
        String tempComicURL = "";
        for ( int i = 0 ; i < pageAmount ; i++ ) {
            if ( i > 0 ) {
                allPageString = getAllPageString( basePageURL + (i + 1) );
            }

            int picCountOnNowPage = allPageString.split( "\"BDE_Image\"" ).length - 1;
            
            Common.debugPrintln( "此頁應該有" + picCountOnNowPage + "張圖");

            // 當此頁面貼圖數少於5張，且之前沒有選擇解析為止，且不是最後一頁，才跳出詢問視窗
            if ( picCountOnNowPage < 5 && choice != 2 && i != pageAmount - 1 ) {
                Object[] options = { "結束解析，完成此貼下載", "繼續解析下一頁", "將全部頁數解析為止" };
                choice = JOptionPane.showOptionDialog( ComicDownGUI.mainFrame,
                        "目前解析到第" + (i + 1) + "頁沒有貼圖，請問應該如何處理？",
                        "下載詢問視窗",
                        JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                        null, options, options[0] );
            }

            // 如果頁面貼圖數少於2張且選擇結束，或者頁面貼圖數少於2張且為最後一頁，就結束迴圈
            if ( picCountOnNowPage < 2 ) {
                if ( choice == 0 || i == pageAmount - 1 ) {
                    break;
                } else {
                    continue;
                }
            }
            
            String[] temps = allPageString.split("\"BDE_Image\"");
            String[] comicURL2 = new String[temps.length-1];
            //String basePicURL = "http://imgsrc.baidu.com/forum/pic/item";
            //String basePicURL = "https://imgsa.baidu.com/forum/pic/item/";
            int picCount = 0;
            
            Common.debugPrintln("預估的圖片數量 : " + temps.length);
            
            for (int j = 1; j < temps.length; j ++)
            {
//                if (temps[j].length() > 163)
//                {
//                    //Common.debugPrintln(i + " 原始碼: " + temps[i].substring(0, 163));
//                }
                //beginIndex = temps[j].indexOf("http://imgsrc.baidu.com/");
                //https://imgsa.baidu.com/forum/w%3D580/sign=02ea7c453fd12f2ece05ae687fc3d5ff/cc640409c93d70cf42c1b79ff1dcd100bba12bc7.jpg
                beginIndex = temps[j].indexOf("https://imgsa.baidu.com/");
                endIndex = temps[j].indexOf("\"", beginIndex);
                beginIndex = temps[j].lastIndexOf("/", endIndex);
                Common.debugPrintln("BeginIndex:"+beginIndex);
                if (beginIndex > endIndex || beginIndex < 0)
                {
                    continue;
                }
                //comicURL2[picCount] = basePicURL + temps[j].substring(beginIndex, endIndex);
                comicURL2[picCount] = temps[j].substring(beginIndex, endIndex);
                Common.debugPrintln("解析到的第" + j + "張圖:" + comicURL2[picCount]);
                
                picCount++;
            }
            
            Common.debugPrintln("實際的圖片數量 : " + picCount);
            String[] comicURL = new String[picCount];
            
            for (int j = 0; j < picCount; j++)
            {
                comicURL[j] = comicURL2[j];
            }

            // get the orginial image
            //String originalBaseURL = "https://imgsa.baidu.com/forum/pic/item/";
            String originalBaseURL = "https://imgsa.baidu.com/forum/pic/item/";
            for ( int k = 0; k < comicURL.length; k ++ )
            {
                String fileName = comicURL[k].split( "/" )[comicURL[k].split( "/" ).length - 1];
                comicURL[k] = originalBaseURL + fileName;
                //Common.debugPrintln( k + ": " + comicURL[k] );
            }
            
            //System.exit( 0 );
            
            Common.debugPrintln( "此頁面解析得" + comicURL.length + "張圖" );
            int tempTotalPage = comicURL.length + pageCount;
            for ( int j = 0 ; j < comicURL.length && Run.isAlive; j++ ) {
                Common.debugPrint( " " + pageCount + " " );
                singlePageDownload( getTitle(), getWholeTitle(), comicURL[j], tempTotalPage, pageCount + 1, 0 );

                pageCount++;
            }


            Common.debugPrintln( "第" + (i + 1) + "頁解析完畢" );
            Common.debugPrintln( "目前共下載" + pageCount + "頁" );
            tempComicURL = ""; // 歸零，讓下一頁使用

            System.out.println( "TEMP_URL: " + tempComicURL ); // debug
        }

        comicURL = tempComicURL.split( "####" );
        totalPage = comicURL.length;//allPageString.split( "\"BDE_Image\"" ).length - 1; 
        Common.debugPrintln( "共 " + totalPage + " 頁" );

        //System.exit( 0 ); // debug
    }

    public void showParameters() { // for debug
        Common.debugPrintln( "----------" );
        Common.debugPrintln( "totalPage = " + totalPage );
        Common.debugPrintln( "webSite = " + webSite );
        Common.debugPrintln( "----------" );
    }

    @Override
    public String getAllPageString( String urlString ) {
        String indexName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_baidu_", "html" );
//        String indexEncodeName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_baidu_encode_", "html" );

        Common.downloadFile( urlString, SetUp.getTempDirectory(), indexName, false, "" );
        //Common.newEncodeFile( SetUp.getTempDirectory(), indexName, indexEncodeName, Zhcode.GBK );

        return Common.getFileString( SetUp.getTempDirectory(), indexName );
    }

    private boolean existsTempFile() {
        System.out.println( SetUp.getTempDirectory() + indexName );
        return new File( SetUp.getTempDirectory() + indexName ).exists();
    }

    @Override
    public boolean isSingleVolumePage( String urlString ) {
        // baidu貼子都判斷為主頁面
        return false;
    }

    public String getMainUrlFromSingleVolumeUrl( String volumeURL ) {
        return volumeURL;
    }

    @Override
    public String getTitleOnSingleVolumePage( String urlString ) {
        String mainUrlString = getMainUrlFromSingleVolumeUrl( urlString );

        return getTitleOnMainPage( mainUrlString, getAllPageString( mainUrlString ) );
    }

    @Override
    public String getTitleOnMainPage( String urlString, String allPageString ) {
        int beginIndex = 0;
        int endIndex = 0;

        boolean titleFound = false;
        String title = "";
        if ( urlString.matches( "(?s).*\\d{7,}(?s).*" ) ) {
            beginIndex = allPageString.indexOf( "<h1 " ) + 1;
            beginIndex = allPageString.indexOf( ">", beginIndex ) + 1;
            endIndex = allPageString.indexOf( "<", beginIndex );
            title = allPageString.substring( beginIndex, endIndex ).trim();
            
            if (beginIndex != endIndex) {
                titleFound = true;
            }
        }
            
        if (!titleFound) {
            beginIndex = allPageString.indexOf( "<title>" ) + 7;
            endIndex = allPageString.indexOf( "_", beginIndex );
            title = allPageString.substring( beginIndex, endIndex ).trim();
        }
        if ((getWholeTitle() == null) ||( getWholeTitle().equals(""))) {
            setWholeTitle(title);
        }
        return Common.getStringRemovedIllegalChar( Common.getTraditionalChinese( "[百度]" + title ) );
    }

    @Override
    public List<List<String>> getVolumeTitleAndUrlOnMainPage( String urlString, String allPageString ) {
        // combine volumeList and urlList into combinationList, return it.

        List<List<String>> combinationList = new ArrayList<List<String>>();
        List<String> urlList = new ArrayList<String>();
        List<String> volumeList = new ArrayList<String>();

        int beginIndex = 0;
        int endIndex = 0;

        if ( urlString.matches( "(?s).*\\d{7,}(?s).*" ) ) { // 單一貼子
            totalVolume = 1;

            beginIndex = allPageString.indexOf( "<title>" ) + 7;
            endIndex = allPageString.indexOf( "_", beginIndex );
            String volumeTitle = allPageString.substring( beginIndex, endIndex ).trim();

            urlList.add( urlString );

            volumeList.add( Common.getStringRemovedIllegalChar(
                    Common.getTraditionalChinese( volumeTitle ) ) );
        } else { // 貼子列表
            String frontKeyword = "";
            if ( allPageString.indexOf( "class=\"thread_title\"" ) > 0 ) {
                frontKeyword = "class=\"thread_title\"";
            } else {
                frontKeyword = "class=\"th_lz\"";
            }

            totalVolume = allPageString.split( frontKeyword ).length - 1;
            beginIndex = 0;
            String tempURL = "";
            String volumeTitle = "";
            for ( int i = 0 ; i < totalVolume ; i++ ) {
                beginIndex = allPageString.indexOf( frontKeyword, beginIndex );
                beginIndex = allPageString.indexOf( "href=\"", beginIndex ) + 6;
                endIndex = allPageString.indexOf( "\"", beginIndex );
                tempURL = allPageString.substring( beginIndex, endIndex );

                if ( Common.isLegalURL( tempURL ) ) {
                    urlList.add( tempURL );
                } else {
                    urlList.add( baseURL + tempURL );
                }

                beginIndex = allPageString.indexOf( ">", beginIndex ) + 1;
                endIndex = allPageString.indexOf( "<", beginIndex );
                volumeTitle = allPageString.substring( beginIndex, endIndex );

                volumeList.add( Common.getStringRemovedIllegalChar(
                        Common.getTraditionalChinese( volumeTitle ) ) );
            }
        }


        Common.debugPrintln( "共有" + totalVolume + "集" );

        combinationList.add( volumeList );
        combinationList.add( urlList );

        return combinationList;
    }

    @Override
    public void outputVolumeAndUrlList( List<String> volumeList, List<String> urlList ) {
        Common.outputFile( volumeList, SetUp.getTempDirectory(), Common.tempVolumeFileName );
        Common.outputFile( urlList, SetUp.getTempDirectory(), Common.tempUrlFileName );
    }

    @Override
    public String[] getTempFileNames() {
        return new String[] { indexName, indexEncodeName, jsName };
    }
}
