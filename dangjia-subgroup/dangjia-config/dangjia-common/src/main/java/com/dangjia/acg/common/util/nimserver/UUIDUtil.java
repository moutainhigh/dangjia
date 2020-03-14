package com.dangjia.acg.common.util.nimserver;

import com.dangjia.acg.common.util.CommonUtil;

import java.util.UUID;

public class UUIDUtil {
	/**
	 * 
	 * @return String UUID
	 */
	public static String getUUID() {
		String s = UUID.randomUUID().toString();
		return s.substring(0, 8) + s.substring(9, 13) + s.substring(14, 18) + s.substring(19, 23) + s.substring(24);
	}

	/**
	 * @param number
	 *            int
	 * @return String[] UUID
	 */
	public static String[] getUUID(int number) {
		if (number < 1) {
			return null;
		}
		String[] ss = new String[number];
		for (int i = 0; i < number; i++) {
			ss[i] = getUUID();
		}
		return ss;
	}

	/**
	 * 根据环境不同对用户进行Md5加密（激光，和网易云消息使用）
	 * @param active 环境
	 * @param userid 用户ID
	 * @return
	 */
	public static String getUserTag(String active ,String userid) {
		if(active.equals("N")){
			return userid;
		}
		if (!("pre".equals(active))) {
			return CommonUtil.md5("test_" + userid);
		} else {
			return CommonUtil.md5(userid);
		}
	}

	public static String[] getUserTags(String active ,String[] userids) {
		if (userids != null && userids.length > 0) {
			for (int i = 0; i < userids.length; i++) {
				userids[i] = getUserTag(active,userids[i]);
			}
		}
		return userids;
	}
}
