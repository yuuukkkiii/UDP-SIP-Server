package udpserver.other;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author: zhaih
 * @Date: 2022/4/21
 * @Time: 14:37
 * @Description:
 */
public class TechTest {

    public  static int maxInt=1000;


    public static  int now=0;

    public static  int count=0;


    public static void main(String[] args) {

        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(15, 30, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
        for(int i=0; i<=1000;i++){
            threadPoolExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    int num = TechTest.getNow();
                    System.out.println(Thread.currentThread().getName());
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    TechTest.doCount(num);
                }
            });
        }
    }

    public static synchronized int getNow(){
        return now>maxInt?maxInt:now++;
    }

    public static synchronized void doCount(int num){
        count+=num;
       System.out.println("传入的数据："+num+"=======加和："+count);
    }
}
