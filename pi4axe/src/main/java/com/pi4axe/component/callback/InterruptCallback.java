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
package com.pi4axe.component.callback;

import com.pi4j.wiringpi.GpioInterruptEvent;
import com.pi4j.wiringpi.GpioInterruptListener;

/**
 * 引脚中断监听的回调处理
 */
public abstract class InterruptCallback implements GpioInterruptListener{

	private int gpio;
	private String callbackName;
	private int initPull;
	
	public InterruptCallback(int gpio, String callbackName,int initPull) {
		this.gpio = gpio;
		this.callbackName = callbackName;
		this.initPull = initPull;
	}

	public int getGpio() {
		return gpio;
	}

	public String getCallbackName() {
		return callbackName;
	}

	public int getInitPull() {
		return initPull;
	}

	@Override
	public final void pinStateChange(GpioInterruptEvent event) {
		if(event.getPin() == this.gpio){
			onInterrupt(event);
		}
	}
	
	public abstract void onInterrupt(GpioInterruptEvent event);
	
}
