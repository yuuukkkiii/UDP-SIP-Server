package udpserver.bean;

/**
 * @Author: zhaih
 * @Date: 2022/4/12
 * @Time: 10:46
 * @Description:
 */
public class CallDetails {
    private String calledName;

    private String calledAddr;

    private String calledPort;

    private String callingName;

    private String callingAddr;

    private String callingPort;

    public CallDetails(){
        this.calledAddr="";
        this.calledName="";
        this.calledPort="";
        this.callingAddr="";
        this.callingName="";
        this.callingPort="";
    }

    public String getCalledName() {
        return calledName;
    }

    public void setCalledName(String calledName) {
        this.calledName = calledName;
    }

    public String getCalledAddr() {
        return calledAddr;
    }

    public void setCalledAddr(String calledAddr) {
        this.calledAddr = calledAddr;
    }

    public String getCalledPort() {
        return calledPort;
    }

    public void setCalledPort(String calledPort) {
        this.calledPort = calledPort;
    }

    public String getCallingName() {
        return callingName;
    }

    public void setCallingName(String callingName) {
        this.callingName = callingName;
    }

    public String getCallingAddr() {
        return callingAddr;
    }

    public void setCallingAddr(String callingAddr) {
        this.callingAddr = callingAddr;
    }

    public String getCallingPort() {
        return callingPort;
    }

    public void setCallingPort(String callingPort) {
        this.callingPort = callingPort;
    }
}
