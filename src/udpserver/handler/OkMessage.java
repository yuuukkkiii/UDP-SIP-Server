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
 * @Time: 11:21
 * @Description:
 */
public class OkMessage extends InviteMessage{
    String fwdIp;

    String fwdPort;

    @Override
    public void fillMessage(StringTokenizer message) {
        super.fillMessage(message);
    }

    @Override
    public void doService(String line1) {
        String fwdNumber = this.from.substring(this.from.indexOf(":") + 1, this.from.indexOf("@"));
        this.fwdIp = UDPUtil.extractIpOrPort(REGISTER_LIST.get(fwdNumber), 0);
        this.fwdPort = UDPUtil.extractIpOrPort(REGISTER_LIST.get(fwdNumber), 1);

        System.out.println("OK forwarded to " + fwdNumber + " at IP:PORT " + fwdIp + ":" + fwdPort + " .");
    }

    @Override
    public void serviceThen(DatagramSocket socket, InetAddress clientAddress, Integer clientPort) throws IOException {
        //forward OK
        byte[] send = this.forwardOk(UDPUtil.servIp).getBytes();
        DatagramPacket p = new DatagramPacket(new byte[ECHOMAX], ECHOMAX);
        p.setAddress(InetAddress.getByName(fwdIp));
        p.setPort(Integer.parseInt(fwdPort.trim()));
        p.setData(send);
        socket.send(p);
    }

    public String forwardOk(String servIp)
    {

        String fwdRes = "SIP/2.0 200 Ok\r\n";

//        if(via.get(0).contains(","))
//        {
//            String modifiedVia = via.get(0).substring(via.get(0).indexOf(",")+1);
//            via.set(0,modifiedVia);
//        }
//        else
//            via.remove(0);


        //add via feilds
        for(int in=0;in<via.size();in++)
            fwdRes = fwdRes + "Via: " + via.get(in) + "\r\n";

        fwdRes = fwdRes + "From: " + from + "\r\n";
        fwdRes = fwdRes + "To: " + to + "\r\n";
        fwdRes = fwdRes+ "Call-ID: " + callId + "\r\n";
        fwdRes = fwdRes + "CSeq: " + cSeq + "\r\n";

        fwdRes = fwdRes + "User-Agent: " + userAgent + "\r\n";
        fwdRes = fwdRes + "Supported: " + supported + "\r\n";
        fwdRes = fwdRes + "Allow: " + allow + "\r\n";

        String modifiedContact = contact.substring(0,contact.indexOf("@")+1)+ servIp+contact.substring(contact.indexOf(";"));
        fwdRes = fwdRes + "Contact: " + modifiedContact + "\r\n";
        fwdRes = fwdRes + "Content-Type: " + contentType + "\r\n";


        if(cSeq.contains("CANCEL"))     //If this OK is in response to a CANCEL then
        {
            modifiedContact = contact.substring(0,contact.indexOf("@")+1)+ servIp+">";
            fwdRes = fwdRes + "Contact: " + modifiedContact + "\r\n";
        }

        fwdRes = fwdRes + "Content-Length: " + contentLength + "\r\n\r\n";

        if(!cSeq.contains("BYE") && !cSeq.contains("CANCEL"))   /*If this OK is not in response to a
                                                                  BYE or a CANCEL then
                                                                 */
        {
            fwdRes = fwdRes + "v=" + v + "\r\n";
            fwdRes = fwdRes + "o=" + o + "\r\n";
            fwdRes = fwdRes + "s=" + s + "\r\n";
            fwdRes = fwdRes + "c=" + c + "\r\n";
            fwdRes = fwdRes + "t=" + t + "\r\n";
            fwdRes = fwdRes + "m=" + m + "\r\n";

            for(int in=0;in<a.size();in++)
                fwdRes = fwdRes + "a=" + a.get(in) + "\r\n";
        }
        return fwdRes;
    }
}
