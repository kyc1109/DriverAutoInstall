/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/*
Note
1. run admin to run the program

Not yet 
1. Add key words in ini file.
2. To category the items ex. vga, chipset... 

Know issue
1. wrong path in desktop
*/

package driverautoinstall;


import java.io.BufferedReader;
import java.io.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author kyc1109
 */
public class DriverAutoInstall {
    static String inifile = "user.ini";
    static String current_path = ".\\src\\driverautoinstall\\"; // replace to "C:\Users\kyc1109\Documents\NetBeansProjects\DriverAutoInstall"
    static String Driver_Folder = "";
    static String file_path = current_path + inifile ;
    static File file = new File(file_path);
    static String exeListc = "";
    static int count = 1;
    static String[][] InstallList=new String[100][100];
    static String[] myList = {"Intel Chipset","Intel TXEI","Display","Audio",
                              "DTS","LAN","Card Reader","Touchpad","WLAN","BT",
                              "USB3.0","DMIX","DDPST","DDPE","Serial IO","Canbus","Zigbee",
                              "Utilities"};
    
    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {
        // TODO code application logic here
    /*
        1. load ini file
        2. get currnet path, OK
        3. Check type of install file , OK
        4. recursive installing file (TC*.exe, install.exe, setup.exe), OK
        5. add install finish tag to avoid some driver do not installed. ok
        6. check previous installed status. ok
        */

        // System.out.println("current file path : "+current_path+inifile); 
        // if file doesnt exists, then create it
        file_path = GetCurrnetPath() + inifile ;// only for release
        if (! new File(file_path).isFile()) { // !file.exists()
            System.out.println("[user.ini file check] Not exist ...");
            ScanFolder(); // if file not exist, goto scan folder
        }else{
            System.out.println("[user.ini file check] OK!!!");
            while (true){
                Execute_Install(current_path,file_path);// if file already exist, goto execute install
            }
            
        }
//            FileWriter fw = new FileWriter(file.getAbsoluteFile());
//            BufferedWriter bw = new BufferedWriter(fw);
//            bw.close();
    
    }
    
    private static void ScanFolder() throws IOException {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        System.out.println("here is scan folder and then going to getEXE()");
        //Driver_Folder = GetCurrnetPath() ; //GetCurrnetPath() or "D:\\"
        
        getEXE(GetCurrnetPath()); // ONLY for release
        //getEXE("E:\\"); // for develop        
        writeToFile(exeListc);
        //System.out.println(exeListc);
        //Execute_Install(current_path,file_path);// if file already exist, goto execute install    
    }


    private static String GetCurrnetPath() throws IOException {
        current_path = new java.io.File( "." ).getCanonicalPath();
        System.out.println("Current dir : "+current_path);
        return current_path;
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private static void getEXE(String path) throws IOException {
        File root = new File( path );   //定義根目錄
        File[] list = root.listFiles(); //列出檔案清單
        String FindName ="";
        if (list == null) return;       //list 為無則結束        
        String dir = "";
        String exeList = "";
        
        for (File f :list) { //File f : list
            if ( f.isDirectory() ) {
               getEXE(f.getAbsolutePath() ); //recursive for folder
               //System.out.println( "Dir:" + f.getAbsoluteFile() );
            }
            else if (f.isFile() 
                    && (f.getName().toLowerCase().endsWith(".exe")
                    || f.getName().toLowerCase().endsWith(".bat")
                    || f.getName().toLowerCase().endsWith(".cmd"))
                    && (f.getName().toLowerCase().startsWith("install")
                    || f.getName().toLowerCase().startsWith("setup")
                    || (f.getName().toLowerCase().contains("x64")
                    && f.getName().toLowerCase().contains("install"))//for x64 bit OS
                    || f.getName().toLowerCase().startsWith("tc"))) { // tc is only for A51 
                        exeList = f.getPath();//f.getAbsoluteFile() or f.getPath()
                        
                        exeListc = exeListc + "Install_"+count+"="+"\""+f.getPath()+"\"" +"\r\n";
                        count++;
            }
        }
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
  
    
    private static void writeToFile(String exeListc) throws FileNotFoundException, IOException {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        System.out.println("Welcome to writeToFile");
        System.out.println("writeToFile file_path : " + GetCurrnetPath() +"\\"+ inifile); // add "\\" to current path

//        Pattern pattern = Pattern.compile("\\");//因為Reader ini會把反斜線忽略掉，所以要加進來
//        Matcher m = pattern.matcher(exeListc);
        exeListc = exeListc.replaceAll("\\\\", "\\\\\\\\"); //http://stackoverflow.com/questions/1701839/string-replaceall-single-backslashes-with-double-backslashes
        System.out.println(exeListc);
        
        File file = new File(GetCurrnetPath() +"\\"+ inifile); // for release, add "\\" to current path
        //File file = new File(file_path); // for develop        
        FileOutputStream fis = new FileOutputStream(file);
        PrintStream out = new PrintStream(fis);
        System.setOut(out);
            System.out.println("Last_Installed=");         
            System.out.println("Current_Install="); 
            System.out.println(exeListc); 
        out.close();
        fis.close();
    }
    

    private static void Execute_Install(String current_path,String file_path) throws IOException {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        System.out.println("Welcome to execute install : " +current_path+inifile);        
        InstallList = LoadIniFile(file_path); //    資料夾的反斜線會消失...
        
        //System.out.println("CurrentPath : "+ current_path); 
        
//        for (int i = 0;i<InstallList.length;i++){ // item
//            for(int j = 0;j<InstallList.length;j++){ // vendor brand
//                 if (InstallList[i][j]!=null&&InstallList[i][j]!=null){
//                    if (InstallList[j]!=null){
//                        //列出要裝的item
//                        System.out.println(i+"-"+j+" : "+ InstallList[i][j]); // show item
//                        //System.out.println(GetCurrnetPath()+"\\"+InstallList[i][j]); //file path
//                    }
//                }
//            }
//        }
        //getEXE((InstallList[23][0]));
        //ExecuteSetup("cmd /c dir");
    }
    
    private static String[][] LoadIniFile(String file_path) { //http://www.java-tips.org/java-se-tips-100019/88888889-java-util/2329-how-to-use-an-ini-file.html
            try{
                Properties p = new Properties();
                //p.load(new FileInputStream(file_path));//".\\src\\driverautoinstall\\user.ini"
                p.load(new FileReader(file_path));
                //p.setProperty("Last_Installed", "上一個安裝");
                //p.setProperty("Current_Install", "目前安裝");
                System.out.println("Last Installed = "+p.getProperty("Last_Installed"));
                System.out.println("Current Install = "+p.getProperty("Current_Install"));
                
                for (int x=1;x<p.size();x++){
                    System.out.println("Install "+x+" = "+p.getProperty(("Install_"+x)));
                }
                if("".equals(p.getProperty("Last_Installed"))){// 判斷安裝進度
                    p.setProperty("Last_Installed", "Install_1");
                    p.setProperty("Current_Install", "Install_1"); // Initial the INI file.
                    System.out.println("Last Installed is empty");                    
                    p.store(new FileWriter(file_path),p.getProperty("Comments for write to file")); // write to ini file and comments.                    
                    driverSetup(p.getProperty(p.getProperty("Current_Install")));
                }else if ( p.getProperty("Last_Installed")!=null ){ //  not last driver yet
                    System.out.println("Last Installed is NOT null, p.size : " + p.size());                    
                    //p.setProperty("Current_Install", "Install_ + 1");   Current = Last +1，別忘了要再裝完driver寫回Last_Installed
                    int x = Integer.valueOf(p.getProperty("Last_Installed").replaceAll("[^0-9]",""));
                    p.setProperty("Current_Install", "Install_"+ (x+1));
                    p.setProperty("Last_Installed", "Install_"+ (x+1)); // 強制進下一個
                    p.store(new FileWriter(file_path),p.getProperty("Comments for write to file")); // write to ini file and comments.
                    driverSetup(p.getProperty(p.getProperty("Current_Install")));
                    System.out.println("Current_Install : " + p.getProperty("Current_Install"));
                    
                    if (p.size() <= (x+2)){ // x-2 因為空一行和多執行一次
                        System.out.println("Driver install finish ! x : p.size " + (x+2) +" " +p.size());// 如果安裝結束就執行 Not yet
                    }
                    //System.out.println("propertyNames : "+p.stringPropertyNames());                    
                }
                
                
                p.store(new FileWriter(file_path),p.getProperty("Comments for write to file")); // write to ini file and comments.
                //p.storeToXML(new FileOutputStream((current_path+"user.xml")), "This is comments");
                
                //p.list(System.out); // println p.list
            
            
//            for (int x=0;x<20; x++){//scan main and second driver items
//                if (null!=p.getProperty("Install-"+x)){
//                    //找Install-x 的部分
//                    System.out.println("Install-"+x+" : "+ p.getProperty("Install-"+x));  
//                    InstallList[x][0]=p.getProperty("Install-"+x); // list component 
//                    
//                    for(int y=0;y<6;y++){//for multi vendor
//                        if (null!=p.getProperty("Install-"+x+"-"+y)){
//                            //找Install-x-y 的部分
//                            System.out.println("Install-"+x+"-"+y+" : "+ p.getProperty("Install-"+x+"-"+y));  
//                            InstallList[x][y]=p.getProperty("Install-"+x+"-"+y); // list vendor of component
//                        }
//                    }
//                }
//            }
            
//            String[] list = new String[]{"user","password","location","filepath","CurrentInstall"}; 
//            for (int i = 0 ;i<list.length;i++) { //等同於 (int i = 0; i<list.length ;i++)
//                //System.out.println(list[i] + " = " + p.getProperty("DB" + list[i]));
//                InstallList[i+20][0]=p.getProperty("DB" + list[i]);
//            }
            
            //p.list(System.out);
        }catch (IOException e){
            System.out.println(e);
        }
            return InstallList;
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private static boolean driverSetup(String cmd) {// NO used
        System.out.println("Welcome to driverSetup !!! "); 
        try {            
            String line=null;
            System.out.println("cmd.exe /c " + cmd );
            //Process p = Runtime.getRuntime().exec("E:\\1. Chipset\\Kit 110752\\Intel  Chipset Software Installation Utility\\Chipset_10.1.1.12_NDA\\SetupChipset.exe");
            ProcessBuilder pb = new ProcessBuilder(cmd); // 關鍵 http://stackoverflow.com/questions/19621838/createprocess-error-2-the-system-cannot-find-the-file-specified
            Process p = pb.start();
            
            if(cmd.toLowerCase().contains("display")||cmd.toLowerCase().contains("vga")||cmd.toLowerCase().contains("chipset")){
                System.out.println("!!! Need to reboot after driver installed !!!");
                System.exit(0);// check the item if need to reboot.                
            }
            BufferedReader bri = new BufferedReader(new InputStreamReader(p.getInputStream(), "UTF-8"));
            BufferedReader bre = new BufferedReader(new InputStreamReader(p.getErrorStream(), "UTF-8"));
            for (int i = 0;i<bri.readLine().length();i++){                
                System.out.println(line);
            }    
            while ((line = bri.readLine()) != null) {
                    System.out.println(line);
                }
            bri.close();
            while ((line = bre.readLine()) != null) {
                System.out.println(line);
            }
            bri.close();
            p.waitFor();
        }
        catch (Exception err) {
        }
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        return true;
    }

    
}



