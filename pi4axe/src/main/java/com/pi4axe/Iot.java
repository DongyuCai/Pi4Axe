package com.pi4axe;

import java.util.Properties;

import org.axe.constant.ConfigConstant;
import org.axe.helper.base.ConfigHelper;
import org.axe.interface_.mvc.AfterConfigLoaded;

/**
 * axe-iot的初始化类
 * 负责结合axe框架的初始化工作，省去了读配置文件
 */
public final class Iot {
	
	public static void config(){
		//添加框架扫描包路径
		//必须在axe框架读取配置后，初始化之前
		ConfigHelper.addAfterConfigLoadedCallback(new AfterConfigLoaded() {
			@Override
			public void doSomething(Properties config) {
				String value = config.getProperty(ConfigConstant.APP_BASE_PACKAGE).toString();
				if(!value.contains("com.pi4axe.rest")){
					value = value+",com.pi4axe.rest";
				}
				if(!value.contains("com.pi4axe.timer")) {
					value = value+",com.pi4axe.timer";
				}
				config.setProperty(ConfigConstant.APP_BASE_PACKAGE, value);
			}
		});
		
	}
	
}
