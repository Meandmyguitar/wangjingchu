package redis.hash;

import redis.clients.jedis.Jedis;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

/**
 * 用户会话管理案例
 */
public class SessionDemo {

    private Jedis jedis = new Jedis("127.0.0.1");

    /**
     * 检查session是否有效
     */
    public boolean isSessionValid(String token) throws Exception {
        // 校验token是否为空
        if (token == null || "".equals(token)) {
            return false;
        }

        // 这里拿到的session可能就是一个json字符串
        String session = jedis.hget("sessions", "session::" + token);
        if (session == null || "".equals(session)) {
            return false;
        }

        // 检查一下这个session是否在有效期内
        String expireTime = jedis.hget("sessions::expire_time", "session::" + token);
        if (expireTime == null || "".equals(expireTime)) {
            return false;
        }

        Date expireTimeDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(expireTime);

        if (new Date().after(expireTimeDate)) {
            return false;
        }

        // 如果token不为空，而且获取到的session不为空，而且session没过期
        // 此时可以认为session在有效期内
        return true;
    }

    /**
     * 模拟的登录方法
     */
    public String login(String username, String password) {
        System.out.println("基于用户名和密码登录：" + username + ", " + password);
        long userId = new Random().nextInt() * 100;
        String token = UUID.randomUUID().toString().replace("-", "");

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.HOUR, 24);
        Date expireTime = calendar.getTime();

        jedis.hset("sessions", "session::" + token, String.valueOf(userId));
        jedis.hset("sessions::expire_time", "session::" + token,
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(expireTime));
        return token;
    }

    public static void main(String[] args) throws Exception {
        SessionDemo demo = new SessionDemo();

        // 第一次访问系统，token都是空的
        boolean isSessionValid = demo.isSessionValid(null);
        System.out.println("第一次访问系统的session校验结果：" + (isSessionValid == true ? "通过" : "不通过"));

        // 强制性进行登录，获取到token
        String token = demo.login("zhangsan", "123456");
        System.out.println("登陆过后拿到令牌：" + token);

        // 第二次再次访问系统，此时是可以访问的
        isSessionValid = demo.isSessionValid(token);
        System.out.println("第二次访问系统的session校验结果：" + (isSessionValid == true ? "通过" : "不通过"));
    }

}
