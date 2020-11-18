package com.pi4axe.rest;

import java.util.List;

import org.axe.annotation.ioc.Controller;
import org.axe.annotation.mvc.Request;
import org.axe.annotation.mvc.RequestParam;
import org.axe.constant.RequestMethod;

import com.pi4axe.component.PinInfo;
import com.pi4axe.manager.GpioManager;

@Controller(basePath="/gpio",desc="GPIO接口")
public final class GpioRest {
	
	@Request(path="/get_provision_pin_list",method=RequestMethod.GET,desc="获取已经初始化定义好的pin列表")
	public List<PinInfo> get_provision_pin_list(){
		return GpioManager.getProvisionedPinInfoSortedList();
	}
	
	@Request(path="/get_provision_pin_info",method=RequestMethod.GET,desc="获取已经初始化好定义好的pin脚位信息")
	public PinInfo get_provision_pin_info(
			@RequestParam(name="pinAddress",required=true,desc="pin脚位置")Integer pinAddress
			){
		return GpioManager.getProvisionedPinInfo(pinAddress);
	}
	
}
