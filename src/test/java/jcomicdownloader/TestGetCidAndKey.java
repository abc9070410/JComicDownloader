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
public class TestGetCidAndKey {
    
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
            new Object[][] {               
                {0,"http:///tel.dm5.com/m216410/","jcomicdownloader.module.ParseDM5"}
            }
        );
    }

    public TestGetCidAndKey(Integer i,String url, String mod) {
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
    
@org.junit.Ignore    
    @org.junit.Test 
    public void test000Run() {
        ((ParseDM5) s).getCidAndKey("http://tel.dm5.com/m216410/chapterfun.ashx?cid=216410&page=1&key=&language=1&gtk=6", url);
                
    }
    
}
