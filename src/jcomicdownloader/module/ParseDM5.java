/*
 ----------------------------------------------------------------------------------------------------
 Program Name : JComicDownloader
 Authors  : surveyorK
 Last Modified : 2013/4/14
 ----------------------------------------------------------------------------------------------------
 ChangeLog:
 5.16: 修復dm5沒有標題名稱時解析錯誤的問題。
 5.05: 修復dm5部分解析網址錯誤的問題。
 5.03: 修復dm5下載錯誤的問題。
 5.02: 修復dm5無法下載的問題。
 5.01: 修復dm5限制漫畫無法下載的問題。
 4.19: 1. 修復dm5無法下載的問題。
 4.02: 1. 修復dm5新集數無法下載的問題。
 *  2.17: 1. 新增對dm.game.mop.com的支援。
 ----------------------------------------------------------------------------------------------------
 */
package jcomicdownloader.module;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import jcomicdownloader.tools.*;
import jcomicdownloader.enums.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import jcomicdownloader.ComicDownGUI;
import jcomicdownloader.SetUp;
import jcomicdownloader.encode.Encoding;

public class ParseDM5 extends ParseOnlineComicSite
{

    private int radixNumber; // use to figure out the name of pic
    private String jsName;
    protected String indexName;
    protected String indexEncodeName;
    protected String baseURL;

    /**

     @author user
     */
    public ParseDM5()
    {
        siteID = Site.DM5;
        siteName = "dm5";
        indexName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_dm5_parse_", "html" );
        indexEncodeName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_mop_dm5_parse_", "html" );

        jsName = "index_dm5.js";
        radixNumber = 15201471; // default value, not always be useful!!

        baseURL = "http://www.dm5.com";
    }

    public ParseDM5( String webSite, String titleName )
    {
        this();
        this.webSite = webSite;
        this.title = titleName;
    }

    @Override
    public void setParameters()
    {
        Common.debugPrintln( "開始解析各參數 :" );

        Common.debugPrintln( "開始解析title和wholeTitle :" );
        String cookie = "isAdult=1";
        Common.simpleDownloadFile( webSite, SetUp.getTempDirectory(), indexName, cookie, webSite );

        if ( getWholeTitle() == null || getWholeTitle().equals( "" ) )
        {
            String allPageString = Common.getFileString( SetUp.getTempDirectory(), indexName );

            int beginIndex = allPageString.indexOf( "DM5_CTITLE" );
            beginIndex = allPageString.indexOf( "\"", beginIndex ) + 1;
            int endIndex = allPageString.indexOf( "\"", beginIndex );
            String tempTitleString = allPageString.substring( beginIndex, endIndex ).trim();

            setWholeTitle( getVolumeWithFormatNumber( Common.getStringRemovedIllegalChar(
                    Common.getTraditionalChinese( tempTitleString.trim() ) ) ) );
        }

        Common.debugPrintln( "作品名稱(title) : " + getTitle() );
        Common.debugPrintln( "章節名稱(wholeTitle) : " + getWholeTitle() );
    }

    @Override
    public void parseComicURL()
    { // parse URL and save all URLs in comicURL  //
        // 先取得前面的下載伺服器網址

        String allPageString = Common.getFileString( SetUp.getTempDirectory(), indexName );
        Common.debugPrint( "開始解析這一集有幾頁 : " );

        // ex. DM5_IMAGE_COUNT=25;
        int beginIndex = allPageString.indexOf( "DM5_IMAGE_COUNT" );
        beginIndex = allPageString.indexOf( "=", beginIndex ) + 1;
        int endIndex = allPageString.indexOf( ";", beginIndex );

        String tempString = allPageString.substring( beginIndex, endIndex );

        totalPage = Integer.parseInt( tempString );
        Common.debugPrintln( "共 " + totalPage + " 頁" );
        comicURL = new String[ totalPage ];

        // 取得主頁網址(用於檔頭Refer）
        beginIndex = allPageString.indexOf( "href=\"/" ) + 2;
        beginIndex = allPageString.indexOf( "href=\"/", beginIndex );
        beginIndex = allPageString.indexOf( "\"", beginIndex ) + 1;
        endIndex = allPageString.indexOf( "\"", beginIndex );
        String referURL = baseURL + allPageString.substring( beginIndex, endIndex );
        Common.debugPrintln( "主頁網址：" + referURL );

        // ex. DM5_CID=85258;
        beginIndex = allPageString.indexOf( "DM5_CID" );
        beginIndex = allPageString.indexOf( "=", beginIndex ) + 1;
        endIndex = allPageString.indexOf( ";", beginIndex );
        String cid = allPageString.substring( beginIndex, endIndex );
        Common.debugPrintln( "DM5_CID=" + cid );

        String dm5Key = "";
        beginIndex = allPageString.indexOf( "eval(function" );
        if ( beginIndex > 0 )
        {
            endIndex = allPageString.indexOf( "</script>", beginIndex );
            tempString = allPageString.substring( beginIndex, endIndex );

            dm5Key = getNewDM5Key( tempString ); // 取得dm5_key，或稱取得mkey
        }

        Common.debugPrintln( "dm5_key = " + dm5Key );

        //String cookieString = Common.getCookieString( webSite );

        String frontURL = webSite.substring( 0, webSite.length() - 1 );
        String[] comicDataURL = new String[ totalPage ];
        for ( int i = 0; i < comicDataURL.length; i++ )
        {
            if ( i > 0 )
            {
                comicDataURL[i] = frontURL + "-" + (i + 1);
            }
            else
            {
                comicDataURL[i] = frontURL;
            }

            // ex. /chapterimagefun.ashx?cid=55303&page=1&language=1&key=
            comicDataURL[i] += "/chapterimagefun.ashx?cid="
                    + cid + "&page="
                    + (i + 1) + "&language=1&key=" + dm5Key;

            Common.debugPrintln( comicDataURL[i] );
        }

        referURL = webSite;

        boolean firstPicDownload = false; // 第一張下載了沒
        String picURL = "";
        String cidAndKey = getCidAndKey( comicDataURL[1], gerReferURL( webSite, 1 ) );

        for ( int p = 0; p < comicURL.length && Run.isAlive; )
        {
            Common.debugPrintln( p + "" );

            referURL = gerReferURL( webSite, p );
            //Common.debugPrintln("REFER: " + referURL);
            if ( !Common.existPicFile( getDownloadDirectory(), p + 1 )
                    || !Common.existPicFile( getDownloadDirectory(), p + 2 ) )
            {
                String dm5DataFileName = "dm5_data";
                Common.downloadFile( comicDataURL[p], SetUp.getTempDirectory(),
                                     dm5DataFileName, false, "", referURL );

                allPageString = Common.getFileString( SetUp.getTempDirectory(), dm5DataFileName );

                String[] picURLs = getPicURLs( allPageString, cidAndKey );

                Common.debugPrintln( "下載張數:" + picURLs.length );

                for ( int i = 0; i < picURLs.length && Run.isAlive; i++ )
                {

                    //singlePageDownload(getTitle(), getWholeTitle(),
                    //        picURLs[i], totalPage, p + 1, 0);
                    referURL = gerReferURL( webSite, p );

                    Common.debugPrintln( "REFER : " + referURL );
                    singlePageDownloadUsingSimple( getTitle(), getWholeTitle(),
                                                   picURLs[i], totalPage, p + 1, "", referURL );

                    p++;
                }
                //System.exit( 0 );

            }
            else
            {
                p++;
            }
        }

        //System.exit( 0 ); // debug
    }

    public String getCidAndKey( String url, String referURL )
    {

        String dm5DataFileName = "dm5_data";
        Common.downloadFile( url, SetUp.getTempDirectory(),
                             dm5DataFileName, false, "", referURL );

        String allPageString = Common.getFileString( SetUp.getTempDirectory(), dm5DataFileName );


        // 取得網址的token 
        // ex. png|pvalue|var|dm5imagefun|pix|10015|11|1_6059|2_2200|
        //     3_1855|111728|tel||http|function|com|yourour||manhua21|
        //     4_2658|for|10_7747|return|length|9_2296|6_2514|5_3750|8_8047|7_1301
        int beginIndex = allPageString.indexOf( "'|" ) + 2;
        int endIndex = allPageString.indexOf( "'.split", beginIndex );

        String[] tokens = allPageString.substring( beginIndex, endIndex ).split( "\\|" );

        Common.debugPrint( "網址Tokens: " );
        for ( int i = 0; i < tokens.length; i++ )
        {
            Common.debugPrint( i + " [" + tokens[i] + "] " );
        }

        // 取得圖片位址?後面的部分 ( cid和key )
        beginIndex = allPageString.indexOf( "'?" ) + 1;
        endIndex = allPageString.indexOf( "'", beginIndex ) - 1;
        String[] tempTokens = allPageString.substring( beginIndex, endIndex ).split( "|" );

        String backURL = "";
        Common.debugPrint( "\n網址後面部分的Token順序: " );
        for ( int i = 0; i < tempTokens.length; i++ )
        {
            Common.debugPrint( " [" + tempTokens[i] + "] " );

            backURL += getCorrespondingToken( tokens, tempTokens[i], -1 );
        }
        Common.debugPrintln( "\n網址後面部分" + backURL );

        return backURL;
    }

    public String gerReferURL( String mainURL, int p )
    {
        if ( p == 0 )
        {
            return mainURL;
        }
        else
        {
            return mainURL.substring( 0, mainURL.length() - 1 ) + "-p" + (p + 1) + "/";
        }
    }

    public String[] getPicURLs( String allPageString, String cidAndKey )
    {

        // 取得網址的token 
        // ex. png|pvalue|var|dm5imagefun|pix|10015|11|1_6059|2_2200|
        //     3_1855|111728|tel||http|function|com|yourour||manhua21|
        //     4_2658|for|10_7747|return|length|9_2296|6_2514|5_3750|8_8047|7_1301
        int beginIndex = allPageString.indexOf( "'|" ) + 2;
        int endIndex = allPageString.indexOf( "'.split", beginIndex );

        String[] tokens = allPageString.substring( beginIndex, endIndex ).split( "\\|" );

        Common.debugPrint( "網址Tokens: " );
        for ( int i = 0; i < tokens.length; i++ )
        {
            Common.debugPrint( i + " [" + tokens[i] + "] " );
        }

        // 取得網址目錄的token順序
        beginIndex = allPageString.indexOf( "://" ) - 1;
        endIndex = allPageString.indexOf( "\"", beginIndex );
        String[] tempTokens = allPageString.substring( beginIndex, endIndex ).split( "|" );

        String basePicURL = "";
        Common.debugPrint( "\n網址Token順序: " );
        for ( int i = 0; i < tempTokens.length; i++ )
        {
            Common.debugPrint( " [" + tempTokens[i] + "] " );

            basePicURL += getCorrespondingToken( tokens, tempTokens[i], -1 );
        }
        Common.debugPrintln( "\n基本位址" + basePicURL );

        // 取得圖片檔名
        beginIndex = allPageString.indexOf( "=[\"" ) + 2;
        endIndex = allPageString.indexOf( "]", beginIndex );
        tempTokens = allPageString.substring( beginIndex, endIndex ).split( "," );

        String[] picNames = new String[ tempTokens.length ];
        String[] picURLs = picNames;

        Common.debugPrint( "\n圖片檔名token順序: " );
        for ( int i = 0; i < tempTokens.length; i++ )
        {
            picNames[i] = "";
            tempTokens[i] = tempTokens[i].replaceAll( "\"", "" );

            Common.debugPrintln( " [" + tempTokens[i] + "] " );

            String[] tempPicTokens = tempTokens[i].split( "|" );
            for ( int j = 1; j < tempPicTokens.length; j++ )
            {
                Common.debugPrint( " [" + tempPicTokens[j] + "] " );

                picNames[i] += getCorrespondingToken( tokens, tempPicTokens[j], -1 );
            }
            Common.debugPrint( i + " 圖片檔名:" + picNames[i] );

            picURLs[i] = Common.getFixedChineseURL( basePicURL + picNames[i] + cidAndKey );

            Common.debugPrintln( " 圖片網址:" + picURLs[i] );
        }



        //System.exit( 0 );

        return picURLs;
    }

    public String getCorrespondingToken( String[] tokens, String token, int offset )
    {
        String correspondingToken = "";
        if ( token == null || token.matches( "" ) || token.matches( "\\\\" ) )
        {
            correspondingToken = "";
        }
        else if ( token.matches( ":|/|\\.|" ) )
        {
            correspondingToken = token;
        }
        else
        {
            int no = getIntegerFromHex( token ) + offset;
            if ( no < tokens.length && no >= 0 )
            {
                correspondingToken = tokens[no];
            }
            else
            {
                correspondingToken = token;
            }


            if ( correspondingToken.matches( "" ) )
            {
                correspondingToken = token;
            }
        }

        return correspondingToken;
    }

    // 取得0~15 (ex. a -> 10, f -> 15)
    private int getIntegerFromHex( String hex )
    {
        int integer;
        try
        {
            integer = Integer.parseInt( hex );
        }
        catch ( NumberFormatException ex )
        {
            if ( hex.charAt( 0 ) - 'a' >= 0
                    && hex.charAt( 0 ) - 'a' <= 23 )
            {
                integer = 10 + (hex.charAt( 0 ) - 'a');
            }
            else
            {
                integer = -1;
            }
        }

        return integer;
    }

    public String getNewDM5Key( String allPageString )
    {
        int beginIndex, endIndex;
        String dm5Key = "";

        // 取得key的token
        endIndex = allPageString.indexOf( "'.split" );
        beginIndex = allPageString.lastIndexOf( "'", endIndex - 1 ) + 1;
        String[] keyTokens = allPageString.substring( beginIndex, endIndex ).split( "\\|" );

        Common.debugPrint( "Key的Tokens: " );
        for ( int i = 0; i < keyTokens.length; i++ )
        {
            Common.debugPrint( i + " [" + keyTokens[i] + "] " );
        }


        // 取得key的token順序
        endIndex = allPageString.indexOf( ";$" );
        beginIndex = allPageString.lastIndexOf( "=", endIndex ) + 1;
        String[] keyOrderTokens = allPageString.substring( beginIndex, endIndex ).split( "\\+" );

        Common.debugPrint( "\nKey的Token順序: " );
        for ( int i = 0; i < keyOrderTokens.length; i++ )
        {
            keyOrderTokens[i] = keyOrderTokens[i].replaceAll( "\\\\'", "" );

            Common.debugPrint( " [" + keyOrderTokens[i] + "] " );

            dm5Key += getCorrespondingToken( keyTokens, keyOrderTokens[i], 0 );
        }
        Common.debugPrintln( "\nDM5 Key: " + dm5Key );



        return dm5Key;
    }

    public String getDM5Key( String scriptString )
    {
        int beginIndex, endIndex;



        // 先取得位置串列
        beginIndex = scriptString.indexOf( "return p" );
        beginIndex = scriptString.indexOf( "'", beginIndex ) + 1;
        endIndex = scriptString.lastIndexOf( "=\\';" );


        int beginIndex1 = scriptString.lastIndexOf( "(", endIndex );
        int beginIndex2 = scriptString.lastIndexOf( ";", endIndex );

        // ('')之內只有一組var變數
        if ( beginIndex1 > beginIndex2 )
        {
            beginIndex = beginIndex1 + 1;
        }
        else
        {
            beginIndex = beginIndex2 + 1;
        }

        endIndex += 3;

        String tempString = scriptString.substring( beginIndex, endIndex );
        Common.debugPrintln( "dm5 key: " + tempString );

        /*
         * // 如果/'+/'就沒轍了，譬如http://www.dm5.com/m9701/ String[] indexStrings =
         tempString.replaceAll( "\\'", "" ).replaceAll( "\\\\", "" ).split(
         "\\s|=|\\+" ); for ( int i = 0; i < indexStrings.length; i++ ) {
         Common.debugPrintln( "index [" + i + "] = " + indexStrings[i] ); }
         */

        Common.debugPrintln( "----------" );
        List<String> indexList = new ArrayList<String>();
        tempString = tempString.replaceAll( "\\'", "" ).replaceAll( "\\\\", "" );

        // var fdlsmfl3511564612d的部份
        indexList.add( String.valueOf( tempString.charAt( 0 ) ) );
        indexList.add( String.valueOf( tempString.charAt( 1 ) ) );

        // =後面的部份
        for ( int i = 5; i < tempString.length(); i += 2 )
        {
            indexList.add( String.valueOf( tempString.charAt( i ) ) );
        }

        Common.debugPrintln( indexList.toString() );

        // 將ArrayList轉給String[]
        String[] indexStrings = new String[ indexList.size() ];
        for ( int i = 0; i < indexList.size(); i++ )
        {
            indexStrings[i] = indexList.get( i );
        }

        //System.exit( 0 );

        // 再取得資料串列
        beginIndex = scriptString.indexOf( "|", beginIndex );
        beginIndex = scriptString.lastIndexOf( "'", beginIndex ) + 1;
        endIndex = scriptString.indexOf( "'", beginIndex );
        tempString = scriptString.substring( beginIndex - 1, endIndex + 1 );

        String[] arraryStrings = tempString.split( "\\|" );
        for ( int i = 0; i < arraryStrings.length; i++ )
        {
            Common.debugPrintln( "arrary [" + i + "] = " + arraryStrings[i] );
        }

        // 防止因為｜在最旁邊而少算
        if ( arraryStrings[0].length() <= 1 )
        {
            arraryStrings[0] = "";
        }
        else
        {
            arraryStrings[0] = arraryStrings[0].substring( 1, arraryStrings[0].length() );
        }

        int len = arraryStrings.length - 1;
        if ( arraryStrings[len].length() <= 1 )
        {
            arraryStrings[len] = "";
        }
        else
        {
            arraryStrings[len] = arraryStrings[len].substring( 0, arraryStrings[len].length() - 1 );
        }

        // 然後將位置套入資料
        String dm5Key = "";
        int index;
        for ( int i = 0; i < indexStrings.length; i++ )
        {
            if ( !indexStrings[i].equals( "" ) )
            {
                index = getIntegerFromHex( indexStrings[i] );
                if ( index < 0 || arraryStrings[index].equals( "" ) )
                {
                    dm5Key += indexStrings[i];
                }
                else
                {
                    dm5Key += arraryStrings[index];
                }

                if ( i == 0 )
                {
                    dm5Key += " ";
                }
                else if ( i == 1 )
                {
                    dm5Key += "=";
                }
            }
        }
        Common.debugPrintln( "原始dm5Key = " + dm5Key );

        dm5Key = dm5Key.split( "=" )[1] + "=";
        try
        {
            dm5Key = URLEncoder.encode( dm5Key, "UTF-8" );
        }
        catch ( UnsupportedEncodingException ex )
        {
            Common.errorReport( "無法轉換為網址格式：" + dm5Key );
            Logger.getLogger( ParseDM5.class.getName() ).log( Level.SEVERE, null, ex );
        }

        return dm5Key;
    }

    @Override
    public String getAllPageString( String urlString )
    {
        return getAllPageString( urlString, "" );
    }

    public String getAllPageString( String urlString, String referURL )
    {
        String indexName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_dm5_", "html" );

        //String cookieString = "isAdult=1; ";
        Common.downloadFile( urlString, SetUp.getTempDirectory(), indexName,
                             false, "", referURL );

        return Common.getFileString( SetUp.getTempDirectory(), indexName );
    }

    @Override
    public boolean isSingleVolumePage( String urlString )
    {
        // ex. http://www.dm5.com/m32738/

        if ( !urlString.matches( ".*-.*" ) )
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public String getMainUrlFromSingleVolumeUrl( String volumeURL )
    {
        // ex. http://www.dm5.com/m32738/轉為
        //     http://www.dm5.com/manhua-haidaozhanji/

        String allPageString = getAllPageString( volumeURL );

        int beginIndex = allPageString.indexOf( "id=\"btnBookmarker\"" );
        beginIndex = allPageString.indexOf( "href=", beginIndex );
        beginIndex = allPageString.indexOf( "\"", beginIndex ) + 1;
        int endIndex = allPageString.indexOf( "\"", beginIndex );

        String mainPageURL = baseURL + allPageString.substring( 0, endIndex );

        Common.debugPrintln( "MAIN_URL: " + mainPageURL );

        return mainPageURL;
    }

    @Override
    public String getTitleOnMainPage( String urlString, String allPageString )
    {
        int beginIndex, endIndex;

        beginIndex = allPageString.indexOf( "inbt_title_h2" );
        beginIndex = allPageString.indexOf( ">", beginIndex ) + 1;
        endIndex = allPageString.indexOf( "<", beginIndex );
        String title = allPageString.substring( beginIndex, endIndex ).trim();

        return Common.getStringRemovedIllegalChar( Common.getTraditionalChinese( title ) );
    }

    @Override
    public List<List<String>> getVolumeTitleAndUrlOnMainPage( String urlString, String allPageString )
    {
        // combine volumeList and urlList into combinationList, return it.

        List<List<String>> combinationList = new ArrayList<List<String>>();
        List<String> urlList = new ArrayList<String>();
        List<String> volumeList = new ArrayList<String>();

        String tempString = "";
        int beginIndex, endIndex;

        if ( allPageString.indexOf( "id=\"checkAdult" ) > 0 )
        {
            Common.debugPrintln( "此為限制漫畫，重新下載網頁" );

            String cookie = "isAdult=1";
            Common.simpleDownloadFile( urlString, SetUp.getTempDirectory(), indexName, cookie, "" );

            allPageString = Common.getFileString( SetUp.getTempDirectory(), indexName );

        }

        beginIndex = allPageString.indexOf( "id=\"cbc" );
        endIndex = allPageString.lastIndexOf( "id=\"cbc" );
        endIndex = allPageString.indexOf( "</ul>", endIndex );

        // 存放集數頁面資訊的字串
        tempString = allPageString.substring( beginIndex, endIndex );
        
        Common.debugPrintln( tempString );

        int volumeCount = tempString.split( "href=" ).length - 1;

        String volumeTitle = "";
        beginIndex = endIndex = 0;
        String tempURL = "";
        int amountOfnonURL = 0;
        for ( int i = 0; i < volumeCount; i++ )
        {
            // 取得單集位址
            beginIndex = tempString.indexOf( "href=", beginIndex );
            beginIndex = tempString.indexOf( "\"", beginIndex ) + 1;
            endIndex = tempString.indexOf( "\"", beginIndex );
            tempURL = baseURL + tempString.substring( beginIndex, endIndex );

            if ( tempURL.matches( ".*javascript:.*" ) )
            {
                amountOfnonURL++;
                beginIndex++;
            }
            else
            {
                urlList.add( tempURL );

                // 取得單集名稱
                beginIndex = tempString.indexOf( "title=", beginIndex ) + 1;
                beginIndex = tempString.indexOf( "\"", beginIndex ) + 1;
                endIndex = tempString.indexOf( "\"", beginIndex );
                volumeTitle = tempString.substring( beginIndex, endIndex );
                
                // 當沒有標題名稱時，拿位址來充數
                if ( "".matches( volumeTitle ) )
                {
                    volumeTitle = "title_" + tempURL.replace( baseURL, "" );
                    Common.debugPrintln( "新的名稱: " + volumeTitle );
                }

                volumeList.add( getVolumeWithFormatNumber( Common.getStringRemovedIllegalChar(
                        Common.getTraditionalChinese( volumeTitle.trim() ) ) ) );
            }

        }

        totalVolume = volumeCount;
        Common.debugPrintln( "共有" + totalVolume + "集" );

        combinationList.add( volumeList );
        combinationList.add( urlList );

        return combinationList;
    }
}
