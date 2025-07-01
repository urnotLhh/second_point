package org.graduate;

import io.rebloom.client.Client;
import io.rebloom.client.InsertOptions;
import io.rebloom.client.ReserveParams;
import io.rebloom.client.cf.CFInsertOptions;
import io.rebloom.client.cf.CFReserveOptions;
import jakarta.annotation.Resource;
import jdk.jfr.DataAmount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.bloom.CFReserveParams;
import redis.clients.jedis.exceptions.JedisException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Redis 布谷鸟过滤器工具类 (jrebloom 2.2.0)
 * 提供线程安全的布谷鸟过滤器操作
 */
@Component
public class CuckooFilterManager {
    private final JedisPool jedisPool;
    private final Client client;
    private final String defaultFilterName;
    private final long defaultCapacity;

    @Autowired
    CFReserveParams cfReserveParams;

    @Autowired
    CFReserveOptions cfReserveOptions;

    @Resource
    InsertOptions insertOptions;

    @Resource
    CFInsertOptions cfInsertOptions;

    @Resource
    ReserveParams reserveParams;

    private final Map<String, Lock> filterLocks = new ConcurrentHashMap<>();

    public CuckooFilterManager() {
        this("127.0.0.1", 6379, "cfFilter", 1000);
    }
    public CuckooFilterManager(String host, int port, String defaultFilterName, long defaultCapacity) {
        this.defaultFilterName = defaultFilterName;
        this.defaultCapacity = defaultCapacity;

        JedisPoolConfig poolConfig = createPoolConfig();
        this.jedisPool = new JedisPool(poolConfig, host, port);
        this.client = new Client(jedisPool);

        try {
            createFilterIfNotExists(defaultFilterName, defaultCapacity);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public CuckooFilterManager(long capacity, int bucketSize) {
        this("127.0.0.1", 6379, "cfFilter", capacity);
    }
    private JedisPoolConfig createPoolConfig() {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(100);
        config.setMaxIdle(20);
        config.setMinIdle(5);
        config.setTestOnBorrow(true);
        config.setTestWhileIdle(true);
        config.setTimeBetweenEvictionRunsMillis(TimeUnit.MINUTES.toMillis(5));
        return config;
    }

    public void createFilterIfNotExists(String filterName, long capacity) throws Exception {
        if (filterExists(filterName)) return;

        Lock lock = filterLocks.computeIfAbsent(filterName, k -> new ReentrantLock());
        lock.lock();
        try {
            if (filterExists(filterName)) return;
            client.cfCreate(filterName, cfReserveOptions);
        } catch (JedisException e) {
            throw new Exception("创建过滤器失败: " + filterName, e);
        } finally {
            lock.unlock();
            filterLocks.remove(filterName);
        }
    }

    public boolean filterExists(String filterName) {
        try {
            client.cfInfo(filterName);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean[] add(String filterName, String item) throws Exception {
        try {
            return client.bfInsert(filterName, item);
        } catch (JedisException e) {
            throw new Exception("添加元素失败: " + item, e);
        }
    }

//    public int batchAdd(String filterName, List<String> items) throws Exception {
//    }

     /* 获取默认过滤器信息
     *
     * @return 过滤器信息对象
     */

    /**
     * 获取指定过滤器信息
     *
     * @param filterName 过滤器名称
     * @return 过滤器信息对象
     */
    public Map<String, Object> getFilterInfo(String filterName) throws Exception {
        try {
            return client.info(filterName);
        } catch (JedisException e) {
            throw new Exception("获取过滤器信息失败: " + filterName, e);
        }
    }

    /**
     * 获取过滤器负载率
     *
     * @param filterName 过滤器名称
     * @return 负载率 (0.0 - 1.0)
     */
    public double getLoadFactor(String filterName) throws Exception {
        Map<String, Object> info = getFilterInfo(filterName);
        long capacity = (long) info.get("Size");
        long numItems = (long) info.get("Number of items");
        return (double) numItems / capacity;
    }

    /**
     * 扩展过滤器容量
     *
     * @param filterName 过滤器名称
     * @param newCapacity 新容量
     */
    public void expandFilter(String filterName, long newCapacity) {
        try {
            client.bfReserve(filterName,
                    0.01,
                    newCapacity,
                    reserveParams);
        } catch (JedisException e) {
            throw new FilterOperationException("扩展过滤器失败: " + filterName, e);
        }
    }

    /**
     * 安全关闭资源
     */
    public void shutdown() {
        if (jedisPool != null && !jedisPool.isClosed()) {
            jedisPool.close();
        }
    }

    /**
     * 自定义过滤器操作异常
     */
    public static class FilterOperationException extends RuntimeException {
        public FilterOperationException(String message) {
            super(message);
        }

        public FilterOperationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}