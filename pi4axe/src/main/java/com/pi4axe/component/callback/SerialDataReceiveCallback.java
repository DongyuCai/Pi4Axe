package com.pi4axe.component.callback;

import com.pi4j.io.serial.SerialDataEvent;
import com.pi4j.io.serial.SerialDataEventListener;

/**
 * 串口数据接收器
 * @author Administrator
 */
public abstract class SerialDataReceiveCallback implements SerialDataEventListener{

	//回调处理的名称，不能重复
	public abstract String callbackName();
	
	public String getName(){
		return callbackName();
	}
	
	@Override
	public final void dataReceived(SerialDataEvent dataEvent) {
		onReceiveData(dataEvent);
	}
	
	public abstract void onReceiveData(SerialDataEvent dataEvent);
	
}
