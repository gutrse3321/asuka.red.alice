package red.asuka.alice.commons.support.generator;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Tomonori
 * @mail gutrse3321@live.com
 * @data 2020-11-18 15:40
 *
 * 推特snowflake 64位自增ID算法
 */
public class IdGenerator {

    private long hostId;                                     //主机地址
    private long sequence = 0L;                              //毫秒自增值
    private long twepoch = 1577905445000L;                   //起始时间 2020-1-02 03:04:05
    private long hostIdBits = 16L;                           //主机值占位
    private long sequenceBits = 6L;                          //毫秒自增占位,该参数决定1毫秒之内并发数量
    private long hostIdShift = sequenceBits;                 //主机值偏移位
    private long timestampShift = sequenceBits + hostIdBits; //时间戳偏移位
    private long maxHostId = -1L ^ (-1L << hostIdBits);      //HostId 最大值
    private long maxSequence = -1L ^ (-1L << sequenceBits);  //毫秒自增最大值
    private long lastTimestamp = 0L;                         //最后请求时间

    private Lock lock = new ReentrantLock();

    public IdGenerator(long hostId) {
        if (hostId > maxHostId || maxHostId < 0)
            throw new IllegalArgumentException(String.format("host Id can't be greater than %d or less than 0", maxHostId));
        this.hostId = hostId;
    }

    public long nextId() {
        lock.lock();
        try {
            long timestamp = timeGen();

            if (timestamp < lastTimestamp) {
                throw new RuntimeException(String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
            }

            if (lastTimestamp == timestamp) {
                sequence = (sequence + 1) & maxSequence;
                if (sequence == 0) {
                    timestamp = tilNextMillis(lastTimestamp);
                }
            } else {
                sequence = 0L;
            }
            lastTimestamp = timestamp;
            return ((timestamp - twepoch) << timestampShift) | (hostId << hostIdShift) | sequence;
        } finally {
            lock.unlock();
        }
    }

    public String nextIdString() {
        return String.valueOf(nextId());
    }

    protected long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    protected long timeGen() {
        return System.currentTimeMillis();
    }

}
