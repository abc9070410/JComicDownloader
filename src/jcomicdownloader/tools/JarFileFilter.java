/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jcomicdownloader.tools;

import java.io.File;

/* ImageFilter.java is used by FileChooserDemo2.java. */
public class JarFileFilter extends javax.swing.filechooser.FileFilter
{

    public boolean accept( File file )
    {
        String fileString = file.getPath();
        if ( file.isDirectory() )
        {
            return true;
        }
        else if ( fileString.matches( "(?s).*\\.(?s).*" ) )
        {
            String extension = fileString.split( "\\." )[1];

            if ( extension.equalsIgnoreCase( "jar" ) )
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        else
        {
            return false;
        }
    }

    //The description of this filter
    public String getDescription()
    {
        return "僅含支援的檔案（jar）";
    }
}
