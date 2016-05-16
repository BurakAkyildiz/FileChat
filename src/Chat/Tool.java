package Chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketOptions;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.channels.SocketChannel;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 *Tools for File chat app.
 * Contains static methods.
 * @author Burak
 */
public class Tool implements FileChatConstants
{
    public static String savedLocalIP = null;
    public static String savedRemoteIP = null;
    /**
     * Checks if text is numeric or not.
     * ( it can be double number.)
     * @param text
     * @return is text number. 
     */
    public static boolean isNumeric(String text)
    {
        try {
            Double a = Double.parseDouble(text);
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    
    
    
    /**Returns ip of this machine.
     * If the connection type is defined local on FileChat.Monitor.isRemoteConnection returns first local ip.
     * If its remote connection returns remote ip.
     * If there is no connection returns null.
     */
    public static String IPCheck()
    {
        String ip = null;
        try
        {
            if(checkInternetConnection() && FileChat.monitor != null && FileChat.monitor.isRemoteConnection)
            {
                ip = readIP();
            }
            else
            {
               ip = getLocalIp();
            }
            
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return ip;
    }
    
    /**
     * Returns the ip of given url.
     * @param url
     * @return 
     */
    public static String getIp(String url)
    {
        String ip = null;
        Process p;
        try {
            p = Runtime.getRuntime().exec("cmd /c ping "+url);
        
                Scanner oku = new Scanner(p.getInputStream(), "UTF-8");
                while (oku.hasNextLine())
                {
                    String line = oku.nextLine();
                    //System.out.println(line);
                    if (line.startsWith("Pinging"))
                    {
                        ip = line.substring(line.indexOf("[")+1,line.indexOf("]"));
                    }
                }
                }
        catch (IOException ex) {
            Logger.getLogger(Tool.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ip;
    }
    
   
    
    /**
     * Returns the first local ip adress.
     * @return
     * @throws UnknownHostException 
     */
    public static String getLocalIp() throws UnknownHostException
    {
        if(savedLocalIP != null && !savedLocalIP.isEmpty())
            return savedLocalIP;
        String ip = null;
        try {
            
            /*Process p;
            try {
            p = Runtime.getRuntime().exec("cmd /c ipconfig");
            
            Scanner oku = new Scanner(p.getInputStream(), "UTF-8");
            while (oku.hasNextLine())
            {
            String line = oku.nextLine();
            //System.out.println(line);
            if (line.contains("IPv4"))
            {
            String tok[] = line.split(":");
            ip = tok[1].trim();
            }
            }
            }
            catch (IOException ex) {
            Logger.getLogger(Tool.class.getName()).log(Level.SEVERE, null, ex);
            }
            return ip;*/
            //return InetAddress.getLocalHost().getHostAddress();
            
            /*try {
            Enumeration interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
            NetworkInterface interf = (NetworkInterface) interfaces.nextElement();
            if (interf.isUp() && !interf.isLoopback()) {
            ip = (""+interf.getInetAddresses().nextElement()).substring(1);
            }
            }
            } catch (SocketException ex) {
            Logger.getLogger(Tool.class.getName()).log(Level.SEVERE, null, ex);
            }*/
            
            
            
            // iterate over the network interfaces known to java
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            OUTER : for (NetworkInterface interface_ : Collections.list(interfaces)) {
              // we shouldn't care about loopback addresses
              if (interface_.isLoopback())
                continue;

              // if you don't expect the interface to be up you can skip this
              // though it would question the usability of the rest of the code
              if (!interface_.isUp())
                continue;

              // iterate over the addresses associated with the interface
              Enumeration<InetAddress> addresses = interface_.getInetAddresses();
              for (InetAddress address : Collections.list(addresses)) {
                // look only for ipv4 addresses
                if (address instanceof Inet6Address)
                  continue;

                // use a timeout big enough for your needs
                if (!address.isReachable(3000))
                  continue;
                
                Socket socket = null;
                try
                {
                  socket = new Socket(addresses.toString().substring(1), 80, address, 8080);
                    
                  socket.setSoTimeout(3000);
                } catch (Exception ex) {
                    //ex.printStackTrace();
                    if(socket != null && !socket.isClosed())
                  continue;
                }
                
                ip = (""+ address).substring(1);
                savedLocalIP = ip;
                // stops at the first *working* solution
                break OUTER;
              }
            }
            
        } catch (SocketException ex) {
            Logger.getLogger(Tool.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Tool.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ip;
    }
    
    
    /**
     * Checks the internet connection. If there is a connection returns true
     * If there is no connection returns false.
     * If connection is true it can be with problems
     * @return
     * @throws SocketException 
     */
     public static boolean checkInternetConnection() throws SocketException {
        Enumeration interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface interf = (NetworkInterface) interfaces.nextElement();
            if (interf.isUp() && !interf.isLoopback()) {
                return true;
            }
        }
        return false;
    }

     
     /**
      * Returns the remote ip of this machine.
      * @return 
      */
    public static String readIP()  {
        
        if(savedRemoteIP != null && !savedRemoteIP.isEmpty())
            return savedRemoteIP;
        try {
            URL myIP = new URL("http://icanhazip.com/");
            BufferedReader in = new BufferedReader(new InputStreamReader(myIP.openStream()));
            String ip = in.readLine();
            savedRemoteIP = ip;
            return ip;
        }
        catch (Exception e) {
            System.err.println("Exception on readIP (Network Connection Problem) :"+e.getMessage());
            return null;
        }
        
        
    }
    
    /**
     * Converts byte to String with format.
     * Simple : long 1234567890 <![CDATA[-> ]]> String 1.177,375
    */
    public static String byteToMb(long num)
    {
        long mbSize = num/1024/1024;
        
        double decimal = (num / (1024.0*1024.0)) % 1 ;
        
        String str = mbSize + "," +(""+decimal+"000").substring(2,5);
        
        return formatNumber(str);
    }
    public static String formatNumber(String num)
    {
        StringBuilder strBuild = new StringBuilder();
        char[] numC = num.toCharArray();
        boolean isStartOfNumber = false;
        int counter = 0;
        for (int i = numC.length-1; i >= 0 ; i--) {
            
            strBuild.append(numC[i]);
            
            if(numC[i] == ',')
            {
                isStartOfNumber = true;
                continue;
            }
            
            if(isStartOfNumber)
            {
                counter++;
                if(counter % 3 == 0 && numC.length-1 > numC.length -1 - i)
                    strBuild.append(".");
            }
            
        }
        
        return strBuild.reverse().toString();
    }
    
    
    static int result = SOCKET_NOT_AVAILABLE;
    static ServerSocket sSock = null;
    /**
     * Checks the ports avaiblity.
     * Gets the ip by connection type and tries to connect it with given port.
     * If it can connect returns true
     * If its not or there is no connection returns false
     * @param port
     * @return 
     */
    public static int isAvailablePort(int port) {
        try {
            sSock = new ServerSocket(port);
            sSock.setSoTimeout(CHECK_WAIT_TIME);
            }
        catch (Exception ex) {
            result = SOCKET_IN_USE;
            try {
                sSock.close();
            }
            catch (Exception ex1) {}
            return result;
        }
        
            new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    try {
                        Thread.sleep(100);
                        Socket client = sSock.accept();
                        if(client.isConnected())
                        {
                            try {
                                if(sSock !=null && !sSock.isClosed()) 
                                    sSock.close();
                                if(client != null && !client.isClosed())
                                    client.close();
                                }
                                catch (Exception ex) {}
                            result = SOCKET_AVAILABLE;
                        }
                        
                            
                    }
                    catch (Exception ex) {}
                }
            }).start();
            
            
            try {
                if(!checkInternetConnection())
                {
                    try {
                        if(sSock !=null && !sSock.isClosed()) 
                        sSock.close();
                    }
                    catch (IOException ex) {}
                    return SOCKET_NOT_AVAILABLE;
                   
                }else
                {
                    Thread.sleep(500L);
                    String ip = IPCheck();
                    if(ip == null)
                    {
                        try {
                            if(sSock !=null && !sSock.isClosed()) 
                            sSock.close();
                        }
                        catch (Exception ex) {}
                        
                        return SOCKET_NOT_AVAILABLE;
                    }
                        
                    
                    Socket client = new Socket(ip,port);
                    if(client.isConnected())
                    {
                        try {
                            if(sSock !=null && !sSock.isClosed()) 
                                sSock.close();
                            if(client != null && !client.isClosed())
                                    client.close();
                        }
                        catch (Exception ex) {}
                        System.out.println("Port : "+port + " available on : "+client.getInetAddress());
                        return SOCKET_AVAILABLE;
                    }else result = SOCKET_NOT_AVAILABLE;
                    
                }


            }
            catch (IOException ex) {}
            catch (InterruptedException ex) {}
            catch(Exception e){e.printStackTrace();}
            
            try {
                if(sSock !=null && !sSock.isClosed()) 
                sSock.close();
            }
            catch (Exception ex) {}
        
        
        
        return result;
    }
 
   
    
    /**
     * Returns Time string defined on 2 miliseconds subtraction.
     * Simple : long 1463326063329 , long  1463335959794 -> String -02:44:56-  
     * @param startMilisecond
     * @param endMilisecond
     * @return Formated timeString
     */
    public static String getTimeString(long startMilisecond, long endMilisecond)
    {
        long time = 0, 
                timeSecond = 0,
                timeMinute = 0,
                timeSecondDecimal = 0,
                timeMinuteDecimal = 0,
                timeHour = 0;
        
        
        time = endMilisecond - startMilisecond;
        
        timeSecond = time / 1000;
        
        if(timeSecond > 0)
            timeMinute = timeSecond / 60;
        
        if(timeMinute > 0 && timeSecond > 0)
            timeSecondDecimal = timeSecond % 60;
        else
            timeSecondDecimal = timeSecond;
        
        
        if(timeMinute > 60)
        {
            timeHour = timeMinute / 60;
        }
        
        if(timeHour > 0)
            timeMinuteDecimal = timeMinute % 60;
        else
            timeMinuteDecimal = timeMinute;
        
        String timeStr = String.format("-%s%2s:%2s-", timeHour > 0 ? (timeHour < 10 ? "0"+timeHour : ""+timeHour)+":" : ""
                                                     , timeMinuteDecimal < 10 ? "0"+timeMinuteDecimal : ""+timeMinuteDecimal
                                                     , timeSecondDecimal < 10 ? "0"+timeSecondDecimal : ""+timeSecondDecimal);
        
        return timeStr;
        
    }
    
    
    /**
     * Returns new JTextFieldLimit instance.
     * @param limit
     * @return 
     */
    public static JTextFieldLimit getJTextFieldLimit(int limit)
    {
        return new JTextFieldLimit(limit);
    }
    
    /**
     * Checks is ip remote.
     * It uses local ip standarts to define ip type.
     * @param ip
     * @return 
     */
    public static boolean isIpRemote(String ip)
    {
        if(ip == null || ip.isEmpty() || ip.split("\\.").length < 4)
            return false;
        
        
        String[] ipStrArr = ip.split("\\.");
        int[] ipToCheck = new int[4];
        for (int i = 0; i < 4; i++) {
            ipToCheck[i] = Integer.parseInt(ipStrArr[i]);
        }
        
        if(ipToCheck[0] == 10)
            return false;
        
        if(ipToCheck[0] == 172 && (ipToCheck[1] >= 16 && ipToCheck[1] <= 31 ))
            return false;
        
        
        if(ipToCheck[0] == 192 && ipToCheck[1] == 168)
            return false;
        
        return true;
    }
    
    
    
}

/**
 * This class creates PlainDocument to limit a JTextFields typed character number.
 * @author Burak
 */
class JTextFieldLimit extends PlainDocument {
  private int limit;
  JTextFieldLimit(int limit) {
    super();
    this.limit = limit;
  }

  JTextFieldLimit(int limit, boolean upper) {
    super();
    this.limit = limit;
  }

  public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException {
    if (str == null)
      return;

    if ((getLength() + str.length()) <= limit) {
      super.insertString(offset, str, attr);
    }
  }
}