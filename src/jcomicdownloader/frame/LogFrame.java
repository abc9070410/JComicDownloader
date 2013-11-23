/*
 ----------------------------------------------------------------------------------------------------
 Program Name : JComicDownloader
 Authors  : surveyorK
 Last Modified : 2011/10/25
 ----------------------------------------------------------------------------------------------------
 ChangeLog:
5.02: 修復Java 7下無法使用NapKin Look and Feel的問題。
----------------------------------------------------------------------------------------------------
 */
package jcomicdownloader.frame;

import jcomicdownloader.tools.*;
import jcomicdownloader.enums.*;
import jcomicdownloader.*;

import java.awt.*;
import javax.swing.*;
import java.io.*;

public class LogFrame extends JFrame {
    private JPanel messagePanel;
    private JTextArea messageArea;
    private String resourceFolder;
    private StringBuffer messageString;

    public static JFrame thisFrame; // for change look and feel

/**
 *
 * 顯示除錯和詳細操作訊息的視窗
 */
    public LogFrame() {
        super( "程式記錄" );

        thisFrame = this; // for change look and feel
        resourceFolder = "resource/";

        messageString = new StringBuffer( "" );

        setUpUIComponent();
        setUpeListener();
        //setVisible( true );
    }

    private void setUpUIComponent() {
        // 因應JRE 7，重新設定Look and Feel
        CommonGUI.setLookAndFeelByClassName( SetUp.getSkinClassName() );
        
        setSize( 540, 690 );
        setDefaultLookAndFeelDecorated( false ); // 讓標題欄可以隨look and feel改變
        setIconImage( new CommonGUI().getImage( CommonGUI.mainIcon ) );

        Container contentPane = getContentPane();

        //setButton( contentPane );

        setText( contentPane );
    }

    private void setText( Container contentPane ) {
        //messagePanel = new JPanel( new BorderLayout() );

        String informationText = "<html>" + "dd" +

        "</html>" ;

        messageArea = new JTextArea();

        messageArea.setFont( new Font( "新細明體", Font.PLAIN, 19 ) );
        messageArea.setLineWrap( true );
        JScrollPane sp = new JScrollPane( messageArea,
                                          ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                                          ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER );

        messagePanel = new CommonGUI().getCenterPanel( sp );

        contentPane.add( messagePanel, BorderLayout.CENTER );
    }

    private void setUpeListener() {
        setDefaultCloseOperation( DISPOSE_ON_CLOSE );
    }


    // --------- print the "system.out.print()" into messageArea --------------

    private void updateTextArea( final String text) {

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {

                messageString.append( text );
                messageArea.setText( messageString.toString() );
            }
        });

        //messageArea.append( text);
    }

    public void redirectSystemStreams() {
        OutputStream out = new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                updateTextArea(String.valueOf((char) b));
            }

            @Override
            public void write(byte[] b, int off, int len) throws IOException {
                updateTextArea(new String(b, off, len));
            }

            @Override
            public void write(byte[] b) throws IOException {
                write(b, 0, b.length);
            }
        };

        if ( !Debug.commandDebugMode ) {
            System.setOut(new PrintStream(out, true)); // input out.print
            System.setErr(new PrintStream(out, true)); // input out.err
        }
    }
}
