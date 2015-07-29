/*
 ----------------------------------------------------------------------------------------------------
 Program Name : JComicDownloader
 Authors  : surveyorK
 Last Modified : 2013/6/26
 ----------------------------------------------------------------------------------------------------
 ChangeLog:
 5.17: 修復178圖片伺服器位址錯誤的問題。
 5.15: 1. 修復178下載錯誤的問題。
          2. 修復178位址改變的問題。
 5.14: 1. 修復178因改版而無法下載的問題。
 2. 調整178的圖片伺服器位址。
 5.03: 修復178部分無法下載的問題。
 4.19: 修復178因網址改變而無法下載的問題。
 4.0 : 1. 翻新178的解析方式，直接轉碼而非參照字典檔。
 *  2.17: 1. 修復檔名含有中文就會解析錯誤的bug。
 *  2.16: 1. 修復少數檔名解析錯誤的bug。
 *  2.11: 1. 修復有時候下載網頁發生錯誤的問題。
 *  2.10: 1. 新增manhua.178.com的支援。
 ----------------------------------------------------------------------------------------------------
 */
package jcomicdownloader.module;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import jcomicdownloader.Flag;
import jcomicdownloader.SetUp;
import jcomicdownloader.enums.*;
import jcomicdownloader.module.ParseOnlineComicSite;
import jcomicdownloader.tools.*;

public class Parse178 extends ParseOnlineComicSite
{

    private int radixNumber; // use to figure out the name of pic
    private String jsName;
    protected String indexName;
    protected String indexEncodeName;
    protected String baseURL;
    protected int waitingTime; // 下載錯誤後的等待時間
    protected int retransmissionLimit; // 最高重試下載次數
    // 用於存放網址中已經解碼的編碼字串和解碼字串
    protected List<String> codeList = new ArrayList<String>();
    protected List<String> decodeList = new ArrayList<String>();
    protected boolean tsukkomiMode;
    
    List<String> newTitleList = new ArrayList<String>(); 
    List<String> newTagList = new ArrayList<String>(); 
    List<String> newVolumeTitleList = new ArrayList<String>(); 
    List<String> newVolumeDirList = new ArrayList<String>(); 
    String res_id = "";
    
    String mainListFileName = "main_list.js";
    String newListFileName = "new_list.js";

    /**

     @author user
     */
    public Parse178()
    {
        enumName = "MANHUA_178";
        parserName=this.getClass().getName();
        regexs=new String[]{"(?s).*178.com(?s).*" ,"(?s).*dmzj.com(?s).*" };
        siteID=Site.formString("MANHUA_178");
        siteName = "178";
        indexName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_178_parse_", "html" );
        indexEncodeName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_178_encode_parse_", "html" );

        jsName = "index_178.js";
        radixNumber = 1593771; // default value, not always be useful!!

        baseURL = "http://manhua.dmzj.com";//"http://manhua.178.com";
        waitingTime = 2000;
        retransmissionLimit = 30;
        
        tsukkomiMode = false;
    }

    public Parse178( String webSite, String titleName )
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
        Common.downloadGZIPInputStreamFile( webSite, SetUp.getTempDirectory(), indexName, false, "" );

        if ( getWholeTitle() == null || getWholeTitle().equals( "" ) )
        {
            String allPageString = Common.getFileString( SetUp.getTempDirectory(), indexName );

            int beginIndex = allPageString.indexOf( "g_chapter_name" );
            beginIndex = allPageString.indexOf( "\"", beginIndex ) + 1;
            int endIndex = allPageString.indexOf( "\"", beginIndex );
            String tempTitleString = allPageString.substring( beginIndex, endIndex );

            setWholeTitle( getVolumeWithFormatNumber( Common.getStringRemovedIllegalChar(
                    Common.getTraditionalChinese( tempTitleString.trim() ) ) ) );
        }

        Common.debugPrintln( "作品名稱(title) : " + getTitle() );
        Common.debugPrintln( "章節名稱(wholeTitle) : " + getWholeTitle() );
    }
    
    
    private boolean isRssPage()
    {
        return (webSite.indexOf("rss.xml") > 0);
    }
    
    private boolean isIllegalPage(String pageName)
    {
        if (pageName.matches("tags") || 
            pageName.matches("css") || 
            pageName.matches("rank") || 
            !pageName.matches("\\w+"))
            return true;
        else 
            return false;
    }
    
    public void print(String message) {
        Common.debugPrintln(message);
    }
    
    private List<String> getTagNameList(String url)
    {
        int beginIndex = 0;
        int endIndex = 0;
        int index = 0;
        List<String> urlList = new ArrayList<String>();
        String temp = "";
        
        String dummyTagName = "ghdxj";
        Common.deleteFile(getBaseOutputDirectory(), dummyTagName + ".js");
        Common.deleteFolder(getBaseOutputDirectory() + dummyTagName);
        urlList.add(dummyTagName); // 因為第一個都會取得錯誤的評論資料，所以想把錯都推給東方 


        String allPageString = getAllPageString(url);

        while (true)
        {
            beginIndex = allPageString.indexOf(" href=", beginIndex);
            if (beginIndex < 0) break;
            beginIndex = allPageString.indexOf("=", beginIndex) + 2;
            if (beginIndex < 0) break;

            temp = allPageString.substring(beginIndex, beginIndex + 30);
            if (temp.indexOf(baseURL) >= 0)
            {
                //print("with BASE");
                // ex. href='http://manhua.dmzj.com/lianaibaojun/
                beginIndex = allPageString.indexOf(".com", beginIndex);
                if (beginIndex < 0) break;
                beginIndex = allPageString.indexOf("/", beginIndex);
                if (beginIndex < 0) break;
            }
            beginIndex ++; // 從"/"之後開始
            endIndex = allPageString.indexOf("/", beginIndex);
            if (endIndex < 0) break;
            temp = allPageString.substring( beginIndex, endIndex );

            boolean existed = false;
            for (int i = 0; i < urlList.size(); i ++)
            {
                if (urlList.get(i).equals(temp))
                {
                    existed = true;
                    break;
                }
            }

            if (existed || isIllegalPage(temp))
            {
                continue;
            }


            urlList.add(temp);
            //print("" + index + " : " + temp);
            index++;
            beginIndex = endIndex;
        }
        
        return urlList;
    }
    
    
    private void outputVolumeComment(String tagName, String volumeTitle, String fileName, String siteName, List<String> commentList)
    {
        String text = "";
        text += "VOLUME_TITLE = '" + volumeTitle + "';\n";
        text += siteName + " = new Array( \n";
        
        for (int i = 0; i < commentList.size(); i ++)
        {
            if (i > 0)
            {
                text += ", ";
            }
            text += "'" + commentList.get(i) + "'";
        }
        text += "\n);";
        
        String outputDirectory = getBaseOutputDirectory() + tagName + Common.getSlash();
        Common.outputFile(text, outputDirectory, fileName + ".js");
    }
    
    private String getBaseOutputDirectory()
    {
        return Common.getNowAbsolutePath() + 
            "down" + Common.getSlash() + 
            "SVN" + Common.getSlash() + 
            "Tsukkomi1" + Common.getSlash();
    }
    
    private String getVolumeID(String snsSysID)
    {
        return snsSysID.split("_")[1];
    }
    
    private void outputVolumeIndex(
            String tagName, 
            String titleName, 
            String titleIntroduction,
            List<String> volumeTitleList, 
            List<String> snsSysIDList )
    {
        String outputDirectory = getBaseOutputDirectory();
        String text = "";
        int count = volumeTitleList.size();
        text += "TITLE_NAME = '" + getOutputText(titleName) + "';\n";
        text += "TITLE_INTRODUCTION = '" + getOutputText(titleIntroduction) + "';\n";
        text += "VOLUME_LIST = new Array( ";
        for (int i = 0; i < count; i++)
        {
            if (i > 0)
                text += ", ";
            text += "'" + getOutputText(volumeTitleList.get(i)) + "', " +
                    "'" + getVolumeID( snsSysIDList.get(i) ) + "'";
        }
        text += "\n);\n";

        Common.outputFile(text, outputDirectory, tagName + ".js");
    }
    
    
    private String getCommentMoreURL(String sns_sys_id)
    {
        String jsonp1 = "141249668" + (int)(9999 * Math.random());
        String jsonp2 = "141249670" + (int)(9999 * Math.random());
        
        return "http://interface3.i.178.com/~cite.embed.ViewAll?callback=jsonp" + 
                jsonp1 + "&_=" + jsonp2 + "&res_id=" + 
                res_id + "&sys_res_id=" + sns_sys_id + "&sys_name=manhua178";
    }

    private String getCommentURL(String sns_sys_id, String sns_view_point_token)
    {
        return "http://interface3.i.178.com/~cite.embed.VoteJS/sysname/manhua178/sys_id/" + 
                sns_sys_id + "/token/" + sns_view_point_token;
    }

    private String getUtf8Text(String code)
    {
        if (code.indexOf("\\u") < 0)
        {
            // 不需要轉碼
            return code;
        }
        
        String lastText = "";
        int index = code.indexOf("(");
        if (index > 0)
        {
            int tempIndex = code.indexOf("\\u", index);
            if (tempIndex < 0)
            {
                lastText = code.substring(index, code.length());
                code = code.substring(0, index);
            }
        }
        
        String[] codes = code.split( "\\\\u");
        String text = "";
        for (int i = 1; i < codes.length; i ++)
        {
            String temp = codes[i];
            String last = "";
            if (temp.length() > 4)
            {
                last = temp.substring(4, temp.length());
                temp = temp.substring(0, 4);
            }
            //print(code + ": " + i + " : " + temp);
            text += ( char ) Integer.parseInt( temp, 16 ) + last;
        }
        
        text += lastText;
        
        if (lastText.equals(""))
        {
            //print("轉換前:" + code + "轉換後:" + text);
        }
         
        return text;
    }
    
    // more : http://interface3.i.178.com/~cite.embed.ViewAll?callback=?res_id=4606&sys_res_id=4606_8436&sys_name=manhua178
    // normal : http://interface3.i.178.com/~cite.embed.VoteJS/sysname/manhua178/sys_id/6567_34593/token/0a7e131c24510879fa79ad4c8c6660bd
    private List<String> getCommentParseText(List<String> textList, String commentURL)
    {
        int beginIndex = 0;
        int endIndex = 0;
        String text = getAllPageString(commentURL);
        
        if (commentURL.indexOf("VoteJS") > 0)
        {
            beginIndex = text.indexOf("cite_vote_num");
            if (beginIndex < 0)
            {
                // 尚未評論
                textList.add("");
                return textList;
            }
            beginIndex = text.indexOf(">", beginIndex) + 1;
            endIndex = text.indexOf("<", beginIndex);
            textList.add(text.substring(beginIndex, endIndex));
            
            beginIndex = text.indexOf("postVote(", beginIndex);
            beginIndex = text.indexOf("(", beginIndex) + 1;
            endIndex = text.indexOf(",", beginIndex);
            res_id = text.substring(beginIndex, endIndex);
        }
        
        while (true)
        {
            beginIndex = text.indexOf("interactive-opinion-block-", beginIndex);
            if (beginIndex < 0)
            {
                break;
            }
            beginIndex = text.indexOf(">", beginIndex) + 1;
            endIndex = text.indexOf("<", beginIndex);
            String comment = text.substring(beginIndex, endIndex);
            comment = getUtf8Text(comment);
            comment = comment.replaceAll("\"|'", "");
            comment = Common.getTraditionalChinese(comment);
            
            if (comment.matches("更多"))
            {
                break;
            }
            textList.add(comment);
            
            beginIndex = text.indexOf("title=", beginIndex);
            if (beginIndex < 0)
            {
                break;
            }
            beginIndex = text.indexOf("\"", beginIndex) + 1;
            endIndex = text.indexOf(")", beginIndex);
            String temp = text.substring(beginIndex, endIndex);
            temp = getUtf8Text(temp);
            temp = temp.replaceAll("共有", "");
            temp = temp.replaceAll("人赞同此观点", "");
            String[] temps = temp.split("\\(");
            
            if (temps.length < 2)
            {
                print("FAIL -> " + temps.length + " : " + temp);
            }
            
            String num = temps[0];
            String ratio = temps[1];
            textList.add(num);
            textList.add(ratio);
        }
        
        
        
        
                
        return textList;
    }
    
    
    private int getExistedVolumeCount(String tagName)
    {
        String text = Common.getFileString( getBaseOutputDirectory(), tagName + ".js" );
        int beginIndex = text.indexOf("new Array");
        int endIndex = text.indexOf(")", beginIndex);
        
        if (beginIndex < 0 || endIndex < 0)
            return 0;
        
        String temp = text.substring( beginIndex, endIndex);
        //print(temp);
        
        return temp.split(",").length;
    }
    
    private boolean needUpdate(String tagName, List<String> volumeTitleList)
    {
        if (volumeTitleList.size() == 0)
            return false;
        
        String text = Common.getFileString( getBaseOutputDirectory(), tagName + ".js" );
        int lastVolumeIndex = volumeTitleList.size() - 1;
        String lastVolumeTitle = volumeTitleList.get(lastVolumeIndex);
        
        //  如果目錄裡面找不到最後一集，代表需要更新
        return (text.indexOf(lastVolumeTitle) < 0); 
    }
    
    private void updateIndexFile(String tagName, String titleName, String titleIntroduction, String volumeTitle, String snsSysID)
    {
        String text = Common.getFileString( getBaseOutputDirectory(), tagName + ".js" );

        int midIndex1 = text.indexOf("new Array(") + 11;
        int endIndex = text.length();

        if (midIndex1 < 11)
        {
            print("第 1 筆索引資料");
            // 新建index file
            List<String> volumeTitleList = new ArrayList<String>(); 
            List<String> snsSysIDList = new ArrayList<String>(); 
            volumeTitleList.add(volumeTitle);
            snsSysIDList.add(snsSysID);
            outputVolumeIndex(tagName, titleName, titleIntroduction, volumeTitleList, snsSysIDList);
            return;
        }
        
        print("第 n 筆索引資料");
        
        text = text.substring(0, midIndex1) + "'" + getOutputText(volumeTitle) + "', " +
               "'" + getVolumeID( snsSysID ) + "', " + 
               text.substring(midIndex1, endIndex);
        
        Common.outputFile(text, getBaseOutputDirectory(), tagName + ".js");
    }
    
    private void handleTitlePic(String tagName, String text)
    {
        String picName = tagName + ".jpg";
        int beginIndex = text.indexOf("anim_intro_ptext");
        if (beginIndex < 0 || new File(getBaseOutputDirectory() + picName).exists())
        {
            return;
        }
        beginIndex = text.indexOf("src=", beginIndex);
        beginIndex = text.indexOf("\"", beginIndex) + 1;
        int endIndex = text.indexOf("\"", beginIndex);
        
        if (beginIndex <= 0 || endIndex <= 0)
        {
            return;
        }
        
        String picURL = text.substring(beginIndex, endIndex).replaceAll("\\s", "%20");
        print(tagName + "圖片網址:" + picURL);
        Common.simpleDownloadFile(picURL, getBaseOutputDirectory(), picName, "", webSite );
    }
    
    private void handleTitleComment(String tagName, String text)
    {
        int beginIndex = text.indexOf("token32:");
        if (beginIndex < 0)
        {
            return;
        }
        beginIndex = text.indexOf("'", beginIndex) + 1;
        int endIndex = text.indexOf("'", beginIndex);
        
        if (beginIndex <= 0 || endIndex <= 0)
        {
            return;
        }
        
        String token32 = text.substring(beginIndex, endIndex );
        String commentURL = Common.getRegularURL( "http://t.178.com/resource/show?token32=" + token32 );
        print(tagName + "'s commentURL : " + commentURL);
        
        // 取得評論頁數
        text = getAllPageString(commentURL);
        int pageCount = 1;
        beginIndex = text.lastIndexOf("<li><a href=") - 5;
        if (beginIndex > 0)
        {
            beginIndex = text.lastIndexOf("<li><a href=", beginIndex);
            beginIndex = text.indexOf("page=", beginIndex);
            beginIndex = text.indexOf(">", beginIndex) + 1;
            endIndex = text.indexOf("<", beginIndex);
            pageCount = Integer.parseInt(text.substring(beginIndex, endIndex));
        }
        
        List<String> nameList = new ArrayList<String>(); 
        List<String> dateList = new ArrayList<String>(); 
        List<String> commentList = new ArrayList<String>(); 
        String temp = "";
        
        // 下載全部評論
        for (int i = 1; i <= pageCount; i ++)
        {
            text = getAllPageString(commentURL + "&page=" + i);
            beginIndex = endIndex = 0;
            while (true)
            {
                beginIndex = text.indexOf("post-by hovercard", beginIndex);
                if (beginIndex < 0)
                    break;
                
                // 取得評論的名字
                beginIndex = text.indexOf(">", beginIndex) + 1;
                endIndex = text.indexOf("<", beginIndex);
                temp = text.substring(beginIndex, endIndex ).trim();
                nameList.add(temp);
                
                // 取得評論內容
                beginIndex = text.indexOf("-->", beginIndex);
                beginIndex = text.indexOf(">", beginIndex) + 1;
                endIndex = text.indexOf("<", beginIndex);
                temp = text.substring(beginIndex, endIndex ).trim();
                commentList.add(temp);
                
                // 取得評論當下時間
                beginIndex = text.indexOf("<a href=", beginIndex);
                beginIndex = text.indexOf(">", beginIndex) + 1;
                endIndex = text.indexOf("<", beginIndex);
                temp = text.substring(beginIndex, endIndex ).trim();
                temp = Common.getTraditionalChinese(temp);
                temp = getFormatDate(temp);
                dateList.add(temp);
            }
            
        }
        
        
        // 寫出評論        
        List<List<String>> combinationList = new ArrayList<List<String>>();
        combinationList.add(nameList);
        combinationList.add(commentList);
        combinationList.add(dateList);
     
        String filePath = getBaseOutputDirectory() + tagName + Common.getSlash();
        outputListFile(combinationList, "TITLE_COMMONET", filePath, "comment.js");

    }
    
    private String getFormatDate(String text)
    {
        SimpleDateFormat nowdate = new java.text.SimpleDateFormat("yyyy-MM-dd-"); 
        String today = nowdate.format(new java.util.Date());
        String date = "";
        String temp = "";
        
        if (text.indexOf("小時前") > 0) // 今天
        {
            temp = text.split("小時")[0];
            date = today + temp + "-00";
        }
        else if (text.indexOf("年") > 0) // 不是今年 ex. 2012年01月27日 10:34 
        {
            text = text.replaceAll("\\s", "");
            date = text.replaceAll("年|月|日|:", "-");
        }
        else // 今年 ex. 10月10日 18:46 
        {
            temp = today.split("-")[0];
            text = text.replaceAll("\\s", "");
            date = temp + "-" + text.replaceAll("月|日|:", "-");
        }
        //print("格式化日期:" + date);
        return date;
    }
    
    private String getTitleIntroduction(String text)
    {
        int beginIndex = text.indexOf( "line_height_content" );
        if (beginIndex < 0)
            return "";
        beginIndex = text.indexOf( "，", beginIndex ) + 1;
        int endIndex = text.indexOf( "<br/>", beginIndex );
        
        String temp = text.substring(beginIndex, endIndex).trim();
        
        temp = temp.replaceAll("<span.+</span>", "");
        temp = temp.replaceAll("<\\!--.+-->", "");

        return getOutputText(temp);
    }
    
    private void handleAllUpdatePage()
    {
        for (int i = 0; i < 10; i ++)
        {
            String listURL = "http://manhua.dmzj.com/update_" + (i+1) + ".shtml";
            List<String> tagNameList = getTagNameList(listURL);
            for (int j = 0; j < tagNameList.size(); j ++)
            {
                String tagName = tagNameList.get(j);
                handleSingleTitle(tagName);
            }
        }
    }
    
    private void handleAllRankPage()
    {
        String[] urlList = new String[] { 
            "http://manhua.dmzj.com/rank/total-list-", 
            "http://manhua.dmzj.com/rank/yiwanjie/total-list-",
            "http://manhua.dmzj.com/rank/gaoxiao/total-list-", 
            "http://manhua.dmzj.com/rank/shaonian/total-list-" };
        
        for (int k = 0; k < urlList.length; k++ )
        {
            String baseListURL = urlList[k];
            for (int i = 0; i < 5; i ++)
            {
                String listURL = baseListURL + (i+1) + ".shtml";
                List<String> tagNameList = getTagNameList(listURL);
                for (int j = 0; j < tagNameList.size(); j ++)
                {
                    String tagName = tagNameList.get(j);
                    handleSingleTitle(tagName);
                }
            }
        }
    }
    
    private String getPreviousVolumeID(String text)
    {
        int beginIndex = 0;
        int endIndex = 0;
        
        beginIndex = text.indexOf("prev_chapter");
        
        if (beginIndex < 0)
            return null;
        
        beginIndex = text.indexOf("href", beginIndex);
        beginIndex = text.indexOf("\"", beginIndex) + 1;
        endIndex = text.indexOf(".shtml", beginIndex);
        return text.substring(beginIndex, endIndex);
    }
    
    private String handleSingleVolume(
            String tagName, String titleName, String titleIntroduction, String volumeTitle, String text)
    {
        int beginIndex = 0;
        int endIndex = 0;

        beginIndex = text.indexOf("sns_sys_id");
        beginIndex = text.indexOf("'", beginIndex) + 1;
        endIndex = text.indexOf("'", beginIndex);
        //snsSysIDList.add(text.substring(beginIndex, endIndex));
        String snsSysID = text.substring(beginIndex, endIndex);

        beginIndex = text.indexOf("sns_view_point_token");
        beginIndex = text.indexOf("'", beginIndex) + 1;
        endIndex = text.indexOf("'", beginIndex);
        //snsViewPointTokenList.add(text.substring(beginIndex, endIndex));      
        String snsViewPointToken = text.substring(beginIndex, endIndex);

        //print("" + j + ": "+snsSysIDList.get(j) + "," + snsViewPointTokenList.get(j));

        String commentURL = getCommentURL(snsSysID, snsViewPointToken);
        String commentMoreURL = getCommentMoreURL(snsSysID);
        List<String> commentList = new ArrayList<String>(); 
        commentList = getCommentParseText(commentList, commentURL);
        commentList = getCommentParseText(commentList, commentMoreURL);

        outputVolumeComment(tagName, volumeTitle, getVolumeID(snsSysID), "DMZJ_COMMENT", commentList);

        updateIndexFile(tagName, titleName, titleIntroduction, volumeTitle, snsSysID);
        //System.exit(0);
        
        return snsSysID;
    }
    
    private String getLastVolumeID(String titleText)
    {
        int beginIndex = 0;
        int endIndex = 0;
        beginIndex = titleText.indexOf("g_last_chapter_id");
        beginIndex = titleText.indexOf("\"", beginIndex) + 1;
        endIndex = titleText.indexOf("\"", beginIndex);
        return titleText.substring(beginIndex, endIndex);
    }
    
    private String getLastVolumeTitle(String titleText)
    {
        int beginIndex = 0;
        int endIndex = 0;
        beginIndex = titleText.indexOf("g_last_update");
        beginIndex = titleText.indexOf("\"", beginIndex) + 1;
        endIndex = titleText.indexOf("\"", beginIndex);
        return titleText.substring(beginIndex, endIndex);
    }
    
    private void handleSingleTitle(String tagName)
    {
        int beginIndex = 0;
        int endIndex = 0;

        String titleURL = Common.getRegularURL( baseURL + "/" + tagName );
        String titleText = getAllPageString(titleURL);

        // 取得標題列表和網址列表
        tsukkomiMode = false;
        String titleName = getTitleOnMainPage( titleURL, titleText );
        
        if (titleName == null)
        {
            print("無效的作品主頁網址: " + titleURL);
            return;
        }
        
        List<List<String>> combinationList = getVolumeTitleAndUrlOnMainPage(titleURL, titleText);
        List<String> volumeTitleList = combinationList.get(0);
        List<String> volumeUrlList = combinationList.get(1);
        tsukkomiMode = true;
        
        //List<String> snsSysIDList = new ArrayList<String>(); // sns_sys_id
        //List<String> snsViewPointTokenList = new ArrayList<String>(); // sns_view_point_token
        String snsSysID = "";
        String snsViewPointToken = "";
        boolean stepByStepMode = false;
        String lastVolumeID = getLastVolumeID(titleText);
        String lastVolumeTitle = getLastVolumeTitle(titleText);
        
        // 用於main_list.js
        newTitleList.add(titleName);
        newTagList.add(tagName);
        newVolumeTitleList.add(lastVolumeTitle);
        newVolumeDirList.add(lastVolumeID);
        

        //  如果不需要更新，就跳過往下個去做
        if (!needUpdate(tagName, volumeTitleList))
        {
            int lastIndex = volumeTitleList.size() - 1;
            if (lastIndex < 0 && titleText.indexOf("g_last_chapter_id") > 0)
            {
                print("集數列表因為版權而拿掉 , 需要一集一集慢慢爬");
                stepByStepMode = true;
            }
            else if (lastIndex < 0)
            {
                print("跳過 , 因為 " + titleName + "[" + tagName + 
                  "] 沒有任何集數 ");
                return;
            }
            else
            {
                print("跳過 , 因為 " + titleName + "[" + tagName + 
                  "] 已有最新集數: " + volumeTitleList.get(lastIndex));
                return;
            }
        }
        
        String titleIntroduction = getTitleIntroduction(titleText);
        handleTitlePic(tagName, titleText);
        
        if (!stepByStepMode || !new File(getBaseOutputDirectory() + tagName + Common.getSlash() + "comment.js").exists())
        {
            handleTitleComment(tagName, titleText);
        }
        
        int volumeCount = volumeUrlList.size();
        int existedVolumeCount = getExistedVolumeCount(tagName);
        
        //print("" + tagName + ":" + volumeCount + "," + existedVolumeCount);
        //System.exit(0);

        // 取得每個集數的評論列表
        for (int j = existedVolumeCount; j < volumeCount && !stepByStepMode; j ++) // 某個作品的集數列表
        {
            String volumeURL = volumeUrlList.get(j);
            String volumeText = getAllPageString(volumeURL);
            String volumeTitle = volumeTitleList.get(j);
            snsSysID = handleSingleVolume(tagName, titleName, titleIntroduction, volumeTitle, volumeText);
        }
        
        String nowVolumeID = lastVolumeID;
        while (stepByStepMode)
        {
            
            String volumeURL = baseURL + "/" + tagName + "/" + nowVolumeID + ".shtml";
            String volumeText = getAllPageString(volumeURL);
            
            beginIndex = volumeText.indexOf("g_chapter_name");
            beginIndex = volumeText.indexOf("\"", beginIndex) + 1;
            endIndex = volumeText.indexOf("\"", beginIndex);
            String volumeTitle = volumeText.substring(beginIndex, endIndex);
            print("正要處理的集數: " + volumeTitle + " : " + volumeURL);
            snsSysID = handleSingleVolume(tagName, titleName, titleIntroduction, volumeTitle, volumeText);
            
            nowVolumeID = getPreviousVolumeID(volumeText);
            
            if (nowVolumeID == null)
                break;
        }
        
        //outputMainListFile(stepByStepMode);
    }
    
    private void initNewData()
    {
        newTitleList.clear();
        newTagList.clear();
        newVolumeTitleList.clear();
        newVolumeDirList.clear();
    }
    
    private void buildIndexFile(String tagName)
    {
        String dirPath = getBaseOutputDirectory() + tagName + Common.getSlash();
        String path = "";
        String text = "";
        List<String> volumeTagList = new ArrayList<String>(); 
        File dir = new File(dirPath); //你的log檔路徑
        File fileList[]= dir.listFiles(); //得出檔案清單
        String volumeTitle = "";
        
        // 取得代號清單
        for (int i=0; i<fileList.length; i++) {
            if (fileList[i].isFile()) { //過濾檔案
                String[] temps = fileList[i].toString().split("\\\\");
                String volumeTag = temps[temps.length-1].split("\\.")[0];
                if (!volumeTag.matches("comment"))
                    volumeTagList.add(volumeTag);
                //print(i + " TAG : " + volumeTag);
            }
        }
        
        for (int i = 0; i < volumeTagList.size(); i ++)
        {
            path = dirPath + volumeTagList.get(i) + ".js";
            text = Common.getFileString(path);
            
            if (text.split("'").length <= 1)
                continue;
            
            volumeTitle = text.split("'")[1];
            
            
        }
        
        System.exit(0);
    }

    private void outputMainListFile(boolean stepByStepMode)
    {
        List<String> tagList = new ArrayList<String>(); 
        List<String> nameList = new ArrayList<String>(); 
        List<String> lastVolumeTitleList = new ArrayList<String>(); 
        List<String> lastVolumeIDList = new ArrayList<String>(); 
        String name = "";
        String path = "";
        String text = "";
        File dir = new File(getBaseOutputDirectory()); //你的log檔路徑
        File fileList[]= dir.listFiles(); //得出檔案清單
        
        // 取得代號清單
        for (int i=0; i<fileList.length; i++) {
            if (fileList[i].isDirectory()) { //過濾檔案
                String[] temps = fileList[i].toString().split("\\\\");
                name = temps[temps.length-1];
                tagList.add(name);
                //print(i + " TAG : " + name);
            }
        }
        
        // 取得名稱清單
        for (int i = 0; i < tagList.size(); i ++)
        {
            path = getBaseOutputDirectory() + tagList.get(i) + ".js";
            text = Common.getFileString(path);

            //print("------------" + text + "------------end");
            String[] temps = text.split("'");
            
            if (temps.length <= 1)
            {
                //buildIndexFile(tagList.get(i));
                continue;
            }
            
            name = temps[1]; // 取第一個''資料字串
            nameList.add(name);
            
            //print(i + " NAME : " + name);
            
            int beginIndex = 0;
            int endIndex = 0;
            String temp = "";
            
            if (stepByStepMode) // 新的放後面
            {
                beginIndex = text.indexOf(");", beginIndex) - 2;
                endIndex = text.lastIndexOf("'", beginIndex);
                beginIndex = text.lastIndexOf("'", endIndex - 2) + 1;
                temp = text.substring(beginIndex, endIndex);
                lastVolumeTitleList.add(temp);
                
                print("文件中最新一集: " + temp);

                beginIndex = text.indexOf(");", endIndex + 1) + 1;
                endIndex = text.lastIndexOf("'", beginIndex);
                beginIndex = text.lastIndexOf("'", endIndex - 2) + 1;
                temp = text.substring(beginIndex, endIndex);
                lastVolumeIDList.add(temp);
            }
            else // 新的放前面
            {
                beginIndex = text.indexOf("new Array(", beginIndex);
                beginIndex = text.indexOf("'", beginIndex) + 1;
                endIndex = text.indexOf("'", beginIndex);
                temp = text.substring(beginIndex, endIndex);
                lastVolumeTitleList.add(temp);

                beginIndex = text.indexOf("'", endIndex + 1) + 1;
                endIndex = text.indexOf("'", beginIndex);
                temp = text.substring(beginIndex, endIndex);
                lastVolumeIDList.add(temp);
            }
            
            
            //print(i + " VOLUME : " + temp);
        }
        
        List<List<String>> combinationList = new ArrayList<List<String>>();
        combinationList.add(nameList);
        combinationList.add(tagList);
        combinationList.add(lastVolumeTitleList);
        combinationList.add(lastVolumeIDList);
        outputListFile(combinationList, "MAIN_LIST", mainListFileName);
    }
    
    private void outputListFile(List<List<String>> combinationList, String variableName, String fileName)
    {
        outputListFile(combinationList, variableName, getBaseOutputDirectory(), fileName);
    }
    
    private String removeNewline(String text)
    {
        return text.replaceAll("\\r|\\n", " ");
    }
    
    private void outputListFile(List<List<String>> combinationList, String variableName, String filePath, String fileName)
    {
        String text = variableName + " = new Array( ";
        String temp = "";
        int count = combinationList.get(0).size();
        for (int i = 0; i < count; i ++)
        {
            if (i > 0 )
                text += ", ";
            
            for (int j = 0; j < combinationList.size(); j ++)
            {
                temp = combinationList.get(j).get(i);
                text += "'" + getOutputText(temp) + "'";
                
                if (j < combinationList.size() - 1)
                    text += ", ";
            }
        }
        text += "\n);";
        
        Common.outputFile(text, filePath, fileName);
        print("已經寫出列表 : " + fileName);
    }
    
    private void outputNewListFile(String url)
    {
        String text = getAllPageString(url);
        String[] temps = text.split("description>");
        int beginIndex = 0;
        int endIndex = 0;
        String temp = "";
        List<String> tagList = new ArrayList<String>(); 
        List<String> nameList = new ArrayList<String>(); 
        List<String> lastVolumeTitleList = new ArrayList<String>(); 
        List<String> lastVolumeIDList = new ArrayList<String>(); 
        
        for (int i = 0; i < temps.length; i ++)
        {
            if (temps[i].indexOf("title=") < 0)
                continue;
            
            // 取得漫畫名稱
            beginIndex = temps[i].indexOf("title=");
            beginIndex = temps[i].indexOf("'", beginIndex) + 1;
            endIndex = temps[i].indexOf("'", beginIndex);
            temp = temps[i].substring(beginIndex, endIndex );
            nameList.add(temp);

            // 取得最新集數名稱
            beginIndex = temps[i].indexOf(">", beginIndex) + 1;
            endIndex = temps[i].indexOf("<", beginIndex);
            temp = temps[i].substring(beginIndex, endIndex );
            lastVolumeTitleList.add(temp);

            // 取得漫畫代號
            beginIndex = temps[i].indexOf("com/", beginIndex);
            beginIndex = temps[i].indexOf("/", beginIndex) + 1;
            endIndex = temps[i].indexOf("/", beginIndex);
            temp = temps[i].substring(beginIndex, endIndex );
            tagList.add(temp);
            
            // 取得最新集數ID
            beginIndex = temps[i].indexOf("chapterid=", beginIndex);
            beginIndex = temps[i].indexOf("=", beginIndex) + 1;
            endIndex = temps[i].indexOf("'", beginIndex);
            temp = temps[i].substring(beginIndex, endIndex );
            lastVolumeIDList.add(temp);
        }
        
        List<List<String>> combinationList = new ArrayList<List<String>>();
        combinationList.add(nameList);
        combinationList.add(tagList);
        combinationList.add(lastVolumeTitleList);
        combinationList.add(lastVolumeIDList);
        
        outputListFile(combinationList, "NEW_LIST", newListFileName);
    }

    @Override
    public void parseComicURL()
    { // parse URL and save all URLs in comicURL  //
        // 先取得前面的下載伺服器網址
        
        initNewData();

        String allPageString = Common.getFileString( SetUp.getTempDirectory(), indexName );
        Common.debugPrint( "開始解析這一集有幾頁 : " );
        
        if (tsukkomiMode)
        {
            int beginIndex = 0;
            int endIndex = 0;
            String listURL = webSite;
            List<String> tagNameList = new ArrayList<String>();
            
            if (webSite.matches(".*/"))
            {
                listURL = webSite.substring(0, webSite.length() - 1);
            }
            
            if (isRssPage()) 
            {
                print("is RSS page : " + listURL);
                outputNewListFile(listURL);
                tagNameList = getTagNameList(listURL);
                
                for (int i = 0; i < tagNameList.size(); i ++ ) // 作品列表
                {
                    String tagName = tagNameList.get(i);
                    handleSingleTitle(tagName);
                }
            }
            else // ex. http://manhua.dmzj.com/tags/category_search/0-0-0-all-0-0-1-447.shtml#category_nav_anchor
            {
                print("is Normal List Page : " + webSite);
                
                if (webSite.indexOf("/update_") > 0)
                {
                    handleAllUpdatePage();
                }
                else if (webSite.indexOf("/rank/") > 0)
                {
                    handleAllRankPage();
                }
            }
            
            System.exit(0);
        }
        
        
        // 取得所有位址編碼代號
        String token = isMobilePage() ? "[" : "'[";
        int beginIndex = allPageString.indexOf( token ) + 2;
        int endIndex = allPageString.indexOf( "\"]", beginIndex ) + 1;

        String allCodeString = allPageString.substring( beginIndex, endIndex );

        totalPage = allCodeString.split( "\",\"" ).length;
        Common.debugPrintln( "共 " + totalPage + " 頁" );
        comicURL = new String[ totalPage ];
        refers = new String[ totalPage ];
        
        String basePicURL = "";
        String parentPicURL = "";
        String[] codeTokens = allCodeString.split( "\",\"" );

        if (!isMobilePage())
        {
        
            // 取得位址編碼代號的替換字元
            beginIndex = allPageString.indexOf( ",'", endIndex ) + 2;
            endIndex = allPageString.indexOf( "'.", beginIndex );
            String allVarString = allPageString.substring( beginIndex, endIndex );

            String[] varTokens = allVarString.split( "\\|" );

            for ( int i = 0; i < varTokens.length; i++ )
            {
                Common.debugPrintln( i + " " + varTokens[i] ); // test
            }
            basePicURL = "http://images.dmzj.com/";//"http://images.manhua.178.com/";
            
            codeTokens = getRealCodeTokens( codeTokens, varTokens );
            String firstCode = codeTokens[0].replaceAll( "\"", "" );
            
            String firstPicURL = "";
            Common.debugPrintln( "第一張編碼：" + firstCode );
            firstPicURL = basePicURL + Common.getFixedChineseURL( getDecodeURL( firstCode ) );
            firstPicURL = firstPicURL.replaceAll( "\\\\", "" );

            Common.debugPrintln( "第一張圖片網址：" + firstPicURL );
            
            endIndex = firstPicURL.lastIndexOf( "/" ) + 1;
            parentPicURL = firstPicURL.substring( 0, endIndex );
        }
        
        String[] picNames = new String[ totalPage ];
        for ( int i = 0; i < picNames.length; i++ )
        {
            codeTokens[i] = codeTokens[i].replaceAll( "\"", "" );
            beginIndex = codeTokens[i].lastIndexOf( "/" ) + 1;
            endIndex = codeTokens[i].length(); //.lastIndexOf( "\"" );
            String temp = isMobilePage() ? codeTokens[i] : codeTokens[i].substring( beginIndex, endIndex );
            //Common.debugPrintln( codeTokens[i] + " " + beginIndex + " " + endIndex );
            picNames[i] = Common.getFixedChineseURL(getDecodeURL( temp ));

            //System.exit( 0 ); // debug
        }

        

        for ( int i = 0; i < codeTokens.length && Run.isAlive; i++ )
        {
            comicURL[i] = parentPicURL + picNames[i]; // 存入每一頁的網頁網址
            comicURL[i] = comicURL[i].replaceAll("\\\\", "");
            refers[i] = webSite;
            Common.debugPrintln( ( i + 1 ) + " " + comicURL[i]  ); // debug

        }

        //System.exit( 0 ); // debug
    }

    private int getVarIndex( char code )
    {
        int index = -1;

        if ( code >= '0' && code <= '9' )
        {
            index = Integer.valueOf( String.valueOf( code ) );
        }
        else if ( code >= 'a' && code <= 'z' )
        {
            index = 10 + (code - 'a');
        }
        else if ( code >= 'A' && code <= 'Z' )
        {
            index = 10 + 26 + (code - 'A');
        }

        return index;
    }

    // 將代號轉為實際字串
    private String[] getRealCodeTokens( String[] codeTokens, String[] varTokens )
    {
        String[] realCodeTokens = new String[ codeTokens.length ];

        String tempChar = "";

        for ( int i = 0; i < codeTokens.length; i++ )
        {
            realCodeTokens[i] = "";
            Common.debugPrintln( "這次要分解的code : " + codeTokens[i] );

            for ( int j = codeTokens[i].length() - 1; j >= 0; j-- )
            {
                int index = -1;
                // 兩個數字字元組合在一起

                index = getVarIndex( codeTokens[i].charAt( j ) );

                if ( j > 0 && index >= 0 )
                {
                    char c = codeTokens[i].charAt( j - 1 );

                    if ( c >= '1' && c <= '9' )
                    {
                        int num = Integer.parseInt( String.valueOf( c ) );
                        
                        index += ((26 + 26 + 10) * num);
                        
                        // 若之後找不到此index對應的token , 可直接用此數字字串
                        tempChar = "" + codeTokens[i].charAt( j );
                        
                         j--;
                    }
                    else
                    {
                        tempChar = "";
                    }
                }
                else
                {
                    tempChar = "";
                }


                if ( index >= 0 && index < varTokens.length && !varTokens[index].equals( "" ) )
                {
                    realCodeTokens[i] = varTokens[index] + realCodeTokens[i];
                }
                else
                {
                    realCodeTokens[i] = "" + codeTokens[i].charAt( j ) + tempChar + realCodeTokens[i];
                }
                //Common.debugPrintln( realCodeTokens[i] );

            }
            Common.debugPrintln( "分解結果: " + realCodeTokens[i] );


        }
        //System.exit( 0 );

        return realCodeTokens;
    }

    // 回傳utf8編碼的十進位數字
    // ex. 0030 -> 48
    private char getUtf8Char( String code )
    {
        int number = 0;

        // 十六位元數字的字串
        String hexString = code.substring( 2, code.length() ); // 去對前面的\\u

        return ( char ) Integer.parseInt( hexString, 16 );
    }

    // 解析網址 
    // ex. 將t\/THREAD\/\u7b2c01\u8bdd\/001.jpg解析為正常網址
    private String getDecodeURL( String code )
    {

        int beginIndex = 0;
        int endIndex = 0;

        char ch = ' ';
        String singleCode = ""; // 單一字的編碼

        while ( true )
        {
            beginIndex = code.indexOf( "\\u" );
            if ( beginIndex < 0 )
            {
                break; // 已找不到需要解碼的部份，跳出迴圈
            }
            else
            {
                endIndex = beginIndex + 6;
                singleCode = code.substring( beginIndex, endIndex );
                ch = getUtf8Char( singleCode ); // 取得utf8原始字

                code = code.replace( singleCode, String.valueOf( ch ) ); // 替代該字
                System.out.println( beginIndex + " " + endIndex + "\t: " + code );
            }
        }

        return code;
    }

    public void showParameters()
    { // for debug
        Common.debugPrintln( "----------" );
        Common.debugPrintln( "totalPage = " + totalPage );
        Common.debugPrintln( "webSite = " + webSite );
        Common.debugPrintln( "----------" );
    }

    @Override
    public String getAllPageString( String urlString )
    {
        String indexName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_178_", "html" );
        Common.downloadGZIPInputStreamFile( urlString, SetUp.getTempDirectory(), indexName, false, "" );

        return Common.getFileString( SetUp.getTempDirectory(), indexName );
    }
    
    private boolean needTsukkomiMode( String url)
    {
        if (url.indexOf("/update") > 0 || 
            url.indexOf("rss.xml") > 0 ||
            url.indexOf("/tags/") > 0 ||
            url.indexOf("/rank/") > 0)
            return true;
        
        return false;
    }

    @Override
    public boolean isSingleVolumePage( String urlString )
    {
        // ex. http://www.kkkmh.com/manhua/0804/9119/65867.html
        tsukkomiMode = false;
        if (needTsukkomiMode(urlString))
        {
            tsukkomiMode = true;
            
            return Flag.allowDownloadFlag;
        }
        
        if (urlString.matches( "(?s).*\\d.shtml" ) || 
            urlString.matches( "(?s).*\\d.html" ))
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
        // ex. http://www.178.com/mh/kongjuzhiyuan/16381-2.shtml轉為
        //    http://manhua.178.com/kongjuzhiyuan/

        String allPageString = getAllPageString( volumeURL );

        int beginIndex = allPageString.indexOf( "g_comic_url" );
        beginIndex = allPageString.indexOf( "\"", beginIndex ) + 1;
        int endIndex = allPageString.indexOf( "\"", beginIndex );
        String mainPageURL = baseURL + "/" + allPageString.substring( beginIndex, endIndex );

        Common.debugPrintln( "MAIN_URL: " + mainPageURL );

        return mainPageURL;
    }

    @Override
    public String getTitleOnSingleVolumePage( String urlString )
    {
        String mainUrlString = getMainUrlFromSingleVolumeUrl( urlString );

        return getTitleOnMainPage( mainUrlString, getAllPageString( mainUrlString ) );
    }

    @Override
    public String getTitleOnMainPage( String urlString, String allPageString )
    {
        if (needTsukkomiMode(urlString))
        {
            tsukkomiMode = true;
            return "Tsukkomi";
        }
        
        tsukkomiMode = false;
        
        int beginIndex = allPageString.indexOf( "<h1>" ) + 4;
        int endIndex = allPageString.indexOf( "</h1>", beginIndex );
        if (urlString.indexOf("mh.") > 0)
        {
            beginIndex = allPageString.indexOf( "g_comic_name" );
            beginIndex = allPageString.indexOf( "\"", beginIndex ) + 1;
            endIndex = allPageString.indexOf( "\"", beginIndex );
        }
        else if (isMobilePage(urlString))
        {
            beginIndex = allPageString.indexOf( "id=\"comicName\"" );
            beginIndex = allPageString.indexOf( ">", beginIndex ) + 1;
            endIndex = allPageString.indexOf( "<", beginIndex );
        }
        
        // Common.debugPrintln( "B: " + beginIndex + "  E: " + endIndex );
        
        if (beginIndex < 0 || endIndex < 0)
        {
            return null;
        }
        
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
        
        if (tsukkomiMode)
        {
            urlList.add( urlString );
            volumeList.add( "tsukkomi" );
            combinationList.add( volumeList );
            combinationList.add( urlList );

            return combinationList;
        }

        int beginIndex = allPageString.indexOf( "class=\"cartoon_online_border\"" );
        int endIndex = allPageString.indexOf( "document.write", beginIndex );

        if (urlString.indexOf("mh.") > 0)
        {
            beginIndex = allPageString.indexOf( "chapter_list" );
            endIndex = allPageString.indexOf( "</script>", beginIndex );
        }
        else if (isMobilePage(urlString))
        {
            beginIndex = allPageString.indexOf( "initIntroData" );
            endIndex = allPageString.indexOf( " openApp()", beginIndex );
        }

        String tempString = allPageString.substring( beginIndex, endIndex );
        int volumeCount = 0;
        String volumeTitle = "";
        beginIndex = endIndex = 0;
       
        if (isMobilePage(urlString))
        {
            String[] tokens = getUtf8Text(tempString).split(":|,");
            String id = "";
            String comic_id = "";
            String chapter_name = "";
            String base = "http://m.dmzj.com/view/";
            
            for (int i = 0; i < tokens.length; i ++)
            {
                if (tokens[i].indexOf("\"id\"") >= 0)
                {
                    id = tokens[i+1];
                }
                else if (tokens[i].indexOf("\"comic_id\"") >= 0)
                {
                    comic_id = tokens[i+1];
                }
                else if (tokens[i].indexOf("\"chapter_name\"") >= 0)
                {
                    chapter_name = tokens[i+1].replaceAll("\"", "");
                    
                    // http://m.dmzj.com/view/10073/38926.html
                    urlList.add( base + comic_id + "/" + id + ".html" );
                    volumeList.add( getVolumeWithFormatNumber( Common.getStringRemovedIllegalChar(
                        Common.getTraditionalChinese( chapter_name ) ) ) );
                    
                    volumeCount++;
                }
                
            }
        }
        else
        {
             volumeCount = tempString.split( "href=\"" ).length - 1;
            
            for ( int i = 0; i < volumeCount; i++ )
            {
                // 取得單集位址
                beginIndex = tempString.indexOf( "href=\"", beginIndex ) + 6;
                endIndex = tempString.indexOf( "\"", beginIndex );
                urlList.add( baseURL + tempString.substring( beginIndex, endIndex ) );

                // 取得單集名稱
                beginIndex = tempString.indexOf( ">", beginIndex ) + 1;
                endIndex = tempString.indexOf( "<", beginIndex );
                volumeTitle = tempString.substring( beginIndex, endIndex );

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
    
    // ex. http://m.dmzj.com/info/gsmmx21.html
    private boolean isMobilePage(String url)
    {
        return url.indexOf("m.dmzj.com") > 0;
    }
    
    private boolean isMobilePage()
    {
        return isMobilePage(webSite);
    }

    @Override
    public void outputVolumeAndUrlList( List<String> volumeList, List<String> urlList )
    {
        Common.outputFile( volumeList, SetUp.getTempDirectory(), Common.tempVolumeFileName );
        Common.outputFile( urlList, SetUp.getTempDirectory(), Common.tempUrlFileName );
    }

    @Override
    public String[] getTempFileNames()
    {
        return new String[]
                {
                    indexName, indexEncodeName, jsName
                };
    }
}
