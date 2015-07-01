/*
 ----------------------------------------------------------------------------------------------------
 Program Name : JComicDownloader
 Authors  : surveyorK
 Last Modified : 2012/1/10
 ----------------------------------------------------------------------------------------------------
 ChangeLog:
 5.12: 修改選擇集數機制，當只有唯一集數時自動選取。
 5.02: 修復Java 7下無法使用NapKin Look and Feel的問題。
 2.17: 增加可以選擇反白集數的選項。
 2.16: 改變暗色系界面的已下載和未下載的顏色標示。
 2.01: 增加是否預設集數全選的選項
 1.16: 讓EH和EX也能判斷是否已經下載。
 1.14: 修改集數選擇視窗（choiceFrame）的關閉功能，允許按右上角的『X』來關閉。
 1.11: 已經存在於資料夾的集數顯示淺色
 1.09: 加入任務的同時也加入記錄
 ----------------------------------------------------------------------------------------------------
 */
package jcomicdownloader.frame;

import java.util.logging.Level;
import java.util.logging.Logger;
import jcomicdownloader.table.DownloadTableModel;
import jcomicdownloader.tools.*;
import jcomicdownloader.enums.*;
import jcomicdownloader.*;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;
import javax.swing.table.*;
import jcomicdownloader.table.ChoiceTableRender;
import jcomicdownloader.table.DownTableRender;

/**

 選取集數視窗
 */
public class ChoiceFrame extends JFrame implements
        TableModelListener, MouseListener, MouseMotionListener
{

    static final long serialVersionUID = 3345678;
    // about RadioButton
    private JRadioButton choiceAll, choiceNull, choiceSelected;
    private ButtonGroup choiceGroup;
    // about Button
    private JButton confirmButton;
    private JButton cancelButton;
    public static JTable volumeTable;
    public DownloadTableModel volumeTableModel;
    private String[] columnNames; // store the colume names
    private static String[] volumeStrings; // store the volume strings
    private static String[] urlStrings; // store the volume url
    private static String[] checkStrings; // store the choice or not of volumes
    private int modifyRow;
    private boolean modifySelected;
    private String title; // 傳入作品名稱，避免連續下載中被混淆
    private String url; // 傳入位址，避免同時有兩個位址在解析會混淆
    public static JFrame choiceFrame; // use by other frame
    public static JFrame thisFrame; // use by self
    private Dimension frameDimension;

    public ChoiceFrame( String title, String url )
    {
        
        this( Common.getStringUsingDefaultLanguage( "選擇欲下載的集數 [", "selected volumes [" ) + title + "]",
              false, 0, title, url );
         
    }

    public ChoiceFrame( String frameTitle, boolean modifySelected, int modifyRow, String title, String url )
    {
       
        super( Common.getStringUsingDefaultLanguage( frameTitle, frameTitle ) );
        
        SwingUtilities.updateComponentTreeUI( this );
        
        this.title = title;
        this.url = url;

        ChoiceFrame.choiceFrame = ChoiceFrame.thisFrame = this; // for close the frame

        this.modifyRow = modifyRow;
        this.modifySelected = modifySelected;

        columnNames = new String[]
        {
            "是否下載", "標題"
        };
        volumeStrings = Common.getFileStrings( SetUp.getTempDirectory(), Common.tempVolumeFileName );
        urlStrings = Common.getFileStrings( SetUp.getTempDirectory(), Common.tempUrlFileName );
        checkStrings = new String[ volumeStrings.length ];

        if ( modifySelected )
        {
            // 因為存入時是依當時顯示的順序，所以取得勾選集數的時候也要依當時順序為主
            checkStrings = getRealOrderCheckStrings( modifyRow, volumeStrings,
                                                     ComicDownGUI.nowSelectedCheckStrings );
        }
        else
        {
            // 依設定選擇是否預設全選
            String choiceString = SetUp.getChoiceAllVolume() ? "true" : "false";

            for ( int i = 0; i < volumeStrings.length; i++ )
            {
                checkStrings[i] = choiceString;
            }
        }

        if ( volumeStrings.length == 1 ) // 如果只有一集，就預設勾選下載
        {
            checkStrings[0] = "true";
        }

        setUpUIComponent();

        setVisible( true );
        
        if ( volumeStrings.length == 1 ) // 如果只有一集，就直接加入任務
        {
            new ActionHandler().confirm();  // 確認加入任務的流程
            //dispose();
            
            //confirmButton.setSelected( true );
        }
    }

    // 取得真實順序的挑選集數，才不會亂掉
    private String[] getRealOrderCheckStrings( int row, String[] volumeStrings, String[] checkStrings )
    {
        String[] realOrderVolumeStrings = Common.getSeparateStrings( ComicDownGUI.downTableModel.getRealValueAt( row, DownTableEnum.VOLUMES ).toString() );
        String[] realOrderCheckStrings = new String[ volumeStrings.length ];

        for ( int i = 0; i < realOrderVolumeStrings.length; i++ )
        {
            for ( int j = 0; j < realOrderVolumeStrings.length; j++ )
            {
                if ( realOrderVolumeStrings[j].equals( volumeStrings[i] ) )
                {
                    realOrderCheckStrings[j] = checkStrings[i];
                    break;
                }
            }
        }

        return realOrderCheckStrings;
    }

    private void setUpUIComponent()
    {
        // 因應JRE 7，重新設定Look and Feel
        CommonGUI.setLookAndFeelByClassName( SetUp.getSkinClassName() );

        String picFileString = SetUp.getBackgroundPicPathOfChoiceFrame();
        // 檢查背景圖片是否存在
        if ( SetUp.getUsingBackgroundPicOfChoiceFrame()
                && !new File( picFileString ).exists() )
        {
            JOptionPane.showMessageDialog( this, picFileString
                    + "\n背景圖片不存在，重新設定為原始佈景",
                                           "提醒訊息", JOptionPane.INFORMATION_MESSAGE );
            SetUp.setUsingBackgroundPicOfChoiceFrame( false );
        }

        if ( SetUp.getUsingBackgroundPicOfChoiceFrame() )
        {
            frameDimension = CommonGUI.getDimension( picFileString );
            int width = ( int ) frameDimension.getWidth() + CommonGUI.widthGapOfBackgroundPic;
            int height = ( int ) frameDimension.getHeight() + CommonGUI.heightGapOfBackgroundPic;
            setSize( width, height );
            setResizable( false );
        }
        else
        {
            int extendWidth = (SetUp.getDefaultFontSize() - 18) * 10; // 跟隨字體加寬
            extendWidth = extendWidth > 0 ? extendWidth : 0;
            setSize( 350 + extendWidth, 470 + extendWidth );
            setResizable( true );
        }

        Container contentPane;
        if ( SetUp.getUsingBackgroundPicOfChoiceFrame() )
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
        contentPane.setLayout( new BorderLayout() );

        setDefaultLookAndFeelDecorated( false ); // 讓標題欄可以隨look and feel改變
        setLocationRelativeTo( this );  // set the frame in middle position of screen
        setIconImage( new CommonGUI().getImage( CommonGUI.mainIcon ) ); // 設置左上角圖示

        setRadioButtonUI( contentPane );
        setVolumeTableUI( contentPane );
        setButtonUI( contentPane );


        setUpeListener();
    }

    private void setRadioButtonUI( Container contentPane )
    {
        JPanel radioPanel = new JPanel();
        radioPanel.setLayout( new FlowLayout( FlowLayout.CENTER ) );
        CommonGUI.setToolTip( radioPanel, "下載順序由上而下，點擊上方『標題名稱』可改變集數的下載順序" );

        choiceAll = getRadioButton( "全部選擇", "mark all", false );
        choiceNull = getRadioButton( "全部取消", "unmark all ", false );
        choiceSelected = getRadioButton( "選擇反白集數", "mark selected", false );

        choiceGroup = new ButtonGroup();
        choiceGroup.add( choiceAll );
        choiceGroup.add( choiceNull );
        choiceGroup.add( choiceSelected );

        radioPanel.add( choiceAll );
        radioPanel.add( choiceNull );
        radioPanel.add( choiceSelected );

        contentPane.add( radioPanel, BorderLayout.NORTH );
    }

    private void setVolumeTableUI( Container contentPane )
    {
        volumeTableModel = getDownloadTableModel();
        volumeTable = new JTable( volumeTableModel )
        {

            protected String[] columnToolTips =
            {
                CommonGUI.getToolTipString( "希望下載哪一集就在同列的此欄位打勾" ),
                CommonGUI.getToolTipString( "顯示淺色代表已經下載過（不保證下載完整，請自行檢查）" )
            };

            //Implement table header tool tips. 
            protected JTableHeader createDefaultTableHeader()
            {
                return new JTableHeader( columnModel )
                {

                    public String getToolTipText( MouseEvent e )
                    {
                        String tip = null;
                        java.awt.Point p = e.getPoint();
                        int index = columnModel.getColumnIndexAtX( p.x );
                        int realIndex = columnModel.getColumn( index ).getModelIndex();
                        return columnToolTips[realIndex];
                    }
                };
            }
        };

        volumeTable.setRowHeight( SetUp.getDefaultFontSize() + 3 ); // 隨字體增加寬度

        volumeTable.setModel( volumeTableModel );
        volumeTable.setPreferredScrollableViewportSize( new Dimension( 400, 170 ) );
        volumeTable.setFillsViewportHeight( true );
        volumeTable.setAutoCreateRowSorter( true );

        volumeTableModel.addTableModelListener( this );

        setDefaultRenderer(); // 設置volumeTable上哪些集數要變色

        // 取得這個table的欄位模型
        TableColumnModel cModel = volumeTable.getColumnModel();

        // 配置每個欄位的寬度比例（可隨視窗大小而變化）
        cModel.getColumn( ChoiceTableEnum.YES_OR_NO ).setPreferredWidth( ( int ) (this.getWidth() * 0.25) );
        cModel.getColumn( ChoiceTableEnum.VOLUME_TITLE ).setPreferredWidth( ( int ) (this.getWidth() * 0.75) );

        // 若設定為透明，就用預定顏色字體。
        if ( SetUp.getUsingBackgroundPicOfChoiceFrame() )
        {
            volumeTable.getTableHeader().setForeground( SetUp.getChoiceFrameTableDefaultColor() );
            volumeTable.setForeground( SetUp.getChoiceFrameTableDefaultColor() );
            //volumeTable.addMouseMotionListener( this ); // 可以隨著滑鼠移動而變色
        }

        if ( SetUp.getSkinClassName().matches( ".*napkin.*" ) )
        {
            // 因為napkin的預設字型不太清楚，所以用選定字型
            volumeTable.setFont( SetUp.getDefaultFont( - 2 ) );
            volumeTable.getTableHeader().setFont( SetUp.getDefaultFont( - 2 ) );
        }

        JScrollPane volumeScrollPane = new JScrollPane( volumeTable );

        JPanel volumePanel = new CommonGUI().getCenterPanel( volumeScrollPane );
        CommonGUI.setToolTip( volumePanel, "下載順序由上而下，點擊上方『標題名稱』可改變集數的下載順序" );

        contentPane.add( volumePanel, BorderLayout.CENTER );

    }

    private void setDefaultRenderer()
    { // 設置volumeTable上哪些集數要變色
        DefaultTableCellRenderer cellRender = null;

        cellRender = new ChoiceTableRender( title, url, volumeTableModel );

        try
        {
            volumeTable.setDefaultRenderer( Class.forName( "java.lang.Object" ), cellRender );
        }
        catch ( ClassNotFoundException ex )
        {
            Common.hadleErrorMessage( ex, "無法設置volumeTable上哪些集數要變色" );
        }
    }

    private Vector<String> getDefaultColumns()
    {
        Vector<String> columnName = new Vector<String>();

        if ( SetUp.getDefaultLanguage() == LanguageEnum.TRADITIONAL_CHINESE )
        {
            columnName.add( "是否下載" );
            columnName.add( "標題名稱" );
        }
        else
        {
            columnName.add( "是否下载" );
            columnName.add( "标题名称" );
        }

        return columnName;
    }

    private DownloadTableModel getDownloadTableModel()
    {
        DownloadTableModel tableModel = new DownloadTableModel( getDefaultColumns(), 0 );

        for ( int i = 0; i < volumeStrings.length; i++ )
        {
            tableModel.addRow( CommonGUI.getVolumeDataRow( checkStrings[i], volumeStrings[i] ) );
        }

        return tableModel;
    }

    private void setButtonUI( Container contentPane )
    {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout( new FlowLayout( FlowLayout.CENTER ) );

        confirmButton = getButton( "確定", "ok", KeyEvent.VK_Y );
        cancelButton = getButton( "取消", "cancel", KeyEvent.VK_N );

        buttonPanel.add( confirmButton );
        buttonPanel.add( cancelButton );


        contentPane.add( buttonPanel, BorderLayout.SOUTH );
    }

    public static String[] getVolumeStrings()
    {
        return volumeStrings;
    }

    public static String[] getCheckStrings()
    {
        return checkStrings;
    }

    public static String[] getUrlStrings()
    {
        return urlStrings;
    }

    private void setUpeListener()
    {
        // do nothing when click X,
        // because it needs click buttons to unlock the downloadLock
        //setDefaultCloseOperation( JFrame.DO_NOTHING_ON_CLOSE );



        thisFrame.addWindowListener( new WindowAdapter()
        {

            public void windowClosing( WindowEvent e )
            {
                notifyAllDownload();
                dispose();
            }
        } );
    }

    public void tableChanged( TableModelEvent event )
    {
        //System.out.println( event.getColumn() + " " + event.getFirstRow() + " " + event.getLastRow() );
        checkStrings[event.getFirstRow()] = volumeTableModel.getValueAt( event.getFirstRow(), event.getColumn() ).toString();
        //System.out.println( checkStrings[event.getFirstRow()] + "#  " );

    }

    // 依最後顯示的集數順序來改變原本順序
    public static String[] changedStringsOrderToView( String[] strings )
    {
        String[] tempStrings = new String[ strings.length ];

        for ( int i = 0; i < strings.length; i++ )
        {
            tempStrings[i] = strings[volumeTable.convertRowIndexToModel( i )];
        }

        return tempStrings;
    }

    // 儲存顯示的正確順序，這樣重新勾選的時候勾選集數才會一致
    public void storeVolumeRealOrder( int row, int totalVolume )
    {
        ComicDownGUI.downTableRealChoiceOrder[row] = new int[ totalVolume ];
        for ( int i = 0; i < totalVolume; i++ )
        {
            ComicDownGUI.downTableRealChoiceOrder[row][i] = volumeTable.convertRowIndexToModel( i );
        }
    }

    // 在重新選擇集數時，取得真實順序
    public int getVolumeReadOrder( int row, int falseOrder )
    {
        int realOrder = ComicDownGUI.downTableRealChoiceOrder[row][falseOrder];

        return realOrder;
    }

    // -------------  Listener  ---------------
    public void notifyAllDownload()
    { // unLock main frame
        synchronized ( ComicDownGUI.mainFrame )
        {
            Common.debugPrintln( "解除downloadLock，允許下載" );
            Common.downloadLock = false;
            ComicDownGUI.mainFrame.notifyAll();
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
        if ( SetUp.getUsingBackgroundPicOfChoiceFrame() )
        {

            if ( event.getSource() instanceof JTable )
            { // 主要是table
                CommonGUI.nowMouseAtRow = 10000; // 給很大的初始值，避免剛開始就有上色情形
                (( JComponent ) event.getSource()).repaint();
            }
            else
            {
                (( JComponent ) event.getSource()).setForeground( SetUp.getChoiceFrameOtherDefaultColor() );
                (( JComponent ) event.getSource()).repaint();

            }
        }
    }

    @Override
    public void mouseEntered( MouseEvent event )
    {
        if ( SetUp.getUsingBackgroundPicOfChoiceFrame() )
        {
            if ( event.getSource() instanceof JTable )
            {
            }
            else
            {
                (( JComponent ) event.getSource()).setForeground( SetUp.getChoiceFrameOtherMouseEnteredColor() );
            }
        }
    }

    @Override
    public void mouseDragged( MouseEvent e )
    {
    }

    @Override
    public void mouseMoved( MouseEvent event )
    {
        JTable table = ( JTable ) event.getSource();

        // 現在滑鼠所在的列
        CommonGUI.nowMouseAtRow = event.getY() / table.getRowHeight();
        table.repaint(); // 給目前滑鼠所在列改變字體顏色
    }

    private class ActionHandler implements ActionListener
    {

        public void actionPerformed( ActionEvent event )
        {


            if ( event.getSource() == cancelButton )
            {
                notifyAllDownload();

                dispose();
            }
            else if ( event.getSource() == confirmButton )
            {
                confirm();  // 確認加入任務的流程
            }
        }

        // 確認加入任務的流程
        private void confirm()
        {
            String[] volumeStrings = ChoiceFrame.getVolumeStrings();
            String[] checkStrings = ChoiceFrame.getCheckStrings();
            String[] urlStrings = ChoiceFrame.getUrlStrings();


            // 依最後調整的順序為主
            volumeStrings = changedStringsOrderToView( volumeStrings );
            checkStrings = changedStringsOrderToView( checkStrings );
            urlStrings = changedStringsOrderToView( urlStrings );

            if ( modifySelected )
            { // 只是重新選擇集數
                ComicDownGUI.downTableModel.removeRow( modifyRow );
                Vector<Object> dataVector = CommonGUI.getDownDataRow(
                        modifyRow + 1,
                        title,
                        volumeStrings,
                        checkStrings,
                        urlStrings );

                storeVolumeRealOrder( modifyRow, volumeStrings.length ); // 存入真實集數順序

                if ( Common.missionCount == 1 )
                {
                    ComicDownGUI.downTableModel.addRow( dataVector );
                }
                else
                {
                    ComicDownGUI.downTableModel.insertRow( modifyRow, dataVector );
                }

            }
            else
            { // 加入新任務
                storeVolumeRealOrder( Common.missionCount + 1, volumeStrings.length ); // 存入真實集數順序

                ComicDownGUI.downTableModel.addRow( CommonGUI.getDownDataRow(
                        ++Common.missionCount,
                        title,
                        volumeStrings,
                        checkStrings,
                        urlStrings ) );

                ComicDownGUI.recordTableModel.addRow( CommonGUI.getRecordDataRow(
                        ++Common.recordCount,
                        title,
                        new String( url ) ) );

                // 只有在第一次選擇集數的時候才紀錄位址，之後重新選擇就可以利用這個位址了。
                ComicDownGUI.downTableUrlStrings[Common.missionCount - 1] = new String( url );
                //ComicDownGUI.recordTableUrlStrings[Common.recordCount-1] = new String( url );

            }

            // 每加一個任務就紀錄一次。
            Common.outputRecordTableFile( ComicDownGUI.recordTableModel );
            Common.outputDownTableFile( ComicDownGUI.downTableModel );

            //Common.preTitle = title;
            notifyAllDownload();

            dispose();
        }
    }

    private class CheckBoxHandler implements ItemListener
    {

        public void itemStateChanged( ItemEvent event )
        {
            if ( event.getStateChange() == ItemEvent.SELECTED )
            {
            }
        }
    }

    private class ItemHandler implements ItemListener
    {

        public void itemStateChanged( ItemEvent event )
        {
            if ( event.getSource() == choiceAll )
            {
                if ( choiceAll.isSelected() )
                {
                    for ( int i = 0; i < volumeTable.getRowCount(); i++ )
                    {
                        volumeTable.setValueAt( true, i, ChoiceTableEnum.YES_OR_NO );
                    }

                    repaint(); // update data on time in the screen
                }
            }
            if ( event.getSource() == choiceNull )
            {
                if ( choiceNull.isSelected() )
                {
                    for ( int i = 0; i < volumeTable.getRowCount(); i++ )
                    {
                        volumeTable.setValueAt( false, i, ChoiceTableEnum.YES_OR_NO );

                    }

                    repaint();
                }
            }
            if ( event.getSource() == choiceSelected )
            {
                if ( choiceSelected.isSelected() )
                {
                    boolean choiceValue;
                    for ( int i = 0; i < volumeTable.getRowCount(); i++ )
                    {
                        // 反白 -> 選擇 ; 沒有反白 -> 不選
                        choiceValue = volumeTable.isRowSelected( i ) ? true : false;
                        volumeTable.setValueAt( choiceValue, i, ChoiceTableEnum.YES_OR_NO );
                    }
                    repaint();
                }
            }

            if ( event.getStateChange() == ItemEvent.SELECTED )
            {
            }

        }
    }

    private JButton getButton( String string, String enString, int keyID )
    {
        string = Common.getStringUsingDefaultLanguage( string, enString ); // 使用預設語言 

        JButton button = new JButton( string );
        button.setMnemonic( keyID ); // 設快捷建為Alt + keyID
        CommonGUI.setToolTip( button, "快捷鍵: Alt + " + KeyEvent.getKeyText( keyID ) );

        //button.setFont( SetUp.getDefaultFont() );
        if ( SetUp.getUsingBackgroundPicOfChoiceFrame() )
        { // 若設定為透明，就用預定字體。
            button.setOpaque( false );
            button.setForeground( SetUp.getChoiceFrameOtherDefaultColor() );
            button.addMouseListener( this );
        }
        button.addActionListener( new ActionHandler() );
        
        if ( SetUp.getSkinClassName().matches( ".*napkin.*" ) )
        {
            // 因為napkin的預設字型不太清楚，所以用選定字型
            button.setFont( SetUp.getDefaultFont( - 2 ) );
        }

        return button;
    }

    private JRadioButton getRadioButton( String string, String enString, boolean selected )
    {
        string = Common.getStringUsingDefaultLanguage( string, enString ); // 使用預設語言 

        JRadioButton radioButton = new JRadioButton( string, selected );
        //button.setFont( SetUp.getDefaultFont() );
        if ( SetUp.getUsingBackgroundPicOfChoiceFrame() )
        { // 若設定為透明，就用預定字體。
            radioButton.setOpaque( false );
            radioButton.setForeground( SetUp.getChoiceFrameOtherDefaultColor() );
            radioButton.addMouseListener( this );

        }
        radioButton.addItemListener( new ItemHandler() );

        if ( SetUp.getSkinClassName().matches( ".*napkin.*" ) )
        {
            // 因為napkin的預設字型不太清楚，所以用選定字型
            radioButton.setFont( SetUp.getDefaultFont( - 2 ) );
        }

        return radioButton;
    }
}
