/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jcomicdownloader;

/**
 *
 * 檢查是否正在下載或是否允許下載等狀況的旗標
 */
public class Flag {
    public static boolean parseFlag = true;
    public static boolean allowDownloadFlag= false;
    public static boolean downloadingFlag= false;
    public static boolean parseUrlFlag = false; // 分析結束
    public static boolean downloadErrorFlag = false; // 是否發生下載錯誤
    public static boolean timeoutFlag = false; // 連線時間是否過長而導致timeout
}
