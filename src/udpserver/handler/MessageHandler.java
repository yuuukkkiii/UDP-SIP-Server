package udpserver.handler;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.StringTokenizer;

/**
 * @Author: zhaih
 * @Date: 2022/4/11
 * @Time: 15:01
 * @Description:
 */
public class MessageHandler {
    private UDPMessage message;

    public UDPMessage getMessage() {
        return message;
    }

    public void setMessage(UDPMessage message) {
        this.message = message;
    }

    public void fillMessage(StringTokenizer message){
        this.message.fillMessage(message);
    }

    public void doService(String line1){
        this.message.doService(line1);
    }

    public void serviceThen(DatagramSocket socket,InetAddress clientAddress, Integer clientPort) throws IOException {
        this.message.serviceThen(socket,clientAddress,clientPort);
    }
}
