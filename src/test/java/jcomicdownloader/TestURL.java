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
import org.junit.*;

/**
 *
 * @author apple
 */
@RunWith(value=Parameterized.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestURL {
   
    @Parameterized.Parameters
    public static Collection<Object[]> getParameters() {
        return Arrays.asList(
            new Object[][] {               
                 {0,"http://www.comicvip.com/html/9718.html","jcomicdownloader.module.ParseEC"}
                ,{1,"http://manhua.dmzj.com/ganwumeixiaomai/","jcomicdownloader.module.Parse178"}
                ,{2,"http://comic.sfacg.com/HTML/GWMXM/","jcomicdownloader.module.ParseSF"}
                ,{3,"http://comic.kukudm.com/comiclist/1842/index.htm","jcomicdownloader.module.ParseKUKU"}
                ,{4,"http://comic.ck101.com/comic/18181","jcomicdownloader.module.ParseCK"}
                ,{5,"http://www.tuku.cc/comic/11977/","jcomicdownloader.module.ParseTUKU"}               
            }
        );
    }
    private final String url,mod;
    private ParseOnlineComicSite s;

    public TestURL(Integer i,String url, String mod) {
        this.url = url;
        this.mod = mod;;        
        Run.isAlive=true;// tweak for no download
        Common.debugPrintln("-----------------"+i+"--------------------");
        Common.consoleThreadName= Thread.currentThread().getName(); // tweak for no GUI
        try{
            this.s=(ParseOnlineComicSite)Class.forName(mod).newInstance(); 
            this.s.setURL(url);// will fail if not set in some case            
        }catch(Exception x){
            this.s=null;
            fail("Module "+mod+" has exception "+x.getMessage());           
        }
    }
        
    @org.junit.Test
    public void test001Url() {
        assertTrue(mod,s.canParserHandle(url));
    }
    
    @org.junit.Test
    public void test002setParameters() {
        try{
            s.setParameters();
        }catch(Exception e){
            e.printStackTrace();
            fail(e.toString()+ " : "+e.getMessage());
        }
    }
    
    @org.junit.Test
    public void test003getAllPageString() {    
        String allPageString = s.getAllPageString(url);
        assertNotNull(allPageString);
    }
    
    @org.junit.Test
    public void test004getTitleOnMainPage() {
        String allPageString = s.getAllPageString(url); 
        String title=s.getTitleOnMainPage(url, allPageString);
        System.out.println("test004getTitleOnMainPage : "+title);
        assertNotNull(title );
    }
}