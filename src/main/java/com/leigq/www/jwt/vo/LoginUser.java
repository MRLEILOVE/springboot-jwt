package com.leigq.www.jwt.vo;

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

	/**
	 * The Token.
	 */
	private String token;

	/**
	 * The Refresh token.
	 */
	private String refreshToken;

	/**
	 * 过期时间
	 */
	private Date expiresAt;

}
