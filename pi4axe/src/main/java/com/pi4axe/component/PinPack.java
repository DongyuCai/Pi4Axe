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

import java.math.BigDecimal;

import com.pi4j.io.gpio.GpioPinAnalogInput;
import com.pi4j.io.gpio.GpioPinAnalogOutput;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.GpioPinPwmOutput;

/**
 * Pin引脚的包
 * 对引脚操控对象、引脚信息封装的打包管理
 * 因为要保证PinInfo中信息与实际信息一致，所以不能对外界公开获取这些input output对象的方法
 * 只能通过此Pack类对象来操作
 */
public final class PinPack {
	
	private PinInfo pinInfo;

	private GpioPinDigitalInput digitalInput;
	
	private GpioPinDigitalOutput digitalOutput;

	private GpioPinAnalogInput analogInput;

	private GpioPinAnalogOutput analogOutput;

	private GpioPinPwmOutput  pwmOutput;
	private BigDecimal pwmRange=null;
	private BigDecimal pwmTotalLen;
	
	public PinPack(PinInfo pinInfo) {
		this.pinInfo = pinInfo;
	}

	
	public GpioPinDigitalInput getDigitalInput() {
		return digitalInput;
	}

	public GpioPinDigitalOutput getDigitalOutput() {
		return digitalOutput;
	}

	public GpioPinAnalogInput getAnalogInput() {
		return analogInput;
	}

	public GpioPinAnalogOutput getAnalogOutput() {
		return analogOutput;
	}

	public GpioPinPwmOutput getPwmOutput() {
		return pwmOutput;
	}

	public PinPack(PinInfo pinInfo, GpioPinDigitalInput digitalInput) {
		this.pinInfo = pinInfo;
		this.digitalInput = digitalInput;
	}
	
	public PinPack(PinInfo pinInfo, GpioPinDigitalOutput digitalOutput) {
		this.pinInfo = pinInfo;
		this.digitalOutput = digitalOutput;
	}

	public PinPack(PinInfo pinInfo, GpioPinAnalogInput analogInput) {
		super();
		this.pinInfo = pinInfo;
		this.analogInput = analogInput;
	}

	public PinPack(PinInfo pinInfo, GpioPinAnalogOutput analogOutput) {
		this.pinInfo = pinInfo;
		this.analogOutput = analogOutput;
	}

	public PinPack(PinInfo pinInfo, GpioPinPwmOutput pwmOutput,BigDecimal range,BigDecimal pwmTotalLen) {
		this.pinInfo = pinInfo;
		this.pwmOutput = pwmOutput;
		this.pwmRange = range;
		this.pwmTotalLen = pwmTotalLen;
	}

	//获取引脚信息
	public PinInfo getPinInfo() {
		//TODO 还不全
		if(digitalInput != null){
			pinInfo.setHigh(digitalInput.isHigh());
		}else{
			if(digitalOutput != null){
				pinInfo.setHigh(digitalOutput.isHigh());
			}
		}
		return pinInfo;
	}

	//引脚操控开始
	public void toggleDigitalOutput(){
		digitalOutput.toggle();
		pinInfo.setHigh(!pinInfo.isHigh());
	}
	
	public void setDigitalOutPutLow(){
		digitalOutput.low();
		pinInfo.setHigh(false);
	}

	public void setDigitalOutPutHigh(){
		digitalOutput.high();
		pinInfo.setHigh(true);
	}
	
	/**
	 * @param pwmLen 输出pwm的高电平持续时间，单位ms
	 */
	public void setPwmOutput(BigDecimal pwmLen){
		int pwm = pwmLen.multiply(pwmRange).divide(pwmTotalLen,0,BigDecimal.ROUND_UP).intValue();
		pwmOutput.setPwm(pwm);
	}
}
