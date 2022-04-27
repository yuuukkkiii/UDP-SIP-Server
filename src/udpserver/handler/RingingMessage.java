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
 * @Time: 11:07
 * @Description:
 */
public class RingingMessage extends UDPMessage {
    String fwdIp;

    String fwdPort;

    public RingingMessage() {
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
        String fwdNumber = this.from.substring(this.from.indexOf(":") + 1, this.from.indexOf("@"));
        this.fwdIp = UDPUtil.extractIpOrPort(REGISTER_LIST.get(fwdNumber), 0);
        this.fwdPort = UDPUtil.extractIpOrPort(REGISTER_LIST.get(fwdNumber), 1);

        System.out.println("Ringing forwarded to " + fwdNumber + " at IP:PORT " + fwdIp + ":" + fwdPort + " .");
    }

    @Override
    public void serviceThen(DatagramSocket socket, InetAddress clientAddress, Integer clientPort) throws IOException {
        //forward ringing
        byte[] send = this.forwardRinging(UDPUtil.servIp).getBytes();
        DatagramPacket p = new DatagramPacket(new byte[ECHOMAX], ECHOMAX);
        p.setAddress(InetAddress.getByName(this.fwdIp));
        p.setPort(Integer.parseInt(this.fwdPort.trim()));
        p.setData(send);
        socket.send(p);
    }

    public String forwardRinging(String servIp) {
        String fwdRes = "SIP/2.0 180 Ringing\r\n";

        String modifiedVia = via.get(0).substring(via.get(0).indexOf(",") + 1, via.get(0).length());
        via.set(0, modifiedVia);

        //add via feilds
        for (int in = 0; in < via.size(); in++)
            fwdRes = fwdRes + "Via: " + via.get(in) + "\r\n";

        fwdRes = fwdRes + "From: " + from + "\r\n";
        fwdRes = fwdRes + "To: " + to + "\r\n";
        fwdRes = fwdRes + "Call-ID: " + callId + "\r\n";
        fwdRes = fwdRes + "CSeq: " + cSeq + "\r\n";

//        String modifiedContact = contact.substring(0, contact.indexOf("@") + 1) + servIp + ">";
//        fwdRes = fwdRes + "Contact: " + modifiedContact + "\r\n";

        fwdRes = fwdRes + "User-Agent: " + userAgent + "\r\n";
        fwdRes = fwdRes + "Supported: " + supported + "\r\n";
        fwdRes = fwdRes + "Content-Length: 0" + "\r\n\r\n";

        return fwdRes;
    }
}
