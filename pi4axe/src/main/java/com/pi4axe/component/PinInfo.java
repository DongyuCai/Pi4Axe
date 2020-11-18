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
package com.pi4axe.component;

import com.pi4j.io.gpio.PinMode;

/**
 * Pin引脚信息封装类
 * 一个引脚，初始化后，要么是输出模式，要么是输入模式
 */
public final class PinInfo {

	private int pinAddress;//引脚地址
	
	private boolean high;//引脚电平状态 false低电平 true高电平
	
	private PinMode pinMode;//引脚的模式
	
	public PinInfo(int pinAddress, boolean high, PinMode pinMode) {
		this.pinAddress = pinAddress;
		this.high = high;
		this.pinMode = pinMode;
	}

	public int getPinAddress() {
		return pinAddress;
	}

	public void setPinAddress(int pinAddress) {
		this.pinAddress = pinAddress;
	}

	public boolean isHigh() {
		return high;
	}

	public void setHigh(boolean high) {
		this.high = high;
	}

	public PinMode getPinMode() {
		return pinMode;
	}

	public void setPinMode(PinMode pinMode) {
		this.pinMode = pinMode;
	}
}
