package udpserver.handler;


import static udpserver.constant.UDPConst.*;

import udpserver.constant.UDPConst;
import udpserver.util.UDPUtil;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * @Author: zhaih
 * @Date: 2022/4/11
 * @Time: 15:14
 * @Description:
 */
public class RegisterMessage extends UDPMessage{
    String allowEvents;
    String expires;

    String username;

    public RegisterMessage(){
        super();
        this.allowEvents="";
        this.expires="";
    }

    @Override
    public void fillMessage(StringTokenizer message) {
        while (message.hasMoreTokens()) {
            String nextLine = message.nextToken();
            String fieldName = UDPUtil.getRowType(nextLine,":");//获取每行信息的前缀，即报文
//                        对报文进行拆解
            if (ALLOW_EVENTS.equals(fieldName)) {
                this.allowEvents = UDPUtil.getRowInfo(nextLine, " ");
            } else if (EXPIRES.equals(fieldName)) {
                this.expires = UDPUtil.getRowInfo(nextLine, " ");
            }
            else{
                super.getHeader(fieldName,nextLine);
            }
        }
    }

    @Override
    public void doService(String line1) {
        String sipUri = this.contact.substring(this.contact.indexOf(":") + 1, this.contact.indexOf(";"));
        //   r.contact.substring(r.contact.indexOf(":")+1, r.contact.length());
        String number;
        String ipPort;
        number = UDPUtil.getRowType(sipUri,"@");
        if (!sipUri.contains(">")) {
            //this means there is no > (for Jitsi)
            ipPort = UDPUtil.getRowInfo(sipUri,"@");
        } else {
            //this means there is a > (for Phoner)
            ipPort = sipUri.substring(sipUri.indexOf("@") + 1, sipUri.length() - 1);
        }



        //check if already registered
        boolean isRegistered = REGISTER_LIST.containsKey(number);
        int expiresTime = Integer.parseInt(this.expires.trim());
        this.username=number;
        if (!isRegistered && expiresTime > 0)    //register
        {
            System.out.println("Phone " + number + " is Successfully Registered at IP:PORT " + ipPort + " .");
            //REGISTER_LIST.put(number, ipPort);
            //REGISTER_LIST.put(number, UDPUtil.servIp+":"+UDPUtil.servPort);
        } else if (isRegistered && expiresTime == 0)   //unregister
        {
            System.out.println("Phone " + number + " is Successfully UNREGISTERED.");
            REGISTER_LIST.remove(number);
        }
    }

    @Override
    public void serviceThen(DatagramSocket socket,InetAddress clientAddress, Integer clientPort) throws IOException {
        //send OK response to caller
        byte[] send = this.OK_200().getBytes();
        DatagramPacket p = new DatagramPacket(new byte[ECHOMAX], ECHOMAX);
        p.setAddress(clientAddress);
        p.setPort(clientPort);
        p.setData(send);
        socket.send(p);
        REGISTER_LIST.put(this.username,clientAddress.getHostAddress()+":"+clientPort);
    }


    public String OK_200()
    {
        String okRes = "SIP/2.0 200 OK\r\n";

        okRes = okRes + UDPUtil.dealVia(this.via);
        okRes = okRes + "From: " + from + "\r\n";
        okRes = okRes + "To: " + to + "\r\n";
        okRes = okRes + "Call-ID: " + callId + "\r\n";
        okRes = okRes + "CSeq: " + cSeq + "\r\n";
        okRes = okRes + "Contact: " + contact + "\r\n";
        okRes = okRes + "Allow: " + allow + "\r\n";
        okRes = okRes + "Max-Forwards: " + maxForwards + "\r\n";
        okRes = okRes + "Allow-Events: " + allowEvents + "\r\n";
        okRes = okRes + "User-Agent: " + userAgent + "\r\n";
        okRes = okRes + "Supported: " + supported + "\r\n";
        okRes = okRes + "Expires: " + expires + "\r\n";
        okRes = okRes + "Content-Length: " + contentLength + "\r\n";

        okRes = okRes + "\r\n";
        return okRes;
    }

}
