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
 * @Time: 14:24
 * @Description:
 */
public class BusyMessage extends RequestMessage{
    public BusyMessage(){
        super();
    }

    @Override
    public void doService(String line1) {
//whom to send this bye
        String fwdNumber = this.to.substring(this.to.indexOf(":") + 1, this.to.indexOf("@"));
        super.doService(line1);
        System.out.println("Busy Here forwarded to " + fwdNumber + " at IP:PORT " + fwdIp + ":" + fwdPort + " .");
    }

    @Override
    public void serviceThen(DatagramSocket socket, InetAddress clientAddress, Integer clientPort) throws IOException {
        //forward to whom
        String fwdNumber = this.from.substring(this.from.indexOf(":") + 1, this.from.indexOf("@"));
        String fwdIp = UDPUtil.extractIpOrPort(REGISTER_LIST.get(fwdNumber), 0);
        String fwdPort = UDPUtil.extractIpOrPort(REGISTER_LIST.get(fwdNumber), 1);

        System.out.println("Busy Here forwarded to " + fwdNumber + " at IP:PORT " + fwdIp + ":" + fwdPort + " .");

        //forward 486
        byte[] send = this.forwardrequestTerminated(line1, UDPUtil.servIp, UDPUtil.servPort).getBytes();
        DatagramPacket p = new DatagramPacket(new byte[ECHOMAX], ECHOMAX);
        p.setAddress(InetAddress.getByName(fwdIp));
        p.setPort(Integer.parseInt(fwdPort.trim()));
        p.setData(send);
        socket.send(p);

        //remove from CURRENTCALLS
        String callId = this.callId.substring(0, this.callId.indexOf("@"));
        CURRENTCALLS.remove(callId);
    }
}
