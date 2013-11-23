/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jcomicdownloader.tools;

/**
 *
----------------------------------------------------------------------------------------------------
Program Name : JComicDownloader
Authors  : surveyorK
Last Modified : 2012/1/3
----------------------------------------------------------------------------------------------------
ChangeLog:
2.15: 可讓NimROD變換風格
----------------------------------------------------------------------------------------------------
 */
public class NimRODTheme {

    public static void setTheme( String themeName ) {
        if ( themeName.equals( "Night" ) ) {
            setNightTheme();
        } else if ( themeName.equals( "DarkGrey" ) ) {
            SetDarkGreyTheme();
        } else if ( themeName.equals( "DarkTabaco" ) ) {
            SetDarkTabacoTheme();
        } else if ( themeName.equals( "LightTabaco" ) ) {
            SetLightTabacoTheme();
        } else if ( themeName.equals( "Burdeos" ) ) {
            SetBurdeosTheme();
        } else if ( themeName.equals( "Snow" ) ) {
            SetSnowTheme();
        } else {
            Common.errorReport( "不可能有別的名稱" );
        }
    }

    public static void setNightTheme() {
        System.setProperty( "nimrodlf.p1", "#5454C0" );
        System.setProperty( "nimrodlf.p2", "#5E5ECA" );
        System.setProperty( "nimrodlf.p3", "#6868D4" );
        System.setProperty( "nimrodlf.s1", "#191E27" );
        System.setProperty( "nimrodlf.s2", "#232831" );
        System.setProperty( "nimrodlf.s3", "#2D323B" );
        System.setProperty( "nimrodlf.w", "#494854" );
        System.setProperty( "nimrodlf.b", "#FFFFFF" );
        System.setProperty( "nimrodlf.menuOpacity", "195" );
        System.setProperty( "nimrodlf.frameOpacity", "180" );
    }

    public static void SetDarkGreyTheme() {
        System.setProperty( "nimrodlf.p1", "#1F2E3A" );
        System.setProperty( "nimrodlf.p2", "#293844" );
        System.setProperty( "nimrodlf.p3", "#33424E" );
        System.setProperty( "nimrodlf.s1", "#3B3B3B" );
        System.setProperty( "nimrodlf.s2", "#454545" );
        System.setProperty( "nimrodlf.s3", "#4F4F4F" );
        System.setProperty( "nimrodlf.w", "#717171" );
        System.setProperty( "nimrodlf.b", "#FFFFFF" );
        System.setProperty( "nimrodlf.menuOpacity", "0" );
        System.setProperty( "nimrodlf.frameOpacity", "180" );
    }

    public static void SetDarkTabacoTheme() {
        System.setProperty( "nimrodlf.p1", "#A45A00" );
        System.setProperty( "nimrodlf.p2", "#AE6400" );
        System.setProperty( "nimrodlf.p3", "#B86E00" );
        System.setProperty( "nimrodlf.s1", "#5A4E26" );
        System.setProperty( "nimrodlf.s2", "#645830" );
        System.setProperty( "nimrodlf.s3", "#6E623A" );
        System.setProperty( "nimrodlf.w", "#4F4C2C" );
        System.setProperty( "nimrodlf.b", "#E9E1D0" );
        System.setProperty( "nimrodlf.menuOpacity", "120" );
        System.setProperty( "nimrodlf.frameOpacity", "180" );
    }

    public static void SetLightTabacoTheme() {
        System.setProperty( "nimrodlf.p1", "#EBB800" );
        System.setProperty( "nimrodlf.p2", "#F5C200" );
        System.setProperty( "nimrodlf.p3", "#FFCC00" );
        System.setProperty( "nimrodlf.s1", "#9B9864" );
        System.setProperty( "nimrodlf.s2", "#A5A26E" );
        System.setProperty( "nimrodlf.s3", "#AFAC78" );
        System.setProperty( "nimrodlf.w", "#D0CB96" );
        System.setProperty( "nimrodlf.b", "#000000" );
        System.setProperty( "nimrodlf.menuOpacity", "195" );
        System.setProperty( "nimrodlf.frameOpacity", "180" );
    }

    public static void SetBurdeosTheme() {
        System.setProperty( "nimrodlf.p1", "#C23B00" );
        System.setProperty( "nimrodlf.p2", "#CC4500" );
        System.setProperty( "nimrodlf.p3", "#D64F00" );
        System.setProperty( "nimrodlf.s1", "#520000" );
        System.setProperty( "nimrodlf.s2", "#5C0000" );
        System.setProperty( "nimrodlf.s3", "#660000" );
        System.setProperty( "nimrodlf.w", "#750000" );
        System.setProperty( "nimrodlf.b", "#FFE0E0" );
        System.setProperty( "nimrodlf.menuOpacity", "195" );
        System.setProperty( "nimrodlf.frameOpacity", "180" );
    }

    public static void SetSnowTheme() {
        System.setProperty( "nimrodlf.p1", "#0085EB" );
        System.setProperty( "nimrodlf.p2", "#008FF5" );
        System.setProperty( "nimrodlf.p3", "#0099FF" );
        System.setProperty( "nimrodlf.s1", "#DCDCDC" );
        System.setProperty( "nimrodlf.s2", "#E6E6E6" );
        System.setProperty( "nimrodlf.s3", "#F0F0F0" );
        System.setProperty( "nimrodlf.w", "#FAFAFA" );
        System.setProperty( "nimrodlf.b", "#000000" );
        System.setProperty( "nimrodlf.menuOpacity", "195" );
        System.setProperty( "nimrodlf.frameOpacity", "180" );
    }
}
