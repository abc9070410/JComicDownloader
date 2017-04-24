/*
 * InformationFrame.java
 ----------------------------------------------------------------------------------------------------
 Program Name : JComicDownloader
 Authors  : surveyorK
 Last Modified : 2012/12/15
 ----------------------------------------------------------------------------------------------------
 ChangeLog:
 5.20: Google Sites 版本資訊已經停用，改為使用 GitHub API 取得 repo 上的 release info；修正 vars 及 methods 的命名
 5.12: 修復資訊視窗在沒有網路時無窮等待的問題。
 5.10: 換新的回報專區網址。
 5.04: 提供執行甫更新的新版本的選項。
 5.02: 修復Java 7下無法使用NapKin Look and Feel的問題。
 4.06: 1. 增添資訊視窗上的最新版本下載訊息。
 2.13: 1. 修改最新版本下載按鈕，使其按下去可以直接下載最新版本。
 2.01: 1. 增加支援網站列表的資訊。
 1.14: 1. 修改文字顯示方式，使用setFont而不使用html語法，避免在某些情況下出現亂碼。
 2. 修復official.html無法刪除的bug。 
 1.08: 修復在讀取最新版本資訊之前，無法點擊按鈕的bug

 ----------------------------------------------------------------------------------------------------

 */
package jcomicdownloader.frame;

import jcomicdownloader.tools.*;
import jcomicdownloader.module.*;
import jcomicdownloader.*;

/**

 顯示訊息視窗
 */
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.TimeZone;
import javax.swing.*;
import com.owlike.genson.*;

public class InformationFrame extends JFrame implements ActionListener, MouseListener
{

    private JPanel informationPanel;
    private JLabel informationLabel;
    private JLabel versionLabel, dateLabel;
    private JLabel supportedSiteLabel;
    private String resourceFolder;
//    private JButton versionButton;
    public static JButton downloadButton;
    private final String strFileNameLatestVersionInfo;
    //private String downloadPageName;
    //private String downloadPageURL;
    private final String strUrlLatestVersionInfo;
    public static JFrame thisFrame; // for change look and feel
    private static boolean downloadLock = false; // 用來檢查是否已取得最新版本資訊，之後才可以下載最新版本
    private Dimension frameDimension;

    private String strUrlDownloadLatestExecutable = null;


    public InformationFrame()
    {
        super( "關於本程式" );

        thisFrame = this; // for change look and feel
        resourceFolder = "resource/";
        strFileNameLatestVersionInfo = "github_latest.json";
        //downloadPageName = "downloadPage.html";
        //downloadPageURL = "https://sites.google.com/site/jcomicdownloader/release";
        strUrlLatestVersionInfo = "https://api.github.com/repos/abc9070410/JComicDownloader/releases/latest";//"https://sites.google.com/site/jcomicdownloader/";

        setUpUIComponent();
        setUpeListener();
        setVisible(true);
//        setResizable(false);

        deleteLatestVersionInfo(); // 刪除官方網頁檔案

        setNewestVersion(); // 檢查是否有新版本
    }

    private void setUpUIComponent()
    {
        // 因應JRE 7，重新設定Look and Feel
        CommonGUI.setLookAndFeelByClassName( SetUp.getSkinClassName() );

        String picFileString = SetUp.getBackgroundPicPathOfInformationFrame();
        // 檢查背景圖片是否存在
        if ( SetUp.getUsingBackgroundPicOfInformationFrame()
                && !new File( picFileString ).exists() )
        {
            CommonGUI.showMessageDialog( this, picFileString
                    + "\n背景圖片不存在，重新設定為原始佈景",
                                         "提醒訊息", JOptionPane.INFORMATION_MESSAGE );
            SetUp.setUsingBackgroundPicOfInformationFrame( false );
        }

        if ( SetUp.getUsingBackgroundPicOfInformationFrame() )
        {

            frameDimension = CommonGUI.getDimension( picFileString );
            //setSize( frameDimension );
            int width = ( int ) frameDimension.getWidth() + CommonGUI.widthGapOfBackgroundPic;
            int height = ( int ) frameDimension.getHeight() + CommonGUI.heightGapOfBackgroundPic;
            setSize( width, height );
            setResizable( false );
        }
        else
        {
            int extendWidth = (SetUp.getDefaultFontSize() - 18) * 20; // 跟隨字體加寬
            extendWidth = extendWidth > 0 ? extendWidth : 0;
            setSize( 500 + extendWidth, 710 + extendWidth / 2 );
            setResizable( true );
        }

        setDefaultLookAndFeelDecorated(false); // 讓標題欄可以隨look and feel改變
        setLocationRelativeTo(this);  // set the frame in middle position of screen
        setIconImage(new CommonGUI().getImage(CommonGUI.mainIcon));

        Container contentPane;
        if ( SetUp.getUsingBackgroundPicOfInformationFrame() )
        {
            (( JPanel ) getContentPane()).setOpaque( false );
            contentPane = CommonGUI.getImagePanel( picFileString );
            contentPane.setPreferredSize( frameDimension );
            getContentPane().add( contentPane, BorderLayout.CENTER );
        }
        else
        {
            contentPane = getContentPane();
        }

        setTextLayout(contentPane);
    }

    private void setTextLayout( Container contentPane )
    {
        //textPanel = new JPanel( new BorderLayout() );

        String informationText = "";
        informationLabel = new JLabel( informationText );

        JLabel informLabel = getLabel( "目前最新版本: ", "Lastest version: " );
        versionLabel = getLabel( "偵測中...", "fetching..." );
        dateLabel = getLabel( "", "" );

        JLabel informLabel2 = getLabel( "目前總共支援: ", "Supported sites: " );
        supportedSiteLabel = getLabel( "偵測中...", "fetching..." );

        JPanel versionPanel = new JPanel( new FlowLayout( FlowLayout.LEFT ) );
        versionPanel.add( informLabel );
        versionPanel.add( versionLabel );
        versionPanel.add( dateLabel );
        versionPanel.setOpaque( !SetUp.getUsingBackgroundPicOfInformationFrame() );

        JPanel supportedSitePanel = new JPanel( new FlowLayout( FlowLayout.LEFT ) );
        supportedSitePanel.add( informLabel2 );
        supportedSitePanel.add( supportedSiteLabel );
        supportedSitePanel.setOpaque( !SetUp.getUsingBackgroundPicOfInformationFrame() );

        JPanel updatePanel = new JPanel( new GridLayout( 2, 1, 0, 0 ) );
        updatePanel.add( versionPanel );
        updatePanel.add( supportedSitePanel );
        updatePanel.setOpaque( !SetUp.getUsingBackgroundPicOfInformationFrame() );

        JButton supportedSiteButton = getButton( " 支援網站列表", "Supported site list", "information_supportedSite.png",
                                                 "https://sites.google.com/site/jcomicdownloader/home", KeyEvent.VK_S );

        downloadButton = getButton( " 最新版本下載", "Download lastest version", "information_download.png",
                                    null, KeyEvent.VK_D );
        downloadButton.addActionListener( this );

        JButton teachingButton = getButton( " 線上使用教學", "Online manual", "information_manual.png",
                                            "https://sites.google.com/site/jcomicdownloader/step-by-step", KeyEvent.VK_I );
        JButton searchComicButton = getButton( " 漫畫搜尋引擎", "Comic search", "information_search.png",
                                               "http://www.google.com/cse/home?cx=002948535609514911011:ls5mhwb6sqa&hl=zh-TW", KeyEvent.VK_C );
        JButton searchNovelButton = getButton( " 小說搜尋引擎", "Novel search", "information_novel_search.png",
                                               "http://www.google.com/cse/home?cx=002948535609514911011:_vv3hzthlt8&hl=zh-TW", KeyEvent.VK_N );
        JButton messageButton = getButton( " 疑難問題回報", "Bug report", "information_report.png",
                                           "https://github.com/abc9070410/JComicDownloader/issues", KeyEvent.VK_R );

        JLabel authorLabel = getLabel( "作者：surveyorK （abc9070410@gmail.com）",
                                       "author: surveyorK (abc9070410@gmail.com)" );

        informationPanel = new JPanel( new GridLayout( 8, 1, 5, 5 ) );
        //informationPanel.add( versionPanel );
        informationPanel.add( updatePanel );
        informationPanel.add( downloadButton );
        informationPanel.add( supportedSiteButton );
        informationPanel.add( teachingButton );
        informationPanel.add( searchComicButton );
        informationPanel.add( searchNovelButton );
        informationPanel.add( messageButton );
        informationPanel.add( authorLabel );
        informationPanel.setOpaque( !SetUp.getUsingBackgroundPicOfInformationFrame() );

        if ( SetUp.getUsingBackgroundPicOfInformationFrame() )
        {
            informationPanel.setPreferredSize( frameDimension );
            contentPane.add( new CommonGUI().getCenterPanel( informationPanel ), BorderLayout.CENTER );
        }
        else
        {
            JScrollPane informationScrollPane = new JScrollPane( new CommonGUI().getCenterPanel( informationPanel ) );

            contentPane.add( informationScrollPane, BorderLayout.CENTER );
        }

    }

    @Deprecated
    private String getHtmlString( String str )
    {
        return "<html><font size=\"5\">" + str + "</font></html>";
    }

    @Deprecated
    private String getHtmlStringWithColor( String str, String colorName )
    {
        return "<font color=\"" + colorName + "\" size=\"5\">" + str + "</font>";
    }

    // 直接抓取官網網頁
    private void downloadLatestVersionInfo()
    {
        Run.isAlive = true;
        Common.downloadFile(strUrlLatestVersionInfo, SetUp.getTempDirectory(), strFileNameLatestVersionInfo, false, "");
        //Common.downloadFile( downloadPageURL, SetUp.getTempDirectory(), downloadPageName, false, "" );
    }

    // 刪除官方網頁
    private void deleteLatestVersionInfo()
    {
        String strPathLatestVersionInfo = SetUp.getTempDirectory() + strFileNameLatestVersionInfo;
        Common.debugPrintln("刪除" + strPathLatestVersionInfo);
        File file = new File( strPathLatestVersionInfo );
        if ( file.exists() )
            file.delete();
    }

    // 回傳最新版本的字串
    private String getUpdateVersionString()
    {
        Map json = new Genson().deserialize(Common.getFileString(SetUp.getTempDirectory(), strFileNameLatestVersionInfo), Map.class);

        String strVersion = (String)json.get("tag_name");
        Boolean bPreRelease = json.get("prerelease").equals("true");

        return Common.getStringUsingDefaultLanguage(
                strVersion + (bPreRelease ? "預版" : ""),
                strVersion + (bPreRelease ? "pre" : ""));
    }

    // 回傳更新日期的字串
    private String getUpdateDateString()
    {
        Map json = new Genson().deserialize(Common.getFileString(SetUp.getTempDirectory(), strFileNameLatestVersionInfo), Map.class);

        // 再找出發佈最新版本的日期
        Calendar calPublish = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        {
            String strPublish = (String)json.get("published_at");

            try {
                calPublish.setTime(
                        (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX")).parse(strPublish)
                );
            } catch (java.text.ParseException e) {
                Common.debugPrintln(String.format("當解析更新日期時發生錯誤，原值為：「%s」", strPublish));
                return "";
            }
        }

        String dateString = "（%04d年%d月%d日發佈）";
        String dateEnString = "(released at %04d-%02d-%02d";

        return Common.getStringUsingDefaultLanguage(
                String.format(dateString, calPublish.get(Calendar.YEAR), calPublish.get(Calendar.MONTH)+1, calPublish.get(Calendar.DATE)),
                String.format(dateEnString, calPublish.get(Calendar.YEAR), calPublish.get(Calendar.MONTH)+1, calPublish.get(Calendar.DATE))); // 使用預設語言 dateString;
    }

    // 回傳目前已經支援網站數目的字串
    private String getUpdateSupportedSiteString()
    {
        return "81+";

        // TODO: 是否需要在其他地方提供最新版本支援網站數目 -- hkgsherlock

//        String allPageString = Common.getFileString( SetUp.getTempDirectory(), strFileNameLatestVersionInfo);
//
//        // 找出目前支援列表數目
//        int supportedSiteAmount = allPageString.split( "<td style=" ).length / 2 + 1;
//
//        String supportedSiteString = supportedSiteAmount + " 個網站";
//        String supportedSiteEnString = supportedSiteAmount + " websites";
//        return Common.getStringUsingDefaultLanguage( supportedSiteString, supportedSiteEnString );
    }

    /**
     * To store the URL for downloading the executable JAR of the latest version.
     */
    private void fetchUrlDownloadLatestExecutable() {
        Map json = new Genson().deserialize(Common.getFileString(SetUp.getTempDirectory(), strFileNameLatestVersionInfo), Map.class);
        // looping assets array and use the best matching one
        for (Object e : (ArrayList)json.get("assets")) {
            Map me = (Map)e;

            String strBrowserDownloadUrl = (String)me.get("browser_download_url");
            String strContentType = (String)me.get("content_type");

            if (strBrowserDownloadUrl.matches("^https://github\\.com/.+\\.jar$") && (strContentType.equals("application/octet-stream") || strContentType.equals("application/java-archive")) ) {
                this.strUrlDownloadLatestExecutable = strBrowserDownloadUrl;
                return;
            }
        }
        Common.debugPrintln("無法找到符合格式的最新版下載網址。");
    }

    // 察覺沒有網路的後續工作
    private void noNetworkProcess()
    {
        String message = "無法連線到官網，請檢查網路是否正常 !!";
        Common.debugPrintln( message );
        InformationFrame.downloadLock = false;
        CommonGUI.showMessageDialog(InformationFrame.thisFrame, message);

    }

    public void setNewestVersion()
    {

        Thread versionThread = new Thread( new Runnable()
        {

            public void run()
            {

                if ( !Common.urlIsOK(strUrlLatestVersionInfo) )
                {
                    noNetworkProcess(); // 察覺沒有網路的後續工作
                    return;
                }

                // 取得介面設定值（不用UIManager.getLookAndFeel().getName()是因為這樣才能讀到_之後的參數）
                String nowSkinName = SetUp.getSkinClassName();

                downloadLatestVersionInfo(); // 下載官方網頁

                versionLabel.setText(getUpdateVersionString()); // 從官方網頁提取更新版本資訊
                dateLabel.setText(getUpdateDateString()); // 從官方網頁提取更新日期資訊
                supportedSiteLabel.setText(getUpdateSupportedSiteString()); // 從官方網頁提取支援網站資訊
                fetchUrlDownloadLatestExecutable();

                synchronized ( InformationFrame.thisFrame )
                { // lock main frame
                    InformationFrame.thisFrame.notifyAll();
                    InformationFrame.downloadLock = false;
                }

                repaint();

                if ( !CommonGUI.isDarkSytleSkin( nowSkinName ) )
                {
                    versionLabel.setForeground( Color.RED );
                    dateLabel.setForeground( Color.BLUE );
                    supportedSiteLabel.setForeground( Color.DARK_GRAY );
                }

                if ( SetUp.getUsingBackgroundPicOfInformationFrame() )
                { // 若設定為透明，就用白色字體。
                    versionLabel.setForeground( SetUp.getInformationFrameOtherDefaultColor() );
                    dateLabel.setForeground( SetUp.getInformationFrameOtherDefaultColor() );
                    supportedSiteLabel.setForeground( SetUp.getInformationFrameOtherDefaultColor() );
                }

                //Thread.currentThread().interrupt();

                deleteLatestVersionInfo(); // 刪除官方網頁檔案

            }
        } );
        versionThread.start();
    }

    private void setUpeListener()
    {
        setDefaultCloseOperation( DISPOSE_ON_CLOSE );
    }

    public static void main( String[] args )
    {
        InformationFrame frame = new InformationFrame();
    }

    private JLabel getLabel( String string, String enString )
    {
        string = Common.getStringUsingDefaultLanguage( string, enString ); // 使用預設語言 

        JLabel label = new JLabel( string );
        label.setFont( SetUp.getDefaultFont() );

        if ( SetUp.getUsingBackgroundPicOfInformationFrame() )
        { // 若設定為透明，就用白色字體。
            label.setForeground(SetUp.getInformationFrameOtherDefaultColor());
        }

        label.setOpaque( !SetUp.getUsingBackgroundPicOfInformationFrame() );

        return label;
    }

    private JButton getButton( String string, String enString, String picName, final String urlString, int keyID )
    {
        string = Common.getStringUsingDefaultLanguage( string, enString ); // 使用預設語言 

        JButton button = new JButton( string, new CommonGUI().getImageIcon( picName ) );
        button.setMnemonic( keyID ); // 設快捷建為Alt + keyID

        CommonGUI.setToolTip( button, "快捷鍵: Alt + " + KeyEvent.getKeyText( keyID ) );

        button.setFont( SetUp.getDefaultFont( 3 ) );
        if ( urlString != null )
        {
            button.addActionListener( new ActionListener()
            {

                public void actionPerformed( ActionEvent e )
                {
                    new RunBrowser().runBroswer( urlString );
                }
            } );
        }

        if ( SetUp.getUsingBackgroundPicOfInformationFrame() )
        { // 若設定為透明，就用預定字體。
            button.setForeground( SetUp.getInformationFrameOtherDefaultColor() );
            button.setOpaque( false );
            button.addMouseListener( this );
        }



        return button;
    }

    // 下載最新版本的JComicDownloader
    private void downloadLastestVersion()
    {
        if (this.strUrlDownloadLatestExecutable == null || this.strUrlDownloadLatestExecutable.length() == 0)
        {
            JPanel pnl = new JPanel();
            pnl.setLayout(new BoxLayout(pnl, BoxLayout.Y_AXIS));
            pnl.add(new JLabel("<html><p>無法取得最新版本執行檔的下載網址！</p>" +
                    "<p>請到本程式在</p></html>"));
            {
                JButton btnReport = new JButton("GitHub 的 Release 頁面");
                btnReport.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        new RunBrowser().runBroswer("https://github.com/abc9070410/JComicDownloader/releases");
                    }
                });
                pnl.add(btnReport);
            }
            pnl.add(new JLabel("<html><p>中查閱。</p>" +
                    "<br/><p>如果可以的話，請同時</p></html>"));
            {
                JButton btnReport = new JButton("回報此錯誤");
                btnReport.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        new RunBrowser().runBroswer("https://github.com/abc9070410/JComicDownloader/issues/new?title=Error+fetching+URL+for+downloading+latest+executable&body=Current+Version:+" + ComicDownGUI.versionString.replace("JComicDownloader v", "") + "%0A%0A");
                    }
                });
                pnl.add(btnReport);
            }
            pnl.add(new JLabel("<html><p>給開發者修正，謝謝。</p></html>"));

            JOptionPane.showMessageDialog(
                    informationPanel,
                    pnl,
                    "無法下載最新版程式",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        InformationFrame.downloadButton.setText( "開始下載最新版本..." );

        new Thread( new Runnable()
        {

            public void run()
            {
                if ( !Common.urlIsOK(strUrlLatestVersionInfo) )
                {
                    noNetworkProcess(); // 察覺沒有網路的後續工作
                    return;
                }

                SwingUtilities.invokeLater( new Runnable()
                {

                    public void run()
                    {
                        Common.debugPrint( "檢查是否已取得最新版本資訊：" );

                        if ( versionLabel.getText().matches( ".*偵測中.*" ) )
                        {
                            InformationFrame.downloadLock = true;
                            Common.debugPrint( "Not yet..." );
                        }
                        else
                        {
                            InformationFrame.downloadLock = false;
                            Common.debugPrintln( "OK" );
                        }

                        synchronized ( InformationFrame.thisFrame )
                        { // lock main frame
                            while ( InformationFrame.downloadLock )
                            {
                                try
                                {
                                    InformationFrame.thisFrame.wait();
                                }
                                catch ( InterruptedException ex )
                                {
                                    Common.handleErrorMessage( ex, "無法讓informationFrame等待（wait）" );
                                }
                            }
                            Common.debugPrintln( "OK" );
                        }
                        
                        deleteLatestVersionInfo(); // 刪除官方網頁檔案

                        String fileName = "JComicDownloader.jar";

                        Common.downloadFile(strUrlDownloadLatestExecutable, Common.getNowAbsolutePath(), fileName, false, null );

                        InformationFrame.downloadButton.setText( "最新版本下載完成" );

                        // 在Unix-Like系統下開啟執行權限
                        if ( Common.isUnix() )
                        {
                            Common.runCmd( "chmod 775", Common.getNowAbsolutePath() + fileName, false );
                        }


                        Object[] options =
                        {
                            "確定", "執行新版本程式", "開啟存放資料夾"
                        };
                        int choice = JOptionPane.showOptionDialog( thisFrame, "<html>最新版本 <font color=red>" + fileName + "</font> 已下載完畢！</html>",
                                                                   "告知視窗",
                                                                   JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
                                                                   null, options, options[0] );

                        if ( choice == 1 )
                        {
                            Common.startJARandExit( fileName );
                        }
                        else if ( choice == 2 )
                        {
                            openNowDirectory( fileName );
                        }


                        InformationFrame.downloadButton.setText( "最新版本下載" );
                    }
                } );

            }
        } ).start();
    }

    private void openNowDirectory( String fileName )
    {
        if ( Common.isWindows() )
        {
            // 開啟資料夾並將最新版本jar檔反白
            Common.runUnansiCmd( "explorer /select, ", Common.getNowAbsolutePath() + fileName );
        }
        else if ( Common.isMac() )
        {
            Common.runCmd( "Finder", Common.getNowAbsolutePath(), true );
        }
        else
        {
            Common.runCmd( "nautilus", Common.getNowAbsolutePath(), true );
        }
    }

    @Override
    public void actionPerformed( ActionEvent event )
    {
        if ( event.getSource() == downloadButton )
        {
            downloadLastestVersion();
        }
    }

    @Override
    public void mouseClicked( MouseEvent e )
    {
    }

    @Override
    public void mousePressed( MouseEvent e )
    {
    }

    @Override
    public void mouseReleased( MouseEvent e )
    {
    }

    @Override
    public void mouseExited( MouseEvent event )
    {
        if ( SetUp.getUsingBackgroundPicOfInformationFrame() )
        {
            (( JComponent ) event.getSource()).setForeground( SetUp.getInformationFrameOtherDefaultColor() );
        }
    }

    @Override
    public void mouseEntered( MouseEvent event )
    {
        if ( SetUp.getUsingBackgroundPicOfInformationFrame() )
        {
            (( JComponent ) event.getSource()).setForeground( SetUp.getInformationFrameOtherMouseEnteredColor() );
        }
    }
}
