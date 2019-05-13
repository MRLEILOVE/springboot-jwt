package com.leigq.www.jwt.config;

import com.leigq.www.jwt.web.AuthenticationInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

/**
 * WebMvcConfig
 * <p>
 * 创建人：LeiGQ <br>
 * 创建时间：2019-05-11 14:19 <br>
 * <p>
 * 修改人： <br>
 * 修改时间： <br>
 * 修改备注： <br>
 * </p>
 */
@Configuration
public class WebMvcConfig extends WebMvcConfigurationSupport {

    private final AuthenticationInterceptor authenticationInterceptor;

    @Autowired
    public WebMvcConfig(AuthenticationInterceptor authenticationInterceptor) {
        this.authenticationInterceptor = authenticationInterceptor;
    }

    /**
     * 添加自定义拦截器
     * <br>创建人： leigq
     * <br>创建时间： 2018-11-05 11:18
     * <br>
     *
     * @param registry 拦截器注册表
     */
    @Override
    protected void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authenticationInterceptor)
                // 拦截所有请求，通过判断是否有 @PassToken 注解 决定是否需要登录
                .addPathPatterns("/**");//添加拦截规则，先把所有路径都加入拦截，再一个个排除
        //.excludePathPatterns("/");//排除拦截，表示该路径不用拦截
        super.addInterceptors(registry);
    }
}
