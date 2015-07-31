/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jcomicdownloader;

import static org.junit.Assert.*;
import java.util.*;
import jcomicdownloader.module.*;
import jcomicdownloader.tools.Common;
import org.junit.Assert;
import org.junit.runner.*;
import org.junit.runners.*;

/**
 *
 * @author apple
 */
@RunWith(value=Parameterized.class)
public class TestURL {
   
    @Parameterized.Parameters
    public static Collection<Object[]> getParameters() {
        return Arrays.asList(
            new Object[][] {
                {1,"http://www.comicvip.com/html/9718.html","jcomicdownloader.module.ParseEC"},
                {2,"http://manhua.dmzj.com/ganwumeixiaomai/","jcomicdownloader.module.Parse178"},
                {3,"http://comic.sfacg.com/HTML/GWMXM/","jcomicdownloader.module.ParseSF"},
                {4,"http://comic.kukudm.com/comiclist/1842/index.htm","jcomicdownloader.module.ParseKUKU"},
                {5,"http://comic.ck101.com/comic/18181","jcomicdownloader.module.ParseCK"},
                {6,"http://www.tuku.cc/comic/11977/","jcomicdownloader.module.ParseTUKU"}
            }
        );
    }
    private final String url,mod;
    private ParseOnlineComicSite s;

    public TestURL(Integer i,String url, String mod) {
        this.url = url;
        this.mod = mod;;        
        try{
            this.s=(ParseOnlineComicSite)Class.forName(mod).newInstance(); 
        }catch(Exception x){
            this.s=null;
            fail("Module "+mod+" has exception "+x.getMessage());           
        }
    }
        
    @org.junit.Test
    public void testUrl() {
        assertTrue(mod,s.canParserHandle(url)); 
    }
    
    @org.junit.Test
      public void testAllPageString(){
          String str ;
                      Common.debugPrintln("Test URL : "+url);

          str= s.getAllPageString(url) ;
          Assert.assertNotNull(mod, str);
    }
      
    @org.junit.Test
    public void testGetTitle(){
        Assert.assertNotNull(mod, s.getTitleOnMainPage(url,s.getAllPageString(url)));        
    }
    
    @org.junit.Test
    public void testSetParamters() {
        boolean b = true;
        try{
            s.setParameters();
            assertTrue(b);
        }catch(Exception x){
            fail("Module "+mod+" has exception "+x.getMessage());           
        }
    }

}
