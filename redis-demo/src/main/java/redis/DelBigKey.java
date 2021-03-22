package redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;
import redis.clients.jedis.Tuple;

import java.util.List;
import java.util.Map;

/**
 * 删除big-key
 *
 * @author wangzhengpeng
 */
public class DelBigKey {

    /**
     * 1. Hash删除: hscan + hdel
     */
    public void delBigHash(String host, int port, String password, String bigHashKey) {
        Jedis jedis = new Jedis(host, port);
        if (password != null && !"".equals(password)) {
            jedis.auth(password);
        }
        ScanParams scanParams = new ScanParams().count(100);
        String cursor = "0";
        do {
            ScanResult<Map.Entry<String, String>> scanResult = jedis.hscan(bigHashKey, cursor, scanParams);
            List<Map.Entry<String, String>> entryList = scanResult.getResult();
            if (entryList != null && !entryList.isEmpty()) {
                for (Map.Entry<String, String> entry : entryList) {
                    jedis.hdel(bigHashKey, entry.getKey());
                }
            }
            cursor = scanResult.getCursor();
        } while (!"0".equals(cursor));

        //删除bigkey
        jedis.del(bigHashKey);
    }

    /**
     * 2. List删除: ltrim
     */
    public void delBigList(String host, int port, String password, String bigListKey) {
        Jedis jedis = new Jedis(host, port);
        if (password != null && !"".equals(password)) {
            jedis.auth(password);
        }
        long llen = jedis.llen(bigListKey);
        int counter = 0;
        int left = 100;
        while (counter < llen) {
            //每次从左侧截掉100个
            jedis.ltrim(bigListKey, left, llen);
            counter += left;
        }
        //最终删除key
        jedis.del(bigListKey);
    }

    /**
     * 3. Set删除: sscan + srem
     */
    public void delBigSet(String host, int port, String password, String bigSetKey) {
        Jedis jedis = new Jedis(host, port);
        if (password != null && !"".equals(password)) {
            jedis.auth(password);
        }
        ScanParams scanParams = new ScanParams().count(100);
        String cursor = "0";
        do {
            ScanResult<String> scanResult = jedis.sscan(bigSetKey, cursor, scanParams);
            List<String> memberList = scanResult.getResult();
            if (memberList != null && !memberList.isEmpty()) {
                for (String member : memberList) {
                    jedis.srem(bigSetKey, member);
                }
            }
            cursor = scanResult.getCursor();
        } while (!"0".equals(cursor));

        //删除bigkey
        jedis.del(bigSetKey);
    }

    /**
     * 4. SortedSet删除: zscan + zrem
     */
    public void delBigZset(String host, int port, String password, String bigZsetKey) {
        Jedis jedis = new Jedis(host, port);
        if (password != null && !"".equals(password)) {
            jedis.auth(password);
        }
        ScanParams scanParams = new ScanParams().count(100);
        String cursor = "0";
        do {
            ScanResult<Tuple> scanResult = jedis.zscan(bigZsetKey, cursor, scanParams);
            List<Tuple> tupleList = scanResult.getResult();
            if (tupleList != null && !tupleList.isEmpty()) {
                for (Tuple tuple : tupleList) {
                    jedis.zrem(bigZsetKey, tuple.getElement());
                }
            }
            cursor = scanResult.getCursor();
        } while (!"0".equals(cursor));

        //删除bigkey
        jedis.del(bigZsetKey);
    }

    /**
     * unlink
     */
    public void unlink(String host, int port, String[] keys) {
        Jedis jedis = new Jedis(host, port);
        jedis.unlink(keys);
    }
}
