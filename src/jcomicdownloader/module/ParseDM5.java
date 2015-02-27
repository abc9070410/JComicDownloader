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

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import jcomicdownloader.tools.*;
import jcomicdownloader.enums.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import jcomicdownloader.ComicDownGUI;
import jcomicdownloader.Flag;
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
    
    private String getBaseOutputDirectory()
    {
        return Common.getNowAbsolutePath() + 
            "down" + Common.getSlash() + 
            "SVN" + Common.getSlash() + 
            "TsukkomiMD5_1" + Common.getSlash();
    }
    
    private List<String> getTagNameList(String allPageString)
    {
        int beginIndex = 0;
        int endIndex = 0;
        int index = 0;
        List<String> tagNameList = new ArrayList<String>();
        String temp = "";
        
        String[] tokens = allPageString.split("/manhua-");
        
        for (int i = 1; i < tokens.length; i ++)
        {
            temp = tokens[i];
            if (temp.indexOf("title=") < 0 ||
                temp.indexOf("alt=") < 0 ||
                temp.indexOf("img src") < 0)
                continue;
            
            endIndex = temp.indexOf("/");
            temp = temp.substring(0, endIndex);
            
            if (tagNameList.contains(temp))
                continue;

            //print("" + i + " : " + temp + " : " + tokens[i]);
            tagNameList.add(temp);
        }

        return tagNameList;
    }
        
    public void print(String message) 
    {
        Common.debugPrintln(message);
    }
    
    
    
    private String handleTitlePic(String text, String tagName)
    {
        String picName = tagName + ".jpg";
        int beginIndex = text.indexOf("class=\"innr91\"");
        if (beginIndex < 0)
            return "";
        beginIndex = text.indexOf("src=", beginIndex);
        beginIndex = text.indexOf("\"", beginIndex) + 1;
        int endIndex = text.indexOf("\"", beginIndex);

        String picURL = text.substring(beginIndex, endIndex).replaceAll("\\s", "%20");
        print(tagName + "圖片網址:" + picURL);
        
        if (!new File(getBaseOutputDirectory() + picName).exists())
        {
            Common.simpleDownloadFile(picURL, getBaseOutputDirectory(), picName, "", webSite );      
        }
        
        return picURL;
    }
    
    private String getTitleIntroduction(String text)
    {
        int beginIndex = text.indexOf("</h3></b>");
        if (beginIndex < 0)
            return "";
        beginIndex = text.indexOf("/>", beginIndex) + 2;
        int endIndex = text.indexOf("</div>", beginIndex);
        if (beginIndex < 0 || endIndex < 0)
            return "";
        
        String introduction = text.substring(beginIndex, endIndex);
       
        int midIndex = introduction.indexOf("<a href=\"###") - 1;
        
        
        //introduction = introduction.replaceAll("<.+>", "");
        introduction = Common.replaceTag(introduction);
        introduction = introduction.replaceAll("\\[\\+展开\\]|\\[\\-折叠\\]", "");
        
        if (midIndex > 0)  // 有展開行為，所以需要把重複的字拿掉
        {
            introduction = introduction.substring(0, midIndex) + 
                           introduction.substring(midIndex + 1, introduction.length());
        }
        
        //print("  [" + introduction + "] ");
        
        return introduction;
    }
    
    private int getCommentPageCount(String text)
    {
        
        int beginIndex = text.lastIndexOf("/#topic") - 3;
        if (beginIndex < 0)
            return 1;
        beginIndex = text.lastIndexOf("/#topic", beginIndex);
        beginIndex = text.indexOf(">", beginIndex) + 1;
        int endIndex = text.indexOf("<", beginIndex);
        
        return Integer.parseInt(text.substring(beginIndex, endIndex));
    }
    
    private List<String> getCommentUrlList(String allPageString)
    {
        int beginIndex = 0;
        int endIndex = 0;
        int index = 0;
        List<String> commentUrlList = new ArrayList<String>();
        String temp = "";
        String commentTag = "/tiezi-";
        
        String[] tokens = allPageString.split(commentTag);
        
        for (int i = 1; i < tokens.length; i ++)
        {
            temp = tokens[i];
            endIndex = temp.indexOf("/");
            temp = temp.substring(0, endIndex);
            
            temp = baseURL + commentTag + temp;
            
            if (temp.matches(".+-p\\d+.*"))
                continue;
            
            if (commentUrlList.contains(temp))
                continue;
            
            

            //print("" + i + " : " + temp );// + " : " + tokens[i]);
            commentUrlList.add(temp);
        }
        //System.exit(0);

        return commentUrlList;
    }
    
    private String getCommentTitle(String text)
    {
        int beginIndex = text.indexOf("<title>");
        if (beginIndex < 0)
            return "";
        beginIndex = text.indexOf(">", beginIndex) + 1;
        int endIndex = text.indexOf(" _", beginIndex);
        
        return text.substring(beginIndex, endIndex).trim();
    }
    
    private int getCommentPostPageCount(String text)
    {
        int beginIndex = text.lastIndexOf("-p2/") - 3;
        if (beginIndex < 0)
            return 1;
        beginIndex = text.lastIndexOf("-p", beginIndex);
        beginIndex = text.indexOf(">", beginIndex) + 1;
        int endIndex = text.indexOf("<", beginIndex);
        
        
        
        return Integer.parseInt(text.substring(beginIndex, endIndex).trim());
    }
    
    private List<List<String>> getCommentPostData(String text)
    {
        List<String> authorList = new ArrayList<String>();
        List<String> commentList = new ArrayList<String>();
        List<String> dateList = new ArrayList<String>();
        List<String> voteList = new ArrayList<String>();

        int beginIndex = 0;
        int endIndex = 0;
        
        String[] tokens = text.split("id=\"replyuser");
        String temp = "";
        
        print("此頁有" + tokens.length + "篇評論");
        
        for (int i = 1; i < tokens.length; i ++)
        {
            String token = tokens[i];
            
            // ex. >暗刃</span> 
            beginIndex = token.indexOf(">") + 1;
            endIndex = token.indexOf("</span>", beginIndex);
            temp = token.substring(beginIndex, endIndex).trim();
            authorList.add(temp);
            
            // ex. 发表于 2014-09-25 18:59:57</div>   
            beginIndex = token.indexOf("发表于");
            beginIndex = token.indexOf(" ", beginIndex) + 1;
            endIndex = token.indexOf("</div>", beginIndex);
            endIndex = Common.getSmallerIndexOfTwoKeyword(token, beginIndex, "</div>", "&nbsp;");
            temp = token.substring(beginIndex, endIndex).trim();
            dateList.add(temp);
            
            // ex. "cpl_nrr2">来来去去了将久，结果又会到最初的战斗之地。                </div> 
            beginIndex = token.indexOf("cpl_nrr2");
            beginIndex = token.indexOf(">", beginIndex) + 1;
            endIndex = token.indexOf("</div>", beginIndex);
            temp = token.substring(beginIndex, endIndex).trim();
            commentList.add(temp);
            
            // ex. id="support_1795599">支持[<span class="hui6">32</span>
            //     id="against_1795599">反对[<span class="hui6">3</span
            //    id="btnreport1795599" >举报[<span class="hui6">0</span>
            String[] tempTokens = token.split("\"hui6\"");
            temp = "";
            for (int j = 1; j < tempTokens.length; j ++)
            {
                if (j > 1)
                {
                    temp += "-";
                }
                
                beginIndex = tempTokens[j].indexOf(">") + 1;
                endIndex = tempTokens[j].indexOf("</span>", beginIndex);
                String tempToken = tempTokens[j].substring(beginIndex, endIndex).trim();
                temp += tempToken;
            }
            voteList.add(temp);
            
            if (false)
            {
                print(authorList.get(i-1) + "_" + 
                  commentList.get(i-1) + "_" +
                  dateList.get(i-1) + "_" +
                  voteList.get(i-1));
            }

        }

        List<List<String>> combinationList = new ArrayList<List<String>>();
        combinationList.add(authorList);
        combinationList.add(commentList);
        combinationList.add(dateList);
        combinationList.add(voteList);
        
        //System.exit(0);
        return combinationList;
    }
    
    private List<List<String>> combinationData(List<List<String>> oldList, List<List<String>> newList)
    {
        if (oldList == null)
            return newList;
        
        for (int i = 0; i < oldList.size(); i ++)
        {
            for (int j = 0; j <newList.get(i).size(); j ++)
            {
                oldList.get(i).add(newList.get(i).get(j));
            }
        }
        return oldList;
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
    
    private void outputIndexFile(String tagName, String titleName, String titleIntroduction, String titlePicURL)
    {
        String text = "";
        text += "TITLE_NAME = '" + getOutputText(titleName) + "';\n";
        text += "TITLE_INTRODUCTION = '" + getOutputText(titleIntroduction) + "';\n";
        text += "TITLE_PIC_URL = '" + titlePicURL + "';\n";
        text += "VOLUME_TITLE_LIST = new Array( ";
        text += "\n);\n";
        
        text += "VOLUME_DIR_LIST = new Array( ";
        text += "\n);\n";
        
        String fileName = tagName + ".js";
        String filePath = getBaseOutputDirectory();
        Common.outputFile(text, filePath, fileName);
    }
    
    private boolean aIsBiggerThanB(String a, String b)
    {
        int aNumber = 0;
        int bNumber = 0;
        
        try
        {
            aNumber = Integer.parseInt(a.replaceAll("[^\\d]+", ""));
            print(a + " [a]轉化為數字:" + aNumber);
        }
        catch(Exception e)
        {
            aNumber = -1;
        }
        
        try
        {
            bNumber = Integer.parseInt(b.replaceAll("[^\\d]+", ""));
            print(b + " [b]轉化為數字:" + bNumber);
        }
        catch(Exception e)
        {
            bNumber = -1;
        }
        
        if (aNumber > 0 && bNumber < 0)
        {
            return true;
        }
        else if (aNumber < 0 && bNumber > 0 )
        {
            return false;
        }
        else if (aNumber > 0 && bNumber > 0)
        {
            return aNumber >= bNumber;
        }
        else
        {
            return a.compareTo(b) >= 0;
        }
    }
    
    private void updateIndexFile(String tagName, String postTag, String volumeTitle)
    {
        String fileName = tagName + ".js";
        String filePath = getBaseOutputDirectory();
        
        String text = Common.getFileString( filePath, fileName );
        
        int beginIndex = text.indexOf("new Array(");
        int endIndex = text.indexOf(");", beginIndex) + 2;
        
        String[] volumeTitleTokens = text.substring(beginIndex, endIndex).split("'");
        String[] postTagTokens = text.substring(endIndex, text.length()).split("'");
           
        int midIndex1 = text.indexOf(");") - 1;
        int midIndex2 = text.indexOf(");", midIndex1 + 3) - 1;
        endIndex = text.length();
        
        int i = 1;
        if (volumeTitleTokens.length > 2) // 已經存在一筆資料
        {
            //print("共有" + volumeTitleTokens.length + " 個token");
            
            String tempVolumeTitle = getOutputText(volumeTitle);
            for (; i < volumeTitleTokens.length - 1; i ++)
            {
                if (volumeTitleTokens[i].matches(", "))
                    continue;
                
                //if (tempVolumeTitle.compareTo(volumeTitleTokens[i]) > 0)
                if (aIsBiggerThanB(tempVolumeTitle, volumeTitleTokens[i]))
                {
                    print("" + i + " 較大: " + tempVolumeTitle + " > " + volumeTitleTokens[i]);
                    break;
                }
                
                //print("" + i + " 較小: " + tempVolumeTitle + " < " + volumeTitleTokens[i]);
            }
            
            // 進行排序
            if (i != volumeTitleTokens.length - 1)
            {
                midIndex1 = text.indexOf(volumeTitleTokens[i]) - 1;
                midIndex2 = text.indexOf(postTagTokens[i]) - 1;
                
                if (false)
                {
                    print("新的三部分:");
                    print("第1部分 : " + text.substring(0, midIndex1));
                    print("第2部分 : " + text.substring(midIndex1, midIndex2));
                    print("第3部分 : " + text.substring(midIndex2, endIndex));
                }
            }
        }
        
        

        
        boolean frontIndexExisted = true;
        boolean backIndexExisted = false;
        if (text.substring(midIndex1, midIndex2).indexOf("'") < 0) 
        {
            // 還沒有存在集數index資料
            frontIndexExisted = false;
        }
        
        if (volumeTitleTokens.length > 2) // 經過排序
        {
            if (i < volumeTitleTokens.length - 1)
            {
                frontIndexExisted = false;
                backIndexExisted = true;
            }
        } 
        
        String frontDotToken = (frontIndexExisted ? ", '" : "'" );
        String backDotToken = (backIndexExisted ? "', " : "'" );
        
        text = text.substring(0, midIndex1) + 
               frontDotToken + getOutputText(volumeTitle) + backDotToken + 
               text.substring(midIndex1, midIndex2) + 
               frontDotToken + postTag + backDotToken + 
               text.substring(midIndex2, endIndex);
        
        if (false)
        {
            print("排序後的結果: " + text);
            print("-----------------------");
        }
        Common.outputFile(text, filePath, fileName );
    }
    
    private void outputCommentPost(String tagName, String postTag, String postTitle, List<List<String>> dataList)
    {
        String fileName = "comment.js";
        String filePath = getBaseOutputDirectory() + tagName + Common.getSlash();
        String volumeKey = "的评论";
        if (postTitle.indexOf(volumeKey) > 0) // ex. Claymor大剑第146话的评论 
        {
            fileName = postTag + ".js";
            String variableName = "DM5_COMMENT";
            String volumeTitle = postTitle.replaceAll(volumeKey, "");
            outputListFile(dataList, variableName, filePath, fileName);
            updateIndexFile(tagName, postTag, volumeTitle);
            return;
        }
        
        print("comment加入 : " + postTag + " : " + postTitle);
        updateCommentFile(tagName, dataList);
    }
    
    private void updateCommentFile(String tagName, List<List<String>> dataList)
    {
        String fileName = "comment.js";
        String filePath = getBaseOutputDirectory() + tagName + Common.getSlash();
        String text = Common.getFileString( filePath, fileName );
        
        int midIndex1 = text.indexOf(");") - 1;
        int endIndex = text.length();
        
        boolean dataExisted = true;
        if (text.substring(0, midIndex1).indexOf("'") < 0)
        {
            // 還沒有存在集數index資料
            dataExisted = false;
        }
        
        String dataText = "";
        int dataCount = dataList.get(0).size();
        for (int i = 0; i < dataCount; i ++)
        {
            for (int j = 0; j < dataList.size(); j ++)
            {
                if (i != 0 || j != 0)
                {
                    dataText += ", ";
                }
                
                dataText += "'" + getOutputText(dataList.get(j).get(i)) + "'";
            }
        }
        
        if (text.indexOf(dataText) >= 0)
        {
            print("不加入，因comment.js已經包含此評論: " + dataList.get(1).get(0));
            return;
        }
        
        
        String dotToken = (dataExisted ? ", " : "" );
        
        text = text.substring(0, midIndex1) + 
               dotToken + dataText + 
               text.substring(midIndex1, endIndex);
        
        Common.outputFile(text, filePath, fileName);
        //System.exit(0);
    }
    
    private void outputCommentFile(String tagName)
    {
        String text = "TITLE_COMMONET = new Array( ";
        text += "\n);\n";
        
        String fileName = "comment.js";
        String filePath = getBaseOutputDirectory() + tagName + Common.getSlash();
        Common.outputFile(text, filePath, fileName);
    }
    
    private void handleSingleCommentPost(String tagName, String url)
    {
        List<List<String>> oldList = null;
        String postTag = url.split("/tiezi-")[1];
 
        if (new File(getBaseOutputDirectory() + tagName + Common.getSlash() + postTag + ".js").exists())
        {
            print("不處理，因為已經存在: " + postTag);
            return;
        }
        
        String text = getAllPageString(url);
        String title = getCommentTitle(text);
        int pageCount = getCommentPostPageCount(text);
        
        for (int i = 1; i <= pageCount; i ++)
        {
            if (i > 1)
            {
                String pageURL = url + "-p" + i + "/";
                text = getAllPageString(pageURL);
            }
            
            List<List<String>> newList = getCommentPostData(text);
            oldList = combinationData(oldList, newList);
        }

        outputCommentPost(tagName, postTag, title, oldList);
    }
    
    private String getTitleName(String text)
    {
        int beginIndex = text.indexOf("<title>");
        beginIndex = text.indexOf(">", beginIndex) + 1;
        int endIndex = text.indexOf("_");
        
        return text.substring(beginIndex, endIndex).trim();
    }
    
    private void handleSingleTitle(String tagName)
    {
        int beginIndex = 0;
        int endIndex = 0;

        String titleURL = Common.getRegularURL( baseURL + "/manhua-" + tagName + "/" );
        String text = getAllPageString(titleURL);
        String titleIntroduction = getTitleIntroduction(text);
        String titleName = getTitleName(text);
        
        if (new File(getBaseOutputDirectory() + tagName + ".jpg").exists())
        {
            // 有圖檔就當作不用做
            print("有圖檔 , 故跳過 : " + titleName + "[" + tagName + "]");
            return;
        }
        
        if (!new File(getBaseOutputDirectory() + tagName + ".js").exists())
        {
            String titlePicURL = handleTitlePic(text, tagName);
            
            outputIndexFile(tagName, titleName, titleIntroduction, titlePicURL);
            outputCommentFile(tagName);
        }
        
        int pageCount = getCommentPageCount(text);
        print( titleName + "[" + tagName + "] 共有 " + pageCount + "頁的評論主題");
        
        for (int i = 1; i <= pageCount; i ++)
        {
            print("開始掃描 " + titleName + " 第 " + i + " 頁的評論主題");
            if (i > 1)
            {
                String pageURL = titleURL + "p" + i + "/";
                text = getAllPageString(pageURL);
            }
            
            List<String> commentUrlList = getCommentUrlList(text);

            for (int j = 0; j < commentUrlList.size(); j ++)
            {
                handleSingleCommentPost(tagName, commentUrlList.get(j));
            }
            //System.exit(0);
        }
        
        
        //System.exit(0);
    }
    
    private void handleTsukkomi(String text)
    {
        String url = webSite; // ex. http://www.dm5.com/manhua-list-s4-size60
        if (url.matches(".+/"))
            url = url.substring(0, url.length()-1);
        
        // 預設可抓五十頁的作品列表
        for (int i = 1; i <= 50; i ++)
        {
            if (i > 1)
            {
                String pageURL = url + "p" + i;
                text = getAllPageString(pageURL);
            }
            
            List<String> tagNameList = getTagNameList(text);

            for (int j = 0; j < tagNameList.size(); j ++)
            {
                handleSingleTitle(tagNameList.get(j));
            }
        }
        
        
    }

    @Override
    public void parseComicURL()
    { // parse URL and save all URLs in comicURL  //
        // 先取得前面的下載伺服器網址

        String allPageString = Common.getFileString( SetUp.getTempDirectory(), indexName );
        Common.debugPrint( "開始解析這一集有幾頁 : " );
        
        if (needTsukkomiMode(webSite))
        {
            handleTsukkomi(allPageString);
            System.exit(0);
            return;
        }

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
        
        boolean needChangeName = false;

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

                Common.debugPrintln( "全文: " + allPageString );

                String[] picURLs = getPicURLs( allPageString, cidAndKey, needChangeName );

                Common.debugPrintln( "下載張數:" + picURLs.length );

                for ( int i = 0; i < picURLs.length && Run.isAlive; i++ )
                {
                    if (isWrongPicName(picURLs[i]))
                    {
                        Common.debugPrintln("此為錯誤位址, 重新擷取");
                        needChangeName = true;
                        continue;
                    }
                    
                    //singlePageDownload(getTitle(), getWholeTitle(),
                    //        picURLs[i], totalPage, p + 1, 0);
                    referURL = gerReferURL( webSite, p );

                    Common.debugPrintln( "REFER : " + referURL );
                    singlePageDownloadUsingSimple( getTitle(), getWholeTitle(),
                                                   picURLs[i], totalPage, p + 1, "", referURL );

                    p++;
                    
                    if ( needChangeName )
                    {
                        needChangeName = false;
                        break; //  只重新擷取後下載一張圖片
                    }
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
    
    // ex. url = http://manhua1023.146-71-123-50.cdndm5.com/8/7946/188138/2_3153.jpg?cid=188138&key=2ca7872f737c86fd342e12b56ee59025&ak=3f66977973db3b1e
    //       pic = 2_3153.jpg
    private boolean isWrongPicName( String url )
    {
        String[] temps = url.split( "/|\\?");
        String name = temps[temps.length-2];
        String namePart = name.split( "_|\\." )[1];
        
        
        
        if ( namePart.length() > 4 )
        {
            Common.debugPrintln("Wrong NAME:" + name);
            return true;
        }
        else
            return false;
    }

    public String getCidAndKey( String url, String referURL )
    {
        Common.debugPrintln( "網址refer: " + referURL );

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

    public String[] getPicURLs( String allPageString, String cidAndKey, boolean needChangeName )
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
        String tempURLtoken = allPageString.substring( beginIndex, endIndex );
        String[] tempTokens = tempURLtoken.split( "|" );

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
        
        // 取得備用檔名 (預防6_70871)
        tempURLtoken += "/"; // ex. ://f.e-g-c-j.b.a/l/k/1/
        beginIndex = allPageString.indexOf( tempURLtoken );
        
        
        if (beginIndex > 0)
        {
            endIndex = allPageString.indexOf( "?", beginIndex );
            String additionalPicName = allPageString.substring(endIndex - 4, endIndex );
            Common.debugPrintln( "額外檔名:" + additionalPicName );
            
            if (needChangeName)
            {
                tempTokens[0] = additionalPicName;
                
                Common.debugPrintln( "取代第一個檔名:" + additionalPicName );
            }
        }

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
    
    private boolean needTsukkomiMode(String url)
    {
        if (url.indexOf("manhua-list-") > 0 || 
            url.indexOf("manhua-updated") > 0 ||
            url.indexOf("/rss.ashx") > 0)
            return true;
        
        return false;
    }

    @Override
    public boolean isSingleVolumePage( String urlString )
    {
        // ex. http://www.dm5.com/m32738/
        
        if (needTsukkomiMode(urlString))
        {
            return Flag.allowDownloadFlag;
        }

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
        if (needTsukkomiMode(urlString))
        {
            return "DM5_list";
        }
        
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
        
        if (needTsukkomiMode(urlString))
        {
            urlList.add( urlString );
            volumeList.add( "dm5_tsukkomi" );
            combinationList.add( volumeList );
            combinationList.add( urlList );

            return combinationList;
        }

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
