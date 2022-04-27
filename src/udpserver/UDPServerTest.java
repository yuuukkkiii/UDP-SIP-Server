package udpserver;

import udpserver.constant.UDPConst;
import udpserver.handler.*;
import udpserver.util.UDPUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.StringTokenizer;

public class UDPServerTest
{
    private static final int ECHOMAX = 2048;    //Stores max length of recieved message in bytes
    //HashMap of REGISTERED users
    private static HashMap<String,String> REGISTERED = new HashMap<>();
    //HashMap of Current Calls going on
    private static HashMap<String,callDetails> CURRENTCALLS = new HashMap<>();
    
    public static void main(String[] args) throws IOException
    {           
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        
        System.out.print("Enter the port of the server (int): ");
        int servPort = Integer.parseInt(br.readLine());
        
        System.out.print("Enter the IP address of the server (String): ");
        String servIp = br.readLine();

        UDPUtil.setServIp(servIp);
        UDPUtil.setServPort(servPort);
        
        System.out.println("Server Started. Listening for requests....");
        
        //Create new packet to recieve into
        DatagramSocket socket = new DatagramSocket(servPort) ;
        DatagramPacket packet = new DatagramPacket(new byte[ECHOMAX], ECHOMAX);

            
        while (true)    //Loops infinitely
        {

            socket.receive(packet);     //阻塞到收到消息
            InetAddress clientAddress = packet.getAddress();    //获取用户 IP
            SocketAddress socketAddress=packet.getSocketAddress();
            int clientPort = packet.getPort();      //获取用户 Port

            String requestMsg = UDPUtil.getUDPMessageInfo(packet);    //从报文中解析到字符串
            if("".equals(requestMsg.trim())){
                System.out.println("空");
                continue;
            }

            MessageHandler messageHandler=new MessageHandler();

            try {
                StringTokenizer st = new StringTokenizer(requestMsg, "\r\n");// \r\n为分隔符
                String line1 = st.nextToken();  //获取首行标记
                String typeOfMsg = line1.substring(0, line1.indexOf(" ")); //获取报文类型
                System.out.println("你是谁，我是： "+line1);
                if (UDPConst.REGISTER.equals(typeOfMsg)){/*注册报文*/
                    messageHandler.setMessage(new RegisterMessage());
                }else if(UDPConst.INVITE.equals(typeOfMsg)){/*邀请报文*/
                    messageHandler.setMessage(new InviteMessage());
                }else if(UDPConst.RINGING.equals(line1)){/*响铃报文*/
                    messageHandler.setMessage(new RingingMessage());
                }else if(UDPConst.OK.equals(line1)){ /*200 OK 报文*/
                    messageHandler.setMessage(new OkMessage());
                }else if(UDPConst.ACK.equals(typeOfMsg)){
                    messageHandler.setMessage(new ACKMessage());
                }else if(UDPConst.BYE.equals(typeOfMsg)){
                    messageHandler.setMessage(new BYEMessage());
                }else if(UDPConst.CANCEL.equals(typeOfMsg)){
                    messageHandler.setMessage(new CancelMessage());
                }else if(line1.contains(UDPConst.REQUEST)){
                    messageHandler.setMessage(new RequestMessage());
                }else if(line1.contains(UDPConst.BUSY)){
                    messageHandler.setMessage(new BusyMessage());
                }else{
                    messageHandler=new MessageHandler();
                    System.out.println("无法接收");
                }

//                    填充报文
                messageHandler.fillMessage(st);
//                进行相关处理
                messageHandler.doService(line1);
//                处理后返回
                messageHandler.serviceThen(socket,clientAddress,clientPort);

                packet.setLength(ECHOMAX);

            }catch (Exception e){
                e.printStackTrace();
            }
        }
            
    }
}