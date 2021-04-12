package com.leigq.www.jwt.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 登录用户
 *
 * @author leigq
 * @date 2021-04-10 15:21:23
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginUser implements Serializable {

	private static final long serialVersionUID = 8851375458710488656L;


	private Long userId;

	private String mobile;

	/**
	 * token 过期时间
	 */
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	private Date tokenExpiresAt;

}
