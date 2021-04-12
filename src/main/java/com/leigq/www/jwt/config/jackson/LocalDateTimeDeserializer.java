package com.leigq.www.jwt.config.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.leigq.www.jwt.util.DateUtils;
import com.leigq.www.jwt.util.RegexUtils;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * LocalDateTime 类型时间反序列化，可接受前端传过来的 yyyy-MM-dd HH:mm:ss 和 yyyy-MM-dd 两种格式时间， 当传 yyyy-MM-dd 格式时，时分秒填充0
 *
 * @author leiguoqing
 * @date 2020-08-08 17:06:58
 */
public class LocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {
    @Override
    public LocalDateTime deserialize(JsonParser jp, DeserializationContext context) throws IOException {
        String text = jp.getText();
        // 判断时间格式
        boolean isDateFormat = RegexUtils.validate(RegexUtils.DATE_FORMAT_PATTERN, text);
        // 如果是年月日的格式则用 "yyyy-MM-dd" 格式化，否则统一使用 "yyyy-MM-dd HH:mm:ss" 格式化
        if (isDateFormat) {
            LocalDate localDate = LocalDate.parse(text, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            return DateUtils.LocalDateTime.from(localDate);
        } else {
            return DateUtils.LocalDateTime.from(text, "yyyy-MM-dd HH:mm:ss");
        }
    }
}
