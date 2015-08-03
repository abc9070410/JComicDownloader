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

public class ParseWiki extends ParseCKNovel {

    private int radixNumber; // use to figure out the name of pic
    private String jsName;
    protected String indexName;
    protected String indexEncodeName;
    protected String baseURL;
    protected int floorCountInOnePage; // 一頁有幾層樓
    protected String cookie;

    protected static int pageParsedCount;

    private List<String> wikiUrlList = new ArrayList<String>();
    private List<String> wikiNameList = new ArrayList<String>();
    private List<String> wikiCoverList = new ArrayList<String>();

    /**
     *
     * @author user
     */
    public ParseWiki() {
        enumName = "WIKI";
        regexs= new String[]{"(?s).*wikipedia.org/(?s).*"};
        parserName=this.getClass().getName();
        novelSite=true;
        siteID=Site.formString("WIKI");
        siteName = "Wiki";
        indexName = Common.getStoredFileName(SetUp.getTempDirectory(), "index_wiki_parse_", "html");
        indexEncodeName = Common.getStoredFileName(SetUp.getTempDirectory(), "index_wiki_encode_parse_", "html");

        jsName = "index_wiki.js";
        radixNumber = 156661; // default value, not always be useful!!

        baseURL = "http://en.wikipedia.org";

        floorCountInOnePage = 10; // 一頁有幾層樓

        cookie = "djAX_e8d7_agree=6; "; // 因應分級頁面

        pageParsedCount = 0;
    }

    @Override
    public void parseComicURL() { // parse URL and save all URLs in comicURL  //
        // 先取得前面的下載伺服器網址

        Common.debugPrintln("無法得知有幾頁，需一步步檢查");
        String allPageString = "";
        String tempPageSring = "";
        String tempString = "";
        String pageURL = webSite; // 將原本頁面轉為庫存頁面;
        int beginIndex = 0;
        int endIndex = 0;
        int p = 1; // 目前頁數

        String name = "";

        totalPage = p;

        allPageString = getAllPageString(pageURL);
        //Common.debugPrintln( allPageString );

        // get Name
        beginIndex = allPageString.indexOf("<title>", beginIndex) + 7;
        endIndex = allPageString.indexOf(" - ", beginIndex);
        name = allPageString.substring(beginIndex, endIndex).trim();
        Common.debugPrintln("NAME: " + name);

        int count = allPageString.split("/wiki/").length - 1;

        beginIndex = 0;
        for (int i = 0; i < count; i++) {
            beginIndex = allPageString.indexOf("/wiki/", beginIndex);
            endIndex = allPageString.indexOf("\"", beginIndex);
            tempString = allPageString.substring(beginIndex, endIndex);
            Common.debugPrintln("URL: " + tempString);

            if (isPersonPage(tempString) && !isExistedPage(tempString)) {
                CommonGUI.stateBarDetailMessage = "共" + count + "頁，第" + (i + 1) + "頁下載中";
                handlePersonPage(baseURL + tempString);

            }

            beginIndex = endIndex;
        }

        // 儲存
        name = name.replaceAll("%E2%80%93", "").replaceAll("\\(|\\)", "").replaceAll("'", "");
        
        if (pageURL.indexOf("from=") > 0)
        {
            int tempBeginIndex = pageURL.indexOf( "from=" ) + 5;
            name += pageURL.substring( tempBeginIndex, pageURL.length());
        }
        
        String fileName = Common.getStringRemovedIllegalChar(name).replaceAll(" ", "") + ".js";
        saveListFile(fileName);
    }

    private void saveListFile(String fileName) {
        String tag = fileName.substring(0, fileName.length() - 3);

        String allText = "";
        allText += "\nvar gasNameList_" + tag + " = new Array( ";
        for (int i = 0; i < wikiNameList.size(); i++) {
            if (i > 0) {
                allText += ", // " + i;
            }
            allText += "\n\"" + wikiNameList.get(i) + "\"";
        }
        allText += "\n);";

        allText += "\nvar gasCoverList_" + tag + " = new Array( ";
        for (int i = 0; i < wikiCoverList.size(); i++) {
            if (i > 0) {
                allText += ", // " + i;
            }
            allText += "\n\"" + wikiCoverList.get(i) + "\"";
        }
        allText += "\n);";

        allText += "\nvar gsDirectory_" + tag + " = \"" + getWholeTitle() + "\";";

        // 清單檔案放在外層
        String tempString = getDownloadDirectory();
        tempString = tempString.substring(0, tempString.length() - 3);
        int endIndex = tempString.lastIndexOf(Common.getSlash()) + 1;
        String listDownloadDirectory = tempString.substring(0, endIndex);
        Common.outputFile(allText, listDownloadDirectory, fileName);
    }

    private boolean isExistedPage(String urlString) {
        for (int j = 0; j < wikiUrlList.size(); j++) {
            if (wikiUrlList.get(j).matches(".*" + urlString + ".*")) {
                Common.debugPrintln("之前解析過這個了: " + urlString);
                return true;
            }
        }

        return false;
    }

    public boolean isOtherPageByTitle(String title) {
        String[] tokens = {
            "F.C.", "U.S.",
            "LA Galaxy", "D.C.",
            " Club", "Club ",
            "USA ", " USA",
            "Premiership",
            "U.C.", " FC",
            " BSC", "FC ",
            "aseball", "ootball",
            "asketball", "occer",
            "WWE", "WWF", "WCW",
            "Professional", "Family name",
            "Wikimedia Commons",
            ", "
        };

        for (int i = 0; i < tokens.length; i++) {
            if (title.indexOf(tokens[i]) >= 0) {
                return true;
            }
        }

        return false;
    }

    public boolean isOtherPageByURL(String urlString) {
        String[] teams = {
            "Boston", "Brooklyn",
            "_Arena", "New_York",
            "Madison_Square_Garden", "Philadelphia",
            "Wells_Fargo", "Toronto",
            "Chicago", "_City",
            "Cleveland", "Los_Angeles",
            "Detroit", "The_Palace_of_Auburn_Hills",
            "Michigan", "Indiana",
            "Bankers_Life", "Milwaukee",
            "_Center", "Atlanta",
            "Philips_Arena", "Charlotte",
            "Miami", "Orlando",
            "Washington", "Denver",
            "Minnesota", "Oklahoma",
            "Portland", "Utah",
            "Golden_State", "Phoenix",
            "Sacramento", "Houston",
            "Memphis", "FedEx",
            "New_Orleans", "San_Antonio",
            "Dallas",
            //  contruny
            "Puerto_Rico", "United_",
            "_Republic", "Korea",
            "_Union", "Germany",
            "South_", "(country)",
            "New_", "_Germany",
            "Republic", "Beijing",
            "Bayer", "Watford",
            // 場所
            "University", "Park",
            "League", "Field",
            "Kingdom", "Florida",
            "Stadium",
            // 其他
            "NBA", "English", "Channel",
            // MLB
            "Baltimore", "Seattle",
            "Cincinnati", "Arizona",
            "Montreal", "Pittsburgh",
            "World", "Series",
            "Orix", "Pittsburgh",
            "Arizona", "Third",
            "Oakland", "Montreal",
            "Texas", "Boston",
            "Tampa_", "Toronto",
            "Kansas", "_Louis",
            "Colorado", "Francisco",
            "Fresno", "America",
            "Providence", "Buffalo",
            // other
            "ESPN", "Yahoo",
            "Sports", "Opening",
            "baseman", "Honkbal",
            "Mediterranean", "_Sea",
            "Ocean", "Constitutional",
            "Islands", "Bacharach",
            "Hilldale", "Homestead",
            "Club", "United",
            "Association", "AIK",
            "AFC", "WWE",
            "Ontario", "Stamford",
            "Georgia", "Professional",
            "Academy",
            // contruny
            "German", "Austrian",
            "Europe", "Italian",
            "Belgian", "Hague",
            "Empire", "Republic",
            "Britain", "Austria",
            "Monarchy", "France",
            "Carolina1"

        };

        for (int i = 0; i < teams.length; i++) {
            if (urlString.indexOf(teams[i]) > 0) {
                return true;
            }
        }
        return false;
    }

    public boolean isPersonPage(String urlString) {

        // 拿掉球隊
        if (isOtherPageByURL(urlString)) {
            return false;
        }

        if (urlString.indexOf("Main_Page") > 0) // ex. /wiki/Main_Page
        {
            return false;
        }
        if (urlString.indexOf(":") > 0) // ex. /wiki/File:Cruz_Roja.svg
        {
            return false;
        }
        if (Common.getAmountOfString(urlString, "_") == 1) // ex. /wiki/Lavoy_Allen
        {
            return true;
        }

        if (Common.getAmountOfString(urlString, "_") == 2) {
            int firstIndex, secondIndex;

            firstIndex = urlString.indexOf("_");
            secondIndex = urlString.indexOf("_", firstIndex + 1);

            if (".".equals(urlString.substring(firstIndex - 1, firstIndex))) // ex. /wiki/B._J._Armstrong
            {
                return true;
            }

            if ("(".equals(urlString.substring(secondIndex + 1, secondIndex + 2))) // ex. /wiki/Ron_Anderson_(basketball,_born_1958)
            {
                return true;
            }

        }

        return false;
    }

    public void handlePersonPage(String urlString) {
        String allPageString = getAllPageString(urlString);
        String tempPageSring = "";
        String tempString = "";
        String lastTag = "See also";
        int beginIndex = 0;
        int endIndex = 0;

        String name = "";
        String coverURL = "";
        String basicIntroduction = "";
        List<String> contentTitleList = new ArrayList<String>();
        List<String> contentTagList = new ArrayList<String>();
        List<String> contentClassList = new ArrayList<String>();
        List<String> contentContentList = new ArrayList<String>();

        // get Name
        beginIndex = allPageString.indexOf("<title>", beginIndex) + 7;
        endIndex = allPageString.indexOf(" - ", beginIndex);
        name = allPageString.substring(beginIndex, endIndex).replaceAll("\"", "");
        name = name.replace("(basketball)", "").trim();
        Common.debugPrintln("#NAME: " + name);

        if (isOtherPageByTitle(name)) {
            Common.debugPrintln("這是其他的頁面:" + name);
            return;
        }

        // get cover URL
        beginIndex = allPageString.indexOf("class=\"image\"");
        if (beginIndex > 0) {
            beginIndex = allPageString.indexOf("//upload.wikimedia.org/wikipedia/", beginIndex);
            endIndex = allPageString.indexOf("\"", beginIndex);
            coverURL = "http:" + allPageString.substring(beginIndex, endIndex).trim();
        }

        if (beginIndex < 0
                || coverURL.indexOf("Question_book") >= 0
                || coverURL.indexOf("Map_of_USA_and_Canada") >= 0
                || coverURL.indexOf("P_vip") >= 0
                || coverURL.indexOf("Basketball") >= 0
                || coverURL.indexOf("5_star") >= 0
                || coverURL.indexOf(".svg") >= 0) {
            Common.debugPrintln("沒有封面的就不繼續解析: " + name);
            return;
        }

        int coverIndex = beginIndex;

        Common.debugPrintln("#Cover: " + coverURL);

        // get basic introduction
        beginIndex = allPageString.indexOf("</table>");
        //beginIndex = allPageString.indexOf("</table>", beginIndex);
        endIndex = allPageString.indexOf("<h2>Contents</h2>", beginIndex);
        beginIndex = allPageString.lastIndexOf("</table>", endIndex);

        if (beginIndex < 0 || endIndex < 0) {
            Common.debugPrintln("沒有簡介的就不繼續解析: " + name);
            return;
        }

        if (coverIndex > endIndex) {
            Common.debugPrintln("圖片不是最上方的封面，不繼續解析: " + coverURL);
            return;
        }

        basicIntroduction = allPageString.substring(beginIndex, endIndex);
        basicIntroduction = replaceProcessToText(basicIntroduction);
        //Common.debugPrintln( "#BI: " + basicIntroduction );

        // get content list
        beginIndex = endIndex;
        beginIndex = allPageString.indexOf("<li class=", beginIndex);
        endIndex = allPageString.indexOf(">" + lastTag + "</span", beginIndex);

        if (endIndex < 0) {
            lastTag = "References";
            endIndex = allPageString.indexOf(">" + lastTag + "</span", beginIndex);

            if (endIndex < 0) {
                Common.debugPrintln("沒有See also和References，不解析了");
                return;
            }
        }

        endIndex += 20;

        tempPageSring = allPageString.substring(beginIndex, endIndex);

        //Common.debugPrintln( "# : " + tempPageSring );
        //System.exit( 0 );
        beginIndex = 0;
        int count = 0;
        int biggestIndex = 0;
        while (true) {
            Common.debugPrintln("> " + count++);

            // get class
            beginIndex = tempPageSring.indexOf("<li class=", beginIndex) + 11;

            // 只能往後找
            if (biggestIndex < beginIndex) {
                biggestIndex = beginIndex;
            } else {
                break;
            }

            endIndex = tempPageSring.indexOf("\"", beginIndex);
            tempString = tempPageSring.substring(beginIndex, endIndex);
            tempString = tempString.replaceAll("toclevel-|tocsection-", "");
            contentClassList.add(tempString);
            //Common.debugPrintln( "#class : " + tempString );

            // get link
            beginIndex = tempPageSring.indexOf(" href=", beginIndex);
            beginIndex = tempPageSring.indexOf("#", beginIndex) + 1;
            endIndex = tempPageSring.indexOf("\"", beginIndex);
            tempString = tempPageSring.substring(beginIndex, endIndex);
            contentTagList.add(tempString);
            //Common.debugPrintln( "#tag : " + tempString );

            // get title
            beginIndex = tempPageSring.indexOf("class=\"toctext\"", beginIndex);
            beginIndex = tempPageSring.indexOf(">", beginIndex) + 1;
            endIndex = tempPageSring.indexOf("<", beginIndex);
            tempString = tempPageSring.substring(beginIndex, endIndex).trim();
            contentTitleList.add(tempString);
            Common.debugPrintln("#title : " + tempString);

            if (tempString.indexOf(lastTag) >= 0) {
                break;
            }

            beginIndex = endIndex;
        }

        beginIndex = 0;
        // get content's content
        for (int i = 0; i < contentTitleList.size() - 1; i++) {
            tempString = "id=\"" + contentTagList.get(i);
            beginIndex = allPageString.indexOf(tempString, beginIndex);
            beginIndex = allPageString.indexOf(">", beginIndex) + 1;
            tempString = "id=\"" + contentTagList.get(i + 1);
            endIndex = allPageString.indexOf(tempString, beginIndex);
            endIndex = allPageString.indexOf(">", endIndex) + 1;

            if (beginIndex < 0 || endIndex < 0 || beginIndex >= endIndex) {
                Common.debugPrintln("超出範圍: " + beginIndex + " ~ " + endIndex);
                continue;
            }

            tempPageSring = allPageString.substring(beginIndex, endIndex);
            tempPageSring = replaceProcessToText(tempPageSring);
            contentContentList.add(tempPageSring);

            Common.debugPrintln("#CTitle: " + contentTitleList.get(i));
            Common.debugPrintln("#CTag: " + contentTagList.get(i));
            Common.debugPrintln("#CC: " + beginIndex + "," + endIndex);//contentContentList.get( i ) );
            if (contentContentList.get(i).length() > 110) {
                Common.debugPrintln("-->" + contentContentList.get(i).substring(0, 100));
            } else {
                Common.debugPrintln("--TOO_SHORT->" + contentContentList.get(i));
            }

        }

        // save this js file
        name = toJsString(name);
        String allText = "";
        allText += "\ngsCurrentURL = \"" + urlString + "\";";
        allText += "\ngsCurrentName = \"" + name + "\";";
        allText += "\ngsCurrentPicURL = \"" + coverURL + "\";";
        allText += "\ngsCurrentBasicIntroduction = \"" + toJsString(basicIntroduction) + "\"";

        count = contentContentList.size();
        Boolean textDeleted[] = new Boolean[count];
        Boolean existText = false;

        Common.debugPrintln("比較: " + contentContentList.size() + "," + contentTitleList.size() + contentClassList.size());

        allText += "\ngasCurrentContent = new Array( ";
        for (int i = 0; i < count; i++) {
            String tempText = toJsString(contentContentList.get(i), contentTitleList.get(i));

            if (tempText.length() < 200) {
                Common.debugPrintln(contentClassList.get(i) + " __ " + contentTitleList.get(i) + " __ " + i + " : " + tempText);
            } else {
                Common.debugPrintln(contentClassList.get(i) + " __ " + contentTitleList.get(i) + " __ " + i + " : " + tempText.substring(0, 100));
            }

            if ("".equals(tempText)) {
                boolean sholdNotPass = false;

                // 如果是第一層才暫時保留，下面再檢查要不要留
                if (contentClassList.get(i).matches("1.+")) {
                    sholdNotPass = true;
                }

                // 如果是第一層，就檢查下個第一層之前有沒有實質內容\
                for (int j = i + 1; !sholdNotPass && j < count; j++) {
                    String tempText2 = toJsString(contentContentList.get(j), contentTitleList.get(j));
                    if (!"".equals(tempText2)) {
                        sholdNotPass = true;
                    }
                }

                if (!sholdNotPass) {
                    Common.debugPrintln("PASS : " + contentClassList.get(i) + contentTitleList.get(i));
                    textDeleted[i] = true;
                    continue;
                }
            }

            // 如果標題有統計(statistics)，就當作無用內容跳過
            if (contentTitleList.get(i).indexOf("statistics") > 0) {
                textDeleted[i] = true;
                continue;
            }

            // 字數太少也直接咖掉
            if (!contentClassList.get(i).matches("1.+") && contentContentList.get(i).length() < 100) {
                textDeleted[i] = true;
                continue;
            }

            textDeleted[i] = false;

            if (existText) {
                allText += ", ";
            }
            allText += "\n\"" + tempText + "\"";

            existText = true;
        }
        allText += "\n);";

        existText = false;

        allText += "\ngasCurrentContentClass = new Array( ";
        for (int i = 0; i < count; i++) {
            if (textDeleted[i]) {
                continue;
            }

            if (existText) {
                allText += ", ";
            }
            allText += "\n\"" + toJsString(contentClassList.get(i)) + "\"";
            existText = true;
        }
        allText += "\n);";

        existText = false;

        allText += "\ngasCurrentContentTitle = new Array( ";
        for (int i = 0; i < count; i++) {
            if (textDeleted[i]) {
                continue;
            }

            if (existText) {
                allText += ", ";
            }
            allText += "\n\"" + toJsString(contentTitleList.get(i)) + "\"";
            existText = true;
        }
        allText += "\n);";

        name = getASCIIFileName(name);

        String fileName = name.replaceAll(" ", "").trim() + ".js";

        // 防止重複寫入紀錄的第二道防線
        if (new File(getDownloadDirectory() + fileName).exists()) {
            return;
        }

        Common.outputFile(allText, getDownloadDirectory(), fileName);

        wikiUrlList.add(urlString);
        wikiNameList.add(name);
        wikiCoverList.add(coverURL);
    }

    private String getASCIIFileName(String fileName) {

        fileName = fileName.replaceAll("\\ʻ", "");
        fileName = fileName.replaceAll("Á", "A");
        fileName = fileName.replaceAll("ō", "o");
        fileName = fileName.replaceAll("ó", "o");
        fileName = fileName.replaceAll("é", "e");
        fileName = fileName.replaceAll("á", "a");
        fileName = fileName.replaceAll("Ń", "N");
        fileName = fileName.replaceAll("ń", "n");
        fileName = fileName.replaceAll("Ḿ", "M");
        fileName = fileName.replaceAll("ḿ", "m");
        fileName = fileName.replaceAll("Í", "I");
        fileName = fileName.replaceAll("í", "i");
        fileName = fileName.replaceAll("Ǵ", "G");
        fileName = fileName.replaceAll("ǵ", "g");
        fileName = fileName.replaceAll("É", "E");
        fileName = fileName.replaceAll("é", "e");
        fileName = fileName.replaceAll("Ć", "C");
        fileName = fileName.replaceAll("ć", "c");
        fileName = fileName.replaceAll("Ĺ", "L");
        fileName = fileName.replaceAll("ĺ", "l");
        fileName = fileName.replaceAll("Ś", "S");
        fileName = fileName.replaceAll("ś", "s");
        fileName = fileName.replaceAll("ü", "u");
        fileName = fileName.replaceAll("ğ", "g");
        fileName = fileName.replaceAll("ü", "u");
        fileName = fileName.replaceAll("č", "c");
        fileName = fileName.replaceAll("š", "s");
        fileName = fileName.replaceAll("ý", "y");
        fileName = fileName.replaceAll("š", "s");
        fileName = fileName.replaceAll("ë", "e");
        fileName = fileName.replaceAll("ū", "u");
        fileName = fileName.replaceAll("č", "c");
        fileName = fileName.replaceAll("i", "i");
        fileName = fileName.replaceAll("ū", "u");
        fileName = fileName.replaceAll("ž", "z");
        fileName = fileName.replaceAll("Š", "S");
        fileName = fileName.replaceAll("š", "s");
        fileName = fileName.replaceAll("ý", "y");
        fileName = fileName.replaceAll("Ž", "Z");
        fileName = fileName.replaceAll("đ", "d");
        fileName = fileName.replaceAll("č", "c");
        fileName = fileName.replaceAll("ç", "c");
        fileName = fileName.replaceAll("Ó", "O");
        fileName = fileName.replaceAll("ë", "e");
        fileName = fileName.replaceAll("Ö", "O");
        fileName = fileName.replaceAll("ş", "s");
        fileName = fileName.replaceAll("ç", "c");
        fileName = fileName.replaceAll("ı", "i");
        fileName = fileName.replaceAll("ņ", "n");
        fileName = fileName.replaceAll("ș", "s");

        return fileName;
    }

    public String toJsString(String text, String takeOff) {
        text = toJsString(text);

        // 拿掉章節內容最前方的章節名稱
        if (text.indexOf(takeOff) == 0) {
            text = text.substring(takeOff.length(), text.length());
        }

        return text;
    }

    public String toJsString(String text) {
        text = text.replaceAll("\\[\\d+\\]", "");
        text = text.replaceAll("\\[edit\\]", "");
        text = text.replaceAll("\\[note \\d+\\]", "");
        text = text.replaceAll("\\[citation needed\\]", "");
        text = text.replaceAll("\\[according to whom\\?\\]", "");
        text = text.replaceAll("\\[who\\?\\]", "");
        text = text.replaceAll("\"", "'");
        text = text.replaceAll("\n", "<br><br>");
        text = text.replaceAll("\r", "");

        // 把簡介前面多餘的拿掉
        String temp = "<br><br><br><br><br><br><br><br><br><br><br><br>";
        int beginIndex = text.indexOf(temp);

        if (beginIndex < 0) {
            beginIndex = text.indexOf("<br><br><br>Official website");

            if (beginIndex > 0) {
                beginIndex = text.indexOf("<br>", beginIndex + 13);

                if (beginIndex < 0) {
                    beginIndex = 0;
                }

                while (text.substring(beginIndex, text.length()).indexOf("<br>") == 0) {
                    beginIndex += 4;
                }
                beginIndex -= 4;
            }
        }

        if (beginIndex >= 0) {
            text = text.substring(beginIndex + temp.length(), text.length());
            Common.debugPrintln("拿掉多於簡介 : " + beginIndex);
        }

        if (text.indexOf("HomeAway") == 0) {
            text = text.substring(8, text.length());
        }

        text = text.replaceAll("<br><br><br><br>", "<br><br>");

        // 把最後面的<br>拿掉
        while (text.matches(".*<br>")) {
            text = text.substring(0, text.length() - 4);
        }

        if (text.indexOf("YearTeamGP") >= 0 || // 拿掉生涯數據
                text.indexOf("StatHighTeamOpponentDate") >= 0 || // 拿掉生涯隊伍
                text.indexOf("GPGames played") >= 0 || // 拿掉標題的生涯欄位
                text.indexOf("seasonGGames") >= 0 || //  拿掉生涯紀錄
                text.indexOf("SeasonTeam") >= 0 || //  拿掉生涯紀錄
                text.indexOf("YearGP") >= 0
                || text.indexOf("playedPoint") >= 0
                || text.indexOf("NameHome") >= 0) {
            text = "";
        }

        return text;
    }

    @Override // 因為原檔就是utf8了，所以無須轉碼
    public String getAllPageString(String urlString) {

        if (urlString.matches("(?).*/")) {
            urlString = urlString.substring(0, urlString.length() - 1);
        }
        
        urlString = urlString.replaceAll("amp;", "");

        String indexName = Common.getStoredFileName(SetUp.getTempDirectory(), "index_wiki_", "html");
        Common.downloadFile(urlString, SetUp.getTempDirectory(), indexName, false, cookie);
        //Common.downloadFile( urlString, "", "test1.html", false, "", "" );
        //Common.downloadFile( urlString, "", "test2.html", false, "", "" );

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
        int beginIndex, endIndex;

        beginIndex = allPageString.indexOf("<title>") + 7;
        endIndex = allPageString.indexOf("</title>", beginIndex);

        String title = allPageString.substring(beginIndex, endIndex).trim();
        title = getRegularFileName(title);

        return Common.getStringRemovedIllegalChar(
                Common.getTraditionalChinese(title));
    }

    public int isListPage(String allPageString, int beginIndex) {
        String tokens[] = {
            "href=\"/wiki/List_of_",
            "href=\"/wiki/Category",
            "roster",
            "Alumni",
            "squad",
            "personnel",
            "from="
        };

        String hrefToken1 = "href=\"/wiki/";
        // ex. http://en.wikipedia.org/w/index.php?title=Category:21st-century_American_actresses&from=D
        String hrefToken2 = "href=\"//en.wikipedia.org/w/index.php?title=Category:";//"class=\"external text\" href=";
        int index = beginIndex;

        while (true) {
            index = Common.getSmallerIndexOfTwoKeyword(allPageString, index, hrefToken1, hrefToken2);

            if (index < 0) {
                break;
            }

            int endIndex = allPageString.indexOf("\"", index + 8);

            if (endIndex < 0) {
                break;
            }

            String tempString = allPageString.substring(index, endIndex);
            //Common.debugPrintln("有位址 : " + tempString);

            tempString = tempString.toLowerCase();
            for (int i = 0; i < tokens.length; i++) {
                int tempIndex2 = tempString.indexOf(tokens[i].toLowerCase());
                if (tempIndex2 >= 0) {
                    
                    if (i == 6 && !tempString.matches(".+from=\\w"))
                    {
                        Common.debugPrintln( "不是基本的字母分類，跳過:" + tempString );
                        continue;
                    }
                    return index;
                }
            }

            index = endIndex + 1;
        }

        return -1;
    }

    @Override
    public List<List<String>> getVolumeTitleAndUrlOnMainPage(String urlString, String allPageString) {
        // combine volumeList and urlList into combinationList, return it.

        List<List<String>> combinationList = new ArrayList<List<String>>();
        List<String> urlList = new ArrayList<String>();
        List<String> volumeList = new ArrayList<String>();

        int beginIndex = 0, endIndex = 0;
        //String listToken = "href=\"/wiki/List_of_";
        //String listToken2 = "href=\"/wiki/Category";
        String listURL = "";
        String listTitle = "";
        String tempString = "";

        // 第一層
        while (true) {
            int tempBeginIndex = beginIndex;
            beginIndex = isListPage(allPageString, beginIndex);

            if (beginIndex < 0) {
                break;
            }

            beginIndex = allPageString.indexOf("\"", beginIndex) + 1;
            endIndex = allPageString.indexOf("\"", beginIndex);
            tempString = allPageString.substring(beginIndex, endIndex).trim();
            Common.debugPrintln("有列表位址: " + tempString);

            if (tempString.indexOf("//") == 0) {
                // ex. //en.wikipedia.org/w/index.php?title=Category:20th-century_American_actresses&amp;from=Zt
                int tempBeginIndex1 = tempString.indexOf("Category:");
                if (tempBeginIndex1 < 0) {
                    break;
                }
                listTitle = tempString.substring(tempBeginIndex1 + 9, tempString.length());
                listTitle = listTitle.replaceAll("&amp;", "").replaceAll("%E2%80%93", "").replaceAll("\\(|\\)", "").replaceAll("=", "_");
                listURL = "http:" + tempString;
            } else {
                listTitle = tempString.substring(6, tempString.length());
                listTitle = listTitle.replaceAll("%E2%80%93", "").replaceAll("\\(|\\)", "");//.replaceAll("-", "_");
                listURL = baseURL + tempString;
            }

            if (listExisted(urlList, listURL)) {
                continue;
            }

            volumeList.add(Common.getStringRemovedIllegalChar(listTitle));
            urlList.add(listURL);

            Common.debugPrintln(totalVolume + " " + listTitle + " : " + listURL);

            totalVolume++;

            String tempAllPageString = getAllPageString(listURL);

            int beginIndex2 = 0;
            int endIndex2 = 0;

            // 第二層
            while (true) {
                int tempBeginIndx2 = beginIndex2;
                beginIndex2 = isListPage(tempAllPageString, tempBeginIndx2);

                if (beginIndex2 < 0) {
                    break;
                }

                beginIndex2 = tempAllPageString.indexOf("\"", beginIndex2) + 1;
                endIndex2 = tempAllPageString.indexOf("\"", beginIndex2);
                tempString = allPageString.substring(beginIndex, endIndex).trim();
                listTitle = tempString.substring(6, tempString.length());
                listURL = baseURL + tempString;

                if (listExisted(urlList, listURL)) {
                    continue;
                }

                volumeList.add(Common.getStringRemovedIllegalChar(listTitle));
                urlList.add(listURL);

                Common.debugPrintln("." + totalVolume + " " + listTitle + " : " + listURL);

                totalVolume++;
            }
        }

        Common.debugPrintln("共有" + totalVolume + "集");

        combinationList.add(volumeList);
        combinationList.add(urlList);

        return combinationList;
    }

    boolean listExisted(List<String> urlList, String currentListURL) {
        for (int i = 0; i < urlList.size(); i++) {
            //Common.debugPrintln( " --> " + urlList.get( i ) + "__" + currentListURL );
            if (urlList.get(i).equals(currentListURL)) {
                return true;
            }
        }

        return false;
    }
}
