package cn.distributed.transaction.framework.bt.config;



import cn.hutool.json.JSONUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
public class RedissonTemplate {
    @Autowired
    RedissonClient redissonClient;

    /**
     * 获取单个对象
     */
    public <T> T get(String key, Class<T> clazz) {
        try {
            RBucket<String> bucket = redissonClient.getBucket(key);
            String str = bucket.get();
            if(clazz==String.class)
                return (T)str;
            return JSONUtil.parse(str).toBean(clazz);
        } catch (Exception e) {
            log.error("获取单个对象失败,key为{}, 异常为{}", key, e);
            return null;
        }
    }

    /**
     * 设置过期时间
     */
    public void expire(String key, int exTime) {
        try {
            RBucket<Object> bucket = redissonClient.getBucket(key);
            bucket.expire(exTime, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("设置过期时间失败,key为{}, 异常为{}", key, e);
        }
    }

    /**
     * 设置对象
     */
    public <T> boolean set( String key, T value, int exTime) {
        try {
            String str = JSONUtil.toJsonStr(value);
            if (str == null || str.length() == 0) {
                return false;
            }
            RBucket<Object> bucket = redissonClient.getBucket(key);
            if (exTime == 0) {
                bucket.set(str);
            } else {
                bucket.set(str, exTime, TimeUnit.SECONDS);
            }
            return true;
        } catch (Exception e) {
            log.error("设置对象失败,key为{}, 异常为{}", key, e);
            return false;
        }
    }

    /**
     * 删除redis中指定的key
     */
    public void delete(String key) {
        try {
            redissonClient.getKeys().delete(key);
        } catch (Exception e) {
            log.error("删除redis中指定的key失败,key为{}, 异常为{}", key, e);
        }
    }

    /**
     * 判断key是否存在
     */
    public boolean exists(String key) {
        try {
            return redissonClient.getKeys().countExists(key) == 1;
        } catch (Exception e) {
            log.error("判断key是否存在失败,key为{}, 异常为{}", key, e);
            return false;
        }
    }

    /**
     * 增加值(加1)
     */
    public void increase(String key) {
        try {
            RAtomicLong atomicLong = redissonClient.getAtomicLong(key);
            atomicLong.incrementAndGet();
        } catch (Exception e) {
            log.error("增加值(加1)失败,key为{}, 异常为{}", key, e);
        }
    }
}