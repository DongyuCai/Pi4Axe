package com.pi4axe.timer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.math.BigDecimal;

import org.axe.interface_.mvc.Timer;

/**
 * Cpu温度定时检测
 */
public final class CpuTempTimer implements Timer{
	
	private String temp = "0.0";
	
	public String getTemp() {
		return temp;
	}
	
	public CpuTempTimer() {
		
	}

	private Integer timer = 10;//axe的定时器时间片是100毫秒，所以10个时间片1周期
	@Override
	public boolean canExecuteNow() {
		//1秒1次
		if(timer>0){
			timer--;
			return false;
		}else{
			timer = 10;
			return true;
		}
	}

	@Override
	public void doSomething() {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(new File("/sys/class/thermal/thermal_zone0/temp")));
			temp = new BigDecimal(reader.readLine()).divide(new BigDecimal(1000),1,BigDecimal.ROUND_HALF_UP).toPlainString();
		} catch (Exception e) {} finally {
			if(reader != null){
				try {
					reader.close();
				} catch (Exception e2) {}
			}
		}
	}

	@Override
	public String name() {
		return "CPU温度定时";
	}
	
}
