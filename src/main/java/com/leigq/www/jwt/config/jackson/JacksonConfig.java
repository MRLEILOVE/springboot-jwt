package com.leigq.www.jwt.config.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.leigq.www.jwt.util.JacksonUtils;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.TimeZone;


/**
 * ObjectMapper配置，在使用 ObjectMapper 的地方，方便直接注入 ObjectMapper 进行使用，
 * 但是推荐统一使用 {@link JacksonUtils} 工具类
 *
 * @author leiguoqing
 * @date 2020 -07-22 21:03:08
 */
@Configuration
public class JacksonConfig {

    /**
     * jackson 2 ObjectMapper 构建定制器
     * <br/>
     * 该段代码并未覆盖SpringBoot自动装配的ObjectMapper对象，而是加强其配置.
     * 详情请参考: <a href='https://www.jianshu.com/p/68fce8b23341'>SpringBoot2.x下的ObjectMapper配置原理</a>
     *
     * @return the jackson 2 object mapper builder customizer
     * @author leiguoqing
     * @date 2020 -07-22 21:23:18
     */
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer customJackson() {
        return jacksonObjectMapperBuilder -> {
            //若POJO对象的属性值为null，序列化时不进行显示，暂时注释掉，为空也显示
//            jacksonObjectMapperBuilder.serializationInclusion(JsonInclude.Include.NON_NULL);

            // objectMapper.configure() 方法与 objectMapper.disable(), objectMapper.enable() 作用一样，
            // 都是进行一些配置，查看源码得知：都是调用 _serializationConfig.without(f) 方法
            /* 禁用一些配置 */
            jacksonObjectMapperBuilder.failOnUnknownProperties(false);

            /* 启用一些配置 */
            // 使科学计数法完整返回给前端
            jacksonObjectMapperBuilder.featuresToEnable(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN);

            /*
             * 时间模块，注意，下面的配置只是针对 Controller 接收 JSON 类型参数或返回 JSON 类型参数做的序列化、反序列化处理。
             * 如果 Controller 层接收的只是普通 Date 或 LocalDateTime，并且没有使用 @RequestBody 注解，此配置就不会生效，这种情况已在 GlobalParamsHand.java 中处理。
             */
            JavaTimeModule javaTimeModule = new JavaTimeModule();
            /* 序列化配置, 针对java8 时间 项目推荐返回前端时间戳，前端根据需要自己转换格式*/
            javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer());
            /* 反序列化配置, 针对java8 时间，后端可接受前端传过来的 yyyy-MM-dd HH:mm:ss 和 yyyy-MM-dd 两种格式时间， 当传 yyyy-MM-dd 格式时，时分秒填充0*/
            javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer());
            /* 反序列化配置, 针对 Date 类型时间，后端可接受前端传过来的 yyyy-MM-dd HH:mm:ss 和 yyyy-MM-dd 两种格式时间， 当传 yyyy-MM-dd 格式时，时分秒填充0*/
            javaTimeModule.addDeserializer(Date.class, new DateDeserializer());

            /* 注册模块 */
            jacksonObjectMapperBuilder.modules(javaTimeModule, new Jdk8Module(), new ParameterNamesModule());

            // 属性命名策略
            jacksonObjectMapperBuilder.propertyNamingStrategy(PropertyNamingStrategy.LOWER_CAMEL_CASE);

            // 时区
            jacksonObjectMapperBuilder.timeZone(TimeZone.getTimeZone("GMT+8"));

            // 针对于Date类型
            jacksonObjectMapperBuilder.simpleDateFormat("yyyy-MM-dd HH:mm:ss");


            //-----------------------------------------------华丽的分割线---------------------------------------------------

            /* ↓↓↓↓超级详细的一些配置↓↓↓↓ */
            //若POJO对象的属性值为null，序列化时不进行显示
//            jacksonObjectMapperBuilder.serializationInclusion(JsonInclude.Include.NON_NULL);
//            //若POJO对象的属性值为""，序列化时不进行显示
//            jacksonObjectMapperBuilder.serializationInclusion(JsonInclude.Include.NON_EMPTY);
//            //DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES相当于配置，JSON串含有未知字段时，反序列化依旧可以成功
//            jacksonObjectMapperBuilder.failOnUnknownProperties(false);
//            //序列化时的命名策略——驼峰命名法
//            jacksonObjectMapperBuilder.propertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
//            //针对于Date类型，文本格式化
//            jacksonObjectMapperBuilder.simpleDateFormat("yyyy-MM-dd HH:mm:ss");
//
//            //针对于JDK新时间类。序列化时带有T的问题，自定义格式化字符串
//            JavaTimeModule javaTimeModule = new JavaTimeModule();
//            javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
//            javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
//            jacksonObjectMapperBuilder.modules(javaTimeModule);
//
////            jacksonObjectMapperBuilder.featuresToEnable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
//            //默认关闭，将char[]数组序列化为String类型。若开启后序列化为JSON数组。
//            jacksonObjectMapperBuilder.featuresToEnable(SerializationFeature.WRITE_CHAR_ARRAYS_AS_JSON_ARRAYS);
//
//            //默认开启，若map的value为null，则不对map条目进行序列化。(已废弃)。
//            // 推荐使用：jacksonObjectMapperBuilder.serializationInclusion(JsonInclude.Include.NON_NULL);
//            jacksonObjectMapperBuilder.featuresToDisable(SerializationFeature.WRITE_NULL_MAP_VALUES);
//
//            //默认开启，将Date类型序列化为数字时间戳(毫秒表示)。关闭后，序列化为文本表现形式(2019-10-23T01:58:58.308+0000)
//            //若设置时间格式化。那么均输出格式化的时间类型。
//            jacksonObjectMapperBuilder.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
//            //默认关闭，在类上使用@JsonRootName(value="rootNode")注解时是否可以包裹Root元素。
//            // (https://blog.csdn.net/blueheart20/article/details/52212221)
////            jacksonObjectMapperBuilder.featuresToEnable(SerializationFeature.WRAP_ROOT_VALUE);
//            //默认开启：如果一个类没有public的方法或属性时，会导致序列化失败。关闭后，会得到一个空JSON串。
//            jacksonObjectMapperBuilder.featuresToDisable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
//
//            //默认关闭，即以文本(ISO-8601)作为Key，开启后，以时间戳作为Key
//            jacksonObjectMapperBuilder.featuresToEnable(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS);
//
//            //默认禁用，禁用情况下，需考虑WRITE_ENUMS_USING_TO_STRING配置。启用后，ENUM序列化为数字
//            jacksonObjectMapperBuilder.featuresToEnable(SerializationFeature.WRITE_ENUMS_USING_INDEX);
//
//            //仅当WRITE_ENUMS_USING_INDEX为禁用时(默认禁用)，该配置生效
//            //默认关闭，枚举类型序列化方式，默认情况下使用Enum.name()。开启后，使用Enum.toString()。注：需重写Enum的toString方法;
//            jacksonObjectMapperBuilder.featuresToEnable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
//
//            //默认开启，空Collection集合类型输出空JSON串。关闭后取消显示。(已过时)
//            // 推荐使用serializationInclusion(JsonInclude.Include.NON_EMPTY);
//            jacksonObjectMapperBuilder.featuresToEnable(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS);
//
//            //默认关闭，当集合Collection或数组一个元素时返回："list":["a"]。开启后，"list":"a"
//            //需要注意，和DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY 配套使用，要么都开启，要么都关闭。
////            jacksonObjectMapperBuilder.featuresToEnable(SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED);
//
//            //默认关闭。打开后BigDecimal序列化为文本。(已弃用)，推荐使用JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN配置
////            jacksonObjectMapperBuilder.featuresToEnable(SerializationFeature.WRITE_BIGDECIMAL_AS_PLAIN);
//            //默认关闭，即使用BigDecimal.toString()序列化。开启后，使用BigDecimal.toPlainString序列化，不输出科学计数法的值。
//            jacksonObjectMapperBuilder.featuresToEnable(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN);
//
//            /**
//             * JsonGenerator.Feature的相关参数（JSON生成器）
//             */
//
//            //默认关闭，即序列化Number类型及子类为{"amount1":1.1}。开启后，序列化为String类型，即{"amount1":"1.1"}
//            jacksonObjectMapperBuilder.featuresToEnable(JsonGenerator.Feature.WRITE_NUMBERS_AS_STRINGS);
//
//            /******
//             *  反序列化
//             */
//            //默认关闭，当JSON字段为""(EMPTY_STRING)时，解析为普通的POJO对象抛出异常。开启后，该POJO的属性值为null。
//            jacksonObjectMapperBuilder.featuresToEnable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
//            //默认关闭
////            jacksonObjectMapperBuilder.featuresToEnable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
//            //默认关闭，若POJO中不含有JSON中的属性，则抛出异常。开启后，不解析该字段，而不会抛出异常。
//            jacksonObjectMapperBuilder.featuresToEnable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        };
    }
}
