package kstdot;


import com.gargoylesoftware.htmlunit.CookieManager;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebWindow;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.Random;


public final class KSConnect {
    private static HtmlPage page;
    private static int min,max;
    private static CookieManager cookies_yay;
    private final WebClient webClient;
    
    KSConnect(CookieManager ck) throws IOException{        
        do{
            min = randvar();
        }while (min == 100 || min >=80);
        do{
            max = randvar();
        }while (max == min || max < min);
        cookies_yay = ck; //Get Cookies from KS
        
        webClient = new WebClient();
        webClient.getOptions().setRedirectEnabled(true);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.setCookieManager(cookies_yay);
    }
 
    public HtmlPage findSKU(String refSearchKSSupCode, String refSearchURLKS, String SKU) throws InterruptedException{
        page = null;
        webClient.getOptions().setTimeout(60*1000);
        int ran = randInt(min,max);
        Sleeper(ran);
        String _SearchString = refSearchURLKS+SKU+"%2c"+refSearchURLKS+SKU.replace("-", "")+"%2c"+refSearchURLKS+SKU.replace(".", "")+"%2c"+refSearchURLKS+SKU.replace(".", "").replace("-", "")+
                "%2c"+refSearchKSSupCode+SKU+"%2c"+refSearchKSSupCode+SKU.replace("-", "")+"%2c"+refSearchKSSupCode+SKU.replace(".", "")+"%2c"+refSearchKSSupCode+SKU.replace(".", "").replace("-", "");
        int i = 0;
        while(i<20){
            
            try{
                page = (HtmlPage) webClient.getPage("www.webpagetoscrape.com");
                    break;
            }catch(SocketTimeoutException | NullPointerException ex){
                }catch (IOException e){}    
            i++;
        }
        return page;
    }
    public void close() throws NullPointerException{
        final List<WebWindow> windows = webClient.getWebWindows();
        for (final WebWindow wd : windows)
            wd.getJobManager().removeAllJobs();
        webClient.closeAllWindows();
        if (page != null)
            page.cleanUp();
    }
    public static int randvar(){
        Random rand = new Random();
        int randomNum = rand.nextInt((100-10)+1) + 10;
        return randomNum;
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
