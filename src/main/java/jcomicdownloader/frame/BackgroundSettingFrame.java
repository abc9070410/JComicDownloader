/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jcomicdownloader.frame;

/** 

 * InformationFrame.java
 ----------------------------------------------------------------------------------------------------
 Program Name : JComicDownloader
 Authors  : surveyorK
 Last Modified : 2012/3/29
 ----------------------------------------------------------------------------------------------------
 ChangeLog:
 5.02: 修復Java 7下無法使用NapKin Look and Feel的問題。
 */
import jcomicdownloader.tools.*;
import jcomicdownloader.module.*;
import jcomicdownloader.*;

/**

 顯示訊息視窗
 */
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import javax.swing.*;
import jcomicdownloader.enums.FrameEnum;

public class BackgroundSettingFrame extends JFrame
    implements ActionListener, MouseListener {

    private int whichFrame;
    JLabel backgroundPicLabel;
    JTextField backgroundPicTextField;
    JButton backgroundPicButton;
    JButton confirmButton;
    JButton choiceOtherDefaultColorButton, // 除了表格外的正常字體顏色
        choiceOtherMouseEnteredColorButton, // 除了表格外的滑鼠碰觸時的字體顏色
        choiceTableDefaultColorButton, // 表格正常字體顏色
        choiceTableMouseEnteredColorButton, // 滑鼠碰觸時的表格字體顏色
        choiceTableFileExistedColorButton, // 表格內已下載集數的字體顏色
        choiceMenuItemDefaultColorButton; // 右鍵選單的正常字體顏色
    public static JFrame thisFrame; // for change look and feel
    private Color backupColor; // 紀錄之前滑鼠經過的元件顏色

    /**

     @author user
     */
    public BackgroundSettingFrame( int whichFrame ) {
        super( "設置背景圖片和字體顏色" );

        this.whichFrame = whichFrame;
        thisFrame = this; // for change look and feel

        setUpUIComponent();
        setUpeListener();
        setVisible( true );

    }

    private void setUpUIComponent() {
        // 因應JRE 7，重新設定Look and Feel
        CommonGUI.setLookAndFeelByClassName( SetUp.getSkinClassName() );
        
        setSize( 470, 340 );
        setDefaultLookAndFeelDecorated( false ); // 讓標題欄可以隨look and feel改變
        setLocationRelativeTo( this );  // set the frame in middle position of screen
        setIconImage( new CommonGUI().getImage( "main_icon.png" ) );

        Container contentPane = getContentPane();

        setTextLayout( contentPane );
    }

    private void setTextLayout( Container contentPane ) {

        JPanel backgroundPicPanel = new CommonGUI().getCenterPanel( new JPanel( new GridLayout( 1, 1 ) ) );
        setBackgroundPicPanel( backgroundPicPanel );

        contentPane.add( backgroundPicPanel, BorderLayout.CENTER );
        contentPane.add( getConfirmPanel(), BorderLayout.SOUTH );

    }

    private void setBackgroundPicPanel( JPanel panel ) {

        backgroundPicLabel = getLabel( getFrameName() + "的背景圖片：       ",
                                     getFrameName() + "'s background pic:     " );
        CommonGUI.setToolTip( backgroundPicLabel, getBackgroundPicToolTip() );

        backgroundPicTextField = getTextField();
        CommonGUI.setToolTip( backgroundPicTextField, getBackgroundPicToolTip() );

        backgroundPicButton = getButton( "選擇新的背景圖片", "select background pic" );
        CommonGUI.setToolTip( backgroundPicButton, getBackgroundPicToolTip() );

        JPanel viewPicFIlePanelHorizontal = new JPanel( new GridLayout( 1, 2, 5, 5 ) );
        viewPicFIlePanelHorizontal.add( backgroundPicLabel );
        viewPicFIlePanelHorizontal.add( backgroundPicButton );


        choiceOtherDefaultColorButton = getButton( "選擇一般介面的正常字體顏色", "select normal font color" );
        CommonGUI.setToolTip( choiceOtherDefaultColorButton, "設定後需重新開啟視窗方可顯現" );
        choiceOtherDefaultColorButton.setForeground( getOtherDefaultColor() );

        choiceOtherMouseEnteredColorButton = getButton( "選擇一般介面被滑鼠碰觸時的字體顏色", "select font color when mouse entered" );
        CommonGUI.setToolTip( choiceOtherMouseEnteredColorButton, "設定後需重新開啟視窗方可顯現" );
        choiceOtherMouseEnteredColorButton.setForeground( getOtherMouseEnteredColor() );

        if ( whichFrame == FrameEnum.MAIN_FRAME
            || whichFrame == FrameEnum.CHOICE_FRAME ) {
            choiceTableDefaultColorButton = getButton( "選擇表格內容的正常字體顏色", "select form font color" );
            CommonGUI.setToolTip( choiceTableDefaultColorButton, "設定後需重新開啟視窗方可顯現" );
            choiceTableDefaultColorButton.setForeground( getTableDefaultColor() );

            choiceTableMouseEnteredColorButton = getButton( "選擇表格內容被滑鼠碰觸時的字體顏色", "select form font color when mouse entered" );
            CommonGUI.setToolTip( choiceTableMouseEnteredColorButton, "設定後需重新開啟視窗方可顯現" );
            choiceTableMouseEnteredColorButton.setForeground( getTableMouseEnteredColor() );
        }
        
        if ( whichFrame == FrameEnum.CHOICE_FRAME ) {
            choiceTableFileExistedColorButton = getButton( "選擇表格裡面已下載集數的字體顏色", "select form font color those exists" );
            CommonGUI.setToolTip( choiceTableFileExistedColorButton, "設定後需重新開啟視窗方可顯現" );
            choiceTableFileExistedColorButton.setForeground( getTableFileExistedColor() );
        }

        if ( whichFrame == FrameEnum.MAIN_FRAME ) {
            choiceMenuItemDefaultColorButton = getButton( "選擇右鍵選單的正常字體顏色", "select menu font color" );
            CommonGUI.setToolTip( choiceMenuItemDefaultColorButton, "設定後需重新開啟視窗方可顯現" );
            choiceMenuItemDefaultColorButton.setForeground( getMenuItemDefaultColor() );
        }
        
        

        JPanel backgroundPicPanel = new JPanel( new GridLayout( 7, 1, 2, 2 ) );
        backgroundPicPanel.add( viewPicFIlePanelHorizontal );
        backgroundPicPanel.add( backgroundPicTextField );

        backgroundPicPanel.add( choiceOtherDefaultColorButton );
        backgroundPicPanel.add( choiceOtherMouseEnteredColorButton );

        if ( whichFrame == FrameEnum.MAIN_FRAME
            || whichFrame == FrameEnum.CHOICE_FRAME ) {
            backgroundPicPanel.add( choiceTableDefaultColorButton );
            backgroundPicPanel.add( choiceTableMouseEnteredColorButton );
        }
        
        if ( whichFrame == FrameEnum.CHOICE_FRAME ) {
            backgroundPicPanel.add( choiceTableFileExistedColorButton );
        }

        if ( whichFrame == FrameEnum.MAIN_FRAME ) {
            backgroundPicPanel.add( choiceMenuItemDefaultColorButton );
        }

        //viewPanel.add( viewZipFilePanelHorizontal );
        //viewPanel.add( viewZipFileTextField );

        panel.add( backgroundPicPanel );
    }

    // 設置最下方的確定按鈕
    private JPanel getConfirmPanel() {
        confirmButton = getButton( "   確定   ", "  OK  " );
        CommonGUI.setToolTip( confirmButton, "儲存目前的設定動作" );

        JPanel choicePanel = new JPanel( new GridLayout( 1, 2, 40, 40 ) );
        choicePanel.add( confirmButton );
        //choicePanel.add( cencelButton );
        //choicePanel.setOpaque( !SetUp.getSetUpValueOfBackgroundImage() );

        JPanel centerPanel = new CommonGUI().getCenterPanel( choicePanel, 10, 40 );

        return centerPanel;
    }

    private String getFrameName() {
        if ( whichFrame == FrameEnum.MAIN_FRAME ) {
            return "主要視窗";
        }
        else if ( whichFrame == FrameEnum.INFORMATION_FRAME ) {
            return "資訊視窗";
        }
        else if ( whichFrame == FrameEnum.OPTION_FRAME ) {
            return "選項視窗";
        }
        else if ( whichFrame == FrameEnum.CHOICE_FRAME ) {
            return "選擇集數視窗";
        }
        else {
            return "沒有這種視窗！";
        }
    }
    
    private String getBackgroundPicToolTip() {
        if ( whichFrame == FrameEnum.MAIN_FRAME ) {
            return "建議選擇640 x 480左右尺寸的圖片";
        }
        else if ( whichFrame == FrameEnum.INFORMATION_FRAME ) {
            return "建議選擇480 x 640左右尺寸的圖片";
        }
        else if ( whichFrame == FrameEnum.OPTION_FRAME ) {
            return "建議選擇600 x 400左右尺寸的圖片";
        }
        else if ( whichFrame == FrameEnum.CHOICE_FRAME ) {
            return "建議選擇350 x 500左右尺寸的圖片";
        }
        else {
            return "沒有這種視窗！";
        }
    }

    private JTextField getTextField() {
        String nowPath = getBackgroundPicPath();
        return new JTextField( nowPath );
    }

    private void setUpeListener() {
        setDefaultCloseOperation( DISPOSE_ON_CLOSE );
    }

    private JLabel getLabel( String string, String enString ) {
        string = Common.getStringUsingDefaultLanguage( string, enString ); // 使用預設語言 
        
        JLabel label = new JLabel( string );
        label.setFont( SetUp.getDefaultFont() );

        return label;
    }

    private JButton getButton( String string, String enString ) {
        string = Common.getStringUsingDefaultLanguage( string, enString ); // 使用預設語言 
        
        JButton button = new JButton( string );
        button.setFont( SetUp.getDefaultFont( 0 ) );
        button.addActionListener( this );
        button.addMouseListener( this );

        return button;
    }

    @Override
    public void actionPerformed( ActionEvent event ) {
        if ( event.getSource() == backgroundPicButton ) {
            String nowPath = getBackgroundPicPath();
            CommonGUI.chooseFile( thisFrame, JFileChooser.FILES_ONLY,
                "選擇背景圖片", backgroundPicTextField, nowPath );
        }
        else if ( event.getSource() == confirmButton ) {
            setBackgroundPicPath( backgroundPicTextField.getText() );

           // JOptionPane.showMessageDialog( this, "設定成功，請重新開啟以便檢視設定成果",
            //    "提醒訊息", JOptionPane.INFORMATION_MESSAGE );

             dispose();
        }
        else if ( event.getSource() == choiceOtherDefaultColorButton ) {
            Color newColor = JColorChooser.showDialog(
                thisFrame, "選擇自己喜歡的顏色",
                getOtherDefaultColor() );

            setOtherDefaultColor( newColor );
            
        }
        else if ( event.getSource() == choiceOtherMouseEnteredColorButton ) {
            Color newColor = JColorChooser.showDialog(
                thisFrame, "選擇自己喜歡的顏色",
                getOtherMouseEnteredColor() );

            setOtherMouseEnteredColor( newColor );
        }
        else if ( event.getSource() == choiceTableDefaultColorButton ) {
            Color newColor = JColorChooser.showDialog(
                thisFrame, "選擇自己喜歡的顏色",
                getTableDefaultColor() );

            setTableDefaultColor( newColor );
        }
        else if ( event.getSource() == choiceTableMouseEnteredColorButton ) {
            Color newColor = JColorChooser.showDialog(
                thisFrame, "選擇自己喜歡的顏色",
                getTableMouseEnteredColor() );

            setTableMouseEnteredColor( newColor );
        }
        else if ( event.getSource() == choiceTableFileExistedColorButton ) {
            Color newColor = JColorChooser.showDialog(
                thisFrame, "選擇自己喜歡的顏色",
                getTableFileExistedColor() );

            setTableFileExistedColor( newColor );
        }
        else if ( event.getSource() == choiceMenuItemDefaultColorButton ) {
            Color newColor = JColorChooser.showDialog(
                thisFrame, "選擇自己喜歡的顏色",
                getMenuItemDefaultColor() );

            setMenuItemDefaultColor( newColor );
        }
    }

    private void setBackgroundPicPath( String path ) {
        if ( whichFrame == FrameEnum.MAIN_FRAME ) {
            SetUp.setBackgroundPicPathOfMainFrame( path );
        }
        else if ( whichFrame == FrameEnum.INFORMATION_FRAME ) {
            SetUp.setBackgroundPicPathOfInformationFrame( path );
        }
        else if ( whichFrame == FrameEnum.OPTION_FRAME ) {
            SetUp.setBackgroundPicPathOfOptionFrame( path );
        }
        else if ( whichFrame == FrameEnum.CHOICE_FRAME ) {
            SetUp.setBackgroundPicPathOfChoiceFrame( path );
        }
        else {
            Common.errorReport( "沒有相對應的視窗: " + whichFrame );
        }
    }

    private Color getOtherDefaultColor() {

        if ( whichFrame == FrameEnum.MAIN_FRAME ) {
            return SetUp.getMainFrameOtherDefaultColor();
        }
        else if ( whichFrame == FrameEnum.INFORMATION_FRAME ) {
            return SetUp.getInformationFrameOtherDefaultColor();
        }
        else if ( whichFrame == FrameEnum.OPTION_FRAME ) {
            return SetUp.getOptionFrameOtherDefaultColor();
        }
        else if ( whichFrame == FrameEnum.CHOICE_FRAME ) {
            return SetUp.getChoiceFrameOtherDefaultColor();
        }
        else {
            Common.errorReport( "沒有相對應的視窗: " + whichFrame );
            return null;
        }
    }

    private void setOtherDefaultColor( Color color ) {
        if ( whichFrame == FrameEnum.MAIN_FRAME ) {
            SetUp.setMainFrameOtherDefaultColor( color );
        }
        else if ( whichFrame == FrameEnum.INFORMATION_FRAME ) {
            SetUp.setInformationFrameOtherDefaultColor( color );
        }
        else if ( whichFrame == FrameEnum.OPTION_FRAME ) {
            SetUp.setOptionFrameOtherDefaultColor( color );
        }
        else if ( whichFrame == FrameEnum.CHOICE_FRAME ) {
            SetUp.setChoiceFrameOtherDefaultColor( color );
        }
        else {
            Common.errorReport( "沒有相對應的視窗: " + whichFrame );
        }
    }

    private Color getOtherMouseEnteredColor() {
        if ( whichFrame == FrameEnum.MAIN_FRAME ) {
            return SetUp.getMainFrameOtherMouseEnteredColor();
        }
        else if ( whichFrame == FrameEnum.INFORMATION_FRAME ) {
            return SetUp.getInformationFrameOtherMouseEnteredColor();
        }
        else if ( whichFrame == FrameEnum.OPTION_FRAME ) {
            return SetUp.getOptionFrameOtherMouseEnteredColor();
        }
        else if ( whichFrame == FrameEnum.CHOICE_FRAME ) {
            return SetUp.getChoiceFrameOtherMouseEnteredColor();
        }
        else {
            Common.errorReport( "沒有相對應的視窗: " + whichFrame );
            return null;
        }
    }

    private void setOtherMouseEnteredColor( Color color ) {
        if ( whichFrame == FrameEnum.MAIN_FRAME ) {
            SetUp.setMainFrameOtherMouseEnteredColor( color );
        }
        else if ( whichFrame == FrameEnum.INFORMATION_FRAME ) {
            SetUp.setInformationFrameOtherMouseEnteredColor( color );
        }
        else if ( whichFrame == FrameEnum.OPTION_FRAME ) {
            SetUp.setOptionFrameOtherMouseEnteredColor( color );
        }
        else if ( whichFrame == FrameEnum.CHOICE_FRAME ) {
            SetUp.setChoiceFrameOtherMouseEnteredColor( color );
        }
        else {
            Common.errorReport( "沒有相對應的視窗: " + whichFrame );
        }
    }

    private Color getTableDefaultColor() {
        if ( whichFrame == FrameEnum.MAIN_FRAME ) {
            return SetUp.getMainFrameTableDefaultColor();
        }
        else if ( whichFrame == FrameEnum.CHOICE_FRAME ) {
            return SetUp.getChoiceFrameTableDefaultColor();
        }
        else {
            Common.errorReport( "沒有相對應的視窗: " + whichFrame );
            return null;
        }
    }

    private void setTableDefaultColor( Color color ) {
        if ( whichFrame == FrameEnum.MAIN_FRAME ) {
            SetUp.setMainFrameTableDefaultColor( color );
        }
        else if ( whichFrame == FrameEnum.CHOICE_FRAME ) {
            SetUp.setChoiceFrameTableDefaultColor( color );
        }
        else {
            Common.errorReport( "沒有相對應的視窗: " + whichFrame );
        }
    }
    
    

    private Color getTableMouseEnteredColor() {
        if ( whichFrame == FrameEnum.MAIN_FRAME ) {
            return SetUp.getMainFrameTableMouseEnteredColor();
        }
        else if ( whichFrame == FrameEnum.CHOICE_FRAME ) {
            return SetUp.getChoiceFrameTableMouseEnteredColor();
        }
        else {
            Common.errorReport( "沒有相對應的視窗: " + whichFrame );
            return null;
        }
    }

    private void setTableMouseEnteredColor( Color color ) {
        if ( whichFrame == FrameEnum.MAIN_FRAME ) {
            SetUp.setMainFrameTableMouseEnteredColor( color );
        }
        else if ( whichFrame == FrameEnum.CHOICE_FRAME ) {
            SetUp.setChoiceFrameTableMouseEnteredColor( color );
        }
        else {
            Common.errorReport( "沒有相對應的視窗: " + whichFrame );
        }
    }

    private Color getTableFileExistedColor() {
        if ( whichFrame == FrameEnum.CHOICE_FRAME ) {
            return SetUp.getChoiceFrameTableFileExistedColor();
        }
        else {
            Common.errorReport( "沒有相對應的視窗: " + whichFrame );
            return null;
        }
    }
    
    private void setMenuItemDefaultColor( Color color ) {
        if ( whichFrame == FrameEnum.MAIN_FRAME ) {
            SetUp.setMainFrameMenuItemDefaultColor( color );
        }
        else {
            Common.errorReport( "沒有相對應的視窗: " + whichFrame );
        }
    }

    private Color getMenuItemDefaultColor() {
        if ( whichFrame == FrameEnum.MAIN_FRAME ) {
            return SetUp.getMainFrameMenuItemDefaultColor();
        }
        else {
            Common.errorReport( "沒有相對應的視窗: " + whichFrame );
            return null;
        }
    }
    
    private void setTableFileExistedColor( Color color ) {
        if ( whichFrame == FrameEnum.CHOICE_FRAME ) {
            SetUp.setChoiceFrameTableFileExistedColor( color );
        }
        else {
            Common.errorReport( "沒有相對應的視窗: " + whichFrame );
        }
    }

    private String getBackgroundPicPath() {
        if ( whichFrame == FrameEnum.MAIN_FRAME ) {
            return SetUp.getBackgroundPicPathOfMainFrame();
        }
        else if ( whichFrame == FrameEnum.INFORMATION_FRAME ) {
            return SetUp.getBackgroundPicPathOfInformationFrame();
        }
        else if ( whichFrame == FrameEnum.OPTION_FRAME ) {
            return SetUp.getBackgroundPicPathOfOptionFrame();
        }
        else if ( whichFrame == FrameEnum.CHOICE_FRAME ) {
            return SetUp.getBackgroundPicPathOfChoiceFrame();
        }
        else {
            return "沒有相對應的視窗！";
        }
    }

    private void allRepaint() {
        this.repaint();
        if ( ComicDownGUI.mainFrame != null ) {
            ComicDownGUI.mainFrame.repaint();
        }
        if ( InformationFrame.thisFrame != null ) {
            InformationFrame.thisFrame.repaint();
        }
        if ( OptionFrame.thisFrame != null ) {
            OptionFrame.thisFrame.repaint();
        }
        if ( ChoiceFrame.thisFrame != null ) {
            ChoiceFrame.thisFrame.repaint();
        }
    }

    public static void main( String[] args ) {
        new BackgroundSettingFrame( FrameEnum.MAIN_FRAME );
    }

    @Override
    public void mouseClicked( MouseEvent e ) {
    }

    @Override
    public void mousePressed( MouseEvent e ) {
    }

    @Override
    public void mouseReleased( MouseEvent e ) {
    }

    @Override
    public void mouseExited( MouseEvent event ) {
        if ( SetUp.getUsingBackgroundPicOfInformationFrame() ) {
            ( ( JComponent ) event.getSource() ).setForeground( backupColor );
        }
    }

    @Override
    public void mouseEntered( MouseEvent event ) {
        backupColor = ( ( JComponent ) event.getSource() ).getForeground();
        if ( SetUp.getUsingBackgroundPicOfInformationFrame() ) {
            ( ( JComponent ) event.getSource() ).setForeground( Color.GREEN );
        }
    }
}