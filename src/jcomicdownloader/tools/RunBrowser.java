/*
 ----------------------------------------------------------------------------------------------------
 Program Name : JComicDownloader
 Authors  : surveyorK
 Last Modified : 2011/10/25
 ----------------------------------------------------------------------------------------------------
 ChangeLog:

----------------------------------------------------------------------------------------------------
 */
package jcomicdownloader.tools;


import java.awt.Cursor;
import java.awt.Desktop;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import javax.swing.JLabel;

/** *//**
*
* @author Anthrax
* 此類負責檢測系統的默認瀏覽器等程序，並負責啟動它們
* @netSite 指定要顯示的網址
*/
public class RunBrowser{
    private Desktop desktop;
    private URI uri;
    private String netSite;
    private Cursor hander;
    
    /** Creates a new instance of DesktopRuner */
    public RunBrowser() {
        this.desktop = Desktop.getDesktop();
    }
    /**//**
    *check the system supports the broswer or not
    */
    public boolean checkBroswer() {
        if( desktop.isDesktopSupported() && desktop.isSupported( Desktop.Action.BROWSE ) ) {
            return true;
        }
        else{
            return false;
        }
    }
    
    /**
    *run default broswer, and open the page by urlString
    */
    public void runBroswer( String urlString ){
        netSite = urlString;
        try {
            uri = new URI( netSite );
        } catch ( URISyntaxException ex ){
            ex.printStackTrace();
        }
        try{
            desktop.browse( uri );
        } catch ( IOException ex ){
            ex.printStackTrace();
        }
    }

}

