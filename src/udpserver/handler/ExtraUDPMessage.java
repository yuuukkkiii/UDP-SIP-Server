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
public abstract class ExtraUDPMessage extends UDPMessage{
    String v,o,s,c,t,m;
    ArrayList<String> a;

    public ExtraUDPMessage( ) {
        super();
        this.v = "";
        this.c = "";
        this.m = "";
        this.o = "";
        this.s = "";
        this.t = "";

        a = new ArrayList<>();
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
    @Override
    protected void getHeader(String header,String line){
        if (V.equals(header)){
            this.v = UDPUtil.getRowInfo(line,"=");
        }
        else if (C.equals(header)) {
            this.c = UDPUtil.getRowInfo(line, "=");
        }
        else if (M.equals(header)) {
            this.m = UDPUtil.getRowInfo(line, "=");
        }
        else if (O.equals(header)) {
            this.o = UDPUtil.getRowInfo(line, "=");
        }
        else if (S.equals(header)) {
            this.s = UDPUtil.getRowInfo(line, "=");
        }
        else if (T.equals(header)) {
            this.t = UDPUtil.getRowInfo(line, "=");
        }else if(A.equals(header)){
            this.a.add(UDPUtil.getRowInfo(line, "="));
        }else {
            super.getHeader(header, line);
        }
    }

    /**
     * @param header 头域
     * @param line 头域的内容
     */
    protected void getExtraInfo(String header ,String line){
        if (V.equals(header)){
            this.v = UDPUtil.getRowInfo(line,"=");
        }
        else if (C.equals(header)) {
            this.c = UDPUtil.getRowInfo(line, "=");
        }
        else if (M.equals(header)) {
            this.m = UDPUtil.getRowInfo(line, "=");
        }
        else if (O.equals(header)) {
            this.o = UDPUtil.getRowInfo(line, "=");
        }
        else if (S.equals(header)) {
            this.s = UDPUtil.getRowInfo(line, "=");
        }
        else if (T.equals(header)) {
            this.t = UDPUtil.getRowInfo(line, "=");
        }else if(A.equals(header)){
            this.a.add(UDPUtil.getRowInfo(line, "="));
        }
    }
}
