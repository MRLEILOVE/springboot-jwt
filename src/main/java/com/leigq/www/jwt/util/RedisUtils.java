package com.leigq.www.jwt.util;

import com.leigq.www.jwt.config.RedisConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;


/**
 * 基于 spring 和 redis 的 redisTemplate 工具类
 * <br/>
 * 请根据不同内部类操作对应类型的缓存，示例如下：<br/>
 * <pre>
 * public void test (){
 *     redisUtils.common.expire("a", 1);
 *     redisUtils.string.get("a");
 *     redisUtils.hash.set("hashKey", "hashItem", "HashValue");
 *     redisUtils.set.get("setKey");
 *     redisUtils.list.get("listKey", 1);
 * }
 * </pre>
 * 参考资料:<br/>
 * <ul>
 *     <li>
 *         <a href='https://blog.csdn.net/varyall/article/details/88785104'>Redis的三个框架：Jedis,Redisson,Lettuce</a>
 *     </li>
 *     <li>
 *         <a href='https://blog.csdn.net/chineseyoung/article/details/80972231'>Redis工具类(基于spring boot)</a>
 *     </li>
 *     <li>
 *         <a href='https://www.jqhtml.com/27461.html'>在Java中使用redisTemplate操作缓存</a>
 *     </li>
 *     <li>
 *         <a href='http://redisdoc.com/'>Redis命令手册</a>
 *     </li>
 * </ul>
 *
 * @author leigq
 * @date 2020 -07-24 13:12:04
 */
@Service
@SuppressWarnings(value = {"unchecked"})
public final class RedisUtils {

    /**
     * The Log.
     */
    private final Logger log = LoggerFactory.getLogger(RedisUtils.class);

    /**
     * 构造注入 redisTemplate，这个是关键
     */
    private final RedisTemplate<Object, Object> redisTemplate;

    /**
     * 使用 RedisConfig 中的 redisTemplate，自定义序列化 及 兼容 java8 时间
     *
     * @param redisTemplate the redis template
     * @author leigq
     * @date 2020 -07-24 13:12:04
     * @see RedisConfig#getRedisTemplate(RedisConnectionFactory)
     */
    public RedisUtils(RedisTemplate<Object, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 提供一些公共操作
     */
    public final Common common = new Common();

    /**
     * 提供一些基础操作，支持存储 String、简单的 pojo 对象
     */
    public final String string = new String();

    /**
     * 针对所有的 hash 操作
     */
    public final Hash hash = new Hash();


    /**
     * 针对所有的 Set 操作
     */
    public final Set set = new Set();


    /**
     * 针对所有的 List 操作
     */
    public final List list = new List();

    /**
     * Gets redis template.
     *
     * @return the redis template
     * @author leiguoqing
     * @date 2020 -07-25 18:02:21
     */
    public RedisTemplate<Object, Object> getRedisTemplate() {
        return redisTemplate;
    }

    /**
     * 提供一些公共操作
     *
     * @author leigq
     * @date 2020 -07-24 13:03:51
     */
    public class Common {
        /**
         * Instantiates a new Common.
         *
         * @author leigq
         * @date 2020 -07-24 13:12:07
         */
        private Common() {

        }

        /**
         * 指定对应 Key 的缓存失效时间
         *
         * @param key  键
         * @param time 时间(秒)，不要传 0 或负数，0或负数缓存将直接失效，想设置不失效请用 {@link RedisUtils#common#persist(java.lang.String)} 实现
         * @return the result
         * @author leigq
         * @date 2020 -07-24 13:04:20
         */
        public Boolean expire(java.lang.String key, long time) {
            return redisTemplate.expire(key, time, TimeUnit.SECONDS);
        }

        /**
         * 根据 key 获取过期时间
         *
         * @param key 键
         * @return 过期时间 时间(秒)
         * @author leigq
         * @date 2020 -07-24 13:09:53
         */
        public Long getExpire(java.lang.String key) {
            return redisTemplate.getExpire(key, TimeUnit.SECONDS);
        }

        /**
         * 根据 key 设置缓存为不失效
         *
         * @param key 键
         * @return the result
         * @author leigq
         * @date 2020 -07-24 13:09:53
         */
        public Boolean persist(java.lang.String key) {
            return redisTemplate.persist(key);
        }

        /**
         * 判断 key 是否存在
         *
         * @param key 键
         * @return the result
         * @author leigq
         * @date 2020 -07-24 13:12:02
         */
        public Boolean hasKey(java.lang.String key) {
            return redisTemplate.hasKey(key);
        }

        /**
         * 递增，此时 value 值必须为 int 类型 否则报错
         *
         * @param key   键
         * @param delta 要增加几，递增因子必须大于0
         * @return 执行递增操作后返回 key 对应的值
         * @author leigq
         * @date 2020 -07-24 12:52:17
         */
        public Long increment(java.lang.String key, long delta) {
            if (delta < 0) {
                throw new RedisUtilsException("递增因子必须大于0");
            }
            return string.getValueOperations().increment(key, delta);
        }

        /**
         * 递减，此时 value 值必须为 int 类型 否则报错
         *
         * @param key   键
         * @param delta 要减少几, 递减因子必须大于0
         * @return 执行递减操作后返回 key 对应的值
         * @author leigq
         * @date 2020 -07-24 11:46:27
         */
        public Long decreasing(java.lang.String key, long delta) {
            if (delta < 0) {
                throw new RedisUtilsException("递减因子必须大于0");
            }
            return string.getValueOperations().increment(key, -delta);
        }

        /**
         * 删除缓存
         *
         * @param key 键
         * @return the result
         * @author leigq
         * @date 2020 -07-24 13:12:07
         */
        public Boolean delete(java.lang.String key) {
            return redisTemplate.delete(key);
        }

        /**
         * 删除缓存
         *
         * @param key 可以多个键
         * @return 影响个数，删除一个不存在的 key 或 失败都返回0
         * @author leigq
         * @date 2020 -07-24 13:12:07
         */
        public Long delete(java.lang.String... key) {
            return redisTemplate.delete(Arrays.asList(key));
        }
    }


    /**
     * 提供一些基础操作，支持存储 String、简单的 pojo 对象
     *
     * @author leigq
     * @date 2020 -07-24 12:47:39
     */
    public class String {
        /**
         * Instantiates a new Base.
         *
         * @author leigq
         * @date 2020 -07-24 13:12:07
         */
        private String() {
        }

        /**
         * Gets value operations.
         *
         * @return the value operations
         * @author leigq
         * @date 2020 -07-24 13:12:07
         */
        private ValueOperations<Object, Object> getValueOperations() {
            return redisTemplate.opsForValue();
        }

        /**
         * 普通缓存获取
         *
         * @param <T> the type parameter
         * @param key 键
         * @return 值 object，不存在则返回 null
         * @author leigq
         * @date 2020 -07-24 12:49:08
         */
        public <T> T get(java.lang.String key) {
            if (Objects.isNull(key)) {
                return null;
            }
            Object object = getValueOperations().get(key);
            if (Objects.isNull(object)) {
                return null;
            }
            return (T) object;
        }

        /**
         * 普通缓存放入
         *
         * @param <T>   the type parameter
         * @param key   键
         * @param value the value
         * @return the result
         * @author leigq
         * @date 2020 -07-24 12:50:06
         */
        public <T> Boolean set(java.lang.String key, T value) {
            try {
                getValueOperations().set(key, value);
                return true;
            } catch (Exception ex) {
                log.error("普通缓存放入异常：", ex);
                return false;
            }
        }

        /**
         * 普通缓存放入并设置时间
         *
         * @param <T>   the type parameter
         * @param key   键
         * @param value the value
         * @param time  时间(秒) 如果设置成0或负数，会默认变成-1，也就是无失效时间
         * @return the result
         * @author leigq
         * @date 2020 -07-24 12:51:34
         */
        public <T> Boolean set(java.lang.String key, T value, long time) {
            try {
                if (time > 0) {
                    getValueOperations().set(key, value, time, TimeUnit.SECONDS);
                } else {
                    set(key, value);
                }
                return true;
            } catch (Exception ex) {
                log.error("普通缓存放入并设置时间异常：", ex);
                return false;
            }
        }
    }


    /**
     * 针对所有的 hash 操作
     *
     * @author leigq
     * @date 2020 -07-24 13:02:27
     */
    public class Hash {

        /**
         * Instantiates a new Hash.
         *
         * @author leigq
         * @date 2020 -07-24 13:12:08
         */
        private Hash() {
        }

        /**
         * Gets hash operations.
         *
         * @return the hash operations
         * @author leigq
         * @date 2020 -07-24 13:12:08
         */
        private HashOperations<Object, Object, Object> getHashOperations() {
            return redisTemplate.opsForHash();
        }

        /**
         * 返回 hash 表中给定域的值
         *
         * @param <T>     the type parameter
         * @param key     键
         * @param hashKey Hash值的Key
         * @return 值 object
         * @author leigq
         * @date 2020 -07-24 13:12:08
         */
        public <T> T get(java.lang.String key, java.lang.String hashKey) {
            return (T) getHashOperations().get(key, hashKey);
        }


        /**
         * 向一张 hash 表中放入数据, 如果不存在将创建
         *
         * @param <T>     the type parameter
         * @param key     键
         * @param hashKey Hash值的Key
         * @param value   the value
         * @return the result
         * @author leigq
         * @date 2020 -07-24 13:12:09
         */
        public <T> Boolean put(java.lang.String key, java.lang.String hashKey, T value) {
            try {
                getHashOperations().put(key, hashKey, value);
                return true;
            } catch (Exception ex) {
                log.error("向一张hash表中放入数据异常：", ex);
                return false;
            }
        }

        /**
         * 向一张 hash 表中放入数据, 如果不存在将创建并设置时间
         *
         * @param <T>     the type parameter
         * @param key     键
         * @param hashKey Hash值的Key
         * @param value   值
         * @param time    时间(秒) 如果设置成0或负数，会默认变成-1，也就是无失效时间，如果已存在的hash表有时间，这里将会替换原有的时间
         * @return the result
         * @author leigq
         * @date 2020 -07-24 13:12:09
         */
        public <T> Boolean put(java.lang.String key, java.lang.String hashKey, T value, long time) {
            try {
                getHashOperations().put(key, hashKey, value);
                if (time > 0) {
                    common.expire(key, time);
                }
                return true;
            } catch (Exception ex) {
                log.error("向一张hash表中放入数据,并设置时间异常：", ex);
                return false;
            }
        }


        /**
         * 获取 key 对应的所有键值
         *
         * @param key 键
         * @return 对应的多个键值 map
         * @author leigq
         * @date 2020 -07-24 13:12:08
         */
        public <K, V> Map<K, V> getMap(java.lang.String key) {
            return (Map<K, V>) getHashOperations().entries(key);
        }

        /**
         * Map 集合缓存放入
         *
         * @param key 键
         * @param map 对应多个键值
         * @return the result
         * @author leigq
         * @date 2020 -07-24 13:12:09
         */
        public <K, V> Boolean putMap(java.lang.String key, Map<K, V> map) {
            try {
                getHashOperations().putAll(key, map);
                return true;
            } catch (Exception e) {
                log.error("HashSet缓存放入异常：", e);
                return false;
            }
        }

        /**
         * HashSet 缓存 Map 并设置时间
         *
         * @param key  键
         * @param map  对应多个键值
         * @param time 时间(秒) 如果设置成0或负数，会默认变成-1，也就是无失效时间，如果已存在的hash表有时间，这里将会替换原有的时间
         * @return the result
         * @author leigq
         * @date 2020 -07-24 13:12:09
         */
        public <K, V> Boolean putMap(java.lang.String key, Map<K, V> map, long time) {
            try {
                getHashOperations().putAll(key, map);
                if (time > 0) {
                    common.expire(key, time);
                }
                return true;
            } catch (Exception e) {
                log.error("HashSet 缓存放入并设置时间异常：", e);
                return false;
            }
        }

        /**
         * 删除 hash 表中的值
         *
         * @param key      键
         * @param hashKeys Hash值的Key
         * @return the long
         * @author leigq
         * @date 2020 -07-24 13:12:09
         */
        public Long delete(java.lang.String key, Object... hashKeys) {
            return getHashOperations().delete(key, hashKeys);
        }

        /**
         * 判断 hash 表中是否有给定域
         *
         * @param key     键
         * @param hashKey Hash值的 Key
         * @return the result
         * @author leigq
         * @date 2020 -07-24 13:12:09
         */
        public Boolean hasKey(java.lang.String key, java.lang.String hashKey) {
            return getHashOperations().hasKey(key, hashKey);
        }

        /**
         * hash递增 如果不存在, 就会创建一个并把新增后的值返回
         *
         * @param key     键
         * @param hashKey Hash值的Key
         * @param by      要增加几(大于0)
         * @return 执行递增操作后返回 key 对应的值
         * @author leigq
         * @date 2020 -07-24 13:12:09
         */
        public Double increment(java.lang.String key, java.lang.String hashKey, double by) {
            return getHashOperations().increment(key, hashKey, by);
        }

        /**
         * hash递减
         *
         * @param key     键
         * @param hashKey Hash值的Key
         * @param by      要减少几(大于0)
         * @return 执行递减操作后返回 key 对应的值
         * @author leigq
         * @date 2020 -07-24 13:12:10
         */
        public Double decreasing(java.lang.String key, java.lang.String hashKey, double by) {
            return getHashOperations().increment(key, hashKey, -by);
        }
    }


    /**
     * 针对所有的 Set 操作
     *
     * @author leigq
     * @date 2020 -07-24 13:42:02
     */
    public class Set {
        /**
         * Instantiates a new Set.
         *
         * @author leigq
         * @date 2020 -07-24 13:42:06
         */
        private Set() {

        }

        /**
         * Gets set operations.
         *
         * @return the set operations
         * @author leigq
         * @date 2020 -07-24 13:42:06
         */
        private SetOperations<Object, Object> getSetOperations() {
            return redisTemplate.opsForSet();
        }

        /**
         * 将数据放入 set 缓存
         *
         * @param key    键
         * @param values 值 可以是多个
         * @return 成功个数 long
         * @author leigq
         * @date 2020 -07-24 13:12:05
         */
        public Long add(java.lang.String key, Object... values) {
            return getSetOperations().add(key, values);
        }

        /**
         * 将set数据放入缓存
         *
         * @param key    键
         * @param time   时间(秒) 如果设置成0或负数，会默认变成-1，也就是无失效时间，如果已存在的hash表有时间，这里将会替换原有的时间
         * @param values 值 可以是多个
         * @return 成功个数 long
         * @author leigq
         * @date 2020 -07-24 13:12:05
         */
        public Long addWithTime(java.lang.String key, long time, Object... values) {
            try {
                Long count = getSetOperations().add(key, values);
                if (time > 0) {
                    common.expire(key, time);
                }
                return count;
            } catch (Exception e) {
                log.error("将set数据放入缓存异常：", e);
                return 0L;
            }
        }

        /**
         * 根据 key 获取 Set 中的所有值
         *
         * @param <E> the type parameter
         * @param key 键
         * @return Set中的所有值 set
         * @author leigq
         * @date 2020 -07-24 13:12:05
         */
        public <E> java.util.Set<E> get(java.lang.String key) {
            return (java.util.Set<E>) getSetOperations().members(key);
        }

        /**
         * 获取set缓存的长度
         *
         * @param key 键
         * @return 长度 long
         * @author leigq
         * @date 2020 -07-24 13:12:05
         */
        public Long size(java.lang.String key) {
            return getSetOperations().size(key);
        }

        /**
         * 从key集合中删除给定值，并返回已删除元素的数量
         *
         * @param key    键
         * @param values 值 可以是多个
         * @return 已删除元素的数量
         * @author leigq
         * @date 2020 -07-24 13:12:05
         */
        public Long remove(java.lang.String key, Object... values) {
            return getSetOperations().remove(key, values);
        }

        /**
         * 根据value从一个set中查询,是否存在
         *
         * @param <T>   the type parameter
         * @param key   键
         * @param value the value
         * @return the result
         * @author leigq
         * @date 2020 -07-24 13:12:05
         */
        public <T> Boolean hasKey(java.lang.String key, T value) {
            return getSetOperations().isMember(key, value);
        }

    }

    /**
     * 针对所有的 List 操作
     *
     * @author leigq
     * @date 2020 -07-24 13:45:53
     */
    public class List {

        private List() {

        }

        /**
         * Gets list operations.
         *
         * @return the list operations
         * @author leigq
         * @date 2020 -07-24 13:45:55
         */
        private ListOperations<Object, Object> getListOperations() {
            return redisTemplate.opsForList();
        }


        /**
         * 将对象放入 List 缓存，向左插入
         *
         * @param <T>   the type parameter
         * @param key   键
         * @param value the value
         * @return the result
         * @author leigq
         * @date 2020 -07-24 13:12:06
         */
        public <T> Boolean leftPush(java.lang.String key, T value) {
            try {
                getListOperations().leftPush(key, value);
                return true;
            } catch (Exception ex) {
                log.error("将list放入缓存异常：", ex);
                return false;
            }
        }


        /**
         * 将对象放入 List 缓存，向左插入并设置过期时间
         *
         * @param <T>   the type parameter
         * @param key   键
         * @param value the value
         * @param time  时间(秒) 如果设置成0或负数，会默认变成-1，也就是无失效时间，如果已存在的hash表有时间，这里将会替换原有的时间
         * @return the result
         * @author leigq
         * @date 2020 -07-24 13:12:06
         */
        public <T> Boolean leftPush(java.lang.String key, T value, long time) {
            try {
                getListOperations().leftPush(key, value);
                if (time > 0) {
                    common.expire(key, time);
                }
                return true;
            } catch (Exception ex) {
                log.error("将list放入缓存并设置过期时间异常：", ex);
                return false;
            }
        }


        /**
         * 将 list 放入缓存，向左插入
         *
         * @param <E>  the type parameter
         * @param key  键
         * @param list 集合
         * @return the result
         * @author leigq
         * @date 2020 -07-24 13:12:06
         */
        public <E> Boolean leftPush(java.lang.String key, java.util.List<E> list) {
            try {
                getListOperations().leftPushAll(key, list);
                return true;
            } catch (Exception e) {
                log.error("将list放入缓存异常：", e);
                return false;
            }
        }


        /**
         * 将 list 放入缓存，向左插入并设置过期时间
         *
         * @param <E>  the type parameter
         * @param key  键
         * @param list 集合
         * @param time 时间(秒) 如果设置成0或负数，会默认变成-1，也就是无失效时间，如果已存在的hash表有时间，这里将会替换原有的时间
         * @return the result
         * @author leigq
         * @date 2020 -07-24 13:12:06
         */
        public <E> Boolean leftPush(java.lang.String key, java.util.List<E> list, long time) {
            try {
                getListOperations().leftPushAll(key, list);
                if (time > 0) {
                    common.expire(key, time);
                }
                return true;
            } catch (Exception e) {
                log.error("将list放入缓存并设置过期时间异常：", e);
                return false;
            }
        }


        /**
         * 将对象放入 List 缓存，向右插入
         *
         * @param <T>   the type parameter
         * @param key   键
         * @param value the value
         * @return the result
         * @author leigq
         * @date 2020 -07-24 13:12:06
         */
        public <T> Boolean rightPush(java.lang.String key, T value) {
            try {
                getListOperations().rightPush(key, value);
                return true;
            } catch (Exception ex) {
                log.error("将list放入缓存异常：", ex);
                return false;
            }
        }

        /**
         * 将对象放入 List 缓存，向右插入并设置过期时间
         *
         * @param <T>   the type parameter
         * @param key   键
         * @param value the value
         * @param time  时间(秒) 如果设置成0或负数，会默认变成-1，也就是无失效时间，如果已存在的hash表有时间，这里将会替换原有的时间
         * @return the result
         * @author leigq
         * @date 2020 -07-24 13:12:06
         */
        public <T> Boolean rightPush(java.lang.String key, T value, long time) {
            try {
                getListOperations().rightPush(key, value);
                if (time > 0) {
                    common.expire(key, time);
                }
                return true;
            } catch (Exception ex) {
                log.error("将list放入缓存并设置过期时间异常：", ex);
                return false;
            }
        }


        /**
         * 将 list 放入缓存，向右插入
         *
         * @param <E>  the type parameter
         * @param key  键
         * @param list 集合
         * @return the result
         * @author leigq
         * @date 2020 -07-24 13:12:06
         */
        public <E> Boolean rightPush(java.lang.String key, java.util.List<E> list) {
            try {
                getListOperations().rightPushAll(key, list);
                return true;
            } catch (Exception e) {
                log.error("将list放入缓存异常：", e);
                return false;
            }
        }

        /**
         * 将 list 放入缓存，向右插入并设置过期时间
         *
         * @param <E>  the type parameter
         * @param key  键
         * @param list 集合
         * @param time 时间(秒) 如果设置成0或负数，会默认变成-1，也就是无失效时间，如果已存在的hash表有时间，这里将会替换原有的时间
         * @return the result
         * @author leigq
         * @date 2020 -07-24 13:12:06
         */
        public <E> Boolean rightPush(java.lang.String key, java.util.List<E> list, long time) {
            try {
                getListOperations().rightPushAll(key, list);
                if (time > 0) {
                    common.expire(key, time);
                }
                return true;
            } catch (Exception e) {
                log.error("将list放入缓存并设置过期时间异常：", e);
                return false;
            }
        }

        /**
         * 返回列表 key 中，下标为 index 的元素
         *
         * @param <T>   the type parameter
         * @param key   键
         * @param index <br/>
         *              下标(index)参数 start 和 stop 都以 0 为底，也就是说，以 0 表示列表的第一个元素，以 1 表示列表的第二个元素，以此类推。<br/>
         *              你也可以使用负数下标，以 -1 表示列表的最后一个元素， -2 表示列表的倒数第二个元素，以此类推。
         * @return 值 object
         * @author leigq
         * @date 2020 -07-24 13:12:06
         */
        public <T> T get(java.lang.String key, long index) {
            try {
                return (T) getListOperations().index(key, index);
            } catch (Exception e) {
                log.error("通过索引获取list中的值异常：", e);
                return null;
            }
        }

        /**
         * 返回列表 key 中，第一个元素
         *
         * @param <T> the type parameter
         * @param key 键
         * @return 值 object
         * @author leigq
         * @date 2020 -07-24 13:12:06
         */
        public <T> T getFirst(java.lang.String key) {
            return get(key, 0);
        }


        /**
         * 返回列表 key 中，最后一个元素
         *
         * @param <T> the type parameter
         * @param key 键
         * @return 值 object
         * @author leigq
         * @date 2020 -07-24 13:12:06
         */
        public <T> T getLast(java.lang.String key) {
            return get(key, -1);
        }


        /**
         * 返回列表 key 中，所有元素
         *
         * @param <T> the type parameter
         * @param key 键
         * @return 值 object
         * @author leigq
         * @date 2020 -07-24 13:12:06
         */
        public <T> T getAll(java.lang.String key) {
            return (T) get(key, 0, -1);
        }


        /**
         * 返回列表 key 中指定区间内的元素，区间以偏移量 start 和 stop 指定。
         * <br/>
         * 下标(index)参数 start 和 stop 都以 0 为底，也就是说，以 0 表示列表的第一个元素，以 1 表示列表的第二个元素，以此类推。
         * <br/>
         * 你也可以使用负数下标，以 -1 表示列表的最后一个元素， -2 表示列表的倒数第二个元素，以此类推。
         * <br/>
         * 假如你有一个包含一百个元素的列表，对该列表执行 LRANGE list 0 10 ，结果是一个包含11个元素的列表，这表明 stop 下标也在 LRANGE 命令的取值范围之内(闭区间)，
         * 这和某些语言的区间函数可能不一致，比如Ruby的 Range.new 、 Array#slice 和Python的 range() 函数。
         * <br/>
         * 0 到 -1代表所有值
         *
         * @param <E>   the type parameter
         * @param key   键
         * @param start 如果 start 下标比列表的最大下标 end ( LLEN list 减去 1 )还要大，那么 LRANGE 返回一个空列表。
         * @param end   如果 stop 下标比 end 下标还要大，Redis将 stop 的值设置为 end 。
         * @return list java . util . list
         * @author leigq
         * @date 2020 -07-24 13:12:05
         */
        public <E> java.util.List<E> get(java.lang.String key, long start, long end) {
            try {
                return (java.util.List<E>) getListOperations().range(key, start, end);
            } catch (Exception e) {
                log.error("获取list缓存的内容异常：", e);
                return Collections.emptyList();
            }
        }


        /**
         * 获取 list 缓存的长度
         *
         * @param key 键
         * @return 长度
         * @author leigq
         * @date 2020 -07-24 13:12:05
         */
        public Long size(java.lang.String key) {
            try {
                return getListOperations().size(key);
            } catch (Exception e) {
                log.error("获取list缓存的长度异常：", e);
                return 0L;
            }
        }

        /**
         * 根据索引修改 list 中的某条数据
         *
         * @param <T>   the type parameter
         * @param key   键
         * @param index 索引
         * @param value the value
         * @return the result
         * @author leigq
         * @date 2020 -07-24 13:12:06
         */
        public <T> Boolean update(java.lang.String key, long index, T value) {
            try {
                getListOperations().set(key, index, value);
                return true;
            } catch (Exception ex) {
                log.error("根据索引修改list中的某条数据异常：", ex);
                return false;
            }
        }

        /**
         * 根据参数 count 的值，移除列表中与参数 value 相等的元素。
         *
         * @param <T>   the type parameter
         * @param key   键
         * @param count <br/>              count > 0 : 从表头开始向表尾搜索，移除与 value 相等的元素，数量为 count 。<br/>              count < 0 : 从表尾开始向表头搜索，移除与 value 相等的元素，数量为 count 的绝对值。<br/>              count = 0 : 移除表中所有与 value 相等的值。
         * @param value 值
         * @return 被移除元素的数量 long
         * @author leigq
         * @date 2020 -07-24 13:12:06
         */
        public <T> Long remove(java.lang.String key, long count, T value) {
            try {
                return getListOperations().remove(key, count, value);
            } catch (Exception ex) {
                log.error("移除元素异常", ex);
                return 0L;
            }
        }
    }


    private static class RedisUtilsException extends RuntimeException {
		static final long serialVersionUID = -7034897190745766939L;

        public RedisUtilsException(java.lang.String message) {
            super(message);
        }
    }

}