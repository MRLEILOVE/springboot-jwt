package com.leigq.www.jwt.util;

import com.leigq.www.jwt.enums.RegexEnum;

import java.util.regex.Pattern;

/**
 * 正则表达式工具类
 * <br/>
 * <a href='https://c.runoob.com/front-end/854'>正则表达式在线测试</a>
 * <p>
 * 创建人：leigq <br>
 * 创建时间：2018-11-21 13:51 <br>
 * <p>
 * 修改人： <br>
 * 修改时间： <br>
 * 修改备注： <br>
 * </p>
 */
public final class RegexUtils {

	/**
	 * 验证手机
	 */
	public static final Pattern PHONE_PATTERN = getPattern(RegexEnum.PHONE);

	/**
	 * 验证邮箱
	 */
	public static final Pattern EMAIL_PATTERN = getPattern(RegexEnum.EMAIL);

	/**
	 * 判断字符串是否为纯数字
	 */
	public static final Pattern ALL_NUMBER_PATTERN = getPattern(RegexEnum.ALL_NUMBER);

	/**
	 * 6-20个字母、数字、下划线或减号，以字母开头（适用于用户名、密码）
	 */
	public static final Pattern SIX_TO_TWENTY_PATTERN = getPattern(RegexEnum.SIX_TO_TWENTY);

	/**
	 * 身份证号码(18位)
	 */
	public static final Pattern EIGHTEEN_IDCARD_PATTERN = getPattern(RegexEnum.EIGHTEEN_IDCARD);

	/**
	 * 2-4位中文汉字(真实姓名)
	 */
	public static final Pattern TWO2FOUR_CHINESE_PATTERN = getPattern(RegexEnum.TWO2FOUR_CHINESE);


    /**
     * 校验日期格式为yyyy-MM-dd HH:mm:ss的正则表达式
     */
    public static final Pattern DATE_TIME_FORMAT_PATTERN = getPattern(RegexEnum.DATE_TIME_FORMAT);


    /**
     * 校验日期格式为yyyy-MM-dd的正则表达式
     */
    public static final Pattern DATE_FORMAT_PATTERN = getPattern(RegexEnum.DATE_FORMAT);

	/**
	 * 通用验证方法，已对正则做预编译处理，推荐使用
	 * <br/>
	 * 参考：https://blog.csdn.net/qq_35312171/article/details/82663344
	 * <br>创建人： leigq
	 * <br>创建时间： 2018-11-21 14:22
	 * <br>
	 *
	 * @param pattern 使用本类中定义的 pattern
	 * @param input   需要验证的字符串
	 * @return 验证成功 true, 失败 false
	 */
	public static boolean validate(Pattern pattern, String input) {
		return pattern.matcher(input).matches();
	}

	private static Pattern getPattern(RegexEnum regexEnum) {
		return Pattern.compile(regexEnum.toString());
	}
}
