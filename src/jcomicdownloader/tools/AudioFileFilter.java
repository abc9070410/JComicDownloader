/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jcomicdownloader.tools;

import java.io.File;
import javax.swing.*;
import javax.swing.filechooser.*;

/* ImageFilter.java is used by FileChooserDemo2.java. */
public class AudioFileFilter extends javax.swing.filechooser.FileFilter {

    public boolean accept( File file ) {
        String fileString = file.getPath();
        if ( file.isDirectory() ) {
            return true;
        } else if ( fileString.matches( "(?s).*\\.(?s).*" ) ) {
            String extension = fileString.split( "\\." )[1];

            if ( extension.equalsIgnoreCase( "wav" )
                    || extension.equalsIgnoreCase( "au" )
                    || extension.equalsIgnoreCase( "aif" )
                    || extension.equalsIgnoreCase( "rmf" )
                    || extension.equalsIgnoreCase( "mid" ) ) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    //The description of this filter
    public String getDescription() {
        return "僅含支援的音效檔案（wav, au, mid, aif, rmf）";
    }
}
