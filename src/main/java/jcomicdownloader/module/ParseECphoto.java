/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jcomicdownloader.module;

import java.util.ArrayList;
import java.util.List;
import jcomicdownloader.SetUp;
import jcomicdownloader.enums.Site;
import jcomicdownloader.tools.Common;

/**
 *
 * @author apple
 */
public class ParseECphoto extends ParseEC {

    public ParseECphoto() {
        enumName = "EIGHT_COMIC_PHOTO";
        parserName=this.getClass().getName();
        regexs= new String[]{"(?s).*\\.8comic.com(?s).*(?s).*[Pp][Hh][Oo][Tt][Oo](?s).*","(?s).*comicvip.com(?s).*(?s).*[Pp][Hh][Oo][Tt][Oo](?s).*"};
        siteID=Site.formString("EIGHT_COMIC_PHOTO");
        indexWrongEncodingFileName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_8comic_photo_parse_", "html" );
        indexFileName = Common.getStoredFileName( SetUp.getTempDirectory(), "index_8comic_photo_encode_parse_", "html" );

        jsName = "index_8comic_photo.js";
    }

    @Override
    public void setParameters() { // let all the non-set attributes get values
        Common.debugPrintln( "開始解析各參數 :" );

        Common.debugPrintln( "開始解析title和wholeTitle :" );

        String allPageString = getAllPageString( webSite );

        if ( this.getWholeTitle() == null || this.getWholeTitle().equals( "" ) ) {
            int beginIndex = allPageString.indexOf( "<title>" ) + 7;
            int endIndex = allPageString.indexOf( "</title>", beginIndex );
            String titleString = allPageString.substring( beginIndex, endIndex );

            this.setWholeTitle( getVolumeWithFormatNumber(
                    Common.getStringRemovedIllegalChar( titleString ) ) );
        }

        Common.debugPrintln( "作品名稱(title) : " + getTitle() );
        Common.debugPrintln( "章節名稱(wholeTitle) : " + getWholeTitle() );
    }

    @Override
    public void parseComicURL() { // parse URL and save all URLs in comicURL
        // 先取得前面的下載伺服器網址
        String allPageString = getAllPageString( webSite );
//        Document doc = Parser.parse(getAllPageString(webSite), webSite);

        Common.debugPrint( "開始解析這一集有幾頁 :" );
        totalPage = allPageString.split( "\\.jpe'" ).length - 1;
        comicURL = new String[totalPage];
        Common.debugPrint( "共" + totalPage + "頁" );

        String[] tokens = allPageString.split( "'|\\." );

        int page = 0;
        for ( int i = 0 ; i < tokens.length ; i++ ) {
            if ( tokens[i].equals( "jpe" ) ) {
                comicURL[page++] = "http://www.8comic.com" + tokens[i - 1] + ".jpg";
                //Common.debugPrintln( (page-1) + " " + comicURL[page-1] );
            }
        }

        //System.exit(0);
    }

    @Override
    public boolean isSingleVolumePage( String urlString ) {
        return !urlString.matches( "(?s).*\\d+-\\d+.html(?s).*" );
    }

    @Override
    public String getTitleOnSingleVolumePage( String urlString ) {
        return "8comic圖集";
    }

    @Override
    public String getTitleOnMainPage( String urlString, String allPageString ) {
        return "8comic圖集";
    }

    @Override
    public List<List<String>> getVolumeTitleAndUrlOnMainPage( String urlString, String allPageString ) {
        // combine volumeList and urlList into combinationList, return it.

        List<List<String>> combinationList = new ArrayList<List<String>>();
        List<String> urlList = new ArrayList<String>();
        List<String> volumeList = new ArrayList<String>();

        int beginIndex = allPageString.indexOf( "id=\"newphoto_dl" );
        int endIndex = allPageString.indexOf( "id=\"newphoto_pager", beginIndex );
        String tempString = allPageString.substring( beginIndex, endIndex );
        String[] tempStrings = tempString.split( "\\d*>\\d*|\\d*<\\d*|\"" );

        totalVolume = tempString.split( "href=" ).length - 1;
        Common.debugPrintln( "共有" + totalVolume + "個圖集" );

        int nowVolume = 0;
        for ( int i = 0 ; i < tempStrings.length ; i++ ) {
            if ( tempStrings[i].matches( "(?s).*href=(?s).*" ) ) {
                urlList.add( "http://www.8comic.com" + tempStrings[i + 1] );
            } else if ( tempStrings[i].matches( "(?s).*br.*" ) ) {
                volumeList.add( tempStrings[i + 1].trim() );
            }
        }

        combinationList.add( volumeList );
        combinationList.add( urlList );

        return combinationList;
    }
}
