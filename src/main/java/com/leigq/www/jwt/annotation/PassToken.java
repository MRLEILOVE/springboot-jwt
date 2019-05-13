package com.leigq.www.jwt.annotation;

import java.lang.annotation.*;

/**
 * 用来跳过验证的
 * <p>
 * 创建人：LeiGQ <br>
 * 创建时间：2019-05-11 14:00 <br>
 * <p>
 * 修改人： <br>
 * 修改时间： <br>
 * 修改备注： <br>
 * </p>
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface PassToken {

}
