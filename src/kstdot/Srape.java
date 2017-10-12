package kstdot;

//HTTPCLIENT/HTTPUNIT
import com.gargoylesoftware.htmlunit.CookieManager;
import java.awt.BorderLayout;
import java.awt.Container;
import java.io.*;
import java.io.BufferedReader;
import static java.lang.Thread.State.TIMED_WAITING;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JProgressBar;
import javax.swing.border.Border;






public class Srape {
    public static CookieManager ck;
    public static int MAXcount,divsize = 5, listsize, remainder;
    public static List<Final> _data;
    private static PrintWriter writer;
    public static float progresscounter;
    public static JProgressBar progressBar;
    public static PrintWriter ErrorLog;
    public static String inputFile;
    public static float percent_divider;
    public static String KSuser,KSpass;

    

    public static void main(String[] args) {
        //Get user info
        getLoginInfo _log = new getLoginInfo();
        _log.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        _log.setVisible(true);
    }
    public static double rnd(Double ref){
        return Math.round(100 * ref)/100d;
    }
    
    public static void checkErrorLog(){
        try{
            if(ErrorLog == null)
                ErrorLog = new PrintWriter(inputFile.replace("txt","csv").replace(".csv", "_Error_Log.txt"), "UTF-8");
        }catch(FileNotFoundException | UnsupportedEncodingException FnFUEE){
        }
    }
    
    public static void doWork(String ku,String kp) throws IOException, URISyntaxException, InterruptedException{
        KSuser = ku;
        KSpass = kp;
        
        
        SelectFile browse = new SelectFile(); //Choose file    
        JFileChooser fileChooser = new JFileChooser();
        progresscounter = 0;
        
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        int result = fileChooser.showOpenDialog(browse);
        browse.dispose();
    

        if (result == JFileChooser.APPROVE_OPTION) {
            getKSCookies _ck = new getKSCookies();
            if ( _ck.isitvalid()){
                JFrame f = new JFrame("Keystone/Tdot");
                f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                Container content = f.getContentPane();
                progressBar = new JProgressBar();
                progressBar.setValue((int)progresscounter);
                progressBar.setStringPainted(true);
                Border border = BorderFactory.createTitledBorder("Reading...");
                progressBar.setBorder(border);
                content.add(progressBar, BorderLayout.NORTH);
                f.setSize(300, 100);
                f.setVisible(true);
                File selectedFile = fileChooser.getSelectedFile();
                inputFile = selectedFile.getAbsolutePath();

                List<String> SKUs; //list for skus	
                //Load CSV with all the unique SKUs
                BufferedReader reader;
                SKUs = new ArrayList<>();
                reader = new BufferedReader(new FileReader(inputFile));	
                String line;
                int _line_counter = 0;
                String li[];
                String SearchURLKS = null;
                String SearchURLTdot = null;
                String SearchKSname = null;
                String SearchKSSupCode = null;
                while ((line = reader.readLine()) != null) {
                    if(_line_counter == 0){
                        li = line.split(";");
                        if(li.length != 1){
                            SearchKSSupCode = li[1];
                            SearchKSname = li[2];
                            if(li.length > 3){
                                SearchURLTdot = li[3];
                                divsize = 20;
                            }
                            else
                                divsize = 40;
                        }
                        SearchURLKS = li[0];
                    }
                    else
                        SKUs.add(line);
                    _line_counter++;
                }
                reader.close();

                //Split array into groups to assign for multi-threading
                //get count of list
                percent_divider = (float) 2.5;
                MAXcount = SKUs.size();
                listsize = (int)(MAXcount/divsize);
                remainder = MAXcount%divsize;

                int _tracker = 0;
                int _stracker = 0; //also MIN for threads
                int _overcount = 0; //also MAX value threads
                boolean _test = false;

                listsize = (int)(MAXcount/divsize);
                remainder = MAXcount%divsize;
                if(remainder > divsize)
                    _overcount = (int) Math.round(((double)remainder/divsize));
                else if(remainder != 0)
                    _overcount = 1;

                //Get Cookies
                ck = _ck.retrieveCookies();

                //Initialize Data Store
                _data = new ArrayList<>();
                multiThreadKS[] _Thread_KS = new multiThreadKS[divsize];
                multiThreadTdot[] _Thread_Tdot = new multiThreadTdot[divsize];

                for(int a = 0; a < divsize; a++){
                    _stracker = _stracker+_tracker;
                    _tracker =  listsize + _overcount;                
                    for(int b = _stracker; b < _stracker+_tracker; b++){
                        Final _tempFinal = new Final();
                        _tempFinal.setSKU(SKUs.get(b));
                        _data.add(_tempFinal);
                    }
                    if((a+1) == remainder && _test == false){
                        _overcount = 0;
                        _test = true;
                    }
                    //Start Thread
                    _Thread_KS[a] = new multiThreadKS(percent_divider,ck,_stracker,(_stracker+_tracker),SearchURLKS,SearchKSSupCode,SearchKSname);
                    if(SearchURLTdot != null)                  
                        _Thread_Tdot[a] = new multiThreadTdot(percent_divider,_stracker,(_stracker+_tracker),SearchURLTdot); 
                }

                for(int g = 0;g<divsize;g++){
                    if(SearchURLTdot != null)
                        _Thread_Tdot[g].start();
                    _Thread_KS[g].start();
                }

                //Join Threads
                for(int l = (divsize-divsize);l<divsize;l++){
                    if(_Thread_KS[l].isAlive() || _Thread_KS[l].getState() == TIMED_WAITING)
                        _Thread_KS[l].join();

                    if(SearchURLTdot != null)
                        if(_Thread_Tdot[l].isAlive() || _Thread_Tdot[l].getState() == TIMED_WAITING)
                            _Thread_Tdot[l].join();

                }

                //Save to file
                inputFile = inputFile.replace("txt","csv").replace(".csv", "_KS-Tdot_OUTPUT.csv");

                writer = new PrintWriter(inputFile,"UTF-8");
                writer.println("SKU;Tdot;Cost;Jobber;Stock;Price;MSRP;inventory");
                for (Final _data_obj : _data) {
                    //Calctulate Price//
                    if (_data_obj.isNULL("Tdot") && !_data_obj.isNULL("Jobber")) {
                        if(!_data_obj.getJobber().isEmpty()){
                            try{
                            if (Double.parseDouble(_data_obj.getJobber()) < 100) {
                                _data_obj.setPrice(String.valueOf(rnd(Double.parseDouble(_data_obj.getCost()) * 1.55)));
                            } else if (Double.parseDouble(_data_obj.getJobber()) < 200 && Double.parseDouble(_data_obj.getJobber()) > 100) {
                                _data_obj.setPrice(String.valueOf(rnd(Double.parseDouble(_data_obj.getCost()) * 1.45)));
                            } else if (Double.parseDouble(_data_obj.getJobber()) < 400 && Double.parseDouble(_data_obj.getJobber()) > 200) {
                                _data_obj.setPrice(String.valueOf(rnd(Double.parseDouble(_data_obj.getCost()) * 1.35)));
                            } else if (Double.parseDouble(_data_obj.getJobber()) < 600 && Double.parseDouble(_data_obj.getJobber()) > 400) {
                                _data_obj.setPrice(String.valueOf(rnd(Double.parseDouble(_data_obj.getCost()) * 1.25)));
                            } else if (Double.parseDouble(_data_obj.getJobber()) > 600) {
                                _data_obj.setPrice(String.valueOf(rnd(Double.parseDouble(_data_obj.getCost()) * 1.2)));
                            }
                            }catch (NumberFormatException | NullPointerException format_exception){
                                checkErrorLog();
                                ErrorLog.println("class : Scrape(Calculate Price)"+";"+format_exception);
                            }
                        }
                    } else if (!_data_obj.isNULL("Tdot")) {
                        _data_obj.setPrice(String.valueOf(rnd(Double.parseDouble(_data_obj.getTdot()) + 1.5)));
                    }
                    if (!_data_obj.isNULL("Price"))
                        if(!_data_obj.getPrice().isEmpty())
                            _data_obj.setMSRP(String.valueOf(rnd(Double.parseDouble(_data_obj.getPrice()) * 1.2)));
                    //End Calculate Price

                    try{
                        //Inventory
                        if(_data_obj.checkStock())
                            _data_obj.setInvet("9999");
                        else
                            _data_obj.setInvet("0");
                        //End Inventory
                    }catch(NumberFormatException form_cept){
                        checkErrorLog();
                        ErrorLog.println("class : Scrape(Calculate Price)"+";"+form_cept);                   
                    }

                    writer.println(_data_obj._dataText());
                }
                writer.close();
                if(ErrorLog != null)
                    ErrorLog.close();
                f.dispose();
            }
        }
    }
    
}    

	


