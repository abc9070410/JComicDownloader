/*
 ----------------------------------------------------------------------------------------------------
 Program Name : JComicDownloader
 Authors  : surveyorK
 Last Modified : 2012/8/8
 ----------------------------------------------------------------------------------------------------
 ChangeLog:
 4.16: 1. 修復Google圖片無法下載的問題。
 4.10: 1. 修復在Substance介面下若跳出選擇檔案下載方式視窗必崩潰的問題。
 3.0:  1. 修復非中文版的google圖片搜尋無法下載的bug。
 *  2.01: 1. 修正Google圖片搜尋中部份非英文關鍵字沒有正確解析為資料夾名稱的bug。
 *  1.19: 1. 修正後已支援『顯示更多結果』後面的圖。
 *       2. 修改下載機制，遇到非正常連線直接放棄，加快速度。
 *  1.18: 1. 新增新增對google圖片搜尋的支援。
 ----------------------------------------------------------------------------------------------------
 */
package jcomicdownloader.module;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;
import jcomicdownloader.tools.*;
import jcomicdownloader.enums.*;
import java.util.*;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import jcomicdownloader.ComicDownGUI;
import jcomicdownloader.SetUp;
import jcomicdownloader.frame.OptionFrame;

enum GoogleEnum {

    PIC, FILE
};

public class ParseGooglePic extends ParseOnlineComicSite {

    private int radixNumber; // use to figure out the name of pic
    private String jsName;
    protected String indexName;
    protected String indexEncodeName;
    protected String baseURL;
    public GoogleEnum googleEnum;
    boolean existSameNameFile = false;
    int choice = -1; // 當資料夾內已有同檔名檔案時應該做何處理

    /**

     @author user
     */
    public ParseGooglePic() {
        siteID = Site.GOOGLE_PIC;
        siteName = "GooglePic";
        indexName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_google_pic_parse_", "html" );
        indexEncodeName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_google_pic_encode_parse_", "html" );

        jsName = "index_google_pic.js";
        radixNumber = 185223571; // default value, not always be useful!!

        baseURL = "";
    }

    public ParseGooglePic( String webSite, String titleName ) {
        this();
        this.webSite = webSite;
        this.title = titleName;
    }

    @Override
    public void setParameters() {
        Common.debugPrintln( "開始解析各參數 :" );

        Common.debugPrintln( "開始解析title和wholeTitle :" );

        Common.debugPrintln( "作品名稱(title) : " + getTitle() );
        Common.debugPrintln( "章節名稱(wholeTitle) : " + getWholeTitle() );
    }

    @Override
    public void parseComicURL() { // parse URL and save all URLs in comicURL  //
        // 先取得前面的下載伺服器網址
        String allPageString = "";
        int beginIndex = 0, endIndex = 0;
        int lastPagePicCount = 0;
        int onePageAmount = 0; // 一個頁面的檔案總數

        allPageString = getNewAllPageString( webSite );

        if ( allPageString.split( "/imghp" ).length > 2 ) {
            googleEnum = GoogleEnum.PIC;
            Common.debugPrintln( "此為圖片搜尋頁面" );
        }
        else {
            googleEnum = GoogleEnum.FILE;
            Common.debugPrintln( "此為一般搜尋頁面" );
        }

        if ( googleEnum == GoogleEnum.PIC ) {
            onePageAmount = 20;

            // 取得基本版位址的前面
            baseURL = getBaseUrlString( webSite );

            // google圖片搜尋只有到980，之後就搜不到了。
            allPageString = getNewAllPageString( baseURL + "&start=980&sa=N" );

            // 最後一頁的圖片張數
            lastPagePicCount = allPageString.split( "imgurl=" ).length - 1;

            beginIndex = allPageString.indexOf( "style=\"float:right\"" );
            beginIndex = allPageString.indexOf( "<div>", beginIndex ) + 5;
            endIndex = allPageString.indexOf( "</div>", beginIndex );

        }
        else {
            onePageAmount = 10;

            String baseGoogleURL = "http://www.google.com";

            System.out.println( allPageString.length() );

            beginIndex = allPageString.lastIndexOf( "text-decoration:underline" );
            beginIndex = allPageString.lastIndexOf( "href=\"", beginIndex ) + 6;
            endIndex = allPageString.indexOf( "\"", beginIndex );

            String tempURL = baseGoogleURL + allPageString.substring( beginIndex, endIndex );
            tempURL = tempURL.replaceAll( "amp;", "" );
            endIndex = tempURL.lastIndexOf( "&start=" );
            baseURL = tempURL.substring( 0, endIndex );

            Common.debugPrintln( "一般搜尋最後頁面網址：" + tempURL );

            allPageString = getNewAllPageString( baseURL + "&start=980&sa=N" );


            beginIndex = allPageString.indexOf( "id=resultStats" );
            beginIndex = allPageString.indexOf( ">", beginIndex ) + 1;
            endIndex = allPageString.indexOf( "<", beginIndex );

        }
        
        

        Common.debugPrint( "開始解析這一集有幾頁 : " );
        String[] pageTokens = allPageString.substring( beginIndex, endIndex ).split( "\\s" );
        
        int lastPageNumber = 1000;
        
        /*
        for ( int i = 1; i < pageTokens.length; i++ ) {
            System.out.println( i + " ->" + pageTokens[i] );
            if ( pageTokens[i].matches( "\\d+" ) ) {
                // 因為最後一頁不全，所以減一
                // 取較小的，防止判斷錯誤
                if ( ( Integer.parseInt( pageTokens[i] ) - 1 ) < lastPageNumber ) {
                    lastPageNumber = Integer.parseInt( pageTokens[i] ) - 1;
                }
            }
        }
        */
        
        endIndex = allPageString.indexOf( "</b></td><td" );
        if ( endIndex > 0 ) {
            beginIndex = allPageString.lastIndexOf( ">", endIndex ) + 1;
            lastPageNumber = Integer.parseInt( allPageString.substring( beginIndex, endIndex ) );
        }
        else {
            lastPageNumber = 1;
        }
        System.out.println( lastPageNumber );
        totalPage = ( lastPageNumber - 1 ) * onePageAmount + lastPagePicCount;

        Common.debugPrintln( "共 " + totalPage + " 頁" );
        comicURL = new String[totalPage];


        int p = 0; // 頁數編號
        for ( int i = 1; i <= lastPageNumber && Run.isAlive; i++ ) {
            // "&start=0&sa=N"
            String baseNo = "&start=" + ( ( i - 1 ) * onePageAmount ) + "&sa=N";
            Common.debugPrintln( "目前進度第" + i + "頁: " + baseURL + baseNo );
            allPageString = getNewAllPageString( baseURL + baseNo );

            if ( googleEnum == GoogleEnum.PIC ) {
                String[] tokens = allPageString.split( "imgurl=" );
                String picName = "";
                for ( int j = 1; j < tokens.length && Run.isAlive; j++ ) {
                    p++;

                    comicURL[p - 1] = tokens[j].split( "&amp;" )[0];
                    beginIndex = comicURL[p - 1].lastIndexOf( "/" ) + 1;
                    picName = comicURL[p - 1].substring( beginIndex, comicURL[p - 1].length() );

                    if ( picName.length() > 40 ) // 檔名太長
                    {
                        continue;
                    }

                    // google獨立下載函式，需要判斷是否有同檔名的已下載檔案
                    downloadGoogle( picName, p );

                    //System.out.println( comicURL[p-1] );
                }
            }
            else {
                String[] tokens = allPageString.split( "<h3 " );
                String fileName = "";
                for ( int j = 1; j < tokens.length && Run.isAlive; j++ ) {
                    p++;

                    beginIndex = tokens[j].indexOf( "href=\"" ) + 6;
                    endIndex = tokens[j].indexOf( "\"", beginIndex );

                    comicURL[p - 1] = tokens[j].substring( beginIndex, endIndex );
                    beginIndex = tokens[j].indexOf( "\">", endIndex ) + 2;
                    endIndex = tokens[j].indexOf( "</a>", beginIndex );
                    fileName = tokens[j].substring( beginIndex, endIndex );
                    fileName = fileName.replaceAll( "</em>|<em>", "" );
                    fileName = Common.getStringRemovedIllegalChar( fileName );
                    if ( fileName.length() > 40 ) // 檔名太長
                    {
                        fileName = fileName.substring( 0, 40 ); // 防止檔名過長
                    }
                    beginIndex = comicURL[p - 1].lastIndexOf( "/" ) + 1;
                    String realFileName = comicURL[p - 1].substring( beginIndex, comicURL[p - 1].length() );
                    if ( realFileName.split( "\\." ).length > 1 ) {
                        String extensionName = realFileName.split( "\\." )[1];
                        fileName = fileName + "." + extensionName;
                    }

                    Common.debugPrint( "檔名：「" + fileName + "」  " );

                    //fileName = comicURL[p - 1].substring(beginIndex, comicURL[p - 1].length());

                    // google獨立下載函式，需要判斷是否有同檔名的已下載檔案
                    downloadGoogle( fileName, p );

                    //System.out.println( comicURL[p-1] );
                }
            }




        }

        //System.exit(0); // debug
    }

    // google獨立下載函式，需要判斷是否有同檔名的已下載檔案
    private void downloadGoogle( final String picName, final int p ) {
        
        if ( new File( getDownloadDirectory() + picName ).exists() ) {
            existSameNameFile = true;
            // 因為substance介面呼叫showOptionDialog時會出現例外情形，只好預設自動更改名稱

            if ( SetUp.getSkinClassName().indexOf( ".substance." ) > 0 ) {
                //choice = 0;
                //String className = ComicDownGUI.getDefaultSkinClassName(); // 回歸預設介面
            }

            if ( choice < 0 ) {
                CommonGUI.optionDialogChoice = choice;
                Object[] options = {"自動更改名稱", "自動略過不下載", "自動複寫檔案"};
                choice = CommonGUI.showOptionDialog( ComicDownGUI.mainFrame,
                    "資料夾內已有" + picName + "，請問應該怎麼處理？（選定後，往後遇到相同情形皆依此次辦理)",
                    "詢問視窗", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                    null, options, options[0] );
                /*
                while ( choice < 0 ) {
                    try {
                        this.wait();
                    }
                    catch ( InterruptedException ex ) {
                        Common.hadleErrorMessage( ex, "無法讓" + this.getClass() + "等待（wait）" );
                    }
                }
                */
            }

            if ( choice == 0 ) {
                String newPicName = "";
                if ( picName.split( "\\." ).length > 1 ) {
                    newPicName = Common.getStoredFileName( SetUp.getTempDirectory(), picName.split( "\\." )[0], picName.split( "\\." )[1] );
                }
                else {
                    newPicName = Common.getStoredFileName( SetUp.getTempDirectory(), picName, "jpg" );
                }
                singlePageDownload( getTitle(), getWholeTitle(), comicURL[p - 1], totalPage, p, newPicName, 0, true );
            }
            else if ( choice == 1 );
            else if ( choice == 2 ) {
                new File( getDownloadDirectory() + picName ).delete();
                singlePageDownload( getTitle(), getWholeTitle(), comicURL[p - 1], totalPage, p, picName, 0, true );
            }
            else {
                Common.errorReport( "不可能有這種數字：" + choice );
            }

        }
        else {
            existSameNameFile = false;
            singlePageDownload( getTitle(), getWholeTitle(), comicURL[p - 1], totalPage, p, picName, 0, true );
        }
    }

    // 取得基本版的網址(最後面還要加上"&start=0&sa=N"才完整)
    private String getBaseUrlString( String standardUrlString ) {
        String allPageString = getNewAllPageString( standardUrlString + "&sout=1" );

        int beginIndex = allPageString.indexOf( "href=\"http" ) + 6;
        int endIndex = allPageString.indexOf( "&gbv=", beginIndex );
        String baseUrlString = allPageString.substring( beginIndex, endIndex );

        return baseUrlString;
    }

    @Override
    public String getAllPageString( String urlString ) {
        // 將識別瀏覽器的部份參數拿掉，避免下載錯誤情形發生
        //urlString = urlString.replaceAll("&sourceid=\\w+", "&");
        //urlString = urlString.replaceAll("&client=\\w+", "&");

        //urlString = Common.getFixedChineseURL(urlString);


        if ( !new File( SetUp.getTempDirectory() + indexName ).exists() ) {
            Common.downloadFile( urlString, SetUp.getTempDirectory(), indexName, false, "", "" );
            //Common.downloadFile(urlString, SetUp.getTempDirectory(), indexName, false, "");
        }

        return Common.getFileString( SetUp.getTempDirectory(), indexName );
    }

    // 不管有沒有存在檔案，都下載新的
    public String getNewAllPageString( String urlString ) {
        // 將識別瀏覽器的部份參數拿掉，避免下載錯誤情形發生
        //urlString = urlString.replaceAll("&sourceid=\\w+", "&");
        //urlString = urlString.replaceAll("&client=\\w+", "&");

        Common.downloadFile( urlString, SetUp.getTempDirectory(), indexName, false, "", "" );
        //Common.downloadFile(urlString, SetUp.getTempDirectory(), indexName, false, "");
        return Common.getFileString( SetUp.getTempDirectory(), indexName );
    }

    @Override
    public boolean isSingleVolumePage( String urlString ) {
        // google圖片搜尋只有一頁
        return false;
    }

    public String getMainUrlFromSingleVolumeUrl( String volumeURL ) {
        return volumeURL; // google搜尋永遠都是全集頁面
    }

    @Override
    public String getTitleOnSingleVolumePage( String urlString ) {
        return "Google圖片搜尋";
    }

    @Override
    public String getTitleOnMainPage( String urlString, String allPageString ) {

        if ( allPageString.split( "/images\\?q=" ).length > 2 ) {
            return "Google圖片搜尋";
        }
        else {
            return "Google一般搜尋";
        }
    }

    @Override
    public List<List<String>> getVolumeTitleAndUrlOnMainPage( String urlString, String allPageString ) {
        // combine volumeList and urlList into combinationList, return it.

        List<List<String>> combinationList = new ArrayList<List<String>>();
        List<String> urlList = new ArrayList<String>();
        List<String> volumeList = new ArrayList<String>();

        int beginIndex = 0;
        int endIndex = 0;

        String volumeTitle = "搜尋結果";

        try {
            beginIndex = urlString.indexOf( "q=" ) + 2;
            endIndex = urlString.indexOf( "&", beginIndex );
            if ( endIndex < 0 ) {
                endIndex = urlString.length();
            }

            Common.debugPrintln( "X: " + urlString.substring( beginIndex, endIndex ) );

            // 由網址碼轉為utf8字串（如果只是普通英文+符號的關鍵字，則不會有影響）
            volumeTitle = URLDecoder.decode( urlString.substring( beginIndex, endIndex ), "UTF-8" );

            //volumeTitle = volumeTitle.replaceAll( "%20", " " );
        }
        catch ( UnsupportedEncodingException ex ) {
            Logger.getLogger( ParseGooglePic.class.getName() ).log( Level.SEVERE, null, ex );
        }
        /*
         else { // 搜尋關鍵字沒有中文（只有英文字母或符號）
         beginIndex = urlString.indexOf( "q=" ) + 2;
         endIndex = urlString.indexOf( "&", beginIndex );
         volumeTitle = urlString.substring( beginIndex, endIndex ).replaceAll(
         "\\+", " " );
         volumeTitle = volumeTitle.replaceAll( "%20", " " );
         }
         */

        urlList.add( urlString );
        volumeList.add( "搜尋「" + volumeTitle + "」" );

        totalVolume = 1;
        Common.debugPrintln( "共有" + totalVolume + "集" );

        combinationList.add( volumeList );
        combinationList.add( urlList );

        return combinationList;
    }
}
