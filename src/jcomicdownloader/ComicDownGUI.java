/*
 * JComicDownloader
 ----------------------------------------------------------------------------------------------------
 Program Name : JComicDownloader
 Authors  : surveyorK
 Version  : v5.19
 Last Modified : 2013/11/23
 ----------------------------------------------------------------------------------------------------
 * 
 * ChangeLog:
 5.19:
 1. 修復baidu無法下載原始圖片的問題。
 2. 修復SFacg解析失敗的問題。
 3. 修復nanadm解析失敗的問題。
 4. 修復6manga解析失敗的問題。
 5. 修復ck101解析失敗的問題。

 5.18:
 1. 修復ck101解析失敗的問題。
 2. 修復sfacg最新集數無法解析的問題。
 3. 取消支援部份網站。
 5.17:
 1. 修復178圖片伺服器位址錯誤的問題。
 2. 修復2ecy解析錯誤的問題。
 3. 修復xxbh解析錯誤的問題。
 4. 修復8comic改變位址的問題。
 5. 加入按鈕可重新選擇或加入反白任務。
 6. 資訊按鈕可開啟反白任務的下載物。
 5.16:
 1. 修復comic131無法下載的問題。
 2. 修復ck101無法下載的問題。
3. 修復99mh無法下載的問題。
4. 修復jumpcn.com.cn解析錯誤的問題。
5. 修復blog.yam.com圖片無法下載的問題。
6. 修復8comic解析失敗的問題。
7. 修復kangdm.com改變網址的問題。
8. 修復citymanga最後一頁無解析的問題。
9. 取消MAC系統下的剪貼簿捕捉功能。
10. 修復ck101改變位址的問題。
11. 修復xxbh位址解析錯誤的問題。
12. 修復dm5沒有標題名稱時解析錯誤的問題。
* 
 5.15:
 1. 修復178下載錯誤的問題。
 2. 修復7tianshi..com無法解析的問題。
 3. 修復位址文件覆蓋下載文件的問題。
 4. 修復2ecy解析頁數錯誤的問題。
 5. 修復iask標題錯誤的問題。
 6. 修復178位址改變的問題。
 5.14:
 1. 修復comic131解析位址錯誤的問題。
 2. 修復99mh無法下載的問題。
 3. 修復kuku解析集數錯誤的問題。
 4. 修復xxbh伺服器位址失效的問題。
 5. 修復comic131名稱解析錯誤的問題。
 5.13:
 1. 修復178因改版而無法下載的問題。
 2. 修復99manga下載錯誤的問題。
 3. 修復wenku8部份章節無法下載的問題。
 4. 修復hhcomic無法下載的問題。
 5. 修復6manga無法下載的問題。
 6. 調整178的圖片伺服器位址。
 7. 修復bengou因網頁編碼改變而無法下載的問題。
 8. 修復comic131無效章節的問題。
 9. 修復ck101解析錯誤的問題。
10. 修復comic131漫畫名稱解析錯誤的問題。
11. 修復xxbh圖片伺服器位址解析錯誤的問題。

 5.12:
 1. 新增對xiami的支援。
 2. 新增簡易檔案下載模式。
 3. 修正連線錯誤時stateBar的文字顯示。
 4. 修改選擇集數機制，當只有唯一集數時自動選取。
 5. 增加暫停時倒數秒數的文字顯示。
 6. 修復資訊視窗在沒有網路時無窮等待的問題。
 7. 修復設置背景圖片後無法正常運行的問題。
 8. 修復部分介面嚴重的崩潰問題。
 9. 修復ck101動態加載部分未收錄的問題。
10. 修復napkin介面使用時無法開啟選擇背景視窗的問題。
11. 修復xxbh解析錯誤的問題。
12. 修復Mac OS X下無法開啟資料夾的問題。
 5.11:
 1. 新增對sogou的支援
 2. 新增對1ting的支援。
 3. 修復book.ifeng.com下載錯誤的問題。
 4. 修復任務置底錯誤的問題。
 5.10:
 1. 修復xxbh解析錯誤的問題。
 2. 換新的回報專區網址。
 3. 修復eh解析頁數錯誤的問題。
 4. 修改eh解析機制，使其無須全部頁面解析完畢才開始下載。
 5.09:
 1. 新增對woyouxian.com的支援。
 2. 新增對shunong.com的支援。
 3. 修復文字檔的第一小節標題位置錯誤的問題。 
 4. 修復jmymh解析位址錯誤的問題。
 5. 修復dmeden.net解析位址錯誤的問題。
 5.08:
 1. 新增對tianyabook的支援。
 2. 修復冗餘設定檔檢查的問題。
 3. 修復dmeden.net解析位址錯誤的問題。
 4. 修復下載發生錯誤時無法正確通知的問題。
 5.07: 
 1. 新增對7wenku的支援。 
 2. 修復iask解析錯誤的問題。
 3. 修復veryim集數解析不全的問題。
 4. 修復在windows系統下執行腳本若有錯誤輸出時會阻塞的問題。
 
 5.06:
 1. 修復8comic因網站改版而解析錯誤的問題。
 2. 修復改變紀錄順序後加入書籤錯亂的問題。
 5.05: 
 1. 增加英文介面。
 2. 修復8novel少數下載不完全的問題。
 3. 修復dm5部分解析網址錯誤的問題。
 4. 加入辨識rar壓縮檔為已下載集數。
 5.04: 
 1. 修正機制，讓書籤和紀錄也可以改變位置順序。
 2. 修改事件：滑鼠雙擊任務編號 -> 開啟網頁。
 3. 修復17kk下載錯誤的問題。
 4. 修復部分圖片附檔名錯誤的問題。
 5. 修復wenku8無法下載的問題。
 6. 提供執行甫更新的新版本的選項。
 5.03: 1. 修復cok101下載頁數不全的問題。
 2. 修復baidu下載錯誤的問題。
 3. 修復178部分無法下載的問題。
 4. 修復dm5下載錯誤的問題。
 5.02:  1. 修復Java 7下無法使用NapKin Look and Feel的問題。
 2. 修復hhcomic因網址改變而下載錯誤的問題。
 3. 修復comic131因改版而解析錯誤的問題。
 4. 修復ck101小說部分合併卡住的問題。
 5. 修復wenku8部分章節下載錯誤的問題。
 6. 修復ck101因網站改版而解析錯誤的問題。
 7. 修復8comic因網站改版而解析錯誤的問題。
 8. 修復dm5無法下載的問題。
 9. 修復bengou因網頁改版而解析失敗的問題。 
 5.01:  1. 增加隨設定改變表格字體的功能。
 2. 修復veryim無法下載的問題。
 3. 修復dm5限制漫畫無法下載的問題。 
 4. 修復xxbh因網站改版而無法解析的問題。
 5. 修復dm.99manga.com因改版而解析錯誤的問題。
 6. 修復baidu因改版而解析錯誤的問題。
 5.0:   1. 支援部落格文章圖片下載。
 2. 修復bengou因網頁改版而解析失敗的問題。
 3. 修復分析後"不"下載圖檔的選項功能。
 4.19: 1. 修復dm5無法下載的問題。
 2. 增加提升任務和沉降任務的按鈕選項。
 3. 改進部分站點張數缺失時下載不確實的問題。
 4. 修復178因網址改變而無法下載的問題。
 4.18: 1. 恢復對xindm的支援。
 *     2. 加入按鈕快捷鍵。
 *     3. 修復jumpcn.com.cn因網頁改版而下載錯誤的問題。
 4.17: 1. 修復comic.131.com因改版而解析錯誤的問題。
 2. 修復cococomic解析錯誤的問題。
 4.16: 1. 修復Google圖片無法下載的問題。
 *     2. 修復hhcomic因變更伺服器而下載錯誤的問題。
 *     3. 修復baidu因網站改版而下載不完全的問題。
 *     4. 修復ck101小說無法批次下載的問題。
 4.15: 1. 為避免混淆，取消暫存資料夾和紀錄檔目錄的設定選項。
 *       2. 修復cococomic部份位址接西錯誤的問題。
 *       3. 修復hhcomic因網站改版而無法下載的問題。
 4.14: 1. 新增對book.ifeng.com的支援。
 2. 新增對xunlook.com的支援。
 3. 修復wenku8繁體頁面下載錯誤的問題。
 4. 修復xxbh因網站改版而無法解析的問題。
 *    5. 修復manmankancom因網站改版而無法解析的問題。
       
 4.13: 1. 修復jmymh解析集數錯誤的問題。
 2. 修正wenku8的封面與插圖下載。
 3. 修正部份訊息視窗無法顯現的問題。
 4. 下載新的JAR介面庫後可自動重新開啟。
 4.12: 1. 新增對uus8.com的支援。
 2. 修正刪除檔案時的路徑檢查。（若因故找不到任務名稱，則無法刪除）
 3. 增加powershell腳本的選擇。
 4. 修復mh.99770.cc和www.cococomic.com部份位址解析錯誤的問題。
 5. 修復fumanhua因伺服器位址更換而無法下載的問題。
 4.11:  1. 新增對bookapp.book.qq.com的支援。
 *        2. 新增任務完成後搜尋並下載小說封面圖的選項。
 *        2. 修復pixnet.net無法取得全部標籤的問題。
 *        3. 修復blog.yam.com無法取得全部標籤的問題。
 *        4. 修復blog.yam.com沒有標示日期的問題。
 5. 修復在Substance介面下若跳出選擇檔案下載方式視窗必崩潰的問題。
 6. 修復在Substance介面下若跳出輸入視窗必崩潰的問題。
 4.10: 1. 修復對17kk的支援。
 2. 修復對99770的支援。
 3. 約略精簡各解析模組。
 4. 修正部份字元轉換錯誤。
 5. 修正部份小說網站的儲存位置以搭配腳本使用。
 6. 修正ck101動態頁面只下載第一頁的問題。
 4.09: 1. 新增對8novel的支援。
 *      2. 新增對book.qq.com的支援。
 *      3. 新增對book.sina.com的支援。
 *      4. 新增對book.51cto.com的支援。
 *       5. 在資訊視窗中加入小說搜尋按鈕。
 *       6. 修復對www.iibq.com的支援。
 *       7. 若檢查有紀錄ID和HASH值，下載EH時自動掛載cookie，以增加下載額度。
 4.08: 1. 新增任務結束後執行腳本的功能選項。
 2. 修復xxbh部份漫畫無法下載的問題。

 4.07: 1. 新增對zuiwanju的支援。
 *       2. 新增對manhua.2ecy.com的支援。
 *       3. 修復www.99comic.com無法解析的問題。
 *       4. 修復fumanhua因改版而無法解析的問題。
 *       5. 修復99comic標題不完整的問題。
 *       6. 調整設定，使其不會重複寫入設定檔。
 4.06: 1. 新增對17kkmh.的支援。
 *      2. 修復eyny部份頁面解析失敗的問題。
 *      3. 修復eyny分級頁面無法下載的問題。
 *      4. 修復wenku非php頁面無法解析的問題。
 5. 修復編碼問題，將GB2312->UTF8改為GBK->UTF8。
 6. 修復ck101動態頁面解析錯誤的問題。
 7. 修正部份資料夾開啟的位置。
 8. 增添資訊視窗上的最新版本下載訊息。
 4.05: 1. 新增對eyny的支援。
 *      2. 新增對blog.xuite.net的支援。
 *      3. 新增對blog.yam.com的支援。
 *      4. 改進例外處理機制，發生錯誤時自動輸出錯誤相關訊息。
 4.04: 1. 新增對pixnet.net的支援。
 *       2. 增加輸出html格式的選項。（作用於文章下載模式）
 4.03: 1. 新增對xxbh的支援。 
 *       2. 新增對blogspot.com的支援（只支援基本範本）。
 *       3. 修復imanhua部份下載錯誤的問題。
 *       4. 更新對於html的tag和NCR（Numeric character reference）的替換機制，使輸出文字檔更趨自然。
 *       5. 修改Wenku模組，使其可輸出單回文字檔與合併文字檔。
 4.02: 1. 新增對comic.131.com的支援。
 2. 修復部份網站按停止無法立刻停止的bug。
 3. 修復dm5新集數無法下載的問題。
 4. 修復dm5集數名稱格式不一的問題。
 5. 修復fumanhua解析集數錯誤的問題。
 4.01: 1. 新增對fumanhua的支援。
 2. 新增對6manga的支援。
 3. 新增對Wenku的支援。
 4. 增加開啟文件檔的功能選項。
 5. 修復jmymh無法下載的問題。（加入伺服器檢查機制）
 6. 修復cococomic簡體版頁面的集數解析問題。
 7. 修復89890部份集數無法下載的問題。
 4.0 : 1. 新增對imanhua的支援。
 2. 新增對veryim的支援。
 3. 翻新178的解析方式，直接轉碼而非參照字典檔。
 3.19: 1. 新增對ck101的支援。
 2. 新增對mybest的支援。
 3. 修復99manga繁體版因改版而解析錯誤的問題。
 3.18: 1. 新增對mangawindow.com的支援。
 2. 修復在不支援trayIcon系統下無法執行的bug。
 3. 修復hhcomic因網站改版而下載錯誤的問題。
 3.17: 1. 修復99770和cococomic繁體版頁面的集數解析問題。
 2. 修復99manga簡體版和繁體版頁面的集數解析問題。
 3. 修復1mh的集數解析問題。
 4. 修復dmeden標題名稱解析錯誤的問題。
 3.16: 1. 新增對www.99comic.com繁體版的支援。
 2. 修復178部分漫畫解析錯誤的問題。
 3.15: 1. 新增對jmymh.com的支援。
 3.14: 1. 修復178部分漫畫解析錯誤的問題。
 2. 修復部分標題名稱出現unicode代碼的問題。
 3. 完善簡體中文介面。
 3.13: 1. 增加簡體中文的選項。
 2. 修復comic.ck101.com無法下載的問題。
 3.12: 1. 修復cococomic因網站改版而無法下載的問題。
 2. 修復99770因網站改版而無法下載的問題。
 3. 修復jumpcn因網站改版而解析標題錯誤的問題。
 3.11: 1. 修復對www.iibq.com的支援。
 2. 修復Linux系統下有時無法開啟檔案的bug。
 3. 增加刪除實際檔案和反白任務的右鍵選單。
 4. 修復無法辨別cbz檔被當作不存在的bug。
 3.10: 1. 修正讀入集數資訊失敗後導致程式無法關閉的bug。
 2. 增加反白任務置頂和置底的右鍵選單。
 3.09: 1. 新增對iask的支援。
 2. 修復對dmeden.net的支援。
 3. 修正加入任務不會馬上儲存的bug。
 3.08: 1. 修復對89890的支援。
 2. 修復對MangaFox的支援。 
 3. 修復對8comic的支援。 
 4. 取消對xindm的支援。
 5. 修正EH的標題命名選擇。
 3.07: 1. 新增對google一般搜尋的支援。
 2. 增加cbz壓縮格式的選擇。
 3.06: 1. 新增對hhcomic.com的支援。
 2. 系統框訊息的全部任務重新定義為已勾選任務總數。
 3. 每次新增任務便立即寫入任務紀錄，避免不正常關閉後紀錄消失。
 3.05: 1. 新增對www.tuku.cc的支援。
 3.04: 1. 修復dm5部份漫畫無法下載的bug。
 2. 修復dm5漫畫集數分析不全的bug。
 3. 修復dmeden標題名稱解析不全的bug。
 3.03: 1. 修復nanadm無法下載的問題。
 2. 修復部份右鍵選單功能會執行兩次的bug。
 3. 修復無法下載最新版本的bug。
 3.02: 1. 新增對dm5的支援。
 2. 修復xindm部份漫畫無法下載的bug。
 3.01: 1. 修復選擇集數視窗中選擇項目失效的bug。
 3.0: 1. 增加設定背景圖片的選項。
 2. 改進Napkin介面的部份顯示。
 3. 修復少數網址被誤判為違法網址的bug。
 4. 修復非中文版的google圖片搜尋無法下載的bug。
 5. 修復無法一執行就掛上proxy的bug。
 3. 修復少數網址被誤判為違法網址的bug。
 4. 修復非中文版的google圖片搜尋無法下載的bug。
 * 2.19: 1. 新增對comic.ck101.com的支援。
 * 　　 2. 修復dmeden少數漫畫無法下載的bug。
 * 2.18: 1. 增加Napkin介面風格。
 2. 增加Substance介面風格（共27種）。
 * 2.17: 1. 新增對dm.game.mop.com的支援。
 2. 增加選擇反白集數的選項。
 3. 加入右鍵選單的圖示。
 4. 修復linux系統下無法開啟檔案總管(nautilus)的bug。
 5. 修復178檔名含有中文就會解析錯誤的bug。
 6. 修復部份漫畫無法正確下載缺少頁數的bug。
 * 
 * 2.16: 1. 改由NetBeans生成JAR檔。
 2. 增加標題重新命名的右鍵選單。
 * 　　 3. 修改暗色系界面的已下載和未下載的顏色標示。 
 4. 修改任務列刪除機制，使其下載中仍能刪除任務。
 5. 修復178少數檔名解析錯誤的bug。
 6. 修復在非下載時，第一列任務仍無法置頂或置底的bug。
 * 2.15: 1. 增加NimROD介面風格（共六種）。
 2. 修復mangaFox已刪除漫畫加入後會當掉的問題。
 * 
 * 2.14: 1. 修改任務列置換機制，使其在下載中仍能置頂或置底。
 2. 修改下載清單版面，將"下載順序"改為"編號"，這樣順序置換後會比較清楚。
 3. 修改紀錄儲存機制，即使取消"是否下載"的勾選，仍會儲存紀錄。
 4. 修復8comic的作品名稱解析不完全的bug。
 5. 修復kuku因為mh.socomic.com無法連接而解析失敗的問題。
 * 2.13: 1. 新增對mh.emland.net的支援。
 * 　　 2. 修改最新版本下載按鈕，使其按下去可以直接下載最新版本。
 3. 修復178少數漫畫無法下載的bug。
 4. 修復8comic少數漫畫名稱解析錯誤的bug。
 * 2.12: 1. 新增對www.bengou.com的支援。
 * 2.11: 1. 新增對www.kangdm.com的支援。
 2. 增加搜尋此本漫畫的右鍵選單。
 3. 增加取消勾選『分析後下載圖檔』時的提醒視窗。
 4. 修復manhua.178.com擷取網頁時出錯的問題。（應該都可以正常下載了）
 5. 修復重試後無法下載中間漏頁的問題。（ex. 5.jpg 7.jpg 8.jpg，中間遺漏6.jpg）
 * 2.10: 1. 新增對manhua.178.com的支援。（仍有些問題，測試中）
 2. 增加任務完成音效的選項。
 3. 修改黑底介面的訊息文字顯示顏色（藍色 -> 黃色）。
 4. 修復kuku解析少數圖片網址時後面多出">"的問題。
 5. 修復沒有設定瀏覽圖片程式便無法開啟網頁的問題。
 * 2.09: 1. 新增對www.kkkmh.com/的支援。
 2. 新增對6comic.com的支援。
 3. 增加開啟原始網頁的右鍵選單。
 4. 修復顯示加入單集的訊息後仍抓取網址的問題。
 5. 修復Linux系統下無法在同目錄讀取set.ini的問題。
 6. 修復Linux系統下無法正常使用JTattoo介面的問題。
 7. 拿掉對comic.92wy.com的支援。（關站了......）
 * 2.08: 1. 增加額外的JTattoo介面選項（共增加11組介面可供選擇）。
 2. 修復xindm解析錯誤的bug。
 3. 修復部份89890解析錯誤的bug。
 * 2.07: 1. 新增對comic.sfacg.com的支援。
 2. 增加baidu頁面下載時的選項。（至貼圖結束為止或解析全部頁面）
 3. 修改紀錄檔機制，下載清單和書籤清單會寫出檔案多次，避免因不正常關閉造成記錄遺失。
 * 2.06: 1. 新增對baidu的支援。
 2. 修復集數名稱數字格式化的bug。
 * 2.05: 1. 修改選項視窗，明確顯示失敗重傳次數和連線逾時時間（因為linux系統下無法看到刻度）。
 2. 修復Linux系統下無法開啟檔案的bug。
 3. 修復無法開啟壓縮檔的bug。（預設開啟圖片和壓縮檔為同個程式）
 4. 修復暫存資料夾路徑無法改變的bug。
 5. 修復部分集數名稱解析失敗的bug。（數字規格化改由前面開始找）
 * 2.04: 1. 增加選擇紀錄檔和暫存資料夾的選項。
 2. 修改下拉式介面選單的渲染機制，使其可改變字型。 
 3. 修改集數名稱命名機制，將裡面的數字格式化（ex. 第3回 -> 第003回），以方便排序。
 4. 增加選擇字型時可以預覽字型的功能。
 5. 修復部份CC漫畫解析錯誤的bug。
 * 
 * 2.03: 1. 新增對www.iibq.com的支援。
 2. 增加可用外部程式開啟漫畫的選項。
 3. 增加逾時倒數時間設定的選項。
 4. 增加下載失敗後重新嘗試次數(retryTimes)的選項
 5. 修復nanadm有些第一集無法解析的bug。
 6. 因應dmeden轉換位址進行解析修正（dmeden.net <-> www.dmeden.com）
 7. 因應改版後的mangaFox進行解析修正
 8. 修改選項視窗為多面板介面。
 * 2.02: 1. 新增對www.citymanga.com的支援。
 2. 修復kuku網址轉碼部份發生錯誤的bug。
 3. 修復92wy部分集數無法讀取的bug。
 4. 修復mangaFox部份集數命名重疊的bug。
 * 
 * 2.01: 1. 在訊息視窗中加入支援列表的資訊。
 2. 在選項視窗中加入可以預設全選集數的選項。
 3. 修改下載機制，不下載青蛙圖（檔案大小10771 bytes）。
 4. 修復NANA無法解析粗體字集數名稱的bug。
 5. 修正Google圖片搜尋中部份非英文關鍵字沒有正確解析為資料夾名稱的bug。
 * 2.0 : 1. 新增對www.nanadm.com的支援。
 2. 修復Google圖片搜尋批次每張圖下載十秒後就逾時的bug。
 3. 修復書籤和紀錄表格改變外觀順序後無法對應的bug。
 4. 修復下載任務置頂或置底卻沒有改變下載順序的bug。
 5. 修復下載表格改變外觀順序後發生錯誤的bug。（作法就是禁止改變下載表格的外觀順序......）

 * 1.19: 1. 修正後已支援『顯示更多結果』後面的圖。
 2. 修改下載機制，遇到非正常連線直接放棄，加快速度。
 * 1.18: 1. 新增新增對google圖片搜尋的支援(僅支援前237張)。
 * 1.17: 1. 按下載按鈕後會回到下載任務頁面。
 2. 修復集數名稱後面數字會消失的bug。
 =======
 * 1.17: 1. 按下載按鈕後會回到下載任務頁面。
 2. 修復集數名稱後面數字會消失的bug。
 >>>>>>> .r66
 * 1.16: 1. 新增新增對comic.92wy.com的支援。
 2. 新增對EX的支援。
 3. 增加將下載任務置頂與置底的右鍵選單。
 4. 增加紀錄檔存放資料夾選項（set.ini裡面的recordFileDirectory項目）
 5. 修改選項視窗，使之勾選自動刪除就要連帶勾選自動壓縮。
 6. 修復部分網站無法立即跳至最後一張圖片開始下載的bug。
 7. 修復記錄檔讀取失敗會無法開啟的bug。
 * 1.15: 1. 新增對xindm.cn的支援。
 2. 增加網址列的右鍵選單（貼上網址）。
 3. 修復"坂"無對應繁體字的bug。
 * 1.14: 1. 新增對manmankan.com的支援。
 2. 增加可選擇字型和字體大小的選項。
 3. 修改集數選擇視窗（choiceFrame）的關閉功能，允許按右上角的『X』來關閉。
 4. 修復若沒有下載成功仍會產生空壓縮檔的bug。
 5. 修復official.html無法刪除的bug。 
 * 1.13: 1. 新增對mangafox.com的支援。
 2. 修復jumpcn.com.cn因置換伺服器而解析錯誤的問題。
 * 1.12: 1. 增加對jumpcn.com的支援。
 2. 增加兩個下載任務區的右鍵選單：『刪除所有未勾選任務』和『刪除所有已完成任務』。
 3. 修復刪除單一下載任務會造成網址錯誤的bug。
 4. 修復tempDirectory最後出現兩個斜線的bug。
 5. 修復8comic的圖庫選擇集數時無法感知是否已經存在於資料夾的bug。
 6. 修復kuku的美食的俘虜解析網址錯誤的bug。
 7. 讓部分網站邊解析邊下載，降低實際下載前的解析時間。
 * 1.11: 1. 新增對dmeden.net的支援。
 2. 將volumeTitle傳入RunModule，可避免重新解析單集名稱不一致的問題。
 3. 選取集數視窗中，已經存在於資料夾的集數會呈淺灰色。
 4. 修復記錄表格無法點選刪除的bug。
 * 1.10: 1. 加入www.jumpcn.com.cn的支援
 2. 修復一些1.09版的bug
 * 1.09: 1. 加入書籤和記錄兩個新頁面。
 2. 修復右鍵表單出現亂碼的bug。
 3. 拿掉多餘標題字尾。
 * 1.08: 1. 新增對8comic的支援，包含免費漫畫區和圖庫區。
 2. 下載圖檔時若發現圖檔大小只有21或22kb，則懷疑連接到盜連警示圖片，停一秒後重新連線一次。
 3. 若logFrame有開啟，就會自動讓logFrame輸出資訊。
 4. 修復在取得最新版本資訊時無法點擊其他按鈕的bug。
 5. 修復選取集數後按『下載』仍跳出選取集數視窗的bug。
 * 1.07: 修復EH無法下載會出現警告頁面Content Warning的問題

 ----------------------------------------------------------------------------------------------------

 */

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jcomicdownloader;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.Vector;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.Document;
import jcomicdownloader.enums.*;
import jcomicdownloader.frame.ChoiceFrame;
import jcomicdownloader.frame.InformationFrame;
import jcomicdownloader.frame.LogFrame;
import jcomicdownloader.frame.OptionFrame;
import jcomicdownloader.module.Run;
import jcomicdownloader.table.BookmarkTableModel;
import jcomicdownloader.table.DownTableRender;
import jcomicdownloader.table.DownloadTableModel;
import jcomicdownloader.table.RecordTableModel;
import jcomicdownloader.tools.*;

/**
 @author surveyorK
 @version 1.14 user 主介面，同時監聽window、mouse、button和textField。

 */
public class ComicDownGUI extends JFrame implements ActionListener,
                                                    DocumentListener,
                                                    MouseListener,
                                                    MouseMotionListener,
                                                    WindowFocusListener
{

    public static JFrame mainFrame; // for change look and feel
    // GUI component
    private BorderLayout layout;
    private JPanel buttonPanel, textPanel;
    private JButton button[];
    private JTextArea messageArea;
    private JTextField urlField;
    private JLabel urlLabel, logoLabel;
    JTabbedPane tabbedPane; // 裡面放三個頁面（任務、書籤、紀錄）
    public static TrayIcon trayIcon_old2; // 系統列圖示
    public static TrayIcon trayIcon;
    private PopupMenu trayPopup;
    private MenuItem trayShowItem;  // 開啟主介面
    private MenuItem trayStartItem; // 開始任務
    private MenuItem trayStopItem;  // 停止任務
    private MenuItem trayExitItem;  // 離開
    private JPopupMenu urlFieldPopup; // 網址列的右鍵選單
    private JMenuItem pasteSystemClipboardItem; // 網址列的右鍵選單項目一
    private JPopupMenu downloadTablePopup;
    private int downloadTablePopupRow; // 觸發downloadTablePopup的所在列
    private JMenuItem tableSearchDownloadComic;  // 以瀏覽器開啟搜尋下載漫畫的搜尋頁面
    private JMenuItem tableSearchBookmarkComic;  // 以瀏覽器開啟搜尋書籤漫畫的搜尋頁面
    private JMenuItem tableSearchRecordComic;  // 以瀏覽器開啟搜尋記錄漫畫的搜尋頁面
    private JMenuItem tableOpenDownloadURL;  // 以瀏覽器開啟漫畫網址
    private JMenuItem tableOpenDownloadFile;  // 開啟下載檔案
    private JMenuItem tableOpenDownloadDirectoryItem;  // 開啟下載資料夾
    private JMenuItem tableAddBookmarkFromDownloadItem;  // 加入到書籤
    private JMenuItem tableRechoiceVolumeItem;  // 重新選擇集數 
    private JMenuItem tableRenameTitleItem;  // 重新命名標題
    private JMenuItem tableDeleteMissionItem;  // 刪除任務
    private JMenuItem tableDeleteSelectedMissionItem;  // 刪除所有反白任務
    private JMenuItem tableDeleteFileItem;  // 刪除檔案（無法復原！）
    private JMenuItem tableDeleteAllUnselectedMissionItem;  // 刪除所有未勾選的任務
    private JMenuItem tableDeleteAllDoneMissionItem;  // 刪除所有已經完成的任務
    private JMenuItem tableMoveToRoofItem;  // 將此任務置頂
    private JMenuItem tableMoveToFloorItem;  // 將此任務置底
    private JMenuItem tableSelectedMoveToRoofItem;  // 將反白任務置頂
    private JMenuItem tableSelectedMoveToFloorItem;  // 將反白任務置底
    private JPopupMenu bookmarkTablePopup;
    private int bookmarkTablePopupRow; // 觸發downloadTablePopup的所在列
    private JMenuItem tableOpenBookmarkURL;  // 開啟漫畫網址
    private JMenuItem tableOpenBookmarkFile;  // 開啟書籤檔案
    private JMenuItem tableOpenBookmarkDirectoryItem;  // 開啟書籤資料夾
    private JMenuItem tableAddMissionFromBookmarkItem;  // 加入到任務
    private JMenuItem tableDeleteBookmarkItem;  // 刪除書籤
    private JPopupMenu recordTablePopup;
    private int recordTablePopupRow; // 觸發downloadTablePopup的所在列
    private JMenuItem tableOpenRecordURL;  // 開啟記錄漫畫網址
    private JMenuItem tableOpenRecordFile;  // 開啟記錄檔案
    private JMenuItem tableOpenRecordDirectoryItem;  // 開啟記錄資料夾
    private JMenuItem tableAddBookmarkFromRecordItem;  // 加入到書籤
    private JMenuItem tableAddMissionFromRecordItem;  // 加入到任務
    private JMenuItem tableDeleteMissionRecordItem; // 刪除記錄
    private JMenuItem tableDeleteFileRecordItem; // 刪除此檔案
    public static LogFrame logFrame; // show log, for debug
    public JTable downTable;
    public JTable bookmarkTable;
    public JTable recordTable;
    public static JLabel stateBar;
    public static DownloadTableModel downTableModel;
    public static BookmarkTableModel bookmarkTableModel;
    public static RecordTableModel recordTableModel;
    public static String[] downTableUrlStrings;
    public static String[] nowSelectedCheckStrings;
    public static int[][] downTableRealChoiceOrder;
    public static String defaultSkinClassName;
    private String onlyDefaultSkinClassName; // 不會被偷改的skin預設值
    // non-GUI component
    private String[] args;
    private static String resourceFolder;
    private StringBuffer messageString;
    private Run mainRun;
    private int nowDownloadMissionRow; // 目前正在進行下載的任務列的順序
    Dimension frameDimension;
    public static String versionString = "JComicDownloader  v5.19";

    public ComicDownGUI()
    {
        super( versionString );

        Common.setHttpProxy(); // 設置代理伺服器

        minimizeEvent();
        inittrayIcon();

        mainFrame = this; // for change look and feel

        if ( Common.isUnix() )
        {
            onlyDefaultSkinClassName = defaultSkinClassName = "com.sun.java.swing.plaf.gtk.GTKLookAndFeel";
        }
        else
        {
            onlyDefaultSkinClassName = defaultSkinClassName = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
        }

        resourceFolder = "resource" + Common.getSlash();

        downTableUrlStrings = new String[ 1000 ]; // 若任務超過1000個可能就會出錯...
        downTableRealChoiceOrder = new int[ 2000 ][]; // 最多1000個任務，每個任務最多2000集... ex.柯南七百集了...

        // 建立logFrame視窗，是否開啟則視預設值而定
        //javax.swing.SwingUtilities.invokeLater( new Runnable() { sdf


        new Thread( new Runnable()
        {

            public void run()
            {
                SwingUtilities.invokeLater( new Runnable()
                {

                    public void run()
                    {
                        // 避免在NapKin Look and Feel下發生錯誤( JRE 7的問題)
                        setSkin( ComicDownGUI.getDefaultSkinClassName()  );
                        logFrame = new LogFrame();
                        setSkin( SetUp.getSkinClassName() );

                        // 解決部分Look and Feel在OS X下不支援command + c/v 的問題
                        if ( Common.isMac() )
                        {
                            InputMap im = ( InputMap ) UIManager.get( "TextField.focusInputMap" );
                            im.put( KeyStroke.getKeyStroke( KeyEvent.VK_C, KeyEvent.META_DOWN_MASK ), DefaultEditorKit.copyAction );
                            im.put( KeyStroke.getKeyStroke( KeyEvent.VK_V, KeyEvent.META_DOWN_MASK ), DefaultEditorKit.pasteAction );
                            im.put( KeyStroke.getKeyStroke( KeyEvent.VK_X, KeyEvent.META_DOWN_MASK ), DefaultEditorKit.cutAction );
                        }

                        //setSkin(SetUp.getSkinClassName());

                        if ( SetUp.getOpenDebugMessageWindow() )
                        { // 由logFrame輸出資訊
                            logFrame.setVisible( true );
                            Debug.commandDebugMode = false;
                        }
                        else
                        {
                            // 由cmd輸出資訊 
                            logFrame.setVisible( false );
                            Debug.commandDebugMode = true;
                        }
                    }
                } );

            }
        } ).start();

        //CommonGUI.newFrameStartInEDT( "jcomicdownloader.frame.LogFrame", 
        //    SetUp.getOpenDebugMessageWindow() );

        //counter();  // 以code頁面記錄開啟次數（好玩測試看看）

        messageString = new StringBuffer( "" );

        setUpUIComponent();
        setUpeListener();
        setVisible( true );

        // 檢查skin是否由外部jar支援，若是外部skin且沒有此jar，則下載
        CommonGUI.checkSkin();

        buildPreprocess(); // 開發階段的預先處理(目的是讓輸出的jar檔與版本一致)
    }

    private void setUpUIComponent()
    {
        String picFileString = SetUp.getBackgroundPicPathOfMainFrame();
        // 檢查背景圖片是否存在
        if ( SetUp.getUsingBackgroundPicOfMainFrame()
                && !new File( picFileString ).exists() )
        {
            CommonGUI.showMessageDialog( this, picFileString
                    + "<BR>背景圖片不存在，重新設定為原始佈景",
                                         "提醒訊息", JOptionPane.INFORMATION_MESSAGE );
            SetUp.setUsingBackgroundPicOfMainFrame( false );
        }

        if ( SetUp.getUsingBackgroundPicOfMainFrame() )
        {
            frameDimension = CommonGUI.getDimension( picFileString );

            int width = ( int ) frameDimension.getWidth() + CommonGUI.widthGapOfBackgroundPic;
            int height = ( int ) frameDimension.getHeight() + CommonGUI.heightGapOfBackgroundPic;
            setSize( width, height );
            //setSize( frameDimension );
            setResizable( false );
        }
        else
        {
            int extendWidth = (SetUp.getDefaultFontSize() - 18) * 10; // 跟隨字體加寬
            extendWidth = extendWidth > 0 ? extendWidth : 0;
            setSize( 790 + extendWidth, 540 );
            setResizable( true );
        }

        setLocationRelativeTo( this );  // set the frame in middle position of screen
        setDefaultLookAndFeelDecorated( false ); // 讓標題欄可以隨look and feel改變
        setIconImage( new CommonGUI().getImage( CommonGUI.mainIcon ) ); // 設置左上角圖示

        addWindowFocusListener( this ); // 用來監測主視窗情形，若取得焦點就在輸入欄貼上剪貼簿網址



        Container contentPane;
        if ( SetUp.getUsingBackgroundPicOfMainFrame() )
        {
            (( JPanel ) getContentPane()).setOpaque( false );
            contentPane = CommonGUI.getImagePanel( picFileString );
            //contentPane.setPreferredSize( frameDimension );
            getContentPane().add( contentPane, BorderLayout.CENTER );
        }
        else
        {
            contentPane = getContentPane();
        }

        setButton( contentPane );

        setTextLayout( contentPane );

        // 改變表格內容預設的字體顏色
        setDefaultRenderer( downTable, downTableModel );
        setDefaultRenderer( bookmarkTable, bookmarkTableModel );
        setDefaultRenderer( recordTable, recordTableModel );


        // default skin: Windows uses Nimbus skin, Ubuntu uses GTK skin

        //setSkin( ComicDownGUI.defaultSkinClassName  ); 
        setSkin( SetUp.getSkinClassName() );

        stateBar = new JLabel( "請貼上網址                                       " );
        stateBar.setHorizontalAlignment( SwingConstants.LEFT );
        stateBar.setBorder( BorderFactory.createEtchedBorder() );
        CommonGUI.setToolTip( stateBar, "可顯示程式執行流程與目前下載進度" );

        if ( SetUp.getUsingBackgroundPicOfMainFrame() )
        {
            int width = ( int ) frameDimension.getWidth() - 10;
            int height = ( int ) frameDimension.getHeight() * 5 / 100;
            stateBar.setPreferredSize( new Dimension( width, height ) );
            stateBar.setForeground( SetUp.getMainFrameOtherDefaultColor() );
        }

        if ( SetUp.getSkinClassName().matches( ".*napkin.*" ) )
        {
            // 因為napkin的預設字型不太清楚，所以用選定字型
            stateBar.setFont( SetUp.getDefaultFont( - 3 ) );
        }

        contentPane.add( stateBar, BorderLayout.SOUTH );

    }

    public static String getDefaultSkinClassName()
    {
        return defaultSkinClassName;
    }

    public static void setDefaultSkinClassName1( String className )
    {
        defaultSkinClassName = className;
    }

    /**
     改成defaultSkinClassName名稱的版面

     */
    private void setSkin()
    {
        setSkin( SetUp.getSkinClassName() );
    }

    private void setSkin( String skinClassName )
    {
        Common.debugPrintln( "設置" + skinClassName + "介面" );

        try
        {
            if ( skinClassName.matches( ".*substance.api.skin.*" )
                    && !new File( Common.getNowAbsolutePath() + "trident.jar" ).exists() )
            {
                throw new Exception( "資料夾內找不到trident.jar，無法設置Substance介面" );
            }

            CommonGUI.setLookAndFeelByClassName( skinClassName );
        }
        catch ( Exception ex )
        {
            Common.hadleErrorMessage( ex, "無法使用" + skinClassName + "介面 !!" );

            // 若無法配置指定的skin，就用預設的
            CommonGUI.setLookAndFeelByClassName( defaultSkinClassName );
        }

        CommonGUI.updateUI( this ); // 更新介面

    }

    // 設置主介面上的主要按鈕
    private void setButton( Container contentPane )
    {
        button = new JButton[ 9 ];
        String buttonPic;
        String buttonText;

        buttonPanel = new JPanel();
        buttonPanel.setLayout( new GridLayout( 1, button.length ) );
        buttonPanel.setOpaque( !SetUp.getUsingBackgroundPicOfMainFrame() );

        button[ButtonEnum.ADD] = getButton( "加入", "Add", "解析網址列的網址，解析後可選擇欲下載集數並加入任務", "add.png", KeyEvent.VK_A );
        button[ButtonEnum.DOWNLOAD] = getButton( "下載", "Download", "若網址列有網址，則解析後加入任務並開始下載；若網址列沒有網址，則開始下載目前的任務清單", "download.png", KeyEvent.VK_D );

        button[ButtonEnum.STOP] = getButton( "停止", "Stop", "停止下載，中斷進行中的任務", "stop.png", KeyEvent.VK_S );
        button[ButtonEnum.UP] = getButton( "提升", "Rise", "將反白任務往上提一個順位", "up.png", KeyEvent.VK_U );
        button[ButtonEnum.DOWN] = getButton( "沉降", "Fall", "將反白任務往下降一個順位", "down.png", KeyEvent.VK_D );
        button[ButtonEnum.CLEAR] = getButton( "清除", "Clean", "清除目前的任務清單（若一次無法清空且按多次）", "clear.png", KeyEvent.VK_C );
        button[ButtonEnum.OPTION] = getButton( "選項", "Option", "功能設定與調整（粗體字為預設功能）", "option.png", KeyEvent.VK_O );
        button[ButtonEnum.INFORMATION] = getButton( "資訊", "Info", "相關提示與訊息", "information.png", KeyEvent.VK_I );
        button[ButtonEnum.EXIT] = getButton( "離開", "Exit", "關閉本程式", "exit.png", KeyEvent.VK_E );


        for ( int count = 0; count < button.length; count++ )
        {
            button[count].setHorizontalTextPosition( SwingConstants.CENTER );
            button[count].setVerticalTextPosition( SwingConstants.BOTTOM );
            buttonPanel.add( button[count] );
            button[count].addActionListener( this );
            button[count].addMouseListener( this );
        }

        if ( SetUp.getUsingBackgroundPicOfMainFrame() )
        {
            int width = ( int ) frameDimension.getWidth();
            int height = ( int ) frameDimension.getHeight() / 4;
            buttonPanel.setPreferredSize( new Dimension( width, height ) );
        }
        contentPane.add( buttonPanel, BorderLayout.NORTH );
    }

    // 設置主介面上的網址輸入框
    private void setTextLayout( Container contentPane )
    {
        if ( Common.isMac() )
        {
            urlField = new JTextField(
                Common.getStringUsingDefaultLanguage( "請貼上欲下載的漫畫頁面網址，而後點擊『加入』按鈕", "paste the URL" ) );
        
        }
        else
        {
            urlField = new JTextField(
                Common.getStringUsingDefaultLanguage( "請複製欲下載的漫畫頁面網址，此輸入欄會自動捕捉", "copy the URL" ) );
        }
        urlField.setFont( SetUp.getDefaultFont( 3 ) );

        if ( SetUp.getUsingBackgroundPicOfMainFrame() )
        {
            urlField.setForeground( SetUp.getMainFrameOtherDefaultColor() );
        }

        urlField.addMouseListener( this );
        CommonGUI.setToolTip( urlField, "請輸入漫畫作品的主頁面或單集頁面網址" );
        urlField.setOpaque( !SetUp.getUsingBackgroundPicOfMainFrame() );

        setUrlFieldJPopupMenu(); // 設置右鍵彈出選單

        Document doc = urlField.getDocument();
        doc.addDocumentListener( this ); // check the string in urlField on time

        // set white space in up, down, left and right
        JPanel urlPanel = new CommonGUI().getCenterPanel( urlField );
        urlPanel.setOpaque( !SetUp.getUsingBackgroundPicOfMainFrame() );

        textPanel = new JPanel( new BorderLayout() );
        textPanel.add( urlPanel, BorderLayout.NORTH );
        textPanel.setOpaque( !SetUp.getUsingBackgroundPicOfMainFrame() );

        setTabbedPane( textPanel );
        contentPane.add( textPanel, BorderLayout.CENTER );
    }

    // 設置主介面上的任務清單
    private void setDownloadTable( final JPanel textPanel )
    {
        downTable = getDownloadTable();//new JTable( new DataTable());
        //downTable.setPreferredScrollableViewportSize( new Dimension( 450, 120 ) );
        downTable.setAutoResizeMode( JTable.AUTO_RESIZE_ALL_COLUMNS );

        JScrollPane downScrollPane = new JScrollPane( downTable );
        //JPanel downPanel = new CommonGUI().getCenterPanel( downScrollPane );

        setDownloadTableJPopupMenu(); // 設置右鍵彈出選單
        //downScrollPane.setOpaque(false); 
        textPanel.add( downScrollPane, BorderLayout.CENTER );
    }

    // 設置主介面上的書籤頁面
    private void setBookmarkTable( JPanel textPanel )
    {
        bookmarkTable = getBookmarkTable();//new JTable( new DataTable());
        //downTable.setPreferredScrollableViewportSize( new Dimension( 450, 120 ) );
        bookmarkTable.setAutoResizeMode( JTable.AUTO_RESIZE_ALL_COLUMNS );
        bookmarkTable.setFillsViewportHeight( true );
        bookmarkTable.setAutoCreateRowSorter( true );

        JScrollPane bookmarkScrollPane = new JScrollPane( bookmarkTable );
        //JPanel downPanel = new CommonGUI().getCenterPanel( downScrollPane );

        setBookmarkTableJPopupMenu(); // 設置右鍵彈出選單

        textPanel.add( bookmarkScrollPane, BorderLayout.CENTER );
    }

    private void setRecordTable( JPanel textPanel )
    {
        recordTable = getRecordTable();//new JTable( new DataTable());
        //downTable.setPreferredScrollableViewportSize( new Dimension( 450, 120 ) );
        recordTable.setAutoResizeMode( JTable.AUTO_RESIZE_ALL_COLUMNS );
        recordTable.setFillsViewportHeight( true );
        recordTable.setAutoCreateRowSorter( true );

        JScrollPane recordScrollPane = new JScrollPane( recordTable );
        //JPanel downPanel = new CommonGUI().getCenterPanel( downScrollPane );

        setRecordTableJPopupMenu(); // 設置右鍵彈出選單

        textPanel.add( recordScrollPane, BorderLayout.CENTER );
    }

    private void setTabbedPane( JPanel textPanel )
    {

        tabbedPane = new JTabbedPane();
        tabbedPane.setOpaque( !SetUp.getUsingBackgroundPicOfMainFrame() );

        //JComponent panel1 = makeTextPanel("Panel #1");
        JPanel downTablePanel = new JPanel( new GridLayout( 1, 1 ) ); // 不規範為gridLayout就會固定大小...
        setDownloadTable( downTablePanel );
        tabbedPane.addTab( Common.getStringUsingDefaultLanguage( " 下載任務  ", "  Download List  " ),
                           new CommonGUI().getImageIcon( "tab_download.png" ),
                           downTablePanel, CommonGUI.getToolTipString( "所有欲下載的任務都會出現在此處，可依序下載" ) );
        tabbedPane.setMnemonicAt( 0, KeyEvent.VK_1 );

        JPanel bookmarkTablePanel = new JPanel( new GridLayout( 1, 1 ) ); // 不規範為gridLayout就會固定大小...
        setBookmarkTable( bookmarkTablePanel );
        tabbedPane.addTab( Common.getStringUsingDefaultLanguage( " 我的書籤  ", "  Bookmark  " ),
                           new CommonGUI().getImageIcon( "tab_bookmark.png" ),
                           bookmarkTablePanel, CommonGUI.getToolTipString( "希望持續追蹤的漫畫可加入到此處" ) );
        tabbedPane.setMnemonicAt( 1, KeyEvent.VK_2 );

        JPanel recordTablePanel = new JPanel( new GridLayout( 1, 1 ) ); // 不規範為gridLayout就會固定大小...
        setRecordTable( recordTablePanel );
        tabbedPane.addTab( Common.getStringUsingDefaultLanguage( " 任務記錄  ", "  Record  " ),
                           new CommonGUI().getImageIcon( "tab_record.png" ),
                           recordTablePanel, CommonGUI.getToolTipString( "所有曾經加入到下載任務的漫畫都會記錄在這邊，可由『選項』來選擇持續記錄或關閉後清空" ) );
        tabbedPane.setMnemonicAt( 2, KeyEvent.VK_3 );

        // 若設定為透明，就用預定顏色字體。
        if ( SetUp.getUsingBackgroundPicOfMainFrame() )
        {
            int width = ( int ) frameDimension.getWidth();
            int height = 0;

            int frameHeight = ( int ) frameDimension.getHeight();

            if ( frameHeight < 400 )
            {
                height = frameHeight * 48 / 100;
            }
            else if ( frameHeight < 500 )
            {
                height = frameHeight * 52 / 100;
            }
            else if ( frameHeight < 600 )
            {
                height = frameHeight * 54 / 100;
            }
            else if ( frameHeight < 700 )
            {
                height = frameHeight * 56 / 100;
            }

            tabbedPane.setPreferredSize( new Dimension( width, height ) );
            tabbedPane.setForeground( SetUp.getMainFrameTableDefaultColor() );

        }
        if ( SetUp.getSkinClassName().matches( ".*napkin.*" ) )
        {
            // 因為napkin的預設字型不太清楚，所以用選定字型
            tabbedPane.setFont( SetUp.getDefaultFont() );
        }

        textPanel.add( tabbedPane, BorderLayout.CENTER );
    }

    protected JComponent makeTextPanel( String text )
    {
        JPanel panel = new JPanel( false );
        JLabel filler = new JLabel( text );
        filler.setHorizontalAlignment( JLabel.CENTER );
        panel.setLayout( new GridLayout( 1, 1 ) );
        panel.add( filler );
        return panel;
    }

    public static Vector<String> getDownloadColumns()
    {
        Vector<String> columnName = new Vector<String>();

        if ( SetUp.getDefaultLanguage() == LanguageEnum.TRADITIONAL_CHINESE )
        {
            columnName.add( ("編號") );
            columnName.add( ("是否下載") );
            columnName.add( ("漫畫名稱") );
            columnName.add( ("總共集數") );
            columnName.add( ("勾選集數") );
            columnName.add( ("目前狀態") );
            columnName.add( ("網址解析") );
        }
        else if ( SetUp.getDefaultLanguage() == LanguageEnum.SIMPLIFIED_CHINESE )
        {
            columnName.add( ("编号") );
            columnName.add( ("是否下载") );
            columnName.add( ("漫画名称") );
            columnName.add( ("总共集数") );
            columnName.add( ("勾选集数") );
            columnName.add( ("目前状态") );
            columnName.add( ("网址解析") );
        }
        else
        {
            columnName.add( ("No") );
            columnName.add( ("Y/N") );
            columnName.add( ("Title") );
            columnName.add( ("Volume") );
            columnName.add( ("Mark") );
            columnName.add( ("State") );
            columnName.add( ("URL") );
        }

        return columnName;
    }

    public static Vector<String> getBookmarkColumns()
    {
        Vector<String> columnName = new Vector<String>();

        if ( SetUp.getDefaultLanguage() == LanguageEnum.TRADITIONAL_CHINESE )
        {
            columnName.add( "編號" );
            columnName.add( "漫畫名稱" );
            columnName.add( "漫畫網址" );
            columnName.add( "加入日期" );
            columnName.add( "評論註解" );
        }
        else if ( SetUp.getDefaultLanguage() == LanguageEnum.SIMPLIFIED_CHINESE )
        {
            columnName.add( "编号" );
            columnName.add( "漫画名称" );
            columnName.add( "漫画网址" );
            columnName.add( "加入日期" );
            columnName.add( "评论注解" );
        }
        else
        {
            columnName.add( "No" );
            columnName.add( "Title" );
            columnName.add( "URL" );
            columnName.add( "Date" );
            columnName.add( "Comment" );
        }

        return columnName;
    }

    public static Vector<String> getRecordColumns()
    {
        Vector<String> columnName = new Vector<String>();

        if ( SetUp.getDefaultLanguage() == LanguageEnum.TRADITIONAL_CHINESE )
        {
            columnName.add( "編號" );
            columnName.add( "漫畫名稱" );
            columnName.add( "漫畫網址" );
            columnName.add( "加入日期" );
        }
        else if ( SetUp.getDefaultLanguage() == LanguageEnum.SIMPLIFIED_CHINESE )
        {
            columnName.add( "编号" );
            columnName.add( "漫画名称" );
            columnName.add( "漫画网址" );
            columnName.add( "加入日期" );
        }
        else
        {
            columnName.add( "No" );
            columnName.add( "Title" );
            columnName.add( "URL" );
            columnName.add( "Date" );
        }

        return columnName;
    }

    private void setUrlFieldJPopupMenu()
    {

        pasteSystemClipboardItem = new JMenuItem( "貼上網址" ); // 開啟下載資料夾
        pasteSystemClipboardItem.addActionListener( this );

        urlFieldPopup = new JPopupMenu();
        urlFieldPopup.add( pasteSystemClipboardItem );
        urlField.add( urlFieldPopup ); // 必須指定父元件，否則會拋出NullPointerException
    }

    private void setDownloadTableJPopupMenu()
    {
        tableSearchDownloadComic = getMenuItem( "搜尋這本漫畫", "Search this comic", new CommonGUI().getImageIcon( "menuItem_search.gif" ) ); // 以瀏覽器開啟搜尋此本漫畫的搜尋頁面
        tableOpenDownloadURL = getMenuItem( "開啟網頁", "Go this page", new CommonGUI().getImageIcon( "menuItem_link.gif" ) ); // 以瀏覽器開啟漫畫網址
        tableOpenDownloadFile = getMenuItem( "開啟檔案", "Open the file", new CommonGUI().getImageIcon( "menuItem_open.png" ) ); // 開啟下載資料夾
        tableOpenDownloadDirectoryItem = getMenuItem( "開啟資料夾", "Open the directory", new CommonGUI().getImageIcon( "menuItem_folder.png" ) ); // 開啟下載資料夾
        tableAddBookmarkFromDownloadItem = getMenuItem( "加入到書籤", "Add into bookmark", new CommonGUI().getImageIcon( "menuItem_bookmark.png" ) ); // 開啟下載資料夾
        tableRechoiceVolumeItem = getMenuItem( "重新選擇集數", "Choice the volumes", new CommonGUI().getImageIcon( "menuItem_readd.png" ) );  // 重新選擇集數
        tableRenameTitleItem = getMenuItem( "重新命名標題", "Rename this title", new CommonGUI().getImageIcon( "menuItem_rename.png" ) );  // 重新命名漫畫名稱
        tableDeleteMissionItem = getMenuItem( "刪除此任務", "Remove this mission", new CommonGUI().getImageIcon( "menuItem_delete_mission.png" ) );  // 刪除任務
        tableDeleteSelectedMissionItem = getMenuItem( "刪除所有反白任務", "Remove all selected missions", new CommonGUI().getImageIcon( "menuItem_delete_mission.png" ) );  // 刪除所有反白任務
        tableDeleteFileItem = getMenuItem( "刪除此漫畫", "Delete this comic", new CommonGUI().getImageIcon( "menuItem_delete_file.gif" ) );  // 刪除檔案
        tableDeleteAllUnselectedMissionItem = getMenuItem( "刪除所有未勾選任務", "Remove all unmarked missions", new CommonGUI().getImageIcon( "menuItem_uncheck.gif" ) );  // 刪除所有未勾選的任務
        tableDeleteAllDoneMissionItem = getMenuItem( "刪除所有已完成任務", "Remove all done missions", new CommonGUI().getImageIcon( "menuItem_done.gif" ) );  // 刪除所有已經完成的任務
        tableMoveToRoofItem = getMenuItem( "將此列任務置頂", "Top this mission", new CommonGUI().getImageIcon( "menuItem_up.png" ) );  // 刪除所有已經完成的任務
        tableMoveToFloorItem = getMenuItem( "將此列任務置底", "Foot this mission", new CommonGUI().getImageIcon( "menuItem_down.png" ) );  // 刪除所有已經完成的任務
        tableSelectedMoveToRoofItem = getMenuItem( "將所有反白任務置頂", "Top all selected missions", new CommonGUI().getImageIcon( "menuItem_up_selected.png" ) );  // 刪除所有已經完成的任務
        tableSelectedMoveToFloorItem = getMenuItem( "將所有反白任務置底", "Foot all selected missions", new CommonGUI().getImageIcon( "menuItem_down_selected.png" ) );  // 刪除所有已經完成的任務


        downloadTablePopup = new JPopupMenu();
        downloadTablePopup.add( tableAddBookmarkFromDownloadItem );
        downloadTablePopup.add( tableOpenDownloadDirectoryItem );
        downloadTablePopup.add( tableOpenDownloadFile );
        downloadTablePopup.add( tableOpenDownloadURL );
        downloadTablePopup.add( tableSearchDownloadComic );
        downloadTablePopup.add( tableRechoiceVolumeItem );
        downloadTablePopup.add( tableRenameTitleItem );
        downloadTablePopup.add( tableDeleteMissionItem );
        downloadTablePopup.add( tableDeleteSelectedMissionItem );
        downloadTablePopup.add( tableDeleteAllUnselectedMissionItem );
        downloadTablePopup.add( tableDeleteAllDoneMissionItem );
        downloadTablePopup.add( tableMoveToRoofItem );
        downloadTablePopup.add( tableMoveToFloorItem );
        downloadTablePopup.add( tableSelectedMoveToRoofItem );
        downloadTablePopup.add( tableSelectedMoveToFloorItem );
        downloadTablePopup.add( tableDeleteFileItem );

        downTable.add( downloadTablePopup ); // 必須指定父元件，否則會拋出NullPointerException

    }

    private void setBookmarkTableJPopupMenu()
    {
        tableSearchBookmarkComic = getMenuItem( "搜尋這本漫畫", "Search this comic", new CommonGUI().getImageIcon( "menuItem_search.gif" ) ); // 以瀏覽器開啟搜尋此本漫畫的搜尋頁面
        tableOpenBookmarkURL = getMenuItem( "開啟網頁", "Go to the page", new CommonGUI().getImageIcon( "menuItem_link.gif" ) ); // 開啟書籤漫畫網址
        tableOpenBookmarkDirectoryItem = getMenuItem( "開啟資料夾", "Open the directory", new CommonGUI().getImageIcon( "menuItem_folder.png" ) ); // 開啟下載資料夾
        tableOpenBookmarkFile = getMenuItem( "開啟檔案", "Open the file", new CommonGUI().getImageIcon( "menuItem_open.png" ) ); // 開啟下載資料夾
        tableAddMissionFromBookmarkItem = getMenuItem( "加入到下載任務", "Add into missions", new CommonGUI().getImageIcon( "menuItem_add.gif" ) ); // 加入到下載任務
        tableDeleteBookmarkItem = getMenuItem( "刪除此書籤", "Remove this bookmark", new CommonGUI().getImageIcon( "menuItem_delete_mission.png" ) );  // 刪除書籤

        bookmarkTablePopup = new JPopupMenu();
        bookmarkTablePopup.add( tableAddMissionFromBookmarkItem );
        bookmarkTablePopup.add( tableOpenBookmarkDirectoryItem );
        bookmarkTablePopup.add( tableOpenBookmarkFile );
        bookmarkTablePopup.add( tableOpenBookmarkURL );
        bookmarkTablePopup.add( tableSearchBookmarkComic );
        bookmarkTablePopup.add( tableDeleteBookmarkItem );

        if ( SetUp.getUsingBackgroundPicOfMainFrame() )
        {
            int spaceCount = 15 - bookmarkTablePopup.getComponentCount();
            for ( int i = 0; i < spaceCount; i++ )
            {
                bookmarkTablePopup.add( "" );
            }
        }

        bookmarkTable.add( bookmarkTablePopup ); // 必須指定父元件，否則會拋出NullPointerException
    }

    private void setRecordTableJPopupMenu()
    {
        tableSearchRecordComic = getMenuItem( "搜尋這本漫畫", "Search this comic", new CommonGUI().getImageIcon( "menuItem_search.gif" ) ); // 以瀏覽器開啟搜尋此本漫畫的搜尋頁面
        tableOpenRecordURL = getMenuItem( "開啟網頁", "Go to the page", new CommonGUI().getImageIcon( "menuItem_link.gif" ) ); // 開啟記錄漫畫網址
        tableOpenRecordFile = getMenuItem( "開啟檔案", "Open file", new CommonGUI().getImageIcon( "menuItem_open.png" ) ); // 開啟下載檔案
        tableOpenRecordDirectoryItem = getMenuItem( "開啟資料夾", "Open the directory", new CommonGUI().getImageIcon( "menuItem_folder.png" ) ); // 開啟下載資料夾
        tableAddMissionFromRecordItem = getMenuItem( "加入到下載任務", "Add into missions", new CommonGUI().getImageIcon( "menuItem_add.gif" ) ); // 加入到下載任務
        tableAddBookmarkFromRecordItem = getMenuItem( "加入到書籤", "Add into bookmarks", new CommonGUI().getImageIcon( "menuItem_bookmark.png" ) ); // 開啟下載資料夾
        tableDeleteMissionRecordItem = getMenuItem( "刪除此記錄", "Remove this record", new CommonGUI().getImageIcon( "menuItem_delete_mission.png" ) );  // 刪除記錄
        tableDeleteFileRecordItem = getMenuItem( "刪除此檔案", "Delete this file", new CommonGUI().getImageIcon( "menuItem_delete_file.gif" ) );  // 刪除檔案

        recordTablePopup = new JPopupMenu();
        recordTablePopup.add( tableAddMissionFromRecordItem );
        recordTablePopup.add( tableAddBookmarkFromRecordItem );
        recordTablePopup.add( tableOpenRecordDirectoryItem );
        recordTablePopup.add( tableOpenRecordFile );
        recordTablePopup.add( tableOpenRecordURL );
        recordTablePopup.add( tableSearchRecordComic );
        recordTablePopup.add( tableDeleteMissionRecordItem );
        recordTablePopup.add( tableDeleteFileRecordItem );

        if ( SetUp.getUsingBackgroundPicOfMainFrame() )
        {
            int spaceCount = 15 - recordTablePopup.getComponentCount();
            for ( int i = 0; i < spaceCount; i++ )
            {
                recordTablePopup.add( "" );
            }
        }

        recordTable.add( recordTablePopup ); // 必須指定父元件，否則會拋出NullPointerException
    }

    private JTable getDownloadTable()
    {
        try {
            downTableModel = Common.inputDownTableFile();//new DataTableModel( getDownloadColumns(), 0 );
        }
        catch( Exception ex )
        {
            Common.debugPrintln( "下載清單讀取失敗!! " );
            downTableModel = null;
        }


        JTable table = new JTable( downTableModel )
        {

            protected String[] columnToolTips =
            {
                CommonGUI.getToolTipString( "此欄位滑鼠左鍵點兩下可刪除該列任務" ),
                CommonGUI.getToolTipString( "此欄位若沒有勾選就不會進行下載" ),
                CommonGUI.getToolTipString( "此欄位滑鼠左鍵點兩下可開啟該列任務的下載資料夾" ),
                CommonGUI.getToolTipString( "此欄位滑鼠左鍵點兩下可重新選取該列任務的下載集數" ),
                CommonGUI.getToolTipString( "此欄位滑鼠左鍵點兩下可重新選取該列任務的下載集數" ),
                CommonGUI.getToolTipString( "此欄位可顯示目前的下載進度，滑鼠左鍵點兩下以預設瀏覽程式開啟" ),
                null
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

        table.setRowHeight( SetUp.getDefaultFontSize() + 5 ); // 隨字體增加寬度

        //table.setPreferredScrollableViewportSize( new Dimension( 400, 170 ) );
        table.setFillsViewportHeight( true );
        //table.setAutoCreateRowSorter( true ); // allow resort
        table.getSelectionModel().addListSelectionListener( new RowListener() );
        table.addMouseListener( this );

        // 取得這個table的欄位模型
        TableColumnModel cModel = table.getColumnModel();

        // 配置每個欄位的寬度比例（可隨視窗大小而變化）
        cModel.getColumn( DownTableEnum.ORDER ).setPreferredWidth( ( int ) (this.getWidth() * 0.07) );
        cModel.getColumn( DownTableEnum.YES_OR_NO ).setPreferredWidth( ( int ) (this.getWidth() * 0.14) );
        cModel.getColumn( DownTableEnum.TITLE ).setPreferredWidth( ( int ) (this.getWidth() * 0.6) );
        cModel.getColumn( DownTableEnum.VOLUMES ).setPreferredWidth( ( int ) (this.getWidth() * 0.14) );
        cModel.getColumn( DownTableEnum.CHECKS ).setPreferredWidth( ( int ) (this.getWidth() * 0.14) );
        cModel.getColumn( DownTableEnum.STATE ).setPreferredWidth( ( int ) (this.getWidth() * 0.3) );
        cModel.getColumn( DownTableEnum.URL ).setPreferredWidth( ( int ) (this.getWidth() * 0.002) );

        //table.setOpaque(false); //無效 

        // 若設定為透明，就用預定顏色字體。
        if ( SetUp.getUsingBackgroundPicOfMainFrame() )
        {
            table.getTableHeader().setForeground( SetUp.getMainFrameTableDefaultColor() );
            table.setForeground( SetUp.getMainFrameTableDefaultColor() );
            table.addMouseMotionListener( this );
        }

        if ( SetUp.getSkinClassName().matches( ".*napkin.*" ) )
        {
            // 因為napkin的預設字型不太清楚，所以用選定字型
            table.setFont( SetUp.getDefaultFont( - 2 ) );
            table.getTableHeader().setFont( SetUp.getDefaultFont( - 2 ) );
        }

        return table;
    }

    private JTable getBookmarkTable()
    {
        bookmarkTableModel = Common.inputBookmarkTableFile();//new DataTableModel( getDownloadColumns(), 0 );
        JTable table = new JTable( bookmarkTableModel )
        {

            protected String[] columnToolTips =
            {
                CommonGUI.getToolTipString( "此欄位滑鼠左鍵點兩下可刪除該列書籤" ),
                CommonGUI.getToolTipString( "此欄位滑鼠左鍵點兩下可開啟該列書籤的下載資料夾" ),
                CommonGUI.getToolTipString( "此欄位滑鼠左鍵點兩下可將該列書籤加入到下載任務清單中" ),
                CommonGUI.getToolTipString( "此欄位顯示該列漫畫加入書籤的系統時間，滑鼠左鍵點兩下以預設瀏覽程式開啟" ),
                CommonGUI.getToolTipString( "此欄位滑鼠左鍵點兩下可自由編輯該列任務的注解" )
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

        table.setRowHeight( SetUp.getDefaultFontSize() + 5 ); // 隨字體增加寬度

        //table.setPreferredScrollableViewportSize( new Dimension( 400, 170 ) );
        table.setFillsViewportHeight( true );
        table.setAutoCreateRowSorter( true ); // allow resort
        table.getSelectionModel().addListSelectionListener( new RowListener() );
        table.addMouseListener( this );
        table.addMouseMotionListener( this );

        // 取得這個table的欄位模型
        TableColumnModel cModel = table.getColumnModel();

        // 配置每個欄位的寬度比例（可隨視窗大小而變化）
        cModel.getColumn( BookmarkTableEnum.ORDER ).setPreferredWidth( ( int ) (this.getWidth() * 0.07) );
        cModel.getColumn( BookmarkTableEnum.TITLE ).setPreferredWidth( ( int ) (this.getWidth() * 0.25) );
        cModel.getColumn( BookmarkTableEnum.DATE ).setPreferredWidth( ( int ) (this.getWidth() * 0.25) );
        cModel.getColumn( BookmarkTableEnum.URL ).setPreferredWidth( ( int ) (this.getWidth() * 0.38) );
        cModel.getColumn( BookmarkTableEnum.COMMENT ).setPreferredWidth( ( int ) (this.getWidth() * 0.27) );

        // 若設定為透明，就用預定顏色字體。
        if ( SetUp.getUsingBackgroundPicOfMainFrame() )
        {
            table.getTableHeader().setForeground( SetUp.getMainFrameTableDefaultColor() );
            table.setForeground( SetUp.getMainFrameTableDefaultColor() );
        }

        if ( SetUp.getSkinClassName().matches( ".*napkin.*" ) )
        {
            // 因為napkin的預設字型不太清楚，所以用選定字型
            table.setFont( SetUp.getDefaultFont( - 2 ) );
            table.getTableHeader().setFont( SetUp.getDefaultFont( - 2 ) );
        }

        return table;
    }

    private JTable getRecordTable()
    {
        recordTableModel = Common.inputRecordTableFile();//new DataTableModel( getDownloadColumns(), 0 );
        JTable table = new JTable( recordTableModel )
        {

            protected String[] columnToolTips =
            {
                CommonGUI.getToolTipString( "此欄位滑鼠左鍵點兩下可刪除該列記錄" ),
                CommonGUI.getToolTipString( "此欄位滑鼠左鍵點兩下可開啟該列記錄的下載資料夾" ),
                CommonGUI.getToolTipString( "此欄位滑鼠左鍵點兩下可將該列漫畫加入到下載任務清單中" ),
                CommonGUI.getToolTipString( "此欄位顯示該列漫畫在當初加入任務的系統時間，滑鼠左鍵點兩下以預設瀏覽程式開啟" )
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

        table.setRowHeight( SetUp.getDefaultFontSize() + 5 ); // 隨字體增加寬度

        //table.setPreferredScrollableViewportSize( new Dimension( 400, 170 ) );
        table.setFillsViewportHeight( true );
        table.setAutoCreateRowSorter( true ); // allow resort
        table.getSelectionModel().addListSelectionListener( new RowListener() );
        table.addMouseListener( this );
        table.addMouseMotionListener( this );

        // 取得這個table的欄位模型
        TableColumnModel cModel = table.getColumnModel();

        // 配置每個欄位的寬度比例（可隨視窗大小而變化）
        cModel.getColumn( RecordTableEnum.ORDER ).setPreferredWidth( ( int ) (this.getWidth() * 0.07) );
        cModel.getColumn( RecordTableEnum.TITLE ).setPreferredWidth( ( int ) (this.getWidth() * 0.32) );
        cModel.getColumn( RecordTableEnum.DATE ).setPreferredWidth( ( int ) (this.getWidth() * 0.25) );
        cModel.getColumn( RecordTableEnum.URL ).setPreferredWidth( ( int ) (this.getWidth() * 0.43) );

        // 若設定為透明，就用預定顏色字體。
        if ( SetUp.getUsingBackgroundPicOfMainFrame() )
        {
            table.getTableHeader().setForeground( SetUp.getMainFrameTableDefaultColor() );
            table.setForeground( SetUp.getMainFrameTableDefaultColor() );
        }

        if ( SetUp.getSkinClassName().matches( ".*napkin.*" ) )
        {
            // 因為napkin的預設字型不太清楚，所以用選定字型
            table.setFont( SetUp.getDefaultFont( - 2 ) );
            table.getTableHeader().setFont( SetUp.getDefaultFont( - 2 ) );
        }

        return table;
    }

    private void showNotifyMessage( String message )
    {
    }

    private void setUpeListener()
    {
        /*
         addComponentListener(new ComponentAdapter() { public void
         componentResized(ComponentEvent e) { Dimension nowSize = getSize();
         panelWidth = getWidth(); panelHeight = getHeight();

         Common.debugPrintln( panelWidth + ", " + panelHeight ); repaint(); } });
         */
        //setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    }

    /**
     @param args the command line arguments
     */
    public static void main( String[] args )
    {
        Common.debugPrintln( "JComicDownloader start ..." );

        SetUp set = new SetUp();
        set.readSetFile(); // 讀入設置檔的設置參數

        SwingUtilities.invokeLater( new Runnable()
        {

            public void run()
            {
                new ComicDownGUI();
            }
        } );
    }

    // --------- window event --------------
    public void windowGainedFocus( WindowEvent e )
    {
        // mac os x系統下的捕捉剪貼簿有問題，暫時不提供
        if ( !Common.isMac() )
        {
            SystemClipBoard clip = new SystemClipBoard();
            String clipString = clip.getClipString();

            if ( !clipString.equals( Common.prevClipString ) )
            {
                //Common.debugPrint( "取得系統剪貼簿內容: " + clipString + "  " );

                if ( Common.isLegalURL( clipString ) )
                {
                    urlField.setText( clipString ); // 當取得焦點時自動貼網址到輸入框中
                    Common.prevClipString = clipString;

                    if ( SetUp.getAutoAddMission() )
                    {
                        String[] tempArgs =
                        {
                            clipString
                        };
                        parseURL( tempArgs, false, false, 0 );
                    }
                }
            }
            else
            {
                urlField.setText( "" ); // 之前貼過的就不要再顯示了
            }
        }
        
    }

    public void windowLostFocus( WindowEvent e )
    {
    }

    private void minimizeEvent()
    {
        this.setDefaultCloseOperation( WindowConstants.HIDE_ON_CLOSE );

        this.addWindowListener( new WindowAdapter()
        {

            public void windowClosing( WindowEvent e )
            {
                if ( Common.isWindows() )
                {
                    setState( Frame.ICONIFIED ); // 縮小後可觸發windowIconified()而縮入系統列
                }
                else
                {
                    // 因為在ubuntu縮小收進系統框後無法再顯示視窗
                    // 只能用取消顯示再產生system tray
                    setVisible( false );
                    minimizeToTray();
                }
            }

            public void windowIconified( WindowEvent e )
            {
                if ( Common.isWindows() )
                {
                    setVisible( false );
                    minimizeToTray();
                }
                else
                {
                    // ubuntu底下縮小收進系統框後無法取出，
                    // 因此在非windows環境，按縮小就只給縮小功能
                    setState( Frame.ICONIFIED );
                }
            }
        } );
    }

    public void minimizeToTray()
    {

        if ( SystemTray.isSupported() )
        {
            SystemTray tray = SystemTray.getSystemTray();
            try
            {
                tray.add( this.trayIcon );
            }
            catch ( AWTException ex )
            {
                Common.hadleErrorMessage( ex, "無法加入系統工具列圖示" );
            }
        }
        else
        {
            setState( Frame.ICONIFIED ); // 若系統沒有支援縮進系統框，就只好縮小到下方列。
        }


    }

    // 設置縮小到系統框的圖示
    private void inittrayIcon()
    {
        if ( SystemTray.isSupported() )
        {

            Image image;

            if ( Common.isUnix() )
            {
                image = new CommonGUI().getImage( "tray_icon_for_linux.png" );
            }
            else
            {
                image = new CommonGUI().getImage( "main_icon.png" );
            }

            trayExitItem = new MenuItem( "Exit Application" );
            trayExitItem.addActionListener( this );
            trayStartItem = new MenuItem( "Start Mission" );
            trayStartItem.addActionListener( this );
            trayStopItem = new MenuItem( "Stop Mission" );
            trayStopItem.addActionListener( this );
            trayShowItem = new MenuItem( "Open Window" );
            trayShowItem.addActionListener( this );

            trayPopup = new PopupMenu();
            trayPopup.add( trayExitItem );
            trayPopup.add( trayStopItem );
            trayPopup.add( trayStartItem );
            trayPopup.add( trayShowItem );

            //

            if ( image != null )
            {
                // 原本的系統列
                //trayIcon_old = new trayIcon_old( image, "JComicDownloader", null );
                //trayIcon_old.addMouseListener( this );

                trayIcon = new TrayIcon( image, "JComicDownloader", trayPopup );
                trayIcon.setImageAutoSize( true );
                trayIcon.addMouseListener( this );
            }
            else
            {
                trayIcon = null;
            }
        }
        else
        {
            trayIcon = null;
            Common.debugPrintln( "系統不支援trayIcon！" );
        }

    }

    // --------- urlField Event -------------
    public void insertUpdate( DocumentEvent event )
    {
        Document doc = event.getDocument();
        try
        {
            args = doc.getText( 0, doc.getLength() ).split( "\\s+" );
        }
        catch ( BadLocationException ex )
        {
            Common.hadleErrorMessage( ex, "無法取得documet的文字！" );
        }

        //messageArea.append( webSite );
    }

    public void removeUpdate( DocumentEvent event )
    {
        Document doc = event.getDocument();
        try
        {
            args = doc.getText( 0, doc.getLength() ).split( "\\s+" );
        }
        catch ( BadLocationException ex )
        {
            Common.hadleErrorMessage( ex, "無法取得document的文字" );
        }
    }

    public void changedUpdate( DocumentEvent event )
    {
        Document doc = event.getDocument();
        try
        {
            args = doc.getText( 0, doc.getLength() ).split( "\\s+" );
        }
        catch ( BadLocationException ex )
        {
            Common.hadleErrorMessage( ex, "無法取得document的文字" );
        }
    }

    // -------------- mouse event   -----------------
    @Override
    public void mousePressed( MouseEvent event )
    {
        // when mouse move on the urlField, clear the text on urlField
        if ( event.getSource() == urlField )
        {
            urlField.setText( "" );
        }

        if ( event.getSource() == trayIcon && event.getButton() == MouseEvent.BUTTON1 )
        {
            setVisible( true );
            setState( Frame.NORMAL );
            SystemTray.getSystemTray().remove( trayIcon );
        }

        if ( event.getSource() == urlField && event.getButton() == MouseEvent.BUTTON3 )
        {
            showUrlFieldPopup( event );
            System.out.print( "." );
        }

        if ( event.getSource() == downTable && tabbedPane.getSelectedIndex() == TabbedPaneEnum.MISSION )
        {
            showDownloadPopup( event );
        }
        else if ( event.getSource() == bookmarkTable && tabbedPane.getSelectedIndex() == TabbedPaneEnum.BOOKMARK )
        {
            showBookmarkPopup( event );
        }
        else if ( event.getSource() == recordTable && tabbedPane.getSelectedIndex() == TabbedPaneEnum.RECORD )
        {
            showRecordPopup( event );
        }
    }

    @Override
    public void mouseExited( MouseEvent event )
    {
        if ( SetUp.getUsingBackgroundPicOfMainFrame() )
        {

            if ( event.getSource() instanceof JTable )
            { // 主要是table
                CommonGUI.nowMouseAtRow = 10000; // 給很大的初始值，避免剛開始就有上色情形
                (( JComponent ) event.getSource()).repaint();
            }
            else
            {
                (( JComponent ) event.getSource()).setForeground( SetUp.getMainFrameOtherDefaultColor() );
            }
        }
    }

    @Override
    public void mouseEntered( MouseEvent event )
    {
        if ( SetUp.getUsingBackgroundPicOfMainFrame() )
        {
            if ( event.getSource() instanceof JTable )
            {
            }
            else
            {
                (( JComponent ) event.getSource()).setForeground( SetUp.getMainFrameOtherMouseEnteredColor() );
            }
        }
    }

    @Override
    public void mouseReleased( MouseEvent event )
    {
        if ( event.getSource() == downTable && tabbedPane.getSelectedIndex() == TabbedPaneEnum.MISSION )
        {
            showDownloadPopup( event );
        }
        else if ( event.getSource() == bookmarkTable && tabbedPane.getSelectedIndex() == TabbedPaneEnum.BOOKMARK )
        {
            showBookmarkPopup( event );
        }
        else if ( event.getSource() == recordTable && tabbedPane.getSelectedIndex() == TabbedPaneEnum.RECORD )
        {
            showRecordPopup( event );
        }

        if ( event.getSource() == urlField && event.getButton() == MouseEvent.BUTTON3 )
        {
            showUrlFieldPopup( event );
        }

        /*
         * // 原本的系統列 if ( event.getSource() == trayIcon_old &&
         event.getButton() == MouseEvent.BUTTON3 ) { if ( event.isPopupTrigger()
         && !trayPopup.isVisible() ) { trayPopup.setLocation( event.getX() + 10,
         event.getY() ); trayPopup.setInvoker( trayPopup );
         trayPopup.setVisible( true ); } else { trayPopup.setVisible( false ); }
         }
         */

    }

    // 在任務列點擊左鍵兩下會跳出重新選擇集數的視窗，
    // 點擊右鍵一下會跳出是否刪除此任務的訊息視窗。
    @Override
    public void mouseClicked( MouseEvent event )
    {

        if ( (event.getModifiers() & MouseEvent.BUTTON1_MASK) != 0
                && event.getClickCount() == 2
                && tabbedPane.getSelectedIndex() == TabbedPaneEnum.MISSION )
        {
            int row = event.getY() / downTable.getRowHeight();
            int col = downTable.getColumnModel().getSelectionModel().getLeadSelectionIndex();

            if ( event.getSource() == downTable
                    && row < Common.missionCount && row >= 0 )
            {

                // 任何時候都能開啟下載資料夾
                if ( col == DownTableEnum.TITLE )
                {
                    openDownloadDirectory( row );
                }
                else if ( col == DownTableEnum.STATE )
                {
                    openDownloadFile( row );
                }
                else if ( col == DownTableEnum.URL )
                {
                    deleteMission( row );
                }

                // 正在下載的時候不能重選集數，也不能刪除任務
                if ( col == DownTableEnum.VOLUMES
                        || col == DownTableEnum.CHECKS )
                {
                    if ( !Flag.downloadingFlag )
                    {
                        rechoiceVolume( row );
                    }
                    else
                    {
                        CommonGUI.showMessageDialog( this, "目前正下載中，無法重新選擇集數",
                                                     "提醒訊息", JOptionPane.INFORMATION_MESSAGE );
                    }
                }
                else if ( col == DownTableEnum.ORDER )
                {
                    openDownloadURL( row );
                }
            }
        }

        if ( (event.getModifiers() & MouseEvent.BUTTON1_MASK) != 0
                && event.getClickCount() == 2
                && tabbedPane.getSelectedIndex() == TabbedPaneEnum.BOOKMARK )
        {
            int row = event.getY() / bookmarkTable.getRowHeight();
            int col = bookmarkTable.getColumnModel().getSelectionModel().getLeadSelectionIndex();

            if ( event.getSource() == bookmarkTable
                    && row < Common.bookmarkCount && row >= 0 )
            {

                // 任何時候都能開啟下載資料夾
                if ( col == BookmarkTableEnum.TITLE )
                {
                    openDownloadDirectory( row );
                }
                else if ( col == BookmarkTableEnum.DATE )
                {
                    openDownloadFile( row );
                }
                else if ( col == BookmarkTableEnum.URL )
                {
                    addMission( row );
                }
                else if ( col == BookmarkTableEnum.ORDER )
                {
                    openDownloadURL( row );
                }
            }
        }

        if ( (event.getModifiers() & MouseEvent.BUTTON1_MASK) != 0
                && event.getClickCount() == 2
                && tabbedPane.getSelectedIndex() == TabbedPaneEnum.RECORD )
        {
            int row = event.getY() / recordTable.getRowHeight();
            int col = recordTable.getColumnModel().getSelectionModel().getLeadSelectionIndex();

            if ( event.getSource() == recordTable
                    && row < Common.recordCount && row >= 0 )
            {

                // 任何時候都能開啟下載資料夾
                if ( col == RecordTableEnum.TITLE )
                {
                    openDownloadDirectory( row );
                }
                else if ( col == RecordTableEnum.DATE )
                {
                    openDownloadFile( row );
                }

                // 正在下載的時候不能重選集數，也不能刪除任務
                if ( col == RecordTableEnum.URL )
                {
                    addMission( row );
                }
                else if ( col == RecordTableEnum.ORDER )
                {
                    openDownloadURL( row );
                }
            }
        }

    }

    private void addMission( int row )
    { // 從書籤或紀錄加入任務
        String[] tempArgs = new String[ 1 ];

        if ( tabbedPane.getSelectedIndex() == TabbedPaneEnum.BOOKMARK )
        {
            row = bookmarkTable.convertRowIndexToModel( row ); // 顯示的列 -> 實際的列
            tempArgs[0] = bookmarkTableModel.getValueAt( row, BookmarkTableEnum.URL ).toString();
        }
        else if ( tabbedPane.getSelectedIndex() == TabbedPaneEnum.RECORD )
        {
            row = recordTable.convertRowIndexToModel( row ); // 顯示的列 -> 實際的列
            tempArgs[0] = recordTableModel.getValueAt( row, RecordTableEnum.URL ).toString();
        }
        else if ( tabbedPane.getSelectedIndex() == TabbedPaneEnum.MISSION )
        {
            rechoiceVolume( row );
            return;
        }
        else
        {
             Common.errorReport( "不可能從書籤和記錄以外的地方加入任務！" );
        }

        urlField.setText( tempArgs[0] );
        parseURL( tempArgs, false, false, 0 );

        // 每加一個任務就紀錄一次。
        Common.outputRecordTableFile( recordTableModel );
    }

    private void rechoiceVolume( int row )
    { // 重新選擇集數
        row = downTable.convertRowIndexToModel( row ); // 顯示的列 -> 實際的列
        Common.debugPrintln( downTableModel.getRealValueAt( row, DownTableEnum.CHECKS ).toString() );
        ComicDownGUI.nowSelectedCheckStrings = Common.getSeparateStrings(
                String.valueOf( downTableModel.getRealValueAt( row, DownTableEnum.CHECKS ) ) );
        Common.debugPrintln( "重新解析位址（為了重選集數）：" + downTableUrlStrings[row] );
        parseURL( new String[]
                {
                    downTableUrlStrings[row]
                }, false, true, row );

    }

    private void renameTitle( int row )
    { // 重新命名標題
        row = downTable.convertRowIndexToModel( row ); // 顯示的列 -> 實際的列

        if ( row == nowDownloadMissionRow && Flag.downloadingFlag )
        {
            CommonGUI.showMessageDialog( this, "目前正下載中，無法重新命名標題",
                                         "提醒訊息", JOptionPane.INFORMATION_MESSAGE );
            return;
        }

        String oldTitleString = downTableModel.getRealValueAt( row, DownTableEnum.TITLE ).toString();
        Common.debugPrintln( "原本標題名稱：" + oldTitleString );

        String newTitleString = CommonGUI.showInputDialog( ComicDownGUI.mainFrame,
                                                           "請輸入新的標題名稱（需在下載之前修改）", "輸入視窗", JOptionPane.INFORMATION_MESSAGE );

        if ( newTitleString != null )
        {
            Common.debugPrintln( "新的標題名稱：" + newTitleString );
            downTableModel.setValueAt( newTitleString, row, DownTableEnum.TITLE );
        }
    }

    // 從beginIndex開始，後面的全部往前挪一格
    private void stringsMoveOneForward( String[] strings, int beginIndex )
    {
        for ( int i = beginIndex; strings[i + 1] != null; i++ )
        {
            strings[i] = strings[i + 1];
        }
    }

    private String getTableName( JTable targetTable )
    {
        if ( targetTable == downTable )
        {
            return "任務";
        }
        else if ( targetTable == bookmarkTable )
        {
            return "書籤";
        }
        else if ( targetTable == recordTable )
        {
            return "紀錄";
        }
        else
        {
            return "未知的表格";
        }
    }

    // moveToRoof = true: 置頂, false: 上提一個順位
    private void moveMissionUp( JTable targetTable, int row, boolean moveToRoof )
    { // 將第row列任務上提
        row = targetTable.convertRowIndexToModel( row ); // 顯示的列 -> 實際的列

        Common.debugPrintln( "目前要提升位置的是" + getTableName( targetTable ) + "表格" );

        Common.debugPrint( "指定要置頂的列：" + row + "\t" );
        //Common.debugPrint("目前正在下載的列：" + nowDownloadMissionRow + "\t");

        // 若要提升的是下載任務，且指定要置頂的該列正好是目前下載列，則禁止置換
        if ( targetTable == downTable && row == nowDownloadMissionRow && Flag.downloadingFlag )
        {
            CommonGUI.showMessageDialog( this, "目前正下載中，無法移動任務的順序位置",
                                         "提醒訊息", JOptionPane.INFORMATION_MESSAGE );
            return;
        }

        // 若不是下載中或位於正在下載該列的上方，則可以置換到最高處；反之，就只能置換到目前正在下載該列的後面。
        int roof;


        if ( moveToRoof )
        {
            if ( row < nowDownloadMissionRow || !Flag.downloadingFlag )
            {
                roof = 0;
            }
            else
            {
                roof = nowDownloadMissionRow + 1;
            }
        }
        else
        {
            if ( targetTable == bookmarkTable || targetTable == recordTable )
            {
                roof = row - 1;
            }
            else if ( row < nowDownloadMissionRow || !Flag.downloadingFlag )
            {
                roof = row - 1;
            }
            else if ( row > nowDownloadMissionRow + 1 )
            {
                roof = row - 1;
            }
            else
            {
                roof = row;
            }
        }

        Common.debugPrintln( "允許置換後的列：" + roof );

        String urlString = downTableUrlStrings[row]; // 先將此列任務另存
        //downTable.setValueAt( roof + 1, row, DownTableEnum.ORDER );
        for ( int i = row - 1; i >= roof; i-- )
        {
            if ( targetTable == downTable )
            {
                downTableUrlStrings[i + 1] = downTableUrlStrings[i]; // row列之前的往後遞移一位
            }
            i = targetTable.convertRowIndexToModel( i ); // 顯示的列 -> 實際的列
            //downTable.setValueAt( i + 2, i, DownTableEnum.ORDER );

            targetTable.addRowSelectionInterval( i, i );
            targetTable.removeRowSelectionInterval( i + 1, i + 1 );
        }

        if ( targetTable == downTable )
        {
            downTableUrlStrings[roof] = urlString; // 再將之前另存任務存入第一個位置
        }

        if ( targetTable == downTable )
        {
            downTableModel.moveRow( row, row, roof );
        }
        else if ( targetTable == bookmarkTable )
        {
            bookmarkTableModel.moveRow( row, row, roof );
        }
        else if ( targetTable == recordTable )
        {
            recordTableModel.moveRow( row, row, roof );
        }

    }

    private void moveMissionDown( JTable targetTable, int row, boolean moveToFloor )
    { // 將第row列任務置底
        row = targetTable.convertRowIndexToModel( row ); // 顯示的列 -> 實際的列

        Common.debugPrintln( "目前要提升位置的是" + getTableName( targetTable ) + "表格" );

        Common.debugPrint( "指定要置底的列：" + row + "\t" );
        //Common.debugPrint( "目前正在下載的列：" + nowDownloadMissionRow + "\t" );

        // 若是下載表格，且指定要置頂的該列正好是目前下載列，則禁止置換
        if ( targetTable == downTable && row == nowDownloadMissionRow && Flag.downloadingFlag )
        {
            CommonGUI.showMessageDialog( this, "目前正下載中，無法移動任務的順序位置",
                                         "提醒訊息", JOptionPane.INFORMATION_MESSAGE );
            return;
        }

        int missionAmount = 0;
        if ( targetTable == downTable )
        {
            missionAmount = downTableModel.getRowCount();
        }
        else if ( targetTable == bookmarkTable )
        {
            missionAmount = bookmarkTableModel.getRowCount();
        }
        else if ( targetTable == recordTable )
        {
            missionAmount = recordTableModel.getRowCount();
        }

        // 若不是下載中或位於正在下載該列的下方，則可以置換到最底處；反之，就只能置換到目前正在下載該列的上面。
        int floor;

        if ( moveToFloor )
        {
            if ( row > nowDownloadMissionRow || !Flag.downloadingFlag )
            {
                floor = missionAmount - 1;
            }
            else
            {
                floor = nowDownloadMissionRow - 1;
            }
        }
        else
        {
            if ( targetTable == bookmarkTable || targetTable == recordTable )
            {
                floor = row + 1;
            }
            else if ( row > nowDownloadMissionRow || !Flag.downloadingFlag )
            {
                floor = row + 1;
            }
            else if ( row < nowDownloadMissionRow - 1 )
            {
                floor = row + 1;
            }
            else
            {
                floor = row;
            }
        }

        Common.debugPrintln( "允許置換後的列：" + floor );

        String urlString = downTableUrlStrings[row]; // 先將此列任務另存
        //downTable.setValueAt( floor + 1, row, DownTableEnum.ORDER );
        for ( int i = row + 1; i <= floor; i++ )
        {
            i = targetTable.convertRowIndexToModel( i ); // 顯示的列 -> 實際的列

            if ( targetTable == downTable )
            {
                downTableUrlStrings[i - 1] = downTableUrlStrings[i]; // row列之後的往前遞移一位
            }
            //downTable.setValueAt( i, i, DownTableEnum.ORDER );

            targetTable.addRowSelectionInterval( i, i );
            targetTable.removeRowSelectionInterval( i - 1, i - 1 );
        }

        if ( targetTable == downTable )
        {
            downTableUrlStrings[floor] = urlString; // 再將之前另存任務存入第一個位置
        }

        if ( targetTable == downTable )
        {
            downTableModel.moveRow( row, row, floor );
        }
        else if ( targetTable == bookmarkTable )
        {
            bookmarkTableModel.moveRow( row, row, floor );
        }
        else if ( targetTable == recordTable )
        {
            recordTableModel.moveRow( row, row, floor );
        }
    }

    private boolean deleteDownloadFile( String fileTitle )
    {

        if ( fileTitle == null || fileTitle.equals( "" ) )
        {
            CommonGUI.showMessageDialog( this,
                                         "<html>找不到此任務的名稱</font>，無法刪除！</html>",
                                         "提醒訊息", JOptionPane.INFORMATION_MESSAGE );

            Common.debugPrintln( "找不到此任務的名稱，無法刪除！" );
            return false;
        }

        String path = SetUp.getOriginalDownloadDirectory() + fileTitle;

        String nowSkinName = SetUp.getSkinClassName();
        String colorString = "blue";

        if ( CommonGUI.isDarkSytleSkin( nowSkinName ) )
        {
            colorString = "yellow"; // 暗色風格界面用黃色比較看得清楚
        }

        if ( new File( path ).exists() && new File( path ).isDirectory() )
        {
        }
        else if ( new File( path + ".zip" ).exists() )
        {
            path += ".zip";
        }
        else if ( new File( path + ".cbz" ).exists() )
        {
            path += ".cbz";
        }
        else
        {
            CommonGUI.showMessageDialog( this,
                                         "<html>找不到<font color=" + colorString + ">"
                    + path + "</font>，無法刪除！</html>",
                                         "提醒訊息", JOptionPane.INFORMATION_MESSAGE );

            Common.debugPrintln( path + " 找不到，無法刪除！" );
            return false;
        }



        String message = "<html>請問是否強制刪除<br><font color="
                + colorString + ">" + path + "</font> ？<br>"
                + "（注意：檔案刪除後將不會存放於資源回收筒，無法復原，請慎重考慮）</html>";
        String enMessage = "<html>Delete <br><font color="
                + colorString + ">" + path + "</font> ?";
        int choice = CommonGUI.showConfirmDialog( this, message, enMessage, "提醒訊息", JOptionPane.YES_NO_OPTION );

        if ( choice == JOptionPane.YES_OPTION )
        { // agree to remove the title in the download list
            if ( new File( path ).isDirectory() )
            {
                Common.deleteFolder( path );
                Common.debugPrintln( path + " 刪除成功！" );
                return true;
            }
            else if ( new File( path ).delete() )
            {
                Common.debugPrintln( path + " 刪除成功！" );
                return true;
            }
            else
            {
                Common.debugPrintln( path + " 刪除失敗！" );
                return false;
            }
        }
        else
        {
            Common.debugPrintln( path + " 取消刪除動作！" );
            return false;
        }
    }

    private void deleteMissionAndFile( int row )
    { // 刪除第row列的任務和實際資料夾
        row = downTable.convertRowIndexToModel( row ); // 顯示的列 -> 實際的列
        String title = String.valueOf( downTableModel.getRealValueAt(
                row, DownTableEnum.TITLE ) );

        if ( Flag.downloadingFlag )
        {
            CommonGUI.showMessageDialog( this, "目前正下載中，無法刪除任務",
                                         "提醒訊息", JOptionPane.INFORMATION_MESSAGE );
        }
        else if ( deleteDownloadFile( title ) )
        {
            deleteMission( row );
        }
    }

    private void deleteMission( int row )
    { // 刪除第row列任務
        row = downTable.convertRowIndexToModel( row ); // 顯示的列 -> 實際的列

        Common.debugPrint( "指定要刪除的列：" + row + "\t" );
        Common.debugPrintln( "目前正在下載的列：" + nowDownloadMissionRow + "\t" );

        if ( row <= nowDownloadMissionRow && Flag.downloadingFlag )
        {
            CommonGUI.showMessageDialog( this, "目前正下載中，無法刪除任務",
                                         "提醒訊息", JOptionPane.INFORMATION_MESSAGE );
        }
        else
        {

            String title = String.valueOf( downTableModel.getRealValueAt(
                    row, DownTableEnum.TITLE ) );

            String message = "是否要在任務清單中刪除" + title + " ?";
            String enMessage = "remove " + title + " ?";

            int choice = CommonGUI.showConfirmDialog( this, message, enMessage, "提醒訊息", JOptionPane.YES_NO_OPTION );

            if ( choice == JOptionPane.YES_OPTION )
            { // agree to remove the title in the download list
                Common.missionCount--;
                downTableModel.removeRow( row );
                stringsMoveOneForward( downTableUrlStrings, row ); // 儲存URL的字串陣列從row開始都往前挪一格
            }
        }
    }

    private void deleteAllUnselectedMission()
    { // 刪除所有未勾選的任務
        String message = "是否要在任務清單中刪除所有未勾選的任務 ?";
        String enMessage = "remove all unmarked mission ?";

        int choice = CommonGUI.showConfirmDialog( this, message, enMessage, "詢問視窗", JOptionPane.YES_NO_OPTION );

        if ( choice == JOptionPane.YES_OPTION )
        { // agree to remove the title in the download list
            int nowRow = 0;
            while ( nowRow < downTableModel.getRowCount() )
            {
                if ( downTableModel.getValueAt( nowRow, DownTableEnum.YES_OR_NO ).toString().equals( "false" ) )
                {
                    downTableModel.removeRow( nowRow );
                    stringsMoveOneForward( downTableUrlStrings, nowRow ); // 儲存URL的字串陣列從row開始都往前挪一格
                    Common.missionCount--;
                }
                else
                {
                    nowRow++; // 沒有符合情況才往後搜尋
                }
            }
            repaint();
        }
    }

    private void deleteAllDoneMission()
    { // 刪除所有已經完成的任務
        String message = "是否要在任務清單中刪除所有已經完成的任務 ?";
        String enMessage = "remove all done missions ?";

        int choice = CommonGUI.showConfirmDialog( this, message, enMessage, "提醒訊息", JOptionPane.YES_NO_OPTION );

        if ( choice == JOptionPane.YES_OPTION )
        { // agree to remove the title in the download list
            int nowRow = 0;
            while ( nowRow < downTableModel.getRowCount() )
            {
                if ( downTableModel.getValueAt( nowRow, DownTableEnum.STATE ).toString().equals( "下載完畢" ) )
                {
                    downTableModel.removeRow( nowRow );
                    stringsMoveOneForward( downTableUrlStrings, nowRow ); // 儲存URL的字串陣列從row開始都往前挪一格
                    Common.missionCount--;
                }
                else
                {
                    nowRow++; // 沒有符合情況才往後搜尋
                }
            }
            repaint();
        }
    }

    private void deleteBookmark( int row )
    { // 刪除第row列書籤
        row = bookmarkTable.convertRowIndexToModel( row ); // 顯示的列 -> 實際的列
        String title = String.valueOf( bookmarkTableModel.getValueAt(
                row, BookmarkTableEnum.TITLE ) );
        String message = "是否要在書籤中刪除" + title + " ?";
        String enMessage = "remove " + title + " from the bookmark ?";
        int choice = CommonGUI.showConfirmDialog( this, message, enMessage, "提醒訊息", JOptionPane.YES_NO_OPTION );

        if ( choice == JOptionPane.YES_OPTION )
        { // agree to remove the title in the download list
            Common.bookmarkCount--;
            bookmarkTableModel.removeRow( row );
        }
    }

    private void deleteFileFromRecord( int row )
    {
        row = recordTable.convertRowIndexToModel( row ); // 顯示的列 -> 實際的列
        String title = String.valueOf( recordTableModel.getValueAt(
                row, RecordTableEnum.TITLE ) );


        if ( deleteDownloadFile( title ) );
    }

    private void deleteRecord( int row )
    { // 刪除第row列記錄
        row = recordTable.convertRowIndexToModel( row ); // 顯示的列 -> 實際的列
        String title = String.valueOf( recordTableModel.getValueAt(
                row, RecordTableEnum.TITLE ) );
        String message = "是否要在記錄中刪除" + title + " ?";
        String enMessage = "remove " + title + " from the records ?";
        int choice = CommonGUI.showConfirmDialog( this, message, enMessage, "提醒訊息", JOptionPane.YES_NO_OPTION );

        if ( choice == JOptionPane.YES_OPTION )
        { // agree to remove the title in the download list
            Common.recordCount--;
            recordTableModel.removeRow( row );
        }
    }

    private void addBookmark( int row )
    {  // 將第row列任務加入到書籤中

        String title = "";
        String url = "";
        if ( tabbedPane.getSelectedIndex() == TabbedPaneEnum.MISSION )
        { // 從任務清單加入
            row = downTable.convertRowIndexToModel( row ); // 顯示的列 -> 實際的列
            title = String.valueOf( downTableModel.getRealValueAt( row, DownTableEnum.TITLE ) );
            url = downTableUrlStrings[row];
        }
        else if ( tabbedPane.getSelectedIndex() == TabbedPaneEnum.RECORD )
        { // 從紀錄清單加入
            row = recordTable.convertRowIndexToModel( row ); // 顯示的列 -> 實際的列
            title = String.valueOf( recordTableModel.getValueAt( row, RecordTableEnum.TITLE ) );
            url = String.valueOf( recordTableModel.getValueAt( row, RecordTableEnum.URL ) );
        }

        Common.debugPrintln( "加入到書籤：" + title + " " + url );
        bookmarkTableModel.addRow( CommonGUI.getBookmarkDataRow(
                ++Common.bookmarkCount,
                title,
                url ) );

        Common.outputBookmarkTableFile( bookmarkTableModel ); // 每加入書籤便寫入書籤記錄檔一次。
    }

    private void searchDownloadComic( int row )
    {  // 以瀏覽器開啟第row列任務的下載檔案網址
        String title = "";
        String url = "";

        if ( tabbedPane.getSelectedIndex() == TabbedPaneEnum.MISSION )
        { // 從任務清單開啟
            row = downTable.convertRowIndexToModel( row ); // 顯示的列 -> 實際的列
            title = String.valueOf( downTableModel.getRealValueAt( row, DownTableEnum.TITLE ) );
            url = downTableUrlStrings[row];
        }
        else if ( tabbedPane.getSelectedIndex() == TabbedPaneEnum.BOOKMARK )
        { // 從書籤清單開啟
            row = bookmarkTable.convertRowIndexToModel( row ); // 顯示的列 -> 實際的列
            title = String.valueOf( bookmarkTableModel.getValueAt( row, BookmarkTableEnum.TITLE ) );
            url = String.valueOf( bookmarkTableModel.getValueAt( row, BookmarkTableEnum.URL ) );
        }
        else if ( tabbedPane.getSelectedIndex() == TabbedPaneEnum.RECORD )
        { // 從記錄清單開啟
            row = recordTable.convertRowIndexToModel( row ); // 顯示的列 -> 實際的列
            title = String.valueOf( recordTableModel.getValueAt( row, RecordTableEnum.TITLE ) );
            url = String.valueOf( recordTableModel.getValueAt( row, RecordTableEnum.URL ) );
        }

        Common.debugPrintln( "瀏覽器開啟自訂搜尋引擎，並以『" + title + "』作為關鍵字進行搜尋" );

        new RunBrowser().runBroswer( getKeywordSearchURL( title ) );
    }

    private void openDownloadURL( int row )
    {  // 以瀏覽器開啟第row列任務的下載檔案網址
        String title = "";
        String url = "";

        if ( tabbedPane.getSelectedIndex() == TabbedPaneEnum.MISSION )
        { // 從任務清單開啟
            row = downTable.convertRowIndexToModel( row ); // 顯示的列 -> 實際的列
            title = String.valueOf( downTableModel.getRealValueAt( row, DownTableEnum.TITLE ) );
            url = downTableUrlStrings[row];
        }
        else if ( tabbedPane.getSelectedIndex() == TabbedPaneEnum.BOOKMARK )
        { // 從書籤清單開啟
            row = bookmarkTable.convertRowIndexToModel( row ); // 顯示的列 -> 實際的列
            title = String.valueOf( bookmarkTableModel.getValueAt( row, BookmarkTableEnum.TITLE ) );
            url = String.valueOf( bookmarkTableModel.getValueAt( row, BookmarkTableEnum.URL ) );
        }
        else if ( tabbedPane.getSelectedIndex() == TabbedPaneEnum.RECORD )
        { // 從記錄清單開啟
            row = recordTable.convertRowIndexToModel( row ); // 顯示的列 -> 實際的列
            title = String.valueOf( recordTableModel.getValueAt( row, RecordTableEnum.TITLE ) );
            url = String.valueOf( recordTableModel.getValueAt( row, RecordTableEnum.URL ) );
        }

        Common.debugPrintln( "以預設瀏覽器開啟" + title + "的原始網頁" );

        new RunBrowser().runBroswer( url );
    }

    private boolean isNovelSite( String url )
    {
        if ( (url.matches( "(?s).*ck101.com(?s).*" ) && !url.matches( "(?s).*comic.ck101.com(?s).*" ))
                || url.matches( "(?s).*eyny.com(?s).*" )
                || url.matches( "(?s).*catcatbox.com(?s).*" )
                || url.matches( "(?s).*mybest.com(?s).*" )
                || url.matches( "(?s).*wenku.com(?s).*" )
                || url.matches( "(?s).*8novel.com(?s).*" )
                || url.matches( "(?s).*book.qq.com(?s).*" )
                || url.matches( "(?s).*book.sina.com(?s).*" )
                || url.matches( "(?s).*51cto.com(?s).*" )
                || url.matches( "(?s).*uus8.com(?s).*" )
                || url.matches( "(?s).*wenku8.com(?s).*" )
                || url.matches( "(?s).*7wenku.com(?s).*" )
                || url.matches( "(?s).*book.ifeng.com(?s).*" )
                || url.matches( "(?s).*xunlook.com(?s).*" )
                || url.matches( "(?s).*tianyabook.com(?s).*" )
                || url.matches( "(?s).*woyouxian.com(?s).*" )
                || url.matches( "(?s).*shunong.com(?s).*" ) )
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    private boolean isBlogSite( String url )
    {
        if ( url.matches( "(?s).*blogspot(?s).*" )
                || url.matches( "(?s).*pixnet.net(?s).*" )
                || url.matches( "(?s).*blog.xuite.net(?s).*" )
                || url.matches( "(?s).*blog.yam.com(?s).*" ) )
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    private boolean openTextFile( String cmd, String path, String title )
    {
        String filePath = "";
        if ( new File( path + title + ".txt" ).exists() )
        {
            filePath = path + title + ".txt";
            //Common.debugPrintln( "開啟命令：" + cmd + " " + filePath );

            if ( Common.isWindows() )
            {
                Common.runUnansiCmd( cmd, filePath );
            }
            else
            {
                Common.runSimpleCmd( cmd, filePath );
            }
            return true;
        }
        else if ( new File( path + title + ".html" ).exists() )
        {
            filePath = path + title + ".html";
            //Common.debugPrintln( "開啟命令：" + cmd + " " + filePath );
            if ( Common.isWindows() )
            {
                Common.runUnansiCmd( cmd, filePath );
            }
            else
            {
                Common.runSimpleCmd( cmd, filePath );
            }
            return true;
        }
        else
        {
            return false;
        }
    }

    private void openDownloadFile( int row )
    {  // 開啟第row列任務的下載檔案
        String title = "";
        String url = "";

        if ( tabbedPane.getSelectedIndex() == TabbedPaneEnum.MISSION )
        { // 從任務清單開啟
            row = downTable.convertRowIndexToModel( row ); // 顯示的列 -> 實際的列
            title = String.valueOf( downTableModel.getRealValueAt( row, DownTableEnum.TITLE ) );
            url = downTableModel.getRealValueAt( row, DownTableEnum.URL ).toString();
        }
        else if ( tabbedPane.getSelectedIndex() == TabbedPaneEnum.BOOKMARK )
        { // 從書籤清單開啟
            row = bookmarkTable.convertRowIndexToModel( row ); // 顯示的列 -> 實際的列
            title = String.valueOf( bookmarkTableModel.getValueAt( row, BookmarkTableEnum.TITLE ) );
            url = String.valueOf( bookmarkTableModel.getValueAt( row, BookmarkTableEnum.URL ) );
        }
        else if ( tabbedPane.getSelectedIndex() == TabbedPaneEnum.RECORD )
        { // 從記錄清單開啟
            row = recordTable.convertRowIndexToModel( row ); // 顯示的列 -> 實際的列
            title = String.valueOf( recordTableModel.getValueAt( row, RecordTableEnum.TITLE ) );
            url = String.valueOf( recordTableModel.getValueAt( row, RecordTableEnum.URL ) );
        }

        // 檢查有無設定外部開啟程式
        String fileFormatName = "";
        String program = ""; // 預設開啟檔案的程式
        if ( isNovelSite( url ) || isBlogSite( url ) )
        {
            program = SetUp.getOpenTextFileProgram();
            fileFormatName = "文字檔";
        }
        else
        {
            program = SetUp.getOpenPicFileProgram();
            fileFormatName = "圖片檔";
        }

        if ( program.matches( "" ) )
        {
            //String nowSkinName = UIManager.getLookAndFeel().getName(); // 目前使用中的面板名稱
            // 取得介面設定值（不用getLookAndFeel()是因為這樣才能讀到_之後的參數）
            String nowSkinName = SetUp.getSkinClassName();
            String colorString = "blue";
            if ( CommonGUI.isDarkSytleSkin( nowSkinName ) )
            {
                colorString = "yellow"; // 暗色風格界面用黃色比較看得清楚
            }

            CommonGUI.showMessageDialog( this,
                                         "<html>尚未設定" + fileFormatName + "開啟程式，請前往<font color=" + colorString + ">選項 -> 瀏覽</font>做設定</html>",
                                         "提醒訊息", JOptionPane.INFORMATION_MESSAGE );
            return;
        }

        Common.debugPrintln( "以外部程式開啟" + title + "的下載資料夾或壓縮檔" );

        if ( isNovelSite( url ) || isBlogSite( url ) )
        {
            String cmd = SetUp.getOpenTextFileProgram();

            String path = SetUp.getOriginalDownloadDirectory();
            if ( !openTextFile( cmd, path, title ) )
            { // 先測試生成在母下載資料夾的那些 ex. ./down/
                path = SetUp.getOriginalDownloadDirectory() + title + Common.getSlash();
                openTextFile( cmd, path, title ); // 再測試生成在子下載資料夾的那些 ex. ./down/title/
            }

        }
        else if ( url.matches( "(?s).*e-hentai(?s).*" ) || url.matches( "(?s).*exhentai(?s).*" ) )
        {
            String cmd = SetUp.getOpenZipFileProgram();
            String path = "";

            boolean existZipFile = false;
            boolean existCbzFile = false;
            if ( new File( SetUp.getOriginalDownloadDirectory() + title + ".zip" ).exists() )
            {
                existZipFile = true;
            }
            if ( new File( SetUp.getOriginalDownloadDirectory() + title + ".cbz" ).exists() )
            {
                existCbzFile = true;
            }

            if ( existZipFile || existCbzFile )
            {
                String compressFormat = existZipFile ? "zip" : "cbz";

                path = SetUp.getOriginalDownloadDirectory() + title + "." + compressFormat;
                Common.debugPrintln( "開啟命令：" + cmd + " " + path );

                if ( Common.isWindows() )
                {
                    Common.runUnansiCmd( cmd, path );
                }
                else
                {
                    Common.runSimpleCmd( cmd, path );
                }
            }
            else
            {
                path = SetUp.getOriginalDownloadDirectory() + title + Common.getSlash();

                if ( Common.isWindows() )
                {
                    Common.debugPrintln( "開啟命令：" + cmd + " " + path );
                    Common.runUnansiCmd( cmd, path );
                }
                else
                {
                    Common.debugPrintln( "開啟命令：" + cmd + " " + path );

                    Common.runCmd( cmd, path, false );
                }
            }

        }
        else
        {
            if ( Common.isWindows() )
            {
                Common.runUnansiCmd( SetUp.getOpenPicFileProgram(),
                                     SetUp.getOriginalDownloadDirectory() + title + Common.getSlash() );
            }
            else
            {
                Common.runCmd( SetUp.getOpenPicFileProgram(),
                               SetUp.getOriginalDownloadDirectory() + title, false );
            }
        }

    }

    private void openDownloadDirectory( int row )
    {  // 開啟第row列任務的下載資料夾
        String title = "";
        String url = "";

        if ( tabbedPane.getSelectedIndex() == TabbedPaneEnum.MISSION )
        { // 從任務清單開啟
            row = downTable.convertRowIndexToModel( row ); // 顯示的列 -> 實際的列
            title = String.valueOf( downTableModel.getRealValueAt( row, DownTableEnum.TITLE ) );
            url = downTableModel.getRealValueAt( row, DownTableEnum.URL ).toString();
        }
        else if ( tabbedPane.getSelectedIndex() == TabbedPaneEnum.BOOKMARK )
        { // 從書籤清單開啟
            row = bookmarkTable.convertRowIndexToModel( row ); // 顯示的列 -> 實際的列
            title = String.valueOf( bookmarkTableModel.getValueAt( row, BookmarkTableEnum.TITLE ) );
            url = String.valueOf( bookmarkTableModel.getValueAt( row, BookmarkTableEnum.URL ) );
        }
        else if ( tabbedPane.getSelectedIndex() == TabbedPaneEnum.RECORD )
        { // 從記錄清單開啟
            row = recordTable.convertRowIndexToModel( row ); // 顯示的列 -> 實際的列
            title = String.valueOf( recordTableModel.getValueAt( row, RecordTableEnum.TITLE ) );
            url = String.valueOf( recordTableModel.getValueAt( row, RecordTableEnum.URL ) );
        }

        Common.debugPrintln( "開啟" + title + "的下載資料夾" );
        Common.debugPrintln( "符合條件: " + url );

        if ( url.matches( "(?s).*e-hentai(?s).*" ) || url.matches( "(?s).*exhentai(?s).*" ) /*
                 // 因為這個url取的是所有集數的位址串連... || ( url.matches(
                 "(?s).*eyny.com(?s).*" ) && url.split( "eyny\\.com" ).length <
                 3 ) || ( url.matches( "(?s).*ck101.com(?s).*" ) && url.split(
                 "ck101\\.com" ).length < 3 ) || url.matches(
                 "(?s).*catcatbox.com(?s).*" ) || url.matches(
                 "(?s).*mybest.com(?s).*" ) || url.matches(
                 "(?s).*wenku.com(?s).*" )
                 */ )
        {
            if ( Common.isWindows() )
            {
                if ( new File( SetUp.getOriginalDownloadDirectory() + title + ".zip" ).exists() )
                {
                    // 開啟資料夾並將指定的檔案反白
                    Common.runUnansiCmd( "explorer /select, ", SetUp.getOriginalDownloadDirectory() + title + ".zip" );
                }
                else if ( new File( SetUp.getOriginalDownloadDirectory() + title + ".cbz" ).exists() )
                {
                    // 開啟資料夾並將指定的檔案反白
                    Common.runUnansiCmd( "explorer /select, ", SetUp.getOriginalDownloadDirectory() + title + ".cbz" );
                }
                else if ( new File( SetUp.getOriginalDownloadDirectory() + title + ".txt" ).exists() )
                {
                    // 開啟資料夾並將指定的檔案反白
                    Common.runUnansiCmd( "explorer /select, ", SetUp.getOriginalDownloadDirectory() + title + ".txt" );
                }
                else if ( new File( SetUp.getOriginalDownloadDirectory() + title + ".html" ).exists() )
                {
                    // 開啟資料夾並將指定的檔案反白
                    Common.runUnansiCmd( "explorer /select, ", SetUp.getOriginalDownloadDirectory() + title + ".html" );
                }
                else if ( new File( SetUp.getOriginalDownloadDirectory() + title + Common.getSlash() ).exists() )
                {
                    // 開啟資料夾並將指定的資料夾反白
                    Common.runUnansiCmd( "explorer /select, ", SetUp.getOriginalDownloadDirectory() + title );
                }
                else
                {
                    Common.runUnansiCmd( "explorer ", SetUp.getOriginalDownloadDirectory() );
                }
            }
            else if ( Common.isMac() )
            {
                Common.runCmd( "open", SetUp.getOriginalDownloadDirectory(), true );
            }
            else
            {
                Common.runCmd( "nautilus", SetUp.getOriginalDownloadDirectory(), true );
            }

        }
        else
        {
            if ( Common.isWindows() )
            {
                Common.runUnansiCmd( "explorer ", SetUp.getOriginalDownloadDirectory() + title + Common.getSlash() );
            }
            else if ( Common.isMac() )
            {
                Common.runCmd( "open", SetUp.getOriginalDownloadDirectory() + title + Common.getSlash(), true );
            }
            else
            {
                //Common.debugPrintln( "--->" + SetUp.getOriginalDownloadDirectory() + title + Common.getSlash() );
                Common.runCmd( "nautilus", SetUp.getOriginalDownloadDirectory() + title + Common.getSlash(), true );
            }
        }
    }

    private void showDownloadPopup( MouseEvent event )
    {
        downloadTablePopupRow = event.getY() / downTable.getRowHeight();
        if ( downloadTablePopupRow < Common.missionCount && downloadTablePopupRow >= 0 )
        {
            if ( event.isPopupTrigger() )
            {
                downloadTablePopup.show( event.getComponent(), event.getX(), event.getY() );
            }
        }
    }

    private void showBookmarkPopup( MouseEvent event )
    {
        bookmarkTablePopupRow = event.getY() / bookmarkTable.getRowHeight();
        if ( bookmarkTablePopupRow < Common.bookmarkCount && bookmarkTablePopupRow >= 0 )
        {
            if ( event.isPopupTrigger() )
            {
                bookmarkTablePopup.show( event.getComponent(), event.getX(), event.getY() );
            }
        }
    }

    private void showRecordPopup( MouseEvent event )
    {
        recordTablePopupRow = event.getY() / recordTable.getRowHeight();
        if ( recordTablePopupRow < Common.recordCount && recordTablePopupRow >= 0 )
        {
            if ( event.isPopupTrigger() )
            {
                recordTablePopup.show( event.getComponent(), event.getX(), event.getY() );
            }
        }
    }

    private void showUrlFieldPopup( MouseEvent event )
    {
        //if ( event.isPopupTrigger() ) {
        System.out.print( "|" );
        Common.debugPrintln( event.getX() + "," + event.getY() );
        urlFieldPopup.show( event.getComponent(), event.getX() + 15, event.getY() );
        //}
    }

    // --------------  button event   -----------------
    // 開始進行下載任務（若downloadAfterChoice為true，則先等待選擇集數完畢後，才開始下載。）
    public void startDownloadList( final boolean downloadAfterChoice )
    {
        Thread downThread = new Thread( new Runnable()
        {

            public void run()
            {
                Common.debugPrintln( "進入下載主函式中" );

                if ( downloadAfterChoice )
                {
                    Common.downloadLock = true;
                    Common.debugPrintln( "進入選擇集數，等待中..." );

                    synchronized ( ComicDownGUI.mainFrame )
                    { // lock main frame
                        while ( Common.downloadLock )
                        {
                            try
                            {
                                ComicDownGUI.mainFrame.wait();
                            }
                            catch ( InterruptedException ex )
                            {
                                Common.hadleErrorMessage( ex, "無法讓mainFrame等待（wait）" );
                            }
                        }
                    }
                    Common.debugPrintln( "選擇集數完畢，結束等待" );
                }

                for ( int i = 0; i < Common.missionCount && Run.isAlive; i++ )
                {
                    if ( downTableModel.getValueAt( i, DownTableEnum.YES_OR_NO ).toString().equals( "false" )
                            || downTableModel.getValueAt( i, DownTableEnum.STATE ).toString().equals( "下載完畢" ) )
                    {
                        Common.processPrintln( "跳過 " + downTableModel.getValueAt( i, DownTableEnum.TITLE ).toString() );
                        continue;
                    }

                    nowDownloadMissionRow = i; // 目前正在進行下載的任務列的順序

                    // ex http://xxx  http://xxx  ...
                    String[] urlStrings = Common.getSeparateStrings(
                            downTableModel.getValueAt( i, DownTableEnum.URL ).toString() );

                    // ex. true  false  false ...
                    String[] checkStrings = Common.getSeparateStrings(
                            String.valueOf( downTableModel.getRealValueAt( i, DownTableEnum.CHECKS ) ) );

                    // volume title
                    String[] volumeStrings = Common.getSeparateStrings(
                            String.valueOf( downTableModel.getRealValueAt( i, DownTableEnum.VOLUMES ) ) );

                    int downloadCount = 0;
                    for ( int j = 0; j < urlStrings.length && Run.isAlive; j++ )
                    {
                        if ( checkStrings[j].equals( "true" ) )
                        {
                            Flag.allowDownloadFlag = true;

                            String nowState = "下載進度：" + downloadCount + " / " + Common.getTrueCountFromStrings( checkStrings );
                            downTableModel.setValueAt( nowState, i, DownTableEnum.STATE );
                            // 啟動下載
                            Run mainRun = new Run( urlStrings[j], volumeStrings[j], downTableModel.getValueAt( i, DownTableEnum.TITLE ).toString(),
                                                   RunModeEnum.DOWNLOAD_MODE );
                            mainRun.run();
                            downloadCount++;
                        }
                    }

                    if ( Run.isAlive )
                    {
                        if ( Flag.downloadErrorFlag )
                        {
                            downTableModel.setValueAt( "下載錯誤", i, DownTableEnum.STATE );
                            Flag.downloadErrorFlag = false; // 歸初始值
                        }
                        else
                        {
                            downTableModel.setValueAt( "下載完畢", i, DownTableEnum.STATE );
                        }
                        String title = String.valueOf( downTableModel.getRealValueAt( i, DownTableEnum.TITLE ) );

                        if ( SetUp.getShowDoneMessageAtSystemTray() && trayIcon != null )
                        {
                            trayIcon.displayMessage( "JComicDownloader Message", title + "下載完畢! ", TrayIcon.MessageType.INFO );
                            if ( SetUp.getPlaySingleDoneAudio() )
                            {
                                Common.playSingleDoneAudio(); // 播放單一任務完成音效
                            }
                        }
                    }
                    else
                    {
                        downTableModel.setValueAt( "下載中斷", i, DownTableEnum.STATE );
                        trayIcon.setToolTip( "下載中斷" );
                    }

                    Common.outputDownTableFile( downTableModel ); // 每處理一個任務就寫出下載任務記錄檔一次
                }
                if ( Run.isAlive )
                {
                    int countOfChoiceMission = getCountOfChoiceMisssion();
                    stateBar.setText( countOfChoiceMission + "個任務全部下載完畢! " );
                    if ( SetUp.getPlayAllDoneAudio() )
                    {
                        Common.playAllDoneAudio(); // 播放全部任務完成音效
                    }
                    if ( SetUp.getRunAllDoneScript() )
                    {
                        ComicDownGUI.stateBar.setText( "全部任務完成，執行腳本中" );
                        Common.runShellScript( SetUp.getAllDoneScriptFile(), SetUp.getDownloadDirectory() ); // 執行全部任務完成腳本
                        ComicDownGUI.stateBar.setText( "腳本執行結束" );

                    }
                    if ( SetUp.getShowDoneMessageAtSystemTray() && trayIcon != null )
                    {
                        trayIcon.displayMessage( "JComicDownloader Message",
                                                 countOfChoiceMission + "個任務全部下載完畢! ", TrayIcon.MessageType.INFO );
                    }
                    trayIcon.setToolTip( "JComicDownloader" );
                    Flag.allowDownloadFlag = false;

                    System.gc(); // 下載完就建議JAVA做垃圾回收
                }
            }
        } );

        Common.downloadThread = downThread;
        downThread.start();
    }

    // 開始下載urlTextField目前位址指向的集數
    public void startDownloadURL( final String[] newArgs )
    {
        Thread downThread = new Thread( new Runnable()
        {

            public void run()
            {
                // download comic from url
                Flag.allowDownloadFlag = true; // allow the action of download
                Common.processPrintln( "開始單集下載" );
                stateBar.setText( "  開始單集下載" );
                Run singleRun = new Run( newArgs, RunModeEnum.DOWNLOAD_MODE );
                singleRun.start();
                try
                {
                    singleRun.join();
                }
                catch ( InterruptedException ex )
                {
                    Common.hadleErrorMessage( ex, "無法加入singleRun.join()" );
                }
                Flag.allowDownloadFlag = false;
            }
        } );

        Common.downloadThread = downThread;
        downThread.start();
    }

    private JTable getSelectedTable()
    {
        JTable targetTable = null;
        if ( tabbedPane.getSelectedIndex() == TabbedPaneEnum.MISSION )
        {
            targetTable = downTable;
        }
        else if ( tabbedPane.getSelectedIndex() == TabbedPaneEnum.BOOKMARK )
        {
            targetTable = bookmarkTable;
        }
        else if ( tabbedPane.getSelectedIndex() == TabbedPaneEnum.RECORD )
        {
            targetTable = recordTable;
        }

        return targetTable;
    }

    // 跳出選擇集數的視窗
    public void runChoiceFrame( final boolean modifySelected,
                                final int modifyRow, final String title, final String urlString )
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
                        setSkin( ComicDownGUI.getDefaultSkinClassName() );

                        ChoiceFrame choiceFrame;
                        if ( modifySelected )
                        {
                            choiceFrame = new ChoiceFrame(
                                    "重新選擇欲下載的集數 [" + title + "]", true, modifyRow, title, urlString );
                        }
                        else
                        {

                            choiceFrame = new ChoiceFrame( title, urlString );


                        }

                        setSkin( SetUp.getSkinClassName() );

                    }
                } );
            }
        } ).start();



        //String[] volumeStrings = choiceFrame.getVolumeStrings();
        //String[] checkStrings = choiceFrame.getCheckStrings();

        args = null;
        urlField.setText( "" );
        repaint();

    }

    private void clearMission()
    { // 清空下載任務
        int downListCount = downTableModel.getRowCount();
        while ( downTableModel.getRowCount() > 1 )
        {
            downTableModel.removeRow( downTableModel.getRowCount() - 1 );
            Common.missionCount--;
        }
        if ( Common.missionCount > 0 )
        {
            downTableModel.removeRow( 0 );
        }
        repaint(); // 重繪

        Common.missionCount = 0;
        Common.processPrint( "全部下載任務清空" );
        stateBar.setText( "全部下載任務清空" );
        trayIcon.setToolTip( "JComicDownloader" );
    }

    private void clearBookmark()
    { // 清空書籤
        int bookmarkListCount = bookmarkTableModel.getRowCount();
        while ( bookmarkTableModel.getRowCount() > 1 )
        {
            bookmarkTableModel.removeRow( bookmarkTableModel.getRowCount() - 1 );
            Common.bookmarkCount--;
        }
        if ( Common.bookmarkCount > 0 )
        {
            bookmarkTableModel.removeRow( 0 );
        }
        repaint(); // 重繪

        Common.bookmarkCount = 0;
        Common.processPrint( "全部書籤清空" );
        stateBar.setText( "全部書籤清空" );
        trayIcon.setToolTip( "JComicDownloader" );
    }

    private void clearRecord()
    {
        int recordListCount = recordTableModel.getRowCount();
        while ( recordTableModel.getRowCount() > 1 )
        {
            recordTableModel.removeRow( recordTableModel.getRowCount() - 1 );
            Common.recordCount--;
        }
        if ( Common.recordCount > 0 )
        {
            recordTableModel.removeRow( 0 );
        }
        repaint(); // 重繪

        Common.recordCount = 0;
        Common.processPrint( "全部記錄清空" );
        stateBar.setText( "全部記錄清空" );
        trayIcon.setToolTip( "JComicDownloader" );
    }
    
    
    
    // 若當前有反白任務，重新選擇集數，並回傳true, 否則回傳false
    public boolean volumeRechoiceNow()
    {
        JTable targetTable = getSelectedTable();
        for ( int i = targetTable.getRowCount() - 1; i >= 0; i-- )
        {
            if ( targetTable.isRowSelected( i ) )
            {
                //rechoiceVolume( i );
                addMission( i );
                return true;
            }
        }
        return false;
    }
    
    
    // 若當前有反白任務，以預設程式開啟此任務下載物
    public boolean viewMission()
    {
        JTable targetTable = getSelectedTable();
        for ( int i = targetTable.getRowCount() - 1; i >= 0; i-- )
        {
            if ( targetTable.isRowSelected( i ) )
            {
                //rechoiceVolume( i );
                openDownloadFile( i );
                return true;
            }
        }
        return false;
    }

    // 分析位址，分析完後開始下載。
    public void parseURL( final String[] newArgs,
                          final boolean allowDownload,
                          final boolean modifySelected,
                          final int modifyRow )
    {  // call by add button and download button
        Thread praseThread = new Thread( new Runnable()
        {

            public void run()
            {
                Common.urlIsUnknown = false; // 解決一次不認識之後就都不認識的bug
                if ( newArgs == null || newArgs[0].equals( "" ) )
                {
                    if ( !allowDownload )
                    {
                        stateBar.setText( "  沒有輸入網址 !!" );
                    }
                    else
                    {
                        if ( Common.missionCount > 0 )
                        {
                            Flag.parseUrlFlag = false; // 分析結束
                            startDownloadList( false ); // download all selected comic
                        }
                        else
                        {
                            stateBar.setText( "  沒有下載任務也沒有輸入網址 !!" );
                        }
                    }
                }
                else
                {
                    if ( !newArgs[0].matches( "http(?s).*" ) )
                    {
                        newArgs[0] = "http://" + newArgs[0]; // 檢查若不是完整網址就加上前綴http://
                    }
                    if ( !Common.isLegalURL( newArgs[0] ) )
                    { // url is illegal
                        stateBar.setText( "  網址錯誤，請輸入正確的網址 !!" );
                    }
                    else
                    {
                        stateBar.setText( "  解析網址中" );
                        if ( Common.withGUI() )
                        {
                            trayIcon.setToolTip( "解析網址中" );
                        }

                        Flag.allowDownloadFlag = false;
                        Run.isAlive = true; // open up the download work

                        String[] tempArgs = Common.getCopiedStrings( newArgs );
                        //args = null;

                        //int runMode = allowDownload ? RunModeEnum.DOWNLOAD_MODE : RunModeEnum.PARSE_MODE;
                        mainRun = new Run( tempArgs, RunModeEnum.PARSE_MODE );
                        mainRun.start();
                        String title = "";
                        try
                        {
                            mainRun.join();

                        }
                        catch ( InterruptedException ex )
                        {
                            Common.hadleErrorMessage( ex, "無法加入mainRun.join()" );
                        }

                        title = mainRun.getTitle();
                        Common.debugPrintln( "選擇集數前解析得到的title：" + title );
                        if ( Common.urlIsUnknown )
                        {
                            if ( allowDownload )
                            {

                                // 當複製位址為非支援網址，按下載後，會直接下載檔案到預定資料夾 (v5.12版新增)
                                directDownloadFile( newArgs[0] );

                                Flag.parseUrlFlag = false; // 分析結束
                                startDownloadList( false ); // 直接下載串列

                            }
                            else
                            {
                                stateBar.setText( "  無法解析此網址 !!" );
                            }
                        }
                        else if ( title == null )
                        {
                            // 避免沒有讀入集數資訊卻仍開啟集數選擇視窗。
                            stateBar.setText( "  解析失敗，無法讀入集數資訊 !!" );
                            
                            if ( !Common.isMainPage )
                            {
                                stateBar.setText( "  單集位址無法加入任務 !!" );
                            }
                        }
                        else if ( Common.isMainPage )
                        { // args is main page
                            runChoiceFrame( modifySelected, modifyRow, title, tempArgs[0] );

                            if ( allowDownload )
                            {
                                Flag.parseUrlFlag = false; // 分析結束
                                startDownloadList( true ); // download all selected comic
                            }
                        }
                        else
                        { // args is single page
                            if ( !allowDownload )
                            {
                                stateBar.setText( "  單集頁面無法加入下載佇列 !!" );
                            }
                            else
                            {
                                stateBar.setText( "  正在下載單一集數" );
                                Flag.parseUrlFlag = false; // 分析結束
                                startDownloadURL( tempArgs ); // download single comic on textfield
                            }
                        }

                    }
                }


                Flag.parseUrlFlag = false; // 分析結束
            }
        } );

        praseThread.start();
    }

    public void actionPerformed( ActionEvent event )
    {
        if ( event.getSource() == urlField )
        {
        }

        if ( event.getSource() == tableSearchDownloadComic )
        {
            searchDownloadComic( downloadTablePopupRow );
        }
        else if ( event.getSource() == tableSearchBookmarkComic )
        {
            searchDownloadComic( bookmarkTablePopupRow );
        }
        else if ( event.getSource() == tableSearchRecordComic )
        {
            searchDownloadComic( recordTablePopupRow );
        }

        if ( event.getSource() == tableOpenDownloadFile )
        {
            openDownloadFile( downloadTablePopupRow );
        }
        else if ( event.getSource() == tableOpenBookmarkFile )
        {
            openDownloadFile( bookmarkTablePopupRow );
        }
        else if ( event.getSource() == tableOpenRecordFile )
        {
            openDownloadFile( recordTablePopupRow );
        }
        if ( event.getSource() == tableOpenDownloadURL )
        {
            openDownloadURL( downloadTablePopupRow );
        }
        else if ( event.getSource() == tableOpenBookmarkURL )
        {
            openDownloadURL( bookmarkTablePopupRow );
        }
        else if ( event.getSource() == tableOpenRecordURL )
        {
            openDownloadURL( recordTablePopupRow );
        }
        else if ( event.getSource() == tableOpenDownloadDirectoryItem )
        {
            openDownloadDirectory( downloadTablePopupRow );
        }
        else if ( event.getSource() == tableOpenBookmarkDirectoryItem )
        {
            openDownloadDirectory( bookmarkTablePopupRow );
        }
        else if ( event.getSource() == tableOpenRecordDirectoryItem )
        {
            openDownloadDirectory( recordTablePopupRow );
        }

        if ( event.getSource() == pasteSystemClipboardItem )
        { // 貼上剪貼簿網址
            String clipString = new SystemClipBoard().getClipString();
            urlField.setText( clipString );
        }

        if ( event.getSource() == tableAddBookmarkFromDownloadItem )
        {
            addBookmark( downloadTablePopupRow );
        }
        else if ( event.getSource() == tableAddBookmarkFromRecordItem )
        {
            addBookmark( recordTablePopupRow );
        }

        if ( event.getSource() == tableAddMissionFromBookmarkItem )
        {
            addMission( bookmarkTablePopupRow );
        }
        else if ( event.getSource() == tableAddMissionFromRecordItem )
        {
            addMission( recordTablePopupRow );
        }

        if ( event.getSource() == tableRechoiceVolumeItem )
        {
            if ( !Flag.downloadingFlag )
            {
                rechoiceVolume( downloadTablePopupRow );
            }
            else
            {
                CommonGUI.showMessageDialog( this, "目前正下載中，無法重新選擇集數",
                                             "提醒訊息", JOptionPane.INFORMATION_MESSAGE );
            }
        }
        if ( event.getSource() == tableRenameTitleItem )
        {
            renameTitle( downloadTablePopupRow );
        }

        if ( event.getSource() == tableDeleteMissionItem )
        {
            deleteMission( downloadTablePopupRow );
        }
        else if ( event.getSource() == tableDeleteSelectedMissionItem )
        {
            // 刪除所有反白任務
            for ( int i = downTable.getRowCount(); i >= 0; i-- )
            {
                if ( downTable.isRowSelected( i ) )
                {
                    deleteMission( i );
                }
            }
        }
        else if ( event.getSource() == tableDeleteAllUnselectedMissionItem )
        {
            if ( !Flag.downloadingFlag )
            {
                deleteAllUnselectedMission();
            }
            else
            {
                CommonGUI.showMessageDialog( this, "目前正下載中，無法刪除任務",
                                             "提醒訊息", JOptionPane.INFORMATION_MESSAGE );
            }
        }
        else if ( event.getSource() == tableDeleteAllDoneMissionItem )
        {
            if ( !Flag.downloadingFlag )
            {
                deleteAllDoneMission();
            }
            else
            {
                CommonGUI.showMessageDialog( this, "目前正下載中，無法刪除任務",
                                             "提醒訊息", JOptionPane.INFORMATION_MESSAGE );
            }
        }
        else if ( event.getSource() == tableDeleteFileItem )
        {
            deleteMissionAndFile( downloadTablePopupRow );
        }

        if ( event.getSource() == tableMoveToRoofItem )
        {
            moveMissionUp( downTable, downloadTablePopupRow, true ); // 置頂

        }
        else if ( event.getSource() == tableMoveToFloorItem )
        {
            moveMissionDown( downTable, downloadTablePopupRow, true ); // 置底
        }
        else if ( event.getSource() == tableSelectedMoveToRoofItem )
        {
            // 所有反白任務置頂
            for ( int i = 0; i < downTable.getRowCount(); i++ )
            {
                if ( downTable.isRowSelected( i ) )
                {
                    moveMissionUp( downTable, i, true );
                }
            }
        }
        else if ( event.getSource() == tableSelectedMoveToFloorItem )
        {
            // 所有反白任務置底 
            for ( int i = downTable.getRowCount() - 1; i >= 0; i-- )
            {
                if ( downTable.isRowSelected( i ) )
                {
                    moveMissionDown( downTable, i, true );
                }
            }
        }
        else if ( event.getSource() == tableDeleteBookmarkItem )
        {
            deleteBookmark( bookmarkTablePopupRow );
        }
        else if ( event.getSource() == tableDeleteMissionRecordItem )
        {
            deleteRecord( recordTablePopupRow );
        }
        else if ( event.getSource() == tableDeleteFileRecordItem )
        {
            deleteFileFromRecord( recordTablePopupRow );
        }

        if ( event.getSource() == trayShowItem )
        {
            setVisible( true );
            setState( Frame.NORMAL );
            SystemTray.getSystemTray().remove( trayIcon );
        }

        if ( event.getSource() == button[ButtonEnum.ADD] )
        { // button of add
            String urlString = urlField.getText();
            Common.debugPrintln( "urlString = " + urlString );

            logFrame.redirectSystemStreams(); // start to log message
            testDownload(); // 測試下載是否正常

            //int rgb = new Color( 155 ).getRed();

            //Common.debugPrintln( Color.PINK.toString()  );
            //Common.debugPrintln( Common.getColor(Color.PINK.toString() ).toString());


            parseURL( args, false, false, 0 );
            args = null;
            
            if ( urlString.equals( "" ) || urlString.matches( "請(?).*" ) )
            {
                volumeRechoiceNow();
            }

            // 每加一個任務就紀錄一次。
            Common.outputRecordTableFile( recordTableModel );
        }
        if ( event.getSource() == button[ButtonEnum.DOWNLOAD]
                || event.getSource() == trayStartItem )
        { // button of Download
            if ( Flag.downloadingFlag || Flag.parseUrlFlag )
            { // 目前正分析網址或下載中，不提供直接下載服務
                CommonGUI.showMessageDialog( this,
                                             "目前正下載中，不提供直接下載，請按「加入」來加入下載任務。", "提醒訊息",
                                             JOptionPane.INFORMATION_MESSAGE );
            }
            else
            {
                logFrame.redirectSystemStreams(); // start to log message
                Run.isAlive = true;
                stateBar.setText( "開始下載中..." );
                tabbedPane.setSelectedIndex( TabbedPaneEnum.MISSION ); // 按下載就會回到下載任務頁面
                Flag.parseUrlFlag = true; // 開始分析
                parseURL( args, true, false, 0 );
                args = null;
            }
        }
        if ( event.getSource() == button[ButtonEnum.UP] )
        { // button of UP
            JTable targetTable = getSelectedTable();
            String targetName = getTableName( targetTable );

            boolean existSelectedRow = false;

            if ( targetTable.isRowSelected( 0 ) )
            {
                stateBar.setText( "反白" + targetName + "已置頂，無法再往上提" );
            }
            else
            {

                // 所有反白任務往上提一個順位
                for ( int i = 0; i < targetTable.getRowCount(); i++ )
                {
                    if ( targetTable.isRowSelected( i ) )
                    {
                        existSelectedRow = true;

                        moveMissionUp( targetTable, i, false );


                    }
                }

                if ( targetTable.getRowCount() < 1 )
                {
                    stateBar.setText( "沒有任何" + targetName + "！" );
                }
                if ( existSelectedRow )
                {
                    stateBar.setText( "提升反白" + targetName );
                }
                else
                {
                    stateBar.setText( "請將欲提升的" + targetName + "反白！" );
                }
            }
        }
        if ( event.getSource() == button[ButtonEnum.DOWN] )
        { // button of DOWN
            JTable targetTable = getSelectedTable();
            String targetName = getTableName( targetTable );

            boolean existSelectedRow = false;

            if ( targetTable.isRowSelected( targetTable.getRowCount() - 1 ) )
            {
                stateBar.setText( "反白" + targetName + "已置底，無法再往下降" );
            }
            else
            {

                // 所有反白任務往上提一個順位
                for ( int i = targetTable.getRowCount() - 1; i >= 0; i-- )
                {
                    if ( targetTable.isRowSelected( i ) )
                    {
                        existSelectedRow = true;

                        moveMissionDown( targetTable, i, false );
                    }
                }

                if ( targetTable.getRowCount() < 1 )
                {
                    stateBar.setText( "沒有任何" + targetName + "！" );
                }
                if ( existSelectedRow )
                {
                    stateBar.setText( "沉降反白" + targetName + "" );
                }
                else
                {
                    stateBar.setText( "請將欲沉降的" + targetName + "反白！" );
                }
            }
        }
        if ( event.getSource() == button[ButtonEnum.STOP]
                || event.getSource() == trayStopItem )
        { // button of stop
            Run.isAlive = false; // forbid download work
            Flag.allowDownloadFlag = Flag.downloadingFlag = Flag.parseUrlFlag = false;
            stateBar.setText( "所有下載任務停止" );
            trayIcon.setToolTip( "JComicDownloader" );
            stateBar.setText( "所有下載任務停止" );

        }
        if ( event.getSource() == button[ButtonEnum.OPTION] )
        { // button of Option
            // 用javax.swing.SwingUtilities.invokeLater反而很頓......

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
                                setSkin( ComicDownGUI.getDefaultSkinClassName()  );
                            }
                            else
                            {
                                setSkin( SetUp.getSkinClassName()  );
                            }
                            new OptionFrame();
                            Common.debugPrintln( "------------" );
                            setSkin( SetUp.getSkinClassName() );

                        }
                    } );
                }
            } ).start();

            //CommonGUI.newFrameStartInEDT( "jcomicdownloader.frame.OptionFrame", true );

        }
        if ( event.getSource() == button[ButtonEnum.INFORMATION] )
        { // button of Information
            
            // 若有反白任務，則嘗試開啟此任務的漫畫，若成功，就不再開啟資訊視窗。
            if ( viewMission() )
            {
                return;
            }
            
            new Thread( new Runnable()
            {

                public void run()
                {
                    SwingUtilities.invokeLater( new Runnable()
                    {

                        public void run()
                        {
                            // 避免在NapKin Look and Feel下發生錯誤( JRE 7的問題)
                            setSkin( ComicDownGUI.getDefaultSkinClassName()  );
                            new InformationFrame();
                            setSkin( SetUp.getSkinClassName() );
                        }
                    } );
                }
            } ).start();

            //CommonGUI.newFrameStartInEDT( "jcomicdownloader.frame.InformationFrame", true );

        }
        if ( event.getSource() == button[ButtonEnum.CLEAR] )
        { // button of CLEAR
            int choice = CommonGUI.showConfirmDialog( this, "請問是否要將目前內容全部清空？",
                                                      "clean all items ?",
                                                      "提醒訊息", JOptionPane.YES_NO_OPTION );

            if ( choice == JOptionPane.YES_OPTION )
            {
                if ( tabbedPane.getSelectedIndex() == TabbedPaneEnum.MISSION )
                {
                    clearMission();
                }
                else if ( tabbedPane.getSelectedIndex() == TabbedPaneEnum.BOOKMARK )
                {
                    clearBookmark();
                }
                else if ( tabbedPane.getSelectedIndex() == TabbedPaneEnum.RECORD )
                {
                    clearRecord();
                }
            }

        }
        if ( event.getSource() == button[ButtonEnum.EXIT]
                || event.getSource() == trayExitItem )
        { // button of Exit
            int choice = CommonGUI.showConfirmDialog( this, "請問是否要關閉JComicDownloader？",
                                                      "exit JComicDownloader ?",
                                                      "提醒訊息", JOptionPane.YES_NO_OPTION );

            if ( choice == JOptionPane.YES_OPTION )
            {
                // 輸出下載任務清單，下次開啟時會自動載入

                exit();
            }
        }
    }

    // 結束程式之前要做的事情
    public static void exit()
    {
        SetUp.writeSetFile(); // 將目前的設定存入設定檔（set.ini）

        Common.outputDownTableFile( downTableModel );
        Common.outputBookmarkTableFile( bookmarkTableModel );
        Common.outputRecordTableFile( recordTableModel );

        Run.isAlive = false;
        Common.debugPrintln( "刪除所有暫存檔案" );
        Common.deleteFolder( SetUp.getTempDirectory() ); // 刪除暫存檔
        Common.debugPrintln( "Exit JComicDownloader ... " );

        System.exit( 0 );
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

    // 取得目前下載任務清單中的已勾選任務總數
    private int getCountOfChoiceMisssion()
    {
        int count = 0;
        for ( int i = 0; i < Common.missionCount; i++ )
        {
            if ( downTableModel.getValueAt( i, DownTableEnum.YES_OR_NO ).toString().equals( "true" ) )
            {
                count++;
            }
        }
        Common.debugPrintln( "目前已勾選的任務總數：" + count );

        return count;
    }

    //addWindowListener(new WindowAdapter(){
    private class RowListener implements ListSelectionListener
    {

        public void valueChanged( ListSelectionEvent event )
        {
            /*
             replace this with mouse event if (event.getValueIsAdjusting()) {
             return; } if ( Flag.downloadFlag ) return;

             int row = downTable.getSelectionModel().getLeadSelectionIndex();
             int col =
             downTable.getColumnModel().getSelectionModel().getLeadSelectionIndex();
             //Common.debugPrintln( col );

             if ( row > 0 && col != 0 && col != 1 && col != 6 ) {
             //Common.debugPrintln( row + " : " + downTableUrlStrings[row] );

             ComicDownGUI.nowSelectedCheckStrings = Common.getSeparateStrings(
             String.valueOf( downTableModel.getRealValueAt( row,
             DownTableEnum.CHECKS ) ) );

             parseURL( new String[]{downTableUrlStrings[row]}, false, true, row
             ); }
             */
        }
    }

    // 取得自訂搜尋引擎的關鍵字搜尋頁面網址
    private String getKeywordSearchURL( String keyword )
    {
        String url = "http://www.google.com/cse?cx=002948535609514911011%3Als5mhwb6sqa&ie=UTF-8&q="
                + keyword
                + "&sa=%E6%90%9C%E5%B0%8B&hl=zh-TW&siteurl=www.google.com%2Fcse%2Fhome%3Fcx%3D002948535609514911011%3Als5mhwb6sqa%26hl%3Dzh-TW#gsc.tab=0&gsc.q="
                + keyword
                + "&gsc.page=1";

        return Common.getFixedChineseURL( url );
    }

    // 以code頁面記錄開啟次數（好玩測試看看）
    private void counter()
    {
        Thread counterThread = new Thread( new Runnable()
        {

            public void run()
            {
                try
                {
                    Thread.sleep( 3000 ); // 先等三秒
                }
                catch ( InterruptedException ex )
                {
                    Common.hadleErrorMessage( ex, "無法等待預定秒數" );
                }

                String counterURL = "http://jcomicdownloader.googlecode.com/files/count.txt";

                //Common.downloadFileByForce( counterURL, SetUp.getTempDirectory(), "counter.txt", false, null );
                //ComicDownGUI.stateBar.setText( "請貼上網址" );

                Common.urlIsOK( counterURL );
            }
        } );
        counterThread.start();
    }

    private JButton getButton( String string, String enString, String toolTip, String picName, int keyID )
    {
        string = Common.getStringUsingDefaultLanguage( string, enString ); // 使用預設語言 
        JButton button = new JButton( string, new CommonGUI().getImageIcon( picName ) );
        CommonGUI.setToolTip( button, toolTip + "  快捷鍵: Alt + " + KeyEvent.getKeyText( keyID ) );

        button.setFont( SetUp.getDefaultFont( 5 ) );

        if ( SetUp.getUsingBackgroundPicOfMainFrame() )
        { // 若設定為透明，就用白色字體。
            button.setForeground( SetUp.getMainFrameOtherDefaultColor() );
            button.setOpaque( false );
        }

        button.setMnemonic( keyID ); // 設快捷建為Alt + keyID

        return button;
    }

    private JMenuItem getMenuItem( String text, String enText, Icon icon )
    {
        text = Common.getStringUsingDefaultLanguage( text, enText ); // 使用預設語言 

        JMenuItem menuItem = new JMenuItem( text, icon );
        menuItem.addActionListener( this );

        if ( SetUp.getUsingBackgroundPicOfMainFrame() )
        { // 若設定為透明，就用預定字體。
            menuItem.setForeground( SetUp.getMainFrameMenuItemDefaultColor() );
            //menuItem.addMouseListener( this );
            //menuItem.setOpaque( true );
        }

        if ( SetUp.getSkinClassName().matches( ".*napkin.*" ) )
        {
            // 因為napkin的預設字型不太清楚，所以用選定字型
            menuItem.setFont( SetUp.getDefaultFont( - 3 ) );
        }

        return menuItem;
    }

    public static String fromCharCode( int... codePoints )
    {
        StringBuilder builder = new StringBuilder( codePoints.length );
        for ( int codePoint : codePoints )
        {
            builder.append( Character.toChars( codePoint ) );
        }
        return builder.toString();
    }

    private void setDefaultRenderer( JTable table, DefaultTableModel tableModel )
    { // 設置volumeTable上哪些集數要變色
        DownTableRender cellRender = new DownTableRender( tableModel, FrameEnum.MAIN_FRAME );
        try
        {
            table.setDefaultRenderer( Class.forName( "java.lang.Object" ), cellRender );
        }
        catch ( ClassNotFoundException ex )
        {
            Common.hadleErrorMessage( ex, "無法將table轉型" );
        }
    }

    // 當複製位址為非支援網址，按下載後，會直接下載檔案到預定資料夾 (v5.12版新增)
    public void directDownloadFile( String url )
    {

        Common.sleep( 3000, "無法解析此網址! 簡易檔案下載模式啟動倒數: " );

        int beginIndex = url.lastIndexOf( "/" ) + 1;
        int endIndex = url.length();
        String fileName = url.substring( beginIndex, endIndex ).split( "\\?" )[0];
        fileName = Common.getReomvedUnnecessaryWord( fileName );

        Common.downloadFile( url, SetUp.getDownloadDirectory(), fileName, false, "" );
    }

    // 編譯為jar檔的前置作業(主要是修改設定檔，使輸出檔名與版本一致)
    private void buildPreprocess()
    {
        String filePath = Common.getNowAbsolutePath() + "nbproject" + Common.getSlash();
        String fileName = "project.properties";
        File projectXmlFile = new File( filePath + fileName );

        // 若此檔存在，代表在開發階段開啟，可預先設定未來要輸出的jar檔名
        if ( projectXmlFile.exists() )
        {
            Common.debugPrintln( "此為開發階段，設定" + fileName + "裡的檔名" );
            String allPageString = Common.getFileString( filePath, fileName );
            allPageString = allPageString.replaceAll( "JComicDownloader.*\\.jar", Common.getThisFileName() );
            //Common.debugPrintln( allPageString );
            Common.outputFile( allPageString, filePath, fileName );
        }
    }

    public void testDownload()
    {
        Thread downThread = new Thread( new Runnable()
        {

            public void run()
            {

                Run.isAlive = true;

                String picURL = "http://6manga.com/comics/11/08/30/16/42344001.jpg";
                String pageURL = "http://6manga.com/comics/11/08/30/16/42344001.jpg";
                String testURL = "http://www.fumanhua.com/images/pic_loading.gif";

                String referURL = "http://6manga.com/page/comics/7/0/418_ibitsu.html";
                String cookie = "";
                String postString = "";
                //pageURL = Common.getFixedChineseURL( pageURL );

                //Common.downloadFile( picURL, "", "test.jpg", false, "", referURL );
                //Common.downloadFile( pageURL, "", "test1.html", false, "", "" );

                //Common.debugPrintln( cookie );

                //Common.simpleDownloadFile( picURL, "", "test.jpg", cookie, referURL );
                //Common.downloadGZIPInputStreamFile( testURL, SetUp.getTempDirectory(), "test.ext", false, "" );

                Common.simpleDownloadFile( picURL, "", "test2.jpg", cookie, referURL );
                //Common.downloadFile( picURL, "", "test.jpg", false, "", referURL );
                
                //Common.downloadPost( testURL, "", "test.jpg", true, cookie, "", "" );

                //Common.testConnection( testURL );

                //setMp3Tag();

                String location = "8h2xt2%141tFi%F2741t%a25F7_.p2mF5%13m%Fi26549p3f.15E573A1n61181%.e%4_11";
                //location = "8h2xt622265tFi%%6%5_Et%a22%2643p2mFF252%5%Fi32F225.3f.51%%%EmA1n575552p%.e41EEE%3";
                //testURL = getMusicURL( location );
                //Common.debugPrintln( "解析的URL = " + testURL );

                //Common.simpleDownloadFile( testURL, "", "test.mp3", cookie, referURL );

                int n = 'z' - 'a';
                //Common.debugPrintln( "ANS: " + n );
                
                Common.debugPrintln( "OVER" );



            }
        } );
        //downThread.start();
    }

    /*
     public void setMp3Tag()
     {
     AudioFile f;

     try
     {
     f = AudioFileIO.read( new File( "03.荷裡活.mp3" ) );

     Tag tag = f.getTag();

     if ( tag == null ) {
     Common.debugPrintln( "原始音樂檔沒有標籤，需重新製作" );
     tag = new ID3v23Tag();
     }

     Artwork art = StandardArtwork.createArtworkFromFile( new File( "123.jpg" ));
     //art.setFromFile( new File( "123.jpg" ) );
     tag.createField( art );
     //tag.setField( art );
     tag.setField( FieldKey.ARTIST, "梁靜茹" );
     tag.setField( FieldKey.ALBUM, "日久見人心專輯" );
     tag.setField( FieldKey.TITLE, "日久見人心" );
     f.setTag( tag );

     try
     {
     f.commit();
     Common.debugPrintln( "寫入完成 !");
     //f.setTag( tag );
     }
     catch ( CannotWriteException ex )
     {
     Logger.getLogger( ComicDownGUI.class.getName() ).log( Level.SEVERE, null, ex );
     }

     }
     catch ( CannotReadException ex )
     {
     Logger.getLogger( ComicDownGUI.class.getName() ).log( Level.SEVERE, null, ex );
     }
     catch ( IOException ex )
     {
     Logger.getLogger( ComicDownGUI.class.getName() ).log( Level.SEVERE, null, ex );
     }
     catch ( org.jaudiotagger.tag.TagException ex )
     {
     Logger.getLogger( ComicDownGUI.class.getName() ).log( Level.SEVERE, null, ex );
     }
     catch ( ReadOnlyFileException ex )
     {
     Logger.getLogger( ComicDownGUI.class.getName() ).log( Level.SEVERE, null, ex );
     }
     catch ( InvalidAudioFrameException ex )
     {
     Logger.getLogger( ComicDownGUI.class.getName() ).log( Level.SEVERE, null, ex );
     }

     }
     */
    
}
