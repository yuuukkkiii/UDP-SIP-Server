package udpserver.util;

import java.net.DatagramPacket;
import java.util.List;

/**
 * @Author: zhaih
 * @Date: 2022/4/11
 * @Time: 14:22
 * @Description:
 */
public class UDPUtil {
    public static String servIp;

    public static int servPort;

    public static void setServIp(String servIp){
        UDPUtil.servIp=servIp;
    }

    public static void setServPort(int servPort){
        UDPUtil.servPort=servPort;
    }

    /**
     * @param packet 接收到的UDP报文
     * @return 报文字符串
     */
    public static String getUDPMessageInfo(DatagramPacket packet){
        byte[] arr = new byte[packet.getLength()];
        System.arraycopy(packet.getData(), packet.getOffset(), arr, 0, arr.length);
        String requestMsg = new String(arr);    //recieved Message
        System.out.println("收到的报文为："+requestMsg);
        return requestMsg;
    }

    /**
     * @param line  传入的报文行
     * @param separator 自定义分隔符
     * @return 头域的名称
     */
    public static String getRowType(String line,String separator){
        return line.substring(0, line.indexOf(separator));
    }

    /**
     * @param line 传入的报文行
     * @param separator 自定义分隔符
     * @return 头域的内容
     */
    public static String getRowInfo(String line ,String separator){
        return line.substring(line.indexOf(separator) + 1);
    }

    public static String extractIpOrPort(String s,int choice) //Returns IP or PORT from SIP URI
    {
        if(choice == 0) //returns IP if choice == 0
        {
            return s.substring(0, s.indexOf(":"));
        }
        else //else returns PORT
            return s.substring(s.indexOf(":")+1);
    }

    public static String dealVia(List<String> list){
        String result="";
        for(String str:list){
            result = result + "Via: " + str + "\r\n";
        }
        return result;
    }
}
