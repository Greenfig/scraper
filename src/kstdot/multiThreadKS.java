package kstdot;

import com.gargoylesoftware.htmlunit.CookieManager;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import static kstdot.Srape.ErrorLog;
import static kstdot.Srape._data;
import static kstdot.Srape.checkErrorLog;
import static kstdot.Srape.progressBar;
import static kstdot.Srape.progresscounter;

/**
 *
 * @author Rene A
 */
public class multiThreadKS extends Thread{
    private final String SearchURLKS,SearchKSname,SearchKSSupCode;
    private final int Min,Max;
    private final float progress;
    public KSConnect _1_temp;
    public boolean _value = false;
    public CookieManager ck;

    
    multiThreadKS(float refprogress,CookieManager refck,int refMIN,int refMAX,String refSearchURLKS,String refSearchKSSupCode,String refSearchKSname){
        SearchURLKS = refSearchURLKS;        
        SearchKSSupCode = refSearchKSSupCode;
        SearchKSname = refSearchKSname;
        Min = refMIN;
        Max = refMAX;
        ck = refck;
        progress = refprogress;
    }

    public void getKS(){
        try{
            for(int i = Min; i < Max ;i++){
                _1_temp = new KSConnect(ck);
                HtmlPage pg = _1_temp.findSKU(SearchKSSupCode,SearchURLKS, _data.get(i).getSKU()); //Get KSDATA
                try{
                    DomElement hello = pg.getElementById("bodyContainer");
                    if(hello != null){
                        Document dc = Jsoup.parse(hello.asXml());
                        try{
                            if(dc.getElementById("partTitle").text().contains(SearchKSname+" "+_data.get(i).getSKU()) && dc.getElementById("webcontent_0_row2_0_productDetailBasicInfo_lblSupplierCode").text().contains(SearchKSSupCode)){
                                _data.get(i).setStock(dc.getElementById("webcontent_0_row2_0_productDetailBasicInfo_aInventory").text().trim());
                                _data.get(i).setCost(dc.getElementById("webcontent_0_row2_0_productDetailBasicInfo_lblMyPrice").text().replace("$", "").replace(",","").replace("CAD", "").trim());
                                _data.get(i).setJobber(dc.getElementById("webcontent_0_row2_0_productDetailBasicInfo_lblJobberPrice").text().replace("$", "").replace("CAD","").replace(",","").trim());  
                            }
                        }catch (NullPointerException NPX){;
                            _data.get(i).setStock("N/A");
                            _data.get(i).setCost("");
                            _data.get(i).setJobber("");
                            _data.get(i).setMSRP("");
                            _data.get(i).setPrice("");
                            }
                    }
                }catch(NullPointerException npe){
                    }
                progresscounter = (float) (progresscounter  + (float)progress/(Max-Min));
                progressBar.setValue((int)progresscounter);
                _1_temp.close();
                _1_temp = null;
            }
        }catch(IOException | InterruptedException multicatch){
            checkErrorLog();
            ErrorLog.println("class : multiThreadKS(getKS)"+";"+multicatch);
        }
    }
    
    @Override
    public void run(){
        getKS();        
    }
}
