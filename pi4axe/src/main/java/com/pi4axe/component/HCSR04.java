package com.pi4axe.component;

import java.math.BigDecimal;

import com.pi4axe.component.callback.InterruptCallback;
import com.pi4axe.manager.GpioManager;
import com.pi4axe.manager.InterruptManager;
import com.pi4j.io.gpio.PinState;
import com.pi4j.wiringpi.Gpio;
import com.pi4j.wiringpi.GpioInterruptEvent;

/**
 * MIT License
 * 
 * Copyright (c) 2020 CaiDongyu
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
public class HCSR04{
	private PinPack trig = null;
	private long startTime = 0;
	
	private String distance = "0.00";//单位cm
	private String maxDistance = "0.00";
	
	public String getDistance() {
		synchronized (distance) {
			return distance;
		}
	}
	
	public String getMaxDistance() {
		synchronized (distance) {
			return maxDistance;
		}
	}
	
	//声速340米/s，切成cm/纳秒单位
	private BigDecimal speed = new BigDecimal("0.000034");
	public HCSR04(int trigPin,int interruptPin) throws Exception{
		trig = GpioManager.provisionDigitalOutputPin(trigPin,PinState.LOW);
		InterruptManager.initInterruptListen(new InterruptCallback(interruptPin, "超声波中断回调", Gpio.PUD_DOWN) {
			
			@Override
			public void onInterrupt(GpioInterruptEvent arg0) {
					if(arg0.getState()){
						startTime = System.nanoTime();
					}else{
						//dur的单位是纳秒，除以2就是单程时间，否则是声音一来一去的总时间
						long dur = (System.nanoTime()-startTime)/2;
						synchronized (distance) {
							BigDecimal value = speed.multiply(new BigDecimal(dur)).setScale(2,BigDecimal.ROUND_HALF_UP);
							if(value.compareTo(new BigDecimal("170")) < 0){//超过1米7的肯定是脏数据了
								distance = value.toPlainString();
								if(value.compareTo(new BigDecimal(maxDistance)) > 0){
									maxDistance = distance;
								}
							}
						}
					}
			}
			
		});
	}

	public void trig() {
		trig.setDigitalOutPutHigh();
		try {
			Thread.sleep(1);
		} catch (Exception e) {}
		trig.setDigitalOutPutLow();
	}

}
