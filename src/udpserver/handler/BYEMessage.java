package udpserver.handler;

import udpserver.util.UDPUtil;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.StringTokenizer;

import static udpserver.constant.UDPConst.*;

/**
 * @Author: zhaih
 * @Date: 2022/4/12
 * @Time: 14:03
 * @Description:
 */
public class BYEMessage extends UDPMessage {
    String fwdIp;

    String fwdPort;

    String line1;
    public BYEMessage() {
        super();
    }

    @Override
    public void fillMessage(StringTokenizer message) {
        String feildName = "";
        String nextLine = "";
        while (message.hasMoreTokens()) {
            nextLine = message.nextToken();
            feildName = nextLine.substring(0, nextLine.indexOf(":"));
            super.getHeader(feildName, nextLine);
        }

    }

    @Override
    public void doService(String line1) {
        this.line1=line1;
        //whom to send this bye
        String fwdNumber = this.to.substring(this.to.indexOf(":") + 1, this.to.indexOf("@"));
        this.fwdIp = UDPUtil.extractIpOrPort(REGISTER_LIST.get(fwdNumber), 0);
        this.fwdPort = UDPUtil.extractIpOrPort(REGISTER_LIST.get(fwdNumber), 1);

        System.out.println("BYE forwarded to " + fwdNumber + " at IP:PORT " + fwdIp + ":" + fwdPort + " .");
    }

    @Override
    public void serviceThen(DatagramSocket socket, InetAddress clientAddress, Integer clientPort) throws IOException {
//Forward BYE
        byte[] send = this.forwardBye(line1, UDPUtil.servIp, UDPUtil.servPort).getBytes();
        DatagramPacket p = new DatagramPacket(new byte[ECHOMAX], ECHOMAX);
        p.setAddress(InetAddress.getByName(fwdIp));
        p.setPort(Integer.parseInt(fwdPort.trim()));
        p.setData(send);
        socket.send(p);

        //remove this call from CURRENTCALLS
        String callId = this.callId.substring(0, this.callId.indexOf("@"));

        if (CURRENTCALLS.containsKey(callId)) {
            System.out.println("The call from " + CURRENTCALLS.get(callId).getCallingAddr() + " to " + CURRENTCALLS.get(callId).getCalledAddr() + " has been ENDED.");
            CURRENTCALLS.remove(callId);
        }
    }

    public String forwardBye(String line1,String servIp,int servPort)
    {
        String fwd_res = line1 + "\r\n";

        //add recieved to topmost via feild
        String upperViaFeild = via.get(0);
        String recieved = upperViaFeild.substring(upperViaFeild.indexOf(" ")+1, upperViaFeild.indexOf(":"));
        upperViaFeild = upperViaFeild + ";recieved=" + recieved;
        via.set(0, upperViaFeild);

        //add this server's Via tag
        via.add(0,"SIP/2.0/UDP "+servIp+":"+servPort+";branch=z9hG4bK2d4790");

        for(int in=0;in<via.size();in++)    //add all via headers
            fwd_res = fwd_res + "Via: " + via.get(in) + "\r\n";


        fwd_res = fwd_res + "From: " + from + "\r\n";
        fwd_res = fwd_res + "To: " + to + "\r\n";
        fwd_res = fwd_res+ "Call-ID: " + callId + "\r\n";
        fwd_res = fwd_res + "CSeq: " + cSeq + "\r\n";

        String modifiedContact = contact.substring(0,contact.indexOf("@")+1)+ servIp+">";
        fwd_res = fwd_res + "Contact: " + modifiedContact + "\r\n";

        fwd_res = fwd_res + "Max-Forwards: " + (Integer.parseInt(maxForwards.trim())-1) + "\r\n";
        fwd_res = fwd_res + "User-Agent: " + userAgent + "\r\n";
        fwd_res = fwd_res + "Content-Length: " + contentLength + "\r\n\r\n";
        return fwd_res;
    }
}
