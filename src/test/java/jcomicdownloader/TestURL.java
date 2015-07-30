/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jcomicdownloader;

import static org.junit.Assert.*;
import java.util.*;
import jcomicdownloader.module.*;
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
                {"http://www.comicvip.com/html/9718.html","jcomicdownloader.module.ParseEC"},
                {"http://manhua.dmzj.com/ganwumeixiaomai/","jcomicdownloader.module.Parse178"},
                {"http://comic.sfacg.com/HTML/GWMXM/","jcomicdownloader.module.ParseSF"},
                {"http://comic.kukudm.com/comiclist/1842/index.htm","jcomicdownloader.module.ParseKUKU"},
                {"http://comic.ck101.com/comic/18181","jcomicdownloader.module.ParseCK"}
            }
        );
    }
    private final String url,mod;
        
    public TestURL(String url, String mod) {
        this.url = url;
        this.mod = mod;
    }
        
    @org.junit.Test
    public void testUrl() {
        ParseOnlineComicSite s;
        boolean b =false;
        try{
            s=(ParseOnlineComicSite)Class.forName(mod).newInstance(); 
            b=s.canParserHandle(url);
            assertTrue(mod,b); 
        }catch(Exception x){
            fail("Module "+mod+" has exception "+x.getMessage());           
        }
    }
}
