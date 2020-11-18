package com.pi4axe.component;

import com.pi4axe.component.callback.InterruptCallback;
import com.pi4axe.manager.InterruptManager;
import com.pi4j.wiringpi.Gpio;
import com.pi4j.wiringpi.GpioInterruptEvent;

/**
 * 人体感应传感器组件
 * 一定要注意，放在H模式（在高电平期间，如果重复检测到人，会保持高电平） */
public class HCSR501{
	
	private Boolean anyBodyHere = false;
	
	private long nobodyStartTime = 0l;//人离开的起始时间
	private long nobodyCheckNanoTime = 8000000000l;//如果持续8秒都是低电平，则认为确实人离开了，传感器有一个2.5秒的封闭期，但那时官方说法，实际还要长一些
	
	public boolean isAnyBodyHere() {
		return anyBodyHere;
	}
	
	public HCSR501(int pin) throws Exception {
		InterruptManager.initInterruptListen(new InterruptCallback(pin, "人体感应中断回调",Gpio.PUD_DOWN) {
			@Override
			public void onInterrupt(GpioInterruptEvent arg0) {
				synchronized (anyBodyHere) {
					if(arg0.getState()){
						//高电平说明有人
						anyBodyHere = true;
						nobodyStartTime = 0;
//						LogUtil.log("有人");
					}else{
						//低电平说明人走了，开始准备计时吧
						nobodyStartTime = System.nanoTime();
//						LogUtil.log("好像走了");
					}
				}
			}
		});
	}
	
	public void testOnce() {
		synchronized (anyBodyHere) {
			if(anyBodyHere && nobodyStartTime > 0){
				if(System.nanoTime()-nobodyStartTime > nobodyCheckNanoTime){
					anyBodyHere = false;//认定确实没人
//					LogUtil.log("确实走了\t"+System.nanoTime()+"\t"+nobodyStartTime+"\t"+nobodyCheckNanoTime);
				}
			}
		}
	}

}
