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
import org.junit.runner.*;
import org.junit.runners.*;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.rules.*;

/**
 *
 * @author apple
 */
@RunWith(value=Parameterized.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestURL {
    
    private final String url, mod;
    private ParseOnlineComicSite s;
    
    
    @Rule
    public TestRule watcher = new TestWatcher() {
        protected void starting(Description description) {
            System.out.println("Starting test: " + description.getMethodName());
        }
        @Override
        protected void failed(Throwable e, Description description) {
            System.out.println("Test fail: "  + mod +"\t"+ description.getMethodName() + "\n");
        }

        @Override
        protected void succeeded(Description description) {
            System.out.println("Test success: " +  mod +"\t"+ description.getMethodName() + "\n");
        }
    };

    @Parameterized.Parameters
    public static Collection<Object[]> getParameters() {
        return Arrays.asList(
                new Object[][] {        //uncomment which site for test
//                  {0,"http://www.comicvip.com/html/9718.html","jcomicdownloader.module.ParseEC"}
//                 ,
//                      {1,"http://manhua.dmzj.com/ganwumeixiaomai/","jcomicdownloader.module.Parse178"}
//                 ,
//                  {2,"http://comic.sfacg.com/HTML/GWMXM/","jcomicdownloader.module.ParseSF"}
//                 ,
//                  {3,"http://comic.kukudm.com/comiclist/1842/index.htm","jcomicdownloader.module.ParseKUKU"}
//                 ,
//                  {4,"http://comic.ck101.com/comic/18181","jcomicdownloader.module.ParseCK"}
//                 ,
//                    {5,"http://www.tuku.cc/comic/11977/","jcomicdownloader.module.ParseTUKU"}
//                 ,
//                {6,"http://www.dm5.com/manhua-kissxdeath/","jcomicdownloader.module.ParseDM5"}
//                    ,
//                    {7,"https://tieba.baidu.com/p/4941848745","jcomicdownloader.module.ParseBAIDU"}
//                        ,
//                        {8,"https://tieba.baidu.com/p/4978124556","jcomicdownloader.module.ParseBAIDU"}

                }
        );
    }


    public TestURL(Integer i,String url, String mod) {
        this.url = url;
        this.mod = mod;

        noGuiTweak:{ //just a label
            Run.isAlive=true;// tweak for no download
            Common.consoleThreadName= Thread.currentThread().getName();
        }

        Common.debugPrintln("-----------------"+i+"--------------------");
        try{
            this.s=(ParseOnlineComicSite)Class.forName(mod).newInstance();
            ignoreBug_001:{// will fail if not set in some case
                this.s.setURL(url);
            }
        }catch(Exception x){
            s=null;
            fail("Module "+mod+" has exception : "+x.getMessage());
        }
    }

    //@org.junit.Ignore
    @org.junit.Test
    public void test000Run() {
        RunModule r =new RunModule();
//        try{
        r.runMainProcess(s, url);
//        }catch(Exception e){
//         fail(e.toString());
//        }
    }

    //@org.junit.Ignore
    @org.junit.Test
    public void test001Url() {
        assertTrue(mod,s.canParserHandle(url));
    }

    //@org.junit.Ignore
    @org.junit.Test
    public void test002setParameters() {
        try{
            s.setParameters();
        }catch(Exception e){
            e.printStackTrace();
            fail(e.toString()+ " : "+e.getMessage());
        }
    }

    //@org.junit.Ignore
    @org.junit.Test
    public void test003getAllPageString() {
        String allPageString = s.getAllPageString(url);
        assertNotNull(allPageString);
    }

    //@org.junit.Ignore
    @org.junit.Test
    public void test004getTitleOnMainPage() {
        String allPageString = s.getAllPageString(url);
        String title=s.getTitleOnMainPage(url, allPageString);
        System.out.println("test004getTitleOnMainPage : "+title);
        assertNotNull(title);
    }
}
