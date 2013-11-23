/*
 ----------------------------------------------------------------------------------------------------
 Program Name : JComicDownloader
 Authors  : surveyorK
 Last Modified : 2012/12/15
 ----------------------------------------------------------------------------------------------------
 ChangeLog:
 5.12: 修復部分介面嚴重的崩潰問題。
 1.09: 加入書籤表格和紀錄表格相關的公用方法
 ----------------------------------------------------------------------------------------------------
 */
package jcomicdownloader.tools;

import java.awt.*;
import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.*;
import jcomicdownloader.ComicDownGUI;
import jcomicdownloader.SetUp;

/**

 少部份的通用方法放在這邊，大都與視窗界面有相關。
 */
public class CommonGUI
{

    private static String resourceFolder;
    public static String stateBarMainMessage;
    public static String stateBarDetailMessage;
    public static int choiceFromShowMessage;
    // 目前滑鼠在表格中的位置（列），給很大初始值是為了避免剛啟動就有列被改變顏色
    public static int nowMouseAtRow = 10000;
    // 背景圖如果照正常貼上去，開出來的frame會比原圖小一點，所以需要修正。
    public static int widthGapOfBackgroundPic = 2;
    public static int heightGapOfBackgroundPic = 33;
    public static int optionDialogChoice = -1; // OptionDialog視窗的選擇
    public static String showInputDialogValue = "IntialValue"; // OptionDialog視窗輸入的值
    public static boolean showMessageOK = false;
    public static String mainIcon = "main_icon.png";
    private static String jtattooFileName = "Jtattoo.jar";
    private static String nimrodFileName = "nimrodlf-1.2.jar";
    private static String napkinFileName = "napkinlaf-alpha001.jar";
    private static String substanceFileName = "substance-6.1.jar";
    private static String tridentFileName = "trident.jar";
    private static String jtattooClassName = "com.jtattoo.plaf.*";
    private static String nimrodClassName = "com.nilo.plaf.nimrod.*";
    public static String napkinClassName = ".*napkin\\..*";
    private static String substanceClassName = ".*substance.api.skin.*";

    public CommonGUI()
    {
        resourceFolder = "resource/";
    }

    public static String getResourceFolder()
    {
        return resourceFolder;
    }

    public URL getResourceURL( String resourceName )
    {  // for show pic on jar
        return getClass().getResource( resourceFolder + resourceName );
    }

    public JLabel getFixedTansparentLabel()
    {
        // set white space in the Label
        return getFixedTansparentLabel( 15 ); // 預設大小為15
    }

    public JLabel getFixedTansparentLabel( int size )
    {
        // set white space in the Label
        URL url = getResourceURL( "tansparent.png" );
        return new JLabel( "<html><img align=\"center\" src="
                + url + " width=\"" + size + "\" height=\"" + size + "\"/></html>" );
    }

    public JPanel getCenterPanel( Component comp )
    { // make fixed border around
        return getCenterPanel( comp, 15 ); // 預設大小為15
    }

    public JPanel getCenterPanel( Component comp, int size )
    { // make fixed border around
        return getCenterPanel( comp, size, size );
    }

    public JPanel getCenterPanel( Component comp, int depth, int width )
    { // make fixed border around
        JPanel panel = new JPanel( new BorderLayout() );

        panel.add( getFixedTansparentLabel( width ), BorderLayout.EAST );
        panel.add( getFixedTansparentLabel( width ), BorderLayout.WEST );
        panel.add( getFixedTansparentLabel( depth ), BorderLayout.SOUTH );
        panel.add( getFixedTansparentLabel( depth ), BorderLayout.NORTH );
        panel.add( comp, BorderLayout.CENTER );
        panel.setOpaque( false );

        return panel;
    }

    public static String getButtonText( String word )
    {
        return word;

        /*
         return "<html><font face='文鼎細明體' size='6' >" +
         word +
         "</font></html>";

         */
    }

    public Icon getImageIcon( String picName )
    {
        Icon icon = new ImageIcon();
        try
        {
            InputStream is = getClass().getResourceAsStream( resourceFolder + picName );
            Image img = ImageIO.read( is );
            icon = new ImageIcon( img );
        }
        catch ( Exception ex )
        {
            //Common.hadleErrorMessage( ex, "找不到此資源：" + resourceFolder + picName );
            Common.errorReport( "找不到此資源：" + resourceFolder + picName );
        }

        return icon;
    }

    public Image getImage( String picName )
    {
        return (( ImageIcon ) getImageIcon( picName )).getImage();
    }

    public String getButtonPic( String picName )
    {
        URL url = getResourceURL( picName );

        return "<html><img src=" + url + " width=\"76\" height=\"76\" /><br>";
    }

    public String getLabelPic( String picName, int width, int height )
    {
        URL url = getResourceURL( picName );
        return "<html><img align=\"center\" src=" + url + " width=\"" + width + "\" height=\"" + height + "\"/></html>";
    }

    public String getTansparent( int width, int height )
    {
        URL url = getResourceURL( "tansparent.png" );
        return "<html><img align=\"center\" src=" + url + " width=\"" + width + "\" height=\"" + height + "\"/></html>";
    }

    public static int getSumOfTrue( boolean[] bool )
    {
        int count = 0;
        for ( int i = 0; i < bool.length; i++ )
        {
            if ( bool[i] )
            {
                count++;
            }
        }

        return count;
    }

    public static int getSumOfTrue( String[] boolStrings )
    {
        int count = 0;

        for ( int i = 0; i < boolStrings.length; i++ )
        {
            if ( boolStrings[i] != null && boolStrings[i].equals( "true" ) )
            {
                count++;
            }
        }
        return count;
    }

    public static Vector<Object> getDownDataRow( int order, String title, String[] volumes, String[] needs, String[] URLs )
    {
        Vector<Object> row = new Vector<Object>();

        row.add( new Integer( order ) );
        row.add( true );
        row.add( title );
        row.add( Common.getConnectStrings( volumes ) );
        row.add( Common.getConnectStrings( needs ) );
        row.add( "等待下載" );
        row.add( Common.getConnectStrings( URLs ) );

        return row;
    }

    public static Vector<Object> getBookmarkDataRow( int order, String title, String url )
    {
        Date date = new Date(); // 取得目前時間
        DateFormat shortFormat = DateFormat.getDateTimeInstance(
                DateFormat.SHORT, DateFormat.SHORT );

        Vector<Object> row = new Vector<Object>();

        row.add( new Integer( order ) );
        row.add( title );
        row.add( url );
        row.add( shortFormat.format( date ) );
        row.add( "" );

        return row;
    }

    public static Vector<Object> getRecordDataRow( int order, String title, String url )
    {
        Date date = new Date(); // 取得目前時間
        DateFormat shortFormat = DateFormat.getDateTimeInstance(
                DateFormat.SHORT, DateFormat.SHORT );

        Vector<Object> row = new Vector<Object>();

        row.add( new Integer( order ) );
        row.add( title );
        row.add( url );
        row.add( shortFormat.format( date ) );

        return row;
    }

    public static Vector<Object> getVolumeDataRow( String checkString, String volume )
    {
        Vector<Object> row = new Vector<Object>();
        row.add( Boolean.valueOf( checkString ) );
        row.add( volume );
        return row;
    }

    public static int getGTKSkinOrder()
    {
        UIManager.LookAndFeelInfo looks[] = UIManager.getInstalledLookAndFeels();

        int gtkOrder = -1;
        for ( int i = 0; i < looks.length; i++ )
        {
            if ( looks[i].getClassName().matches( ".*GTK.*" ) )
            {
                gtkOrder = i;
            }
        }

        return gtkOrder;
    }

    // 取得JTattoo所有可用的界面類別名稱
    public String[] getJTattooClassNames()
    {
        String[] jtattooClassNames =
        {
            "com.jtattoo.plaf.noire.NoireLookAndFeel", // 柔和黑
            "com.jtattoo.plaf.smart.SmartLookAndFeel", // 木質感+xp風格
            "com.jtattoo.plaf.mint.MintLookAndFeel", // 橢圓按鈕+黃色按鈕背景
            "com.jtattoo.plaf.mcwin.McWinLookAndFeel", // 橢圓按鈕+綠色按鈕背景
            "com.jtattoo.plaf.luna.LunaLookAndFeel", // 純XP風格
            "com.jtattoo.plaf.hifi.HiFiLookAndFeel", // 黑色風格
            "com.jtattoo.plaf.fast.FastLookAndFeel", // 普通swing風格+藍色邊框
            "com.jtattoo.plaf.bernstein.BernsteinLookAndFeel", // 黃色風格
            "com.jtattoo.plaf.aluminium.AluminiumLookAndFeel", // 橢圓按鈕+翠綠色按鈕背景+金屬質感（預設）
            "com.jtattoo.plaf.aero.AeroLookAndFeel", // xp清新風格
            "com.jtattoo.plaf.acryl.AcrylLookAndFeel" // 布質感+swing純風格
        };

        return jtattooClassNames;
    }

    // 取得NimROD所有可用的界面類別名稱
    public String[] getNimRodClassNames()
    {
        String[] nimRodClassNames =
        {
            "com.nilo.plaf.nimrod.NimRODLookAndFeel_Night", // 夜晚風格
            "com.nilo.plaf.nimrod.NimRODLookAndFeel_DarkGrey", // 暗灰風格
            "com.nilo.plaf.nimrod.NimRODLookAndFeel_DarkTabaco", // 暗Tabaco風格
            "com.nilo.plaf.nimrod.NimRODLookAndFeel_LightTabaco", // 亮Tabaco風格
            "com.nilo.plaf.nimrod.NimRODLookAndFeel_Burdeos", // Burdeos風格
            "com.nilo.plaf.nimrod.NimRODLookAndFeel_Snow", // 白雪風格
        };

        return nimRodClassNames;
    }

    // 取得Napkin所有可用的界面類別名稱
    public String[] getNapkinClassNames()
    {
        String[] nimRodClassNames =
        {
            "napkin.NapkinLookAndFeel", // 預設潦草風格
        };

        return nimRodClassNames;
    }

    // 取得Substance所有可用的界面類別名稱
    public String[] getSubstanceClassNames()
    {
        String[] substanceClassNames =
        {
            "org.pushingpixels.substance.api.skin.SubstanceAutumnLookAndFeel",
            "org.pushingpixels.substance.api.skin.SubstanceBusinessBlackSteelLookAndFeel",
            "org.pushingpixels.substance.api.skin.SubstanceBusinessBlueSteelLookAndFeel",
            "org.pushingpixels.substance.api.skin.SubstanceBusinessLookAndFeel",
            "org.pushingpixels.substance.api.skin.SubstanceChallengerDeepLookAndFeel",
            "org.pushingpixels.substance.api.skin.SubstanceCremeCoffeeLookAndFeel",
            "org.pushingpixels.substance.api.skin.SubstanceCremeLookAndFeel",
            "org.pushingpixels.substance.api.skin.SubstanceDustCoffeeLookAndFeel",
            "org.pushingpixels.substance.api.skin.SubstanceDustLookAndFeel",
            "org.pushingpixels.substance.api.skin.SubstanceEmeraldDuskLookAndFeel",
            "org.pushingpixels.substance.api.skin.SubstanceGeminiLookAndFeel",
            "org.pushingpixels.substance.api.skin.SubstanceGraphiteAquaLookAndFeel",
            "org.pushingpixels.substance.api.skin.SubstanceGraphiteGlassLookAndFeel",
            "org.pushingpixels.substance.api.skin.SubstanceGraphiteLookAndFeel",
            "org.pushingpixels.substance.api.skin.SubstanceMagellanLookAndFeel",
            "org.pushingpixels.substance.api.skin.SubstanceMarinerLookAndFeel",
            "org.pushingpixels.substance.api.skin.SubstanceMistAquaLookAndFeel",
            "org.pushingpixels.substance.api.skin.SubstanceMistSilverLookAndFeel",
            "org.pushingpixels.substance.api.skin.SubstanceModerateLookAndFeel",
            "org.pushingpixels.substance.api.skin.SubstanceNebulaBrickWallLookAndFeel",
            "org.pushingpixels.substance.api.skin.SubstanceNebulaLookAndFeel",
            "org.pushingpixels.substance.api.skin.SubstanceOfficeBlack2007LookAndFeel",
            "org.pushingpixels.substance.api.skin.SubstanceOfficeBlue2007LookAndFeel",
            "org.pushingpixels.substance.api.skin.SubstanceOfficeSilver2007LookAndFeel",
            "org.pushingpixels.substance.api.skin.SubstanceRavenLookAndFeel",
            "org.pushingpixels.substance.api.skin.SubstanceSaharaLookAndFeel",
            "org.pushingpixels.substance.api.skin.SubstanceTwilightLookAndFeel",
        };

        return substanceClassNames;
    }

    // 是否為暗色風格的介面
    public static boolean isDarkSytleSkin( String skinName )
    {
        if ( skinName.matches( ".*HiFi.*" )
                || skinName.matches( ".*Noire.*" )
                || skinName.matches( ".*Night.*" )
                || skinName.matches( ".*DarkGrey.*" )
                || skinName.matches( ".*DarkTabaco.*" )
                || skinName.matches( ".*Burdeos.*" )
                || skinName.matches( ".*ChallengerDeep.*" )
                || skinName.matches( ".*EmeraldDusk.*" )
                || skinName.matches( ".*GraphiteAqua.*" )
                || skinName.matches( ".*GraphiteGlass.*" )
                || skinName.matches( ".*Graphite.*" )
                || skinName.matches( ".*Magellan.*" )
                || skinName.matches( ".*Raven.*" )
                || skinName.matches( ".*Twilight.*" ) )
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    // 取得所有預設可用的界面類別名稱
    public String[] getDefaultClassNames()
    {
        String classNames = "";
        try
        {
            for ( UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels() )
            {
                //System.out.println( info.getName() );
                classNames += info.getClassName() + "###";
            }
        }
        catch ( Exception e )
        {
            Common.errorReport( "無法取得預設的Look and Feel !!" );
        }

        return classNames.split( "###" );
    }

    // 取得JTattoo所有可用的界面名稱
    public String[] getJTattooSkinStrings()
    {
        String[] jtattooSkinStrings =
        {
            "JTattoo.Noire", // 柔和黑
            "JTattoo.Smart", // 木質感+xp風格
            "JTattoo.Mint", // 橢圓按鈕+黃色按鈕背景
            "JTattoo.McWin", // 橢圓按鈕+綠色按鈕背景
            "JTattoo.Luna", // 純XP風格
            "JTattoo.HiFi", // 黑色風格
            "JTattoo.Fast", // 普通swing風格+藍色邊框
            "JTattoo.Bernstein", // 黃色風格
            "JTattoo.Aluminium", // 橢圓按鈕+翠綠色按鈕背景+金屬質感（預設）
            "JTattoo.Aero", // xp清新風格
            "JTattoo.Acryl" // 布質感+swing純風格
        };

        return jtattooSkinStrings;
    }

    // 取得NimROD所有可用的界面名稱
    public String[] getNimRodSkinStrings()
    {
        String[] nimRodSkinStrings =
        {
            "NimROD.Night", // 夜晚風格
            "NimROD.DarkGrey", // 暗灰風格
            "NimROD.DarkTabaco", // 暗Tabaco風格
            "NimROD.LightTabaco", // 亮Tabaco風格
            "NimROD.Burdeos", // Burdeos風格
            "NimROD.Snow", // 白雪風格
        };

        return nimRodSkinStrings;
    }

    // 取得Napkin所有可用的界面名稱
    public String[] getNapkinSkinStrings()
    {
        String[] napkinSkinStrings =
        {
            "Napkin.Napkin", // 潦草風格
        };

        return napkinSkinStrings;
    }

    // 取得Napkin所有可用的界面名稱
    public String[] getSubstanceSkinStrings()
    {
        String[] substanceSkinStrings =
        {
            "Substance.Autumn",
            "Substance.BusinessBlackSteel",
            "Substance.BusinessBlueSteel",
            "Substance.Business",
            "Substance.ChallengerDeep",
            "Substance.CremeCoffee",
            "Substance.Creme",
            "Substance.DustCoffee",
            "Substance.Dust",
            "Substance.EmeraldDusk",
            "Substance.Gemini",
            "Substance.GraphiteAqua",
            "Substance.GraphiteGlass",
            "Substance.Graphite",
            "Substance.Magellan",
            "Substance.Mariner",
            "Substance.MistAqua",
            "Substance.MistSilver",
            "Substance.Moderate",
            "Substance.NebulaBrickWall",
            "Substance.Nebula",
            "Substance.OfficeBlack2007",
            "Substance.OfficeBlue2007",
            "Substance.OfficeSilver2007",
            "Substance.Raven",
            "Substance.Sahara",
            "Substance.TwilightLookAndFeel"
        };

        return substanceSkinStrings;
    }

    // 取得所有預設可用的界面名稱
    public String[] getDefaultSkinStrings()
    {
        String skinString = "";
        try
        {
            for ( UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels() )
            {
                //System.out.println( info.getName() );
                skinString += "Default." + info.getName() + "###";
            }
        }
        catch ( Exception e )
        {
            Common.errorReport( "無法取得預設的Look and Feel !!" );
        }

        return skinString.split( "###" );
    }

    // 取得預設所有skins和jtattoo所有skins的類別名稱
    public String[] getClassNames()
    {
        String[] defaultClassNames = getDefaultClassNames();
        String[] jtattooClassNames = getJTattooClassNames();
        String[] nimRodClassNames = getNimRodClassNames();
        String[] napkinClassNames = getNapkinClassNames();
        String[] substanceClassNames = getSubstanceClassNames();

        List<String> allClassNameList = new ArrayList<String>();

        int count = 0;
        for ( String className : defaultClassNames )
        {
            allClassNameList.add( className );
        }
        for ( String className : napkinClassNames )
        {
            allClassNameList.add( className );
        }

        for ( String className : nimRodClassNames )
        {
            allClassNameList.add( className );
        }
        for ( String className : jtattooClassNames )
        {
            allClassNameList.add( className );
        }
        for ( String className : substanceClassNames )
        {
            allClassNameList.add( className );
        }

        String[] classNames = new String[ allClassNameList.size() ];
        Object[] objects = allClassNameList.toArray();
        for ( int i = 0; i < objects.length; i++ )
        {
            classNames[i] = objects[i].toString();
        }

        return classNames;
    }

    // 取得所有預設的小說封面搜尋過程中的挑選數量（挑前n個）
    public String[] getCoverStrings()
    {

        return new String[]
                {
                    "取搜尋到的第1張圖當作封面", "從搜尋到的前5張圖挑出封面",
                    "從搜尋到的前10張圖挑出封面", "從搜尋到的前15張圖挑出封面", "從搜尋到的前20張圖挑出封面"
                };
    }

    public String[] getCoverEnStrings()
    {

        return new String[]
                {
                    "choice first pic as cover", "choice cover from 5 pics",
                    "choice cover from 10 pics", "choice cover from 15 pics", "choice cover from 20 pics"
                };
    }

    // 取得所有預設語言的名稱
    public String[] getLanguageStrings()
    {

        return new String[]
                {
                    "正體中文", "简体中文", "English"
                };
    }

    // 取得所有預設語言的名稱
    public String[] getShellScriptStrings()
    {

        return new String[]
                {
                    "bash", "sh", "csh", "ksh", "powershell"
                };
    }

    // 取得預設所有skins和jtattoo所有skins的名稱
    public String[] getSkinStrings()
    {
        String[] defaultSkinStrings = getDefaultSkinStrings();
        String[] jtattooSkinStrings = getJTattooSkinStrings();
        String[] nimRodSkinStrings = getNimRodSkinStrings();
        String[] napkinSkinStrings = getNapkinSkinStrings();
        String[] substanceSkinStrings = getSubstanceSkinStrings();


        List<String> allSkinNameList = new ArrayList<String>();

        int count = 0;
        for ( String skinString : defaultSkinStrings )
        {
            allSkinNameList.add( skinString );
        }
        for ( String skinString : napkinSkinStrings )
        {
            allSkinNameList.add( skinString );
        }
        for ( String skinString : nimRodSkinStrings )
        {
            allSkinNameList.add( skinString );
        }
        for ( String skinString : jtattooSkinStrings )
        {
            allSkinNameList.add( skinString );
        }
        for ( String skinString : substanceSkinStrings )
        {
            allSkinNameList.add( skinString );
        }

        String[] skinNames = new String[ allSkinNameList.size() ];
        Object[] objects = allSkinNameList.toArray();
        for ( int i = 0; i < objects.length; i++ )
        {
            skinNames[i] = objects[i].toString();
        }

        return skinNames;
    }

    // 找尋skinClassName在looks[i]中的順位，若沒找到就回傳-1
    public int getSkinOrderBySkinClassName( String skinClassName )
    {
        String[] classNames = getClassNames();

        int gtkOrder = -1;
        for ( int i = 0; i < classNames.length; i++ )
        {
            if ( classNames[i].equals( skinClassName ) )
            {
                gtkOrder = i;
            }
        }

        return gtkOrder;
    }

    public static void setLookAndFeelByClassOrder( int no )
    {
        UIManager.LookAndFeelInfo looks[] = UIManager.getInstalledLookAndFeels();
        try
        {
            UIManager.setLookAndFeel( looks[no].getClassName() );
        }
        catch ( Exception ex )
        {
            Common.errorReport( "無法設置預設的Lood And Feel" );
        }
    }

    // 動態讀取外部JAR檔中的class建構式
    public static Class getOuterClass( String className, String jarFileName )
    {
        File jarFile = new File( jarFileName );
        String jarFileURL = jarFile.toURI().toString();
        URLClassLoader classLoader = null;
        Class newClass = null;
        //Constructor constructor = null;
        try
        {
            classLoader = new URLClassLoader( new URL[]
                    {
                        new URL( jarFileURL )
                    } );
            newClass = classLoader.loadClass( className );
            //constructor = newClass.getConstructor( null );
        }
        catch ( MalformedURLException ex )
        {
            Common.errorReport( "無法動態讀取函式庫: " + jarFileName );
        }
        catch ( ClassNotFoundException ex )
        {
            Common.errorReport( "找不到此類別名稱: " + className );
        }

        Common.debugPrintln( "從 " + jarFileName + " 中取得 " + newClass.getName() );

        return newClass;
    }

    //取得（外部JAR擷取的）Class類別的新物件
    public static Object getNewInstanceFromClass( Class outerClass )
    {

        Object object = null;
        try
        {
            object = outerClass.getConstructor().newInstance();
        }
        catch ( Exception ex )
        {
            Common.errorReport( "無法建立新物件: " + outerClass );
            Logger.getLogger( CommonGUI.class.getName() ).log( Level.SEVERE, null, ex );
        }

        Common.debugPrintln( outerClass.getName() + "的物件建立成功 !" );

        return object;
    }

    // 是否為外部的L&F
    public static boolean isOuterLookAndFeel( String className )
    {
        if ( className.matches( jtattooClassName )
                || className.matches( nimrodClassName )
                || className.matches( napkinClassName )
                || className.matches( substanceClassName ) )
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public static void setLookAndFeelByClassName( String className )
    {

        // 代表不是原版的類別名稱，而是後面有加參數的
        // ex. com.nilo.plaf.nimrod.NimRODLookAndFeel_Night // 夜晚風格
        if ( className.split( "_" ).length > 1 )
        {
            String realClassName = className.split( "_" )[0];
            String classThemeName = className.split( "_" )[1];

            if ( realClassName.matches( ".*nimrod.*" ) ) // NimROD介面
            {
                NimRODTheme.setTheme( classThemeName );
            }


            className = realClassName; // 復原真正的類別名稱，這樣才有辦法setLookAndFeel
        }

        //className = "com.oyoaha.swing.plaf.oyoaha.OyoahaLookAndFeel";

        try
        {

            // 外部的L&F
            if ( className.matches( napkinClassName ) ) //CommonGUI.isOuterLookAndFeel( className ) ) // 
            {
                Class skinClass = null;
                skinClass = CommonGUI.getOuterClass( className, napkinFileName );
                
                /*
                if ( className.matches( napkinClassName ) )
                {
                    skinClass = CommonGUI.getOuterClass( className, napkinFileName );
                }
                
                else if ( className.matches( nimrodClassName ) )
                {
                    skinClass = CommonGUI.getOuterClass( className, nimrodFileName );
                }
                else if ( className.matches( jtattooClassName ) )
                {
                    skinClass = CommonGUI.getOuterClass( className, jtattooFileName );

                }
                else if ( className.matches( substanceClassName ) )
                {
                    skinClass = CommonGUI.getOuterClass( className, substanceFileName );
                }
                */

                LookAndFeel laf = ( LookAndFeel ) CommonGUI.getNewInstanceFromClass( skinClass );
                if ( laf != null )
                {
                    UIManager.setLookAndFeel( laf );
                }
                else
                {
                    Common.errorReport( "建立" + skinClass.getSimpleName() + "介面的物件失敗！（null）" );
                }
            }
            else
            {
                UIManager.setLookAndFeel( className );
            }



            /*
             else if ( className.matches( ".*substance.api.skin.*" ) )
             {
             Class napkinClass = CommonGUI.getOuterClass( className, "substance-6.1.jar" );
             LookAndFeel laf = ( LookAndFeel ) CommonGUI.getNewInstanceFromClass( napkinClass );
             if ( laf != null )
             {
             UIManager.setLookAndFeel( laf );
             }
             else
             {
             Common.errorReport( "建立substance介面的物件失敗！（null）" );
             }
             }
             */
            /*
             else
             {
             UIManager.setLookAndFeel( className );
             }
             */

            //UIManager.setLookAndFeel( new com.oyoaha.swing.plaf.oyoaha.OyoahaLookAndFeel());
        }
        catch ( Exception ex )
        {
            Common.errorReport( "無法使用" + className + "界面 !!" );
            try
            {
                UIManager.setLookAndFeel( ComicDownGUI.getDefaultSkinClassName() );
            }
            catch ( Exception exx )
            {
                
                Common.errorReport( "無法設置預設的Lood And Feel: " + ComicDownGUI.getDefaultSkinClassName() );
            }
        }

    }

    // 檢查skin是否由外部jar支援，若是外部skin且沒有此jar，則下載
    public static boolean checkSkin()
    {


        boolean foundJAR = false; // 是否有設定值需要用到的JAR檔

        if ( SetUp.getSkinClassName().matches( jtattooClassName )
                && !Common.existJAR( jtattooFileName ) )
        {
            new CommonGUI().downloadNewTheme( "JTattoo", jtattooFileName,
                                              "http://jcomicdownloader.googlecode.com/files/JTattoo.jar" ); // 下載JTattoo.jar
        }
        else if ( SetUp.getSkinClassName().matches( nimrodClassName )
                && !Common.existJAR( nimrodFileName ) )
        {
            new CommonGUI().downloadNewTheme( "NimRod", nimrodFileName,
                                              "http://jcomicdownloader.googlecode.com/files/nimrodlf-1.2.jar" ); // 下載nimrodlf-1.2.jar
        }
        else if ( SetUp.getSkinClassName().matches( napkinClassName )
                && !Common.existJAR( napkinFileName ) )
        {
            new CommonGUI().downloadNewTheme( "Napkin", napkinFileName,
                                              "https://sites.google.com/site/jcomicdownloaderbackup/release/napkinlaf-alpha001.jar?attredirects=0&d=1" ); // 下載napkinlaf-alpha001.jar
        }
        else if ( SetUp.getSkinClassName().matches( substanceClassName )
                && (!Common.existJAR( substanceFileName ) || !Common.existJAR( tridentFileName )) )
        {
            String[] themeNames = new String[]
            {
                "Substance", "Trident"
            };
            String[] fileNames = new String[]
            {
                substanceFileName, tridentFileName
            };
            String[] urls = new String[]
            {
                "https://sites.google.com/site/jcomicdownloaderbackup/release/substance-6.1.jar?attredirects=0&d=1",
                "https://sites.google.com/site/jcomicdownloaderbackup/release/trident.jar?attredirects=0&d=1"
            };
            new CommonGUI().downloadNewTheme( themeNames, fileNames,
                                              urls ); // 
        }
        else
        {
            foundJAR = true;
        }

        return foundJAR;

    }

    // 下載單一個jar檔
    public void downloadNewTheme( String themeName,
                                  final String themeFileName, final String themeURL )
    {

        downloadNewTheme( new String[]
                {
                    themeName
                },
                          new String[]
                {
                    themeFileName
                },
                          new String[]
                {
                    themeURL
                } );
    }

    // 下載多個jar檔
    public void downloadNewTheme( String[] themeNames,
                                  final String[] themeFileNames, final String[] themeURLs )
    {
        String skinName = getSkinNameFromClassName( SetUp.getSkinClassName() );

        String tempString = "";
        for ( String string : themeFileNames )
        {
            tempString += string + ", ";
        }
        tempString =
                tempString.substring( 0, tempString.length() - 2 );

        final String themeFileNameString = tempString;

        String themeNameString = "";
        for ( String string : themeNames )
        {
            themeNameString += string + ", ";
        }
        themeNameString =
                themeNameString.substring( 0, themeNameString.length() - 2 );

        int choice = CommonGUI.showConfirmDialog( null,
                                                  "資料夾內未發現"
                + themeFileNameString + "，無法使用"
                + skinName + "界面！<br><br>請問是否要下載" + themeFileNameString + " ？",
                                                  "Download the " + themeFileNameString + " for new theme ?",
                                                  "提醒訊息", JOptionPane.YES_NO_OPTION );

        if ( choice == JOptionPane.YES_OPTION )
        {
            Common.downloadJarFiles( themeURLs, themeFileNames );
        }
        else
        {
            skinName = getSkinNameFromClassName( ComicDownGUI.getDefaultSkinClassName() );
            SetUp.setSkinClassName( ComicDownGUI.getDefaultSkinClassName() );
            CommonGUI.showMessageDialog( ComicDownGUI.mainFrame, "不下載" + themeFileNameString + "，使用預設的"
                    + skinName + "界面",
                                         "提醒訊息", JOptionPane.INFORMATION_MESSAGE );

        }
    }

    // 更新介面
    public static void updateUI( final Component c )
    {
        // 把SwingUtilities.invokeLater註解掉後置換laf就不會有錯誤訊息了....
        SwingUtilities.invokeLater( new Runnable()
        {

            public void run()
            {
                try
                {
                    SwingUtilities.updateComponentTreeUI( c );
                    //c.repaint();setLookAndFeelByClassName
                }
                catch ( Exception ex )
                {
                    Common.errorReport( "更新界面時發生錯誤" );
                }
            }
        } );
    }

    private String getSkinNameFromClassName( String className )
    {
        String[] tempStrings = className.split( "\\." );
        return tempStrings[tempStrings.length - 1].replaceAll( "LookAndFeel", "" );
    }

    public static void newFrameStartInEDT( final String frameClassName, final boolean isVisible )
    {
        new Thread( new Runnable()
        {

            public void run()
            {
                SwingUtilities.invokeLater( new Runnable()
                {

                    public void run()
                    {
                        try
                        {
                            Class newClass = Class.forName( frameClassName );

                            JFrame frame = ( JFrame ) newClass.getConstructor().newInstance();
                            frame.setVisible( isVisible );
                        }
                        catch ( Exception ex )
                        {
                            Logger.getLogger( CommonGUI.class.getName() ).log( Level.SEVERE, null, ex );
                        }
                    }
                } );
            }
        } ).start();
    }

    // 回傳一個有設定背景圖片的panel
    public static JPanel getImagePanel( final String picFileString )
    {
        JPanel imagePanel = new JPanel()
        {

            protected void paintComponent( Graphics g )
            {
                ImageIcon icon = new ImageIcon( picFileString );
                Image img = icon.getImage();
                g.drawImage( img, 0, 0, icon.getIconWidth(),
                             icon.getIconHeight(), icon.getImageObserver() );
                //frame.setSize( icon.getIconWidth(), icon.getIconHeight() );

            }
        };

        return imagePanel;
    }

    // 取得圖片尺寸
    public static Dimension getDimension( String picFileString )
    {
        ImageIcon image = new ImageIcon( picFileString );
        return new Dimension( image.getIconWidth(), image.getIconHeight() );
    }

    public static void chooseFile( final Component thisComponent, final int type,
                                   final String title, final JTextField textField, final String directoryString )
    {
        chooseFile( thisComponent, type, title, textField, directoryString, null );
    }

    public static void chooseFile( final Component thisComponent, final int type,
                                   final String title, final JTextField textField, final String directoryString,
                                   final javax.swing.filechooser.FileFilter fileFilter )
    {
        new Thread( new Runnable()
        {

            public void run()
            {
                SwingUtilities.invokeLater( new Runnable()
                {

                    public void run()
                    {
                        // 避免在NapKin Look and Feel下發生錯誤( JRE 7的問題)
                        if ( SetUp.getSkinClassName().matches( CommonGUI.napkinClassName ) )
                        {
                            CommonGUI.setLookAndFeelByClassName( ComicDownGUI.getDefaultSkinClassName() );
                        }

                        if ( SetUp.getSkinClassName().matches( CommonGUI.napkinClassName ) )
                        {
                            // 因為napkin不支援JFileChooser，所以瀏覽檔案之前先轉為預設介面
                            CommonGUI.setLookAndFeelByClassName( ComicDownGUI.getDefaultSkinClassName() );
                        }

                        JFileChooser dirChooser = new JFileChooser( directoryString );
                        //SwingUtilities.updateComponentTreeUI(dirChooser);

                        if ( thisComponent.getClass().getName().matches( ".*BackgroundSettingFrame.*" ) )
                        {
                            // 開啟圖片預覽功能
                            ImagePreview preview = new ImagePreview( dirChooser );
                            dirChooser.addPropertyChangeListener( preview );
                            dirChooser.setAccessory( preview );
                            //dirChooser.showOpenDialog( null );
                        }

                        dirChooser.setFileSelectionMode( type );

                        if ( fileFilter != null )
                        {
                            dirChooser.addChoosableFileFilter( fileFilter );
                            dirChooser.setAcceptAllFileFilterUsed( false );
                        }

                        dirChooser.setDialogTitle( title );

                        try
                        {
                            //CommonGUI.setLookAndFeelByClassName( ComicDownGUI.getDefaultSkinClassName() );
                            //CommonGUI.updateUI( dirChooser );


                            int result = dirChooser.showDialog( thisComponent, "確定" );

                            if ( result == JFileChooser.APPROVE_OPTION )
                            {
                                File file = dirChooser.getSelectedFile();

                                String path = "";

                                if ( file.getPath().matches( "(?s).*" + Common.getRegexSlash() )
                                        || type == JFileChooser.FILES_ONLY )
                                { // 若是檔案就不須在最後加斜線
                                    path = file.getPath();
                                }
                                else
                                {
                                    path = file.getPath() + Common.getSlash();
                                }

                                textField.setText( path );



                            }
                        }
                        catch ( HeadlessException ex )
                        {
                            Common.errorReport( "選擇視窗發生錯誤 !!" );
                        }

                        if ( SetUp.getSkinClassName().matches( CommonGUI.napkinClassName ) )
                        {
                            // 再將laf改回來。
                            CommonGUI.setLookAndFeelByClassName( SetUp.getSkinClassName() );
                        }
                        //CommonGUI.setLookAndFeelByClassName( SetUp.getSkinClassName() );

                    }
                } );

            }
        } ).start();
    }

    // 取得由html包起來的tooltip字串，使用設定字型和背景顏色
    public static String getHtmlStringOfToolTip( String string )
    {
        int htmlFontSize = ( int ) (SetUp.getDefaultFontSize() / 4);
        String htmlFontFace = SetUp.getDefaultFontName();
        return "<html><font face=\"" + htmlFontFace
                + "\" size=\"" + htmlFontSize + "\"" + " bgcolor=\"" + "white" + "\" > " + string + "</font></html>";
    }

    // 用於tab和table
    public static String getToolTipString( String toolTipString )
    {
        toolTipString = Common.getStringUsingDefaultLanguage( toolTipString, toolTipString ); // 使用預設語言 

        if ( SetUp.getSkinClassName().matches( ".*napkin.*" ) )
        {
            // 因為有可能變成透明而看不清楚
            return getHtmlStringOfToolTip( toolTipString );
        }
        else
        {
            return toolTipString;
        }
    }

    // 用於絕大多數的元件
    public static void setToolTip( JComponent componet, String toolTipString )
    {
        toolTipString = Common.getStringUsingDefaultLanguage( toolTipString, "" ); // 使用預設語言 

        if ( SetUp.getSkinClassName().matches( ".*napkin.*" ) )
        {
            // 因為有可能變成透明而看不清楚
            toolTipString = getHtmlStringOfToolTip( toolTipString );
        }
        componet.setToolTipText( toolTipString );
    }

    public static void showMessageDialog( Component parentComponent,
                                          String message, String title, int messageType )
    {
        message = Common.getStringUsingDefaultLanguage( message, message ); // 使用預設語言 
        title = Common.getStringUsingDefaultLanguage( title, title ); // 使用預設語言 

        CommonGUI.showMessageDialogRun( parentComponent, message, title, messageType );
    }

    // 取得訊息中的字型大小
    public static int geteMessageFontSize()
    {
        return SetUp.getDefaultFontSize() / 4;
    }

    public static void showMessageDialog( Component parentComponent, String message )
    {
        message = Common.getStringUsingDefaultLanguage( message, message ); // 使用預設語言 
        JOptionPane.showMessageDialog( parentComponent,
                                       "<html><font size="
                + CommonGUI.geteMessageFontSize()
                + ">" + message
                + "</font></html>" );
    }

    public static String showInputDialog( Component parentComponent, String message, String title, int messageType )
    {
        message = Common.getStringUsingDefaultLanguage( message, message ); // 使用預設語言 
        title = Common.getStringUsingDefaultLanguage( title, title ); // 使用預設語言 
        return CommonGUI.showInputDialogRun( parentComponent,
                                             message, title, messageType );
    }

    public static int showConfirmDialog( Component parentComponent, String message, String enMessage, String title, int optionType )
    {
        message = Common.getStringUsingDefaultLanguage( message, enMessage ); // 使用預設語言 
        title = Common.getStringUsingDefaultLanguage( title, title ); // 使用預設語言 
        return JOptionPane.showConfirmDialog( parentComponent,
                                              "<html><font size="
                + CommonGUI.geteMessageFontSize()
                + ">"
                + message
                + "</font></html>", title, optionType );
    }

    public static int showOptionDialog( Component parentComponent,
                                        Object message, String title, int optionType,
                                        int messageType, Icon icon, Object[] options, Object initialValue )
    {
        message = Common.getStringUsingDefaultLanguage( message.toString(), message.toString() ); // 使用預設語言 
        title = Common.getStringUsingDefaultLanguage( title, title ); // 使用預設語言 

        return showOptionDialogRun( parentComponent, message, title, optionType,
                                    messageType, icon, options, initialValue );
    }

    public static int showOptionDialogRun( final Component parentComponent,
                                           final Object message, final String title, final int optionType,
                                           final int messageType, final Icon icon, final Object[] options, Object initialValue )
    {

        SwingUtilities.invokeLater( new Runnable()
        {

            public void run()
            {
                try
                {
                    CommonGUI.optionDialogChoice = JOptionPane.showOptionDialog( parentComponent, "<html><font size="
                            + CommonGUI.geteMessageFontSize()
                            + ">"
                            + message
                            + "</font></html>",
                                                                                 title, optionType, messageType, icon, options, options[0] );

                    //notifyAll();
                }
                catch ( Exception ex )
                {
                    Common.errorReport( "更新界面時發生錯誤" );
                }
            }
        } );

        while ( CommonGUI.optionDialogChoice < 0 )
        {
            try
            {
                // 每睡一秒就檢查一次
                Thread.sleep( 1000 );
            }
            catch ( InterruptedException ex )
            {
                Common.hadleErrorMessage( ex, "無法讓Thread睡眠（sleep）" );
            }
        }

        return CommonGUI.optionDialogChoice;
    }

    public static String showInputDialogRun( final Component parentComponent,
                                             final String message, final String title, final int messageType )
    {

        SwingUtilities.invokeLater( new Runnable()
        {

            public void run()
            {
                try
                {
                    CommonGUI.showInputDialogValue = JOptionPane.showInputDialog(
                            parentComponent, "<html><font size="
                            + CommonGUI.geteMessageFontSize()
                            + ">"
                            + message
                            + "</font</html>", title, messageType );

                    //notifyAll();
                }
                catch ( Exception ex )
                {
                    Common.errorReport( "更新界面時發生錯誤" );
                }
            }
        } );

        while ( CommonGUI.showInputDialogValue.matches( "InitialValue" ) )
        {
            try
            {
                // 每睡一秒就檢查一次
                Thread.sleep( 1000 );
            }
            catch ( InterruptedException ex )
            {
                Common.hadleErrorMessage( ex, "無法讓Thread睡眠（sleep）" );
            }
        }

        return CommonGUI.showInputDialogValue;
    }

    public static void showMessageDialogRun( final Component parentComponent,
                                             final String message, final String title, final int messageType )
    {
        CommonGUI.showMessageOK = false;


        JOptionPane.showMessageDialog(
                parentComponent, "<html><font size="
                + CommonGUI.geteMessageFontSize()
                + ">"
                + message
                + "</font></html>", title, messageType );

        /*
         SwingUtilities.invokeLater( new Runnable() {
         public void run() {
         try {
         JOptionPane.showMessageDialog(
         parentComponent, message, title, messageType );
         }
         catch ( Exception ex ) {
         Common.errorReport( "更新界面時發生錯誤" );
         }
         CommonGUI.showMessageOK = true;
         }
         } );
         //CommonGUI.showMessageOK = true;

         System.out.println( CommonGUI.showMessageOK );

         while ( !CommonGUI.showMessageOK ) {
         try {
         // 每睡0.5秒就檢查一次
         Thread.sleep( 500 );
         Common.debugPrint( "." );
         }
         catch ( InterruptedException ex ) {
         Common.hadleErrorMessage( ex, "無法讓Thread睡眠（sleep）" );
         }
         }
         */

    }
}