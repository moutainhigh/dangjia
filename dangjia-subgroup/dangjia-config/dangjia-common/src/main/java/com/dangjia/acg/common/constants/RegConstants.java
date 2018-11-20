package com.dangjia.acg.common.constants;


import com.dangjia.acg.common.util.EntityValidator;

/**
 * 正则常量类
 * 
 * @author Qiyuxiang
 */
public class RegConstants {
	public static final String DEFAULT_MSG = EntityValidator.SIGN_FIELDDESC + "格式不正确";
	/** 只含有数字、字母、下划线，下划线位置不限 */
	public static final String NAME = "^[a-zA-Z0-9_]+$";
	public static final String NAME_MSG = EntityValidator.SIGN_FIELDDESC + "含非法字符";
	/** 真实姓名 */
	public static final String REALNAME = "^[a-zA-Z0-9_\\u4e00-\\u9fa5]+$";

	public static final String IMAGE_URL = "^[\\s\\S]*(jpg|jpeg|gif|png|bmp)$";
	public static final String EXCEL = "^[\\s\\S]*(xls)$";

	/**账号以字母开头,只能包行字符数字和下划线*/
	public static final String ACCOUNT = "^[a-zA-Z0-9_]{6,20}$";//账号

	/**以字母开头，长度在6-20之间，包含字符、数字和下划线*/
	public static final String PASSWORD = "^[a-zA-Z0-9_]{6,20}$"; //

	public static final String MOBILE_OR_PHONE = "((^(13|15|18)[0-9]{9}$)|(^0[1,2]{1}d{1}-?d{8}$)|"
			+ "(^0[3-9] {1}d{2}-?d{7,8}$)|"
			+ "(^0[1,2]{1}d{1}-?d{8}-(d{1,4})$)|"
+ "(^0[3-9]{1}d{2}-? d{7,8}-(d{1,4})$))";
	/** 只含有数字、字母的其他名称 */
	public static final String OTHERNAME = "^[a-zA-Z0-9]+$";
	/** MD5加密后的数据 */
	public static final String MD5 = "^[a-zA-Z0-9]{32}$";
	/** imsi验证 */
	public static final String IMSI = "^460(00|01|02|03|07)\\d{10}$";
	/** 身份证号码 */
	public static final String CARD = "^\\d{15}$|(^\\d{17}(\\d|X)+$)";
	/** 手机号码 */
	public static final String PHONE = "^((13[0-9])|(14[0-9])|(17[0-9])|(15[0-9])|(18[0-9]))\\d{8}$";
	/** 邮箱 */
	public static final String EMAIL = "^\\w+((-\\w+)|(\\.\\w+))*\\@[A-Za-z0-9]+((\\.|-)[A-Za-z0-9]+)*\\.[A-Za-z0-9]+$";
	/** 邮编 */
	public static final String ZIPCODE = "^[1-9][0-9]{5}$";
	/** IPv4地址 */
	public static final String IP = "^\\d{1,3}(\\.\\d{1,3}){3}$";
	/** url校验规则 */
	public static final String URL = "^http[s]?:\\/\\/[\\w-]+(\\.[\\w-]+)+([\\w-\\.\\/?%&=]*)?$";
	/** 所属角色 */
	public static final String ROLES = "^[0-9]+(,[1-9]\\d*)*$";;
	/** 日期验证 (年月日) 如：20140712 */
	public static final String DATE = "^(\\d{4})(0[1-9]|1[0-2])(0[1-9]|[12]\\d{1}|3[01])$";
	/** 日期验证(年月)如：201407 */
	public static final String MDATE = "^(\\d{4})(0[1-9]|1[0-2])$";
	/** 日期验证(年) */
	public static final String YDATE = "^\\d{4}$";
	/** 只能是数字或者是空 */
	public static final String ONLYNUMBER = "^[0-9]*$";
	

}
