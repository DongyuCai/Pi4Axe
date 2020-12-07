package com.pi4axe.timer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.math.BigDecimal;

import org.axe.extra.timer.TimerTask;

/**
 * Cpu温度定时检测
 */
public final class CpuTempTimer extends TimerTask{
	
	private String temp = "0.0";
	
	public String getTemp() {
		return temp;
	}
	
	public CpuTempTimer() {
		
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

	@Override
	public int timeSec() {
		return 1;
	}
	
}
