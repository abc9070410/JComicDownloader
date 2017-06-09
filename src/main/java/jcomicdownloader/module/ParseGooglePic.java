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
        enumName = "GOOGLE_PIC";
        regexs= new String[]{"(?s).*google.com(?s).*"};
	parserName=this.getClass().getName();
        downloadBefore=true;
        siteID=Site.formString("GOOGLE_PIC");
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
    
    private void downloadGooglePicURL(String allPageString)
    {
        int beginIndex = 0, endIndex = 0;
        
        String[] picUrls = new String[1000];
        
        beginIndex = allPageString.indexOf("\"/search?q=") + 11;
        endIndex = allPageString.indexOf("&", beginIndex);
        
        String q = allPageString.substring(beginIndex, endIndex);
        
        beginIndex = allPageString.indexOf("sei=") + 4;
        endIndex = allPageString.indexOf("\"", beginIndex);
        
        String ei = allPageString.substring(beginIndex, endIndex);
     
        beginIndex = allPageString.indexOf("yp\" data-ved=\"") + 14;
        endIndex = allPageString.indexOf("\"", beginIndex);
        
        String ved = allPageString.substring(beginIndex, endIndex);
    
        String requestUrl = "https://www.google.com.tw/search?async=_id:rg_s,_pms:s&ei=" + ei + "&tbs=isz:lt,islt:xga&yv=2&q=" + q + "&asearch=ichunk&tbm=isch&ved=" + ved + "&ijn=";
        
        Common.debugPrintln("requestURL:" + requestUrl);
        
        endIndex = beginIndex = 0;
        
        int p = 0;
        int beginP = 0;
        
        for (int i = 0; i < 10; i++)
        {
            allPageString = getNewAllPageString( requestUrl + i );
            
            if (allPageString.length() < 200)
            {
                Common.debugPrintln("已經沒有頁面了");
                break;
            }
            
            beginP = p;
        
            while (p < 1000)
            {
                endIndex = allPageString.indexOf(".jpg", endIndex);
                beginIndex = allPageString.lastIndexOf("http", endIndex);
            
                if (beginIndex > 0 && endIndex > beginIndex)
                {
                    picUrls[p] = allPageString.substring(beginIndex, endIndex).replaceAll("\\\\", "") + ".jpg";
                    Common.debugPrintln(p + " pic url:" + picUrls[p]);
                    
                    p++;
                    endIndex++;
                }
                else
                {
                    break;
                }
            }
            
            for (int j = beginP; j < p; j++)
            {
                downloadSingleGooglePic(picUrls[j], j + 1, p);
            }
        }

    }
    
    private void downloadSingleGooglePic(String picUrl, int p, int totalPage)
    {
        int endIndex = picUrl.lastIndexOf( "/" ) + 1;
        String referUrl = picUrl.substring(0, endIndex);
        
        if ( !Common.existPicFile( getDownloadDirectory(), p ) ||
             !Common.existPicFile( getDownloadDirectory(), p + 1 ) )
        {
            //referUrl = "";
            singlePageDownloadUsingSimple( getTitle(), getWholeTitle(), picUrl, totalPage, p, referUrl );
        }
    }

    @Override
    public void parseComicURL() { // parse URL and save all URLs in comicURL  //
        // 先取得前面的下載伺服器網址
        String allPageString = "";
        int beginIndex = 0, endIndex = 0;
        int lastPagePicCount = 0;
        int onePageAmount = 0; // 一個頁面的檔案總數

        allPageString = getNewAllPageString( webSite );

        if ( allPageString.split( "imgurl=" ).length > 10 ||
             webSite.indexOf( "tbm=isch" ) > 0) {
            googleEnum = GoogleEnum.PIC;
            Common.debugPrintln( "此為圖片搜尋頁面" );
        }
        else {
            googleEnum = GoogleEnum.FILE;
            Common.debugPrintln( "此為一般搜尋頁面" );
        }

        if ( googleEnum == GoogleEnum.PIC ) {
            totalPage = 1000; // default
        }
        else {
            onePageAmount = 10;

            String baseGoogleURL = "http://www.google.com";

            System.out.println( allPageString.length() );

            beginIndex = allPageString.lastIndexOf( "text-decoration:underline" );
            beginIndex = allPageString.lastIndexOf( "href=\"", beginIndex ) + 6;
            endIndex = allPageString.indexOf( "\"", beginIndex );
// 
            String tempURL = baseGoogleURL + allPageString.substring( beginIndex, endIndex );
            tempURL = tempURL.replaceAll( "amp;", "" );
            endIndex = tempURL.lastIndexOf( "&start=" );
            baseURL = tempURL.substring( 0, endIndex );

            Common.debugPrintln( "一般搜尋最後頁面網址：" + tempURL );

            allPageString = getNewAllPageString( baseURL + "&start=980&sa=N" );


            beginIndex = allPageString.indexOf( "id=resultStats" );
            beginIndex = allPageString.indexOf( ">", beginIndex ) + 1;
            endIndex = allPageString.indexOf( "<", beginIndex );
            
            Common.debugPrint( "開始解析這一集有幾頁 : " );

            String[] pageTokens = allPageString.substring( beginIndex, endIndex ).split( "\\s" );

            int lastPageNumber = 1000;
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
        }
        
        Common.debugPrintln( "共 " + totalPage + " 頁" );
        comicURL = new String[totalPage];

        downloadGooglePicURL(allPageString);
        
        

        /*
        System.exit(0);

        boolean useOriginalFileName = false; // 取流水號檔名還是原始檔名
        int i = 0; // 每一個搜尋頁一百張圖片
        int p = 0; // 頁數編號
        while (Run.isAlive) {

            if (allPageString.length() < 1000)
            {
                Common.debugPrintln("下一頁已經沒有圖片可下載，收工");
                break;
            }
            
            if ( googleEnum == GoogleEnum.PIC ) {
                String[] tokens = allPageString.split( "imgurl=" );
                String picName = "";
                for ( int j = 1; j < tokens.length && Run.isAlive; j++ ) {
                    p++;

                    comicURL[p - 1] = tokens[j].split( "&amp;" )[0];
                    
                    endIndex = comicURL[p - 1].lastIndexOf( "/" ) + 1;
                    String referUrl = comicURL[p - 1].substring(0, endIndex);
                    
                    if (useOriginalFileName)
                    {
                        beginIndex = endIndex;
                        picName = comicURL[p - 1].substring( beginIndex, comicURL[p - 1].length() );

                        if ( picName.length() > 40 ) // 檔名太長
                        {
                            continue;
                        }

                        // google獨立下載函式，需要判斷是否有同檔名的已下載檔案
                        downloadGoogle( picName, p );
                    }
                    else
                    {
                        if ( !Common.existPicFile( getDownloadDirectory(), p ) ||
                             !Common.existPicFile( getDownloadDirectory(), p + 1 ) )
                        {
                            //referUrl = "";
                            singlePageDownloadUsingSimple( getTitle(), getWholeTitle(), comicURL[p-1], totalPage, p, referUrl );
                        }
                    }
                    
                    if (false) // for the specific purpose...
                    {
                        String listText = Common.getFileString( getDownloadDirectory(), "list.txt" );
                        listText += p + " : " + comicURL[p-1] + "\r\n";
                        Common.outputFile( listText, getDownloadDirectory(), "list.txt" );
                    }
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

            i++;
            String baseNo = "&ijn=" + i + "&start=" + (i *100);
            Common.debugPrintln( "目前進度第" + i + "頁: " + webSite + baseNo );
            allPageString = getNewAllPageString( webSite + baseNo );
        }
        */

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
                        Common.handleErrorMessage( ex, "無法讓" + this.getClass() + "等待（wait）" );
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

        int endIndex = allPageString.indexOf( "&amp;start=" );
        int beginIndex = allPageString.lastIndexOf( "/search", endIndex );
        String baseUrlString = "https://www.google.com" + allPageString.substring( beginIndex, endIndex );
        
        baseUrlString = baseUrlString.replaceAll("amp;", "");
        
        Common.debugPrintln("GooglePic BaseUrl: " + baseUrlString);

        //System.exit(0);
        
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
