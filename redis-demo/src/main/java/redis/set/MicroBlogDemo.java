package redis.set;

import redis.clients.jedis.Jedis;

import java.util.Random;
import java.util.Set;

/**
 * 微博案例
 */
public class MicroBlogDemo {

    private Jedis jedis = new Jedis("127.0.0.1");

    /**
     * 关注别人
     */
    public void follow(long userId, long followUserId) {
        jedis.sadd("user::" + followUserId + "::followers", String.valueOf(userId));
        jedis.sadd("user::" + userId + "::follow_users", String.valueOf(followUserId));
    }

    /**
     * 取消关注别人
     */
    public void unfollow(long userId, long followUserId) {
        jedis.srem("user::" + followUserId + "::followers", String.valueOf(userId));
        jedis.srem("user::" + userId + "::follow_users", String.valueOf(followUserId));
    }

    /**
     * 查看有哪些人关注了自己
     */
    public Set<String> getFollowers(long userId) {
        return jedis.smembers("user::" + userId + "::followers");
    }

    /**
     * 查看关注了自己的人数
     */
    public long getFollowersCount(long userId) {
        return jedis.scard("user::" + userId + "::followers");
    }

    /**
     * 查看自己关注了哪些人
     */
    public Set<String> getFollowUsers(long userId) {
        return jedis.smembers("user::" + userId + "::follow_users");
    }

    /**
     * 查看自己关注的人数
     */
    public long getFollowUsersCount(long userId) {
        return jedis.scard("user::" + userId + "::follow_users");
    }

    /**
     * 获取用户跟其他用户之间共同关注的人有哪些
     */
    public Set<String> getSameFollowUsers(long userId, long otherUserId) {
        return jedis.sinter("user::" + userId + "::follow_users",
                "user::" + otherUserId + "::follow_users");
    }

    /**
     * 获取给我推荐的可关注人
     * 我关注的某个好友关注的一些人，我没关注那些人，此时推荐那些人给我
     */
    public Set<String> getRecommendFollowUsers(long userId, long otherUserId) {
        return jedis.sdiff("user::" + otherUserId + "::follow_users",
                "user::" + userId + "::follow_users");
    }

    /**
     * 查找bigkeys
     *
     * wangzhengpeng@wangzhengpengdeMacBook-Pro bin % redis-cli --bigkeys
     *
     * # Scanning the entire keyspace to find biggest keys as well as
     * # average sizes per key type.  You can use -i 0.1 to sleep 0.1 sec
     * # per 100 SCAN commands (not usually needed).
     *
     * [00.00%] Biggest set    found so far '"user::130::follow_users"' with 98460 members
     * [00.00%] Biggest set    found so far '"user::110::follow_users"' with 4529866 members
     *
     * -------- summary -------
     *
     * Sampled 3 keys in the keyspace!
     * Total key length in bytes is 69 (avg len 23.00)
     *
     * Biggest    set found '"user::110::follow_users"' has 4529866 members
     *
     * 0 lists with 0 items (00.00% of keys, avg size 0.00)
     * 0 hashs with 0 fields (00.00% of keys, avg size 0.00)
     * 0 strings with 0 bytes (00.00% of keys, avg size 0.00)
     * 0 streams with 0 entries (00.00% of keys, avg size 0.00)
     * 3 sets with 4727816 members (100.00% of keys, avg size 1575938.67)
     * 0 zsets with 0 members (00.00% of keys, avg size 0.00)
     * wangzhengpeng@wangzhengpengdeMacBook-Pro bin %
     */

    public static void main(String[] args) throws Exception {
//        long startTime = System.currentTimeMillis();
        MicroBlogDemo demo = new MicroBlogDemo();
//        for (int i = 0; i < 1000 * 10000; i++) {
//            demo.follow(110L, new Random().nextInt(10000000));
//        }

//        for (int i = 0; i < 500 * 10000; i++) {
//            demo.follow(120L, new Random().nextInt(10000000));
//        }
//        Set<String> recommendFollowUsers = demo.getRecommendFollowUsers(110, 120);
//        System.out.println("sinter size "+recommendFollowUsers.size());
//
//        for (String recommendFollowUser : recommendFollowUsers) {
//            demo.follow(130L, Long.parseLong(recommendFollowUser));
//        }

//        System.out.println(System.currentTimeMillis() - startTime);
        // 定义用户id
        long userId = 31;
        long friendId = 32;
        long superstarId = 33;
        long classmateId = 34;
        long motherId = 35;

        // 定义关注的关系链
        demo.follow(userId, friendId);
        demo.follow(userId, motherId);
        demo.follow(userId, superstarId);
        demo.follow(friendId, superstarId);
        demo.follow(friendId, classmateId);

        // 明星看看自己被哪些人关注了
        Set<String> superstarFollowers = demo.getFollowers(superstarId);
        long superstarFollowersCount = demo.getFollowersCount(superstarId);
        System.out.println("明星被哪些人关注了：" + superstarFollowers + "，关注自己的人数为：" + superstarFollowersCount);

        // 朋友看看自己被哪些人关注了，自己关注了哪些人
        Set<String> friendFollowers = demo.getFollowers(friendId);
        long friendFollowersCount = demo.getFollowersCount(friendId);

        Set<String> friendFollowUsers = demo.getFollowUsers(friendId);
        long friendFollowUsersCount = demo.getFollowUsersCount(friendId);

        System.out.println("朋友被哪些人关注了：" + friendFollowers + "，被多少人关注了：" + friendFollowersCount
                + "，朋友关注了哪些人：" + friendFollowUsers + "，关注了多少人：" + friendFollowUsersCount);

        // 查看我自己关注了哪些
        Set<String> myFollowUsers = demo.getFollowUsers(userId);
        long myFollowUsersCount = demo.getFollowUsersCount(userId);
        System.out.println("我关注了哪些人：" + myFollowUsers + ", 我关注的人数：" + myFollowUsersCount);

        // 获取我和朋友共同关注的好友
        Set<String> sameFollowUsers = demo.getSameFollowUsers(userId, friendId);
        System.out.println("我和朋友共同关注的人有哪些：" + sameFollowUsers);

        // 获取推荐给我的可以关注的人，就是我关注的人关注的其他人
        Set<String> recommendFollowUsers = demo.getRecommendFollowUsers(userId, friendId);
        System.out.println("推荐给我的关注的人有哪些：" + recommendFollowUsers);
    }

}
