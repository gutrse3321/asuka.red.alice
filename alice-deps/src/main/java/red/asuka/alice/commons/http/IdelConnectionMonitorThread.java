package red.asuka.alice.commons.http;

import org.apache.http.conn.HttpClientConnectionManager;

/**
 * @Author: Tomonori
 * @Date: 2019/12/18 10:34
 * @Title: 关闭过期连接
 * @Desc: ↓ ↓ ↓ ↓ ↓
 * -----
 */
public class IdelConnectionMonitorThread extends Thread {

    private final HttpClientConnectionManager clientConnectionManager;
    //volatile: main memory变量，不是各个线程的work memory取的。不保证并发正确性
    //https://www.cnblogs.com/blog-Aevin/p/9302678.html
    private volatile boolean shutdown;

    public IdelConnectionMonitorThread(HttpClientConnectionManager clientConnectionManager) {
        super();
        this.clientConnectionManager = clientConnectionManager;
    }

    @Override
    public void run() {
        try {
            while (!shutdown) {
                //synchronized保证方法或者代码块在运行时，同一时刻只有一个方法可以进入到临界区
                //还可以保证共享变量的内存可见性
                synchronized (this) {
                    wait(5000);
                    if (null != clientConnectionManager) {
                        clientConnectionManager.closeExpiredConnections();
                    }
                }
            }
        } catch (InterruptedException e) {
        }
    }

    public void shutDown() {
        shutdown = true;
        synchronized (this) {
            notifyAll();
        }
    }
}
