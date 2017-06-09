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
public class TestParseComicURL {
    
    private final String url, mod, title;
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
            new Object[][] {               
                {0,"http://www.tuku.cc/comic/11977/c_67693/","GATE","jcomicdownloader.module.ParseTUKU"}
                ,{6,"http://tel.dm5.com/m216410/","COMIC","jcomicdownloader.module.ParseDM5"}
            }
        );
    }
    

    public TestParseComicURL(Integer i,String url,String title, String mod) {
        this.url = url;
        this.mod = mod;
        this.title= title;
        noGuiTweak:{ //just a label
            Run.isAlive=true;// tweak for no download
            Common.consoleThreadName= Thread.currentThread().getName(); 
        }
        
        Common.debugPrintln("-----------------"+i+"--------------------");
        try{
            this.s=(ParseOnlineComicSite)Class.forName(mod).newInstance(); 
            ignoreBug_001:{// will fail if not set in some case 
                this.s.setURL(url);   
                this.s.setTitle(title);
                this.s.setWholeTitle(title);
            }
        }catch(Exception x){
            s=null;
            fail("Module "+mod+" has exception : "+x.getMessage());           
        }
    }
    
@org.junit.Ignore    
    @org.junit.Test 
    public void test000Run() {
        s.setParameters();
        s.parseComicURL();
    }
}
