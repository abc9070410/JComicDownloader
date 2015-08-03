/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jcomicdownloader.module;

import java.util.ArrayList;
import java.util.List;
import jcomicdownloader.enums.Site;
import static jcomicdownloader.module.ParseNINENINE.getHowManyKeyWordInString;
import jcomicdownloader.tools.Common;

/**
 *
 * @author apple
 */

public class Parse99Manga extends ParseNINENINE
{

    public Parse99Manga()
    {
        super();
        enumName = "NINENINE_MANGA";
        regexs= new String[]{"(?s).*99manga.com(?s).*" };
        parserName=this.getClass().getName();
        siteID=Site.formString("NINENINE_MANGA");
        siteName = "99manga.com";

        baseURL = "http://99manga.com";
    }

    @Override // 從主頁面取得title(作品名稱)
    public String getTitleOnMainPage( String urlString, String allPageString )
    {
        Common.debugPrintln( "開始由主頁面位址取得title：" );

        int beginIndex = allPageString.lastIndexOf( " alt=" );
        beginIndex = allPageString.indexOf( "\"", beginIndex ) + 1;
        int endIndex = allPageString.indexOf( "\"", beginIndex );
        String tempString = allPageString.substring( beginIndex, endIndex );

        String title = Common.getStringRemovedIllegalChar(
                Common.getTraditionalChinese( tempString.trim() ) );

        return title;
    }

    @Override // 從主頁面取得所有集數名稱和網址
    public List<List<String>> getVolumeTitleAndUrlOnMainPage( String urlString, String allPageString )
    {
        // combine volumeList and urlList into combinationList, return it.
        // ex. <li><a href=/manga/4142/84144.htm?s=4 target=_blank>bakuman151集</a>
        //     <a href="javascript:ShowA(4142,84144,4);" class=Showa>加速A</a>
        //     <a href="javascript:ShowB(4142,84144,4);" class=Showb>加速B</a></li>

        List<List<String>> combinationList = new ArrayList<List<String>>();
        List<String> urlList = new ArrayList<String>();
        List<String> volumeList = new ArrayList<String>();

        int endIndexOfBaseURL = Common.getIndexOfOrderKeyword( urlString, "/", 3 );
        String baseURL = urlString.substring( 0, endIndexOfBaseURL );

        int beginIndex = allPageString.indexOf( "class=\"vol\"" );
        int endIndex = allPageString.indexOf( "class=\"replv\"", beginIndex );
        String tempString = allPageString.substring( beginIndex, endIndex );


        int totalVolume = getHowManyKeyWordInString( tempString, "ShowA" );
        int index = 0;
        for ( int count = 0; count < totalVolume; count++ )
        {
            index = tempString.indexOf( "href=/", index );

            int urlBeginIndex = tempString.indexOf( "/", index );
            int urlEndIndex = Common.getSmallerIndexOfTwoKeyword( tempString, index, " ", ">" );

            urlList.add( baseURL + tempString.substring( urlBeginIndex, urlEndIndex ) );

            int volumeBeginIndex = tempString.indexOf( ">", index ) + 1;
            int volumeEndIndex = tempString.indexOf( "<", volumeBeginIndex );

            String title = tempString.substring( volumeBeginIndex, volumeEndIndex );

            volumeList.add( getVolumeWithFormatNumber(
                    Common.getStringRemovedIllegalChar(
                    Common.getTraditionalChinese( title ) ) ) );

            index = volumeEndIndex;
        }

        combinationList.add( volumeList );
        combinationList.add( urlList );

        return combinationList;
    }
}
