package com.leigq.www.jwt.config;

import lombok.Data;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.io.Serializable;

/**
 * JwtProperties
 *
 * @author leigq
 * @date 2021-04-10 13:49:40
 */
@Data
@Configuration
@EnableConfigurationProperties(value = JwtProperties.class)
public class JwtConfig implements Serializable {
	private static final long serialVersionUID = 6653274237419642531L;

}
