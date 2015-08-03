/*
 ----------------------------------------------------------------------------------------------------
 Program Name : JComicDownloader
 Authors  : surveyorK
 Last Modified : 2012/5/31
 ----------------------------------------------------------------------------------------------------
 ChangeLog:
 *  4.06: 1. 修復分級頁面無法下載的問題。
 *  4.05: 1. 新增對eyny的支援。
 ----------------------------------------------------------------------------------------------------
 */
package jcomicdownloader.module;

import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import jcomicdownloader.tools.*;
import jcomicdownloader.enums.*;
import java.util.*;
import jcomicdownloader.ComicDownGUI;
import jcomicdownloader.Flag;
import jcomicdownloader.SetUp;
import jcomicdownloader.encode.Encoding;
import static jcomicdownloader.module.ParseWiki.pageParsedCount;

public class ParsePtt extends ParseCKNovel {

    private int radixNumber; // use to figure out the name of pic
    private String jsName;
    protected String indexName;
    protected String indexEncodeName;
    protected String baseURL;
    protected int floorCountInOnePage; // 一頁有幾層樓
    protected String cookie;

    // title
    // site
    // style
    private int PT_WORK_SUMMARY = 0;
    // 平常日薪資
    // 國定假日薪資
    // 超時加班費：
    // 勞健保、勞退︰
    // 薪資發放日：
    private int PT_WORK_PAY = 1;
    // 每日工作&休息時間︰
    // 工作日期&排班方式：
    // 休息有無計薪&供餐：
    private int PT_WORK_TIME = 2;
    // 工作地點︰
    private int PT_WORK_ADDRESS = 3;
    // 工作內容︰
    private int PT_WORK_DETAIL = 4;
    // 統一編號：
    // 單位名稱：
    // 單位地址：
    private int PT_WORK_COMPANY = 5;
    // 聯絡人姓氏︰
    // 電話︰
    // 是否回信給報名者：
    private int PT_WORK_CONTACT = 6;
    // 需求人數：
    // 通知方式：
    // 面試時間：
    // 受訓時間：
    // 截止時間：
    private int PT_WORK_OTHER = 7;

    private String[] NEW_TAG = new String[2];

    private String gMailAddress = "";
    private String gPhoneNumber = "";

    /**
     *
     * @author user
     */
    public ParsePtt() {
        regexs= new String[]{"(?s).*ptt.cc/(?s).*"};
        enumName = "PTT";
	parserName=this.getClass().getName();
        downloadBefore=true;
	siteID=Site.formString("PTT");
        siteName = "Ptt";
        indexName = Common.getStoredFileName(SetUp.getTempDirectory(), "index_ptt_parse_", "html");
        indexEncodeName = Common.getStoredFileName(SetUp.getTempDirectory(), "index_ptt_encode_parse_", "html");

        jsName = "index_ptt.js";
        radixNumber = 15661; // default value, not always be useful!!

        baseURL = "https://www.ptt.cc";

        floorCountInOnePage = 10; // 一頁有幾層樓

        cookie = "djAX_e8d7_agree=6; "; // 因應分級頁面

        NEW_TAG[0] = "<new>";
        NEW_TAG[1] = "</new>";
    }

    @Override
    public void parseComicURL() { // parse URL and save all URLs in comicURL  //
        // 先取得前面的下載伺服器網址

        Common.debugPrintln("無法得知有幾頁，需一步步檢查");
        String allPageString = "";
        String tempString = "";
        int beginIndex = 0;
        int endIndex = 0;
        int p = 1; // 目前頁數
        totalPage = p;

        String allText = "";

        String url = webSite;

        int ptIndex = 0;
        boolean over = false;

        // for debug
        if (false) {
            String tempURL = "https://www.ptt.cc/bbs/part-time/M.1410620942.A.19D.html";
            getInformation(tempURL, 0);
            System.exit(0);
        }

        while (true) {
            allPageString = getAllPageString(url);
            int count = allPageString.split("class=\"title\">").length - 1;
            Common.debugPrintln((p++) + " 頁共有 " + count + " 篇文章");
            String pageURL = "";
            String pageTitle = "";
            String tempAllPageString = "";
            totalVolume += count;
            beginIndex = endIndex = 0;
            int downCount = 0;
            for (int i = 0; i < count; i++) {
                // 取得文章位址
                beginIndex = allPageString.indexOf("class=\"title\">", beginIndex);
                beginIndex = allPageString.indexOf("href=", beginIndex);
                beginIndex = allPageString.indexOf("\"", beginIndex) + 1;
                endIndex = allPageString.indexOf("\"", beginIndex);
                //Common.debugPrintln( beginIndex + " " + endIndex);
                pageURL = allPageString.substring(beginIndex, endIndex);

                if (pageURL.indexOf(".css") > 0
                        || pageURL.indexOf("utf-8") >= 0) {
                    continue;
                }

                // 取得文章標題
                beginIndex = allPageString.indexOf(">", beginIndex) + 1;
                endIndex = allPageString.indexOf("</a>", beginIndex);
                pageTitle = allPageString.substring(beginIndex, endIndex);

                if (jobFinished(pageTitle)) {
                    print("已經結束徵求: " + " [" + i + "] [" + pageTitle + "]  [" + pageURL + "]");
                    continue;
                }

                Common.debugPrintln("有效篇數: " + ptIndex + "/" + (downCount++) + " 列表的標題: [" + i + "] [" + pageTitle + "]  [" + pageURL + "]");

                if (pageTitle.indexOf("name=\"viewport\"") > 0
                        || pageTitle.indexOf("滿") > 0
                        || pageTitle.indexOf("結束") > 0) {
                    continue;
                }

                String tempInformation = getInformation(baseURL + pageURL, ptIndex);

                if (tempInformation != null) {
                    allText += tempInformation;
                    ptIndex++;
                    //print( "目前有效篇數: " + ptIndex);
                }

                if (ptIndex > 40) {
                    over = true;
                    break;
                }

            }
            beginIndex = allPageString.indexOf("class=\"btn-group pull-right\">");
            beginIndex = allPageString.indexOf("href=", beginIndex) + 1;
            beginIndex = allPageString.indexOf("href=", beginIndex);
            beginIndex = allPageString.indexOf("\"", beginIndex) + 1;
            endIndex = allPageString.indexOf("\"", beginIndex);
            pageURL = allPageString.substring(beginIndex, endIndex);

            url = baseURL + pageURL;

            if (over) {
                break;
            }

        }

        if (Run.isAlive) {
            Common.outputFile(allText, getDownloadDirectory(), getWholeTitle() + ".js");
        }

        System.exit(0);
    }

    public String getInformation(String urlString, int ptIndex) {
        String information = "";
        int beginIndex = 0;
        int endIndex = 0;
        String text = "";
        boolean badData = false;
        List<String[]> textList = new ArrayList<String[]>();

        String allText = getAllPageString(urlString);

        beginIndex = allText.indexOf("<title>");
        beginIndex = allText.indexOf(">", beginIndex) + 1;
        endIndex = allText.indexOf(" - ", beginIndex);
        String title = allText.substring(beginIndex, endIndex).trim();
        title = getRegularFileName(title);

        text = getLocalString(allText, "《工作時間》", "《");
        String[] timeText = new String[3];
        timeText[0] = getLocalString(text, "每日工作");
        if (timeText[0].equals("")) {
            timeText[0] = getLocalString(allText, "每日工作");
        }
        timeText[1] = getLocalString(text, "工作日期");
        if (timeText[1].equals("")) {
            timeText[1] = getLocalString(allText, "工作日期");
        }
        timeText[2] = getLocalString(text, "休息有無計薪");
        if (timeText[2].equals("")) {
            timeText[2] = getLocalString(allText, "休息有無計薪");
        }

        String tempText = "";
        String foundText = NEW_TAG[0] + NEW_TAG[1];
        boolean timeFound = false;
        for (int i = 0; i < timeText.length; i++) {
            tempText = getDateString(getContent(timeText[i]), false);
            if (tempText != null && tempText.indexOf(NEW_TAG[0] + NEW_TAG[1]) < 0) {
                foundText = tempText;
                print("->找到時間 [" + i + "] " + tempText);
                timeFound = true;
                //break;
            }
        }

        timeText[1] = foundText;

        // for those case which the date only exists on title  . ex. 9/5（五）臨時包裝工讀生
        if (timeText[1].indexOf(NEW_TAG[0] + NEW_TAG[1]) >= 0) {
            print("尚未取得時間:" + timeText[1]);
            String tempTitle = title.replaceAll("\\[.+\\]", "").trim();
            String tempDate = getDateString(tempTitle, true);
            if (tempDate.indexOf(NEW_TAG[0] + NEW_TAG[1]) < 0) {
                timeText[1] = timeText[1].replace(NEW_TAG[0] + NEW_TAG[1], tempDate);

                print("從標題取得打工日期: " + timeText[1]);
            } else // 標題和內文都沒有時間資訊
            {
                print("沒有時間資訊:" + timeText[1]);
                badData = true;
            }
        }

        textList.add(timeText);

        text = getLocalString(allText, "《工作待遇》", "《");
        String[] payText = new String[5];
        payText[0] = getLocalString(text, "平常日薪資");
        if (payText[0].equals("")) {
            payText[0] = getLocalString(allText, "平常日薪資");
        }
        payText[1] = getLocalString(text, "國定假日薪資");
        payText[2] = getLocalString(text, "超時加班費");
        payText[3] = getLocalString(text, "勞健保");
        payText[4] = getLocalString(text + "\n", "發放日");
        textList.add(payText);

        text = getLocalString(allText, "《工作內容》", "《");
        String[] workText = new String[2];
        workText[0] = getLocalString(text, "工作地點");
        if (workText[0].equals("")) {
            workText[0] = getLocalString(allText, "\n工作地點", "\n");
        }
        workText[1] = getLocalString(text, "工作內容︰", true);
        if (workText[1].equals("")) {
            workText[1] = getLocalString(allText, "工作內容：", "：");

            // 拿掉最後一行
            endIndex = workText[1].lastIndexOf("\n");
            if (endIndex > 0) {
                workText[1] = workText[1].substring(0, endIndex);
            }
        }

        tempText = getAddressString(getContent(workText[0]));
        if (tempText.indexOf(NEW_TAG[0] + NEW_TAG[1]) < 0) {
            workText[0] = tempText;
        } else {
            badData = true;
        }

        textList.add(workText);

        text = getLocalString(allText, "《事業登記資料》", "《");
        String[] companyText = new String[3];
        companyText[0] = getLocalString(text, "統一編號");
        companyText[1] = getLocalString(text, "單位名稱：");
        companyText[2] = getLocalString(text, "單位地址");
        textList.add(companyText);

        text = getLocalString(allText, "《聯絡資訊》", "《");
        String[] contactText = new String[4];
        contactText[0] = getLocalString(text, "聯絡人姓氏");
        if (contactText[0].equals("")) {
            contactText[0] = getLocalString(allText, "聯絡人姓氏");
        }
        contactText[1] = getLocalString(text, "電話︰");
        if (contactText[1].equals("")) {
            contactText[1] = getLocalString(allText, "電話︰");
        }
        contactText[2] = contactText[1]; // email
        contactText[3] = getLocalString(text, "是否回信給報名者");
        if (contactText[3].equals("")) {
            contactText[3] = getLocalString(allText, "是否回信給報名者");
        }
        // mail phone
        for (int i = 0; i < contactText.length; i++) {
            print("contactText " + i + " : " + getContent(contactText[i]));
            tempText = getMailAddressString(getContent(contactText[i]));

            if (tempText.indexOf(NEW_TAG[0] + NEW_TAG[1]) < 0) {
                contactText[2] = tempText;
                break;
            }
        }
        for (int i = 0; i < contactText.length; i++) {
            String tempContactText = contactText[i].replaceAll(NEW_TAG[0] + "(?s).+" + NEW_TAG[1], "");
            tempContactText = tempContactText.replaceAll(gMailAddress, "");
            tempContactText = tempContactText.replaceAll("/", "").trim();

            print("contactText " + i + " : " + tempContactText);
            tempText = getPhoneNumberString(getContent(tempContactText));

            if (tempText.indexOf(NEW_TAG[0] + NEW_TAG[1]) < 0) {
                contactText[1] = tempText;

                print("OLD: " + contactText[2]);
                print("RE: " + getContent(tempContactText));
                // ex. mos002@mos.com.tw / 02-23945709 -> mos002@mos.com.tw
                if (contactText[2] != null) {
                    String tempMailText = contactText[2];
                    String tempTag = getContent(tempContactText);
                    int tempBeginIndex = tempMailText.indexOf(tempTag);
                    if (tempBeginIndex > 0) {
                        tempMailText = tempMailText.substring(tempBeginIndex, tempBeginIndex + tempTag.length());
                    }

                    tempMailText = tempMailText.replaceAll("\\s+/", "").trim();
                    tempMailText = tempMailText.replaceAll("/\\s+", "").trim();

                    contactText[2] = tempMailText;
                }
                print("NEW: " + contactText[2]);

                break;
            }
        }
        // phone
        if (!contactText[1].matches("(?s).*" + NEW_TAG[0] + ".*" + NEW_TAG[1])) {
            contactText[1] += NEW_TAG[0] + "" + NEW_TAG[1];
        }
        // mail
        if (!contactText[2].matches("(?s).*" + NEW_TAG[0] + ".*" + NEW_TAG[1])) {
            contactText[2] += NEW_TAG[0] + "" + NEW_TAG[1];
        }

        // do not exist email address
        if (contactText[2].indexOf(NEW_TAG[0] + "" + NEW_TAG[1]) >= 0) {
            badData = true;
        }

        textList.add(contactText);

        text = getLocalString(allText, "《其他資訊》", "強烈建議雇主");
        String[] otherText = new String[5];
        otherText[0] = getLocalString(text, "需求人數");
        if (otherText[0].equals("")) {
            otherText[0] = getLocalString(allText, "需求人數：");
        }
        otherText[1] = getLocalString(text, "通知方式");
        if (otherText[1].equals("")) {
            otherText[1] = getLocalString(allText, "通知方式：");
        }
        otherText[2] = getLocalString(text, "面試時間");
        if (otherText[2].equals("")) {
            otherText[2] = getLocalString(allText, "面試時間：");
        }
        otherText[3] = getLocalString(text, "受訓時間");
        otherText[4] = getLocalString(text, "截止時間");
        if (otherText[4].equals("")) {
            otherText[4] = getLocalString(allText, "截止時間：");
        }
        textList.add(otherText);

        for (int i = 0; i < textList.size(); i++) {
            String[] temp = textList.get(i);
            for (int j = 0; j < temp.length; j++) {
                print("" + temp[j]);
            }
            print("-- " + i + " -- ");
        }

        String varDataName = "PT_TEMP_DATA[" + ptIndex + "]";

        information += varDataName + " = new Array( 8 );\n";

        // title
        // site
        // style
        String[] summaryText = new String[7];
        summaryText[0] = getTitleString(title);
        summaryText[1] = "PTT";

        tempText = getCountyString(title, getContent(workText[0]));
        //int locationIndex = getLocationIndex(title, getContent(workText[0]));
        summaryText[2] = (tempText != null) ? tempText : getLocationString(title, getContent(workText[0]));
        summaryText[3] = urlString;

        endIndex = urlString.indexOf(".html");
        beginIndex = urlString.lastIndexOf("/", endIndex) + 1;
        String key = urlString.substring(beginIndex, endIndex);
        key = key.replaceAll("\\.", "_");
        summaryText[4] = key;

        // ex. Wed Aug 27 02:47:03 2014
        beginIndex = allText.lastIndexOf("article-meta-value");
        beginIndex = allText.indexOf(">", beginIndex) + 1;
        endIndex = allText.indexOf("</span>", beginIndex);
        String time = allText.substring(beginIndex, endIndex);
        time = time.replaceAll("\\s+", " ");
        if (time.split(" ").length > 6) {
            badData = true;
        }
        summaryText[5] = time;
        summaryText[6] = getCategoryString(allText);

        // ex. "Tue Sep  2 11:53:21 2014"
        if (!time.matches("\\w+\\s+\\w+\\s+\\d+\\s+")) {
            //badData = true;
        }

        information += varDataName + "[PT_WORK_SUMMARY] = new Array(\n";
        for (int i = 0; i < summaryText.length; i++) {
            information += "\"" + getContent(summaryText[i]) + "\"";
            if (i < summaryText.length - 1) {
                information += ", \n";
            }
        }
        information += "\n);\n";

        // 平常日薪資
        // 國定假日薪資
        // 超時加班費：
        // 勞健保、勞退︰
        // 薪資發放日：
        information += varDataName + "[PT_WORK_PAY] = new Array(\n";
        for (int i = 0; i < payText.length; i++) {
            information += "\"" + getContent(payText[i]) + "\"";
            if (i < payText.length - 1) {
                information += ", \n";
            }
        }
        information += "\n);\n";

        if (payText[0].matches("")) {
            badData = true;
        }

        // 每日工作&休息時間︰
        // 工作日期&排班方式：
        // 休息有無計薪&供餐：
        information += varDataName + "[PT_WORK_TIME] = new Array(\n";
        for (int i = 0; i < timeText.length; i++) {
            information += "\"" + getContent(timeText[i]) + "\"";
            if (i < timeText.length - 1) {
                information += ", \n";
            }
        }
        information += "\n);\n";

        // 工作地點︰
        information += varDataName + "[PT_WORK_ADDRESS] = new Array(\n";
        information += "\"" + getContent(workText[0]) + "\" );\n";
        // 工作內容︰
        information += varDataName + "[PT_WORK_DETAIL] = new Array(\n";
        information += "\"" + getContent(workText[1]) + "\" );\n";

        // 統一編號：
        // 單位名稱：
        // 單位地址：
        information += varDataName + "[PT_WORK_COMPANY] = new Array(\n";
        for (int i = 0; i < companyText.length; i++) {
            information += "\"" + getContent(companyText[i]) + "\"";
            if (i < companyText.length - 1) {
                information += ", \n";
            }
        }
        information += "\n);\n";

        // 聯絡人姓氏︰
        // 電話︰
        // 是否回信給報名者：
        information += varDataName + "[PT_WORK_CONTACT] = new Array(\n";
        for (int i = 0; i < contactText.length; i++) {
            information += "\"" + getContent(contactText[i]) + "\"";
            if (i < contactText.length - 1) {
                information += ", \n";
            }
        }
        information += "\n);\n";

        // 需求人數：
        // 通知方式：
        // 面試時間：
        // 受訓時間：
        // 截止時間：
        information += varDataName + "[PT_WORK_OTHER] = new Array(\n";
        for (int i = 0; i < otherText.length; i++) {
            information += "\"" + getContent(otherText[i]) + "\"";
            if (i < otherText.length - 1) {
                information += ", \n";
            }
        }
        information += "\n);\n";

        //System.exit(0);
        if (badData) {
            return null;
        } else {
            return information;
        }
    }

    private String removeDuplicateComma(String text) {
        while (text.indexOf(",,") >= 0) {
            text = text.replaceAll(",,", ",");
        }
        
        // 最前面是,
        if (text.matches(",.+"))
        {
            text = text.substring( 1, text.length());
            print("拿掉最前面的',' : " + text);
        }
        
        if (text.matches(".+,"))
        {
            text = text.substring(0, text.length() - 1);
            print("拿掉最後面的',' : " + text);
        }

        return text;
    }

    /*
     // write together

     // days with "~"

     */
    public String getDateString(String text, boolean removeOrigin) {
        int beginIndex, endIndex, atIndex;
        String temp = "";

        print("原本的日期字串: " + text);

        // ex. 九月三號 下午一點
        // ex. 2014/09/15 ~ 2014/10/10 共四周 每周1.5小時
        text = text.trim();
        text = text.replaceAll("\\s+", " ");
        //print("------------->" + text);
        text = chineseToNumber(text);
        text = getAllNumberDate(text); // ex. 0903
        
        text = text.replaceAll("[\\(\\)]+", ""); // ex. 9/22()-9/24()、9/29()-9/30()
        //print("---->" + text);
        text = text.replaceAll("\\d+周", "");
        text = text.replaceAll("\\d+週", "");
        text = text.replaceAll("\\d+天", ""); // 9/17()跟9/24(),如果只能配合1天也可
        text = text.replaceAll("\\d+人", ""); // 9:00-12:00 1人
        text = text.replaceAll("\\d+位", ""); // 9/26,9/27,10/3(台中需求1位),10/4(台北2位)
        text = text.replaceAll("\\d+小時", ""); // 10/25 & 11/22 每次1小時
        text = text.replaceAll("\\d+次", ""); // 暫定為10/1-11/28,大約18次
        text = text.replaceAll("$\\d+", ""); // ex. 

        text = text.replaceAll("[AM|am|PM|pm|早上|下午]+", "");
        
        text = text.replaceAll("\\d+:\\d+\\s*-*\\s*\\d+:\\d+", ","); // ex. 9/1-9/5中午11:50-12:50、下午15:40-17:40
        text = text.replaceAll("\\d+:\\d+:*\\d+:\\d+", ","); // ex.9/6(六)11:00:13:00
        text = text.replaceAll("\\d+:\\d+\\s*~*\\s*\\d+:\\d+", ","); // ex. 9/3 14:00~24:00 9/6 16:00~24:00
        text = text.replaceAll("\\d+:\\d+", ",");
        text = text.replaceAll("\\d+點", "");
        text = text.replaceAll(" ", ","); //
        

        text = text.replaceAll("2014/", "");
        text = text.replaceAll("2015/", "");
        text = text.replaceAll("104/", "");
        text = text.replaceAll("105/", "");
        text = text.replaceAll("106/", "");
        text = text.replaceAll("月", "/");

        //text = "7/29 - 8/22 (中間若有幾天有事可事先請假)";
        if (text.matches("(?s).*\\d+/\\d+(?s).*")) {
            temp = text.replaceAll("跟|和|或|、|\\&|＆|\\(", ",").trim();

            temp = temp.replaceAll("~|-|～", "~");
            temp = temp.replaceAll("[^\\d/~,、]", "");
            temp = temp.replaceAll("//", "/");
            while (temp.matches("(?s).*/")) {
                temp = temp.substring(0, temp.length() - 1);
            }
            String textModified = temp;

            print("textModified : " + textModified);

            boolean noNeedMore = false;
            String[] temps = textModified.split(",");
            temp = "";
            for (int i = 0; i < temps.length; i++) {
                if (temps.length == 1) {
                    print("沒有',' , 跳過此次處理");
                    temp = textModified.replaceAll(",", "").trim();
                    break; // 留給下面~來做
                }
                String[] temps1 = temps[i].split("~");
                String singleData;
                if (temps1.length == 2) {
                    singleData = getSingleDate(temps1[0]) + "~" + getSingleDate(temps1[1]);
                    noNeedMore = true;
                } else {
                    singleData = getSingleDate(temps[i]);

                    // ex. 2, 12
                    if (i > 0 && singleData.length() <= 2) {
                        String tempData = getSingleDate(temps[0]); // 以第一個日期的月份作為後面的基準
                        int tempEndIndex = tempData.indexOf("/") + 1;
                        singleData = tempData.substring(0, tempEndIndex) + singleData;
                        print("跟第一個日期的月份做組合: " + singleData);
                    }
                }

                if (!singleData.equals("")) {
                    if (i > 0) {
                        temp += ",";
                    }
                    temp += singleData;
                }
            }

            Common.debugPrintln("轉換後日期1 : " + temp);

            String temp2 = "";
            temps = textModified.split("~");
            for (int i = 0; i < temps.length; i++) {
                if (noNeedMore) {
                    temp2 = temp;
                    break;
                } else if (temps.length == 1) {
                    temp2 = temp.replaceAll("~", "").trim();
                    break; // 不處理
                }

                // ex. 9/4~14
                String singleData = temps[i];
                if (i > 0 && singleData.length() <= 2) {
                    String tempData = getSingleDate(temps[i - 1]);
                    int tempEndIndex = tempData.indexOf("/") + 1;
                    singleData = tempData.substring(0, tempEndIndex) + singleData;
                }

                singleData = getSingleDate(singleData);

                if (!singleData.equals("")) {
                    if (i > 0) {
                        temp2 += "~";
                    }
                    temp2 += singleData;
                }
            }

            Common.debugPrintln("原本的日期 : " + text);
            Common.debugPrintln("轉換後日期 : " + temp2);

            text = removeDuplicateComma(text); // 拿掉多餘的','
            temp2 = removeDuplicateComma(temp2); // 拿掉多餘的','
            //System.exit(0);
            if (removeOrigin) {
                return NEW_TAG[0] + temp2.trim() + NEW_TAG[1];
            } else {
                return text + NEW_TAG[0] + temp2.trim() + NEW_TAG[1];
            }
        }

        if (removeOrigin) {
            return NEW_TAG[0] + NEW_TAG[1];
        } else {
            return text + NEW_TAG[0] + NEW_TAG[1];
        }
    }

    // ex. 0901 -> 9/1
    // ex. filter this case : 平日白天1130-1430
    private String getAllNumberDate(String text) {
        int count = text != null ? text.length() : 0;

        print("getAllNumberDate IN:" + text);

        String[] dateString = new String[10];
        int dateCount = 0;
        boolean timeStringExisted = false;

        for (int i = 0; i <= count - 4; i++) {
            String temp = text.substring(i, i + 4);

            //print("" + i  + "等待解析的:" + temp);
            if (temp.matches("\\d+")) {
                int number = Integer.parseInt(temp);
                int month = number / 100;
                int day = number % 100;

                if (month > 0 && month <= 12 && day > 0 && day < 31) {
                    dateString[dateCount++] = "" + month + "/" + day;

                    print("第" + dateCount + "個解析出來的數字日期 : " + dateString[dateCount - 1]);
                } else // this is not a date string, may be a time string, like 1430 (14:30)
                {
                    print("存在不合理的日期，應該是時間:" + month + "/" + day);
                    timeStringExisted = true;
                }
            }
        }

        if (timeStringExisted || dateCount == 0) {
            return text;
        }

        return dateString[0]; //  取第一個日期
    }

    // ex. 九月三號  -> 9月3號
    private String chineseToNumber(String text) {
        String[] chineseText = {
            "一", "二", "三", "四", "五", "六", "七", "八", "九", "十"
        };

        String tag = "";
        for (int i = 0; i < 7; i++) {
            tag = chineseText[i];
            text = text.replaceAll("(" + tag + ")", ""); // ex. (一)
        }
        for (int i = 0; i < chineseText.length - 1; i++) {
            tag = chineseText[chineseText.length - 1] + chineseText[i];
            text = text.replaceAll(tag, "" + (i + 11));
        }
        for (int i = 0; i < chineseText.length - 1; i++) {
            tag = chineseText[i] + chineseText[chineseText.length - 1];
            text = text.replaceAll(tag, "" + ((i + 1) * 10));
        }

        for (int i = 0; i < chineseText.length; i++) {
            text = text.replaceAll(chineseText[i], "" + (i + 1));
        }

        return text;
    }

    // ex. 五 -> 5
    // ex. 十五 -> 15
    private int getMonthNumber(String chinese) {
        String[] chineseText = {
            "一", "二", "三", "四", "五", "六", "七", "八", "九", "十"
        };

        int number = 0;

        for (int i = 0; i < chinese.length(); i++) {
            int tempNumber = 0;
            for (int j = 0; j < chineseText.length; j++) {
                if (chinese.indexOf(chineseText[j]) >= 0) {
                    tempNumber = i + 1;
                }
            }
            number = number * 10 + tempNumber;
        }

        return number;
    }

    private String getSingleDate(String text) {
        if (text.trim().matches(".+,")) {
            text = text.trim();
            text = text.replaceAll(",", "");
        }

        String temp = "";
        String[] temps2 = text.split("/");
        for (int j = 0; j < temps2.length; j++) {
            if (j == 0) {
                int month = 99;
                try {
                    month = Integer.parseInt(temps2[0]);
                } catch (Exception ex) {

                }
                if (month > 12 || month < 1) {
                    break;
                }
                temp += "" + month;
            } else if (j == 1) {
                int length = temps2[1].length() > 2 ? 2 : temps2[1].length();
                String dateString = temps2[1].substring(0, length);
                int date = 99;
                try {
                    date = Integer.parseInt(dateString);
                } catch (Exception ex) {

                }
                if (date > 31 || date < 1) {
                    break;
                }
                temp += "/" + date;
            }
        }

        print("getSingleDate: " + text + " -> " + temp);

        return temp;
    }

    private String getNumberString(String text, int beginIndex, int endIndex) {
        String numberString = "";
        String temp = "";
        boolean numberExisted = false;
        for (int i = beginIndex; i < endIndex; i++) {
            temp = text.substring(beginIndex, endIndex);

            if (temp.matches("\\d+")) {
                numberString += temp;
                numberExisted = true;
            }
        }

        return numberExisted ? numberString : null;
    }

    private String getTitleString(String text) {
        String temp = text.replaceAll("\\[.+\\]", "").trim();

        return text + NEW_TAG[0] + temp.trim() + NEW_TAG[1];
    }

    private boolean keywordExisted(String text, String[] keyword) {
        for (int i = 0; i < keyword.length; i++) {
            if (text.indexOf(keyword[i]) >= 0) {
                return true;
            }
        }

        return false;
    }

    //  ex. 9/10高雄、9/11台北-會議接待人員 -> 接待
    private String getCategoryString(String text) {
        /*
         var CATEGORY_ARRAY =  new Array( 
         '0', '活動協助', 'Party Assist', '1', '進場&撤場', 'Decorate & Recovery', '2', '賣場&專櫃', 'Sell', '3', '訪談&演出', 'Interview', '4', '其他', 'The other' );
         */
        String[] category = new String[]{"家庭日", "嘉年華", "活動", "研討會"};
        if (keywordExisted(text, category)) {
            return "活動";
        }

        category = new String[]{"進場", "撤場"};
        if (keywordExisted(text, category)) {
            return "進場";
        }

        category = new String[]{"專櫃", "賣場", "大潤發", "推銷", "特賣", "櫃位"};
        if (keywordExisted(text, category)) {
            return "賣場";
        }

        category = new String[]{"來賓", "訪談"};
        if (keywordExisted(text, category)) {
            return "訪談";
        }

        return "其他";
    }

    private boolean jobFinished(String title) {
        String[] keyword = {"已徵滿", "已徵到", "已滿", "結束徵"};
        for (int i = 0; i < keyword.length; i++) {
            if (title.indexOf(keyword[i]) >= 0) {
                return true;
            }
        }

        return false;
    }

    /*
   
     */
    @SuppressWarnings("empty-statement")
    public String getPhoneNumberString(String text) {
        int beginIndex, endIndex, atIndex;
        String temp = "";

        beginIndex = text.indexOf("09");
        if (beginIndex >= 0) {

            for (endIndex = beginIndex; endIndex < text.length(); endIndex++) {
                if (!text.substring(endIndex, endIndex + 1).matches("\\d|\\-|\\)|\\(|（|）")) {
                    break;
                }
            }

            temp = text.substring(beginIndex, endIndex);

            temp = temp.replaceAll("-|\\(|\\)|（|）|/", "");

            if (temp.length() > 9) {
                gPhoneNumber = temp.trim();
                return text + NEW_TAG[0] + temp.trim() + NEW_TAG[1];
            }
        }

        beginIndex = text.indexOf("0");
        if (beginIndex >= 0) {
            for (endIndex = beginIndex; endIndex < text.length(); endIndex++) {
                if (!text.substring(endIndex, endIndex + 1).matches("\\d|\\-|\\)|\\(|（|）")) {
                    break;
                }
            }

            temp = text.substring(beginIndex, endIndex);

            temp = temp.replaceAll("-|\\(|\\)|（|）|/", "").trim();

            if (temp.length() > 8) {
                gPhoneNumber = temp;
                return text + NEW_TAG[0] + temp.trim() + NEW_TAG[1];
            }
        }

        return text + NEW_TAG[0] + "" + NEW_TAG[1];
    }

    public String getMailAddressString(String text) {
        int beginIndex, endIndex, atIndex;
        String temp = "";
        if (text.matches("(?s).*\\w+@\\w+(?s).*")) {
            atIndex = text.indexOf("@");

            for (beginIndex = atIndex; beginIndex > 0; beginIndex--) {
                if (!text.substring(beginIndex - 1, beginIndex).matches("\\w|\\.|\\-")) {
                    break;
                }
            }

            for (endIndex = atIndex + 1; endIndex < text.length(); endIndex++) {
                if (!text.substring(endIndex, endIndex + 1).matches("\\w|\\.")) {
                    break;
                }
            }

            if (beginIndex >= 0 && endIndex > 0) {
                temp = text.substring(beginIndex, endIndex);
            } else {
                temp = "";
            }

            temp = temp.replaceAll("\\(|\\)|（|）|/", "").trim();

            gMailAddress = temp.trim();

            return text + NEW_TAG[0] + temp.trim() + NEW_TAG[1];
        }

        return text + NEW_TAG[0] + "" + NEW_TAG[1];
    }

    /*
     // http://maps.google.com/maps?q=

     */
    public String getAddressString(String text) {
        String temp = text;

        if (text.matches("(?s).*\\((?s).+\\)(?s).*")) {
            int beginIndex = text.indexOf("(");
            int endIndex = text.indexOf(")", beginIndex);

            temp = text.substring(beginIndex, endIndex);
            if (temp.indexOf("路") > 0
                    && (temp.indexOf("縣") > 0 || temp.indexOf("市") > 0 || temp.indexOf("區") > 0)) {
                return text + NEW_TAG[0] + temp.trim() + NEW_TAG[1];
            }

            if (beginIndex > 0 && endIndex > 0) {
                temp = text.substring(0, beginIndex);
            }
            //temp = text.replaceAll("\\((?s).+\\)", "");
            //temp = temp.replaceAll("", "");

            return text + NEW_TAG[0] + temp.trim() + NEW_TAG[1];
        }

        if (text.matches("(?s).+(縣|市|路)+(?s).*(，|,)+(?s).+")) {
            temp = text.split("，|,")[0];

            Common.debugPrintln("----OLD---->" + text);
            Common.debugPrintln("----NEW---->" + temp);
            //System.exit(0);
            return text + NEW_TAG[0] + temp.trim() + NEW_TAG[1];
        }

        return text + NEW_TAG[0] + "" + NEW_TAG[1];
    }

    private String getCountyString(String title, String address) {
        int beginIndex, endIndex;
        String temp = "";
        boolean countyExisted = false;
        if (address.matches("(?s).+縣(?s).+")) {
            endIndex = address.indexOf("縣");
            beginIndex = endIndex - 2;

            if (beginIndex >= 0) {
                countyExisted = true;
                temp = address.substring(beginIndex, endIndex);
            }
        }

        if (!countyExisted && address.matches("(?s).+市(?s).+")) {
            endIndex = address.indexOf("市");
            beginIndex = endIndex - 2;

            if (beginIndex >= 0) {
                countyExisted = true;
                temp = address.substring(beginIndex, endIndex);
            }
        }

        /*
         if (!countyExisted) {
         beginIndex = title.indexOf("[") + 1;
         endIndex = title.indexOf("/", beginIndex);

         if (beginIndex > 0 && endIndex > 0) {
         countyExisted = true;
         temp = title.substring(beginIndex, endIndex);
         }
         }
         */
        return countyExisted ? temp : null;
    }

    private String getLocationString(String title, String address) {
        //private int getLocationIndex(String title, String address) {

        List<String[]> locationList = new ArrayList<String[]>();

        locationList.add(new String[]{"台北", "臺北", "中正", "大同", "中山",
            "萬華", "信義", "松山", "大安", "南港", "北投", "內湖", "士林", "文山"});
        locationList.add(new String[]{"新北", "板橋", "新莊", "泰山", "林口",
            "淡水", "金山", "八里", "萬里", "石門", "三芝", "瑞芳", "汐止", "平溪",
            "貢寮", "雙溪", "深坑", "石碇", "新店", "坪林", "烏來", "中和", "永和",
            "土城", "三峽", "樹林", "鶯歌", "三重", "蘆洲", "五股"});
        locationList.add(new String[]{"台中", "臺中", "台中", "北屯", "西屯",
            "南屯", "太平", "大里", "霧峰", "烏日", "豐原", "后里", "東勢", "石岡",
            "新社", "和平", "神岡", "潭子", "大雅", "大肚", "龍井", "沙鹿", "梧棲",
            "清水", "大甲", "外埔", "大安"});
        locationList.add(new String[]{"台南", "臺南", "中西", "安平", "安南",
            "永康", "歸仁", "新化", "玉井", "楠西", "南化", "仁德", "關廟",
            "龍崎", "官田", "麻豆", "佳里", "西港", "七股", "將軍", "學甲", "北門",
            "新營", "後壁", "白河", "東山", "六甲", "下營", "柳營", "鹽水", "善化",
            "大內", "山上", "新", "安定"});
        locationList.add(new String[]{"高雄", "楠梓", "左營", "鼓山", "三民",
            "鹽埕", "前金", "新興", "苓雅", "小港", "旗津", "鳳山", "大寮", "鳥松",
            "林園", "仁武", "大樹", "大社", "岡山", "路竹", "橋頭", "梓官", "彌陀",
            "永安", "燕巢", "田寮", "阿蓮", "茄萣", "湖內", "旗山", "美濃", "內門",
            "杉林", "甲仙", "六龜", "茂林", "桃源", "那瑪夏"});
        locationList.add(new String[]{"基隆", "仁愛", "中正", "信義", "中山",
            "安樂", "暖暖", "七堵"});
        locationList.add(new String[]{"新竹", "香山"});
        locationList.add(new String[]{"嘉義"});
        locationList.add(new String[]{"桃園", "桃園", "中壢", "八德", "楊梅",
            "蘆竹", "大溪", "龍潭", "龜山", "大園", "觀音", "新屋", "復興"});
        locationList.add(new String[]{"新竹", "竹北", "竹東", "新埔", "關西",
            "峨眉", "寶山", "北埔", "橫山", "芎林", "湖口", "新豐", "尖石", "五峰"});
        locationList.add(new String[]{"苗栗", "苗栗", "通霄", "苑裡", "竹南",
            "頭份", "後龍", "卓蘭", "西湖", "頭屋", "公館", "銅鑼", "三義", "造橋",
            "三灣", "南庄", "大湖", "獅潭", "泰安"});
        locationList.add(new String[]{"彰化", "彰化", "員林", "和美", "鹿港",
            "溪湖", "二林", "田中", "北斗", "花壇", "芬園", "大村", "永靖", "伸港",
            "線西", "福興", "秀水", "埔心", "埔鹽", "大城", "芳苑", "竹塘", "社頭",
            "二水", "田尾", "埤頭", "溪州"});
        locationList.add(new String[]{"南投", "南投", "埔里", "草屯", "竹山",
            "集集", "名間", "鹿谷", "中寮", "魚池", "國姓", "水里", "信義", "仁愛"});
        locationList.add(new String[]{"雲林", "斗六", "斗南", "虎尾", "西螺",
            "土庫", "北港", "莿桐", "林內", "古坑", "大埤", "崙背", "二崙", "麥寮",
            "台西", "臺西", "東勢", "褒忠", "四湖", "口湖", "水林", "元長"});
        locationList.add(new String[]{"嘉義", "太保", "朴子", "布袋", "大林",
            "民雄", "溪口", "新港", "六腳", "東石", "義竹", "鹿草", "水上", "中埔",
            "竹崎", "梅山", "番路", "大埔", "阿里山"});
        locationList.add(new String[]{"屏東", "屏東", "潮州", "東港", "恆春",
            "萬丹", "長治", "麟洛", "九如", "里港", "鹽埔", "高樹", "萬巒", "內埔",
            "竹田", "新埤", "枋寮", "新園", "崁頂", "林邊", "南州", "佳冬", "琉球",
            "車城", "滿州", "枋山", "霧台", "瑪家", "泰武", "來義", "春日", "獅子",
            "牡丹", "三地門"});
        locationList.add(new String[]{"宜蘭", "宜蘭", "羅東", "蘇澳", "頭城",
            "礁溪", "壯圍", "員山", "冬山", "五結", "三星", "大同", "南澳"});
        locationList.add(new String[]{"花蓮", "花蓮", "鳳林", "玉里", "新城",
            "吉安", "壽豐", "秀林", "光復", "豐濱", "瑞穗", "萬榮", "富里", "卓溪"});
        locationList.add(new String[]{"台東", "臺東", "成功", "關山", "長濱",
            "海端", "池上", "東河", "鹿野", "延平", "卑南", "金峰", "大武", "達仁",
            "綠島", "蘭嶼", "太麻里"});
        locationList.add(new String[]{"澎湖", "馬公", "湖西", "白沙", "西嶼",
            "望安", "七美"});
        locationList.add(new String[]{"金門", "金城", "金湖", "金沙", "金寧",
            "烈嶼", "烏坵"});
        locationList.add(new String[]{"連江", "南竿", "北竿", "莒光", "東引"});

        for (int i = 0; i < locationList.size(); i++) {
            String[] tempLocations = locationList.get(i);
            for (int j = 0; j < tempLocations.length; j++) {
                if (address.indexOf(tempLocations[j]) >= 0) {
                    return tempLocations[0];
                }
            }
        }

        for (int i = 0; i < locationList.size(); i++) {
            String[] tempLocations = locationList.get(i);
            for (int j = 0; j < tempLocations.length; j++) {
                if (title.indexOf(tempLocations[j]) >= 0) {
                    return tempLocations[0];
                }
            }
        }

        return "NONE";
    }

    public String getContent(String wholeText) {
        int beginIndex = wholeText.indexOf("：") + 1;
        if (beginIndex == 0) {
            beginIndex = wholeText.indexOf("︰") + 1;
        }

        String text = wholeText.substring(beginIndex, wholeText.length());

        text = text.trim();
        text = text.replaceAll("\n", "<br><br>");
        text = text.replaceAll("\r", "");
        text = text.replaceAll("\"", "'");
        text = removeDuplicateComma(text);

        return text;
    }

    public void print(String message) {
        Common.debugPrintln(message);
    }

    public String getLocalString(String text, String beginKeyword) {
        return getLocalString(text, beginKeyword, "\n", false);
    }

    public String getLocalString(String text, String beginKeyword, boolean toTheEnd) {
        return getLocalString(text, beginKeyword, "\n", toTheEnd);
    }

    public String getLocalString(String text, String beginKeyword, String endKeyword) {
        return getLocalString(text, beginKeyword, endKeyword, false);
    }

    public String getLocalString(String text, String beginKeyword, String endKeyword, boolean toTheEnd) {
        int beginIndex = 0;
        int endIndex = 0;
        beginIndex = text.indexOf(beginKeyword);
        //beginIndex = text.indexOf("︰", beginIndex) + 1;
        endIndex = toTheEnd ? text.length() : text.indexOf(endKeyword, beginIndex + beginKeyword.length());
        //print("" + beginIndex + "," + endIndex);

        if (beginIndex > endIndex || beginIndex <= 0 || endIndex <= 0) {
            if (text.length() < 100) {
                print(beginKeyword + "," + endKeyword + " : \n" + text);
            }
            return "";
        } else {
            String tempText = text.substring(beginIndex, endIndex);
            tempText = replaceNCR(tempText);
            tempText = tempText.replaceAll("<span.+>|</span>|★|◎", "");
            return tempText.trim();
        }
    }

    @Override // 因為原檔就是utf8了，所以無須轉碼
    public String getAllPageString(String urlString) {

        String indexName = Common.getStoredFileName(SetUp.getTempDirectory(), "index_ptt_", "html");
        Common.downloadFile(urlString, SetUp.getTempDirectory(), indexName, true, cookie);

        return Common.getFileString(SetUp.getTempDirectory(), indexName);
    }

    @Override
    public boolean isSingleVolumePage(String urlString) {
        Common.debugPrintln("parse " + pageParsedCount);
        pageParsedCount++;

        if (Flag.allowDownloadFlag) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String getTitleOnMainPage(String urlString, String allPageString) {
        return "PTT";
    }

    @Override
    public List<List<String>> getVolumeTitleAndUrlOnMainPage(String urlString, String allPageString) {
        // combine volumeList and urlList into combinationList, return it.

        List<List<String>> combinationList = new ArrayList<List<String>>();
        List<String> urlList = new ArrayList<String>();
        List<String> volumeList = new ArrayList<String>();

        int beginIndex = 0, endIndex = 0;

        totalVolume = 1;
        urlList.add(urlString);

        beginIndex = allPageString.indexOf("<title>");
        beginIndex = allPageString.indexOf(">", beginIndex) + 1;
        endIndex = allPageString.indexOf(" - ", beginIndex);

        String title = allPageString.substring(beginIndex, endIndex).trim();
        title = getRegularFileName(title);
        volumeList.add(Common.getStringRemovedIllegalChar(title.trim()));

        /*
         totalVolume = allPageString.split("class=\"r-ent\">").length - 1;

         String volumeURL = "";
         String volumeTitle = "";
         for (int i = 0; i < totalVolume; i++) {

         // 取得單集位址
         beginIndex = allPageString.indexOf("class=\"r-ent\">", beginIndex);
         beginIndex = allPageString.indexOf("href=", beginIndex);
         beginIndex = allPageString.indexOf("\"", beginIndex) + 1;
         endIndex = allPageString.lastIndexOf("\"", beginIndex);
         volumeURL = allPageString.substring(beginIndex, endIndex);
         urlList.add(baseURL + volumeURL);

         // 取得單集名稱
         beginIndex = allPageString.indexOf(">", beginIndex) + 1;
         endIndex = allPageString.indexOf("</a>", beginIndex);
         volumeTitle = allPageString.substring(beginIndex, endIndex);
         volumeList.add(Common.getStringRemovedIllegalChar(volumeTitle.trim()));
         }
         */
        Common.debugPrintln("共有" + totalVolume + "集");

        combinationList.add(volumeList);
        combinationList.add(urlList);

        return combinationList;
    }
}
