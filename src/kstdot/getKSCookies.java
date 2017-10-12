package kstdot;

import com.gargoylesoftware.htmlunit.CookieManager;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import java.io.IOException;
import static kstdot.Srape.ErrorLog;
import static kstdot.Srape.KSpass;
import static kstdot.Srape.KSuser;
import static kstdot.Srape.checkErrorLog;

/**
 *
 * @author Rene A
 */
public final class getKSCookies {
    private static CookieManager cookies;
    private WebClient webClient;
    private static HtmlPage page;
    
    getKSCookies() throws IOException{       
        getCookies();
    }
    
    public void getCookies(){        
        try{   
            String login = KSuser, password = KSpass;

            webClient = new WebClient();
            webClient.getOptions().setRedirectEnabled(true);
            webClient.getOptions().setThrowExceptionOnScriptError(false);
            webClient.getOptions().setCssEnabled(false);
            webClient.getOptions().setJavaScriptEnabled(false);
            webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);

            // Get the first page
            page = (HtmlPage) webClient.getPage("www.websiteloginurl.com");

            // Get the form that we are dealing with and within that form,
            // find the submit button and the field that we want to change.
            HtmlForm form = page.getFirstByXPath("//form[@action='/login?Logout=true&RedirectURL=/']");

            // Enter login and passwd
            form.getInputByName("webcontent_0$txtUserName").setValueAttribute(login);
            form.getInputByName("webcontent_0$txtPassword").setValueAttribute(password);

            // Click "Sign In" button/link
            page = (HtmlPage) form.getInputByValue("Login").click();
            cookies = webClient.getCookieManager();
            webClient.closeAllWindows();
            webClient = null;
            page.cleanUp();
            page = null;
        }catch (IOException getKSCookiesIOE){
            checkErrorLog();
            ErrorLog.println("class : getKSCookies"+";"+getKSCookiesIOE);
        }
    }
    public boolean isitvalid(){
        if(cookies.getCookies().size() < 6)
            return false;
        else
            return true;
                    
    }
    public CookieManager retrieveCookies(){
        return cookies;
    }
}
