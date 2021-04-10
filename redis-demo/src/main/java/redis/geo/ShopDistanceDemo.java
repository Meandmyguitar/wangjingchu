package redis.geo;

import redis.clients.jedis.Jedis;

/**
 * @author wangzhengpeng
 */
public class ShopDistanceDemo {

    private Jedis jedis = new Jedis("127.0.0.1");

    /**
     * 添加地址位置
     */
    public void addLocation(String name, double longitude, double latitude) {
        jedis.geoadd("location_data", longitude, longitude, name);
    }

    /**
     * 用户到商家的距离
     */
    public Double Distance(String user, String shop) {
        return jedis.geodist("location_data", user, shop);
    }

    public static void main(String[] args) {

    }


}
