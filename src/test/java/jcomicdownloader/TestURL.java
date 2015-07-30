/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jcomicdownloader;

import static org.junit.Assert.*;
import java.util.*;
import jcomicdownloader.module.*;
import org.junit.*;
import org.junit.runner.*;
import org.junit.runners.*;

/**
 *
 * @author apple
 */
@RunWith(value=Parameterized.class)

public class TestURL {
   
    @Parameterized.Parameters
    public static Collection<String[]> getParameters() {
        return Arrays.asList(
            new String[][] {
                {"http://www.comicvip.com/html/9718.html","EC","jcomicdownloader.module.ParseEC"},
                {"http://manhua.dmzj.com/ganwumeixiaomai/","178","jcomicdownloader.module.Parse178"},
                {"http://comic.sfacg.com/HTML/GWMXM/","SF","jcomicdownloader.module.ParseSF"},
                {"http://comic.kukudm.com/comiclist/1842/index.htm","KUKU","jcomicdownloader.module.ParseKUKU"},
                {"http://comic.ck101.com/comic/18181","CK","jcomicdownloader.module.ParseCK"}
            }
        );
    }
    private ParseOnlineComicSite s;
    private String url,name,mod;
        
    public TestURL(String url, String name, String mod) {
        this.url = url;
        this.name = name;
        this.mod = mod;
    }
        
    @org.junit.Test
    public void testUrl() {
        boolean b =false;
        try{
            s=(ParseOnlineComicSite)Class.forName(mod).newInstance(); 
            b=s.canParserHandle(url);
        }catch(Exception x){}
        assertEquals(name, true,b); 
    }
}
