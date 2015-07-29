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
public class Parse3G extends ParseNINENINE
{

    public Parse3G()
    {
        super();
        enumName = "NINENINE_3G";
		parserName=this.getClass().getName();

		siteID=Site.formString("NINENINE_3G");
        siteName = "3G";
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

        int totalVolume = getHowManyKeyWordInString( allPageString, " target=" );
        int index = 0;
        for ( int count = 0; count < totalVolume; count++ )
        {
            index = allPageString.indexOf( "href=/", index );

            int urlBeginIndex = allPageString.indexOf( "/", index );
            int urlEndIndex = Common.getSmallerIndexOfTwoKeyword( allPageString, index, " ", ">" );

            urlList.add( baseURL + allPageString.substring( urlBeginIndex, urlEndIndex ) );

            int volumeBeginIndex = allPageString.indexOf( ">", index ) + 1;
            int volumeEndIndex = allPageString.indexOf( "<", volumeBeginIndex );

            String volumeTitle = allPageString.substring( volumeBeginIndex, volumeEndIndex );
            volumeList.add( getVolumeWithFormatNumber( Common.getStringRemovedIllegalChar(
                    Common.getTraditionalChinese( volumeTitle ) ) ) );

            index = volumeEndIndex;
        }

        combinationList.add( volumeList );
        combinationList.add( urlList );

        return combinationList;
    }
}

