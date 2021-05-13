package com.leigq.www.jwt.util;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Objects;


/**
 * 获取 spring bean 实例
 * <br/>
 * 详情：<a href='https://www.jianshu.com/p/3cd2d4e73eb7'>如何获取SpringBoot项目的applicationContext对象</a>
 * <br/>
 * 这里要注意ApplicationContextProvider类上的@Component注解是不可以去掉的，去掉后Spring就不会自动调用setApplicationContext方法来为我们设置上下文实例。
 *
 * @author leigq
 * @date 2020 -06-12 14:12:12
 */
@Component
public class SpringContextHolder implements ApplicationContextAware {

	private static ApplicationContext applicationContext;

	/**
	 * Sets application context.
	 *
	 * @param applicationContext the application context
	 * @throws BeansException the beans exception
	 */
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) {
		SpringContextHolder.applicationContext = applicationContext;
	}

	/**
	 * Gets application context.
	 *
	 * @return the application context
	 */
	public static ApplicationContext getApplicationContext() {
		assertApplicationContext();
		return applicationContext;
	}

	/**
	 * Gets bean.
	 *
	 * @param <T>      the type parameter
	 * @param beanName the bean name
	 * @return the bean
	 * @deprecated 推荐使用 {@link #getBean(Class)}
	 */
	@SuppressWarnings("unchecked")
	@Deprecated
	public static <T> T getBean(String beanName) {
		assertApplicationContext();
		return (T) applicationContext.getBean(beanName);
	}

	/**
	 * Gets bean.
	 *
	 * @param <T>          the type parameter
	 * @param requiredType the required type
	 * @return the bean
	 */
	public static <T> T getBean(Class<T> requiredType) {
		assertApplicationContext();
		return applicationContext.getBean(requiredType);
	}

	public static DefaultListableBeanFactory getDefaultListableBeanFactory() {
		assertApplicationContext();
		return (DefaultListableBeanFactory) ((ConfigurableApplicationContext) applicationContext).getBeanFactory();
	}

	private static void assertApplicationContext() {
		if (Objects.isNull(applicationContext)) {
			throw new NullPointerException("applicationContext属性为null,请检查是否注入了SpringContextHolder!");
		}
	}

}
