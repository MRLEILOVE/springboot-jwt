package com.leigq.www.jwt.config;

import com.leigq.www.jwt.web.AuthenticationInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * WebMvcConfig
 *
 * @author leigq
 */
@RequiredArgsConstructor
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

	private final AuthenticationInterceptor authenticationInterceptor;

	/**
	 * 添加自定义拦截器
	 *
	 * @param registry 拦截器注册表
	 */
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		/* 添加自定义拦截器 */
		// 登录拦截器
		registry.addInterceptor(authenticationInterceptor)
				// 添加拦截规则，先把所有路径都加入拦截，再一个个排除, 通过判断是否有 @PassToken 注解 决定是否需要登录
				.addPathPatterns("/**")
				// 排除拦截，表示该路径不用拦截，除了登录不拦截，其他都拦截
				.excludePathPatterns("/user/login");
	}
}
