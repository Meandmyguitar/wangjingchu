package redis.bitmap;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Tuple;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

/**
 * 操作记录
 *
 * @author wangzhengpeng
 */
public class OperationLogDemo {

    private Jedis jedis = new Jedis("127.0.0.1");

    public void recordLog(String operation, long userId) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String format = simpleDateFormat.format(new Date());
        jedis.setbit("operation:" + format, userId, "1");
    }

    public boolean getLog(String operation, long userId) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String format = simpleDateFormat.format(new Date());
        return jedis.getbit("operation:" + format, userId);
    }

    public static void main(String[] args) {
        OperationLogDemo operationLogDemo = new OperationLogDemo();
        operationLogDemo.recordLog("wzp", 123L);

        boolean wzp = operationLogDemo.getLog("wzp", 123L);
        System.out.println("getLog is :" + wzp);
    }

}
