package kstdot;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URISyntaxException;
import java.util.Iterator;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import static kstdot.Srape.ErrorLog;
import static kstdot.Srape._data;
import static kstdot.Srape.checkErrorLog;
import static kstdot.Srape.progressBar;
import static kstdot.Srape.progresscounter;

/**
 *
 * @author Rene A
 */
public class multiThreadTdot extends Thread{
    private final String SearchURLTdot;
    public KSConnect _1_temp;
    public TdotConnect _2_temp;
    private final int Min,Max;
    private final float progress;

    multiThreadTdot(float refprogress,int refMin,int refMax,String refSearchURLTdot){
        Min = refMin;
        Max = refMax;
        SearchURLTdot = refSearchURLTdot;
        progress = refprogress;
    }

    public void getTdot(){
        for (int i = Min;i<Max;i++){
            _2_temp = new TdotConnect();
            try{
                Document htm = _2_temp.findSKU(SearchURLTdot, _data.get(i).getSKU());
                Elements Tdotsearch = htm.getElementsByClass("product-shop");
                Iterator<Element> itr = Tdotsearch.listIterator();
                
                while(itr.hasNext()){
                    Element ele = itr.next();
                    if(ele.getElementsByClass("product-name").text().contains(SearchURLTdot+" "+_data.get(i).getSKU())){
                        _data.get(i).setTdot(ele.getElementsByClass("price").text().replace("CAD", "").replace("$", "").replace(",","").trim());
                        break;
                    }
                }
            }catch(NullPointerException NPX){
                _data.get(i).setTdot("N/A");
            }
            progresscounter = (float) (progresscounter  + (float)progress/(Max-Min));
            progressBar.setValue((int)progresscounter);
            _2_temp.close();
            _2_temp = null;
        }
    }    
    
    @Override
    public void run(){
        getTdot();
    }
}
