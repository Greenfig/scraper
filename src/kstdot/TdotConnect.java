package scrape;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebWindow;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.utils.URIBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 *
 * @author Rene A
 */
public final class TdotConnect {
    private static HtmlPage page;
    private final WebClient webClient;
    
    TdotConnect() throws IOException{
        
        webClient = new WebClient();
        webClient.getOptions().setRedirectEnabled(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setActiveXNative(false);
        webClient.getOptions().setAppletEnabled(false);
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        LogFactory.getFactory().setAttribute("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");
        java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(Level.OFF); 
        java.util.logging.Logger.getLogger("org.apache.commons.httpclient").setLevel(Level.OFF);
    }
    public Document findSKU(String SearchURLTdot, String SKU) throws IOException, URISyntaxException, InterruptedException{
        page = null;
                
        //Sleep Logic//
        Sleeper(randInt(30,60));
        //End Sleep Logic//
        
        URI uri = new URIBuilder()
                .setScheme("http")
                .setHost("www.websitetoscrape.com")
                .setPath("/asearch/")
                .setParameter("q", SearchURLTdot +" "+ SKU)
                .build();
        
        int i = 0;
        while(i<20){
            try{
                page = (HtmlPage) webClient.getPage(uri.toURL());
                break;
            }catch(SocketTimeoutException ex){
                System.out.println(ex);
                }catch (IOException e){
                    System.out.println(e);
                }    
            i++;
        }
        
        return Jsoup.parse(page.asXml());
    }
    public void close() throws NullPointerException{
        final List<WebWindow> windows = webClient.getWebWindows();
        for (final WebWindow wd : windows)
            wd.getJobManager().removeAllJobs();
        webClient.closeAllWindows();
        if(page != null)
            page.cleanUp();
    }

    public static int randInt(int small, int big){
        Random rand = new Random();
        int randomNum = rand.nextInt((big-small)+1) + small;
        return randomNum;
    }
    public void Sleeper(int refran) throws InterruptedException{
        //Sleep Logic//
        if(refran >= 80)
            Thread.sleep(((refran*1000)/2)-20000);
        else if (refran < 80 && refran >= 50)
            Thread.sleep(((refran*1000)/2)-15000);
        else if (refran < 50 && refran >= 30)
            Thread.sleep(((refran*1000)/2)-5000);
        else
            Thread.sleep(((refran*1000)));
        //End Sleep Logic//
    }
}
