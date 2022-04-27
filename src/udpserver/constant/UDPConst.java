package udpserver.constant;

import udpserver.bean.CallDetails;

import java.util.HashMap;

/**
 * @Author: zhaih
 * @Date: 2022/4/11
 * @Time: 14:21
 * @Description:
 */
public class UDPConst {

  public static final HashMap<String,String> REGISTER_LIST = new HashMap<>();

  public static HashMap<String, CallDetails> CURRENTCALLS = new HashMap<>();

  public static final int ECHOMAX = 2048;

  /*--------------------- 报文类型-----------------------*/
  public static final String REGISTER="REGISTER";

  public static final String INVITE="INVITE";

  public static final String RINGING="SIP/2.0 180 Ringing";

  public static final String OK="SIP/2.0 200 Ok";

  public static final String ACK="ACK";

  public static final String BYE="BYE";

  public static final String CANCEL="CANCEL";

  public static final String BUSY="486 Busy";

  public static final String REQUEST="487 Request";

  /*--------------------- 报文头域-----------------------*/

  /**
   * Via头域是用来描述请求当前经历的路径，并且标志了应答所应当经过的路径
   */
  public static final String VIA="Via";
  /**
   * From头域表示了请求的来源地
   */
  public static final String FROM="From";
  /**
   * To头域定义了逻辑上请求的接收者
   */
  public static final String TO="To";
  /**
   * Call-ID是一个在一系列消息中，区分一组消息的唯一标志
   */
  public static final String CALL_ID="Call-ID";
  /**
   * CSeq 头域是用来区分事务的顺序，随着事务递增
   */
  public static final String CSEQ="CSeq";
  /**
   * 在INVITE请求和200 OK响应里面必须存在
   */
  public static final String CONTACT="Contact";
  /**
   * 给出代理服务器支持的所有请求消息类型列表。
   */
  public static final String ALLOW="Allow";
  /**
   * 定义一个请求到达其目的地址所允许经过的中转站的最大值
   */
  public static final String MAX_FORWARDS="Max-Forwards";
  /**
   * 允许事件
   */
  public static final String ALLOW_EVENTS="Allow-Events";
  /**
   *包含有发起请求的用户终端的信息
   */
  public static final String USER_AGENT="User-Agent";
  /**
   * SIP 协议中定义的 100 类临时响应消息的传输是不可靠的，100rel 扩展为 100 类响应消息的可靠传输提供了相应的机制。Supported头域说明options tags描述那些SIP扩展
   */
  public static final String SUPPORTED="Supported";
  /**
   *  Expires头域给定了消息（或者内容）过期的相关时间
   */
  public static final String EXPIRES="Expires";
  /**
   * 表示消息体的大小，为十进制值。
   */
  public static final String CONTENT_LENGTH="Content-Length";


  /*--------------------- 报文头域扩充字段-----------------------*/

  /**
   *
   */
  public static final String V="v";

  /**
   *
   */
  public static final String C="c";

  /**
   *
   */
  public static final String M="m";
  /**
   *
   */
  public static final String O="o";

  /**
   *
   */
  public static final String S="s";
  /**
   *
   */
  public static final String T="t";

  /**
   *
   */
  public static final String A="a";

  /*--------------------- INVITE报文扩充字段-----------------------*/
  public static final String CONTENT_TYPE="Content-Type";

  public static final String P_EARLY_MEDIA ="P-Early-Media";
  public static final String P_PREFERRED_IDENTITY ="P-Preferred-Identity";


}
