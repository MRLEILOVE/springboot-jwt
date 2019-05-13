package com.leigq.www.jwt.entity;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Objects;

/**
 * User
 * <p>
 * 创建人：LeiGQ <br>
 * 创建时间：2019-05-11 14:03 <br>
 * <p>
 * 修改人： <br>
 * 修改时间： <br>
 * 修改备注： <br>
 * </p>
 */
@Data
@Builder
@Accessors(chain = true)
public class User {
    private Long id;
    private String userName;
    private String passWord;
    private String token;

    public boolean checkUserName(String userName) {
        return Objects.equals(userName, this.userName);
    }

    public boolean checkPwd(String pwd) {
        return Objects.equals(pwd, this.passWord);
    }

}
