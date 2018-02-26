package com.github.bpazy.utils;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.List;

/**
 * Created by Ziyuan.
 * 2016/12/5 13:44
 */
public class QueueAndRedis {
    private static final String REDIS_MOVIE_SET_KEY = "movieSet";
    private static final String REDIS_MOVIE_LIST_KEY = "movieList";
    private JedisPool jedisPool;

    public QueueAndRedis() {
        JedisPoolConfig config = new JedisPoolConfig();
        this.jedisPool = new JedisPool(config, "10.101.25.169");
    }

    /**
     * 判断目标是否已经存在队列中，不存在则插入，存在则不插入。
     *
     * @param tar 目标
     * @return 插入成功返回true
     */
    public boolean queuePut(String tar) {
        boolean success = redisSetAdd(Helper.MD5(tar));
        if (success) {
            Jedis jedis = getJedis();
            jedis.rpush(REDIS_MOVIE_LIST_KEY, tar);
            jedis.close();
        }
        return success;
    }

    public String queueTake() throws InterruptedException {
        Jedis jedis = getJedis();
        List<String> value = jedis.blpop(0, REDIS_MOVIE_LIST_KEY);
        jedis.close();
        return value.get(1);
    }

    /**
     * @param tar 目标
     * @return 添加成功返回true
     */
    public boolean redisSetAdd(String tar) {
        Jedis jedis = getJedis();
        Long book = jedis.sadd(REDIS_MOVIE_SET_KEY, tar);
        jedis.close();
        return book == 1;
    }

    private Jedis getJedis() {
        Jedis jedis = jedisPool.getResource();
        return jedis;
    }

    public boolean redisSetContains(String tar) {
        Jedis jedis = getJedis();
        Boolean isExist = jedis.sismember(REDIS_MOVIE_SET_KEY, tar);
        jedis.close();
        return isExist;
    }

    public long redisSetRemove(String tar) {
        Jedis jedis = getJedis();
        jedis.close();
        return jedis.srem(REDIS_MOVIE_SET_KEY, tar);
    }
}
