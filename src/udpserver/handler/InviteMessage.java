package udpserver.handler;

import udpserver.bean.CallDetails;
import udpserver.util.UDPUtil;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static udpserver.constant.UDPConst.*;

/**
 * @Author: zhaih
 * @Date: 2022/4/11
 * @Time: 16:54
 * @Description:
 */
public class InviteMessage extends ExtraUDPMessage{
    String contentType;

    String pEarlyMedia;

    String prefferedIdentity;

    CallDetails callDetails;

    String line1;

    public InviteMessage(){
        super();
        this.contentType="";
        this.pEarlyMedia="";
        this.prefferedIdentity="";
    }


    @Override
    public void fillMessage(StringTokenizer message) {
        String feildName = "";
        String nextLine = "";

        while (message.hasMoreTokens()) {
            nextLine = message.nextToken();
            int position;
            Pattern pattern = Pattern.compile("^a=(.*?)");
            Matcher matcher = pattern.matcher(nextLine);
            if(matcher.find()){
                position=nextLine.indexOf("=");
            }else{
                position=nextLine.indexOf(":");
                if(position==-1){
                    position=nextLine.indexOf("=");
                }
            }
            feildName = nextLine.substring(0, position==-1?0:position);
            if (CONTENT_TYPE.equals(feildName)){
                this.contentType = UDPUtil.getRowInfo(nextLine," ");
            } else if (P_EARLY_MEDIA.equals(feildName))
                this.pEarlyMedia = UDPUtil.getRowInfo(nextLine," ");
            else if (P_PREFERRED_IDENTITY.equals(feildName))
                this.prefferedIdentity = UDPUtil.getRowInfo(nextLine," ");
            else if (CONTENT_LENGTH.equals(feildName)) {/*这里可能会出问题*/
                this.contentLength = nextLine.substring(nextLine.indexOf(" ") + 1, nextLine.length());
               // break;
            }else{
                super.getHeader(feildName,nextLine);
            }
        }
        while (message.hasMoreTokens())   //Fill data into SDP part of msg
        {
            nextLine = message.nextToken();
            int sl = nextLine.indexOf("=");
            feildName = nextLine.substring(0, sl == -1 ? 0 : sl);
           super.getExtraInfo(feildName,nextLine);
        }
    }

    @Override
    public void doService(String line1) {
        this.line1=line1;
        //查找呼叫的是谁,获取用户名（手机号）
        String callerNumber = this.from.substring(this.from.indexOf(":") + 1, this.from.indexOf("@"));

        REGISTER_LIST.put("78910","10.0.2.14:12345");
        //find who to send this invite
        String calleeNumber = line1.substring(line1.indexOf(":") + 1, line1.indexOf("@"));
        String calleeIp = UDPUtil.extractIpOrPort(REGISTER_LIST.get(calleeNumber), 0);
        String calleePort = UDPUtil.extractIpOrPort(REGISTER_LIST.get(calleeNumber), 1);

        this.callDetails=new CallDetails();
        this.callDetails.setCallingName(callerNumber);
        this.callDetails.setCalledName(calleeNumber);
        this.callDetails.setCalledAddr(calleeIp);
        this.callDetails.setCalledPort(calleePort);
        System.out.println("INVITE coming from " + callerNumber + " to " + calleeNumber + " .");
    }

    @Override
    public void serviceThen(DatagramSocket socket, InetAddress clientAddress, Integer clientPort) throws IOException {
//        记录拨号信息
        this.callDetails.setCallingAddr(clientAddress.getHostAddress());
        this.callDetails.setCallingPort(clientPort.toString());

        //Now forward this packet to callee
        byte[] send1 = this.forwardInvite(line1, UDPUtil.servIp, UDPUtil.servPort).getBytes();
        DatagramPacket p1 = new DatagramPacket(new byte[ECHOMAX], ECHOMAX);

        //发送100try报文
        byte[] send = this.TRYING_100().getBytes();
        DatagramPacket p = new DatagramPacket(new byte[ECHOMAX], ECHOMAX);
        p.setAddress(clientAddress);
        p.setPort(clientPort);
        p.setData(send);
        socket.send(p);

        p1.setAddress(InetAddress.getByName(this.callDetails.getCalledAddr()));
        p1.setPort(Integer.parseInt(this.callDetails.getCalledPort()));
        p1.setData(send1);
        System.out.println(p1.getSocketAddress());
        socket.send(p1);
        //add details to CURRENTCALLS


        int callTarget = this.callId.indexOf("@");
        String callId = this.callId.substring(0, callTarget == -1 ? this.callId.length() : callTarget);
        CURRENTCALLS.put(callId, this.callDetails);
    }


    public String TRYING_100()      //method to generate trying response
    {
        String tryingRes = "SIP/2.0 100 TRYING\r\n";

        //add recieved to topmost via feild
        String upperViaFeild = via.get(0);
        String recieved = upperViaFeild.substring(upperViaFeild.indexOf(" ")+1, upperViaFeild.indexOf(":"));
        upperViaFeild = upperViaFeild + ";recieved=" + recieved;
        via.set(0, upperViaFeild);

        for(int in=0;in<via.size();in++)
            tryingRes = tryingRes + "Via: " + via.get(in) + "\r\n";

        tryingRes = tryingRes + "From: " + from + "\r\n";
        tryingRes = tryingRes + "To: " + to + "\r\n";
        tryingRes = tryingRes + "Call-ID: " + callId + "\r\n";
        tryingRes = tryingRes + "CSeq: " + cSeq + "\r\n";
        tryingRes = tryingRes + "Allow: " + allow + "\r\n";
        tryingRes = tryingRes + "User-Agent: " + userAgent + "\r\n";
        tryingRes = tryingRes + "Supported: " + supported + "\r\n";
        tryingRes = tryingRes + "Content-Length: 0" + "\r\n";

        tryingRes = tryingRes + "\r\n";
        return tryingRes;
    }

    public String forwardInvite(String line1,String servIp,int servPort)    //to generate fwd response
    {
        String fwdRes = line1 + "\r\n";

        //add this server's Via tag
        via.add(0,"SIP/2.0/UDP "+servIp+":"+servPort+";branch=z9hG4bK2d4790");

        for(int in=0;in<via.size();in++)
            fwdRes = fwdRes + "Via: " + via.get(in) + "\r\n";

        fwdRes = fwdRes + "From: " + from + "\r\n";
        fwdRes = fwdRes + "To: " + to + "\r\n";
        fwdRes = fwdRes+ "Call-ID: " + callId + "\r\n";
        fwdRes = fwdRes + "CSeq: " + cSeq + "\r\n";

        String modifiedContact = contact.substring(0,contact.indexOf("@")+1)+ servIp+contact.substring(contact.indexOf(";"));
        fwdRes = fwdRes + "Contact: " + modifiedContact + "\r\n";

        fwdRes = fwdRes + "Allow: " + allow + "\r\n";
        fwdRes = fwdRes + "Supported: " + supported + "\r\n";
        fwdRes = fwdRes + "Content-Type: " + contentType + "\r\n";
        fwdRes = fwdRes + "Max-Forwards: " + (Integer.parseInt(maxForwards.trim())-1) + "\r\n";
        fwdRes = fwdRes + "User-Agent: " + userAgent + "\r\n";
        fwdRes = fwdRes + "Content-Length: " + contentLength + "\r\n\r\n";

        fwdRes = fwdRes + "v=" + v + "\r\n";
        fwdRes = fwdRes + "o=" + o + "\r\n";
        fwdRes = fwdRes + "s=" + s + "\r\n";
        fwdRes = fwdRes + "c=" + c + "\r\n";
        fwdRes = fwdRes + "t=" + t + "\r\n";
        fwdRes = fwdRes + "m=" + m + "\r\n";

        for(int in=0;in<a.size();in++)
            fwdRes = fwdRes + "a=" + a.get(in) + "\r\n";

        return fwdRes;
    }
}
