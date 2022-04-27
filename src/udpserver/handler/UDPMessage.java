package udpserver.handler;

import udpserver.util.UDPUtil;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.StringTokenizer;

import static udpserver.constant.UDPConst.*;

/**
 * @Author: zhaih
 * @Date: 2022/4/11
 * @Time: 15:05
 * @Description:
 */
public abstract class UDPMessage {
    String from,to,callId,cSeq,contact,allow,maxForwards,
            userAgent,supported,contentLength;
    ArrayList<String> via;
    public UDPMessage( ) {
        this.via = new ArrayList<>();
        this.from = "";
        this.to = "";
        this.callId = "";
        this.cSeq = "";
        this.contact = "";
        this.allow = "";
        this.maxForwards = "";
        this.userAgent = "";
        this.supported = "";
        this.contentLength = "";
    }

    /**
     * @param message 报文内容
     * 完善报文信息
     */
    public abstract void fillMessage(StringTokenizer message);

    public abstract void doService(String line1);

    public abstract void serviceThen(DatagramSocket socket,InetAddress clientAddress, Integer clientPort) throws IOException;

    /**
     * @param header 头域
     * @param line 头域的内容
     */
    protected void getHeader(String header,String line){
        if (VIA.equals(header)){
            this.via.add(UDPUtil.getRowInfo(line," "));
        }
        else if (FROM.equals(header)) {
            this.from = UDPUtil.getRowInfo(line, " ");
        }
        else if (TO.equals(header)) {
            this.to = UDPUtil.getRowInfo(line, " ");
        }
        else if (CALL_ID.equals(header)) {
            this.callId = UDPUtil.getRowInfo(line, " ");
        }
        else if (CSEQ.equals(header)) {
            this.cSeq = UDPUtil.getRowInfo(line, " ");
        }
        else if (CONTACT.equals(header)) {
            this.contact = UDPUtil.getRowInfo(line, " ");
        }
        else if (ALLOW.equals(header)) {
            this.allow = UDPUtil.getRowInfo(line, " ");
        }
        else if (MAX_FORWARDS.equals(header)) {
            this.maxForwards = UDPUtil.getRowInfo(line, " ");
        }
        else if (USER_AGENT.equals(header)) {
            this.userAgent = UDPUtil.getRowInfo(line, " ");
        }
        else if (SUPPORTED.equals(header)) {
            this.supported = UDPUtil.getRowInfo(line, " ");
        }
        else if (CONTENT_LENGTH.equals(header)) {
            this.contentLength = UDPUtil.getRowInfo(line, " ");
        }
    }

}
