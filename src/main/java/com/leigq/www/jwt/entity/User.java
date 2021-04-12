package com.leigq.www.jwt.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

/**
 * User 实体
 *
 * @author leigq
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {
	/**
	 * The Id.
	 */
	private Long id;

	/**
	 * The User name.
	 */
	private String userName;

	/**
	 * The Mobile.
	 */
	private String mobile;

	/**
	 * The Pass word.
	 */
	private String passWord;

	/**
	 * Check user name boolean.
	 *
	 * @param userName the user name
	 * @return the boolean
	 */
	public boolean checkUserName(String userName) {
        return Objects.equals(userName, this.userName);
    }

	/**
	 * Check pwd boolean.
	 *
	 * @param pwd the pwd
	 * @return the boolean
	 */
	public boolean checkPwd(String pwd) {
        return Objects.equals(pwd, this.passWord);
    }

}
