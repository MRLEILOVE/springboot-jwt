package com.leigq.www.jwt.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.leigq.www.jwt.util.JacksonUtils;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis配置
 * <p>
 * 创建人：leigq <br>
 * 创建时间：2018-11-08 10:11 <br>
 * <p>
 * 修改人： <br>
 * 修改时间： <br>
 * 修改备注： <br>
 * </p>
 */
@Configuration
public class RedisConfig extends CachingConfigurerSupport {

    /**
     * redisTemplate 序列化默认使用的 JdkSerializationRedisSerializer, 存储二进制字节码，这里改为使用 jackson2JsonRedisSerializer 自定义序列化
     * 想了解 SpringBoot 是如何默认使用 JdkSerializationRedisSerializer 的，看这里：<a href='https://www.cnblogs.com/HuuuWnnn/p/11864380.html'>SpringBoot项目使用RedisTemplate设置序列化方式</a>
     * <br/>
     * StringRedisTemplate 使用的是 StringRedisSerializer，不受影响，不用重新配置
     * <br/>
     * 相关文章：<br/>
     * <ul>
     *     <li>
     *         <a href='https://blog.csdn.net/m0_37893932/article/details/78259288'>Spring-boot通过redisTemplate使用redis(无须手动序列化)</a>
     *     </li>
     *     <li>
     *         <a href='https://www.cnblogs.com/wangzhuxing/p/10198347.html'>redisTemplate和stringRedisTemplate对比、redisTemplate几种序列化方式比较</a>
     *     </li>
     * </ul>
     *
     * <br>创建人： leigq
     * <br>创建时间： 2018-11-08 10:12
     * <br>
     *
     * @param redisConnectionFactory redis连接工厂
     * @return RedisTemplate
     */
    @Bean
    @Primary
    public RedisTemplate<Object, Object> getRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);

        // 使用 Jackson2JsonRedisSerialize 替换默认序列化
        // 以下代码为将 RedisTemplate 的 Value 序列化方式由 JdkSerializationRedisSerializer更换为 Jackson2JsonRedisSerializer
        // 此种序列化方式结果清晰、容易阅读、存储字节少、速度快，所以推荐更换
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer());
        // 设置 key 的序列化规则
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer());
        redisTemplate.setStringSerializer(new StringRedisSerializer());
        // 是否启用事务
        // redisTemplate.setEnableTransactionSupport(true);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }


    /**
     * Jackson 2 json redis serializer jackson 2 json redis serializer.
     *
     * @return the jackson 2 json redis serializer
     * @author leiguoqing
     * @date 2020 -07-25 14:31:07
     */
    private Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer() {
        // 使用 Jackson2JsonRedisSerialize 替换默认序列化
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        jackson2JsonRedisSerializer.setObjectMapper(SingletonEnum.INSTANCE.objectMapper);
        return jackson2JsonRedisSerializer;
    }


    /**
     * 使用枚举实现单例获取重写了一些配置的（Redis序列化专用） objectMapper
     * <br/>
     * 参考：<a href='https://www.jianshu.com/p/d35f244f3770'>枚举实现单例模式</a>
     */
    private enum SingletonEnum {

        /**
         * Instance singleton enum.
         */
        INSTANCE;

        /**
         * The Object mapper.
         */
        private final ObjectMapper objectMapper;

        /**
         * 在枚举构造方法里面初始化自定义的 objectMapper，以实现单例
         */
        SingletonEnum() {
            objectMapper = JacksonUtils.CustomObjectMapper.newInstance();

            // 重写一些配置
            objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
            // 序列化时允许非常量字段均输出类型 (此项必须配置，否则会报java.lang.ClassCastException: java.util.LinkedHashMap cannot be cast to XXX)
            objectMapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL);

            objectMapper.disable(MapperFeature.USE_ANNOTATIONS);
            objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        }
    }
}
