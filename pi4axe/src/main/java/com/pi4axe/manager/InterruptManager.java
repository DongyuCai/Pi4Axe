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
package com.pi4axe.manager;

import java.util.HashMap;

import com.pi4axe.component.callback.InterruptCallback;
import com.pi4j.wiringpi.Gpio;
import com.pi4j.wiringpi.GpioInterrupt;
import com.pi4j.wiringpi.GpioUtil;

/**
 * 中断控制
 */
public final class InterruptManager {
	
	//存回调的名称
	private final static HashMap<Integer,String> CALLBACK_NAME_MAP = new HashMap<>();
	
	/**
	 * 初始化引脚的中断监听，可以重复
	 */
	public static void initInterruptListen(InterruptCallback callback)throws Exception{
		synchronized (CALLBACK_NAME_MAP) {
			if(CALLBACK_NAME_MAP.containsKey(callback.getGpio())){
				throw new Exception("GPIO:"+callback.getGpio()+" 不能重复添加中断回调方法");
			}
			
			// add GPIO listener
			GpioInterrupt.addListener(callback);
			
			// setup wiring pi
	        if (Gpio.wiringPiSetup() == -1) {
	            throw new Exception("GPIO SETUP FAILED");
	        }
	        
	        // export all the GPIO pins that we will be using
	        GpioUtil.export(callback.getGpio(), GpioUtil.DIRECTION_IN);
	        
	        // set the edge state on the pins we will be listening for
	        GpioUtil.setEdgeDetection(callback.getGpio(), GpioUtil.EDGE_BOTH);
	        
	        // configure GPIO 0 as an INPUT pin; enable it for callbacks
	        Gpio.pinMode(callback.getGpio(), Gpio.INPUT);
	        Gpio.pullUpDnControl(callback.getGpio(), callback.getInitPull());
	        GpioInterrupt.enablePinStateChangeCallback(callback.getGpio());
	        
	        CALLBACK_NAME_MAP.put(callback.getGpio(),callback.getCallbackName());
		}
		
	}
	
}
