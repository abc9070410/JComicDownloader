/*
 ----------------------------------------------------------------------------------------------------
 Program Name : JComicDownloader
 Authors  : surveyorK
 Last Modified : 2011/10/25
 ----------------------------------------------------------------------------------------------------
 ChangeLog:

----------------------------------------------------------------------------------------------------
 */
package jcomicdownloader;


import jcomicdownloader.tools.*;
import jcomicdownloader.module.*;
import jcomicdownloader.enums.*;


/**
 *
 * 原本是指令主程式，但後來被眾不肖子弟（SetUP、ParseWebPage、Run、RunModule）搬空了......
 */
public class ComicDown {
    public static void main( String[] args ) {
        Thread mainRun = new Run( args, RunModeEnum.DOWNLOAD_MODE );

        mainRun.setName( Common.consoleThreadName );
        mainRun.start();
	}
}
