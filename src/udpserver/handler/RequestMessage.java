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
 * @Time: 14:23
 * @Description:
 */
public class RequestMessage extends UDPMessage{
    String fwdIp;

    String fwdPort;

    String line1;
    public RequestMessage(){
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

        System.out.println("Request Terminated forwarded to " + fwdNumber + " at IP:PORT " + fwdIp + ":" + fwdPort + " .");
    }

    @Override
    public void serviceThen(DatagramSocket socket, InetAddress clientAddress, Integer clientPort) throws IOException {
        //forward 487
        byte[] send = this.forwardrequestTerminated(line1, UDPUtil.servIp, UDPUtil.servPort).getBytes();
        DatagramPacket p = new DatagramPacket(new byte[ECHOMAX], ECHOMAX);
        p.setAddress(InetAddress.getByName(fwdIp));
        p.setPort(Integer.parseInt(fwdPort.trim()));
        p.setData(send);
        socket.send(p);
    }

    public String forwardrequestTerminated(String line1,String servIp,int servPort)
    {
        String fwd_res = line1 + "\r\n";

        if(via.get(0).contains(","))
        {
            String modifiedVia = via.get(0).substring(via.get(0).indexOf(",")+1,via.get(0).length());
            via.set(0,modifiedVia);
        }
        else
            via.remove(0);


        //add via feilds
        for(int in=0;in<via.size();in++)
            fwd_res = fwd_res + "Via: " + via.get(in) + "\r\n";

        fwd_res = fwd_res + "From: " + from + "\r\n";
        fwd_res = fwd_res + "To: " + to + "\r\n";
        fwd_res = fwd_res+ "Call-ID: " + callId + "\r\n";
        fwd_res = fwd_res + "CSeq: " + cSeq + "\r\n";

        String modifiedContact = contact.substring(0,contact.indexOf("@")+1)+ servIp+">";
        fwd_res = fwd_res + "Contact: " + modifiedContact + "\r\n";

        fwd_res = fwd_res + "User-Agent: " + userAgent + "\r\n";

        fwd_res = fwd_res + "Content-Length: 0" + "\r\n\r\n";

        return fwd_res;
    }
}
