package com.dangjia.acg.util;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author view
 * @data  2017-10-28
 * @remark 配置文件处理工具
 */
public class PropertiesKitUtils {

	public static String readPropertiesValue(String key,String configFileName){
		Properties p = new Properties();
		try {
			InputStream profile = Thread.currentThread().getContextClassLoader().getResourceAsStream(configFileName);
			p.load(profile);
			String value = p.getProperty(key);
			profile.close();
			return value;
		} catch (Exception e) {
			System.out.println(">>>>key&configFileName:"+key+"&"+configFileName);
			System.out.println("logError:EcwxUtils readPropertiesValue occur error. "+e.getMessage());
			return null;
		}
	}
}
